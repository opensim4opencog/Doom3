package daxclr.inference;

import java.io.IOException;
import java.io.Serializable;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.net.UnknownHostException;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

import org.opencyc.api.CycApiException;
import org.opencyc.cycobject.CycList;
import org.opencyc.cycobject.CycObject;


import daxclr.bsf.AbstractPropertySource;
import daxclr.bsf.ConsoleChannel;
import daxclr.bsf.IMultiSourceProperty;
import daxclr.bsf.IScriptObject;
import daxclr.bsf.IScriptObjectRemote;
import daxclr.bsf.ObjectRepository;

public class CycPropertyEntry extends AbstractPropertySource {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1369723194787158309L;

	private CycObject proppred;

	private CycObject defaultQueryMt;

	private CycObject defaultAssertMt;

	public CycPropertyEntry(IScriptObjectRemote source, String name) {
		super(source, name);
	}

	public CycPropertyEntry(IScriptObjectRemote remote, CycObject pred,
			CycObject defaultQueryMt0, CycObject defaultAssertMt0) {
		super(remote, ObjectRepository.resolverMap.toString(pred));
		proppred = pred;
		defaultQueryMt = defaultQueryMt0;
		defaultAssertMt = defaultAssertMt0;
	}

	public Object getValue() {
		return get(getSource());
	}

	public Object get(Object src) {
		return getValueImpl(getSource());
	}

	public Object setValue(Object value) {
		Object old = getValueImpl(getSource());
		changeValue(getSource(), old, value);
		return old;
	}

	public void set(Object src, Object val) {
		setValueImpl(getSource(), val);
	}

	@SuppressWarnings("unchecked")
	public Object getValueImpl(Object source) {
		try {
			return (unwrapCycLResults(CycAPI.current().converseList(toString())));
		} catch (Throwable e) {
			ConsoleChannel.debug(e);
			return null;
		}
	}

	@SuppressWarnings("unchecked")
	static Serializable unwrapCycLResults(CycList set) {
		Serializable o = null;
		switch (set.size()) {
		case 0:
			return o;
		case 1:
			o = (Serializable) set.iterator().next();
			if (o instanceof String) {
				return ("" + o).replace("\"", "\\\"");
			}
			return o;
		default:
			return new HashSet(set);
		}
	}

	public void setValueImpl(Object source, Object val) {
		changeValue(getSource(), getValueImpl(source), val);
	}

	public void changeValue(IScriptObject src, Object old, Object value) {
		removeValue(src, old);
		addValue(src, old);
	}

	private void removeValue(IScriptObject src, Object old) {
		if (old instanceof CycObject) {
			try {
				CycFortPropertyMap.cycAccess.converseVoid("(cyc-unassert '("
						+ proppred.cyclify() + " " + cyclifySource() + " "
						+ ((CycObject) old).cyclify() + ") "
						+ defaultAssertMt.cyclify() + ")");
			} catch (UnknownHostException e) {
				ConsoleChannel.debug(e);
			} catch (CycApiException e) {
				ConsoleChannel.debug(e);
			} catch (IOException e) {
				ConsoleChannel.debug(e);
			}
			return;
		} else if (old instanceof Iterable) {
			removeValue(src, ((Iterable) old).iterator());
		} else if (old instanceof Iterator) {
			Iterator its = (Iterator) old;
			while (its.hasNext())
				removeValue(src, its.next());
		}

	}

	private void addValue(IScriptObject src, Object old) {
		if (old instanceof CycObject) {
			try {
				CycFortPropertyMap.cycAccess.converseVoid("(cyc-assert '("
						+ proppred.cyclify() + " " + cyclifySource() + " "
						+ ((CycObject) old).cyclify() + ") "
						+ defaultAssertMt.cyclify() + ")");
			} catch (UnknownHostException e) {
				ConsoleChannel.debug(e);
			} catch (CycApiException e) {
				ConsoleChannel.debug(e);
			} catch (IOException e) {
				ConsoleChannel.debug(e);
			}
			return;
		} else if (old instanceof Iterable) {
			removeValue(src, ((Iterable) old).iterator());
		} else if (old instanceof Iterator) {
			Iterator its = (Iterator) old;
			while (its.hasNext())
				removeValue(src, its.next());
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see daxclr.java.AbstractPropertyEntry#invokeProperty(java.lang.Object[])
	 */
	public Object invokeImpl(Object obj, Object[] params)
			throws UnsupportedOperationException, ClassCastException,
			IllegalArgumentException, NullPointerException,
			IllegalStateException {
		Set<Object> set = CycAPI.getVariableSet("VALUE",
				CycFortPropertyMap.cycAccess.converseList("(cyc-query '("
						+ proppred.cyclify() + " " + cyclifySource() + " "
						+ ConsoleChannel.joinString(params, " ") + " ?VALUE) '"
						+ defaultQueryMt.cyclify() + ")"));
		return set;
	}

	private String cyclifySource() {
		return getSource().getCycObject().cyclify();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see daxclr.java.IPropertyEntry#isReadOnly()
	 */
	public boolean isFinal() {
		// TODO Auto-generated method stub
		return false;
	}

	public IMultiSourceProperty getParent() {
		return null;
	}

	public Field toField() {
		return null;
	}

	@Override
	public Method toMethod() {
		// Class declaringClass = getSource().getBeanClass();
		// String name = "get" +

		// Field f = ReflectionFactory.getReflectionFactory().newMethod(
		// declaringClass, name, parameterTypes, returnType, checkedExceptions,
		// modifiers, slot, signature, annotations, parameterAnnotations,
		// annotationDefault)
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Method toSetMethod() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String toString() {
		// TODO Auto-generated method stub
		return "(ask-template '?VALUE '(" + proppred.cyclify() + " "
				+ cyclifySource() + " ?VALUE) " + defaultQueryMt.cyclify()
				+ ")";
	}

	@Override
	public String getName() {
		// TODO Auto-generated method stub
		try {
			return sourceBean.getName() + "." + propertyName;
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			throw new RuntimeException(e);
		} catch (Throwable e) {
			throw new RuntimeException(e);

		}
	}
}
