#!/usr/bin/env python3
"""Generate static QR SVGs for GitHub latest-release download links."""
from __future__ import annotations

import io
import re
from pathlib import Path

import qrcode
from qrcode.image.svg import SvgPathImage

ROOT = Path(__file__).resolve().parents[1]
OUT_DIR = ROOT / "site" / "assets" / "qr"
CANVAS = 200

URLS: dict[str, str] = {
    "c-week.svg": "https://github.com/gnatecheng/c-week/releases/latest",
    "easy-ledger.svg": "https://github.com/gnatecheng/easy-ledger/releases/latest",
    "group-matters.svg": "https://github.com/gnatecheng/group-matters/releases/latest",
}


def make_svg(url: str) -> str:
    qr = qrcode.QRCode(
        version=None,
        error_correction=qrcode.constants.ERROR_CORRECT_M,
        box_size=8,
        border=4,
    )
    qr.add_data(url)
    qr.make(fit=True)
    buf = io.BytesIO()
    img = qr.make_image(image_factory=SvgPathImage)
    img.save(buf)
    inner = buf.getvalue().decode("utf-8")
    start = inner.find("<svg")
    end = inner.rfind("</svg>")
    if start == -1 or end == -1:
        raise RuntimeError("unexpected QR SVG output")
    fragment = inner[start : end + len("</svg>")]
    vb_match = re.search(r'viewBox="([^"]+)"', fragment)
    path_match = re.search(r"<path[^>]+/>", fragment)
    if not vb_match or not path_match:
        raise RuntimeError("could not parse QR SVG path/viewBox")
    view_box = vb_match.group(1)
    path_el = path_match.group(0)
    # Single canvas: white quiet zone + QR scaled to fill (no nested mm dimensions).
    return (
        '<?xml version="1.0" encoding="UTF-8"?>\n'
        f'<svg xmlns="http://www.w3.org/2000/svg" viewBox="0 0 {CANVAS} {CANVAS}" role="img" aria-hidden="true">\n'
        f'  <rect width="{CANVAS}" height="{CANVAS}" fill="#ffffff"/>\n'
        f'  <svg x="0" y="0" width="{CANVAS}" height="{CANVAS}" viewBox="{view_box}" preserveAspectRatio="xMidYMid meet">\n'
        f"    {path_el}\n"
        "  </svg>\n"
        "</svg>\n"
    )


def main() -> None:
    OUT_DIR.mkdir(parents=True, exist_ok=True)
    for filename, url in URLS.items():
        path = OUT_DIR / filename
        path.write_text(make_svg(url), encoding="utf-8")
        print("wrote", path.relative_to(ROOT), "->", url)


if __name__ == "__main__":
    main()
