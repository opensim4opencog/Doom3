
/*
 This file is part of Cyc.

 Created by Glendon Holst for Alan Mackworth and the
 "Computational Intelligence: A Logical Approach" text.

 Copyright 1998, 2000, 2002 by University of British Columbia and Alan Mackworth.

 This notice must remain in all files which belong to, or are derived from Cyc.

 Check <http://cycic.sourceforge.net/> or
 <http://sourceforge.net/projects/cycic> for further information
 about Cyc, or to contact the authors.

 Cyc is free software; you can redistribute it and/or modify
 it under the terms of the GNU General Public License as published by
 the Free Software Foundation; either version 2 of the License, or
 (at your option) any later version.

 Cyc is distributed in the hope that it will be useful,
 but WITHOUT ANY WARRANTY; without even the implied warranty of
 MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 GNU General Public License for more details.

 You should have received a copy of the GNU General Public License
 along with Cyc; if not, write to the Free Software
 Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA
 URLs: <http://www.fsf.org> or <http://www.gnu.org>
 */
//#########################################################################
//	CycBSFEngine
//#########################################################################

package daxclr.inference;

import java.lang.reflect.Field;
import java.util.*;

import org.apache.bsf.*;
import org.apache.bsf.util.BSFEngineImpl;

import org.opencyc.cyclobject.*;
import org.opencyc.cycobject.*;

import ubc.cs.JLog.Terms.iTermToObject;

/**
 * This is an adapter for Cyc to be used with BSF,
 * the Bean Scripting Framework from IBM and Apache Jakarta.
 *
 * This BSFEngine does not support the concept of 'declared' beans;
 * it treats them the same as 'registered' beans. That is due to there not
 * really being a concept of free-standing variables in CycL, just predicate calls.
 *
 * @author       Ulf Dittmer (Copyright 2005)
 */

public class CycBSFEngine extends BSFEngineImpl {

	protected static final boolean DEBUG = false;

	// static package access, so that the other classes can get at it
	static CycBSFEngine engine = null;

	private CycAPI cycAccess;
	private List imports;

	/**
	 * Initialize the engine.
	 */
	public void initialize (BSFManager mgrParam, String langParam, Vector declaredBeansParam) throws BSFException {
		super.initialize(mgrParam, langParam, declaredBeansParam);
		if (DEBUG) System.out.println("CycBSFEngine.initialize: "+langParam);

		// get an instance of the API
		cycAccess = CycAPI.current();

		imports = new ArrayList();
		imports.add("");			// used for package-less classes
		addImport("java.lang");		// simply because Java does it as well

		engine = this;

		// the BSF library load call should only we executed
		// if we're not already in the library init routine
		boolean amInInit = false;
		StackTraceElement[] ste = new Throwable().getStackTrace();
		for (int i=0; i<ste.length; i++)
			if (ste[i].getClassName().indexOf("CycBSFInit") != -1)
				amInInit = true;

		if (! amInInit)
			engine.exec("load-library", -1, -1, ":- load_library('bsf').");
	}

	/**
	 * Shut down the engine.
	 */
	public void terminate() {
		super.terminate();
		cycAccess = null;
	}

	/**
	 * Package visibility, because clients should not call this method.
	 */
	BSFManager getManager() {
		return mgr;
	}

	/**
	 * Adds a package to the list of imports
	 * Package visibility, because clients should not call this method.
	 */
	void addImport (String pckg) {
		if (pckg.lastIndexOf(".") == pckg.length())
			imports.add(pckg);
		else
			imports.add(pckg+".");
	}

	/**
	 * Tries to find a class for this (probably not fully qualified) class name.
	 * Package visibility, because clients should not call this method.
	 * It is also possible to pass static field designators, like System.out.
	 * If the class or field is found, 'true' is returned, otherwise 'false'.
	 * In that case, the Class of 'out', not of 'System', is returned,
	 * and the field is also returned.
	 * The 'results' array has the class at position 0,
	 * and the field (which can be null) at position 1. It needs to be preallocated.
	 */
	boolean getClass (String className, Object[] result) {
		if (DEBUG) System.out.println("trying to get class: " + className);

		if ((result == null) || (result.length < 2))
			throw new RuntimeException("CycBSFEngine.getClass needs a 'result' parameter that is at least two elements long.");

		Class clazz = null;
		int idx = -1;
		String name = className;

		for (;;) {
			Iterator iter = imports.iterator();
			while (iter.hasNext() && (clazz == null)) {
				String pkg = (String) iter.next();
				try {
					if (DEBUG) System.out.println(pkg+name);
					clazz = Class.forName(pkg+name);
				} catch (Exception ex) { }
			}
			if (clazz != null) break;

			idx = name.lastIndexOf(".");
			if (idx > -1) {
				name = name.substring(0, idx);
			} else {
				break;
			}
		}

		if ((clazz != null) && (name.length() != className.length())) {
			// at least one field was referenced,
			// and idx is the index of the dot between the class and the first field
			String fieldName = className.substring(idx+1);
			Field fld = null;

			for (;;) {
				if (DEBUG) System.out.println("fieldName: " + fieldName);
				idx = fieldName.indexOf(".");
				if (idx > -1) {
					try { fld = clazz.getDeclaredField(fieldName.substring(0,idx)); } catch (NoSuchFieldException nsfex) { }
					fieldName = fieldName.substring(idx+1);
				} else {
					try { fld = clazz.getDeclaredField(fieldName); } catch (NoSuchFieldException nsfex) { }
				}

				if (fld == null) {
					clazz = null;
					break;
				} else {
					try {
						result[1] = fld.get(null);		// this works for static fields only
					} catch (IllegalAccessException iaex) {
						return false;
					}
					clazz = fld.getType();
					if (DEBUG) System.out.println("replacing class with: " + clazz.getName());
					if (idx < 0) break;
				}
			}
		}

		result[0] = clazz;
		return (clazz != null);
	}

	/**
	 * Translates a Java object to its CycL equivalent.
	 */
	public CycObject object2term (Object obj) {
		InferenceAPI translator = cycAccess.getObjectToTermConverter(obj.getClass());
		CycObject term =  (CycObject)translator.createTermFromObject(obj);
		if (DEBUG) System.out.println("obj2term: "+obj.getClass()+" --> "+term.getClass());
		return term;
	}

	/**
	 * Translates a CycL object to its Java equivalent.
	 */
	public Object term2object (CycObject term) {
		InferenceAPI translator = cycAccess.getTermToObjectConverter(term.getClass());
		Object obj = translator.createObjectFromTerm(term);
		if (DEBUG) System.out.println("term2obj: "+term.getClass()+" --> "+obj.getClass());
		return obj;
	}

	/**
	 * Internally used method to retrieve the String value of a CycObject
	 * Package visibility, because clients should not call this method.
	 */
	CycObject extractValue (CycObject term) {
		if (term instanceof CycList) {
			return CycNart.coerceToCycNart(term);	
		} else {
			if (true) return term;
			return CycAPI.c(term.toString());
		}
	}

	/**
	 * Look up a bean in the BSFManager.
	 */
	public Object lookupBean (String beanName) {
		if (DEBUG) System.out.println("lookupBean: "+beanName);
		return mgr.lookupBean(beanName);
	}

	/**
	 * Register a bean with the BSFManager.
	 */
	public void registerBean (String beanName, Object bean) {
		if (DEBUG) System.out.println("registerBean: "+beanName);
		mgr.registerBean(beanName, bean);
	}

	/**
	 * Unregister a previously registered bean.
	 */
	public void unregisterBean (String beanName) throws BSFException {
		if (DEBUG) System.out.println("unregisterBean: "+beanName);
		mgr.unregisterBean(beanName);
	}

	/**
	 * Declare a bean with the BSFManager.
	 * Since declared beans are unsupported, the bean is simply registered.
	 */
	public void declareBean (BSFDeclaredBean bean) throws BSFException {
		if (DEBUG) System.out.println("declareBean: "+bean.name);
		registerBean(bean.name, bean.bean);
	}

	/**
	 * Undeclare a previously registered bean.
	 */
	public void undeclareBean (BSFDeclaredBean bean) throws BSFException {
		if (DEBUG) System.out.println("undeclareBean: "+bean.name);
		unregisterBean(bean.name);
	}

	/**
	 * @param method The name of the method to call.
	 * @param args an array of arguments to be
	 * passed to the extension, which may be either Vectors of Nodes, or Strings.
	 */
	public Object call (Object obj, String method, Object[] args) throws BSFException {
		StringBuffer script = new StringBuffer(method);
		script.append("(");
		boolean first = true;
		if (args != null) {
			for (int i=0; i<args.length; i++) {
				if (! first)
					script.append(", ");
				script.append(args[i].toString());
				first = false;
			}
		}
		script.append(").");
		return eval("<function call>", 0, 0, script.toString());
	}

	/**
	 * This is used by an application to evaluate a string containing some expression.
	 */
	public Object eval (String source, int lineNo, int columnNo, Object oscript) throws BSFException {
		String script = oscript.toString();
		if (DEBUG) System.out.println("CycBSFEngine.eval: "+script);
		CycList result =  cycAccess.converseList(script);
		/*
		if (result == null)
			throw new BSFException(BSFException.REASON_EXECUTION_ERROR,
				"error while eval'ing CycL expression: " + script);
		*/
		return result;
	}

	/**
	 * This is used to consult a string. The very last statement is used as a query,
	 * because otherwise 'exec' wouldn't actually execute anything, just load.
	 * If querying the last statement throws an exception, it is simply consulted instead.
	 */
	public void exec (String source, int lineNo, int columnNo, Object script) throws BSFException {
		String part1, part2, scriptStr = script.toString();
		int lastStmt = getStartOfLastStatement(scriptStr);

		if (lastStmt == 0) {
			part1 = null;
			part2 = scriptStr;
		} else if (lastStmt == scriptStr.length()) {
			part1 = scriptStr;
			part2 = null;
		} else {
			part1 = scriptStr.substring(0, lastStmt);
			part2 = scriptStr.substring(lastStmt);
		}
		if (DEBUG) System.out.println("part1="+part1);
		if (DEBUG) System.out.println("part2="+part2);

		if (part1 != null) 
		{
		 try
		 {
		  cycAccess.converseVoid(part1);
         }
		 catch (Exception e)
		 {
		  throw new BSFException(BSFException.REASON_INVALID_ARGUMENT,
						"error while consulting CycL script");
		 }
		}

		if (part2 != null)
			eval(source, lineNo, columnNo, part2);
	}

	/**
	 * Determines the start of the last statement of the source.
	 */
	private int getStartOfLastStatement (String source) {
		int firstDot=-1, secondDot=-1, notBefore=-1;
		int paren = 0;
		boolean inQuotes=false, inComment=false, colon=false;

		for (int i=0; i<source.length(); i++) {
			char ch = source.charAt(i);
			if (ch == '\n' || ch == '\r') { if (! inQuotes) inComment = false; continue; }
			if (ch == '%') { if (!inQuotes) inComment = true; continue; }
			if (inComment) continue;
			if (ch == '\'') { inQuotes = !inQuotes; continue; }
			if (inQuotes) continue;
			if (ch == '(') { paren++; continue; }
			if (ch == ')') { paren--; continue; }
			if (paren != 0) continue;
			if ((ch == '-') && colon) { firstDot = secondDot = -1; notBefore = i; }
			colon = (ch == ':');
			if ((ch == '.') && (firstDot == -1)) { firstDot = i; continue; }
			if ((ch == '.') && (secondDot == -1)) { secondDot = i; continue; }
			if (ch == '.') { firstDot = secondDot; secondDot = i; continue; }
		}

		if (secondDot == -1) {
			if (notBefore == -1)
				return 0;
			else
				return source.length();
		} else
			return firstDot+1;
	}
}
