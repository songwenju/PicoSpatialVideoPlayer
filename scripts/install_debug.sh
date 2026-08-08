#!/usr/bin/env bash
set -euo pipefail

PACKAGE_NAME="com.picoxr.destinationvideo"
APK_PATH="app/build/outputs/apk/debug/app-debug.apk"

DEVICE_SERIAL=""
SKIP_BUILD=0
START_APP=1
COMMAND="install"

usage() {
  cat <<'USAGE'
Usage: scripts/install_debug.sh [command] [options]

Commands:
  install               Build, install, and launch the debug APK. This is the default.
  open                  Launch the installed app without building or installing.

Options:
  -s, --serial <serial>  Install to the specified adb device.
  --skip-build          Install the existing debug APK without rebuilding.
  --no-start            Install only, do not launch the app.
  -h, --help            Show this help message.
USAGE
}

while [[ $# -gt 0 ]]; do
  case "$1" in
    install|open)
      COMMAND="$1"
      shift
      ;;
    -s|--serial)
      if [[ $# -lt 2 ]]; then
        echo "Missing value for $1" >&2
        exit 2
      fi
      DEVICE_SERIAL="$2"
      shift 2
      ;;
    --skip-build)
      SKIP_BUILD=1
      shift
      ;;
    --no-start)
      START_APP=0
      shift
      ;;
    -h|--help)
      usage
      exit 0
      ;;
    *)
      echo "Unknown option: $1" >&2
      usage >&2
      exit 2
      ;;
  esac
done

cd "$(dirname "$0")/.."

if ! command -v adb >/dev/null 2>&1; then
  echo "adb not found in PATH" >&2
  exit 1
fi

if [[ -z "$DEVICE_SERIAL" ]]; then
  DEVICES=()
  while IFS= read -r device; do
    DEVICES+=("$device")
  done < <(adb devices | awk 'NR > 1 && $2 == "device" { print $1 }')
  if [[ "${#DEVICES[@]}" -eq 0 ]]; then
    echo "No adb device is connected." >&2
    adb devices >&2
    exit 1
  fi
  if [[ "${#DEVICES[@]}" -gt 1 ]]; then
    echo "Multiple adb devices are connected. Use -s <serial>." >&2
    adb devices >&2
    exit 1
  fi
  DEVICE_SERIAL="${DEVICES[0]}"
fi

if [[ "$COMMAND" == "install" ]]; then
  if [[ "$SKIP_BUILD" -eq 0 ]]; then
    ./gradlew :app:assembleDebug
  fi

  if [[ ! -f "$APK_PATH" ]]; then
    echo "APK not found: $APK_PATH" >&2
    echo "Run without --skip-build to generate it." >&2
    exit 1
  fi

  echo "Installing $APK_PATH to $DEVICE_SERIAL"
  adb -s "$DEVICE_SERIAL" install -r "$APK_PATH"
fi

if [[ "$START_APP" -eq 1 ]]; then
  echo "Starting $PACKAGE_NAME"
  adb -s "$DEVICE_SERIAL" shell monkey \
    -p "$PACKAGE_NAME" \
    -c android.intent.category.LAUNCHER \
    1
fi
