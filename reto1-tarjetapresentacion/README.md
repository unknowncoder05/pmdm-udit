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
| `res/drawable` | Avatar vectorial pequeño, reemplazable por una foto propia. |
| `res/mipmap-anydpi*` | Icono propio de tarjeta, adaptable y monocromático. |
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
recursos vectoriales permiten tener un icono propio sin incorporar imágenes grandes.
La experiencia personal de aprendizaje debe completarla el alumno antes de entregar.

## Dificultades y soluciones de la implementación

- La referencia usa SDK 37 y Gradle 9.6; se adapta a SDK 35/JDK 17 disponibles.
- Se omite la foto original de 701876 bytes y se añade un avatar vectorial.
- El contenido centrado puede exceder pantallas pequeñas: se permite desplazamiento.
- Un dispositivo sin navegador no debe cerrar la app: se captura la ausencia de
  una Activity compatible y se muestra un aviso.

## Capturas y archivos personales pendientes

Faltan la foto propia y las capturas reales de la app y de su icono instalado.
Sube los archivos al bucket privado indicado en
[las instrucciones de archivos](../docs/archivos-personales.md).
No se presentan capturas ficticias ni el avatar provisional como foto personal.

## Estructura del proyecto

Este módulo usa `src/main` directamente (equivale a `app/src/main` en la referencia):

```text
src/main/java/com/example/reto1_tarjetapresentacion/MainActivity.kt
src/main/java/com/example/reto1_tarjetapresentacion/GitHubAction.kt
src/main/res/drawable/foto_perfil.xml
src/main/res/mipmap-anydpi*/ic_launcher.xml
src/main/res/values/strings.xml
src/androidTest/java/com/example/reto1_tarjetapresentacion/TarjetaPresentacionTest.kt
```

## Enlace

GitHub: https://github.com/unknowncoder05
