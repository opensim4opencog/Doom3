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
//	TermToListGoal
//#########################################################################
 
package ubc.cs.JLog.Builtins.Goals;

import java.lang.*;
import java.util.*;
import ubc.cs.JLog.Terms.*;
import ubc.cs.JLog.Foundation.*;
import ubc.cs.JLog.Parser.*;
import ubc.cs.JLog.Builtins.*;

public class jTermToListGoal extends jGoal
{
 protected jTermToList 		ttl;
 
 // for use by ttl
 public jTerm 				lhs,rhs;
 public jUnifiedVector 		unified;
 
 public 	jTermToListGoal(jTermToList t,jTerm l,jTerm r)
 {
  ttl = t;
  lhs = l;
  rhs = r;
  unified = new jUnifiedVector();
 };

 public boolean 	prove(iGoalStack goals,iGoalStack proved)
 {Thread 		t;
  
  t = Thread.currentThread();
  
  if (t instanceof jPrologServiceThread)
  {jPrologServiceThread			pst = (jPrologServiceThread) t;
   jPrologServices				prolog = pst.getPrologServices();
   pPredicateRegistry 			registry = prolog.getPredicateRegistry();
   pOperatorRegistry 			oregistry = prolog.getOperatorRegistry();
    
   if (ttl.prove(this,registry,oregistry))
   {
    proved.push(this);
    return true;
   }
  }
  {
   { // we need to initialize goal to potentially restart
    unified.restoreVariables();
   }
   goals.push(this); // a retry that follows may need a node to remove or retry
   return false;
  } 
 };

 public boolean 	retry(iGoalStack goals,iGoalStack proved)
 {
  unified.restoreVariables();
  
  goals.push(this); // a retry that follows may need a node to remove or retry
  return false;
 }; 
 
 public void 	internal_restore(iGoalStack goals)
 {
  unified.restoreVariables();
 };

 public String 		getName() 
 {
  return ttl.getName();
 };
 
 public int 		getArity() 
 {
  return ttl.getArity();
 };
 
 public String 		toString()
 {StringBuffer 	sb = new StringBuffer();
   
  sb.append(getName()+"/"+String.valueOf(getArity())+" goal: ");
  sb.append(getName()+"("+lhs.toString()+","+rhs.toString()+")");
  
  return sb.toString();
 };
};

 