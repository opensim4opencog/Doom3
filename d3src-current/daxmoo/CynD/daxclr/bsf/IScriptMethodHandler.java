package daxclr.bsf;

import java.io.Serializable;
import java.lang.reflect.InvocationHandler;
import java.rmi.Remote;
import java.rmi.RemoteException;

import org.apache.bsf.BSFException;

import bsh.EvalError;

public interface IScriptMethodHandler extends Remote, InvocationHandler{

	/**
	 * The DoomConsole calls this method each time any command is used
	 * 
	 * @param target
	 * @param cmd
	 * @param cmdArgs
	 *            is the String[] with the command used located at cmdArgs[0]
	 * 
	 * @return true if this AbstractDoomCommand module decides to handle the
	 *         event as this will cancle other commands from processing it
	 * @throws NoSuchMethodException
	 * @throws Error 
	 * @throws Exception 
	 * @throws Exception 
	 * @throws
	 */
	// public Object call(Object target,String cmd, Object[] cmdArgs) throws
	// BSFException;
	public Serializable invokeMethod(String cmd, Object[] cmdArgs)
			throws NoSuchMethodException, RemoteException, BSFException,
			EvalError;

}