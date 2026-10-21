# Emulador Android gratuito

El emulador ejecuta la APK real de Local Burger, sin convertir ni modificar Kotlin.
Android Emulator, Xvfb, x11vnc y noVNC se ejecutan en este servidor; no se contrata
ningún servicio de emulación externo. La pantalla del dispositivo se transmite
al navegador mediante WebSocket.

## Componentes

- SDK Android existente: `/home/ubuntu/android-sdk`.
- Android Emulator y sistema AOSP Android 7.0 / API 24 / x86, compatible con el
  `minSdk = 24` de la app y sin servicios de Google Play.
- Dispositivo virtual `pmdm-free`, 360 × 800 píxeles, 1 GB de RAM y dos núcleos.
- `Xvfb :94`, x11vnc en `127.0.0.1:5940` y websockify en `127.0.0.1:6084`.
- Nginx con autenticación HTTP básica para el acceso desde el navegador.

Este host no expone `/dev/kvm`: el arranque utiliza `-accel off` y renderizado
SwiftShader. Puede ser considerablemente más lento que un emulador con KVM.
El servicio limita CPU y memoria para compartir el servidor con las otras apps.
Antes de arrancar debe haber suficiente disco libre para los datos del dispositivo.

## Instalación y ejecución

Los paquetes del sistema necesarios son `xvfb`, `x11vnc`, `novnc` y `websockify`.
Los paquetes del SDK son `emulator` y `system-images;android-24;default;x86`.
El AVD se guarda en `.emulator/avd`, excluido de Git, junto con los registros.

```sh
sudo install -m 644 deploy/pmdm-emulator.service /etc/systemd/system/
sudo systemctl daemon-reload
sudo systemctl start pmdm-emulator
sudo systemctl status pmdm-emulator
```

El servicio está habilitado para arrancar después de reiniciar el host.

La configuración del AVD es local. El lanzador comprueba que exista y abre
`dist/local-burger.apk` al terminar de arrancar Android. Regenera esa APK mediante
Gradle y `scripts/build-web.py` cuando cambie la app.

```sh
sudo systemctl stop pmdm-emulator
```

Detener el servicio libera los procesos del emulador, pantalla y transmisión.
Los datos locales del dispositivo se conservan y no se utilizan snapshots.
La APK se reinstala solamente cuando cambia su contenido o no está instalada.

## Acceso web

El fragmento `deploy/nginx-emulator-location.conf` reserva `/pmdm/emulador/`.
Debe insertarse en el servidor HTTPS existente después de preparar el servicio
y `/etc/nginx/pmdm-emulator.htpasswd`. Ese fichero contiene únicamente la contraseña
codificada y debe poder leerlo el grupo `www-data`.

Las credenciales no se guardan en Git. La contraseña local se conserva en un
archivo privado dentro de `.emulator/`. Los puertos VNC y WebSocket escuchan
exclusivamente en loopback; el acceso público pasa por HTTPS y autenticación.
Los navegadores conectados comparten el mismo dispositivo Android.

Para comprobar la cuadrícula adaptable, usa los botones de giro de la barra
lateral del emulador. En vertical se muestra una columna y en horizontal caben
dos. Las capturas reales están enlazadas en el README del Reto 2.

## Referencias

- [Android Emulator: línea de comandos](https://developer.android.com/studio/run/emulator-commandline).
- [Aceleración y KVM](https://developer.android.com/studio/run/emulator-acceleration).
- [noVNC](https://github.com/novnc/noVNC).
