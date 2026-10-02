/**
 * Fail if any ?v= in site/ is lower than the max seen on main for the same asset path.
 * Run from repo root: node scripts/check-site-cache-versions.mjs
 */
import { execSync } from "node:child_process";
import { readFileSync, readdirSync, statSync } from "node:fs";
import { join } from "node:path";

const SITE = "site";
const RE = /([\w./-]+)\?v=(\d+)/g;

function collectFromText(text, into) {
  RE.lastIndex = 0;
  let m;
  while ((m = RE.exec(text)) !== null) {
    const key = m[1];
    const v = Number.parseInt(m[2], 10);
    into.set(key, Math.max(into.get(key) ?? 0, v));
  }
}

function walkDir(dir, into) {
  for (const ent of readdirSync(dir)) {
    const p = join(dir, ent);
    if (statSync(p).isDirectory()) walkDir(p, into);
    else if (/\.(html|js|json)$/.test(ent)) {
      collectFromText(readFileSync(p, "utf8"), into);
    }
  }
}

function collectFromGitRef(ref) {
  const into = new Map();
  const files = execSync(`git ls-tree -r --name-only ${ref} -- site`, { encoding: "utf8" })
    .trim()
    .split("\n")
    .filter(Boolean);
  for (const f of files) {
    if (!/\.(html|js|json)$/.test(f)) continue;
    let text;
    try {
      text = execSync(`git show ${ref}:${f}`, { encoding: "utf8", maxBuffer: 10_000_000 });
    } catch {
      continue;
    }
    collectFromText(text, into);
  }
  return into;
}

const mainV = collectFromGitRef("main");
const branchV = new Map();
walkDir(SITE, branchV);

const failures = [];
for (const [key, mainMax] of mainV) {
  const branchVal = branchV.get(key);
  if (branchVal === undefined) continue;
  if (branchVal < mainMax) {
    failures.push(`${key}: branch v=${branchVal} < main v=${mainMax}`);
  }
}

if (failures.length) {
  console.error("Cache version regressions vs main:");
  for (const f of failures) {
    console.error(`  ${f}`);
  }
  process.exit(1);
}
console.log("site cache ?v= OK (no regressions vs main)");
