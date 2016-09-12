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
//	NumericComparison
//#########################################################################
 
package ubc.cs.JLog.Builtins;

import java.lang.*;
import java.util.*;
import ubc.cs.JLog.Terms.*;
import ubc.cs.JLog.Builtins.Goals.*;

abstract public class jNumericComparison extends jOperator
{
 public jNumericComparison(jTerm l,jTerm r)
 {
  super(l,r,TYPE_NUMERICCOMPARE);
 };
 
 public boolean 	prove(jOperatorGoal og)
 {jTerm 	l,r;
  float 	rl,rr;
 
  l = og.lhs.getValue();
  r = og.rhs.getValue();
  
  if (l.type == TYPE_INTEGER && r.type == TYPE_INTEGER)
   return compareInteger(((jInteger) l).getIntegerValue(),((jInteger) r).getIntegerValue());
   
  if (l.type == TYPE_ATOM && r.type == TYPE_ATOM)
   return compareString(l.getName(),r.getName());
  
  if (l.type == TYPE_INTEGER)
   rl = (float) ((jInteger) l).getIntegerValue();
  else if (l.type == TYPE_REAL)
   rl = ((jReal) l).getRealValue();
  else
   return false;
  
  if (r.type == TYPE_INTEGER)
   rr = (float) ((jInteger) r).getIntegerValue();
  else if (r.type == TYPE_REAL)
   rr = ((jReal) r).getRealValue();
  else
   return false;

  return compareReal(rl,rr);
 };
 
 abstract protected boolean 	compareInteger(int l,int r);
 abstract protected boolean 	compareReal(float l,float r);
 abstract protected boolean 	compareString(String l,String r);
};

