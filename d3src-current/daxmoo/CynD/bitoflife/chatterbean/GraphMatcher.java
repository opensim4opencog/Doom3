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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import bitoflife.chatterbean.aiml.Category;

/**
Implementation of the Matcher interface with (nearly) constant search time.
*/
public class GraphMatcher implements Matcher
{
  /*
  Attributes
  */
  
  /* The children of this node. */
  private final Map<String, GraphMatcher> children = new HashMap<String, GraphMatcher>();
  public  List<Category> GuardedCategories = new ArrayList<Category>(2);

  private int size = 0;
  private GraphMatcher parent;
  private Category category;
  private String name; // The name of a node is the pattern element it represents.

  private GraphMatcher(String name)
  {
    this.name = name;
  }

  /**
  Constructs a new root node.
  */
  public GraphMatcher()
  {
  }

  /**
  Constructs a new tree, with this node as the root.
  */  
  public GraphMatcher(List<Category> categories)
  {
    add(categories);
  }

  /*
  Methods
  */
  
  private void add(Category category, String[] elements, int index)
  {
    GraphMatcher child = children.get(elements[index]);
    if(child == null) addChild(child = new GraphMatcher(elements[index]));

    int nextIndex = index + 1;
    if(elements.length <= nextIndex)
    {
        // We want to add multiple elements to the guards
     if (category.getGuard ()!=null) {
         child.GuardedCategories.add(0,category);
     }
     else
         // no merge on plain categories for now
      child.category = category;
    }
    else
      child.add(category, elements, nextIndex);
  }

  private void addChild(GraphMatcher child)
  {
    children.put(child.name, child);
    child.parent = this;
  }

  private boolean isWildcard()
  {
    return ("_".equals(name) || "*".equals(name));
  }

  private Category checkCategoryGuard(Sentence sentence, Category category)
  {
      boolean guardResult;

      if (category==null){
          //System.out.println(" checkCategoryGuard null ["+dbg+"] "+sentence.getWildcardStack ()+" :: "); 
          return category;}

      if (category.getGuard()==null) return category;
      //System.out.println(" checkCategoryGuard ["+dbg+"] "+sentence.getWildcardStack ()+" :: "+ category.getGuard().toString());
      //Guard myGuard=category.getGuard();
      guardResult=category.getGuard().testGuard (sentence);
      if (guardResult==true)
      {
        //System.out.println("Guard TRUE for : "+category.toString());
          return category;
      }
      else
          return null;

  }

  private Category match(Sentence sentence, int index)
  {
    //String dbg="match |("+sentence.toString ()+","+index+") {"+name+"}"; 
    //System.out.println("Called:"+dbg);
    if(isWildcard()) return matchWildcard(sentence, index);

    if(!name.equals(sentence.getMatchPath(index))) return null;

    int nextIndex = index + 1;
    if(sentence.getMatchPathLength() <= nextIndex)
    {
        for(int i=0;i<GuardedCategories.size();i++)
        {
          Category replyCategory =checkCategoryGuard(sentence,GuardedCategories.get(i)); 
          if (replyCategory!=null) return replyCategory;
              //return checkCategoryGuard(sentence,category);   //return category;
        }
        return category;
    }
    return matchChildren(sentence, nextIndex);
  }

  private Category matchChildren(Sentence sentence, int nextIndex)
  {
      //String dbg="     +matchChildren ("+sentence.toString ()+","+nextIndex+") {"+name+"}"; 
      //System.out.println("Called:"+dbg);
    GraphMatcher[] nodes = getChildren(sentence.getMatchPath(nextIndex));
    for(int i = 0, n = nodes.length; i < n; i++)
    {
       Category resultCategory=null; //new Category(new Pattern("*"),new Guard("*"),new Template(""));
      Category category = (nodes[i] != null ? nodes[i].match(sentence, nextIndex) : null);
      if (category!=null) { return category;
      }
      //if (category!=null) resultCategory= checkCategoryGuard(dbg, sentence, category);
      //if(resultCategory != null) return  category;  //return category

    }

    return null;
  }

  private Category matchWildcard(Sentence sentence, int index)
  {
    //String dbg="     +matchWildcard ("+sentence.toString ()+","+index+") {"+name+"}"; 
    boolean guessed;
    //System.out.println("Called:"+dbg);
    Category resultCategory=null;//=new Category(new Pattern("*"),new Guard("*"),new Template(""));
    int n = sentence.getMatchPathLength();
    for(int i = index + 1; i < n; i++)
    {
      guessed=guessWildcard(sentence,index,i);  // Guess that this is the wildcards
      Category category = matchChildren(sentence, i);
     // resultCategory=null;
     // if (category!=null) resultCategory =  checkCategoryGuard(sentence,category);
      //resultCategory=category;
      if (guessed==true) sentence.popWildcardGuess(); 
      if (category != null)
      {
        addWildcard(sentence, index, i, category);
        return category;
      }
    }

    guessed=guessWildcard(sentence,index,n);  // Guess that this is the wildcards
    //Category category = matchChildren(sentence, i);
    //resultCategory=null;
    //resultCategory =  checkCategoryGuard(sentence,category);
  for(int i=0;i<GuardedCategories.size();i++)
    {
      Category replyCategory =checkCategoryGuard(sentence,GuardedCategories.get(i)); 
      if (replyCategory!=null)
      {
          if (guessed==true) sentence.popWildcardGuess(); 
     
           return replyCategory;
          //return checkCategoryGuard(sentence,category);   //return category;
      }
    }
    if (guessed==true) sentence.popWildcardGuess(); 
//    return category;



    if (category != null)
    {
      addWildcard(sentence, index, n, category);
      return category;
    }
    return null;
  //  if(category != null) addWildcard(sentence, index, n, category);
  //  return category;   //return category;
}

  private void addWildcard(Sentence sentence, int beginIndex, int endIndex, Category category)
  {
    String value = " ";
    if (beginIndex < sentence.getLength())
    {
      for(int i = beginIndex; i < endIndex; i++)
        value += sentence.getOriginal(i) + " ";

      sentence.addWildcard(value);
    }
  }

  private boolean guessWildcard(Sentence sentence, int beginIndex, int endIndex)
  {
    String value = " ";
    boolean guessed=false;
    //System.out.println("GuessWildCard ("+beginIndex+", "+endIndex+")");
    if ( (beginIndex < sentence.getLength()) && (endIndex <=sentence.getLength()) )
    {
      for(int i = beginIndex; i < endIndex; i++)
        value += sentence.getOriginal(i) + " ";

      sentence.pushWildcardGuess(value);
      guessed=true;
    }
    return guessed;
  }
  
  public void add(List<Category> categories)
  {
    for (Iterator<Category> i = categories.iterator(); i.hasNext();)
      add(i.next());
  }

  public void add(Category category)
  {
    String matchPath[] = category.getMatchPath();
    add(category, matchPath, 0);
    size++;
  }

  public int size()
  {
    return size;
  }

  /**
  Returns the Catgeory which Pattern matches the given Sentence, or <code>null</code> if it cannot be found.
  */
  public Category match(Sentence sentence)
  {
    return matchChildren(sentence, 0);
  }
  
  /*
  Properties
  */

  /**
  <p>
    Returns an array with three child nodes, in the following order:
  </p>
  <ul>
    <li>The "_" node;</li>
    <li>The node with the given name;</li>
    <li>The "*" node.</li>
  </ul>
  <p>
    If any of these nodes can not be found among this node's children, its position is filled by <code>null</code>.
  </p>
  */
  private GraphMatcher[] getChildren(String name)
  {
    return new GraphMatcher[] {children.get("_"), children.get(name), children.get("*")};
  }

  /**
  Gets the number of children of this node.
  */
  private int getChildCount()
  {
    return children.size();
  }
}
