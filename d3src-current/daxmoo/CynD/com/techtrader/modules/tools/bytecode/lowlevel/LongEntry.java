package com.techtrader.modules.tools.bytecode.lowlevel;


import java.io.*;

import com.techtrader.modules.tools.bytecode.visitor.*;


/**
 *	Represnts a long constant in the constant pool.
 *	
 *	@author		Abe White
 */
public class LongEntry
	implements ConstantEntry, LowLevelConstants
{
	private long _value = 0L;


	public int getType ()
	{
		return ENTRY_LONG;
	}


	/**
	 *	Get the value of the constant.
	 */
	public long getValue ()
	{
		return _value;
	}


	/**
	 *	Set the value of the constant.
	 */
	public void setValue (long value)
	{
		_value = value;
	}


	public Object getConstantValue ()
	{
		return new Long (_value);
	}


	public void setConstantValue (Object val)
	{
		_value = ((Number) val).longValue ();
	}


	public void readData (DataInput in)
		throws IOException
	{
		setValue (in.readLong ());
	}


	public void writeData (DataOutput out)
		throws IOException
	{
		out.writeLong (getValue ());
	}


	public String getKey ()
	{
		return getType () + "|" + getValue ();
	}


	public void acceptVisit (BCVisitor visit)
	{
		visit.enterLongEntry (this);
		visit.exitLongEntry (this);
	}
}
