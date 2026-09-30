/**
 * Generate per-app landing pages (zh + en).
 * Run: node scripts/build-app-pages.mjs
 */
import { mkdir, readFile, writeFile } from "node:fs/promises";
import path from "node:path";
import { fileURLToPath } from "node:url";
import { APP_PAGES, GITHUB_ICON_PATH } from "./site-app-config.mjs";
import { loadTranslations, t } from "./static-i18n.mjs";
import { buildAppPageJsonLd } from "./site-seo-jsonld.mjs";

const root = path.resolve(path.dirname(fileURLToPath(import.meta.url)), "..");
const CSS_V = 23;
const IMG_VER = { qingjizhang: "10", "class-record": "12", cweek: "5" };

function escAttr(s) {
  return String(s).replace(/&/g, "&amp;").replace(/"/g, "&quot;");
}

function padSlide(n) {
  return String(n).padStart(2, "0");
}

function slideFile(slideIndex, screenDir) {
  const names = {
    1: "01-home.webp",
    2: screenDir === "cweek" ? "02-day1.webp" : "02-attendance.webp",
    3: screenDir === "cweek" ? "03-lesson.webp" : "03-payment.webp",
    4: screenDir === "cweek" ? "04-quiz.webp" : "04-ledger.webp",
    5: screenDir === "cweek" ? "05-labs.webp" : "05-members.webp",
    6: "06-report.webp",
  };
  return names[slideIndex];
}

function renderGallery(app, dict, lang) {
  const ver = IMG_VER[app.screenDir] || "10";
  const legacyBase = `/assets/screens/${app.legacyScreenDir}/`;
  const slides = [];
  for (let i = 1; i <= app.slideCount; i++) {
    const sn = padSlide(i);
    const altKey = `${app.galleryPrefix}.s${sn}.alt`;
    const capKey = `${app.galleryPrefix}.s${sn}.caption`;
    const file = slideFile(i, app.screenDir);
    slides.push(`                  <li class="screenshot-slide">
                    <figure class="screenshot-figure">
                      <div class="phone-frame screenshot-phone">
                        <div class="phone-notch"><span></span></div>
                        <div class="phone-screen">
                          <img src="${legacyBase}${file}?v=${ver}" width="540" height="1171" loading="lazy" alt="${escAttr(t(dict, altKey))}" />
                        </div>
                      </div>
                      <figcaption class="screenshot-caption">${t(dict, capKey)}</figcaption>
                    </figure>
                  </li>`);
  }
  return `              <div class="screenshot-gallery" role="region" aria-label="${escAttr(t(dict, app.keys.galleryAria))}">
                <ul class="screenshot-gallery-track">
${slides.join("\n")}
                </ul>
              </div>`;
}

function renderFaq(app, dict) {
  return app.faqKeys
    .map(([titleKey, bodyKey, isHtml]) => {
      const body = t(dict, bodyKey);
      const title = t(dict, titleKey);
      if (isHtml) {
        return `            <article class="faq-item">
              <h3>${title}</h3>
              <p>${body}</p>
            </article>`;
      }
      return `            <article class="faq-item">
              <h3>${title}</h3>
              <p>${body}</p>
            </article>`;
    })
    .join("\n");
}

function renderHighlights(app, dict) {
  const items = app.highlightKeys.map((key) => `              <li>${t(dict, key)}</li>`).join("\n");
  return `          <ul class="app-highlights">
${items}
          </ul>`;
}

function renderPage(app, lang, dict, T) {
  const isEn = lang === "en";
  const htmlLang = isEn ? "en" : "zh-CN";
  const basePath = isEn ? `/en/${app.slug}/` : `/${app.slug}/`;
  const canonical = `https://etais.dev${basePath}`;
  const zhPath = `https://etais.dev/${app.slug}/`;
  const enPath = `https://etais.dev/en/${app.slug}/`;
  const homeHref = isEn ? "/en/" : "/";
  const altHome = isEn ? "/" : "/en/";
  const siteName = t(dict, "meta.siteName");
  const name = t(dict, app.keys.name);
  const altName = t(isEn ? T.zh : T.en, app.keys.name);
  const metaTitle = t(dict, app.keys.metaTitle);
  const metaDesc = t(dict, app.keys.metaDescription);
  const ogTitle = t(dict, app.keys.ogTitle);
  const ogDesc = t(dict, app.keys.ogDescription);
  const jsonLd = JSON.stringify(buildAppPageJsonLd(canonical, dict, app, altName), null, 2);
  const learnOtherLang = isEn
    ? `<a class="app-lang-alt" href="${zhPath}">${t(dict, "prefs.langZh")}</a>`
    : `<a class="app-lang-alt" href="${enPath}">${t(dict, "prefs.langEn")}</a>`;

  return `<!DOCTYPE html>
<html lang="${htmlLang}">
  <head>
    <meta charset="utf-8" />
    <meta name="viewport" content="width=device-width, initial-scale=1" />
    <title>${escAttr(metaTitle)}</title>
    <meta name="description" content="${escAttr(metaDesc)}" />
    <link rel="alternate" hreflang="zh-CN" href="${zhPath}" />
    <link rel="alternate" hreflang="en" href="${enPath}" />
    <link rel="alternate" hreflang="x-default" href="${zhPath}" />
    <link rel="canonical" href="${canonical}" />
    <link rel="icon" href="/favicon.ico?v=1" sizes="any" />
    <link rel="icon" href="/assets/icon.svg?v=1" type="image/svg+xml" />
    <link rel="manifest" href="${isEn ? "/site.webmanifest.en.json?v=2" : "/site.webmanifest?v=3"}" />
    <meta name="theme-color" content="#0f766e" />
    <script src="/js/init-theme.js?v=3"></script>
    <link rel="stylesheet" href="/css/style.css?v=${CSS_V}" />
    <meta property="og:type" content="website" />
    <meta property="og:site_name" content="${escAttr(siteName)}" />
    <meta property="og:url" content="${canonical}" />
    <meta property="og:title" content="${escAttr(ogTitle)}" />
    <meta property="og:description" content="${escAttr(ogDesc)}" />
    <meta property="og:locale" content="${isEn ? "en_US" : "zh_CN"}" />
    <meta property="og:locale:alternate" content="${isEn ? "zh_CN" : "en_US"}" />
    <meta property="og:image" content="https://etais.dev/assets/og-image.png?v=6" />
    <meta property="og:image:width" content="1200" />
    <meta property="og:image:height" content="630" />
    <meta property="og:image:type" content="image/png" />
    <meta name="twitter:card" content="summary_large_image" />
    <meta name="twitter:title" content="${escAttr(ogTitle)}" />
    <meta name="twitter:description" content="${escAttr(ogDesc)}" />
    <meta name="twitter:image" content="https://etais.dev/assets/og-image.png?v=6" />
    <script type="application/ld+json" id="structured-data">
${jsonLd}
    </script>
  </head>
  <body class="app-page" data-page-lang="${isEn ? "en" : "zh"}" data-app-slug="${app.slug}">
    <a class="skip-link" href="#main">${t(dict, "skipLink")}</a>
    <header class="site-header app-page-header">
      <div class="wrap header-inner">
        <a class="brand" href="${homeHref}">
          <img src="/assets/icon.svg?v=1" width="36" height="36" alt="" />
          <span>${t(dict, "brand")}</span>
        </a>
        <div class="header-tools">
          <div class="site-prefs" id="lang-switch" role="group" aria-label="${escAttr(t(dict, "prefs.langLabel"))}">
            <button type="button" id="lang-zh" aria-pressed="${isEn ? "false" : "true"}">${t(dict, "prefs.langZh")}</button>
            <button type="button" id="lang-en" aria-pressed="${isEn ? "true" : "false"}">${t(dict, "prefs.langEn")}</button>
          </div>
          <button type="button" id="theme-toggle" aria-label="${escAttr(t(dict, "prefs.themeLabel"))}">
            <svg class="icon-sun" aria-hidden="true" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2"><circle cx="12" cy="12" r="4"/><path d="M12 2v2M12 20v2M4.93 4.93l1.41 1.41M17.66 17.66l1.41 1.41M2 12h2M20 12h2M4.93 19.07l1.41-1.41M17.66 6.34l1.41-1.41"/></svg>
            <svg class="icon-moon" aria-hidden="true" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2"><path d="M21 12.79A9 9 0 1 1 11.21 3 7 7 0 0 0 21 12.79z"/></svg>
            <svg class="icon-system" aria-hidden="true" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2"><rect x="2" y="3" width="20" height="14" rx="2"/><path d="M8 21h8M12 17v4"/></svg>
          </button>
        </div>
      </div>
    </header>
    <main id="main" class="app-page-main">
      <div class="wrap">
        <p class="app-page-breadcrumb"><a href="${homeHref}">${t(dict, "appPage.backHome")}</a> · ${learnOtherLang}</p>
        <header class="app-page-hero">
          <img class="app-page-icon" src="${app.iconSrc}" width="72" height="72" alt="" decoding="async" />
          <h1>${name}</h1>
          <p class="lead">${t(dict, app.keys.tagline)}</p>
        </header>
${renderHighlights(app, dict)}
        <div class="app-page-grid">
          <div class="app-page-primary">
            <div class="download-panel ${app.panelClass}" data-app-meta="${app.appMeta}">
              <div class="download-panel__body">
                <div class="download-panel__left">
                  <div class="download-panel__head">
                    <img class="download-panel__icon" src="${app.iconSrc}" width="44" height="44" alt="" decoding="async" />
                    <div>
                      <p class="download-panel__name">${name}</p>
                      <p class="download-panel__version">
                        v<span class="app-meta-version">${app.versionFallback}</span><span aria-hidden="true"> · </span
                        ><span>${t(dict, "download.androidMin")}</span><span aria-hidden="true"> · </span
                        ><span>${t(dict, "download.apkLabel")}</span>
                      </p>
                    </div>
                  </div>
                  <div class="download-panel__primary">
                    <a class="btn btn-primary" href="https://github.com/${app.github}/releases/latest" rel="noopener noreferrer">${t(dict, app.keys.downloadApk)}</a>
                    <p class="download-panel__note">${t(dict, "download.releasesNote")}</p>
                  </div>
                </div>
                <div class="download-panel__divider" role="presentation">
                  <div class="download-panel__divider-track">
                    <span class="download-panel__divider-label">${t(dict, "download.scanOr")}</span>
                  </div>
                </div>
                <div class="download-panel__scan">
                  <figure class="download-panel__qr">
                    <div class="download-panel__qr-frame">
                      <img src="${app.qrSrc}" width="120" height="120" alt="" decoding="async" />
                    </div>
                    <figcaption>${t(dict, "download.qrCaption")}</figcaption>
                  </figure>
                </div>
              </div>
            </div>
            <a class="download-panel__source" href="https://github.com/${app.github}" rel="noopener noreferrer">
              <svg class="icon-github" aria-hidden="true" viewBox="0 0 24 24" fill="currentColor"><path d="${GITHUB_ICON_PATH}" /></svg>
              <span>${t(dict, app.keys.viewGithub)}</span>
            </a>
          </div>
          <div class="hero-visual screenshot-wrap app-page-shots">
${renderGallery(app, dict, lang)}
          </div>
        </div>
        <section class="app-page-faq" aria-labelledby="app-faq-title">
          <h2 id="app-faq-title">${t(dict, "appPage.faqTitle")}</h2>
          <div class="faq-list">
${renderFaq(app, dict)}
          </div>
        </section>
        <p class="app-page-back"><a class="btn btn-secondary" href="${homeHref}">${t(dict, "appPage.backHome")}</a></p>
      </div>
    </main>
    <footer class="site-footer">
      <div class="wrap inner">
        <p>${t(dict, "footer.copyright")}</p>
      </div>
    </footer>
    <script src="/js/translations.js?v=25" defer></script>
    <script src="/js/site.js?v=9" defer></script>
    <script src="/js/screens-lang.js?v=9" defer></script>
    <script src="/js/carousel.js?v=3" defer></script>
    <script src="/js/github-meta.js?v=6" defer></script>
  </body>
</html>
`;
}

async function main() {
  const T = await loadTranslations(readFile, root);
  for (const app of APP_PAGES) {
    const zhHtml = renderPage(app, "zh", T.zh, T);
    const enHtml = renderPage(app, "en", T.en, T);
    const zhDir = path.join(root, "site", app.slug);
    const enDir = path.join(root, "site", "en", app.slug);
    await mkdir(zhDir, { recursive: true });
    await mkdir(enDir, { recursive: true });
    await writeFile(path.join(zhDir, "index.html"), zhHtml, "utf8");
    await writeFile(path.join(enDir, "index.html"), enHtml, "utf8");
    console.log("Wrote", app.slug, "zh+en");
  }
}

main().catch((err) => {
  console.error(err);
  process.exit(1);
});
