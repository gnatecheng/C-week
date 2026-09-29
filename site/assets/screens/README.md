# Homepage screenshot assets

Carousels load **language × theme** WebP sets:

```text
/assets/screens/{lang}/{theme}/{app}/{file}.webp
```

- `lang`: `zh` | `en`
- `theme`: `light` | `dark`
- `app`: `cweek` | `qingjizhang` | `class-record`

**Legacy zh/light (current):** files live at `/assets/screens/{app}/{file}.webp` when `legacyZhLight` is true in the manifest.

## Registering a new set

1. Add all slides for that app under the canonical folder (or legacy root for zh/light only).
2. Edit **`manifest.json`** in this directory: add `"lang/theme"` to that app’s array in `available`, e.g. `"cweek": ["zh/light", "zh/dark"]`.
3. Bump **`manifest.json?v=`** in `site/js/screens-lang.js` (`MANIFEST_URL`) and **`IMG_VER`** / gallery `?v=` in `index.html` when slide bytes change.
4. Bump **`screens-lang.js?v=`** in `index.html` if loader logic changed.

**Fallback order** (chosen in JS, no network probing): requested lang/theme → same lang other theme → zh same theme → zh/light. Only sets listed in the manifest are requested.

`screens-lang.js` fetches this manifest once from `'self'` (allowed by CSP). Missing sets never hit the network.

Per app, mirror filenames under `cweek/`, `qingjizhang/`, and `class-record/` (6 + 5 + 5 slides). WebP ~540px wide.
