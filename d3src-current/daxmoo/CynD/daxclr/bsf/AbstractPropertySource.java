/**
 * 
 */
package daxclr.bsf;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeSupport;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.rmi.RemoteException;
import java.util.Collection;
import java.util.Map;

/**
 * @author Administrator
 * 
 */
public abstract class AbstractPropertySource extends PropertyChangeSupport
		implements IPropertySource, INamedObject {

	private static final long serialVersionUID = 6690357631388936760L;

	final public String propertyName;

	abstract public String getName();

	final public IScriptObjectRemote sourceBean;

	public void firePropertyChange(PropertyChangeEvent evt) {
		if (propertyName.equalsIgnoreCase(evt.getPropertyName()))
			super.firePropertyChange(evt);
	}

	final public void propertyChange(PropertyChangeEvent evt) {
		if (!propertyName.equalsIgnoreCase(evt.getPropertyName()))
			return;
		IPropertySource parent = getParent();
		if (parent != null)
			parent.propertyChange(evt);
		try {
			changeValue(getSource(), evt.getOldValue(), evt.getNewValue());
			super.firePropertyChange(evt);
		} catch (UnsupportedOperationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ClassCastException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IllegalArgumentException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (NullPointerException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IllegalStateException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (NoSuchFieldException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public AbstractPropertySource(final IScriptObjectRemote source,
			final String propertyName) {
		super(source);
		this.propertyName = propertyName;
		this.sourceBean = source;
	}

	/**
	 * Replaces the val corresponding to this entry with the specified val
	 * (optional operation). (Writes through to the map.) The behavior of this
	 * call is undefined if the mapping has already been removed from the map
	 * (by the iterator's <tt>remove</tt> operation).
	 * 
	 * @param val
	 *            new val to be stored in this entry.
	 * @return old val corresponding to the entry.
	 * 
	 * @throws UnsupportedOperationException
	 *             if the <tt>put</tt> operation is not supported by the
	 *             backing map.
	 * @throws ClassCastException
	 *             if the class of the specified val prevents ex from being
	 *             stored in the backing map.
	 * @throws IllegalArgumentException
	 *             if some aspect of this val prevents ex from being stored in
	 *             the backing map.
	 * @throws NullPointerException
	 *             if the backing map does not permit <tt>null</tt> values,and
	 *             the specified val is <tt>null</tt>.
	 * @throws IllegalStateException
	 *             implementations may,but are not required to,throw this
	 *             exception if the entry has been removed from the backing map
	 * @throws NoSuchFieldException
	 */
	/*
	 * abstract public Object getValueImpl(Object source) throws
	 * NoSuchFieldException;
	 * 
	 * abstract public Object invokeImpl(Object source, Object[] params) throws
	 * NoSuchMethodException, UnsupportedOperationException, ClassCastException,
	 * IllegalArgumentException, NullPointerException, IllegalStateException;
	 */
	abstract public boolean isFinal();

	abstract public String toString();

	abstract public IPropertySource getParent();

	abstract public Field toField();

	abstract public Method toMethod();

	abstract public Method toSetMethod();

	abstract public void changeValue(IScriptObject source, Object old,
			Object value) throws NoSuchFieldException,
			UnsupportedOperationException, ClassCastException,
			IllegalArgumentException, NullPointerException,
			IllegalStateException;

	final public Object invoke(final Object obj, final Object[] args)
			throws IllegalArgumentException, InvocationTargetException {
		IScriptObject sourceObject = getSource();
		try {
			return sourceObject.invokeMethod(propertyName, args);
		} catch (Exception e) {
			throw new IllegalArgumentException(toString(), e);
		}
	}

	public Object get(final Object obj) throws IllegalArgumentException {
		try {
			return getSource().getProperty(getKey());
		} catch (NoSuchFieldException e) {
			throw new IllegalArgumentException(toString(), e);
		}
	}

	public void set(final Object obj, final Object val)
			throws IllegalArgumentException, IllegalAccessException {
		getSource().setProperty(getKey(), val);
	}

	final public String getKey() {
		return propertyName;
	}

	public Object getValue() throws IllegalStateException {
		try {
			return getSource().getProperty(getKey());
		} catch (NoSuchFieldException e) {
			throw new IllegalStateException(toString(), e);
		}
	}

	/**
	 * Replaces the value corresponding to this entry with the specified value
	 * (optional operation). (Writes through to the map.) The behavior of this
	 * call is undefined if the mapping has already been removed from the map
	 * (by the iterator's <tt>remove</tt> operation).
	 * 
	 * @param value
	 *            new value to be stored in this entry.
	 * @return old value corresponding to the entry.
	 * 
	 * @throws UnsupportedOperationException
	 *             if the <tt>put</tt> operation is not supported by the
	 *             backing map.
	 * @throws ClassCastException
	 *             if the class of the specified value prevents it from being
	 *             stored in the backing map.
	 * @throws IllegalArgumentException
	 *             if some aspect of this value prevents it from being stored in
	 *             the backing map.
	 * @throws NullPointerException
	 *             if the backing map does not permit <tt>null</tt> values,
	 *             and the specified value is <tt>null</tt>.
	 * @throws IllegalStateException
	 *             implementations may, but are not required to, throw this
	 *             exception if the entry has been removed from the backing map
	 */
	public Object setValue(final Object val)
			throws UnsupportedOperationException, NullPointerException,
			IllegalStateException, ClassCastException {
		final Object old = this.getValue();
		try {
			getSource().setProperty(getKey(), val);
			// firePropertyChange(sourceBean, getKey(), getValue(), val);
		} catch (IllegalArgumentException e) {
			throw new IllegalStateException(toString(), e);
		} catch (IllegalAccessException e) {
			throw new UnsupportedOperationException(toString(), e);
		}
		return old;
	}

	public IScriptObject getSource() {
		try {
			return sourceBean.getNameSpace();
		} catch (RemoteException e) {
			e.printStackTrace();
			throw new RuntimeException(e);
		} catch (Throwable e) {
			throw new RuntimeException(e);

		}
	}

	public int hashCode() {
		return (this.getKey() == null ? 0 : this.getKey().hashCode())
				^ (this.getValue() == null ? 0 : this.getValue().hashCode());
	}

	public boolean equals(final Object e) {
		if (!(e instanceof Map.Entry))
			return false;
		/* final */final Map.Entry e2 = (Map.Entry) e;
		return (this.getKey() == null ? e2.getKey() == null : this.getKey()
				.equals(e2.getKey()))
				&& (this.getValue() == null ? e2.getValue() == null : this
						.getValue().equals(e2.getValue()));
	}

	/** Matches specification in {@link java.lang.reflect.Field} */
	final public boolean getBoolean(final Object obj)
			throws IllegalArgumentException {
		final Object value = get(obj);
		if (value == null)
			return false;
		if (value instanceof Boolean)
			return ((Boolean) value).booleanValue();
		if (value instanceof Number)
			return ((Number) value).doubleValue() != 0.0D;
		if (value instanceof Map)
			return ((Map) value).size() != 0;
		if (value instanceof Collection)
			return ((Collection) value).size() != 0;
		/* final */final String str = obj.toString().toLowerCase();
		if (str.equals("") || str.equals("0") || str.startsWith("n")
				|| str.startsWith("f"))
			return false;
		return true;
	}

	/** Matches specification in {@link java.lang.reflect.Field} */
	final public byte getByte(final Object obj) throws IllegalArgumentException {
		return ((Number) get(obj)).byteValue();
	}

	/** Matches specification in {@link java.lang.reflect.Field} */
	final public char getChar(final Object obj) throws IllegalArgumentException {
		return ((Character) get(obj)).charValue();
	}

	/** Matches specification in {@link java.lang.reflect.Field} */
	final public double getDouble(final Object obj)
			throws IllegalArgumentException {
		return ((Number) get(obj)).doubleValue();
	}

	/** Matches specification in {@link java.lang.reflect.Field} */
	final public float getFloat(final Object obj)
			throws IllegalArgumentException {
		return ((Number) get(obj)).floatValue();
	}

	/** Matches specification in {@link java.lang.reflect.Field} */
	final public int getInt(final Object obj) throws IllegalArgumentException {
		return ((Number) get(obj)).intValue();
	}

	/** Matches specification in {@link java.lang.reflect.Field} */
	final public long getLong(final Object obj) throws IllegalArgumentException {
		return ((Number) get(obj)).longValue();
	}

	/** Matches specification in {@link java.lang.reflect.Field} */
	final public short getShort(final Object obj)
			throws IllegalArgumentException {
		return ((Number) get(obj)).shortValue();
	}

	/** Matches specification in {@link java.lang.reflect.Field} */
	final public void setBoolean(final Object obj, final boolean z)
			throws IllegalArgumentException, IllegalAccessException {
		set(obj, new Boolean(z));
	}

	/** Matches specification in {@link java.lang.reflect.Field} */
	final public void setByte(final Object obj, final byte b)
			throws IllegalArgumentException, IllegalAccessException {
		set(obj, new Byte(b));
	}

	/** Matches specification in {@link java.lang.reflect.Field} */
	final public void setChar(final Object obj, final char c)
			throws IllegalArgumentException, IllegalAccessException {
		set(obj, new Character(c));
	}

	/** Matches specification in {@link java.lang.reflect.Field} */
	final public void setDouble(final Object obj, final double d)
			throws IllegalArgumentException, IllegalAccessException {
		set(obj, new Double(d));
	}

	/** Matches specification in {@link java.lang.reflect.Field} */
	final public void setFloat(final Object obj, final float f)
			throws IllegalArgumentException, IllegalAccessException {
		set(obj, new Float(f));
	}

	/** Matches specification in {@link java.lang.reflect.Field} */
	final public void setInt(final Object obj, final int i)
			throws IllegalArgumentException, IllegalAccessException {
		set(obj, new Integer(i));
	}

	/** Matches specification in {@link java.lang.reflect.Field} */
	final public void setLong(final Object obj, final long l)
			throws IllegalArgumentException, IllegalAccessException {
		set(obj, new Long(l));
	}

	/** Matches specification in {@link java.lang.reflect.Field} */
	final public void setShort(final Object obj, final short s)
			throws IllegalArgumentException, IllegalAccessException {
		set(obj, new Short(s));
	}

}
