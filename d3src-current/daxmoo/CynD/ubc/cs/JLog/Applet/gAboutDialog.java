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
//	gAboutDialog
//##################################################################################

package ubc.cs.JLog.Applet;

import java.lang.*;
import java.util.*;
import java.awt.*;
import java.awt.event.*;
import java.applet.Applet;

public class gAboutDialog extends Dialog
{
 public gAboutDialog(gJLogApplicationBase b,boolean visible)
 {
  super(b,"About JLog",true);
  
  setLayout(new GridLayout());
  
  {TextArea		about_text;
 
   about_text = new TextArea(b.getRequiredCreditInfo(),-1,-1,TextArea.SCROLLBARS_VERTICAL_ONLY);
   about_text.setFont(new Font("SansSerif",Font.BOLD,12));
   about_text.setEditable(false);

   add(about_text);
  }
  
  pack();

  addWindowListener(new WindowAdapter() 
                {
		 public void windowClosing(WindowEvent evt) 
                 {
		  close();
		 }
		}
               );

  setSize(650, 350);
  setLocation(50,100);

  setVisible(visible);
 };
 
 protected void 	close()
 {
  dispose();
 };
};
