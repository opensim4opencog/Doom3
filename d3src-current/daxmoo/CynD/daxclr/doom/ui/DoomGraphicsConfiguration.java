package daxclr.doom.ui;

import java.awt.Component;
import java.awt.GraphicsConfiguration;
import java.awt.GraphicsDevice;
import java.awt.Image;
import java.awt.Rectangle;
import java.awt.Transparency;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;
import java.awt.image.ColorModel;
import java.awt.image.VolatileImage;

public class DoomGraphicsConfiguration extends GraphicsConfiguration {

    static DoomGraphicsDevice theGraphicsDevice;
    static Rectangle theRectangle;
    static ColorModel theColorModel;
//	java.awt.Image[] images;
    static java.util.ArrayList<Image> images;
    static AffineTransform theAffineTransform;

    public DoomGraphicsConfiguration(int width, int height) {
        theAffineTransform = new AffineTransform();
        theRectangle = new Rectangle(width, height);
        theColorModel = new DoomColorModel(this);
        theGraphicsDevice = new DoomGraphicsDevice(this);
    }

    public DoomGraphicsConfiguration() {
        this(640, 480);
    }


    static public DoomBufferedImage addBImage(BufferedImage image) {
        images.add(image);
        return (DoomBufferedImage) image;
    }

    static public DoomVolatileImage addVImage(VolatileImage image) {
        images.add(image);
        return (DoomVolatileImage) image;
    }

    /**
     * Returns a {@link BufferedImage} with a data layout and color model
     * compatible with this <code>DoomGraphicsConfiguration</code>.  This
     * method has nothing to do with memory-mapping
     * a device.  The returned <code>BufferedImage</code> has
     * a layout and color model that is closest to this native device
     * configuration and can therefore be optimally blitted to this
     * device.
     * @param width the width of the returned <code>BufferedImage</code>
     * @param height the height of the returned <code>BufferedImage</code>
     * @return a <code>BufferedImage</code> whose data layout and color
     * model is compatible with this <code>DoomGraphicsConfiguration</code>.
     */
    public BufferedImage createCompatibleImage(int width, int height) {
        return addBImage(new DoomBufferedImage(width, height));
    }

    /**
     * Returns the {@link DoomGraphicsDevice} associated with this
     * <code>DoomGraphicsConfiguration</code>.
     * @return a <code>DoomGraphicsDevice</code> object that is
     * associated with this <code>DoomGraphicsConfiguration</code>.
     */
    public GraphicsDevice getDevice() {
        return theGraphicsDevice;
    }

    /**
     * Returns the {@link ColorModel} associated with this
     * <code>DoomGraphicsConfiguration</code>.
     * @return a <code>ColorModel</code> object that is associated with
     * this <code>DoomGraphicsConfiguration</code>.
     */
    public ColorModel getColorModel() {
        return theColorModel;
    }

    /**
     * Returns a {@link VolatileImage} with a data layout and color model
     * compatible with this <code>DoomGraphicsConfiguration</code>.
     * The returned <code>VolatileImage</code>
     * may have data that is stored optimally for the underlying graphics
     * device and may therefore benefit from platform-specific rendering
     * acceleration.
     * @param width the width of the returned <code>VolatileImage</code>
     * @param height the height of the returned <code>VolatileImage</code>
     * @return a <code>VolatileImage</code> whose data layout and color
     * model is compatible with this <code>DoomGraphicsConfiguration</code>.
     * @see Component#createVolatileImage(int, int)
     */
    public VolatileImage createCompatibleVolatileImage(int width, int height) {
        return addVImage(new DoomVolatileImage(width, height));
    }

    /**
     * Returns a {@link VolatileImage} with a data layout and color model
     * compatible with this <code>DoomGraphicsConfiguration</code>.
     * The returned <code>VolatileImage</code>
     * may have data that is stored optimally for the underlying graphics
     * device and may therefore benefit from platform-specific rendering
     * acceleration.
     * @param width the width of the returned <code>VolatileImage</code>
     * @param height the height of the returned <code>VolatileImage</code>
     * @param transparency the specified transparency mode
     * @return a <code>VolatileImage</code> whose data layout and color
     * model is compatible with this <code>DoomGraphicsConfiguration</code>.
     * @throws IllegalArgumentException if the transparency is not a valid value
     * @see Transparency#OPAQUE
     * @see Transparency#BITMASK
     * @see Transparency#TRANSLUCENT
     * @see Component#createVolatileImage(int, int)
     * @since 1.5
     */
    public VolatileImage createCompatibleVolatileImage(int width, int height,
            int transparency) {
        return addVImage(new DoomVolatileImage(width, height, transparency));
    }

    /**
     * Returns the default {@link AffineTransform} for this
     * <code>DoomGraphicsConfiguration</code>. This
     * <code>AffineTransform</code> is typically the Identity transform
     * for most normal screens.  The default <code>AffineTransform</code>
     * maps coordinates onto the device such that 72 user space
     * coordinate units measure approximately 1 inch in device
     * space.  The normalizing transform can be used to make
     * this mapping more exact.  Coordinates in the coordinate space
     * defined by the default <code>AffineTransform</code> for screen and
     * printer devices have the origin in the upper left-hand corner of
     * the target region of the device, with X coordinates
     * increasing to the right and Y coordinates increasing downwards.
     * For image buffers not associated with a device, such as those not
     * created by <code>createCompatibleImage</code>,
     * this <code>AffineTransform</code> is the Identity transform.
     * @return the default <code>AffineTransform</code> for this
     * <code>DoomGraphicsConfiguration</code>.
     */
    public AffineTransform getDefaultTransform() {
        return theAffineTransform;
    }

    /**
     * Returns a <code>BufferedImage</code> that supports the specified
     * transparency and has a data layout and color model
     * compatible with this <code>DoomGraphicsConfiguration</code>.  This
     * method has nothing to do with memory-mapping
     * a device. The returned <code>BufferedImage</code> has a layout and
     * color model that can be optimally blitted to a device
     * with this <code>DoomGraphicsConfiguration</code>.
     * @param width the width of the returned <code>BufferedImage</code>
     * @param height the height of the returned <code>BufferedImage</code>
     * @param transparency the specified transparency mode
     * @return a <code>BufferedImage</code> whose data layout and color
     * model is compatible with this <code>DoomGraphicsConfiguration</code>
     * and also supports the specified transparency.
     * @throws IllegalArgumentException if the transparency is not a valid value
     * @see Transparency#OPAQUE
     * @see Transparency#BITMASK
     * @see Transparency#TRANSLUCENT
     */
    public BufferedImage createCompatibleImage(int width, int height,
                                               int transparency) {
        return addBImage(new DoomBufferedImage(width, height, transparency));
    }

    /**
     * Returns the bounds of the <code>DoomGraphicsConfiguration</code>
     * in the device coordinates. In a multi-screen environment
     * with a virtual device, the bounds can have negative X
     * or Y origins.
     * @return the bounds of the area covered by this
     * <code>DoomGraphicsConfiguration</code>.
     * @since 1.3
     */
    public Rectangle getBounds() {
        return theRectangle;
    }

    /**
     * Returns the <code>ColorModel</code> associated with this
     * <code>DoomGraphicsConfiguration</code> that supports the specified
     * transparency.
     * @param transparency the specified transparency mode
     * @return a <code>ColorModel</code> object that is associated with
     * this <code>DoomGraphicsConfiguration</code> and supports the
     * specified transparency or null if the transparency is not a valid
     * value.
     * @see Transparency#OPAQUE
     * @see Transparency#BITMASK
     * @see Transparency#TRANSLUCENT
     */
    public ColorModel getColorModel(int transparency) {
        return new DoomColorModel(transparency);
    }

    /**
     *
     * Returns a <code>AffineTransform</code> that can be concatenated
     * with the default <code>AffineTransform</code>
     * of a <code>DoomGraphicsConfiguration</code> so that 72 units in user
     * space equals 1 inch in device space.
     * <p>
     * For a particular {@link DoomGraphics2D}, g, one
     * can reset the transformation to create
     * such a mapping by using the following pseudocode:
     * <pre>
     *      DoomGraphicsConfiguration gc = g.getDoomGraphicsConfiguration();
     *
     *      g.setTransform(gc.getDefaultTransform());
     *      g.transform(gc.getNormalizingTransform());
     * </pre>
     * Note that sometimes this <code>AffineTransform</code> is identity,
     * such as for printers or metafile output, and that this
     * <code>AffineTransform</code> is only as accurate as the information
     * supplied by the underlying system.  For image buffers not
     * associated with a device, such as those not created by
     * <code>createCompatibleImage</code>, this
     * <code>AffineTransform</code> is the Identity transform
     * since there is no valid distance measurement.
     * @return an <code>AffineTransform</code> to concatenate to the
     * default <code>AffineTransform</code> so that 72 units in user
     * space is mapped to 1 inch in device space.
     */
    public AffineTransform getNormalizingTransform() {
        return theAffineTransform;
    }
}
