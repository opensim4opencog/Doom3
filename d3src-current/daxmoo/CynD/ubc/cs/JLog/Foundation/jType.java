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
//	Type
//#########################################################################

package ubc.cs.JLog.Foundation;

import java.lang.*;
import java.util.*;

/**
* This abstract class defines all the prolog types.   
* Use <code>jType.getType()</code> in place of instanceof for speed, and when only the 
* actual instance type matters, and not any super classes. 
* For sub classes, the type should be set in constructor only, and read-only from then on.
* <P>
* Any new types added to the system must be registered in this class.  It is unlikely
* that this would be needed, since they are quite general.
* 
* @author       Glendon Holst
* @version      %I%, %G%
*/
abstract public class jType implements iType
{
 // type should be set in constructor only, and read-only from then on. 
 public int 	type = TYPE_UNDEFINED;
 
 public final int 	getType() {return type;};
 
/**
  * Invokes the <code>toString</code> provided by the <code>Object</code> class.  
  * This is intended for classes whose superclasses have overridden toString, but 
  * need access to the default <code>toString</code> that the <code>Object</code> 
  * class returns.
  *
  * @return 			the <code>String</code> from <code>Object.toString</code>.
  */
 protected String 		objectToString()
 {
  return super.toString();
 };
};
