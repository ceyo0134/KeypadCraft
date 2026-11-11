#!/usr/bin/env python3
"""Build: textures.js (base64 textures) and dist/blocks.html (one self-contained file)."""
import base64, os, re, json
root = os.path.dirname(os.path.dirname(os.path.abspath(__file__)))
tex = {}
for f in sorted(os.listdir(os.path.join(root, 'textures'))):
    if f.endswith('.png'):
        tex[f[:-4]] = 'data:image/png;base64,' + base64.b64encode(open(os.path.join(root, 'textures', f), 'rb').read()).decode()
tjs = 'window.BLOCKS_TEXTURES=' + json.dumps(tex) + ';\n'
open(os.path.join(root, 'src', 'textures.js'), 'w').write(tjs)
css = open(os.path.join(root, 'src', 'style.css')).read()
js = open(os.path.join(root, 'src', 'app.js'), encoding='utf-8').read()
html = open(os.path.join(root, 'src', 'index.html'), encoding='utf-8').read()
html = html.replace('<link rel="stylesheet" href="style.css">', '<style>\n' + css + '</style>')
html = html.replace('<script src="textures.js"></script>', '<script>' + tjs + '</script>')
html = html.replace('<script src="app.js"></script>', '<script>\n' + js + '</script>')
os.makedirs(os.path.join(root, 'dist'), exist_ok=True)
open(os.path.join(root, 'dist', 'blocks.html'), 'w', encoding='utf-8').write(html)
print('ok', len(html))
