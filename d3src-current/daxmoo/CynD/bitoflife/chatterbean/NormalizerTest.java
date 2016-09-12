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

import bitoflife.chatterbean.Normalizer;
import bitoflife.chatterbean.NormalizerMother;

public class NormalizerTest extends TestCase
{
  /*
  Attributes
  */
  
  private static final NormalizerMother mother = new NormalizerMother();
  private Normalizer normalizer;

  /*
  Events
  */
  
  protected void setUp() throws Exception
  {
    normalizer = mother.newInstance();
  }

  protected void tearDown()
  {
    normalizer = null;
  }
  
  /*
  Methods
  */

  public void testSplit()
  {
    String[] entries = normalizer.split(" Hello Alice. How are you? You look fine! Please forgive my manners; I am so happy today... ");

    String[] expected = new String[] {" Hello Alice",
                                      " How are you",
                                      " You look fine",
                                      " Please forgive my manners",
                                      " I am so happy today"};

    for (int i = 0, n = expected.length; i < n; i++)
      assertEquals("Expected entry: " + expected[i] + " Actual entry: " + entries[i], expected[i], entries[i]);
  }

  public void testFit()
  {
      assertEquals("HELLO ALICE CAN YOU HELP ME", normalizer.fit("- HELLO ALICE, CAN   YOU HELP ME"));
  }

  public void testApplyCorrection()
  {
    assertEquals(" I am Hélio ", normalizer.applyCorrection("I am Hélio"));
    assertEquals(" what is a Cancer, Alice ", normalizer.applyCorrection("What's a Cancer, Alice"));
    assertEquals(" what is a Cancer, Alice ", normalizer.applyCorrection("Waht's a Cancer, Alice"));
    assertEquals(" Do you know what time is it ", normalizer.applyCorrection("Do you know waht time is it"));
    assertEquals(" what is your name ", normalizer.applyCorrection("What's your name"));
    assertEquals(" what is your name ", normalizer.applyCorrection("What s your name"));
    assertEquals(" what is your name ", normalizer.applyCorrection("Waht s your name"));
    assertEquals(" I like you smile ", normalizer.applyCorrection("I like you :-)"));
    assertEquals(" Tell me where can I download you smile ", normalizer.applyCorrection("Tell me where can I down load you :-)"));
    assertEquals(" This is what I am ", normalizer.applyCorrection("This is waht iam"));
  }
  
  public void testApplyPunctuation()
  {
    assertEquals(" What is AIML?  ", normalizer.applyPunctuation("What is AIML?"));
    assertEquals(" That is good.  ", normalizer.applyPunctuation("That is good..."));
  }
}
