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
import org.xml.sax.Attributes;

import daxclr.bsf.ObjectRepository;


import bitoflife.chatterbean.Match;

public class Doom extends TemplateNodeTag {
    public static CycAccess cycAccess = null;
    public String filter;
    /*
    Constructors
    */

    public Doom(Attributes attributes) {
        filter= attributes.getValue(0);
    }

    public Doom(Object... children) {
        super(children);
    }

    /*
    Methods
    */

    public String toString(Match match) {
        String query;
        String aimlresult = super.toString(match);
        if (aimlresult == null || "".equals(aimlresult.trim())) return "";
	query=aimlresult;
        try {
            aimlresult = ""+ObjectRepository.resolverMap.get(query);             
	    java.lang.System.out.println("Doom("+query+") =["+aimlresult+"]");
        } catch (Exception e) {
            e.printStackTrace(java.lang.System.err);
	    aimlresult = query;
        }
        return ""+aimlresult;
    }

}

