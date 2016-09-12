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

import java.io.File;
import java.io.FileInputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.Writer;
import java.util.Properties;

import bitoflife.chatterbean.ChatterBean;
import bitoflife.chatterbean.Context;
import bitoflife.chatterbean.Logger;
import bitoflife.chatterbean.util.Searcher;
import bitoflife.chatterbean.util.Sequence;

public class ChatterBeanParser
{
  /*
  Attributes
  */
  
  private AliceBotParser botParser;
  private ContextParser  conParser;
  
  private Class<? extends Logger> loggerClass = Logger.class;
  
  /*
  Constructor
  */
  
  public ChatterBeanParser() throws AliceBotParserConfigurationException
  {
    try
    {
      botParser = new AliceBotParser();
      conParser = new ContextParser();
    }
    catch (AliceBotParserConfigurationException e)
    {
      throw e;
    }
    catch (Exception e)
    {
      throw new AliceBotParserConfigurationException(e);
    }
  }
  
  /*
  Methods
  */
  
  private Context newContext(String root, String context) throws Exception
  {
    if (context == null) return null;
    return conParser.parse(new FileReader(root + context));
  }
  
  private Logger newLogger(String root, String dir) throws Exception
  {
    if (dir == null) return null;
    String path = root + dir;
    
    Sequence sequence = new Sequence(path + "sequence.txt");
    File file = new File(path + "log" + sequence.getNext() + ".txt");
    return loggerClass.getConstructor(Writer.class).newInstance(new FileWriter(file));
  }
  
  public void parse(ChatterBean bot, String path) throws AliceBotParserException
  {
    try
    {
      Properties properties = new Properties();
      properties.loadFromXML(new FileInputStream(path));

      String root = path.substring(0, path.lastIndexOf('/') + 1);
      String categories = root + properties.getProperty("categories");
      String logs = properties.getProperty("logs");
      String context = properties.getProperty("context");
      String splitters = root + properties.getProperty("splitters");
      String substitutions = root + properties.getProperty("substitutions");
      
      Searcher searcher = new Searcher();
      bot.setContext(newContext(root, context));
      bot.setLogger(newLogger(root, logs));
      botParser.parse(bot, new FileReader(splitters),
                           new FileReader(substitutions),
                           searcher.search(categories, ".*\\.aiml"));
    }
    catch (AliceBotParserException e)
    {
      throw e;
    }
    catch (Exception e)
    {
      throw new AliceBotParserException(e);
    }
  }
  
  /*
  Properties
  */

  public <L extends Logger> void setLoggerClass(Class<L> loggerClass)
  {
    this.loggerClass = loggerClass;
  }
}
