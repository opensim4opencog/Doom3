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
//	jInvoke5
//#########################################################################
 
package ubc.cs.JLog.Extras.BSF;

import ubc.cs.JLog.Builtins.Goals.*;
import ubc.cs.JLog.Foundation.*;
import ubc.cs.JLog.Terms.*;

/**
 * This file is part of the JLog BSF library.
 *
 * @author       Ulf Dittmer (Copyright 2005)
 */

public class jInvoke5 extends jNaryBuiltinPredicate 
{
 protected JLogBSFEngine engine;

 public jInvoke5 (jCompoundTerm args) 
 {
  super(args, TYPE_BUILTINPREDICATE);
  this.engine = JLogBSFInit.getBSFEngine();
 };
  
 public jInvoke5 (jTerm t1, jTerm t2, jTerm t3, jTerm t4, jTerm t5) 
 {
  super(init5CompoundTerm(t1, t2, t3, t4, t5), TYPE_BUILTINPREDICATE);
  this.engine = JLogBSFInit.getBSFEngine();
 };
  
 protected static jCompoundTerm		init5CompoundTerm(jTerm t1, jTerm t2, jTerm t3, 
																jTerm t4, jTerm t5)
 {jCompoundTerm		ct = new jCompoundTerm(5);
 
  ct.addTerm(t1);
  ct.addTerm(t2);
  ct.addTerm(t3);
  ct.addTerm(t4);
  ct.addTerm(t5);
  
  return ct; 
 };
  
 public String getName()
 {
  return "invoke";
 };

 public void addGoals(jGoal g,jVariable[] vars,iGoalStack goals)
 {jCompoundTerm		ct = (jCompoundTerm) arguments.duplicate(vars);
 
  goals.push(new jInvokeGoal(ct.elementAt(0), ct.elementAt(1), ct.elementAt(2),
						   ct.elementAt(3), ct.elementAt(4), engine));
 }; 

 public void addGoals (jGoal g,iGoalStack goals)
 {jCompoundTerm		ct = arguments;
 
  goals.push(new jInvokeGoal(ct.elementAt(0), ct.elementAt(1), ct.elementAt(2),
						   ct.elementAt(3), ct.elementAt(4), engine));
 }; 

 public jNaryBuiltinPredicate duplicate (jCompoundTerm args)
 {
  return new jInvoke5(args); 
 };
};

