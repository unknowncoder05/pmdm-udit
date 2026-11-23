import 'package:flutter_test/flutter_test.dart';
import 'package:uditflix/main.dart';

void main() {
  testWidgets('Filtra el catálogo y abre la vista previa de un juego', (tester) async {
    await tester.pumpWidget(const UDITflixApp());
    expect(find.text('UDITflix'), findsOneWidget);
    expect(find.text('Introducción a Flutter'), findsOneWidget);

    await tester.tap(find.text('Juego'));
    await tester.pumpAndSettle();
    expect(find.text('Introducción a Flutter'), findsNothing);
    await tester.tap(find.text('Programación de videojuegos'));
    await tester.pumpAndSettle();
    final abrir = find.text('Abrir vista previa');
    expect(find.text("Ficha de catálogo"), findsOneWidget);
    await tester.scrollUntilVisible(abrir, 180);
    await tester.tap(abrir);
    await tester.pumpAndSettle();
    expect(find.text('Vista previa'), findsOneWidget);
    await tester.pageBack();
    await tester.pumpAndSettle();
    await tester.pageBack();
    await tester.pumpAndSettle();
    expect(find.text('Introducción a Flutter'), findsNothing);
    expect(find.text('Programación de videojuegos'), findsOneWidget);
  });
}
