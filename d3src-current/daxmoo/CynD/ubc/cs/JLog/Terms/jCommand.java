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
//	Command
//#########################################################################

package ubc.cs.JLog.Terms;

import java.lang.*;
import java.util.*;
import ubc.cs.JLog.Foundation.*;

public class jCommand extends jTerm
{
 protected jTerm 		rhs = null;
 
 public jCommand(jTerm r)
 {
  rhs = r;
  type = TYPE_COMMAND;
 };
 
 public jTerm 		getRHS()
 {
  return rhs;
 };
 
 public int 	compare(jTerm term,boolean first_call,boolean var_equal)
 {jTerm 	t = term.getTerm();
 
  if ((t instanceof jVariable) || (t instanceof jReal) || (t instanceof jInteger))
   return GREATER_THAN;
   
  if (t instanceof iPredicate)
  {iPredicate 	ip = (iPredicate) t;
   int 			compare_val;
   int 			arity_b;
   
   arity_b = ip.getArity();
   
   if (1 < arity_b)
    return LESS_THAN;
   else if (1 > arity_b)
    return GREATER_THAN;
   
   compare_val = getName().compareTo(ip.getName());
   
   if (compare_val < 0)
    return LESS_THAN;
   if (compare_val > 0)
    return GREATER_THAN;

   return EQUAL;
  }
 
  if (t instanceof jCommand)
  {jTerm 	rt = ((jCommand) t).rhs;
   int 		compare_val;
   
   compare_val = getName().compareTo(t.getName());
   
   if (compare_val < 0)
    return LESS_THAN;
   if (compare_val > 0)
    return GREATER_THAN;
  
   return rhs.compare(rt,true,var_equal);
  }
  
  return (first_call ? -t.compare(this,false,var_equal) : EQUAL);
 };

 public boolean 	requiresCompleteVariableState()
 {
  return rhs.requiresCompleteVariableState();
 };
 
 public void 		registerUnboundVariables(jUnifiedVector v)
 {
  rhs.registerUnboundVariables(v);
 };

 public boolean 	unify(jTerm term,jUnifiedVector v)
 {
  // if term is variable we let it handle the unification
  if (term.type == TYPE_VARIABLE)
   return term.unify(this,v);

  // only unify with other cons terms of same type
  if (type != term.type)
   return false;
 
  // altough we cannot be certain that term is a jCommand, if it is not then type 
  // was wrong so this warrents a failing exception.
  {jCommand 			cterm;
   int 					sz;
   
   cterm = (jCommand) term;
   
  // unify each element of cons term, exit on first unification failure.

   return rhs.unify(cterm.rhs,v);
  }
 };

 public void 		registerVariables(jVariableVector v)
 {
  rhs.registerVariables(v);
 };
 
 public void 		enumerateVariables(jVariableVector v,boolean all)
 {
  rhs.enumerateVariables(v,all);
 };

 public jTerm 		duplicate(jVariable[] vars)
 {
  return new jCommand(rhs.duplicate(vars));
 };
 
 public jTerm 		copy(jVariableRegistry vars)
 {
  return new jCommand(rhs.copy(vars));
 };

 public void 		consult(jKnowledgeBase kb)
 {
  rhs.consult(kb);
 };
 
 public void 		consultReset()
 {
  rhs.consultReset();
 };

 public String 		toString(boolean usename)
 {
  return ":- " + rhs.toString(usename);
 };
};
