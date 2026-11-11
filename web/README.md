# Blocks Web

Open-source browser port of **Blocks J2ME 1.0.1** (by Ceyo): an isometric block builder
on a 48 x 16 x 48 world with 24 block types. Runs offline, no dependencies.

## Run
- Open `dist/blocks.html` (single self-contained file), or
- open `src/index.html`, or serve the folder: `python3 -m http.server`.

## Controls
Click/tap = Put or Del (mode button) - drag = pan - wheel = zoom -
`< >` or Q/E = block - `I` = inventory - Tab = mode - arrows/WASD + Space = keyboard play - `H` = help.

## Build
`python3 tools/build.py` regenerates `src/textures.js` and `dist/blocks.html`.

## What was ported
World generation (bilinear height grid, water level 5, beaches, trees, coal/iron ore),
the painter's-algorithm isometric renderer, tile shading and outlines, hit testing, zoom (4-16),
placement rules (only into air/water, bedrock is unbreakable), inventory order.

## License
MIT (see LICENSE).
