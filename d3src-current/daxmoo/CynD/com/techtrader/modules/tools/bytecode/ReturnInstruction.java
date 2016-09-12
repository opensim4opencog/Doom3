package com.techtrader.modules.tools.bytecode;


import java.util.*;
import java.io.*;

import com.techtrader.modules.tools.bytecode.visitor.*;


/**
 *	Represents a return instruction.
 *
 *	@author		Abe White
 */
public class ReturnInstruction
	extends Instruction
{
	protected static final Map 	_typeNames 	= new HashMap ();
	static
	{
		_typeNames.put ("void", void.class);
		_typeNames.put ("int", int.class);
		_typeNames.put ("long", long.class);
		_typeNames.put ("float", float.class);
		_typeNames.put ("double", double.class);
		_typeNames.put ("boolean", int.class);
		_typeNames.put ("byte", int.class);
		_typeNames.put ("char", int.class);
		_typeNames.put ("short", int.class);
	}

	private Class _type = null;


	protected ReturnInstruction (Code owner)
	{
		super (owner);
	}


	protected ReturnInstruction (Code owner, int opcode, Class type)
	{
		super (owner);
		_opcode = opcode;
		_type = type;
	}


	/**
	 *	Get the type to return; this is one of:
	 *	int, float, double, long, void, or Object.class.
	 *	If the type has not been set, this method will return null.
	 */
	public Class getType ()
	{
		return _type;
	}


	/**
	 *	Get the type to return; this is one of:
	 *	int, float, double, long, void, or java.lang.Object.
	 *	If the type has not been set, this method will return null.
	 */
	public String getTypeName ()
	{
		if (_type == null)
			return null;

		return _type.getName ();
	}


	/**
	 *	Set the type to rturn; Object types other than Object.class
	 *	will be demoted to Object.class, and primitives that have no direct
	 *	support (boolean, char, short, byte) will be converted to int.class.
	 *
	 *	@return	this Instruction, for method chaining
	 */
	public ReturnInstruction setType (Class type)
	{
		if (Object.class.isAssignableFrom (type))
			_type = Object.class;
		else if (boolean.class.equals (type) || char.class.equals (type)
			|| byte.class.equals (type) || short.class.equals (type))
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
	public ReturnInstruction setTypeName (String name)
	{
		_type = (Class) _typeNames.get (name);
		if (_type == null && name != null)
			_type = Object.class;

		calculateOpCode ();
		return this;
	}


	/**
	 *	Two return instructions are equal if the types they
	 *	reference are equal or if either unset.
	 */
	public boolean equals (Object other)
	{
		if (this == other)
			return true;
		if (!(other instanceof ReturnInstruction))
			return false;

		ReturnInstruction ins = (ReturnInstruction) other;

		return (_type == null || ins._type == null
			|| _type.equals (ins._type));
	}


	public int getStackChange ()
	{
		switch (_opcode)
		{
		case RETURN:
			return 0;
		case LRETURN:
		case DRETURN:
			return -2;
		default:
			return -1;
		}
	}


	protected void copy (Instruction orig)
	{
		super.copy (orig);
		_type = ((ReturnInstruction) orig)._type;
	}


	private void calculateOpCode ()
	{
		if (void.class.equals (_type))
			_opcode = RETURN;
		else
			_opcode = IRETURN + _opcodeTypes.indexOf (_type);
	}


	public void acceptVisit (BCVisitor visit)
	{
		visit.enterReturnInstruction (this);
		visit.exitReturnInstruction (this);
	}
}
