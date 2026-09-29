/* Etai 应用集: fixed-size screenshot carousel — autoplay, arrows, dots, swipe, i18n */
(function () {
  "use strict";

  var galleries = document.querySelectorAll(".screenshot-gallery");
  if (!galleries.length) return;

  var reduceMotion =
    window.matchMedia && window.matchMedia("(prefers-reduced-motion: reduce)").matches;
  var INTERVAL = 4000;
  var RESUME_AFTER = 8000;
  var instances = [];

  function getLang() {
    var l = (document.documentElement.lang || "zh-CN").toLowerCase();
    return l.indexOf("en") === 0 ? "en" : "zh";
  }

  function tr(key, lang, vars) {
    var T = window.ETAI_TRANSLATIONS;
    var bag = T && T[lang];
    var s = (bag && bag["carousel." + key]) || (T && T.zh && T.zh["carousel." + key]) || "";
    if (!vars) return s;
    return s.replace(/\{(\w+)\}/g, function (_, k) {
      return vars[k] != null ? String(vars[k]) : "";
    });
  }

  function galleryLabel(gallery) {
    return gallery.getAttribute("aria-label") || tr("fallbackLabel", getLang()) || "";
  }

  Array.prototype.forEach.call(galleries, function (gallery) {
    var track = gallery.querySelector(".screenshot-gallery-track");
    if (!track) return;
    var slides = Array.prototype.slice.call(track.querySelectorAll(".screenshot-slide"));
    if (!slides.length) return;

    var root = document.createElement("div");
    root.className = "shot-carousel";
    gallery.parentNode.insertBefore(root, gallery);

    var stage = document.createElement("div");
    stage.className = "shot-carousel-stage";
    root.appendChild(stage);
    stage.appendChild(gallery);
    gallery.classList.add("is-carousel");
    gallery.setAttribute("aria-roledescription", "carousel");

    var prev = null;
    var next = null;
    var dots = [];
    var dotsWrap = null;

    function updateSlideAria() {
      var lang = getLang();
      var label = galleryLabel(gallery);
      var total = slides.length;
      slides.forEach(function (s, i) {
        s.setAttribute(
          "aria-label",
          tr("slideAria", lang, { n: i + 1, total: total, gallery: label })
        );
      });
    }

    function updateControlsI18n() {
      var lang = getLang();
      var label = galleryLabel(gallery);
      if (prev) {
        prev.setAttribute("aria-label", tr("prevAria", lang, { gallery: label }));
      }
      if (next) {
        next.setAttribute("aria-label", tr("nextAria", lang, { gallery: label }));
      }
      dots.forEach(function (d, i) {
        d.setAttribute(
          "aria-label",
          tr("dotAria", lang, { n: i + 1, total: slides.length, gallery: label })
        );
      });
      updateSlideAria();
    }

    updateSlideAria();

    if (slides.length < 2) {
      instances.push({ updateI18n: updateControlsI18n });
      return;
    }

    function makeBtn(cls, text, ariaKey) {
      var b = document.createElement("button");
      b.type = "button";
      b.className = "shot-carousel-btn " + cls;
      b.textContent = text;
      b.setAttribute("aria-label", tr(ariaKey, getLang(), { gallery: galleryLabel(gallery) }));
      return b;
    }
    prev = makeBtn("is-prev", "\u2039", "prevAria");
    next = makeBtn("is-next", "\u203A", "nextAria");
    stage.appendChild(prev);
    stage.appendChild(next);

    dotsWrap = document.createElement("div");
    dotsWrap.className = "shot-carousel-dots";
    dotsWrap.setAttribute("role", "tablist");
    dots = slides.map(function (s, i) {
      var d = document.createElement("button");
      d.type = "button";
      d.className = "shot-carousel-dot";
      d.setAttribute("role", "tab");
      d.addEventListener("click", function () {
        go(i, true);
      });
      dotsWrap.appendChild(d);
      return d;
    });
    root.appendChild(dotsWrap);

    var current = 0;
    var timer = null;
    var resumeTimer = null;
    var hovering = false;
    var visible = false;
    var userPaused = false;
    var programmatic = 0;

    function setDots(i) {
      dots.forEach(function (d, k) {
        var on = k === i;
        d.classList.toggle("is-active", on);
        if (on) {
          d.setAttribute("aria-current", "true");
          d.setAttribute("aria-selected", "true");
        } else {
          d.removeAttribute("aria-current");
          d.setAttribute("aria-selected", "false");
        }
      });
    }

    function go(i, byUser) {
      var n = slides.length;
      current = ((i % n) + n) % n;
      programmatic = Date.now();
      gallery.scrollTo({
        left: current * gallery.clientWidth,
        behavior: reduceMotion ? "auto" : "smooth",
      });
      setDots(current);
      if (byUser) pauseForUser();
    }

    function stop() {
      if (timer) {
        clearInterval(timer);
        timer = null;
      }
    }
    function start() {
      stop();
      if (reduceMotion || hovering || !visible || userPaused || document.hidden) return;
      timer = setInterval(function () {
        go(current + 1, false);
      }, INTERVAL);
    }
    function pauseForUser() {
      userPaused = true;
      stop();
      clearTimeout(resumeTimer);
      resumeTimer = setTimeout(function () {
        userPaused = false;
        start();
      }, RESUME_AFTER);
    }

    prev.addEventListener("click", function () {
      go(current - 1, true);
    });
    next.addEventListener("click", function () {
      go(current + 1, true);
    });

    var scrollTick = false;
    gallery.addEventListener(
      "scroll",
      function () {
        if (scrollTick) return;
        scrollTick = true;
        requestAnimationFrame(function () {
          scrollTick = false;
          if (Date.now() - programmatic < 900) return;
          var w = gallery.clientWidth || 1;
          var i = Math.round(gallery.scrollLeft / w);
          if (i !== current && i >= 0 && i < slides.length) {
            current = i;
            setDots(i);
          }
        });
      },
      { passive: true }
    );

    ["pointerdown", "touchstart", "wheel"].forEach(function (ev) {
      gallery.addEventListener(ev, pauseForUser, { passive: true });
    });
    root.addEventListener("mouseenter", function () {
      hovering = true;
      stop();
    });
    root.addEventListener("mouseleave", function () {
      hovering = false;
      start();
    });
    root.addEventListener("focusin", function () {
      hovering = true;
      stop();
    });
    root.addEventListener("focusout", function (e) {
      if (root.contains(e.relatedTarget)) return;
      hovering = false;
      start();
    });
    gallery.setAttribute("tabindex", "0");
    gallery.addEventListener("keydown", function (e) {
      if (e.key === "ArrowLeft") {
        e.preventDefault();
        go(current - 1, true);
      } else if (e.key === "ArrowRight") {
        e.preventDefault();
        go(current + 1, true);
      }
    });
    document.addEventListener("visibilitychange", function () {
      if (document.hidden) stop();
      else start();
    });

    if ("IntersectionObserver" in window) {
      new IntersectionObserver(
        function (entries) {
          visible = entries[0].isIntersecting;
          if (visible) start();
          else stop();
        },
        { threshold: 0.35 }
      ).observe(root);
    } else {
      visible = true;
      start();
    }

    setDots(0);
    instances.push({ updateI18n: updateControlsI18n });
  });

  document.addEventListener("etai:langchange", function () {
    instances.forEach(function (inst) {
      inst.updateI18n();
    });
  });
})();
