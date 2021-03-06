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
//	UnifiedVector
//#########################################################################

package ubc.cs.JLog.Foundation;

import java.lang.*;
import java.util.*;
import ubc.cs.JLog.Terms.*;

/**
* This class is used to register a <code>jVariable</code> after it is bound 
* during unification.
* A single call can then return those variables back to their unbound state.
*  
* @author       Glendon Holst
* @version      %I%, %G%
*/
public class jUnifiedVector
{
 protected Vector 		variables;
 
 public jUnifiedVector()
 {
  variables = new Vector();
 };
 
 /**
  * Registers a variable with this instance.  Should be invoked by the 
  * <code>jVariable</code> after it binds itself during unification.
  *
  * @param v 		the variable to register.
  */
 public final void 		addVariable(jVariable v)
 {
  variables.addElement(v); 
 };
 
 public final int 		size()
 {
  return variables.size();
 };
 
 public final boolean 	isEmpty()
 {
  return variables.isEmpty();
 };
 
/**
  * Restores all registered variables to their unbound state, and removes the 
  * variables from this registery. Only call once, since after restoration the 
  * vector is empty (ready for re-use)
  */
 public final void		restoreVariables()
 {int 		i,sz = variables.size();
  
  for (i = 0; i < sz; i++)
   ((jVariable) variables.elementAt(i)).setBinding(null);
   
  variables.removeAllElements();
 };
};