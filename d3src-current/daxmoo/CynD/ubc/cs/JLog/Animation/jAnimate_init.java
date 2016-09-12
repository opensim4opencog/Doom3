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
//	Animate Init
//#########################################################################
 
package ubc.cs.JLog.Animation;

import java.lang.*;
import java.util.*;
import java.awt.*;
import ubc.cs.JLog.Terms.*;

public class jAnimate_init extends jAnimate
{
 public jAnimate_init(jTerm t)
 {
  super(t);
 };
  
 public String 		getName()
 {
  return "animate<init>";
 };
 
 public int 		getNumberArguments()
 {
  return 1;
 };
 
 protected jUnaryBuiltinPredicate 		duplicate(jTerm r)
 {
  return new jAnimate_init(r); 
 };
 
 protected void 	action(aAnimationEnvironment ae,jTerm[] terms)
 {float 	mag = aAttributeTranslation.convertToFloat(terms[0],true);
 
  ae.init(); 
  ae.setMagnification(mag);
 };
};
