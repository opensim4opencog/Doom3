package com.techtrader.modules.tools.bytecode;


import java.util.*;

import com.techtrader.modules.tools.bytecode.visitor.*;


/**
 *	Represents one of the math operations defined in the 
 *	{@link Constants} interface.  
 *	Changing the type or operation of the instruction will automatically
 *	update the underlying opcode.  Note, however, that some operations
 *	cannot act on floating point types.
 *
 *	@author		Abe White
 */
public class MathInstruction
	extends Instruction
{
	private static final Map _typeNames = new HashMap ();
	static
	{
		_typeNames.put ("long", long.class);
		_typeNames.put ("float", float.class);
		_typeNames.put ("double", double.class);
	}

	private Class	_type		= null;
	private int		_operation	= -1;


	protected MathInstruction (Code owner)
	{
		super (owner);
	}


	protected MathInstruction (Code owner, int opcode, int operation, 
		Class type)
	{
		super (owner);
		_opcode = opcode;
		_operation = operation;
		_type = type;
	}


	/**
 	 *	Set the math operation to be performed.  This should be one of the
	 *	math constant defined in {@link Constants}.
 	 *	
	 *	@return		this Instruction, for method chaining
	 */
	public MathInstruction setOperation (int operation)
	{
		_operation = operation;
		calculateOpCode ();
		return this;
	}


	/**
 	 *	Return the operation for this math instruction; will be one of the
	 *	math constant defined in {@link Constants}, or -1 if
 	 *	unset.
	 */
	public int getOperation ()
	{
		return _operation;
	}


	/**
	 *	Get the type of args to operation on; will be one of:
	 *	int, float, double, long.
	 *	If the type has not been set, this method will return null.
	 */
	public Class getType ()
	{
		return _type;
	}


	/**
	 *	Set the type of args to operate on.  Types without direct support are
	 *	demoted to int.class.
	 *
	 *	@return	this Instruction, for method chaining
	 */
	public MathInstruction setType (Class type)
	{
		if (float.class.equals (type) || double.class.equals (type)
			|| long.class.equals (type))
			_type = type;
		else
			_type = int.class;
		
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
	public MathInstruction setTypeName (String name)
	{
		_type = (Class) _typeNames.get (name);
		if (_type == null && name != null)
			_type = int.class;

		calculateOpCode ();
		return this;
	}


	/**
	 *	MathInstructions are equal if they have the same operation and type,
	 *	or the type of either is unset.
	 */
	public boolean equals (Object other)
	{
		if (this == other)
			return true;
		if (!(other instanceof MathInstruction))
			return false;

		MathInstruction ins = (MathInstruction) other;

		boolean opEq = (_operation == -1 || ins._operation == -1
			|| _operation == ins._operation);
		boolean typeEq = (_type == null || ins._type == null
			|| _type.equals (ins._type));

		return opEq && typeEq;
	}


	public int getStackChange ()
	{
		if (_operation == MATH_NEG)
			return 0;

		if (_type.equals (long.class) || _type.equals (double.class))
			return -2;

		return -1;
	}


	protected void copy (Instruction orig)
	{
		super.copy (orig);

		MathInstruction mi = (MathInstruction) orig;
		_type = mi._type;
		_operation = mi._operation;
	}


	/**
	 *	Helper method to calculate the correct opcode for this instruction.
	 *	Takes advantage of the arrangements of the opcodes and constants.
	 */
	private void calculateOpCode ()
	{
		_opcode = _operation + _opcodeTypes.indexOf (_type);
	}


	public void acceptVisit (BCVisitor visit)
	{
		visit.enterMathInstruction (this);
		visit.exitMathInstruction (this);
	}
}
