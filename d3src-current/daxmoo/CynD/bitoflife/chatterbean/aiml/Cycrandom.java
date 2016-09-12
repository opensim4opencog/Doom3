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
import org.opencyc.cycobject.CycList;
import org.opencyc.cycobject.CycNart;
import org.opencyc.cycobject.CycObject;
import org.opencyc.cycobject.CycSymbol;
import org.xml.sax.Attributes;

import bitoflife.chatterbean.Match;

public class Cycrandom extends TemplateNodeTag {
    public static CycAccess cycAccess = null;
    /*
    Constructors
    */

    public Cycrandom(Attributes attributes) {
    }

    public Cycrandom(Object... children) {
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
            query=aimlresult;
            java.lang.System.out.println("CycRandom aimlresult=["+aimlresult+"]");
            Object guessList =cycAccess.converseObject(query);
            java.lang.System.out.println("CycRandom Cycguess=["+((CycList)guessList).cyclify()+"]");
            if (guessList instanceof CycList) {
                //for (int i=0; i<((CycList)guessList).size();i++) {
                java.util.Random   randseq = new java.util.Random();
                int nodeCount=((CycList)guessList).size();
                int nodeSelect;
                if (nodeCount==0) {
                    return null;
                }
                if (nodeCount==1)
                    nodeSelect=0;
                else
                    nodeSelect=(int)Math.ceil(nodeCount * randseq.nextDouble())-1;

                if (nodeSelect<0) {
                    nodeSelect=0;
                }
                int i=nodeSelect;
                //return RandomList.get(nodeSelect).toString(match);

                CycObject resultFort =null;

                CycList pair1=(CycList)((CycList) guessList).get(i);
                CycList pair2=(CycList)pair1.get(0);
                Object resultConstant = getBinding(pair2);
                String resultText =""+ pair2.get(0);

            if (resultConstant instanceof CycList) {
                resultFort = new CycNart((CycList) resultConstant);
                cycResult=resultFort.cyclify();
            } else if (resultConstant instanceof String) {
                cycResult=(String) resultConstant;
            } else if (resultConstant instanceof Number) {
                cycResult= resultConstant.toString();
            } else if (resultConstant instanceof CycObject) {
                resultFort = (CycObject) resultConstant;
                cycResult=resultFort.cyclify();
            } else
                cycResult=""+resultConstant;

                java.lang.System.out.println("ResultFort "+i+ ":"+cycResult);

                //result = getBinding(pair).cyclify();

                if (resultText.length()>bestKey.length()) {
                    // more text
                    bestKey=resultText;
                    bestResult=cycResult;
                } else
                    if ((resultText.length()==bestKey.length()) && (cycResult.length()<bestResult.length())) {
                    // same text coverage, shorter answer
                    bestKey=resultText;
                    bestResult=cycResult;
                }
                //}

            } else if (guessList instanceof CycSymbol) {
                bestResult="NIL";
            }

        } catch (Exception e) {
            //if (cycAccess!=null) cycAccess.close();
            e.printStackTrace(java.lang.System.err);
        }
        //if (cycAccess!=null) cycAccess.close();

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
