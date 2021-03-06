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
//	jPrologServiceBroadcaster
//#########################################################################

package ubc.cs.JLog.Foundation;

import java.lang.*;
import java.util.*;

/**
* The boadcasting component of the Observer pattern. Interested 
* <code>jPrologServiceListener</code> instances register, and are
* notified of any broadcast events.
*
* @author       Glendon Holst
* @version      %I%, %G%
*/
public class jPrologServiceBroadcaster
{
 protected Vector 		listeners;
 protected Vector 		cached_listeners = null;
 
 public 	jPrologServiceBroadcaster()
 {
  listeners = new Vector();
 };

 /**
  * Register provided listener as an interested observer to receive broadcast events.
  * Events are ordered by their priority.
  *
  * @param l 		The listener to register.
  */
 public synchronized void 		addListener(jPrologServiceListener l)
 {
  if (!listeners.contains(l))
  {int 	i, max = listeners.size();
   
   for (i = 0; i < max; i++)
   {jPrologServiceListener 	e = (jPrologServiceListener) listeners.elementAt(i);

    if (l.getPriority() <= e.getPriority())
    {
     listeners.insertElementAt(l,i);
     cached_listeners = null;
     return;
    } 
   }
   
   listeners.insertElementAt(l,max);
   cached_listeners = null;
  }
 };
 
 /**
  * Remove specified listener from broadcast receipients (listener is no longer 
  * interested).
  *
  * @param l 		The listener to remove.
  */
 public synchronized void 		removeListener(jPrologServiceListener l)
 {
  listeners.removeElement(l);
  cached_listeners = null;
 };
 
 /**
  * Sends provided event to all registered <code>jPrologServiceListener</code>s.
  *
  * @param event 	The event to send.
  */
 public synchronized void 		broadcastEvent(jPrologServiceEvent event)
 {Enumeration 	e = getCachedListeners().elements();
  
  while (e.hasMoreElements())
   ((jPrologServiceListener) e.nextElement()).handleEvent(event);
 };
 
 public synchronized Vector 		getCachedListeners()
 {
  if (cached_listeners != null)
   return cached_listeners;
  else
  {Enumeration 	e;
 
   cached_listeners = new Vector(listeners.size());

   e = listeners.elements();
  
   while (e.hasMoreElements())
    cached_listeners.addElement(e.nextElement());
    
   return cached_listeners; 
  }
 };
};
