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
//	PrologServiceText
//#########################################################################

package ubc.cs.JLog.Foundation;

import java.lang.*;
import java.util.*;

/**
* This is the interface for text.  It represents the minimal interface suitable
* for jPrologService* to interface with text sources and destinations.  The 
* interface also supports the user interface aspects of TextArea and TextField,
* which can be ignored by Strings.
*  
* @author       Glendon Holst
* @version      %I%, %G%
*/
public interface iPrologServiceText
{
 public String 			getText();
 public void 			setText(String t);
 public void 			append(String a);
 public void 			insert(String i,int p);
 public void 			remove(int s,int e);
 
 public void 			setCaretPosition(int i);
 public void 			select(int s,int e);
 public void 			selectAll();
 public void   			requestFocus(); 
};
