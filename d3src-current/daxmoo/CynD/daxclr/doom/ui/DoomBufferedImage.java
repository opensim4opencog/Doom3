package daxclr.doom.ui;

import java.awt.image.BufferedImage;

public class DoomBufferedImage extends BufferedImage {
    public DoomBufferedImage() {
        this(640, 480);
    }

    public DoomBufferedImage(int x, int y) {
        super(x, y, TYPE_INT_RGB);
    }

    public DoomBufferedImage(int x, int y, int transp) {
        this(x, y);
    }

    public DoomBufferedImage(DoomGraphicsConfiguration gc) {
        this();
    }

}
