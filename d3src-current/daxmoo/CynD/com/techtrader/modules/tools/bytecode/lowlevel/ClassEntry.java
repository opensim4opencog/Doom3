package com.techtrader.modules.tools.bytecode.lowlevel;


import java.io.*;

import com.techtrader.modules.tools.bytecode.visitor.*;


/**
 *	Low-level representation of a constant pool entry describing a Class.
 *	Class entries are used to refer to the compiled class, the superclass,
 *	implemented interfaces, field types, etc.  Each ClassEntry contains an
 *	index into the constant pool of the UTF8Entry that stores the class name, 
 *	which is represented in internal form.
 *	
 *	@author		Abe White
 */
public class ClassEntry
	implements Entry, LowLevelConstants
{
	private int _nameIndex = -1;


	/**
	 *	Get the index into the constant pool of a UTF8Entry containing
	 *	the class name.
	 */
	public int getNameIndex ()
	{
		return _nameIndex;
	}


	/**
	 *	Set the index into the constant pool of a UTF8Entry containing
	 *	the class name.
	 */
	public void setNameIndex (int nameIndex)
	{
		_nameIndex = nameIndex;
	}


	public int getType ()
	{
		return ENTRY_CLASS;
	}


	public void readData (DataInput in)
		throws IOException
	{
		setNameIndex (in.readUnsignedShort ());
	}


	public void writeData (DataOutput out)
		throws IOException
	{
		out.writeShort (getNameIndex ());
	}


	public String getKey ()
	{
		return getType () + "|" + getNameIndex ();
	}


	public void acceptVisit (BCVisitor visit)
	{
		visit.enterClassEntry (this);
		visit.exitClassEntry (this);
	}
}
