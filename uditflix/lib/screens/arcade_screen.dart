import 'dart:math';
import 'package:flutter/material.dart';
import 'package:flutter/scheduler.dart';

enum _Fase { inicio, jugando, fin }

class ArcadeScreen extends StatefulWidget {
  const ArcadeScreen({super.key});
  @override
  State<ArcadeScreen> createState() => _ArcadeState();
}

class _ArcadeState extends State<ArcadeScreen>
    with SingleTickerProviderStateMixin {
  static const radio = 10.0;
  static const altura = 400.0;
  static const ancho = 320.0;
  static const jugadorX = 75.0;
  static const hueco = 125.0;
  static const muro = 38.0;
  final azar = Random();
  late final Ticker reloj;
  Duration anterior = Duration.zero;
  _Fase fase = _Fase.inicio;
  double y = 200, velocidad = 0, x = ancho, paso = 210;
  int puntos = 0;
  bool contado = false;

  @override
  void initState() {
    super.initState();
    reloj = createTicker(_avanzar);
  }

  void _iniciar() {
    setState(() {
      fase = _Fase.jugando;
      puntos = 0;
      contado = false;
      y = 200;
      velocidad = -165;
      x = ancho;
      paso = 210;
      anterior = Duration.zero;
    });
    reloj.start();
  }

  void _avanzar(Duration ahora) {
    final dt = min((ahora - anterior).inMicroseconds / 1000000, 0.04);
    anterior = ahora;
    setState(() {
      velocidad += 445 * dt;
      y += velocidad * dt;
      x -= 116 * dt;
      final tocaMuro =
          jugadorX + radio > x &&
          jugadorX - radio < x + muro &&
          (y - radio < paso - hueco / 2 || y + radio > paso + hueco / 2);
      if (y - radio <= 0 || y + radio >= altura || tocaMuro) {
        fase = _Fase.fin;
        reloj.stop();
        return;
      }
      if (!contado && x + muro < jugadorX - radio) {
        puntos++;
        contado = true;
      }
      if (x < -muro) {
        x = ancho;
        paso = 105 + azar.nextDouble() * 190;
        contado = false;
      }
    });
  }

  @override
  void dispose() {
    reloj.dispose();
    super.dispose();
  }

  @override
  Widget build(BuildContext context) => Scaffold(
    appBar: AppBar(title: const Text('UDIT Arcade')),
    body: SafeArea(
      child: Column(
        children: [
          Padding(
            padding: const EdgeInsets.all(8),
            child: Text('Puntos: $puntos'),
          ),
          Expanded(
            child: Center(
              child: AspectRatio(
                aspectRatio: ancho / altura,
                child: LayoutBuilder(
                  builder: (context, area) {
                    final escala = area.maxWidth / ancho;
                    Widget pared(double top, double alto) => Positioned(
                      left: x * escala,
                      top: top * escala,
                      width: muro * escala,
                      height: alto * escala,
                      child: const ColoredBox(color: Color(0xff9e83ff)),
                    );
                    return GestureDetector(
                      behavior: HitTestBehavior.opaque,
                      onTap: () {
                        if (fase == _Fase.jugando) velocidad = -165;
                      },
                      child: Stack(
                        children: [
                          const Positioned.fill(
                            child: DecoratedBox(
                              decoration: BoxDecoration(
                                gradient: LinearGradient(
                                  begin: Alignment.topCenter,
                                  end: Alignment.bottomCenter,
                                  colors: [
                                    Color(0xff181329),
                                    Color(0xff35315d),
                                  ],
                                ),
                              ),
                            ),
                          ),
                          pared(0, paso - hueco / 2),
                          pared(paso + hueco / 2, altura - paso - hueco / 2),
                          Positioned(
                            left: (jugadorX - radio) * escala,
                            top: (y - radio) * escala,
                            child: Icon(
                              Icons.rocket_launch_rounded,
                              color: const Color(0xffdafa79),
                              size: 2 * radio * escala,
                            ),
                          ),
                          if (fase != _Fase.jugando)
                            Positioned.fill(
                              child: ColoredBox(
                                color: Colors.black54,
                                child: Center(
                                  child: Column(
                                    mainAxisSize: MainAxisSize.min,
                                    children: [
                                      Text(
                                        fase == _Fase.fin
                                            ? 'Game Over'
                                            : 'Vuelo orbital',
                                        style: const TextStyle(
                                          fontSize: 30,
                                          fontWeight: FontWeight.bold,
                                        ),
                                      ),
                                      Text(
                                        fase == _Fase.fin
                                            ? 'Puntuación: $puntos'
                                            : 'Toca para subir y esquiva los muros.',
                                      ),
                                      const SizedBox(height: 12),
                                      FilledButton(
                                        onPressed: _iniciar,
                                        child: Text(
                                          fase == _Fase.fin
                                              ? 'Reiniciar'
                                              : 'Jugar',
                                        ),
                                      ),
                                    ],
                                  ),
                                ),
                              ),
                            ),
                        ],
                      ),
                    );
                  },
                ),
              ),
            ),
          ),
        ],
      ),
    ),
  );
}
