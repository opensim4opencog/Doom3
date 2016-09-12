package com.techtrader.modules.tools.bytecode.lowlevel;


import java.io.*;

import com.techtrader.modules.tools.bytecode.visitor.*;


/**
 *	Represents a constant float value in the constant pool.
 *	
 *	@author		Abe White
 */
public class FloatEntry
	implements ConstantEntry, LowLevelConstants
{
	private float _value = 0.0F;


	public int getType ()
	{
		return ENTRY_FLOAT;
	}


	/**
	 *	Return the value of this constant.	
 	 */
	public float getValue ()
	{
		return _value;
	}


	/**
	 *	Set the value of this constant.	
 	 */
	public void setValue (float value)
	{
		_value = value;
	}


	public Object getConstantValue ()
	{
		return new Float (_value);
	}


	public void setConstantValue (Object val)
	{
		_value = ((Number) val).floatValue ();
	}


	public void readData (DataInput in)
		throws IOException
	{
		setValue (in.readFloat ());
	}


	public void writeData (DataOutput out)
		throws IOException
	{
		out.writeFloat (getValue ());
	}


	public String getKey ()
	{
		return getType () + "|" + getValue ();
	}


	public void acceptVisit (BCVisitor visit)
	{
		visit.enterFloatEntry (this);
		visit.exitFloatEntry (this);
	}
}
