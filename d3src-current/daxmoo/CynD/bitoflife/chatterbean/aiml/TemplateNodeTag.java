/*
Copyleft (C) 2005 Hélio Perroni Filho
xperroni@yahoo.com
ICQ: 2490863

This file is part of ChatterBean.

ChatterBean is free software; you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation; either version 2 of the License, or (at your option) any later version.

ChatterBean is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for more details.

You should have received a copy of the GNU General Public License along with ChatterBean (look at the Documents/ directory); if not, either write to the Free Software Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307 USA, or visit (http://www.gnu.org/licenses/gpl.txt).
*/

package bitoflife.chatterbean.aiml;

import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import bitoflife.chatterbean.Match;

public class TemplateNodeTag extends TemplateTag implements NodeTag
{
  /*
  Attributes
  */

  private final List<TemplateTag> children = new LinkedList<TemplateTag>();
  
  /*
  Constructor
  */
  
  public TemplateNodeTag(Object... newChildren)
  {
    for (int i = 0, n = newChildren.length; i < n; i++)
    {
      Object child = newChildren[i];
      if (child instanceof TemplateTag)
        children.add((TemplateTag) child);
      else
        children.add(new Text(child.toString()));
    }
  }
  
  /*
  Methods
  */
  
  public void add(List<Object> newChildren) throws AIMLParserException
  {
    for (Object o : newChildren)
      children.add((TemplateTag) o);
  }

  public boolean equals(Object obj)
  {
    if (obj == null) return false;
    TemplateTag[] type = new TemplateTag[0];
    TemplateTag[] comparing = children.toArray(type);
    TemplateTag[] compared  = ((TemplateNodeTag) obj).children.toArray(type);

    if (comparing.length != compared.length)
      return false;

    for (int i = 0, n = comparing.length; i < n; i++)
      if (!comparing[i].equals(compared[i]))
        return false;
    
    return true;
  }

  public int hashCode()
  {
    return children.hashCode();
  }

  public String toString(Match match)
  {
    StringBuilder value = new StringBuilder();
    for (TemplateTag i : children)
    {
      value.append(i.toString(match));
    }
    return value.toString();
  }
  
  /*
  Properties
  */
  
  public TemplateTag[] getChildren()
  {
    TemplateTag[] array = new TemplateTag[children.size()];
    children.toArray(array);
    return array;
  }
  
  public void setChildren(TemplateTag[] newChildren)
  {
    children.clear();
    children.addAll(Arrays.asList(newChildren));
  }
}
