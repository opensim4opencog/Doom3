package com.techtrader.modules.tools.bytecode;


import java.util.*;


/**
 *	Represents any array load or store instruction.
 *
 *	@author		Abe White
 */
public abstract class ArrayInstruction
	extends Instruction
{
	protected static final Map 	_typeNames 	= new HashMap ();
	static
	{
		_typeNames.put ("int", int.class);
		_typeNames.put ("boolean", int.class);
		_typeNames.put ("long", long.class);
		_typeNames.put ("float", float.class);
		_typeNames.put ("double", double.class);
		_typeNames.put ("byte", byte.class);
		_typeNames.put ("char", char.class);
		_typeNames.put ("short", short.class);
	}

	protected Class	_type = null;


	protected ArrayInstruction (Code owner)
	{
		super (owner);
	}


	protected ArrayInstruction (Code owner, int opcode, Class type)
	{
		super (owner);
		_opcode = opcode;
		_type = type;
	}


	/**
	 *	Get the type of array to load; this is one of:
	 *	int, float, double, long, char, byte, short, or Object.class.
	 *	If the type has not been set, this method will return null.
	 */
	public Class getType ()
	{
		return _type;
	}


	/**
	 *	Get the type of array to load; this is one of:
	 *	int, float, double, long, char, byte, short, or java.lang.Object.
	 *	If the type has not been set, this method will return null.
	 */
	public String getTypeName ()
	{
		if (_type == null)
			return null;

		return _type.getName ();
	}


	/**
	 *	Set the type of array to load; Object types other than Object.class
	 *	will be demoted to Object.class, and primitives that have no direct
	 *	support (boolean) will be converted to int.class.
	 *
	 *	@return	this Instruction, for method chaining
	 */
	public ArrayInstruction setType (Class type)
	{
		if (Object.class.isAssignableFrom (type))
			_type = Object.class;
		else if (boolean.class.equals (type))
			_type = int.class;
		else
			_type = type;
		
		calculateOpCode ();
		return this;
	}


	/**
	 *	Set the type to load by name.
	 *
	 *	@return	this Instruction, for method chaining
	 *	
	 *	@see	#setType(java.lang.Class)
	 */
	public ArrayInstruction setTypeName (String name)
	{
		_type = (Class) _typeNames.get (name);
		if (_type == null && name != null)
			_type = Object.class;

		calculateOpCode ();
		return this;
	}


	/**
	 *	Two array instructions are equal if the index and type they
	 *	reference are equal or if either index is 0/unset.
	 */
	public boolean equals (Object other)
	{
		if (this == other)
			return true;
		if (!(other instanceof ArrayInstruction))
			return false;

		ArrayInstruction ins = (ArrayInstruction) other;

		return (_type == null || ins._type == null || _type.equals (ins._type));
	}


	protected void copy (Instruction orig)
	{
		super.copy (orig);
		_type = (((ArrayInstruction) orig)._type);
	}


	/**
	 *	Subclasses with variable opcodes can use this method to be
	 *	notified that information possibly affecting the opcode has been
	 *	changed.
	 */
	protected abstract void calculateOpCode ();
}
