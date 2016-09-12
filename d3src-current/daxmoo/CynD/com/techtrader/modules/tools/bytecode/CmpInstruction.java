package com.techtrader.modules.tools.bytecode;


import com.techtrader.modules.tools.bytecode.visitor.*;


/**
 *	An instruction comparing two stack values that varies depending on the
 *	value type.
 *
 *	@author		Abe White
 */
public class CmpInstruction
	extends Instruction
{
	private Class 	_type 	= null;
	private int		_nan	= -1;	


	protected CmpInstruction (Code owner)
	{
		super (owner);
	}


	protected CmpInstruction (Code owner, int opcode, Class type, int nan)
	{
		super (owner);
		_opcode = opcode;
		_type = type;
		_nan = nan;
	}


	/**
	 *	Get the type of value to operate on; this is one of:
	 *	float, double, long.
	 */
	public Class getType ()
	{
		return _type;
	}


	/**
	 *	Get the type of value to operate on; this is one of:
	 *	float, double, long.
	 */
	public String getTypeName ()
	{
		if (_type == null)
			return null;

		return _type.getName ();
	}


	/**
	 *	Set the type of value to operate on; primitives that have no direct
	 *	support (byte, char, boolean, int) will be conerted to long.class.
	 *
	 *	@return		this Instruction, for method chaining
	 */
	public CmpInstruction setType (Class type)
	{
		if (float.class.equals (type))
			_type = float.class;
		else if (double.class.equals (type))
			_type = double.class;
		else
			_type = long.class;
		
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
	public CmpInstruction setTypeName (String name)
	{
		if ("float".equals (name))
			_type = float.class;
		else if ("double".equals (name))
			_type = double.class;
		else
			_type = long.class;

		calculateOpCode ();
		return this;
	}


	/**
	 *	Get the number that will be placed on the stack if this instruction
	 *	is of type float or double and one of the operands is NaN.  The
	 *	return value will be either 1 or -1.
	 */
	public int getNaNValue ()
	{
		if (_type == null || _type.equals (long.class))
			return 0;

		return _nan;
	}
	

	/**
	 *	Set the number that will be placed on the stack if this instruction
	 *	is of type float or double and one of the operands is NaN.  The
	 *	value must be either 1 or -1.
	 */
	public void setNaNValue (int nan)
	{
		_nan = nan;
	}


	
	/**
	 *	Two cmp instructions are equal if the type and NaN value they
	 *	reference is equal or the type of either instruction is unset.
	 */
	public boolean equals (Object other)
	{
		if (this == other)
			return true;
		if (!(other instanceof CmpInstruction))
			return false;
		if (!super.equals (other))
			return false;

		CmpInstruction ins = (CmpInstruction) other;
		if (_type == null || ins._type == null)
			return true;


		return _type.equals (ins._type) && _nan == ins._nan;
	}


	public int getStackChange ()
	{
		if (_type.equals (long.class) || _type.equals (double.class))
			return -3;

		return -1;
	}


	public void acceptVisit (BCVisitor visit)
	{
		visit.enterCmpInstruction (this);
		visit.exitCmpInstruction (this);
	}


	protected void copy (Instruction orig)
	{
		super.copy (orig);

		CmpInstruction ins = (CmpInstruction) orig;
		_type = ins._type;
		_nan = ins._nan;
	}


	private void calculateOpCode ()
	{
		if (_type.equals (long.class))
			_opcode = LCMP;
		else if (_type.equals (float.class))
			_opcode = (_nan == 1) ? FCMPG : FCMPL;
		else
			_opcode = (_nan == 1) ? DCMPG : DCMPL;
	}
}
