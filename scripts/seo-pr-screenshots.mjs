import { chromium } from "playwright";
import { mkdir } from "node:fs/promises";
import { spawn } from "node:child_process";
import path from "node:path";
import { fileURLToPath } from "node:url";

const root = path.resolve(path.dirname(fileURLToPath(import.meta.url)), "..");
const OUT = "/opt/cursor/artifacts/screenshots";
const PORT = 8788;

function serve() {
  return spawn("npx", ["wrangler", "dev", "--port", String(PORT), "--ip", "127.0.0.1"], {
    cwd: root,
    stdio: "ignore",
    detached: true,
  });
}

async function shot(page, name) {
  await page.screenshot({ path: `${OUT}/${name}.png` });
  console.log("saved", name);
}

const server = serve();
await new Promise((r) => setTimeout(r, 6000));
await mkdir(OUT, { recursive: true });

const browser = await chromium.launch({ headless: true, channel: "chrome" });

{
  const page = await browser.newPage({ viewport: { width: 1280, height: 900 }, deviceScaleFactor: 2 });
  await page.goto(`http://127.0.0.1:${PORT}/`, { waitUntil: "networkidle" });
  await page.evaluate(() => localStorage.setItem("etai-theme", "light"));
  await page.reload({ waitUntil: "networkidle" });
  await shot(page, "seo-home-zh-light-top");
  await page.close();
}

{
  const page = await browser.newPage({ viewport: { width: 1280, height: 900 }, deviceScaleFactor: 2 });
  await page.goto(`http://127.0.0.1:${PORT}/en/`, { waitUntil: "networkidle" });
  await page.evaluate(() => localStorage.setItem("etai-theme", "dark"));
  await page.reload({ waitUntil: "networkidle" });
  await shot(page, "seo-en-dark-top");
  await page.close();
}

{
  const page = await browser.newPage({ viewport: { width: 390, height: 844 }, deviceScaleFactor: 2 });
  await page.goto(`http://127.0.0.1:${PORT}/en/`, { waitUntil: "networkidle" });
  await shot(page, "seo-en-mobile-top");
  await page.close();
}

{
  const context = await browser.newContext({
    viewport: { width: 1280, height: 900 },
    deviceScaleFactor: 2,
    javaScriptEnabled: false,
  });
  const page = await context.newPage();
  await page.goto(`http://127.0.0.1:${PORT}/#faq`, { waitUntil: "domcontentloaded" });
  await page.evaluate(() => {
    var faq = document.getElementById("faq");
    if (faq) faq.scrollIntoView({ block: "start" });
  });
  await page.screenshot({ path: `${OUT}/seo-faq-zh-nojs.png`, fullPage: false });
  console.log("saved seo-faq-zh-nojs");
  await context.close();
}

await browser.close();
process.kill(-server.pid);
console.log("done");
