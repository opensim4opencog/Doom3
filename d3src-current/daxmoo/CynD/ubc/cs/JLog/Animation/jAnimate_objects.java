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
//	Animate Objects
//#########################################################################
 
package ubc.cs.JLog.Animation;

import java.lang.*;
import java.util.*;
import java.awt.*;
import ubc.cs.JLog.Foundation.*;
import ubc.cs.JLog.Terms.*;

public class jAnimate_objects extends jAnimate
{
 public jAnimate_objects(jTerm t)
 {
  super(t);
 };
  
 public String 		getName()
 {
  return "animate<objects>";
 };
 
 public int 		getNumberArguments()
 {
  return 1;
 };
 
 protected jUnaryBuiltinPredicate 		duplicate(jTerm r)
 {
  return new jAnimate_objects(r); 
 };
 
 public boolean 	prove(jAnimateGoal ag,aAnimationEnvironment ae)
 {
  return action(ae,aAttributeTranslation.convertToTerms(ag.term,getNumberArguments()),
					ag.unified);
 };

 protected boolean 	action(aAnimationEnvironment ae,jTerm[] terms,jUnifiedVector uv)
 {jList				list = jListPair.createListFromEnumeration(ae.enumObjects(),
					   new iObjectToTerm()
                       {
                        public jTerm 	createTermFromObject(Object obj)
                        {aAnimationObject ao = (aAnimationObject) obj;
						
						 return new jCons(new jAtom(ao.getName()),new jObject(obj)); 
                        }
                       });
  
  return terms[0].unify(list,uv); 
 };

 protected void 	action(aAnimationEnvironment ae,jTerm[] terms)
 {
  // do nothing
 };
};

