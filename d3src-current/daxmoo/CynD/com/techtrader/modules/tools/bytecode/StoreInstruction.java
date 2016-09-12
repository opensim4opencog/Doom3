package com.techtrader.modules.tools.bytecode;


import java.io.*;

import com.techtrader.modules.tools.bytecode.visitor.*;


/**
 *	Represents an instruction to store a value in a local variable from 
 *	the stack; can be any of astore*, istore*, etc.
 *
 *	@author		Abe White
 */
public class StoreInstruction
	extends TypedLocalVariableInstruction
{
	protected StoreInstruction (Code owner)
	{
		super (owner);
	}


	protected StoreInstruction (Code owner, int opcode, Class type, int index)
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
		if (!(other instanceof StoreInstruction))
			return false;
		return super.equals (other);
	}


	public int getLength ()
	{
		switch (_opcode)
		{
		case ISTORE:
		case LSTORE:
		case FSTORE:
		case DSTORE:
		case ASTORE:
			return super.getLength () + 1;
		default:
			return super.getLength ();
		}
	}


	public int getStackChange ()
	{
		if (_type.equals (long.class) || _type.equals (double.class))
			return -2;


		return -1;
	}


	protected void readData (DataInput in)
		throws IOException
	{
		switch (_opcode)
		{
		case ISTORE:
		case LSTORE:
		case FSTORE:
		case DSTORE:
		case ASTORE:
			_index = in.readUnsignedByte ();
		}
	}


	protected void writeData (DataOutput out)
		throws IOException
	{
		switch (_opcode)
		{
		case ISTORE:
		case LSTORE:
		case FSTORE:
		case DSTORE:
		case ASTORE:
			out.writeByte (_index);
		}
	}


	protected void calculateOpCode ()
	{
		// take advantage of the arrangement of the opcode values --
		// see Constants.java for details
		int marker = _opcodeTypes.indexOf (_type);
		if (_index < 4)
			_opcode = ISTORE_0 + (4 * marker) + _index;
		else
			_opcode = ISTORE + marker;
	}


	public void acceptVisit (BCVisitor visit)
	{
		visit.enterStoreInstruction (this);
		visit.exitStoreInstruction (this);
	}
}
