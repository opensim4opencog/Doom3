/**
 * 
 */
package daxclr.doom.server;

import java.awt.Container;
import java.beans.BeanInfo;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.beans.PropertyVetoException;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.ObjectStreamException;
import java.io.Serializable;
import java.lang.Thread.UncaughtExceptionHandler;
import java.lang.reflect.Array;
import java.lang.reflect.Method;
import java.net.MalformedURLException;
import java.rmi.NotBoundException;
import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.StringTokenizer;
import java.util.Vector;

import javax.swing.JDesktopPane;

import org.apache.bsf.BSFDeclaredBean;
import org.apache.bsf.BSFEngine;
import org.apache.bsf.BSFException;
import org.apache.bsf.BSFManager;
import org.apache.bsf.util.CodeBuffer;
import org.apache.bsf.util.ObjInfo;
import org.apache.bsf.util.StringUtils;

import bsh.BshClassManager;
import bsh.EvalError;
import bsh.Interpreter;
import bsh.InterpreterError;
import bsh.NameSource;
import bsh.NameSpace;
import bsh.Primitive;
import bsh.TargetError;
import bsh.NameSource.Listener;

import com.netbreeze.bbowl.BeanBowl;
import com.netbreeze.bbowl.gui.BeanBowlGUI;
import com.netbreeze.swing.BeansContextListener;

import daxclr.bsf.ConsoleChannel;
import daxclr.bsf.DoomEclipse;
import daxclr.bsf.INamedObject;
import daxclr.bsf.IScriptMethodHandler;
import daxclr.bsf.IScriptObject;
import daxclr.bsf.IScriptObjectProxy;
import daxclr.bsf.IObjectRepository;
import daxclr.bsf.IScriptObjectRemote;
import daxclr.bsf.ObjectRepository;
import daxclr.bsf.ScriptingSecurityManager;
import daxclr.doom.IAI;
import daxclr.doom.IClass;
import daxclr.doom.IDoor;
import daxclr.doom.IEntity;
import daxclr.doom.IGameLocal;
import daxclr.doom.ILight;
import daxclr.doom.IMover;
import daxclr.doom.IPlayer;
import daxclr.doom.ISys;
import daxclr.doom.IUserInterface;
import daxclr.doom.IVector;
import daxclr.doom.modules.IDoomMapListener;
import daxclr.doom.modules.IDoomModule;
import daxclr.doom.modules.RunnableDoomModule;
import daxclr.doom.ui.AbstractPanelModule;
import fipaos.platform.ams.GetDescriptionTask;

/**
 * A IdGameLocal is a container of objects and corresponding IObjectInfos, which
 * add the concept of "name" and "selected".
 * <p>
 * 
 * Each object inside the Game has a corresponding IScriptObject. A
 * IScriptObject has a reference to the object it respresents, but given an
 * object the only way to find the corresponding IScriptObject is to use
 * findBroker(Object o)
 * <p>
 * 
 * PropertyChangeListeners can register to find out when the selected bean is
 * changed, in which case the property "selectedBean" will change.
 * <p>
 * 
 * BeansContextListeners can register to find out when beans are added or
 * removed.
 * 
 * 
 * This is the IdGameLocal adapter for IBM's Bean Scripting Famework. It is an
 * implementation of the BSFEngine class, allowing BSF aware applications to use
 * IdGameLocal as a scripting host.
 * <p>
 * 
 * 
 * I believe this implementation is complete (with some hesitation about the the
 * usefullness of the compileXXX() style methods - provided by the base utility
 * class). <p/>
 * 
 * @see IScriptObject
 * @author Administrator
 */
public class IdGameLocal extends DoomServer implements IGameLocal,
		PropertyChangeListener, BSFEngine, IScriptObjectProxy,
		UncaughtExceptionHandler, BeansContextListener {
	/**
	 * 
	 */
	public static final long serialVersionUID = -8766566188860961881L;

	static {
		ScriptingSecurityManager.install();
		System.out
				.println("IdGameLocal installed: ScriptingSecurityManager (to allow RMI)");
	}

	public INativeServer getDoomServer() {
		try {
			return (INativeServer) java.rmi.Naming.lookup("rmi://localhost:"
					+ IGameLocal.REGISTRY_PORT_NATIVESERVER + "/nativeServer");
			/*
			 * // Why what madness is this and should it work? InvocationHandler
			 * handler = Proxy.getInvocationHandler(a); ClassLoader classLoader =
			 * handler.getClass().getClassLoader(); if (classLoader == null)
			 * classLoader = getClass().getClassLoader(); //
			 * ClassLoader.getSystemClassLoader(); Class classDoomServer =
			 * classLoader .loadClass(IDOOMSERVER_CLASSNAME); Class classRemote =
			 * classLoader.loadClass("java.rmi.Remote"); return
			 * (IRemoteNativeServer) Proxy.newProxyInstance(classLoader, new
			 * Class[] { classDoomServer, classRemote }, handler); }
			 */
		} catch (MalformedURLException e) {
			e.printStackTrace();
			throw (new RuntimeException(e));
		} catch (RemoteException e) {
			e.printStackTrace();
			throw (new RuntimeException(e));
		} catch (NotBoundException e) {
			e.printStackTrace();
			DoomEclipse.executeDoomApplication();
			return getDoomServer();
		} catch (Throwable e) {
			throw (new RuntimeException(e));
		}
	}

	public IGameLocal getGameLocal() {
		return this;
	}

	public boolean isGameLocalAvailable() {
		return true;
	}

	// public static final long serialVersionUID = -2068901658715138002L;

	final public Map<String, IdMethod> theIdMethodTable = new Hashtable<String, IdMethod>(
			10);

	final ConsoleChannel consoleChannel = new ConsoleChannel() {
		@Override
		protected void debugLocal(String msg) {
			printLocal("DEBUG: " + msg);
		}

		@Override
		protected void printLocal(String msg) {
			try {
				getDoomServer().println(msg);
			} catch (RemoteException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				throw new RuntimeException(e);
			} catch (Throwable e) {
				throw new RuntimeException(e);

			}
		}

		@Override
		public boolean isLinked() {
			// TODO Auto-generated method stub
			return false;
		}
	};

	/*
	 * (non-Javadoc)
	 * 
	 * @see daxclr.doom.server.IGameLocal#getPointer()
	 */
	public long getPointer() {
		return 0L;
	}

	public/* static */void waitmsecs(int millis) {
		try {
			Thread.sleep(millis);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see daxclr.doom.server.IGameLocal#isMapSpecific()
	 */
	public boolean isMapSpecific() {
		return false;
	}

	/* Dictionary of All objects available to Scripting engine */
	// /*dmiles*/ public DoomIrcBot theBot = null;
	// ===== Object instance variables ================
	// Maps beans to their objectInfos
	// public Map beansToObjectInfos = new HashMap();
	// public List theGameLocal = new LinkedList();
	// /*dmiles*/ public LinkedList theGameLocal = new LinkedList(); // An
	// ordered
	// list of beans
	// /*dmiles*/ public List theGameLocal = new ArrayList(); // An ordered
	// list of
	// beans
	/* Ensures Scripting Engine is running */
	/*
	 * (non-Javadoc)
	 * 
	 * @see daxclr.doom.server.IGameLocal#call(java.lang.Object,
	 *      java.lang.String, java.lang.Object[])
	 */
	public Serializable invokeMethod(String cmd, Object[] cmdArgs)
			throws NoSuchMethodException {
		BSFException bsfException = null;
		// super.call(name, cmd, args);
		if (cmd.equalsIgnoreCase("listtypes")) {
			Class<?> types = IDoomModule.class;
			if (cmdArgs.length > 1)
				try {
					types = Class.forName("" + cmdArgs[1]);
				} catch (ClassNotFoundException e) {
					e.printStackTrace();
				}
			Iterator its = getRepository().valuesOfType(types).iterator();
			while (its.hasNext()) {
				Object plug = its.next();
				String info = "Plugin '" + plug + "' ";
				if (plug instanceof IScriptMethodHandler)
					info += " IDoomCommand";
				if (plug instanceof IDoomMapListener)
					info += " IDoomMapListener";
				if (plug instanceof IDoomModule)
					info += " IDoomModule";
				info += " " + plug.getClass();
				debug(info);
			}
			debug("mapLoaded = " + mapLoaded);
			return toObject(cmd);
		}
		if (cmd.startsWith("bsh")) {
			String evalme = ConsoleChannel.joinString(cmdArgs, " ", 1, 10);
			try {
				debug("bsh: " + evalme);
				debug("" + eval(evalme));
				return toObject(cmd);
			} catch (Throwable e) {
				debug(e);
			}
		}
		/*
		 * If object is null use the interpreter's global scope.
		 */
		Serializable object = eval("global");

		if (object != null && object instanceof bsh.This)
			try {
				final Object value = ((bsh.This) object).invokeMethod(cmd,
						cmdArgs);
				return toObject(Primitive.unwrap(value));
			} catch (final InterpreterError e) {
				bsfException = new BSFException(
						"IdGameLocal interpreter internal error: " + e);
			} catch (final TargetError e2) {
				bsfException = new BSFException(
						"The application script threw an exception: "
								+ e2.getTarget());
			} catch (final EvalError e3) {
				bsfException = new BSFException("IdGameLocal script error: "
						+ e3);
			}
		IScriptObject is = getRepository().findOrCreateEntry(object);
		try {
			return toObject(is.invokeMethod(cmd, cmdArgs));
		} catch (Exception e) {
			// TODO Auto-generated catch block
			bsfException = new BSFException("IScriptObject " + is
					+ " script error: " + e);
		}
		// return is;
		/*
		 * throw new BSFException("Cannot invoke method: " + name + ". Object: " +
		 * object + " is not a IdGameLocal scripted object.");
		 */

		try {
			return getDoomServer().invokeDoomObject(toObject(object), cmd,
					toObjects(cmdArgs));
		} catch (RemoteException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			throw new RuntimeException(e);
		} catch (Throwable e) {
			throw new RuntimeException(e);
		}
		// throw new NoSuchMethodException(""+bsfException);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see daxclr.doom.server.IGameLocal#getAllNames()
	 */
	public String[] getAllNames() {
		return new String[] { "listtypes", "bsh" };
	}

	/* public until refactor */
	public void eventInGame(Object args[]) {
		if (args != null) {
			Object[] cmdArgs = new Object[args.length + 1];
			cmdArgs[0] = "event";
			for (int i = 0; i < args.length; i++)
				cmdArgs[i + 1] = args[i];
			try {
				invokeCommand(cmdArgs);
			} catch (NoSuchMethodException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}

	/* public until refactor */
	public void eventInGameSay(boolean team, String name, String text) {
		// debug("Java heard "+name+" say: "+text);
		eventInGame(new String[] { "say", name, text });
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see daxclr.doom.server.IGameLocal#getTopmostContainer(java.awt.Container)
	 */
	public Container getTopmostContainer(Container child) {
		Container top = child;
		while (child != null) {
			top = child;
			child = child.getParent();
		}
		return top;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see daxclr.doom.server.IGameLocal#run()
	 */
	public void run() {
	}

	public/* static */boolean mapLoaded = false;

	/* static */final Hashtable<String, IUserInterface> uiMap = new Hashtable<String, IUserInterface>(
			10);

	/* public until refactor */
	public void onMapLoad() {
		debug("signalMapLoad()");
		getEntityMap();
		// add(theBeanBowlGUI);
		// set("LinkedHashSet-beanContext.beanList", getPlugins());
		Iterator its = getRepository().valuesOfType(IDoomMapListener.class)
				.iterator();
		while (its.hasNext()) {
			Object target = its.next();
			if (target instanceof IDoomMapListener) {
				IDoomMapListener l = (IDoomMapListener) target;
				try {
					l.onMapLoad();
				} catch (Throwable e) {
					debug(e);
					debug(l.toString());
				}
			} else if (target instanceof Thread || target instanceof Runnable)
				startObject(target);
		}
		mapLoaded = true;
	}

	/* public until refactor */
	public/* static */void onMapUnload() {
		debug("signalMapUnload()");
		Iterator its = getRepository().valuesOfType(IDoomMapListener.class)
				.iterator();
		while (its.hasNext()) {
			Object target = its.next();
			if (target instanceof IDoomMapListener) {
				IDoomMapListener l = (IDoomMapListener) target;
				try {
					l.onMapUnload();
				} catch (Throwable e) {
					debug(e);
				}
			}
		}
		mapLoaded = false;
	}

	/**
	 * 
	 * @param name
	 * @throws Exception
	 */
	/* public until refactor */
	public boolean startObject(String name) {
		if (name == null)
			throw new NullPointerException("startPlugin: plugin name was Null");
		if (name.length() > 2)
			try {
				return startObject(toObject(name));
			} catch (Throwable e) {
				debug(e);
			}
		return false;
	}

	/* public until refactor */
	public boolean startObject(Object plug) {
		boolean isPlugStarted = false;
		String name = "" + plug;
		Thread thread = null;
		if (plug instanceof Thread) {
			thread = (Thread) plug;
			thread.setName(name);
			isPlugStarted = thread.isAlive();
			if (!isPlugStarted) {
				if (!thread.isAlive())
					thread.start();
				debug("starting the plugin: " + name + "");
			} else
				debug("ussing the plugin: " + name + "");
		} else if (plug instanceof Runnable) {
			thread = (Thread) toObject(name + "_thread");
			if (thread == null) {
				thread = new Thread((Runnable) plug);
				thread.setName(name + "_thread");
				set(name + "_thread", thread);
			} else
				isPlugStarted = thread.isAlive();
			if (!isPlugStarted) {
				if (!thread.isAlive())
					thread.start();
				debug("starting the plugin: " + name + "_thread");
			} else
				debug("ussing the plugin: " + name + "_thread");
		} else
			debug("assuming the plugin is already running: " + name);
		return isPlugStarted;
	}

	/* public until refactor */
	public IUserInterface getUserInterface() {
		String target;
		try {
			target = getDoomServer().getCurrentGUI();
		} catch (RemoteException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			throw new RuntimeException(e);
		} catch (Throwable e) {
			throw new RuntimeException(e);

		}
		if (target == null)
			return null;
		return getUserInterface(target);
	}

	/* public until refactor */
	public void setUserInterface(IdUserInterface ui) {
		try {
			getDoomServer().setGUI(ui.getPointer());
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
	 * @param s
	 * @return
	 */
	/* public until refactor */public Object[] tokenizeString(String s) {
		StringTokenizer st = new StringTokenizer(s);
		ArrayList<Object> ss = new ArrayList<Object>(st.countTokens());
		while (st.hasMoreElements())
			ss.add(st.nextElement());
		return (Object[]) ss.toArray();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see daxclr.doom.server.IGameLocal#toEntity(int)
	 */
	/*
	 * (non-Javadoc)
	 * 
	 * @see daxclr.doom.IgetResolverMap()#toEntity(int)
	 */
	public IEntity toEntity(int entnum) {
		try {
			return toEntity(getDoomServer().entityName(entnum));
		} catch (RemoteException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			throw new RuntimeException(e);
		} catch (Throwable e) {
			throw new RuntimeException(e);

		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see daxclr.doom.server.IGameLocal#toEntity(java.lang.String)
	 */
	public IEntity toEntity(String name) {
		return (IEntity) getRepository().findOrCreateEntry(name).coerceTo(
				IEntity.class);
	}

	/**
	 * 
	 * @return
	 * @param named
	 */
	// /*dmiles*/ public Class forName( String named) throws
	// ClassNotFoundException {
	// return theClassManager.getDoomClass(named);
	// }
	/**
	 * Returns the function or event calling parameters as well as the return
	 * type into a string array
	 * 
	 * @param proxy
	 * @param method
	 * @param args
	 * 
	 * @return the function or event calling parameters as well as the return
	 *         type into a string array
	 * @exception Throwable
	 */
	/*
	 * protected Object invoke( Object proxy, Method method, Object[] args)
	 * throws Throwable { // if (proxy==null) throw new
	 * NullPointerException("null object for " + // method); if if (proxy
	 * instanceof IdThread) {
	 * 
	 * return invokeThread(((IdThread)proxy).threadnum,method.getName(),args); }
	 * if (proxy instanceof IdEntity) { return
	 * invokeEntity(((IdEntity)proxy).entityNumber,method.getName(),args); } if
	 * (proxy instanceof IdClass) { return
	 * invokeEvent((IdClass)proxy,method.getName(),args); } return
	 * invokeScript(""+proxy,method.getName(),args); //
	 * return((IScriptObject)proxy).invoke(proxy,method,args); // if
	 * (IScriptObject.class.isAssignableFrom(method.getDeclaringClass())) return
	 * invoke(toObject("" + proxy), method, args); // return
	 * method.invoke(proxy, args); }
	 */
	/*
	 * (non-Javadoc)
	 * 
	 * @see daxclr.doom.server.IGameLocal#resolveDoomObject(java.lang.String)
	 */
	/* public until refactor */
	public IClass resolveDoomObject(String s) {
		// long pointer = evalInDoom(s);
		// ((DoomObjectHandler) o).addInterface(IClass);
		return null;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see daxclr.doom.server.IGameLocal#spawnEntity(java.lang.String,
	 *      java.lang.String, java.lang.String)
	 */
	public IEntity spawnEntity(String classname, String name, String locname) {
		try {
			getDoomServer().invokeThread(0, "named_spawn",
					new Serializable[] { classname, name, locname });
		} catch (RemoteException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			throw new RuntimeException(e);
		} catch (Throwable e) {
			throw new RuntimeException(e);

		}
		return toEntity(name);
	}

	/**
	 * 
	 * @param pname
	 * @return
	 */
	/* public until refactor */
	public String toLowerPropercase(String pname) {
		return pname.substring(0, 1).toLowerCase() + pname.substring(1);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see daxclr.doom.server.IGameLocal#addMDIChild2(java.lang.String,
	 *      java.awt.Container)
	 */
	public RunnableDoomModule addMDIChild2(String name, java.awt.Container cont) {
		AbstractPanelModule panel = new AbstractPanelModule() {
			public final long serialVersionUID = 3027885435367252004L;

			public ClassLoader getContextClassLoader() {
				return getContextClassLoader();
			}

			public void initializeModule(IGameLocal shell, Object[] config) {
			}

			public void removeModule() {
			}

			public void run() {
			}

		};
		cont.setVisible(true);
		// cont.show();
		cont.setSize(400, 300);
		// AbstractPanelModule
		// panel.setContentPane(cont);
		panel.add(cont);
		// panel.setDoubleBuffered(true);
		panel.setVisible(true);
		panel.setSize(500, 400);
		// AbstractPanelModule.setContentOfFrame(objectScriptEditor.getDesk(),panel,cont);
		// panel.show();
		panel.repaint();
		set(name, cont);
		// addPlugin(panel);
		// .add(cont);
		// panel.setContentPane(cont);
		return panel;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see daxclr.doom.server.IGameLocal#addCommand(java.lang.String,
	 *      daxclr.java.IScriptHandler)
	 */
	// /*dmiles*/ public native void setMouseCursor(int x, int y);
	public void addCommand(String cmd, IScriptMethodHandler plug) {
		try {
			addPlugin(plug);
			try {
				getDoomServer().commandAdd(cmd, "From " + plug);
			} catch (RemoteException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (Throwable e) {
				e.printStackTrace();
				throw new RuntimeException(e);
			}
		} catch (UnsatisfiedLinkError e) {
			// ignored
		}
	}

	public void addPlugin(Object plug) {
		// TODO Auto-generated method stub
		// ScriptManager.addNameSource(plug);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see daxclr.doom.server.IGameLocal#eventMissing(java.lang.String)
	 */
	/* public until refactor */
	public IScriptMethodHandler eventMissing(String why)
			throws NoSuchMethodException {
		throw new NoSuchMethodException("eventMissing:" + why);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see daxclr.doom.server.IGameLocal#toString()
	 */
	/*
	 * (non-Javadoc)
	 * 
	 * @see daxclr.doom.IgetResolverMap()#toString()
	 */
	public String getName() {
		return "gameLocal";
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see daxclr.doom.server.IGameLocal#evalJavaCommand(java.lang.Object[])
	 */
	public Serializable invokeCommand(final Object[] cmdArgs)
			throws NoSuchMethodException {
		if (cmdArgs == null || cmdArgs.length < 1)
			throw new NoSuchMethodException("<null>");
		java.util.List<Object> list = Arrays.asList(cmdArgs);
		debug("evalJavaCommand: ->"
				+ ConsoleChannel.joinString(cmdArgs, ",", 0, -1) + "<-");
		final String cmd = "" + list.remove(0);
		(new Thread() {
			NoSuchMethodException nsm = new NoSuchMethodException(cmd);

			public void run() {
				try {
					evalJavaCommandBlocking(cmd, cmdArgs);
					nsm = null;
				} catch (NoSuchMethodException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (Exception ex) {
					// TODO Auto-generated catch block
					ex.printStackTrace();
				}
			}

			public void finalize() throws NoSuchMethodException {
				if (nsm != null)
					throw nsm;
			}
		}).start();
		return toObject(true);
	}

	/* public until refactor */
	public Object evalJavaCommandBlocking(String cmd, Object[] cmdArgs)
			throws NoSuchMethodException, Exception {
		Exception ex = null;
		final List<Object> results = new ArrayList<Object>();
		Iterator its = getRepository().valuesOfType(IScriptObjectProxy.class)
				.iterator();
		while (its.hasNext()) {
			Object object = its.next();
			if (object instanceof IScriptMethodHandler)
				try {
					IScriptMethodHandler plug = (IScriptMethodHandler) object;
					Object lresult = plug.invokeMethod(cmd, cmdArgs);
					if (lresult != null)
						results.add(lresult);
				} catch (NoSuchMethodException e) {
				} catch (Exception e) {
					ex = e;
					debug(e);
				} catch (Error e) {
					ex = new RuntimeException(e);
				} catch (Throwable e) {
					ex = new RuntimeException(e);
				}
		}
		if (results.size() > 0)
			return results.get(0);
		if (ex != null)
			throw ex;
		return eventMissing(cmd);
	}

	public String getBaseDirectory() {
		// TODO Auto-generated method stub
		try {
			return getDoomServer().getBaseDirectory();
		} catch (RemoteException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			throw new RuntimeException(e);
		} catch (Throwable e) {
			throw new RuntimeException(e);

		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see daxclr.doom.server.IGameLocal#addNameSourceListener(bsh.NameSource.Listener)
	 */
	public void addNameSourceListener(Listener listener) {
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see daxclr.doom.IgetResolverMap()#toInteger(int)
	 */
	public Integer toInteger(int value) {
		return new Integer(value);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see daxclr.doom.IgetResolverMap()#toFloat(float)
	 */
	public Float toFloat(float value) {
		return new Float(value);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see daxclr.doom.IgetResolverMap()#toBoolean(boolean)
	 */
	public Boolean toBoolean(boolean tf) {
		return new Boolean(tf);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see daxclr.doom.IgetResolverMap()#toNull()
	 */
	public Object toNull() {
		return null;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see daxclr.doom.IgetResolverMap()#floatArrayValue(java.lang.Object, int)
	 */
	public float floatArrayValue(Object a, int i) {
		if (a instanceof IVector) {
			double d = ((IVector) a).get(i);
			return new Double(d).floatValue();
		}
		if (a instanceof float[])
			return ((float[]) a)[i];
		if (a instanceof double[])
			return new Double(((double[]) a)[i]).floatValue();
		if (a instanceof Object[])
			return floatValue(((Object[]) a)[i]);
		return floatValue(a);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see daxclr.doom.IgetResolverMap()#floatValue(java.lang.Object)
	 */
	public float floatValue(Object target) {
		if (target instanceof Number)
			return ((Number) target).floatValue();
		try {
			return new Float("" + target).floatValue();
		} catch (NumberFormatException e) {
			debug(e);
			return 0.0f;
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see daxclr.doom.IgetResolverMap()#intValue(java.lang.Object)
	 */
	public int intValue(Object target) {
		if (target instanceof Number)
			return ((Number) target).intValue();
		if (target instanceof IVector)
			return ((IVector) target).size();
		if (target instanceof IScriptMethodHandler)
			return ((IScriptObjectProxy) target).hashCode();
		if (target instanceof IScriptObject)
			return ((IScriptObject) target).hashCode();
		try {
			return new Integer("" + target).intValue();
		} catch (NumberFormatException e) {
			return 0;
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see daxclr.doom.server.IGameLocal#isJavaFn(java.lang.String,
	 *      java.lang.String)
	 */
	public boolean isJavaFn(String scope, String fnname) {
		if (scope == null)
			return false;
		if (scope.equals("java"))
			return true;
		if (scope.equals("bsh"))
			return true;
		if (scope.equals("doom"))
			return true;
		try {
			return getDoomServer().scriptNumber(scope, 0, fnname) == 0;
		} catch (RemoteException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			throw new RuntimeException(e);
		} catch (Throwable e) {
			throw new RuntimeException(e);

		}
		/*
		 * int dot = scopeAndFn.indexOf('.'); if (dot<0) return false; String
		 * scope = scopeAndFn.substring(0,dot-1); Object object =
		 * toObject(scope); if (object==null) return false; String fn =
		 * scopeAndFn.substring(dot+1); if (object.getClass().getMethod()) { }
		 */
		// return false;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see daxclr.doom.server.IGameLocal#addPlugin(java.lang.String,
	 *      daxclr.modules.IDoomMapListener)
	 */
	public void addPlugin(String modulename, IDoomMapListener module) {
		// TODO Auto-generated method stub
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see daxclr.doom.server.IGameLocal#addPlugin(java.lang.String,
	 *      daxclr.modules.IDoomModule)
	 */
	public void addPlugin(String modulename, IDoomModule module) {
		// TODO Auto-generated method stub
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see daxclr.doom.server.IGameLocal#addPlugin(java.lang.String,
	 *      java.lang.Runnable)
	 */
	public void addPlugin(String modulename, Runnable module) {
		// TODO Auto-generated method stub
	}

	/**
	 * 
	 * @return
	 */
	final public Map<String, IdMethod> getIdMethodMap() {
		if (theIdMethodTable.size() == 0) {
			for (int i = 1; i < 4096; i++) {
				String name;
				try {
					name = getDoomServer().scriptName(i);
				} catch (RemoteException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
					throw new RuntimeException(e);
				} catch (Throwable e) {
					throw new RuntimeException(e);

				}
				if (name != null) {
					if (!name.equals("<nofunct>")) {
						IdMethod objectInfo;
						try {
							objectInfo = new IdMethod(name, i, getDoomServer()
									.scriptClass(i), getDoomServer()
									.scriptSignature(i), getDoomServer()
									.scriptArity(i));
						} catch (RemoteException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
							throw new RuntimeException(e);
						} catch (Throwable e) {
							throw new RuntimeException(e);

						}
						theIdMethodTable.put(name, objectInfo);
					}
				}
			}
		}
		return theIdMethodTable;
	}

	/**
	 * Returns the Set of entities that are in the game
	 * 
	 */
	final public Collection<IScriptObject> getEntitySet() {
		return getEntityMap().values();
	}

	final public Map<String, IScriptObject> getEntityMap() {
		final Hashtable<String, IScriptObject> map = new Hashtable<String, IScriptObject>(
				0);
		try {
			if (isMapLoaded()) {
				try {
					// isMapLoaded();
					// List set = new ArrayList(30);
					for (int i = 1; i < 24; i++) {
						final String name = getDoomServer().entityName(i);
						if (name != null) {
							if (!name.equals("<noent>")) {
								map.put(name, getRepository()
										.findOrCreateEntry(name));
							}
							// if (target!=null)set.add(target);
						}
					}
				} catch (final Throwable e) {
					debug(e);
				}
			}
		} catch (Throwable e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return map;
	}

	/*
	 * FYI: this also will have installed the DoomSecurityMananger on <clinit>
	 * This may be called more then once since the UnsatisfiedLinkError can
	 * prevent earlier efforts @return true if it was able to (re)establish the
	 * DoomConsole from C++
	 */
	public boolean isLinked() {
		try {
			if (getDoomServer() != null) {
				getDoomServer().println("isLinked()\n");
			}
			return true;
		} catch (final Throwable e) {
			ConsoleChannel
					.printPrevOut("DoomConsoleChannel invoked from java\n");
		}
		return false;
	}

	public void debug(Object error) {
		if (error instanceof Throwable) {
			((Throwable) error).printStackTrace();
		} else {
			System.out.println("" + error);
		}
	}

	public boolean isMapLoaded() {
		try {
			return getDoomServer().isMapLoaded();
		} catch (RemoteException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			throw new RuntimeException(e);
		} catch (Throwable e) {
			throw new RuntimeException(e);

		}
	}

	public Serializable invokeJavaFn(String fnClass, String fnString,
			IClass entity, ISys thread, Object[] params) {

		IScriptObject is = null;
		Class clazz = null;

		if (fnClass != null && (fnClass.length() > 0)) {
			try {
				clazz = toClass(fnClass);
			} catch (ClassNotFoundException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				throw new RuntimeException(e);
			} catch (Throwable e) {
				throw new RuntimeException(e);
			}
			is.importObject(thread);
		}
		if (thread != null) {
			is = getRepository().findOrCreateEntry(thread.getName());
			is.importObject(thread);
		}
		if (entity != null) {
			is = getRepository().findOrCreateEntry(entity.getName());
			is.importObject(entity);
			is.importClass(clazz);
		}
		try {
			return is.invokeMethod(fnString, params);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return null;
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see daxclr.doom.IgetResolverMap()#toVector(java.lang.String)
	 */
	final public IVector toVector(final String v) {
		return new IdVector(v);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see daxclr.doom.IgetResolverMap()#toPoint3D(float, float, float)
	 */
	final public IVector toPoint3D(final float x, final float y, final float z) {
		return new IdVector(x, y, z);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see daxclr.doom.IgetResolverMap()#toColor(float, float, float, float)
	 */
	final public IVector toColor(final float x, final float y, final float z,
			final float a) {
		return new IdVector(x, y, z, a);
	}

	/* static */public IUserInterface getUserInterface(final String name) {
		IUserInterface ui = uiMap.get(name);
		try {
			ui = (IUserInterface) (new IdUserInterface(name)).getNameSpace()
					.coerceTo(IUserInterface.class);
		} catch (final Exception e) {
		}
		uiMap.put(name, ui);
		return ui;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see daxclr.doom.IgetResolverMap()#toThread(int)
	 */
	public ISys toThread(int vals) {
		return (ISys) getThreadMap().get(vals);
	}

	public boolean isDoomServerAvailable() {
		try {
			return getDoomServer().isDoomServerAvailable();
		} catch (Throwable t) {
			t.printStackTrace();
			return false;
		}
	}

	public IdGameLocal() throws RemoteException {
//		ObjectRepository
		// DoomEclipse.bsfManager.setClassLoader(bshClassLoader);
		try {
			getRepository().getInterpreter().eval("import sun.reflect.*;");
			getRepository().getInterpreter().eval("import java.io.*;");
			getRepository().getInterpreter().eval("import java.util.*;");
			getRepository().getInterpreter().eval("import java.lang.*;");
			getRepository().getInterpreter()
					.eval("import java.lang.reflect.*;");
			getRepository().getInterpreter().eval(
					"import daxclr.doom.idclass.*;");
			getRepository().getInterpreter().eval(
					"import daxclr.doom.typedef.*;");
			getRepository().getInterpreter().eval("import daxclr.bfs.*;");
			getRepository().getInterpreter().eval("import daxclr.doom.*;");
			getRepository().getInterpreter().eval("import daxclr.doom.server.*;");
			getRepository().getInterpreter().eval("import daxclr.inference.*;");
			getRepository().getInterpreter().eval(
					"import org.opencyc.cycobject.*;");
			getRepository().getInterpreter().eval(
					"import org.opencyc.cyclobject.*;");
			getRepository().getInterpreter().eval("import org.opencyc.api.*;");
			getRepository().getInterpreter().eval("import jinni.kernel.*;");
			getRepository().getInterpreter().eval("import jinni.core.*;");
			getRepository().getInterpreter().eval("import daxclr.beanbowl.*;");
			getRepository().getInterpreter().eval(
					"import daxclr.beanbowl.swing.*;");
			getRepository().getInterpreter().eval(
					"import daxclr.beanbowl.editors.*;");
			getRepository().getInterpreter().eval("import sun.beans.*;");
			getRepository().getInterpreter().eval("import sun.beans.infos.*;");
			getRepository().getInterpreter()
					.eval("import sun.beans.editors.*;");
			getRepository().getInterpreter().eval("import java.beans.*;");
			getRepository().getInterpreter().eval("import java.beans.infos.*;");
			getRepository().getInterpreter().eval(
					"import java.beans.editors.*;");
		} catch (EvalError e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		getRepository().getInterpreter().setExitOnEOF(false);
		getRepository().getInterpreter().setStrictJava(false);
		addCommand("listtypes", this);
		addCommand("bsh", this);
		addCommand("event", this);
		// interpreter = new Interpreter( new StringReader(""), System.out, //
		// System.err, false, namespace, null, null );
		// console = new
		// JConsole(getInputStream(),getOutputStream());
		/*
		 * cacheClassInfo("D", double.class); cacheClassInfo("F", float.class);
		 * cacheClassInfo("I", int.class); cacheClassInfo("J", long.class);
		 * cacheClassInfo("V", Void.class); cacheClassInfo("Z", boolean.class);
		 * cacheClassInfo("bool", boolean.class); cacheClassInfo("boolean",
		 * int.class); cacheClassInfo("byte", byte.class);
		 * cacheClassInfo("char", char.class); cacheClassInfo("d", int.class);
		 * cacheClassInfo("double", double.class); cacheClassInfo("entity",
		 * IEntity.class); cacheClassInfo("float", float.class);
		 * 
		 * cacheClassInfo("int", int.class); cacheClassInfo("long", long.class);
		 * cacheClassInfo("s", String.class); cacheClassInfo("string",
		 * String.class); cacheClassInfo("vector", IdVector.class);
		 * cacheClassInfo("void", Void.class);
		 * cacheClassInfo("no_type",Void.TYPE);
		 * ScriptContext.cacheClassInfo("idAI", IAI.class);
		 * ScriptContext.cacheClassInfo("idEntity", IEntity.class);
		 * ScriptContext.cacheClassInfo("idPlayer", IPlayer.class);
		 * ScriptContext.cacheClassInfo("idLight", ILight.class);
		 * ScriptContext.cacheClassInfo("idMover", IMover.class);
		 * ScriptContext.cacheClassInfo("idMoveable", IMover.class);
		 * ScriptContext.cacheClassInfo("idPlayerStart", IEntity.class);
		 * ScriptContext.cacheClassInfo("idDoor", IDoor.class);
		 * ScriptContext.cacheClassInfo("idScriptEvent", IdMethod.class);
		 */
		// theBeanInterpreter.DEBUG = true;
		// theBeanInterpreter.TRACE = true;
		// register beanshell with the BSF framework
		// bsfManager.registerBean("theScriptObjectRegistry",
		// scriptObjectRegistry);
		// bsfManager.registerBean("theScriptManager", scriptManager);
		// bsfManager.registerBean("theScriptInterpreter",
		// scriptInterpreter);
		// bsfManager.registerBean("theScriptNameSpace",
		// getNameSpace());
		// bsfManager.registerBean("scriptConsole",
		// scriptConsole);
	}

	/**
	 * Creates a new context linked to the given GUI. All operations will use
	 * either the given GUI or the bean bowl that it represents
	 */
	public transient PropertyChangeSupport pcSupport = new PropertyChangeSupport(
			this);

	/*
	 * public boolean isDoomServerConnected() { // TODO Auto-generated method
	 * stub return false; }
	 * 
	 * public boolean isGameLocalConnected() { // TODO Auto-generated method
	 * stub return false; }
	 * 
	 * public IGameLocal getGameLocal() { // TODO Auto-generated method stub
	 * return null; }
	 * 
	 * public IDoomServer getDoomServer() { // TODO Auto-generated method stub
	 * return null; }
	 */
	public BeanBowlGUI objectScriptEditor = null;

	public void beanAdded(Object obj) {
		try {
			getRepository().beanAdded(obj);
			getBowl().addBean(obj);
		} catch (Throwable e) {
			throw new RuntimeException(e);
		}
	}

	public void beanRemoved(Object obj) {
		try {
			getRepository().beanRemoved(obj);
			getBowl().removeBean(obj);
		} catch (Throwable e) {
			throw new RuntimeException(e);
		}
	}

	/*
	 * public static BSFManager getBSFManager() { return
	 * getResolverMap().bsfManager; }
	 */

	/**
	 * Default impl of compileApply - calls compileExpr ignoring parameters.
	 */
	public void compileApply(String source, int lineNo, int columnNo,
			Object funcBody, Vector paramNames, Vector arguments, CodeBuffer cb)
			throws BSFException {
		compileExpr(source, lineNo, columnNo, funcBody, cb);
	}

	/**
	 * Default impl of compileExpr - generates code that'll create a new
	 * manager, evaluate the expression, and return the value.
	 */
	public void compileExpr(String source, int lineNo, int columnNo,
			Object expr, CodeBuffer cb) throws BSFException {
		ObjInfo bsfInfo = cb.getSymbol("bsf");

		if (bsfInfo == null) {
			bsfInfo = new ObjInfo(BSFManager.class, "bsf");
			cb.addFieldDeclaration("org.apache.bsf.BSFManager bsf = "
					+ "new org.apache.bsf.BSFManager();");
			cb.putSymbol("bsf", bsfInfo);
		}

		String lang = "beanshell";
		String evalString = bsfInfo.objName + ".eval(\"" + lang + "\", ";
		evalString += "request.getRequestURI(), " + lineNo + ", " + columnNo;
		evalString += "," + StringUtils.lineSeparator;
		evalString += StringUtils.getSafeString(expr.toString()) + ")";

		ObjInfo oldRet = cb.getFinalServiceMethodStatement();

		if (oldRet != null && oldRet.isExecutable()) {
			cb.addServiceMethodStatement(oldRet.objName + ";");
		}

		cb
				.setFinalServiceMethodStatement(new ObjInfo(Object.class,
						evalString));

		cb.addServiceMethodException("org.apache.bsf.BSFException");
	}

	/**
	 * Default impl of compileScript - generates code that'll create a new
	 * manager, and execute the script.
	 */
	public void compileScript(String source, int lineNo, int columnNo,
			Object script, CodeBuffer cb) throws BSFException {
		String lang = cb.getClassName();
		ObjInfo bsfInfo = cb.getSymbol("bsf");

		if (bsfInfo == null) {
			bsfInfo = new ObjInfo(BSFManager.class, "bsf");
			cb.addFieldDeclaration("org.apache.bsf.BSFManager bsf = "
					+ "new org.apache.bsf.BSFManager();");
			cb.putSymbol("bsf", bsfInfo);
		}

		String execString = bsfInfo.objName + ".exec(\"" + lang + "\", ";
		execString += "request.getRequestURI(), " + lineNo + ", " + columnNo;
		execString += "," + StringUtils.lineSeparator;
		execString += StringUtils.getSafeString(script.toString()) + ")";

		ObjInfo oldRet = cb.getFinalServiceMethodStatement();

		if (oldRet != null && oldRet.isExecutable()) {
			cb.addServiceMethodStatement(oldRet.objName + ";");
		}

		cb.setFinalServiceMethodStatement(new ObjInfo(void.class, execString));

		cb.addServiceMethodException("org.apache.bsf.BSFException");
	}

	/**
	 * Invoke method name on the specified bsh scripted object. The object may
	 * be null to indicate the global namespace of the interpreter.
	 * 
	 * @param object
	 *            may be null for the global namespace.
	 */
	public Object call(Object object, final String name, final Object[] args)
			throws BSFException {
		try {
			return getRepository().findOrCreateEntry(object).invokeMethod(name,
					args);
		} catch (NoSuchMethodException e) {
			throw new BSFException(BSFException.REASON_UNSUPPORTED_FEATURE, ""
					+ e, e);
		} catch (RemoteException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			throw new BSFException(BSFException.REASON_IO_ERROR, "" + e, e);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			throw new BSFException(BSFException.REASON_INVALID_ARGUMENT,
					"" + e, e);

		} catch (Error e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			throw new BSFException(BSFException.REASON_EXECUTION_ERROR, "" + e,
					e);
		}
	}

	/**
	 * Default impl of execute - calls eval and ignores the result.
	 */
	public void exec(String source, int lineNo, int columnNo, Object script)
			throws BSFException {
		eval(source, lineNo, columnNo, script);
	}

	/**
	 * Default impl of interactive execution - calls eval and ignores the
	 * result.
	 */
	public void iexec(String source, int lineNo, int columnNo, Object script)
			throws BSFException {
		eval(source, lineNo, columnNo, script);
	}

	final BeanBowl beanBowl = new BeanBowl();

	/**
	 * initialize the engine; called right after construction by the manager.
	 * Declared beans are simply kept in a vector and that's it. Subclasses must
	 * do whatever they want with it.
	 */
	public void initialize(final BSFManager mgr1, final String lang1,
			final Vector declaredBeans1) throws BSFException {
		// localClient.initialize(mgr1, lang1,
		// declaredBeans1);
		if (pcSupport == null)
			pcSupport = new PropertyChangeSupport(this);
		// if (scriptContextListeners == null) scriptContextListeners = new
		// LinkedHashSet<IEntrySetListener>();
		// if (objectScriptEditor==null) objectScriptEditor = new
		// ObjectScriptEditor();
		// scriptInterpreter = new Interpreter();
		// declare the bsf manager for callbacks, etc.
	//	set("bsf", mgr1);
		//set("gameLocal", this);
		addListener(this);
		objectScriptEditor = new BeanBowlGUI((BeanBowl) beanBowl);// resolverMap);
		objectScriptEditor.setSize(600, 500);
		objectScriptEditor.setVisible(true);
		// makeDesktop(gameLocal,objectScriptEditor.getDesk());
		objectScriptEditor.repaint();
		// Container top =
		// getTopmostContainer(objectScriptEditor.getDesk());
		// println("top class="+top.getClass().getName()+"
		// named="+top.getName()); //top.getToolkit().getDesktopProperty( }
		for (int i = 0; i < declaredBeans1.size(); i++) {
			final BSFDeclaredBean bean = (BSFDeclaredBean) declaredBeans1
					.get(i);
			declareBean(bean);
		}
		// getUserInterface(null);
		final Iterator it = getRepository().values().iterator();
		while (it.hasNext()) {
			// mgr.declareBean(arg0, arg1, arg2)
			final IScriptObject b = (IScriptObject) it.next();
			b.addPropertyChangeListener(this);
			try {
				mgr1.registerBean(b.getName(), b.getValue());
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				throw new RuntimeException(e);
			} catch (Throwable e) {
				throw new RuntimeException(e);

			}// declareBean(b);
		}
		addBean(this);
		addBean(mgr1);
		addBean(toEntity("cyc_bot_1"));
	}

	public void addListener(Object listener) {
		if (listener instanceof BeansContextListener) {
			getBowl().addListener((BeansContextListener) listener);
		}
		if (listener instanceof PropertyChangeListener) {
			getBowl().addPropertyChangeListener(
					(PropertyChangeListener) listener);
			pcSupport
					.addPropertyChangeListener((PropertyChangeListener) listener);
		}

	}

	public void addBean(Object obj) {
		getBowl().addBean(obj);
		beanAdded(obj);
	}

	public BeanBowl getBowl() {
		return beanBowl;
	}

	/**
	 * Receive property change events from the manager and update my fields as
	 * needed.
	 * 
	 * @param e
	 *            PropertyChange event with the change data This is used for
	 *            IObjectInfos to tell their Game that a property such as "name"
	 *            or "selected" has changed. The Game will update its state as
	 *            necessary.
	 */
	public void propertyChange(PropertyChangeEvent e) {
		// localClient.propertyChange(e);
		pcSupport.firePropertyChange(e);
		Object object = e.getSource();
		String property = e.getPropertyName();
		Object value = e.getNewValue();
	}

	/**
	 */

	public void terminate() {
		// localClient.terminate();
	}

	public void undeclareBean(BSFDeclaredBean bean) throws BSFException {
		getRepository().remove(bean.name);
		getRepository().remove(bean.bean);
	}

	public transient boolean installedApplyMethod = false;

	{
		if (!installedApplyMethod)
			try {
				getRepository()
						.getInterpreter()
						.eval(
								"_bsfApply( _bsfNames, _bsfArgs, _bsfText ) { for(i=0;i<_bsfNames.length;i++) this.namespace.setVariable(_bsfNames[i], _bsfArgs[i],false); return this.interpreter.eval(_bsfText, this.namespace); }");
				// scriptInterpreter.set("doomChannel",RemoteConsoleChannel.ISTREAM);
				// globalInterpreter.eval("doomChannel.init()");
				installedApplyMethod = true;
			} catch (final EvalError e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
	}

	public Serializable toObject(final Object target) {
		return getRepository().toObject(target);
	}

	public void set(final String name, final Object obj) {
		getRepository().put(name, obj);
	}

	public Object unset(final Object key) {
		return getRepository().remove(key);
	}

	BshClassManager getClassManager() {
		return DoomEclipse.bshClassManager;
	}

	public void makeDesktop(final JDesktopPane theDesktopPane) {
		set("objectScriptEditorDesktop", theDesktopPane);
		try {
			getRepository().getInterpreter().eval(
					"beanDesktop(objectScriptEditorDesktop);");
			getRepository()
					.getInterpreter()
					.eval(
							"this.workspace=beanWorkspace(\"Doom Script Console\",scriptInterpreter,scriptConsole);");
			getRepository().getInterpreter().eval(
					"bsh.system.desktop.frame.setVisible(true);");
			getRepository().getInterpreter().eval(
					"bsh.system.desktop.frame.setSize(200,100);");
			getRepository().getInterpreter().eval(
					"bsh.system.desktop.frame.toFront();");
		} catch (EvalError e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		getRepository().getInterpreter().setShowResults(true);
		// interpreter.run();
	}

	public void addNameSource(NameSource plug) {
		// TODO Auto-generated method stub
	}

	// ==== Property getters and setters ==================
	// ==== Instance variables ================
	/*
	 * String script = "foo + bar + bsf.lookupBean(\"gee\")"; Object result =
	 * bsfEngine.eval("Test eval...", -1, -1, script);
	 * 
	 * assertTrue(result.equals("fooStringbarStringgeeString")); // test apply()
	 * final Vector names = new Vector(); names.addElement("name"); final Vector
	 * vals = new Vector(); vals.addElement("Pat");
	 * 
	 * script = "name + name";
	 * 
	 * result = bsfEngine.apply("source string...", -1, -1, script,names, vals);
	 * 
	 * assertTrue(result.equals("PatPat"));
	 * 
	 * result = bsfEngine.eval("Test eval...", -1, -1, "name"); // name should
	 * not be set assertTrue(result == null); // Verify the primitives are
	 * unwrapped result = bsfEngine.eval("Test eval...", -1, -1, "1+1");
	 * 
	 * assertTrue(result instanceof Integer&& ((Integer) result).intValue() ==
	 * 2);
	 * 
	 * void assertTrue(final boolean cond) { if (cond)
	 * System.out.println("Passed..."); else throw new Error("assert
	 * failed..."); }
	 * 
	 */
	// written in lisp,prolog,c++,java and doomscript
	/**
	 * Tells the bowl that it has just been loaded from a file and needs to
	 * initialize itself. For example update transient instance variables that
	 * were "lost" during the serialization, and add itself as listener to all
	 * the beans.
	 */
	public Class classForName(final String name) {
		return getClassManager().classForName(name);
	}

	Hashtable<String, Class> doomClasses = new Hashtable<String, Class>();

	void cacheClassInfo(final String name, final Class<?> value) {
		getClassManager().associatedClasses.put(name, value);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see daxclr.doom.IgetResolverMap()#isEqual(java.lang.Object,
	 *      java.lang.Object)
	 */
	public boolean isEqual(final Object o1, final Object o2) {
		if (o1 == null)
			return o2 == null;
		else
			return o1.equals(o2);
	}

	public String getShortClassName(final Class c) {
		final String name = c.getName();
		final int i = name.lastIndexOf(".");
		if (i == -1)
			return name;
		else
			return name.substring(i + 1);
	}

	/*
	 * public BeanInfo getBeanInfo(final Object c) { //
	 * gameLocal.debug("toObjectInfo " + c); try { return get
	 * toObject(c).getNameSpace().getBeanInfo(); } catch (Throwable e) { throw
	 * new RuntimeException(e); } }
	 */
	/**
	 * @param controller
	 */
	public void addListener(PropertyChangeListener controller) {
	}

	public void removeListener(PropertyChangeListener controller) {
	}

	/**
	 * @return the doomClasses
	 */
	public Hashtable<String, Class> getDoomClasses() {
		return doomClasses;
	}

	public void loadPluginFile(final String filename)
			throws FileNotFoundException, IOException {
		final FileReader filer = new FileReader(filename);
		final BufferedReader br = new BufferedReader(filer);
		new Thread(filename) {
			public void run() {
				int found = 0;
				try {
					debug("loadPlugins from " + filename);
					while (br.ready()) {
						String line = br.readLine();
						found++;
						try {
							loadPlugin(line);
						} catch (Throwable ee) {
							debug(ee);
						}
					}
					br.close();
					debug("lines in cfg = " + found);
				} catch (Throwable e) {
					debug(e);
				}
				// daxclr.java.pluginInitThread = null;
			}
		}.start();
	}

	// public void addPlugin( Object plug) {
	// set(plug.toString(), plug);
	// }
	/**
	 * 
	 * @param line
	 * @throws Exception
	 */
	public IDoomModule loadPlugin(String line) throws Exception {
		IDoomModule module = null;
		if (line == null)
			return module;
		line = line.trim();
		if (line.length() < 5)
			return module;
		if (line.startsWith("/"))
			return module;
		try {
			// CycAPI
			line = line.replace('\t', ' ');
			line = line.replace(" ", " ");
			String[] theConfigLine = line.split(" ");
			String named = theConfigLine[0];
			String classname = theConfigLine[1];
			Object thePlug = module;
			debug("START module " + named + " " + classname);
			try {
				Class newClass = toClass(classname);
				try {
					thePlug = newClass.newInstance();
				} catch (Throwable eee) {
					debug("could not create an instance of " + classname + " ("
							+ named + ")");
					debug(eee);
					return module;
				}
			} catch (Throwable ee) {
				debug("could not find class for an instance of " + classname
						+ " (" + named + ")");
				debug(ee);
				return module;
			}
			if (thePlug == null)
				return module;
			if (thePlug instanceof IDoomModule) {
				module = (IDoomModule) thePlug;
				try {
					module.setConfigLine(line);
					module.initializeModule(this, theConfigLine);
				} catch (Throwable eee) {
					debug(eee);
					debug("ERROR module " + module);
					return module;
				}
			}
			if (isMapLoaded())
				if (thePlug instanceof IDoomMapListener) {
					IDoomMapListener plug = (IDoomMapListener) thePlug;
					try {
						plug.onMapLoad();
					} catch (Throwable eee) {
						debug(eee);
						debug("ERROR module " + plug);
						return null;
					}
				}
			if (thePlug instanceof IScriptMethodHandler) {
				INamedObject plug = (INamedObject) thePlug;
				thePlug = plug;
			}
			// if (thePlug instanceof Thread) addThread((Thread)thePlug);
			set(named, thePlug);
			debug("COMPLETE module " + thePlug);
			return module;
		} catch (Throwable e) {
			debug(e);
		}
		return module;
	}

	// public void debug(Object e) {
	// ConsoleChannel.debug(e);
	// }

	/*
	 * (non-Javadoc)
	 * 
	 * @see daxclr.doom.IgetResolverMap()#toString(java.lang.Object)
	 */
	public String toString(final Object target) {
		return getRepository().toString(target);
	}

	public IObjectRepository load(final File source) throws IOException,
			ClassNotFoundException {
		final FileInputStream fileIn = new FileInputStream(source);
		final ObjectInputStream objectIn = new ObjectInputStream(fileIn);
		final IObjectRepository b = (IObjectRepository) objectIn.readObject();
		fileIn.close();
		return b;
	}

	/**
	 * Listeners will be notifed when the currently bean selection is changed.
	 */
	public void addPropertyChangeListener(final PropertyChangeListener p) {
		pcSupport.addPropertyChangeListener(p);
	}

	/**
	 * This is an implementation of the BSF apply() method. It exectutes the
	 * funcBody text in an "anonymous" method call with arguments.
	 * 
	 * Note: the apply() method may be supported directly in IdGameLocal in an
	 * upcoming release and would not require special support here.
	 */
	public Object apply(final String source, final int lineNo,
			final int columnNo, final Object funcBody, final Vector namesVec,
			final Vector argsVec) throws BSFException {

		BSFException bsfe = null;

		if (namesVec.size() != argsVec.size())
			bsfe = new BSFException("number of params/names mismatch");

		if (!(funcBody instanceof String))
			bsfe = new BSFException("apply: function body must be a string");

		final String[] names = new String[namesVec.size()];
		namesVec.copyInto(names);
		final Object[] args = new Object[argsVec.size()];
		argsVec.copyInto(args);

		try {
			final bsh.This global = (bsh.This) eval("global");
			final Object value = global.invokeMethod("_bsfApply", new Object[] {
					names, args, (String) funcBody });
			return Primitive.unwrap(value);
		} catch (final InterpreterError e) {
			bsfe = new BSFException("IdGameLocal interpreter internal error: "
					+ e + sourceInfo(source, lineNo, columnNo));
		} catch (final TargetError e2) {
			bsfe = new BSFException(
					"The application script threw an exception: "
							+ e2.getTarget()
							+ sourceInfo(source, lineNo, columnNo));
		} catch (final EvalError e3) {
			bsfe = new BSFException("IdGameLocal script error: " + e3
					+ sourceInfo(source, lineNo, columnNo));
		}
		/**
		 * Default impl of apply - calls eval ignoring parameters and returns
		 * the result.
		 */
		try {
			return eval(source, lineNo, columnNo, funcBody);
		} catch (BSFException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		throw bsfe;
	}

	/*
	 * I don't quite understand these compile methods. The default impl will use
	 * the CodeBuffer utility to produce an example (Test) class that turns
	 * around and invokes the BSF Manager to call the script again.
	 * 
	 * I assume a statically compiled language would return a real
	 * implementation class adapter here? But in source code form? Would't it be
	 * more likely to generate bytecode?
	 * 
	 * And shouldn't a non-compiled language simply return a standard
	 * precompiled adapter to itself? The indirection of building a source class
	 * to call the scripting engine (possibly through the interpreter) seems
	 * kind of silly.
	 */
	/*
	 * public void compileApply (String source, int lineNo, int columnNo, Object
	 * funcBody, Vector paramNames, Vector arguments, CodeBuffer cb) throws
	 * BSFException;
	 * 
	 * public void compileExpr (String source, int lineNo, int columnNo, Object
	 * expr, CodeBuffer cb) throws BSFException;
	 * 
	 * public void compileScript (String source, int lineNo, int columnNo,
	 * Object script, CodeBuffer cb) throws BSFException;
	 */
	public void declareBean(final BSFDeclaredBean bean) throws BSFException {
		set(bean.name, bean.bean);
	}

	public Object eval(final String source, final int lineNo,
			final int columnNo, final Object expr) throws BSFException {
		if (!(expr instanceof String))
			throw new BSFException("IdGameLocal expression must be a string");
		try {
			return eval(((String) expr));
		} catch (final Throwable e) {
			throw new BSFException("IdGameLocal interpreter internal error: "
					+ e + sourceInfo(source, lineNo, columnNo));
		}
	}

	// ===== Object instance variables ================
	// ============ Constructors
	// ==================================================
	// ==== Manipulating the collection of beans ==================
	/**
	 * Creates a new object of the given class and adds to this bowl. The given
	 * class must have an empty constructor.
	 * 
	 * @throws InstantiationException
	 *             if the given Class represents an abstract class, an
	 *             interface, an array class, a primitive type, or void; or if
	 *             the instantiation fails for some other reason
	 * @throws IllegalAccessException
	 *             if the given class or initializer is not accessible.
	 * 
	 * @returns the newly created IScriptObject
	 */
	public/* dmiles synchronized */Object newObject(final Class cl)
			throws InstantiationException, IllegalAccessException {
		// Create the object
		final Object obj = cl.newInstance();
		// Add it
		getRepository().findOrCreateEntry(obj);
		return obj;
	}

	public void setDebug(final boolean debug) {
		Interpreter.DEBUG = debug;
	}

	/**
	 * Listeners will be notifed when the currently bean selection is changed.
	 */
	public void removePropertyChangeListener(final PropertyChangeListener p) {
		pcSupport.removePropertyChangeListener(p);
	}

	// ====== Save and load operations ================================
	public void save(final File destination) throws IOException {
		System.out.println("Saving bowl to " + destination);
		final FileOutputStream fileOut = new FileOutputStream(destination);
		final ObjectOutputStream objectOut = new ObjectOutputStream(fileOut);
		objectOut.writeObject(getRepository());
		fileOut.close();
		System.out.println("Successfully saved!");
	}

	// ===== Property notifications (i.e. others notifying me) =========
	/**
	 * This is used for IObjectInfos to tell their Game that a property such as
	 * "name" or "selected" is about to change, allowing the Game to fire a
	 * PropertyVetoException to stop the change if it likes.
	 * <p>
	 * 
	 * This would happen, for example, if someone is trying to rename a bean to
	 * a name that another bean within this bowl already has.
	 */
	public void vetoableChange(final PropertyChangeEvent evt)
			throws PropertyVetoException {

		if (evt.getPropertyName().equals("name")) {
			// The name of a broker has changed. Make sure there are no name
			// collisions

			final IScriptObject broker = getRepository().get(evt.getSource());

			if (broker != null) {

				final String name = (String) evt.getNewValue();

				final IScriptObject otherBroker = getRepository().get(name);

				if (otherBroker != null && otherBroker != broker)

					throw new PropertyVetoException(
							"Another bean already has the name '" + name + "'",

							evt);
			}
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see daxclr.doom.IgetResolverMap()#showError(java.lang.String,
	 *      java.lang.Throwable)
	 */
	public void showError(final String msg, final Throwable error) {
		debug(error);
		debug(msg);
		// BeanBowlGUI.showError(msg, error);
	}

	public String sourceInfo(final String source, final int lineNo,
			final int columnNo) {
		return " BSF info: " + source + " at line: " + lineNo
				+ " column: columnNo";
	}

	public Serializable eval(String target) {
		if (target == null)
			return null;
		if (target.equals(""))
			return null;
		IScriptObject scriptObject = getRepository().get(target);
		if (scriptObject != null)
			return scriptObject.getValue();
		char a1 = target.charAt(0);
		switch (a1) {
		case '"':
		case '\'':
			return toObject(target.substring(1, target.length() - 2));
		case 'n':
			if (target.equals("null"))
				return null;
			break;
		case '$':
			if (target.equals("$null"))
				return null;
			return toObject(resolveDoomObject(target));
		default:
			break;
		}
		String[] oo = target.split(" ");
		try {
			if (oo.length == 1) {
				if (target.indexOf('.') >= 0)
					return toObject(new Double(oo[0]));
				else
					return toObject(new Integer(oo[0]));
			} else
				return new IdVector(target);
		} catch (NumberFormatException ex) {
		}
		try {
			target = target.trim();
			final int len = target.length();
			if (len > 0) {
				final char c = target.charAt(len - 1);
				if (c != ';' && c != '}')
					target = target + ";";
				return toObject(getRepository().getInterpreter().eval(target));
			}
		} catch (final Throwable ee) {
			ee.printStackTrace();
			throw new RuntimeException(ee);
		}
		return null;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see daxclr.doom.IgetResolverMap()#toClass(java.lang.Object)
	 */
	public Class toClass(Object object) throws ClassNotFoundException {
		return getRepository().toClass(object);
	}

	public void uncaughtException(Thread t, Throwable e) {
		debug(e);
		debug("" + t);
	}

	public ClassLoader getContextClassLoader() {
		return Thread.currentThread().getContextClassLoader();
	}

	public String getPathToClasses() {
		// TODO Auto-generated method stub
		try {
			return getDoomServer().getBaseDirectory() + "classlib\\";
		} catch (RemoteException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			throw new RuntimeException(e);
		} catch (Throwable e) {
			throw new RuntimeException(e);

		}
	}

	public String removeChars(String originalName, String chars) {
		return replaceChars(originalName, chars, "");
	}

	public String replaceChars(String originalName, String chars, String repl) {
		for (int i = 0; i < chars.length(); i++)
			originalName = originalName.replace("" + chars.charAt(i), repl);
		return originalName;
	}

	public/* static */String unqualifiedClassname(Class type) {
		if (type.isArray()) {
			return unqualifiedClassname(type.getComponentType()) + "[]";
		}
		String objectName = type.getName();
		return objectName.substring(objectName.lastIndexOf('.') + 1);
	}

	public void addDoomClass(String name, Class value) {
		cacheClassInfo(name, value);
		doomClasses.put(name, value);
	}

	public void addDoomClasses() {
		addDoomClass("D", double.class);
		addDoomClass("F", float.class);
		addDoomClass("I", int.class);
		addDoomClass("J", long.class);
		addDoomClass("V", Void.class);
		addDoomClass("Z", boolean.class);
		addDoomClass("bool", boolean.class);
		addDoomClass("boolean", int.class);
		addDoomClass("byte", byte.class);
		addDoomClass("char", char.class);
		addDoomClass("d", int.class);
		addDoomClass("double", double.class);
		addDoomClass("entity", IEntity.class);
		addDoomClass("float", float.class);
		addDoomClass("idAI", IAI.class);
		addDoomClass("idEntity", IEntity.class);
		addDoomClass("idPlayer", IPlayer.class);
		addDoomClass("idLight", ILight.class);
		addDoomClass("idMover", IMover.class);
		addDoomClass("idMoveable", IMover.class);
		addDoomClass("idPlayerStart", IEntity.class);
		addDoomClass("idDoor", IDoor.class);
		addDoomClass("int", int.class);
		addDoomClass("long", long.class);
		addDoomClass("s", String.class);
		addDoomClass("string", String.class);
		addDoomClass("vector", IdVector.class);
		addDoomClass("void", Void.class);
		addDoomClass("no_type", Void.TYPE);

	}

	/**
	 * 
	 * @return
	 */
	// public Map getClassTable() {
	// if (absoluteClassCache.get("idScriptEvent")==null) addDoomClasses();
	// return absoluteClassCache;
	// }
	public Class getDoomClass(String name) throws ClassNotFoundException {
		boolean isArray = false;
		if (name.endsWith("[]")) {
			name = name.substring(0, name.length() - 2);
			isArray = true;
		} else if (name.charAt(0) == '[') {
			name = name.substring(1);
			isArray = true;
		}
		if (name.startsWith("L") && name.contains("/")) {
			name = name.substring(1);
			name.replace(";", "");
			name.replace("/", ".");
		}

		Class clazz = doomClasses.get(name);
		if (clazz != null)
			return clazz;
		clazz = classForName(name);
		if (clazz == null)
			throw new ClassCastException(name);
		// add(clazz);
		if (isArray) {
			clazz = Array.newInstance(clazz, 0).getClass();
		}
		return clazz;
	}

	final public Map<String, IScriptObject> getThreadMap() {
		final Hashtable<String, IScriptObject> map = new Hashtable<String, IScriptObject>(
				0);
		try {
			if (isMapLoaded())
				try {
					// isMapLoaded();
					// List set = new ArrayList(30);
					for (int i = 1; i < 24; i++) {
						final String name = threadName(i);
						if (name != null)
							if (!name.equals("<noent>"))
								map.put(name, new IdThread(name, i)
										.getNameSpace());
					}
				} catch (final Throwable e) {
					IdThread.debug(e);
				}

		} catch (Throwable e) {
			throw new RuntimeException(e);

		}
		return map;
	}

	public IObjectRepository getRepository() {
		return ObjectRepository.resolverMap;
	}

	public Object readResolve() throws ObjectStreamException, RemoteException {
		return this;
	}

	public IScriptObject getNameSpace() {
		return getRepository().put(getName(), this);
	}

	public IScriptObjectRemote toRemote() throws RemoteException {
		return getNameSpace().toRemote();
	}
}
