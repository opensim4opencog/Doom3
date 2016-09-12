/*
Copyleft (C) 2005 Hélio Perroni Filho
xperroni@yahoo.com
ICQ: 2490863

This file is part of ChatterBean.

ChatterBean is free software; you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation; either version 2 of the License, or (at your option) any later version.

ChatterBean is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for more details.

You should have received a copy of the GNU General Public License along with ChatterBean (look at the Documents/ directory); if not, either write to the Free Software Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307 USA, or visit (http://www.gnu.org/licenses/gpl.txt).
*/

package bitoflife.chatterbean.util;

import java.io.File;
import java.io.FileReader;
import java.io.FilenameFilter;
import java.io.Reader;
import java.util.Arrays;

public class Searcher implements FilenameFilter
{
  /*
  Attributes
  */
  
  private String expression;

  /*
  Methods
  */

  public boolean accept(File dir, String name)
  {
    return name.matches(expression);
  }
  
  public String[] dir(String path, String expression)
  {
    this.expression = expression;

    if (path.charAt(path.length() - 1) != '/') path += "/";
    File dir = new File(path);
    String[] names = dir.list(this);
    Arrays.sort(names);
    
    for (int i = 0, n = names.length; i < n; i++)
      names[i] = path + names[i];

    return names;
    
  }

  public Reader[] search(String path, String expression)
  {
    try
    {
      String[] names = dir(path, expression);
      Reader[] files = new Reader[names.length];
      for (int i = 0, n = names.length; i < n; i++)
      {
        java.lang.System.out.println(" File "+i +" = "+names[i]);
        files[i] = new FileReader(names[i]);
      }
      return files;
    }
    catch (Exception e)
    {
      e.printStackTrace();
      return new Reader[0];
    }
  }
}
