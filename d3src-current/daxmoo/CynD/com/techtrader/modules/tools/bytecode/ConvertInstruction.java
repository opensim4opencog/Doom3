package com.techtrader.modules.tools.bytecode;


import java.util.*;

import com.techtrader.modules.tools.bytecode.visitor.*;


/**
 *	Represents one of the conversion opcodes defined in the 
 *	{@link Constants} interface for	converting between primitive types.  
 *	Changing the types of the instruction will automatically
 *	update the underlying opcode.  If converting from one type to the same
 *	type will result in a NOP.  Note that the result of conversions not 
 *	supported directly by the JVM (i.e. char to double) is undefined.
 *
 *	@author		Abe White
 */
public class ConvertInstruction
	extends Instruction
{
	private static final Map _typeNames = new HashMap ();
	static
	{
		_typeNames.put ("long", long.class);
		_typeNames.put ("float", float.class);
		_typeNames.put ("double", double.class);
	}

	private Class	_fromType	= null;
	private Class	_toType		= null;


	protected ConvertInstruction (Code owner)
	{
		super (owner);
	}


	protected ConvertInstruction (Code owner, int opcode, Class from, Class to)
	{
		super (owner);
		_opcode = opcode;
		_fromType = from;
		_toType = to;
	}


	/**
	 *	Get the type of being converted from; will be one of:
	 *	int, float, double, long.
	 *	If the type has not been set, this method will return null.
	 */
	public Class getFromType ()
	{
		return _fromType;
	}


	/**
	 *	Get the type of being converted from; will be one of:
	 *	int, float, double, long.
	 *	If the type has not been set, this method will return null.
	 */
	public String getFromTypeName ()
	{
		if (_fromType == null)
			return null;

		return _fromType.getName ();
	}


	/**
	 *	Set the type to convert from.  Types without direct support are
	 *	demoted to int.class.
	 *
	 *	@return	this Instruction, for method chaining
	 */
	public ConvertInstruction setFromType (Class type)
	{
		if (float.class.equals (type) || double.class.equals (type)
			|| long.class.equals (type))
			_fromType = type;
		else
			_fromType = int.class;
		
		calculateOpCode ();
		return this;
	}


	/**
	 *	Set the type to convert from by name.
	 *
	 *	@return	this Instruction, for method chaining
	 *	
	 *	@see	#setFromType(java.lang.Class)
	 */
	public ConvertInstruction setFromTypeName (String name)
	{
		_fromType = (Class) _typeNames.get (name);
		if (_fromType == null && name != null)
			_fromType = int.class;

		calculateOpCode ();
		return this;
	}


	/**
	 *	Get the type being converted to; will be one of:
	 *	int, float, double, long, byte, char, short.
	 *	If the type has not been set, this method will return null.
	 */
	public Class getToType ()
	{
		return _toType;
	}


	/**
	 *	Get the type being converted to; will be one of:
	 *	int, float, double, long, byte, char, short.
	 *	If the type has not been set, this method will return null.
	 */
	public String getToTypeName ()
	{
		if (_toType == null)
			return null;

		return _toType.getName ();
	}


	/**
	 *	Set the type to convert to.  Types without direct support are
	 *	demoted to int.class.
	 *
	 *	@return	this Instruction, for method chaining
	 */
	public ConvertInstruction setToType (Class type)
	{
		if (_opcodeTypes.indexOf (type) != -1)
			_toType = type;
		else
			_toType = int.class;
		
		calculateOpCode ();
		return this;
	}


	/**
	 *	Set the type to convert to by name.
	 *
	 *	@return	this Instruction, for method chaining
	 *	
	 *	@see	#setToType(java.lang.Class)
	 */
	public ConvertInstruction setToTypeName (String name)
	{
		_toType = (Class) _typeNames.get (name);

		// there are more 'to' types available than 'from' types
		if (_toType == null && name != null)
		{
			if (name.equals ("byte"))
				_toType = byte.class;
			else if (name.equals ("char"))
				_toType = char.class;
			else if (name.equals ("short"))
				_toType = short.class;
			else
				_toType = int.class;
		}

		calculateOpCode ();
		return this;
	}


	/**
	 *	ConvertInstructions are equal if they convert between the same types,
	 *	or the types of either is unset.
	 */
	public boolean equals (Object other)
	{
		if (this == other)
			return true;
		if (!(other instanceof ConvertInstruction))
			return false;

		ConvertInstruction ins = (ConvertInstruction) other;

		boolean fromEq = (_fromType == null || ins._fromType == null
			|| _fromType.equals (ins._fromType));
		boolean toEq = (_toType == null || ins._toType == null
			|| _toType.equals (ins._toType));

		return fromEq && toEq;
	}


	public int getStackChange ()
	{
		switch (_opcode)
		{
		case I2L:
		case I2D:
		case F2L:
		case F2D:
			return 1;
		case L2I:
		case L2F:
		case D2I:
		case D2F:
			return -1;
		default:
			return 0;
		}
	}


	protected void copy (Instruction orig)
	{
		super.copy (orig);

		ConvertInstruction ins = (ConvertInstruction) orig;
		_fromType = ins._fromType;
		_toType = ins._toType;
	}


	/**
	 *	Helper method to calculate the correct opcode for this conversion.
	 */
	private void calculateOpCode ()
	{
		int fromIdx		= _opcodeTypes.indexOf (_fromType);
		int toIdx 		= _opcodeTypes.indexOf (_toType);

		// take advantage of the grouping of the opcodes
		_opcode	= I2L + 3 * fromIdx;

		// if not converting from int then any conversion to byte,char,float
		// should be considered a conversion to int
		if (fromIdx != 0 && toIdx > 4)
			toIdx = 0;
		else if (toIdx > 4)	// the weird 'to' types are placed after the rest 
			_opcode += 8;

		// do nothing if converting to the same type
		if (fromIdx == toIdx)
		{
			_opcode = NOP;
			return;
		}

		// move from the <fromtype>2I opcode to <fromtype>2<totype>
		_opcode += toIdx;
		// compensate: there is no <fromtype>2<fromtype> opcode
		if (toIdx > fromIdx)
			_opcode--;
	}


	public void acceptVisit (BCVisitor visit)
	{
		visit.enterConvertInstruction (this);	
		visit.exitConvertInstruction (this);	
	}
}
