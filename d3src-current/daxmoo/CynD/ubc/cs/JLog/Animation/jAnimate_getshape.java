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
//	Animate GetShape
//#########################################################################
 
package ubc.cs.JLog.Animation;

import java.lang.*;
import java.util.*;
import java.awt.*;
import ubc.cs.JLog.Foundation.*;
import ubc.cs.JLog.Terms.*;

public class jAnimate_getshape extends jAnimate
{
 public jAnimate_getshape(jTerm t)
 {
  super(t);
 };
  
 public String 		getName()
 {
  return "animate<getshape>";
 };
 
 public int 		getNumberArguments()
 {
  return 2;
 };
 
 protected jUnaryBuiltinPredicate 		duplicate(jTerm r)
 {
  return new jAnimate_getshape(r); 
 };
 
 public boolean 	prove(jAnimateGoal ag,aAnimationEnvironment ae)
 {
  return action(ae,aAttributeTranslation.convertToTerms(ag.term,getNumberArguments()),
				ag.unified);
 };

 protected boolean 	action(aAnimationEnvironment ae,jTerm[] terms,jUnifiedVector uv)
 {aAnimationObject 	obj = aAttributeTranslation.convertToAnimationObject(terms[0],ae);
  jTerm[] 			shapeinfo = aAttributeTranslation.convertToTerms(terms[1],2);
  String 			shpname = aAttributeTranslation.convertToString(shapeinfo[0]);
  jTerm[]			shaperef = aAttributeTranslation.convertToTerms(shapeinfo[1],1);
  aAnimationShape 	shape;
  
  if ((shape = obj.getShape(shpname)) != null)
   return shaperef[0].unify(new jObject(shape),uv); 
  else
   return false;
 };

 protected void 	action(aAnimationEnvironment ae,jTerm[] terms)
 {
  // do nothing
 };
};

