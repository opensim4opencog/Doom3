/*
Copyleft (C) 2004 Hélio Perroni Filho
xperroni@yahoo.com
ICQ: 2490863

This file is part of ChatterBean.

ChatterBean is free software; you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation; either version 2 of the License, or (at your option) any later version.

ChatterBean is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for more details.

You should have received a copy of the GNU General Public License along with ChatterBean (look at the Documentos/ directory); if not, either write to the Free Software Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307 USA, or visit (http://www.gnu.org/licenses/gpl.txt).
*/

package bitoflife.chatterbean;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.concurrent.atomic.AtomicReference;

import bitoflife.chatterbean.parser.ChatterBeanParser;

public class ChatterBean extends AliceBot implements Runnable
{
  /*
  Attributes
  */

  private final AtomicReference<Thread> thread = new AtomicReference<Thread>();
  
  private Logger logger;
  
  public static final String VERSION = "0.7.0 Alpha";
  
  /*
  Constructor
  */
  
  public ChatterBean()
  {
  }
  
  public ChatterBean(String properties)
  {
    try
    {
      ChatterBeanParser parser = new ChatterBeanParser();
      parser.parse(this, properties);
    }
    catch (Exception e)
    {
      ExceptionHandler handler = getExceptionHandler();
      if (handler != null)
        getExceptionHandler().handle(e);
      else
        e.printStackTrace();
    }    
  }

  /*
  Methods
  */
  
  public static void main(String[] args)
  {
    String file = (args.length > 0 ? args[0] : "chatterbean.properties.xml");
    ChatterBean bot = new ChatterBean(file);
    bot.run();
  }
  
  public String respond(String request)
  {
    try
    {
      if(request == null || "".equals(request.trim())) return "";
      String response = getResponder().respond(this, request);
      if (logger != null) logger.append(request, response);
      return response;
    }
    catch(Exception e)
    {
      getExceptionHandler().handle(e);
      return "";
    }
  }
  
  public void run()
  {
    /* If no thread is associated to this bot, or if the associated thread is the current one, then it is legal to call this method; otherwise, the Exception Handler must be notified about an illegal operation. */
    if (!(thread.compareAndSet(null, Thread.currentThread()) ||
          thread.get().equals(Thread.currentThread())))
    {
      getExceptionHandler().handle(new IllegalThreadStateException("Bot is already running on another thread"));
      return;
    }

    try
    {
      BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
      for(;;)
      {
        String input = reader.readLine();
        if (input == null || "".equals(input.trim())) break;
        System.out.println(respond(input));
      }
    }
    catch (Exception e)
    {
      getExceptionHandler().handle(e);
    }

    // After this method returns, the bot is free to run in any available thread.
    thread.set(null);
  }
  
  public void run(boolean inNewThread)
  {
    if (!inNewThread)
      run();
    else if (thread.compareAndSet(null, new Thread(this)))
      thread.get().start();
    else // The bot can only run on one thread at any single time.
    {
      getExceptionHandler().handle(new IllegalThreadStateException("Bot is already running on another thread"));
      return;
    }
  }
  
  /*
  Properties
  */
  
  public Logger getLogger()
  {
    return logger;
  }
  
  public void setLogger(Logger logger)
  {
    this.logger = logger;
  }
}
