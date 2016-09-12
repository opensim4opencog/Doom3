package com.techtrader.modules.tools.bytecode;


import com.techtrader.modules.tools.bytecode.visitor.*;


/**
 *	Represents any array store instruction.
 *
 *	@author		Abe White
 */
public class ArrayStoreInstruction
	extends ArrayInstruction
{
	protected ArrayStoreInstruction (Code owner)
	{
		super (owner);
	}


	protected ArrayStoreInstruction (Code owner, int opcode, Class type)
	{
		super (owner, opcode, type);
	}


	public int getStackChange ()
	{
		if (_opcode == DASTORE || _opcode == LASTORE)
			return -4;

		return -3;
	}


	public boolean equals (Object other)
	{
		if (this == other)
			return true;
		if (!(other instanceof ArrayStoreInstruction))
			return false;
		return super.equals (other);
	}


	protected void calculateOpCode ()
	{
		_opcode = IASTORE + _opcodeTypes.indexOf (_type);
	}


	public void acceptVisitor (BCVisitor visit)
	{
		visit.enterArrayStoreInstruction (this);
		visit.exitArrayStoreInstruction (this);
	}
}
