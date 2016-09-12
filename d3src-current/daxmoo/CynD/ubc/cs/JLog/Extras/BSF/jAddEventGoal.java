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
//	jAddEventGoal
//#########################################################################
 
package ubc.cs.JLog.Extras.BSF;

import org.apache.bsf.util.EngineUtils;

import ubc.cs.JLog.Builtins.*;
import ubc.cs.JLog.Builtins.Goals.*;
import ubc.cs.JLog.Foundation.*;
import ubc.cs.JLog.Terms.*;

/**
 * This file is part of the JLog BSF library.
 *
 * @author       Ulf Dittmer (Copyright 2005)
 */

public class jAddEventGoal extends jGoal {

	protected static final boolean DEBUG = false;

	protected jTerm bean, action, script;
	protected jUnifiedVector unified;
	protected JLogBSFEngine engine;

	public jAddEventGoal (jTerm t1, jTerm t2, jTerm t3, JLogBSFEngine engineParam) {
		super();
		bean = t1;
		action = t2;
		script = t3;
		engine = engineParam;
		unified = new jUnifiedVector();
	}
 
	public boolean prove (iGoalStack goals, iGoalStack proved) {
		if (! (bean instanceof jVariable) || ! ((jVariable) bean).isBound())
			throw new IllegalArgumentException("Parameter 'Bean' is not a variable");
		String actionStr = action.toString();
		if (actionStr == null)
			throw new RuntimeException("Variable 'Action' is unbound");
		String scriptStr = engine.extractValue(script).toString();
		if (scriptStr == null)
			throw new RuntimeException("Variable 'Script' is unbound");

		if (DEBUG) System.out.println("bsf_addevent/3::"+actionStr+"::"+scriptStr);

		boolean success = false;

		try {
			// do something here
			jTerm beanTerm = ((jVariable) bean).getTerm();
			Object obj = null;
			if (beanTerm instanceof jObject)
				obj = ((jObject) beanTerm).getObjectReference();
			else
				obj = beanTerm;
			EngineUtils.addEventListener(obj,
						actionStr, null, engine, engine.getManager(),
						"<event-script>", 0, 0, scriptStr);
			success = true;
		} catch (Exception ex) {
			throw new RuntimeException(ex);
		}

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

	public String getName() { return "bsf_addevent"; }

	public int getArity() { return 3; }

	public String toString() {
		StringBuffer sb = new StringBuffer(100);

		sb.append(getName()).append("/").append(getArity()).append(" goal: ");

		sb.append(getName()).append("(").append(action).append(",").append(script).append(")");

		return sb.toString();
	}
}
