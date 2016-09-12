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

import java.util.Collections;
import java.util.Iterator;
import java.util.List;

import bitoflife.chatterbean.aiml.Category;

/**
Linear implementation of the <code>Matcher</code> interface.
*/
public class ListMatcher implements Matcher
{
  /*
  Attributes
  */
  
  private final ListMatcherSorter sorter = new ListMatcherSorter(); 
  
  private List<Category> categories;
  
  /*
  Constructors
  */
  
  public ListMatcher()
  {
  }
  
  public ListMatcher(List<Category> categories)
  {
    add(categories);
  }

  /*
  Methods
  */

  private void addWildcard(Sentence sentence, int beginIndex, int endIndex)
  {
    if (beginIndex < sentence.getLength())
    {
      String value = " ";
      for(int i = beginIndex; i < endIndex; i++)
          value += sentence.getOriginal(i) + " ";
  
      sentence.addWildcard(value);
    }
  }

  /**
  Recursive expansion for the public match() method.

  @param matchPath The match path for the Category.
  @param sentence The matching Sentence.
  @param sentenceIndex the index of the currently matching Sentence element.
  @param matchPathIndex the index of the currently matching Pattern element.

  @return Whether the Sentence is matched by this Pattern.
  */
  private boolean match(String[] matchPath, Sentence sentence, int sentenceIndex, int matchPathIndex)
  {
    /* If both the Sentence and Pattern arrays are done for, the matching succeeds. Otherwise, if either one of them is done for, then either the Sentence has elements the Pattern cannot match, or the Pattern has elements which are not in the Sentence (or cannot be related to Setence elements). Either way the matching fails. */
    if(sentenceIndex >= sentence.getMatchPathLength() && matchPathIndex >= matchPath.length)
        return true;
    else if(sentenceIndex >= sentence.getMatchPathLength() || matchPathIndex >= matchPath.length)
        return false;

    /* If this Pattern element is a wildcard, then try to match successively more sentence elements to it, until either the remaining elements are matched by the rest of the Pattern, or the sentence runs out of them (in which case the matching can either succeed or fail, depending on whether there are remaining Pattern elements). */
    if(matchPath[matchPathIndex].equals("_") || matchPath[matchPathIndex].equals("*"))
    {
      for(int i = sentenceIndex + 1, n = sentence.getMatchPathLength(); i <= n; i++)
        if(match(matchPath, sentence, i, matchPathIndex + 1))
        {
          addWildcard(sentence, sentenceIndex, i);
          return true;
        }
    }
    else if(matchPath[matchPathIndex].equals(sentence.getMatchPath(sentenceIndex)))
      return match(matchPath, sentence, sentenceIndex + 1, matchPathIndex + 1);

    return false;
  }
  
  public void add(List<Category> categories)
  {
    Collections.sort(categories, sorter);  
    this.categories = categories;
  }

  public Category match(Sentence sentence)
  {
    for(Category category : categories)
      if (match(category.getMatchPath(), sentence, 0, 0))
        return category;

    return null;
  }
  
  public int size()
  {
    return categories.size();
  }
  
  public String toString()
  {
    StringBuilder value = new StringBuilder();
    for(Iterator<Category> i = categories.iterator(); i.hasNext();)
    {
      value.append(i.next());
      value.append("\n");
    }

    return value.toString();
  }
}
