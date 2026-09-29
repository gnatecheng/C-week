# Homepage screenshot assets

Carousels resolve **language × theme** WebP paths:

```text
/assets/screens/{lang}/{theme}/{app}/{file}.webp
```

- `lang`: `zh` | `en`
- `theme`: `light` | `dark`
- `app`: `cweek` | `qingjizhang` | `class-record`

**Legacy (still used as fallback):**

- `/assets/screens/{app}/{file}.webp` → zh / light
- `/assets/screens/en/{app}/{file}.webp` → en / light

**Fallback order** when a file is missing: same language other theme → zh same theme → zh light → legacy paths above.

Per app, mirror the filenames already under `cweek/`, `qingjizhang/`, and `class-record/` (6 + 5 + 5 slides). Optimize as WebP ~540px wide.
