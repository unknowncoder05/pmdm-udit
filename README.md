# PMDM · UDIT · Tarjeta de presentación

Aplicación Android basada en `reto1-tarjetapresentacion` de
[joscanoav/pmdm-udit](https://github.com/joscanoav/pmdm-udit).
Muestra un avatar circular, un nombre, un rol y un botón para abrir GitHub.
Consulta el [análisis y procedencia](docs/analisis-repositorios.md).

## Reto 2 · Local Burger

El módulo [reto2-locales](reto2-locales/README.md) es un marketplace sencillo de
hamburgueserías en venta y traspaso: seis locales de ejemplo, carga inicial,
cuadrícula adaptable, filtro por operación, detalles y compra simulada.
Usa Kotlin y Jetpack Compose, siguiendo `s4-burgershop` de Jorge Oscanoa.

```sh
./gradlew :reto2-locales:assembleDebug
```

La [APK de Local Burger](https://sandbox.yerson.co/pmdm/local-burger.apk) está
disponible desde la página existente. La app funciona sin conexión y no realiza pagos.

## Ejecutar

Abre esta carpeta en Android Studio con JDK 17. Instala Android SDK 35 y
Build Tools 35.0.0. Android Studio genera `local.properties`; también puedes definir
`ANDROID_HOME` apuntando a tu SDK. Selecciona `reto1-tarjetapresentacion` y pulsa Run
con un dispositivo/emulador Android 7.0 (API 24) o superior.

```sh
./gradlew :reto1-tarjetapresentacion:assembleDebug
./gradlew :reto1-tarjetapresentacion:lintDebug
./gradlew :reto1-tarjetapresentacion:connectedDebugAndroidTest
```

La última orden requiere un dispositivo o emulador conectado.
La APK se genera en `reto1-tarjetapresentacion/build/outputs/apk/debug/`.

## Personalizar

Edita `reto1-tarjetapresentacion/src/main/res/values/strings.xml` para cambiar
`profile_name`, `profile_role` y `github_url`. El perfil inicial es `unknowncoder05`,
obtenido del remoto Git de este repositorio. La foto propia está en
`res/drawable/foto_perfil.jpg` y el icono en `res/drawable/ic_launcher_personal.png`.
Puedes sustituirlos manteniendo los nombres de recurso y archivos menores de 512 KiB.

La imagen de referencia de 685 KiB se omite. La foto subida a S3 se convierte a
JPEG de 134 KiB, conservando el encuadre, y se incorpora junto al icono personal.
La pantalla respeta los márgenes del sistema y permite desplazar el contenido en
pantallas pequeñas. Si no existe una app para abrir GitHub, muestra un aviso.

Consulta el [README del reto](reto1-tarjetapresentacion/README.md) y las
[instrucciones para subir foto y capturas](docs/archivos-personales.md).

## Navegador y despliegue

La versión de navegador de la tarjeta está preparada para
`https://sandbox.yerson.co/pmdm/`. Usa los mismos textos y avatar que Android;
el código nativo sigue siendo Kotlin/Compose. No requiere API ni lista de hosts
en un backend. Nginx sirve archivos estáticos bajo el host existente.

```sh
python3 scripts/build-web.py
./scripts/deploy-web.sh
```

El script añade exclusivamente `/pmdm` y `/pmdm/`, guarda una copia de la
configuración existente y ejecuta `nginx -t` antes de recargar. La escritura en
`/etc/nginx` y la recarga necesitan permisos de administrador. La carpeta `dist`
se regenera desde los recursos Android y no se guarda en Git. Si se ha compilado
la APK, la página también permite descargarla.

Comprobación visual con Chromium/Playwright instalados:
`node scripts/check-web.cjs https://sandbox.yerson.co/pmdm/`. Se pueden definir
`PMDM_PLAYWRIGHT_PATH` y `PMDM_CHROMIUM_PATH` para otra instalación.
