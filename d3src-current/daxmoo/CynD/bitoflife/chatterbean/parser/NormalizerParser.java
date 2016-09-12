/*
Copyleft (C) 2005 Hélio Perroni Filho
xperroni@yahoo.com
ICQ: 2490863

This file is part of ChatterBean.

ChatterBean is free software; you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation; either version 2 of the License, or (at your option) any later version.

ChatterBean is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for more details.

You should have received a copy of the GNU General Public License along with ChatterBean (look at the Documents/ directory); if not, either write to the Free Software Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307 USA, or visit (http://www.gnu.org/licenses/gpl.txt).
*/

package bitoflife.chatterbean.parser;


import java.io.IOException;
import java.io.Reader;
import java.util.List;
import java.util.Map;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

import bitoflife.chatterbean.Normalizer;

public class NormalizerParser
{
  /*
  Attributes
  */

  private final SubstitutionBuilder substBuilder = new SubstitutionBuilder();
  private final ReflectionHandler substHandler = new ReflectionHandler(substBuilder);
  private final SplitterHandler splitHandler = new SplitterHandler();
  
  private SAXParser parser;

  /*
  Constructor
  */
  
  public NormalizerParser() throws ParserConfigurationException, SAXException
  {
    parser = SAXParserFactory.newInstance().newSAXParser();
  }
  
  
  /*
  Methods
  */

  private List<String> parseSplitters(Reader splitters) throws IOException, SAXException
  {
    splitHandler.clear();
    parser.parse(new InputSource(splitters), splitHandler);
    return splitHandler.parsed();
  }

  private Map<String, Map<String, String>> parseSubstitutions(Reader substitutions) throws IOException, SAXException
  {
    substBuilder.clear();
    parser.parse(new InputSource(substitutions), substHandler);
    return substBuilder.parsed();
  }
  
  public Normalizer parse(Reader splitters, Reader substitutions) throws IOException, SAXException
  {
    return new Normalizer(parseSplitters(splitters), parseSubstitutions(substitutions));
  }
}
