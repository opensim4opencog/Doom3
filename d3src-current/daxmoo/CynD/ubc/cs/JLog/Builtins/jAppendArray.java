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
//	AppendArray
//#########################################################################
 
package ubc.cs.JLog.Builtins;

import java.lang.*;
import java.util.*;
import ubc.cs.JLog.Terms.*;
import ubc.cs.JLog.Foundation.*;
import ubc.cs.JLog.Builtins.Goals.*;

public class jAppendArray extends jBinaryBuiltinPredicate
{
 /**
  * Constructor for <code>jAppendArray</code>.
  *
  * @param l 	The array term (should be bound to CompoundTerm before prove).
  * @param r 	The term to add to the end of the array.
  * 			
  */
 public jAppendArray(jTerm l,jTerm r)
 {
  super(l,r,TYPE_BUILTINPREDICATE);
 };
  
 public String 		getName()
 {
  return "APPENDARRAY";
 };
 
 public final boolean 	prove(jAppendArrayGoal aag)
 {jTerm 	l,r;
  
  l = aag.lhs.getTerm();
  r = aag.rhs.getTerm().copy();
   
   
  if (l instanceof jCompoundTerm)
  {jCompoundTerm 	ct = (jCompoundTerm) l;
  
   ct.addTerm(r);
  }
  else
   throw new ExpectedCompoundTermException();
   
  return true;
 };

 public void 		addGoals(jGoal g,jVariable[] vars,iGoalStack goals)
 {
  goals.push(new jAppendArrayGoal(this,lhs.duplicate(vars),rhs.duplicate(vars)));
 }; 

 public void 		addGoals(jGoal g,iGoalStack goals)
 {
  goals.push(new jAppendArrayGoal(this,lhs,rhs));
 }; 

 public jBinaryBuiltinPredicate 		duplicate(jTerm l,jTerm r)
 {
  return new jAppendArray(l,r); 
 };
};

