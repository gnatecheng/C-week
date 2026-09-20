#!/usr/bin/env bash
set -euo pipefail
ROOT="$(cd "$(dirname "$0")/.." && pwd)"
cd "$ROOT"
if [[ ! -f local.properties ]]; then
  if [[ -n "${ANDROID_HOME:-}" ]]; then
    echo "sdk.dir=${ANDROID_HOME}" > local.properties
  elif [[ -n "${ANDROID_SDK_ROOT:-}" ]]; then
    echo "sdk.dir=${ANDROID_SDK_ROOT}" > local.properties
  else
    echo "Create local.properties with sdk.dir=... first" >&2
    exit 1
  fi
fi
./gradlew :app:assembleDebug
