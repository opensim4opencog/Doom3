package com.techtrader.modules.tools.bytecode;


import java.io.*;

import com.techtrader.modules.tools.bytecode.visitor.*;


/**
 *	Represents the MULTIANEWARRAY opcode instruction, which creates a new
 *	multi-dimensional array.
 *
 *	@author		Abe White
 */
public class MultiANewArrayInstruction
	extends ClassInstruction
{
	private int _dims = 0;


	protected MultiANewArrayInstruction (Code owner)
	{
		super (owner, MULTIANEWARRAY);
	}


	/**
	 *	Set the dimensions of the array.
	 *	
	 *	@return		this Instruction, for method chaining
	 */
	public MultiANewArrayInstruction setDimensions (int dims)
	{
		_dims = dims;
		return this;
	}	


	/**
 	 *	Get the dimensions of the array.
	 */
	public int getDimensions ()
	{
		return _dims;
	}	


	/**
	 *	Two MULTIANEWARRAY instructions are equal if they have the same
	 *	type and dimensions, or if the type and dimensions of either
	 *	is unset.
	 */
	public boolean equals (Object other)
	{
		if (other == this)
			return true;
		if (!(other instanceof MultiANewArrayInstruction))
			return false;
		if (!super.equals (other))
			return false;

		MultiANewArrayInstruction ins = (MultiANewArrayInstruction) other;
		return _dims == 0 || ins._dims == 0 || _dims == ins._dims;
	}


	public int getLength ()
	{
		return super.getLength () + 1;
	}

	
	public int getStackChange ()
	{
		return -(getDimensions ()) + 1;
	}


	protected void copy (Instruction orig)
	{
		super.copy (orig);
		setDimensions (((MultiANewArrayInstruction) orig).getDimensions ());
	}

	
	protected void readData (DataInput in)
		throws IOException
	{
		super.readData (in);
		setDimensions (in.readUnsignedByte ());
	}


	protected void writeData (DataOutput out)
		throws IOException
	{
		super.writeData (out);
		out.writeByte (getDimensions ());
	}


	public void acceptVisit (BCVisitor visit)
	{
		visit.enterMultiANewArrayInstruction (this);
		visit.exitMultiANewArrayInstruction (this);
	}
}
