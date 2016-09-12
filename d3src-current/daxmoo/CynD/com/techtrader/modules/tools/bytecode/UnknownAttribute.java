package com.techtrader.modules.tools.bytecode;


import java.io.*;

import com.techtrader.modules.tools.bytecode.visitor.*;


/**
 *	An unrecognized attribute; .class files are allowed to contain attributes
 *	that are not recognized, and the JVM must ignore them.
 *	
 *	@author		Abe White
 */
public class UnknownAttribute
	extends Attribute
{
	private byte[] _value = new byte[0];


	public UnknownAttribute (int nameIndex, BCEntity owner)
	{
		super (nameIndex, owner);
	}


	/**
	 *	The value's type is unknown, so just store the byte array.
	 */
	public byte[] getValue ()
	{
		return _value;
	}


	/**
	 *	The value's type is unknown, so just store the byte array.
	 */
	public void setValue (byte[] value)
	{
		_value = value;
	}


	public int getLength ()
	{
		return _value.length;
	}


	protected void copy (Attribute other)
	{
		setValue (((UnknownAttribute) other).getValue ());
	}


	protected void readData (DataInput in, int length)
		throws IOException
	{
		_value = new byte[length];
		in.readFully (_value);
	}


	protected void writeData (DataOutput out, int length)
		throws IOException
	{
		out.write (_value);
	}


	public void acceptVisit (BCVisitor visit)
	{
		visit.enterUnknownAttribute (this);
		visit.exitUnknownAttribute (this);
	}
}
