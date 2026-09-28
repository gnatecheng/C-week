/* Etai 应用集 — floating side navigation: smooth scroll + active section highlight */
(function () {
  "use strict";

  var nav = document.querySelector(".side-nav");
  if (!nav) return;

  var links = Array.prototype.slice.call(nav.querySelectorAll("a[data-target]"));
  var sections = Array.prototype.slice.call(document.querySelectorAll("main [data-group]"));
  if (!sections.length) return;

  var reduceMotion =
    window.matchMedia && window.matchMedia("(prefers-reduced-motion: reduce)").matches;
  var ticking = false;

  function setActive(group) {
    links.forEach(function (a) {
      var on = a.getAttribute("data-target") === group;
      a.classList.toggle("is-active", on);
      if (on) {
        a.setAttribute("aria-current", "true");
      } else {
        a.removeAttribute("aria-current");
      }
    });
  }

  function update() {
    ticking = false;
    var line = window.innerHeight * 0.35;
    var current = sections[0].getAttribute("data-group");
    for (var i = 0; i < sections.length; i++) {
      if (sections[i].getBoundingClientRect().top <= line) {
        current = sections[i].getAttribute("data-group");
      }
    }
    var doc = document.documentElement;
    if (window.innerHeight + window.pageYOffset >= doc.scrollHeight - 4) {
      current = sections[sections.length - 1].getAttribute("data-group");
    }
    setActive(current);
  }

  function onScroll() {
    if (!ticking) {
      ticking = true;
      window.requestAnimationFrame(update);
    }
  }

  nav.addEventListener("click", function (e) {
    var a = e.target.closest ? e.target.closest("a") : null;
    if (!a) return;
    var href = a.getAttribute("href") || "";
    var behavior = reduceMotion ? "auto" : "smooth";
    if (href === "#top") {
      e.preventDefault();
      window.scrollTo({ top: 0, behavior: behavior });
      if (history.replaceState) history.replaceState(null, "", location.pathname + location.search);
      return;
    }
    if (href.charAt(0) !== "#") return;
    var target = document.getElementById(href.slice(1));
    if (!target) return;
    e.preventDefault();
    target.scrollIntoView({ behavior: behavior, block: "start" });
    if (history.replaceState) history.replaceState(null, "", href);
    var group = a.getAttribute("data-target");
    if (group) setActive(group);
  });

  window.addEventListener("scroll", onScroll, { passive: true });
  window.addEventListener("resize", onScroll);
  update();
})();
