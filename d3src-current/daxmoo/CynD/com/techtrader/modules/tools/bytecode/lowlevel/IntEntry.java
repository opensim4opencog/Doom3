package com.techtrader.modules.tools.bytecode.lowlevel;


import java.io.*;

import com.techtrader.modules.tools.bytecode.visitor.*;


/**
 *	Represents a constant int value in the constant pool.
 *	
 *	@author		Abe White
 */
public class IntEntry
	implements ConstantEntry, LowLevelConstants
{
	private int _value = -1;


	public int getType ()
	{
		return ENTRY_INT;
	}


	/**
	 *	Get the value of this constant.
	 */
	public int getValue ()
	{
		return _value;
	}


	/**
	 *	Set the value of this constant.
	 */
	public void setValue (int value)
	{
		_value = value;
	}


	public Object getConstantValue ()
	{
		return new Integer (_value);
	}


	public void setConstantValue (Object val)
	{
		_value = ((Number) val).intValue ();
	}


	public void readData (DataInput in)
		throws IOException
	{
		setValue (in.readInt ());
	}


	public void writeData (DataOutput out)
		throws IOException
	{
		out.writeInt (getValue ());
	}


	public String getKey ()
	{
		return getType () + "|" + getValue ();
	}


	public void acceptVisit (BCVisitor visit)
	{
		visit.enterIntEntry (this);
		visit.exitIntEntry (this);
	}
}
