# Reto 1 · Tarjeta de Presentación Profesional

**Módulo:** 0489 · Programación Multimedia y Dispositivos Móviles  
**Perfil:** unknowncoder05  
**Tecnología:** Kotlin + Jetpack Compose (Android nativo)  
**RA vinculado:** RA1 · Tecnologías de desarrollo para dispositivos móviles

## Qué es esta app

Tarjeta digital con avatar circular, nombre, rol y botón al perfil de GitHub.
Adaptada del ejercicio de Jorge Oscanoa (joscanoav); la procedencia y el commit
analizado están en [el análisis](../docs/analisis-repositorios.md).

## Objetivo del reto

Construir una aplicación propia a partir de una base Android, usando componentes
Compose, recursos de imagen y un icono personalizado, con commits por funcionalidad.

## Componentes y conceptos utilizados

| Componente | Uso |
| --- | --- |
| `Column` | Organiza avatar, nombre, rol y botón en vertical. |
| `Image` + `clip(CircleShape)` | Avatar de 150 dp con recorte circular. |
| `Text` y `Spacer` | Presentación y separación entre elementos. |
| `Button`, `Intent.ACTION_VIEW` y `Uri` | Abren el perfil GitHub. |
| `res/drawable` | Foto propia en JPEG e icono personal en PNG, incorporados desde S3. |
| `res/mipmap-anydpi*` | Icono personal adaptable y monocromático; compatible desde API 24. |
| `strings.xml` | Nombre de app, nombre personal, rol, enlace y textos accesibles. |
| `safeDrawingPadding` y `verticalScroll` | Evitan barras del sistema y permiten pantallas pequeñas. |

## Cómo ejecutar el proyecto

Abre la carpeta superior `pmdm-udit` en Android Studio, sincroniza Gradle y selecciona
este módulo. Usa JDK 17, SDK 35 y un emulador/dispositivo API 24 o superior.
Desde la carpeta superior: `./gradlew :reto1-tarjetapresentacion:assembleDebug`.

## Memoria breve del reto

La implementación demuestra la separación entre Activity y componentes Compose,
el uso de recursos para personalizar textos e imágenes, y la apertura de enlaces
mediante intents. El tema Material 3 conserva variantes claras y oscuras. Los
recursos de imagen se mantienen por debajo de 512 KiB para evitar archivos grandes.
La experiencia personal de aprendizaje debe completarla el alumno antes de entregar.

## Dificultades y soluciones de la implementación

- La referencia usa SDK 37 y Gradle 9.6; se adapta a SDK 35/JDK 17 disponibles.
- Se omite la foto de la referencia de 701876 bytes. La foto propia de S3 se
  convierte de PNG a JPEG (137432 bytes), conservando resolución y encuadre.
- El icono personal de S3 (24784 bytes) se usa en los launchers antiguos,
  adaptables y monocromáticos.
- El contenido centrado puede exceder pantallas pequeñas: se permite desplazamiento.
- Un dispositivo sin navegador no debe cerrar la app: se captura la ausencia de
  una Activity compatible y se muestra un aviso.

## Archivos personales y capturas

La foto propia y el icono personal ya están incorporados desde el bucket privado.
Faltan las capturas reales de la app Android y de su icono instalado.
Sube esas capturas al bucket indicado en
[las instrucciones de archivos](../docs/archivos-personales.md).

## Estructura del proyecto

Este módulo usa `src/main` directamente (equivale a `app/src/main` en la referencia):

```text
src/main/java/com/example/reto1_tarjetapresentacion/MainActivity.kt
src/main/java/com/example/reto1_tarjetapresentacion/GitHubAction.kt
src/main/res/drawable/foto_perfil.jpg
src/main/res/drawable/ic_launcher_personal.png
src/main/res/mipmap-anydpi*/ic_launcher.xml
src/main/res/values/strings.xml
src/androidTest/java/com/example/reto1_tarjetapresentacion/TarjetaPresentacionTest.kt
```

## Enlace

GitHub: https://github.com/unknowncoder05
