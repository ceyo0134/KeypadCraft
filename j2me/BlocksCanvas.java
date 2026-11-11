/*
 * KeypadCraft - Blocks J2ME 1.0.1
 * Copyright (c) 2026 Ceyo. See COPYRIGHT.txt / LICENSE in this repository.
 *
 * NOTE: this source was DECOMPILED (CFR 0.152) from the released Blocks 1.0.1 jar,
 * because the original .java files of 1.0.1 were not available. Comments and some
 * local variable names are lost; behaviour matches the jar. The original 1.0 source
 * is in the git history (tag v1.0).
 */
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.util.Hashtable;
import java.util.Random;
import java.util.Vector;
import javax.microedition.lcdui.Canvas;
import javax.microedition.lcdui.Command;
import javax.microedition.lcdui.CommandListener;
import javax.microedition.lcdui.Displayable;
import javax.microedition.lcdui.Font;
import javax.microedition.lcdui.Graphics;
import javax.microedition.lcdui.Image;

public class BlocksCanvas
extends Canvas
implements CommandListener {
    static final int AIR = 0;
    static final int WATER = 1;
    static final int STONE = 2;
    static final int GRASS = 3;
    static final int DIRT = 4;
    static final int COBBLE = 5;
    static final int PLANKS = 6;
    static final int BRICKS = 7;
    static final int SLABS = 8;
    static final int WOOD = 9;
    static final int LEAVES = 10;
    static final int SAND = 11;
    static final int BOOKS = 12;
    static final int TNT = 13;
    static final int CRAFTING = 14;
    static final int FURNACE = 15;
    static final int JUKEBOX = 16;
    static final int SPONGE = 17;
    static final int GRAVEL = 18;
    static final int MOSS = 19;
    static final int COAL_ORE = 20;
    static final int IRON_ORE = 21;
    static final int BEDROCK = 22;
    static final int IRON = 23;
    static final int GOLD = 24;
    static final int[] COLOR = new int[]{0, 3166432, 0x808080, 5025584, 9132587, 0x6E6E6E, 12093003, 10502192, 0xD0D0D0, 6045728, 3050286, 14733456, 8016432, 0xD02020, 10119738, 0x555555, 7027242, 0xDDDD44, 0x999080, 0x3F7F3F, 0x404040, 11571328, 0x202020, 0xDCDCDC, 16766720};
    static final int N = 48;
    static final int H = 16;
    static final int WATER_LEVEL = 5;
    static final int GRID_STEP = 8;
    static final int GRID_SIZE = 7;
    private final byte[] blocks = new byte[36864];
    private final Random rnd = new Random();
    private final BlocksMIDlet midlet;
    private int cur = 2;
    Hashtable lang = new Hashtable();
    String lZoomOut = "-";
    String lZoomIn = "+";
    String lPrev = "<";
    String lNext = ">";
    String lPut = "Put";
    String lDel = "Del";
    String lHelp = "?";
    String[] helpText;
    static final int BAR = 30;
    int hw = 16;
    int hh = 8;
    int bh = 16;
    Image[] src = new Image[24];
    static final String[] TEX = new String[]{"water", "stone", "grass", "dirt", "cobblestone", "planks", "bricks", "slabs", "wood", "leaves", "sand", "bookshelf", "tnt", "crafting_table", "furnace", "jukebox", "sponge", "gravel", "moss", "coal_ore", "iron_ore", "bedrock", "iron_block", "gold_block"};
    Image icon;
    int iconFor = -1;
    int iconW = 0;
    Image[] tiles;
    int ox;
    int oy;
    boolean inited;
    boolean removeMode;
    boolean keyMode;
    boolean help = true;
    int helpPage;
    int downX;
    int downY;
    int lastX;
    int lastY;
    boolean dragged;
    int hx;
    int hy;
    int hz;
    int hface;
    int lpx = -1;
    int lpy;
    int lpz;
    boolean inv;
    int invSel;
    int invScroll;
    int invCols;
    int invCellW;
    int invCellH;
    int invIw;
    int invTop;
    int invRows;
    int invX0;
    Image[] invIcons;
    int invIconW = -1;
    static final String[] HELP = new String[]{"BLOCKS J2ME - HELP", "", "TOUCHSCREEN:", "Tap a block: place next", "  to it", "Drag: move the map", "- + : zoom out / in", "< > : change block", "Icon: open inventory", "Put/Del: switch mode", "? : open this help", "", "KEYPAD PHONE:", "D-pad / 2 4 6 8:", "  move the map", "5 or OK: place/remove", "  at the red crosshair", "1: zoom out  3: zoom in", "7: prev  9: next block", "*: Put/Del mode", "0: inventory (pick block)", "#: open/close help", "", "Next: press any key/tap"};

    void loadLang() {
        try {
            String string;
            int n;
            InputStream inputStream = ((Object)((Object)this)).getClass().getResourceAsStream("/lang.txt");
            if (inputStream == null) {
                return;
            }
            ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
            byte[] byArray = new byte[512];
            while ((n = inputStream.read(byArray)) > 0) {
                byteArrayOutputStream.write(byArray, 0, n);
            }
            inputStream.close();
            try {
                string = new String(byteArrayOutputStream.toByteArray(), "UTF-8");
            }
            catch (Exception exception) {
                string = new String(byteArrayOutputStream.toByteArray());
            }
            if (string.length() > 0 && string.charAt(0) == '\ufeff') {
                string = string.substring(1);
            }
            int n2 = 0;
            int n3 = string.length();
            while (n2 < n3) {
                int n4;
                int n5 = string.indexOf(10, n2);
                if (n5 < 0) {
                    n5 = n3;
                }
                String string2 = string.substring(n2, n5);
                n2 = n5 + 1;
                if (string2.length() > 0 && string2.charAt(string2.length() - 1) == '\r') {
                    string2 = string2.substring(0, string2.length() - 1);
                }
                if (string2.length() == 0 || string2.charAt(0) == '#' || (n4 = string2.indexOf(61)) <= 0) continue;
                this.lang.put(string2.substring(0, n4).trim(), string2.substring(n4 + 1));
            }
        }
        catch (Exception exception) {
            // empty catch block
        }
    }

    String t(String string, String string2) {
        Object v = this.lang.get(string);
        return v == null ? string2 : (String)v;
    }

    void applyLang() {
        Object v;
        this.lZoomOut = this.t("btn.zoomout", this.lZoomOut);
        this.lZoomIn = this.t("btn.zoomin", this.lZoomIn);
        this.lPrev = this.t("btn.prev", this.lPrev);
        this.lNext = this.t("btn.next", this.lNext);
        this.lPut = this.t("btn.put", this.lPut);
        this.lDel = this.t("btn.del", this.lDel);
        this.lHelp = this.t("btn.help", this.lHelp);
        Vector vector = new Vector();
        for (int i = 1; i < 200 && (v = this.lang.get("help." + i)) != null; ++i) {
            vector.addElement(v);
        }
        if (vector.size() > 0) {
            this.helpText = new String[vector.size()];
            vector.copyInto(this.helpText);
        } else {
            this.helpText = HELP;
        }
    }

    public BlocksCanvas(BlocksMIDlet blocksMIDlet) {
        this.midlet = blocksMIDlet;
        this.setFullScreenMode(true);
        this.addCommand(new Command("Cikis", 7, 1));
        this.setCommandListener(this);
        for (int i = 0; i < 24; ++i) {
            try {
                this.src[i] = Image.createImage((String)("/textures/" + TEX[i] + ".png"));
                continue;
            }
            catch (Exception exception) {
                this.src[i] = null;
            }
        }
        this.loadLang();
        this.applyLang();
        this.generateNatural();
    }

    static int face(int n, int n2, int n3, int n4) {
        int n5;
        int n6 = (2 * n + 1) * 32 / n3;
        int n7 = (2 * n2 + 1) * 31 / n4;
        int n8 = n5 = n6 < 32 ? 16 + n6 / 2 : 48 - n6 / 2;
        if (n7 < n5) {
            return 0;
        }
        return n6 < 32 ? 1 : 2;
    }

    Image makeTile(Image image, int n, int n2, boolean bl) {
        int n3;
        int n4;
        int n5;
        int n6 = image.getWidth();
        int n7 = image.getHeight();
        int[] nArray = new int[n6 * n7];
        int[] nArray2 = new int[n * n2];
        int[] nArray3 = new int[n * n2];
        image.getRGB(nArray, 0, n6, 0, 0, n6, n7);
        for (n5 = 0; n5 < n2; ++n5) {
            n4 = n5 * n7 / n2;
            for (n3 = 0; n3 < n; ++n3) {
                nArray2[n5 * n + n3] = nArray[n4 * n6 + n3 * n6 / n];
            }
        }
        n5 = !bl && n >= 12 ? 1 : 0;
        for (n4 = 0; n4 < n2; ++n4) {
            for (n3 = 0; n3 < n; ++n3) {
                int n8;
                int n9;
                int n10 = nArray2[n4 * n + n3];
                if (n10 >>> 24 == 0) continue;
                int n11 = BlocksCanvas.face(n3, n4, n, n2);
                int n12 = bl ? (n11 == 0 ? 100 : (n11 == 1 ? 92 : 84)) : (n11 == 0 ? 108 : (n9 = n11 == 1 ? 80 : 62));
                if (n5 != 0) {
                    n8 = 0;
                    if (n3 == 0 || n4 == 0 || n3 == n - 1 || n4 == n2 - 1) {
                        n8 = 1;
                    } else if (nArray2[n4 * n + n3 - 1] >>> 24 == 0 || nArray2[n4 * n + n3 + 1] >>> 24 == 0 || nArray2[(n4 - 1) * n + n3] >>> 24 == 0 || nArray2[(n4 + 1) * n + n3] >>> 24 == 0) {
                        n8 = 1;
                    } else if (BlocksCanvas.face(n3 + 1, n4, n, n2) != n11 || BlocksCanvas.face(n3, n4 + 1, n, n2) != n11) {
                        n8 = 1;
                    }
                    if (n8 != 0) {
                        n9 = n9 * 50 / 100;
                    }
                }
                n8 = (n10 >> 16 & 0xFF) * n9 / 100;
                int n13 = (n10 >> 8 & 0xFF) * n9 / 100;
                int n14 = (n10 & 0xFF) * n9 / 100;
                if (n8 > 255) {
                    n8 = 255;
                }
                if (n13 > 255) {
                    n13 = 255;
                }
                if (n14 > 255) {
                    n14 = 255;
                }
                nArray3[n4 * n + n3] = n10 & 0xFF000000 | n8 << 16 | n13 << 8 | n14;
            }
        }
        return Image.createRGBImage((int[])nArray3, (int)n, (int)n2, (boolean)true);
    }

    void buildTiles() {
        this.tiles = null;
        int n = this.hw * 2;
        int n2 = 31 * this.hw / 16;
        Image[] imageArray = new Image[24];
        for (int i = 0; i < 24; ++i) {
            if (this.src[i] == null) continue;
            imageArray[i] = this.makeTile(this.src[i], n, n2, i == 0);
        }
        this.tiles = imageArray;
    }

    public void commandAction(Command command, Displayable displayable) {
        this.midlet.quit();
    }

    int get(int n, int n2, int n3) {
        if (n < 0 || n2 < 0 || n3 < 0 || n >= 48 || n2 >= 16 || n3 >= 48) {
            return 0;
        }
        return this.blocks[(n2 * 48 + n) * 48 + n3];
    }

    void set(int n, int n2, int n3, int n4) {
        if (n < 0 || n2 < 0 || n3 < 0 || n >= 48 || n2 >= 16 || n3 >= 48) {
            return;
        }
        this.blocks[(n2 * 48 + n) * 48 + n3] = (byte)n4;
    }

    void fill(int n, int n2, int n3, int n4, int n5, int n6, int n7) {
        for (int i = n2; i <= n5; ++i) {
            for (int j = n; j <= n4; ++j) {
                for (int k = n3; k <= n6; ++k) {
                    this.set(j, i, k, n7);
                }
            }
        }
    }

    int randInt(int n, int n2) {
        return n + (this.rnd.nextInt() >>> 1) % (n2 - n + 1);
    }

    void addTree(int n, int n2, int n3) {
        this.fill(n - 2, n2 + 3, n3 - 2, n + 2, n2 + 4, n3 + 2, 10);
        this.fill(n - 1, n2 + 5, n3 - 1, n + 1, n2 + 5, n3 + 1, 10);
        this.set(n + 1, n2 + 6, n3, 10);
        this.set(n - 1, n2 + 6, n3, 10);
        this.set(n, n2 + 6, n3 + 1, 10);
        this.set(n, n2 + 6, n3 - 1, 10);
        this.set(n, n2 + 6, n3, 10);
        this.fill(n, n2, n3, n, n2 + 5, n3, 9);
    }

    void generateNatural() {
        int n;
        int n2;
        int n3;
        int n4;
        int n5;
        int n6;
        int n7;
        this.fill(0, 0, 0, 47, 0, 47, 22);
        this.fill(0, 1, 0, 47, 5, 47, 1);
        int[][] nArray = new int[7][7];
        for (n7 = 0; n7 < 7; ++n7) {
            for (n6 = 0; n6 < 7; ++n6) {
                nArray[n7][n6] = this.randInt(3, 12);
            }
        }
        for (n7 = 0; n7 < 48; ++n7) {
            for (n6 = 0; n6 < 48; ++n6) {
                n5 = n7 / 8;
                n4 = n6 / 8;
                n3 = n7 - n5 * 8;
                n2 = n6 - n4 * 8;
                n = nArray[n5][n4] * (8 - n3) * (8 - n2) + nArray[n5 + 1][n4] * n3 * (8 - n2) + nArray[n5][n4 + 1] * (8 - n3) * n2 + nArray[n5 + 1][n4 + 1] * n3 * n2;
                if ((n /= 64) > 3) {
                    this.fill(n7, 1, n6, n7, n - 3, n6, 2);
                    this.fill(n7, n - 2, n6, n7, n, n6, 4);
                } else {
                    this.fill(n7, 1, n6, n7, n, n6, 4);
                }
                if (n < 5) continue;
                this.fill(n7, n, n6, n7, n, n6, 3);
            }
        }
        for (n7 = 4; n7 <= 5; ++n7) {
            for (n6 = 0; n6 < 48; ++n6) {
                for (n5 = 0; n5 < 48; ++n5) {
                    n4 = this.get(n6, n7, n5);
                    if (n4 != 3 && n4 != 4 || (n3 = this.get(n6, n7 + 1, n5)) != 0 && n3 != 1) continue;
                    n2 = 0;
                    for (n = -1; n <= 1 && n2 == 0; ++n) {
                        block8: for (int i = -2; i <= 2 && n2 == 0; ++i) {
                            for (int j = -2; j <= 2; ++j) {
                                if (this.get(n6 + i, n7 + n, n5 + j) != 1) continue;
                                n2 = 1;
                                continue block8;
                            }
                        }
                    }
                    if (n2 == 0) continue;
                    this.set(n6, n7, n5, 11);
                }
            }
        }
        for (n7 = 0; n7 < 12; ++n7) {
            n6 = 2 + (this.rnd.nextInt() >>> 1) % 43;
            if (this.get(n6, (n4 = 8) + 1, n5 = 2 + (this.rnd.nextInt() >>> 1) % 43) != 0) continue;
            while (n4 > 0 && this.get(n6, n4, n5) == 0) {
                --n4;
            }
            if (this.get(n6, n4, n5) != 3) continue;
            this.addTree(n6, n4 + 1, n5);
            this.set(n6, n4, n5, 4);
        }
        for (n7 = 1; n7 < 16; ++n7) {
            for (n6 = 0; n6 < 48; ++n6) {
                for (n5 = 0; n5 < 48; ++n5) {
                    if (this.get(n6, n7, n5) != 2) continue;
                    n4 = this.randInt(0, 19);
                    if (n4 == 0) {
                        this.set(n6, n7, n5, 20);
                        continue;
                    }
                    if (n4 != 1) continue;
                    this.set(n6, n7, n5, 21);
                }
            }
        }
    }

    static int idOf(int n) {
        return n < 23 ? n + 2 : 1;
    }

    static int indexOf(int n) {
        return n == 1 ? 23 : n - 2;
    }

    static int shade(int n, int n2) {
        int n3 = (n >> 16 & 0xFF) * n2 / 100;
        int n4 = (n >> 8 & 0xFF) * n2 / 100;
        int n5 = (n & 0xFF) * n2 / 100;
        return n3 << 16 | n4 << 8 | n5;
    }

    boolean vis(int n, int n2) {
        if (n2 == 0) {
            return true;
        }
        if (n2 == 1) {
            return n != 1;
        }
        return false;
    }

    void scan(Graphics graphics, int n, int n2) {
        int n3 = this.getWidth();
        int n4 = this.getHeight();
        this.hface = -1;
        for (int i = 0; i < 48; ++i) {
            for (int j = 0; j < 48; ++j) {
                int n5 = this.ox + (i - j) * this.hw;
                if (graphics != null ? n5 < -this.hw || n5 > n3 + this.hw : Math.abs(n - n5) > this.hw) continue;
                for (int k = 0; k < 16; ++k) {
                    int n6;
                    int n7;
                    int n8 = this.get(i, k, j);
                    if (n8 == 0) continue;
                    int n9 = this.oy + (i + j) * this.hh - k * this.bh;
                    if (graphics != null ? n9 + this.hh + this.bh < 0 || n9 - this.hh > n4 : n2 < n9 - this.hh || n2 > n9 + this.hh + this.bh) continue;
                    boolean bl = this.vis(n8, this.get(i, k + 1, j));
                    boolean bl2 = this.vis(n8, this.get(i, k, j + 1));
                    boolean bl3 = this.vis(n8, this.get(i + 1, k, j));
                    if (graphics != null) {
                        if (this.tiles != null && this.tiles[n8 - 1] != null) {
                            if (!bl && !bl2 && !bl3) continue;
                            graphics.drawImage(this.tiles[n8 - 1], n5 - this.hw, n9 - this.hh, 20);
                            continue;
                        }
                        n7 = COLOR[n8];
                        if (bl) {
                            graphics.setColor(n7);
                            graphics.fillTriangle(n5, n9 - this.hh, n5 + this.hw, n9, n5, n9 + this.hh);
                            graphics.fillTriangle(n5, n9 - this.hh, n5 - this.hw, n9, n5, n9 + this.hh);
                        }
                        if (bl2) {
                            graphics.setColor(BlocksCanvas.shade(n7, 75));
                            graphics.fillTriangle(n5 - this.hw, n9, n5, n9 + this.hh, n5, n9 + this.hh + this.bh);
                            graphics.fillTriangle(n5 - this.hw, n9, n5 - this.hw, n9 + this.bh, n5, n9 + this.hh + this.bh);
                        }
                        if (!bl3) continue;
                        graphics.setColor(BlocksCanvas.shade(n7, 55));
                        graphics.fillTriangle(n5, n9 + this.hh, n5 + this.hw, n9, n5 + this.hw, n9 + this.bh);
                        graphics.fillTriangle(n5, n9 + this.hh, n5, n9 + this.hh + this.bh, n5 + this.hw, n9 + this.bh);
                        continue;
                    }
                    n7 = -1;
                    if (bl && Math.abs(n - n5) * this.hh + Math.abs(n2 - n9) * this.hw <= this.hw * this.hh) {
                        n7 = 0;
                    }
                    if (bl2 && n >= n5 - this.hw && n < n5 && n2 >= (n6 = n9 + (n - (n5 - this.hw)) * this.hh / this.hw) && n2 <= n6 + this.bh) {
                        n7 = 1;
                    }
                    if (bl3 && n >= n5 && n <= n5 + this.hw && n2 >= (n6 = n9 + this.hh - (n - n5) * this.hh / this.hw) && n2 <= n6 + this.bh) {
                        n7 = 2;
                    }
                    if (n7 < 0) continue;
                    this.hx = i;
                    this.hy = k;
                    this.hz = j;
                    this.hface = n7;
                }
            }
        }
    }

    protected void paint(Graphics graphics) {
        int n;
        int n2;
        int n3;
        int n4 = this.getWidth();
        int n5 = this.getHeight();
        if (!this.inited) {
            this.inited = true;
            this.buildTiles();
            this.ox = n4 / 2;
            this.oy = (n5 - 30) / 2 - 48 * this.hh + 6 * this.bh;
        }
        graphics.setColor(8900331);
        graphics.fillRect(0, 0, n4, n5);
        this.scan(graphics, 0, 0);
        if (this.lpx >= 0 && this.get(this.lpx, this.lpy, this.lpz) != 0) {
            n3 = this.ox + (this.lpx - this.lpz) * this.hw;
            n2 = this.oy + (this.lpx + this.lpz) * this.hh - this.lpy * this.bh;
            graphics.setColor(0xFFFF00);
            graphics.drawLine(n3, n2 - this.hh, n3 + this.hw, n2);
            graphics.drawLine(n3 + this.hw, n2, n3 + this.hw, n2 + this.bh);
            graphics.drawLine(n3 + this.hw, n2 + this.bh, n3, n2 + this.hh + this.bh);
            graphics.drawLine(n3, n2 + this.hh + this.bh, n3 - this.hw, n2 + this.bh);
            graphics.drawLine(n3 - this.hw, n2 + this.bh, n3 - this.hw, n2);
            graphics.drawLine(n3 - this.hw, n2, n3, n2 - this.hh);
        }
        if (this.keyMode && !this.help && !this.inv) {
            n3 = n4 / 2;
            n2 = (n5 - 30) / 2;
            this.scan(null, n3, n2);
            if (this.hface >= 0) {
                this.drawFace(graphics, this.hx, this.hy, this.hz, this.hface);
            }
            graphics.setColor(0xFF0000);
            graphics.drawLine(n3 - 6, n2, n3 + 6, n2);
            graphics.drawLine(n3, n2 - 6, n3, n2 + 6);
        }
        graphics.setColor(0x202020);
        graphics.fillRect(0, n5 - 30, n4, 30);
        graphics.setColor(0xFFFFFF);
        n3 = n4 / 7;
        n2 = n5 - 30 + 9;
        String[] stringArray = new String[]{this.lZoomOut, this.lZoomIn, this.lPrev, "", this.lNext, this.removeMode ? this.lDel : this.lPut, this.lHelp};
        for (n = 0; n < 7; ++n) {
            graphics.drawRect(n * n3 + 1, n5 - 30 + 1, n3 - 2, 28);
            if (stringArray[n].length() <= 0) continue;
            graphics.drawString(stringArray[n], n * n3 + n3 / 2, n2, 17);
        }
        if (this.src[this.cur - 1] != null) {
            n = n3 - 4 < 24 ? n3 - 4 : 24;
            int n6 = n * 31 / 32;
            if (this.icon == null || this.iconFor != this.cur || this.iconW != n) {
                this.icon = this.makeTile(this.src[this.cur - 1], n, n6, this.cur == 1);
                this.iconFor = this.cur;
                this.iconW = n;
            }
            graphics.drawImage(this.icon, 3 * n3 + n3 / 2 - n / 2, n5 - 30 + (30 - n6) / 2, 20);
        } else {
            graphics.setColor(COLOR[this.cur]);
            graphics.fillRect(3 * n3 + n3 / 2 - 7, n5 - 30 + 8, 14, 14);
        }
        if (this.inv) {
            this.paintInv(graphics, n4, n5);
        }
        if (this.help) {
            this.paintHelp(graphics, n4, n5);
        }
    }

    void drawFace(Graphics graphics, int n, int n2, int n3, int n4) {
        int[] nArray;
        int[] nArray2;
        int n5 = this.ox + (n - n3) * this.hw;
        int n6 = this.oy + (n + n3) * this.hh - n2 * this.bh;
        if (n4 == 0) {
            nArray2 = new int[]{n5, n5 + this.hw, n5, n5 - this.hw};
            nArray = new int[]{n6 - this.hh, n6, n6 + this.hh, n6};
        } else if (n4 == 1) {
            nArray2 = new int[]{n5 - this.hw, n5, n5, n5 - this.hw};
            nArray = new int[]{n6, n6 + this.hh, n6 + this.hh + this.bh, n6 + this.bh};
        } else {
            nArray2 = new int[]{n5, n5 + this.hw, n5 + this.hw, n5};
            nArray = new int[]{n6 + this.hh, n6, n6 + this.bh, n6 + this.hh + this.bh};
        }
        graphics.setColor(0xFF0000);
        for (int i = 0; i < 4; ++i) {
            int n7 = i + 1 & 3;
            graphics.drawLine(nArray2[i], nArray[i], nArray2[n7], nArray[n7]);
        }
    }

    String blockName(int n) {
        String string = TEX[BlocksCanvas.idOf(n) - 1];
        StringBuffer stringBuffer = new StringBuffer();
        for (int i = 0; i < string.length(); ++i) {
            char c = string.charAt(i);
            if (c == '_') {
                c = ' ';
            }
            if (i == 0) {
                c = Character.toUpperCase(c);
            }
            stringBuffer.append(c);
        }
        return this.t("block." + BlocksCanvas.idOf(n), stringBuffer.toString());
    }

    void invLayout(int n, int n2) {
        this.invIw = n >= 200 ? 24 : (n >= 140 ? 20 : 16);
        this.invCellW = this.invIw + 10;
        this.invCellH = this.invIw * 31 / 32 + 10;
        this.invCols = (n - 8) / this.invCellW;
        if (this.invCols < 2) {
            this.invCols = 2;
        }
        int n3 = Font.getFont((int)0, (int)0, (int)8).getHeight();
        this.invTop = 6 + n3 + 6;
        int n4 = n2 - n3 - 8;
        this.invRows = (n4 - this.invTop) / this.invCellH;
        if (this.invRows < 1) {
            this.invRows = 1;
        }
        this.invX0 = (n - this.invCols * this.invCellW) / 2;
        int n5 = this.invSel / this.invCols;
        if (n5 < this.invScroll) {
            this.invScroll = n5;
        }
        if (n5 >= this.invScroll + this.invRows) {
            this.invScroll = n5 - this.invRows + 1;
        }
        if (this.invScroll < 0) {
            this.invScroll = 0;
        }
    }

    void openInv() {
        this.inv = true;
        this.invSel = BlocksCanvas.indexOf(this.cur);
        this.invScroll = 0;
        this.repaint();
    }

    void paintInv(Graphics graphics, int n, int n2) {
        int n3;
        this.invLayout(n, n2);
        if (this.invIcons == null || this.invIconW != this.invIw) {
            this.invIcons = new Image[24];
            for (int i = 0; i < 24; ++i) {
                n3 = BlocksCanvas.idOf(i);
                if (this.src[n3 - 1] == null) continue;
                this.invIcons[i] = this.makeTile(this.src[n3 - 1], this.invIw, this.invIw * 31 / 32, n3 == 1);
            }
            this.invIconW = this.invIw;
        }
        Font font = Font.getFont((int)0, (int)0, (int)8);
        graphics.setFont(font);
        graphics.setColor(0x808080);
        graphics.fillRect(2, 2, n - 4, n2 - 4);
        graphics.setColor(0xFFFFFF);
        graphics.drawRect(2, 2, n - 5, n2 - 5);
        graphics.drawString(this.t("inv.title", "INVENTORY"), n / 2, 6, 17);
        for (int i = n3 = this.invScroll * this.invCols; i < 24 && i < n3 + this.invRows * this.invCols; ++i) {
            int n4 = this.invX0 + (i - n3) % this.invCols * this.invCellW;
            int n5 = this.invTop + (i - n3) / this.invCols * this.invCellH;
            if (this.invIcons[i] != null) {
                graphics.drawImage(this.invIcons[i], n4 + 5, n5 + 5, 20);
            } else {
                graphics.setColor(COLOR[BlocksCanvas.idOf(i)]);
                graphics.fillRect(n4 + 5, n5 + 5, this.invIw, this.invIw * 31 / 32);
            }
            if (i != this.invSel) continue;
            graphics.setColor(16737856);
            graphics.drawRect(n4 + 1, n5 + 1, this.invCellW - 3, this.invCellH - 3);
            graphics.drawRect(n4 + 2, n5 + 2, this.invCellW - 5, this.invCellH - 5);
        }
        if (this.invScroll > 0) {
            graphics.drawString("^", n - 8, 6, 24);
        }
        if ((this.invScroll + this.invRows) * this.invCols < 24) {
            graphics.drawString("v", n - 8, n2 - font.getHeight() - 6, 24);
        }
        graphics.setColor(0xFFFFFF);
        graphics.drawString(this.blockName(this.invSel), n / 2, n2 - font.getHeight() - 6, 17);
    }

    void invPick() {
        this.cur = BlocksCanvas.idOf(this.invSel);
        this.inv = false;
        this.repaint();
    }

    void invKey(int n) {
        int n2 = this.getGameAction(n);
        if (n2 == 2 || n == 52) {
            if (this.invSel > 0) {
                --this.invSel;
            }
        } else if (n2 == 5 || n == 54) {
            if (this.invSel < 23) {
                ++this.invSel;
            }
        } else if (n2 == 1 || n == 50) {
            if (this.invSel >= this.invCols) {
                this.invSel -= this.invCols;
            }
        } else if (n2 == 6 || n == 56) {
            this.invSel = Math.min(23, this.invSel + this.invCols);
        } else {
            if (n2 == 8 || n == 53) {
                this.invPick();
                return;
            }
            if (n == 35 || n == 42 || n == 48) {
                this.inv = false;
            } else if (n == 49 || n == 55) {
                if (this.invSel > 0) {
                    --this.invSel;
                }
            } else if (n == 51 || n == 57) {
                if (this.invSel < 23) {
                    ++this.invSel;
                }
            } else {
                return;
            }
        }
        this.repaint();
    }

    void invTap(int n, int n2) {
        int n3;
        int n4 = (n - this.invX0) / this.invCellW;
        int n5 = (n2 - this.invTop) / this.invCellH;
        if (n >= this.invX0 && n2 >= this.invTop && n4 >= 0 && n4 < this.invCols && n5 >= 0 && n5 < this.invRows && (n3 = (this.invScroll + n5) * this.invCols + n4) < 24) {
            this.invSel = n3;
            this.invPick();
            return;
        }
        if (n2 > this.getHeight() - 24 && n > this.getWidth() / 2) {
            if ((this.invScroll + this.invRows) * this.invCols < 24) {
                ++this.invScroll;
            }
            this.repaint();
            return;
        }
        if (n2 < this.invTop && n > this.getWidth() / 2) {
            if (this.invScroll > 0) {
                --this.invScroll;
            }
            this.repaint();
            return;
        }
        this.inv = false;
        this.repaint();
    }

    void paintHelp(Graphics graphics, int n, int n2) {
        Font font = Font.getFont((int)0, (int)0, (int)8);
        int n3 = 13;
        int n4 = (n2 - 12) / n3;
        if (n4 < 4) {
            n4 = 4;
        }
        graphics.setColor(1054752);
        graphics.fillRect(2, 2, n - 4, n2 - 4);
        graphics.setColor(0xFFFFFF);
        graphics.drawRect(2, 2, n - 5, n2 - 5);
        graphics.setFont(font);
        int n5 = this.helpPage * n4;
        for (int i = 0; i < n4 && n5 + i < this.helpText.length; ++i) {
            graphics.drawString(this.helpText[n5 + i], 8, 6 + i * n3, 20);
        }
    }

    void nextHelp() {
        int n = 13;
        int n2 = (this.getHeight() - 12) / n;
        if (n2 < 4) {
            n2 = 4;
        }
        ++this.helpPage;
        if (this.helpPage * n2 >= this.helpText.length) {
            this.help = false;
            this.helpPage = 0;
        }
        this.repaint();
    }

    void openHelp() {
        this.help = true;
        this.helpPage = 0;
        this.repaint();
    }

    protected void pointerPressed(int n, int n2) {
        this.keyMode = false;
        this.lpx = -1;
        this.downX = this.lastX = n;
        this.downY = this.lastY = n2;
        this.dragged = false;
    }

    protected void pointerDragged(int n, int n2) {
        if (this.help || this.inv || this.downY >= this.getHeight() - 30) {
            return;
        }
        if (!this.dragged && Math.abs(n - this.downX) + Math.abs(n2 - this.downY) < 6) {
            return;
        }
        this.dragged = true;
        this.ox += n - this.lastX;
        this.oy += n2 - this.lastY;
        this.lastX = n;
        this.lastY = n2;
        this.repaint();
    }

    protected void pointerReleased(int n, int n2) {
        if (this.help) {
            this.nextHelp();
            return;
        }
        if (this.inv) {
            this.invTap(n, n2);
            return;
        }
        if (this.dragged) {
            return;
        }
        int n3 = this.getWidth();
        int n4 = this.getHeight();
        if (n2 >= n4 - 30) {
            int n5 = n * 7 / n3;
            if (n5 == 0) {
                this.zoom(-2);
            } else if (n5 == 1) {
                this.zoom(2);
            } else if (n5 == 2) {
                this.nextBlock(-1);
            } else {
                if (n5 == 3) {
                    this.openInv();
                    return;
                }
                if (n5 == 4) {
                    this.nextBlock(1);
                } else if (n5 == 5) {
                    this.removeMode = !this.removeMode;
                } else {
                    this.openHelp();
                    return;
                }
            }
            this.repaint();
            return;
        }
        this.tapAt(n, n2);
    }

    void tapAt(int n, int n2) {
        this.scan(null, n, n2);
        if (this.hface < 0) {
            return;
        }
        if (this.removeMode) {
            if (this.get(this.hx, this.hy, this.hz) != 22) {
                this.set(this.hx, this.hy, this.hz, 0);
            }
            this.lpx = -1;
        } else {
            int n3;
            int n4 = this.hx;
            int n5 = this.hy;
            int n6 = this.hz;
            if (this.hface == 0) {
                ++n5;
            } else if (this.hface == 1) {
                ++n6;
            } else {
                ++n4;
            }
            if (n4 < 48 && n5 < 16 && n6 < 48 && ((n3 = this.get(n4, n5, n6)) == 0 || n3 == 1)) {
                this.set(n4, n5, n6, this.cur);
                this.lpx = n4;
                this.lpy = n5;
                this.lpz = n6;
            }
        }
        this.repaint();
    }

    void nextBlock(int n) {
        this.cur += n;
        if (this.cur > 24) {
            this.cur = 1;
        }
        if (this.cur < 1) {
            this.cur = 24;
        }
    }

    void zoom(int n) {
        int n2 = this.hw + n;
        if (n2 < 4 || n2 > 16) {
            return;
        }
        int n3 = this.getWidth() / 2;
        int n4 = (this.getHeight() - 30) / 2;
        this.ox = n3 + (this.ox - n3) * n2 / this.hw;
        this.oy = n4 + (this.oy - n4) * n2 / this.hw;
        this.hw = n2;
        this.hh = n2 / 2;
        this.bh = n2;
        this.buildTiles();
    }

    protected void keyPressed(int n) {
        if (this.help) {
            this.nextHelp();
            return;
        }
        if (this.inv) {
            this.invKey(n);
            return;
        }
        this.keyMode = true;
        this.lpx = -1;
        int n2 = 8;
        int n3 = this.getGameAction(n);
        if (n3 == 1 || n == 50) {
            this.oy += n2;
        } else if (n3 == 6 || n == 56) {
            this.oy -= n2;
        } else if (n3 == 2 || n == 52) {
            this.ox += n2;
        } else if (n3 == 5 || n == 54) {
            this.ox -= n2;
        } else {
            if (n3 == 8 || n == 53) {
                this.tapAt(this.getWidth() / 2, (this.getHeight() - 30) / 2);
                return;
            }
            if (n == 49) {
                this.zoom(-2);
            } else if (n == 51) {
                this.zoom(2);
            } else if (n == 55) {
                this.nextBlock(-1);
            } else if (n == 57) {
                this.nextBlock(1);
            } else {
                if (n == 48) {
                    this.openInv();
                    return;
                }
                if (n == 42) {
                    this.removeMode = !this.removeMode;
                } else {
                    if (n == 35) {
                        this.openHelp();
                        return;
                    }
                    return;
                }
            }
        }
        this.repaint();
    }

    protected void keyRepeated(int n) {
        int n2 = this.getGameAction(n);
        if (!(this.help || this.inv || n2 != 1 && n2 != 6 && n2 != 2 && n2 != 5 && n != 50 && n != 52 && n != 54 && n != 56)) {
            this.keyPressed(n);
        }
    }
}
