/* Fetch GitHub release version & date — prefer semver tags, ignore date-like tags. */
(function () {
  "use strict";

  var REPOS = {
    cweek: "gnatecheng/c-week",
    qingjizhang: "gnatecheng/easy-ledger",
    "class-record": "gnatecheng/group-matters",
  };

  /** @param {string} tag e.g. v1.4.0 or v20260928 */
  function isSemverReleaseTag(tag) {
    if (!tag || typeof tag !== "string") return false;
    var body = tag.replace(/^v/i, "").trim();
    if (/^20\d{6}$/.test(body) || /^\d{8}$/.test(body)) return false;
    return /^\d+\.\d+\.\d+(-[0-9A-Za-z.-]+)?(\+[0-9A-Za-z.-]+)?$/.test(body);
  }

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
    var wrap = root.querySelector(".app-meta-date-wrap");
    if (data && data.published_at && dateEl) {
      var formatted = formatDate(data.published_at, lang);
      if (formatted) {
        dateEl.textContent = formatted;
        if (wrap) wrap.hidden = false;
      } else if (wrap) {
        wrap.hidden = true;
      }
    } else if (wrap) {
      wrap.hidden = true;
    }
  }

  function pickSemverRelease(list) {
    if (!Array.isArray(list)) return null;
    for (var i = 0; i < list.length; i++) {
      if (list[i] && isSemverReleaseTag(list[i].tag_name)) return list[i];
    }
    return null;
  }

  function fetchSemverRelease(fullName) {
    return fetch(
      "https://api.github.com/repos/" + fullName + "/releases?per_page=30",
      { headers: { Accept: "application/vnd.github+json" } }
    ).then(function (res) {
      if (!res.ok) throw new Error("github " + res.status);
      return res.json();
    }).then(function (list) {
      var picked = pickSemverRelease(list);
      if (!picked) throw new Error("no semver release");
      return picked;
    });
  }

  function refreshAll(lang) {
    Object.keys(REPOS).forEach(function (group) {
      fetchSemverRelease(REPOS[group])
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
