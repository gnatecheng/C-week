/**
 * Generate site/en/index.html from site/index.html + translations.js (English).
 * Run from repo root: node scripts/build-en-index.mjs
 */
import { mkdir, readFile, writeFile } from "node:fs/promises";
import path from "node:path";
import { fileURLToPath } from "node:url";
import { buildJsonLd } from "./site-seo-jsonld.mjs";
import { loadTranslations, applyStaticI18n } from "./static-i18n.mjs";

const root = path.resolve(path.dirname(fileURLToPath(import.meta.url)), "..");

function faqBlock(en) {
  const items = [
    ["faq.install.title", "faq.install.body"],
    ["faq.installTips.title", "faq.installTips.body"],
    ["faq.requirements.title", "faq.requirements.body"],
    ["faq.offline.title", "faq.offline.body"],
    ["faq.compile.title", "faq.compile.body"],
    ["faq.vscode.title", "faq.vscode.body"],
  ];
  const htmlKeys = new Set(["faq.install.body", "faq.installTips.body", "faq.vscode.body"]);
  return items
    .map(([titleKey, bodyKey]) => {
      const i18nHtml = htmlKeys.has(bodyKey)
        ? ` data-i18n-html="${bodyKey}"`
        : ` data-i18n="${bodyKey}"`;
      return `            <article class="faq-item">
              <h3 data-i18n="${titleKey}">${en[titleKey]}</h3>
              <p${i18nHtml}>${en[bodyKey]}</p>
            </article>`;
    })
    .join("\n");
}

function setMetaContent(html, attrMatch, value) {
  const re = new RegExp(`(<meta\\s[^>]*${attrMatch}[^>]*\\scontent=")([^"]*)(")`, "i");
  return html.replace(re, `$1${value.replace(/"/g, "&quot;")}$3`);
}

async function main() {
  const T = await loadTranslations(readFile, root);
  const en = T.en;
  let html = await readFile(path.join(root, "site/index.html"), "utf8");

  html = html.replace("<html lang=\"zh-CN\">", "<html lang=\"en\">");
  if (html.includes('data-page-lang="zh"')) {
    html = html.replace('data-page-lang="zh"', 'data-page-lang="en"');
  } else if (!html.includes("data-page-lang")) {
    html = html.replace("<body", '<body data-page-lang="en"');
  }

  html = html.replace(/<title>[^<]*<\/title>/, `<title>${en["meta.title"]}</title>`);
  html = setMetaContent(html, 'name="description"', en["meta.description"]);
  html = html.replace(
    '<link rel="canonical" href="https://etais.dev/" />',
    '<link rel="canonical" href="https://etais.dev/en/" />'
  );
  html = setMetaContent(html, 'property="og:url"', "https://etais.dev/en/");
  html = setMetaContent(html, 'property="og:title"', en["meta.ogTitle"]);
  html = setMetaContent(html, 'property="og:description"', en["meta.ogDescription"]);
  html = setMetaContent(html, 'property="og:site_name"', en["meta.siteName"]);
  html = setMetaContent(html, 'property="og:image:alt"', en["meta.ogImageAlt"]);
  html = setMetaContent(html, 'name="twitter:title"', en["meta.twitterTitle"]);
  html = setMetaContent(html, 'name="twitter:description"', en["meta.twitterDescription"]);

  html = html.replace('property="og:locale" content="zh_CN"', 'property="og:locale" content="en_US"');
  html = html.replace(
    'property="og:locale:alternate" content="en_US"',
    'property="og:locale:alternate" content="zh_CN"'
  );

  html = html.replace(
    /<h1 class="page-title"[^>]*>[\s\S]*?<\/h1>/,
    `<h1 class="page-title" data-i18n="meta.pageH1">${en["meta.pageH1"]}</h1>`
  );

  const jsonLd = JSON.stringify(buildJsonLd("en", en), null, 2);
  html = html.replace(
    /<script type="application\/ld\+json" id="structured-data">[\s\S]*?<\/script>/,
    `<script type="application/ld+json" id="structured-data">\n${jsonLd}\n    </script>`
  );

  html = html.replace(
    /<div class="faq-list">[\s\S]*?<\/div>\s*\n\s*<\/div>\s*\n\s*<\/section>\s*\n\s*<\/main>/,
    `<div class="faq-list">\n${faqBlock(en)}\n          </div>\n        </div>\n      </section>\n\n    </main>`
  );

  html = html.replace(/href="\/#([^"]+)"/g, 'href="/en/#$1"');
  html = html.replace('<a class="brand" href="/">', '<a class="brand" href="/en/">');
  html = html.replace('href="/easy-ledger/"', 'href="/en/easy-ledger/"');
  html = html.replace('href="/group-matters/"', 'href="/en/group-matters/"');
  html = html.replace('href="/c-week/"', 'href="/en/c-week/"');
  html = html.replace(
    /id="lang-hint-link" href="\/en\/"/,
    'id="lang-hint-link" href="/en/" style="display:none" aria-hidden="true"'
  );
  html = html.replace(
    /<div id="lang-hint" class="lang-hint" hidden[\s\S]*?<\/div>\n\n    <main/,
    "<main"
  );

  const i18nKeyFix = [
    ['name="description"', "meta.description"],
    ['property="og:title"', "meta.ogTitle"],
    ['property="og:description"', "meta.ogDescription"],
    ['name="twitter:title"', "meta.twitterTitle"],
    ['name="twitter:description"', "meta.twitterDescription"],
  ];
  for (const [sel, key] of i18nKeyFix) {
    html = html.replace(
      new RegExp(`(${sel}[\\s\\S]*?data-i18n-content=")[^"]*(")`, "i"),
      `$1${key}$2`
    );
  }

  html = applyStaticI18n(html, en);

  html = html.replace(/\/js\/init-theme\.js\?v=\d+/g, "/js/init-theme.js?v=3");
  html = html.replace(/\/css\/style\.css\?v=\d+/g, "/css/style.css?v=24");
  html = html.replace(/\/js\/translations\.js\?v=\d+/g, "/js/translations.js?v=25");
  html = html.replace(/\/js\/site\.js\?v=\d+/g, "/js/site.js?v=9");
  html = html.replace(/\/js\/screens-lang\.js\?v=\d+/g, "/js/screens-lang.js?v=9");
  html = html.replace(/group-matters\.webp\?v=\d+/g, "group-matters.webp?v=3");
  html = html.replace(/class-record\/[^"?]+\.webp\?v=\d+/g, (m) => m.replace(/\?v=\d+/, "?v=12"));
  html = html.replace(/og-image\.png\?v=\d+/g, "og-image.png?v=6");

  await mkdir(path.join(root, "site/en"), { recursive: true });
  await writeFile(path.join(root, "site/en/index.html"), html, "utf8");
  console.log("Wrote site/en/index.html");
}

main().catch((err) => {
  console.error(err);
  process.exit(1);
});
