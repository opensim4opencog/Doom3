package daxclr.bsf;

import java.io.Serializable;

import bsh.CallStack;
import bsh.Interpreter;
import bsh.NameSpace;
import bsh.SimpleNode;
import bsh.This;

public interface IScriptMethodImpl {

	public Serializable invokeMethodImpl(NameSpace nameSpace, This xthis,
			Object proxy, String methodName, Class[] paramaterTypes,
			Object[] params, Interpreter interpreter, CallStack callstack,
			SimpleNode simpleNode, boolean declaredOnly);

}