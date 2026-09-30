import { chromium } from "playwright";
import { mkdir } from "node:fs/promises";
import { createServer } from "node:http";
import { readFile } from "node:fs/promises";
import path from "node:path";
import { fileURLToPath } from "node:url";

const root = path.resolve(path.dirname(fileURLToPath(import.meta.url)), "..");
const siteRoot = path.join(root, "site");
const OUT = "/opt/cursor/artifacts";

const MIME = {
  ".html": "text/html; charset=utf-8",
  ".css": "text/css; charset=utf-8",
  ".js": "application/javascript; charset=utf-8",
  ".webp": "image/webp",
  ".png": "image/png",
  ".svg": "image/svg+xml",
  ".json": "application/json",
  ".ico": "image/x-icon",
  ".webmanifest": "application/manifest+json",
};

function server() {
  return createServer(async (req, res) => {
    let urlPath = decodeURIComponent(new URL(req.url || "/", "http://x").pathname);
    if (urlPath === "/") urlPath = "/index.html";
    if (urlPath.endsWith("/")) urlPath += "index.html";
    const filePath = path.join(siteRoot, urlPath);
    if (!filePath.startsWith(siteRoot)) {
      res.writeHead(403);
      res.end();
      return;
    }
    try {
      const data = await readFile(filePath);
      const ext = path.extname(filePath);
      res.writeHead(200, { "Content-Type": MIME[ext] || "application/octet-stream" });
      res.end(data);
    } catch {
      res.writeHead(404);
      res.end("not found");
    }
  });
}

async function hideHeader(page) {
  await page.evaluate(() => {
    document.querySelectorAll(".site-header").forEach((el) => {
      el.style.display = "none";
    });
  });
}

await mkdir(OUT, { recursive: true });
const httpServer = server();
await new Promise((r) => httpServer.listen(8765, "127.0.0.1", r));
const browser = await chromium.launch({ headless: true });

async function shot(name, viewport, url, { fullPage = false, disableJs = false } = {}) {
  const page = await browser.newPage({
    viewport,
    javaScriptEnabled: !disableJs,
  });
  await page.goto(`http://127.0.0.1:8765${url}`, { waitUntil: "networkidle", timeout: 60000 });
  if (!disableJs) {
    await page.waitForTimeout(800);
  }
  await hideHeader(page);
  const out = `${OUT}/${name}.png`;
  await page.screenshot({ path: out, fullPage });
  console.log("saved", out);
  await page.close();
}

await shot("app-group-matters-zh-mobile-full", { width: 390, height: 844 }, "/group-matters/", {
  fullPage: true,
});
await shot("app-en-group-matters-mobile-full", { width: 390, height: 844 }, "/en/group-matters/", {
  fullPage: true,
});
await shot("app-c-week-zh-desktop-hero", { width: 1280, height: 900 }, "/c-week/", { fullPage: false });
await shot("app-easy-ledger-zh-mobile-hero", { width: 390, height: 844 }, "/easy-ledger/", {
  fullPage: false,
});
await shot("en-home-static-nojs-desktop-hero", { width: 1280, height: 900 }, "/en/", {
  fullPage: false,
  disableJs: true,
});

await browser.close();
httpServer.close();
