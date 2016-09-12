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
//	AnimateGoal
//#########################################################################
 
package ubc.cs.JLog.Animation;

import java.lang.*;
import java.util.*;
import java.awt.*;
import ubc.cs.JLog.Foundation.*;
import ubc.cs.JLog.Terms.*;

/**
 * Goal for displaying animated graphics.
 *
 * @author       Glendon Holst
 * @version      %I%, %G%
 */
public class jAnimateGoal extends jGoal
{
 protected jAnimate 		animate;

 // for use by animate
 public jTerm				term = null;
 public jUnifiedVector 		unified;
 
 public 	jAnimateGoal(jAnimate a,jTerm t)
 {
  animate = a;
  term = t;
  unified = new jUnifiedVector();
 };
 
 public boolean 	prove(iGoalStack goals,iGoalStack proved)
 {Thread 		t;
  
  t = Thread.currentThread();
  
  if (t instanceof jPrologServiceThread)
  {jPrologServiceThread		pst = (jPrologServiceThread) t;
   jPrologServices			prolog = pst.getPrologServices();
   aAnimationEnvironment 	animation = prolog.getAnimationEnvironment();

   if (animate == null)
    throw new InvalidAnimationAPIException("Animation Environment is not available.");
	
   if (animate.prove(this,animation))
   {
    proved.push(this);
    return true;
   }
  }
  { // we need to initialize goal to potentially restart
   unified.restoreVariables();
  }
  goals.push(this); // a retry that follows may need a node to remove or retry
  return false;
 };

 public boolean 	retry(iGoalStack goals,iGoalStack proved)
 {
  unified.restoreVariables();
  
  goals.push(this); // a retry that follows may need a node to remove or retry
  return false;
 }; 
 
 public String 		getName() 
 {
  return animate.getName();
 };
 
 public int 		getArity() 
 {
  return animate.getArity();
 };
 
 public String 		toString()
 {StringBuffer 	sb = new StringBuffer();
   
  sb.append(getName()+"/"+String.valueOf(getArity())+" goal: ");
  sb.append(getName()+"("+term.toString()+")");
  
  return sb.toString();
 };
};

 