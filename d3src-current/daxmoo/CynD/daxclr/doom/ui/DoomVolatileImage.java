package daxclr.doom.ui;

import java.awt.Graphics2D;
import java.awt.GraphicsConfiguration;
import java.awt.ImageCapabilities;
import java.awt.image.BufferedImage;
import java.awt.image.ImageObserver;
import java.awt.image.VolatileImage;

public class DoomVolatileImage extends VolatileImage {
    BufferedImage buffer = new DoomBufferedImage();
    GraphicsConfiguration theGC;
    /**
     * Returns a static snapshot image of this object.  The
     * <code>BufferedImage</code> returned is only current with
     * the <code>VolatileImage</code> at the time of the request
     * and will not be updated with any future changes to the
     * <code>VolatileImage</code>.
     * @return a {@link BufferedImage} representation of this
     *		<code>VolatileImage</code>
     * @see BufferedImage
     */
    public BufferedImage getSnapshot() {
        return buffer;
    }

    /**
     * Returns the width of the <code>VolatileImage</code>.
     * @return the width of this <code>VolatileImage</code>.
     */
    public int getWidth() {
        return buffer.getWidth();
    }

    /**
     * Returns <code>true</code> if rendering data was lost since last
     * <code>validate</code> call.  This method should be called by the
     * application at the end of any series of rendering operations to
     * or from the image to see whether
     * the image needs to be validated and the rendering redone.
     * @return <code>true</code> if the drawing surface needs to be restored;
     * <code>false</code> otherwise.
     */
    public boolean contentsLost() {
        return false;
    }

    /**
     * Returns the height of the <code>VolatileImage</code>.
     * @return the height of this <code>VolatileImage</code>.
     */
    public int getHeight() {
        return buffer.getHeight();
    }

    /**
     * Creates a <code>Graphics2D</code>, which can be used to draw into
     * this <code>VolatileImage</code>.
     * @return a <code>Graphics2D</code>, used for drawing into this
     *          image.
     */
    public Graphics2D createGraphics() {
        return buffer.createGraphics();
    }

    /**
     * Attempts to restore the drawing surface of the image if the surface
     * had been lost since the last <code>validate</code> call.  Also
     * validates this image against the given GraphicsConfiguration
     * parameter to see whether operations from this image to the
     * GraphicsConfiguration are compatible.  An example of an
     * incompatible combination might be a situation where a VolatileImage
     * object was created on one graphics device and then was used
     * to render to a different graphics device.  Since VolatileImage
     * objects tend to be very device-specific, this operation might
     * not work as intended, so the return code from this validate
     * call would note that incompatibility.  A null or incorrect
     * value for gc may cause incorrect values to be returned from
     * <code>validate</code> and may cause later problems with rendering.
     *
     * @param   gc   a <code>GraphicsConfiguration</code> object for this
     *		image to be validated against.  A null gc implies that the
     *		validate method should skip the compatibility test.
     * @return	<code>IMAGE_OK</code> if the image did not need validation<BR>
     *		<code>IMAGE_RESTORED</code> if the image needed restoration.
     *		Restoration implies that the contents of the image may have
     *		been affected and the image may need to be re-rendered.<BR>
     *		<code>IMAGE_INCOMPATIBLE</code> if the image is incompatible
     *		with the <code>GraphicsConfiguration</code> object passed
     *		into the <code>validate</code> method.  Incompatibility
     *		implies that the image may need to be recreated with a
     *		new <code>Component</code> or
     *		<code>GraphicsConfiguration</code> in order to get an image
     *		that can be used successfully with this
     *		<code>GraphicsConfiguration</code>.
     *		An incompatible image is not checked for whether restoration
     *		was necessary, so the state of the image is unchanged
     *		after a return value of <code>IMAGE_INCOMPATIBLE</code>
     *		and this return value implies nothing about whether the
     *		image needs to be restored.
     * @see java.awt.GraphicsConfiguration
     * @see java.awt.Component
     * @see #IMAGE_OK
     * @see #IMAGE_RESTORED
     * @see #IMAGE_INCOMPATIBLE
     */
    public int validate(GraphicsConfiguration gc) {
        return IMAGE_RESTORED;
    }

    /**
     * Returns an ImageCapabilities object which can be
     * inquired as to the specific capabilities of this
     * VolatileImage.  This would allow programmers to find
     * out more runtime information on the specific VolatileImage
     * object that they have created.  For example, the user
     * might create a VolatileImage but the system may have
     * no video memory left for creating an image of that
     * size, so although the object is a VolatileImage, it is
     * not as accelerated as other VolatileImage objects on
     * this platform might be.  The user might want that
     * information to find other solutions to their problem.
     * @return an <code>ImageCapabilities</code> object that contains
     *         the capabilities of this <code>VolatileImage</code>.
     * @since 1.4
     */
    public ImageCapabilities getCapabilities() {
        return new ImageCapabilities(true); //theGC);
    }

    /**
     * Gets a property of this image by name.
     * <p>
     * Individual property names are defined by the various image
     * formats. If a property is not defined for a particular image, this
     * method returns the <code>UndefinedProperty</code> object.
     * <p>
     * If the properties for this image are not yet known, this method
     * returns <code>null</code>, and the <code>ImageObserver</code>
     * object is notified later.
     * <p>
     * The property name <code>"comment"</code> should be used to store
     * an optional comment which can be presented to the application as a
     * description of the image, its source, or its author.
     * @param       name   a property name.
     * @param       observer   an object waiting for this image to be loaded.
     * @return      the value of the named property.
     * @throws      <code>NullPointerException<code> if the property name is null.
     * @see         java.awt.image.ImageObserver
     * @see         java.awt.Image#UndefinedProperty
     */
    public Object getProperty(String name, ImageObserver observer) {
        return buffer.getProperty(name, observer);
    }

    /**
     * Determines the width of the image. If the width is not yet known,
     * this method returns <code>-1</code> and the specified
     * <code>ImageObserver</code> object is notified later.
     * @param     observer   an object waiting for the image to be loaded.
     * @return    the width of this image, or <code>-1</code>
     *                   if the width is not yet known.
     * @see       java.awt.Image#getHeight
     * @see       java.awt.image.ImageObserver
     */
    public int getWidth(ImageObserver observer) {
        return buffer.getWidth(observer);
    }

    /**
     * Determines the height of the image. If the height is not yet known,
     * this method returns <code>-1</code> and the specified
     * <code>ImageObserver</code> object is notified later.
     * @param     observer   an object waiting for the image to be loaded.
     * @return    the height of this image, or <code>-1</code>
     *                   if the height is not yet known.
     * @see       java.awt.Image#getWidth
     * @see       java.awt.image.ImageObserver
     */
    public int getHeight(ImageObserver observer) {
        return buffer.getHeight(observer);
    }

    public DoomVolatileImage() {
        this(640, 480);
    }

    public DoomVolatileImage(int x, int y) {
        //super(x,y,TYPE_INT_RGB);
    }

    public DoomVolatileImage(int x, int y, int transp) {
        this(x, y);
    }

    public DoomVolatileImage(DoomGraphicsConfiguration gc) {
        this();
    }
}
