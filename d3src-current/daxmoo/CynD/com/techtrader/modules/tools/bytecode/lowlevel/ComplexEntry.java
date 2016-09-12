package com.techtrader.modules.tools.bytecode.lowlevel;


import java.io.*;


/**
 *	The complex entry serves as a base class for field, method, and interface
 *	method constant pool entries.
 *	
 *	@author		Abe White
 */
public abstract class ComplexEntry
	implements Entry, LowLevelConstants
{
	protected int _classIndex 			= 0;
	protected int _nameAndTypeIndex 	= 0;


	/**
	 *	Get the index of the ClassEntry describing the class to which
	 *	this entry applies.
	 */
	public int getClassIndex ()
	{
		return _classIndex;
	}


	/**
	 *	Set the index of the ClassEntry describing the class to which
	 *	this entry applies.
	 */
	public void setClassIndex (int classIndex)
	{
		_classIndex = classIndex;
	}


	/**
	 *	Get the index in the constant pool of the NameAndTypeEntry
	 *	describing this entity.
	 */
	public int getNameAndTypeIndex ()
	{
		return _nameAndTypeIndex;
	}


	/**
	 *	Set the index in the constant pool of the NameAndTypeEntry
	 *	describing this entity.
	 */
	public void setNameAndTypeIndex (int nameAndTypeIndex)
	{
		_nameAndTypeIndex = nameAndTypeIndex;
	}


	public void readData (DataInput in)
		throws IOException
	{
		setClassIndex (in.readUnsignedShort ());
		setNameAndTypeIndex (in.readUnsignedShort ());
	}


	public void writeData (DataOutput out)
		throws IOException
	{
		out.writeShort (getClassIndex ());
		out.writeShort (getNameAndTypeIndex ());
	}


	public String getKey ()
	{
		return getType () + "|" + getClassIndex () 
			+ "|" + getNameAndTypeIndex ();
	}
}
