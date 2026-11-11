import javax.microedition.lcdui.Display;
import javax.microedition.midlet.MIDlet;

/* Blocks J2ME - TheScienceElf/Blocks-TI-84 oyununun J2ME (MIDP 2.0 / CLDC 1.1) uyarlamasi */
public class BlocksMIDlet extends MIDlet {
    private BlocksCanvas canvas;

    protected void startApp() {
        if (canvas == null) canvas = new BlocksCanvas(this);
        Display.getDisplay(this).setCurrent(canvas);
    }
    protected void pauseApp() {}
    protected void destroyApp(boolean unconditional) {}
    void quit() { destroyApp(true); notifyDestroyed(); }
}
