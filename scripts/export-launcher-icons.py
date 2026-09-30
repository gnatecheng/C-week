#!/usr/bin/env python3
"""Export adaptive Android launcher icons to site/assets/app-icons/*.webp (128×128, rounded)."""
from __future__ import annotations

import re
import subprocess
import tempfile
import urllib.request
import xml.etree.ElementTree as ET
from pathlib import Path

ROOT = Path(__file__).resolve().parents[1]
OUT = ROOT / "site" / "assets" / "app-icons"
SIZE = 128
RADIUS_RATIO = 24 / 108  # match adaptive icon corner feel

ANDROID_NS = "{http://schemas.android.com/apk/res/android}"


def android_paths_from_vector(xml_text: str) -> list[tuple[str, str]]:
    root = ET.fromstring(xml_text)
    out: list[tuple[str, str]] = []
    for path in root.iter("path"):
        d = path.get(f"{ANDROID_NS}pathData")
        fill = path.get(f"{ANDROID_NS}fillColor", "#000000")
        if d:
            out.append((d, fill))
    return out


def solid_bg_svg(color: str) -> str:
    return f'<rect width="108" height="108" fill="{color}"/>'


def paths_to_svg_inner(paths: list[tuple[str, str]]) -> str:
    chunks = [f'<path d="{d}" fill="{fill}"/>' for d, fill in paths]
    return "\n    ".join(chunks)


def compose_svg(*layers: str) -> str:
    body = "\n    ".join(layers)
    return f"""<?xml version="1.0" encoding="UTF-8"?>
<svg xmlns="http://www.w3.org/2000/svg" viewBox="0 0 108 108">
  {body}
</svg>
"""


def rsvg_to_png(svg: str, out_png: Path) -> None:
    with tempfile.NamedTemporaryFile("w", suffix=".svg", delete=False, encoding="utf-8") as f:
        f.write(svg)
        svg_path = f.name
    subprocess.run(
        ["rsvg-convert", "-w", str(SIZE), "-h", str(SIZE), svg_path, "-o", str(out_png)],
        check=True,
    )
    Path(svg_path).unlink(missing_ok=True)


def round_mask_png(png_path: Path) -> None:
    from PIL import Image, ImageDraw

    im = Image.open(png_path).convert("RGBA")
    im = im.resize((SIZE, SIZE), Image.Resampling.LANCZOS)
    mask = Image.new("L", (SIZE, SIZE), 0)
    draw = ImageDraw.Draw(mask)
    r = int(SIZE * RADIUS_RATIO)
    draw.rounded_rectangle((0, 0, SIZE, SIZE), radius=r, fill=255)
    im.putalpha(mask)
    im.save(png_path, optimize=True)


def save_webp(png_path: Path, webp_path: Path) -> None:
    from PIL import Image

    im = Image.open(png_path).convert("RGBA")
    im.save(webp_path, "WEBP", quality=88, method=6)


def export_cweek() -> None:
    bg = (ROOT / "app/src/main/res/drawable/ic_launcher_background.xml").read_text(encoding="utf-8")
    fg = (ROOT / "app/src/main/res/drawable/ic_launcher_foreground.xml").read_text(encoding="utf-8")
    svg = compose_svg(
        paths_to_svg_inner(android_paths_from_vector(bg)),
        paths_to_svg_inner(android_paths_from_vector(fg)),
    )
    png = OUT / "c-week.png"
    rsvg_to_png(svg, png)
    round_mask_png(png)
    save_webp(png, OUT / "c-week.webp")


def export_easy_ledger() -> None:
    fg_url = "https://raw.githubusercontent.com/gnatecheng/easy-ledger/main/app/src/main/res/drawable/ic_launcher_foreground.xml"
    fg = urllib.request.urlopen(fg_url, timeout=30).read().decode("utf-8")
    svg = compose_svg(
        solid_bg_svg("#1F6F6A"),
        paths_to_svg_inner(android_paths_from_vector(fg)),
    )
    png = OUT / "easy-ledger.png"
    rsvg_to_png(svg, png)
    round_mask_png(png)
    save_webp(png, OUT / "easy-ledger.webp")


def export_group_matters() -> None:
    fg_url = (
        "https://raw.githubusercontent.com/gnatecheng/group-matters/main/"
        "app/src/main/res/drawable/ic_launcher_fg.xml"
    )
    fg = urllib.request.urlopen(fg_url, timeout=30).read().decode("utf-8")
    # @color/ic_launcher_bg from group-matters app/src/main/res/values/colors.xml
    svg = compose_svg(solid_bg_svg("#2962FF"), paths_to_svg_inner(android_paths_from_vector(fg)))
    png = OUT / "group-matters.png"
    rsvg_to_png(svg, png)
    round_mask_png(png)
    save_webp(png, OUT / "group-matters.webp")


def main() -> None:
    OUT.mkdir(parents=True, exist_ok=True)
    export_cweek()
    print("exported c-week from app/src/main/res/drawable/ic_launcher_{background,foreground}.xml")
    export_easy_ledger()
    print("exported easy-ledger from easy-ledger drawable/ic_launcher_* + bg #1F6F6A")
    export_group_matters()
    print(
        "exported group-matters from group-matters drawable/ic_launcher_fg.xml + colors ic_launcher_bg #2962FF"
    )
    for name in ("c-week", "easy-ledger", "group-matters"):
        p = OUT / f"{name}.webp"
        print(p.relative_to(ROOT), p.stat().st_size, "bytes")


if __name__ == "__main__":
    main()
