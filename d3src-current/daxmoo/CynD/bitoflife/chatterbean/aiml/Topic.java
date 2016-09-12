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
import org.xml.sax.Attributes;

public class Topic implements NodeTag
{
  /*
  Attributes
  */
  
  private List<Category> categories = new LinkedList<Category>();
  
  private String name;
  
  /*
  Constructors
  */
  
  public Topic(Attributes attributes)
  {
    name = attributes.getValue(0);
  }
  
  public Topic(String name, Category... children)
  {
    this.name = name;
    categories.addAll(Arrays.asList(children));
  }

  /*
  Methods
  */
  
  public void add(List<Object> children)
  {
    for (Object i : children)
    {
      Category category = (Category) i;
      category.setTopic(this);
      categories.add(category);
    }
  }
  
  public List<Category> asList()
  {
    return categories;
  }
  
  public boolean equals(Object obj)
  {
    if (obj == null || !(obj instanceof Topic))
      return false;
    else
    {
      Topic compared = (Topic) obj;
      return (name.equals(compared.name) && categories.equals(compared.categories));
    }
  }
  
  public String toString()
  {
    StringBuilder result = new StringBuilder();
    for (Category i : categories)
    {
      result.append(i);
      result.append('\n');
    }
    
    return result.toString();
  }
  
  /*
  Properties
  */
  
  public String[] getElements()
  {
    return name.split(" ");
  }
  
  public String getName()
  {
    return name;
  }
}
