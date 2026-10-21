#!/usr/bin/env python3
"""Build a browser card from Android strings and vector/image resources."""
from pathlib import Path
import html
import mimetypes
import shutil
import xml.etree.ElementTree as ET
from urllib.parse import urlparse

ROOT = Path(__file__).resolve().parents[1]
RES = ROOT / 'reto1-tarjetapresentacion/src/main/res'
OUT = ROOT / 'dist'
OUT.mkdir(exist_ok=True)
strings = {entry.attrib['name']: entry.text or '' for entry in ET.parse(RES / 'values/strings.xml').getroot()}
url = urlparse(strings['github_url'])
if url.scheme != 'https' or url.hostname != 'github.com':
    raise SystemExit('github_url must use HTTPS and github.com')

def vector_svg(source, destination):
    vector = ET.parse(source).getroot()
    android = '{http://schemas.android.com/apk/res/android}'
    paths = ''.join(
        '<path fill="{}" d="{}"/>'.format(
            html.escape(path.attrib[android + 'fillColor'], quote=True),
            html.escape(path.attrib[android + 'pathData'], quote=True)
        ) for path in vector if path.tag == 'path'
    )
    destination.write_text('<svg xmlns="http://www.w3.org/2000/svg" viewBox="0 0 {} {}">{}</svg>'.format(
        vector.attrib[android + 'viewportWidth'], vector.attrib[android + 'viewportHeight'], paths
    ))

photo = next((RES / ('drawable/foto_perfil' + ext) for ext in ['.png', '.jpg', '.jpeg', '.webp']
              if (RES / ('drawable/foto_perfil' + ext)).exists()), None)
if photo:
    if photo.stat().st_size > 512 * 1024:
        raise SystemExit('Profile photo exceeds the 512 KiB asset limit')
    avatar_name = 'avatar' + photo.suffix
    shutil.copy2(photo, OUT / avatar_name)
else:
    avatar_name = 'avatar.svg'
    vector_svg(RES / 'drawable/foto_perfil.xml', OUT / avatar_name)
icon = RES / 'drawable/ic_launcher_personal.png'
if icon.exists():
    if icon.stat().st_size > 512 * 1024:
        raise SystemExit('Launcher icon exceeds the 512 KiB asset limit')
    icon_name = 'icon.png'
    shutil.copy2(icon, OUT / icon_name)
else:
    icon_name = 'icon.svg'
    vector_svg(RES / 'drawable/ic_launcher_foreground.xml', OUT / icon_name)
for generated in ['avatar.svg', 'avatar.png', 'avatar.jpg', 'avatar.jpeg', 'avatar.webp', 'icon.svg', 'icon.png']:
    if generated not in [avatar_name, icon_name]:
        (OUT / generated).unlink(missing_ok=True)
icon_mime = mimetypes.guess_type(icon_name)[0]
shutil.copy2(ROOT / 'web/styles.css', OUT / 'styles.css')
escape = lambda key: html.escape(strings[key], quote=True)
apk = ROOT / 'reto1-tarjetapresentacion/build/outputs/apk/debug/reto1-tarjetapresentacion-debug.apk'
apk_link = ''
if apk.exists():
    shutil.copy2(apk, OUT / 'pmdm-udit.apk')
    apk_link = '<a class="apk" href="./pmdm-udit.apk" download>Descargar Reto 1 para Android</a>'
marketplace_apk = ROOT / 'reto2-locales/build/outputs/apk/debug/reto2-locales-debug.apk'
if marketplace_apk.exists():
    shutil.copy2(marketplace_apk, OUT / 'local-burger.apk')
    apk_link += '<a class="apk" href="./local-burger.apk" download>Descargar Reto 2 · Local Burger</a>'
(OUT / 'index.html').write_text(f'''<!doctype html>
<html lang="es">
<head>
  <meta charset="utf-8">
  <meta name="viewport" content="width=device-width, initial-scale=1, viewport-fit=cover">
  <meta name="theme-color" content="#6750a4">
  <meta name="description" content="Tarjeta de presentación de {escape('profile_name')} · {escape('profile_role')}">
  <title>{escape('app_name')}</title>
  <link rel="icon" href="./{icon_name}" type="{icon_mime}">
  <link rel="stylesheet" href="./styles.css">
</head>
<body>
  <main aria-labelledby="profile-name">
    <img class="avatar" src="./{avatar_name}" alt="{escape('profile_image_description')}" width="150" height="150">
    <h1 id="profile-name">{escape('profile_name')}</h1>
    <p class="role">{escape('profile_role')}</p>
    <a class="github" href="{escape('github_url')}" target="_blank" rel="noopener noreferrer">{escape('github_button')}</a>
    <a class="apk" href="./emulador/">Abrir Local Burger en el emulador Android</a>
    {apk_link}
  </main>
</body>
</html>
''')
print('Built browser card:', OUT)
