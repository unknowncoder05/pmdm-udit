#!/usr/bin/env bash
set -euo pipefail
PROJECT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")/.." && pwd)"
NGINX_SITE="/etc/nginx/sites-enabled/sandbox.yerson.co"
python3 "$PROJECT_DIR/scripts/build-web.py"
mkdir -p "$PROJECT_DIR/deploy/backups"
backup="$PROJECT_DIR/deploy/backups/sandbox.$(date -u +%Y%m%dT%H%M%S).conf"
candidate="$PROJECT_DIR/deploy/nginx-site.candidate"
cp "$NGINX_SITE" "$backup"
python3 - "$NGINX_SITE" "$PROJECT_DIR/deploy/nginx-location.conf" "$candidate" <<'PY'
from pathlib import Path
import sys
site = Path(sys.argv[1]).read_text()
snippet = Path(sys.argv[2]).read_text().rstrip()
start = '    # BEGIN PMDM CARD'
end = '    # END PMDM CARD'
block = start + '\n' + '\n'.join('    ' + line for line in snippet.splitlines()) + '\n' + end
if start in site:
    a = site.index(start)
    b = site.index(end, a) + len(end)
    site = site[:a] + block + site[b:]
else:
    marker = '    location / {'
    if site.count(marker) != 1:
        raise SystemExit('Cannot identify the HTTPS server location; config left untouched')
    site = site.replace(marker, block + '\n\n' + marker, 1)
Path(sys.argv[3]).write_text(site)
PY
sudo install -m 644 "$candidate" "$NGINX_SITE"
if ! sudo nginx -t; then
    sudo install -m 644 "$backup" "$NGINX_SITE"
    echo 'Nginx config rejected; restored the backup.' >&2
    exit 1
fi
sudo systemctl reload nginx
python3 "$PROJECT_DIR/scripts/check-deployment.py"
