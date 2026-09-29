/* Etai site — language, theme, and i18n application */
(function () {
  "use strict";

  var LANG_KEY = "etai-lang";
  var THEME_KEY = "etai-theme";
  var T = window.ETAI_TRANSLATIONS;

  function resolveLang(stored) {
    if (stored === "zh" || stored === "en") return stored;
    var nav = (navigator.language || navigator.userLanguage || "").toLowerCase();
    return nav.indexOf("en") === 0 ? "en" : "zh";
  }

  function getLang() {
    return resolveLang(localStorage.getItem(LANG_KEY));
  }

  function t(key, lang) {
    var bag = T && T[lang];
    if (bag && Object.prototype.hasOwnProperty.call(bag, key)) return bag[key];
    bag = T && T.zh;
    if (bag && Object.prototype.hasOwnProperty.call(bag, key)) return bag[key];
    return "";
  }

  function applyTheme(mode) {
    var prefersDark =
      window.matchMedia && window.matchMedia("(prefers-color-scheme: dark)").matches;
    var dark = mode === "dark" || (mode === "system" && prefersDark);
    document.documentElement.setAttribute("data-theme", mode);
    document.documentElement.classList.toggle("theme-dark", dark);
    document.documentElement.classList.toggle("theme-light", !dark);
    var metaTheme = document.querySelector('meta[name="theme-color"]');
    if (metaTheme) metaTheme.setAttribute("content", dark ? "#152125" : "#0f766e");
  }

  function cycleTheme() {
    var order = ["light", "dark", "system"];
    var cur = localStorage.getItem(THEME_KEY) || "system";
    var idx = order.indexOf(cur);
    var next = order[(idx + 1) % order.length];
    localStorage.setItem(THEME_KEY, next);
    applyTheme(next);
    syncThemeControl(next);
  }

  function syncThemeControl(mode) {
    var btn = document.getElementById("theme-toggle");
    if (!btn) return;
    var lang = getLang();
    var label =
      mode === "light"
        ? t("prefs.themeLight", lang)
        : mode === "dark"
          ? t("prefs.themeDark", lang)
          : t("prefs.themeSystem", lang);
    btn.setAttribute("aria-label", t("prefs.themeLabel", lang) + ": " + label);
    btn.setAttribute("title", label);
    btn.setAttribute("data-theme-mode", mode);
  }

  function applyI18n(lang) {
    if (!T) return;
    document.documentElement.lang = lang === "en" ? "en" : "zh-CN";

    document.querySelectorAll("[data-i18n]").forEach(function (el) {
      var key = el.getAttribute("data-i18n");
      var val = t(key, lang);
      if (val) el.textContent = val;
    });

    document.querySelectorAll("[data-i18n-html]").forEach(function (el) {
      var key = el.getAttribute("data-i18n-html");
      var val = t(key, lang);
      if (val) el.innerHTML = val;
    });

    document.querySelectorAll("[data-i18n-content]").forEach(function (el) {
      var key = el.getAttribute("data-i18n-content");
      var val = t(key, lang);
      if (val) el.setAttribute("content", val);
    });

    document.querySelectorAll("[data-i18n-alt]").forEach(function (el) {
      var key = el.getAttribute("data-i18n-alt");
      var val = t(key, lang);
      if (val) el.setAttribute("alt", val);
    });

    document.querySelectorAll("[data-i18n-aria]").forEach(function (el) {
      var key = el.getAttribute("data-i18n-aria");
      var val = t(key, lang);
      if (val) el.setAttribute("aria-label", val);
    });

    var titleKey = document.body.getAttribute("data-page-title-key") || "meta.title";
    document.title = t(titleKey, lang);

    var desc = document.querySelector('meta[name="description"]');
    if (desc) {
      var dk = desc.getAttribute("data-i18n-content") || "meta.description";
      var dv = t(dk, lang);
      if (dv) desc.setAttribute("content", dv);
    }

    syncLangControl(lang);
    syncThemeControl(localStorage.getItem(THEME_KEY) || "system");

    document.dispatchEvent(new CustomEvent("etai:langchange", { detail: { lang: lang } }));
  }

  function setLang(lang) {
    localStorage.setItem(LANG_KEY, lang);
    applyI18n(lang);
  }

  function syncLangControl(lang) {
    var zhBtn = document.getElementById("lang-zh");
    var enBtn = document.getElementById("lang-en");
    if (zhBtn) {
      zhBtn.setAttribute("aria-pressed", lang === "zh" ? "true" : "false");
      zhBtn.textContent = t("prefs.langZh", lang);
    }
    if (enBtn) {
      enBtn.setAttribute("aria-pressed", lang === "en" ? "true" : "false");
      enBtn.textContent = t("prefs.langEn", lang);
    }
    var group = document.getElementById("lang-switch");
    if (group) group.setAttribute("aria-label", t("prefs.langLabel", lang));
  }

  function initControls() {
    var zhBtn = document.getElementById("lang-zh");
    var enBtn = document.getElementById("lang-en");
    var themeBtn = document.getElementById("theme-toggle");
    if (zhBtn) zhBtn.addEventListener("click", function () { setLang("zh"); });
    if (enBtn) enBtn.addEventListener("click", function () { setLang("en"); });
    if (themeBtn) themeBtn.addEventListener("click", cycleTheme);

    if (window.matchMedia) {
      window.matchMedia("(prefers-color-scheme: dark)").addEventListener("change", function () {
        var mode = localStorage.getItem(THEME_KEY) || "system";
        if (mode === "system") applyTheme("system");
      });
    }
  }

  function init() {
    var lang = getLang();
    applyI18n(lang);
    applyTheme(localStorage.getItem(THEME_KEY) || "system");
    initControls();
  }

  if (document.readyState === "loading") {
    document.addEventListener("DOMContentLoaded", init);
  } else {
    init();
  }
})();
