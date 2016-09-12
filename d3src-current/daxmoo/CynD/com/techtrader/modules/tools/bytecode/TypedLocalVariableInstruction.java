package com.techtrader.modules.tools.bytecode;


import java.util.*;


/**
 *	An instruction manipulating a local variable that varies with the
 *	variable type.
 *
 *	@author		Abe White
 */
public abstract class TypedLocalVariableInstruction
	extends LocalVariableInstruction
{
	protected static final Map 	_typeNames 	= new HashMap ();
	static
	{
		_typeNames.put ("int", int.class);
		_typeNames.put ("long", long.class);
		_typeNames.put ("float", float.class);
		_typeNames.put ("double", double.class);
		_typeNames.put ("boolean", int.class);
		_typeNames.put ("byte", int.class);
		_typeNames.put ("char", int.class);
		_typeNames.put ("short", int.class);
	}

	protected Class _type = null;


	protected TypedLocalVariableInstruction (Code owner)
	{
		super (owner);
	}


	/**
	 *	Get the type of variable to load; this is one of:
	 *	int, float, double, long, or Object.class.
	 */
	public Class getType ()
	{
		return _type;
	}


	/**
	 *	Get the type of variable to load; this is one of:
	 *	int, float, double, long, or java.lang.Object.
	 */
	public String getTypeName ()
	{
		if (_type == null)
			return null;

		return _type.getName ();
	}


	/**
	 *	Set the type of variable to load; Object types other than Object.class
	 *	will be demoted to Object.class, and primitives that have no direct
	 *	support (byte, char, boolean) will be conerted to int.class.
	 *
	 *	@return		this Instruction, for method chaining
	 */
	public TypedLocalVariableInstruction setType (Class type)
	{
		if (Object.class.isAssignableFrom (type))
			_type = Object.class;
		else if (byte.class.equals (type) || char.class.equals (type)
			|| boolean.class.equals (type) || short.class.equals (type))
			_type = int.class;
		else
			_type = type;
		
		calculateOpCode ();
		return this;
	}


	/**
	 *	Set the type to load by name.
	 *
	 *	@return		this Instruction, for method chaining
	 *	
	 *	@see	#setType(java.lang.Class)
	 */
	public TypedLocalVariableInstruction setTypeName (String name)
	{
		_type = (Class) _typeNames.get (name);
		if (_type == null && name != null)
			_type = Object.class;

		calculateOpCode ();
		return this;
	}

	
	/**
	 *	Two local variable instructions are equal if the local index they
	 *	reference is equal and their types are equal, 
	 *	or if either index is 0/unset.
	 */
	public boolean equals (Object other)
	{
		if (this == other)
			return true;
		if (!(other instanceof TypedLocalVariableInstruction))
			return false;
		if (!super.equals (other))
			return false;

		TypedLocalVariableInstruction ins = 
			(TypedLocalVariableInstruction) other;

		return _type == null || ins._type == null || _type.equals (ins._type);
	}


	protected void copy (Instruction orig)
	{
		super.copy (orig);
		_type = ((TypedLocalVariableInstruction) orig)._type;
	}
}
