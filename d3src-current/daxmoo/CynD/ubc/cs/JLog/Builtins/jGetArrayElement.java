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
//	GetArrayElement
//#########################################################################
 
package ubc.cs.JLog.Builtins;

import java.lang.*;
import java.util.*;
import ubc.cs.JLog.Terms.*;
import ubc.cs.JLog.Foundation.*;
import ubc.cs.JLog.Builtins.Goals.*;

public class jGetArrayElement extends jTrinaryBuiltinPredicate
{
 /**
  * Constructor for <code>jGetArrayElement</code>.
  *
  * @param t1 	The array term (should be bound to a jCompoundTerm).
  * @param t2 	The term (should be bound to jInteger) specifying the zero-based element index.
  * @param t3 	The term representing the element at index t2 in array t1.
  * 			
  */
 public jGetArrayElement(jTerm t1,jTerm t2,jTerm t3)
 {
  super(t1,t2,t3,TYPE_BUILTINPREDICATE);
 };
  
 public String 		getName()
 {
  return "GETARRAYELEMENT";
 };
 
 public final boolean 	prove(jGetArrayElementGoal gaeg)
 {jTerm 	t1,t2,t3;
  
  t1 = gaeg.term1.getTerm();
  t2 = gaeg.term2.getTerm();
  t3 = gaeg.term3.getTerm().copy();
   
  if (t1 instanceof jCompoundTerm)
  {jCompoundTerm 	ct = (jCompoundTerm) t1;

   if (t2.type == TYPE_INTEGER)
   {int		index = ((jInteger) t2).getIntegerValue();
	 
    return ct.elementAt(index).getTerm().unify(t3,new jUnifiedVector());
   }
   else 
    throw new InvalidArgArgumentException();
  }
  else
   throw new ExpectedCompoundTermException();
 };

 public void 		addGoals(jGoal g,jVariable[] vars,iGoalStack goals)
 {
  goals.push(new jGetArrayElementGoal(this,
					term1.duplicate(vars),term2.duplicate(vars),term3.duplicate(vars)));
 }; 

 public void 		addGoals(jGoal g,iGoalStack goals)
 {
  goals.push(new jGetArrayElementGoal(this,term1,term2,term3));
 }; 

 public jTrinaryBuiltinPredicate 		duplicate(jTerm t1,jTerm t2,jTerm t3)
 {
  return new jGetArrayElement(t1,t2,t3); 
 };
};

