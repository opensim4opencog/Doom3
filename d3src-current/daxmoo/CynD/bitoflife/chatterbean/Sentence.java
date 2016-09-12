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
import java.util.List;

/**

*/
public class Sentence implements Match
{
  /*
  Attributes
  */
  
  /* Two wildcards is as good a guess as any. */
  private final List<String> wildcards = new ArrayList<String>(2);
  private final List<String> wildcardsguess = new ArrayList<String>(2);
  private int wildcardstack;

  private AliceBot callback; // Stores the callback AliceBot reference for the Match property.

  private String[] original;   // The (almost) original, unformatted entry.
  private String[] normalized; // The normalized entry.
  private String[] matchPath;  // The match path for this Sentence.
  private boolean inguardtest;
  
  /*
  Constructors
  */
  
  /**
  Constructs a Sentence out of a responder and an unformatted Sentence string.
  */
  public Sentence(AliceBot callback, String input, String that, String topic)
  {
    wildcardstack=0;
    inguardtest=false;
    this.callback = callback;
    if (input == null || "".equals(input.trim())) return;
    this.original = input.trim().split(" ");

    Normalizer normalizer = callback.getResponder().getNormalizer();
    normalized = normalizer.fit(normalizer.applyAccentuation(input)).split(" ");
    setUpMatchPath(that, topic);
  }

  /**
  Constructs a Sentence out of a normalized Sentence string. This is meant for tests only, since it will make the "original" entry the same as the normalized one.
  */
  public Sentence(String normalized)
  {
    this.normalized = normalized.trim().split(" ");
    original = this.normalized;
    setUpMatchPath("*", "*");
  }
  
  /*
  Methods
  */

  private void setUpMatchPath(String that, String topic)
  {
    String[] thatPath  = that.split(" ");
    String[] topicPath = topic.split(" ");

    int m = normalized.length, n = thatPath.length, o = topicPath.length;
    matchPath = new String[m + 1 + n + 1 + o];
    matchPath[m] = "<THAT>";
    matchPath[m + 1 + n] = "<TOPIC>";

    System.arraycopy(normalized, 0, matchPath, 0, m);
    System.arraycopy(thatPath, 0, matchPath, m + 1, n);
    System.arraycopy(topicPath, 0, matchPath, m + 1 + n + 1, o);
  }
  
  /**
  Inserts the newest wildcard match of the Sentence.
  */
  public void addWildcard(String value)
  {
    wildcards.add(0, value);
  }

  /**
  Inserts the newest wildcard match of the Sentence.
  */
  public void pushWildcardGuess(String value)
  {
//      java.lang.System.out.println("pushWildcardGuess "+value);
    wildcardsguess.add(wildcardsguess.size(),value);
  }

  public void popWildcardGuess()
  {
   if (wildcardsguess.size()>0) 
   {
//       java.lang.System.out.println("popWildcardGuess "+wildcardsguess.get(wildcardsguess.size()-1));
        wildcardsguess.remove(wildcardsguess.size()-1);
   }
  else
      java.lang.System.out.println("popWildcardGuess EMPTY !!!");


  }
  /**
  Returns whether this Sentence has a null value.
  */
  public boolean isEmpty()
  {
    return original == null;
  }
  
  public String toString()
  {
    String[][] versions = new String[][] {original, normalized};
    StringBuilder value = new StringBuilder(" "); 
    for (int i = 0; i < 2; i++)
    {
      if (versions[i] == null) continue;
      for (int j = 0, n = versions[i].length; j < n; j++)
      {
        value.append(versions[i][j]);
        value.append(" ");
      }

      if (i == 0) value.append("\n");
    }
    
    return value.toString();
  }

  /*
  Properties
  */
  
  /**
  Gets the number of individual words contained by the Sentence. 
  */
  public int getLength()
  {
    return normalized.length;
  }
  
  public String getMatchPath(int index)
  {
    return matchPath[index];
  }
  
  public int getMatchPathLength()
  {
    return matchPath.length;
  }

  /**
  Gets the (index)th word of the Sentence, in its normalized form.
  */
  public String getNormalized(int index)
  {
    return normalized[index];
  }

  /**
  Gets the (index)th word of the Sentence, in its (almost) original, unformatted form.
  */
  public String getOriginal(int index)
  {
    return original[index];
  }
  
  /*
  Match Implementation
  */
  
  public AliceBot getCallback()
  {
    return callback;
  }

  public String getWildcard(int index)
  {
      if (index <=0)
          {
           java.lang.System.out.println("getWildCard index<=0 !!!");
           return null;
            }
    if (inguardtest==true) {
//         java.lang.System.out.println("getWildCard ("+index+") on "+getWildcardStack());
        if (wildcardsguess.size() <= 0) 
            {
            java.lang.System.out.println("getWildCard wildcardsguess.size() <=0 !!!");
             
            return null;
            }
        return (String) wildcardsguess.get(index-1);
       }
    
    if (wildcards.size() <= 0)
        { 
        java.lang.System.out.println("getWildCard wildcards.size() <=0 !!!");
         return null;
        }
    return (String) wildcards.get(index - 1);
  }
  
public String getWildcardGuess(int index)
  {
    return (String) wildcardsguess.get(index - 1);
  }

public String getWildcardStack()
{
    String Stk="";
    for (int i=0; i<wildcardsguess.size();i++) {
        Stk=Stk+ "/("+i+")"+ wildcardsguess.get(i);
    }
    return Stk;
}

public void setInGuardTest(boolean value)

{
    inguardtest=value;
}

}
