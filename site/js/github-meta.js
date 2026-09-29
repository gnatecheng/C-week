/* Fetch latest GitHub release version & date for app badges (graceful fallback). */
(function () {
  "use strict";

  var REPOS = {
    cweek: "gnatecheng/C-week",
    qingjizhang: "gnatecheng/qingjizhang",
    "class-record": "gnatecheng/class-activity-record",
  };

  function formatDate(iso, lang) {
    if (!iso) return "";
    try {
      var d = new Date(iso);
      return d.toLocaleDateString(lang === "en" ? "en-US" : "zh-CN", {
        year: "numeric",
        month: "short",
        day: "numeric",
      });
    } catch (e) {
      return "";
    }
  }

  function applyMeta(group, data, lang) {
    var root = document.querySelector('[data-app-meta="' + group + '"]');
    if (!root) return;
    var verEl = root.querySelector(".app-meta-version");
    var dateEl = root.querySelector(".app-meta-updated");
    if (data && data.tag_name && verEl) {
      var v = data.tag_name.replace(/^v/i, "");
      verEl.textContent = v;
    }
    if (data && data.published_at && dateEl) {
      dateEl.textContent = formatDate(data.published_at, lang);
      var wrap = root.querySelector(".app-meta-date-wrap");
      if (wrap) wrap.hidden = false;
    }
  }

  function fetchRepo(fullName) {
    return fetch("https://api.github.com/repos/" + fullName + "/releases/latest", {
      headers: { Accept: "application/vnd.github+json" },
    }).then(function (res) {
      if (!res.ok) throw new Error("github " + res.status);
      return res.json();
    });
  }

  function refreshAll(lang) {
    Object.keys(REPOS).forEach(function (group) {
      fetchRepo(REPOS[group])
        .then(function (data) {
          applyMeta(group, data, lang);
        })
        .catch(function () {
          /* keep HTML fallback values */
        });
    });
  }

  window.ETAI_refreshGithubMeta = refreshAll;

  document.addEventListener("etai:langchange", function (e) {
    refreshAll((e.detail && e.detail.lang) || "zh");
  });

  if (document.readyState === "loading") {
    document.addEventListener("DOMContentLoaded", function () {
      refreshAll(document.documentElement.lang === "en" ? "en" : "zh");
    });
  } else {
    refreshAll(document.documentElement.lang === "en" ? "en" : "zh");
  }
})();
