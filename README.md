# PMDM · UDIT · Tarjeta de presentación

Aplicación Android basada en `reto1-tarjetapresentacion` de
[joscanoav/pmdm-udit](https://github.com/joscanoav/pmdm-udit).
Muestra un avatar circular, un nombre, un rol y un botón para abrir GitHub.
Consulta el [análisis y procedencia](docs/analisis-repositorios.md).

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
