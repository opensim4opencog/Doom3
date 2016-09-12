package com.techtrader.modules.tools.bytecode;


import java.io.*;

import com.techtrader.modules.tools.bytecode.visitor.*;


/**
 *	Represents an instruction that takes as an argument a Class to operate on.
 *	Examples include ANEWARRAY, CHECKCAST, INSTANCEOF, NEW.
 *
 *	@author		Abe White
 */
public class ClassInstruction
	extends Instruction
{
	private int _index = 0;


	protected ClassInstruction (Code owner, int opcode)
	{
		super (owner);
		_opcode = opcode;
	}


	/**
	 *	Get the constant pool index of the class for this instruction.
	 */
	public int getClassIndex ()
	{
		return _index;
	}


	/**
	 *	Set the constant pool index of the class for this instruction.
	 */
	public void setClassIndex (int index)
	{
		_index = index;
	}


	/**
 	 *	Set the name of the class to operate on.
 	 *	
	 *	@return		this Instruction, for method chaining
	 */
	public ClassInstruction setClassName (String type)
	{
		_index = _owner.getPool ().setClassName 
			(0, BCHelper.getInternalForm (type, false));
		return this;
	}


	/**
 	 *	Get the nam of the class to operate on.
	 */
	public String getClassName ()
	{
		return BCHelper.getExternalForm 
			(_owner.getPool ().getClassName (_index), true);
	}	


	/**
	 *	Set the Class of the type to operate on.
 	 *	
	 *	@return		this Instruction, for method chaining
	 */
	public Instruction setClassType (Class type)
	{
		return setClassName (type.getName ());
	}


	/**
	 *	Get the Class of the type to operate on.
	 */
	public Class getClassType ()
		throws ClassNotFoundException
	{
		return BCHelper.classForName (_owner.getPool ().getClassName (_index));
	}


	public int getLength ()
	{
		return super.getLength () + 2;
	}


	public int getStackChange ()
	{
		if (_opcode == NEW)
			return 1;

		return 0;
	}


	/**
	 *	ClassInstructions are equal if the type they reference is the same, 
	 *	or if the type of either is unset.
	 */
	public boolean equals (Object other)
	{
		if (other == this)
			return true;
		if (!(other instanceof ClassInstruction))
			return false;
		if (!super.equals (other))
			return false;

		ClassInstruction ins = (ClassInstruction) other;
		String name = getClassName ();
		String insName = ins.getClassName ();

		return name.length () == 0 || insName.length () == 0
			|| name.equals (insName);
	}


	protected void copy (Instruction orig)
	{
		super.copy (orig);
		setClassName (((ClassInstruction) orig).getClassName ());
	}


	protected void readData (DataInput in)
		throws IOException
	{
		_index = in.readUnsignedShort ();
	}


	protected void writeData (DataOutput out)
		throws IOException
	{
		out.writeShort (_index);
	}


	public void acceptVisit (BCVisitor visit)
	{
		visit.enterClassInstruction (this);
		visit.exitClassInstruction (this);
	}
}
