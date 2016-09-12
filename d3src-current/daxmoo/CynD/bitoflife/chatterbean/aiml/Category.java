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

import java.lang.System;
import java.util.List;
import org.xml.sax.Attributes;
import bitoflife.chatterbean.Match;

public class Category implements NodeTag
{
  /*
  Attributes
  */
  
  private Pattern pattern;
  private Template template;
  private That that;
  private Topic topic;
  private Guard guard;
  
  /*
  Constructor
  */

  public Category()
  {
  }

  public Category(String pattern, Object... children)
  {
    this(new Pattern(pattern), new That("*"), new Topic("*"), new Template(children));
  }
  
  public Category(Pattern pattern, Template template)
  {
    this(pattern, new That("*"), new Topic("*"), template);
 }

public Category(Pattern pattern, Guard guard,Template template)
    {
      this(pattern, new That("*"), new Topic("*"), guard, template);
    }
  
  public Category(Pattern pattern, That that, Topic topic, Template template)
  {
    this.pattern = pattern;
    this.template = template;
    this.that = that;
    this.topic = topic;
  }
  public Category(Pattern pattern, That that, Topic topic, Guard guard, Template template)
  {
    this.pattern = pattern;
    this.template = template;
    this.that = that;
    this.topic = topic;
    this.guard=guard;
  }
  
  public Category(Attributes attributes)
  {
  }
  
  /*
  Methods
  */

  private Pattern toPattern(Object o)
  {
    try
    {
      return (Pattern) o;
    }
    catch (Exception e)
    {
      throw new ClassCastException(o.getClass().getName() + " of value (" + o + ") to Pattern");
    }
  }

  private Template toTemplate(Object o)
  {
    try
    {
      return (Template) o;
    }
    catch (Exception e)
    {
      throw new ClassCastException(o.getClass().getName() + " of value (" + o + ") to Template");
    }
  }

  public void add(List<Object> children) throws AIMLParserException
  {
      /*
    pattern  = toPattern(children.get(0));
    
    Object child = children.get(1);
    if (child instanceof That)
    {
      that = (That) child;
      template = toTemplate(children.get(2));
    }
    else if (child instanceof Guard) {
        guard = (Guard) child;
        that = new That("*");
        template = toTemplate(children.get(2));
    }
    else
    {
      that = new That("*");
      template = toTemplate(child);
    }
    */
      that=new That("*");
      topic=new Topic("*");
      pattern = new Pattern("*");
       guard=null; //new Guard("*");
      for (Object child : children)
      {
          if (child instanceof Guard)     {guard = (Guard) child;}
          if (child instanceof That)      {that = (That) child;}
          if (child instanceof Pattern)   {pattern=toPattern(child);}
          if (child instanceof Template) {template=toTemplate(child);}
          if (child instanceof Topic)      {topic=(Topic)child;}
      }
      
  }

  public boolean equals(Object obj)
  {
    if (obj == null || !(obj instanceof Category)) return false;
    Category compared = (Category) obj;
    
    return (pattern.equals(compared.pattern) &&
            template.equals(compared.template) &&
            that.equals(compared.that));
  }

  public String toString()
  {
    return "[" + pattern.toString() + "][" + that.toString() + "][" + template.toString() + "]";
  }

  public String toString(Match match)
  {
    return template.toString(match);
  }
  
  /*
  Properties
  */
  
  public String[] getMatchPath()
  {
    String[] pattPath = pattern.getElements();     
    String[] thatPath = that.getElements();
    String[] topicPath = topic.getElements();
    int m = pattPath.length;
    int n = thatPath.length;
    int o = topicPath.length;
    String[] matchPath = new String[m + 1 + n + 1 + o];

    matchPath[m] = "<THAT>";
    matchPath[m + 1 + n] = "<TOPIC>";
    System.arraycopy(pattPath, 0, matchPath, 0, m);
    System.arraycopy(thatPath, 0, matchPath, m + 1, n);
    System.arraycopy(topicPath, 0, matchPath, m + 1 + n + 1, o);
    return matchPath;
  }

  public Pattern getPattern()
  {
    return pattern;
  }

  public void setPattern(Pattern pattern)
  {
    this.pattern = pattern;
  }

  public Template getTemplate()
  {
    return template;
  }

  public void setTemplate(Template template)
  {
    this.template = template;
  }

  public That getThat()
  {
    return that;
  }

  public void setThat(That that)
  {
    this.that = that;
  }

  public Topic getTopic()
  {
    return topic;
  }

  public void setTopic(Topic topic)
  {
    this.topic = topic;
  }
  
public Guard getGuard()
   {
     return guard;
   }

   public void setGuard(Guard guard)
   {
     this.guard = guard;
   }
}
