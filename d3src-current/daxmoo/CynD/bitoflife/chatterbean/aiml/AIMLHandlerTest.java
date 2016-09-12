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

import static java.net.InetAddress.getLocalHost;
import junit.framework.TestCase;

import org.xml.sax.helpers.AttributesImpl;

import bitoflife.chatterbean.AliceBotMother;
import bitoflife.chatterbean.ChatterBean;

public class AIMLHandlerTest extends TestCase
{
  /*
  Attributes
  */
  
  private AIMLHandler handler;
  private AIMLStack stack;
  
  private AliceBotMother aliceBot = new AliceBotMother();

  /*
  Events
  */

  protected void setUp() throws Exception
  {
    handler = new AIMLHandler();
    stack = handler.stack;
  }

  protected void tearDown()
  {
    handler = null;
  }

  /*
  Methods
  */
  
  private char[] toCharArray(String string)
  {
    int n = string.length();
    char[] chars = new char[n];
    string.getChars(0, n, chars, 0);
    return chars;
  }

  public void testAiml() throws Exception
  {
    char[] text;
    AttributesImpl attributes = new AttributesImpl();
    AttributesImpl aimlAtts = new AttributesImpl();
    aimlAtts.addAttribute(null, "version", null, "String", "1.0.1");
    handler.startElement(null, null, "aiml", aimlAtts);
      handler.startElement(null, null, "category", attributes);
        handler.startElement(null, null, "pattern", attributes);
          handler.characters(text = toCharArray("HELLO ALICE I AM *"), 0, text.length);
        handler.endElement(null, null, "pattern");
        handler.startElement(null, null, "template", attributes);
          handler.characters(text = toCharArray("Hello "), 0, text.length);
          handler.startElement(null, null, "star", attributes);
          handler.characters(text = toCharArray(", nice to meet you."), 0, text.length);
        handler.endElement(null, null, "template");
      handler.endElement(null, null, "category");
    handler.endElement(null, null, "aiml");

    Aiml actual = (Aiml) stack.peek();
    Aiml expected = new Aiml(new Category(new Pattern("HELLO ALICE I AM *"),
                                          new Template("Hello ",
                                                       new Star(1),
                                                       ", nice to meet you.")));
    assertEquals(expected, actual);
    assertEquals("1.0.1", actual.getVersion());
  }

  public void testBot() throws Exception
  {
    AttributesImpl attributes = new AttributesImpl();
    attributes.addAttribute(null, "name", null, "String", "bot.predicate1");
    handler.startElement(null, null, "bot", attributes);

    Bot expected = new Bot("bot.predicate1");
    Bot actual   = (Bot) stack.peek();
    assertEquals(expected, actual);
    
    Bot bot2 = new Bot("bot.predicate2");
    assertFalse(bot2.equals(actual));
  }

  public void testCategory() throws Exception
  {
    char[] text;
    AttributesImpl attributes = new AttributesImpl();
    handler.startElement(null, null, "category", attributes);
      handler.startElement(null, null, "pattern", attributes);
        handler.characters(text = toCharArray("HELLO ALICE I AM *"), 0, text.length);
      handler.endElement(null, null, "pattern");
      handler.startElement(null, null, "template", attributes);
        handler.characters(text = toCharArray("Hello "), 0, text.length);
        handler.startElement(null, null, "star", attributes);
        handler.characters(text = toCharArray(", nice to meet you."), 0, text.length);
      handler.endElement(null, null, "template");
    handler.endElement(null, null, "category");

    Category actual = (Category) stack.peek();
    Category expected = new Category(new Pattern("HELLO ALICE I AM *"),
                                     new Template("Hello ", new Star(1), ", nice to meet you."));
    assertEquals(expected, actual);
  }

  public void testDate() throws Exception
  {
    AttributesImpl attributes = new AttributesImpl();
    handler.startElement(null, null, "date", attributes);

    Date expected = new Date();
    Date actual = (Date) stack.peek();
    assertEquals(expected, actual);
  }

  public void testFormal() throws Exception
  {
    char[] text;
    AttributesImpl attributes = new AttributesImpl();
    handler.startElement(null, null, "formal", attributes);
      handler.characters(text = toCharArray("change this input case to title case"), 0, text.length);
    handler.endElement(null, null, "formal");

    Formal expected = new Formal("change this input case to title case");
    Formal actual   = (Formal) stack.peek();
    assertEquals(expected, actual);

    assertEquals("Change This Input Case To Title Case", actual.toString(null));
  }

  public void testGet() throws Exception
  {
    AttributesImpl attributes = new AttributesImpl();
    attributes.addAttribute(null, "name", null, "String", "predicate1");
    handler.startElement(null, null, "get", attributes);

    Get tag = (Get) stack.peek();
    assertEquals(new Get("predicate1"), tag);
  }

  public void testId() throws Exception
  {
    AttributesImpl attributes = new AttributesImpl();
    handler.startElement(null, null, "id", attributes);

    Id expected = new Id();
    Id actual = (Id) stack.peek();
    assertEquals(expected, actual);
    assertEquals(getLocalHost().getHostName(), actual.toString(null));
  }

  public void testJavascript() throws Exception
  {
    char[] text;
    AttributesImpl attributes = new AttributesImpl();
    handler.startElement(null, null, "javascript", attributes);
      handler.characters(text = toCharArray("Anything can go here"), 0, text.length);
    handler.endElement(null, null, "javascript");

    Javascript expected = new Javascript("Anything can go here");
    Javascript actual = (Javascript) stack.peek();
    assertEquals(expected, actual);
    assertFalse(expected.equals(new Javascript("Anything else can go here")));

    assertEquals("", actual.toString(null));
  }

  public void testLowercase() throws Exception
  {
    char[] text;
    AttributesImpl attributes = new AttributesImpl();
    handler.startElement(null, null, "lowercase", attributes);
      handler.characters(text = toCharArray("CONVERT THIS TO LOWERCASE"), 0, text.length);
    handler.endElement(null, null, "lowercase");

    Lowercase expected = new Lowercase("CONVERT THIS TO LOWERCASE");
    Lowercase actual = (Lowercase) stack.peek();
    assertEquals(expected, actual);

    assertEquals("convert this to lowercase", actual.toString(null));
  }

  public void testPattern() throws Exception
  {
    char[] text;
    AttributesImpl attributes = new AttributesImpl();
    handler.startElement(null, null, "pattern", attributes);
    handler.characters(text = toCharArray("HELLO ALICE"), 0, text.length);
    handler.endElement(null, null, "pattern");

    Pattern expected = new Pattern(" HELLO ALICE ");
    Pattern actual = (Pattern) stack.peek();
    
    assertEquals(expected, actual);
    assertEquals(expected.hashCode(), actual.hashCode());
  }

  public void testSentence() throws Exception
  {
    char[] text;
    AttributesImpl attributes = new AttributesImpl();
    handler.startElement(null, null, "sentence", attributes);
      handler.characters(text = toCharArray("testing sentence... will this work? hope so! let's see."), 0, text.length);
    handler.endElement(null, null, "sentence");

    Sentence expected = new Sentence("testing sentence... will this work? hope so! let's see.");
    Sentence actual   = (Sentence) stack.peek();
    assertEquals(expected, actual);

    assertEquals("Testing sentence... Will this work? Hope so! Let's see.", actual.toString(null));
  }
  
  public void testSet() throws Exception
  {
    char[] text;
    AttributesImpl attributes = new AttributesImpl();
    attributes.addAttribute(null, "name", null, "String", "predicate1");
    handler.startElement(null, null, "set", attributes);
    handler.characters(text = toCharArray("value"), 0, text.length);
    handler.endElement(null, null, "set");

    Set expected = new Set("predicate1", "value");
    Set actual = (Set) stack.peek();
    assertEquals(expected, actual);
  }

  public void testSize() throws Exception
  {
    AttributesImpl attributes = new AttributesImpl();
    handler.startElement(null, null, "size", attributes);

    Size expected = new Size();
    Size actual = (Size) stack.peek();
    assertEquals(expected, actual);
  }
  
  public void testSr() throws Exception
  {
    AttributesImpl attributes = new AttributesImpl();
    handler.startElement(null, null, "sr", attributes);
    Srai srai = (Srai) stack.peek();
    assertEquals(new Srai(1), srai);
  }
  
  public void testSrai() throws Exception
  {
    AttributesImpl attributes = new AttributesImpl();

    handler.startElement(null, null, "srai", attributes);
    handler.startElement(null, null, "star", attributes);
    handler.endElement(null, null, "srai");
    
    Srai srai = (Srai) stack.peek();
    assertEquals(new Srai(1), srai);
  }
  
  public void testStar() throws Exception
  {
    AttributesImpl attributes = new AttributesImpl();
    handler.startElement(null, null, "star", attributes);
    
    assertEquals(new Star(1), stack.peek());

    attributes.addAttribute(null, "index", null, "String", "2");    
    handler.startElement(null, null, "star", attributes);
    assertEquals(new Star(2), stack.peek());
  }
  
  public void testSystem() throws Exception
  {
    char[] text;
    AttributesImpl attributes = new AttributesImpl();
    handler.startElement(null, null, "system", attributes);
    handler.characters(text = toCharArray("system = \"Hello System!\""), 0, text.length);
    handler.endElement(null, null, "system");
    
    System tag = (System) stack.peek();
    assertEquals(new System("system = \"Hello System!\""), tag);
  }
  
  public void testTemplate() throws Exception
  {
    char[] text;
    AttributesImpl attributes = new AttributesImpl();
    handler.startElement(null, null, "template", attributes);
    handler.characters(text = toCharArray("Hello "), 0, text.length);
    handler.startElement(null, null, "star", attributes);
    handler.characters(text = toCharArray(", nice to meet you, too."), 0, text.length);
    handler.endElement(null, null, "template");

    Template tag = (Template) stack.peek();
    assertEquals(new Template("Hello ", new Star(1), ", nice to meet you, too."), tag);
  }
  
  public void testThat() throws Exception
  {
    AttributesImpl attributes = new AttributesImpl();
    attributes.addAttribute(null, "index", null, "String", "1, 2");
    handler.startElement(null, null, "that", attributes);
    
    That expected = new That(1, 2);
    That actual = (That) stack.peek();
    assertEquals(expected, actual);
  }
  
  public void testThink() throws Exception
  {
    char[] text;
    AttributesImpl attributes = new AttributesImpl();
    handler.startElement(null, null, "think", attributes);
      handler.characters(text = toCharArray("Thinking..."), 0, text.length);
    handler.endElement(null, null, "think");
    
    Think expected = new Think("Thinking...");
    Think actual = (Think) stack.peek();
    assertEquals(expected, actual);
    
    assertEquals("", actual.toString(null));
  }

  public void testTopic() throws Exception
  {
    char[] text;
    AttributesImpl attributes = new AttributesImpl();
    AttributesImpl topicAtts = new AttributesImpl();
    topicAtts.addAttribute(null, "name", null, "String", "TEST TOPIC");
    handler.startElement(null, null, "topic", topicAtts);
      handler.startElement(null, null, "category", attributes);
        handler.startElement(null, null, "pattern", attributes);
          handler.characters(text = toCharArray("HELLO ALICE I AM *"), 0, text.length);
        handler.endElement(null, null, "pattern");
        handler.startElement(null, null, "template", attributes);
          handler.characters(text = toCharArray("Hello "), 0, text.length);
          handler.startElement(null, null, "star", attributes);
          handler.characters(text = toCharArray(", nice to meet you."), 0, text.length);
        handler.endElement(null, null, "template");
      handler.endElement(null, null, "category");
    handler.endElement(null, null, "topic");

    Topic actual = (Topic) stack.peek();
    Topic expected = new Topic("TEST TOPIC",
                       new Category(
                         new Pattern("HELLO ALICE I AM *"),
                         new Template("Hello ", new Star(1), ", nice to meet you.")
                       )
                     );
    assertEquals(expected, actual);
    assertEquals("TEST TOPIC", actual.getName());
  }
  
  public void testUppercase() throws Exception
  {
    char[] text;
    AttributesImpl attributes = new AttributesImpl();
    handler.startElement(null, null, "uppercase", attributes);
      handler.characters(text = toCharArray("Convert this to uppercase"), 0, text.length);
    handler.endElement(null, null, "uppercase");
    
    Uppercase expected = new Uppercase("Convert this to uppercase");
    Uppercase actual = (Uppercase) stack.peek();
    assertEquals(expected, actual);
    
    assertEquals("CONVERT THIS TO UPPERCASE", actual.toString(null));
  }

  public void testVersion() throws Exception
  {
    char[] text;
    AttributesImpl attributes = new AttributesImpl();
    handler.startElement(null, null, "version", attributes);

    Version expected = new Version();
    Version actual   = (Version) stack.peek();
    assertEquals(expected, actual);

    assertEquals(ChatterBean.VERSION, actual.toString(null));
  }
}
