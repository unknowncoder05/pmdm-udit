# 🎬 UDITflix

**Cine, series y una partida entre sesiones.** Explora seis títulos desde un catálogo hecho con Flutter, prueba el reproductor de vídeo o pilota una nave en UDIT Arcade.

<p align="center">
  <img src="assets/cubiertas/1.png" width="32%" alt="Cubierta geométrica azul con una figura en zigzag">
  <img src="assets/cubiertas/3.png" width="32%" alt="Cubierta amarilla con una estrella en relieve">
  <img src="assets/cubiertas/4.png" width="32%" alt="Cubierta violeta con un personaje de videojuego">
</p>

*Tres de las cubiertas locales que acompañan al catálogo cuando las portadas de Internet no están disponibles.*

## 🍿 Una vuelta por UDITflix

| Desde la app | Qué puedes hacer |
| --- | --- |
| **Catálogo** | Empieza por *Stranger Things*, el título destacado. Explora **Tendencias**, **Películas** o **Series** y abre una ficha para consultar el género, la duración y la sinopsis. |
| **Reproductor** | Pulsa **Ver demo** en el destacado o **Reproducir demo** en una ficha. Todas comparten un clip original de diez segundos incluido en la app. Puedes pausarlo o arrastrar la barra para repetir un momento. |
| **UDIT Arcade** | Entra desde la cabecera y pulsa **Jugar**. Toca la pantalla para impulsar la nave. Cada muro superado vale un punto. Si chocas con un muro, el techo o el suelo, pulsa **Reiniciar** para intentarlo de nuevo. |

## 🚀 Ejecutar en local

Necesitas Flutter con Dart 3.8.1 o compatible. Desde la raíz de este repositorio, entra en el proyecto e instala sus dependencias.

```sh
cd uditflix
flutter pub get
```

| Para abrir la app en… | Ejecuta |
| --- | --- |
| Chrome | `flutter run -d chrome` |
| Android | Conecta un móvil con depuración USB o inicia un emulador. Ejecuta `flutter devices` y después `flutter run -d <id>` con el identificador que aparezca. |

## 🛠️ Pruebas y compilación

Ejecuta estos comandos desde `uditflix/`.

| Tarea | Comando | Resultado |
| --- | --- | --- |
| Analizar el código | `flutter analyze` | Diagnóstico de Dart y Flutter |
| Probar la app | `flutter test` | Comprueba el acceso a la ficha y el ciclo de juego, derrota y reinicio |
| Compilar para la web | `flutter build web` | Sitio estático en `build/web/` |
| Generar el APK | `flutter build apk --release` | APK con firma de desarrollo en `build/app/outputs/flutter-apk/app-release.apk` |

Con las herramientas de Android instaladas y el móvil conectado por USB, ejecuta `flutter install` para instalar el APK generado.

## 🧩 Dentro del proyecto

- **Catálogo.** Los seis títulos se definen en [los datos del catálogo](lib/data/contenidos.dart) y comparten el [modelo Contenido](lib/models/contenido.dart). La [pantalla principal](lib/screens/catalogo_screen.dart) y la [ficha](lib/screens/ficha_screen.dart) utilizan el mismo [componente de portada](lib/widgets/portada_network.dart).
- **Vídeo.** El [reproductor](lib/screens/reproductor_screen.dart) utiliza `video_player` para leer el [MP4 local](assets/video/uditflix.mp4).
- **Arcade.** El [juego](lib/screens/arcade_screen.dart) utiliza el reloj de animación de Flutter para actualizar el vuelo, detectar colisiones y contar puntos.
- **Pruebas.** Los recorridos del catálogo y del juego se comprueban en las [pruebas de widgets](test/widget_test.dart).

El proyecto parte del [avance del catálogo visual de UDITflix](https://github.com/joscanoav/pmdm-udit/tree/mejora-catalogo-uditflix/uditflix).
