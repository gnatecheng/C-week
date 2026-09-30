import { chromium } from "playwright";
import { mkdir, writeFile } from "node:fs/promises";
import { spawn, spawnSync } from "node:child_process";
import path from "node:path";
import { fileURLToPath } from "node:url";

const root = path.resolve(path.dirname(fileURLToPath(import.meta.url)), "..");
const OUT = "/opt/cursor/artifacts/screenshots";
const PORT = 8777;

function serveSite() {
  return spawn("python3", ["-m", "http.server", String(PORT), "--directory", path.join(root, "site")], {
    stdio: "ignore",
    detached: true,
  });
}

async function prep(page, lang, theme) {
  await page.goto(`http://127.0.0.1:${PORT}/`, { waitUntil: "networkidle", timeout: 60000 });
  await page.evaluate(
    ({ lang, theme }) => {
      localStorage.setItem("etai-lang", lang);
      localStorage.setItem("etai-theme", theme);
    },
    { lang, theme }
  );
  await page.reload({ waitUntil: "networkidle", timeout: 60000 });
  await page.waitForTimeout(1200);
}

const server = serveSite();
await new Promise((r) => setTimeout(r, 900));
await mkdir(OUT, { recursive: true });

const browser = await chromium.launch({
  headless: true,
  channel: "chrome",
  args: ["--virtual-time-budget=8000"],
});

const decodeResults = [];

async function fullPage(name, w, h, lang, theme) {
  const page = await browser.newPage({ viewport: { width: w, height: h }, deviceScaleFactor: 1 });
  await prep(page, lang, theme);
  await page.screenshot({ path: `${OUT}/${name}.png`, fullPage: true });
  await page.close();
  console.log("saved", name);
}

await fullPage("D-zh-light", 1280, 900, "zh", "light");

const panels = [
  ["D-panel-closeup-cweek-zh", "#cweek .download-panel--cweek", "zh", "light"],
  ["D-panel-closeup-qjz-zh", "#qingjizhang .download-panel--qjz", "zh", "light"],
  ["D-panel-closeup-gm-zh", "#group-matters .download-panel--class", "zh", "light"],
  ["D-panel-closeup-cweek-en-dark", "#cweek .download-panel--cweek", "en", "dark"],
];

for (const [name, sel, lang, theme] of panels) {
  const page = await browser.newPage({ viewport: { width: 1280, height: 900 }, deviceScaleFactor: 1 });
  await prep(page, lang, theme);
  const panel = page.locator(sel);
  await panel.scrollIntoViewIfNeeded();
  await panel.screenshot({ path: `${OUT}/${name}.png` });
  await page.close();
  console.log("saved", name);
}

const decodePage = await browser.newPage({ viewport: { width: 1280, height: 900 }, deviceScaleFactor: 1 });
await prep(decodePage, "en", "dark");
const qrFrames = decodePage.locator(".download-panel__qr-frame");
const count = await qrFrames.count();
for (let i = 0; i < count; i++) {
  const file = `${OUT}/D-qr-decode-${i + 1}.png`;
  await qrFrames.nth(i).screenshot({ path: file });
  const zbar = spawnSync("zbarimg", ["-q", "--raw", file], { encoding: "utf-8" });
  const url = (zbar.stdout || "").trim();
  decodeResults.push({ file: path.basename(file), url, ok: zbar.status === 0 && url.startsWith("https://") });
  console.log("decode", file, url || zbar.stderr);
}
await decodePage.close();
await browser.close();
process.kill(-server.pid);

await writeFile(`${OUT}/D-qr-decode-results.json`, JSON.stringify(decodeResults, null, 2) + "\n", "utf-8");
console.log("done");
