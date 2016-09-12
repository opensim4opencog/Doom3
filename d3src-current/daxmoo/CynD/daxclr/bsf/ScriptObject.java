package daxclr.bsf;

import java.beans.BeanDescriptor;
import java.io.Serializable;
import java.lang.reflect.Array;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.lang.reflect.Proxy;
import java.lang.reflect.Type;
import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.Iterator;
import java.util.Map;

import javax.naming.NameAlreadyBoundException;

import org.apache.bsf.BSFException;
import org.opencyc.cycobject.CycFort;
import org.opencyc.cycobject.CycObject;
import org.opencyc.xml.XMLWriter;

import sun.reflect.FieldAccessor;
import sun.reflect.MethodAccessor;
import bsh.BshMethod;
import bsh.EvalError;
import bsh.Interpreter;
import bsh.NameSpace;
import bsh.This;
import bsh.UtilEvalError;

import com.sun.beans.ObjectHandler;

import daxclr.inference.CycAPI;
import daxclr.inference.NativeManager;

/**
 * @author Administrator
 * 
 */
class ScriptObject extends ScriptNameSpace implements IScriptObject,Remote
/* Entry<String, IScriptObject> */{


	/**
	 * 
	 */
	private static final long serialVersionUID = -138166643836289048L;
	/**
	 * 
	public Object readResolve() throws ObjectStreamException, RemoteException {
		return this;
	}

	private Object writeReplace() {
		return new ScriptObjectNameSpace_Stub(getName());
	}
	 */

	// public final transient ConsoleInterface console =
	// DoomConsoleChannel.getConsole();
	public Object immutable = null;

	/**
	 * Create a ScriptObject
	 * 
	 * @param parentNameSpace
	 * @param name
	 * 
	 * intitally this.getValue() will implement all methods defined on
	 * ScriptObject
	 * 
	 * each this.setValue(something) will make the getValue() Proxy for
	 * 'something'
	 * 
	 * the 'something' also will be internally mutated to call into 'this' for
	 * all of its methods properties
	 * @throws NameAlreadyBoundException
	 */

	/*
	 * public Serializable invoke(String cmd, Serializable[] params) { try {
	 * return getGameServer().invokeFunction(
	 * getGameServer().scriptNumber(getTypeName(), getPointer(), cmd), params); }
	 * catch (RemoteException e) { // TODO Auto-generated catch block
	 * e.printStackTrace(); throw new RuntimeException(e); } catch (Throwable e) {
	 * throw new RuntimeException(e); } }
	 */
	public String getTypeName() {
		Class clazz = getBeanClass();
		if (clazz == null)
			return "object";
		return clazz.getSimpleName();
	}

	public ScriptObject(NameSpace parentNameSpace, final String name) {
		super(null, parentNameSpace, name);
	}

	public IPropertySource importField(final Field info, final Object target) {
		final String fname = info.getName();
		Object oldValue;
		try {
			oldValue = getProperty(fname);
		} catch (NoSuchFieldException ex) {
			// TODO Auto-generated catch block
			oldValue = null;
		}
		System.out.println("hooking " + info);
		final IPropertySource hook = new ScriptedObjectField(target, info,
				fname);
		final Object newValue = hook.get(target);
		if (newValue != null) {
			updateSuperclassProperty(fname, oldValue, newValue);
		} else if (oldValue != null) {
			updateSuperclassProperty(fname, newValue, oldValue);
		}
		return hook;
	}

	/**
	 * @param propertyName
	 * @param oldValue
	 * @param newValue
	 */
	protected void onPropertyChanged(final String propertyName,
			final Object oldValue, final Object newValue) {
		try {
			setProperty(propertyName, newValue);
		} catch (final IllegalArgumentException ex) {
			traceTODO(ex);
		} catch (final IllegalAccessException ex) {
			traceTODO(ex);
		}
	}

	public ScriptingConstructorAccessor importConstructor(
			final Constructor info, final Class avatar) {
		return new ScriptingConstructorAccessor(avatar, info);
	}

	private static MethodAccessor getNativeMethodAccessor(final Method mf) {
		MethodAccessor methodAccessor = ScriptObject.reflectionFactory
				.getMethodAccessor(mf);
		while (methodAccessor instanceof ScriptingMethodAccessor) {
			final ScriptingMethodAccessor hook = (ScriptingMethodAccessor) methodAccessor;
			methodAccessor = hook.nativeAccessor;
		}
		if (methodAccessor == null)
			methodAccessor = ScriptObject.reflectionFactory
					.newMethodAccessor(mf);
		return methodAccessor;
	}

	private static boolean isRemoteMethod(Method root2,
			MethodAccessor nativeAccessor) {
		Class[] interfaces = root2.getDeclaringClass().getInterfaces();
		for (int i = 0; i < interfaces.length; i++) {
			if (interfaces[i].equals(Remote.class))
				return true;
		}
		return false;
	}

	// ReflectionFactory.getReflectionFactory().newMethod(getBeanClass(),name,ptypes,getPropertyType(propname),getPropertyExceptionTypes(propname),Modifier.PUBLIC,0,paramsToSignature());
	public class ScriptingMethodAccessor extends AbstractPropertySource
			implements IMultiSourceProperty {
		@Override
		public String getName() {
			// TODO Auto-generated method stub
			try {
				return this.getName() + "." + root.getName()+"("+root.getParameterTypes().length+")";
			} catch (Throwable e) {
				throw new RuntimeException(e);

			}
		}

		/**
		 * 
		 */
		private static final long serialVersionUID = -8981520654956610003L;

		/**
		 * @param previousHook
		 *            the parent to set
		 */
		final public void setParent(IMultiSourceProperty parent0) {
			this.previousHook = parent0;
		}

		// This is the next Accessor we must call
		private IMultiSourceProperty previousHook;

		/**
		 * @return the parent
		 */
		final public IMultiSourceProperty getParent() {
			return previousHook;
		}

		/**
		 * Matches invoke(obj,args) specification in
		 * {@link java.lang.reflect.Method}
		 * 
		 * but
		 * 
		 * @throws NoSuchMethodException
		 * @throws IllegalStateException
		 * @throws NullPointerException
		 * @throws ClassCastException
		 * @throws UnsupportedOperationException
		 * 
		 * @throws NoSuchFieldException,NoSuchMethodException
		 *             depending on missing Scripting
		 * @throws BSFException
		 */

		final public Object invokeImpl(Object src, final Object[] args)
				throws IllegalArgumentException, UnsupportedOperationException,
				ClassCastException, NullPointerException, IllegalStateException {
			Object value = null;
			Object pvalue = null;
			if (nativeAccessor != null)
				try {
					value = nativeAccessor.invoke(instance, args);
				} catch (InvocationTargetException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
			if (previousHook != null)
				pvalue = previousHook.invokeImpl(src, args);

			return value != null ? value : pvalue;

		}

		final public String aritySignature;

		final public MethodAccessor nativeAccessor;

		final public Method root;

		final public Object instance;

		/**
		 * This is a workarround to get the method initialized
		 * 
		 * @param mf
		 */
		public ScriptingMethodAccessor(final Object obj, final Method mf,
				final String prop) {
			super(ScriptObject.this, prop);
			instance = obj;
			root = mf;
			aritySignature = prop + root.getParameterTypes().length;
			previousHook = scriptingAccessors.get(aritySignature);

			nativeAccessor = getNativeMethodAccessor(root);
			if (!isRemoteMethod(root, nativeAccessor)) {
				ScriptObject.reflectionFactory.setMethodAccessor(root, this);
			}
			scriptingAccessors.put(aritySignature, this);
		}

		public void changeValue(IScriptObject src, Object old, Object value)
				throws NoSuchFieldException, UnsupportedOperationException,
				ClassCastException, IllegalArgumentException,
				NullPointerException, IllegalStateException {
			setValueImpl(instance, value);
			// } catch (InvocationTargetException e) {
			// throw new UnsupportedOperationException(toString() + e);

		}

		public void setValueImpl(Object src, Object value)
				throws UnsupportedOperationException, ClassCastException,
				IllegalArgumentException, NullPointerException,
				IllegalStateException {
			invokeImpl(src, new Object[] { value });
			// } catch (InvocationTargetException e) {

		}

		public Object getValueImpl(Object src) {
			try {
				return invokeImpl(src, new Object[0]);
			} catch (IllegalArgumentException e) {
				throw new IllegalArgumentException(toString() + e);
			} catch (UnsupportedOperationException e) {
				throw new IllegalArgumentException(toString() + e);
			} catch (ClassCastException e) {
				throw new IllegalArgumentException(toString() + e);
			} catch (NullPointerException e) {
				throw new IllegalArgumentException(toString() + e);
			} catch (IllegalStateException e) {
				throw new IllegalArgumentException(toString() + e);
			}
			// } catch (InvocationTargetException e) {
		}

		@Override
		public boolean isFinal() {
			return false;
		}

		@Override
		public String toString() {
			// TODO Auto-generated method stub
			return root.toString();
		}

		@Override
		public Field toField() {
			// TODO Auto-generated method stub
			return null;
		}

		@Override
		public Method toMethod() {
			// TODO Auto-generated method stub
			return root;
		}

		@Override
		public Method toSetMethod() {
			// TODO Auto-generated method stub
			return null;
		}
	}

	// public Field root = null;public Object cloneCache = null;
	public class ScriptedObjectField extends AbstractPropertySource implements
			IMultiSourceProperty {
		@Override
		public String getName() {
			// TODO Auto-generated method stub
			try {
				return this.getName() + "." + root.getName();
			} catch (Throwable e) {
				throw new RuntimeException(e);

			}
		}

		/**
		 * 
		 */
		private static final long serialVersionUID = 6611496630056110967L;

		public void changeValue(IScriptObject src, Object old, Object value)
				throws NoSuchFieldException, UnsupportedOperationException,
				ClassCastException, IllegalArgumentException,
				NullPointerException, IllegalStateException {
			try {
				setValueImpl(instance, value);
			} catch (IllegalAccessException e) {
				throw new UnsupportedOperationException(toString() + e);

			}
			// } catch (InvocationTargetException e) {
			// throw new UnsupportedOperationException(toString() + e);

		}

		// This 'original' is a child copy but indeed looks like the parent
		// should of
		// final public Field originalState;

		// This is a reference to the real field
		public final Field root;

		// This is the Field (root) nativeAccessor the real one called by us
		public FieldAccessor nativeAccessor;

		// Field modifiers before
		public int modifiers = -1;

		// Field modifiers before
		public Type type = null;

		// Did we need to bypass security
		public boolean override = false;

		final public Object instance;

		public ScriptedObjectField(final Object obj, final Field field0,
				final String prop) {
			super(ScriptObject.this, prop);
			root = field0;
			// type = field0.getGenericType();
			String before = "" + field0;
			modifiers = NativeManager.setFieldPublic(field0);
			try {

				// Check to see if it already overridden
				nativeAccessor = (FieldAccessor) NativeManager
						.getInstanceFieldValue(getRoot(),
								"overrideFieldAccessor");

				if (nativeAccessor != null) {
					System.err.println("overridden: " + before + " from "
							+ nativeAccessor);
					setOverride(true);
				} else {

				}
				// Call test to ensure root.fieldAccessor Loaded
				try {
					getRoot().get(obj);
				} catch (final IllegalArgumentException ex1) {
					traceTODO(ex1);
				} catch (final IllegalAccessException ex1) {
					// Set accessable
					setOverride(true);
					// actually override the true original
					field0.setAccessible(true);
				}
				if (isOverride()) {
					try {
						// try again in order to cache the overrideFieldAccessor
						getRoot().get(obj);
					} catch (final IllegalArgumentException ex) {
						traceTODO(ex);
					} catch (final IllegalAccessException ex) {
						traceTODO(ex);
					}
					// capture the new overrideFieldAccessor
					System.err
							.println("overriding: " + root + " was " + before);
					nativeAccessor = (FieldAccessor) NativeManager
							.getInstanceFieldValue(getRoot(),
									"overrideFieldAccessor");
				} else {
					nativeAccessor = (FieldAccessor) NativeManager
							.getInstanceFieldValue(getRoot(), "fieldAccessor");
				}
				putFieldAccessor(getRoot(), isOverride(), this);
			} catch (final SecurityException ex2) {
				traceTODO(ex2);
				trace("IPropertyEntry.SecurityException");
			} catch (final NoSuchFieldException ex2) {
				traceTODO(ex2);
				trace("IPropertyEntry.overrideFieldAccessor NoSuchFieldException");
			}
			setParent(scriptingAccessors.get(prop));
			scriptingAccessors.put(prop, this);
			instance = obj;
			putFieldAccessor(getRoot(), isOverride(), this);
		}

		public Object getValueImpl(final Object obj)
				throws IllegalArgumentException {
			if ((Modifier.isStatic(getRoot().getModifiers()))
					|| getRoot().getDeclaringClass().isInstance(obj))
				return getNativeAccessor().get(obj);
			if (getParent() != null)
				try {
					return getParent().getValueImpl(obj);
				} catch (UnsupportedOperationException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			return getNativeAccessor().get(instance);
		}

		public void setValueImpl(final Object obj, Object newValue)
				throws IllegalArgumentException, IllegalAccessException {
			if (getParent() != null)
				try {
					getParent().setValueImpl(obj, newValue);
				} catch (UnsupportedOperationException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (ClassCastException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (NullPointerException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (IllegalStateException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			if ((Modifier.isStatic(getRoot().getModifiers()))
					|| getRoot().getDeclaringClass().isInstance(obj))
				getNativeAccessor().set(obj, newValue);
			else
				getNativeAccessor().set(instance, newValue);
		}

		/**
		 * @return the root Field
		 */
		final public Field getRoot() {
			return root;
		}

		/**
		 * @param override
		 *            the override to set
		 */
		final public void setOverride(boolean override0) {
			this.override = override0;
		}

		/**
		 * @return the override
		 */
		final public boolean isOverride() {
			return override;
		}

		/**
		 * @param nativeAccessor
		 *            the fieldAccessor to set
		 */
		final public void setNativeAccessor(FieldAccessor fieldAccessor0) {
			while (fieldAccessor0 instanceof ScriptedObjectField)
				fieldAccessor0 = ((ScriptedObjectField) fieldAccessor0)
						.getNativeAccessor();
			this.nativeAccessor = fieldAccessor0;
		}

		/**
		 * @return the fieldAccessor
		 */
		public FieldAccessor getNativeAccessor() {
			return nativeAccessor;
		}

		/**
		 * @param previousHook
		 *            the parent to set
		 */
		final public void setParent(IMultiSourceProperty parent0) {
			this.parent = parent0;
		}

		// This is the next Accessor we must call
		private IMultiSourceProperty parent;

		/**
		 * @return the parent
		 */
		final public IMultiSourceProperty getParent() {
			return parent;
		}

		/**
		 * @param field
		 * @param override
		 * @param fieldAccessor
		 */
		public void putFieldAccessor(final Field field,
				final boolean override0, final FieldAccessor fieldAccessor) {
			try {
				if (override0) {
					NativeManager.setInstanceFieldValue(field,
							"overrideFieldAccessor", fieldAccessor);
					NativeManager.setInstanceFieldValue(field, "override",
							new Boolean(override));
					if (ScriptObject.catchSneakyFields)
						NativeManager.setInstanceFieldValue(field,
								"fieldAccessor", fieldAccessor);
				} else
					NativeManager.setInstanceFieldValue(field, "fieldAccessor",
							fieldAccessor);
			} catch (final NoSuchFieldException ex) {
				traceTODO(ex);
			}
		}

		public Object invokeImpl(Object source, Object[] params)
				throws UnsupportedOperationException, ClassCastException,
				IllegalArgumentException, NullPointerException,
				IllegalStateException {
			Object value = getValue();
			if (params != null && params.length > 0)
				setValue(params[params.length - 1]);
			return value;
		}

		@Override
		public boolean isFinal() {
			// TODO Auto-generated method stub
			return false;
		}

		@Override
		public String toString() {
			// TODO Auto-generated method stub
			return root.toGenericString();
		}

		@Override
		public Field toField() {
			// TODO Auto-generated method stub
			return root;
		}

		@Override
		public Method toMethod() {
			if (parent != null)
				return parent.toMethod();
			return null;
		}

		@Override
		public Method toSetMethod() {
			// TODO Auto-generated method stub
			return null;
		}
	}

	public static Method getMethod00(final Class typ, final String name,
			final Class[] ptypes) {
		Method ret = null;
		try {
			ret = typ.getMethod(name, ptypes);
			if (ret != null)
				return ret;
		} catch (final Exception e) {
		}
		/* final */final Method[] meth = typ.getMethods();
		for (final Method element : meth)
			if (element.getName().equalsIgnoreCase(name))
				if (ret == null)
					ret = element;
				else if (ret.getParameterTypes().length >= element
						.getParameterTypes().length)
					ret = element;
		return ret;
	}

	public static Object[] prependArray(final Object bot, final Object[] args) {
		if (args == null) {
			if (bot == null)
				return new Object[1];
			/* final */final Object[] r = (Object[]) Array.newInstance(bot
					.getClass(), 1);
			r[0] = bot;
			return r;
		}
		/* final */final int len = args.length;
		final Class clz = args.getClass().getComponentType();
		final Object[] toret = (Object[]) Array.newInstance(clz, len + 1);
		toret[0] = bot;
		for (int i = 0; i < args.length; i++)
			toret[i + 1] = args[i];
		return toret;
	}

	// public Map<String,Class> proptypes = new Hashtable<String,Class>(10);
	// public Hashtable<String,ArrayList<Class>> propexceptions = new
	// Hashtable<String,ArrayList<Class>>(10);
	// public Map<String,SetPropertyMethodAccessor> propsetterroot = new
	// Hashtable<String,SetPropertyMethodAccessor>(10);
	// public Map<String,GetPropertyMethodAccessor> propgetterroot = new
	// Hashtable<String,GetPropertyMethodAccessor>(10);
	// public Map<String,IPropertyEntry> properties = new
	// HashMap<String,IPropertyEntry>(10);
	// public ArrayList<Constructor> constructors = new
	// ArrayList<Constructor>(10);
	// public ArrayList<Method> methods = new ArrayList<Method>(10);
	// public ArrayList<Field> fields = new ArrayList<Field>(10);
	public static Object toArrayOf(final Class clz, final Object o) {
		int len = Array.getLength(o);
		final Object al = Array.newInstance(clz, len);
		while (len-- > 0)
			Array.set(al, len, Array.get(o, len));
		return al;
	}

	public static Class typeToObjectClass(final Class type) {
		if (type == null)
			return Object.class;
		return type.isPrimitive() ? ObjectHandler.typeNameToClass(type
				.getName()) : type;
	}

	// private Interpreter interpreter = new Interprete public void
	// addPropertyCode(final String propname, final String code) throws
	// bsh.EvalError {
	// throw new bsh.EvalError(code,null,null);
	/*
	 * public DynamicScriptObject(NameSpace definingNameSpace1, String name)
	 * throws TransformerException { super(definingNameSpace1, name); } public
	 * DynamicScriptObject(String name, Object value0) throws
	 * TransformerException{ this(name); if (value0 instanceof BSFDeclaredBean) {
	 * value0 = ((BSFDeclaredBean)value0).bean; }
	 * instanceNameSpace.importObject(value0); }
	 */
	public Serializable invokeMethod(final String methodName,
			final Object[] args) throws BSFException, NoSuchMethodException {
		String str = "" + getName() + "." + methodName + "("
				+ ConsoleChannel.joinString(args, " ") + ");";
		Class[] parameterTypes = NativeManager.getClasses(args);
		// TODO Auto-generated catch block
		try {
			return toObject(invokeMethodObject(methodName, args,
					parameterTypes, str));
		} catch (IllegalArgumentException e) {
			e.printStackTrace();
			throw new RuntimeException(e);
		} catch (IllegalAccessException e) {
			e.printStackTrace();
			throw new RuntimeException(e);
		} catch (InvocationTargetException e) {
			e.printStackTrace();
			throw new RuntimeException(e);
		} catch (InternalError ex) {
			str = str + " caused: InternalError " + ex;
			if (ScriptObject.runtimeErrors)
				throw new RuntimeException(str, new UnsatisfiedLinkError(
						methodName));
			if (ScriptObject.nsmErrors)
				throw new BSFException(BSFException.REASON_UNSUPPORTED_FEATURE,
						str, new NoSuchMethodException(str));
		} catch (final EvalError ex) {
			str = str + " caused: " + ex;
			if (ScriptObject.runtimeErrors)
				throw new RuntimeException(str, ex);
			if (ScriptObject.nsmErrors)
				throw new BSFException(BSFException.REASON_UNSUPPORTED_FEATURE,
						str, new NoSuchMethodException(str));
		} catch (final Throwable ex) {
			str = str + " caused: Throwable " + ex;
			if (ScriptObject.runtimeErrors)
				throw new RuntimeException(str, ex);
			if (ScriptObject.nsmErrors)
				throw new BSFException(BSFException.REASON_UNSUPPORTED_FEATURE,
						str, new NoSuchMethodException(str));
		}
		if (ScriptObject.nsmErrors)
			throw new BSFException(BSFException.REASON_UNSUPPORTED_FEATURE,
					str, new NoSuchMethodException(str));
		return getValue();

	}

	public Object invokeMethodObject(final String methodName,
			final Object[] args, Class[] parameterTypes, String str)
			throws EvalError, BSFException, NoSuchMethodException,
			IllegalArgumentException, RuntimeException, IllegalAccessException,
			InvocationTargetException {
		final BshMethod bshMethod;
		try {
			bshMethod = getMethod(methodName, parameterTypes);
			if (bshMethod != null)
				return bshMethod.invoke(args, getInterpreter());
		} catch (UtilEvalError ex) {
			ex.printStackTrace();
			// throw new BSFException(str + " " + ex);
		} catch (EvalError e) {
			e.printStackTrace();
			// throw new BSFException(str + " " + ex);
		} catch (Throwable e) {
			throw new RuntimeException(e);
			// throw new BSFException(str + " " + ex);
		}
		return this.getClass().getMethod(methodName, parameterTypes).invoke(
				this, args);
	}

	static public Serializable toObject(Object object) {
		// TODO Auto-generated method stub
		return ObjectRepository.resolverMap.toObject(object);
	}

	/*
	 * public boolean addPropertyType( String propname,Class proptype) { if
	 * (propname==null) return false; this.addProperty(propname); if
	 * (proptype==null) return this.modified; Class old =
	 * this.proptypes.get(propname); if (old==null) {
	 * this.proptypes.put(propname,proptype); return true; } return false; }
	 */
	// public void subclassPostConstruct(){
	// this.getInterface(getInterfaces());
	// / }
	public boolean coerceableTo(final Class<?> target) {
		if (target == null)
			return true;
		try {
			this.coerceTo(target);
		} catch (final ClassCastException ex) {
			return false;
		}
		return true;// target.isInstance(getValue());
	}

	// public Object h2;
	public <C> Object coerceTo(final Class<C> target) throws ClassCastException {
		final Serializable o = getValue();
		if (target != null) {
			importClass(target);
			try {
				getThis().getInterface(target);
			} catch (RemoteException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				throw new RuntimeException(e);
				} catch (Throwable e) {
				throw new RuntimeException(e);
				
			}
			if (target.isInstance(o))
				return o;
			if (target.isAssignableFrom(String.class))
				return getName();
			if (target.isAssignableFrom(CycObject.class))
				return getCycObject();
			if (target.isInstance(getValue()) || instanceOf(target))
				return getValue();
			if (target.isInstance(this))
				return this;
			return getValue();
		}
		throw new ClassCastException(getName() + " wont become " + target);
	}

	public boolean instanceOf(final Class<?> target) {
		final Class[] classes = getClasses();
		for (final Class element : classes)
			if (target.isAssignableFrom(element))
				return true;
		return false;
	}

	public BeanDescriptor getBeanDescriptor() {// throws
		// java.beans.IntrospectionException
		// {
		final ScriptBeanInfo beanInfo = new ScriptBeanInfo(this);
		final Class[] classes = getClasses();
		if (classes != null)
			for (final Class element : classes)
				beanInfo.addBeanClass(element);
		return beanInfo;
	}

	/*
	 * //public void addNameSourceListener( Listener listener) { // TODO
	 * Auto-generated method stub //
	 * this.objectNameSpace.addNameSourceListener(listener); // }
	 * 
	 * //public abstract Object invokeScript(String mn,Object[] args) throws
	 * NoSuchMethodException; class DeclaredHandler implements InvocationHandler {
	 * public Object invoke(Object proxy,Method method,Object[] params) throws
	 * Throwable { String propname = method.getName(); String message =
	 * "DeclaredHandler.invoke " + getName() + "." + propname + "(" +
	 * ScriptManager.joinString(params,",") + ")"; if
	 * (propname.equals("equals")) { return equals(params[0]); } else if
	 * (propname.equals("toString")) { return ObjectInfo.this.toString(); } else
	 * if (propname.equals("getClasses")) { return getClasses(); } else if
	 * (propname.equals("getConstructors")) { return getConstructors(); } else
	 * if (propname.equals("getMethods")) { return getMethods(); } else if
	 * (propname.equals("getFields")) { return getFields(); } else if
	 * (propname.equals("hashCode")) { return hashCode(); } else if
	 * (propname.equals("toObjectInfo")) { return toObjectInfo(); } else if
	 * (propname.equals("getBeanClass")) { return getBeanClass(); } else if
	 * (propname.equals("getThis")) { return objectNameSpace.getThis(); } else
	 * if (propname.equals("getValue")) { return getValue(); } else throw new
	 * NoSuchMethodException(message); } }
	 * 
	 * class DoomScriptHandler implements InvocationHandlerChain.Always { public
	 * Object invoke(Object proxy,Method method,Object[] params) throws
	 * Throwable { return invokeScript(method.getName(),params); } }
	 * 
	 * class ObjectInfoLive implements InvocationHandler { public Object invoke(
	 * Object proxy,Method method,Object[] args) throws Throwable { Class[]
	 * paramTypes = IdDeclManager.getClasses(args); try {
	 * ScriptManager.convertTypeArray(args,paramTypes,args,method.getParameterTypes()); }
	 * catch ( Throwable ex ) { debugln(ex); debugln("ObjectInfo
	 * convertTypeArray"); } return
	 * invoke(proxy,method.getName(),args,paramTypes,(Class<?>)method.getReturnType()); }
	 * public Object invoke( Object proxy,String propname,Object[] params)
	 * throws Throwable { return
	 * invoke(proxy,propname,params,IdDeclManager.getClasses(params),(Class<?>)Object.class); }
	 * public Object invoke(Object proxy, String propname, Object[] params,
	 * Class[] paramTypes, Class<?> rettype) throws Throwable { String message =
	 * "DeclaredHandler.invoke " + getName() + "." + propname + "(" +
	 * ScriptManager.joinString(params,",") + ")"; Throwable resulterr = null;
	 * Throwable missing = null; if (rettype==null||rettype==Object.class) { try {
	 * rettype = getPropertyType(propname); } catch ( NoSuchFieldException ex ) { } }
	 * try { return
	 * this.getClass().getMethod(propname,paramTypes).invoke(this,params); }
	 * catch (NoSuchMethodException ex) { missing = ex; } catch
	 * (InvocationTargetException ex) { missing = ex; } catch
	 * (IllegalAccessException ex) { missing = ex; } catch
	 * (IllegalArgumentException ex) { missing = ex; } catch (Throwable e) {
	 * resulterr = e; } Object target = theValue; Class oclass =
	 * target.getClass(); try {
	 * return(ScriptManager.invokeSomething(oclass,target,propname,params)); }
	 * catch (NoSuchMethodException ex) { missing = ex; } catch (Throwable ex) {
	 * resulterr = ex; } try { if (paramTypes.length<1) {
	 * return(oclass.getField(propname).get(target)); } else { Object res
	 * =(oclass.getField(propname).get(target));
	 * oclass.getField(propname).set(target,params[params.length-1]); return
	 * res; } } catch (NoSuchFieldError ex) { missing = ex; } catch
	 * (NoSuchFieldException ex) { missing = ex; } catch (IllegalAccessException
	 * ex) { missing = ex; } catch (Throwable ex) { resulterr = ex; } if
	 * (resulterr!=null) throw new RuntimeException(message,resulterr); throw
	 * new NoSuchMethodException(message); } }
	 */

	// public PropertyListenerMap getExternalMap() {
	// return propertyListenerMap;
	// }
	public Object getFirstValue(final String[] propname)
			throws NoSuchFieldException {
		final String message = "ObjectInfo getFirstPropertyValue "
				+ getBeanClass() + "." + propname[0];
		Throwable resulterr = null;
		for (final String element : propname)
			try {
				return getProperty(element);
			} catch (final NoSuchFieldException ex) {
			} catch (final Throwable ex) {
				resulterr = ex;
			}
		if (resulterr != null)
			throw new RuntimeException(message, resulterr);
		throw new NoSuchFieldException(message);
	}

	public CycFort getFunctor() {
		return CycAPI.c("ObjectIDFn");
	}

	/**
	 * @param key
	 * @return
	 */
	public Object get(final Object key) {
		try {
			return getProperty(keyToProperty(key));
		} catch (final NoSuchFieldException ex) {
			return null;
		}
	}

	public Class getPropertyType(final String propname)
			throws NoSuchFieldException {
		final Object prop = getProperty(propname);
		if (prop == null)
			return Object.class;
		return prop.getClass();
	}

	public java.util.List getReferencedConstants() {
		return getCycObject().getReferencedConstants();
	}

	@Override
	public int hashCode() {
		return getName().hashCode();
	}

	public boolean isExtendable() {
		return getBeanClass().isInterface();
	}

	final public boolean isImmutable() {
		return immutable != null;
	}

	public boolean isMapSpecific() {
		return true;
	}

	public boolean isReadOnly(final String propname)
			throws NoSuchFieldException {
		final Class[] types = new Class[] { getPropertyType(propname) };
		try {
			if (getMethod("set" + ScriptObject.capitalize(propname), types) != null)
				return false;
		} catch (UtilEvalError e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		try {
			if (getMethod("is" + ScriptObject.capitalize(propname), types) != null)
				return false;
		} catch (UtilEvalError e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		try {
			if (getMethod(propname, types) != null)
				return false;
		} catch (UtilEvalError e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return true;
	}

	public IScriptObject newInstance(String newName)
			throws InstantiationException {
		if (newName == null || newName.equals(getName()))
			newName = newName + "_extended";
		IScriptObject moi = null;
		moi = new ScriptObject(this, newName);
		ObjectRepository.set(newName, moi);
		return moi; // .getValue();
	}

	public void run() {
		getInterpreter().run();
	}

	public void setImmutable(final boolean tf) {
		if (tf) {
			if (immutable == null)
				immutable = getValue();
		} else if (immutable != null) {
			importObject(immutable);
			immutable = null;
		}
	}

	public Object put(final Object key, final Object value) {
		final Object old = get(key);
		try {
			setProperty(keyToProperty(key), value);
		} catch (final IllegalArgumentException ex) {
			traceTODO(ex);
		} catch (final IllegalAccessException ex) {
			traceTODO(ex);
		}
		return old;
	}

	/**
	 * @param key
	 * @return
	 */
	private String keyToProperty(final Object key) {
		return "" + key;
	}

	// public ObjectNameSpace getNameSpace() {
	// return objectNameSpace;
	// }
	public void setVar(final String name, final Class type, final Object value) {
		try {
			getParent().setTypedVariable(name, type, value,
					ScriptObject.modifiers);
		} catch (final Exception ex) {
			traceTODO(ex);
		}
	}

	public String stringApiValue() {
		return getCycObject().stringApiValue();
	}

	public IScriptObject toObjectInfo() {
		return this;
	}

	public void toXML(final XMLWriter xmlWriter, final int indent,
			final boolean relative) throws java.io.IOException {
		getCycObject().toXML(xmlWriter, indent, relative);
	}

	// abstract public boolean equals(final Object obj);
	final public boolean valueEquals(final Object target) {
		if (target == null)
			return getName().equals("null");
		if (target == this)
			return true;
		if (target == getValue())
			return true;
		if (target == getInvocationHandler())
			return true;
		final String tname = "" + target;
		if (getName().equals(tname))
			return true;
		if (immutable != null && tname.equals("" + immutable))
			return true;
		if (target instanceof CharSequence)
			return tname.equals(getName());
		if (target instanceof CycObject && getCycObject().equals(target))
			return true;
		if (target instanceof Map.Entry) {
			if (valueEquals(((Map.Entry) target).getKey()))
				return true;
		}
		if (target instanceof NameSpace) {
			if (valueEquals(((NameSpace) target).getName()))
				return true;
		}
		if (target instanceof Interpreter) {
			if (target == getInterpreter())
				return true;
			if (valueEquals(((Interpreter) target).getNameSpace()))
				return true;
		}
		if (target instanceof This) {
			if (valueEquals(((This) target).getNameSpace()))
				return true;
		}
		final Iterator vs = getImportedObjects().iterator();
		while (vs.hasNext())
			if (vs.next() == target)
				return true;
		if (Proxy.isProxyClass(target.getClass()))
			try {
				if (valueEquals(Proxy.getInvocationHandler(target)))
					return true;
			} catch (final IllegalArgumentException iae) {
			}
		return false;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see daxclr.java.ScriptObject#getMethodHook(java.lang.reflect.Method,
	 *      java.lang.Object)
	 */
	@Override
	public IPropertySource importMethod(Method element, Object target) {
		// TODO Auto-generated method stub
		return new ScriptingMethodAccessor(target, element, element.getName());
	}
}
