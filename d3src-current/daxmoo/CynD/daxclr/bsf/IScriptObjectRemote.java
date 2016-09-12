package daxclr.bsf;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.Serializable;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.Map;
import java.util.Map.Entry;

import org.opencyc.cycobject.CycFort;


public interface IScriptObjectRemote extends INamedObject, Remote,IScriptMethodHandler {
	// public Map<String,Object> getProperties() throws RemoteException;
	// public V getFirstValue(K[] propnames) throws NoSuchFieldException;
	// public Class<V> getPropertyType(K propname) throws NoSuchFieldException;
	// public V get(K propname) throws NoSuchFieldException;
	// public V put(K propname, V value) throws PropertyVetoException;

	public IScriptMethodHandler getInvocationHandler() throws RemoteException;

	// public ExternalNameSpace getDefiningNameSpace() throws RemoteException;
	// public NameSpace getNameSpace() throws RemoteException;
	// public ScriptThis getThis() throws RemoteException;
	// public Method[] getMethods() throws RemoteException;
	public Constructor[] getConstructors() throws RemoteException;

	public Field[] getFields() throws RemoteException;

	// public void setKey(String target) throws PropertyVetoException;

	public void addPropertyChangeListener(PropertyChangeListener listener) throws RemoteException;

	/**
	 * Returns the Display name of this bean
	 */
	// public String getKey() throws RemoteException;
	/* Gets the parent or Root */
	// public IObjectInfo getRoot() throws RemoteException;
	/**
	 * Gets a BeanInfo object for this bean, using the Introspector class
	 */
	public ScriptBeanInfo getBeanInfo() throws RemoteException;// throws

	// java.beans.IntrospectionException;

	// final public Class initialProxyClass = IObjectProxy.class;
	// final public IdGameLocal initialInvocationHandler =
	// IdGameLocal.getGameLocal() throws RemoteException;
	// final public Object initialProxyObject =
	// java.lang.reflect.Proxy.newProxyInstance(java.lang.reflect.Proxy.class.getClassLoader(),
	// new Class[]{IObjectProxy.class},initialInvocationHandler) throws RemoteException;

	// public int hashCode() throws RemoteException;
	// public String toString() throws RemoteException;

	// public Class getBeanClass() throws RemoteException;

	// public XThis getThis() throws RemoteException;
	// public NameSpace getNameSpace() throws RemoteException;
	// public ThisInterpreter getInterpreter() throws RemoteException;
	// public InvocationHandler getInvocationHandler() throws RemoteException;
	// public BeanDescriptor getBeanDescriptor() throws RemoteException;

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
	public boolean valueEquals(Object keyOrValue) throws RemoteException;

	public boolean instanceOf(Class<?> type) throws RemoteException;

	/* reset the bean that this is representing */
	// public Object setValue(Object object) throws IllegalArgumentException;
	/* get the bean proxy that this is representing */
	// public IObjectProxy getValue() throws RemoteException;
//	public boolean equals(Object obj) throws RemoteException;

	/**
	 * Returns the Class that this will become when the target class is
	 * requested
	 */
	public boolean coerceableTo(Class<?> target) throws RemoteException;

	/**
	 * Returns an instance of Class that meant to refer to this
	 * 
	 * @return
	 */
	// public Object coerceTo(Class<?> target) throws ClassCastException;// cast
	public <C> Object coerceTo(Class<C> type) throws RemoteException,ClassCastException;// Si8mular

	// to
	// Class.cast

	/**
	 * gets the class of this bsh.This
	 */
	public Class<?> getBeanClass() throws RemoteException;

	// public Object setValue(Object object) throws RemoteException;

	/**
	 * @param evt
	 */
	public void propertyChange(PropertyChangeEvent evt) throws RemoteException;

	public void setProperty(String key, Object val)
			throws RemoteException,IllegalArgumentException, IllegalAccessException;

	public Object getProperty(String key) throws RemoteException,NoSuchFieldException;

	// public Object invokeRemote(Object obj, String name, Object[] args) throws
	// BSFException;

	public CycFort getCycObject() throws RemoteException;

	public Method[] getJavaMethods() throws RemoteException;

	public void importObject(Object object) throws RemoteException;

	public IPropertySource importField(Field field, Object object) throws RemoteException;

	public IPropertySource importMethod(Method method, Object object) throws RemoteException;

	public void importClass(Object object) throws RemoteException;

	public void importHandler(InvocationHandler handler, Class[] interfaces) throws RemoteException;

	public void importMap(Map spawnArgs) throws RemoteException; // , Map<K,V>

	public Class[] getClasses() throws RemoteException;

	public String[] getAllNames() throws RemoteException;

	public Entry<Object, IScriptObject> toEntry() throws RemoteException;

	public IScriptObject getNameSpace() throws RemoteException;
	
	public Serializable getValue() throws RemoteException;

}
