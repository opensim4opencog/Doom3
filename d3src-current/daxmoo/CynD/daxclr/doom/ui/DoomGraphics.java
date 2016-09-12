package daxclr.doom.ui;

/**
 * The <code>Graphics</code> class is the abstract base class for
 * all graphics contexts that allow an application to draw onto
 * components that are realized on various devices, as well as
 * onto off-screen images.
 * <p>
 * A <code>Graphics</code> object encapsulates state information needed
 * for the basic rendering operations that Java supports.  This
 * state information includes the following properties:
 * <p>
 * <ul>
 * <li>The <code>Component</code> object on which to draw.
 * <li>A translation origin for rendering and clipping coordinates.
 * <li>The current clip.
 * <li>The current color.
 * <li>The current font.
 * <li>The current logical pixel operation function (XOR or Paint).
 * <li>The current XOR alternation color
 *     (see {@link Graphics#setXORMode}).
 * </ul>
 * <p>
 * Coordinates are infinitely thin and lie between the pixels of the
 * output device.
 * Operations that draw the outline of a figure operate by traversing
 * an infinitely thin path between pixels with a pixel-sized pen that hangs
 * down and to the right of the anchor point on the path.
 * Operations that fill a figure operate by filling the interior
 * of that infinitely thin path.
 * Operations that render horizontal text render the ascending
 * portion of character glyphs entirely above the baseline coordinate.
 * <p>
 * The graphics pen hangs down and to the right from the path it traverses.
 * This has the following implications:
 * <p><ul>
 * <li>If you draw a figure that covers a given rectangle, that
 * figure occupies one extra row of pixels on the right and bottom edges
 * as compared to filling a figure that is bounded by that same rectangle.
 * <li>If you draw a horizontal line along the same <i>y</i> coordinate as
 * the baseline of a line of text, that line is drawn entirely below
 * the text, except for any descenders.
 * </ul><p>
 * All coordinates that appear as arguments to the methods of this
 * <code>Graphics</code> object are considered relative to the
 * translation origin of this <code>Graphics</code> object prior to
 * the invocation of the method.
 * <p>
 * All rendering operations modify only pixels which lie within the
 * area bounded by the current clip, which is specified by a {@link Shape}
 * in user space and is controlled by the program using the
 * <code>Graphics</code> object.  This <i>user clip</i>
 * is transformed into device space and combined with the
 * <i>device clip</i>, which is defined by the visibility of windows and
 * device extents.  The combination of the user clip and device clip
 * defines the <i>composite clip</i>, which determines the final clipping
 * region.  The user clip cannot be modified by the rendering
 * system to reflect the resulting composite clip. The user clip can only
 * be changed through the <code>setClip</code> or <code>clipRect</code>
 * methods.
 * All drawing or writing is done in the current color,
 * using the current paint mode, and in the current font.
 *
 * @version 	1.69, 05/18/04
 * @author 	Sami Shaio
 * @author 	Arthur van Hoff
 * @see     java.awt.Component
 * @see     java.awt.Graphics#clipRect(int, int, int, int)
 * @see     java.awt.Graphics#setColor(java.awt.Color)
 * @see     java.awt.Graphics#setPaintMode()
 * @see     java.awt.Graphics#setXORMode(java.awt.Color)
 * @see     java.awt.Graphics#setFont(java.awt.Font)
 * @since       JDK1.0
 */
import java.awt.Canvas;
import java.awt.Graphics2D;

abstract public class DoomGraphics extends Graphics2D {

    public DoomGraphics() {
        super();
    }

/*

I have defined a class that calls my native methods that show up on my proprietray graphics device that implements these methods:

void clearScreen(); // Writes the background color (black) from 0,0-Width,Height
void setPoint(Point2D xy, Color c);  //Sets the pixel at x/y to Color c 
int getWidth(); //Returns the 0-Width Visible Area
int getHeight(); //Returns the 0-Height Visible Area
Color getPoint(Point2D xy); // Returns the color of this point
InputStream getKeypad(); // Returns an InputStream that tells what keys have been pressed
Point2D getPointerXY(); // screen position the pointer is located
int getPointerButtons(); //returns the number of buttons (3) 
boolean getPointerButtonState(int buttonnumber); //retreives button state (down=true,up=false)


I would like Swing to write on my class instead of my monitor.
Is this possible?
What would I need to do?

I don't mind making my MouseDevice more statefull/friendly what interface should i implement for it?


Thank you in advance~!

PS:                          
My tech level in java is pretty high.. but my problem in figuring this out myself.. lead me to these Questions:
Do i subclass VolatileImage.. How do I make it use my volatile image instead of its own.. 
Ok, if i subclass GraphicsConfiguration, don't i need to write my own Graphics class.. 
Ok if i subclass Graphics, do i need to implement rectangle and circle fills?.. 
Why Cant i just get away with being the target bitmap space for the existing Graphics object.. .. 
Possible i am sure.. ok am i a Cavas,Window,VolatileImage,BufferedImage,PrinterDevice,Raster? grr!

*/
    public static class DoomCanvas extends Canvas {

		/**
		 * 
		 */
		private static final long serialVersionUID = -8003565729796706148L;
    }

}
