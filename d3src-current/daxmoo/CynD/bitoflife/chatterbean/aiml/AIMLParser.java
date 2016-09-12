/*
Copyleft (C) 2005 Hélio Perroni Filho
xperroni@yahoo.com
ICQ: 2490863

This file is part of ChatterBean.

ChatterBean is free software; you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation; either version 2 of the License, or (at your option) any later version.

ChatterBean is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for more details.

You should have received a copy of the GNU General Public License along with ChatterBean (look at the Documents/ directory); if not, either write to the Free Software Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307 USA, or visit (http://www.gnu.org/licenses/gpl.txt).
*/

package bitoflife.chatterbean.aiml;

import java.io.Reader;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;
import org.xml.sax.InputSource;

import bitoflife.chatterbean.Matcher;
import bitoflife.chatterbean.util.Searcher;

public class AIMLParser {
    /*
    Attributes
    */

    private final Searcher searcher = new Searcher();
    private final AIMLHandler handler = new AIMLHandler();
    private SAXParser parser;

    /*
    Constructor
    */

    public AIMLParser() throws AIMLParserConfigurationException
    {
        try {
            parser = SAXParserFactory.newInstance().newSAXParser();
        } catch (Exception e) {
            throw new AIMLParserConfigurationException(e);
        }
    }

    /*
    Methods
    */

    public void parse(Matcher matcher, Reader... sources) throws AIMLParserException
    {
        try {
            int i=0;
            for (Reader aiml : sources) {
                java.lang.System.out.println(" Reading File "+i   );
                parser.parse(new InputSource(aiml), handler);
                i++;
            }
            matcher.add(handler.unload());
        } catch (Exception e) {
            if (e instanceof org.xml.sax.SAXParseException) {
                org.xml.sax.SAXParseException se =(org.xml.sax.SAXParseException)e;
                String reason = "line="+se.getLineNumber()+" col="+se.getColumnNumber()+ " id="+se.getPublicId();
                java.lang.System.out.println(reason);
            }
            throw new AIMLParserException(e);

        }
    }
}