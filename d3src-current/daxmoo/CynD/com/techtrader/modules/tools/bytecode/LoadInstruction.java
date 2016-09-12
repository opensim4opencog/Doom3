package com.techtrader.modules.tools.bytecode;


import java.io.*;

import com.techtrader.modules.tools.bytecode.visitor.*;


/**
 *	Represents an instruction to load a value from a local variable onto 
 *	the stack; can be any of aload_*, iload_*, etc.
 *
 *	@author		Abe White
 */
public class LoadInstruction
	extends TypedLocalVariableInstruction
{
	protected LoadInstruction (Code owner)
	{
		super (owner);
	}


	protected LoadInstruction (Code owner, int opcode, Class type, int index)
	{
		super (owner);
		_opcode = opcode;
		_type = type;
		_index = index;
	}


	public boolean equals (Object other)
	{
		if (this == other)
			return true;
		if (!(other instanceof LoadInstruction))
			return false;
		return super.equals (other);
	}


	public int getLength ()
	{
		switch (_opcode)
		{
		case ILOAD:
		case LLOAD:
		case FLOAD:
		case DLOAD:
		case ALOAD:
			return super.getLength () + 1;
		default:
			return super.getLength ();
		}
	}


	public int getStackChange ()
	{
		if (_type.equals (long.class) || _type.equals (double.class))
			return 2;

		return 1;
	}


	protected void readData (DataInput in)
		throws IOException
	{
		switch (_opcode)
		{
		case ILOAD:
		case LLOAD:
		case FLOAD:
		case DLOAD:
		case ALOAD:
			_index = in.readUnsignedByte ();
		}
	}


	protected void writeData (DataOutput out)
		throws IOException
	{
		switch (_opcode)
		{
		case ILOAD:
		case LLOAD:
		case FLOAD:
		case DLOAD:
		case ALOAD:
			out.writeByte (_index);
		}
	}


	protected void calculateOpCode ()
	{
		// take advantage of the arrangement of the opcode values --
		// see Constants.java for details
		int marker = _opcodeTypes.indexOf (_type);
		if (_index < 4)
			_opcode = ILOAD_0 + (4 * marker) + _index;
		else
			_opcode = ILOAD + marker;
	}


	public void acceptVisit (BCVisitor visit)
	{
		visit.enterLoadInstruction (this);
		visit.exitLoadInstruction (this);
	}
}
