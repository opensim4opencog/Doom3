package com.techtrader.modules.tools.bytecode;


import com.techtrader.modules.tools.bytecode.visitor.*;


/**
 *	Represents the PUTFIELD or PUTSTATIC instruction.
 *
 *	@author		Abe White
 */
public class PutFieldInstruction
	extends FieldInstruction
{
	protected PutFieldInstruction (Code owner, int opcode)
	{
		super (owner, opcode);
	}


	public int getStackChange ()
	{
		int stack = -2;

		String type = getFieldTypeName ();
		if (type.equals ("long") || type.equals ("double"))
			stack++;

		if (_opcode == PUTSTATIC)
			stack++;

		return stack;
	}


	public void acceptVisit (BCVisitor visit)
	{
		visit.enterPutFieldInstruction (this);
		visit.exitPutFieldInstruction (this);
	}
}
