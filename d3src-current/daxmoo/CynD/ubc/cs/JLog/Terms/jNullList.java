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
//	NullList
//#########################################################################

package ubc.cs.JLog.Terms;

import java.lang.*;
import java.util.*;
import ubc.cs.JLog.Foundation.*;

public class jNullList extends jList
{
 public final static jNullList		NULL_LIST = new jNullList();
 public final static Enumeration	NULL_ENUM = new Enumeration()
					{
					 public boolean hasMoreElements() {return false;};
					 public Object  nextElement() {return null;};
					};
					
 protected jNullList()
 {
  type = TYPE_NULLLIST;
 };
 
 public String 		getName()
 {
  return LIST_NULL;
 };
 
 protected int 		compare(jTerm term,boolean first_call,boolean var_equal)
 {jTerm 		t = term.getTerm();
 
  if ((t instanceof jVariable) || (t instanceof jReal) || (t instanceof jInteger))
   return GREATER_THAN;
  
  if (t instanceof iPredicate)
  {iPredicate 	ip = (iPredicate) t;
   int 			compare_val;
   int 			arity_b;
   
   arity_b = ip.getArity();
   
   if (0 < arity_b)
    return LESS_THAN;
   else if (0 > arity_b)
    return GREATER_THAN;
   
   compare_val = getName().compareTo(ip.getName());
   
   if (compare_val < 0)
    return LESS_THAN;
   if (compare_val > 0)
    return GREATER_THAN;

   return EQUAL;
  }
  
  if (t instanceof jNullList)
   return EQUAL;

  if (t instanceof jListPair || t instanceof jCommand || t instanceof jConjunctTerm)
   return LESS_THAN;
   
  return (first_call ? -t.compare(this,false,var_equal) : EQUAL);
 };

 public boolean 	unify(jTerm term,jUnifiedVector v)
 {
  // if term is variable we let it handle the unification
  if (term.type == TYPE_VARIABLE)
   return term.unify(this,v);
   
  // many null lists may be same instances
  if (this == term)
   return true;
   
  return (type == term.type);
 };
  
 public void 		registerVariables(jVariableVector v)
 {
 };
 
 public void 		enumerateVariables(jVariableVector v,boolean all)
 {
 };

 public jTerm 		duplicate(jVariable[] vars)
 {
  return this; // since nulllists are constants, don't duplicate for memory and gc considerations
 };

 public jTerm 		copy(jVariableRegistry vars)
 {
  return this; // since nulllists are constants, don't duplicate for memory and gc considerations
 };

 public Enumeration		elements(iTermToObject conv)
 {
  return NULL_ENUM;
 };

 public String 		toString(boolean usename)
 {
  return LIST_NULL;
 };
};

