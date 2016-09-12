package daxclr.bsf;

import java.io.Serializable;
import java.rmi.Remote;
import java.rmi.RemoteException;

import bsh.EvalError;

public interface IScriptObjectThis extends Remote, IScriptObjectProxy {

	public IScriptMethodHandler getInvocationHandler() throws RemoteException;

	/**
	 * Get dynamic proxy for interface, caching those it creates.
	 */
	public Object getInterface(Class clas) throws RemoteException;

	/**
	 * Get dynamic proxy for interface, caching those it creates.
	 */
	public Object getInterface(Class[] clas) throws RemoteException;

	/**
	 * The namespace that this This reference wraps.
	 */
	// NameSpace namespace;
	/**
	 * This is the interpreter running when the This ref was created. It's used
	 * as a default interpreter for callback through the This where there is no
	 * current interpreter instance e.g. interface proxy or event call backs
	 * from outside of bsh.
	 */
	// transient Interpreter declaringInterpreter;
	/**
	 * getThis() is a factory for bsh.This type references. The capabilities of
	 * ".this" references in bsh are version dependent up until jdk1.3. The
	 * version dependence was to support different default interface
	 * implementations. i.e. different sets of listener interfaces which
	 * scripted objects were capable of implementing. In jdk1.3 the reflection
	 * proxy mechanism was introduced which allowed us to implement arbitrary
	 * interfaces. This is fantastic.
	 * 
	 * A This object is a thin layer over a namespace, comprising a bsh object
	 * context. We create it here only if needed for the namespace.
	 * 
	 * Note: this method could be considered slow because of the way it
	 * dynamically factories objects. However I've also done tests where I
	 * hard-code the factory to return JThis and see no change in the rough test
	 * suite time. This references are also cached in NameSpace.
	 */
	/*
	 * static This getThis(NameSpace namespace, Interpreter
	 * declaringInterpreter) { try { Class c; if
	 * (Capabilities.canGenerateInterfaces()) c = Class.forName("bsh.XThis")
	 * throws RemoteException; else if (Capabilities.haveSwing()) c =
	 * Class.forName("bsh.JThis") throws RemoteException; else return new
	 * This(namespace, declaringInterpreter) throws RemoteException;
	 * 
	 * return (This) Reflect.constructObject(c, new Object[] { namespace,
	 * declaringInterpreter }) throws RemoteException; } catch (Exception e) {
	 * throw new InterpreterError("internal error 1 in This: " + e) throws
	 * RemoteException; } }
	 */
	/*
	 * I wish protected access were limited to children and not also package
	 * scope... I want this to be a singleton implemented by various children.
	 */

	public void run() throws RemoteException;

	/**
	 * Invoke specified method as from outside java code, using the declaring
	 * interpreter and current namespace. The call stack will indicate that the
	 * method is being invoked from outside of bsh in native java code. Note:
	 * you must still wrap/unwrap params/return values using
	 * Primitive/Primitive.unwrap() for use outside of BeanShell.
	 * 
	 * @see bsh.Primitive
	 */
	public Serializable invokeMethod(String methodName, Object[] params)
			throws RemoteException, EvalError;

	public IScriptObjectRemote toRemote() throws RemoteException;

}