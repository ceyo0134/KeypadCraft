/*
 * Blocks Web - an open-source browser port of "Blocks J2ME" 1.0.1.
 * Isometric 48 x 16 x 48 block builder. MIT licensed (code). See README.
 */
(function () {
  'use strict';

  // ---------- constants (taken from the original game) ----------
  var N = 48, H = 16, WATER_LEVEL = 5, BAR_H = 0;
  var AIR = 0, WATER = 1, STONE = 2, GRASS = 3, DIRT = 4, WOOD = 9, LEAVES = 10, SAND = 11, COAL = 20, IRON_ORE = 21, BEDROCK = 22;
  var TEX = ['water', 'stone', 'grass', 'dirt', 'cobblestone', 'planks', 'bricks', 'slabs', 'wood', 'leaves', 'sand',
    'bookshelf', 'tnt', 'crafting_table', 'furnace', 'jukebox', 'sponge', 'gravel', 'moss', 'coal_ore', 'iron_ore',
    'bedrock', 'iron_block', 'gold_block']; // block id = index + 1
  var SKY = '#87ceeb';

  var I18N = {
    en: {
      zoomout: '-', zoomin: '+', prev: '<', next: '>', put: 'Put', del: 'Del', help: '?',
      inv: 'Inventory', helpTitle: 'Blocks Web - Help',
      names: ['Water', 'Stone', 'Grass', 'Dirt', 'Cobblestone', 'Planks', 'Bricks', 'Slabs', 'Wood', 'Leaves', 'Sand',
        'Bookshelf', 'TNT', 'Crafting table', 'Furnace', 'Jukebox', 'Sponge', 'Gravel', 'Moss', 'Coal ore', 'Iron ore',
        'Bedrock', 'Iron block', 'Gold block'],
      helpLines: [
        ['Mouse / touch', ['Click or tap a block: <b>Put</b> places a block next to it, <b>Del</b> removes it',
          'Drag: move the map', 'Mouse wheel or - / +: zoom',
          '&lt; &gt; : change block, centre icon: open inventory', 'Put/Del: switch mode']],
        ['Keyboard', ['<kbd>Arrows</kbd> / <kbd>WASD</kbd>: move the map (red crosshair appears)',
          '<kbd>Space</kbd> / <kbd>Enter</kbd>: place or remove at the crosshair', '<kbd>-</kbd> <kbd>+</kbd>: zoom',
          '<kbd>Q</kbd> <kbd>E</kbd>: previous / next block', '<kbd>I</kbd>: inventory', '<kbd>Tab</kbd>: Put/Del mode',
          '<kbd>H</kbd>: this help']],
        ['Notes', ['Yellow frame = last block you placed', 'Bedrock cannot be removed']]
      ]
    }
  };

  // ---------- helpers ----------
  function $(id) { return document.getElementById(id); }
  function lsGet(k) { try { return localStorage.getItem(k); } catch (e) { return null; } }
  function lsSet(k, v) { try { localStorage.setItem(k, v); } catch (e) { /* ignore */ } }

  // ---------- world ----------
  var blocks = new Uint8Array(N * H * N);
  function get(x, y, z) { if (x < 0 || y < 0 || z < 0 || x >= N || y >= H || z >= N) return 0; return blocks[(y * N + x) * N + z]; }
  function set(x, y, z, id) { if (x < 0 || y < 0 || z < 0 || x >= N || y >= H || z >= N) return; blocks[(y * N + x) * N + z] = id; }
  function fill(x1, y1, z1, x2, y2, z2, id) {
    for (var y = y1; y <= y2; y++) for (var x = x1; x <= x2; x++) for (var z = z1; z <= z2; z++) set(x, y, z, id);
  }
  function randInt(a, b) { return a + Math.floor(Math.random() * (b - a + 1)); }
  function addTree(x, y, z) {
    fill(x - 2, y + 3, z - 2, x + 2, y + 4, z + 2, LEAVES);
    fill(x - 1, y + 5, z - 1, x + 1, y + 5, z + 1, LEAVES);
    set(x + 1, y + 6, z, LEAVES); set(x - 1, y + 6, z, LEAVES); set(x, y + 6, z + 1, LEAVES);
    set(x, y + 6, z - 1, LEAVES); set(x, y + 6, z, LEAVES);
    fill(x, y, z, x, y + 5, z, WOOD);
  }
  function generateNatural() {
    blocks.fill(0);
    fill(0, 0, 0, 47, 0, 47, BEDROCK);
    fill(0, 1, 0, 47, WATER_LEVEL, 47, WATER);
    var g = [], i, j, x, z, y;
    for (i = 0; i < 7; i++) { g.push([]); for (j = 0; j < 7; j++) g[i].push(randInt(3, 12)); }
    for (x = 0; x < N; x++) for (z = 0; z < N; z++) {
      var gx = Math.floor(x / 8), gz = Math.floor(z / 8), fx = x % 8, fz = z % 8;
      var h = Math.floor((g[gx][gz] * (8 - fx) * (8 - fz) + g[gx + 1][gz] * fx * (8 - fz) +
        g[gx][gz + 1] * (8 - fx) * fz + g[gx + 1][gz + 1] * fx * fz) / 64);
      if (h > 3) { fill(x, 1, z, x, h - 3, z, STONE); fill(x, h - 2, z, x, h, z, DIRT); }
      else fill(x, 1, z, x, h, z, DIRT);
      if (h >= 5) set(x, h, z, GRASS);
    }
    for (y = 4; y <= 5; y++) for (x = 0; x < N; x++) for (z = 0; z < N; z++) {
      var b = get(x, y, z);
      if (b !== GRASS && b !== DIRT) continue;
      var up = get(x, y + 1, z);
      if (up !== AIR && up !== WATER) continue;
      var near = false;
      for (var dy = -1; dy <= 1 && !near; dy++) for (var dx = -2; dx <= 2 && !near; dx++) for (var dz = -2; dz <= 2; dz++)
        if (get(x + dx, y + dy, z + dz) === WATER) { near = true; break; }
      if (near) set(x, y, z, SAND);
    }
    for (i = 0; i < 12; i++) {
      x = 2 + Math.floor(Math.random() * 43); z = 2 + Math.floor(Math.random() * 43); y = 8;
      if (get(x, 9, z) !== AIR) continue;
      while (y > 0 && get(x, y, z) === AIR) y--;
      if (get(x, y, z) !== GRASS) continue;
      addTree(x, y + 1, z); set(x, y, z, DIRT);
    }
    for (y = 1; y < H; y++) for (x = 0; x < N; x++) for (z = 0; z < N; z++) {
      if (get(x, y, z) !== STONE) continue;
      var r = randInt(0, 19);
      if (r === 0) set(x, y, z, COAL); else if (r === 1) set(x, y, z, IRON_ORE);
    }
  }

  // ---------- textures & tiles ----------
  var src = new Array(24);         // ImageData per texture
  var tiles = [];                  // canvases at current zoom
  var iconCache = {};
  var hw = 16, hh = 8, bh = 16;    // zoom state
  var ox = 0, oy = 0;

  function loadTextures() {
    var map = window.BLOCKS_TEXTURES || {};
    return Promise.all(TEX.map(function (name, i) {
      return new Promise(function (res) {
        var im = new Image();
        im.onload = function () {
          var c = document.createElement('canvas'); c.width = im.width; c.height = im.height;
          var cx = c.getContext('2d'); cx.drawImage(im, 0, 0);
          src[i] = cx.getImageData(0, 0, im.width, im.height); res();
        };
        im.onerror = function () { src[i] = null; res(); };
        im.src = map[name] || ('textures/' + name + '.png');
      });
    }));
  }
  // which face of a tile a pixel belongs to: 0 top, 1 left, 2 right (transparent pixels never reach this)
  function face(px, py, w, h) {
    var x4 = Math.floor((2 * px + 1) * 32 / w), y5 = Math.floor((2 * py + 1) * 31 / h);
    var b = x4 < 32 ? 16 + (x4 >> 1) : 48 - (x4 >> 1);
    if (y5 < b) return 0;
    return x4 < 32 ? 1 : 2;
  }
  function makeTile(img, W, Hh, water) {
    var sw = img.width, sh = img.height, sd = img.data, i, x, y;
    var a = new Uint8ClampedArray(W * Hh * 4);
    for (y = 0; y < Hh; y++) for (x = 0; x < W; x++) {
      var sy = Math.floor(y * sh / Hh), sx = Math.floor(x * sw / W), si = (sy * sw + sx) * 4, di = (y * W + x) * 4;
      a[di] = sd[si]; a[di + 1] = sd[si + 1]; a[di + 2] = sd[si + 2]; a[di + 3] = sd[si + 3];
    }
    function opaque(x, y) { return a[(y * W + x) * 4 + 3] !== 0; }
    var outline = !water && W >= 12;
    for (y = 0; y < Hh; y++) for (x = 0; x < W; x++) {
      i = (y * W + x) * 4;
      if (a[i + 3] === 0) continue;
      var f = face(x, y, W, Hh), pct;
      if (water) pct = f === 0 ? 100 : f === 1 ? 92 : 84; else pct = f === 0 ? 108 : f === 1 ? 80 : 62;
      if (outline) {
        var edge = false;
        if (x === 0 || y === 0 || x === W - 1 || y === Hh - 1) edge = true;
        else if (!opaque(x - 1, y) || !opaque(x + 1, y) || !opaque(x, y - 1) || !opaque(x, y + 1)) edge = true;
        else if (face(x + 1, y, W, Hh) !== f || face(x, y + 1, W, Hh) !== f) edge = true;
        if (edge) pct = Math.floor(pct * 50 / 100);
      }
      for (var k = 0; k < 3; k++) a[i + k] = Math.min(255, Math.floor(a[i + k] * pct / 100));
    }
    var c = document.createElement('canvas'); c.width = W; c.height = Hh;
    c.getContext('2d').putImageData(new ImageData(a, W, Hh), 0, 0);
    return c;
  }
  function buildTiles() {
    var W = hw * 2, Hh = Math.floor(31 * hw / 16);
    tiles = [];
    for (var i = 0; i < 24; i++) if (src[i]) tiles[i] = makeTile(src[i], W, Hh, i === 0);
  }
  function iconURL(id) {
    if (!iconCache[id]) {
      var s = src[id - 1];
      iconCache[id] = s ? makeTile(s, 32, 31, id === 1).toDataURL() : '';
    }
    return iconCache[id];
  }

  // ---------- state ----------
  var cur = STONE, removeMode = false, keyMode = false;
  var lpx = -1, lpy = 0, lpz = 0;      // last placed block
  var hx = 0, hy = 0, hz = 0, hface = -1;
  var lang = 'en';
  var T = I18N[lang];
  var cv = $('view'), ctx = cv.getContext('2d');
  var VW = 320, VH = 240, S = 2, mouse = null, dirty = false;

  function invOrder() { var a = [], i; for (i = 2; i <= 24; i++) a.push(i); a.push(1); return a; }

  // ---------- rendering ----------
  // Visible-face rule from the original: a face shows when the neighbour is air, or when the neighbour is water and the block is not.
  function vis(id, nb) { if (nb === 0) return true; if (nb === 1) return id !== 1; return false; }
  // scan(draw): draws the scene back to front; otherwise returns the front-most face under (px,py).
  function scan(draw, px, py) {
    hface = -1;
    var x, y, z;
    for (x = 0; x < N; x++) for (z = 0; z < N; z++) {
      var sx = ox + (x - z) * hw;
      if (draw) { if (sx < -hw || sx > VW + hw) continue; } else if (Math.abs(px - sx) > hw) continue;
      for (y = 0; y < H; y++) {
        var id = get(x, y, z); if (id === 0) continue;
        var sy = oy + (x + z) * hh - y * bh;
        if (draw) { if (sy + hh + bh < 0 || sy - hh > VH) continue; }
        else if (py < sy - hh || py > sy + hh + bh) continue;
        var vt = vis(id, get(x, y + 1, z)), vl = vis(id, get(x, y, z + 1)), vr = vis(id, get(x + 1, y, z));
        if (draw) {
          if (tiles[id - 1] && (vt || vl || vr)) ctx.drawImage(tiles[id - 1], sx - hw, sy - hh);
        } else {
          var f = -1;
          if (vt && Math.abs(px - sx) * hh + Math.abs(py - sy) * hw <= hw * hh) f = 0;
          if (vl && px >= sx - hw && px < sx) { var t = sy + Math.floor((px - (sx - hw)) * hh / hw); if (py >= t && py <= t + bh) f = 1; }
          if (vr && px >= sx && px <= sx + hw) { var t2 = sy + hh - Math.floor((px - sx) * hh / hw); if (py >= t2 && py <= t2 + bh) f = 2; }
          if (f >= 0) { hx = x; hy = y; hz = z; hface = f; }
        }
      }
    }
  }
  function poly(pts, color) {
    ctx.strokeStyle = color; ctx.lineWidth = 1; ctx.beginPath();
    ctx.moveTo(pts[0][0] + .5, pts[0][1] + .5);
    for (var i = 1; i < pts.length; i++) ctx.lineTo(pts[i][0] + .5, pts[i][1] + .5);
    ctx.closePath(); ctx.stroke();
  }
  function drawFace(x, y, z, f, color) {
    var sx = ox + (x - z) * hw, sy = oy + (x + z) * hh - y * bh;
    if (f === 0) poly([[sx, sy - hh], [sx + hw, sy], [sx, sy + hh], [sx - hw, sy]], color);
    else if (f === 1) poly([[sx - hw, sy], [sx, sy + hh], [sx, sy + hh + bh], [sx - hw, sy + bh]], color);
    else poly([[sx, sy + hh], [sx + hw, sy], [sx + hw, sy + bh], [sx, sy + hh + bh]], color);
  }
  function paint() {
    dirty = false;
    ctx.fillStyle = SKY; ctx.fillRect(0, 0, VW, VH);
    scan(true);
    if (lpx >= 0 && get(lpx, lpy, lpz) !== 0) {
      var sx = ox + (lpx - lpz) * hw, sy = oy + (lpx + lpz) * hh - lpy * bh;
      poly([[sx, sy - hh], [sx + hw, sy], [sx + hw, sy + bh], [sx, sy + hh + bh], [sx - hw, sy + bh], [sx - hw, sy]], '#ffff00');
    }
    var p = null;
    if (keyMode) p = [Math.floor(VW / 2), Math.floor(VH / 2)]; else if (mouse) p = mouse;
    if (p) {
      scan(false, p[0], p[1]);
      if (hface >= 0) drawFace(hx, hy, hz, hface, removeMode ? '#ff4a3d' : '#ffffff');
      if (keyMode) {
        ctx.strokeStyle = '#ff0000'; ctx.beginPath();
        ctx.moveTo(p[0] - 6, p[1] + .5); ctx.lineTo(p[0] + 6, p[1] + .5);
        ctx.moveTo(p[0] + .5, p[1] - 6); ctx.lineTo(p[0] + .5, p[1] + 6); ctx.stroke();
      }
    }
  }
  function redraw() { if (!dirty) { dirty = true; requestAnimationFrame(paint); } }

  // ---------- editing ----------
  function actAt(px, py, invert) {
    scan(false, px, py);
    if (hface < 0) return;
    var del = invert ? !removeMode : removeMode;
    if (del) {
      if (get(hx, hy, hz) !== BEDROCK) set(hx, hy, hz, 0);
      lpx = -1;
    } else {
      var x = hx, y = hy, z = hz;
      if (hface === 0) y++; else if (hface === 1) z++; else x++;
      if (x < N && y < H && z < N) {
        var b = get(x, y, z);
        if (b === 0 || b === 1) { set(x, y, z, cur); lpx = x; lpy = y; lpz = z; }
      }
    }
    redraw();
  }
  function nextBlock(d) { cur += d; if (cur > 24) cur = 1; if (cur < 1) cur = 24; updateBar(); }
  function zoom(d) {
    var nw = hw + d; if (nw < 4 || nw > 16) return;
    var cx = VW / 2, cy = VH / 2;
    ox = Math.round(cx + (ox - cx) * nw / hw); oy = Math.round(cy + (oy - cy) * nw / hw);
    hw = nw; hh = nw >> 1; bh = nw; buildTiles(); redraw();
  }

  // ---------- UI ----------
  function updateBar() {
    $('zoomOut').textContent = T.zoomout; $('zoomIn').textContent = T.zoomin;
    $('prev').textContent = T.prev; $('next').textContent = T.next; $('helpBtn').textContent = T.help;
    $('modeBtn').textContent = removeMode ? T.del : T.put; $('modeBtn').className = removeMode ? 'del' : '';
    $('curImg').src = iconURL(cur); $('cur').title = T.names[cur - 1];
    redraw();
  }
  function buildInv() {
    $('invTitle').textContent = T.inv;
    var g = $('invGrid'); g.textContent = '';
    invOrder().forEach(function (id) {
      var b = document.createElement('button'); b.type = 'button'; b.setAttribute('aria-pressed', id === cur ? 'true' : 'false');
      var im = document.createElement('img'); im.src = iconURL(id); im.alt = '';
      var sp = document.createElement('span'); sp.textContent = T.names[id - 1];
      b.appendChild(im); b.appendChild(sp);
      b.onclick = function () { cur = id; updateBar(); $('invDlg').close(); };
      g.appendChild(b);
    });
  }
  function buildHelp() {
    $('helpTitle').textContent = T.helpTitle;
    $('helpBody').innerHTML = T.helpLines.map(function (s) {
      return '<h3>' + s[0] + '</h3><ul>' + s[1].map(function (l) { return '<li>' + l + '</li>'; }).join('') + '</ul>';
    }).join('');
  }
  function applyLang() { updateBar(); buildInv(); buildHelp(); }

  // ---------- sizing ----------
  function resize() {
    var st = $('stage'), cw = st.clientWidth, ch = st.clientHeight;
    S = Math.max(1, Math.min(4, Math.round(Math.min(cw, ch) / 330)));
    var nw = Math.ceil(cw / S), nh = Math.ceil(ch / S);
    if (VW && ox !== 0) { ox += Math.round((nw - VW) / 2); oy += Math.round((nh - VH) / 2); }
    VW = nw; VH = nh; cv.width = VW; cv.height = VH;
    cv.style.width = VW * S + 'px'; cv.style.height = VH * S + 'px';
    ctx.imageSmoothingEnabled = false; redraw();
  }

  // ---------- input ----------
  function logical(e) {
    var r = cv.getBoundingClientRect();
    return [Math.floor((e.clientX - r.left) / S), Math.floor((e.clientY - r.top) / S)];
  }
  var drag = null;
  cv.addEventListener('contextmenu', function (e) { e.preventDefault(); });
  cv.addEventListener('pointerdown', function (e) {
    cv.setPointerCapture(e.pointerId); keyMode = false;
    drag = { x: e.clientX, y: e.clientY, moved: false, button: e.button, ox: ox, oy: oy };
  });
  cv.addEventListener('pointermove', function (e) {
    if (e.pointerType === 'mouse') { mouse = logical(e); }
    if (drag) {
      var dx = e.clientX - drag.x, dy = e.clientY - drag.y;
      if (!drag.moved && Math.abs(dx) + Math.abs(dy) > 6) drag.moved = true;
      if (drag.moved) { ox = drag.ox + Math.round(dx / S); oy = drag.oy + Math.round(dy / S); }
    }
    redraw();
  });
  cv.addEventListener('pointerup', function (e) {
    if (drag && !drag.moved) { var p = logical(e); actAt(p[0], p[1], false); }
    drag = null;
  });
  cv.addEventListener('pointercancel', function () { drag = null; });
  cv.addEventListener('pointerleave', function () { mouse = null; redraw(); });
  cv.addEventListener('wheel', function (e) { e.preventDefault(); zoom(e.deltaY < 0 ? 2 : -2); }, { passive: false });

  window.addEventListener('keydown', function (e) {
    if (document.querySelector('dialog[open]')) return;
    var k = e.key, step = 16, used = true;
    if (k === 'ArrowLeft' || k === 'a') ox += step; else if (k === 'ArrowRight' || k === 'd') ox -= step;
    else if (k === 'ArrowUp' || k === 'w') oy += step; else if (k === 'ArrowDown' || k === 's') oy -= step;
    else if (k === ' ' || k === 'Enter') actAt(Math.floor(VW / 2), Math.floor(VH / 2), false);
    else if (k === '+' || k === '=') zoom(2); else if (k === '-' || k === '_') zoom(-2);
    else if (k === 'q' || k === '[') nextBlock(-1); else if (k === 'e' || k === ']') nextBlock(1);
    else if (k === 'i' || k === '0') { buildInv(); $('invDlg').showModal(); }
    else if (k === 'Tab') { removeMode = !removeMode; updateBar(); }
    else if (k === 'h' || k === '?' || k === '#') $('helpDlg').showModal();
    else used = false;
    if (used) { e.preventDefault(); if (/^Arrow|^[wasd]$/.test(k)) keyMode = true; redraw(); }
  });

  $('zoomOut').onclick = function () { zoom(-2); };
  $('zoomIn').onclick = function () { zoom(2); };
  $('prev').onclick = function () { nextBlock(-1); };
  $('next').onclick = function () { nextBlock(1); };
  $('cur').onclick = function () { buildInv(); $('invDlg').showModal(); };
  $('modeBtn').onclick = function () { removeMode = !removeMode; updateBar(); };
  $('helpBtn').onclick = function () { $('helpDlg').showModal(); };
  Array.prototype.forEach.call(document.querySelectorAll('[data-close]'), function (b) {
    b.onclick = function () { b.closest('dialog').close(); };
  });
  window.addEventListener('resize', resize);

  // ---------- start ----------
  document.documentElement.lang = lang;
  loadTextures().then(function () {
    generateNatural();
    hw = 16; hh = 8; bh = 16; buildTiles();
    var st = $('stage'); VW = 0; resize();
    ox = Math.floor(VW / 2); oy = Math.floor((VH) / 2) - 48 * hh + 6 * bh;
    applyLang(); redraw();
  });
})();
