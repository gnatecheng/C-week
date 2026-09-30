import { chromium } from "playwright";
import { mkdir } from "node:fs/promises";
import { spawn } from "node:child_process";
import path from "node:path";
import { fileURLToPath } from "node:url";

const root = path.resolve(path.dirname(fileURLToPath(import.meta.url)), "..");
const OUT = "/opt/cursor/artifacts/screenshots";
const PORT = 8780;

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
  await page.waitForTimeout(1500);
  await page
    .waitForFunction(() => typeof window.ETAI_applyScreenshots === "function", { timeout: 15000 })
    .catch(function () {});
  await page.evaluate(function () {
    if (window.ETAI_applyScreenshots) window.ETAI_applyScreenshots();
  });
  await page.waitForTimeout(500);
}

const server = serveSite();
await new Promise((r) => setTimeout(r, 900));
await mkdir(OUT, { recursive: true });

const browser = await chromium.launch({
  headless: true,
  channel: "chrome",
  args: ["--virtual-time-budget=8000"],
});

async function sectionShot(name, lang, theme, mobile = false) {
  const page = await browser.newPage({
    viewport: mobile ? { width: 390, height: 844 } : { width: 1280, height: 900 },
    deviceScaleFactor: 2,
  });
  await prep(page, lang, theme);
  const section = page.locator("#group-matters");
  if (mobile) {
    await page.screenshot({ path: `${OUT}/${name}.png` });
  } else {
    await section.scrollIntoViewIfNeeded();
    await section.screenshot({ path: `${OUT}/${name}.png` });
  }
  await page.close();
  console.log("saved", name);
}

for (const [name, lang, theme] of [
  ["gm-section-zh-light", "zh", "light"],
  ["gm-section-zh-dark", "zh", "dark"],
  ["gm-section-en-light", "en", "light"],
  ["gm-section-en-dark", "en", "dark"],
]) {
  await sectionShot(name, lang, theme);
}
await sectionShot("gm-section-mobile-zh-light", "zh", "light", true);

await browser.close();
process.kill(-server.pid);
console.log("done");
