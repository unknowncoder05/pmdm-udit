# UDITflix

Adaptación del [avance del catálogo visual](https://github.com/joscanoav/pmdm-udit/tree/mejora-catalogo-uditflix/uditflix). La portada destaca Stranger Things y permite explorar tendencias, películas y series. Las imágenes se cargan desde Internet y cuentan con una alternativa local.

## Reproducción

Pulsa **Ver demo** en el destacado o abre una tarjeta y elige **Reproducir demo**. Todas las fichas utilizan el mismo clip original de diez segundos, incluido en el paquete. Pulsa el botón de reproducción para empezar o pausar. La barra permite avanzar y retroceder.

## UDIT Arcade

Abre **UDIT Arcade** en la cabecera y pulsa **Jugar**. Cada toque impulsa la nave hacia arriba. Ganas un punto por cada muro superado. Chocar con un muro, el techo o el suelo termina la partida. Pulsa **Reiniciar** para volver a jugar.

## Ejecutar y verificar

Desde esta carpeta, con el SDK de Flutter y las herramientas de Android instalados:

```sh
flutter pub get
flutter run
flutter analyze
flutter test
flutter build web
flutter build apk --release
```

La versión web se genera en `build/web/`. El APK de demostración se genera en `build/app/outputs/flutter-apk/app-release.apk` y utiliza la firma de desarrollo. Conecta un dispositivo con depuración USB y ejecuta `flutter install` para instalarlo.

## Implementación

- `lib/models/contenido.dart` y `lib/data/contenidos.dart` contienen el modelo y los seis títulos de la rama de avance.
- `lib/screens/catalogo_screen.dart`, `ficha_screen.dart` y `lib/widgets/portada_network.dart` implementan el destacado, los filtros, las fichas y las portadas.
- `lib/screens/reproductor_screen.dart` usa el paquete añadido `video_player` y el recurso `assets/video/uditflix.mp4`.
- `lib/screens/arcade_screen.dart` usa el reloj de animación de Flutter para calcular movimiento, colisiones y puntuación.
- `test/widget_test.dart` comprueba la navegación del catálogo y el ciclo de juego.
