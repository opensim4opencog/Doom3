package com.techtrader.modules.tools.bytecode;


import java.io.*;

import com.techtrader.modules.tools.bytecode.visitor.*;


/**
 *	Represents the RET instruction used in the implementation of finally.
 *
 *	@author		Abe White
 */
public class RetInstruction
	extends LocalVariableInstruction
{
	protected RetInstruction (Code owner)
	{
		super (owner);
		_opcode = RET;
	}


	public boolean equals (Object other)
	{
		if (this == other)
			return true;
		if (!(other instanceof RetInstruction))
			return false;
		return super.equals (other);
	}


	public int getLength ()
	{
		return super.getLength () + 1;
	}


	protected void readData (DataInput in)
		throws IOException
	{
		_index = in.readUnsignedByte ();
	}


	protected void writeData (DataOutput out)
		throws IOException
	{
		out.writeByte (_index);
	}


	public void acceptVisit (BCVisitor visit)
	{
		visit.enterRetInstruction (this);
		visit.exitRetInstruction (this);
	}
}
