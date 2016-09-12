/**
 * 
 */
package daxclr.bsf;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.io.IOException;
import java.io.ObjectStreamException;
import java.io.Serializable;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.rmi.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.EventListener;
import java.util.HashSet;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Vector;
import java.util.Map.Entry;

import org.apache.bsf.BSFException;
import org.opencyc.cycobject.CycFort;
import org.opencyc.cycobject.CycList;
import org.opencyc.cycobject.CycNart;
import org.opencyc.cycobject.CycObject;
import org.opencyc.xml.XMLStringWriter;

import sun.misc.Unsafe;
import sun.reflect.ConstructorAccessor;
import sun.reflect.ReflectionFactory;
import bsh.BshMethod;
import bsh.CallStack;
import bsh.EvalError;
import bsh.ExternalNameSpace;
import bsh.Interpreter;
import bsh.Modifiers;
import bsh.Name;
import bsh.NameSpace;
import bsh.Primitive;
import bsh.SimpleNode;
import bsh.This;
import bsh.Types;
import bsh.UtilEvalError;
import bsh.Variable;
import bsh.XThis;
import daxclr.inference.CycAPI;
import daxclr.inference.CycFortPropertyMap;
import daxclr.inference.NativeManager;

/**
 * @author Administrator
 * 
 */
abstract class ScriptNameSpace extends NameSpace implements Remote,
		IScriptObject, IScriptMethodImpl {
	ScriptBeanInfo beanInfo;

	public Object invoke(Object proxy, Method method, Object[] params) {
		Object a = null;
		Iterator<InvocationHandler> handlers = hookedHandlers.iterator();
		while (handlers.hasNext()) {
			InvocationHandler h = handlers.next();
			try {
				a = h.invoke(proxy, method, params);
			} catch (Exception e) {
				e.printStackTrace();
			} catch (Throwable e) {
				e.printStackTrace();
			}
		}
		Object b = invokeMethodImpl(ScriptNameSpace.this,
				(This) ScriptNameSpace.this.getThis(), proxy, method
						.getName(), method.getParameterTypes(), params,
				ScriptNameSpace.this.getInterpreter(), null,
				callerInfoNode, false);
		return b != null ? b : a;
	}

	public Serializable invokeMethod(String methodName, Object[] params)
			throws EvalError, BSFException, NoSuchMethodException {
		return invokeMethodImpl(ScriptNameSpace.this,
				(This) ScriptNameSpace.this.getThis(), getValue(),
				methodName, Types.getTypes(params), params, getInterpreter(),
				null, null, false);
	}

	public Serializable invokeMethodImpl(NameSpace nameSpace, This xthis,
			Object proxy, String methodName, Class[] paramaterTypes,
			Object[] params, Interpreter interpreter, CallStack callstack,
			SimpleNode simpleNode, boolean declaredOnly) {
				
		/*
		 * Wrap nulls. This is a bit of a cludge to address a deficiency in the
		 * class generator whereby it does not wrap nulls on method delegate.
		 * See Class Generator.java. If we fix that then we can remove this.
		 * (just have to generate the code there.)
		 */
		if (interpreter == null)
			interpreter = ScriptNameSpace.this.getInterpreter();
		if (nameSpace == null)
			nameSpace = interpreter.getNameSpace();
		if (callstack == null)
			callstack = new CallStack(nameSpace);
		if (simpleNode == null)
			simpleNode = SimpleNode.JAVACODE;
		if (xthis == null)
			xthis = getThis(nameSpace, interpreter);
		if (proxy == null)
			proxy = getValue();

		// Find the bsh method
		BshMethod bshMethod = null;

		
		if (params != null) {
			Object[] oa = new Object[params.length];
			for (int i = 0; i < params.length; i++)
				oa[i] = (params[i] == null ? Primitive.NULL : params[i]);
			params = oa;
		} else {
			params = new Object[0];
		}
		
		Object vresult = null;
		Iterator<IScriptMethodHandler> handlers = scriptMethodHandlers.iterator();
		while (handlers.hasNext()) {
			IScriptMethodHandler h = handlers.next();
			try {
				Object a = h.invokeMethod(methodName, params);
				if (a!=null) vresult = a;
			} catch (Exception e) {
				e.printStackTrace();
			} catch (Throwable e) {
				e.printStackTrace();
			}
		}
		try {
			bshMethod = nameSpace.getMethod(methodName, paramaterTypes,
					declaredOnly);
		} catch (UtilEvalError e) {
			// leave null
		}

		if (bshMethod != null) {
			try {
				return (Serializable) bshMethod.invoke(params, interpreter,
						callstack, simpleNode);
			} catch (EvalError e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			} catch (Throwable e1) {
				e1.printStackTrace();
			}
		}
		/*
		 * If equals() is not explicitly defined we must override the default
		 * implemented by the This object protocol for scripted object. To
		 * support XThis equals() must test for equality with the generated
		 * proxy object, not the scripted bsh This object; otherwise callers
		 * from outside in Java will not see a the proxy object as equal to
		 * itself.
		 */
		if (methodName.equals("equals")) {
			if (bshMethod == null) {
				Object obj = params[0];
				return valueEquals(obj);
			} else {
				Object obj = params[0];
				return proxy == obj ? Boolean.TRUE : Boolean.FALSE;
			}
		}
		// Look for a default invoke() handler method in the namespace
		// Note: this code duplicates that in NameSpace getCommand()
		// is that ok?
		try {
			bshMethod = nameSpace.getMethod("invoke",
					new Class[] { null, null });
		} catch (UtilEvalError e) { /* leave null */
		}
		
		try {
			if (bshMethod!=null) return (Serializable) bshMethod.invoke(new Object[] { methodName,
					params }, interpreter, callstack, simpleNode);
		} catch (EvalError e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		} catch (Throwable e1) {
			e1.printStackTrace();
		}

		/*
		 * No scripted method of that name. Implement the required part of the
		 * Object protocol: public int hashCode(); public boolean
		 * equals(java.lang.Object); public java.lang.String toString(); if
		 * these were not handled by scripted methods we must provide a default
		 * impl.
		 */
		try {
			return (Serializable) this.getClass().getMethod(methodName,
					paramaterTypes).invoke(this, params);
		} catch (NoSuchMethodException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		} catch (Throwable e1) {
			e1.printStackTrace();
		}
		try {
			return (Serializable)  getThis().getClass().getMethod(methodName,
					paramaterTypes).invoke(getThis(), params);
		} catch (NoSuchMethodException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		} catch (Throwable e1) {
			e1.printStackTrace();
		}
		try {
			return (Serializable)  getThis().getInvocationHandler().getClass().getMethod(methodName,
					paramaterTypes).invoke(getThis().getInvocationHandler().getClass(), params);
		} catch (NoSuchMethodException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		} catch (Throwable e1) {
			e1.printStackTrace();
		}
		return (Serializable) vresult;

	}

	public ScriptNameSpace getNameSpace() throws RemoteException {
		return this;
	}

	public transient ExternalNameSpace listenerMapNameSpace;

	public transient List<Constructor> hookedConstructors;

	public transient Map<String, ScriptingConstructorAccessor> scriptConstructorHooks;

	public transient List<Field> hookedFields;

	public transient List<Method> hookedMethods;

	public transient MergedMap mergedMap;

	public transient PropertyListenerMap propertyListenerMap;

	public transient PropertyChangeSupport pcSupport;

	public transient Map<String, IMultiSourceProperty> scriptingAccessors;

	public transient Interpreter interpreter;

	public transient CycFortPropertyMap cycMap;

	public transient CycFort cycObject;

	public transient List<InvocationHandler> hookedHandlers;

	public transient List<IScriptMethodHandler> scriptMethodHandlers;

	public ScriptNameSpace(This xthis, NameSpace parentNameSpace,
			String name) {
		super((NameSpace) parentNameSpace, name);
		thisReference = xthis != null ? xthis : new ScriptableThis(this,
				interpreter);
		/*
		 * if (RemoteOperations.findBroker(name) != null) throw new
		 * NameAlreadyBoundException(name);
		 */
		// RemoteOperations.set(name, this);
		// setValue(propertyListenerMap);
		// BSFManager.registerScriptingEngine(lang, engineClassName, extensions)
		// importObject(getInterpreter());
	}

	protected void initObject() {
		if (cycObject == null) {
			initTransient();
			cycObject = getCycObject();
			importClass(IScriptObject.class.getName());
			importObject(this);
			try {
				importMap(getCycMap());
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

	private void initTransient() {
		if (interpreter == null) {
			interpreter = new Interpreter();
			interpreter.setNameSpace(this);
			if (mergedMap == null)
				mergedMap = new MergedMap(new LinkedList<Map<Object, Object>>());
			if (propertyListenerMap == null)
				propertyListenerMap = new PropertyListenerMap(mergedMap);
			propertyListenerMap.addListener(this);
			if (pcSupport == null)
				pcSupport = new PropertyChangeSupport(this);
			if (scriptConstructorHooks == null)
				scriptConstructorHooks = new Hashtable<String, ScriptingConstructorAccessor>();
			if (hookedHandlers == null)
				hookedHandlers = new LinkedList<InvocationHandler>();
			if (scriptMethodHandlers == null)
				scriptMethodHandlers = new LinkedList<IScriptMethodHandler>();
			if (hookedConstructors == null)
				hookedConstructors = new LinkedList<Constructor>();
			if (hookedFields == null)
				hookedFields = new LinkedList<Field>();
			if (hookedMethods == null)
				hookedMethods = new LinkedList<Method>();
			if (scriptingAccessors == null)
				scriptingAccessors = new Hashtable<String, IMultiSourceProperty>();
			if (!(thisReference instanceof ScriptableThis))
				thisReference = new ScriptableThis(this, this.getInterpreter());
			try {
				thisReference.getInterface(this.getClass().getInterfaces());
			} catch (UtilEvalError e) {
				e.printStackTrace();
			} catch (Throwable e) {
				e.printStackTrace();
			}
			importClass(this.getClass());
			beanInfo = new ScriptBeanInfo(this);
		}
		// getBeanInfo();
	}

	// public static IRemoteGame getGameServer() {
	// return RemoteOperations.getGameLocal();
	// }

	/*
	 * public static IRemoteNativeServer getDoomServer() { try { return
	 * getGameServer().getDoomServer(); } catch (Throwable e) { throw new
	 * RuntimeException(e); } }
	 */
	public void importClass(Class clazz) {
		if (clazz.isInterface())
			try {
				getThis().getInterface(clazz);
			} catch (RemoteException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				throw new RuntimeException(e);
				} catch (Throwable e) {
				throw new RuntimeException(e);
				
			}
		importClass(clazz.getName());
		Class clazz2 = clazz.getSuperclass();
		if (clazz2 != null)
			importClass(clazz2);
	}

	public void importClass(Object iface) {
		try {
			importClass(ObjectRepository.resolverMap.toClass(iface).getName());
		} catch (ClassNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (Throwable e) {
			throw new RuntimeException(e);
		}
	}

	public ScriptBeanInfo getBeanInfo() {
		if (beanInfo == null)
			beanInfo = new ScriptBeanInfo(this);
		return beanInfo;
	}

	class ScriptableThis extends XThis implements IScriptObjectThis {
		// public static final long serialVersionUID = 2L;

		/**
		 * A cache of proxy interface handlers. Currently just one per
		 * interface.
		 */
		// protected/* dmiles */Hashtable interfaces;
		// protected/* dmiles */InvocationHandler invocationHandler = new
		// ThisHandler();
		/**
		 * 
		 */
		private static final long serialVersionUID = 6065932215465437362L;

		public ScriptableThis(NameSpace namespace, Interpreter declaringInterp) {
			super(namespace, declaringInterp);
			invocationHandler = new ScriptingHandler();
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see daxclr.scripting.IScriptObjectThis#getInvocationHandler()
		 */
		public ScriptingHandler getInvocationHandler() {
			return (ScriptingHandler) invocationHandler;
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see daxclr.scripting.IScriptObjectThis#getInterface(java.lang.Class)
		 */
		public Object getInterface(Class clas) {
			return getInterfaceProxy(new Class[] { clas });
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see daxclr.scripting.IScriptObjectThis#getInterface(java.lang.Class[])
		 */
		public Object getInterface(Class[] clas) {
			return getInterfaceProxy(clas);
		}

		/**
		 * The namespace that this This reference wraps.
		 */
		// NameSpace namespace;
		/**
		 * This is the interpreter running when the This ref was created. It's
		 * used as a default interpreter for callback through the This where
		 * there is no current interpreter instance e.g. interface proxy or
		 * event call backs from outside of bsh.
		 */
		// transient Interpreter declaringInterpreter;
		/*
		 * (non-Javadoc)
		 * 
		 * @see daxclr.scripting.IScriptObjectThis#run()
		 */
		/*
		 * static This getThis(NameSpace namespace, Interpreter
		 * declaringInterpreter) { try { Class c; if
		 * (Capabilities.canGenerateInterfaces()) c =
		 * Class.forName("bsh.XThis"); else if (Capabilities.haveSwing()) c =
		 * Class.forName("bsh.JThis"); else return new This(namespace,
		 * declaringInterpreter);
		 * 
		 * return (This) Reflect.constructObject(c, new Object[] { namespace,
		 * declaringInterpreter }); } catch (Exception e) { throw new
		 * InterpreterError("internal error 1 in This: " + e); } }
		 */
		/*
		 * I wish protected access were limited to children and not also package
		 * scope... I want this to be a singleton implemented by various
		 * children.
		 */

		public void run() {
			try {
				invokeMethod("run", new Object[0]);
			} catch (EvalError e) {
				ScriptNameSpace.this.getInterpreter().error(
						"Exception in runnable:" + e);
			}
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see daxclr.scripting.IScriptObjectThis#invokeMethod(java.lang.String,
		 *      java.lang.Object[])
		 */
		public Serializable invokeMethod(String methodName, Object[] params)
				throws EvalError {
			return invokeMethodImpl(namespace, getThis(namespace,
					declaringInterpreter), getValue(), methodName, Types
					.getTypes(params), params, getInterpreter(), null, null,
					false);
		}

		/**
		 * Invoke a method in this namespace with the specified params,
		 * interpreter reference, callstack, and caller info.
		 * <p>
		 * 
		 * Note: If you use this method outside of the bsh package and wish to
		 * use variables with primitive values you will have to wrap them using
		 * bsh.Primitive. Consider using This getInterface() to make a true Java
		 * interface for invoking your scripted methods.
		 * <p>
		 * 
		 * This method also implements the default object protocol of
		 * toString(), hashCode() and equals() and the invoke() meta-method
		 * handling as a last resort.
		 * <p>
		 * 
		 * Note: The invoke() meta-method will not catch the Object protocol
		 * methods (toString(), hashCode()...). If you want to override them you
		 * have to script them directly.
		 * 
		 * <p>
		 * 
		 * @see bsh.This.invokeMethod( String methodName, Object [] params,
		 *      Interpreter interpreter, CallStack callstack, SimpleNode
		 *      callerInfo )
		 * @param if
		 *            callStack is null a new CallStack will be created and
		 *            initialized with this namespace.
		 * @param declaredOnly
		 *            if true then only methods declared directly in the
		 *            namespace will be visible - no inherited or imported
		 *            methods will be visible.
		 * @see bsh.Primitive
		 */
		/*
		 * invokeMethod() here is generally used by outside code to callback
		 * into the bsh interpreter. e.g. when we are acting as an interface for
		 * a scripted listener, etc. In this case there is no real call stack so
		 * we make a default one starting with the special JAVACODE namespace
		 * and our namespace as the next.
		 */
		public Object invokeMethod(String methodName, Object[] params,
				Interpreter interpreter, CallStack callstack,
				SimpleNode callerInfo, boolean declaredOnly) {

			return invokeMethodImpl(namespace, this, this, methodName, Types
					.getTypes(params), params, interpreter, callstack,
					callerInfo, declaredOnly);
		}

		/**
		 * Bind a This reference to a parent's namespace with the specified
		 * declaring interpreter. Also re-init the callstack. It's necessary to
		 * bind a This reference before it can be used after deserialization.
		 * This is used by the bsh load() command.
		 * <p>
		 * 
		 * This is a static utility method because it's used by a bsh command
		 * bind() and the interpreter doesn't currently allow access to direct
		 * methods of This objects (small hack)
		 */
		// public static void bind(This ths, NameSpace namespace, Interpreter
		// declaringInterpreter) {
		// ths.namespace.setParent(namespace);
		// ths.declaringInterpreter = declaringInterpreter;
		// }
		/**
		 * Allow invocations of these method names on This type objects. Don't
		 * give bsh.This a chance to override their behavior.
		 * <p>
		 * 
		 * If the method is passed here the invocation will actually happen on
		 * the bsh.This object via the regular reflective method invocation
		 * mechanism. If not, then the method is evaluated by bsh.This itself as
		 * a scripted method call.
		 */
		// static boolean isExposedThisMethod(String name) {
		// return name.equals("getClass") || name.equals("invokeMethod") ||
		// name.equals("getInterface")
		// These are necessary to let us test synchronization from scripts
		// || name.equals("wait") || name.equals("notify") ||
		// name.equals("notifyAll");
		// }
		/**
		 * This is the invocation handler for the dynamic proxy.
		 * <p>
		 * 
		 * Notes: Inner class for the invocation handler seems to shield this
		 * unavailable interface from JDK1.2 VM...
		 * 
		 * I don't understand this. JThis works just fine even if those classes
		 * aren't there (doesn't it?) This class shouldn't be loaded if an XThis
		 * isn't instantiated in NameSpace.java, should it?
		 */
		public class ScriptingHandler extends Handler implements
				IScriptMethodHandler, java.io.Serializable {
			private static final long serialVersionUID = -3094182360931316009L;

			public Object invoke(Object proxy, Method method, Object[] params)
					throws Throwable {
				return super.invoke(proxy, method, params);
			}

			public Serializable invokeMethod(String methodName, Object[] params)
					throws EvalError {
				return invokeMethodImpl(namespace, ScriptableThis.this,
						ScriptableThis.this, methodName,
						Types.getTypes(params), params, declaringInterpreter,
						null, callerInfoNode, false);
			}

			public Object invokeImpl(Object proxy, Method method,
					Object[] params) throws EvalError {
				return invokeMethodImpl(namespace, ScriptableThis.this, proxy,
						method.getName(), method.getParameterTypes(), params,
						declaringInterpreter, null, callerInfoNode, false);
			}

			public Object readResolve() throws ObjectStreamException {
				// TODO Auto-generated method stub
				return ObjectRepository.getResolverMap().findOrCreateEntry(
						getName()).getInvocationHandler();
			}

			public String toString() {
				return "'this'reference (" + this.getClass().getName()
						+ ") to Bsh object: " + getNameSpace();
			}
		}

		public String toString() {
			return "'this'reference (" + this.getClass().getName()
					+ ") to Bsh object: " + getNameSpace();
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see daxclr.scripting.IScriptObjectThis#readResolve()
		 */
		public Object readResolve() throws ObjectStreamException,
				RemoteException {
			return ObjectRepository.getResolverMap().findOrCreateEntry(
					getName()).getThis();
		}

		@SuppressWarnings("unchecked")
		/**
		 * Get dynamic proxy for interface, caching those it creates.
		 */
		public Object getInterfaceProxy(Class[] ca) {

			if (interfaces == null)
				interfaces = new Hashtable();

			// Make a hash of the interface hashcodes in order to cache them
			int hash = 21;
			for (int i = 0; i < ca.length; i++)
				hash *= ca[i].hashCode() + 3;
			Object hashKey = new Integer(hash);

			Object interf = interfaces.get(hashKey);

			if (interf == null) {
				ClassLoader classLoader = Thread.currentThread()
						.getContextClassLoader();// ca[0].getClassLoader();
				// // ?
				interf = Proxy.newProxyInstance(classLoader, ca,
						getInvocationHandler());
				interfaces.put(hashKey, interf);
			}

			return interf;
		}

		public String getName() {
			return getNameSpace().getName();
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see daxclr.scripting.IScriptObjectThis#toRemote()
		 */
		public IScriptObjectRemote toRemote() throws RemoteException {
			// TODO Auto-generated method stub
			return null;
		}
	}

	public String toString() {
		return "'this.namespace' reference (" + this.getClass().getName()
				+ ") to Bsh object: " + getName();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see daxclr.scripting.IScriptMethodImpl#invokeMethodImpl(bsh.NameSpace,
	 *      bsh.This, java.lang.Object, java.lang.String, java.lang.Class[],
	 *      java.lang.Object[], bsh.Interpreter, bsh.CallStack, bsh.SimpleNode,
	 *      boolean)
	 */

	// a default toString() that shows the interfaces we implement
	// CallStack callstack = new CallStack(getNameSpace());
	/**
	 * @see #getMethod( String, Class [], boolean )
	 * @see #getMethod( String, Class [] )
	 */
	public BshMethod getMethod(String name, Class[] sig) throws UtilEvalError {
		return getMethod(name, sig, false/* declaredOnly */);
	}

	public IScriptMethodHandler getInvocationHandler() {
		try {
			return getThis().getInvocationHandler();
		} catch (RemoteException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			throw new RuntimeException(e);
			} catch (Throwable e) {
			throw new RuntimeException(e);
			
		}
	}

	/**
	 * Get the bsh method matching the specified signature declared in this name
	 * space or a parent.
	 * <p>
	 * Note: this method is primarily intended for use internally. If you use
	 * this method outside of the bsh package you will have to be familiar with
	 * BeanShell's use of the Primitive wrapper class.
	 * 
	 * @see bsh.Primitive
	 * @return the BshMethod or null if not found
	 * @param declaredOnly
	 *            if true then only methods declared directly in this namespace
	 *            will be found and no inherited or imported methods will be
	 *            visible.
	 */
	public BshMethod getMethod(String name, Class[] sig, boolean declaredOnly)
			throws UtilEvalError {
		return super.getMethod(name, sig, declaredOnly);
	}

	/* ,BeanContextChild,java.io.Serializable,BshClassManager.Listener,NameSource */
	public static transient ReflectionFactory reflectionFactory = ReflectionFactory
			.getReflectionFactory();

	public static transient CycFort cycIDObjectFn = CycAPI.c("IDObjectFn");

	public static transient CycFort cycJavaClassFn = CycAPI.c("JavaClassFn");

	public static transient boolean nsmErrors = true;

	public static transient boolean runtimeErrors = false;

	public static transient boolean catchSneakyFields = false;

	public static transient Modifiers modifiers = new Modifiers();
	static {
		ScriptingSecurityManager.install();
		if (!ScriptObject.modifiers.hasModifier("public"))
			ScriptObject.modifiers.addModifier(Modifiers.FIELD, "public");
		// NativeManager.setAllAccessable(Class.class);
		NativeManager.setAllAccessable(Field.class);
		NativeManager.setAllAccessable(Method.class);
		NativeManager.setAllAccessable(Unsafe.class);
	}

	static public CycFort lookUpName(String key) {
		CycObject found = (CycObject) CycAPI.current().converseObject(
				"(dfn \"" + key + "\")");
		if (found instanceof CycList)
			found = CycNart.coerceToCycNart(found);
		return (CycFort) found;
	}

	private CycFortPropertyMap getCycMap() {
		if (cycMap == null) {
			cycMap = CycFortPropertyMap.toCycObjectInfo(this);
		}
		return cycMap;
	}

	public void setParent(NameSpace parentNameSpace) {
		initTransient();
		loadDefaultImports();
		if (listenerMapNameSpace == null)
			listenerMapNameSpace = new ExternalNameSpace(parentNameSpace,
					getName(), propertyListenerMap);
		super.setParent(listenerMapNameSpace);
		// this.parent = parent;
		// If we are disconnected from root we need to handle the def imports
	}

	// this updates the Interpreter
	public Interpreter getInterpreter() {
		interpreter.setNameSpace(this);
		return interpreter;
	}

	/**
	 * @return the multipleMaps
	 */
	public MergedMap getMergedMap() {
		return mergedMap;
	}

	/**
	 * @param cycObject
	 *            the cycObject to set
	 */
	public void setCycObject(CycFort fort) {
		if (cycObject == null || !cycObject.equals(fort)) {
			cycObject = fort;
			cycMap = CycFortPropertyMap.toCycObjectInfo(this);
			importMap(cycMap);
		}
	}

	public CycFort getCycObject() {
		if (cycObject == null) {
			CycFort found = lookUpName(getName());
			if (found == null) {
				found = new CycNart(cycIDObjectFn, getName());
			}
			setCycObject(found);
		}
		return cycObject;
	}

	/**
	 * @return the beanClass
	 */
	public Class<?> getBeanClass() {
		if (true)
			return IScriptObject.class;
		if (true)
			return getValue().getClass();
		Class[] c = null;
		if (isImmutable()) {
			c = getInterfaces();
			if (c.length > 0)
				return c[0];
			return getValue().getClass();
			// return IObjectProxy.class;
		} else {
			c = getClasses();
			if (c.length > 0)
				return c[0];
			return IScriptObject.class;
		}
	}

	// public Class getCustomizerClass() {
	// return daxclr.beanbowl.editors.ClassCustomizer.class;
	// }

	abstract boolean isImmutable();

	// org.ecli EObj
	// static Ecore2XMLItemProviderAdapterFactory effactory = new
	// Ecore2XMLItemProviderAdapterFactory();
	// public Object toEObject() {
	// }
	// public Map dynaprops = new Hashtable();
	public String toXMLString() {
		String string0 = getName();
		try {
			XMLStringWriter xml = new XMLStringWriter();
			getCycObject().toXML(xml, 0, false);
			string0 = xml.toString();
		} catch (IOException ex) {
			traceTODO(ex);
		}
		return string0.replace('\n', ' ').replace('\r', ' ').replace('\t', ' ')
				.replace((char) 12, ' ').replace("  ", " ").replace("  ", " ")
				.replace("  ", " ").replace("> <", "><").trim();
	}

	public String getPropertyCode(String propname) throws NoSuchFieldException {
		return "";
	}

	public Serializable getValue() {
		try {
			return (Serializable) getThis().getInterface(getInterfaces());
		} catch (RemoteException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			throw new RuntimeException(e);
			} catch (Throwable e) {
			throw new RuntimeException(e);
			
		}
	}

	/**
	 * @return the listenerMapNameSpace
	 */
	public ExternalNameSpace getParent() {
		return listenerMapNameSpace;
	}

	/**
	 * @param obj
	 * @param name
	 * @param params
	 * @return
	 */
	// abstract public Object call(Object obj, String name, Object[] params)
	// throws BSFException;
	public void trace(String msg) {
		getInterpreter().println(msg);
	}

	final static public void debug(Object msg) {
		ConsoleChannel.debug(msg);
	}

	public void traceTODO(Throwable t) {
		if (t != null)
			t.printStackTrace(ConsoleChannel.getErrorStream());
		trace("TODO: " + t);
	}

	@Override
	public String[] getAllNames() {
		HashSet<String> names = new HashSet<String>();
		names.addAll(Arrays.asList(getVariableNames()));
		names.addAll(Arrays.asList(getMethodNames()));
		return names.toArray(new String[0]);
	}

	@SuppressWarnings("unchecked")
	public Hashtable getImportedClasses() {
		Hashtable all = new Hashtable();
		// all = this.importedClasses;
		Hashtable more = super.getImportedClasses();
		if (more != null)
			all.putAll(more);
		NameSpace tparent = getParent();
		while (tparent != null && !(tparent instanceof ExternalNameSpace)) {
			more = tparent.getImportedClasses();
			if (more != null)
				all.putAll(more);
			tparent = tparent.getParent();
		}
		return all;
	}

	@SuppressWarnings("unchecked")
	@Override
	public Vector getImportedObjects() {
		Vector all = new Vector();
		// all = this.importedClasses;
		Vector more = super.getImportedObjects();
		if (more != null)
			all.addAll(more);
		NameSpace tparent = getParent();
		while (tparent != null && !(tparent instanceof ExternalNameSpace)) {
			more = tparent.getImportedObjects();
			if (more != null)
				all.addAll(more);
			tparent = tparent.getParent();
		}
		return all;
	}

	@Override
	public void importClass(String name) {
		initTransient();
		super.importClass(name);
		Class clazz = null;
		try {
			clazz = this.getClass(name);
			NativeManager.setAllAccessable(clazz);
			if (beanInfo != null)
				beanInfo.addBeanClass(clazz);
			importConstructors(clazz.getDeclaredConstructors(), clazz);
		} catch (UtilEvalError uee) {
			uee.printStackTrace();
		}
		if (clazz != null) {
			Class[] local_interfaces = clazz.getInterfaces();
			if (local_interfaces != null)
				for (Class element : local_interfaces)
					importClass(element.getName());
			Class[] classes = clazz.getClasses();
			if (classes != null)
				for (Class element : classes)
					importClass(element.getName());
		}
	}

	public void importMap(Map newValue) {
		if (newValue == null || getMergedMap() == newValue)
			return;
		mergedMap.addMap(newValue);
	}

	public void importNameSpace(NameSpace newValue) {
		if (newValue == null || this == newValue || getParent() == newValue)
			return;
		super.importObject(newValue);
	}

	private void importThis(This newValue) {
		super.importObject(newValue);
	}

	@Override
	public void importObject(Object newValue) {
		initObject();
		if (newValue != null) {
			Class<? extends Object> cl = newValue.getClass();
			if (Proxy.isProxyClass(cl)) {
				InvocationHandler handler = Proxy
						.getInvocationHandler(newValue);
				importHandler(handler, cl.getInterfaces());
			} else if (newValue instanceof Map) {
				importMap((Map) newValue);
			} else if (newValue instanceof NameSpace) {
				importNameSpace((NameSpace) newValue);
			} else if (newValue instanceof This) {
				importThis((This) newValue);
			} else if (newValue instanceof IScriptMethodHandler) {
				importMethodHandler((IScriptMethodHandler) newValue);
			} else {
				importObject(cl, newValue);
				super.importObject(newValue);
			}
		}
	}

	private void importMethodHandler(IScriptMethodHandler handler) {
		// TODO Auto-generated method stub

	}

	public void importObject(Class clazz, Object newValue) {
		importClass(clazz.getName());
		importMethods(clazz.getDeclaredMethods(), newValue);
		importFields(clazz.getDeclaredFields(), newValue);
		Class clazz2 = clazz.getSuperclass();
		if (clazz2 != null)
			importObject(clazz2, newValue);
	}

	@Override
	public void nameSpaceChanged() {
		super.nameSpaceChanged();
	}

	// }

	/**
	 * This ConstructorHook provides the declaration for
	 * java.lang.reflect.Constructor.invoke(). Each Constructor object is
	 * configured with a (possibly dynamically-generated) class which implements
	 * this ConstructorAccessor.
	 */
	public class ScriptingConstructorAccessor implements ConstructorAccessor {
		public String propname;

		public String aritySignature;

		public Class<?> instance;

		public ConstructorAccessor nativeAccessor;

		public Constructor original;

		public ScriptingConstructorAccessor previousHook;

		public ScriptingConstructorAccessor(Class obj, Constructor mf) {
			propname = obj.getName();
			original = mf;
			instance = obj;
			aritySignature = mf.getName() + mf.getParameterTypes().length;
			previousHook = scriptConstructorHooks.get(aritySignature);
			nativeAccessor = ScriptObject.reflectionFactory
					.getConstructorAccessor(mf);
			scriptConstructorHooks.put(aritySignature, this);
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see sun.reflect.ConstructorAccessor#newInstance(java.lang.Object[])
		 */
		public Object newInstance(Object[] params)
				throws InstantiationException, IllegalArgumentException,
				InvocationTargetException {
			IScriptObject so;
			try {
				so = ScriptNameSpace.this.newInstance(getName()
						+ (hashCode() + 1));
				so.importObject(ObjectRepository.resolverMap.toObject(nativeAccessor
						.newInstance(params)));
				return so.getValue();
			} catch (Throwable ex) {
				// TODO Auto-generated catch block
				ex.printStackTrace();
				throw new IllegalArgumentException("ScriptObject.newInstance "
						+ propname, ex);
			}
		}
	}

	/**
	 * @param newName
	 * @return IScriptObject
	 */
	abstract public IScriptObject newInstance(String newName)
			throws InstantiationException;

	/**
	 * @param newValue
	 */
	/**
	 * abstract public void setProperty(String name, Object val) throws
	 * IllegalAccessException;
	 * 
	 * @param name
	 * @param val
	 */
	public void setProperty(String propname, Object value)
			throws IllegalArgumentException, IllegalAccessException {
		System.out.println("setProperty " + propname);
		IPropertySourceImpl hook = scriptingAccessors.get(propname);
		if (hook != null)
			try {
				hook.setValueImpl(this, value);
			} catch (UnsupportedOperationException e) {
				throw new IllegalArgumentException(e);
			} catch (ClassCastException e) {
				throw new IllegalArgumentException(e);
			} catch (NullPointerException e) {
				throw new IllegalArgumentException(e);
			} catch (IllegalStateException e) {
				throw new IllegalArgumentException(e);
			}
		throw new IllegalArgumentException("setProperty " + propname);
	}

	public Object getVariableOrProperty(String name, Interpreter interp)
			throws UtilEvalError {
		try {
			return getProperty(name);
		} catch (NoSuchFieldException e) {
			// TODO Auto-generated catch block
			return super.getVariableOrProperty(name, interp);
		}
	}

	/**
	 * Resolve name to an object through this namespace.
	 * 
	 * super is: CallStack callstack = new CallStack(this); return
	 * getNameResolver(name).toObject(callstack, interpreter);
	 */
	public Object get(String name, Interpreter interpreter)
			throws UtilEvalError {
		try {
			return getProperty(name);
		} catch (NoSuchFieldException e) {
			// TODO Auto-generated catch block
			return super.get(name, interpreter);
		}
	}

	@SuppressWarnings("unchecked")
	public Name getNameResolver(String ambigname) {
		System.out.println("getNameResolver: " + ambigname);
		if (names == null)
			names = new Hashtable();
		Name name = (Name) names.get(ambigname);
		if (name == null) {
			name = new Name(this, ambigname);
			names.put(ambigname, name);
		}
		return name;
	}

	public Variable getVariableImpl(String name, boolean recurse)
			throws UtilEvalError {
		recurse = true;
		return super.getVariableImpl(name, recurse);
	}

	/**
	 * @param name
	 * @return
	 */
	public Object getProperty(String name) throws NoSuchFieldException {
		System.out.println("getProperty " + name);
		IPropertySourceImpl hook = scriptingAccessors.get(name);
		if (name.equalsIgnoreCase("pcSupport"))
			return pcSupport;
		if (hook != null)
			return hook.getValueImpl(this);
		if (name.equalsIgnoreCase("interpreter"))
			return getInterpreter();
		if (name.equalsIgnoreCase("namespace"))
			return this; // getParent();
		if (name.equalsIgnoreCase("beanClass"))
			return getBeanClass();
		if (name.equalsIgnoreCase("interfaces"))
			return getInterfaces();
		if (name.equalsIgnoreCase("this"))
			return getValue();
		String capped = ScriptObject.capitalize(name);
		try {
			return invokeMethod("get" + capped, new Object[0]);
		} catch (Throwable ex) {
			try {
				return invokeMethod("is" + capped, new Object[0]);
			} catch (Throwable ex2) {
				try {
					Object val = getVariable(name, true);
					return (val == Primitive.VOID) ? getPropertyValue(name,
							getInterpreter()) : val;
				} catch (UtilEvalError e) {
					traceTODO(e);
				}
				throw new NoSuchFieldException("getProperty: " + name + ex2);
			}
		}
	}

	/**
	 * @param src
	 * @return
	 */
	public Object eval(String src) throws EvalError {
		try {
			return getInterpreter().eval(src);
		} catch (EvalError ex) {
			traceTODO(ex);
			throw ex;
		}
	}

	/**
	 * @param name
	 * @param oldValue
	 * @param newValue
	 */
	protected void updateSuperclassProperty(String propertyName,
			Object oldValue, Object newValue) {
		if (newValue != oldValue) {
			PropertyChangeEvent evt = new PropertyChangeEvent(this,
					propertyName, oldValue, newValue);
			pcSupport.firePropertyChange(evt);
		}
	}

	abstract public IPropertySource importMethod(Method element, Object target);

	/**
	 * Returns a String which capitalizes the first letter of the string.
	 */
	protected static String capitalize(String name) {
		if (name == null || name.length() == 0)
			return name;
		return name.substring(0, 1).toUpperCase() + name.substring(1);
	}

	/**
	 * BeanUtils method to take a string and convert it to normal Java variable
	 * name capitalization. This normally means converting the first character
	 * from upper case to lower case,but in the (unusual) special case when
	 * there is more than one character and both the first and second characters
	 * are upper case,we leave it alone.
	 * <p>
	 * Thus "FooBah" becomes "fooBah" and "X" becomes "x",but "URL" stays as
	 * "URL".
	 * 
	 * @param name
	 *            The string to be decapitalized.
	 * @return The decapitalized version of the string.
	 */
	public static String decapitalize(String name) {
		if (name == null || name.length() == 0)
			return name;
		if (name.length() > 1 && Character.isUpperCase(name.charAt(1))
				&& Character.isUpperCase(name.charAt(0)))
			return name;
		/*  */char chars[] = name.toCharArray();
		chars[0] = Character.toLowerCase(chars[0]);
		return new String(chars);
	}

	public void addListener(EventListener l) {
		if (l instanceof PropertyChangeListener)
			addPropertyChangeListener((PropertyChangeListener) l);
	}

	public void addPropertyChangeListener(PropertyChangeListener p) {
		pcSupport.addPropertyChangeListener(p);
	}

	/**
	 * @param declaredConstructors
	 * @param clazz
	 */
	abstract protected ScriptingConstructorAccessor importConstructor(
			Constructor declaredConstructors, Class clazz);

	abstract public IPropertySource importField(Field element, Object target);

	public void importFields(Field[] info, Object target) {
		if (info != null)
			for (Field element : info) {
				NativeManager.setFieldPublic(element);
				if (element != null && !hookedFields.contains(element)) {
					hookedFields.add(element);
					importField(element, target);
				}
			}
		// this.setVariableOrProperty(name, value, strictJava); //
		// definingNameSpace.setMethod(name, method)
	}

	public void importMethods(Method[] info, Object target) {
		if (info != null)
			for (Method element : info)
				if (!hookedMethods.contains(element)) {
					hookedMethods.add(element);
					importMethod(element, target);
				}
	}

	public ScriptableThis getThis(NameSpace namespace,
			Interpreter declaringInterpreter) {
		if (!(thisReference instanceof ScriptableThis))
			thisReference = new ScriptableThis(this, declaringInterpreter);
		return (ScriptableThis) thisReference;
	}

	public void importConstructors(Constructor[] info, Class target) {
		initTransient();
		for (Constructor element : info)
			if (element != null && !hookedConstructors.contains(element))
				importConstructor(element, target);
	}

	public Class[] getClasses() {
		Hashtable names = getImportedClasses();
		ArrayList<Class> classes = new ArrayList<Class>(names.size());
		Iterator its = names.values().iterator();
		while (its.hasNext()) {
			Class c = null;
			try {
				c = getClass("" + its.next());
			} catch (UtilEvalError e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			if (c != null)
				classes.add(c);
		}
		return classes.toArray(new Class[0]);
	}

	public Class[] getInterfaces() {
		Class[] list = getClasses();
		HashSet<Class> classes = new HashSet<Class>(0);
		for (Class element : list)
			if (element != null && element.isInterface())
				classes.add(element);
		trace("getInterfaces: " + getName() + "=" + classes);
		return classes.toArray(new Class[0]);
	}

	public String getName() {
		return super.getName();
	}

	public boolean capitolChar(String s, int loc) {
		if (s == null)
			return false;
		if (s.length() < loc + 1)
			return false;
		/*  */String ss = "" + s.charAt(loc);
		if (ss.equals(ss.toUpperCase()))
			return true;
		return false;
	}

	public void propertyChange(PropertyChangeEvent evt) {
		onPropertyChanged(evt.getPropertyName(), evt.getOldValue(), evt
				.getNewValue());
		pcSupport.firePropertyChange(evt);
	}

	/**
	 * @param propertyName
	 * @param oldValue
	 * @param newValue
	 */
	abstract protected void onPropertyChanged(String propertyName,
			Object oldValue, Object newValue);

	public void setName(String newName) {
		super.setName(newName);
	}

	public void removePropertyChangeListener(PropertyChangeListener p) {
		pcSupport.removePropertyChangeListener(p);
	}

	public boolean equals(Object keyOrValue) {
		return valueEquals(keyOrValue);
	}

	/**
	 * @return the hookedFields
	 */
	public List<Field> getHookedFields() {
		return hookedFields;
	}

	/**
	 * @return the hookedMethods
	 */
	public List<Method> getHookedMethods() {
		return hookedMethods;
	}

	public Constructor[] getConstructors() {
		if (hookedConstructors == null)
			hookedConstructors = new LinkedList<Constructor>();
		return hookedConstructors.toArray(new Constructor[0]);
	}

	public Field[] getFields() {
		return hookedFields.toArray(new Field[0]);
	}

	public Method[] getJavaMethods() {
		return hookedMethods.toArray(new Method[0]);
	}

	public void importHandler(final InvocationHandler h, final Class[] cls) {
		importClasses(cls);
		importHandler(h);
	}

	public void importHandler(InvocationHandler h) {
		if (h == null || hookedHandlers.contains(h))
			return;
		hookedHandlers.add(h);
		// importObject(h.getClass(), h);
	}

	public void importClasses(Class[] cls) {
		for (final Class c : cls) {
			importClass(c);
		}
	}

	@SuppressWarnings("unchecked")
	public <C> Object coerceTo(Class<C> type) throws ClassCastException {
		importClass(type.getName());
		try {
			return (Serializable) (C) toRemote().coerceTo(type);
		} catch (RemoteException e) {
			e.printStackTrace();
		} catch (Throwable e) {
			e.printStackTrace();
		}
		return getValue();
	}

	public IScriptObjectThis getThis() {
		return (IScriptObjectThis) getThis(getInterpreter());
	}

	public ScriptableThis getThis(Interpreter declaringInterpreter) {
		if (!(thisReference instanceof ScriptableThis))
			thisReference = new ScriptableThis(this, this.getInterpreter());
		return (ScriptableThis) thisReference;
	}

	final public IScriptObjectRemote toRemote() {
		return (IScriptObjectRemote) getValue();
	}

	public IScriptObject setValue(IScriptObject value) {
		importObject(value);
		return this;
	}

	public Entry<Object, IScriptObject> toEntry() {
		// TODO Auto-generated method stub
		return new Map.Entry<Object, IScriptObject>() {
			public Object getKey() {
				// TODO Auto-generated method stub
				return ScriptNameSpace.this;
			}

			public IScriptObject getValue() {
				return ScriptNameSpace.this;
			}

			public IScriptObject setValue(IScriptObject value) {
				return ScriptNameSpace.this.setValue(value);
			}
		};
	}

}