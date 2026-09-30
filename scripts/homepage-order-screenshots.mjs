import { chromium } from "playwright";
import { mkdir } from "node:fs/promises";
import { spawn } from "node:child_process";
import path from "node:path";
import { fileURLToPath } from "node:url";

const root = path.resolve(path.dirname(fileURLToPath(import.meta.url)), "..");
const OUT = "/opt/cursor/artifacts/screenshots";
const PORT = 8788;

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
  await page.waitForTimeout(1000);
}

const server = serveSite();
await new Promise((r) => setTimeout(r, 800));
await mkdir(OUT, { recursive: true });
const browser = await chromium.launch({ headless: true, channel: "chrome", args: ["--virtual-time-budget=8000"] });

for (const [name, w, h, lang, theme, full] of [
  ["order-desktop-zh-light-full", 1280, 900, "zh", "light", true],
  ["order-desktop-en-dark-top", 1280, 900, "en", "dark", false],
  ["order-mobile-zh-light-top", 390, 844, "zh", "light", false],
]) {
  const page = await browser.newPage({ viewport: { width: w, height: h }, deviceScaleFactor: 1 });
  await prep(page, lang, theme);
  await page.screenshot({ path: `${OUT}/${name}.png`, fullPage: full });
  await page.close();
  console.log("saved", name);
}

await browser.close();
process.kill(-server.pid);
console.log("done");
