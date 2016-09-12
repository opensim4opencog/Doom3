package com.techtrader.modules.tools.bytecode;


import java.util.*;
import java.io.*;

import com.techtrader.modules.tools.bytecode.visitor.*;


/**
 *	An Instruction represents an opcode in a method of a Class.
 *
 *	@author 	Abe White
 */
public class Instruction
	implements Constants, VisitAcceptor
{
	// in all opcodes that are typed, the types appear in this order; useful
	// for calculating opcodes based on the type to operate on
	protected static final List _opcodeTypes = new ArrayList (6);
	static
	{
		_opcodeTypes.add (int.class);
		_opcodeTypes.add (long.class);
		_opcodeTypes.add (float.class);
		_opcodeTypes.add (double.class);
		_opcodeTypes.add (Object.class);
		_opcodeTypes.add (byte.class);
		_opcodeTypes.add (char.class);
		_opcodeTypes.add (short.class);
	}

	protected Code	_owner		= null;
	protected int 	_opcode 	= NOP;
	protected int	_byteIndex	= 0;

	
	/**
	 *	Protected constructor.
	 */
	protected Instruction (Code owner)
	{
		_owner = owner;
	}


	/**
	 *	Get the Code block that owns this Instruction.
	 */
	public Code getOwner ()
	{
		return _owner;
	}


	/**
 	 *	Get the name of this opcode.
	 */
	public String getName ()
	{
		return OPCODE_NAMES[_opcode];
	}


	/**
	 *	Get the opcode this instruction represents.
	 */
	public int getOpCode ()
	{
		return _opcode;
	}


	/**
	 *	Set the opcode this instruction represents.
	 */
	protected void setOpCode (int opcode)
	{
		_opcode = opcode;
	}


	/**
	 *	Set the index in the method code byte block at which this opcode starts.
	 *	Some opcodes rely on knowing where they appear in the method block
	 *	to be able to calculate their byte representations; therefore, this
	 *	method must be called before getLength(), readData(), or writeData().
	 */
	public void setByteIndex (int index)
	{
		_byteIndex = index;
	}


	/**
	 *	Get the index in the method code byte block at which this opcode starts.
	 */
	public int getByteIndex ()
	{
		return _byteIndex;
	}


	/**
	 *	Return the length in bytes of this opcode, including all arguments.  
	 *	This method	should be overridden by opcodes that take arguments.
	 */
	public int getLength ()
	{
		return 1;
	}


	/**
	 *	Return the number of stack positions this instruction pushes
	 *	or pops during its execution.  
	 *
	 *	@return		0 if the stack is not affected by this instruction, a
	 *				positive number if it pushes onto the stack, and a negative
	 *				number if it pops from the stack
	 */
	public int getStackChange ()
	{
		return 0;
	}


	/**
	 *	Instructions are equal if their opcodes are the same.  Subclasses
	 *	should override this method to perform a template comparison: 
	 *	Instructions should compare equal to other Instructions of the same
	 *	type where the data is either the same or the data is unset.
	 */
	public boolean equals (Object other)
	{
		return (other instanceof Instruction
			&& ((Instruction) other).getOpCode () == _opcode);
	}	


	protected void copy (Instruction orig)
	{
		setByteIndex (orig.getByteIndex ());
	}


	/**
	 *	Read the arguments for this opcode from the given stream. 
	 *	This method should be overridden by opcodes that take arguments.
	 */
	protected void readData (DataInput in)
		throws IOException
	{
	}


	/**
	 *	Write the arguments for this opcode to the given stream. 
	 *	This method should be overridden by opcodes that take arguments.
	 */
	protected void writeData (DataOutput out)
		throws IOException
	{
	}


	public void acceptVisit (BCVisitor visit)
	{
	}
}
