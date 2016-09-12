package daxclr.bsf;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.Serializable;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.util.Map;
import java.util.Map.Entry;

import org.opencyc.cycobject.CycFort;

/**
 * A objectInfo for beans used in the bean bowl system. It holds an object, a
 * name, and info about whether it is selected or not. The "name" and "selected"
 * properties are bound and constrained, i.e. you can listen to changes using
 * addPropertyChangeListener, and you can also prevent changes in some cases if
 * you use addVetoableChangeListener.
 * 
 * (RKF-PROVE '(#$isa #$Dog #$Mammal) #$EverythingPSC)
 * 
 * @see IScriptObject
 */
// Map.Entry TreeMap
public interface IScriptObject extends PropertyChangeListener,
		IScriptObjectRemote, IScriptObjectProxy {
	/*
	 * ,BeanContextChild , IScriptObjectRemote
	 * ,Customizer,BeanContextServicesListener
	 */
	public Serializable getValue();

	// public Map<String,Object> getProperties();
	// public V getFirstValue(K[] propnames) throws NoSuchFieldException;
	// public Class<V> getPropertyType(K propname) throws NoSuchFieldException;
	// public V get(K propname) throws NoSuchFieldException;
	// public V put(K propname, V value) throws PropertyVetoException;

	public IScriptMethodHandler getInvocationHandler();

	// public ExternalNameSpace getDefiningNameSpace();
	// public NameSpace getNameSpace();
	// public ScriptThis getThis();
	// public Method[] getMethods();
	public Constructor[] getConstructors();

	public Field[] getFields();

	// public IScriptObject getValue();

	// public void setKey(String target) throws PropertyVetoException;

	public void addPropertyChangeListener(PropertyChangeListener listener);

	/**
	 * Returns the Display name of this bean
	 */
	// public String getKey();
	/* Gets the parent or Root */
	// public IObjectInfo getRoot();
	/**
	 * Gets a BeanInfo object for this bean, using the Introspector class
	 */
	public ScriptBeanInfo getBeanInfo();// throws

	// java.beans.IntrospectionException;

	// final public Class initialProxyClass = IObjectProxy.class;
	// final public IdGameLocal initialInvocationHandler =
	// IdGameLocal.getGameLocal();
	// final public Object initialProxyObject =
	// java.lang.reflect.Proxy.newProxyInstance(java.lang.reflect.Proxy.class.getClassLoader(),
	// new Class[]{IObjectProxy.class},initialInvocationHandler);

	// public int hashCode();
	// public String toString();

	// public Class getBeanClass();

	// public XThis getThis();
	// public NameSpace getNameSpace();
	// public ThisInterpreter getInterpreter();
	// public InvocationHandler getInvocationHandler();
	// public BeanDescriptor getBeanDescriptor();

	// public void setSelected(boolean tf) throws PropertyVetoException;

	// public Field getField(String propname) throws NoSuchFieldException;
	// public Method getMethod( String name, Class[] ptypes) throws
	// NoSuchMethodException;
	// public Method getPropertySetterMethod(String propname) throws
	// NoSuchMethodException;
	// public Method getPropertyGetterMethod(String propname) throws
	// NoSuchMethodException;

	/**
	 * Returns true if the given info is meant to refer to the object this bean
	 * represents
	 */
	public boolean valueEquals(Object keyOrValue);

	public boolean instanceOf(Class<?> type);

	/* reset the bean that this is representing */
	// public Object setValue(Object object) throws IllegalArgumentException;
	/* get the bean proxy that this is representing */
	// public IObjectProxy getValue();
	public boolean equals(Object obj);

	/**
	 * Returns the Class that this will become when the target class is
	 * requested
	 */
	public boolean coerceableTo(Class<?> target);

	/**
	 * Returns an instance of Class that meant to refer to this
	 * 
	 * @return
	 */
	// public Object coerceTo(Class<?> target) throws ClassCastException;// cast
	public <C> Object coerceTo(Class<C> type) throws ClassCastException;// Si8mular

	// to
	// Class.cast

	/**
	 * gets the class of this bsh.This
	 */
	public Class<?> getBeanClass();

	// public Object setValue(Object object);

	/**
	 * @param evt
	 */
	public void propertyChange(PropertyChangeEvent evt);

	public void setProperty(String key, Object val)
			throws IllegalArgumentException, IllegalAccessException;

	public Object getProperty(String key) throws NoSuchFieldException;

	// public Object invokeRemote(Object obj, String name, Object[] args) throws
	// BSFException;

	public CycFort getCycObject();

	public Method[] getJavaMethods();

	public void importObject(Object object);

	public IPropertySource importField(Field field, Object object);

	public IPropertySource importMethod(Method method, Object object);

	public void importClass(Object object);

	public void importHandler(InvocationHandler handler, Class[] interfaces);

	public void importMap(Map spawnArgs); // , Map<K,V>

	public Class[] getClasses();

	public String[] getAllNames();

	public Entry<Object, IScriptObject> toEntry();

	public IScriptObjectRemote toRemote();

	public IScriptObjectThis getThis();

	// public IScriptObject getValue();

}
