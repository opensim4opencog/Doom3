package daxclr.doom.modules;

import java.beans.PropertyChangeEvent;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.ObjectStreamException;
import java.io.Serializable;
import java.lang.Thread.UncaughtExceptionHandler;
import java.lang.reflect.Method;
import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.Hashtable;
import java.util.Map;

import javax.naming.NameAlreadyBoundException;

import org.apache.bsf.BSFException;

import bsh.NameSource.Listener;

import daxclr.bsf.ConsoleChannel;
import daxclr.bsf.IScriptMethodHandler;
import daxclr.bsf.IScriptObject;
import daxclr.bsf.IObjectRepository;
import daxclr.bsf.IScriptObjectRemote;
import daxclr.bsf.ObjectRepository;
import daxclr.doom.IGameLocal;

abstract public class RemoteDoomModule implements java.lang.Runnable, IDoomModule,
Remote,IDoomMapListener, IScriptMethodHandler,UncaughtExceptionHandler {

	final static Map<String, IDoomModule> modules = new Hashtable<String, IDoomModule>();

	final static public void debug(Object msg) {
		ConsoleChannel.debug(msg);
	}


	public boolean interrupted() {
		// TODO Auto-generated method stub
		return interrupted;
	}

	private RemoteDoomThread thread;

	public String getName() {
		return name;
	}
	
	public IScriptObject getNameSpace() {
		return getRepository().put(getName(), this);
	}
	
	public static IObjectRepository getRepository() {
		return ObjectRepository.resolverMap;
	}

	public IScriptObjectRemote toRemote() throws RemoteException {
		return getNameSpace().toRemote();
	}

	/**
	 * Method invoked when the given thread terminates due to the given uncaught
	 * exception.
	 * <p>
	 * Any exception thrown by this method will be ignored by the Java Virtual
	 * Machine.
	 * 
	 * @param t
	 *            the thread
	 * @param e
	 *            the exception
	 */
	public void uncaughtException(Thread t, Throwable e) {
		debug(e);
		debug("uncaughtException in thread " + t.getName());
	}

	public class RemoteDoomThread extends Thread {
		/**
		 * @param group
		 * @param target
		 * @param name
		 */
		public RemoteDoomThread(ThreadGroup group, Runnable target, String name) {
			super(group, target, name);
			// TODO Auto-generated constructor stub
		}

		public final ClassLoader getContextClassLoader() {
			return super.getContextClassLoader();
		}

		public UncaughtExceptionHandler getUncaughtExceptionHandler() {
			return RemoteDoomModule.this;
		}

	}

	private boolean interrupted = false;

	private boolean startcalled = false;

	// public boolean interrupted() {
	// return interrupted;
	// }
	// public String getName() {
	// return toString();
	// }
	// public boolean isAlive() {
	// return !interrupted && startcalled;
	// }
	// public void setName(String newName) {
	// resetName(newName);
	// }
	public void resetName(String neNamwe) {

	}

	// public void stop() {
	// interrupted = true;
	// }
	public void start() {
		thread.start();
		startcalled = true;
	}

	public String name;

	public RemoteDoomModule(String modulename) throws NameAlreadyBoundException {
		// super(modulename);
		if (modulename == null) {
			modulename = getClass().getName();
			modulename = modulename.substring(modulename.lastIndexOf('.') + 1);
		}
		name = modulename;
		modules.put(name, this);
		// ScriptManager.set(initialName, this);
		thread = new RemoteDoomThread(Thread.currentThread().getThreadGroup(),
				this, modulename);
	}

	public String toString() {
		return name;
	}

	// abstract public boolean isMapSpecific();

	@SuppressWarnings("deprecation")
	public void removeModule() {
		thread.stop();
	}

	public void initializeModule(IGameLocal shell, Object[] config) throws RemoteException {
		gameLocal = shell;
		shell.addPlugin(name, (IDoomModule) this);
		shell.addCommand(name, this);
		if (!isMapSpecific()) {
			if (!thread.isAlive()) {
				thread.start();
			}
		}
	}

	public void onMapLoad() {
		if (!thread.isAlive()) {
			thread.start();
		}
	}

	@SuppressWarnings("deprecation")
	public void onMapUnload() {
		if (isMapSpecific()) {
			thread.stop();
		}
	}

	/**
	 * The DoomConsole calls thread method each time any command is used
	 * 
	 * @param cmdArgs
	 *            is the String[] with the command used located at cmdArgs[0]
	 * 
	 * @return true if thread AbstractDoomCommand module decides to handle the
	 *         event as thread will cancle other commands from processing it
	 * @throws BSFException
	 */
	public static Serializable eventMissing(Remote target, String cmd,
			Object[] cmdArgs) throws NoSuchMethodException {
		String debuging = "IDoomConsoleListener " + target + " recieved: "
				+ cmd + " " + ConsoleChannel.joinString(cmdArgs, " ");
		NoSuchMethodException nsm = new NoSuchMethodException(debuging);
		// throw new
		// BSFException(BSFException.REASON_INVALID_ARGUMENT,debuging,nsm);
		throw nsm;
	}

	public void addNameSourceListener(Listener listener) {
	}

	public String[] getAllNames() {
		// TODO Auto-generated method stub
		return new String[0];
	}

	/* Return 0L if thread is purely a java class and C++ needs not to delete */
	public long getPointer() {
		// TODO Auto-generated method stub
		return 0;
	}

	public boolean isMapSpecific() {
		return false;
	}

	public void propertyChange(PropertyChangeEvent evt) {
	}

	final public Object invoke(Object proxy, Method method, Object[] args)
			throws Throwable {
		return getGameLocal().getRepository().findOrCreateEntry(proxy).toRemote().invokeMethod(method.getName(),
				args);
	}

	// public void run() { }
	/*
	 * public Serializable invokeRemote(Object target, String cmd, Object[]
	 * cmdArgs) throws RemoteException, NoSuchMethodException, Exception, Error { //
	 * TODO Auto-generated method stub return null; }
	 */

	final private void writeObject(ObjectOutputStream out) throws IOException {
		out.writeObject(getName());
		out.writeObject(getConfigLine());
	}

	IDoomModule stub = null;

	private String theConfigLine;

	public static IGameLocal gameLocal;

	final private void readObject(ObjectInputStream in) throws IOException, ClassNotFoundException {
		name = "" + in.readObject();
		theConfigLine = "" + in.readObject();
		stub = modules.get(name);
		if (stub == null) {
			try {
				modules.put(name, getGameLocal().loadPlugin(theConfigLine));
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}
	
	public static IGameLocal getGameLocal() {
		// TODO Auto-generated method stub
		return gameLocal;
	}


	final public void setConfigLine(String theConfigLine) {
		this.theConfigLine = theConfigLine;
	}
	final public String getConfigLine() {
		return theConfigLine;
	}

	final public Object readResolve() throws ObjectStreamException, RemoteException {
		return modules.get(name);
	}

}
