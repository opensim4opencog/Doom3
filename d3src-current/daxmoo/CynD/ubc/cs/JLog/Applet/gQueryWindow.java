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
//	gQueryWindow
//##################################################################################

package ubc.cs.JLog.Applet;

import java.lang.*;
import java.util.*;
import java.awt.*;
import java.awt.event.*;
import ubc.cs.JLog.Foundation.*;

public class gQueryWindow extends gWindowBase
{
 gQueryWindow(gJLogApplicationBase b,String default_source_text,boolean visible)
 {
  super(b);
 
  setLayout(new GridLayout());
  add(new gQueryPanel(parent.getPrologServices(),default_source_text,false));
  pack();

  setSize(600, 400);
  setLocation(20,40);

  setTitle("JLog - Query");
  setVisible(visible);
 };

 public boolean 		useMenuItem(MenuItem mi)
 {
  if (mi.getActionCommand() == parent.MENU_CLOSE)
  {
   mi.addActionListener(new ActionListener() 
        {
         public void actionPerformed (ActionEvent e) 
         {
          close();
         }
        }
       );  
   return true;
  }
  return false;
 };
};
