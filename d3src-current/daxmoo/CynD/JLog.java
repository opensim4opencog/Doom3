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
//	JLog Applet
//#########################################################################

import java.awt.*;
import java.awt.event.*;
import java.applet.Applet;
import ubc.cs.JLog.Applet.gJLogApplet;

public class JLog extends gJLogApplet
{

 public class WarningDialog extends Dialog
 {
  public WarningDialog(Frame w,String info_str)
 {
  super(w,"Warning!",true);
  
  setLayout(new BorderLayout());
  
  {TextArea 	info;
   String		infos = info_str + 
				"\n\nFuture versions of JLog may require Java VM versions 1.2.x and higher " +
					"(version 1.2 is needed to load libraries properly).\n\n"+
					"To upgrade, visit the Download section of:\n\n" +
					"http://www.java.com/ \n\n" +
					"Upgrading will also get rid of this nag dialog.";
					
   info = new TextArea(infos,0,30,TextArea.SCROLLBARS_VERTICAL_ONLY);
   info.setFont(new Font("Dialog",Font.BOLD,12));
   info.setEditable(false);
   
   add(info,BorderLayout.CENTER);
  }
   
  add(new Panel(),BorderLayout.WEST);
  add(new Panel(),BorderLayout.EAST);
  
  {Panel 	buttons = new Panel();
   Button 	b;

   buttons.setLayout(new GridLayout(1,0));
   
   b = new Button("Ok");
   b.addActionListener(new ActionListener()
                {
                 public void 	actionPerformed(ActionEvent e)
                 {
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
				  close();
				 }
				}
               );

   setSize(400, 300);
   setVisible(true);
  };
 
  protected void 	close()
  {
   dispose();
  };
 };

 public JLog()
 {
  super();
  
  // determine Java VM version and give warning for old versions
  try
  {String   ver = System.getProperty("java.version");
   
   if (ver.startsWith("0") || ver.startsWith("1.0") || ver.startsWith("1.1")) 
   {String   infos = "\nWarning! Your Java VM (Virtual Machine) is very old.";

    new WarningDialog(new Frame("Old Java VM Warning"),infos);
   }
  }
  catch (Exception e)
  {String   infos = "\nWarning! The version of your Java VM (Virtual Machine) is unknown.";

   new WarningDialog(new Frame("Unknown Java VM Version Warning"),infos);
  }
 };
};
