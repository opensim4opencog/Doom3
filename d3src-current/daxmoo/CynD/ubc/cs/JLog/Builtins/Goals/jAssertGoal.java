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
//	AssertGoal
//#########################################################################
 
package ubc.cs.JLog.Builtins.Goals;

import java.lang.*;
import java.util.*;
import ubc.cs.JLog.Foundation.*;
import ubc.cs.JLog.Terms.*;
import ubc.cs.JLog.Builtins.*;

public class jAssertGoal extends jGoal
{
 protected jAssert 			assert_it;
 
 // for use by assert
 public jTerm 			term;
 public boolean 		addlast;
 
 public 	jAssertGoal(jAssert a,jTerm t,boolean al)
 {
  assert_it = a;
  term = t;
  addlast = al;
 };
 
 public boolean 	prove(iGoalStack goals,iGoalStack proved)
 {Thread 		t;
  
  t = Thread.currentThread();
  
  if (t instanceof jPrologServiceThread)
  {jPrologServiceThread		pst = (jPrologServiceThread) t;
   jPrologServices			prolog = pst.getPrologServices();
   jKnowledgeBase 			database = prolog.getKnowledgeBase();

   if (assert_it.prove(this,database))
   {
    proved.push(this);
    return true;
   }
  }
  goals.push(this); // a retry that follows may need a node to remove or retry
  return false;
 };

 public boolean 	retry(iGoalStack goals,iGoalStack proved)
 {
  goals.push(this); // a retry that follows may need a node to remove or retry
  return false;
 };
  
 public String 		getName() 
 {
  return assert_it.getName();
 };
 
 public int 		getArity() 
 {
  return assert_it.getArity();
 };
 
 public String 		toString()
 {StringBuffer 	sb = new StringBuffer();
   
  sb.append(getName()+"/"+String.valueOf(getArity())+" goal: ");
  sb.append(getName()+"("+term.toString()+")");
  
  return sb.toString();
 };
};

 