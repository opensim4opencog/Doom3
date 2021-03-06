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
//	ExpandTerm
//#########################################################################
 
package ubc.cs.JLog.Builtins;

import java.lang.*;
import java.util.*;
import ubc.cs.JLog.Terms.*;
import ubc.cs.JLog.Foundation.*;
import ubc.cs.JLog.Terms.Goals.*;

public class jExpandTerm extends jQuadaryBuiltinPredicate
{
 public jExpandTerm(jTerm l,jTerm r,jTerm S,jTerm E)
 {
  super(l,r,S,E,TYPE_BUILTINPREDICATE);
 };
  
 public String 		getName()
 {
  return "expand_term";
 };
 
 public boolean 	prove(jQuadaryBuiltinPredicateGoal eg)
 {jTerm 	l,r;
  jDCG 		dcg;
 
  l = eg.term1.getTerm();
  
  if (l instanceof jDCG)
   dcg = (jDCG) l;
  else
   dcg = new jDCG(l);
  
  r = eg.term2.getTerm();
  
  return r.unify(dcg.makeDCGTerm(eg.term3.getTerm(),eg.term4.getTerm()),eg.unified);
 };

 public jQuadaryBuiltinPredicate 		duplicate(jTerm t1,jTerm t2,jTerm t3,jTerm t4)
 {
  return new jExpandTerm(t1,t2,t3,t4); 
 };
};

