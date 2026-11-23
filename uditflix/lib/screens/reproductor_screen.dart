import 'package:flutter/material.dart';
import 'package:video_player/video_player.dart';
import '../models/contenido.dart';

class ReproductorScreen extends StatefulWidget {
  final Contenido contenido;
  const ReproductorScreen({super.key, required this.contenido});
  @override
  State<ReproductorScreen> createState() => _ReproductorState();
}

class _ReproductorState extends State<ReproductorScreen> {
  final video = VideoPlayerController.asset('assets/video/uditflix.mp4');
  late final Future<void> preparado;
  @override
  void initState() {
    super.initState();
    preparado = video.initialize();
  }

  @override
  void dispose() {
    video.dispose();
    super.dispose();
  }

  @override
  Widget build(BuildContext context) => Scaffold(
    backgroundColor: Colors.black,
    appBar: AppBar(title: Text(widget.contenido.titulo)),
    body: SafeArea(
      child: Center(
        child: SingleChildScrollView(
          child: ConstrainedBox(
            constraints: const BoxConstraints(maxWidth: 960),
            child: Padding(
              padding: const EdgeInsets.all(16),
              child: FutureBuilder<void>(
                future: preparado,
                builder: (context, snapshot) {
                  if (snapshot.hasError) {
                    return const Text('No se ha podido abrir el vídeo.');
                  }
                  if (snapshot.connectionState != ConnectionState.done) {
                    return const CircularProgressIndicator();
                  }
                  return ValueListenableBuilder<VideoPlayerValue>(
                    valueListenable: video,
                    builder: (context, estado, child) {
                      if (estado.hasError) {
                        return const Text(
                          'Se ha interrumpido la reproducción.',
                        );
                      }
                      return Column(
                        mainAxisSize: MainAxisSize.min,
                        crossAxisAlignment: CrossAxisAlignment.start,
                        children: [
                          AspectRatio(
                            aspectRatio: estado.aspectRatio,
                            child: VideoPlayer(video),
                          ),
                          VideoProgressIndicator(
                            video,
                            allowScrubbing: true,
                            padding: const EdgeInsets.symmetric(vertical: 12),
                          ),
                          Row(
                            children: [
                              IconButton.filled(
                                tooltip: estado.isPlaying
                                    ? 'Pausar'
                                    : 'Reproducir',
                                icon: Icon(
                                  estado.isPlaying
                                      ? Icons.pause
                                      : Icons.play_arrow,
                                ),
                                onPressed: () async {
                                  if (estado.isPlaying) {
                                    await video.pause();
                                    return;
                                  }
                                  if (estado.position >= estado.duration) {
                                    await video.seekTo(Duration.zero);
                                  }
                                  if (mounted) await video.play();
                                },
                              ),
                              const SizedBox(width: 12),
                              Text(
                                '${estado.position.inSeconds} / ${estado.duration.inSeconds} s',
                              ),
                            ],
                          ),
                          const SizedBox(height: 12),
                          const Text('UDITflix · Clip de demostración'),
                        ],
                      );
                    },
                  );
                },
              ),
            ),
          ),
        ),
      ),
    ),
  );
}
