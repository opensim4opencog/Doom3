/*
Copyleft (C) 2005 Hélio Perroni Filho
xperroni@yahoo.com
ICQ: 2490863

This file is part of ChatterBean.

ChatterBean is free software; you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation; either version 2 of the License, or (at your option) any later version.

ChatterBean is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for more details.

You should have received a copy of the GNU General Public License along with ChatterBean (look at the Documents/ directory); if not, either write to the Free Software Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307 USA, or visit (http://www.gnu.org/licenses/gpl.txt).
*/

package bitoflife.chatterbean;

import junit.framework.TestCase;
import bitoflife.chatterbean.aiml.Category;
import bitoflife.chatterbean.aiml.Pattern;
import bitoflife.chatterbean.aiml.Template;
import bitoflife.chatterbean.aiml.That;
import bitoflife.chatterbean.aiml.Topic;

public class ListMatcherSorterTest extends TestCase
{
  /*
  Attributes
  */

  private ListMatcherSorter sorter;
  
  /*
  Events
  */
  
  protected void setUp()
  {
    sorter = new ListMatcherSorter();
  }

  protected void tearDown()
  {
    sorter = null;
  }
  
  /*
  Methods
  */

  /**
  Validates the Comparable interface implementation for the Pattern class. The tests prove that the behaviours demanded by the contract of the Comparable interface, as found in the Java (TM) 2 Platform Std. Ed. v1.4.2 Javadocs, were correctly implemented.
  */
  public void testCompare()
  {
    // An ordered set of Categories.
    Category c0 = new Category(new Pattern(" I AM CALLED _ "), new Template("Whatever.")); // These two are actually
    Category c1 = new Category(new Pattern(" I SEE THE _ "), new Template("Whatever."));   // the same order.
    Category c2 = new Category(new Pattern(" I SEE THE * IN YOUR EYES "), new Template("Whatever."));
    Category c3 = new Category(new Pattern(" I SEE THE * "), new Template("Whatever."));
    Category c4 = new Category(new Pattern(" * "), new Template("Whatever."));
    Category c5 = new Category(new Pattern(" YES "),
                               new That(" DO YOU LIKE CHEESE "),
                               new Topic("*"),
                               new Template("Good for you."));
    Category c6 = new Category(new Pattern(" YES "), new Template("\"Yes\" what?"));

    // sgn(x.compareTo(y)) == -sgn(y.compareTo(x)) for all x and y
    assertEquals(sorter.compare(c1, c2), -sorter.compare(c2, c1));
    assertEquals(sorter.compare(c3, c2), -sorter.compare(c2, c3));
    
    // if (x.compareTo(y) > 0 && y.compareTo(z) > 0) then x.compareTo(z) > 0
    assertTrue(sorter.compare(c3, c2) > 0);
    assertTrue(sorter.compare(c2, c1) > 0);
    assertTrue(sorter.compare(c3, c1) > 0);
    
    // if (x.compareTo(y) == 0) then sgn(x.compareTo(z)) == sgn(y.compareTo(z))
    assertTrue(sorter.compare(c0, c1) == 0);
    assertTrue(sorter.compare(c0, c2) == sorter.compare(c1, c2));
    
    // "*" is the biggest (that is, that last) pattern there is.
    assertTrue(sorter.compare(c4, c0) > 0);
    assertTrue(sorter.compare(c4, c1) > 0);
    assertTrue(sorter.compare(c4, c2) > 0);
    assertTrue(sorter.compare(c4, c3) > 0);
    
    // Ordering must take the That into account.
    assertTrue(sorter.compare(c5, c6) < 0);
  }
}
