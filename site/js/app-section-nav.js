/* Sticky section nav on app subpages — scroll spy + mobile link centering */
(function () {
  "use strict";

  var nav = document.getElementById("app-section-nav");
  if (!nav || !document.body.getAttribute("data-app-slug")) return;

  var inner = nav.querySelector(".app-section-nav__inner");
  if (!inner) return;

  var links = inner.querySelectorAll("a[href^='#']");
  if (!links.length) return;

  var sectionIds = [];
  links.forEach(function (link) {
    var id = (link.getAttribute("href") || "").slice(1);
    if (id) sectionIds.push(id);
  });

  var sections = sectionIds
    .map(function (id) {
      return document.getElementById(id);
    })
    .filter(Boolean);

  if (!sections.length) return;

  sections.forEach(function (section) {
    section.__navRatio = 0;
  });

  function stickyHeight() {
    var stack = document.querySelector(".site-sticky-top");
    if (stack) return stack.offsetHeight;
    var header = document.querySelector(".site-header");
    return (header ? header.offsetHeight : 0) + nav.offsetHeight;
  }

  function syncScrollOffset() {
    var h = stickyHeight();
    document.documentElement.style.setProperty("--scroll-offset", h + "px");
    document.documentElement.style.scrollPaddingTop = h + "px";
  }

  function scrollLinkIntoView(link) {
    var track = nav.querySelector(".app-section-nav__track");
    if (!track || !link) return;
    var pad = 12;
    var linkLeft = link.offsetLeft;
    var linkRight = linkLeft + link.offsetWidth;
    var viewLeft = track.scrollLeft;
    var viewRight = viewLeft + track.clientWidth;
    if (linkLeft < viewLeft + pad) {
      track.scrollLeft = Math.max(0, linkLeft - pad);
    } else if (linkRight > viewRight - pad) {
      track.scrollLeft = linkRight - track.clientWidth + pad;
    }
  }

  function setActive(id) {
    links.forEach(function (link) {
      var hrefId = (link.getAttribute("href") || "").slice(1);
      var on = hrefId === id;
      link.classList.toggle("is-active", on);
      if (on) {
        link.setAttribute("aria-current", "location");
        scrollLinkIntoView(link);
      } else {
        link.removeAttribute("aria-current");
      }
    });
  }

  function pickActiveFromRatios() {
    var marker = window.scrollY + stickyHeight() + 180;
    var chosen = sections[0];
    sections.forEach(function (section) {
      var docTop = section.getBoundingClientRect().top + window.scrollY;
      if (docTop <= marker) chosen = section;
    });
    setActive(chosen.id);
  }

  var observer;
  function bindObserver() {
    if (observer) observer.disconnect();
    syncScrollOffset();
    var topInset = stickyHeight();
    observer = new IntersectionObserver(
      function (entries) {
        entries.forEach(function (entry) {
          entry.target.__navRatio = entry.intersectionRatio;
        });
        pickActiveFromRatios();
      },
      {
        root: null,
        rootMargin: "-" + topInset + "px 0px -35% 0px",
        threshold: [0, 0.08, 0.2, 0.35, 0.5, 0.75, 1],
      }
    );
    sections.forEach(function (section) {
      observer.observe(section);
    });
    pickActiveFromRatios();
  }

  links.forEach(function (link) {
    link.addEventListener("click", function () {
      var id = (link.getAttribute("href") || "").slice(1);
      if (id) setActive(id);
    });
  });

  function scrollToHashIfNeeded() {
    if (!location.hash) return;
    var id = location.hash.slice(1);
    if (sectionIds.indexOf(id) < 0) return;
    var el = document.getElementById(id);
    if (!el) return;
    syncScrollOffset();
    var y = window.scrollY + el.getBoundingClientRect().top - stickyHeight() - 12;
    window.scrollTo(0, Math.max(0, y));
    setActive(id);
  }

  function init() {
    bindObserver();
    scrollToHashIfNeeded();
    window.addEventListener("hashchange", scrollToHashIfNeeded);
  }

  window.addEventListener("resize", function () {
    bindObserver();
  });

  if (document.readyState === "loading") {
    document.addEventListener("DOMContentLoaded", init);
  } else {
    init();
  }
})();
