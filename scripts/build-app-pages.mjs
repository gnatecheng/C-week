/**
 * Generate per-app landing pages (zh + en) — layout matches homepage app sections.
 * Run: node scripts/build-app-pages.mjs
 */
import { mkdir, readFile, writeFile } from "node:fs/promises";
import path from "node:path";
import { fileURLToPath } from "node:url";
import { APP_PAGES, GITHUB_ICON_PATH } from "./site-app-config.mjs";
import { loadTranslations, t } from "./static-i18n.mjs";
import { buildAppPageJsonLd } from "./site-seo-jsonld.mjs";
import {
  renderAllScreens,
  renderChangelog,
  renderFeatureGrid,
  renderFaqList,
  renderProseSection,
  renderRoadmap,
} from "./app-page-sections.mjs";

const root = path.resolve(path.dirname(fileURLToPath(import.meta.url)), "..");
const CSS_V = 26;
const TRANSLATIONS_V = 28;
const IMG_VER = { qingjizhang: "10", "class-record": "12", cweek: "5" };

function escAttrLocal(s) {
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

function renderGallery(app, dict) {
  const ver = IMG_VER[app.legacyScreenDir] || "10";
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
                          <img src="${legacyBase}${file}?v=${ver}" width="540" height="1171" loading="lazy" alt="${escAttrLocal(t(dict, altKey))}" />
                        </div>
                      </div>
                      <figcaption class="screenshot-caption">${t(dict, capKey)}</figcaption>
                    </figure>
                  </li>`);
  }
  return `              <div class="screenshot-gallery" role="region" aria-label="${escAttrLocal(t(dict, app.keys.galleryAria))}">
                <ul class="screenshot-gallery-track">
${slides.join("\n")}
                </ul>
              </div>`;
}

function renderNavChip(slug, isEn, dict, activeSlug) {
  const base = isEn ? "/en" : "";
  const href = `${base}/${slug}/`;
  const current = slug === activeSlug ? ' aria-current="page"' : "";
  const labels = {
    "easy-ledger": ["chip-qjz", "账", "nav.qingjizhang"],
    "group-matters": ["chip-class", "团", "nav.classRecord"],
    "c-week": ["chip-cweek", "C", "nav.cweek"],
  };
  const [cls, badge, key] = labels[slug];
  return `<a class="app-chip ${cls}" href="${href}"${current}><span class="chip-badge" aria-hidden="true">${badge}</span><span>${t(dict, key)}</span></a>`;
}

function renderSiteHeader(isEn, dict, activeSlug) {
  const homeHref = isEn ? "/en/" : "/";
  return `    <header class="site-header">
      <div class="wrap header-shell">
        <div class="brand-block">
          <a class="brand" href="${homeHref}">
            <img src="/assets/icon.svg?v=2" width="36" height="36" alt="${escAttrLocal(t(dict, "meta.brandIconAlt"))}" />
            <span>${t(dict, "brand")}</span>
          </a>
          <p class="brand-tagline">
            <span class="brand-tagline-full">${t(dict, "header.tagline")}</span>
            <span class="brand-tagline-short">${t(dict, "header.taglineShort")}</span>
          </p>
        </div>
        <nav class="header-nav" aria-label="${escAttrLocal(t(dict, "navSectionsLabel"))}">
          <div class="header-nav-inner">
            ${renderNavChip("easy-ledger", isEn, dict, activeSlug)}
            ${renderNavChip("group-matters", isEn, dict, activeSlug)}
            ${renderNavChip("c-week", isEn, dict, activeSlug)}
            <a class="nav-link" href="#faq">${t(dict, "nav.faq")}</a>
          </div>
        </nav>
        <div class="header-tools">
          <div class="site-prefs" id="lang-switch" role="group" aria-label="${escAttrLocal(t(dict, "prefs.langLabel"))}">
            <button type="button" id="lang-zh" aria-pressed="${isEn ? "false" : "true"}">${t(dict, "prefs.langZh")}</button>
            <button type="button" id="lang-en" aria-pressed="${isEn ? "true" : "false"}">${t(dict, "prefs.langEn")}</button>
          </div>
          <button type="button" id="theme-toggle" aria-label="${escAttrLocal(t(dict, "prefs.themeLabel"))}">
            <svg class="icon-sun" aria-hidden="true" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2"><circle cx="12" cy="12" r="4"/><path d="M12 2v2M12 20v2M4.93 4.93l1.41 1.41M17.66 17.66l1.41 1.41M2 12h2M20 12h2M4.93 19.07l1.41-1.41M17.66 6.34l1.41-1.41"/></svg>
            <svg class="icon-moon" aria-hidden="true" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2"><path d="M21 12.79A9 9 0 1 1 11.21 3 7 7 0 0 0 21 12.79z"/></svg>
            <svg class="icon-system" aria-hidden="true" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2"><rect x="2" y="3" width="20" height="14" rx="2"/><path d="M8 21h8M12 17v4"/></svg>
          </button>
        </div>
      </div>
    </header>`;
}

function renderDownloadPanel(app, dict) {
  const name = t(dict, app.keys.name);
  return `              <div class="download-panel ${app.panelClass}" data-app-meta="${app.appMeta}">
                <div class="download-panel__body">
                  <div class="download-panel__left">
                  <div class="download-panel__head">
                  <img
                    class="download-panel__icon"
                    src="${app.iconSrc}"
                    width="44"
                    height="44"
                    alt="${escAttrLocal(name)}"
                    decoding="async"
                  />
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
                    <a
                      class="btn btn-primary"
                      href="https://github.com/${app.github}/releases/latest"
                      rel="noopener noreferrer"
                    >
                      ${t(dict, app.keys.downloadApk)}
                    </a>
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
                        <img src="${app.qrSrc}" width="120" height="120" alt="${escAttrLocal(t(dict, app.qrAltKey))}" decoding="async" />
                      </div>
                      <figcaption>${t(dict, "download.qrCaption")}</figcaption>
                    </figure>
                  </div>
                </div>
              </div>
              <a
                class="download-panel__source"
                href="https://github.com/${app.github}"
                rel="noopener noreferrer"
              >
                <svg class="icon-github" aria-hidden="true" viewBox="0 0 24 24" fill="currentColor">
                  <path d="${GITHUB_ICON_PATH}" />
                </svg>
                <span>${t(dict, app.keys.viewGithub)}</span>
              </a>`;
}

function renderFooter(dict) {
  return `    <footer class="site-footer">
      <div class="wrap inner">
        <p>${t(dict, "footer.copyright")}</p>
        <p class="footer-links">
          <a href="https://github.com/gnatecheng/c-week" rel="noopener noreferrer"
            ><svg class="icon-github" aria-hidden="true" viewBox="0 0 24 24" fill="currentColor"
              ><path d="${GITHUB_ICON_PATH}" /></svg
            ><span>${t(dict, "footer.linkCweek")}</span></a
          >
          <a href="https://github.com/gnatecheng/easy-ledger" rel="noopener noreferrer"
            ><svg class="icon-github" aria-hidden="true" viewBox="0 0 24 24" fill="currentColor"
              ><path d="${GITHUB_ICON_PATH}" /></svg
            ><span>${t(dict, "footer.linkQjz")}</span></a
          >
          <a href="https://github.com/gnatecheng/group-matters" rel="noopener noreferrer"
            ><svg class="icon-github" aria-hidden="true" viewBox="0 0 24 24" fill="currentColor"
              ><path d="${GITHUB_ICON_PATH}" /></svg
            ><span>${t(dict, "footer.linkClass")}</span></a
          >
        </p>
      </div>
    </footer>`;
}

function renderPage(app, lang, dict, T) {
  const isEn = lang === "en";
  const htmlLang = isEn ? "en" : "zh-CN";
  const basePath = isEn ? `/en/${app.slug}/` : `/${app.slug}/`;
  const canonical = `https://etais.dev${basePath}`;
  const zhPath = `https://etais.dev/${app.slug}/`;
  const enPath = `https://etais.dev/en/${app.slug}/`;
  const name = t(dict, app.keys.name);
  const altName = t(isEn ? T.zh : T.en, app.keys.name);
  const metaTitle = t(dict, app.keys.metaTitle);
  const metaDesc = t(dict, app.keys.metaDescription);
  const ogTitle = t(dict, app.keys.ogTitle);
  const ogDesc = t(dict, app.keys.ogDescription);
  const jsonLd = JSON.stringify(buildAppPageJsonLd(canonical, dict, app, altName), null, 2);
  const titleId = `${app.slug.replace(/-/g, "")}-title`;
  const gridWrap =
    app.gridClass === "hero-grid"
      ? `<div class="wrap hero-grid">`
      : `<div class="wrap">
          <div class="project-grid">`;
  const gridClose = app.gridClass === "hero-grid" ? `        </div>` : `          </div>
        </div>`;

  const ver = IMG_VER[app.legacyScreenDir] || "10";
  app.slideFile = (i) => slideFile(i, app.screenDir);

  const sections = [
    renderFeatureGrid(dict, t, app.featureCards, app.footKey),
    app.showRoadmap ? renderRoadmap(dict, t) : "",
    renderAllScreens(app, dict, t, isEn, ver),
    renderProseSection("privacy", "appPage.section.privacy", app.privacyBodyKey, dict, t),
    renderProseSection("requirements", "appPage.section.requirements", app.requirementsBodyKey, dict, t),
    renderChangelog(app, dict, t),
  ].filter(Boolean);

  return `<!DOCTYPE html>
<html lang="${htmlLang}">
  <head>
    <meta charset="utf-8" />
    <meta name="viewport" content="width=device-width, initial-scale=1" />
    <title>${escAttrLocal(metaTitle)}</title>
    <meta name="description" content="${escAttrLocal(metaDesc)}" />
    <link rel="alternate" hreflang="zh-CN" href="${zhPath}" />
    <link rel="alternate" hreflang="en" href="${enPath}" />
    <link rel="alternate" hreflang="x-default" href="${zhPath}" />
    <link rel="canonical" href="${canonical}" />
    <link rel="icon" href="/favicon.ico?v=1" sizes="any" />
    <link rel="icon" href="/assets/icon.svg?v=2" type="image/svg+xml" />
    <link rel="manifest" href="${isEn ? "/site.webmanifest.en.json?v=2" : "/site.webmanifest?v=3"}" />
    <meta name="theme-color" content="#0f766e" />
    <script src="/js/init-theme.js?v=3"></script>
    <link rel="stylesheet" href="/css/style.css?v=${CSS_V}" />
    <meta property="og:type" content="website" />
    <meta property="og:site_name" content="${escAttrLocal(t(dict, "meta.siteName"))}" />
    <meta property="og:url" content="${canonical}" />
    <meta property="og:title" content="${escAttrLocal(ogTitle)}" />
    <meta property="og:description" content="${escAttrLocal(ogDesc)}" />
    <meta property="og:locale" content="${isEn ? "en_US" : "zh_CN"}" />
    <meta property="og:locale:alternate" content="${isEn ? "zh_CN" : "en_US"}" />
    <meta property="og:image" content="https://etais.dev/assets/og-image.png?v=6" />
    <meta property="og:image:width" content="1200" />
    <meta property="og:image:height" content="630" />
    <meta property="og:image:type" content="image/png" />
    <meta name="twitter:card" content="summary_large_image" />
    <meta name="twitter:title" content="${escAttrLocal(ogTitle)}" />
    <meta name="twitter:description" content="${escAttrLocal(ogDesc)}" />
    <meta name="twitter:image" content="https://etais.dev/assets/og-image.png?v=6" />
    <script type="application/ld+json" id="structured-data">
${jsonLd}
    </script>
  </head>
  <body data-page-lang="${isEn ? "en" : "zh"}" data-app-slug="${app.slug}">
    <a class="skip-link" href="#main">${t(dict, "skipLink")}</a>
${renderSiteHeader(isEn, dict, app.slug)}
    <main id="main">
      <section class="${app.sectionClass}" data-group="${app.appMeta}" aria-labelledby="${titleId}">
${gridWrap}
          <div>
            <h1 id="${titleId}" class="${app.titleClass}">${name}</h1>
            <p class="lead">${t(dict, app.keys.tagline)}</p>
${renderDownloadPanel(app, dict)}
          </div>
          <div class="hero-visual screenshot-wrap">
${renderGallery(app, dict)}
          </div>
${gridClose}
      </section>
${sections.join("\n")}
      <section id="faq" data-group="faq">
        <div class="wrap">
          <header class="section-head">
            <h2>${t(dict, "appPage.faqTitle")}</h2>
          </header>
          <div class="faq-list">
${renderFaqList(app, dict, t)}
          </div>
        </div>
      </section>
    </main>
${renderFooter(dict)}
    <script src="/js/translations.js?v=${TRANSLATIONS_V}" defer></script>
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
