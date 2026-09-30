#!/usr/bin/env python3
"""Move panel head into body left column; divider as sibling of scan."""
import re
from pathlib import Path

path = Path(__file__).resolve().parents[1] / "site/index.html"
text = path.read_text(encoding="utf-8")
pattern = re.compile(
    r"(?P<indent>[ \t]*)<div class=\"download-panel__head\">(?P<head>.*?)</div>\s*"
    r"<div class=\"download-panel__body\">\s*"
    r"<div class=\"download-panel__primary\">(?P<primary>.*?)</div>\s*"
    r"<div class=\"download-panel__scan\">\s*"
    r"<div class=\"download-panel__divider\" role=\"presentation\">\s*"
    r"<div class=\"download-panel__divider-track\">\s*"
    r"<span class=\"download-panel__divider-label\" data-i18n=\"download.scanOr\">[^<]*</span>\s*"
    r"</div>\s*</div>\s*"
    r"(?P<qr><figure class=\"download-panel__qr\">.*?</figure>)\s*"
    r"</div>\s*</div>",
    re.S,
)


def repl(m: re.Match[str]) -> str:
    ind = m.group("indent")
    ind2 = ind + "  "
    ind3 = ind2 + "  "
    head = m.group("head")
    primary = m.group("primary")
    qr = m.group("qr")
    return (
        f'{ind}<div class="download-panel__body">\n'
        f'{ind2}<div class="download-panel__left">\n'
        f'{ind2}<div class="download-panel__head">{head}</div>\n'
        f'{ind2}<div class="download-panel__primary">{primary}</div>\n'
        f'{ind2}</div>\n'
        f'{ind2}<div class="download-panel__divider" role="presentation">\n'
        f'{ind3}<div class="download-panel__divider-track">\n'
        f'{ind3}  <span class="download-panel__divider-label" data-i18n="download.scanOr">或</span>\n'
        f'{ind3}</div>\n'
        f'{ind2}</div>\n'
        f'{ind2}<div class="download-panel__scan">\n'
        f'{ind3}{qr.strip()}\n'
        f'{ind2}</div>\n'
        f'{ind}</div>'
    )


new_text, n = pattern.subn(repl, text)
if n != 3:
    raise SystemExit(f"expected 3 panels, got {n}")
path.write_text(new_text, encoding="utf-8")
print("restructured", n, "panels")
