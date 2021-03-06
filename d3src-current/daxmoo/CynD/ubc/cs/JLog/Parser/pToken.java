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
//	pToken
//#########################################################################

package ubc.cs.JLog.Parser;

import java.util.*;
import java.lang.*;

/**
* Abstract base class representing tokens in the parse stream. Each token
* contains information about its location in the parse stream. 
*  
* @author       Glendon Holst
* @version      %I%, %G%
*/
abstract class pToken
{
 protected String 		token;

 // these are of starting positions of the token.  
 // use the token string to determine ending positions.
 protected int 			position = 0;
 protected int 			lineno = 0;
 protected int 			charpos = 0;

 public pToken(String s)
 {
  token = s;
 };
 
 public pToken(String s,int pos,int line,int cpos)
 {
  token = s;
  this.position = pos;
  this.lineno = line;
  this.charpos = cpos;
 };
 
 public String 		getToken()
 {
  return token;
 };

 public int 		getPosition()
 {
  return position;
 };

 public int 		getLine()
 {
  return lineno;
 };

 public int 		getCharPos()
 {
  return charpos;
 };

 public String 		toString()
 {
  return getClass().toString() + ": " + token;
 };
};

