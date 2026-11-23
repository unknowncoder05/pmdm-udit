import 'package:flutter/material.dart';

class PortadaNetwork extends StatelessWidget {
  final String url;
  final int alternativa;
  final AlignmentGeometry alignment;
  const PortadaNetwork({
    super.key,
    required this.url,
    required this.alternativa,
    this.alignment = Alignment.center,
  });

  @override
  Widget build(BuildContext context) => Image.network(
    url,
    fit: BoxFit.cover,
    alignment: alignment,
    errorBuilder: (_, error, trace) =>
        Image.asset('assets/cubiertas/$alternativa.png', fit: BoxFit.cover),
  );
}
