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
//	jStaticGoal
//#########################################################################
 
package ubc.cs.JLog.Extras.BSF;

import ubc.cs.JLog.Builtins.*;
import ubc.cs.JLog.Builtins.Goals.*;
import ubc.cs.JLog.Foundation.*;
import ubc.cs.JLog.Terms.*;

/**
 * This file is part of the JLog BSF library.
 *
 * @author       Ulf Dittmer (Copyright 2005)
 */

public class jStaticGoal extends jGoal {

	protected static final boolean DEBUG = false;

	protected jTerm className, result;
	protected jUnifiedVector unified;
	protected JLogBSFEngine engine;

	public jStaticGoal (jTerm t1, jTerm t2, JLogBSFEngine engineParam) {
		super();
		className = t1;
		result = t2;
		engine = engineParam;
		unified = new jUnifiedVector();
	}
 
	public boolean prove (iGoalStack goals, iGoalStack proved) {
		String classNameStr = engine.extractValue(className).toString();
		if (classNameStr == null)
			throw new RuntimeException("Variable 'ClassName' is unbound");

		if (DEBUG) System.out.println("bsf_static/2::"+classNameStr);

		boolean success = false;

		if (! (result instanceof jVariable))
			throw new RuntimeException(result.toString() + " is not a variable");

		Object[] resultArr = new Object[2];
		if (! engine.getClass(classNameStr, resultArr))
			throw new IllegalArgumentException("class '"+classNameStr+"' not found");
		Class clazz = (Class) resultArr[0];
		Object field = resultArr[1];

		if (DEBUG) System.out.println(clazz.getName()+"::"+field);

		// if field is null, we assume that the class itself was meant,
		// and not that the class did not have the field requested
		success = result.unify(new jObject(field==null ? clazz : field), unified);

		if (success) {
			proved.push(this);
			return true;
		} else {
			// we need to initialize goal to potentially restart
			unified.restoreVariables();
			goals.push(this); // a retry that follows may need a node to remove or retry
			return false;
		}
	}

	public boolean retry (iGoalStack goals,iGoalStack proved) {
		unified.restoreVariables();

		goals.push(this); // a retry that follows may need a node to remove or retry
		return false;
	}

	public void internal_restore (iGoalStack goals) { unified.restoreVariables(); }

	public String getName() { return "bsf_static"; }

	public int getArity() { return 2; }

	public String toString() {
		StringBuffer sb = new StringBuffer(100);

		sb.append(getName()).append("/").append(getArity()).append(" goal: ");

		sb.append(getName()).append("(").append(className).append(")");

		return sb.toString();
	}
}
