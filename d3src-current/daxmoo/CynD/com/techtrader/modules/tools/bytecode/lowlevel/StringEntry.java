package com.techtrader.modules.tools.bytecode.lowlevel;


import java.io.*;

import com.techtrader.modules.tools.bytecode.visitor.*;


/**
 *	String constant constant pool entry.
 *	
 *	@author		Abe White
 */
public class StringEntry
	implements ConstantEntry, LowLevelConstants
{
	private int _stringIndex = -1;


	public int getType ()
	{
		return ENTRY_STRING;
	}


	/**
	 *	Get the index into the constant pool of the UTF8Entry storing the
	 *	value of this string constant.
	 */
	public int getStringIndex ()
	{
		return _stringIndex;
	}


	/**
	 *	Set the index into the constant pool of the UTF8Entry storing the
	 *	value of this string constant.
	 */
	public void setStringIndex (int stringIndex)
	{
		_stringIndex = stringIndex;
	}


	public Object getConstantValue ()
	{
		return new Integer (_stringIndex);
	}


	public void setConstantValue (Object val)
	{
		_stringIndex = ((Number) val).intValue ();
	}


	public void readData (DataInput in)
		throws IOException
	{
		setStringIndex (in.readUnsignedShort ());
	}


	public void writeData (DataOutput out)
		throws IOException
	{
		out.writeShort (getStringIndex ());
	}


	public String getKey ()
	{
		return getType () + "|" + getStringIndex ();
	}


	public void acceptVisit (BCVisitor visit)
	{
		visit.enterStringEntry (this);
		visit.exitStringEntry (this);
	}
}
