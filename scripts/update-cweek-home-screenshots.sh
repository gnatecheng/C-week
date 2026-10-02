#!/usr/bin/env bash
# Convert Roborazzi PNGs to site WebP (540px wide) for 01-home.
set -euo pipefail
ROOT="$(cd "$(dirname "$0")/.." && pwd)"
ART="/opt/cursor/artifacts"

convert_one() {
  local src="$1"
  local dest="$2"
  if [[ ! -f "$src" ]]; then
    echo "Missing screenshot: $src" >&2
    exit 1
  fi
  mkdir -p "$(dirname "$dest")"
  cwebp -quiet -resize 540 0 "$src" -o "$dest"
  echo "Wrote $dest"
}

convert_one "$ART/cweek-home-zh-light-streak1.png" "$ROOT/site/assets/screens/cweek/01-home.webp"
convert_one "$ART/cweek-home-zh-light-streak1.png" "$ROOT/site/assets/screens/zh/light/cweek/01-home.webp"
convert_one "$ART/cweek-home-zh-dark-streak1.png" "$ROOT/site/assets/screens/zh/dark/cweek/01-home.webp"
convert_one "$ART/cweek-home-en-light-streak1.png" "$ROOT/site/assets/screens/en/light/cweek/01-home.webp"
convert_one "$ART/cweek-home-en-dark-streak1.png" "$ROOT/site/assets/screens/en/dark/cweek/01-home.webp"
