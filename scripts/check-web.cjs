const path = require('path');
const {pathToFileURL} = require('url');
const playwrightPath = process.env.PMDM_PLAYWRIGHT_PATH || '/home/ubuntu/.local/lib/python3.8/site-packages/playwright/driver/package';
const {chromium} = require(playwrightPath);
(async () => {
  const browser = await chromium.launch({
    headless: true,
    executablePath: process.env.PMDM_CHROMIUM_PATH || '/home/ubuntu/.cache/ms-playwright/chromium-1228/chrome-linux64/chrome',
    args: ['--no-sandbox', '--headless=new'],
    ignoreDefaultArgs: ['--headless=old'],
  });
  const url = process.argv[2] || pathToFileURL(path.resolve(__dirname, '../dist/index.html')).href;
  for (const [width, height] of [[1440, 900], [390, 844], [320, 480]]) {
    const page = await browser.newPage({viewport: {width, height}});
    const errors = [];
    page.on('pageerror', error => errors.push(error.message));
    await page.goto(url);
    if (!await page.getByRole('heading', {name: 'unknowncoder05'}).isVisible()) throw Error('Profile missing');
    if (await page.getByRole('link', {name: 'Mi perfil de GitHub'}).getAttribute('href') !== 'https://github.com/unknowncoder05') throw Error('Wrong GitHub URL');
    if (!await page.locator('.avatar').evaluate(img => img.complete && img.naturalWidth > 0)) throw Error('Avatar missing');
    if (!await page.evaluate(() => document.documentElement.scrollWidth <= innerWidth)) throw Error('Horizontal overflow');
    if (errors.length) throw Error(errors.join('\n'));
    if (width === 390) await page.screenshot({path: '/tmp/pmdm-browser-card.png', fullPage: true});
    await page.close();
  }
  await browser.close();
  console.log('Browser checks passed at desktop, mobile and small-screen sizes.');
})().catch(error => { console.error(error); process.exit(1); });
