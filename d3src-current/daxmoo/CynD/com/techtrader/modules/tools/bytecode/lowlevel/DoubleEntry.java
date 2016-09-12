package com.techtrader.modules.tools.bytecode.lowlevel;


import java.io.*;

import com.techtrader.modules.tools.bytecode.visitor.*;


/**
 *	Representation of a constant double value in the constant pool.
 *	
 *	@author		Abe White
 */
public class DoubleEntry
	implements ConstantEntry, LowLevelConstants
{
	private double _value = 0.0;


	public int getType ()
	{
		return ENTRY_DOUBLE;
	}


	/**
	 *	Get the value of the constant.
	 */
	public double getValue ()
	{
		return _value;
	}


	/**
	 *	Set the value of the constant.
	 */
	public void setValue (double value)
	{
		_value = value;
	}


	public Object getConstantValue ()
	{
		return new Double (_value);
	}


	public void setConstantValue (Object val)
	{
		_value = ((Number) val).doubleValue ();
	}


	public void readData (DataInput in)
		throws IOException
	{
		setValue (in.readDouble ());
	}


	public void writeData (DataOutput out)
		throws IOException
	{
		out.writeDouble (getValue ());
	}


	public String getKey ()
	{
		return getType () + "|" + getValue ();
	}


	public void acceptVisit (BCVisitor visit)
	{
		visit.enterDoubleEntry (this);
		visit.exitDoubleEntry (this);
	}
}
