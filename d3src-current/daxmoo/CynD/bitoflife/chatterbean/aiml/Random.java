/*
Copyleft (C) 2005 Hélio Perroni Filho
xperroni@yahoo.com
ICQ: 2490863

This file is part of ChatterBean.

ChatterBean is free software; you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation; either version 2 of the License, or (at your option) any later version.

ChatterBean is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for more details.

You should have received a copy of the GNU General Public License along with ChatterBean (look at the Documents/ directory); if not, either write to the Free Software Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307 USA, or visit (http://www.gnu.org/licenses/gpl.txt).
*/
 // Kino Coursey
 // Daxtron Labs 2005
 
package bitoflife.chatterbean.aiml;

import java.lang.System;
import java.util.List;
import java.util.ArrayList;
import org.xml.sax.Attributes;
import bitoflife.chatterbean.Match;
//import java.util.Random;

public class Random extends TemplateNodeTag
{
  /*
  Attributes
  */
  
    public  List<TemplateTag> RandomList = new ArrayList<TemplateTag>(2);
  /*
  Constructor
  */

  public Random()
  {
  }

  
  public Random(Attributes attributes)
  {
  }
  
  /*
  Methods
  */

   public void add(List<Object> children) throws AIMLParserException
  {
      for (Object child : children)
      {
          if (child instanceof TemplateTag)     {RandomList.add(0,(TemplateTag)child);}
      }
      
  }

  public boolean equals(Object obj)
  {
      if (obj == null) return false;
      String text = obj.toString();
      return (text != null ? text.equals(toString()) : toString() == null);
  }

  public String toString()
  {
      String xcode="<random>";
             for(int i=0;i<RandomList.size();i++)
                 xcode=xcode+"<li>"+RandomList.get(i).toString()+"</li>";
             xcode=xcode+"</random>";
    return xcode;
  }

  public String toString(Match match)
  {
     java.util.Random   randseq = new java.util.Random();
     int nodeCount=RandomList.size();
     int nodeSelect;
     if (nodeCount==0) {
         return null;
     }
     if (nodeCount==1) {
          return RandomList.get(0).toString(match);
     }
      nodeSelect=(int)Math.ceil(nodeCount * randseq.nextDouble())-1;
      if (nodeSelect<0) { nodeSelect=0;
      }
      return RandomList.get(nodeSelect).toString(match);
  }
  
  /*
  Properties
  */
  
 }
