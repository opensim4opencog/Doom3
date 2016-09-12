package daxclr.bsf;

import java.io.Serializable;

import java.util.Collection;
import java.util.Map;

import bsh.Interpreter;

import com.netbreeze.swing.BeansContextListener;

public interface IObjectRepository extends  Map<Object, IScriptObject>,BeansContextListener, java.rmi.registry.Registry {

	// public <String,T> Map getBeansOfType(final Class<T> type);

	// public abstract String objectKey(Object bean);

	// public abstract IScriptObject keyEntry(String key);

	/**
	 * Adds a new bean, if it wasn't already there
	 * 
	 * @returns true if the bean was added, false if the bean was already there
	 */
	public IScriptObject findOrCreateEntry(Object value);

	// public abstract boolean addEntry(IScriptObject bean);

	/**
	 * Removes a bean, if it is there
	 * 
	 * @returns true if the bean was removed, false if that bean wasn't in this
	 *          context
	 */
	// public abstract boolean removeEntry(IScriptObject bean);
	public <C> Collection valuesOfType(Class<C> name);

	// public BeanContext getBeanContext();
	/**
	 * Opens up a GUI to show the details of the given bean
	 */
	// public void showBeanDetails(IScriptObject bean) throws Exception;
	/**
	 * Returns all actions that can be carried out on the given bean
	 */
	// public Collection getActions(IScriptObject bean);
	/**
	 * Displays the given error message somehow
	 */
	// public void showError(String msg, Throwable err);
	/**
	 * Adds a BeansContextListener to this context. The listener will find out
	 * when beans are added or removed.
	 */
	// public void addListener(IEntrySetListener o);
	/**
	 * Adds a BeansContextListener to this context. The listener will find out
	 * when beans are added or removed.
	 */
	// public void removeListener(IEntrySetListener o);
	/**
	 * Returns all beans of the given type (including subclasses)
	 */
	// public <T> Set valuesOfType(Class<T> type);
	// public Iterator<IScriptObject> getBrokers();
	// public Map<String, Object> getNameSpaceMap();
	// public IScriptObject findOrCreateEntry(Object object);
	public Object resolveObject(Object[] path, Object[] specs, boolean localOnly);

	/**
	 * @param event
	 */
	// public void propertyChange(PropertyChangeEvent event);
	/**
	 * @param object
	 * @param err
	 */
	// public void showError(Object object, Throwable err);
	/**
	 * @param object
	 */
	// public void showBeanDetails(IScriptObject object);
	/**
	 * @param listener
	 */
	// public Object put(String string, Object string2);

	//public void register(String name, Object obj);

	public IScriptObject put(Object name, Object object);

	public String toString(Object target);

	public void importExisting();

	public Class toClass(Object name) throws  ClassNotFoundException;

	public Interpreter getInterpreter();

	public Serializable toObject(Object target);


	// public abstract IScriptObject findEntry(Object name);
}