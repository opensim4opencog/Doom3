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
//	pVariableRegistry
//#########################################################################

package ubc.cs.JLog.Parser;

import java.lang.*;
import java.util.*;
import ubc.cs.JLog.Terms.*;

/**
* Hashtable for <code>pVar</code> variables and their associated <code>jVariables</code>.
* This class is used to ensure that the reference to a single named variable in the
* parse stream (usually at the level of a rule or command), which my be represented by
* several <code>pVar</code> packets, only produce a single instance of 
* <code>jVariable</code>. 
*  
* @author       Glendon Holst
* @version      %I%, %G%
*/
public class pVariableRegistry
{
 protected Hashtable 		variables;
 
 public pVariableRegistry()
 {
  variables = new Hashtable();
 };
 
 public jVariable 		getVariable(pVar v)
 {
  return getVariable(v.getName());
 };

 public jVariable 		getVariable(String v)
 {jVariable jv;
 
  if (v.equals("_"))
   return new jVariable();
   
  jv = (jVariable) variables.get(v);
  if (jv == null)
   variables.put(v,jv = new jVariable(v));

  return jv;
 };
};
