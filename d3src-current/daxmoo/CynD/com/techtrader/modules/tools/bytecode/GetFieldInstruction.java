package com.techtrader.modules.tools.bytecode;


import com.techtrader.modules.tools.bytecode.visitor.*;


/**
 *	Represents the GETFIELD or GETSTATIC instruction.
 *
 *	@author		Abe White
 */
public class GetFieldInstruction
	extends FieldInstruction
{
	protected GetFieldInstruction (Code owner, int opcode)
	{
		super (owner, opcode);
	}


	public int getStackChange ()
	{
		int stack = 0;

		String type = getFieldTypeName ();
		if (type.equals ("long") || type.equals ("double"))
			stack++;

		if (_opcode == GETSTATIC)
			stack++;

		return stack;
	}


	public void acceptVisit (BCVisitor visit)
	{
		visit.enterGetFieldInstruction (this);	
		visit.exitGetFieldInstruction (this);	
	}
}
