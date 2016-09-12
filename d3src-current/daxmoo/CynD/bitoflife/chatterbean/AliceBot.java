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

import java.util.List;
import bitoflife.chatterbean.aiml.Category;

public class AliceBot
{
  /*
  Attributes
  */

  private Context context;
  
  private ExceptionHandler handler;

  private Responder responder;
  
  /*
  Constructors
  */
  
  public AliceBot()
  {
  }
  
  public AliceBot(ExceptionHandler handler, Responder responder)
  {
    this.context = new Context();
    this.handler = handler;
    this.responder = responder;
  }
  
  /*
  Methods
  */
  
  public void add(List<Category> categories)
  {
    responder.getMatcher().add(categories);
  }
  
  public String respond(String request)
  {
    if(request == null || "".equals(request.trim()))
      return null;
    else
      return responder.respond(this, request);
  }
  
  /*
  Properties
  */
  
  public Context getContext()
  {
    return context;
  }
  
  public void setContext(Context context)
  {
    this.context = context;
  }
  
  public ExceptionHandler getExceptionHandler()
  {
    return handler;
  }
  
  public void setExceptionHandler(ExceptionHandler handler)
  {
    this.handler = handler;
  }
  
  public Responder getResponder()
  {
    return responder;
  }
  
  public void setResponder(Responder responder)
  {
    this.responder = responder;
  }
}
