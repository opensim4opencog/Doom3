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
//	gCardPanel
//##################################################################################

package ubc.cs.JLog.Applet;

import java.lang.*;
import java.util.*;
import java.awt.*;

/**
* This is a panel that represents the working area for a tabbed interface.
*
* @author       Glendon Holst
* @version      %I%, %G%
*/
public class gCardPanel extends Panel implements iCardPanel
{
 protected String 	defaultcard = null;

 public 	gCardPanel()
 {
  setLayout(new CardLayout());
  setFont(new Font("SansSerif",Font.PLAIN,12));
  setBackground(Color.lightGray);
  setForeground(Color.black);
 };
 
 public void 		setDefaultCard(String s)
 {
  defaultcard = s;
 };

 public void 		setCard(String s)
 {
  ((CardLayout) getLayout()).show(this,s);
 };
 
 public void 		resetCard()
 {
  ((CardLayout) getLayout()).show(this,defaultcard);
 };
};