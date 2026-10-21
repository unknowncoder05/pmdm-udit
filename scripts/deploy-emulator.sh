#!/usr/bin/env bash
set -euo pipefail
PROJECT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")/.." && pwd)"
NGINX_SITE=/etc/nginx/sites-enabled/sandbox.yerson.co
test -f /etc/nginx/pmdm-emulator.htpasswd
systemctl is-active --quiet pmdm-emulator
curl -fsS http://127.0.0.1:6084/vnc.html -o /dev/null
mkdir -p "$PROJECT_DIR/deploy/backups"
backup="$PROJECT_DIR/deploy/backups/emulator.$(date -u +%Y%m%dT%H%M%S).conf"
candidate="$PROJECT_DIR/deploy/nginx-emulator.candidate"
cp "$NGINX_SITE" "$backup"
python3 - "$NGINX_SITE" "$PROJECT_DIR/deploy/nginx-emulator-location.conf" "$candidate" <<'PY'
from pathlib import Path
import sys
site = Path(sys.argv[1]).read_text()
snippet = Path(sys.argv[2]).read_text().rstrip()
start, end = '    # BEGIN PMDM EMULATOR', '    # END PMDM EMULATOR'
block = start + '\n' + '\n'.join('    ' + line for line in snippet.splitlines()) + '\n' + end
if start in site:
    a = site.index(start)
    b = site.index(end, a) + len(end)
    site = site[:a] + block + site[b:]
else:
    marker = '    # BEGIN PMDM CARD'
    if site.count(marker) != 1:
        raise SystemExit('No se ha localizado el bloque PMDM; Nginx no se modifica.')
    site = site.replace(marker, block + '\n\n' + marker, 1)
Path(sys.argv[3]).write_text(site)
PY
sudo install -m 644 "$candidate" "$NGINX_SITE"
if ! sudo nginx -t; then
    sudo install -m 644 "$backup" "$NGINX_SITE"
    exit 1
fi
if ! sudo systemctl reload nginx; then
    sudo install -m 644 "$backup" "$NGINX_SITE"
    sudo systemctl reload nginx
    exit 1
fi
echo 'Emulador publicado en https://sandbox.yerson.co/pmdm/emulador/'
