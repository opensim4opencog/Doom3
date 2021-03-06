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
//	TermToObject
//#########################################################################

package ubc.cs.JLog.Terms;

import java.lang.*;
import java.util.*;

/**
* This is the interface for objects which take a <code>jTerm</code>
* and return a corresponding Java object.
* 
* @author       Glendon Holst
* @version      %I%, %G%
*/
public interface iTermToObject
{
 
/**
  * Create a corresponding Java object representation from the given Prolog term.
  *
  * @param term			The <code>jTerm</code> to construct the representation from.
  *
  * @return 			the <code>Object</code> sub-class represting term.
  */
 public Object 		createObjectFromTerm(jTerm term);
};
