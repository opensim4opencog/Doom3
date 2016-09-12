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
//	AnimatePredicateEntry
//#########################################################################

package ubc.cs.JLog.Animation;

import java.lang.*;
import java.util.*;
import ubc.cs.JLog.Terms.*;
import ubc.cs.JLog.Foundation.*;
import ubc.cs.JLog.Builtins.*;
import ubc.cs.JLog.Parser.*;

public class pAnimatePredicateEntry extends pPredicateEntry
{
 public 	pAnimatePredicateEntry()
 {
  super("animate",2);
 };
 
 public iPredicate 		createPredicate(jCompoundTerm cterm)
 {String 	command = cterm.elementAt(0).getName();
  
  if (command.equals("init"))
   return new jAnimate_init(cterm.elementAt(1));
  if (command.equals("update"))
   return new jAnimate_update(cterm.elementAt(1));
  if (command.equals("create"))
   return new jAnimate_create(cterm.elementAt(1));
  if (command.equals("delete"))
   return new jAnimate_delete(cterm.elementAt(1));
  if (command.equals("objects"))
   return new jAnimate_objects(cterm.elementAt(1));
  if (command.equals("getobject"))
   return new jAnimate_getobject(cterm.elementAt(1));
  if (command.equals("getattr"))
   return new jAnimate_getattr(cterm.elementAt(1));
  if (command.equals("setattr"))
   return new jAnimate_setattr(cterm.elementAt(1));
  if (command.equals("move"))
   return new jAnimate_move(cterm.elementAt(1));
  if (command.equals("rotate"))
   return new jAnimate_rotate(cterm.elementAt(1));
  if (command.equals("setlevel"))
   return new jAnimate_setlevel(cterm.elementAt(1));
  if (command.equals("setmagnify"))
   return new jAnimate_setmagnify(cterm.elementAt(1));
  if (command.equals("setview"))
   return new jAnimate_setview(cterm.elementAt(1));
  if (command.equals("path"))
   return new jAnimate_path(cterm.elementAt(1));
  if (command.equals("addshape"))
   return new jAnimate_addshape(cterm.elementAt(1));
  if (command.equals("getshape"))
   return new jAnimate_getshape(cterm.elementAt(1));
  if (command.equals("shapes"))
   return new jAnimate_shapes(cterm.elementAt(1));
  if (command.equals("removeshape"))
   return new jAnimate_removeshape(cterm.elementAt(1));
   
  throw new InvalidAnimationAPIException("Unknown command.");
 };
};


