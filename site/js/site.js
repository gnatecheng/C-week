/* Etai site — language, theme, and i18n application */
(function () {
  "use strict";

  var LANG_KEY = "etai-lang";
  var LANG_HINT_KEY = "etai-lang-hint-dismissed";
  var THEME_KEY = "etai-theme";
  var T = window.ETAI_TRANSLATIONS;

  function isEnglishPath() {
    var path = (location.pathname || "/").replace(/\/+$/, "") || "/";
    return path === "/en" || path.indexOf("/en/") === 0;
  }

  function pageLang() {
    var fromBody = document.body && document.body.getAttribute("data-page-lang");
    if (fromBody === "en" || fromBody === "zh") return fromBody;
    return isEnglishPath() ? "en" : "zh";
  }

  function getLang() {
    return pageLang();
  }

  function t(key, lang) {
    var bag = T && T[lang];
    if (bag && Object.prototype.hasOwnProperty.call(bag, key)) return bag[key];
    bag = T && T.zh;
    if (bag && Object.prototype.hasOwnProperty.call(bag, key)) return bag[key];
    return "";
  }

  var APP_PAGE_TITLE_KEYS = {
    "easy-ledger": "app.easyLedger.meta.title",
    "group-matters": "app.groupMatters.meta.title",
    "c-week": "app.cWeek.meta.title",
  };

  var APP_PAGE_DESC_KEYS = {
    "easy-ledger": "app.easyLedger.meta.description",
    "group-matters": "app.groupMatters.meta.description",
    "c-week": "app.cWeek.meta.description",
  };

  var APP_PAGE_OG_TITLE_KEYS = {
    "easy-ledger": "app.easyLedger.meta.ogTitle",
    "group-matters": "app.groupMatters.meta.ogTitle",
    "c-week": "app.cWeek.meta.ogTitle",
  };

  var APP_PAGE_OG_DESC_KEYS = {
    "easy-ledger": "app.easyLedger.meta.ogDescription",
    "group-matters": "app.groupMatters.meta.ogDescription",
    "c-week": "app.cWeek.meta.ogDescription",
  };

  function bodyMetaKey(attr, slugMap) {
    if (!document.body) return null;
    var direct = document.body.getAttribute(attr);
    if (direct) return direct;
    var slug = document.body.getAttribute("data-app-slug") || currentAppSlug();
    if (slug && slugMap && slugMap[slug]) return slugMap[slug];
    return null;
  }

  function setMetaContent(selector, value) {
    if (!value) return;
    document.querySelectorAll(selector).forEach(function (el) {
      el.setAttribute("content", value);
    });
  }

  function applyPageDocumentMeta(lang) {
    var titleKey = bodyMetaKey("data-page-title-key", APP_PAGE_TITLE_KEYS);
    if (titleKey) {
      var titleVal = t(titleKey, lang);
      if (titleVal) document.title = titleVal;
    } else if (!document.body.getAttribute("data-app-slug") && !currentAppSlug()) {
      var hubTitle = bodyMetaKey("data-page-title-key", null) || "meta.title";
      var hubVal = t(hubTitle, lang);
      if (hubVal) document.title = hubVal;
    }

    var descKey = bodyMetaKey("data-page-description-key", APP_PAGE_DESC_KEYS);
    var ogTitleKey = bodyMetaKey("data-page-og-title-key", APP_PAGE_OG_TITLE_KEYS);
    var ogDescKey = bodyMetaKey("data-page-og-description-key", APP_PAGE_OG_DESC_KEYS);
    if (descKey) setMetaContent('meta[name="description"]', t(descKey, lang));
    if (ogTitleKey) {
      var ogT = t(ogTitleKey, lang);
      setMetaContent('meta[property="og:title"]', ogT);
      setMetaContent('meta[name="twitter:title"]', ogT);
    }
    if (ogDescKey) {
      var ogD = t(ogDescKey, lang);
      setMetaContent('meta[property="og:description"]', ogD);
      setMetaContent('meta[name="twitter:description"]', ogD);
    }
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
    document.dispatchEvent(
      new CustomEvent("etai:themechange", { detail: { mode: mode, dark: dark } })
    );
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

    applyPageDocumentMeta(lang);

    syncLangControl(lang);
    syncThemeControl(localStorage.getItem(THEME_KEY) || "system");
    syncManifestLink(lang);
    syncLangHintLink(lang);

    document.dispatchEvent(new CustomEvent("etai:langchange", { detail: { lang: lang } }));
  }

  var APP_SLUGS = ["easy-ledger", "group-matters", "c-week"];

  function currentAppSlug() {
    var path = (location.pathname || "/").replace(/\/+$/, "") || "/";
    var parts = path.split("/").filter(Boolean);
    if (parts.length === 1 && APP_SLUGS.indexOf(parts[0]) >= 0) return parts[0];
    if (parts.length === 2 && parts[0] === "en" && APP_SLUGS.indexOf(parts[1]) >= 0) return parts[1];
    return null;
  }

  function langHomePath(lang) {
    var slug = currentAppSlug();
    if (slug) return (lang === "en" ? "/en/" : "/") + slug + "/";
    return lang === "en" ? "/en/" : "/";
  }

  function navigateLang(lang) {
    localStorage.setItem(LANG_KEY, lang);
    var target = langHomePath(lang) + (location.hash || "");
    if (pageLang() !== lang) {
      location.href = target;
      return;
    }
    applyI18n(lang);
  }

  function syncManifestLink(lang) {
    var link = document.querySelector('link[rel="manifest"]');
    if (!link) return;
    link.href =
      lang === "en" ? "/site.webmanifest.en.json?v=2" : "/site.webmanifest?v=2";
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

  function syncLangHintLink(lang) {
    var link = document.getElementById("lang-hint-link");
    if (!link) return;
    var hash = location.hash || "";
    link.href = "/en/" + hash;
  }

  function initLangHint() {
    if (pageLang() !== "zh") return;
    var bar = document.getElementById("lang-hint");
    if (!bar) return;
    if (localStorage.getItem(LANG_HINT_KEY) === "1") return;
    var nav = (navigator.language || navigator.userLanguage || "").toLowerCase();
    if (nav.indexOf("zh") === 0) return;
    bar.hidden = false;
    var dismiss = document.getElementById("lang-hint-dismiss");
    if (dismiss) {
      dismiss.addEventListener("click", function () {
        localStorage.setItem(LANG_HINT_KEY, "1");
        bar.hidden = true;
      });
    }
  }

  function initControls() {
    var zhBtn = document.getElementById("lang-zh");
    var enBtn = document.getElementById("lang-en");
    var themeBtn = document.getElementById("theme-toggle");
    if (zhBtn) zhBtn.addEventListener("click", function () { navigateLang("zh"); });
    if (enBtn) enBtn.addEventListener("click", function () { navigateLang("en"); });
    if (themeBtn) themeBtn.addEventListener("click", cycleTheme);

    if (window.matchMedia) {
      window.matchMedia("(prefers-color-scheme: dark)").addEventListener("change", function () {
        var mode = localStorage.getItem(THEME_KEY) || "system";
        if (mode === "system") {
          applyTheme("system");
          syncThemeControl("system");
        }
      });
    }
  }

  function normalizeLegacyHash() {
    if (location.hash !== "#class-record") return;
    var section = document.getElementById("group-matters");
    if (!section) return;
    section.scrollIntoView({ behavior: "auto", block: "start" });
    if (history.replaceState) {
      history.replaceState(null, "", location.pathname + location.search + "#group-matters");
    }
  }

  function init() {
    var lang = getLang();
    localStorage.setItem(LANG_KEY, lang);
    applyI18n(lang);
    applyTheme(localStorage.getItem(THEME_KEY) || "system");
    initControls();
    initLangHint();
    normalizeLegacyHash();
    window.addEventListener("hashchange", normalizeLegacyHash);
  }

  if (document.readyState === "loading") {
    document.addEventListener("DOMContentLoaded", init);
  } else {
    init();
  }
})();
