package daxclr.inference;

/**
 * Description TODO
 *
 * @version $Id: PrologClient.java,v 1.9 2002/10/30 15:51:48 stephenreed Exp $
 * @author Douglas R. Miles
 *
 * <p>Copyright 2001 Cycorp, Inc., license is open source GNU LGPL.
 * <p><a href="http://www.opencyc.org/license.txt">the license</a>
 * <p><a href="http://www.opencyc.org">www.opencyc.org</a>
 * <p><a href="http://www.sourceforge.net/projects/opencyc">OpenCyc at SourceForge</a>
 * <p>
 * THIS SOFTWARE AND KNOWLEDGE BASE CONTENT ARE PROVIDED ``AS IS'' AND
 * ANY EXPRESSED OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO,
 * THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A
 * PARTICULAR PURPOSE ARE DISCLAIMED.  IN NO EVENT SHALL THE OPENCYC
 * ORGANIZATION OR ITS CONTRIBUTORS BE LIABLE FOR ANY DIRECT,
 * INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES
 * (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR
 * SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION)
 * HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT,
 * STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE)
 * ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE AND KNOWLEDGE
 * BASE CONTENT, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */

//CycService import java.lang.reflect.*;
import java.io.IOException;
import java.io.StreamTokenizer;
import java.io.StringReader;
import java.util.ArrayList;
import org.opencyc.api.CycAccess;
import org.opencyc.api.CycApiException;
import org.opencyc.api.CycObjectFactory;
import org.opencyc.cycobject.CycConstant;
import org.opencyc.cycobject.CycList;
import org.opencyc.cycobject.CycSymbol;
import org.opencyc.cycobject.CycVariable;
import org.opencyc.util.StackWithPointer;



//CycObjectFactory
public class LispReader extends CycObjectFactory {
    static void fixST(StreamTokenizer st) {
        st.commentChar( ';' );
        st.ordinaryChar( '(' );
        st.ordinaryChar( ')' );
        st.ordinaryChar( '\'' );
        st.ordinaryChar( '`' );
        st.ordinaryChar( '.' );
        st.wordChars( '=', '=' );
        st.wordChars( '+', '+' );
        st.wordChars( '-', '-' );
        st.wordChars( '_', '_' );
        st.wordChars( '<', '<' );
        st.wordChars( '>', '>' );
        st.wordChars( '*', '*' );
        st.wordChars( '/', '/' );
        st.wordChars( '.', '.' );
        st.wordChars( '#', '#' );
        st.wordChars( ':', ':' );
        st.wordChars( '!', '!' );
        st.wordChars( '$', '$' );
        st.wordChars( '?', '?' );
        st.wordChars( '%', '%' );
        st.wordChars( '&', '&' );
        st.wordChars('+', '+');
    }


    // Read/scan functions' lexical analysis variables.
    private boolean endQuote = false;
   // private boolean dot = false;
   // private boolean dotWord = false;
   // private boolean dotParen = false;
    private int parenLevel = 0;
    private StackWithPointer readStack = new StackWithPointer();
    private StackWithPointer quoteStack = new StackWithPointer();

    /**
     * Cyc api support.
     */

    private static final String consMarkerSymbol = "**consMarkerSymbol**";
    private static final int STWORD = StreamTokenizer.TT_WORD;
    private static final int STNUMBER = StreamTokenizer.TT_NUMBER;

    /**
     * Verbosity indicator <tt>0</tt> indicates quiet on a range of
     * <tt>0</tt> ... <tt>10</tt>
     */
    public static int verbosity = 5;

    /**
     * Constructs a new <tt>CycListParser</tt> object.
     */
    public LispReader() {
    }
    public LispReader(CycAccess cycAccess) {
    }

    /**
     * Parses a <tt>CycList</tt> string representation.
     *
     * @param st a <tt>StreamTokenizer</tt> whose source is the
     * <tt>CycList</tt> string representation.
     * @return the corresponding <tt>CycList</tt>
     */
    public Object read(String string) throws CycApiException {
        String tempString = string.replace('\r', ' ').replace('\n', ' ').replace('\t', ' ').replace("  "," ").trim();
        StringReader stringReader = new StringReader(tempString);
        StreamTokenizer st = new StreamTokenizer(stringReader);
        fixST(st);
        return read(st);
    }

    /**
     *  Read and parse a lisp symbolic expression.
     *
     * @param st a <tt>StreamTokenizer</tt> whose source is the
     * <tt>CycList</tt> string representation.
     * @return the corresponding <tt>CycList</tt>
     */
    public Object read (StreamTokenizer st) throws CycApiException {
        fixST(st);
        int tok;
        endQuote = false;

        // Read and parse a lisp symbolic expression.
        try {
            while (true) {
                tok = st.nextToken();
                if (tok == st.TT_EOF) return null;
                if (verbosity > 0)
                    System.out.println("sval: " + st.sval +
                                       "  st: " + st.toString() +
                                       "  tok: " + tok);

                if (endQuote) {
                    // Close a quoted expression by inserting a right paren.
                    endQuote = false;
                    st.pushBack();
                    scanRightParen();
                } else if (tok == st.TT_EOF)
                    break;
                else {
                    switch (tok) {
                        case STWORD:
                            scanWord(st,false);
                            break;
                        case STNUMBER:
                            scanNumber(st, true);
                            break;
                        case 34:    // "
                            scanString(st);
                            break;
                        case 39:    // Quote.
                            scanQuote();
                            continue;
                        case 96:    // Backquote.
                            scanBackquote();
                            continue;
                        case 40:    // Left Paren
                            ScanLeftParen();
                            continue;
                        case 41:    // Right Paren
                            scanRightParen();
                            break;
                        case 44:    // ,
                            scanComma(st);
                            break;
                        case 45:    // -
                            scanMinus();
                            break;
                        case 124:    // |
                            st.nextToken();
                            scanWord(st,true);
                            tok = st.nextToken();
                            break;
                        default:
                            throw new RuntimeException("Invalid symbol: " + st.toString() +  " token: " + tok);
                    }
                }
                if ((readStack.sp > 0) && (parenLevel == 0)) {
                    // Parsed a complete symbolic expression.
                    Object object = readStack.pop();
                    if (object.equals(CycObjectFactory.nil))
                        return new CycList(new ArrayList());
                    else
                        return	reduceDottedPairs( object);
                }
            }
            if (readStack.sp > 0)
                throw new RuntimeException ("Invalid expression, sval: " +
                                            st.sval +
                                            "  st: " +
                                            st.toString() +
                                            "  tok: " +
                                            tok);
        } catch (IOException ioe) {
            throw new RuntimeException(ioe.getMessage());
        }
        throw new RuntimeException("End of stream");
    }

    /**
     * Expands 's to (quote s  when reading.
     */
    private void scanQuote() {
        Integer i;
        if (verbosity > 5)
            System.out.println("'");

        if ((parenLevel > 0) && (parenLevel != readStack.sp))
            readStack.push(consMarkerSymbol);

        readStack.push(consMarkerSymbol);
        quoteStack.push(new Integer(++parenLevel));
        readStack.push(CycObjectFactory.quote);
    }

    /**
     * Expands #'s to (function s  when reading.
     */
    private void scanFunctionQuote() {
        Integer i;

        if (verbosity > 5)
            System.out.println("#'");

        if ((parenLevel > 0) && (parenLevel != readStack.sp))
            readStack.push(consMarkerSymbol);

        readStack.push(consMarkerSymbol);
        quoteStack.push(new Integer(++parenLevel));
        readStack.push(CycObjectFactory.makeCycSymbol("function"));
    }

    /**
     * Scans a left parenthesis when reading.
     */
    private void ScanLeftParen() {
        if (verbosity > 5)
            System.out.println("(");
        // Begin a list.
        readStack.push(consMarkerSymbol );
        ++parenLevel;
    }

    /**
     * Scans a right parenthesis when reading.
     */
    private void scanRightParen() {
        CycConstant cons;
        Object firstElement;
        Object remainingElements;

        if (verbosity > 5)
            System.out.println(")");

        if (parenLevel == 0)
            throw new RuntimeException( "read: Extra right parenthesis" );
        else if ((readStack.sp == parenLevel) &&
                 (readStack.peek().equals(CycObjectFactory.cons)))
            // Have an empty list.
            readStack.pop();

        // Terminate the list.
        readStack.push(CycObjectFactory.nil);
        --parenLevel;

        checkQuotes();

        // Construct the list from cons cells.
        // 'a becomes (1)cons (2)quote (3)cons (4)a (5)nil
        // Transformed to (1) cons  (quote a)

        while (readStack.sp > 2) {
            remainingElements = readStack.pop();
            firstElement = readStack.pop();

            if ((readStack.peek()).equals(consMarkerSymbol) &&
                (! firstElement.equals(consMarkerSymbol)) &&
                (! remainingElements.equals(consMarkerSymbol))) {
                readStack.pop();    // Discard cons marker atom.
                // Replace it with cons cell.
                readStack.push(CycList.construct(firstElement, remainingElements));
            } else {
                // Not a cons, so restore readStack.
                readStack.push(firstElement);
                readStack.push(remainingElements);
                break;
            }
        }
    }

    /**
     * Scans a number while reading.
     *
     * @param the input <tt>StreamTokenizer</tt> from which to get the numerical value.
     */
    private void scanNumber(StreamTokenizer st, boolean positive) {
        Double doubleNumber;
        Integer integerNumber;
        Long longNumber;
        Object number = null;

        if (verbosity > 5)
            System.out.println(st.nval );
        // Try representing the scanned number as both java double and long.
        if (positive) {
            doubleNumber = new Double (st.nval);
            integerNumber = new Integer(doubleNumber.intValue());
            longNumber = new Long(doubleNumber.longValue());
        } else {
            doubleNumber = new Double (- st.nval);
            integerNumber = new Integer(- doubleNumber.intValue());
            longNumber = new Long(- doubleNumber.longValue());
        }


        if (integerNumber.doubleValue() == doubleNumber.doubleValue())
            // Choose integer if no loss of accuracy.
            number = integerNumber;
        else if (longNumber.doubleValue() == doubleNumber.doubleValue())
            number = longNumber;
        else
            number = doubleNumber;

        if (( parenLevel > 0 ) && ( parenLevel != readStack.sp ))
            // Within a list.
            readStack.push( consMarkerSymbol );

        readStack.push(number);
        checkQuotes();
    }

    /**
     * Scans a minus while reading.
     */
    private void scanMinus() {
        if (verbosity > 5)
            System.out.println("-");
        CycSymbol w = CycObjectFactory.makeCycSymbol("-");

        if (( parenLevel > 0 ) && ( readStack.sp != parenLevel ))
            // Within a list.
            readStack.push(consMarkerSymbol);

        readStack.push(w);
        checkQuotes();
    }

    /**
     * Scans a backquote while reading.
     */
    private void scanBackquote() {
        if (verbosity > 5)
            System.out.println("`");
        CycSymbol w = CycObjectFactory.makeCycSymbol("`");

        if (( parenLevel > 0 ) && ( readStack.sp != parenLevel ))
            // Within a list.
            readStack.push(consMarkerSymbol);

        readStack.push(w);
        checkQuotes();
    }

    /**
     * Scans a comma while reading.
     */
    private void scanComma(StreamTokenizer st) throws IOException {
        CycSymbol w;
        if (st.nextToken() == '@') {
            if (verbosity > 5)
                System.out.println(",@");
            w = CycObjectFactory.makeCycSymbol(",@");
        } else {
            if (verbosity > 5)
                System.out.println(",");
            w = CycObjectFactory.makeCycSymbol(",");
        }
        st.pushBack();

        if (( parenLevel > 0 ) && ( readStack.sp != parenLevel ))
            // Within a list.
            readStack.push(consMarkerSymbol);

        readStack.push(w);
        checkQuotes();
    }



    /**
     * Scans a word while reading.
     *
     * @param the input <tt>StreamTokenizer</tt> from which to get the word value.
     */
    private void scanWord(StreamTokenizer st, boolean casePreserve)
    throws IOException, CycApiException {
        if (verbosity > 5)
            System.out.println("scanWord: \""+st.sval+"\" " + casePreserve);
        Object w = null;
        char firstchar = st.sval.charAt(0);
        String substr = st.sval.substring(1);
        switch (firstchar) {
            case '!': {
                    if (!casePreserve) substr = substr.toUpperCase();
                    w = CycObjectFactory.makeCycSymbol(null,substr);
                }
                break;
            case ':': {
                    if (!casePreserve) substr = substr.toUpperCase();
                    w = CycObjectFactory.getCycVariableCache(substr);
                    if (w==null) {
                        if (!casePreserve) st.sval = st.sval.toUpperCase();
                        w = CycObjectFactory.makeCycSymbol(null,st.sval);
                    }
                }
                break;
            case '?': {
                    if (!casePreserve) substr = substr.toUpperCase();
                    w = CycObjectFactory.getCycVariableCache(substr);
                    if (w==null) {
                        // if HL variable
                        if (substr.startsWith("VAR")) {
                            w = new CycVariable(substr,new Integer(substr.substring(3)));
                            CycObjectFactory.addCycVariableCache((CycVariable)w);
                        } else {
                            // EL variable
                            if (!casePreserve) st.sval = st.sval.toUpperCase();
                            w = CycObjectFactory.makeCycVariable(st.sval);
                            // Dontcare variable
                            if (substr.charAt(0)=='?') {
                                w = CycObjectFactory.makeUniqueCycVariable((CycVariable)w);
                            }
                        }
                    }
                }
                break;
            case '#': {
                    if (substr.charAt(0)=='$') {
                        // #$constant
                        w = daxclr.inference.CycAPI.current().c(substr.substring(1));
                    } else if (st.sval.equals("#")) {
                        int nextTok = st.nextToken();
                        // #' function
                        if (nextTok == 39) {
                            scanFunctionQuote();
                            return;
                        } else {
                            // just a "#"
                            st.pushBack();
                            w = CycObjectFactory.makeCycSymbol(null,st.sval);
                        }
                    } else {
                        // #< ?
                        if (!casePreserve) st.sval = st.sval.toUpperCase();
                        w = CycObjectFactory.makeCycSymbol(null,st.sval);
                    }
                }
                break;
            case '-': {
                    if (st.sval.equals("-")) {
                        int nextTok = st.nextToken();
                        st.pushBack();
                        if (nextTok == STNUMBER) {
                            scanNumber(st, false);
                            return;
                        }
                    }
                    if (!casePreserve) st.sval = st.sval.toUpperCase();
                    w = CycObjectFactory.makeCycSymbol(null,st.sval);
                }
                break;
            default: {
                    if (!casePreserve) st.sval = st.sval.toUpperCase();
                    w = CycObjectFactory.makeCycSymbol(null,st.sval);
                }
        }
        // Within a list.
        if ((parenLevel > 0) && (readStack.sp != parenLevel))
            readStack.push(consMarkerSymbol );
        readStack.push(w);
        checkQuotes();
    }

    /**
     * Scans a string while reading.
     */
    private void scanString(StreamTokenizer st) {
        String string = new String(st.sval);
        String line1;
        String line2;
        int index;

        //Replace `~ combination with crlf since StreamTokenizer cannot
        //span multiple lines.

        while (true) {
            index = string.indexOf("`~");
            if (index == -1)
                break;

            line1 = new String (string.substring( 0, index ));
            line2 = new String ( string.substring( index + 2 ));
            string = line1 + "\r\n" + line2;
        }

        if (verbosity > 5)
            System.out.println(st.sval );
        if (( parenLevel > 0 ) && ( readStack.sp != parenLevel ))
            // Within a list.
            readStack.push(consMarkerSymbol );

        readStack.push(string);
        checkQuotes();
    }

    /**
     * Read/Scan helper routine to check for the end of quoted forms.
     */
    private void checkQuotes() {
        if ((! quoteStack.empty()) &&
            (( (Integer) quoteStack.peek()).intValue() == parenLevel )) {
            quoteStack.pop();
            endQuote = true;
        }
    }

    /**
     * Performs a lexical analysis of the list and perform dot
     * cons cell operations.
     *
     * @param the <tt>Object</tt> under consideration.
     * @return the input <tt>Object</tt> if not a <tt>CycList</tt>, otherwise
     * reduce the dotted pairs in the list if possible.
     */
    private Object reduceDottedPairs (Object s ) {
        if (! (s instanceof CycList))
            return s;
        CycList cycList = (CycList) s;
        if (cycList.size() == 0)
            return s;
        else if (cycList.size() == 3 &&
                 cycList.second().equals(CycObjectFactory.dot)) {
            Object first = reduceDottedPairs(cycList.first());
            Object third = reduceDottedPairs(cycList.third());
            if (cycList.third() instanceof CycList) {
                // Replace list (a . (b)) with list (a b)
                CycList reducedCycList = new CycList(first);
                reducedCycList.addAll((CycList) third);
                if (! ((CycList) third).isProperList())
                    reducedCycList.setDottedElement(((CycList) third).getDottedElement());
                return reducedCycList;
            } else {
                // Mark list (a . b) as improper and remove the dot symbol.
                CycList improperList = new CycList(first);
                improperList.setDottedElement(third);
                return improperList;
            }
        }
        Object firstReducedDottedPair = reduceDottedPairs(cycList.first());
        Object restReducedDottedPair = reduceDottedPairs(cycList.rest());
        CycList constructedCycList = CycList.construct(firstReducedDottedPair, restReducedDottedPair);
        return constructedCycList;
    }


}




