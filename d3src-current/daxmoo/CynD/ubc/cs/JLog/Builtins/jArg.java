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
//	Arg
//#########################################################################
 
package ubc.cs.JLog.Builtins;

import java.lang.*;
import java.util.*;
import ubc.cs.JLog.Terms.*;
import ubc.cs.JLog.Foundation.*;
import ubc.cs.JLog.Terms.Goals.*;

public class jArg extends jTrinaryBuiltinPredicate
{
 public jArg(jTerm n,jTerm t,jTerm a)
 {
  super(n,t,a,TYPE_BUILTINPREDICATE);
 };
  
 public String 		getName()
 {
  return "arg";
 };

 public boolean 	prove(jTrinaryBuiltinPredicateGoal ag)
 {jTerm 	t1,t2,t3;
  
  t1 = ag.term1.getTerm();
  t2 = ag.term2.getTerm();
  t3 = ag.term3.getTerm();
  
  if (t1 instanceof jInteger && t2 instanceof iPredicate)
  {jCompoundTerm 	ct = ((iPredicate) t2).getArguments();
   int  			arity = ct.size();
   int 				arg = ((jInteger) t1).getIntegerValue() - 1;
   
   if (arg < arity && arg >= 0)
   {
    return t3.unify(ct.elementAt(arg),ag.unified);
   }
   else
    throw new InvalidArgArgumentException(); 
  }
  else
   throw new InvalidArgArgumentException();
 };

 protected jTrinaryBuiltinPredicate 		duplicate(jTerm t1,jTerm t2,jTerm t3)
 {
  return new jArg(t1,t2,t3); 
 };
};

