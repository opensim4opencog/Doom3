package com.techtrader.modules.tools.bytecode;


import com.techtrader.modules.tools.bytecode.visitor.*;


/**
 *	Represents an instruction that has an argument of an index into the
 *	local variable array of the current frame.  This includes most of the 
 *	LOAD and STORE instructions.
 *	<p>
 *	The local variable array size is fixed by the 'maxLocals' property of 
 *	the code block.  Long and double types take up 2 local variable indexes.
 *	<p>  
 *	Parameter values to methods are loaded into the local variable array 
 *	prior to the execution of the first instruction.  The 0th-index of the 
 *	array is set to the instance of the class the method is being invoked on.  
 *
 *	@author		Abe White
 */
public abstract class LocalVariableInstruction
	extends Instruction
{
	protected int _index = -1;


	protected LocalVariableInstruction (Code owner)
	{
		super (owner);
	}


	/**
 	 *	Set the index of the local variable that this instruction should 
	 *	operate on.
 	 *	
	 *	@return		this Instruction, for method chaining
	 */
	public LocalVariableInstruction setIndex (int index)
	{
		_index = index;
		calculateOpCode ();

		return this;
	}


	/**
 	 *	Get the index of the local variable that this instruction should 
	 *	operate on.
	 */
	public int getIndex ()
	{
		return _index;
	}


	/**
	 *	Two local variable instructions are equal if the local index they
	 *	reference is equal or if either index is 0/unset.
	 */
	public boolean equals (Object other)
	{
		if (this == other)
			return true;
		if (!(other instanceof LocalVariableInstruction))
			return false;

		LocalVariableInstruction ins = (LocalVariableInstruction) other;
		int index = getIndex ();
		int insIndex = ins.getIndex ();

		return index == -1 || insIndex == -1 || index == insIndex;
	}


	/**
	 *	Subclasses with variable opcodes can use this method to be
	 *	notified that information possibly affecting the opcode has been
	 *	changed.
	 */
	protected void calculateOpCode ()
	{
	}


	protected void copy (Instruction orig)
	{
		super.copy (orig);
		_index = ((LocalVariableInstruction) orig)._index;
	}
}
