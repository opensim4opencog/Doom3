package daxclr.doom.ui;

import java.awt.GraphicsConfiguration;
import java.awt.GraphicsDevice;

public class DoomGraphicsDevice extends GraphicsDevice {

    DoomGraphicsConfiguration[] configs = null;

    public DoomGraphicsDevice(DoomGraphicsConfiguration cf) {
        super();
        configs = new DoomGraphicsConfiguration[] {cf};
    }

    /**
     * Returns the type of this <code>DoomGraphicsDevice</code>.
     * @return the type of this <code>DoomGraphicsDevice</code>, which can
     * either be TYPE_RASTER_SCREEN, TYPE_PRINTER or TYPE_IMAGE_BUFFER.
     * @see #TYPE_RASTER_SCREEN
     * @see #TYPE_PRINTER
     * @see #TYPE_IMAGE_BUFFER
     */
    public int getType() {
        return TYPE_RASTER_SCREEN;
    }

    /**
     * Returns the identification string associated with this
     * <code>DoomGraphicsDevice</code>.
     * <p>
     * A particular program might use more than one
     * <code>DoomGraphicsDevice</code> in a <code>DoomGraphicsEnvironment</code>.
     * This method returns a <code>String</code> identifying a
     * particular <code>DoomGraphicsDevice</code> in the local
     * <code>DoomGraphicsEnvironment</code>.  Although there is
     * no public method to set this <code>String</code>, a programmer can
     * use the <code>String</code> for debugging purposes.  Vendors of
     * the Java<sup><font size=-2>TM</font></sup> Runtime Environment can
     * format the return value of the <code>String</code>.  To determine
     * how to interpret the value of the <code>String</code>, contact the
     * vendor of your Java Runtime.  To find out who the vendor is, from
     * your program, call the
     * {@link System#getProperty(String) getProperty} method of the
     * System class with "java.vendor".
     * @return a <code>String</code> that is the identification
     * of this <code>DoomGraphicsDevice</code>.
     */
    public String getIDstring() {
        return this.getClass().getName();
    }

    /**
     * Returns all of the <code>DoomGraphicsConfiguration</code>
     * objects associated with this <code>DoomGraphicsDevice</code>.
     * @return an array of <code>DoomGraphicsConfiguration</code>
     * objects that are associated with this
     * <code>DoomGraphicsDevice</code>.
     */
    public GraphicsConfiguration[] getConfigurations() {
        return configs;
    }

    /**
     * Returns the default <code>DoomGraphicsConfiguration</code>
     * associated with this <code>DoomGraphicsDevice</code>.
     * @return the default <code>DoomGraphicsConfiguration</code>
     * of this <code>DoomGraphicsDevice</code>.
     */
    public GraphicsConfiguration getDefaultConfiguration() {
        return configs[0];
    }
}
