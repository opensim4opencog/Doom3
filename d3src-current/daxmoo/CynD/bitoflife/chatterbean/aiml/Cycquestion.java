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

import org.opencyc.api.CycConnection;
import org.opencyc.cycobject.CycList;
import org.opencyc.cycobject.CycObject;
import org.opencyc.cycobject.CycSymbol;
import org.xml.sax.Attributes;

import bitoflife.chatterbean.Match;

public class Cycquestion extends TemplateNodeTag {
    public static daxclr.inference.CycAPI cycAccess = null;
    /*
    Constructors
    */

    public Cycquestion(Attributes attributes) {
    }

    public Cycquestion(Object... children) {
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
            query="(parse-a-question-completely \""+aimlresult+"\" #$AllEnglishLexicalMicrotheoryPSC '(:wff-check? t))";
            java.lang.System.out.println("Cycquestuion query=["+query+"]");
            Object guessList =cycAccess.converse(query)[1];

            if (guessList instanceof CycList) {
                //bestResult=((CycList)guessList).cyclify();
                // iterate throught the list and try each 
                for (int i=0; i<((CycList)guessList).size();i++) {
                    Object resultFort =null;

                    CycList parserQuery=(CycList)((CycList) guessList).get(i);
                    String SubQuery = "(cyc-query '"+parserQuery.cyclify()+" #$EverythingPSC)";
                    java.lang.System.out.println("Cycquestuion Subquery("+i+")=["+SubQuery+"]");

                    CycList resultList = null;
                    Object resultO =cycAccess.converse(SubQuery)[1];

                    if (resultO instanceof CycSymbol) {
                        resultList = new CycList();
                    } else resultList = (CycList)resultO;


                    bestResult=bestResult+" "+i+" ";
                    for (int j=0; j<resultList.size();j++) {
                        CycList answerBinding=(CycList)((CycList) resultList).get(j);

                        resultFort = getBinding((CycList)answerBinding.get(0));

                        java.lang.System.out.println("answerBinding "+j+ ":"+answerBinding);//.cyclify ());
                        java.lang.System.out.println("resultFort "+j+ ":"+resultFort);//.cyclify ());


                        //result = getBinding(pair).cyclify();
                        String resultText =""; //+ pair.get(0);
                        cycResult=""+daxclr.inference.CycAPI.paraphrase (resultFort);//.cyclify();
                        /*
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
                         */
                        bestResult=bestResult+" "+cycResult;

                    }
                    bestResult=bestResult+"." ;


                }

            } else if (guessList instanceof CycSymbol) {
                bestResult=((CycSymbol)guessList).cyclify();
            } else
                bestResult=((CycObject)guessList).cyclify();


        } catch (Exception e) {
            //if (cycAccess!=null) cycAccess.close();
            e.printStackTrace(java.lang.System.err);
        }
        //if (cycAccess!=null) cycAccess.close();
        if (bestResult.trim().equals("(NIL)")) bestResult="TRUE";
        if (bestResult.trim().equals("NIL")) bestResult="FALSE";

        return bestResult;
    }

    static public Object getBinding(CycList list) {
        Object o = list.getDottedElement();
        if (o!=null) return(o);
        list =(CycList) list.clone();
        list.remove(0);
        return list;
    }

}

