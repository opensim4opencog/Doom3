package daxclr.doom.server;

import java.io.Serializable;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Modifier;
import java.rmi.RemoteException;

import javax.naming.NameAlreadyBoundException;

import org.apache.bsf.BSFException;

import bsh.EvalError;

import sun.reflect.MethodAccessor;
import daxclr.doom.IClass;
import daxclr.doom.IEntity;
import daxclr.doom.IGameLocal;
import daxclr.doom.modules.RemoteDoomModule;

class IdMethod extends RemoteDoomModule implements java.lang.reflect.Member,
		MethodAccessor {

	public String o_scriptClass = null;

	public String o_scriptType = null;

	public String o_scriptFullname;

	public String o_scriptName;

	public int o_scriptArity = -1;

	public String o_scriptSignature = null;

	public String[] o_scriptParam = null;

	public String[] o_scriptParamName = null;

	public String o_scriptReturn = null;

	public int o_function_index = -1;

	public int o_function_type = -1;

	public static int METHOD_MISSING = 0;

	public static int DOOM_FUNCTION = 1;

	public static int DOOM_EVENT = 2;

	public static int JAVA_FUNCTION = -1;

	public static int JAVA_EVENT = -2;

	/**
	 * Returns the Java language modifiers for the member or constructor
	 * represented by this Member, as an integer. The Modifier class should be
	 * used to decode the modifiers in the integer.
	 * 
	 * @return the Java language modifiers for the underlying member
	 * @see Modifier
	 */
	public int getModifiers() {
		return 1; // Public
	}

	/**
	 * Returns <tt>true</tt> if this member was introduced by the compiler;
	 * returns <tt>false</tt> otherwise.
	 * 
	 * @return true if and only if this member was introduced by the compiler.
	 * @since 1.5
	 */
	public boolean isSynthetic() {
		return false;
	}

	/**
	 * Returns a <code>Class</code> object that represents the formal return
	 * type of the method represented by this <code>Method</code> object.
	 * 
	 * @return the return type for the method this object represents
	 */
	public Class getReturnType() throws java.lang.ClassNotFoundException {
		syncOnIndex();
		try {
			return gameLocal.toClass(o_scriptReturn);
		} catch (RemoteException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			throw new RuntimeException(e);
		} catch (Throwable e) {
			throw new RuntimeException(e);

		}
	}

	public int hashCode() {
		return o_function_index;
	}// ISys

	/**
	 * /** Returns the simple name of the underlying member or constructor
	 * represented by this Member.
	 * 
	 * @return * Returns the name of the method represented by this
	 *         <code>Method</code> object, as a <code>String</code>.
	 */
	public String getName() {
		syncOnIndex();
		return o_scriptFullname;
	}

	/**
	 * Returns an array of <code>Class</code> objects that represent the
	 * formal parameter types, in declaration order, of the method represented
	 * by this <code>Method</code> object. Returns an array of length 0 if the
	 * underlying method takes no parameters.
	 * 
	 * @return the parameter types for the method this object represents
	 */
	public Class[] getParameterTypes() {
		syncOnIndex();
		Class[] theJavaParams = new Class[o_scriptArity];
		for (int i = 0; i < o_scriptArity; i++) {
			try {
				try {
					theJavaParams[i] = gameLocal.toClass(o_scriptParam[i]);
				} catch (RemoteException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
					throw new RuntimeException(e);
				}
			} catch (ClassNotFoundException e) {
				// TODO Auto-generated catch block
				theJavaParams[i] = String.class;
			}
		}
		return theJavaParams;
	}

	public Serializable invoke(Object obj, Object[] args)
			throws IllegalArgumentException, InvocationTargetException {
		if (args.length == o_scriptArity) {
			try {
				return gameLocal.invokeFunction(o_function_index, gameLocal
						.toObjects(args));
			} catch (RemoteException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				throw new RuntimeException(e);
			} catch (Throwable e) {
				throw new RuntimeException(e);

			}
		}
		if (args.length == o_scriptArity - 1) {
			Serializable[] newargs = new Serializable[o_scriptArity];
			try {
				newargs[0] = gameLocal.toObject(obj);
			} catch (RemoteException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
				throw new RuntimeException(e1);
				} catch (Throwable e1) {
				throw new RuntimeException(e1);
				
			}
			for (int i = 0; i < o_scriptArity - 1; i++) {
				try {
					newargs[i + 1] = gameLocal.toObject(args[i]);
				} catch (RemoteException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
					throw new RuntimeException(e);
					} catch (Throwable e) {
					throw new RuntimeException(e);
					
				}
			}
			try {
				return invoke(obj, newargs);
			} catch (Throwable ex) {
				// TODO Auto-generated catch block
				ex.printStackTrace();
			}
		}
		throw new IllegalArgumentException("" + o_scriptFullname + " uses "
				+ o_scriptArity + " but only recieved " + args.length);
	}

	// static public IdGameLocal getGameLocal();
	public IdMethod(String longname, int i, String clazz, String scriptDesc,
			int arity) throws NameAlreadyBoundException {
		super(longname);
		o_scriptFullname = longname;
		o_scriptName = longname;
		o_scriptClass = clazz;
		o_scriptSignature = scriptDesc;
		o_function_index = i;
		o_scriptArity = arity;
	}

	public IdMethod(int i) throws NameAlreadyBoundException, RemoteException {
		super(gameLocal. scriptFullname(i));
		o_function_index = i;
		syncOnIndex();
	}

	// public Method getMethod() {
	// return new Method(getDeclaringClass(),
	// }
	/**
	 * 
	 * @param clzz
	 * @param entname
	 * @param funcname
	 * @return
	 */
	final static private String funInfo(final String clzz,
			final String entname, final String funcname) {
		IClass ic;
		try {
			ic = gameLocal.getGameLocal().resolveDoomObject(entname);
		} catch (RemoteException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			throw new RuntimeException(e);
		} catch (Throwable e) {
			throw new RuntimeException(e);

		}
		long pointer = 0L;
		if (ic != null) {
			pointer = ic.getPointer();
		}
		try {
			return funInfo(gameLocal.scriptNumber(clzz, pointer, funcname));
		} catch (RemoteException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			throw new RuntimeException(e);
		} catch (Throwable e) {
			throw new RuntimeException(e);

		}
	}

	/**
	 * 
	 * @param funcnum
	 * @return
	 */
	final static private String funInfo(final int funcnum) {
		String buf;
		try {
			buf = gameLocal.scriptParameterType(funcnum, -1) + " "
					+ gameLocal.scriptParameterType(funcnum, 0) + "::"
					+ gameLocal.scriptName(funcnum) + "(";

			final int len = gameLocal.scriptArity(funcnum);
			for (int i = 1; i <= len; i++) {
				buf += gameLocal.scriptParameterType(funcnum, i);
				if (i < len) {
					buf += ",";
				}
			}
			return buf + "); // " + funcnum;
		} catch (RemoteException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			throw new RuntimeException(e);
		} catch (Throwable e) {
			throw new RuntimeException(e);

		}
	}

	public void syncOnIndex() {
		if (o_function_index < 1) {
			return;
		}
		if (o_scriptClass == null) {
			try {
				o_scriptClass = gameLocal.scriptClass(o_function_index);
				if (o_scriptReturn == null) {
					o_scriptReturn = gameLocal
							.scriptReturnType(o_function_index);
				}
				if (o_scriptName == null) {
					o_scriptName = gameLocal.scriptName(o_function_index);
				}
				if (o_scriptFullname == null) {
					o_scriptFullname = gameLocal
							.scriptFullname(o_function_index);
				}
				if (o_scriptSignature == null) {
					o_scriptSignature = gameLocal
							.scriptSignature(o_function_index);
				}
				if (o_scriptArity < 0) {
					o_scriptArity = gameLocal.scriptArity(o_function_index);
				}
				if (o_scriptParam == null) {
					o_scriptParam = new String[o_scriptArity];
				}
				if (o_scriptParamName == null) {
					o_scriptParamName = new String[o_scriptArity];
					for (int i = 0; i < o_scriptArity; i++) {
						o_scriptParam[i] = gameLocal.scriptParameterType(
								o_function_index, i + 1);
						o_scriptParamName[i] = gameLocal.scriptParameterName(
								o_function_index, i + 1);
					}
				}
			} catch (RemoteException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				throw new RuntimeException(e);
			} catch (Throwable e) {
				throw new RuntimeException(e);

			}
		}
	}

	public String toString() {
		return getScriptSignature();
	}

	public String getScriptSignature() {
		if (o_scriptSignature == null) {
			try {
				o_scriptSignature = gameLocal.scriptSignature(o_function_index);
			} catch (RemoteException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				throw new RuntimeException(e);
			} catch (Throwable e) {
				throw new RuntimeException(e);

			}
		}
		return o_scriptSignature;
	}

	/**
	 * Returns the Class object representing the class or interface that
	 * declares the member or constructor represented by this Member.
	 * 
	 * @return an object representing the declaring class of the underlying
	 *         member
	 */
	public Class getDeclaringClass() {
		return IEntity.class;
	}

	public void run() {

	}

	public Serializable invokeMethod(String cmd, Object[] cmdArgs)
			throws NoSuchMethodException, RemoteException, BSFException,
			EvalError {
		return null;
	}
}
