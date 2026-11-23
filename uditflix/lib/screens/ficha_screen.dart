import 'package:flutter/material.dart';
import '../models/contenido.dart';
import '../widgets/portada_network.dart';
import 'reproductor_screen.dart';

class FichaScreen extends StatelessWidget {
  final Contenido contenido;
  const FichaScreen({super.key, required this.contenido});

  @override
  Widget build(BuildContext context) => Scaffold(
    appBar: AppBar(title: const Text('Ficha de catálogo')),
    body: SafeArea(
      child: Align(
        alignment: Alignment.topCenter,
        child: ConstrainedBox(
          constraints: const BoxConstraints(maxWidth: 840),
          child: ListView(
            padding: const EdgeInsets.all(24),
            children: [
              ClipRRect(
                borderRadius: BorderRadius.circular(20),
                child: AspectRatio(
                  aspectRatio: 2,
                  child: PortadaNetwork(
                    url: contenido.imagenUrl,
                    alternativa: contenido.id,
                  ),
                ),
              ),
              const SizedBox(height: 28),
              Text(
                '${contenido.categoria.toUpperCase()} · ${contenido.tipo}   /   ${contenido.duracion}',
                style: TextStyle(color: Theme.of(context).colorScheme.primary),
              ),
              const SizedBox(height: 10),
              Text(
                contenido.titulo,
                style: const TextStyle(
                  fontSize: 40,
                  height: 1.05,
                  fontWeight: FontWeight.w900,
                  letterSpacing: -1.5,
                ),
              ),
              const SizedBox(height: 18),
              Text(
                contenido.descripcion,
                style: const TextStyle(fontSize: 18, height: 1.5),
              ),
              const SizedBox(height: 24),
              Align(
                alignment: Alignment.centerLeft,
                child: FilledButton.icon(
                  icon: const Icon(Icons.play_arrow),
                  label: const Text('Reproducir demo'),
                  onPressed: () => Navigator.push(
                    context,
                    MaterialPageRoute(
                      builder: (_) => ReproductorScreen(contenido: contenido),
                    ),
                  ),
                ),
              ),
            ],
          ),
        ),
      ),
    ),
  );
}
