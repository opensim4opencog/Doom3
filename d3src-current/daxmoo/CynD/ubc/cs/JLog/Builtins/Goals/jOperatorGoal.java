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
//	OperatorGoal
//#########################################################################
 
package ubc.cs.JLog.Builtins.Goals;

import java.lang.*;
import java.util.*;
import ubc.cs.JLog.Terms.*;
import ubc.cs.JLog.Foundation.*;
import ubc.cs.JLog.Builtins.*;

public class jOperatorGoal extends jGoal
{
 protected jOperator 			op;

 // for use by operators
 public jTerm 				lhs,rhs;

 public 	jOperatorGoal(jOperator o,jTerm l,jTerm r)
 {
  op = o;
  lhs = l;
  rhs = r;
 };
 
 public boolean 	prove(iGoalStack goals,iGoalStack proved)
 {
  if (op.prove(this))
  {
   proved.push(this);
   return true;
  }
  else
  {
   goals.push(this); // a retry that follows may need a node to remove or retry
   return false;
  }
 };

 public boolean 	retry(iGoalStack goals,iGoalStack proved)
 {
  // by default, we assume that builtins only produce one result.
  
  goals.push(this); // a retry that follows may need a node to remove or retry
  return false;
 }; 

 public String 		getName() 
 {
  return op.getName();
 };
 
 public int 		getArity() 
 {
  return op.getArity();
 };
 
 public String 		toString()
 {StringBuffer 	sb = new StringBuffer();
   
  sb.append(getName()+"/"+String.valueOf(getArity())+" goal: ");
  sb.append(getName()+"("+lhs.toString()+","+rhs.toString()+")");
  
  return sb.toString();
 };
};
