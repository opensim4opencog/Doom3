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
//	Integer
//#########################################################################

package ubc.cs.JLog.Terms;

import java.lang.*;
import java.util.*;
import ubc.cs.JLog.Foundation.*;

public class jInteger extends jTerm
{
 protected int 		value;

 public jInteger(int v)
 {
  value = v;
  type = TYPE_INTEGER;
 };

 public String 		getName()
 {
  return String.valueOf(value);
 };
 
 public int 		getIntegerValue()
 {
  return value;
 };

 protected int 		compare(jTerm term,boolean first_call,boolean var_equal)
 {jTerm 		t = term.getTerm();
 
  if (t instanceof jVariable)
   return GREATER_THAN;
   
  if (t instanceof jReal)
   return GREATER_THAN;
   
  if (t instanceof jInteger)
  {int 	i = ((jInteger) t).getIntegerValue();
  
   if (value < i)
    return LESS_THAN;
   else if (value > i)
    return GREATER_THAN;
   else
    return EQUAL;
  }
  
  if (t instanceof iPredicate)
   return LESS_THAN;
  
  return (first_call ? -t.compare(this,false,var_equal) : EQUAL);
 };
 
 public boolean 	unify(jTerm term,jUnifiedVector v)
 {
  // if term is variable we let it handle the unification
  if (term.type == TYPE_VARIABLE)
   return term.unify(this,v);
   
  // many integer may be same instances
  if (this == term)
   return true;
   
  if (type != term.type)
   return false;

  // altough we cannot be certain that term is a jInteger, if it is not then type was wrong
  // so this warrents a failing exception.
  return (value == ((jInteger) term).value);
 };
  
 public void 		registerVariables(jVariableVector v)
 {
 };
 
 public void 		enumerateVariables(jVariableVector v,boolean all)
 {
 };

 public jTerm 		duplicate(jVariable[] vars)
 {
  return this; // since integers are constants, don't duplicate for memory and gc considerations
 };

 public jTerm 		copy(jVariableRegistry vars)
 {
  return this; // since integers are constants, don't duplicate for memory and gc considerations
 };

 public String 		toString(boolean usename)
 {
  return String.valueOf(value);
 };
};
