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

import junit.framework.Test;
import junit.framework.TestSuite;
import junit.swingui.TestRunner;

public class Tests extends TestSuite
{
  /*
  Methods
  */
  
  public static void main(String args[])
  {
    TestRunner.main(new String[] {"-noloading", "bitoflife.chatterbean.Tests"});
  }

  public static Test suite()
  {
    TestSuite suite = new TestSuite("All ChatterBean Tests");

    suite.addTestSuite(bitoflife.chatterbean.AliceBotTest.class);
    suite.addTestSuite(bitoflife.chatterbean.GraphMatcherTest.class);
    suite.addTestSuite(bitoflife.chatterbean.ListMatcherSorterTest.class);
    suite.addTestSuite(bitoflife.chatterbean.LoggerTest.class);
    suite.addTestSuite(bitoflife.chatterbean.NormalizerTest.class);
    suite.addTestSuite(bitoflife.chatterbean.aiml.AIMLHandlerTest.class);
    suite.addTestSuite(bitoflife.chatterbean.aiml.AIMLParserTest.class);
    suite.addTestSuite(bitoflife.chatterbean.aiml.CategoryTest.class);
    suite.addTestSuite(bitoflife.chatterbean.aiml.SystemTest.class);
    suite.addTestSuite(bitoflife.chatterbean.parser.ContextParserTest.class);
    suite.addTestSuite(bitoflife.chatterbean.util.SearcherTest.class);
    suite.addTestSuite(bitoflife.chatterbean.util.SequenceTest.class);

    return suite;
  }
}
