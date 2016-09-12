package com.techtrader.modules.tools.bytecode.lowlevel;


import java.io.*;

import com.techtrader.modules.tools.bytecode.visitor.*;


/**
 *	A constant pool entry representing a unicode string value.
 *	
 *	@author		Abe White
 */
public class UTF8Entry
	implements Entry, LowLevelConstants
{
	private String _value = "";


	public int getType ()
	{
		return ENTRY_UTF8;
	}


	/**
	 *	Get the value of the entry.
	 */
	public String getValue ()
	{
		return _value;
	}


	/**
	 *	Set the value of the entry.
	 */
	public void setValue (String value)
	{
		_value = value;
	}


	public void readData (DataInput in)
		throws IOException
	{
		setValue (in.readUTF ());
	}


	public void writeData (DataOutput out)
		throws IOException
	{
		out.writeUTF (getValue ());
	}


	public String getKey ()
	{
		return getType () + "|" + getValue ();
	}


	public void acceptVisit (BCVisitor visit)
	{
		visit.enterUTF8Entry (this);
		visit.exitUTF8Entry (this);
	}
}
