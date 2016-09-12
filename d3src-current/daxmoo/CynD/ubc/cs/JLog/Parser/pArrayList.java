/*
    This file is part of JLog.

    Created by Glendon Holst for Alan Mackworth and the 
    "Computational Intelligence: A Logical Approach" text.
    
    Copyright 1998, 2000, 2002 by University of British Columbia and 
    Alan Mackworth.
    
    This notice must remain in all files which belong to, or are derived 
    from JLog.
    
    Check <http://jlogic.sourceforge.net/> or 
    <http://sourceforge.net/projects/jlogic> for further information
    about JLog, or to contact the authors.
     
    JLog is free software; you can redistribute it and/or modify
    it under the terms of the GNU General Public License as published by
    the Free Software Foundation; either version 2 of the License, or
    (at your option) any later version.

    JLog is distributed in the hope that it will be useful,
    but WITHOUT ANY WARRANTY; without even the implied warranty of
    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
    GNU General Public License for more details.

    You should have received a copy of the GNU General Public License
    along with JLog; if not, write to the Free Software
    Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA
    URLs: <http://www.fsf.org> or <http://www.gnu.org>
*/
//#########################################################################
//	pArrayList
//#########################################################################

package ubc.cs.JLog.Parser;

import java.util.*;
import java.lang.*;
import ubc.cs.JLog.Terms.*;

class pArrayList extends pPacket
{
 public 	pArrayList(pArray pt)
 {
  super(pt);
 };
 
 public jTerm 		getTerm(pVariableRegistry vars,pTermToPacketHashtable phash)
 {jTerm 	term;
 
  term = createArrayList();
  phash.putPacket(term,this);
  
  return term;
 };
 
 protected jList 	createArrayList()
 {jList 			start = null;
  jListPair 			last = null;
  int 				pos,max;
  String 			s;
   
  s = token.getToken();
   
  if (s.length() <= 0)
   return jNullList.NULL_LIST;
   
  start = last = new jListPair(new jInteger(s.charAt(0)),null);
   
  for (pos = 1, max = s.length(); pos < max; pos++)
   last.setTail(last = new jListPair(new jInteger(s.charAt(pos)),null));
    
  last.setTail(jNullList.NULL_LIST);
  return start;
 };

 public void 		setGeneric(boolean genericpred)
 {
 };
};

