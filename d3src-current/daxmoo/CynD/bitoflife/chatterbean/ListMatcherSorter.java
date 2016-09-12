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

import java.util.Comparator;
import bitoflife.chatterbean.aiml.Category;

public class ListMatcherSorter implements Comparator<Category>
{
  /*
  Methods
  */
  
  private int compare(String[] comparing, String[] compared)
  {
    /* The patterns will be different if, at any given index, they have different elements, AND at least one of them is a wildcard caracter. */    
    for (int i = 0, n = Math.min(comparing.length, compared.length); i < n; i++)
    {
      int diff = comparing[i].compareTo(compared[i]);
      if (diff == 0) continue;

      /* Which pattern is the bigger one depends on whether one of the differing elements is a wildcard. */      
      if ("_".equals(comparing[i]) || "*".equals(compared[i]))
        return -1;
      else if ("*".equals(comparing[i]) || "_".equals(compared[i]))
        return 1;
    }

    /* If the loop above didn't find any differences, the patterns can still be different, if they are not the same length. */    
    if (compared.length > comparing.length)
      return 1;
    else if (compared.length < comparing.length)
      return -1;
    else
      return 0;
  }
  
  public int compare(Category c1, Category c2)
  {
    String[] comparing;
    String[] compared;
    
    // Compares the patterns of the Categories.
    comparing = c1.getPattern().getElements();
    compared  = c2.getPattern().getElements();
    int diff = compare(comparing, compared);
    if (diff != 0) return diff;
    
    // If the patterns are equal, the Categories can still be different, if their That's are different.
    comparing = c1.getThat().toString().split(" ");
    compared  = c2.getThat().toString().split(" ");
    return compare(comparing, compared);
  }
}
