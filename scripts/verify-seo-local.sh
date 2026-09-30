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

jsonld_ok("site/index.html")
print("JSON-LD zh index: OK")
jsonld_ok("site/en/index.html")
print("JSON-LD en index: OK")

APP_PAGES = [
    ("site/easy-ledger/index.html", "zh"),
    ("site/en/easy-ledger/index.html", "en"),
    ("site/group-matters/index.html", "zh"),
    ("site/en/group-matters/index.html", "en"),
    ("site/c-week/index.html", "zh"),
    ("site/en/c-week/index.html", "en"),
]

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
    print(f"app page OK: {path}")

WHITELIST = [
    "中文", "轻记账", "团团记", "C一周通", "Etai 应用集", "账", "团",
]

def strip_scripts(html):
    return re.sub(r"<script\b[\s\S]*?</script>", "", html, flags=re.I)

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

PY

grep -q 'hreflang="zh-CN"' site/index.html && grep -q 'hreflang="en"' site/index.html && echo "hreflang in index: OK"

tmux -f /exec-daemon/tmux.portal.conf send-keys -t "$SESSION_NAME:0.0" C-c
sleep 1
echo "done"
