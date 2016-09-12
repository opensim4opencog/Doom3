package daxclr.doom.server;

import java.io.Serializable;
import java.net.MalformedURLException;
import java.rmi.AccessException;
import java.rmi.NotBoundException;
import java.rmi.Remote;
import java.rmi.RemoteException;
import java.rmi.registry.Registry;

import org.apache.bsf.BSFException;

import bsh.EvalError;
import daxclr.bsf.IObjectRepository;
import daxclr.bsf.IScriptObjectRemote;
import daxclr.doom.IClass;
import daxclr.doom.IEntity;
import daxclr.doom.IGameLocal;
import daxclr.doom.IVector;

public class NativeDoomServer extends DoomServer implements Remote,
		INativeServer {
	
	public static void main(String [] t) {
		
	}
	/**
	 * 
	 */
	//private static final long serialVersionUID = 4974527148936298033L;
	// IDoomServer

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	/**
	 * 
	 */

	public IScriptObjectRemote toRemote() throws RemoteException {
		return getGameLocal().toRemote();
	}

	public native IClass resolveDoomObject(String s);

	
	/**
	 * @return the pathToBase
	 * 
	 * 
	 * http://www.mindswap.org/2004/SWOOP/media/ to run it hava java enabled in
	 * browser http://www.mindswap.org/2004/SWOOP/Swoop.jnlp when you run it you
	 * have to imagine this is CycL and not OWL
	 * 
	 * 
	 */
	public String getBaseDirectory() {
		return "c:/doom3/base/";
	}
	public String getName() throws RemoteException {
		return "nativeServer";
	}

	public native void printLocal(String msg);

	public native void debugLocal(String msg);

	public boolean isDoomServerAvailable() {
		try {
			isMapLoaded();
			return true;
		} catch (Throwable t) {
			return false;
		}
	}

	public IGameLocal getGameLocal() {
		try {
			return (IGameLocal) java.rmi.Naming.lookup("rmi://localhost:"
					+ IGameLocal.REGISTRY_PORT_GAMELOCAL + "/gameLocal");
		} catch (MalformedURLException e) {
			e.printStackTrace();
			throw (new RuntimeException(e));
		} catch (RemoteException e) {
			e.printStackTrace();
			throw (new RuntimeException(e));
		} catch (NotBoundException e) {
			e.printStackTrace();
			throw (new RuntimeException(e));
		} catch (Throwable e) {
			e.printStackTrace();
			throw (new RuntimeException(e));
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see daxclr.doom.server.IDoomServer#scriptArity(int)
	 */
	public native int scriptArity(int func);

	/*
	 * (non-Javadoc)
	 * 
	 * @see daxclr.doom.server.IDoomServer#scriptClass(int)
	 */
	public native String scriptClass(int func);

	/*
	 * (non-Javadoc)
	 * 
	 * @see daxclr.doom.server.IDoomServer#scriptFullname(int)
	 */
	public native String scriptFullname(int func);

	/*
	 * (non-Javadoc)
	 * 
	 * @see daxclr.doom.server.IDoomServer#scriptName(int)
	 */
	public native String scriptName(int func);

	// final static public Category cat = Category.getInstance(class);
	/*
	 * (non-Javadoc)
	 * 
	 * @see daxclr.doom.server.IDoomServer#scriptNumber(java.lang.String, long,
	 *      java.lang.String)
	 */
	public native int scriptNumber(String typename, long pointer, String method);

	/*
	 * (non-Javadoc)
	 * 
	 * @see daxclr.doom.server.IDoomServer#scriptParameter(int, int)
	 */
	public native String scriptParameterType(int func, int num);

	/*
	 * (non-Javadoc)
	 * 
	 * @see daxclr.doom.server.IDoomServer#scriptParameterName(int, int)
	 */
	public native String scriptParameterName(int func, int num);

	/*
	 * (non-Javadoc)
	 * 
	 * @see daxclr.doom.server.IDoomServer#scriptReturn(int)
	 */
	public native String scriptReturnType(int func);

	public native boolean isMapLoaded();

	/*
	 * (non-Javadoc)
	 * 
	 * @see daxclr.doom.server.IDoomServer#scriptSignature(int)
	 */
	public native String scriptSignature(int func);

	/*
	 * (non-Javadoc)
	 * 
	 * @see daxclr.doom.server.IDoomServer#commandAdd(java.lang.String,
	 *      java.lang.String)
	 */
	public native void commandAdd(String name, String help);

	/*
	 * (non-Javadoc)
	 * 
	 * @see daxclr.doom.server.IDoomServer#entityName(int)
	 */
	public native String entityName(int entnum);

	/*
	 * (non-Javadoc)
	 * 
	 * @see daxclr.doom.server.IDoomServer#threadSpawnArgs(int)
	 */
	public native long threadSpawnArgs(int threadnum);

	/*
	 * (non-Javadoc)
	 * 
	 * @see daxclr.doom.server.IDoomServer#createEntityPointer(java.lang.String,
	 *      java.lang.String, java.lang.String)
	 */
	public native long createEntityPointer(String classname, String entityname,
			String locationname);

	/*
	 * (non-Javadoc)
	 * 
	 * @see daxclr.doom.server.IDoomServer#entityPointer(int)
	 */
	public native long entityPointer(int entnum);

	/*
	 * (non-Javadoc)
	 * 
	 * @see daxclr.doom.server.IDoomServer#entityClass(int)
	 */
	public native String entityClass(int entnum);

	/*
	 * (non-Javadoc)
	 * 
	 * @see daxclr.doom.server.IDoomServer#entityType(int)
	 */
	public native String entityType(int entnum);

	/*
	 * (non-Javadoc)
	 * 
	 * @see daxclr.doom.server.IDoomServer#entitySpawnArgs(int)
	 */
	public native long entitySpawnArgs(int entnum);

	/*
	 * (non-Javadoc)
	 * 
	 * @see daxclr.doom.server.IDoomServer#entityNumber(java.lang.String)
	 */
	public native int entityNumber(String name);

	/*
	 * (non-Javadoc)
	 * 
	 * @see daxclr.doom.server.IDoomServer#invokeEntity(int, java.lang.String,
	 *      java.lang.Serializable[])
	 */
	public native Serializable invokeEntity(int entnum, String cmd,
			Serializable[] args) throws NoSuchMethodException;

	/*
	 * (non-Javadoc)
	 * 
	 * @see daxclr.doom.server.IDoomServer#invokeThread(int, java.lang.String,
	 *      java.lang.Serializable[])
	 */
	public native Serializable invokeThread(int threadnum, String cmd,
			Serializable[] args) throws NoSuchMethodException;

	/*
	 * (non-Javadoc)
	 * 
	 * @see daxclr.doom.server.IDoomServer#createThreadPointer(java.lang.String)
	 */
	public native long createThreadPointer(String classname);

	/*
	 * (non-Javadoc)
	 * 
	 * @see daxclr.doom.server.IDoomServer#threadPointer(int)
	 */
	public native long threadPointer(int threadnum);

	/*
	 * (non-Javadoc)
	 * 
	 * @see daxclr.doom.server.IDoomServer#threadEntity(int)
	 */
	public native int threadEntity(int threadnum);

	/*
	 * (non-Javadoc)
	 * 
	 * @see daxclr.doom.server.IDoomServer#threadName(int)
	 */
	public native String threadName(int threadnum);

	/*
	 * (non-Javadoc)
	 * 
	 * @see daxclr.doom.server.IDoomServer#threadNumber(java.lang.String)
	 */
	public native int threadNumber(String name);

	/*
	 * (non-Javadoc)
	 * 
	 * @see daxclr.doom.server.IDoomServer#threadState(int)
	 */
	public native Serializable threadState(int threadnum);

	/*
	 * (non-Javadoc)
	 * 
	 * @see daxclr.doom.server.IDoomServer#getIdPointer(short, int)
	 */
	public native long getIdPointer(short type, int ordinal);

	/*
	 * (non-Javadoc)
	 * 
	 * @see daxclr.doom.server.IDoomServer#respondsTo(long, java.lang.String)
	 */
	public native boolean respondsTo(long pointer1, String eventname);

	/*
	 * (non-Javadoc)
	 * 
	 * @see daxclr.doom.server.IDoomServer#invokeEvent(long, java.lang.String,
	 *      java.lang.Serializable[])
	 */
	public native Serializable invokeEvent(long pointer, String eventname,
			Serializable[] params) throws NoSuchMethodException, BSFException;

	/*
	 * (non-Javadoc)
	 * 
	 * @see daxclr.doom.server.IDoomServer#deletePointer(long)
	 */
	public native void deletePointer(long pointer);

	/*
	 * (non-Javadoc)
	 * 
	 * @see daxclr.doom.server.IDoomServer#setSpawnArg(long, java.lang.String,
	 *      java.lang.String)
	 */
	public native String setSpawnArg(long pointer, String string, String defualt);

	/*
	 * (non-Javadoc)
	 * 
	 * @see daxclr.doom.server.IDoomServer#removeSpawnArg(long,
	 *      java.lang.String)
	 */
	public native String removeSpawnArg(long pointer, String string);

	/*
	 * (non-Javadoc)
	 * 
	 * @see daxclr.doom.server.IDoomServer#getSpawnArg(long, java.lang.String,
	 *      java.lang.String)
	 */
	public native String getSpawnArg(long pointer, String string,
			String defaultstr);

	/*
	 * (non-Javadoc)
	 * 
	 * @see daxclr.doom.server.IDoomServer#getSpawnKeys(long)
	 */
	public native String[] getSpawnKeys(long pointer);

	/*
	 * (non-Javadoc)
	 * 
	 * @see daxclr.doom.server.IDoomServer#allocateIdDict()
	 */
	public native long allocateIdDict();

	/*
	 * (non-Javadoc)
	 * 
	 * @see daxclr.doom.server.IDoomServer#setGUI(long)
	 */
	public native long setGUI(String name);

	/*
	 * (non-Javadoc)
	 * 
	 * @see daxclr.doom.server.IDoomServer#invokeDoomConsole(java.lang.String)
	 */
	public native Serializable invokeDoomConsole(String params);

	/*
	 * (non-Javadoc)
	 * 
	 * @see daxclr.doom.server.IDoomServer#classSpawnArgs(java.lang.String)
	 */
	public native long classSpawnArgs(String typeDef);

	public native Serializable getLastClickedObject();

	public native Serializable invokeDoomObject(Serializable name, String cmd,
			Serializable[] args);

	public native long resolveVarDef(String s);

	public native IVector getLastClickedXY();

	public native IVector getLastXY();

	public native void setMouseXY(float x, float y);

	public native void setPixel(float x, float y, float r, float g, float b,
			float a);

	public native void setMouseImage(String target);

	public native String getCurrentGUI();

	public native long setCurrentGUI(String name);

	public native long findGUI(String name);

	public native boolean setGUI(long pointer);

	public native int defineEvent(String idclazz, String name,
			String return_plus_params);

	public native int defineFunction(String typeclazz, String name,
			String return_plus_params);

	public native Serializable invokeFunction(int o_function_index,
			Serializable[] args);

	public native Serializable initCompletedCallback(Serializable obj);

	public native int scriptNumber(String fnClass, String fnName);

	/*
	 * protected void finalize() throws Throwable { Throwable t = null; try {
	 * remoteRegistry.unbind("DoomServer"); remoteRegistry = null; } catch
	 * (AccessException e) { e.printStackTrace(); t = e; } catch
	 * (RemoteException e) { e.printStackTrace(); t = e; } catch
	 * (NotBoundException e) { e.printStackTrace(); t = e; } super.finalize();
	 * if (t != null) throw t; }
	 */

	public void println(String msg) {
		try {
			System.out.println(msg);
		} catch (Throwable e) {
		}
	}

	Registry remoteRegistry = null;

	public NativeDoomServer() throws RemoteException {
		super();
		String named = getName();
		try {
			remoteRegistry = java.rmi.registry.LocateRegistry
					.createRegistry(IGameLocal.REGISTRY_PORT_NATIVESERVER);
		} catch (RemoteException e) {
			e.printStackTrace();
		}

		// IDoomServer remoteProxy = (IDoomServer) this;
		/*
		 * Proxy.newProxyInstance( getClass().getClassLoader(), new Class[] {
		 * IDoomServer.class }, this);
		 */
		try {
			System.out.println("init happening");
		} catch (Throwable t) {
		}
		try {
			remoteRegistry.rebind(named, this);
		} catch (AccessException e) {
			e.printStackTrace();
		} catch (RemoteException e) {
			e.printStackTrace();
		} catch (IllegalArgumentException e) {
			e.printStackTrace();
		}
		try {
			NativeDoomServer.interpreter.set(named, this);
			NativeDoomServer.interpreter.set("remoteRegistry", remoteRegistry);
			// interpreter.set("doomProxy", remoteProxy);
		} catch (EvalError e) {
			e.printStackTrace();
		}
		try {
			commandAdd("jtest", "type jtest");
		} catch (Throwable e) {
			e.printStackTrace();
		}
	}

	/*
	 * public Serializable invoke(Serializable proxy, Method method,
	 * Serializable[] args) throws Throwable { Throwable t = null;
	 * System.out.println("" + method); try { Method found =
	 * getClass().getMethod(method.getName(), method.getParameterTypes());
	 * System.out.println("->" + found); try { return found.invoke(this, args); }
	 * catch (IllegalArgumentException e) { t = e; } catch (SecurityException e) {
	 * t = e; } catch (IllegalAccessException e) { t = e; } catch
	 * (InvocationTargetException e) { t = e; } catch (UnsatisfiedLinkError e) {
	 * t = e; } catch (Throwable e) { t = e; } } catch (NoSuchMethodException e) {
	 * t = e; } return t; // throw t; }
	 */
	public void uncaughtException(Thread t, Throwable e) {
		e.printStackTrace();
	}

	public INativeServer getDoomServer() {
		return this;
	}

	public boolean isGameLocalAvailable() {
		try {
			return getGameLocal().isGameLocalAvailable();
		} catch (RemoteException e) {
			return false;
		}
	}

	public native INativeServer initCompletedCallback(IGameLocal obj);

	public void showError(String msg, Throwable error) {
		error.printStackTrace();
		System.out.println(msg);
	}
	
	public IScriptObjectRemote getNameSpace() {
		try {
			return getGameLocal().getNameSpace();
		} catch (Throwable e) {
			e.printStackTrace();
			throw new RuntimeException(e);
		}
	}

	@Override
	public IObjectRepository getRepository() {
		try {
			return getGameLocal().getRepository();
		} catch (RemoteException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			throw new RuntimeException(e);
		} catch (Throwable e) {
			throw new RuntimeException(e);

		}
	}

	public void set(String string, Object module) throws RemoteException {
		getRepository().put(string, module);
	}

	public IEntity spawnEntity(String classname, String string, String location)
			throws RemoteException {
		// TODO Auto-generated method stub
		return null;
	}

}