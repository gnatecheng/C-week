/* Apply stored theme & language before first paint (CSP: synchronous in head). */
(function () {
  "use strict";
  var THEME_KEY = "etai-theme";
  var LANG_KEY = "etai-lang";
  var storedTheme = localStorage.getItem(THEME_KEY);
  var mode =
    storedTheme === "light" || storedTheme === "dark" || storedTheme === "system"
      ? storedTheme
      : "system";
  var prefersDark =
    window.matchMedia && window.matchMedia("(prefers-color-scheme: dark)").matches;
  var dark = mode === "dark" || (mode === "system" && prefersDark);
  document.documentElement.setAttribute("data-theme", mode);
  document.documentElement.classList.toggle("theme-dark", dark);
  document.documentElement.classList.toggle("theme-light", !dark);

  var storedLang = localStorage.getItem(LANG_KEY);
  var lang =
    storedLang === "zh" || storedLang === "en"
      ? storedLang
      : (navigator.language || "").toLowerCase().indexOf("en") === 0
        ? "en"
        : "zh";
  document.documentElement.lang = lang === "en" ? "en" : "zh-CN";
  document.documentElement.setAttribute("data-lang", lang);
})();
