/** Shared HTML section renderers for per-app landing pages. */

export function escHtml(s) {
  return String(s)
    .replace(/&/g, "&amp;")
    .replace(/</g, "&lt;")
    .replace(/>/g, "&gt;")
    .replace(/"/g, "&quot;");
}

/** Default static src for SEO (zh/light legacy or en/light); JS swaps lang × theme. */
export function screenDefaultSrc(isEn, legacyDir, file, ver) {
  if (isEn) {
    return `/assets/screens/en/light/${legacyDir}/${file}?v=${ver}`;
  }
  return `/assets/screens/${legacyDir}/${file}?v=${ver}`;
}

export function screenshotRel(legacyDir, file) {
  return `${legacyDir}/${file}`;
}

export function renderFeatureGrid(dict, t, cards, footKey) {
  const items = cards
    .map(([tagKey, titleKey, bodyKey]) => {
      const tag = tagKey ? `<span class="tag">${t(dict, tagKey)}</span>\n              ` : "";
      return `            <article class="card">
              ${tag}
              <h3>${t(dict, titleKey)}</h3>
              <p>${t(dict, bodyKey)}</p>
            </article>`;
    })
    .join("\n");
  const foot = footKey
    ? `          <p class="project-foot">${t(dict, footKey)}</p>`
    : "";
  return `      <section id="features" class="project app-page-block" aria-labelledby="features-title">
        <div class="wrap">
          <h2 id="features-title" class="project-subhead">${t(dict, "appPage.section.fullFeatures")}</h2>
          <div class="feature-grid">
${items}
          </div>
${foot}
        </div>
      </section>`;
}

export function renderRoadmap(dict, t) {
  const days = ["d1", "d2", "d3", "d4", "d5", "d6", "d7"];
  const cards = days
    .map((d, i) => {
      const num = i + 1;
      return `            <article class="card day-card">
              <div class="day-num">D${num}</div>
              <div class="day-body">
                <h3>${t(dict, `roadmap.${d}.title`)}</h3>
                <p class="day-sub">${t(dict, `roadmap.${d}.sub`)}</p>
                <p class="day-outcome">${t(dict, `roadmap.${d}.outcome`)}</p>
                <span class="day-lab">${t(dict, `roadmap.${d}.lab`)}</span>
              </div>
            </article>`;
    })
    .join("\n");
  return `      <section id="roadmap" class="app-page-block" aria-labelledby="roadmap-title">
        <div class="wrap">
          <header class="section-head">
            <h2 id="roadmap-title">${t(dict, "roadmap.title")}</h2>
            <p>${t(dict, "roadmap.lead")}</p>
          </header>
          <div class="roadmap">
${cards}
          </div>
        </div>
      </section>`;
}

export function renderAllScreens(app, dict, t, isEn, verForFile) {
  const slides = [];
  for (let i = 1; i <= app.slideCount; i++) {
    const sn = String(i).padStart(2, "0");
    const altKey = `${app.galleryPrefix}.s${sn}.alt`;
    const capKey = `${app.galleryPrefix}.s${sn}.caption`;
    const file = app.slideFile(i);
    const ver =
      typeof verForFile === "function"
        ? verForFile(app.legacyScreenDir, file)
        : verForFile;
    const src = screenDefaultSrc(isEn, app.legacyScreenDir, file, ver);
    const rel = screenshotRel(app.legacyScreenDir, file);
    slides.push(`            <figure class="app-screens-grid__item">
              <div class="phone-frame screenshot-phone">
                <div class="phone-notch"><span></span></div>
                <div class="phone-screen">
                  <img src="${src}" width="540" height="1171" loading="lazy" data-screenshot-rel="${rel}" alt="${escHtml(t(dict, altKey))}" />
                </div>
              </div>
              <figcaption class="screenshot-caption">${t(dict, capKey)}</figcaption>
            </figure>`);
  }
  const themeNote = t(dict, isEn ? "appPage.section.screensThemeEn" : "appPage.section.screensThemeZh");
  return `      <section id="screenshots" class="app-page-block" aria-labelledby="screenshots-title">
        <div class="wrap">
          <header class="section-head">
            <h2 id="screenshots-title">${t(dict, "appPage.section.allScreens")}</h2>
            <p>${themeNote}</p>
          </header>
          <div class="app-screens-grid">
${slides.join("\n")}
          </div>
        </div>
      </section>`;
}

export function renderProseSection(id, titleKey, bodyKey, dict, t) {
  return `      <section id="${id}" class="app-page-block" aria-labelledby="${id}-title">
        <div class="wrap">
          <h2 id="${id}-title" class="project-subhead">${t(dict, titleKey)}</h2>
          <div class="app-prose">${t(dict, bodyKey)}</div>
        </div>
      </section>`;
}

export function renderChangelog(app, dict, t) {
  const blocks = app.changelog
    .map((rel) => {
      const items = rel.itemKeys.map((k) => `<li>${t(dict, k)}</li>`).join("\n");
      return `            <article class="changelog-release">
              <h3><a href="${rel.url}" rel="noopener noreferrer">v${rel.version}</a></h3>
              <ul>
${items}
              </ul>
            </article>`;
    })
    .join("\n");
  return `      <section id="changelog" class="app-page-block" aria-labelledby="changelog-title">
        <div class="wrap">
          <header class="section-head">
            <h2 id="changelog-title">${t(dict, "appPage.section.changelog")}</h2>
            <p>${t(dict, "appPage.section.changelogLead")} <a href="https://github.com/${app.github}/releases" rel="noopener noreferrer">${t(dict, "appPage.changelog.allReleases")}</a>.</p>
          </header>
          <div class="changelog-list">
${blocks}
          </div>
        </div>
      </section>`;
}

export function renderFaqList(app, dict, t) {
  return app.faqKeys
    .map(([titleKey, bodyKey, asHtml]) => {
      const body = t(dict, bodyKey);
      const title = t(dict, titleKey);
      const bodyEl = asHtml ? `<div class="faq-body">${body}</div>` : `<p>${body}</p>`;
      return `            <article class="faq-item">
              <h3>${title}</h3>
              ${bodyEl}
            </article>`;
    })
    .join("\n");
}
