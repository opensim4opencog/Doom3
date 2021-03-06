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
//	aAnimationShape Image
//##################################################################################

package ubc.cs.JLog.Animation;

import java.lang.*;
import java.util.*;
import java.awt.*;

import ubc.cs.JLog.Terms.*;

public class aAnimationShape_image extends aAnimationShape
{
 public static final String			WIDTH_ATTR_KEY = "width";
 public static final String			HEIGHT_ATTR_KEY = "height";

 private static jTermTranslation		translations;
 
 static {
  translations = new jTermTranslation();
  setDefaultsForTranslation(translations);
 }

 protected Image 					image;
 protected float					width,height;

 public 	aAnimationShape_image(aAnimationObject ao,String n,String iname,
									float x,float y,float w,float h)
 {
  super(ao,n);
  image = object.getEnvironment().getImage(iname);
  x_offset = x;
  y_offset = y;
  width = w;
  height = h;
 };

 public void 		draw(Graphics g)
 {Point		p = object.getDisplayPointFromLocalPosition(x_offset,y_offset,rotation_offset);
  float		m = object.getEnvironment().getMagnification();
 
  g.drawImage(image,p.x,p.y,Math.round(width*m),Math.round(height*m),object.getEnvironment());
 };
 
 public Rectangle 		getBounds()
 {Point		p = object.getDisplayPointFromLocalPosition(x_offset,y_offset,rotation_offset);
  float		m = object.getEnvironment().getMagnification();
 
  return new Rectangle(p.x,p.y,Math.round(width*m),Math.round(height*m));
 };

 public String 		getType()
 {
  return "image";
 };

 public static void		setDefaultsForTranslation(jTermTranslation t)
 {
  aAnimationShape.setDefaultsForTranslation(t);
  
  setStringKeysForTranslation(t,WIDTH_ATTR_KEY,jReal.class,Float.class);
  setStringKeysForTranslation(t,HEIGHT_ATTR_KEY,jReal.class,Float.class);
 };

 public jTermTranslation	getTermTranslation()
 {
  return translations;
 };

 public Hashtable   getAttributes()
 {Hashtable		ht;
 
  ht = super.getAttributes();
 
  ht.put(WIDTH_ATTR_KEY,new Float(width));
  ht.put(HEIGHT_ATTR_KEY,new Float(height));

  return ht;  
 };

 public void		setAttributes(Hashtable attributes)
 {
  super.setAttributes(attributes);

  {Float		fw = null, fh = null;
  
   if ((fw = (Float) attributes.get(WIDTH_ATTR_KEY)) != null)
    width = fw.floatValue();
   if ((fh = (Float) attributes.get(HEIGHT_ATTR_KEY)) != null)
    height = fh.floatValue();
  }
 };
 
};