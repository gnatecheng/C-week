import { chromium } from "playwright";
import { mkdir, copyFile } from "node:fs/promises";
import { spawn } from "node:child_process";
import path from "node:path";
import { fileURLToPath } from "node:url";

const root = path.resolve(path.dirname(fileURLToPath(import.meta.url)), "..");
const OUT = "/opt/cursor/artifacts/screenshots";
const PORT = 8765;

function serveSite() {
  return spawn("python3", ["-m", "http.server", String(PORT), "--directory", path.join(root, "site")], {
    stdio: "ignore",
    detached: true,
  });
}

async function prep(page, lang, theme) {
  await page.goto(`http://127.0.0.1:${PORT}/`, { waitUntil: "networkidle", timeout: 30000 });
  await page.evaluate(
    ({ lang, theme }) => {
      localStorage.setItem("etai-lang", lang);
      localStorage.setItem("etai-theme", theme);
    },
    { lang, theme }
  );
  await page.reload({ waitUntil: "networkidle", timeout: 30000 });
  await page.waitForTimeout(800);
}

const server = serveSite();
await new Promise((r) => setTimeout(r, 800));
await mkdir(OUT, { recursive: true });

const browser = await chromium.launch({ headless: true, channel: "chrome" });

const shots = [
  ["seo-desktop-zh-light", 1280, 900, "zh", "light", null],
  ["seo-desktop-en-dark-qr", 1280, 900, "en", "dark", "#cweek"],
  ["seo-mobile-zh-light-no-qr", 390, 844, "zh", "light", "#cweek"],
  ["seo-faq-install-tips", 1280, 900, "zh", "light", "#faq"],
];

for (const [name, w, h, lang, theme, hash] of shots) {
  const page = await browser.newPage({ viewport: { width: w, height: h } });
  await prep(page, lang, theme);
  if (hash) {
    await page.goto(`http://127.0.0.1:${PORT}/${hash}`, { waitUntil: "networkidle" });
    await page.waitForTimeout(500);
  }
  if (name === "seo-faq-install-tips") {
    await page.locator('[data-i18n="faq.installTips.title"]').scrollIntoViewIfNeeded();
  }
  await page.screenshot({ path: `${OUT}/${name}.png`, fullPage: false });
  await page.close();
  console.log("saved", name);
}

await copyFile(path.join(root, "site/assets/og-image.png"), `${OUT}/seo-og-image-1200x630.png`);
console.log("saved seo-og-image-1200x630.png");

await browser.close();
process.kill(-server.pid);
console.log("done");
