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
//	CreateArray
//#########################################################################
 
package ubc.cs.JLog.Builtins;

import java.lang.*;
import java.util.*;
import ubc.cs.JLog.Terms.*;
import ubc.cs.JLog.Foundation.*;
import ubc.cs.JLog.Terms.Goals.*;

public class jCreateArray extends jBinaryBuiltinPredicate
{
 /**
  * Constructor for <code>jCreateArray</code>.
  *
  * @param l 	The term (should be bound to a jInteger) specifiying the array size.
  * @param r 	The array term (will be bound to CompoundTerm).
  * 			
  */
 public jCreateArray(jTerm l,jTerm r)
 {
  super(l,r,TYPE_BUILTINPREDICATE);
 };
  
 public String 		getName()
 {
  return "CREATEARRAY";
 };
 
 public final boolean 	prove(jBinaryBuiltinPredicateGoal bg)
 {jTerm 	l = bg.term1.getTerm();
  jTerm		r = bg.term2.getTerm();

  if (l.type == TYPE_INTEGER)
  {int				size = ((jInteger) l).getIntegerValue();
   int				cnt;
   jCompoundTerm 	ct = new jCompoundTerm(size);
  
   for (cnt = 0; cnt < size; cnt++)
    ct.addTerm(new jVariable());
  
   return r.unify(ct,bg.unified); 
  }
  else
   throw new InvalidArgArgumentException();
 };

 public jBinaryBuiltinPredicate 		duplicate(jTerm l,jTerm r)
 {
  return new jCreateArray(l,r); 
 };
};

