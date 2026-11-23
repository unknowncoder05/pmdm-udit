import 'package:flutter/material.dart';
import '../data/contenidos.dart';
import '../models/contenido.dart';
import 'ficha_screen.dart';
import 'arcade_screen.dart';
import 'reproductor_screen.dart';
import '../widgets/portada_network.dart';

class CatalogoScreen extends StatefulWidget {
  const CatalogoScreen({super.key});
  @override
  State<CatalogoScreen> createState() => _CatalogoState();
}

class _CatalogoState extends State<CatalogoScreen> {
  String filtro = 'Tendencias';

  @override
  Widget build(BuildContext context) => Scaffold(
    appBar: AppBar(
      leading: Icon(
        Icons.local_movies_outlined,
        color: Theme.of(context).colorScheme.primary,
      ),
      titleSpacing: 0,
      actions: [
        TextButton.icon(
          icon: const Icon(Icons.sports_esports_outlined),
          label: const Text('UDIT Arcade'),
          onPressed: () => Navigator.push(
            context,
            MaterialPageRoute(builder: (_) => const ArcadeScreen()),
          ),
        ),
      ],
      title: const Text(
        'UDITflix',
        style: TextStyle(fontWeight: FontWeight.w900),
      ),
    ),
    body: SafeArea(
      child: Center(
        child: ConstrainedBox(
          constraints: const BoxConstraints(maxWidth: 1200),
          child: ListView(
            padding: const EdgeInsets.fromLTRB(24, 16, 24, 40),
            children: [
              const _Destacado(),
              const SizedBox(height: 24),
              const Text(
                'Catálogo',
                style: TextStyle(
                  fontWeight: FontWeight.w900,
                  fontSize: 48,
                  letterSpacing: -2,
                ),
              ),
              const SizedBox(height: 12),
              Wrap(
                spacing: 8,
                children: [
                  for (final tipo in ['Tendencias', 'Películas', 'Series'])
                    ChoiceChip(
                      label: Text(tipo),
                      selected: filtro == tipo,
                      onSelected: (_) => setState(() => filtro = tipo),
                    ),
                ],
              ),
              const SizedBox(height: 24),
              LayoutBuilder(
                builder: (context, bounds) {
                  final columnas = (bounds.maxWidth / 265).floor().clamp(1, 3);
                  final ancho =
                      (bounds.maxWidth - 16 * (columnas - 1)) / columnas;
                  return Wrap(
                    spacing: 16,
                    runSpacing: 20,
                    children: [
                      for (final item in contenidos.where(
                        (c) =>
                            filtro == 'Tendencias' ||
                            '${c.categoria}s' == filtro,
                      ))
                        SizedBox(width: ancho, child: _Tarjeta(item)),
                    ],
                  );
                },
              ),
            ],
          ),
        ),
      ),
    ),
  );
}

class _Tarjeta extends StatelessWidget {
  const _Tarjeta(this.item);
  final Contenido item;

  @override
  Widget build(BuildContext context) => Material(
    color: const Color(0xff202225),
    borderRadius: BorderRadius.circular(16),
    clipBehavior: Clip.antiAlias,
    child: InkWell(
      onTap: () => Navigator.push(
        context,
        MaterialPageRoute(builder: (_) => FichaScreen(contenido: item)),
      ),
      child: Column(
        crossAxisAlignment: CrossAxisAlignment.stretch,
        children: [
          AspectRatio(
            aspectRatio: 1.5,
            child: PortadaNetwork(url: item.imagenUrl, alternativa: item.id),
          ),
          Padding(
            padding: const EdgeInsets.all(16),
            child: Column(
              crossAxisAlignment: CrossAxisAlignment.start,
              children: [
                Text(
                  '${item.tipo.toUpperCase()}  ·  ${item.duracion}',
                  style: TextStyle(
                    color: Theme.of(context).colorScheme.primary,
                  ),
                ),
                const SizedBox(height: 8),
                Row(
                  children: [
                    Expanded(
                      child: Text(
                        item.titulo,
                        style: const TextStyle(
                          fontSize: 20,
                          fontWeight: FontWeight.bold,
                        ),
                      ),
                    ),
                    const Icon(Icons.arrow_outward, size: 18),
                  ],
                ),
              ],
            ),
          ),
        ],
      ),
    ),
  );
}

class _Destacado extends StatelessWidget {
  const _Destacado();

  @override
  Widget build(BuildContext context) {
    final obra = contenidos.first;
    return ClipRRect(
      borderRadius: BorderRadius.circular(24),
      child: Stack(
        children: [
          Positioned.fill(
            child: PortadaNetwork(
              url: obra.imagenUrl,
              alternativa: obra.id,
              alignment: const Alignment(0, -0.65),
            ),
          ),
          const Positioned.fill(
            child: DecoratedBox(
              decoration: BoxDecoration(
                gradient: LinearGradient(
                  begin: Alignment.centerLeft,
                  end: Alignment.centerRight,
                  colors: [
                    Color(0xf0121214),
                    Color(0x80121214),
                    Color(0x30121214),
                  ],
                ),
              ),
            ),
          ),
          Padding(
            padding: const EdgeInsets.all(28),
            child: Column(
              crossAxisAlignment: CrossAxisAlignment.start,
              children: [
                Text(
                  'DESTACADO',
                  style: TextStyle(
                    color: Theme.of(context).colorScheme.primary,
                    letterSpacing: 2,
                  ),
                ),
                const SizedBox(height: 48),
                Text(
                  obra.titulo,
                  style: const TextStyle(
                    fontSize: 38,
                    fontWeight: FontWeight.w900,
                  ),
                ),
                Text('${obra.tipo}  ·  ${obra.duracion}'),
                const SizedBox(height: 16),
                Wrap(
                  spacing: 12,
                  runSpacing: 6,
                  children: [
                    FilledButton.icon(
                      icon: const Icon(Icons.play_arrow),
                      label: const Text('Ver demo'),
                      onPressed: () => Navigator.push(
                        context,
                        MaterialPageRoute(
                          builder: (_) => ReproductorScreen(contenido: obra),
                        ),
                      ),
                    ),
                    OutlinedButton(
                      onPressed: () => Navigator.push(
                        context,
                        MaterialPageRoute(
                          builder: (_) => FichaScreen(contenido: obra),
                        ),
                      ),
                      child: const Text('Ver ficha'),
                    ),
                  ],
                ),
              ],
            ),
          ),
        ],
      ),
    );
  }
}
