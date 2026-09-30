/* Apply stored theme & document language before first paint (CSP: synchronous in head). */
(function () {
  "use strict";
  var THEME_KEY = "etai-theme";
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

  var path = (location.pathname || "/").replace(/\/+$/, "") || "/";
  var lang = path === "/en" || path.indexOf("/en/") === 0 ? "en" : "zh";
  document.documentElement.lang = lang === "en" ? "en" : "zh-CN";
  document.documentElement.setAttribute("data-lang", lang);
  document.documentElement.classList.add("etai-screens-pending");
})();
