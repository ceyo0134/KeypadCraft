/*
 * KeypadCraft - Blocks J2ME 1.0.1
 * Copyright (c) 2026 Ceyo. See COPYRIGHT.txt / LICENSE in this repository.
 *
 * NOTE: this source was DECOMPILED (CFR 0.152) from the released Blocks 1.0.1 jar,
 * because the original .java files of 1.0.1 were not available. Comments and some
 * local variable names are lost; behaviour matches the jar. The original 1.0 source
 * is in the git history (tag v1.0).
 */
import javax.microedition.lcdui.Display;
import javax.microedition.lcdui.Displayable;
import javax.microedition.midlet.MIDlet;

public class BlocksMIDlet
extends MIDlet {
    private BlocksCanvas canvas;

    protected void startApp() {
        if (this.canvas == null) {
            this.canvas = new BlocksCanvas(this);
        }
        Display.getDisplay((MIDlet)this).setCurrent((Displayable)this.canvas);
    }

    protected void pauseApp() {
    }

    protected void destroyApp(boolean bl) {
    }

    void quit() {
        this.destroyApp(true);
        this.notifyDestroyed();
    }
}
