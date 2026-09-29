/* Swap homepage app screenshots by site language (zh default, en under /assets/screens/en/). */
(function () {
  "use strict";

  var SCREEN_ROOT = "/assets/screens/";
  var EN_PREFIX = "/assets/screens/en/";
  var IMG_VER = "3";

  function getLang() {
    var l = (document.documentElement.lang || "zh-CN").toLowerCase();
    return l.indexOf("en") === 0 ? "en" : "zh";
  }

  function stripQuery(url) {
    return (url || "").split("?")[0];
  }

  function withVersion(path) {
    return path + "?v=" + IMG_VER;
  }

  function isAppScreenshot(path) {
    return path.indexOf(SCREEN_ROOT) === 0 && path.indexOf(EN_PREFIX) !== 0;
  }

  function enPathFromZh(zhPath) {
    if (!isAppScreenshot(zhPath)) return null;
    return EN_PREFIX + zhPath.slice(SCREEN_ROOT.length);
  }

  function collectImages() {
    return Array.prototype.slice.call(
      document.querySelectorAll(".screenshot-gallery img[src*='/assets/screens/']")
    );
  }

  function ensureZhStored(img) {
    if (!img.dataset.srcZh) {
      var src = stripQuery(img.getAttribute("src") || "");
      if (src.indexOf(EN_PREFIX) === 0) {
        img.dataset.srcZh = SCREEN_ROOT + src.slice(EN_PREFIX.length);
      } else {
        img.dataset.srcZh = src;
      }
    }
    return img.dataset.srcZh;
  }

  function applyScreenshotLang(lang) {
    collectImages().forEach(function (img) {
      var zh = ensureZhStored(img);
      var desired = lang === "en" ? enPathFromZh(zh) || zh : zh;

      img.onerror = function () {
        var current = stripQuery(img.src);
        if (current !== zh) {
          img.onerror = null;
          img.src = withVersion(zh);
        }
      };

      img.src = withVersion(desired);
    });
  }

  document.addEventListener("etai:langchange", function (e) {
    var lang = (e.detail && e.detail.lang) || getLang();
    applyScreenshotLang(lang);
  });

  function boot() {
    applyScreenshotLang(getLang());
  }

  if (document.readyState === "loading") {
    document.addEventListener("DOMContentLoaded", boot);
  } else {
    boot();
  }

  window.ETAI_applyScreenshotLang = applyScreenshotLang;
})();
