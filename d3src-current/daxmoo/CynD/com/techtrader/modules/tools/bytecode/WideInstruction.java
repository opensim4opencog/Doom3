package com.techtrader.modules.tools.bytecode;


import java.io.*;

import com.techtrader.modules.tools.bytecode.visitor.*;


/**
 *	Representation of the WIDE instruction, which is used to allow other
 *	instructions to index values beyond what they can normally index baed
 *	on the length of their arguments.
 *
 *	@author		Abe White
 */
public class WideInstruction
	extends IIncInstruction
{
	private int _ins = 0;


	protected WideInstruction (Code owner)
	{
		super (owner);
		_opcode = WIDE;
	}


	/**
	 *	Set the type of instruction this wide instruction modifies.
	 *
	 *	@return		this Instruction, for method chaining
	 */
	public WideInstruction iinc ()
	{
		_ins = IINC;
		return this;
	}


	/**
	 *	Set the type of instruction this wide instruction modifies.
	 *
	 *	@return		this Instruction, for method chaining
	 */
	public WideInstruction ret ()
	{
		_ins = RET;
		return this;
	}


	/**
	 *	Set the type of instruction this wide instruction modifies.
	 *
	 *	@return		this Instruction, for method chaining
	 */
	public WideInstruction iload ()
	{
		_ins = ILOAD;
		return this;
	}


	/**
	 *	Set the type of instruction this wide instruction modifies.
	 *
	 *	@return		this Instruction, for method chaining
	 */
	public WideInstruction fload ()
	{
		_ins = FLOAD;
		return this;
	}


	/**
	 *	Set the type of instruction this wide instruction modifies.
	 *
	 *	@return		this Instruction, for method chaining
	 */
	public WideInstruction aload ()
	{
		_ins = ALOAD;
		return this;
	}


	/**
	 *	Set the type of instruction this wide instruction modifies.
	 *
	 *	@return		this Instruction, for method chaining
	 */
	public WideInstruction lload ()
	{
		_ins = LLOAD;
		return this;
	}


	/**
	 *	Set the type of instruction this wide instruction modifies.
	 *
	 *	@return		this Instruction, for method chaining
	 */
	public WideInstruction dload ()
	{
		_ins = DLOAD;
		return this;
	}


	/**
	 *	Set the type of instruction this wide instruction modifies.
	 *
	 *	@return		this Instruction, for method chaining
	 */
	public WideInstruction istore ()
	{
		_ins = ISTORE;
		return this;
	}


	/**
	 *	Set the type of instruction this wide instruction modifies.
	 *
	 *	@return		this Instruction, for method chaining
	 */
	public WideInstruction fstore ()
	{
		_ins = FSTORE;
		return this;
	}


	/**
	 *	Set the type of instruction this wide instruction modifies.
	 *
	 *	@return		this Instruction, for method chaining
	 */
	public WideInstruction astore ()
	{
		_ins = ASTORE;
		return this;
	}


	/**
	 *	Set the type of instruction this wide instruction modifies.
	 *
	 *	@return		this Instruction, for method chaining
	 */
	public WideInstruction lstore ()
	{
		_ins = LSTORE;
		return this;
	}


	/**
	 *	Set the type of instruction this wide instruction modifies.
	 *
	 *	@return		this Instruction, for method chaining
	 */
	public WideInstruction dstore ()
	{
		_ins = DSTORE;
		return this;
	}


	/**
	 *	Set the code of the instruction to modify.  Should be one of:
	 *	IINC, RET, ILOAD, ALOAD, FLOAD, DLOAD, LLOAD, ISTORE, ASTORE, FSTORE,
	 *	DSTORE, LSTORE.
	 *
	 *	@return		this Instruction, for method chaining
	 */
	public WideInstruction setInstruction (int code)
	{
		_ins = code;
		return this;
	}


	/**
 	 *	Get the code of the instruction to modify; this will return one
	 *	of the constants defined in	{@link Constants}.
	 */
	public int getInstruction ()
	{
		return _ins;
	}	

	
	protected void copy (Instruction orig)
	{
		super.copy (orig);
		setInstruction (((WideInstruction) orig).getInstruction ());
	}


	public int getLength ()
	{
		// don't call super

		// opcode, ins, index
		int length = 1 + 1 + 2;
		
		// increment
		if (_ins == IINC)
			length += 2;

		return length;	
	}


	public int getStackChange ()
	{
		switch (_ins)
		{
		case ILOAD:
		case FLOAD:
		case ALOAD:
			return 1;

		case LLOAD:
		case DLOAD:
			return 2;

		case ISTORE:
		case FSTORE:
		case ASTORE:
			return -1;

		case LSTORE:
		case DSTORE:
			return -2;

		default:
			return 0;
		}
	}


	protected void readData (DataInput in)
		throws IOException
	{
		// don't call super

		_ins = in.readUnsignedByte ();
		setIndex (in.readUnsignedShort ());
		if (_ins == IINC)
			setIncrement (in.readUnsignedShort ());
	}


	protected void writeData (DataOutput out)
		throws IOException
	{
		// don't call super

		out.writeByte (_ins);
		out.writeShort (getIndex ());
		if (_ins == IINC)
			out.writeShort (getIncrement ());
	}


	public void acceptVisit (BCVisitor visit)
	{
		visit.enterWideInstruction (this);
		visit.exitWideInstruction (this);
	}
}
