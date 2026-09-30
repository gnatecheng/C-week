/**
 * Refresh SEO snippets on site/index.html from translations.js, then rebuild /en/.
 * Run from repo root: node scripts/sync-site-seo.mjs
 */
import { readFileSync, writeFileSync } from "node:fs";
import { execSync } from "node:child_process";
import path from "node:path";
import { fileURLToPath } from "node:url";
import { buildJsonLd, loadTranslationsFromFile } from "./site-seo-jsonld.mjs";

const root = path.resolve(path.dirname(fileURLToPath(import.meta.url)), "..");
const indexPath = path.join(root, "site/index.html");
const T = loadTranslationsFromFile(readFileSync, path.join(root, "site/js/translations.js"));
const zh = T.zh;

let html = readFileSync(indexPath, "utf8");

html = html.replace(/<title>[^<]*<\/title>/, `<title>${zh["meta.title"]}</title>`);

html = html.replace(
  /(<meta\s+name="description"\s+data-i18n-content=")meta\.description("[\s\S]*?content=")[^"]*(")/,
  `$1meta.description"\n      content="${zh["meta.description"].replace(/"/g, "&quot;")}$3`
);

const hreflang = `    <link rel="alternate" hreflang="zh-CN" href="https://etais.dev/" />
    <link rel="alternate" hreflang="en" href="https://etais.dev/en/" />
    <link rel="alternate" hreflang="x-default" href="https://etais.dev/" />`;

if (!html.includes('hreflang="zh-CN"')) {
  html = html.replace('<link rel="canonical"', hreflang + "\n    <link rel=\"canonical\"");
}

html = html.replace(
  /(<meta\s+property="og:title"[\s\S]*?content=")[^"]*(")/,
  `$1${zh["meta.ogTitle"].replace(/"/g, "&quot;")}$2`
);
html = html.replace(
  /(<meta\s+property="og:description"[\s\S]*?content=")[^"]*(")/,
  `$1${zh["meta.ogDescription"].replace(/"/g, "&quot;")}$2`
);
html = html.replace(
  /(<meta\s+name="twitter:title"[\s\S]*?content=")[^"]*(")/,
  `$1${zh["meta.twitterTitle"].replace(/"/g, "&quot;")}$2`
);
html = html.replace(
  /(<meta\s+name="twitter:description"[\s\S]*?content=")[^"]*(")/,
  `$1${zh["meta.twitterDescription"].replace(/"/g, "&quot;")}$2`
);

if (!html.includes("og:site_name")) {
  html = html.replace(
    '<meta property="og:type" content="website" />',
    `<meta property="og:type" content="website" />\n    <meta property="og:site_name" content="${zh["meta.siteName"]}" data-i18n-content="meta.siteName" />`
  );
}

if (!html.includes("og:image:alt")) {
  html = html.replace(
    '<meta property="og:image:type" content="image/png" />',
    `<meta property="og:image:type" content="image/png" />\n    <meta property="og:image:alt" content="${zh["meta.ogImageAlt"].replace(/"/g, "&quot;")}" data-i18n-content="meta.ogImageAlt" />`
  );
}

if (!html.includes('href="/favicon.ico"')) {
  html = html.replace(
    '<link rel="icon" href="/assets/icon.svg?v=1"',
    '<link rel="icon" href="/favicon.ico?v=1" sizes="any" />\n    <link rel="icon" href="/assets/icon.svg?v=1"'
  );
}

const ld = JSON.stringify(buildJsonLd("zh", zh), null, 2);
const block = `<script type="application/ld+json" id="structured-data">\n${ld}\n    </script>`;
html = html.replace(/<script type="application\/ld\+json" id="structured-data">[\s\S]*?<\/script>/, block);

html = html.replace(
  /\s*<div class="wrap">\s*<h1 class="page-title"[^>]*>[\s\S]*?<\/h1>\s*<\/div>\s*/g,
  "\n"
);

html = html.replace(
  '<h1 data-i18n="cweek.hero.title">C一周通</h1>',
  '<h2 class="hero-title" data-i18n="cweek.hero.title">C一周通</h2>'
);

if (!html.includes('id="lang-hint"')) {
  html = html.replace(
    "</header>\n\n    <main id=\"main\">",
    `</header>\n\n    <div id="lang-hint" class="lang-hint" hidden role="region" aria-label="Language suggestion">
      <div class="wrap lang-hint-inner">
        <p><span data-i18n="langHint.message">此页面为中文版。</span> <a id="lang-hint-link" href="/en/" data-i18n="langHint.switch">切换到 English</a></p>
        <button type="button" id="lang-hint-dismiss" class="lang-hint-dismiss" data-i18n="langHint.dismiss">关闭</button>
      </div>
    </div>\n\n    <main id="main">`
  );
}

writeFileSync(indexPath, html);
console.log("Updated site/index.html SEO snippets");
execSync("node scripts/build-en-index.mjs", { cwd: root, stdio: "inherit" });
