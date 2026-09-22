#!/usr/bin/env bash
set -Eeuo pipefail

ROOT="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"
SDK="${ANDROID_HOME:-${ANDROID_SDK_ROOT:-$HOME/Library/Android/sdk}}"
EMU="${SDK}/emulator/emulator"
ADB="${SDK}/platform-tools/adb"
AVD="Cinerific_Standard_Tablet"
AVD_CONFIG="$HOME/.android/avd/${AVD}.avd/config.ini"
DEVICE="emulator-5554"
PORT="5554"
APP_PACKAGE="com.prahlin.cinerific"
APP_ACTIVITY="${APP_PACKAGE}/.MainActivity"
EXPECTED_SIZE="1280x800"
EXPECTED_DENSITY="160"
EXPECTED_CORES="2"
EXPECTED_SKIN="cinerific_pixel_tablet_1280"
BOOT_TIMEOUT_SECONDS="${BOOT_TIMEOUT_SECONDS:-180}"
LOG_DIR="$HOME/Library/Logs/Cinerific"
LOG="$LOG_DIR/standard-emulator.log"
ERROR_LOG="$LOG_DIR/standard-emulator.err.log"
LAUNCHD_LABEL="com.cinerific.standardavd"
LAUNCHD_PLIST="$ROOT/launchd/${LAUNCHD_LABEL}.plist"

fail() {
  printf 'ERROR: %s\n' "$*" >&2
  exit 1
}

[[ -x "$EMU" ]] || fail "Android emulator not found at $EMU"
[[ -x "$ADB" ]] || fail "adb not found at $ADB"
[[ -f "$LAUNCHD_PLIST" ]] || fail "LaunchAgent not found at $LAUNCHD_PLIST"
[[ -f "$AVD_CONFIG" ]] || fail "AVD configuration not found at $AVD_CONFIG"
"$EMU" -list-avds | grep -qx "$AVD" || fail "AVD $AVD was not found"

config_skin="$(sed -n 's/^skin.name=//p' "$AVD_CONFIG")"
config_frame="$(sed -n 's/^showDeviceFrame=//p' "$AVD_CONFIG")"
[[ "$config_skin" == "$EXPECTED_SKIN" ]] || fail "Expected skin $EXPECTED_SKIN, got $config_skin"
[[ "$config_frame" == "yes" ]] || fail "Device frame is not enabled in $AVD_CONFIG"

mkdir -p "$LOG_DIR"

device_is_connected() {
  "$ADB" devices | awk -v device="$DEVICE" \
    '$1 == device && $2 == "device" { found = 1 } END { exit !found }'
}

if device_is_connected; then
  actual_avd="$("$ADB" -s "$DEVICE" emu avd name 2>/dev/null | tr -d '\r' | sed -n '1p')"
  [[ "$actual_avd" == "$AVD" ]] || fail "$DEVICE is running $actual_avd, not $AVD"
  printf '%s is already running.\n' "$AVD"
else
  printf 'Starting %s with the low-load QA profile...\n' "$AVD"
  launchctl bootout "gui/$(id -u)/${LAUNCHD_LABEL}" >/dev/null 2>&1 || true
  : > "$LOG"
  : > "$ERROR_LOG"
  launchctl bootstrap "gui/$(id -u)" "$LAUNCHD_PLIST"
fi

deadline=$((SECONDS + BOOT_TIMEOUT_SECONDS))
until device_is_connected; do
  (( SECONDS < deadline )) || fail "Emulator did not connect within ${BOOT_TIMEOUT_SECONDS}s; see $LOG"
  sleep 2
done

until [[ "$("$ADB" -s "$DEVICE" shell getprop sys.boot_completed 2>/dev/null | tr -d '\r')" == "1" ]]; do
  (( SECONDS < deadline )) || fail "Android did not finish booting within ${BOOT_TIMEOUT_SECONDS}s; see $LOG"
  sleep 2
done

actual_avd="$("$ADB" -s "$DEVICE" emu avd name 2>/dev/null | tr -d '\r' | sed -n '1p')"
actual_size="$("$ADB" -s "$DEVICE" shell wm size | sed -n 's/^Physical size: //p' | tr -d '\r')"
actual_density="$("$ADB" -s "$DEVICE" shell wm density | sed -n 's/^Physical density: //p' | tr -d '\r')"
actual_cores="$("$ADB" -s "$DEVICE" shell 'grep -c "^processor" /proc/cpuinfo' | tr -d '\r')"

[[ "$actual_avd" == "$AVD" ]] || fail "Expected $AVD, got $actual_avd"
[[ "$actual_size" == "$EXPECTED_SIZE" ]] || fail "Expected $EXPECTED_SIZE, got $actual_size"
[[ "$actual_density" == "$EXPECTED_DENSITY" ]] || fail "Expected density $EXPECTED_DENSITY, got $actual_density"
[[ "$actual_cores" == "$EXPECTED_CORES" ]] || fail "Expected $EXPECTED_CORES cores, got $actual_cores"

sleep 10
device_is_connected || fail "Emulator disconnected after boot; see $LOG"

if "$ADB" -s "$DEVICE" shell pm path "$APP_PACKAGE" >/dev/null 2>&1; then
  "$ADB" -s "$DEVICE" shell am force-stop "$APP_PACKAGE"
  "$ADB" -s "$DEVICE" shell am start -n "$APP_ACTIVITY" >/dev/null
  printf 'Cinerific launched on %s.\n' "$DEVICE"
else
  printf 'Cinerific is not installed on %s; emulator verification still passed.\n' "$DEVICE"
fi

printf 'Verified: %s @ %s dpi, %s cores, host GPU.\n' \
  "$actual_size" "$actual_density" "$actual_cores"
printf 'Emulator log: %s\n' "$LOG"
