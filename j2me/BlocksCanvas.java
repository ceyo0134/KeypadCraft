import javax.microedition.lcdui.*;
import java.util.Random;

public class BlocksCanvas extends Canvas implements CommandListener {
    // Orijinal block.h
    static final int AIR=0, WATER=1, STONE=2, GRASS=3, DIRT=4, COBBLE=5, PLANKS=6, BRICKS=7,
        SLABS=8, WOOD=9, LEAVES=10, SAND=11, BOOKS=12, TNT=13, CRAFTING=14, FURNACE=15,
        JUKEBOX=16, SPONGE=17, GRAVEL=18, MOSS=19, COAL_ORE=20, IRON_ORE=21, BEDROCK=22,
        IRON=23, GOLD=24;
    static final int[] COLOR = {
        0, 0x3050E0, 0x808080, 0x4CAF30, 0x8B5A2B, 0x6E6E6E, 0xB8864B, 0xA04030,
        0xD0D0D0, 0x5C4020, 0x2E8B2E, 0xE0D090, 0x7A5230, 0xD02020, 0x9A6A3A, 0x555555,
        0x6B3A2A, 0xDDDD44, 0x999080, 0x3F7F3F, 0x404040, 0xB09080, 0x202020, 0xDCDCDC, 0xFFD700 };

    // Orijinal world.h / worldgen.h
    static final int N = 48, H = 16, WATER_LEVEL = 5, GRID_STEP = 8, GRID_SIZE = N / GRID_STEP + 1;

    private final byte[] blocks = new byte[H * N * N];   // [y][x][z]
    private final Random rnd = new Random();
    private final BlocksMIDlet midlet;
    private int cur = STONE;

    // ---- Dil / yazi dosyasi (lang.txt) ----
    java.util.Hashtable lang = new java.util.Hashtable();
    String lZoomOut = "-", lZoomIn = "+", lPrev = "<", lNext = ">", lPut = "Put", lDel = "Del", lHelp = "?";
    String[] helpText;

    void loadLang() {
        try {
            java.io.InputStream in = getClass().getResourceAsStream("/lang.txt");
            if (in == null) return;
            java.io.ByteArrayOutputStream bo = new java.io.ByteArrayOutputStream();
            byte[] buf = new byte[512];
            int n;
            while ((n = in.read(buf)) > 0) bo.write(buf, 0, n);
            in.close();
            String txt;
            try { txt = new String(bo.toByteArray(), "UTF-8"); } catch (Exception e) { txt = new String(bo.toByteArray()); }
            if (txt.length() > 0 && txt.charAt(0) == '\uFEFF') txt = txt.substring(1);
            int p = 0, len = txt.length();
            while (p < len) {
                int e = txt.indexOf('\n', p);
                if (e < 0) e = len;
                String line = txt.substring(p, e);
                p = e + 1;
                if (line.length() > 0 && line.charAt(line.length() - 1) == '\r') line = line.substring(0, line.length() - 1);
                if (line.length() == 0 || line.charAt(0) == '#') continue;
                int eq = line.indexOf('=');
                if (eq <= 0) continue;
                lang.put(line.substring(0, eq).trim(), line.substring(eq + 1));
            }
        } catch (Exception e) { }
    }
    String t(String k, String d) { Object v = lang.get(k); return v == null ? d : (String) v; }

    void applyLang() {
        lZoomOut = t("btn.zoomout", lZoomOut); lZoomIn = t("btn.zoomin", lZoomIn);
        lPrev = t("btn.prev", lPrev); lNext = t("btn.next", lNext);
        lPut = t("btn.put", lPut); lDel = t("btn.del", lDel); lHelp = t("btn.help", lHelp);
        java.util.Vector v = new java.util.Vector();
        for (int i = 1; i < 200; i++) {
            Object o = lang.get("help." + i);
            if (o == null) break;
            v.addElement(o);
        }
        if (v.size() > 0) {
            helpText = new String[v.size()];
            v.copyInto(helpText);
        } else helpText = HELP;
    }

    public BlocksCanvas(BlocksMIDlet m) {
        midlet = m;
        setFullScreenMode(true);
        addCommand(new Command("Cikis", Command.EXIT, 1));
        setCommandListener(this);
        for (int i = 0; i < 24; i++) {
            try { src[i] = Image.createImage("/textures/" + TEX[i] + ".png"); } catch (Exception e) { src[i] = null; }
        }
        loadLang();
        applyLang();
        generateNatural();
    }

    // textures/*.png dosyalarini mevcut zoom'a gore olcekler (en yakin komsu). Dosya boyutu 32x31 olmak zorunda degil.
    void buildTiles() {
        tiles = null;
        int tw = hw * 2, th = 31 * hw / 16;
        Image[] t = new Image[24];
        int[] dst = new int[tw * th];
        for (int b = 0; b < 24; b++) {
            Image im = src[b];
            if (im == null) continue;                  // dosya yoksa bu blok duz renk cizilir
            int sw = im.getWidth(), sh = im.getHeight();
            int[] sp = new int[sw * sh];
            im.getRGB(sp, 0, sw, 0, 0, sw, sh);
            for (int y = 0; y < th; y++) {
                int sy = y * sh / th;
                for (int x = 0; x < tw; x++) dst[y * tw + x] = sp[sy * sw + x * sw / tw];
            }
            t[b] = Image.createRGBImage(dst, tw, th, true);
        }
        tiles = t;
    }

    public void commandAction(Command c, Displayable d) { midlet.quit(); }

    // ---------------- Dunya ----------------
    int get(int x, int y, int z) {
        if (x < 0 || y < 0 || z < 0 || x >= N || y >= H || z >= N) return AIR;
        return blocks[(y * N + x) * N + z];
    }
    void set(int x, int y, int z, int b) {
        if (x < 0 || y < 0 || z < 0 || x >= N || y >= H || z >= N) return;
        blocks[(y * N + x) * N + z] = (byte) b;
    }
    void fill(int x0, int y0, int z0, int x1, int y1, int z1, int b) {
        for (int y = y0; y <= y1; y++)
            for (int x = x0; x <= x1; x++)
                for (int z = z0; z <= z1; z++) set(x, y, z, b);
    }
    int randInt(int a, int b) { return a + (rnd.nextInt() >>> 1) % (b - a + 1); }

    void addTree(int tx, int ty, int tz) {
        fill(tx - 2, ty + 3, tz - 2, tx + 2, ty + 4, tz + 2, LEAVES);
        fill(tx - 1, ty + 5, tz - 1, tx + 1, ty + 5, tz + 1, LEAVES);
        set(tx + 1, ty + 6, tz, LEAVES); set(tx - 1, ty + 6, tz, LEAVES);
        set(tx, ty + 6, tz + 1, LEAVES); set(tx, ty + 6, tz - 1, LEAVES);
        set(tx, ty + 6, tz, LEAVES);
        fill(tx, ty, tz, tx, ty + 5, tz, WOOD);
    }

    // Orijinal generate_natural()
    void generateNatural() {
        fill(0, 0, 0, N - 1, 0, N - 1, BEDROCK);
        fill(0, 1, 0, N - 1, WATER_LEVEL, N - 1, WATER);
        int[][] grid = new int[GRID_SIZE][GRID_SIZE];
        for (int x = 0; x < GRID_SIZE; x++)
            for (int z = 0; z < GRID_SIZE; z++) grid[x][z] = randInt(3, 12);
        for (int x = 0; x < N; x++) {
            for (int z = 0; z < N; z++) {
                int gx = x / GRID_STEP, gz = z / GRID_STEP;
                int lx = x - gx * GRID_STEP, lz = z - gz * GRID_STEP;
                int h = grid[gx][gz] * (GRID_STEP - lx) * (GRID_STEP - lz)
                      + grid[gx + 1][gz] * lx * (GRID_STEP - lz)
                      + grid[gx][gz + 1] * (GRID_STEP - lx) * lz
                      + grid[gx + 1][gz + 1] * lx * lz;
                h /= GRID_STEP * GRID_STEP;
                if (h > 3) {
                    fill(x, 1, z, x, h - 3, z, STONE);
                    fill(x, h - 2, z, x, h, z, DIRT);
                } else {
                    fill(x, 1, z, x, h, z, DIRT);
                }
                if (h >= WATER_LEVEL) fill(x, h, z, x, h, z, GRASS);
            }
        }
        // Suya yakin toprak -> kum
        for (int y = WATER_LEVEL - 1; y <= WATER_LEVEL; y++)
            for (int x = 0; x < N; x++)
                for (int z = 0; z < N; z++) {
                    int b = get(x, y, z);
                    if (b != GRASS && b != DIRT) continue;
                    int up = get(x, y + 1, z);
                    if (up != AIR && up != WATER) continue;
                    boolean near = false;
                    for (int by = -1; by <= 1 && !near; by++)
                        for (int bx = -2; bx <= 2 && !near; bx++)
                            for (int bz = -2; bz <= 2; bz++)
                                if (get(x + bx, y + by, z + bz) == WATER) { near = true; break; }
                    if (near) set(x, y, z, SAND);
                }
        // Agaclar
        for (int i = 0; i < 12; i++) {
            int x = 2 + (rnd.nextInt() >>> 1) % (N - 1 - 4);
            int z = 2 + (rnd.nextInt() >>> 1) % (N - 1 - 4);
            int y = H - 8;
            if (get(x, y + 1, z) != AIR) continue;
            while (y > 0 && get(x, y, z) == AIR) y--;
            if (get(x, y, z) != GRASS) continue;
            addTree(x, y + 1, z);
            set(x, y, z, DIRT);
        }
        // Komur / demir
        for (int y = 1; y < H; y++)
            for (int x = 0; x < N; x++)
                for (int z = 0; z < N; z++) {
                    if (get(x, y, z) != STONE) continue;
                    int r = randInt(0, 19);
                    if (r == 0) set(x, y, z, COAL_ORE);
                    else if (r == 1) set(x, y, z, IRON_ORE);
                }
    }

    // ---------------- Cizim ----------------
    static final int BAR = 30;                 // alt arac cubugu yuksekligi
    int hw = 16, hh = 8, bh = 16;              // yakinlastirma (16 = orijinal doku boyutu)
    Image[] src = new Image[24];               // textures/<name>.png dosyalari (block id 1..24)
    static final String[] TEX = { "water", "stone", "grass", "dirt", "cobblestone", "planks", "bricks", "slabs",
        "wood", "leaves", "sand", "bookshelf", "tnt", "crafting_table", "furnace", "jukebox", "sponge", "gravel",
        "moss", "coal_ore", "iron_ore", "bedrock", "iron_block", "gold_block" };
    Image[] tiles;                             // yakinlastirmaya gore olceklenmis bloklar
    int ox, oy;                                // kaydirma
    boolean inited, removeMode, keyMode, help = true;
    int helpPage;
    int downX, downY, lastX, lastY;
    boolean dragged;
    int hx, hy, hz, hface;                     // dokunulan yuz

    static int shade(int c, int pct) {
        int r = ((c >> 16) & 255) * pct / 100, g = ((c >> 8) & 255) * pct / 100, b = (c & 255) * pct / 100;
        return (r << 16) | (g << 8) | b;
    }
    boolean vis(int b, int nb) {
        if (nb == AIR) return true;
        if (nb == WATER) return b != WATER;
        return false;
    }

    // g != null: tum dunyayi ciz (mesafe siniri yok). g == null: (tx,ty) noktasindaki en onde yuzu bul.
    void scan(Graphics g, int tx, int ty) {
        int w = getWidth(), h = getHeight();
        hface = -1;
        for (int x = 0; x < N; x++) {
            for (int z = 0; z < N; z++) {
                int sx = ox + (x - z) * hw;
                if (g != null) { if (sx < -hw || sx > w + hw) continue; }
                else if (Math.abs(tx - sx) > hw) continue;
                for (int y = 0; y < H; y++) {
                    int b = get(x, y, z);
                    if (b == AIR) continue;
                    int sy = oy + (x + z) * hh - y * bh;
                    if (g != null) { if (sy + hh + bh < 0 || sy - hh > h) continue; }
                    else if (ty < sy - hh || ty > sy + hh + bh) continue;
                    boolean top = vis(b, get(x, y + 1, z));
                    boolean left = vis(b, get(x, y, z + 1));
                    boolean right = vis(b, get(x + 1, y, z));
                    if (g != null) {
                        if (tiles != null && tiles[b - 1] != null) {
                            if (top || left || right) g.drawImage(tiles[b - 1], sx - hw, sy - hh, Graphics.TOP | Graphics.LEFT);
                            continue;
                        }
                        int c = COLOR[b];
                        if (top) {
                            g.setColor(c);
                            g.fillTriangle(sx, sy - hh, sx + hw, sy, sx, sy + hh);
                            g.fillTriangle(sx, sy - hh, sx - hw, sy, sx, sy + hh);
                        }
                        if (left) {
                            g.setColor(shade(c, 75));
                            g.fillTriangle(sx - hw, sy, sx, sy + hh, sx, sy + hh + bh);
                            g.fillTriangle(sx - hw, sy, sx - hw, sy + bh, sx, sy + hh + bh);
                        }
                        if (right) {
                            g.setColor(shade(c, 55));
                            g.fillTriangle(sx, sy + hh, sx + hw, sy, sx + hw, sy + bh);
                            g.fillTriangle(sx, sy + hh, sx, sy + hh + bh, sx + hw, sy + bh);
                        }
                    } else {
                        int f = -1;
                        if (top && Math.abs(tx - sx) * hh + Math.abs(ty - sy) * hw <= hw * hh) f = 0;
                        if (left && tx >= sx - hw && tx < sx) {
                            int t = sy + (tx - (sx - hw)) * hh / hw;
                            if (ty >= t && ty <= t + bh) f = 1;
                        }
                        if (right && tx >= sx && tx <= sx + hw) {
                            int t = sy + hh - (tx - sx) * hh / hw;
                            if (ty >= t && ty <= t + bh) f = 2;
                        }
                        if (f >= 0) { hx = x; hy = y; hz = z; hface = f; }   // sonuncu = en onde
                    }
                }
            }
        }
    }

    protected void paint(Graphics g) {
        int w = getWidth(), h = getHeight();
        if (!inited) { inited = true; buildTiles(); ox = w / 2; oy = (h - BAR) / 2 - N * hh + 6 * bh; }
        g.setColor(0x87CEEB);
        g.fillRect(0, 0, w, h);
        scan(g, 0, 0);
        // Tuslu telefon: ortadaki hedef isareti
        if (keyMode && !help) {
            int cx = w / 2, cy = (h - BAR) / 2;
            g.setColor(0xFF0000);
            g.drawLine(cx - 6, cy, cx + 6, cy);
            g.drawLine(cx, cy - 6, cx, cy + 6);
        }
        // Arac cubugu (7 hucre)
        g.setColor(0x202020);
        g.fillRect(0, h - BAR, w, BAR);
        g.setColor(0xFFFFFF);
        int cw = w / 7, ty = h - BAR + (BAR - 12) / 2;
        String[] lab = { lZoomOut, lZoomIn, lPrev, "", lNext, removeMode ? lDel : lPut, lHelp };
        for (int i = 0; i < 7; i++) {
            g.drawRect(i * cw + 1, h - BAR + 1, cw - 2, BAR - 2);
            if (lab[i].length() > 0) g.drawString(lab[i], i * cw + cw / 2, ty, Graphics.TOP | Graphics.HCENTER);
        }
        g.setColor(COLOR[cur]);
        g.fillRect(3 * cw + cw / 2 - 7, h - BAR + (BAR - 14) / 2, 14, 14);
        if (help) paintHelp(g, w, h);
    }

    // ---------------- Ogretici ----------------
    static final String[] HELP = {
        "BLOCKS J2ME - HELP",
        "",
        "TOUCHSCREEN:",
        "Tap a block: place next",
        "  to it",
        "Drag: move the map",
        "- + : zoom out / in",
        "< > : change block",
        "Put/Del: switch mode",
        "? : open this help",
        "",
        "KEYPAD PHONE:",
        "D-pad / 2 4 6 8:",
        "  move the map",
        "5 or OK: place/remove",
        "  at the red crosshair",
        "1: zoom out  3: zoom in",
        "7: prev  9: next block",
        "*: Put/Del mode",
        "0: next block",
        "#: open/close help",
        "",
        "Next: press any key/tap"
    };

    void paintHelp(Graphics g, int w, int h) {
        Font f = Font.getFont(Font.FACE_SYSTEM, Font.STYLE_PLAIN, Font.SIZE_SMALL);
        int fh = 13, per = (h - 12) / fh;
        if (per < 4) per = 4;
        g.setColor(0x101820);
        g.fillRect(2, 2, w - 4, h - 4);
        g.setColor(0xFFFFFF);
        g.drawRect(2, 2, w - 5, h - 5);
        g.setFont(f);
        int start = helpPage * per;
        for (int i = 0; i < per && start + i < helpText.length; i++)
            g.drawString(helpText[start + i], 8, 6 + i * fh, Graphics.TOP | Graphics.LEFT);
    }

    // Yardimi bir sayfa ilerlet, bitince kapat
    void nextHelp() {
        int fh = 13, per = (getHeight() - 12) / fh;
        if (per < 4) per = 4;
        helpPage++;
        if (helpPage * per >= helpText.length) { help = false; helpPage = 0; }
        repaint();
    }
    void openHelp() { help = true; helpPage = 0; repaint(); }

    // ---------------- Dokunmatik ----------------
    protected void pointerPressed(int x, int y) {
        keyMode = false;
        downX = lastX = x; downY = lastY = y; dragged = false;
    }

    protected void pointerDragged(int x, int y) {
        if (help || downY >= getHeight() - BAR) return;
        if (!dragged && Math.abs(x - downX) + Math.abs(y - downY) < 6) return;
        dragged = true;
        ox += x - lastX; oy += y - lastY;
        lastX = x; lastY = y;
        repaint();
    }

    protected void pointerReleased(int x, int y) {
        if (help) { nextHelp(); return; }
        if (dragged) return;
        int w = getWidth(), h = getHeight();
        if (y >= h - BAR) {
            int cell = x * 7 / w;
            if (cell == 0) zoom(-2);
            else if (cell == 1) zoom(2);
            else if (cell == 2) nextBlock(-1);
            else if (cell == 3 || cell == 4) nextBlock(1);
            else if (cell == 5) removeMode = !removeMode;
            else { openHelp(); return; }
            repaint();
            return;
        }
        tapAt(x, y);
    }

    // (x,y) noktasindaki yuze koy / kir
    void tapAt(int x, int y) {
        scan(null, x, y);
        if (hface < 0) return;
        if (removeMode) {
            if (get(hx, hy, hz) != BEDROCK) set(hx, hy, hz, AIR);
        } else {
            int nx = hx, ny = hy, nz = hz;
            if (hface == 0) ny++; else if (hface == 1) nz++; else nx++;
            if (nx < N && ny < H && nz < N) {
                int t = get(nx, ny, nz);
                if (t == AIR || t == WATER) set(nx, ny, nz, cur);
            }
        }
        repaint();
    }

    void nextBlock(int d) {
        cur += d;
        if (cur > GOLD) cur = WATER;
        if (cur < WATER) cur = GOLD;
    }

    void zoom(int d) {
        int nhw = hw + d;
        if (nhw < 4 || nhw > 16) return;
        int cx = getWidth() / 2, cy = (getHeight() - BAR) / 2;
        ox = cx + (ox - cx) * nhw / hw;
        oy = cy + (oy - cy) * nhw / hw;
        hw = nhw; hh = nhw / 2; bh = nhw;
        buildTiles();
    }

    // ---------------- Tuslu telefon ----------------
    protected void keyPressed(int k) {
        if (help) { nextHelp(); return; }
        keyMode = true;
        int step = 8;
        int a = getGameAction(k);
        if (a == UP || k == KEY_NUM2) oy += step;
        else if (a == DOWN || k == KEY_NUM8) oy -= step;
        else if (a == LEFT || k == KEY_NUM4) ox += step;
        else if (a == RIGHT || k == KEY_NUM6) ox -= step;
        else if (a == FIRE || k == KEY_NUM5) { tapAt(getWidth() / 2, (getHeight() - BAR) / 2); return; }
        else if (k == KEY_NUM1) zoom(-2);
        else if (k == KEY_NUM3) zoom(2);
        else if (k == KEY_NUM7) nextBlock(-1);
        else if (k == KEY_NUM9 || k == KEY_NUM0) nextBlock(1);
        else if (k == KEY_STAR) removeMode = !removeMode;
        else if (k == KEY_POUND) { openHelp(); return; }
        else return;
        repaint();
    }
    protected void keyRepeated(int k) {
        // Sadece kaydirma tuslari tekrar eder (yanlislikla coklu koy/kir olmasin)
        int a = getGameAction(k);
        if (!help && (a == UP || a == DOWN || a == LEFT || a == RIGHT
            || k == KEY_NUM2 || k == KEY_NUM4 || k == KEY_NUM6 || k == KEY_NUM8)) keyPressed(k);
    }
}
