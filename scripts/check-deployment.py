#!/usr/bin/env python3
"""Check the real redirect, card identity and static resources over HTTPS."""
from pathlib import Path
from html.parser import HTMLParser
import hashlib
import mimetypes
import urllib.request
import urllib.error
import time
import xml.etree.ElementTree as ET

BASE = 'https://sandbox.yerson.co'
ROOT = Path(__file__).resolve().parents[1]
strings = {entry.attrib['name']: entry.text or '' for entry in ET.parse(
    ROOT / 'reto1-tarjetapresentacion/src/main/res/values/strings.xml').getroot()}

class NoRedirect(urllib.request.HTTPRedirectHandler):
    def redirect_request(self, req, fp, code, msg, headers, newurl):
        return None

class CardAssets(HTMLParser):
    def __init__(self):
        super().__init__()
        self.assets = []

    def handle_starttag(self, tag, attrs):
        attrs = dict(attrs)
        if tag == 'img' and 'avatar' in attrs.get('class', '').split():
            self.assets.append(attrs['src'])
        if tag == 'link' and attrs.get('rel') == 'icon':
            self.assets.append(attrs['href'])

# Nginx reload is graceful: a request can briefly reach an old worker.
for attempt in range(10):
    try:
        urllib.request.build_opener(NoRedirect).open(BASE + '/pmdm', timeout=15)
        error = 'Expected /pmdm to return 308, but it returned a success response'
    except urllib.error.HTTPError as response:
        if response.code == 308 and response.headers.get('Location') == BASE + '/pmdm/':
            break
        error = 'Incorrect redirect: {} {}'.format(response.code, response.headers.get('Location'))
    except urllib.error.URLError as response:
        error = 'Redirect check failed: {}'.format(response.reason)
    if attempt == 9:
        raise SystemExit(error)
    time.sleep(0.5)

with urllib.request.urlopen(BASE + '/pmdm/', timeout=15) as response:
    body = response.read().decode()
    assert response.status == 200
    assert '<title>' + strings['app_name'] + '</title>' in body, 'Default app returned instead of PMDM card'
    assert strings['github_url'] in body
    assert 'class="avatar"' in body

card = CardAssets()
card.feed(body)
assert len(card.assets) == 2, 'Expected profile photo and launcher icon'
for reference in ['./styles.css'] + card.assets:
    assert reference.startswith('./') and '/' not in reference[2:], 'Unexpected asset path'
    asset = reference[2:]
    mime = mimetypes.guess_type(asset)[0]
    with urllib.request.urlopen(BASE + '/pmdm/' + asset, timeout=15) as response:
        assert response.status == 200 and response.headers.get_content_type() == mime, asset
        content = response.read()
        assert content, asset + ' is empty'
        assert hashlib.sha256(content).digest() == hashlib.sha256(
            (ROOT / 'dist' / asset).read_bytes()).digest(), asset + ' differs from local build'
print('Verified /pmdm redirect, PMDM card identity, GitHub link and exact deployed assets.')
