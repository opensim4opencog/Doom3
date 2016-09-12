package daxclr.doom.ui;

import java.awt.image.ColorModel;

public class DoomColorModel extends ColorModel {

    public DoomColorModel(int trans) {
        super(trans);
    }

    public DoomColorModel(DoomGraphicsConfiguration gc) {
        this(32);
    }

    /**
     * Returns the red color component for the specified pixel, scaled
     * from 0 to 255 in the default RGB ColorSpace, sRGB.  A color conversion
     * is done if necessary.  The pixel value is specified as an int.
     * An <code>IllegalArgumentException</code> is thrown if pixel
     * values for this <code>ColorModel</code> are not conveniently
     * representable as a single int.  The returned value is not a
     * pre-multiplied value.  For example, if the
     * alpha is premultiplied, this method divides it out before returning
     * the value.  If the alpha value is 0, the red value is 0.
     * @param pixel a specified pixel
     * @return the value of the red component of the specified pixel.
     */
    public int getRed(int pixel) {
        return pixel;
    }

    /**
     * Returns the green color component for the specified pixel, scaled
     * from 0 to 255 in the default RGB ColorSpace, sRGB.  A color conversion
     * is done if necessary.  The pixel value is specified as an int.
     * An <code>IllegalArgumentException</code> is thrown if pixel
     * values for this <code>ColorModel</code> are not conveniently
     * representable as a single int.  The returned value is a non
     * pre-multiplied value.  For example, if the alpha is premultiplied,
     * this method divides it out before returning
     * the value.  If the alpha value is 0, the green value is 0.
     * @param pixel the specified pixel
     * @return the value of the green component of the specified pixel.
     */
    public int getGreen(int pixel) {
        return pixel;
    }

    /**
     * Returns the blue color component for the specified pixel, scaled
     * from 0 to 255 in the default RGB ColorSpace, sRGB.  A color conversion
     * is done if necessary.  The pixel value is specified as an int.
     * An <code>IllegalArgumentException</code> is thrown if pixel values
     * for this <code>ColorModel</code> are not conveniently representable
     * as a single int.  The returned value is a non pre-multiplied
     * value, for example, if the alpha is premultiplied, this method
     * divides it out before returning the value.  If the alpha value is
     * 0, the blue value is 0.
     * @param pixel the specified pixel
     * @return the value of the blue component of the specified pixel.
     */
    public int getBlue(int pixel) {
        return pixel;
    }

    /**
     * Returns the alpha component for the specified pixel, scaled
     * from 0 to 255.  The pixel value is specified as an int.
     * An <code>IllegalArgumentException</code> is thrown if pixel
     * values for this <code>ColorModel</code> are not conveniently
     * representable as a single int.
     * @param pixel the specified pixel
     * @return the value of alpha component of the specified pixel.
     */
    public int getAlpha(int pixel) {
        return pixel;
    }

}
