package com.techtrader.modules.tools.bytecode;


import java.io.*;

import com.techtrader.modules.tools.bytecode.visitor.*;


/**
 *	Represents an instruction that that loads a constant onto the stack.  
 *	The opcode represented by this Instruction may change depending on the
 *	type and value of the constant set.  For example, if the constant value
 *	is initially set to '5', the opcode will be iconst_5; if later incremented
 *	to '6', the opcode will be changed to bipush(6).
 *
 *	@author		Abe White
 */
public class ConstantInstruction
	extends Instruction
{
	private Object 	_value 	= null;
	private int		_arg	= -1;


	protected ConstantInstruction (Code owner)
	{
		super (owner);
	}


	protected ConstantInstruction (Code owner, int opcode, Object value)
	{
		super (owner);
		_opcode = opcode;
		_value = value;
	}


	/**
	 *	Set the constant to the given Object value.  The Object should be
	 *	an instance of String, Integer, Long, Double, or Float depending
	 *	on the constant type.
 	 *	
	 *	@return		this Instruction, for method chaining
	 */
	public ConstantInstruction setConstant (Object value)
	{
		_value = value;
		calculateOpCode ();

		return this;
	}


	/**
	 *	Return the constant value as an Object; will be an instance of
	 *	String, Integer, Float, Double, or Long, as necessary.  Returns null
	 *	if the constant has not been set, or if this represents the 
	 *	aconst_null opcode.
	 */
	public Object getConstant ()
	{
		return _value;
	}


	/**
	 *	Return the class of constant this instruction references.  Will return
	 *	one of: Object.class, String.class, int.class, long.class, 
	 *	double.class, float.class.
	 */
	public Class getConstantType ()
	{
		if (_value == null)
			return Object.class;

		Class cls = _value.getClass ();
		if (cls.equals (Float.class))
			return float.class;
		if (cls.equals (Double.class))
			return double.class;
		if (cls.equals (Long.class))
			return long.class;
		if (cls.equals (String.class))
			return String.class;
		return int.class;
	}


	/**
	 *	Return the class of constant this instruction references.  Will return
	 *	one of: java.lang.Object, java.lang.String, int, long, double, float.
	 */
	public String getConstantTypeName ()
	{
		return getConstantType ().getName ();
	}


	/**
 	 *	Set the constant to load, for String constants.
 	 *	
	 *	@return		this Instruction, for method chaining
	 */
	public ConstantInstruction setStringConstant (String value)
	{
		return setConstant (value);
	}


	/**
 	 *	Get the constant to load, for String constants.
	 */
	public String getStringConstant ()
	{
		return (String) getConstant ();
	}


	/**
 	 *	Set the constant to load, for int constants.
 	 *	
	 *	@return		this Instruction, for method chaining
	 */
	public ConstantInstruction setIntConstant (int value)
	{
		return setConstant (new Integer (value));
	}


	/**
 	 *	Get the constant to load, for int constants.
	 */
	public int getIntConstant ()
	{
		return (_value == null) ? 0 : ((Number) _value).intValue ();
	}


	/**
 	 *	Set the constant to load, for float constants.
 	 *	
	 *	@return		this Instruction, for method chaining
	 */
	public ConstantInstruction setFloatConstant (float value)
	{
		return setConstant (new Float (value));
	}


	/**
 	 *	Get the constant to load, for float constants.
	 */
	public float getFloatConstant ()
	{
		return (_value == null) ? 0F : ((Number) _value).floatValue ();
	}


	/**
 	 *	Set the constant to load, for long constants; must be a ldc2
	 *	instruction.
 	 *	
	 *	@return		this Instruction, for method chaining
	 */
	public ConstantInstruction setLongConstant (long value)
	{
		return setConstant (new Long (value));
	}


	/**
 	 *	Get the constant to load, for float constants; must be a ldc2
	 *	instruction.
	 */
	public long getLongConstant ()
	{
		return (_value == null) ? 0L : ((Number) _value).longValue ();
	}


	/**
 	 *	Set the constant to load, for double constants; must be a ldc2
	 *	instruction.
 	 *	
	 *	@return		this Instruction, for method chaining
	 */
	public ConstantInstruction setDoubleConstant (double value)
	{
		return setConstant (new Double (value));
	}


	/**
 	 *	Get the constant to double, for float constants; must be a ldc2
	 *	instruction.
	 */
	public double getDoubleConstant ()
	{
		return (_value == null) ? 0D : ((Number) _value).doubleValue ();
	}


	/**
	 *	ConstantInstructions are equal if the const they reference is the same, 
	 *	or if the const of either is unset.
	 */
	public boolean equals (Object other)
	{
		if (this == other)
			return true;
		if (!(other instanceof ConstantInstruction))
			return false;

		ConstantInstruction ins = (ConstantInstruction) other;

		Object value = getConstant ();
		Object insValue = ins.getConstant ();

		return (value == null && !Object.class.equals (getConstantType ()))
			|| (insValue == null && !Object.class.equals(ins.getConstantType()))
			|| (value == null && insValue == null)
			|| (value != null && value.equals (insValue));
	}


	public int getLength ()
	{
		switch (_opcode)
		{
		case BIPUSH:
		case LDC:
			return super.getLength () + 1;
		case SIPUSH:
		case LDC_W:
		case LDC2_W:
			return super.getLength () + 2;
		default:
			return super.getLength ();
		}
	}


	public int getStackChange ()
	{
		if (_value instanceof Long || _value instanceof Double)
			return 2;

		return 1;
	}


	protected void copy (Instruction orig)
	{
		super.copy (orig);
		setConstant (((ConstantInstruction) orig).getConstant ());
	}


	protected void readData (DataInput in)
		throws IOException
	{
		switch (_opcode)
		{
		case BIPUSH:
			_value = new Integer (in.readUnsignedByte ());
			break;
		case SIPUSH:
			_value = new Integer (in.readUnsignedShort ());
			break;
		case LDC:
			_arg = in.readUnsignedByte ();
			_value = _owner.getPool ().getConstant (_arg);
			break;
		case LDC_W:
		case LDC2_W:
			_arg = in.readUnsignedShort ();
			_value = _owner.getPool ().getConstant (_arg);
			break;
		}
	}


	protected void writeData (DataOutput out)
		throws IOException
	{
		switch (_opcode)
		{
		case BIPUSH:
			out.writeByte (getIntConstant ());
			break;
		case SIPUSH:
			out.writeShort (getIntConstant ());
			break;
		case LDC:
			out.writeByte (_arg);
			break;
		case LDC_W:
		case LDC2_W:
			out.writeShort (_arg);
			break;
		}
	}


	/**
	 *	Helper method to calculate the optimal opcode for the constant
	 *	to be pushed onto the stack.
	 */
	private void calculateOpCode ()
	{
		Class type = getConstantType ();
		double val = 0;
		if (_value instanceof Number)
			val = getDoubleConstant ();
		else if (_value instanceof Boolean)
			val = (((Boolean) _value).booleanValue ()) ? 1 : 0;
		_arg = -1;
			
		if (type.equals (Object.class))
			_opcode = ACONST_NULL;
		else if (type.equals (float.class) && (val == 0 || val == 1 
			|| val == 2))
			_opcode = FCONST_0 + (int) val;
		else if (type.equals (long.class) && val > -1 && val < 2)
			_opcode = LCONST_0 + (int) val;
		else if (type.equals (double.class) && (val == 0 || val == 1 
			|| val == 2))
			_opcode = DCONST_0 + (int) val;
		else if (type.equals(int.class) && val >= -(2 << 15) && val < (2 << 15))
		{
			if (val >= -1 && val <= 5)
				_opcode = ICONST_0 + (int) val;
			else if (val >= -(2 << 7) && val < (2 << 7))
				_opcode = BIPUSH;
			else
				_opcode = SIPUSH;
		}
		else
		{
			_arg = _owner.getPool ().setConstant (0, _value);
			if (type.equals (long.class) || type.equals (double.class))
				_opcode = LDC2_W;
			else
				_opcode = (_arg > 255) ? LDC_W : LDC;
		}
	}


	public void acceptVisit (BCVisitor visit)
	{
		visit.enterConstantInstruction (this);	
		visit.exitConstantInstruction (this);	
	}
}
