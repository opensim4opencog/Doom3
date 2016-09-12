package com.techtrader.modules.tools.bytecode;


import java.io.*;

import com.techtrader.modules.tools.bytecode.visitor.*;


/**
 *	Represents the IINC instruction.
 *
 *	@author		Abe White
 */
public class IIncInstruction
	extends LocalVariableInstruction
{
	private int _inc = 0;

	
	protected IIncInstruction (Code owner)
	{
		super (owner);
		_opcode = IINC;
	}


	/**
	 *	Set the increment on this IINC instruction.
	 *	
	 *	@return		this Instruction, for method chaining
	 */
	public IIncInstruction setIncrement (int val)
	{
		_inc = val;
		return this;
	}


	/**
	 *	Return the increment for this IINC instruction.	
 	 */
	public int getIncrement ()
	{
		return _inc;
	}


	public boolean equals (Object other)
	{
		if (this == other)
			return true;
		if (!(other instanceof IIncInstruction))
			return false;
		if (!super.equals (other))
			return false;

		IIncInstruction inc = (IIncInstruction) other;

		return (_inc == 0 || inc._inc == 0 || _inc == inc._inc);
	}


	public int getLength ()
	{
		return super.getLength () + 2;
	}


	protected void copy (Instruction other)
	{
		super.copy (other);
		setIncrement (((IIncInstruction) other).getIncrement ());	
	}


	protected void readData (DataInput in)
		throws IOException
	{
		setIndex (in.readUnsignedByte ());
		setIncrement (in.readByte ());
	}


	protected void writeData (DataOutput out)
		throws IOException
	{
		out.writeByte (getIndex ());
		out.writeByte (getIncrement ());
	}


	public void acceptVisit (BCVisitor visit)
	{
		visit.enterIIncInstruction (this);	
		visit.exitIIncInstruction (this);	
	}
}
