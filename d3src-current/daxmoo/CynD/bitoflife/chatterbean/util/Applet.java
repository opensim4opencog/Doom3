/*
Copyleft (C) 2005 Hélio Perroni Filho
xperroni@yahoo.com
ICQ: 2490863

This file is part of ChatterBean.

ChatterBean is free software; you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation; either version 2 of the License, or (at your option) any later version.

ChatterBean is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for more details.

You should have received a copy of the GNU General Public License along with ChatterBean (look at the Documents/ directory); if not, either write to the Free Software Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307 USA, or visit (http://www.gnu.org/licenses/gpl.txt).
*/

/*
<object classid="clsid:CAFEEFAC-0015-0000-0002-ABCDEFFEDCBA"
        codebase="http://java.sun.com/update/1.5.0/jinstall-1_5_0_02-windows-i586.cab#Version=5,0,20,9"
        width="350" height="200">
  <param name="code" value="bitoflife.chatterbean.util.Applet">
  <param name="type" value="application/x-java-applet;jpi-version=1.5.0_02">
  <param name="scriptable" value="false">
  <comment>
    <embed type="application/x-java-applet;jpi-version=1.5.0_02"
           code="bitoflife.chatterbean.util.Applet"
           width="350" height="200"
           scriptable="false"
           pluginspage="http://java.sun.com/products/plugin/index.html#download">
      <noembed></noembed>
    </embed>
  </comment>
</object>
*/

package bitoflife.chatterbean.util;

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;

import bitoflife.chatterbean.ChatterBean;

public class Applet extends JApplet
{
  /*
  Attributes
  */

  private ChatterBean bot;
  
  private final JTextField input  = new JTextField(30);
  private final JTextArea  output = new JTextArea(10, 30); 
  
  /*
  Methods
  */
  
  public void init()
  {
    ActionListener listener = new ActionListener()
    {
      public void actionPerformed(ActionEvent e)
      {
        String request = input.getText();
        String response = bot.respond(request);

        input.setText("");
        output.append("> " + request + "\n");
        output.append(response + "\n");
      }
    };
    
    input.addActionListener(listener);
    
    output.setEditable(false);
    output.setLineWrap(true);
    output.setWrapStyleWord(true);
    Container cp = getContentPane();
    cp.setLayout(new FlowLayout());
    cp.add(new JScrollPane(output));
    cp.add(input);
  }
  
  public static void main(String[] args)
  {
    Applet applet = new Applet();
    applet.bot = new ChatterBean(args[0]);
    
    JFrame frame = new JFrame("ChatterBean GUI Window");
    frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    frame.getContentPane().add(applet);
    frame.setSize(350, 210);
    applet.init();
    applet.start();
    frame.setVisible(true);
  }
}
