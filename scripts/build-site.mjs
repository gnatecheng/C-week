/**
 * Regenerate generated site HTML (app pages + /en/ homepage).
 * Run from repo root: node scripts/build-site.mjs
 */
import { execSync } from "node:child_process";
import path from "node:path";
import { fileURLToPath } from "node:url";

const root = path.resolve(path.dirname(fileURLToPath(import.meta.url)), "..");
execSync("node scripts/build-app-pages.mjs", { cwd: root, stdio: "inherit" });
execSync("node scripts/build-en-index.mjs", { cwd: root, stdio: "inherit" });
