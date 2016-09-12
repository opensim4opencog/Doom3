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
//	Animate GetAttributes
//#########################################################################
 
package ubc.cs.JLog.Animation;

import java.lang.*;
import java.util.*;
import java.awt.*;
import ubc.cs.JLog.Foundation.*;
import ubc.cs.JLog.Terms.*;

public class jAnimate_getattr extends jAnimate
{
 public jAnimate_getattr(jTerm t)
 {
  super(t);
 };
  
 public String 		getName()
 {
  return "animate<getattr>";
 };
 
 public int 		getNumberArguments()
 {
  return 2;
 };
 
 protected jUnaryBuiltinPredicate 		duplicate(jTerm r)
 {
  return new jAnimate_getattr(r); 
 };
 
 public boolean 	prove(jAnimateGoal ag,aAnimationEnvironment ae)
 {
  return action(ae,aAttributeTranslation.convertToTerms(ag.term,getNumberArguments()),
					ag.unified);
 };

 protected boolean 	action(aAnimationEnvironment ae,jTerm[] terms,jUnifiedVector uv)
 {aAttributeTranslation		obj = aAttributeTranslation.convertToAttributesObject(terms[0]);
  final Hashtable			ht = obj.getAttributes();
  final jTermTranslation	ttrans = obj.getTermTranslation();
  jList				attr = jListPair.createListFromEnumeration(ht.keys(),
					   new iObjectToTerm()
                       {
                        public jTerm 	createTermFromObject(Object obj)
                        {String		key = (String) obj;
						 Object		val = ht.get(key);
						 jTerm		term = ttrans.createTermFromObject(val,key);
						 
						 return new jCons(new jAtom(key),term); 
                        }
                       });

  return terms[1].unify(attr,uv); 
 };

 protected void 	action(aAnimationEnvironment ae,jTerm[] terms)
 {
  // do nothing
 };
};

