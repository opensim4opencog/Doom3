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

import java.text.SimpleDateFormat;
import java.util.Date;
import junit.framework.TestCase;

public class AliceBotTest extends TestCase
{
  /*
  Attributes
  */

  private final AliceBotMother mother = new AliceBotMother();

  /*
  Methods
  */

  private void doTests(AliceBot bot)
  {
    assertEquals("Yes, I see the fire in your eyes.", bot.respond("Do you see the fire in my eyes?"));
    assertEquals("What makes you think that if I am a human then you are a chatterbot?",
                 bot.respond("If you are a human then I am a chatterbot."));
    assertEquals("I am sorry, my answers are limited -- you must provide the right questions.",
                 bot.respond("What are you?"));
    assertEquals("Hello ! My name is Alice, who are you?", bot.respond("Hello?"));
    assertEquals("I said \"Hello\".", bot.respond("What did you just say?"));
    assertEquals("Hello ! My name is Alice, who are you?", bot.respond("Hi ya!"));
    assertEquals("Nice to meet you, Makoto. :-)", bot.respond("I am Makoto."));
    assertEquals("Nice to meet you, Green Lantern. :-)", bot.respond("I am called Green Lantern."));
    assertEquals("'kay. Nice to meet you, Freiya. :-) What's up?", bot.respond("Name's Freiya."));
    assertEquals("Yes, I am an ALICE Bot.", bot.respond("Are you a bot, Alice?"));
    assertEquals("My name is Alice, nice to meet you!", bot.respond("What is your name?"));
    assertEquals("Sorry, I don't know what a chatterbot is.", bot.respond("What is a chatterbot?"));
    assertEquals("Once more? \"that\".", bot.respond("You may say that again, Alice."));
    assertEquals("Hello System", bot.respond("Print this: Hello System"));
    assertEquals("My engine is an Alpha series ChatterBean engine.", bot.respond("What series your engine is?"));
    
    assertEquals("Nice to meet you, Hélio. :-)", bot.respond("My name is Hélio."));
    assertEquals("Hélio", bot.getContext().getPredicate("name"));
    
    assertEquals("Thank you, Hélio.", bot.respond("Nice to meet you, too."));
    
    assertEquals("Do you like cheese?", bot.respond("What do you want to know?"));
    assertEquals("DO YOU LIKE CHEESE", bot.getContext().getThat());
    assertEquals("LIKES", bot.getContext().getTopic());
    assertEquals("Good for you.", bot.respond("Yes."));
    
    int size = bot.getResponder().getMatcher().size();
    assertEquals("I currently contain " + size + " categories.", bot.respond("What size are you?"));
    
    String date = (new SimpleDateFormat("yyyy-MM-dd HH:mm:ss")).format(new Date());
    assertEquals("Now is " + date + ".", bot.respond("What time is now?"));
  }

  public void testGraphedRespond() throws Exception
  {
    doTests(mother.newGraphedInstance());
  }

  public void testListedRespond() throws Exception
  {
    doTests(mother.newListedInstance());
  }
}
