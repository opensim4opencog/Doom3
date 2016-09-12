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
import bitoflife.chatterbean.AliceBot;
import bitoflife.chatterbean.Context;
import bitoflife.chatterbean.ExceptionHandler;
import bitoflife.chatterbean.GraphMatcher;
import bitoflife.chatterbean.Matcher;
import bitoflife.chatterbean.Normalizer;
import bitoflife.chatterbean.Responder;
import bitoflife.chatterbean.aiml.AIMLParser;

public class AliceBotParser {
    /*
    Attributes
    */

    private Class<? extends Context> contextClass = Context.class;
    private Class<? extends ExceptionHandler> expHandlerClass = ExceptionHandler.class;
    private Class<? extends Matcher> matcherClass = GraphMatcher.class;
    private Class<? extends Responder> responderClass = Responder.class;

    private AIMLParser aimlParser;
    private NormalizerParser normParser;

    /*
    Constructor
    */

    public AliceBotParser() throws AliceBotParserConfigurationException
    {
        try {
            aimlParser = new AIMLParser();
            normParser = new NormalizerParser();
        } catch (Exception e) {
            throw new AliceBotParserConfigurationException(e);
        }
    }

    /*
    Methods
    */

    private Context newContext() throws Exception
    {
        return(Context) contextClass.newInstance();
    }

    private ExceptionHandler newExceptionHandler() throws Exception
    {
        return(ExceptionHandler) expHandlerClass.newInstance();
    }

    private Matcher newMatcher() throws Exception 
    {
        return(Matcher) matcherClass.newInstance();
    }

    private Responder newResponder(Matcher matcher, Normalizer normalizer) throws Exception 
    {
        Responder responder = (Responder) responderClass.newInstance();
        responder.setMatcher(matcher);
        responder.setNormalizer(normalizer);
        return responder;
    }

    public void parse(AliceBot bot, Reader splitters, Reader substitutions, Reader... aiml) throws AliceBotParserException
    {
        try {
            Matcher matcher = newMatcher();
            aimlParser.parse(matcher, aiml);
            Normalizer normalizer = normParser.parse(splitters, substitutions);

            bot.setExceptionHandler(newExceptionHandler());
            bot.setResponder(newResponder(matcher, normalizer));
        } catch (Exception e) {
            if (e instanceof org.xml.sax.SAXParseException ) {
            org.xml.sax.SAXParseException se =(org.xml.sax.SAXParseException)e;
            String reason = "line="+se.getLineNumber()+" col="+se.getColumnNumber()+ " id="+se.getPublicId();
           daxclr.doom.modules.CycAIMLModule.debugln(reason);
           System.out.println(reason);
            }
            throw new AliceBotParserException(e);
        }
    }

    public AliceBot parse(Reader splitters, Reader substitutions, Reader... aiml) throws AliceBotParserException
    {
        try {
            AliceBot bot = new AliceBot();
            bot.setContext(newContext());
            parse(bot, splitters, substitutions, aiml);
            return bot;
        } catch (AliceBotParserException e) {
            throw e;
        } catch (Exception e) {
            if (e instanceof org.xml.sax.SAXParseException ) {
              org.xml.sax.SAXParseException se =(org.xml.sax.SAXParseException)e;
              String reason = "line="+se.getLineNumber()+" col="+se.getColumnNumber()+ " id="+se.getPublicId();
            // daxclr.modules.CycAIMLModule.debugln(reason);
             System.out.println(reason);
            }
            throw new AliceBotParserException(e);
        }
    }

    /*
    Properties
    */

    public <C extends Context> void setContextClass(Class<C> contextClass) {
        this.contextClass = contextClass;
    }

    public <E extends ExceptionHandler> void setExceptionHandlerClass(Class<E> expHandlerClass) {
        this.expHandlerClass = expHandlerClass;
    }

    public <M extends Matcher> void setMatcherClass(Class<M> matcherClass) {
        this.matcherClass = matcherClass;
    }

    public <R extends Responder> void setResponderClass(Class<R> responderClass) {
        this.responderClass = responderClass;
    }
}
