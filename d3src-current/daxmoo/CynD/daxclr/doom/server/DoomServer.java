package daxclr.doom.server;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.Serializable;
import java.lang.Thread.UncaughtExceptionHandler;
import java.lang.reflect.Method;
import java.rmi.Remote;
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;

import javax.swing.JFrame;

import org.apache.bsf.BSFException;

import bsh.EvalError;
import bsh.Interpreter;

import bsh.util.JConsole;
import daxclr.bsf.IScriptMethodHandler;
import daxclr.bsf.IScriptObject;
import daxclr.bsf.IObjectRepository;
import daxclr.bsf.ScriptingSecurityManager;
import daxclr.doom.IClass;
import daxclr.doom.IEntity;
import daxclr.doom.IGameLocal;
import daxclr.doom.ISys;
import daxclr.doom.IVector;
import daxclr.doom.modules.IDoomModule;

abstract public class DoomServer extends UnicastRemoteObject implements
		IGameLocal, Remote {
	static JConsole jconsole = null;

	static JFrame frame = null;

	static Thread interpreterThread = null;

	static Interpreter interpreter = null;

	static {
		ScriptingSecurityManager.install();
		frame = new JFrame(DoomServer.class.getName());
		jconsole = new JConsole();
		jconsole.setVisible(true);
		jconsole.setSize(400, 300);
		frame.add(jconsole);
		frame.setSize(500, 400);
		frame.setVisible(true);
		System.setOut(jconsole.getOut());
		System.setErr(jconsole.getErr());
		System.setIn(jconsole.getInputStream());
		UncaughtExceptionHandler h = new UncaughtExceptionHandler() {
			public void uncaughtException(Thread t, Throwable e) {
				if (e != null)
					e.printStackTrace();
				System.out.println("" + t + e);
			}

		};
		Thread.setDefaultUncaughtExceptionHandler(h);
		Thread.currentThread().setUncaughtExceptionHandler(h);
		interpreter = new Interpreter(jconsole);
		interpreter.setShowResults(true);
		interpreterThread = new Thread(interpreter, "interpreterThread");
		interpreterThread.start();
	}

	// protected final long serialVersionUID = 1309597423746643820L;

	abstract public INativeServer getDoomServer();

	abstract public IGameLocal getGameLocal();

	abstract public boolean isDoomServerAvailable();

	abstract public boolean isGameLocalAvailable();

	abstract public void showError(String msg, Throwable error);

	protected DoomServer() throws RemoteException {
		super();
		String named = getClass().getName();
		try {
			named = getName();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (Throwable e) {
			e.printStackTrace();
		}
		frame.setTitle(named);
		try {
			interpreter.set(named, this);
		} catch (EvalError e) {
			e.printStackTrace();
		} catch (Throwable e) {
			e.printStackTrace();
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see daxclr.doom.server.IGameLocal#allocateIdDict()
	 */

	// Return stub that impelements the native methods
	// abstract public IRemoteDoomServer getDoomServer();
	// Return stub that impelements the java methods
	// abstract public IRemoteGame getGameLocal() throws;
	// abstract public IRemoteNativeServer getDoomServer() throws
	// RemoteException;
	public long allocateIdDict() {
		try {
			return getDoomServer().allocateIdDict();
		} catch (RemoteException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			throw new RuntimeException(e);
		}
	}

	public Serializable invokeDoomObject(Serializable name, String cmd,
			Serializable[] args) throws NoSuchMethodException, RemoteException {
		return getDoomServer().invokeDoomObject(name, cmd, args);
	}

	public long classSpawnArgs(String typeDef) {
		try {
			return getDoomServer().classSpawnArgs(typeDef);
		} catch (RemoteException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			throw new RuntimeException(e);
		}
	}

	public void commandAdd(String name, String help) {
		try {
			getDoomServer().commandAdd(name, help);
		} catch (RemoteException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			throw new RuntimeException(e);
		}
	}

	public long createEntityPointer(String classname, String entityname,
			String locationname) {
		try {
			return getDoomServer().createEntityPointer(classname, entityname,
					locationname);
		} catch (RemoteException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			throw new RuntimeException(e);
		}
	}

	public long createThreadPointer(String classname) {
		try {
			return getDoomServer().createThreadPointer(classname);
		} catch (RemoteException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			throw new RuntimeException(e);
		}
	}

	public int defineEvent(String idclazz, String name,
			String return_plus_params) {
		try {
			return getDoomServer().defineEvent(idclazz, name,
					return_plus_params);
		} catch (RemoteException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			throw new RuntimeException(e);
		}
	}

	public int defineFunction(String typeclazz, String name,
			String return_plus_params) {
		try {
			return getDoomServer().defineFunction(typeclazz, name,
					return_plus_params);
		} catch (RemoteException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			throw new RuntimeException(e);
		}
	}

	public void deletePointer(long pointer) {
		try {
			getDoomServer().deletePointer(pointer);
		} catch (RemoteException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			throw new RuntimeException(e);
		} catch (Throwable e) {
			throw new RuntimeException(e);
		}
	}

	public String entityClass(int entnum) {
		try {
			return getDoomServer().entityClass(entnum);
		} catch (RemoteException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			throw new RuntimeException(e);
		} catch (Throwable e) {
			throw new RuntimeException(e);

		}
	}

	public String entityName(int entnum) {
		try {
			return getDoomServer().entityName(entnum);
		} catch (RemoteException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			throw new RuntimeException(e);
		} catch (Throwable e) {
			throw new RuntimeException(e);

		}
	}

	public int entityNumber(String name) {
		try {
			return getDoomServer().entityNumber(name);
		} catch (RemoteException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			throw new RuntimeException(e);
		} catch (Throwable e) {
			throw new RuntimeException(e);

		}
	}

	public long entityPointer(int entnum) {
		try {
			return getDoomServer().entityPointer(entnum);
		} catch (RemoteException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			throw new RuntimeException(e);
		} catch (Throwable e) {
			throw new RuntimeException(e);

		}
	}

	public long entitySpawnArgs(int entnum) {
		try {
			return getDoomServer().entitySpawnArgs(entnum);
		} catch (RemoteException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			throw new RuntimeException(e);
		} catch (Throwable e) {
			throw new RuntimeException(e);

		}
	}

	public String entityType(int entnum) {
		try {
			return getDoomServer().entityType(entnum);
		} catch (RemoteException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			throw new RuntimeException(e);
		} catch (Throwable e) {
			throw new RuntimeException(e);

		}
	}

	public long resolveVarDef(String s) {
		try {
			return getDoomServer().resolveVarDef(s);
		} catch (RemoteException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			throw new RuntimeException(e);
		} catch (Throwable e) {
			throw new RuntimeException(e);

		}
	}

	public long findGUI(String name) {
		try {
			return getDoomServer().findGUI(name);
		} catch (RemoteException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			throw new RuntimeException(e);
		} catch (Throwable e) {
			throw new RuntimeException(e);

		}
	}

	public String getBaseDirectory() {
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

	public String getCurrentGUI() {
		try {
			return getDoomServer().getCurrentGUI();
		} catch (RemoteException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			throw new RuntimeException(e);
		} catch (Throwable e) {
			throw new RuntimeException(e);

		}
	}

	public long getIdPointer(short type, int ordinal) {
		try {
			return getDoomServer().getIdPointer(type, ordinal);
		} catch (RemoteException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			throw new RuntimeException(e);
		} catch (Throwable e) {
			throw new RuntimeException(e);

		}
	}

	public Serializable getLastClickedObject() {
		try {
			return getDoomServer().getLastClickedObject();
		} catch (RemoteException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			throw new RuntimeException(e);
		} catch (Throwable e) {
			throw new RuntimeException(e);

		}
	}

	public IVector getLastClickedXY() {
		try {
			return getDoomServer().getLastClickedXY();
		} catch (RemoteException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			throw new RuntimeException(e);
		} catch (Throwable e) {
			throw new RuntimeException(e);

		}
	}

	public IVector getLastXY() {
		try {
			return getDoomServer().getLastXY();
		} catch (RemoteException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			throw new RuntimeException(e);
		} catch (Throwable e) {
			throw new RuntimeException(e);

		}
	}

	public String getSpawnArg(long pointer, String string, String defaultstr) {
		try {
			return getDoomServer().getSpawnArg(pointer, string, defaultstr);
		} catch (RemoteException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			throw new RuntimeException(e);
		} catch (Throwable e) {
			throw new RuntimeException(e);

		}
	}

	public String[] getSpawnKeys(long pointer) {
		try {
			return getDoomServer().getSpawnKeys(pointer);
		} catch (RemoteException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			throw new RuntimeException(e);
		} catch (Throwable e) {
			throw new RuntimeException(e);

		}
	}

	public Serializable invokeDoomConsole(String params) {
		try {
			return getDoomServer().invokeDoomConsole(params);
		} catch (RemoteException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			throw new RuntimeException(e);
		} catch (Throwable e) {
			throw new RuntimeException(e);

		}
	}

	public Serializable invokeEntity(int entnum, String cmd, Serializable[] args)
			throws NoSuchMethodException, RemoteException {
		return getDoomServer().invokeEntity(entnum, cmd, args);
	}

	public Serializable invokeEvent(long pointer, String eventname,
			Serializable[] params) throws NoSuchMethodException, BSFException,
			RemoteException {
		return getDoomServer().invokeEvent(pointer, eventname, params);
	}

	public Serializable invokeFunction(int o_function_index, Serializable[] args) {
		try {
			return getDoomServer().invokeFunction(o_function_index, args);
		} catch (RemoteException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			throw new RuntimeException(e);
		} catch (Throwable e) {
			throw new RuntimeException(e);

		}
	}

	public Serializable invokeThread(int threadnum, String cmd,
			Serializable[] args) throws NoSuchMethodException, RemoteException {
		return getDoomServer().invokeThread(threadnum, cmd, args);
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

	public void println(String msg) {
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

	public String removeSpawnArg(long pointer, String string) {
		try {
			return getDoomServer().removeSpawnArg(pointer, string);
		} catch (RemoteException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			throw new RuntimeException(e);
		} catch (Throwable e) {
			throw new RuntimeException(e);

		}
	}

	public boolean respondsTo(long pointer1, String eventname) {
		try {
			return getDoomServer().respondsTo(pointer1, eventname);
		} catch (RemoteException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			throw new RuntimeException(e);
		} catch (Throwable e) {
			throw new RuntimeException(e);

		}
	}

	public int scriptArity(int func) {
		try {
			return getDoomServer().scriptArity(func);
		} catch (RemoteException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			throw new RuntimeException(e);
		} catch (Throwable e) {
			throw new RuntimeException(e);

		}
	}

	public String scriptClass(int func) {
		try {
			return getDoomServer().scriptClass(func);
		} catch (RemoteException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			throw new RuntimeException(e);
		} catch (Throwable e) {
			throw new RuntimeException(e);

		}
	}

	public String scriptFullname(int func) {
		try {
			return getDoomServer().scriptFullname(func);
		} catch (RemoteException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			throw new RuntimeException(e);
		} catch (Throwable e) {
			throw new RuntimeException(e);

		}
	}

	public String scriptName(int func) {
		try {
			return getDoomServer().scriptName(func);
		} catch (RemoteException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			throw new RuntimeException(e);
		} catch (Throwable e) {
			throw new RuntimeException(e);

		}
	}

	public int scriptNumber(String fnClass, String fnName) {
		try {
			return getDoomServer().scriptNumber(fnClass, fnName);
		} catch (RemoteException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			throw new RuntimeException(e);
		} catch (Throwable e) {
			throw new RuntimeException(e);

		}
	}

	public int scriptNumber(String typename, long pointer, String method) {
		try {
			return getDoomServer().scriptNumber(typename, pointer, method);
		} catch (RemoteException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			throw new RuntimeException(e);
		} catch (Throwable e) {
			throw new RuntimeException(e);

		}
	}

	public String scriptParameterName(int func, int num) {
		try {
			return getDoomServer().scriptParameterName(func, num);
		} catch (RemoteException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			throw new RuntimeException(e);
		} catch (Throwable e) {
			throw new RuntimeException(e);

		}
	}

	public String scriptParameterType(int func, int num) {
		try {
			return getDoomServer().scriptParameterType(func, num);
		} catch (RemoteException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			throw new RuntimeException(e);
		} catch (Throwable e) {
			throw new RuntimeException(e);

		}

	}

	public String scriptReturnType(int func) {
		try {
			return getDoomServer().scriptReturnType(func);
		} catch (RemoteException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			throw new RuntimeException(e);
		} catch (Throwable e) {
			throw new RuntimeException(e);

		}
	}

	public String scriptSignature(int func) {
		try {
			return getDoomServer().scriptSignature(func);
		} catch (RemoteException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			throw new RuntimeException(e);
		} catch (Throwable e) {
			throw new RuntimeException(e);

		}
	}

	public long setCurrentGUI(String name) {
		try {
			return getDoomServer().setCurrentGUI(name);
		} catch (RemoteException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			throw new RuntimeException(e);
		} catch (Throwable e) {
			throw new RuntimeException(e);

		}

	}

	public boolean setGUI(long pointer) {
		try {
			return getDoomServer().setGUI(pointer);
		} catch (RemoteException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			throw new RuntimeException(e);
		} catch (Throwable e) {
			throw new RuntimeException(e);

		}

	}

	public void setMouseImage(String target) {
		try {
			getDoomServer().setMouseImage(target);
		} catch (RemoteException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			throw new RuntimeException(e);
		} catch (Throwable e) {
			throw new RuntimeException(e);

		}
	}

	public void setMouseXY(float x, float y) {
		try {
			getDoomServer().setMouseXY(x, y);
		} catch (RemoteException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			throw new RuntimeException(e);
		} catch (Throwable e) {
			throw new RuntimeException(e);

		}
	}

	public void setPixel(float x, float y, float r, float g, float b, float a) {
		try {
			getDoomServer().setPixel(x, y, r, g, b, a);
		} catch (RemoteException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			throw new RuntimeException(e);
		} catch (Throwable e) {
			throw new RuntimeException(e);

		}
	}

	public String setSpawnArg(long pointer, String string, String string2) {
		try {
			return getDoomServer().setSpawnArg(pointer, string, string2);
		} catch (RemoteException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			throw new RuntimeException(e);
		} catch (Throwable e) {
			throw new RuntimeException(e);

		}
	}

	public int threadEntity(int threadnum) {
		try {
			return getDoomServer().threadEntity(threadnum);
		} catch (RemoteException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			throw new RuntimeException(e);
		} catch (Throwable e) {
			throw new RuntimeException(e);

		}
	}

	public String threadName(int threadnum) {
		try {
			return getDoomServer().threadName(threadnum);
		} catch (RemoteException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			throw new RuntimeException(e);
		} catch (Throwable e) {
			throw new RuntimeException(e);

		}

	}

	public int threadNumber(String name) {
		try {
			return getDoomServer().threadNumber(name);
		} catch (RemoteException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			throw new RuntimeException(e);
		} catch (Throwable e) {
			throw new RuntimeException(e);

		}
	}

	public long threadPointer(int threadnum) {
		try {
			return getDoomServer().threadPointer(threadnum);
		} catch (RemoteException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			throw new RuntimeException(e);
		} catch (Throwable e) {
			throw new RuntimeException(e);

		}

	}

	public long threadSpawnArgs(int threadnum) {
		try {
			return getDoomServer().threadSpawnArgs(threadnum);
		} catch (RemoteException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			throw new RuntimeException(e);
		} catch (Throwable e) {
			throw new RuntimeException(e);

		}
	}

	public Serializable threadState(int threadnum) {
		try {
			return getDoomServer().threadState(threadnum);
		} catch (RemoteException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			throw new RuntimeException(e);
		} catch (Throwable e) {
			throw new RuntimeException(e);

		}

	}

	public IScriptMethodHandler eventMissing(String why)
			throws NoSuchMethodException, RemoteException {
		return getGameLocal().eventMissing(why);

	}

	public boolean isMapSpecific() {
		return false;

	}

	// public void set(String name, Remote obj) {
	// getGameLocal().set(name, obj);
	// }

	public IClass resolveDoomObject(String s) {
		try {
			return getDoomServer().resolveDoomObject(s);
		} catch (RemoteException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			throw new RuntimeException(e);
		} catch (Throwable e) {
			throw new RuntimeException(e);

		}
	}

	public void loadPluginFile(String plugin_filename)
			throws FileNotFoundException, IOException {
		getGameLocal().loadPluginFile(plugin_filename);

	}

	public String toString(Serializable object) {
		try {
			return getGameLocal().toString(object);
		} catch (RemoteException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			throw new RuntimeException(e);
		} catch (Throwable e) {
			throw new RuntimeException(e);

		}
	}

	// return gameLocal;
	public Serializable invokeCommand(final Object[] cmdArgs)
			throws NoSuchMethodException {
		try {
			return toObject(getGameLocal().invokeCommand(cmdArgs));
		} catch (RemoteException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			throw new RuntimeException(e);
		} catch (Throwable e) {
			throw new RuntimeException(e);

		}
	}

	public boolean isJavaFn(String fnClass, String fnString) {
		try {
			return getGameLocal().isJavaFn(fnClass, fnString);
		} catch (RemoteException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			throw new RuntimeException(e);
		} catch (Throwable e) {
			throw new RuntimeException(e);

		}
	}

	public Serializable invokeJavaFn(String fnClass, String fnString,
			IClass entity, ISys thread, Serializable[] params) {
		try {
			return getGameLocal().invokeJavaFn(fnClass, fnString, entity,
					thread, params);
		} catch (RemoteException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			throw new RuntimeException(e);
		} catch (Throwable e) {
			throw new RuntimeException(e);

		}

	}

	public IEntity toEntity(int vals) {
		try {
			return (IEntity) getGameLocal().toEntity(vals);
		} catch (RemoteException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			throw new RuntimeException(e);
		} catch (Throwable e) {
			throw new RuntimeException(e);

		}
	}

	public ISys toThread(int vals) {
		try {
			return getGameLocal().toThread(vals);
		} catch (RemoteException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			throw new RuntimeException(e);
		} catch (Throwable e) {
			throw new RuntimeException(e);

		}
	}

	public IVector toVector(String vals) {
		try {
			return getGameLocal().toVector(vals);
		} catch (RemoteException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			throw new RuntimeException(e);
		} catch (Throwable e) {
			throw new RuntimeException(e);

		}
	}

	public IVector toColor(float r, float g, float b, float a) {
		try {
			return getGameLocal().toColor(r, g, b, a);
		} catch (RemoteException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			throw new RuntimeException(e);
		} catch (Throwable e) {
			throw new RuntimeException(e);

		}
	}

	public IVector toPoint3D(float x, float y, float z) {
		try {
			return getGameLocal().toPoint3D(x, y, z);
		} catch (RemoteException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			throw new RuntimeException(e);
		} catch (Throwable e) {
			throw new RuntimeException(e);

		}
	}

	public IDoomModule loadPlugin(String name) throws Exception {
		try {
			return getGameLocal().loadPlugin(name);
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}

	public boolean startObject(String name) {
		try {
			getGameLocal().startObject(name);
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
		return true;
	}

	public void onMapLoad() {
		try {
			getGameLocal().onMapLoad();
		} catch (RemoteException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			throw new RuntimeException(e);
		} catch (Throwable e) {
			throw new RuntimeException(e);

		}
	}

	public void onMapUnload() {
		try {
			getGameLocal().onMapUnload();
		} catch (RemoteException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			throw new RuntimeException(e);
		} catch (Throwable e) {
			throw new RuntimeException(e);

		}
	}

	public Class classForName(String name) {
		try {
			return getGameLocal().classForName(name);
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
		try {
			return getGameLocal().invokeJavaFn(fnClass, fnString, entity,
					thread, params);
		} catch (RemoteException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			throw new RuntimeException(e);
		} catch (Throwable e) {
			throw new RuntimeException(e);

		}

	}

	public Class toClass(Object object) throws ClassNotFoundException {
		try {
			return getGameLocal().toClass(object);
		} catch (RemoteException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			throw new RuntimeException(e);
		} catch (Throwable e) {
			throw new RuntimeException(e);

		}

	}

	public Serializable toObject(Object result) {
		try {
			return getGameLocal().toObject(result);
		} catch (RemoteException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			throw new RuntimeException(e);
		} catch (Throwable e) {
			throw new RuntimeException(e);

		}

	}

	public String toString(Object object) {
		try {
			return getGameLocal().toString(object);
		} catch (RemoteException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			throw new RuntimeException(e);
		} catch (Throwable e) {
			throw new RuntimeException(e);

		}

	}

	public String toArrayIntValue(Object entnum, int ord) {
		try {
			return getGameLocal().toArrayIntValue(entnum, ord);
		} catch (RemoteException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			throw new RuntimeException(e);
		} catch (Throwable e) {
			throw new RuntimeException(e);

		}
	}

	public void beanAdded(Object obj) {
		try {
			getGameLocal().beanAdded(obj);
		} catch (Throwable e) {
			throw new RuntimeException(e);

		}
	}

	public void beanRemoved(Object obj) {
		try {
			getGameLocal().beanRemoved(obj);
		} catch (Throwable e) {
			throw new RuntimeException(e);
		}
	}

	public float floatArrayValue(Object a, int i) {
		try {
			return getGameLocal().floatArrayValue(a, i);
		} catch (RemoteException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			throw new RuntimeException(e);
		} catch (Throwable e) {
			throw new RuntimeException(e);

		}
	}

	public float floatValue(Object target) {
		try {
			return getGameLocal().floatValue(target);
		} catch (RemoteException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			throw new RuntimeException(e);
		} catch (Throwable e) {
			throw new RuntimeException(e);

		}
	}

	public int intValue(Object target) {
		try {
			return getGameLocal().intValue(target);
		} catch (RemoteException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			throw new RuntimeException(e);
		} catch (Throwable e) {
			throw new RuntimeException(e);

		}
	}

	public boolean isEqual(Object o1, Object o2) {
		try {
			return getGameLocal().isEqual(o1, o2);
		} catch (RemoteException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			throw new RuntimeException(e);
		} catch (Throwable e) {
			throw new RuntimeException(e);

		}
	}

	public Boolean toBoolean(boolean tf) {
		try {
			return getGameLocal().toBoolean(tf);
		} catch (RemoteException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			throw new RuntimeException(e);
		} catch (Throwable e) {
			throw new RuntimeException(e);

		}
	}

	public Float toFloat(float value) {
		try {
			return getGameLocal().toFloat(value);
		} catch (RemoteException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			throw new RuntimeException(e);
		} catch (Throwable e) {
			throw new RuntimeException(e);

		}
	}

	public Integer toInteger(int value) {
		try {
			return getGameLocal().toInteger(value);
		} catch (RemoteException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			throw new RuntimeException(e);
		} catch (Throwable e) {
			throw new RuntimeException(e);

		}
	}

	public Object toNull() {
		try {
			return getGameLocal().toNull();
		} catch (RemoteException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			throw new RuntimeException(e);
		} catch (Throwable e) {
			throw new RuntimeException(e);

		}
	}

	public INativeServer initCompletedCallback(IGameLocal obj) {
		try {
			return getDoomServer().initCompletedCallback(obj);
		} catch (RemoteException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			throw new RuntimeException(e);
		} catch (Throwable e) {
			throw new RuntimeException(e);

		}
	}

	public Serializable eval(String string) {
		// TODO Auto-generated method stub
		try {
			return getGameLocal().eval(string);
		} catch (RemoteException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			throw new RuntimeException(e);
		} catch (Throwable e) {
			throw new RuntimeException(e);

		}
	}

	public void addCommand(String string, IScriptMethodHandler module) {
		try {
			getGameLocal().addCommand(string, module);
		} catch (RemoteException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			throw new RuntimeException(e);
		} catch (Throwable e) {
			throw new RuntimeException(e);

		}

	}

	public void addPlugin(String string, IDoomModule module) {
		try {
			getGameLocal().addPlugin(string, module);
		} catch (RemoteException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			throw new RuntimeException(e);
		} catch (Throwable e) {
			throw new RuntimeException(e);

		}

	}

	public Serializable invokeMethod(String cmd, Object[] cmdArgs)
			throws NoSuchMethodException, BSFException, EvalError {
		try {
			return getGameLocal().invokeMethod(cmd, cmdArgs);
		} catch (Throwable e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			throw new RuntimeException(e);
		}
	}

	/**
	 * @return the scriptInterpreter.getNameSpace();
	 */
	final public IScriptObject getToplevelNameSpace() {
		return getRepository().get("global");
	}

	/**
	 * @return the scriptInterpreter public Interpreter getInterpreter() {
	 *         return getResolverMap().getInterpreter(); }
	 */

	/**
	 * 
	 * @return
	 * @param args
	 */
	final public Serializable[] toObjects(Object[] args) {
		if (args == null)
			return new Serializable[0];
		int len = args.length;
		Serializable[] toReturnObjects = new Serializable[len];
		while (len-- > 0)
			toReturnObjects[len] = getRepository().toObject(args[len]);
		return toReturnObjects;
	}

	public abstract IObjectRepository getRepository();

	public Object invoke(Object proxy, Method method, Object[] args)
			throws NoSuchMethodException, RemoteException, EvalError,
			BSFException {
		try {
			return getRepository().get(proxy).invoke(proxy, method, args);
		} catch (Throwable e) {
			e.printStackTrace();
			throw new RuntimeException(e);
		}
	}
}
