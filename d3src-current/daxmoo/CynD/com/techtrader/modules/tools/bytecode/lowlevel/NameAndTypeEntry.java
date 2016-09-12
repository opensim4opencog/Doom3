package com.techtrader.modules.tools.bytecode.lowlevel;


import java.io.*;

import com.techtrader.modules.tools.bytecode.visitor.*;


/**
 *	Constant pool entry containing indexes referencing a name and a type.
 *	
 *	@author		Abe White
 */
public class NameAndTypeEntry
	implements Entry, LowLevelConstants
{
	private int _nameIndex 			= 0;
	private int _descriptorIndex 	= 0;


	public int getType ()
	{
		return ENTRY_NAME_AND_TYPE;
	}


	/**
	 *	Get the index into the constant pool of a UTF8Entry containing
	 *	the name of this entity.
	 */
	public int getNameIndex ()
	{
		return _nameIndex;
	}


	/**
	 *	Set the index into the constant pool of a UTF8Entry containing
	 *	the name of this entity.
	 */
	public void setNameIndex (int nameIndex)
	{
		_nameIndex = nameIndex;
	}


	/**
	 *	Get the index into the constant pool of a UTF8Entry containing
	 *	the descriptor for this entity.
	 */
	public int getDescriptorIndex ()
	{
		return _descriptorIndex;
	}


	/**
	 *	Set the index into the constant pool of a UTF8Entry containing
	 *	the descriptor for this entity.
	 */
	public void setDescriptorIndex (int descriptorIndex)
	{
		_descriptorIndex = descriptorIndex;
	}


	public void readData (DataInput in)
		throws IOException
	{
		setNameIndex (in.readUnsignedShort ());
		setDescriptorIndex (in.readUnsignedShort ());
	}


	public void writeData (DataOutput out)
		throws IOException
	{
		out.writeShort (getNameIndex ());
		out.writeShort (getDescriptorIndex ());
	}


	public String getKey ()
	{
		return getType () + "|" + getNameIndex () 
			+ "|" + getDescriptorIndex ();
	}


	public void acceptVisit (BCVisitor visit)
	{
		visit.enterNameAndTypeEntry (this);
		visit.exitNameAndTypeEntry (this);
	}
}
