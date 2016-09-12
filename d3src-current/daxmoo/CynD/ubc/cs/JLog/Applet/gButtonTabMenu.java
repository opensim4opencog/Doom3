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
//##################################################################################
//	gButtonTabMenu
//##################################################################################

package ubc.cs.JLog.Applet;

import java.lang.*;
import java.util.*;
import java.awt.*;

/**
* A collection of <code>gButtonTab</code>s. Used to synchronized the appearance
* of related buttons, so that only one remains active at any time.
*
* @author       Glendon Holst
* @version      %I%, %G%
*/
public class gButtonTabMenu extends Panel
{
 public final static int 		HORIZONTAL = 0;
 public final static int 		VERTICAL = 1;

 protected gButtonTab 			activetab = null;
 
 public 	gButtonTabMenu()
 {
  this(HORIZONTAL);
 };
 
 public 	gButtonTabMenu(int direction)
 {
  this(direction,new Font("Dialog",Font.BOLD,12));
 };

 public 	gButtonTabMenu(int direction,Font f)
 {
  setLayout(new GridLayout((direction == VERTICAL ? 0 : 1),(direction == VERTICAL ? 1 : 0)));
  setFont(f);
  setBackground(Color.lightGray);
  setForeground(Color.black);
 };

 public void 	setActiveTab(gButtonTab a)
 {
  if (activetab == a)
   return;
   
  if (activetab != null)
   activetab.setState(false);
   
  activetab = a;
  
  if (activetab != null)
   activetab.setState(true);
 };
};

