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
//	CreateGoal
//
// bsf_create(ResultVar, Class, Parameters [, Types])
//
//#########################################################################

package ubc.cs.JLog.Extras.BSF;

import java.lang.reflect.Constructor;
import java.util.*;

import ubc.cs.JLog.Builtins.*;
import ubc.cs.JLog.Builtins.Goals.*;
import ubc.cs.JLog.Foundation.*;
import ubc.cs.JLog.Terms.*;

/**
 * This file is part of the JLog BSF library.
 *
 * @author       Ulf Dittmer (Copyright 2005)
 */

public class jCreateGoal extends jGoal {

	protected static final boolean DEBUG = false;

	protected jTerm bean, beanClass, types, parameters;
	protected jUnifiedVector unified;
	protected JLogBSFEngine engine;

	public jCreateGoal (jTerm t1, jTerm t2, jTerm t3, JLogBSFEngine engineParam) {
		this(t1, t2, t3, null, engineParam);
	}

	public jCreateGoal (jTerm t1, jTerm t2, jTerm t3, jTerm t4, JLogBSFEngine engineParam) {
		super();
		bean = t1;
		beanClass = t2;
		parameters = t3;
		types = t4;
		engine = engineParam;
		unified = new jUnifiedVector();
	}

	public boolean prove (iGoalStack goals, iGoalStack proved) {
		if (! (bean instanceof jVariable))
			throw new IllegalArgumentException("Parameter 'Bean' is not a variable");

		String beanClassStr = engine.extractValue(beanClass).toString();
		if (beanClassStr == null)
			throw new IllegalArgumentException("Variable 'BeanClass' is unbound");

		if (DEBUG) System.out.println("bsf_create::"+beanClassStr);

		Object[] result = new Object[2];
		if (! engine.getClass(beanClassStr, result))
			throw new IllegalArgumentException("class '"+beanClassStr+"' not found");
		Class clazz = (Class) result[0];

		boolean success = false;

		try {
			Constructor constructor;
			Object[] parameterValues;

			if (types == null) {
				// construct list of parameter values
				List paramList = new ArrayList();
				jTerm ptr = parameters;
				while (ptr != null) {
					if (ptr instanceof jListPair) {
						jTerm head = ((jListPair) ptr).getHead();
						ptr = ((jListPair) ptr).getTail();
						if (head instanceof jVariable) {
							if (((jVariable) head).isBound()) {
								jTerm value = ((jVariable) head).getTerm();
								if (DEBUG) System.out.println("variable: "+value.getClass());
								if (value instanceof jObject) {
									paramList.add(((jObject) value).getObjectReference());
								} else {
									paramList.add(value);
								}
							} else {
								throw new RuntimeException(getName()+": variable is unbound");
							}
						} else {
							paramList.add(head);
						}
					} else if (ptr instanceof jAtom) {
						paramList.add(ptr);
						ptr = null;
					} else if (ptr instanceof jVariable) {
						if (((jVariable) ptr).isBound()) {
							paramList.add(new jAtom(((jVariable) ptr).toString()));
						} else {
							throw new RuntimeException(getName()+": variable is unbound");
						}
						ptr = null;
					} else if (ptr instanceof jNullList) {
						ptr = null;
					}
				}
				if (DEBUG) System.out.println(paramList);
				parameterValues = paramList.toArray();

				// get the constructor that's appropriate for the given parameters,
				// and, finally, create the object and register it with the Engine
				constructor = getSuitableConstructor(clazz, parameterValues);
			} else {
				// construct list of parameter types
				List paramList = new ArrayList();
				jTerm ptr = types;
				String headStr = null;
				while (ptr != null) {
					if (ptr instanceof jListPair) {
						jTerm head = ((jListPair) ptr).getHead();
						ptr = ((jListPair) ptr).getTail();
						headStr = engine.extractValue(head).toString();
						if (! engine.getClass(headStr, result))
							throw new IllegalArgumentException("class '"+headStr+"' not found");
						paramList.add(result[0]);
					} else if (ptr instanceof jAtom) {
						headStr = engine.extractValue(ptr).toString();
						if (! engine.getClass(headStr, result))
							throw new IllegalArgumentException("class '"+headStr+"' not found");
						paramList.add(result[0]);
						ptr = null;
					} else if (ptr instanceof jNullList) {
						ptr = null;
					}
				}
				if (DEBUG) System.out.println(paramList);
				Class[] parameterTypes = new Class[paramList.size()];
				for (int i=0; i<paramList.size(); i++)
					parameterTypes[i] = (Class) paramList.get(i);
				// get the constructor that's appropriate for the given parameters
				constructor = clazz.getConstructor(parameterTypes);

				// construct list of parameter values
				paramList = new ArrayList();
				ptr = parameters;
				while (ptr != null) {
					if (ptr instanceof jListPair) {
						jTerm head = ((jListPair) ptr).getHead();
						ptr = ((jListPair) ptr).getTail();
						paramList.add(engine.extractValue(head).toString());
					} else if (ptr instanceof jAtom) {
						paramList.add(engine.extractValue(ptr).toString());
						ptr = null;
					} else if (ptr instanceof jNullList) {
						ptr = null;
					}
				}
				if (DEBUG) System.out.println(paramList);
				parameterValues = paramList.toArray();
			}

			// finally, create the object and register it with the Engine
			Object obj = constructor.newInstance(parameterValues);
			success = bean.unify(new jObject(obj), unified);
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

	// internally used method that retrieves a suitable constructor
	// based on the parameters and some very simple heuristics
	protected Constructor getSuitableConstructor (Class clazz, Object[] parameterValues) {
		int numParams = parameterValues.length;
		if (DEBUG) {
			for (int i=0; i<parameterValues.length; i++) {
				System.out.println(parameterValues[i]);
				System.out.println("jInteger="+(parameterValues[i] instanceof jInteger));
				System.out.println("jReal="+(parameterValues[i] instanceof jReal));
			}
		}

		List possibleConstructors = new ArrayList();
		Constructor[] constr = clazz.getConstructors();
		for (int i=0; i<constr.length; i++) {
			if (constr[i].getParameterTypes().length == numParams) {
				if (DEBUG) System.out.println("considering "+constr[i]);
				Class[] constrTypes = constr[i].getParameterTypes();
				boolean good = true;
				for (int j=0; j<constrTypes.length; j++) {
					String paramClass = constrTypes[j].getName();
					// we assume that it works for longs as well
					if (parameterValues[j] instanceof jInteger
						 && ("int".equals(paramClass) || "long".equals(paramClass)
						   || "java.lang.Integer".equals(paramClass) || "java.lang.Long".equals(paramClass)))
						continue;
					// we assume that it works for doubles as well
					if (parameterValues[j] instanceof jReal
						 && ("float".equals(paramClass) || "double".equals(paramClass)
						   || "java.lang.Float".equals(paramClass) || "java.lang.Double".equals(paramClass)))
						continue;
					// Object is good for everything, and since every Object has a toString method,
					// String is good for everything as well
					if ("java.lang.Object".equals(paramClass) || "java.lang.String".equals(paramClass))
							continue;
					// jAtom is essentially a String
					if ("java.lang.String".equals(paramClass) && (parameterValues[j] instanceof jAtom))
							continue;
					try {
						if (DEBUG) System.out.println(constrTypes[j]+"::"+parameterValues[j].getClass());
						if (constrTypes[j].isInstance(parameterValues[j]))
							continue;
					} catch (Exception ex) {}
					good = false;
					break;
				}
				if (good)
					possibleConstructors.add(new Integer(i));
			}
		}

		// Now we have a list of possible constructors. Determine which one to use by
		// examining the exact fitness of integer/float parameters.
		int idx = -1;
		int unfitnessMax=parameterValues.length+1, unfitness;

		Iterator iter = possibleConstructors.iterator();
		while (iter.hasNext()) {
			unfitness = 0;
			int curr = ((Integer) iter.next()).intValue();
			Class[] constrTypes = constr[curr].getParameterTypes();
			for (int j=0; j<constrTypes.length; j++) {
				String paramClass = constrTypes[j].getName();
				if ((parameterValues[j] instanceof jInteger) &&
					("float".equals(paramClass) || "double".equals(paramClass)
					 || "java.lang.Float".equals(paramClass) || "java.lang.Double".equals(paramClass)))
					unfitness++;
			}
			if (unfitness < unfitnessMax) {
				unfitnessMax = unfitness;
				idx = curr;
			}
			if (DEBUG) {
				System.out.println("constructor "+constr[curr]);
				System.out.println("unfitness="+unfitness);
				System.out.println("unfitnessMax="+unfitnessMax);
			}
		}

		if (idx == -1) {
			throw new RuntimeException("No suitable constructors with "+parameterValues.length
							  +" parameters was found for class "+clazz.getName()
							  +". Use bsf_create/4 instead of bsf_create/3.");
		} else {
			if (DEBUG) System.out.println("choosing "+constr[idx]);
			// now we need to replace any Strings with their numerical equivalents
		   // for those parameters that require it
			Class[] constrTypes = constr[idx].getParameterTypes();
			for (int j=0; j<constrTypes.length; j++) {
				String paramClass = constrTypes[j].getName();
				if ("int".equals(paramClass) || "long".equals(paramClass)
						|| "java.lang.Integer".equals(paramClass) || "java.lang.Long".equals(paramClass))
					parameterValues[j] = new Integer(((jInteger) parameterValues[j]).getIntegerValue());
				else if ("float".equals(paramClass) || "double".equals(paramClass)
						 || "java.lang.Float".equals(paramClass) || "java.lang.Double".equals(paramClass))
					parameterValues[j] = new Float(((jReal) parameterValues[j]).getRealValue());
				else if ("java.lang.String".equals(paramClass))
					parameterValues[j] = parameterValues[j].toString();
				else if (parameterValues[j] instanceof jTerm)
					parameterValues[j] = ((jTerm) parameterValues[j]).toString();
			}

			return constr[idx];
		}
	}

 public boolean retry (iGoalStack goals,iGoalStack proved) {
  unified.restoreVariables();
  
  goals.push(this); // a retry that follows may need a node to remove or retry
  return false;
 }
 
 public void internal_restore (iGoalStack goals) { unified.restoreVariables(); }

	public String getName() { return "bsf_create"; }

	public int getArity() { return types==null ? 3 : 4; }
	
 public String toString() {
	 StringBuffer sb = new StringBuffer(100);
	 
	sb.append(getName()).append("/").append(getArity()).append(" goal: ");
	
	sb.append(getName()).append("(").append(bean).append(",").append(beanClass).append(")");
  
  return sb.toString();
 }
}
