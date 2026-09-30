import { chromium } from "playwright";
import { mkdir } from "node:fs/promises";
import { spawn } from "node:child_process";
import path from "node:path";
import { fileURLToPath } from "node:url";

const root = path.resolve(path.dirname(fileURLToPath(import.meta.url)), "..");
const OUT = "/opt/cursor/artifacts/screenshots";
const PORT = 8778;

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

async function panelCloseup(name, sel, lang, theme) {
  const page = await browser.newPage({ viewport: { width: 1280, height: 900 }, deviceScaleFactor: 2 });
  await prep(page, lang, theme);
  const panel = page.locator(sel);
  await panel.scrollIntoViewIfNeeded();
  await panel.screenshot({ path: `${OUT}/${name}.png` });
  await page.close();
  console.log("saved", name);
}

await panelCloseup(
  "download-panel-align-zh-light-closeup",
  "#qingjizhang .download-panel--qjz",
  "zh",
  "light"
);
await panelCloseup(
  "download-panel-align-en-dark-closeup",
  "#qingjizhang .download-panel--qjz",
  "en",
  "dark"
);

{
  const page = await browser.newPage({ viewport: { width: 1280, height: 900 }, deviceScaleFactor: 1 });
  await prep(page, "zh", "light");
  await page.screenshot({ path: `${OUT}/download-panel-align-desktop-full.png`, fullPage: true });
  await page.close();
  console.log("saved download-panel-align-desktop-full");
}

{
  const page = await browser.newPage({ viewport: { width: 390, height: 844 }, deviceScaleFactor: 2 });
  await prep(page, "zh", "light");
  await page.screenshot({ path: `${OUT}/download-panel-align-mobile-top.png` });
  await page.close();
  console.log("saved download-panel-align-mobile-top");
}

await browser.close();
process.kill(-server.pid);
console.log("done");
