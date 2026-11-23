import 'dart:ui' as ui;
import 'package:flutter/painting.dart';
import 'package:uditflix/data/contenidos.dart';
import 'package:flutter_test/flutter_test.dart';
import 'package:uditflix/main.dart';

void main() {
  TestWidgetsFlutterBinding.ensureInitialized();
  setUp(() async {
    final recorder = ui.PictureRecorder();
    ui.Canvas(recorder);
    final image = await recorder.endRecording().toImage(2, 2);
    for (final obra in contenidos) {
      PaintingBinding.instance.imageCache.putIfAbsent(
        NetworkImage(obra.imagenUrl),
        () => OneFrameImageStreamCompleter(
          Future.value(ImageInfo(image: image.clone())),
        ),
      );
    }
    image.dispose();
  });
  testWidgets('El destacado abre la ficha y ofrece reproducción', (
    tester,
  ) async {
    await tester.pumpWidget(const UDITflixApp());
    await tester.tap(find.text('Ver ficha'));
    await tester.pumpAndSettle();
    expect(find.text('Stranger Things'), findsOneWidget);
    await tester.scrollUntilVisible(find.text('Reproducir demo'), 120);
    expect(find.text('Reproducir demo'), findsOneWidget);
  });

  testWidgets('Arcade permite jugar, perder y reiniciar', (tester) async {
    await tester.pumpWidget(const UDITflixApp());
    await tester.tap(find.text('UDIT Arcade'));
    await tester.pumpAndSettle();
    await tester.tap(find.text('Jugar'));
    for (int i = 0; i < 80; i++) {
      await tester.pump(const Duration(milliseconds: 30));
    }
    expect(find.text('Game Over'), findsOneWidget);
    await tester.tap(find.text('Reiniciar'));
    await tester.pump();
    expect(find.text('Game Over'), findsNothing);
    expect(find.text('Puntos: 0'), findsOneWidget);
    await tester.pageBack();
    await tester.pumpAndSettle();
  });
}
