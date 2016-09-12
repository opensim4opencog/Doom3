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
//	InvokeGoal
//
// bsf_invoke(ResultVar, Bean, MethodName, Parameters [, Types])
//
//#########################################################################

package ubc.cs.JLog.Extras.BSF;

import java.lang.reflect.Method;
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

public class jInvokeGoal extends jGoal {

	protected static final boolean DEBUG = false;

	protected jTerm result, bean, methodName, types, parameters;
	protected jUnifiedVector unified;
	protected JLogBSFEngine engine;

	public jInvokeGoal (jTerm t1, jTerm t2, jTerm t3, jTerm t4, JLogBSFEngine engineParam) 
	{
		this(t1, t2, t3, t4, null, engineParam);
	}

	public jInvokeGoal (jTerm t1, jTerm t2, jTerm t3, jTerm t4, jTerm t5, JLogBSFEngine engineParam) 
	{
		super();
		result = t1;
		bean = t2;
		methodName = t3;
		parameters = t4;
		types = t5;
		engine = engineParam;
		unified = new jUnifiedVector();
	}

	public boolean prove (iGoalStack goals, iGoalStack proved) 
	{
		if (! (bean instanceof jVariable) || (! ((jVariable) bean).isBound()))
			throw new IllegalArgumentException("Variable 'Bean' is unbound");
	
		Object beanObj = ((jObject) ((jVariable) bean).getTerm()).getObjectReference();
		Class clazz = beanObj.getClass();
		
		if ("java.lang.Class".equals(clazz.getName())) 
		{
			// this is the case if a static method of a class is invoked
			try 
			{
				if (DEBUG) 
					System.out.println("replacing class::"+((Class) beanObj).getName()+"::");

				clazz = Class.forName(((Class) beanObj).getName());
			} catch (Exception ex) {
				System.err.println("invoke.prove: problem with Class.forName: "+ex.getMessage());
			}
		}

		String methodNameStr = engine.extractValue(methodName).toString();
		if (methodNameStr == null)
			throw new IllegalArgumentException("Variable 'MethodName' is unbound");

		if (DEBUG) 
			System.out.println("bsf_invoke::"+bean.toString()+"("+clazz.getName()+")::"+methodNameStr);

		boolean success = false;
		jTerm rtnJTerm = null;

		try 
		{
			Method method;
			Object[] parameterValues;

			if (types == null) {
				// construct list of parameter values
				List paramList = new ArrayList();
				jTerm ptr = parameters;
				while (ptr != null) {
					if (ptr instanceof jListPair) {
						jTerm head = ((jListPair) ptr).getHead();
						ptr = ((jListPair) ptr).getTail();
						if (DEBUG) System.out.println("list: "+head.getClass());
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
						if (DEBUG) System.out.println("atom: "+ptr.getClass());
						paramList.add(ptr);
						ptr = null;
					} else if (ptr instanceof jVariable) {
						if (((jVariable) ptr).isBound()) {
							jTerm value = ((jVariable) ptr).getTerm();
							if (DEBUG) System.out.println("variable: "+value.getClass());
							if (value instanceof jObject) {
								paramList.add(((jObject) value).getObjectReference());
							} else {
								paramList.add(value);
							}
						} else {
							throw new RuntimeException(getName()+": variable is unbound");
						}
						ptr = null;
					} else if (ptr instanceof jNullList) {
						if (DEBUG) System.out.println("null-list");
						ptr = null;
					}
				}
				if (DEBUG) System.out.println(paramList);
				parameterValues = paramList.toArray();
				if (DEBUG)
					for (int i=0; i<parameterValues.length; i++)
						System.out.println(i+"::"+parameterValues[i].getClass()+"::"+parameterValues[i]);

				// get the method that's appropriate for the given parameters
				// and, finally, invoke method and handle return value
				method = getSuitableMethod(clazz, methodNameStr, parameterValues);
				if (DEBUG) {
					System.out.println(methodNameStr+"::"+method.getName()+"::numParams="+parameterValues.length);
					for (int i=0; i<parameterValues.length; i++)
						System.out.println(i+"::"+parameterValues[i].getClass()+"::"+parameterValues[i]);
					if (bean != null) System.out.println("bean="+bean.getClass());
				}
			} else {
				// construct list of parameter types
				List paramList = new ArrayList();
				jTerm ptr = types;
				String headStr = null;
// UCD: things seemed to work with the two 'tmpClass' statements active - what did they do?
//				Class tmpClass = null;
				while (ptr != null) {
					if (ptr instanceof jListPair) {
						jTerm head = ((jListPair) ptr).getHead();
						ptr = ((jListPair) ptr).getTail();
						headStr = engine.extractValue(head).toString();
						Object[] resultObj = new Object[2];
						if (! engine.getClass(headStr, resultObj))
							throw new IllegalArgumentException("class '"+headStr+"' not found");
						paramList.add(resultObj[0]);
					} else if (ptr instanceof jAtom) {
						headStr = engine.extractValue(ptr).toString();
						Object[] resultObj = new Object[2];
						if (! engine.getClass(headStr, resultObj))
							throw new IllegalArgumentException("class '"+headStr+"' not found");
						paramList.add(resultObj[0]);
//						paramList.add(tmpClass);
						ptr = null;
					} else if (ptr instanceof jNullList) {
						ptr = null;
					}
				}
				if (DEBUG) System.out.println(paramList);
				Class[] parameterTypes = new Class[paramList.size()];
				for (int i=0; i<paramList.size(); i++)
					parameterTypes[i] = (Class) paramList.get(i);
				method = clazz.getMethod(methodNameStr, parameterTypes);

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

			// finally, invoke method and handle return value
			Object obj = method.invoke(beanObj, parameterValues);
			if ("void".equals(method.getReturnType().toString())) {
				success = true;
			} else {
				if (result instanceof jVariable)
					success = result.unify(new jObject(obj), unified);
				if (DEBUG) System.out.println("result="+obj+",success="+success);
			}
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

	// internally used method that retrieves a suitable method
	// based on the parameters and some very simple heuristics
	protected Method getSuitableMethod (Class clazz, String methodNameStr, Object[] parameterValues) {
		int numParams = parameterValues.length;
		if (DEBUG) {
			System.out.println("class="+clazz.getName());
			for (int i=0; i<parameterValues.length; i++) {
				System.out.println(parameterValues[i]);
				System.out.println("jInteger="+(parameterValues[i] instanceof jInteger));
				System.out.println("jReal="+(parameterValues[i] instanceof jReal));
			}
		}

		List possibleMethods = new ArrayList();
		Method[] methods = clazz.getMethods();
		boolean isStringArray = false;
		for (int i=0; i<methods.length; i++) {
			if (methodNameStr.equals(methods[i].getName())
					&& (methods[i].getParameterTypes().length == numParams)) {
				if (DEBUG) System.out.println("considering "+methods[i]);
				Class[] methodTypes = methods[i].getParameterTypes();
				boolean good = true;
				for (int j=0; j<methodTypes.length; j++) {
					String paramClass = methodTypes[j].getName();
					if (DEBUG) System.out.println(j+"::"+paramClass+"::"+methodTypes[j].isArray());
					// we know that works even for longs, because we tried it when initializing canInt[]
					if (parameterValues[j] instanceof jInteger
						&& ("int".equals(paramClass) || "long".equals(paramClass)
							|| "java.lang.Integer".equals(paramClass) || "java.lang.Long".equals(paramClass)))
						continue;
					// we know that works even for doubles, because we tried it when initializing canFloat[]
					if (parameterValues[j] instanceof jReal
						&& ("float".equals(paramClass) || "double".equals(paramClass)
							|| "java.lang.Float".equals(paramClass) || "java.lang.Double".equals(paramClass)))
						continue;
					// Object is good for everything, and since every Object has a toString method,
					// String is good for everything as well
					if ("java.lang.Object".equals(paramClass) || "java.lang.String".equals(paramClass))
						continue;
					// kind of loose: an array squares with a jListPair
					if (methodTypes[j].isArray() && (parameterValues[j] instanceof jListPair)) {
							// extremely loose: we're assuming that any array that's not String is Object
						isStringArray = methodTypes[j].getName().indexOf("String") > -1;
						if (DEBUG) System.out.println("isStringArray="+isStringArray);
						continue;
					}
					// jAtom is essentially a String
					if ("java.lang.String".equals(paramClass) &&
		 		 		 ((parameterValues[j] instanceof jAtom)
						 	|| (parameterValues[j] instanceof jVariable)
						 	|| (parameterValues[j] instanceof jTerm)))
						continue;
					try {
						if (DEBUG) System.out.println(methodTypes[j]+"::"+parameterValues[j].getClass());
						if (methodTypes[j].isInstance(parameterValues[j]))
							continue;
					} catch (Exception ex) {}
					good = false;
					break;
				}
				if (good)
					possibleMethods.add(new Integer(i));
			}
		}

		// Now we have a list of possible methods. Determine which one to use by
		// examining the exact fitness of integer/float parameters.
		int idx = -1;
		int unfitnessMax=parameterValues.length+1, unfitness;

		Iterator iter = possibleMethods.iterator();
		while (iter.hasNext()) {
			unfitness = 0;
			int curr = ((Integer) iter.next()).intValue();
			Class[] methTypes = methods[curr].getParameterTypes();
			for (int j=0; j<methTypes.length; j++) {
				String paramClass = methTypes[j].getName();
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
				System.out.println("method "+methods[curr]);
				System.out.println("unfitness="+unfitness);
				System.out.println("unfitnessMax="+unfitnessMax);
			}
		}

		if (idx == -1) {
			throw new RuntimeException("No suitable method named '"+methodNameStr+" with "+parameterValues.length
							  +" parameters was found in class "+clazz.getName()
							  +". Use bsf_invoke/5 instead of bsf_invoke/4.");
		} else {
			if (DEBUG) System.out.println("choosing "+methods[idx]);
			// now we need to replace any Strings with their numerical equivalents
			// for those parameters that require it
			Class[] methodTypes = methods[idx].getParameterTypes();
			for (int j=0; j<methodTypes.length; j++) {
				String paramClass = methodTypes[j].getName();
				if ("int".equals(paramClass) || "long".equals(paramClass)
					|| "java.lang.Integer".equals(paramClass) || "java.lang.Long".equals(paramClass)) {
					parameterValues[j] = new Integer(((jInteger) parameterValues[j]).getIntegerValue());
				} else if ("float".equals(paramClass) || "double".equals(paramClass)
					|| "java.lang.Float".equals(paramClass) || "java.lang.Double".equals(paramClass)) {
					parameterValues[j] = new Float(((jReal) parameterValues[j]).getRealValue());
				} else if ("java.lang.String".equals(paramClass)) {
					parameterValues[j] = parameterValues[j].toString();
				} else if (parameterValues[j] instanceof jListPair) {
						// copy all list elements into an array
					List vec = new ArrayList();
					jTerm next = (jTerm) parameterValues[j];
					while (next != null) {
						if (DEBUG) System.out.println(next.getClass());
						if (next instanceof jNullList) break;
						jListPair list = (jListPair) next;
						vec.add(list.getHead().toString());
						next = list.getTail();
					}
					// DO NOT DO: generalize this for array types other than Object and String
					// firstly, this doesn't work due to the (Object[]) cast, and secondly,
					// the above code ensures that all elements are String anyway
					//parameterValues[j] = vec.toArray((Object[]) Array.newInstance(methodTypes[j], methodTypes.length));

					parameterValues[j] = vec.toArray(isStringArray ? new String[0] : new Object[0]);
				} else if (parameterValues[j] instanceof jTerm) {
					parameterValues[j] = ((jTerm) parameterValues[j]).toString();
				}
				if (DEBUG) System.out.println(paramClass+"::"+parameterValues[j]);
			}

			return methods[idx];
		}
	}

 public boolean retry (iGoalStack goals,iGoalStack proved) {
  unified.restoreVariables();
  
  goals.push(this); // a retry that follows may need a node to remove or retry
  return false;
 }
 
 public void internal_restore (iGoalStack goals) { unified.restoreVariables(); }

 public String getName() 
 { 
  return "invoke"; 
 }
 
 public int getArity() 
 { 
  return (types == null ? 4 : 5); 
 }
 
 public String toString() 
 {StringBuffer sb = new StringBuffer(100);
	 
  sb.append(getName()).append("/").append(getArity()).append(" goal: ");
  sb.append(getName()).append("(").append(bean).append(",").append(methodName).append(")");
  
  return sb.toString();
 }
}
