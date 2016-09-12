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
//	FloatPolygon
//##################################################################################

package ubc.cs.JLog.Animation;

import java.lang.*;
import java.util.*;
import java.awt.*;

public class FloatPolygon
{
 public int			npoints;
 public float[]		xpoints, ypoints;
  
 public FloatPolygon()
 {
  npoints = 0;
  xpoints = new float[1];
  ypoints = new float[1];
 };

 public FloatPolygon(int capacity)
 {
  npoints = 0;
  if (capacity < 1)
   capacity = 1;
	
  xpoints = new float[capacity];
  ypoints = new float[capacity];
 };

 public FloatPolygon(float[] xpts,float[] ypts,int npts)
 {
  npoints = npts;
  xpoints = new float[npoints];
  ypoints = new float[npoints];
  System.arraycopy(xpts,0,xpoints,0,npts);
  System.arraycopy(ypts,0,ypoints,0,npts);
 };

 public void		addPoint(float x,float y)
 {
  if (xpoints.length > npoints+1 && ypoints.length > npoints+1)
  {
   xpoints[npoints]=x;
   ypoints[npoints]=y;
   npoints++;
  }
  else // resize arrays
  {float[]		xp = new float[npoints+1];
   float[]		yp = new float[npoints+1];
	
   System.arraycopy(xpoints,0,xp,0,npoints);
   System.arraycopy(ypoints,0,yp,0,npoints);
   
   xpoints = xp;
   ypoints = yp;
	
   xpoints[npoints]=x;
   ypoints[npoints]=y;
   npoints++;
  }
 };
  
 public void		translate(float deltaX,float deltaY)
 {int 			i;
  
  for (i = 0; i < npoints; i++)
  {
   xpoints[i] += deltaX;
   ypoints[i] += deltaY;
  }
 };
};
