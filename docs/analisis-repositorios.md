# Análisis de los repositorios de referencia

Repositorios descargados en `../.pmdm/`, a partir de `r.md`.

| Repositorio | Commit analizado | Contenido |
| --- | --- | --- |
| [Chacal231](https://github.com/Chacal231/pmdm-udit) | `454e9344d457076a5ca91a69a324fad237fddc31` | Saludo Kotlin y tarjeta con foto circular y GitHub. |
| [jorgeberenguer2023-ops](https://github.com/jorgeberenguer2023-ops/pmdm-udit) | `079c2caca0fb55949112c70395f2e9c543ae6792` | Solo README y .gitignore; no contiene aplicación. |
| [lucas196669](https://github.com/lucas196669/pmdm-udit) | `4207196967ce7a6335af62634e673d9cd2d23983` | Saludo, tarjeta con GitHub y plantilla Flutter de contador. |
| [sha2dev56](https://github.com/sha2dev56/pmdm-udit) | `63f8ed5690fd722ee85c557f40d2a7dd876c3ebd` | Saludo y tarjeta con GitHub, LinkedIn y correo. |

## Referencia principal: joscanoav

Tras ampliar `r.md`, la reconstrucción se centra en
[joscanoav/pmdm-udit](https://github.com/joscanoav/pmdm-udit), commit `8d2ddda95496ca6b2c89559c8268a7df48624636`.
Contiene `S02-MiPrimeraApp` (saludo con nombre y curso) y
`reto1-tarjetapresentacion` (avatar circular, nombre, rol y botón GitHub).
Su README también explica los componentes de Compose y la entrega del ejercicio.

## Reconstrucción

Por petición del usuario, solo se reconstruye `reto1-tarjetapresentacion`.
`S02-MiPrimeraApp` queda como referencia descargada. La pantalla, temas y
recursos pequeños proceden de joscanoav y se adaptan al perfil `unknowncoder05`,
obtenido del remoto local. El Gradle Wrapper inicial procede de sha2dev56.

Se omiten archivos de más de 512 KiB en el checkout de referencia. El único archivo
por encima de ese límite es `foto_perfil.jpg` (701876 bytes); en el proyecto local
se sustituye por la foto propia subida a S3, convertida a JPEG de 137432 bytes.
El icono propio de S3 ocupa 24784 bytes. No se incorporan fotos de otras personas,
carpetas `build`, cachés `.gradle`, configuración `.idea` ni rutas SDK personales.

Se usan SDK 35, Java 17, AGP 8.9.2, Gradle 8.12 y Kotlin 2.1.20 para aprovechar el
SDK/JDK disponibles. Se reemplaza así la configuración SDK 37/Gradle 9.6 de las
referencias. Compatibilidad de AGP:
https://developer.android.com/build/releases/agp-8-9-0-release-notes
Compose BOM: https://developer.android.com/develop/ui/compose/bom/bom-mapping

Las otras cuatro referencias quedan descargadas para consulta. No se incorpora
la plantilla Flutter ni las funciones adicionales de contacto de sha2dev56.
No se encontró un archivo LICENSE; se documenta la procedencia sin atribuir
una licencia nueva al código de referencia.

## Historial por funcionalidad

El historial local se agrupa en tres cambios: reconstrucción de la tarjeta Android,
foto e icono personales, y publicación de la versión de navegador en `/pmdm`.
Por petición del usuario, las fechas de autor de los dos cambios Android coinciden
con el commit de referencia `8d2ddda`: `2026-09-30T20:09:19+02:00`.
Las fechas de confirmación conservan el momento real de la reorganización;
la publicación web conserva también su fecha de autor real.
La autoría sigue siendo del perfil local, y la procedencia del código se documenta
arriba. El historial anterior se conserva en una rama local de respaldo.
