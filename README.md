**English** | [中文](README.zh-CN.md)

[Homepage: Etai Apps — C Week section](https://etais.dev/#cweek)

# C Week

In seven days, take programming beginners to the point where they can **implement Dijkstra shortest paths in C** (using arrays, pointers, and memory correctly).

**Brand name:** **C一周通** (English: **C Week**).  
Lesson IDE demos are **real VS Code screen recordings** (H.264, bundled in the APK), played with Media3 and a checklist synced to the timeline. Static illustrations are fallbacks when no recording exists.

## Homepage

The static **Etai Apps** landing page (C Week features, 7-day path, download links, and two sibling apps) lives at **https://etais.dev**.  
Source is in [`site/`](site/). The homepage is split by URL: **Chinese** at `/`, **English** at `/en/` (regenerate with `node scripts/build-en-index.mjs` after editing `site/index.html` or FAQ/meta strings). Language toggle navigates between those URLs (hash preserved). **Light / dark / system** theme uses `localStorage`. App version labels are fetched from GitHub Releases when possible (HTML fallbacks if the API fails).

### Site deploy (Cloudflare Worker `etai`)

**Only automatic deploy path:** Cloudflare **Workers Builds** (Git integration). Each push to `main` deploys Worker **`etai`** from [`wrangler.jsonc`](wrangler.jsonc) at the repo root; static assets from [`site/`](site/); 404 via [`site/404.html`](site/404.html) (`not_found_handling: 404-page`).

- Local preview: `npx wrangler dev` (repo root) or any static server serving `site/`.
- Security and cache: [`site/_headers`](site/_headers). `/css/*` and `/js/*` are cached 7 days as `immutable`; **after editing any CSS/JS, bump the `?v=` query on script/style URLs in [`site/index.html`](site/index.html) and [`site/404.html`](site/404.html)** so returning visitors do not keep stale assets.
- Do **not** use the removed GitHub Actions “Deploy site to Cloudflare Pages” workflow (wrong Pages project, missing secrets).

### Cloudflare Web Analytics

The site uses Cloudflare Web Analytics (beacon in [`site/index.html`](site/index.html)). CSP in [`site/_headers`](site/_headers) allows `static.cloudflareinsights.com` and `cloudflareinsights.com`.

### Release signing & GitHub Secrets

GitHub Actions [`.github/workflows/release-apk.yml`](.github/workflows/release-apk.yml) builds a **signed release APK** when you push a **semver tag** `vX.Y.Z` (must match `versionName` in `app/build.gradle.kts`). If required secrets are missing, the workflow **fails with a clear error**—it will not silently publish a debug APK.

| Secret | Purpose |
| --- | --- |
| `ANDROID_KEYSTORE_BASE64` | Base64 of the release keystore (see command below) |
| `ANDROID_KEYSTORE_PASSWORD` | Keystore password |
| `ANDROID_KEY_ALIAS` | Key alias |
| `ANDROID_KEY_PASSWORD` | (Optional) key password; defaults to store password |

**Generate a keystore (local machine only—never commit):**

```bash
keytool -genkeypair -v \
  -keystore release.keystore \
  -alias c-week-release \
  -keyalg RSA -keysize 2048 -validity 10000 \
  -storetype PKCS12
base64 -w0 release.keystore   # macOS/Linux → GitHub Secret ANDROID_KEYSTORE_BASE64
```

Optional local release build: put `release.keystore` in the repo root and set `ANDROID_KEYSTORE_FILE`, `ANDROID_KEYSTORE_PASSWORD`, `ANDROID_KEY_ALIAS` (and optional `ANDROID_KEY_PASSWORD`), then `./gradlew :app:assembleRelease`. **Never** commit keystore or passwords.

**Release tags:** use semver only (e.g. `v1.5.0`); do not use date tags (e.g. `v20260928`) to avoid duplicate releases.

Homepage screenshots live under `site/assets/screens/` (WebP, ~540px wide) from real Compose UI in debug APKs. The page loads `/assets/screens/{zh|en}/{light|dark}/{app}/…` (fallback chain in README); legacy `screens/{app}/` is treated as Chinese light. When adb/emulator capture is unavailable, Roborazzi + Robolectric can record in a temporary build (not committed in the Android module).

Apps featured on the homepage:

| App | Summary | Repository |
| --- | --- | --- |
| C Week | Learn C in 7 days, Hello World → Dijkstra (this repo) | [gnatecheng/c-week](https://github.com/gnatecheng/c-week) |
| Easy Ledger | Local-first personal finance (Room, no login) | [gnatecheng/easy-ledger](https://github.com/gnatecheng/easy-ledger) |
| Group Matters | Attendance, payments, splits, checklists | [gnatecheng/group-matters](https://github.com/gnatecheng/group-matters) |

---

## Features

- **Week plan:** Day 1–7 cards, checkboxes, overall percentage; progress in DataStore.
- **Study calendar / check-in** (1.3.0): one day per level D1–D7, week date strip + streak; completing lesson/lab/quiz stamps the day.
- **Wrong-answer book + retry** (1.4.0): quiz/lab mistakes stored (day, question, your answer, correct answer, error category). Bottom nav and home entry; list by day, tap to retry; correct retry marks fixed.
- **Multi-case labs** (1.4.0): offline simulated grading against several I/O pairs, partial score and clearer feedback (no gcc on device).
- **Learning report** (1.4.0): streak, per-day completion, quiz accuracy; share via system sheet.
- **English UI + full English curriculum** (1.5.0): Settings → Chinese / English; Days 1–7 lessons, quiz hints, lab grading, VS Code demo copy in English.
- **Settings & About** (1.5.0): language, light / dark / system theme, version, build time, open-source repo link.
- **English plural strings** (1.5.1): streaks, quiz counts, lab hints, and other quantity labels use correct singular/plural forms (e.g. “Streak 1 day” instead of “Streak 1 days”).
- **Lessons:** Chinese narration (with analogies), quizzes, **real VS Code recordings**. Day 1 includes paths/files and **step-by-step bash** (pwd/ls/cd/… plus cheat sheet).
- **Code labs:** edit C in-app; **simulated run** against cases; categorized hints (missing headers, off-by-one, pointers, formulas, empty TODO shells, BFS vs Dijkstra confusion, etc.).
- **Day 7 capstone:** adjacency list + O(V²) Dijkstra fill-in; multiple tests; optional grid walkthrough for dist and shortest path.
- **Glossary:** pointer, stack/heap, array decay, launch.json, bash paths, etc.
- System light/dark (overridable in Settings); large tap targets; monospace code font.
- Current debug build: **1.5.1** (versionCode 8).

---

## Curriculum

| Day | Topic | Micro-lessons | Lab |
| --- | --- | --- | --- |
| 1 | Paths, bash step-by-step, env, Hello World, debug | 12 | Print `Hello, Ada` |
| 2 | Types, printf/scanf, no GC | 5 | Celsius → Fahrenheit |
| 3 | if/for, prototypes, pass-by-value | 5 | `is_prime` |
| 4 | Arrays, pointers, C strings | 5 | Reverse string in place |
| 5 | struct, malloc/free, files | 4 | Highest score in struct array |
| 6 | Selection sort, adjacency list, BFS | 5 | Complete BFS |
| 7 | Dijkstra, complexity, exercises | 4 | Complete relaxations (capstone) |

Full lesson text: `app/src/main/java/com/py2c/week/data/curriculum/`. Map JSON: `app/src/main/assets/curriculum/map.json`. Desktop companion `.c` files: `app/src/main/assets/labs/`.

---

## Open and run in Android Studio

1. Install [Android Studio](https://developer.android.com/studio) (Koala / Ladybug or newer, AGP 8.7).
2. **File → Open** this repository root (folder containing `settings.gradle.kts`).
3. Wait for Gradle sync. SDK: **compileSdk/targetSdk 35**, **minSdk 26**.
4. Select a device or emulator (API 26+).
5. Click **Run** (green triangle) on the `app` configuration.

Command line:

```bash
# macOS/Linux — install Android SDK, then:
echo "sdk.dir=/path/to/Android/sdk" > local.properties
./gradlew :app:assembleDebug
# APK: app/build/outputs/apk/debug/app-debug.apk
```

`local.properties` is gitignored. Android Studio creates it automatically.

**中文说明**见 [README.zh-CN.md](README.zh-CN.md#在-android-studio-中打开并运行).

---

## Architecture

```
com.py2c.week
  data/          models, DataStore progress, curriculum assembly
  data/curriculum/  seven days: lessons, quizzes, labs, VS Code scripts, glossary
  ui/theme/      Material 3, system theme
  ui/home day lesson quiz lab glossary wrongbook report settings
  ui/components  code highlight, memory viz, Media3 VS Code player
  ui/navigation  Navigation Compose + bottom bar
```

- **Offline-first:** no backend. Lessons and `assets/vscode_demos/*.mp4` ship in the APK.
- **Progress:** Jetpack DataStore Preferences (lessons / labs / quiz scores / reveal answers / wrong book).
- **ViewModel:** `ProgressViewModel` exposes progress Flows to the UI.
- **Simulated grading:** multiple cases + regex/substring checks + common C mistake hints—not on-device gcc. Partial credit shows a score; lab completes only when all cases pass.
- **VS Code demos:** real VS Code video + time-synced checklist (Chinese/English); default 0.75× speed. Compose fake editor only when video is missing.
- **Fullscreen:** player fullscreen button opens `FullscreenVideoActivity` (landscape, immersive, `PlayerView`); back or “exit fullscreen” returns to the lesson.

---

## Re-record VS Code demos

Requires Linux, Xvfb, ffmpeg, xdotool, wmctrl, openbox, VS Code, gcc, gdb.

```bash
# 1. Virtual display
Xvfb :99 -screen 0 1280x720x24 -ac +extension RANDR &
DISPLAY=:99 openbox &

# 2. Install C/C++ extension into recording extensions dir
DISPLAY=:99 code --no-sandbox --disable-gpu \
  --user-data-dir=/tmp/vscode-ext-cache \
  --extensions-dir=/tmp/vscode-record-ext \
  --install-extension ms-vscode.cpptools

# 3. Record (overwrites app/src/main/assets/vscode_demos/*.mp4)
DISPLAY=:99 python3 scripts/record_vscode_demos.py

# 4. End frames in /tmp/vscode-shots/ — confirm real VS Code before shipping
./gradlew :app:assembleDebug
```

Manifest and filenames: `app/src/main/assets/vscode_demos/index.json`. After recording, write durations into subtitle `atMs`.

---

## Build requirements / cloud environments

- Gradle **8.11.1** (wrapper)
- Android Gradle Plugin **8.7.3**
- Kotlin **2.0.21** + Compose Compiler plugin
- JDK **17** (21 also works for Gradle)

Without Android SDK in a cloud VM: install Command-line Tools, `sdkmanager "platforms;android-35" "build-tools;35.0.0"`, set `sdk.dir` in `local.properties`. With no emulator, validate via `./gradlew :app:assembleDebug`.

---

## Follow along on a PC (VS Code)

1. Install VS Code + **C/C++** (Microsoft).
2. Windows: MinGW/MSYS2 gcc; macOS: `xcode-select --install`; Linux: `build-essential`.
3. Open the `c-week` folder, create a `.c` file, terminal:

```bash
gcc hello.c -o hello -Wall -Wextra
./hello          # Windows: .\hello.exe
```

Day 7:

```bash
gcc dijkstra.c -o dijkstra -Wall
./dijkstra       # expected: 0 2 1 3
```

Reference: `app/src/main/assets/labs/day7_dijkstra.c`.

## License

MIT — see [LICENSE](LICENSE).
