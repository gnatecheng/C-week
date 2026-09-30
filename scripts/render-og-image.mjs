#!/usr/bin/env node
/** Render site/assets/og-image.source.html → site/assets/og-image.png (1200×630). */
import { chromium } from "playwright";
import { fileURLToPath } from "node:url";
import path from "node:path";

const root = path.resolve(path.dirname(fileURLToPath(import.meta.url)), "..");
const htmlPath = path.join(root, "site/assets/og-image.source.html");
const outPath = path.join(root, "site/assets/og-image.png");
const fileUrl = `file://${htmlPath}`;

const browser = await chromium.launch({ headless: true });
const page = await browser.newPage({ viewport: { width: 1200, height: 630 } });
await page.goto(fileUrl, { waitUntil: "networkidle", timeout: 30000 });
await page.screenshot({ path: outPath, type: "png", omitBackground: false });
await browser.close();

import sharp from "sharp";
const { statSync, writeFileSync, readFileSync } = await import("node:fs");
const raw = readFileSync(outPath);
const optimized = await sharp(raw)
  .png({ compressionLevel: 9, palette: true, quality: 65, effort: 10 })
  .toBuffer();
writeFileSync(outPath, optimized);
const kb = (statSync(outPath).size / 1024).toFixed(1);
console.log("wrote", outPath, kb, "KB");
