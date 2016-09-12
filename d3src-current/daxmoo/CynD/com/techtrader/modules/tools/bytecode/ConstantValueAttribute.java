package com.techtrader.modules.tools.bytecode;


import java.io.*;

import com.techtrader.modules.tools.bytecode.visitor.*;


/**
 *	Representation of a constant value in a .class file.
 *	
 *	@author		Abe White
 */
public class ConstantValueAttribute
	extends Attribute
{
	int	_valueIndex = -1;


	/**
	 *	Protected constructor.
	 */
	public ConstantValueAttribute (int nameIndex, BCEntity owner)
	{
		super (nameIndex, owner);
	}


	/**
	 *	Get the index in the constnat pool of the Entry describeing this 
	 *	constant; this might be a StringEntry, IntEntry, DoubleEntry, etc.
	 */
	public int getValueIndex ()
	{
		return _valueIndex;
	}


	/**
	 *	Set the index in the constnat pool of the Entry describing this 
	 *	constant; this might be a StringEntry, IntEntry, DoubleEntry, etc.
	 */
	public void setValueIndex (int valueIndex)
	{
		_valueIndex = valueIndex;
	}


	/**
	 *	Return the type of constant this attribute represents, or null if
	 *	the constant has not been set.
	 */
	public Class getType ()	
	{
		Object value = getValue ();
		if (value == null)
			return null;

		Class type = value.getClass ();
		if (type.equals (Integer.class))
			return int.class;
		if (type.equals (Float.class))
			return float.class;
		if (type.equals (Double.class))
			return double.class;
		if (type.equals (Long.class))
			return long.class;
		return String.class;
	}


	/**
	 *	Return the type of constant this attribute represents, or null if
	 *	the constant has not been set.
	 */
	public String getTypeName ()	
	{
		Class type = getType ();
		if (type == null)
			return null;

		return type.getName ();
	}


	/**
	 *	Return the value of this constant as an Object of the appropriate
	 *	type (String, Integer, Double, etc).
	 */
	public Object getValue ()
	{
		return getPool ().getConstant (_valueIndex);
	}


	/**
	 *	Set the value of this constant using the appropriate wrapper Object
	 *	type (String, Integer, Double, etc).  It is an error to change the
	 *	type of a constant once it has been set.
	 */
	public void setValue (Object value)
	{
		_valueIndex = getPool ().setConstant (0, value);
	}


	/**
	 *	Get the value of this int constant.
	 */
	public int getIntValue ()
	{
		Object value = getValue ();
		if (value == null)
			return 0;

		return ((Number) value).intValue ();
	}


	/**
	 *	Set the value of this int constant.
	 */
	public void setIntValue (int value)
	{
		setValue (new Integer (value));
	}


	/**
	 *	Get the value of this float constant.
	 */
	public float getFloatValue ()
	{
		Object value = getValue ();
		if (value == null)
			return 0.0F;

		return ((Number) value).floatValue ();
	}


	/**
	 *	Set the value of this float constant.
	 */
	public void setFloatValue (float value)
	{
		setValue (new Float (value));
	}


	/**
	 *	Get the value of this double constant.
	 */
	public double getDoubleValue ()
	{
		Object value = getValue ();
		if (value == null)
			return 0.0;

		return ((Number) value).doubleValue ();
	}


	/**
	 *	Set the value of this double constant.
	 */
	public void setDoubleValue (double value)
	{
		setValue (new Double (value));
	}


	/**
	 *	Get the value of this long constant.
	 */
	public long getLongValue ()
	{
		Object value = getValue ();
		if (value == null)
			return 0L;

		return ((Number) value).longValue ();
	}


	/**
	 *	Set the value of this long constant.
	 */
	public void setLongValue (long value)
	{
		setValue (new Long (value));
	}


	/**
	 *	Get the value of this String constant.
	 */
	public String getStringValue ()
	{
		return (String) getValue ();
	}


	/**
	 *	Set the value of this String constant.
	 */
	public void setStringValue (String value)
	{
		setValue ((Object) value);
	}


	public int getLength ()
	{
		return 2;
	}


	protected void copy (Attribute other)
	{
		setValue (((ConstantValueAttribute) other).getValue ());		
	}


	protected void readData (DataInput in, int length)
		throws IOException
	{
		setValueIndex (in.readUnsignedShort ());
	}


	protected void writeData (DataOutput out, int length)
		throws IOException
	{
		out.writeShort (getValueIndex ());
	}


	public void acceptVisit (BCVisitor visit)
	{
		visit.enterConstantValueAttribute (this);	
		visit.exitConstantValueAttribute (this);	
	}
}
