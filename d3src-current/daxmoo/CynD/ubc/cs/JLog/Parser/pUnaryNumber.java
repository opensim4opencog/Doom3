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
//	pUnaryNumber
//#########################################################################

package ubc.cs.JLog.Parser;

import java.util.*;
import java.lang.*;

/**
* Token representing numbers immediately preceeded by a sign (either '-' or '+').
* These may be treated either as a single number, or as a unary arithmetic operator
* and a number.
*  
* @author       Glendon Holst
* @version      %I%, %G%
*/
class pUnaryNumber extends pToken
{
 protected pToken 	sign;
 protected pToken 	value;
 
 public 	pUnaryNumber(pToken s,pToken v)
 {
  super(s.token+v.token,s.position,s.lineno,s.charpos);
  sign = s;
  value = v;
 };

 public pToken 		getSign()
 {
  return sign;
 };

 public pToken 		getValue()
 {
  return value;
 };
};


