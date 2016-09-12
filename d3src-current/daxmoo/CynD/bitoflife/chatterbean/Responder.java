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

import bitoflife.chatterbean.aiml.Category;

public class Responder
{
  /*
  Attributes
  */

  private Matcher matcher;
  private Normalizer normalizer;

  /*
  Constructor
  */
  
  public Responder()
  {
  }
  
  public Responder(Matcher matcher, Normalizer normalizer)
  {
    this.matcher = matcher;
    this.normalizer = normalizer;
  }
  
  /*
  Methods
  */

  public String respond(AliceBot callback, String input)
  {
    input = normalizer.applyPunctuation(input);
    input = normalizer.applyCorrection(input);
    input = normalizer.applyProtection(input);
    String entries[] = normalizer.split(input);

    Context context = callback.getContext();
    Response response = new Response(normalizer);
    String that = context.getThat();
    String topic = context.getTopic();
    for(int i = 0, n = entries.length; i < n; i++)
    {
      Sentence sentence = new Sentence(callback, entries[i], that, topic);
      if(!sentence.isEmpty())
      {
        Category category = matcher.match(sentence);
        response.addSentences(category.toString(sentence));
      }
    }

    context.addResponse(response);
    return response.getOriginal();
  }

  public String toString()
  {
    return matcher.toString();
  }
  
  /*
  Properties
  */
  
  public Matcher getMatcher()
  {
    return matcher;
  }

  public void setMatcher(Matcher matcher)
  {
    this.matcher = matcher;
  }
  
  public Normalizer getNormalizer()
  {
    return normalizer;
  }

  public void setNormalizer(Normalizer normalizer)
  {
    this.normalizer = normalizer;
  }
}
