/* Etai 应用集: fixed-size screenshot carousel with autoplay, arrows, dots and swipe */
(function () {
  "use strict";

  var galleries = document.querySelectorAll(".screenshot-gallery");
  if (!galleries.length) return;

  var reduceMotion =
    window.matchMedia && window.matchMedia("(prefers-reduced-motion: reduce)").matches;
  var INTERVAL = 3500;
  var RESUME_AFTER = 8000;

  Array.prototype.forEach.call(galleries, function (gallery, gi) {
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

    var label = gallery.getAttribute("aria-label") || "截图";
    slides.forEach(function (s, i) {
      s.setAttribute("role", "group");
      s.setAttribute("aria-roledescription", "slide");
      s.setAttribute("aria-label", i + 1 + " / " + slides.length);
    });

    if (slides.length < 2) return;

    function makeBtn(cls, text, aria) {
      var b = document.createElement("button");
      b.type = "button";
      b.className = "shot-carousel-btn " + cls;
      b.setAttribute("aria-label", aria);
      b.textContent = text;
      return b;
    }
    var prev = makeBtn("is-prev", "\u2039", "上一张" + label);
    var next = makeBtn("is-next", "\u203A", "下一张" + label);
    stage.appendChild(prev);
    stage.appendChild(next);

    var dotsWrap = document.createElement("div");
    dotsWrap.className = "shot-carousel-dots";
    var dots = slides.map(function (s, i) {
      var d = document.createElement("button");
      d.type = "button";
      d.className = "shot-carousel-dot";
      d.setAttribute("aria-label", "第 " + (i + 1) + " 张");
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
        if (on) d.setAttribute("aria-current", "true");
        else d.removeAttribute("aria-current");
      });
    }

    function go(i, byUser) {
      var n = slides.length;
      current = ((i % n) + n) % n;
      programmatic = Date.now();
      gallery.scrollTo({
        left: current * gallery.clientWidth,
        behavior: reduceMotion ? "auto" : "smooth"
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
    root.addEventListener("focusout", function () {
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
        { threshold: 0.4 }
      ).observe(root);
    } else {
      visible = true;
      start();
    }

    setDots(0);
  });
})();
