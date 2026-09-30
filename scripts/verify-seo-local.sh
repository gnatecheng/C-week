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
echo "== alias /en/ =="
curl -sI -H "Host: app.etais.dev" "$BASE/en/" | grep -iE '^(HTTP|location|content-security)' || true

python3 <<'PY'
import json, re, pathlib
html = pathlib.Path("site/index.html").read_text(encoding="utf-8")
m = re.search(r'<script type="application/ld\+json" id="structured-data">\s*(\{.*?\})\s*</script>', html, re.S)
assert m, "missing JSON-LD"
json.loads(m.group(1))
print("JSON-LD zh: OK")
html = pathlib.Path("site/en/index.html").read_text(encoding="utf-8")
m = re.search(r'<script type="application/ld\+json" id="structured-data">\s*(\{.*?\})\s*</script>', html, re.S)
json.loads(m.group(1))
print("JSON-LD en: OK")
PY

grep -q 'hreflang="zh-CN"' site/index.html && grep -q 'hreflang="en"' site/index.html && echo "hreflang in index: OK"

tmux -f /exec-daemon/tmux.portal.conf send-keys -t "$SESSION_NAME:0.0" C-c
sleep 1
echo "done"
