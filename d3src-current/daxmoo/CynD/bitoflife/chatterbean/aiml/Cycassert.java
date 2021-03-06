/*
Copyleft (C) 2005 H�lio Perroni Filho
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

import org.opencyc.api.CycAccess;
import org.opencyc.api.CycConnection;
import org.opencyc.cycobject.CycList;
import org.opencyc.cycobject.CycObject;
import org.opencyc.cycobject.CycSymbol;
import org.xml.sax.Attributes;

import bitoflife.chatterbean.Match;

public class Cycassert extends TemplateNodeTag {
    public static CycAccess cycAccess = null;
    /*
    Constructors
    */

    public Cycassert(Attributes attributes) {
    }

    public Cycassert(Object... children) {
        super(children);
    }

    /*
    Methods
    */

    public String toString(Match match) {
        String query;
        String bestResult="";
        String bestKey="";

        String aimlresult = super.toString(match);
        if (aimlresult == null || "".equals(aimlresult.trim())) return "";
        //   result="#$"+result;
        //   return result;
        if (cycAccess!=null) {
        
            if (((CycConnection)cycAccess.getCycConnection ()).isValidBinaryConnection()==false) {
                cycAccess=null;
            }
        }
        if (cycAccess == null) {

            try {
                cycAccess = daxclr.inference.CycAPI.current();
            } catch (Exception e) {
                //if (cycAccess!=null) cycAccess.close();
                e.printStackTrace(java.lang.System.err);
            }
        }

        try {
            String cycResult;
            query="(cyc-assert '"+aimlresult+")";
            java.lang.System.out.println("Cycassert query=["+query+"]");
            Object guessList =cycAccess.converseObject(query);

            if (guessList instanceof CycList) {
                bestResult=((CycList)guessList).cyclify();
                /*
                for (int i=0; i<((CycList)guessList).size();i++) {
                    CycObject resultFort =null;

                    CycList pair=(CycList)((CycList) guessList).get(i);
                    Object resultConstant = getBinding(pair);
                    if (resultConstant instanceof CycList) {
                        resultFort = new CycNart((CycList) resultConstant);
                    } else
                        resultFort = (CycObject) resultConstant;

                    java.lang.System.out.println("ResultFort "+i+ ":"+resultFort.cyclify ());

                    //result = getBinding(pair).cyclify();
                    String resultText =""+ pair.get(0);
                    cycResult=resultFort.cyclify();
                    if (resultText.length()>bestKey.length()) {
                        // more text
                        bestKey=resultText;
                        bestResult=cycResult;
                    }else
                    if ( (resultText.length()==bestKey.length()) && (cycResult.length()<bestResult.length()) )
                    {
                        // same text coverage, shorter answer
                        bestKey=resultText;
                        bestResult=cycResult;
                    }
                }
                 */
            } else if (guessList instanceof CycSymbol) {
                     bestResult=((CycSymbol)guessList).cyclify();
            } else
                    bestResult=((CycObject)guessList).cyclify();


        } catch (Exception e) {
            //if (cycAccess!=null) cycAccess.close();
            e.printStackTrace(java.lang.System.err);
        }
        //if (cycAccess!=null) cycAccess.close();
        if (bestResult.trim().equals("(NIL)") ) bestResult="TRUE";
        if (bestResult.trim().equals("NIL") ) bestResult="FALSE";

         return bestResult;
    }

    
}

