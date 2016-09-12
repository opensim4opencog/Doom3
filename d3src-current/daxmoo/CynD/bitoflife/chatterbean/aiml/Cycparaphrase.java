/*
Copyleft (C) 2005 Hélio Perroni Filho
xperroni@yahoo.com
ICQ: 2490863

This file is part of ChatterBean.

ChatterBean is free software; you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation; either version 2 of the License, or (at your option) any later version.

ChatterBean is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for more details.

You should have received a copy of the GNU General Public License along with ChatterBean (look at the Documents/ directory); if not, either write to the Free Software Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307 USA, or visit (http://www.gnu.org/licenses/gpl.txt).
*/

package bitoflife.chatterbean.aiml;

import org.opencyc.api.CycAccess;
import org.xml.sax.Attributes;

import bitoflife.chatterbean.Match;

public class Cycparaphrase extends TemplateNodeTag {
    public static CycAccess cycAccess = null;
    public String filter;
    /*
    Constructors
    */

    public Cycparaphrase(Attributes attributes) {
        filter= attributes.getValue(0);
    }

    public Cycparaphrase(Object... children) {
        super(children);
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

        if (cycAccess!=null) {

            //    if (((CycConnection)cycAccess.getCycConnection ()).isValidBinaryConnection()==false) {
            cycAccess=null;
            //    }
        }

        if (cycAccess == null) {

            try {
                cycAccess = daxclr.inference.CycAPI.current();
            } catch (Exception e) {
                // if (cycAccess!=null)
                //     { cycAccess.close(); cycAccess=null;}
                daxclr.inference.CycAPI.sharedCycAccessInstance=null;
                cycAccess=null;
                e.printStackTrace(java.lang.System.err);
            }
        }
        try {
            String cycResult;
            java.lang.System.out.println("Cycparaphrase in=["+aimlresult+"]");
            Object cyclObj=daxclr.inference.CycAPI.cyclify(aimlresult);
            bestResult=daxclr.inference.CycAPI.paraphrase (cyclObj);
        } catch (Exception e) {
            //if (cycAccess!=null){ cycAccess.close(); cycAccess=null;}
            daxclr.inference.CycAPI.sharedCycAccessInstance=null;
            cycAccess=null;

            e.printStackTrace(java.lang.System.err);
            // cycAccess=null; // it's bad and must be killed!
        }
        //if (cycAccess!=null){ cycAccess.close(); cycAccess=null;}

        java.lang.System.out.println("Cycparaphrase out=["+bestResult+"]");
        return bestResult;
    }

}

