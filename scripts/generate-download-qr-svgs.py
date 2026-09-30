#!/usr/bin/env python3
"""Generate static QR SVGs for GitHub latest-release download links."""
from __future__ import annotations

import io
from pathlib import Path

import qrcode
from qrcode.image.svg import SvgPathImage

ROOT = Path(__file__).resolve().parents[1]
OUT_DIR = ROOT / "site" / "assets" / "qr"

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
    # White quiet zone for scanning on dark page backgrounds.
    return (
        '<?xml version="1.0" encoding="UTF-8"?>\n'
        '<svg xmlns="http://www.w3.org/2000/svg" viewBox="0 0 200 200" role="img" aria-hidden="true">\n'
        '  <rect width="200" height="200" fill="#ffffff"/>\n'
        '  <g transform="translate(10,10) scale(0.9)">\n'
        f"    {fragment.replace('<?xml version=\"1.0\" encoding=\"UTF-8\"?>', '').strip()}\n"
        "  </g>\n"
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
