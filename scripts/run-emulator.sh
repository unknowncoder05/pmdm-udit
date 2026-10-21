#!/usr/bin/env bash
# Ejecuta la APK Android real y comparte únicamente su pantalla mediante noVNC.
set -euo pipefail
PROJECT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")/.." && pwd)"
RUNTIME_DIR="$PROJECT_DIR/.emulator"
SDK_DIR="${ANDROID_SDK_ROOT:-/home/ubuntu/android-sdk}"
export ANDROID_AVD_HOME="$RUNTIME_DIR/avd"
export DISPLAY=:94
mkdir -p "$RUNTIME_DIR"
chmod 700 "$RUNTIME_DIR"
if [[ ! -f "$ANDROID_AVD_HOME/pmdm-free.avd/config.ini" ]]; then
    echo 'Falta el dispositivo virtual pmdm-free. Consulta docs/emulador.md.' >&2
    exit 1
fi

service_pids=()
bootstrap_pid=''
cleanup() {
    if [[ -n "$bootstrap_pid" ]]; then kill "$bootstrap_pid" 2>/dev/null || true; fi
    for service_pid in "${service_pids[@]}"; do kill "$service_pid" 2>/dev/null || true; done
}
trap cleanup EXIT
trap 'exit 0' TERM INT

Xvfb "$DISPLAY" -screen 0 520x920x24 -nolisten tcp -ac > "$RUNTIME_DIR/display.log" 2>&1 &
service_pids+=("$!")
for attempt in {1..30}; do
    [[ -S /tmp/.X11-unix/X94 ]] && break
    sleep 1
done
"$SDK_DIR/emulator/emulator" -avd pmdm-free -accel off -gpu swiftshader \
    -no-snapshot -no-boot-anim -no-audio -no-cache -memory 1024 -cores 2 -port 5580 \
    > "$RUNTIME_DIR/android.log" 2>&1 &
service_pids+=("$!")
x11vnc -display "$DISPLAY" -rfbport 5940 -localhost -forever -shared -nopw \
    -noxdamage -quiet > "$RUNTIME_DIR/vnc.log" 2>&1 &
service_pids+=("$!")
/usr/bin/websockify --web=/usr/share/novnc 127.0.0.1:6084 127.0.0.1:5940 \
    > "$RUNTIME_DIR/web.log" 2>&1 &
service_pids+=("$!")

(
    adb_tool="$SDK_DIR/platform-tools/adb"
    for attempt in {1..180}; do
        if [[ "$(timeout 10 "$adb_tool" -s emulator-5580 shell getprop sys.boot_completed 2>/dev/null | tr -d '\r')" == 1 ]]; then
            "$adb_tool" -s emulator-5580 shell settings put global window_animation_scale 0
            "$adb_tool" -s emulator-5580 shell settings put global transition_animation_scale 0
            "$adb_tool" -s emulator-5580 shell settings put global animator_duration_scale 0
            "$adb_tool" -s emulator-5580 shell input keyevent 82
            package_digest="$(sha256sum "$PROJECT_DIR/dist/local-burger.apk")"
            package_digest="${package_digest%% *}"
            previous_digest="$(cat "$RUNTIME_DIR/installed-apk.sha256" 2>/dev/null || true)"
            installed_package="$("$adb_tool" -s emulator-5580 shell pm path com.unknowncoder05.localburger 2>/dev/null || true)"
            if [[ "$package_digest" != "$previous_digest" || "$installed_package" != package:* ]]; then
                "$adb_tool" -s emulator-5580 install -r "$PROJECT_DIR/dist/local-burger.apk"
                printf '%s\n' "$package_digest" > "$RUNTIME_DIR/installed-apk.sha256"
            fi
            "$adb_tool" -s emulator-5580 shell am start -n com.unknowncoder05.localburger/.MainActivity
            echo 'Local Burger abierta en el emulador.'
            exit 0
        fi
        sleep 5
    done
    echo 'Android no ha terminado de arrancar en el tiempo previsto.' >&2
    exit 1
) > "$RUNTIME_DIR/app.log" 2>&1 &
bootstrap_pid="$!"

# Bash 5.0 puede despertar wait -n cuando termina el instalador. Vigilar solo
# los procesos esenciales permite que el emulador siga abierto tras instalar.
while true; do
    for service_pid in "${service_pids[@]}"; do
        if ! kill -0 "$service_pid" 2>/dev/null; then
            echo "Ha terminado un proceso esencial del emulador: $service_pid" >&2
            exit 1
        fi
    done
    sleep 2
done
