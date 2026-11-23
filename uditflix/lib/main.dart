import 'package:flutter/material.dart';
import 'screens/catalogo_screen.dart';

void main() => runApp(const UDITflixApp());

class UDITflixApp extends StatelessWidget {
  const UDITflixApp({super.key});

  @override
  Widget build(BuildContext context) => MaterialApp(
    title: 'UDITflix',
    debugShowCheckedModeBanner: false,
    theme: ThemeData(
      brightness: Brightness.dark,
      scaffoldBackgroundColor: const Color(0xff111214),
      colorScheme: ColorScheme.fromSeed(
        seedColor: const Color(0xffdafa79),
        brightness: Brightness.dark,
        primary: const Color(0xffdafa79),
      ),
      appBarTheme: const AppBarTheme(backgroundColor: Color(0xff111214)),
      useMaterial3: true,
    ),
    home: const CatalogoScreen(),
  );
}
