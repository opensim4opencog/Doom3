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
//	IntersectArray
//#########################################################################
 
package ubc.cs.JLog.Builtins;

import java.lang.*;
import java.util.*;
import ubc.cs.JLog.Terms.*;
import ubc.cs.JLog.Foundation.*;
import ubc.cs.JLog.Builtins.Goals.*;

public class jIntersectArray extends jTrinaryBuiltinPredicate
{
 public jIntersectArray(jTerm t1,jTerm t2,jTerm t3)
 {
  super(t1,t2,t3,TYPE_BUILTINPREDICATE);
 };
  
 public String 		getName()
 {
  return "INTERSECTARRAY";
 };
 
 public final boolean 	prove(jIntersectArrayGoal iag)
 {jTerm 	t1,t2,t3;
  
  t1 = iag.term1.getTerm();
  t2 = iag.term2.getTerm();
  t3 = iag.term3.getTerm();
   
  if (t1.type == iType.TYPE_COMPOUND && t2.type == iType.TYPE_COMPOUND && 
		t3.type == iType.TYPE_COMPOUND) 
  {jCompoundTerm 	ct1 = (jCompoundTerm) t1;
   jCompoundTerm 	ct2 = (jCompoundTerm) t2;
   jCompoundTerm 	ct3 = (jCompoundTerm) t3;

   ct1.copyCompoundTerm(ct2);
   ct1.intersectionCompoundTerm(ct3);
  }
  else
   throw new ExpectedCompoundTermException();
   
  return true;
 };

 public void 		addGoals(jGoal g,jVariable[] vars,iGoalStack goals)
 {
  goals.push(new jIntersectArrayGoal(this,term1.duplicate(vars),term2.duplicate(vars),term3.duplicate(vars)));
 }; 

 public void 		addGoals(jGoal g,iGoalStack goals)
 {
  goals.push(new jIntersectArrayGoal(this,term1,term2,term3));
 }; 

 public jTrinaryBuiltinPredicate 		duplicate(jTerm t1,jTerm t2,jTerm t3)
 {
  return new jIntersectArray(t1,t2,t3); 
 };
};

