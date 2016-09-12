package com.techtrader.modules.tools.bytecode;


import com.techtrader.modules.tools.bytecode.visitor.*;


/**
 *	Represents an instruction that manipulates the stack of the current
 *	frame.  Using the setType methods is a hint about the type being 
 *	manipulated that might cause this Instruction to use the wide version 
 *	of the opcode it represents (if manipulating a long or double).  This
 *	saves the developer from having to decide at compile time whether to
 *	use pop() or pop2(), etc.
 *
 *	@author		Abe White
 */
public class StackInstruction
	extends Instruction
{
	protected StackInstruction (Code owner, int opcode)
	{
		super (owner);
		_opcode = opcode;
	}


	/**
	 *	Set the type that is being manipulated; this might cause the
	 *	instruction to change to the wide version of the opcode it represents.
	 *
	 *	@return	this Instruction, for method chaining
	 */
	public StackInstruction setType (Class type)
	{
		boolean wide = type.equals (long.class) || type.equals (double.class);
		calculateOpCode (wide);
		return this;
	}


	/**
	 *	Set the type to manipulate by name.
	 *
	 *	@return	this Instruction, for method chaining
	 *	
	 *	@see	#setType(java.lang.Class)
	 */
	public StackInstruction setTypeName (String name)
	{
		boolean wide = name.equals ("long") || name.equals ("double");
		calculateOpCode (wide);
		return this;
	}


	public int getStackChange ()
	{
		switch (_opcode)
		{
		case POP:
			return -1;

		case POP2:
			return -2;

		case DUP:
		case DUP_X1:
		case DUP_X2:
			return 1;

		case DUP2:
		case DUP2_X1:
		case DUP2_X2:
			return 2;

		default:
			return 0;
		}
	}


	/**
	 *	Helper method to change the opcode of this instruction if need be.
	 */
	private void calculateOpCode (boolean wide)
	{
		switch (_opcode)
		{
		case POP:
			if (wide) _opcode = POP2;
			break;
		case POP2:
			if (!wide) _opcode = POP;
			break;
		case DUP:
			if (wide) _opcode = DUP2;
			break;
		case DUP2:
			if (!wide) _opcode = DUP;
			break;
		case DUP_X1:
			if (wide) _opcode = DUP2_X1;
			break;
		case DUP2_X1:
			if (!wide) _opcode = DUP_X1;
			break;
		case DUP_X2:
			if (wide) _opcode = DUP2_X2;
			break;
		case DUP2_X2:
			if (!wide) _opcode = DUP_X2;
			break;
		}
	}


	public void acceptVisit (BCVisitor visit)
	{
		visit.enterStackInstruction (this);
		visit.exitStackInstruction (this);
	}
}
