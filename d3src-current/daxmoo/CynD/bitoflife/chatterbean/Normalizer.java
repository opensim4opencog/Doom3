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

import static java.util.regex.Pattern.CASE_INSENSITIVE;
import static java.util.regex.Pattern.UNICODE_CASE;

import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
Provides operation for normalizing a request, before submiting it to the matching operation.
*/
public class Normalizer
{
  /*
  Attributes
  */

  // The regular expression which will split entries by sentence splitters.  
  private String splitters = "";

  // The collection of substitutions known to the system.  
  private Map<String, String> accentuation;
  private Map<String, String> correction;
  private Map<String, String> protection;
  private Map<String, String> punctuation;

  /*
  Constructor
  */

  /**
  Constructs a new Normalizer out of a list of sentence splitters and a map of substitutions.
  */
  public Normalizer(List splitters, Map<String, Map<String, String>> substitutions)
  {
    accentuation = substitutions.get("accentuation");
    correction = substitutions.get("correction");
    protection = substitutions.get("protection");
    punctuation = substitutions.get("punctuation");

    for (Iterator i = splitters.iterator();;)
    {
      this.splitters += i.next().toString();

      if (!i.hasNext()) break; // Any more splitters? If not, get out.

      // Otherwise, add the "or" character, preparing to the next splitter.
      this.splitters += "|";
    }
  }
  
  /*
  Methods
  */

  /**
  Turn an entry into an entry list, spliting it by the sentence splitters.
  */
  public String[] split(String input)
  {
    return input.split(splitters);
  }

  /**
  Turns the entry to UPPERCASE, takes sequences of non-alphanumeric characters out of it (replacing them with a single whitespace) and sees that the entry is trimmed off leading and trailing whitespaces.
  */
  public String fit(String entry)
  {
    return entry.toUpperCase().replaceAll("[^a-zA-Z0-9]+", " ").trim();
  }

  /**
  Searches and replaces the substitutions known to the system in the given entry. This is done in the same order the substitutions were inserted, so the user can define priorities for each one of them. For example, if the substitutions <code>(" WAHT ", " WHAT ")</code> and <code>(" WHAT S ", " WHAT IS ")</code> are inserted in that order, the entry "Waht s your name?" will be correctly changed to " WHAT IS YOUR NAME ?  "; the same would not be true for a different insertion order.
  */
  private String apply(Map<String, String> substitutions, String input)
  {
    input = " " + input + " ";
    for (String find : substitutions.keySet())
    {
      Pattern pattern = Pattern.compile(find, CASE_INSENSITIVE | UNICODE_CASE);
      Matcher matcher = pattern.matcher(input);
      String replace = substitutions.get(find);      
      input = matcher.replaceAll(replace);
    }
  
    return input;
  }
  
  public String applyAccentuation(String input)
  {
    return apply(accentuation, input);
  }
  
  public String applyCorrection(String input)
  {
    return apply(correction, input);
  }
  
  public String applyProtection(String input)
  {
    return apply(protection, input);
  }
  
  public String applyPunctuation(String input)
  {
    return apply(punctuation, input);
  }
}
