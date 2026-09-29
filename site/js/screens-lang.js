/* Homepage carousels: lang × theme screenshot sets with chained fallback (CSP-safe). */
(function () {
  "use strict";

  var SCREEN_ROOT = "/assets/screens/";
  var LEGACY_EN = "/assets/screens/en/";
  var IMG_VER = "4";

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

  /** @returns {string|null} e.g. cweek/01-home.webp */
  function parseRelativePath(path) {
    if (!path || path.indexOf(SCREEN_ROOT) !== 0) return null;
    var rest = path.slice(SCREEN_ROOT.length);
    var m = rest.match(/^(zh|en)\/(light|dark)\/(.+)$/);
    if (m) return m[3];
    if (rest.indexOf("en/") === 0) return rest.slice(3);
    return rest;
  }

  function buildCandidates(lang, theme, rel) {
    var other = theme === "dark" ? "light" : "dark";
    var list = [
      SCREEN_ROOT + lang + "/" + theme + "/" + rel,
      SCREEN_ROOT + lang + "/" + other + "/" + rel,
    ];
    if (lang !== "zh") {
      list.push(SCREEN_ROOT + "zh/" + theme + "/" + rel);
    }
    list.push(SCREEN_ROOT + "zh/light/" + rel);
    list.push(SCREEN_ROOT + rel);
    if (lang === "en") {
      list.push(LEGACY_EN + rel);
    }
    var seen = {};
    return list.filter(function (p) {
      if (seen[p]) return false;
      seen[p] = true;
      return true;
    });
  }

  function collectImages() {
    return Array.prototype.slice.call(
      document.querySelectorAll(".screenshot-gallery img[src*='/assets/screens/']")
    );
  }

  function ensureRelative(img) {
    if (!img.dataset.screenshotRel) {
      img.dataset.screenshotRel = parseRelativePath(stripQuery(img.getAttribute("src") || "")) || "";
    }
    return img.dataset.screenshotRel;
  }

  function loadWithFallback(img, candidates) {
    var idx = 0;
    function tryNext() {
      if (idx >= candidates.length) return;
      var next = candidates[idx++];
      img.onerror = function () {
        tryNext();
      };
      img.src = withVersion(next);
    }
    tryNext();
  }

  function applyScreenshots() {
    var lang = getLang();
    var theme = getTheme();
    collectImages().forEach(function (img) {
      var rel = ensureRelative(img);
      if (!rel) return;
      loadWithFallback(img, buildCandidates(lang, theme, rel));
    });
  }

  document.addEventListener("etai:langchange", applyScreenshots);
  document.addEventListener("etai:themechange", applyScreenshots);

  function boot() {
    applyScreenshots();
  }

  if (document.readyState === "loading") {
    document.addEventListener("DOMContentLoaded", boot);
  } else {
    boot();
  }

  window.ETAI_applyScreenshots = applyScreenshots;
  window.ETAI_screenshotCandidates = buildCandidates;
})();
