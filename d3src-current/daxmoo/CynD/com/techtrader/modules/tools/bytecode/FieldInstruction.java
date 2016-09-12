package com.techtrader.modules.tools.bytecode;


import java.lang.reflect.*;
import java.io.*;

import com.techtrader.modules.tools.bytecode.lowlevel.*;
import com.techtrader.modules.tools.bytecode.visitor.*;


/**
 *	Represents an instruction that takes as an argument a field to operate on.
 *	Examples include GETFIELD, GETSTATIC, SETFIELD, SETSTATIC.
 *
 *	@author		Abe White
 */
public abstract class FieldInstruction
	extends Instruction
{
	private int _index = 0;


	protected FieldInstruction (Code owner, int opcode)
	{
		super (owner);
		_opcode = opcode;	
	}


	/**
	 *	Get the index of the ComplexEntry in the constant pool describing
 	 *	the field to operate on.
	 */
	public int getFieldIndex ()
	{
		return _index;
	}


	/**
	 *	Set the index of the ComplexEntry in the constant pool describing
 	 *	the field to operate on.
	 */
	public void setFieldIndex (int index)
	{
		_index = index;
	}


	/**
 	 *	Set the field to operate on.
	 *	
	 *	@return		this Instruction, for method chaining
	 */
	public FieldInstruction setField (BCField field)
	{
		BCClass ownerType = field.getOwner ();
		return setField (field.getName (), field.getTypeName (), 
			ownerType.getName ());
	}


	/**
	 *	Set the field to operate on.
	 *	
	 *	@return		this Instruction, for method chaining
	 */
	public FieldInstruction setField (Field field)
	{
		return setField (field.getName (), field.getType ().getName (),
			field.getDeclaringClass ().getName ());
	}


	/**
	 *	Set the field to operate on.
	 *	
	 *	@return		this Instruction, for method chaining
	 */
	public FieldInstruction setField (String name, String type, 
		String ownerType)
	{
		String internalType = BCHelper.getInternalForm (type, true);
		String internalOwner = BCHelper.getInternalForm(ownerType, false);

		_index = _owner.getPool ().setComplex (0, name, internalType, 
			internalOwner, FieldEntry.class);

		return this;
	}

	
	/**
	 *	Set the field to operate on.
	 *	
	 *	@return		this Instruction, for method chaining
	 */
	public FieldInstruction setField (String name, Class type, String ownerType)
	{
		String typeName = (type == null) ? null : type.getName ();
		return setField (name, typeName, ownerType);
	}


	/**
	 *	Set the field to operate on.
	 *	
	 *	@return		this Instruction, for method chaining
	 */
	public FieldInstruction setField (String name, Class type, Class ownerType)
	{
		String typeName = (type == null) ? null : type.getName ();
		String ownerName = (ownerType == null) ? null : ownerType.getName ();
		return setField (name, typeName, ownerName);
	}


	/**
	 *	Change the field name.
	 *	
	 *	@return		this Instruction, for method chaining
	 */
	public FieldInstruction setFieldName (String name)
	{
		return setField (name, getFieldTypeName (), getFieldOwnerTypeName ());
	}


	/**
	 *	Return true if the field is a member of the current class.
	 */
	public boolean isFieldInCurrentClass ()
	{
		BCClass type = ((BCMethod) _owner.getOwner ()).getOwner ();
		return type.getName ().equals (getFieldOwnerTypeName ());
	}


	/**
	 *	If the field is a member of the current class, then this method will
	 *	retrieve the BCField object for it; otherwise it will return null.
	 */
	public BCField getField ()
	{
		if (!isFieldInCurrentClass ())
			return null;

		return ((BCMethod) _owner.getOwner ()).getOwner ().
			getField (getFieldName ());
	}


	/**
 	 *	Get the nam of the field to operate on.
	 */
	public String getFieldName ()
	{
		return _owner.getPool ().getComplexName (_index);
	}	


	/**
	 *	Get the type of the field.
	 */
	public String getFieldTypeName ()
	{
		return BCHelper.getExternalForm 
			(_owner.getPool ().getComplexTypeName (_index), true);
	}


	/**
	 *	Get the type of the field.
	 */
	public Class getFieldType ()
		throws ClassNotFoundException
	{
		return BCHelper.classForName 
			(_owner.getPool ().getComplexTypeName (_index));
	}


	/**
	 *	Get the type of the field.
	 */
	public String getFieldOwnerTypeName ()
	{
		return BCHelper.getExternalForm 
			(_owner.getPool ().getComplexOwnerTypeName (_index), true);
	}


	/**
	 *	Get the type of the field.
	 */
	public Class getFieldOwnerType ()
		throws ClassNotFoundException
	{
		return BCHelper.classForName
			(_owner.getPool ().getComplexOwnerTypeName (_index));
	}


	/**
	 *	FieldInstructions are equal if the field they reference is the same, 
	 *	or if the field of either is unset.
	 */
	public boolean equals (Object other)
	{
		if (other == this)
			return true;
		if (!(other instanceof FieldInstruction))
			return false;
		if (!super.equals (other))
			return false;

		FieldInstruction ins = (FieldInstruction) other;

		String s1 = getFieldName ();
		String s2 = ins.getFieldName ();
		if (!(s1.length () == 0 || s2.length () == 0 || s1.equals (s2)))
			return false;

		s1 = getFieldTypeName ();
		s2 = ins.getFieldTypeName ();
		if (!(s1.length () == 0 || s2.length () == 0 || s1.equals (s2)))
			return false;
		
		s1 = getFieldOwnerTypeName ();
		s2 = ins.getFieldOwnerTypeName ();
		if (!(s1.length () == 0 || s2.length () == 0 || s1.equals (s2)))
			return false;

		return true;
	}


	public int getLength ()
	{
		return super.getLength () + 2;
	}


	protected void copy (Instruction orig)
	{
		super.copy (orig);
		FieldInstruction origFI = (FieldInstruction) orig;

		setField (origFI.getFieldName (), origFI.getFieldTypeName (), 
			origFI.getFieldOwnerTypeName ());
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
}
