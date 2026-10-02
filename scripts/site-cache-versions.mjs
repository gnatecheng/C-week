/**
 * Single source of truth for static asset ?v= cache busting.
 * Import here in build scripts — do not hardcode lower values in replace() rules.
 */

export const CSS_V = 31;
export const TRANSLATIONS_V = 36;
export const SITE_JS_V = 11;
export const SECTION_NAV_JS_V = 6;
export const INIT_THEME_V = 4;
export const SCREENS_LANG_V = 10;
export const CAROUSEL_JS_V = 3;
export const GITHUB_META_JS_V = 6;
export const OG_IMAGE_V = 6;

/** Default ?v= for all slides under a legacy screen directory. */
export const SCREENSHOT_VER_DEFAULT = {
  qingjizhang: "10",
  "class-record": "12",
  cweek: "5",
};

/** Per-file overrides (path relative to screens root, e.g. cweek/01-home.webp). */
export const SCREENSHOT_VER_OVERRIDE = {
  "cweek/01-home.webp": "8",
};

/**
 * @param {string} legacyDir e.g. cweek, qingjizhang
 * @param {string} file e.g. 01-home.webp
 */
export function screenshotVer(legacyDir, file) {
  const rel = `${legacyDir}/${file}`;
  return SCREENSHOT_VER_OVERRIDE[rel] ?? SCREENSHOT_VER_DEFAULT[legacyDir] ?? "10";
}

/** Apply hub /en/ index cache-bust values (from zh index.html clone). */
export function applyHubEnCacheVersions(html) {
  let out = html;
  out = out.replace(/\/js\/init-theme\.js\?v=\d+/g, `/js/init-theme.js?v=${INIT_THEME_V}`);
  out = out.replace(/\/css\/style\.css\?v=\d+/g, `/css/style.css?v=${CSS_V}`);
  out = out.replace(/\/js\/translations\.js\?v=\d+/g, `/js/translations.js?v=${TRANSLATIONS_V}`);
  out = out.replace(/\/js\/site\.js\?v=\d+/g, `/js/site.js?v=${SITE_JS_V}`);
  out = out.replace(/\/js\/screens-lang\.js\?v=\d+/g, `/js/screens-lang.js?v=${SCREENS_LANG_V}`);
  out = out.replace(/group-matters\.webp\?v=\d+/g, "group-matters.webp?v=3");
  out = out.replace(/class-record\/[^"?]+\.webp\?v=\d+/g, (m) =>
    m.replace(/\?v=\d+/, `?v=${SCREENSHOT_VER_DEFAULT["class-record"]}`),
  );
  out = out.replace(/cweek\/01-home\.webp\?v=\d+/g, `cweek/01-home.webp?v=${SCREENSHOT_VER_OVERRIDE["cweek/01-home.webp"]}`);
  out = out.replace(/og-image\.png\?v=\d+/g, `og-image.png?v=${OG_IMAGE_V}`);
  return out;
}
