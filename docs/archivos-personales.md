# Archivos personales para completar el reto

Bucket privado creado en `us-east-1`:
`s3://pmdm-udit-assets-365799760841-20260930/`.

[Subir desde la consola AWS](https://s3.console.aws.amazon.com/s3/buckets/pmdm-udit-assets-365799760841-20260930?region=us-east-1&tab=objects)

Sube estos archivos a la raíz del bucket:

- `foto_perfil.jpg` o `foto_perfil.png`: foto propia, preferentemente cuadrada y menor de 512 KiB.
- `captura.png`: captura real de la tarjeta funcionando.
- `icono.png`: captura del icono y nombre de la app instalada.
- `perfil.json` (opcional): nombre, rol y URL GitHub si quieres cambiar los valores actuales.

Ejemplo de `perfil.json`:

```json
{"name": "Tu nombre", "role": "Estudiante de DAM", "github_url": "https://github.com/unknowncoder05"}
```

También puedes subir desde tu terminal autenticado en AWS:

```sh
aws s3 cp foto_perfil.jpg s3://pmdm-udit-assets-365799760841-20260930/foto_perfil.jpg --region us-east-1
aws s3 cp captura.png s3://pmdm-udit-assets-365799760841-20260930/captura.png --region us-east-1
aws s3 cp icono.png s3://pmdm-udit-assets-365799760841-20260930/icono.png --region us-east-1
```

El bucket bloquea el acceso público y utiliza cifrado SSE-S3. Los archivos se
integrarán como recursos locales después de subirlos; la app no necesita acceso
S3, claves AWS ni conexión a internet para dibujar la tarjeta.

Cuando estén subidos, indica que ya están disponibles para incorporarlos y
actualizar las capturas del README. La memoria personal de aprendizaje la
completa el alumno; el README ya describe los conceptos y decisiones técnicas.
