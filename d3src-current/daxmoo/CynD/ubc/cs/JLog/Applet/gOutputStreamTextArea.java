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
//	OutputStreamTextArea
//#########################################################################

package ubc.cs.JLog.Applet;

import java.lang.*;
import java.util.*;
import java.io.*;
import ubc.cs.JLog.Foundation.iPrologServiceText;
import ubc.cs.JLog.Foundation.jPrologServiceBroadcaster;
import ubc.cs.JLog.Foundation.jPrologServiceListener;
import ubc.cs.JLog.Foundation.jPrologServiceEvent;
import java.awt.TextArea;

/**
* This class represents an OuputStream which directs to a TextArea.
*  
* @author       Glendon Holst
* @version      %I%, %G%
*/
public class gOutputStreamTextArea extends OutputStream
{
 protected TextArea						text;
 protected jPrologServiceBroadcaster 	addedText = null;

 public gOutputStreamTextArea(TextArea t)
 {
  text = t;
 }

 public void	close() throws IOException
 {
  text = null;
  super.close();
 };

 public void	flush() throws IOException
 {
  super.flush();
 };
 
 public void	write(int b) throws IOException
 {
  write(new byte[]{(byte) b});  
 };

 public void	write(byte[] b) throws IOException
 {String	s = new String(b);
 
  if (text != null)
  {
   text.append(s);
   if (addedText != null)
    addedText.broadcastEvent(new jPrologServiceEvent());
  }
  else
   throw new IOException();
 };

 public void	write(byte[] b,int off,int len) throws IOException
 {String	s = new String(b,off,len);
 
  if (text != null)
  {
   text.append(s);
   if (addedText != null)
    addedText.broadcastEvent(new jPrologServiceEvent());
  }
  else
   throw new IOException();
 };

 public synchronized void 		addTextAddedListener(jPrologServiceListener l)
 {
  if (addedText == null)
   addedText = new jPrologServiceBroadcaster();

  addedText.addListener(l);
 };
 
 public synchronized void 		removeTextAddedListener(jPrologServiceListener l)
 {
  addedText.removeListener(l);
 };
};
