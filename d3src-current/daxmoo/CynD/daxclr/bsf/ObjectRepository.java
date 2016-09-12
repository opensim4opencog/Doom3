/**
 * 
 */
package daxclr.bsf;

import java.io.Serializable;
import java.lang.reflect.InvocationTargetException;
import java.net.MalformedURLException;
import java.rmi.AccessException;
import java.rmi.AlreadyBoundException;
import java.rmi.NotBoundException;
import java.rmi.Remote;
import java.rmi.RemoteException;
import java.rmi.registry.Registry;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import org.apache.bsf.BSFDeclaredBean;
import org.apache.bsf.BSFEngine;
import org.apache.bsf.BSFException;
import org.apache.bsf.BSFManager;
import org.apache.bsf.util.ObjectRegistry;
import org.opencyc.cycobject.CycConstant;
import org.opencyc.cycobject.CycList;
import org.opencyc.cycobject.CycNart;
import org.opencyc.cycobject.CycSymbol;

import bsh.EvalError;
import bsh.Interpreter;
import bsh.InterpreterError;
import bsh.NameSource;
import bsh.Primitive;

import com.netbreeze.swing.BeansContextListener;

import daxclr.doom.IClass;
import daxclr.doom.IGameLocal;
import daxclr.doom.IVector;

public class ObjectRepository extends ObjectRegistry implements Remote,
		IObjectRepository, NameSource, BeansContextListener {
	private final static Interpreter scriptingInterpreter = new Interpreter();

	static Registry registry;
	static {
		try {
			registry = java.rmi.registry.LocateRegistry
					.createRegistry(daxclr.doom.IGameLocal.REGISTRY_PORT_GAMELOCAL);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	private final static BSFManager bsfManager00 = new BSFManager();

	private static Object gameLocal11;

	public final static IObjectRepository resolverMap = new ObjectRepository(
			bsfManager00.getObjectRegistry());

	static {
		BSFManager.registerScriptingEngine("beanshell",
				"daxclr.doom.server.IdGameLocal", new String[] { "bsh", "moo",
						"doo" });

		BSFManager.registerScriptingEngine("ruby",
				"org.jruby.javasupport.bsf.JRubyEngine", new String[] { "rb" });

		resolverMap.importExisting();
		resolverMap.put("bsf", bsfManager00);
		gameLocal11 = loadScriptingEngine("beanshell");

		try {
			// resolverMap.put("gameLocal",o);
			resolverMap.put("xslt", loadScriptingEngine("xslt"));
			resolverMap.put("jython", ObjectRepository
					.loadScriptingEngine("jython"));
			resolverMap.put("jacl", ObjectRepository
					.loadScriptingEngine("jacl"));
			// set("ruby", bsfManager.loadScriptingEngine("ruby"));
			// bsfManager.loadScriptingEngine("rexx");
		} catch (Throwable e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		try {
			// resolverMap.getDoomServer().println("Java thinks it's ready to
			// play?");
		} catch (Throwable e) {
			throw new RuntimeException(e);

		}

	}

	private static Process runningDoom = null;

	public static void main(String[] args) {
		DoomEclipse.main(args);
	}

	/*
	 * public static IScriptObject findBroker(final Object object) { return
	 * getGameServer().findBroker(object); }
	 * 
	 * public static Object unset(final Object key) { return
	 * getGameServer().unset(key); }
	 */

	/**
	 * Returns the objectInfo corresponding to the given object, i.e the
	 * IObjectInfo who's object corresponds to the given one. Returns null if
	 * the bean bowl does not contain the given object.
	 */
	public final Serializable toObject(final Object object) {
		try {
			return findOrCreateEntry(object).getValue();
		} catch (Throwable e) {
			e.printStackTrace();
			throw new RuntimeException(e);
		}
	}

	/**
	 * 
	 * 
	 * @return
	 * @param object
	 * @throws ClassNotFoundException
	 */
	public final Class toClass(final Object object)
			throws ClassNotFoundException {
		if (object == null)
			return Object.class;
		if (object instanceof Class)
			return (Class) object;
		try {
			if (object instanceof CharSequence)
				return Thread.currentThread().getContextClassLoader()
						.loadClass(object.toString());
		} catch (final NoClassDefFoundError cnf) {
			System.out.println(cnf);
		} catch (final ClassNotFoundException cnf) {
			System.out.println(cnf);
			return toClass(null);
		}
		return object.getClass();
	}

	public static IGameLocal getGameLocal() {
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

	public static Serializable eval(String commands) {
		try {
			return getGameLocal().eval(commands);
		} catch (Throwable e) {
			throw new RuntimeException(e);
		}
	}

	public static boolean isLinked() {
		return gameLocal11 != null;
	}

	public static void setLocalClient(IGameLocal local) {
		gameLocal11 = local;
		set("gameLocal", local);
	}

	public static Serializable[] toObjects(Object[] args) {
		try {
			return getGameLocal().toObjects(args);
		} catch (RemoteException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			throw new RuntimeException(e);
		} catch (Throwable e) {
			throw new RuntimeException(e);

		}
	}

	/**
	 * @param runningDoom
	 *            the runningDoom to set
	 */
	public static void setRunningDoom(Process runningDoom) {
		ObjectRepository.runningDoom = runningDoom;
	}

	/**
	 * @return the runningDoom
	 */
	public static Process getDoomProcess() {
		return runningDoom;
	}

	public static boolean isRunningDoom() {
		try {
			return (runningDoom != null || (gameLocal11 != null && getGameLocal()
					.isDoomServerAvailable()));
		} catch (Throwable e) {
			e.printStackTrace();
			return false;
		}
	}

	/**
	 * @return the resolverMap
	 */
	public static IObjectRepository getResolverMap() {
		return resolverMap;
	}

	/**
	 * @return the scriptInterpreter
	 */
	public Interpreter getInterpreter() {
		return scriptingInterpreter;
	}

	public static BSFEngine loadScriptingEngine(String lang) {
		try {
			BSFEngine engine = bsfManager00.loadScriptingEngine(lang);
			set(lang, engine);
			return engine;
		} catch (BSFException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			throw new RuntimeException(e);
		} catch (Throwable e) {
			throw new RuntimeException(e);
		}
	}

	ObjectRegistry parent;

	public ObjectRepository(ObjectRegistry parent2) {
		super(parent2);
		this.parent = parent2;
		bsfManager00.setObjectRegistry(this);
	}

	@Override
	public IScriptObject lookup(String key) throws IllegalArgumentException {
		return get(key);
	}

	@Override
	public void register(String key, Object value) {
		set(key, value);
	}

	/*
	 * IScriptObject scriptObject = resolverMap.getResolverMap()
	 * .findOrCreateEntry(name); scriptObject.setValue(obj);
	 * scriptObject.addPropertyChangeListener(gameLocal);
	 * resolverMap.beanAdded(scriptObject); if (scriptObject instanceof Remote)
	 * try { resolverMap.rebind(name, (Remote) scriptObject); } catch
	 * (AccessException e) { // TODO Auto-generated catch block
	 * e.printStackTrace(); } catch (RemoteException e) { // TODO Auto-generated
	 * catch block e.printStackTrace(); } try { getInterpreter().set(name, obj); }
	 * catch (EvalError e) { // TODO Auto-generated catch block
	 * e.printStackTrace(); }
	 */
	@Override
	public void unregister(String arg0) {
		this.remove(arg0);
	}

	public final HashMap<String, IScriptObject> nameToBroker = new HashMap<String, IScriptObject>();

	public final Set<IScriptObject> brokerSet = new HashSet<IScriptObject>();

	public void beanAdded(Object obj) {
		set(obj, obj);
	}

	public void beanRemoved(Object obj) {
		// TODO Auto-generated method stub
	}

	public void entryAdded(java.util.Map.Entry obj) {
		put(obj.getKey(), obj.getValue());
	}

	public void entryRemoved(java.util.Map.Entry obj) {
		remove(obj.getKey());
	}

	public void addNameSourceListener(Listener listener) {
		// TODO Auto-generated method stub
	}

	public String[] getAllNames() {
		return nameToBroker.keySet().toArray(new String[0]);
	}

	public void importExisting() {
		try {
			createEntry("global").importObject(
					scriptingInterpreter.eval("global"));
		} catch (EvalError e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
			throw new RuntimeException(e1);
		} catch (Throwable e1) {
			throw new RuntimeException(e1);

		}
		importExisting(scriptingInterpreter.getNameSpace().getVariableNames());
		try {
			importExisting(registry.list());
		} catch (AccessException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (RemoteException e) {
			e.printStackTrace();
		} catch (Throwable e) {
			throw new RuntimeException(e);
		}
	}

	private void importExisting(String[] variableNames) {
		if (variableNames == null)
			return;
		for (int i = 0; i < variableNames.length; i++)
			importExisting(variableNames[i]);
	}

	private void importExisting(String name) {
		findOrCreateEntry(name);
	}

	public IScriptObject findOrCreateEntry(Object object) {
		if (object == null)
			return null;
		IScriptObject scriptObject = get(object);
		if (scriptObject != null)
			return scriptObject;
		String name = toString(object);
		scriptObject = createEntry(name);
		scriptObject.importObject(object);
		return scriptObject;
	}

	private IScriptObject createEntry(String name) {
		try {
			IScriptObject scriptObject = new ScriptObject(scriptingInterpreter
					.getNameSpace(), name);
			nameToBroker.put(name, scriptObject);
			brokerSet.add(scriptObject);
			scriptObject.importClass(IScriptObject.class);
			parent.register(name, scriptObject);
			registry.rebind(name, scriptObject.toRemote());
			scriptingInterpreter.set(name, scriptObject.getValue());
			return scriptObject;
		} catch (Throwable e) {
			throw new RuntimeException(e);
		}
	}

	public Object resolveObject(Object[] path, Object[] specs, boolean localOnly) {
		// TODO Auto-generated method stub
		return null;
	}

	public void clear() {
		brokerSet.clear();
		nameToBroker.clear();
	}

	public boolean containsKey(Object key) {
		return get(key) != null;
	}

	public boolean containsValue(Object value) {
		return get(value) != null;
	}

	public IScriptObject get(Object key) {
		// CharSequence
		String name = null;
		if (key instanceof INamedObject)
			try {
				name = ((INamedObject) key).getName();
			} catch (RemoteException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
				throw new RuntimeException(e1);
			} catch (Throwable e1) {
				throw new RuntimeException(e1);

			}
		if (key instanceof String) {
			name = (String) key;
		}
		if (name == null) {
			name = toString(key);
		}
		Object superValue = null;
		try {
			superValue = super.lookup(name);
		} catch (IllegalArgumentException e) {
			System.out.println("" + e);
		}

		Remote registryValue = null;
		try {
			registryValue = registry.lookup(name);
		} catch (NotBoundException e) {
			System.out.println("" + e);
		} catch (AccessException e) {
			e.printStackTrace();
		} catch (RemoteException e) {
			e.printStackTrace();
		} catch (Throwable e) {
			throw new RuntimeException(e);
		}

		Object nameSpaceValue = null;
		try {
			nameSpaceValue = scriptingInterpreter.eval(name);
			if (nameSpaceValue instanceof Primitive) {
				nameSpaceValue = ((Primitive) nameSpaceValue).getValue();
			}
		} catch (EvalError e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (InterpreterError e) {
			// if ( nameSpaceValue == Special.VOID_TYPE ) throw new
			// InterpreterError("attempt to unwrap void type");
			nameSpaceValue = null;
		} catch (Throwable e) {
		}
		IScriptObject nameToBrokerValue = nameToBroker.get(name);
		IScriptObject brokerSetValue = null;

		Iterator<IScriptObject> its = brokerSet.iterator();
		while (its.hasNext()) {
			IScriptObject nxt = its.next();
			if (nxt.valueEquals(key))
				brokerSetValue = nxt;
		}
		if (brokerSetValue == null) {
			if (nameToBrokerValue != null) {
				brokerSetValue = nameToBrokerValue;
				brokerSet.add(brokerSetValue);
			} else {
				if (registryValue == null && superValue == null
						&& nameSpaceValue == null)
					return null;
				brokerSetValue = createEntry(name);
			}
		}
		brokerSetValue.importObject(registryValue);
		brokerSetValue.importObject(superValue);
		brokerSetValue.importObject(nameToBrokerValue);
		if (nameToBrokerValue != null)
			nameToBrokerValue.importObject(brokerSetValue);
		return brokerSetValue;
	}

	public boolean isEmpty() {
		return size() == 0;
	}

	public void putAll(Map<? extends Object, ? extends IScriptObject> t) {
		// TODO Auto-generated method stub
	}

	public IScriptObject remove(Object key) {
		// TODO Auto-generated method stub
		return null;
	}

	public Set<Object> keySet() {
		Set<Object> ret = new HashSet<Object>();
		Iterator<IScriptObject> its = brokerSet.iterator();
		while (its.hasNext()) {
			IScriptObject nxt = its.next();
			ret.add(nxt);
		}
		return ret;
	}

	public IScriptObject put(Object key, Object object) {
		return set(key, object);
	}

	public IScriptObject put(String key, Object object) {
		return set(key, object);
	}

	public int size() {
		return brokerSet.size();
	}

	public Collection<IScriptObject> values() {
		return brokerSet;
	}

	public IScriptObject put(Object key, IScriptObject value) {
		return set(key, value);
	}

	static IScriptObject set(Object key, Object value) {
		if (value instanceof BSFDeclaredBean)
			return set(key, ((BSFDeclaredBean) value).bean);
		IScriptObject old = resolverMap.findOrCreateEntry(key);
		old.importObject(key);
		old.importObject(value);
		return old;
	}

	public Set<java.util.Map.Entry<Object, IScriptObject>> entrySet() {
		Set<java.util.Map.Entry<Object, IScriptObject>> ret = new HashSet<java.util.Map.Entry<Object, IScriptObject>>();
		Iterator<IScriptObject> its = brokerSet.iterator();
		while (its.hasNext()) {
			IScriptObject nxt = its.next();
			ret.add(nxt.toEntry());
		}
		return ret;
	}

	public void bind(String name, Remote obj) throws RemoteException,
			AlreadyBoundException, AccessException {
		set(name, obj);
	}

	public String[] list() throws RemoteException, AccessException {
		return getAllNames();
	}

	public void rebind(String name, Remote obj) throws RemoteException,
			AccessException {
		set(name, obj);
	}

	public void unbind(String name) throws RemoteException, NotBoundException,
			AccessException {
		remove(name);
	}

	public/* static */String unqualifiedClassname(Class type) {
		if (type.isArray()) {
			return unqualifiedClassname(type.getComponentType()) + "[]";
		}
		String objectName = type.getName();
		return objectName.substring(objectName.lastIndexOf('.') + 1);
	}

	public String toString(Object target) {
		// System.out.println("toName: " + target);
		if (target == null)
			return "null";
		if (target instanceof INamedObject)
			try {
				return (((INamedObject) target).getName());
			} catch (RemoteException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
				throw new RuntimeException(e1);
			} catch (Throwable e1) {
				throw new RuntimeException(e1);

			}
		if (target instanceof CharSequence)
			return target.toString();
		if (target instanceof bsh.This)
			return toString(((bsh.This) target).getNameSpace());
		if (target instanceof bsh.Interpreter)
			return toString(((bsh.Interpreter) target).getNameSpace());
		if (target instanceof bsh.NameSpace)
			return ((bsh.NameSpace) target).getName();
		if (target instanceof IClass)
			return ((IClass) target).getName();
		if (target instanceof IVector)
			return target.toString();
		if (target instanceof CycNart)
			return toString(((CycNart) target).toCycList());
		if (target instanceof Number)
			return ""
					+ ((((Number) target).intValue() == ((Number) target)
							.hashCode()) ? ((Number) target).intValue()
							: ((Number) target).doubleValue());
		if (target instanceof Class)
			return unqualifiedClassname((Class) target); // +"-JavaClass";
		if (target instanceof CycList) {
			final CycList l = (CycList) target;
			final Iterator its = l.iterator();
			while (its.hasNext()) {
				final Object it = its.next();
				if (it instanceof CharSequence)
					return it.toString();
			}
			if (l.size() > 1)
				return toString(((CycList) target).second());
		}
		if (target instanceof CycConstant)
			return ((CycConstant) target).getName();
		if (target instanceof CycSymbol) {
			if (((CycSymbol) target).isKeyword())
				return "" + target;
			return "!" + target;
		}
		String name = null;
		try {
			name = toString(invokeFirst(target.getClass(), target,
					new String[] { "getName", "getShortname", "getEntname",
							"toName", "name", "getKey", "getTitle", "getText",
							"toString" }, new Class[0], new Object[0]));
		} catch (final Throwable ex) {
		}
		if (name == null) {
			name = target.getClass().getName();
			name = name.substring(name.lastIndexOf('.')) + target.hashCode();
		}
		// name = java.beans.NameGenerator.instanceName(target);
		// name = name.substring(name.lastIndexOf(":")+1);
		// final int dot = name.lastIndexOf('.');
		// if (dot < name.length() - 4) name = name.substring(dot + 1);
		// dot = name.lastIndexOf('@');if (dot<name.length()-2) {name =
		// name.substring(dot + 1);}
		// dot = name.lastIndexOf('@');
		// name = name.substring(dot + 1);
		// name = name.split(" ")[0];
		// name = name.split("$")[0];
		// name = name.split(",")[0];
		return name;
	}

	public static Object invokeFirst(final Class oclass, final Object target,
			final String[] propname, final Class[] paramTypes,
			final Object[] params) throws Throwable {
		String message = "ObjectInfo invokeFirst "
				+ target.getClass().getName() + " " + target + " [";
		Throwable resulterr = null;
		Throwable missing = null;
		if (propname != null)
			for (final String element : propname)
				try {
					message += element + " ";
					return oclass.getMethod(element, paramTypes).invoke(target,
							params);
				} catch (final NoSuchMethodException ex) {
					missing = ex;
				} catch (final InvocationTargetException ex) {
					missing = ex;
				} catch (final IllegalArgumentException ex) {
					missing = new InvocationTargetException(ex);
				} catch (final IllegalAccessException ex) {
					missing = ex;
				} catch (final Throwable ex) {
					resulterr = ex;
				}
		if (resulterr != null)
			throw new RuntimeException(message, resulterr);
		if (missing != null)
			throw missing;
		throw new NoSuchMethodException(message);
	}

	@SuppressWarnings("unchecked")
	public <C> Collection valuesOfType(Class<C> name) {
		Collection<C> col = new ArrayList<C>();
		Iterator<IScriptObject> its = brokerSet.iterator();
		while (its.hasNext()) {
			IScriptObject ext = its.next();
			C coerceTo = null;
			if (ext.instanceOf(name)) {
				try {
					coerceTo = (C) ext.coerceTo(name);
				} catch (ClassCastException e) {
					e.printStackTrace();
				} catch (Throwable e) {
					e.printStackTrace();

				}
				if (coerceTo != null)
					col.add(coerceTo);
			}
		}
		return col;
	}

}
