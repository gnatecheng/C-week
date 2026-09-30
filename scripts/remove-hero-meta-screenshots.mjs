import { chromium } from "playwright";
import { mkdir } from "node:fs/promises";
import { spawn } from "node:child_process";
import path from "node:path";
import { fileURLToPath } from "node:url";

const root = path.resolve(path.dirname(fileURLToPath(import.meta.url)), "..");
const OUT = "/opt/cursor/artifacts/screenshots";
const PORT = 8779;

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

async function sectionShot(name, sel, lang, theme, fullPage = false) {
  const page = await browser.newPage({
    viewport: fullPage ? { width: 390, height: 844 } : { width: 1280, height: 900 },
    deviceScaleFactor: fullPage ? 2 : 2,
  });
  await prep(page, lang, theme);
  if (fullPage) {
    await page.screenshot({ path: `${OUT}/${name}.png` });
  } else {
    const section = page.locator(sel);
    await section.scrollIntoViewIfNeeded();
    await section.screenshot({ path: `${OUT}/${name}.png` });
  }
  await page.close();
  console.log("saved", name);
}

await sectionShot("remove-hero-meta-zh-light-section", "#qingjizhang", "zh", "light");
await sectionShot("remove-hero-meta-en-dark-section", "#qingjizhang", "en", "dark");
await sectionShot("remove-hero-meta-mobile-zh-top", "body", "zh", "light", true);

await browser.close();
process.kill(-server.pid);
console.log("done");
