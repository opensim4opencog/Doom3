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

import java.io.Reader;
import java.io.StringReader;
import junit.framework.TestCase;
import bitoflife.chatterbean.Context;

public class ContextParserTest extends TestCase
{
  /*
  Attributes
  */
  
  private static final String xml =
    "<?xml version=\"1.0\" encoding=\"UTF-8\"?>" +
    "<context>" +
      "<bot name=\"date.format\" value=\"yyyy-MM-dd HH:mm:ss\"/>" +
      "<bot name=\"output.file\" value=\"Logs/\"/>" +
      "<set name=\"user\" value=\"Unknown Person\"/>" +
      "<set name=\"me\" value=\"Alice\"/>" +
      "<set name=\"engine\" value=\"ChatterBean\"/>" +
    "</context>";

  private Reader source;
  
  private ContextParser parser;
  
  /*
  Events
  */
  
  public void setUp() throws Exception
  {
    source = new StringReader(xml);
    parser = new ContextParser();
  }
    
  /*
  Methods
  */
  
  public void testParse() throws Exception
  {
    Context context = parser.parse(source);
    assertEquals("Unknown Person", context.getPredicate("user"));
    assertEquals("Alice", context.getPredicate("me"));
    assertEquals("ChatterBean", context.getPredicate("engine"));
    assertEquals("yyyy-MM-dd HH:mm:ss", context.getBotPredicate("date.format"));
    assertEquals("Logs/", context.getBotPredicate("output.file"));
  }
}
