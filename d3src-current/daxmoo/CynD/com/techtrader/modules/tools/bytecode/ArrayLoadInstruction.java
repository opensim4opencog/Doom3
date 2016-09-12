package com.techtrader.modules.tools.bytecode;


import com.techtrader.modules.tools.bytecode.visitor.*;


/**
 *	Represents any array load instruction.
 *
 *	@author		Abe White
 */
public class ArrayLoadInstruction
	extends ArrayInstruction
{
	protected ArrayLoadInstruction (Code owner)
	{
		super (owner);
	}


	protected ArrayLoadInstruction (Code owner, int opcode, Class type)
	{
		super (owner, opcode, type);
	}


	public int getStackChange ()
	{
		if (_opcode == DALOAD || _opcode == LALOAD)
			return -2;

		return -1;
	}


	public boolean equals (Object other)
	{
		if (this == other)
			return true;
		if (!(other instanceof ArrayLoadInstruction))
			return false;
		return super.equals (other);
	}


	protected void calculateOpCode ()
	{
		_opcode = IALOAD + _opcodeTypes.indexOf (_type);
	}


	public void acceptVisitor (BCVisitor visit)
	{
		visit.enterArrayLoadInstruction (this);
		visit.exitArrayLoadInstruction (this);
	}
}
