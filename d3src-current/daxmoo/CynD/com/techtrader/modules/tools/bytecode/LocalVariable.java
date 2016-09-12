package com.techtrader.modules.tools.bytecode;


import java.io.*;

import com.techtrader.modules.tools.bytecode.visitor.*;


/**
 *	Represents a local variable in a method.
 *	TODO: Add the ability to manipulate the local variable declaration
 *	at a high level.
 *	
 *	@author		Abe White
 */
public class LocalVariable
{
	private int	_startPc 			= 0;
	private int	_length				= 0;
	private int	_nameIndex 			= 0;
	private int	_descriptorIndex 	= 0;
	private int	_index 				= 0;

	private LocalVariableTableAttribute _owner = null;


	/**
	 *	Protected constructor.
	 */
	protected LocalVariable (LocalVariableTableAttribute owner)
	{
		_owner = owner;
	}


	/**
	 *	Used when the local is removed from the method, so that it can 
	 *	no longer affect the constant pool.
	 */
	protected void invalidate ()
	{
		_owner = null;
	}


	/**
	 *	Local variables are contained in LocalVariableTableAttributds.
	 */
	public LocalVariableTableAttribute getOwner ()
	{
		return _owner;
	}


	/**
	 *	Get the index into local variable table of the current frame
	 *	for this variable.
	 */	
	public int getIndex ()
	{
		return _index;
	}


	/**
	 *	Set the index into local variable table of the current frame
	 *	for this variable.
	 */	
	public void setIndex (int index)
	{
		_index = index;
	}


	/**	
 	 *	Get the start position of the program counter at which this local
	 *	variable has a value.  This is an index into the code byte array.
	 */
	public int getStartPc ()
	{
		return _startPc;
	}


	/**	
 	 *	Set the start position of the program counter at which this local
	 *	variable has a value.  This is an index into the code byte array.
	 */
	public void setStartPc (int startPc)
	{
		_startPc = startPc;
	}


	/**
	 *	Get the number of bytes for which this local variable has a value in
	 *	the code byt array.
	 */
	public int getLength ()
	{
		return _length;
	}


	/**
	 *	Set the number of bytes for which this local variable has a value in
	 *	the code byt array.
	 */
	public void setLength (int length)
	{
		_length = length;
	}


	/**
	 *	Get the index into the constant pool of the UTF8Entry holding the name
	 *	of this variable.
	 */
	public int getNameIndex ()
	{
		return _nameIndex;
	}


	/**
	 *	Set the index into the constant pool of the UTF8Entry holding the name
	 *	of this variable.
	 */
	public void setNameIndex (int nameIndex)
	{
		_nameIndex = nameIndex;
	}


	/**
	 *	Get the name of this local.
	 */
	public String getName ()
	{
		return _owner.getPool ().getUTF (_nameIndex);
	}


	/**
	 *	Set the name of this local.
	 */
	public void setName (String name)
	{
		_nameIndex = _owner.getPool ().setUTF (0, name);
	}


	/**
	 *	Get the index in the constant pool of the UTF entry holding the 
	 *	descriptor of this local.
	 */
	public int getDescriptorIndex ()
	{
		return _descriptorIndex;
	}


	/**
	 *	Set the index in the constant pool of the UTF entry holding the
	 *	descriptor of this local.
	 */
	public void setDescriptorIndex (int index)
	{
		_descriptorIndex = index;
	}


	/**
	 *	Get the name of the type of this local.
	 */
	public String getTypeName ()
	{
		return BCHelper.getExternalForm (_owner.getPool ().getUTF 
			(_descriptorIndex), true);
	}


	/**
	 *	Set the type name for this local.
	 */
	public void setTypeName (String name)
	{
		_descriptorIndex = _owner.getPool ().setUTF 
			(0, BCHelper.getInternalForm (name, true));
	}


	/**
	 *	Get the Class object for the type of this field.
	 */
	public Class getType ()
		throws ClassNotFoundException
	{
		return BCHelper.classForName (_owner.getPool ().
			getUTF (_descriptorIndex));
	}


	/**
	 *	Set the type of this field.
	 */
	public void setType (Class type)
	{
		setTypeName (type.getName ());
	}


	protected void readData (DataInput in)
		throws IOException
	{
		setStartPc (in.readUnsignedShort ());
		setLength (in.readUnsignedShort ());
		setNameIndex (in.readUnsignedShort ());
		setDescriptorIndex (in.readUnsignedShort ());
		setIndex (in.readUnsignedShort ());
	}


	protected void writeData (DataOutput out)
		throws IOException
	{
		out.writeShort (getStartPc ());
		out.writeShort (getLength ());
		out.writeShort (getNameIndex ());
		out.writeShort (getDescriptorIndex ());
		out.writeShort (getIndex ());
	}


	public void acceptVisit (BCVisitor visit)
	{
		visit.enterLocalVariable (this);
		visit.exitLocalVariable (this);
	}
}
