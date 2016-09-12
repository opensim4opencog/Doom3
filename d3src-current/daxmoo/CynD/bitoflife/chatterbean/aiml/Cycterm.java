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

import org.opencyc.api.CycAccess;
import org.opencyc.api.CycConnection;
import org.opencyc.cycobject.CycFort;
import org.opencyc.cycobject.CycList;
import org.opencyc.cycobject.CycNart;
import org.opencyc.cycobject.CycObject;
import org.opencyc.cycobject.CycSymbol;
import org.xml.sax.Attributes;

import bitoflife.chatterbean.Match;

public class Cycterm extends TemplateNodeTag {
    public static CycAccess cycAccess = null;
    public String filter;
    /*
    Constructors
    */

    public Cycterm(Attributes attributes) {
        filter= attributes.getValue(0);
    }

    public Cycterm(Object... children) {
        super(children);
    }

    public Cycterm(String filter, Object... children)
    {
      super(children);
      this.filter = filter;
    }

    /*
    Methods
    */

    public String toString(Match match) {
        String query;
        String bestResult="";
        String bestKey="";
        boolean filterflag=false;

        String aimlresult = super.toString(match);
        if (aimlresult == null || "".equals(aimlresult.trim())) return "";
        //   result="#$"+result;
        //   return result;

        if (cycAccess!=null) {
        
        //    if (((CycConnection)cycAccess.getCycConnection ()).isValidBinaryConnection()==false) {
                cycAccess=null;
        //    }
        }

       if (cycAccess == null) {
                cycAccess = daxclr.inference.CycAPI.current();
        }

        try {
            String cycResult;
            query="(denotation-mapper \""+aimlresult+"\")";
            java.lang.System.out.println("Cycterm Query=[ "+query+"] connectionstate=" +((CycConnection)cycAccess.getCycConnection ()).isValidBinaryConnection());
            Object guessList =cycAccess.converseObject(query);
            
            CycFort filterConstant=null;
            if (filter!=null)  filterConstant = cycAccess.getKnownConstantByName(filter);
            

            if (guessList instanceof CycList) {
                for (int i=0; i<((CycList)guessList).size();i++) {
                    CycObject resultFort =null;

                    CycList pair=(CycList)((CycList) guessList).get(i);
                    Object resultConstant = getBinding(pair);
                    if (resultConstant instanceof CycList) {
                        resultFort = new CycNart((CycList) resultConstant);
                    } else
                        resultFort = (CycObject) resultConstant;

                    java.lang.System.out.println("ResultFort "+i+ ":"+resultFort.cyclify ());
                    boolean isFilterTrue=false;
                    if (filterConstant!=null) {
                        isFilterTrue = cycAccess.isa((CycFort)resultFort,filterConstant) ||  cycAccess.isSpecOf((CycFort)resultFort,filterConstant);
                        java.lang.System.out.println("  Filter["+resultFort.cyclify()+ ":"+filterConstant.cyclify ()+"]="+isFilterTrue);

                    }
                    //result = getBinding(pair).cyclify();
                    String resultText =""+ pair.get(0);
                    cycResult=resultFort.cyclify();

                    if (isFilterTrue && !filterflag) {
                        bestKey=resultText;
                        bestResult=cycResult;
                        filterflag =isFilterTrue;
                    }
                    else
                        if( !(!isFilterTrue && filterflag )) {
                        
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
                }

            } else if (guessList instanceof CycSymbol) {
                     bestResult="NIL";
            }
            // if system fails then try the trusty backup "fi-complete"
            if (bestResult.equals ("NIL")) {
                query="(fi-complete \""+aimlresult+"\")";
                Object guessList2 =cycAccess.converseObject(query);

                if (guessList2 instanceof CycList) {
                    for (int i=0; i<((CycList)guessList2).size();i++) {
                        CycObject resultFort =null;

                        //CycList pair=(CycList)((CycList) guessList).get(i);
                        Object resultConstant = ((CycList)guessList2).get(i); //getBinding(pair);
                        if (resultConstant instanceof CycList) {
                            resultFort = new CycNart((CycList) resultConstant);
                        } else
                            resultFort = (CycObject) resultConstant;

                        java.lang.System.out.println("ResultFort "+i+ ":"+resultFort.cyclify ());

                        //result = getBinding(pair).cyclify();
                        cycResult=resultFort.cyclify();
                        String resultText =cycResult;    // Maybe we could do a reverse lookup ???
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

                } else if (guessList instanceof CycSymbol) {
                         bestResult="NIL";
                }


            }

        } catch (Exception e) {
            //if (cycAccess!=null){ cycAccess.close(); cycAccess=null;}
            cycAccess=null;

            e.printStackTrace(java.lang.System.err);
           // cycAccess=null; // it's bad and must be killed!
        }
        //if (cycAccess!=null){ cycAccess.close(); cycAccess=null;}

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

