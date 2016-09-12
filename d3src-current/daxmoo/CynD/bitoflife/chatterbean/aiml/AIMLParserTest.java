/*
Copyleft (C) 2005 Hélio Perroni Filho
xperroni@bol.com.br
ICQ: 2490863

This file is part of ChatterBean.

ChatterBean is free software; you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation; either version 2 of the License, or (at your option) any later version.

ChatterBean is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for more details.

You should have received a copy of the GNU General Public License along with ChatterBean (look at the Documents/ directory); if not, either write to the Free Software Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307 USA, or visit (http://www.gnu.org/licenses/gpl.txt).
*/

package bitoflife.chatterbean.aiml;

import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import javax.xml.parsers.ParserConfigurationException;
import org.xml.sax.SAXException;

import junit.framework.TestCase;

import bitoflife.chatterbean.Context;
import bitoflife.chatterbean.Matcher;
import bitoflife.chatterbean.Sentence;
import bitoflife.chatterbean.util.Searcher;

public class AIMLParserTest extends TestCase
{
  /*
  Inner Classes
  */
  
  public class MockMatcher implements Matcher
  {
    /*
    Attributes
    */
    
    public List<Category> categories;

    /*
    Methods
    */
    
    public void add(List<Category> categories)
    {
       this.categories = categories;
    }

    public Category match(Sentence sentence)
    {
      return null;
    }
      
    public int size()
    {
      return categories.size();
    }
  }

  /*
  Attributes
  */

  private AIMLParser loader;
  
  /*
  Events
  */

  protected void setUp() throws Exception
  {
    loader = new AIMLParser();
  }

  protected void tearDown()
  {
    loader = null;
  }
  
  /*
  Methods
  */

  public void testParse() throws Exception
  {
    Searcher searcher = new Searcher();
    AIMLParser parser = new AIMLParser();
    MockMatcher mock = new MockMatcher();
    parser.parse(mock, new FileReader("Bots/Alice/Again.aiml"));

    Category category = null;
    Map<Pattern, Category> categories = new HashMap<Pattern, Category>();
    for(Iterator<Category> i = mock.categories.iterator(); i.hasNext();)
    {
      category = i.next();
      categories.put(category.getPattern(), category);
    }

    /* Categories from the Again.aiml file. */
    category = new Category(new Pattern(" _ AGAIN "), new Template(new Text("Once more? "), new Srai(1)));
    Category actual = categories.get(category.getPattern());
    assertNotNull(actual);
    assertEquals(category, actual);

    category = new Category(new Pattern(" _ ALICE "), new Template(new Srai(1)));
    assertEquals(category, categories.get(category.getPattern()));

    category = new Category(new Pattern(" YOU MAY * "), new Template(new Srai(1)));
    assertEquals(category, categories.get(category.getPattern()));

    category = new Category(new Pattern(" SAY * "), new Template(new Text("\""), new Star(1), new Text("\".")));
    assertEquals(category, categories.get(category.getPattern()));
  }
}
