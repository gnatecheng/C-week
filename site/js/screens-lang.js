/* Homepage carousels: lang × theme screenshots via manifest (no 404 probing). */
(function () {
  "use strict";

  var SCREEN_ROOT = "/assets/screens/";
  var MANIFEST_URL = "/assets/screens/manifest.json?v=2";
  var IMG_VER = "6";

  var manifest = null;
  var manifestReady = null;

  function getLang() {
    var l = (document.documentElement.lang || "zh-CN").toLowerCase();
    return l.indexOf("en") === 0 ? "en" : "zh";
  }

  function getTheme() {
    return document.documentElement.classList.contains("theme-dark") ? "dark" : "light";
  }

  function stripQuery(url) {
    return (url || "").split("?")[0];
  }

  function withVersion(path) {
    return path + "?v=" + IMG_VER;
  }

  /** @returns {{ app: string, file: string }|null} */
  function parseSlide(rel) {
    if (!rel) return null;
    var slash = rel.indexOf("/");
    if (slash <= 0) return null;
    return { app: rel.slice(0, slash), file: rel.slice(slash + 1) };
  }

  /** @returns {string|null} e.g. cweek/01-home.webp */
  function parseRelativePath(path) {
    if (!path || path.indexOf(SCREEN_ROOT) !== 0) return null;
    var rest = path.slice(SCREEN_ROOT.length);
    var m = rest.match(/^(zh|en)\/(light|dark)\/(.+)$/);
    if (m) return m[3];
    if (rest.indexOf("en/") === 0) return rest.slice(3);
    return rest;
  }

  function loadManifest() {
    if (!manifestReady) {
      manifestReady = fetch(MANIFEST_URL)
        .then(function (res) {
          if (!res.ok) throw new Error("manifest " + res.status);
          return res.json();
        })
        .then(function (data) {
          manifest = data;
          return data;
        })
        .catch(function () {
          manifest = {
            available: {
              cweek: ["zh/light"],
              qingjizhang: ["zh/light", "zh/dark", "en/light", "en/dark"],
              "class-record": ["zh/light"],
            },
            legacyZhLight: true,
          };
          return manifest;
        });
    }
    return manifestReady;
  }

  function hasSet(app, lang, theme) {
    if (!manifest || !manifest.available) return false;
    var sets = manifest.available[app];
    if (!sets) return false;
    return sets.indexOf(lang + "/" + theme) >= 0;
  }

  function fallbackKeys(lang, theme) {
    var other = theme === "dark" ? "light" : "dark";
    var keys = [lang + "/" + theme, lang + "/" + other];
    if (lang !== "zh") keys.push("zh/" + theme);
    keys.push("zh/light");
    var seen = {};
    return keys.filter(function (k) {
      if (seen[k]) return false;
      seen[k] = true;
      return true;
    });
  }

  function pathForSet(app, file, lang, theme) {
    if (
      lang === "zh" &&
      theme === "light" &&
      manifest &&
      manifest.legacyZhLight !== false
    ) {
      return SCREEN_ROOT + app + "/" + file;
    }
    return SCREEN_ROOT + lang + "/" + theme + "/" + app + "/" + file;
  }

  function resolveSlideUrl(lang, theme, rel) {
    var slide = parseSlide(rel);
    if (!slide || !manifest) return null;
    var keys = fallbackKeys(lang, theme);
    for (var i = 0; i < keys.length; i++) {
      var parts = keys[i].split("/");
      if (hasSet(slide.app, parts[0], parts[1])) {
        return pathForSet(slide.app, slide.file, parts[0], parts[1]);
      }
    }
    return null;
  }

  function collectImages() {
    return Array.prototype.slice.call(
      document.querySelectorAll(".screenshot-gallery img[src*='/assets/screens/']")
    );
  }

  function ensureRelative(img) {
    if (!img.dataset.screenshotRel) {
      img.dataset.screenshotRel =
        parseRelativePath(stripQuery(img.getAttribute("src") || "")) || "";
    }
    return img.dataset.screenshotRel;
  }

  function applyScreenshots() {
    if (!manifest) return;
    var lang = getLang();
    var theme = getTheme();
    collectImages().forEach(function (img) {
      var rel = ensureRelative(img);
      if (!rel) return;
      var url = resolveSlideUrl(lang, theme, rel);
      if (!url) return;
      img.removeAttribute("onerror");
      var next = withVersion(url);
      if (stripQuery(img.getAttribute("src") || "") !== url) img.src = next;
    });
  }

  function onVariantChange() {
    loadManifest().then(applyScreenshots);
  }

  document.addEventListener("etai:langchange", onVariantChange);
  document.addEventListener("etai:themechange", onVariantChange);

  function boot() {
    loadManifest().then(applyScreenshots);
  }

  if (document.readyState === "loading") {
    document.addEventListener("DOMContentLoaded", boot);
  } else {
    boot();
  }

  window.ETAI_applyScreenshots = applyScreenshots;
  window.ETAI_resolveSlideUrl = resolveSlideUrl;
})();
