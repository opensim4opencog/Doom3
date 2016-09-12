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
//	IntegerOnlyArithmetic
//#########################################################################
 
package ubc.cs.JLog.Builtins;

import java.lang.*;
import java.util.*;
import ubc.cs.JLog.Terms.*;

// integer only arithmetic class
abstract public class jIntegerOnlyArithmetic extends jArithmetic
{
 public jIntegerOnlyArithmetic(jTerm l,jTerm r)
 {
  super(l,r);
 };
  
 public  jTerm 		getValue()
 {jTerm 		l,r;
  
  l = lhs.getValue();
  r = rhs.getValue();

  if (l.type == TYPE_INTEGER && r.type == TYPE_INTEGER)
   return new jInteger(operatorInt(((jInteger) l).getIntegerValue(),
                                   ((jInteger) r).getIntegerValue()));
  else
   throw new InvalidArithmeticOperationException();
 };

 protected float 	operatorReal(float l,float r)
 {
  throw new InvalidArithmeticOperationException();
 };
};
