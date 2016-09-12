package com.techtrader.modules.tools.bytecode;


import java.util.*;
import java.io.*;

import com.techtrader.modules.tools.bytecode.visitor.*;


/**
 *	Represents a NEWARRAY instruction, which is used to create new arrays
 * 	of primitive types.
 *
 *	@author		Abe White
 */
public class NewArrayInstruction
	extends Instruction
{
	private static final int[] _arrayCodes = new int[] {
		ARRAY_BOOLEAN, ARRAY_CHAR, ARRAY_FLOAT, ARRAY_DOUBLE,
		ARRAY_BYTE, ARRAY_SHORT, ARRAY_INT, ARRAY_LONG
	};
	private static final Class[] _arrayTypes = new Class[] {
		boolean.class, char.class, float.class, double.class,
		byte.class, short.class, int.class, long.class
	};
	private static final String[] _arrayNames = new String[] {
		"boolean", "char", "float", "double",
		"byte", "short", "int", "long"
	};

	private int _code = 0;


	protected NewArrayInstruction (Code owner)
	{
		super (owner);
		_opcode = NEWARRAY;
	}


	/**
	 *	Get the array code used in the lowlevel bytecode.
	 */
	public int getArrayTypeCode ()
	{
		return _code;
	}


	/**
	 *	Set the array code used in the lowlevel bytecode.
	 */
	public void setArrayTypeCode (int code)
	{
		_code = code;
	}


	/**
 	 *	Get the type of array to create.
	 */
	public Class getArrayType ()
	{
		for (int i = 0; i < _arrayCodes.length; i++)
			if (_code == _arrayCodes[i])
				return _arrayTypes[i];

		return null;
	}	


	/**
 	 *	Get the type of array to create.
	 */
	public String getArrayTypeName ()
	{
		for (int i = 0; i < _arrayCodes.length; i++)
			if (_code == _arrayCodes[i])
				return _arrayTypes[i].getName ();

		return null;
	}	


	/**
 	 *	Set the type of array to create.
 	 *	
	 *	@return		this Instruction, for method chaining
	 */
	public NewArrayInstruction setArrayType (Class type)
	{
		_code = 0;
		for (int i = 0; i < _arrayTypes.length; i++)
		{
			if (_arrayTypes[i].equals (type))
			{
				_code = _arrayCodes[i];
				break;
			}
		}
		return this;
	}


	/**
 	 *	Set the type of array to create.
 	 *	
	 *	@return		this Instruction, for method chaining
	 */
	public NewArrayInstruction setArrayTypeName (String type)
	{
		_code = 0;
		for (int i = 0; i < _arrayNames.length; i++)
		{
			if (_arrayNames[i].equals (type))
			{
				_code = _arrayCodes[i];
				break;
			}
		}
		return this;
	}


	/**
	 *	Two NEWARRAY instructions are equal if the array type is the same,
	 *	of if the array type of either is unset.
	 */
	public boolean equals (Object other)
	{
		if (this == other)
			return true;
		if (!(other instanceof NewArrayInstruction))
			return false;
		if (!super.equals (other))
			return false;

		NewArrayInstruction ins = (NewArrayInstruction) other;
		
		return getArrayType () == null || ins.getArrayType () == null
			|| getArrayType ().equals (ins.getArrayType ());
	}


	public int getLength ()
	{
		return super.getLength () + 1;
	}


	protected void copy (Instruction orig)
	{
		super.copy (orig);
		setArrayType (((NewArrayInstruction) orig).getArrayType ());
	}


	protected void readData (DataInput in)
		throws IOException
	{
		_code = in.readUnsignedByte ();
	}


	protected void writeData (DataOutput out)
		throws IOException
	{
		out.writeByte (_code);
	}


	public void acceptVisit (BCVisitor visit)
	{
		visit.enterNewArrayInstruction (this);
		visit.exitNewArrayInstruction (this);
	}
}
