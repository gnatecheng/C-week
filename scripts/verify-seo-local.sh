#!/usr/bin/env bash
set -euo pipefail
ROOT="$(cd "$(dirname "$0")/.." && pwd)"
PORT="${PORT:-8787}"
BASE="http://127.0.0.1:${PORT}"

SESSION_NAME="wrangler-seo-verify"
tmux -f /exec-daemon/tmux.portal.conf has-session -t "=$SESSION_NAME" 2>/dev/null || \
  tmux -f /exec-daemon/tmux.portal.conf new-session -d -s "$SESSION_NAME" -c "$ROOT" -- "${SHELL:-zsh}" -l
tmux -f /exec-daemon/tmux.portal.conf send-keys -t "$SESSION_NAME:0.0" "npx wrangler dev --port ${PORT} --ip 127.0.0.1" C-m
sleep 5

echo "== / =="
curl -sI "$BASE/" | head -5
echo "== /en/ =="
curl -sI "$BASE/en/" | head -5
echo "== /en redirect =="
curl -sI "$BASE/en" | grep -i location || true
echo "== app trailing slash redirects =="
for p in easy-ledger group-matters c-week; do
  curl -sI "$BASE/$p" | grep -i location || true
  curl -sI "$BASE/en/$p" | grep -i location || true
done
echo "== alias /en/ =="
curl -sI -H "Host: app.etais.dev" "$BASE/en/" | grep -iE '^(HTTP|location|content-security)' || true

for route in \
  "/easy-ledger/" "/en/easy-ledger/" \
  "/group-matters/" "/en/group-matters/" \
  "/c-week/" "/en/c-week/"; do
  code=$(curl -sI "$BASE$route" | head -1)
  echo "== $route ==" "$code"
done

python3 <<'PY'
import json, re, pathlib, sys

def jsonld_ok(path):
    html = pathlib.Path(path).read_text(encoding="utf-8")
    m = re.search(r'<script type="application/ld\+json" id="structured-data">\s*(\{.*?\})\s*</script>', html, re.S)
    assert m, f"missing JSON-LD in {path}"
    json.loads(m.group(1))

def h1_count(path):
    html = pathlib.Path(path).read_text(encoding="utf-8")
    n = len(re.findall(r"<h1\b", html, re.I))
    assert n == 1, f"expected 1 h1 in {path}, got {n}"

def hub_no_faq(path):
    html = pathlib.Path(path).read_text(encoding="utf-8")
    assert 'id="faq"' not in html, f"hub FAQ section must be removed: {path}"
    assert "FAQPage" not in html, f"hub FAQPage JSON-LD must be removed: {path}"
    assert 'href="#faq"' not in html, f"hub nav FAQ link must be removed: {path}"

jsonld_ok("site/index.html")
hub_no_faq("site/index.html")
print("JSON-LD zh index: OK")
print("hub FAQ removed zh: OK")
h1_count("site/index.html")
print("h1 zh index: OK")
jsonld_ok("site/en/index.html")
hub_no_faq("site/en/index.html")
print("JSON-LD en index: OK")
print("hub FAQ removed en: OK")
h1_count("site/en/index.html")
print("h1 en index: OK")

def strip_scripts(html):
    return re.sub(r"<script\b[\s\S]*?</script>", "", html, flags=re.I)

APP_PAGES = [
    ("site/easy-ledger/index.html", "zh"),
    ("site/en/easy-ledger/index.html", "en"),
    ("site/group-matters/index.html", "zh"),
    ("site/en/group-matters/index.html", "en"),
    ("site/c-week/index.html", "zh"),
    ("site/en/c-week/index.html", "en"),
]

SUBPAGE_CROSS_APP = {
    "site/easy-ledger/index.html": {
        "slug": "easy-ledger",
        "forbidden": [
            "group-matters", "c-week", "团团记", "C一周通",
            "Group Matters", "C Week",
            "gnatecheng/group-matters", "gnatecheng/c-week",
        ],
    },
    "site/en/easy-ledger/index.html": {
        "slug": "easy-ledger",
        "forbidden": [
            "group-matters", "c-week", "团团记", "C一周通",
            "Group Matters", "C Week",
            "gnatecheng/group-matters", "gnatecheng/c-week",
        ],
    },
    "site/group-matters/index.html": {
        "slug": "group-matters",
        "forbidden": [
            "easy-ledger", "c-week", "轻记账", "C一周通",
            "Easy Ledger", "C Week",
            "gnatecheng/easy-ledger", "gnatecheng/c-week",
        ],
    },
    "site/en/group-matters/index.html": {
        "slug": "group-matters",
        "forbidden": [
            "easy-ledger", "c-week", "轻记账", "C一周通",
            "Easy Ledger", "C Week",
            "gnatecheng/easy-ledger", "gnatecheng/c-week",
        ],
    },
    "site/c-week/index.html": {
        "slug": "c-week",
        "forbidden": [
            "easy-ledger", "group-matters", "轻记账", "团团记",
            "Easy Ledger", "Group Matters",
            "gnatecheng/easy-ledger", "gnatecheng/group-matters",
        ],
    },
    "site/en/c-week/index.html": {
        "slug": "c-week",
        "forbidden": [
            "easy-ledger", "group-matters", "轻记账", "团团记",
            "Easy Ledger", "Group Matters",
            "gnatecheng/easy-ledger", "gnatecheng/group-matters",
        ],
    },
}

def subpage_no_cross_app(path):
    cfg = SUBPAGE_CROSS_APP[path]
    html = strip_scripts(pathlib.Path(path).read_text(encoding="utf-8"))
    for needle in cfg["forbidden"]:
        assert needle not in html, f"subpage {path} must not mention other app ({needle!r})"
    chips = len(re.findall(r'class="app-chip\b', html))
    assert chips == 1, f"expected 1 app chip in {path}, got {chips}"
    assert 'data-app-slug="' + cfg["slug"] + '"' in html, f"missing data-app-slug in {path}"
    assert "BreadcrumbList" in pathlib.Path(path).read_text(encoding="utf-8"), f"missing BreadcrumbList JSON-LD in {path}"
    assert 'id="app-section-nav"' in html, f"missing section nav in {path}"
    assert 'href="#features"' in html and 'href="#faq"' in html, f"section nav anchors missing in {path}"
    header_part = html.split("<main", 1)[0]
    assert 'class="nav-link" href="#faq"' not in header_part, f"FAQ must not stay in header nav on {path}"
    if cfg["slug"] == "c-week":
        assert 'href="#roadmap"' in html, f"c-week subpage needs roadmap nav link: {path}"
    else:
        assert 'href="#roadmap"' not in html, f"non-c-week subpage must not have roadmap nav: {path}"

for path, lang in APP_PAGES:
    p = pathlib.Path(path)
    assert p.is_file(), f"missing {path}"
    html = p.read_text(encoding="utf-8")
    assert 'rel="canonical"' in html, f"no canonical in {path}"
    assert 'hreflang="zh-CN"' in html and 'hreflang="en"' in html, f"hreflang missing in {path}"
    h1 = len(re.findall(r"<h1\b", html, re.I))
    assert h1 == 1, f"expected 1 h1 in {path}, got {h1}"
    jsonld_ok(path)
    assert f'lang="{lang}"' in html or (lang == "zh" and 'lang="zh-CN"' in html), f"html lang in {path}"
    subpage_no_cross_app(path)
    print(f"app page OK: {path}")

WHITELIST = [
    "中文", "轻记账", "团团记", "C一周通", "Etai 应用集", "账", "团",
]

def unexpected_cjk(text):
    chars = [c for c in text if "\u4e00" <= c <= "\u9fff"]
    if not chars:
        return ""
    # remove whitelist phrases
    t = text
    for w in WHITELIST:
        t = t.replace(w, "")
    rest = [c for c in t if "\u4e00" <= c <= "\u9fff"]
    return "".join(rest)

en_paths = [
    "site/en/index.html",
    "site/en/easy-ledger/index.html",
    "site/en/group-matters/index.html",
    "site/en/c-week/index.html",
]
for path in en_paths:
    body = strip_scripts(pathlib.Path(path).read_text(encoding="utf-8"))
    leftover = unexpected_cjk(body)
    if leftover.strip():
        print(f"::error:: unexpected CJK in {path}: {leftover[:120]}", file=sys.stderr)
        sys.exit(1)
    print(f"static EN text OK: {path}")

def page_title(html):
    m = re.search(r"<title>([^<]*)</title>", html, re.I)
    assert m, "missing <title>"
    return m.group(1).strip()

# Hub zh title is user-specified (may exceed 30 chars); app subpages must stay within limits.
TITLE_PAGES = [
    ("site/en/index.html", "en", 60),
    ("site/easy-ledger/index.html", "zh", 30),
    ("site/en/easy-ledger/index.html", "en", 60),
    ("site/group-matters/index.html", "zh", 30),
    ("site/en/group-matters/index.html", "en", 60),
    ("site/c-week/index.html", "zh", 30),
    ("site/en/c-week/index.html", "en", 60),
]

hub_zh = pathlib.Path("site/index.html").read_text(encoding="utf-8")
hub_title = page_title(hub_zh)
assert hub_title, "missing zh hub title"
print(f"zh hub title ({len(hub_title)} chars): {hub_title}")

for path, lang, limit in TITLE_PAGES:
    html = pathlib.Path(path).read_text(encoding="utf-8")
    title = page_title(html)
    n = len(title)
    assert n <= limit, f"title too long ({n}>{limit}) in {path}: {title!r}"
    print(f"title length OK ({n}/{limit}): {path}")

ALT_PAGES = [
    "site/index.html",
    "site/en/index.html",
    "site/easy-ledger/index.html",
    "site/en/easy-ledger/index.html",
    "site/group-matters/index.html",
    "site/en/group-matters/index.html",
    "site/c-week/index.html",
    "site/en/c-week/index.html",
]

def img_tag_has_empty_alt(html, src_fragment):
    for m in re.finditer(r"<img\b[^>]*>", html, re.I):
        tag = m.group(0)
        if src_fragment not in tag:
            continue
        alt_m = re.search(r'\balt="([^"]*)"', tag, re.I)
        assert alt_m, f"missing alt on {src_fragment} in page"
        assert alt_m.group(1).strip(), f"empty alt on {src_fragment}: {tag[:120]}"
    assert src_fragment in html, f"expected {src_fragment} in page"

for path in ALT_PAGES:
    html = pathlib.Path(path).read_text(encoding="utf-8")
    img_tag_has_empty_alt(html, "/assets/icon.svg")
    for qr in ("easy-ledger.svg", "group-matters.svg", "c-week.svg"):
        frag = f"/assets/qr/{qr}"
        if frag in html:
            img_tag_has_empty_alt(html, frag)
    print(f"brand + QR alt OK: {path}")

PY

grep -q 'hreflang="zh-CN"' site/index.html && grep -q 'hreflang="en"' site/index.html && echo "hreflang in index: OK"

tmux -f /exec-daemon/tmux.portal.conf send-keys -t "$SESSION_NAME:0.0" C-c
sleep 1
echo "done"
