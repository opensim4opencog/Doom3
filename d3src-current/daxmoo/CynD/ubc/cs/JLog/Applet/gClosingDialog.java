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
//	gClosingDialog
//##################################################################################

package ubc.cs.JLog.Applet;

import java.lang.*;
import java.util.*;
import java.awt.*;
import java.awt.event.*;
import java.applet.Applet;

public class gClosingDialog extends Dialog
{
 public static final int 	CANCEL = 1;
 public static final int 	SAVE = 2;
 public static final int 	SAVEAS = 3;
 public static final int 	DONTSAVE = 4;
 
 protected int 			choice;
 
 public gClosingDialog(gWindowBase w)
 {
  super(w,"Save Changes?",true);
  
  choice = CANCEL;
  
  setLayout(new BorderLayout());
  
  {Label 	info;
   
   info = new Label("Save changes before closing?",Label.LEFT);
   info.setFont(new Font("Dialog",Font.BOLD,12));

   add(info,BorderLayout.CENTER);
  }
   
  add(new Panel(),BorderLayout.WEST);
  add(new Panel(),BorderLayout.EAST);
  
  {Panel 	buttons = new Panel();
   Button 	b;

   buttons.setLayout(new GridLayout(1,0));
   
   b = new Button("Save");
   b.addActionListener(new ActionListener()
                {
                 public void 	actionPerformed(ActionEvent e)
                 {
                  choice = SAVE;
		  close();
                 }
                }
               ); 
   b.setBackground(Color.white);
   b.setForeground(Color.black);
   buttons.add(b);

   b = new Button("Cancel");
   b.addActionListener(new ActionListener()
                {
                 public void 	actionPerformed(ActionEvent e)
                 {
                  choice = CANCEL;
		  close();
                 }
                }
               ); 
   b.setBackground(Color.white);
   b.setForeground(Color.black);
   buttons.add(b);
   
   buttons.add(new Panel());
   
   b = new Button("Don't Save");
   b.addActionListener(new ActionListener()
                {
                 public void 	actionPerformed(ActionEvent e)
                 {
                  choice = DONTSAVE;
		  close();
                 }
                }
               ); 
   b.setBackground(Color.white);
   b.setForeground(Color.black);            
   buttons.add(b);
   
   {Panel 	button_area = new Panel();
   
    button_area.setLayout(new BorderLayout());
    button_area.add(buttons,BorderLayout.CENTER);
    button_area.add(new Panel(),BorderLayout.WEST);
    button_area.add(new Panel(),BorderLayout.EAST);
    
    add(button_area,BorderLayout.SOUTH);
   }
  }
  
  pack();

  addWindowListener(new WindowAdapter() 
                {
		 public void windowClosing(WindowEvent evt) 
                 {
                  choice = CANCEL;
		  close();
		 }
		}
               );

  setSize(400, 100);
  {Point	ploc = w.getLocation();
  
   setLocation(ploc.x+16,ploc.y+32);
  }  
  setVisible(true);
 };
 
 public int 		getChoiceValue()
 {
  return choice;
 };
 
 protected void 	close()
 {
  dispose();
 };
};
