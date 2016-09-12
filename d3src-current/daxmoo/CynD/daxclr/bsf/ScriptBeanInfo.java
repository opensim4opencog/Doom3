package daxclr.bsf;

import java.beans.BeanDescriptor;
import java.beans.BeanInfo;
import java.beans.EventSetDescriptor;
import java.beans.IntrospectionException;
import java.beans.Introspector;
import java.beans.MethodDescriptor;
import java.beans.PropertyDescriptor;
import java.util.ArrayList;
import java.util.List;
import java.util.Vector;

import daxclr.bsf.IScriptObject;


/**
 * @author Administrator
 * 
 */
public class ScriptBeanInfo extends BeanDescriptor implements BeanInfo {
	final List<EventSetDescriptor> descevents = new Vector<EventSetDescriptor>(10);
	final List<MethodDescriptor> descmethods = new ArrayList<MethodDescriptor>(10);
	final List<PropertyDescriptor> descproperties = new ArrayList<PropertyDescriptor>(10);
	final ArrayList<BeanInfo> additionalBeanInfo = new ArrayList<BeanInfo>(10);
	boolean modified = false;
	IScriptObject scriptObject;
	private Class customizerClass;
	/**
	 * @param beanClass1
	 */
	public ScriptBeanInfo(IScriptObject scriptObject) {
		super(scriptObject.getBeanClass());
		this.scriptObject = scriptObject;
		addClasses(scriptObject.getClasses());
	}
	public void addClasses(Class[] classes) {
		for (Class clazz:classes)
			addBeanClass(clazz);
	}
	// public DynamicBeanInfo( final Class beanClass) {
	// super(beanClass);
	// this.setName(name);
	// }
	public void addCustomizerClass(final Class cuz) {
		if (cuz != null) if (this.customizerClass == null) {
			this.customizerClass = cuz;
			this.modified = true;
		}
	}
	public void addBeanDescriptor(final BeanDescriptor cuz) {
		addBeanClass(cuz.getBeanClass());
		addCustomizerClass(cuz.getCustomizerClass());
	}
	
	public void addBeanClass(final Class beanClass1) {
		if (beanClass1 != null) {
			try {
				this.addBeanInfo(Introspector.getBeanInfo(beanClass1, Introspector.USE_ALL_BEANINFO));
			} catch (final IntrospectionException ex) {
				ConsoleChannel.debug(ex);
			}
		}
	}
	public void addBeanInfos(final BeanInfo[] info) {
		if (info != null) for (final BeanInfo element : info)
			this.addBeanInfo(element);
	}
	public void addBeanInfo(final BeanInfo info) {
		if (info != null) {
			this.addMethodDescriptors(info.getMethodDescriptors());
			this.addEventSetDescriptors(info.getEventSetDescriptors());
			this.addPropertyDescriptors(info.getPropertyDescriptors());
			this.addCustomizerClass(info.getBeanDescriptor().getCustomizerClass());
			this.addAdditionalBeanInfo(info.getAdditionalBeanInfo());
		}
	}
	public void addAdditionalBeanInfo(final BeanInfo[] info) {
		if (info != null) for (final BeanInfo element : info)
			this.modified = this.modified || this.additionalBeanInfo.add(element);
	}
	public void addEventSetDescriptor(final EventSetDescriptor info) {
		if (info == null || this.descevents.contains(info)) return;
		this.modified = this.modified || this.descevents.add(info);
		// System.out.println("Appending "+this+" with "+info);
	}
	public void addEventSetDescriptors(final EventSetDescriptor[] info) {
		if (info != null) for (final EventSetDescriptor element : info)
			this.addEventSetDescriptor(element);
	}
	public void addMethodDescriptor(final MethodDescriptor info) {
		if (info == null || this.descmethods.contains(info)) return;
		this.modified = this.modified || this.descmethods.add(info);
		// System.out.println("Appending "+this+" with "+info);
	}
	public void addMethodDescriptors(final MethodDescriptor[] info) {
		if (info != null) for (final MethodDescriptor element : info)
			this.addMethodDescriptor(element);
	}
	public void addPropertyDescriptor(final PropertyDescriptor info) {
		if (info == null || this.descproperties.contains(info)) return;
		this.modified = true;
		this.descproperties.add(info);
	}
	public void addPropertyDescriptors(final PropertyDescriptor[] info) {
		if (info != null) for (final PropertyDescriptor element : info)
			this.addPropertyDescriptor(element);
	}
	public EventSetDescriptor[] getEventSetDescriptors() {
		if (this.descevents.size() == 0) return null;
		return this.descevents.toArray(new EventSetDescriptor[0]);
	}
	public MethodDescriptor[] getMethodDescriptors() {
		if (this.descmethods.size() == 0) return null;
		return this.descmethods.toArray(new MethodDescriptor[0]);
	}
	public PropertyDescriptor[] getPropertyDescriptors() {
		if (this.descproperties.size() == 0) return null;
		return this.descproperties.toArray(new PropertyDescriptor[0]);
	}
	public BeanInfo[] getAdditionalBeanInfo() {
		if (this.additionalBeanInfo.size() == 0) return null;
		return this.additionalBeanInfo.toArray(new BeanInfo[0]);
	}
	public java.awt.Image getIcon(final int kind) {
		BeanInfo beanInfo;
		try {
			beanInfo = Introspector.getBeanInfo(getBeanClass());
		} catch (IntrospectionException ex) {
			// TODO Auto-generated catch block
			return (java.awt.Image) null;
		}
		if (beanInfo != null) return beanInfo.getIcon(kind);
		return null;
	}
	public BeanDescriptor getBeanDescriptor() {
		return this;
	}
	
	public Class<?> getBeanClass() {
		return scriptObject.getBeanClass();
	}
	public Class<?> getCustomizerClass() {
		if (this.customizerClass == null) try {
			return ScriptBeanInfo.findCustomizerClass(getBeanClass());
		} catch (final Exception e) {}
		return this.customizerClass;
	}
	public int getDefaultEventIndex() {
		return -1;
	}
	public int getDefaultPropertyIndex() {
		return -1;
	}
	private static Class findCustomizerClass(Class c) throws IntrospectionException {
		BeanInfo beanInfo = Introspector.getBeanInfo(c);
		Class customizerClass = null;
		BeanDescriptor descriptor = beanInfo.getBeanDescriptor();
		if (descriptor != null) {
			customizerClass = descriptor.getCustomizerClass();
		}
		if (customizerClass == null) {
			if (c == Object.class) {
				return null;
			} else {
				return findCustomizerClass(c.getSuperclass());
			}
		} else {
			return customizerClass;
		}
	}
}
