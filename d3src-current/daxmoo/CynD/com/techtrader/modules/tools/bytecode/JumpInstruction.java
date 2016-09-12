package com.techtrader.modules.tools.bytecode;


import java.util.*;
import java.io.*;

import com.techtrader.modules.tools.bytecode.visitor.*;


/**
 *	Represents an IF, GOTO, JSR, or similar instruction that specifies as
 *	its argument a position in the code block to jump to.
 *
 *	@author		Abe White
 */
public class JumpInstruction
	extends Instruction
	implements InstructionPtr
{
	protected int 			_offset = 0;
	protected Instruction	_target = null;


	protected JumpInstruction (Code owner, int opcode)
	{
		super (owner);
		_opcode = opcode;
	}


	/**
	 *	Set the byte offset for the jump instruction.
	 */
	public void setOffset (int offset)
	{
		_offset = offset;
		_target = null;
	}


	/**
	 *	Get the byte offset for the jump instruction.
	 */
	public int getOffset ()
	{
		if (_target != null)
			return _target.getByteIndex () - getByteIndex ();

		return _offset;
	}


	/**
 	 *	Set the instruction to jump to; the instruction must already be
	 *	added to the code block.  WARNING: if this instruction is later
	 *	deleted from the code block, the results are undefined.
 	 *	
	 *	@return		this Instruction, for method chaining
	 */
	public JumpInstruction setTarget (Instruction instruction)
	{
		_target = instruction;
		return this;
	}


	/**
 	 *	Get the current target instruction to jump to, if it has been set.
	 *	WARNING: if this instruction is later
	 *	deleted from the code block, the results are undefined.
	 */
	public Instruction getTarget ()
	{
		return _target;
	}	


	public void setMarkers (List instructions)
	{
		int jumpByteIndex = getByteIndex () + _offset;

		Instruction ins;
		for (Iterator i = instructions.iterator (); i.hasNext ();)
		{
			ins = (Instruction) i.next ();

			if (ins.getByteIndex () == jumpByteIndex)
			{
				setTarget (ins);
				break;
			}
		}
	}


	/**
 	 *	JumpInstructions are equal if they represent the same operation and 
	 *	the Instruction they jump to is the
	 *	same, or if the jump Instruction of either is unset.
	 */
	public boolean equals (Object other)
	{
		if (this == other)
			return true;
		if (!(other instanceof JumpInstruction))
			return false;
		if (!super.equals (other))
			return false;

		Instruction target = ((JumpInstruction) other).getTarget ();
		return (target == null || _target == null || target == _target);
	}


	public int getLength ()
	{
		switch (_opcode)
		{
		case GOTO_W:
		case JSR_W:
			return super.getLength () + 4;
		default:
			return super.getLength () + 2;
		}
	}


	public int getStackChange ()
	{
		switch (_opcode)
		{
		case IF_ACMPEQ:
		case IF_ACMPNE:
		case IF_ICMPEQ:
		case IF_ICMPNE:
		case IF_ICMPLT:
		case IF_ICMPGT:
		case IF_ICMPLE:
		case IF_ICMPGE:
			return -2;

		case IF_EQ:
		case IF_NE:
		case IF_LT:
		case IF_GT:
		case IF_LE:
		case IF_GE:
		case IF_NULL:
		case IF_NONNULL:
			return -1;

		case JSR:
			return 1;

		default:
			return 0;
		}
	}


	protected void copy (Instruction orig)
	{
		super.copy (orig);
		setOffset (((JumpInstruction) orig).getOffset ());
	}


	protected void readData (DataInput in)
		throws IOException
	{
		switch (_opcode)
		{
		case GOTO_W:
		case JSR_W:
			_offset = in.readInt ();
			break;
		default:
			_offset = in.readShort ();
		}
	}


	protected void writeData (DataOutput out)
		throws IOException
	{
		switch (_opcode)
		{
		case GOTO_W:
		case JSR_W:
			out.writeInt (getOffset ());
			break;
		default:
			out.writeShort (getOffset ());
		}
	}


	public void acceptVisit (BCVisitor visit)
	{
		visit.enterJumpInstruction (this);
		visit.exitJumpInstruction (this);
	}
}
