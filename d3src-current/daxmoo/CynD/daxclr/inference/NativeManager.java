package daxclr.inference;

import java.io.InvalidClassException;
import java.lang.reflect.AccessibleObject;
import java.lang.reflect.Array;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.lang.reflect.Proxy;
import java.security.AccessController;
import java.security.PrivilegedAction;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;


import sun.misc.Unsafe;
import daxclr.bsf.ConsoleChannel;
import daxclr.bsf.IScriptObject;
import daxclr.bsf.ObjectRepository;

/**
 * @author Administrator
 * 
 */
public class NativeManager {
	final static public Class[] stringClassArrayOfOne = new Class[] { String.class };

	public static Unsafe unsafe = getUnsafe();

	/**
	 * Class with these modifiers are skipped while searching the JVM classes.
	 */
	public NativeManager() {
		super();
	}

	/**
	 * 
	 * @param object
	 * @param methodName
	 * @param argClasses
	 * @throws java.lang.Throwable
	 * @return
	 */
	public static final Method getMethodForObject(final Object object,
			final String methodName, final Class[] argClasses)
			throws NoSuchMethodException {
		try {
			return ObjectRepository.resolverMap.toClass(object).getMethod(methodName,
					argClasses);
		} catch (SecurityException e) {
			throw new NoSuchMethodException(methodName
					+ " because security for: " + object + " " + e);
		} catch (ClassNotFoundException e) {
			throw new NoSuchMethodException(methodName
					+ " because Class is indeterminate for: " + object + " "
					+ e);
		}
	}

	static final void clearException() {
		try {
			NativeManager.exceptionClear();
		} catch (final UnsatisfiedLinkError le) {
		}
	}

	/**
	 * 
	 * @param innerClass
	 * @param objs
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public static final Collection createCollection(final Class<?> innerClass,
			final Object[] objs) {
		try {
			Collection<Object> newInstance = (Collection<Object>) innerClass.newInstance();
			final Collection<Object> col = newInstance;
			for (final Object element : objs)
				col.add(element);
			return col;
		} catch (final Throwable e) {
			ConsoleChannel.debug(e);
			return null;
		}
	}

	/**
	 * 
	 * @return
	 * @param className
	 * @param params
	 */
	public static final Object createObject(final String className,
			final Object[] params) {
		try {
			return ObjectRepository.resolverMap.toObject(
					NativeManager.newObject(
							ObjectRepository.resolverMap.toClass(className), params));
		} catch (final Throwable e) {
			return NativeManager.makeError(e);
		}
	}

	/**
	 * Executes the given method now
	 */
	public static final Object executeMethod(final Method method,
			final Object bean, final Object[] params) throws Exception {
		Object returnValue = null;
		if (method != null) {
			ConsoleChannel.debug("Invoking " + method + " on ("
					+ bean.getClass().getName() + ")" + bean + " with args ("
					+ ConsoleChannel.joinString(params, ",") + ")");
			InvocationHandler handler = null;
			try {
				handler = Proxy.getInvocationHandler(bean);
			} catch (final IllegalArgumentException notproxy) {
			}
			if (handler == null)
				returnValue = method.invoke(bean, params);
			else
				try {
					returnValue = handler.invoke(bean, method, params);
				} catch (final Exception ex) {
					// TODO Auto-generated catch block
					throw ex;
				} catch (final Throwable ex) {
					ConsoleChannel.debug(ex);
					// TODO Auto-generated catch block
					throw new RuntimeException("" + ex);
				}
		}
		return returnValue;
	}

	/**
	 * 
	 * @param to
	 * @param len
	 * @param objs
	 * @param from
	 * @param callArray
	 * @return
	 */
	public static final boolean fillArrayType(final Class to, final int len,
			final Object[] objs, final Class[] from, final Object callArray) {
		for (int i = 0; i < len; i++)
			if (!NativeManager.convertType(objs[i], from[i], callArray, i, to))
				return false;
		return true;
	}

	/**
	 * 
	 * @return
	 * @param object
	 * @param methodName
	 * @throws java.lang.Throwable
	 */
	public static final Field getFieldForObject(final Object object,
			final String methodName) throws Throwable {
		return ObjectRepository.resolverMap.toClass(object).getField(methodName);
	}

	/**
	 * 
	 * @return
	 * @param innerInstance
	 * @param fieldName
	 */
	public static final Object getObjectField(final Object target,
			final String fieldName) throws IllegalArgumentException,
			IllegalAccessException, NoSuchFieldException {
		return ((IScriptObject) ObjectRepository.resolverMap.toObject(target))
				.getProperty(fieldName);
	}

	/**
	 * 
	 * @param innerInstance
	 * @param methodName
	 * @param args
	 * @throws java.lang.Throwable
	 * @return
	 */
	public static final Object invokeObject(final Object innerInstance,
			final String methodName, final Object[] args) throws Throwable {
		// Get/Set Fields
		if (methodName.startsWith("field")) {
			Field innerField;
			if (methodName.charAt(7) == 's') {
				innerField = NativeManager.getFieldForObject(innerInstance,
						(String) args[0]);
				innerField.set(innerInstance, args[1]);
				return innerField.get(innerInstance);
			}
			if (methodName.charAt(7) == 'g') {
				innerField = NativeManager.getFieldForObject(innerInstance,
						(String) args[0]);
				return innerField.get(innerInstance);
			}
		}
		// Invokes Methods
		final Class[] classes = getClasses(args);
		final String message = "getMethodForObject " + innerInstance + "("
				+ methodName + ","
				+ ConsoleChannel.joinString(classes, ",", 0, -1) + ")";
		ConsoleChannel.debug(message);
		final Method m = NativeManager.getMethodForObject(innerInstance,
				methodName, classes);
		if (m == null)
			throw new NoSuchMethodException(message);
		if (NativeManager.convertTypeArray(args, classes, args, m
				.getParameterTypes()))
			return m.invoke(innerInstance, args);
		throw new NoSuchMethodException("not convertable " + message);
	}

	/**
	 * 
	 * @return
	 * @param innerClass
	 * @param methodName
	 * @param args
	 */
	public static final Object invokeStatic(final Class innerClass,
			final String methodName, final Object[] args)
			throws NoSuchMethodException, IllegalAccessException,
			InvocationTargetException {
		return NativeManager
				.invokeSomething(innerClass, null, methodName, args);
	}

	/**
	 * 
	 * @param hmap
	 * @return
	 */
	public static final LinkedHashSet<String> keysOfMap(
			final java.util.Dictionary<String, Object> hmap) {
		final Enumeration<String> list = hmap.keys();
		final LinkedHashSet<String> keyset = new LinkedHashSet<String>();
		while (list.hasMoreElements())
			keyset.add(list.nextElement());
		return keyset;
	}

	/**
	 * 
	 * @param theType
	 * @param theData
	 * @throws java.lang.Throwable
	 * @return
	 */
	public static final Object makeInstanceFromClass(final String theType,
			final String theData) throws Throwable {
		final Class newClass = ObjectRepository.resolverMap.toClass(theType);
		try {
			return newClass.getConstructor(stringClassArrayOfOne).newInstance(
					new Object[] { theData });
		} catch (final Throwable e) {
			return newClass.newInstance();
		}
	}

	/**
	 * 
	 * @param arg
	 * @return
	 */
	public static final Object mktype(final String arg) {
		int comma = arg.indexOf(',');
		try {
			return NativeManager.makeInstanceFromClass(arg
					.substring(5, comma++), arg.substring(comma,
					arg.length() - 1));
		} catch (final Throwable e) {
			return NativeManager.makeError(e);
		}
	}

	/**
	 * 
	 * @param theType
	 * @param theData
	 * @throws java.lang.Throwable
	 * @return
	 */
	public static final Object mktype(final String theType, final String theData)
			throws Throwable {
		if (theType.equals("Long"))
			try {
				return new java.lang.Long(theData);
			} catch (final Throwable e) {
				debug(e);
				return new java.lang.Long(0);
			}
		if (theType.equals("Integer"))
			try {
				return new java.lang.Integer(theData);
			} catch (final Throwable e) {
				debug(e);
				return new java.lang.Integer(0);
			}
		if (theType.equals("Short"))
			try {
				return new Short(theData);
			} catch (final Throwable e) {
				debug(e);
				return new Short((short) 0);
			}
		if (theType.equals("Float"))
			try {
				return new java.lang.Float(theData);
			} catch (final Throwable e) {
				debug(e);
				return new java.lang.Float(0);
			}
		if (theType.equals("Byte"))
			try {
				return new Byte(theData);
			} catch (final Throwable e) {
				debug(e);
				return new Byte((byte) 0);
			}
		if (theType.equals("Byte"))
			try {
				return new Byte(theData);
			} catch (final Throwable e) {
				debug(e);
				return new Byte((byte) 0);
			}
		if (theType.equals("Boolean"))
			try {
				return new Boolean(theData);
			} catch (final Throwable e) {
				debug(e);
				return new Boolean(false);
			}
		if (theType.equals("Char"))
			try {
				return new Character(theData.charAt(0));
			} catch (final Throwable e) {
				debug(e);
				return new Character('\0');
			}
		if (theType.equals("Class"))
			try {
				return ObjectRepository.resolverMap.toClass(theData);
			} catch (final Throwable e) {
				debug(e);
				return ObjectRepository.resolverMap.toClass("Object");
			}
		if (theType.equals("String"))
			return theData;
		// if (theType.equals("Date")) return new Date(theData);
		return NativeManager.makeInstanceFromClass(theType, theData);
	}

	private static void debug(Throwable e) {
		ConsoleChannel.debug(e);

	}

	/**
	 * 
	 * @return
	 * @param innerClass
	 * @param args
	 * @throws java.lang.Throwable
	 */
	public static final Object newObject(final Class innerClass,
			final Object[] args) throws Throwable {
		try {
			if (args == null || args.length == 0)
				return innerClass.newInstance();
			final Class[] clasparams = getClasses(args);
			final Constructor[] methods = innerClass.getConstructors();
			final Object calling = new Object[args.length];
			for (final Constructor element : methods) {
				final Class[] methodParms = element.getParameterTypes();
				if (clasparams.length == methodParms.length)
					try {
						if (NativeManager.convertTypeArray(args, clasparams,
								calling, methodParms))
							return element.newInstance((Object[]) calling);
					} catch (final IllegalArgumentException iae) {
					}
			}
		} catch (final Throwable e) {
			throw new Throwable("" + innerClass + " "
					+ ConsoleChannel.listToStringDebug(getClasses(args)) + " "
					+ ConsoleChannel.listToStringDebug(args) + " " + e);
		}
		throw new InvalidClassException("" + innerClass + " "
				+ ConsoleChannel.listToStringDebug(getClasses(args)) + " "
				+ ConsoleChannel.listToStringDebug(args));
	}

	/**
	 * 
	 * @param target
	 * @param name
	 * @param value
	 * @throws java.lang.Throwable
	 */
	public static final void setFieldObject(final Object target,
			final String name, final Object value)
			throws IllegalArgumentException, IllegalAccessException,
			NoSuchFieldException {
		final IScriptObject scriptObject = (IScriptObject) ObjectRepository.resolverMap.toObject(target);
		try {
			scriptObject.setProperty(name, value);
		} catch (final Throwable e) {
			throw new IllegalAccessException("setFieldObject " + e);
		}
	}

	/**
	 * 
	 * @return
	 * @param innerInstance
	 * @param fieldName
	 * @param prolog_value
	 */
	public static final String setObjectField(final Object innerInstance,
			final String fieldName, final Object prolog_value) {
		try {
			ObjectRepository.resolverMap.toClass(innerInstance).getField(fieldName).set(
					innerInstance, prolog_value);
			return "true";
		} catch (final Throwable e) {
			return NativeManager.makeError(e);
		}
	}

	/**
	 * Bypass Security
	 * 
	 * The CallNonvirtual<type>Method families of routines and the Call<type>Method
	 * families of routines are different. Call<type>Method routines invoke the
	 * method based on the class of the object, while CallNonvirtual<type>Method
	 * routines invoke the method based on the class, designated by the clazz
	 * parameter, from which the method ID is obtained. The method ID must be
	 * obtained from the real class of the object or from one of its
	 * superclasses.
	 */
	final public static native Object callNonvirtualObjectMethod(Class clazz,
			Object obj, Method method, Object[] params) throws Throwable;

	/**
	 * Bypass Security
	 * 
	 * The CallNonvirtual<type>Method families of routines and the Call<type>Method
	 * families of routines are different. Call<type>Method routines invoke the
	 * method based on the class of the void, while CallNonvirtual<type>Method
	 * routines invoke the method based on the class, designated by the clazz
	 * parameter, from which the method ID is obtained. The method ID must be
	 * obtained from the real class of the void or from one of its superclasses.
	 */
	final public static native void callNonvirtualVoidMethod(Class clazz,
			Object obj, Method method, Object[] params) throws Throwable;

	/**
	 * Bypass Security
	 */
	final public static native Object callObjectMethod(Object obj,
			Method method, Object[] params) throws Throwable;

	/**
	 * Bypass Security
	 */
	final public static native Object callStaticObjectMethod(Class clazz,
			Method method, Object[] params) throws Throwable;

	/**
	 * Bypass Security
	 */
	final public static native void callStaticVoidMethod(Class clazz,
			Method method, Object[] params) throws Throwable;

	/**
	 * Bypass Security
	 */
	final public static native void callVoidMethod(Object obj, Method method,
			Object[] params) throws Throwable;

	final public static native void exceptionClear();

	/**
	 * Bypass Security
	 */
	final public static native Object constructObject(Class clazz,
			Object[] params) throws Throwable;

	final public static native boolean getFieldAsBoolean(Class clazz,
			Object target, String fieldName) throws NoSuchFieldException;

	final public static native byte getFieldAsByte(Class clazz, Object target,
			String fieldName) throws NoSuchFieldException;

	final public static native char getFieldAsChar(Class clazz, Object target,
			String fieldName) throws NoSuchFieldException;

	final public static native double getFieldAsDouble(Class clazz,
			Object target, String fieldName) throws NoSuchFieldException;

	final public static native float getFieldAsFloat(Class clazz,
			Object target, String fieldName) throws NoSuchFieldException;

	final public static native int getFieldAsInt(Class clazz, Object target,
			String fieldName) throws NoSuchFieldException;

	final public static native long getFieldAsLong(Class clazz, Object target,
			String fieldName) throws NoSuchFieldException;

	final public static native Object getFieldAsObject(Class clazz,
			Object target, String fieldName, String sig)
			throws NoSuchFieldException;

	final public static native short getFieldAsShort(Class clazz,
			Object target, String fieldName) throws NoSuchFieldException;

	final public static native void setFieldAsBoolean(Class clazz,
			Object target, String fieldName, boolean val)
			throws NoSuchFieldException;

	final public static native void setFieldAsByte(Class clazz, Object target,
			String fieldName, byte val) throws NoSuchFieldException;

	final public static native void setFieldAsChar(Class clazz, Object target,
			String fieldName, char val) throws NoSuchFieldException;

	final public static native void setFieldAsDouble(Class clazz,
			Object target, String fieldName, double val)
			throws NoSuchFieldException;

	final public static native void setFieldAsFloat(Class clazz, Object target,
			String fieldName, float val) throws NoSuchFieldException;

	final public static native void setFieldAsInt(Class clazz, Object target,
			String fieldName, int val) throws NoSuchFieldException;

	final public static native void setFieldAsLong(Class clazz, Object target,
			String fieldName, long val) throws NoSuchFieldException;

	final public static native void setFieldAsObject(Class clazz,
			Object target, String fieldName, String sig, Object val)
			throws NoSuchFieldException;

	final public static native void setFieldAsShort(Class clazz, Object target,
			String fieldName, short val) throws NoSuchFieldException;

	/**
	 * 
	 * @param e
	 * @return
	 */
	static final String makeError(final Throwable e) {
		return "error('" + e + "')";
	}

	final static public void error(final String ln, final Throwable err) {
		ConsoleChannel.debug(err);
		error(ln);
	}

	final static public void error(final String ln) {
		ConsoleChannel.debug("ERROR: " + ln);
	}

	/**
	 * @return Object from C/C++ jobject AllocObject(jclass clazz)
	 */
	final static public native Object allocateObject(Class clazz);

	/**
	 * 
	 * @return
	 * @param objs
	 * @param len
	 */
	static public Class[] getClasses(Object[] objs, int len) {
		Class[] toReturnClasses = new Class[len];
		for (int i = 0; i < len; i++) {
			if (objs[i] != null) {
				if (objs[i] instanceof IScriptObject) {
					toReturnClasses[i] = ((IScriptObject) objs[i])
							.getBeanClass();
				}
				if (toReturnClasses[i] == null) {
					toReturnClasses[i] = objs[i].getClass();
				}
			} else {
				toReturnClasses[i] = Object.class;
				// toReturnClasses[i] = null;
			}
		}
		return toReturnClasses;
	}

	/**
	 * 
	 * @return
	 * @param objs
	 */
	static public Class[] getClasses(Object[] objs) {
		if (objs == null)
			return new Class[0];
		return getClasses(objs, objs.length);
	}

	/**
	 * @param obj
	 * @param fieldName
	 * @param value
	 * @throws NoSuchFieldException
	 */
	static public void setInstanceFieldValue(Object obj, String fieldName,
			Object value) throws NoSuchFieldException {
		final Unsafe unsafe = NativeManager.getUnsafe();
		unsafe.putObject(obj, unsafe.objectFieldOffset(findField(
				obj.getClass(), fieldName)), value);
	}

	static public void setInstanceFieldValue(Object obj, String fieldName,
			int value) throws NoSuchFieldException {
		final Unsafe unsafe = NativeManager.getUnsafe();
		unsafe.putInt(obj, unsafe.objectFieldOffset(findField(obj.getClass(),
				fieldName)), value);
	}

	/**
	 * @param fieldClass
	 * @param fieldName
	 * @throws NoSuchFieldException
	 */
	static public Field findField(Class<?> fieldClass, String fieldName)
			throws NoSuchFieldException {
		try {
			return fieldClass.getDeclaredField(fieldName);
		} catch (NoSuchFieldException ex) {
			Class<?> superClass = fieldClass.getSuperclass();
			if (superClass != null)
				return findField(superClass, fieldName);
			throw ex;
		}
	}

	static public int setFieldPublic(Field field0) {
	//	String before = "" + field0;
		int newModifers = field0.getModifiers();
		int modifiers = newModifers;
		if (Modifier.isPrivate(newModifers))
			newModifers = newModifers - Modifier.PRIVATE;
		if (Modifier.isProtected(newModifers))
			newModifers = newModifers - Modifier.PROTECTED;
		if (Modifier.isFinal(newModifers))
			newModifers = newModifers - Modifier.FINAL;
		if (Modifier.isNative(newModifers))
			newModifers = newModifers - Modifier.NATIVE;
		if (!Modifier.isPublic(newModifers))
			newModifers = newModifers + Modifier.PUBLIC;
		if (newModifers != modifiers) {
			try {
				NativeManager.setInstanceFieldValue(field0, "modifiers",
						newModifers);
			} catch (NoSuchFieldException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			//System.err.println("modifiers: " + field0 + " was " + before);
		}
		return modifiers;
	}

	@SuppressWarnings("unchecked")
	public
	static void setAllAccessable(final Class<?> type) {
		if (type != null) {
			AccessController.doPrivileged(new PrivilegedAction() {
				public Object run() {
					Field[] fields = type.getDeclaredFields();
					for (final Field element : fields)
						setFieldPublic(element);
					try {
						AccessibleObject.setAccessible(type
								.getDeclaredConstructors(), true);
					} catch (SecurityException se) {
						if (type != Class.class)
							se.printStackTrace(System.err);
					}
					try {
						AccessibleObject.setAccessible(
								type.getDeclaredFields(), true);
					} catch (SecurityException se) {
						se.printStackTrace(System.err);
					}
					try {
						AccessibleObject.setAccessible(type
								.getDeclaredMethods(), true);
					} catch (SecurityException se) {
						se.printStackTrace(System.err);
					}
					return type;
				}
			});
			setAllAccessable(type.getSuperclass());
		}
	}

	/**
	 * 
	 * @param objs
	 * @param from
	 * @param callArray
	 * @param to
	 * @return
	 */
	public final static/* static */boolean convertTypeArray(
			final Object[] objs, final Class[] from, final Object callArray,
			final Class[] to) {
		for (int i = 0; i < to.length; i++)
			if (!NativeManager.convertType(objs[i], from[i], callArray, i,
					to[i]))
				return false;
		return true;
	}

	/**
	 * 
	 * @param object
	 * @param from
	 * @param callArray
	 * @param arg
	 * @param to
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public final static/* static */boolean convertType(final Object object,
			final Class<?> from, final Object callArray, final int arg,
			Class<?> to) {
		if (to.isAssignableFrom(from)) {
			Array.set(callArray, arg, object);
			return true;
		}
		if (to == Number.class)
			to = Float.class;
		else if (to == Map.class)
			to = HashMap.class;
		else if (to == Set.class)
			to = LinkedHashSet.class;
		else if (to == List.class)
			to = ArrayList.class;
		else if (to == Collection.class)
			to = ArrayList.class;
		if (to.isArray()) {
			if (to == char[].class)
				if (object instanceof String) {
					Array.set(callArray, arg, ((String) object).toCharArray());
					return true;
				}
			if (!from.isArray())
				return false;
			final Object[] objA = (Object[]) object;
			final Object subCallArray = Array.newInstance(to, objA.length);
			if (!fillArrayType(to.getComponentType(), objA.length, objA,
					getClasses(objA), subCallArray))
				return false;
			Array.set(callArray, arg, subCallArray);
			return true;
		}
		if (!to.isPrimitive()) {
			try {
				final Class[] fromA = { from };
				// fromA[0]=from;
				final Constructor cons = to.getConstructor(fromA);
				final Object[] objA = { object };
				Array.set(callArray, arg, cons.newInstance(objA));
				return true;
			} catch (final Throwable e) {
			}
			// Object to
			if (from.isArray()) {
				final Object[] fromA = (Object[]) object;
				if (to == String.class) {
					final Object puthere = Array.newInstance(char.class,
							fromA.length);
					if (!fillArrayType(char.class, fromA.length, fromA,
							getClasses(fromA), puthere))
						return false;
					Array.set(callArray, arg, puthere);
					return true;
				}
				if (isSuperClass(to, Collection.class)) {
					try {
						final Collection<Object> newInstance = (Collection<Object>) to
								.newInstance();
						final Collection<Object> make = newInstance;
						for (final Object element : fromA)
							make.add(element);
						Array.set(callArray, arg, make);
					} catch (final Throwable ie) {
						return false;
					}
					return true;
				}
				return false;
			}
			if (from == String.class)
				return false;
			else {
				try {
					final Class[] sa1 = { String.class };
					final Object[] objA = { object.toString() };
					Array.set(callArray, arg, to.getConstructor(sa1)
							.newInstance(objA));
					return true;
				} catch (final Throwable e) {
				}
				return false;
			}
			// return false; // Object to;
		} else {
			// Primitive to
			if (from.isArray())
				return false;
			if (to == boolean.class) {
				if (object == Boolean.TRUE) {
					Array.setBoolean(callArray, arg, true);
					return true;
				}
				if (object == Boolean.FALSE) {
					Array.setBoolean(callArray, arg, false);
					return true;
				}
				return false;
			}
			if (object instanceof Number) {
				if (to == float.class) {
					Array.setFloat(callArray, arg, ((Number) object)
							.floatValue());
					return true;
				}
				if (to == int.class) {
					Array.setInt(callArray, arg, ((Number) object).intValue());
					return true;
				}
				if (to == long.class) {
					Array
							.setLong(callArray, arg, ((Number) object)
									.longValue());
					return true;
				}
				if (to == double.class) {
					Array.setDouble(callArray, arg, ((Number) object)
							.doubleValue());
					return true;
				}
				if (to == short.class) {
					Array.setShort(callArray, arg, ((Number) object)
							.shortValue());
					return true;
				}
				if (to == byte.class) {
					Array
							.setByte(callArray, arg, ((Number) object)
									.byteValue());
					return true;
				}
				if (to == char.class) {
					Array.setChar(callArray, arg, new Character(
							(char) ((Number) object).intValue()).charValue());
					return true;
				}
				return false;
			}
			if (object instanceof String) {
				if (to == char.class) {
					Array.setChar(callArray, arg, ((String) object).charAt(0));
					return true;
				}
				Float cvfloat = null;
				try {
					cvfloat = new Float((String) object);
				} catch (final Throwable e) {
					return false;
				}
				if (to == float.class) {
					Array.setFloat(callArray, arg, cvfloat.floatValue());
					return true;
				}
				if (to == int.class) {
					Array.setInt(callArray, arg, cvfloat.intValue());
					return true;
				}
				if (to == long.class) {
					Array.setLong(callArray, arg, cvfloat.longValue());
					return true;
				}
				if (to == double.class) {
					Array.setDouble(callArray, arg, cvfloat.doubleValue());
					return true;
				}
				if (to == short.class) {
					Array.setShort(callArray, arg, cvfloat.shortValue());
					return true;
				}
				if (to == byte.class) {
					Array.setByte(callArray, arg, cvfloat.byteValue());
					return true;
				}
				return false;
			}
		}
		return false;
	}

	/**
	 * 
	 * @param innerClass
	 * @param innerInstance
	 * @param methodName
	 * @param args
	 * @throws java.lang.Throwable ( *
	 *             NoSuchMethodException,IllegalAccessException,InvocationTargetException )
	 * @return
	 */
	public static final Object invokeSomething(final Class innerClass,
			final Object innerInstance, final String methodName,
			final Object[] args) throws NoSuchMethodException {
		Throwable was = null;
		final String message = "invokeSomething: ((" + innerClass + ")"
				+ innerInstance + ")." + methodName + "("
				+ ConsoleChannel.joinString(args, ",", 0, -1) + ")";
		ConsoleChannel.debug(message);
		if (args == null || args.length == 0)
			try {
				return innerClass.getMethod(methodName, (Class[]) null).invoke(
						innerInstance, args);
			} catch (final IllegalAccessException iae) {
				was = iae;
			} catch (final InvocationTargetException iae) {
				was = iae;
			}
		else {
			final Class[] clasparams = getClasses(args);
			final Method[] methods = innerClass.getMethods();
			final Object[] calling = new Object[args.length];
			for (final Method element : methods) {
				final Class[] methodParms = element.getParameterTypes();
				if (methodParms.length == clasparams.length
						&& methodName.equals(element.getName()))
					try {
						if (convertTypeArray(args, clasparams, calling,
								methodParms))
							return element.invoke(innerInstance, calling);
					} catch (final InvocationTargetException iae) {
						was = iae;
					} catch (final IllegalAccessException iae) {
						was = iae;
					} catch (final IllegalArgumentException iae) {
						was = iae;
					}
			}
		}
		if (was != null)
			throw new RuntimeException(message, was);
		throw new NoSuchMethodException(message);
	}

	/**
	 * 
	 * @return
	 * @param sub
	 * @param sup
	 */
	public final static/* static */boolean isSuperClass(final Class sub,
			final Class sup) {
		final Class[] sups = sub.getClasses();
		for (final Class element : sups)
			if (element == sup)
				return true;
		return false;
	}

	public static Unsafe getUnsafe() {
		if (NativeManager.unsafe == null) {
			try {
				Class uc = Unsafe.class;
				Field[] fields = uc.getDeclaredFields();
				for (int i = 0; i < fields.length; i++) {
					if (fields[i].getName().equals("theUnsafe")) {
						fields[i].setAccessible(true);
						NativeManager.unsafe = (Unsafe) fields[i].get(uc);
						break;
					}
				}
			} catch (Exception ignore) {
			}
		}
		return NativeManager.unsafe;
	}

	/**
	 * @param obj
	 * @param fieldName
	 */
	public static Object getInstanceFieldValue(Object obj, String fieldName)
			throws NoSuchFieldException {
		final Unsafe unsafe = getUnsafe();
		return unsafe.getObject(obj, unsafe.objectFieldOffset(findField(obj
				.getClass(), fieldName)));
	}
}