/*
Copyleft (C) 2005 Hélio Perroni Filho
xperroni@yahoo.com
ICQ: 2490863

This file is part of ChatterBean.

ChatterBean is free software; you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation; either version 2 of the License, or (at your option) any later version.

ChatterBean is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for more details.

You should have received a copy of the GNU General Public License along with ChatterBean (look at the Documents/ directory); if not, either write to the Free Software Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307 USA, or visit (http://www.gnu.org/licenses/gpl.txt).
*/
// Modification for CynD
// Kino Coursey 2005 Daxtron Labs

package bitoflife.chatterbean.aiml;
import org.opencyc.cycobject.CycFort;
import org.opencyc.cycobject.CycList;
import org.opencyc.cycobject.CycSymbol;
import org.xml.sax.Attributes;

import bitoflife.chatterbean.AliceBot;
import bitoflife.chatterbean.Context;
import bitoflife.chatterbean.Match;
import bitoflife.chatterbean.Sentence;

//public class Guard implements NodeTag
public class Guard extends TemplateNodeTag {
    /*
   Attributes
   */
    public static daxclr.inference.CycAPI cycAccess = null;

    private int responseIndex = 1, sentenceIndex = 1;

    /*
    Constructors
    */

    public Guard(Attributes attributes) {
        String value = attributes.getValue(0);
        if (value == null) return;

        String[] indexes = value.split(",");
        responseIndex = Integer.parseInt(indexes[0].trim());
        if (indexes.length > 1) sentenceIndex = Integer.parseInt(indexes[1].trim());
    }

    public Guard(Object... children) {
        super(children);
    }

    public Guard(int responseIndex, int sentenceIndex) {
        this.responseIndex = responseIndex;
        this.sentenceIndex = sentenceIndex;
    }

    /*
    Methods
    */

    public boolean equals(Object obj) {
        if (obj == null || !(obj instanceof Guard)) return false;

        Guard compared = (Guard) obj;

        return(responseIndex  == compared.responseIndex &&
               sentenceIndex == compared.sentenceIndex);
    }

    public int hashCode() {
        return responseIndex + sentenceIndex;
    }

    public String toString(Match match) {
        if (match == null)
            return super.toString(null);

        AliceBot bot = match.getCallback();
        Context context = bot.getContext();
        return context.getSentences(responseIndex, sentenceIndex);
    }

    /*
    Properties
    */

    public String[] getElements() {
        return super.toString(null).split(" ");
    }

    public boolean testGuard(Sentence match) {
        //String guardResponse;
        boolean Answer=false;
        match.setInGuardTest (true);
        //System.Out.println("testGuard "+match.getWildcardStack ());
        AliceBot bot = (match != null ? match.getCallback() : null);
        String request = super.toString(match);
        //guardResponse= (bot != null ? bot.respond(request) : "<srai>" + request + "</srai>");
        match.setInGuardTest (false);
        // return request;
        java.lang.System.out.println("testguard(1) ["+request+"]"+request.length ());
        if (request.trim()=="*" || request.length ()<=1) {
            return true;
        }
        if (cycAccess == null) {

            try {
                cycAccess = daxclr.inference.CycAPI.current();
            } catch (Exception e) {
                e.printStackTrace(java.lang.System.err);
            }
        }

        try {
            CycList gaf = cycAccess.makeCycList(request);
            CycFort mt = cycAccess.getKnownConstantByName("EverythingPSC");
            boolean isQueryTrue = true; //Cycsystem
            String qresult = null;
            if (gaf.first() instanceof CycSymbol) {
                qresult = ""+cycAccess.converse(request)[1];
                java.lang.System.out.println("converse "+request + "->" + qresult);
                if (qresult.equals("NIL") || qresult.equals("()") || qresult.equals("0")|| qresult.equals("0.0")) {
                    isQueryTrue  = false;
                }
            } else {
                isQueryTrue = cycAccess.isQueryTrue(gaf,mt);
            }
            if (isQueryTrue) {
                Answer=true; 
                java.lang.System.out.println("\nThe assertion\n" + gaf + "\nis true in the " + mt.cyclify());
            } else {
                Answer=false;
                java.lang.System.out.println("\nThe assertion\n" + gaf + "\nis not true in the " + mt.cyclify());
            }
        } catch (Exception e) {
            //cycAccess.close();
            cycAccess=null;
            e.printStackTrace(java.lang.System.err);
        }

        //if(cycAccess!=null) cycAccess.close();

        return Answer;
    }
}

