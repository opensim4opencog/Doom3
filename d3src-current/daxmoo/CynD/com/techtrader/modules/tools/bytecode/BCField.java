package com.techtrader.modules.tools.bytecode;


import java.io.*;

import com.techtrader.modules.tools.bytecode.lowlevel.*;
import com.techtrader.modules.tools.bytecode.visitor.*;


/**
 *	Representation of a bytecode field of a class; a BCField can only
 *	be obtained from a BCClass.
 *	
 *	@author		Abe White
 */
public class BCField
	extends BCEntity
	implements Constants
{
	private BCClass	_owner				= null;
	private int 	_access 			= ACCESS_PRIVATE;
	private int		_nameIndex			= 0;
	private int		_descriptorIndex 	= 0;


	/**
	 *	Protected constructor.
	 */
	protected BCField (BCClass owner)
	{
		_owner = owner;
	}


	/**
	 *	Used when this field is deleted from its class.
	 */
	protected void invalidate ()
	{
		_owner = null;
	}


	/**
	 *	Get the BCClass that owns this field.
	 */
	public BCClass getOwner ()
	{
		return _owner;
	}


	/**
	 *	Return the access flags for this class as a bit array of 
 	 *	ACCESS_XXX constants.  This can be used to transfer access flags
	 *	between fields without getting/setting each possible access flag.
	 */
	public int getAccessFlags ()
	{
		return _access;
	}


	/**
	 *	Set the access flags for this class as a bit array of 
 	 *	ACCESS_XXX constants.  This can be used to transfer access flags
	 *	between fields without getting/setting each possible access flag.
	 */
	public void setAccessFlags (int access)
	{
		_access = access;	
	}


	/**
	 *	Manipulate the method access flags.
	 */
	public boolean isPublic ()
	{
		return BCHelper.hasFlag (_access, ACCESS_PUBLIC);
	}


	/**
	 *	Manipulate the method access flags.
	 */
	public void makePublic ()
	{
		_access = BCHelper.setFlag (_access, ACCESS_PUBLIC, true);
		_access = BCHelper.setFlag (_access, ACCESS_PRIVATE, false);
		_access = BCHelper.setFlag (_access, ACCESS_PROTECTED, false);
	}


	/**
	 *	Manipulate the method access flags.
	 */
	public boolean isProtected ()
	{
		return BCHelper.hasFlag (_access, ACCESS_PROTECTED);
	}


	/**
	 *	Manipulate the method access flags.
	 */
	public void makeProtected ()
	{
		_access = BCHelper.setFlag (_access, ACCESS_PUBLIC, false);
		_access = BCHelper.setFlag (_access, ACCESS_PRIVATE, false);
		_access = BCHelper.setFlag (_access, ACCESS_PROTECTED, true);
	}


	/**
	 *	Manipulate the method access flags.
	 */
	public boolean isPrivate ()
	{
		return BCHelper.hasFlag (_access, ACCESS_PRIVATE);
	}


	/**
	 *	Manipulate the method access flags.
	 */
	public void makePrivate ()
	{
		_access = BCHelper.setFlag (_access, ACCESS_PUBLIC, false);
		_access = BCHelper.setFlag (_access, ACCESS_PRIVATE, true);
		_access = BCHelper.setFlag (_access, ACCESS_PROTECTED, false);
	}


	/**
	 *	Manipulate the method access flags.
	 */
	public boolean isPackage ()
	{
		boolean hasAccess = false;
		hasAccess = hasAccess ||  BCHelper.hasFlag (_access, ACCESS_PRIVATE);
		hasAccess = hasAccess ||  BCHelper.hasFlag (_access, ACCESS_PROTECTED);
		hasAccess = hasAccess ||  BCHelper.hasFlag (_access, ACCESS_PUBLIC);

		return !hasAccess;
	}


	/**
	 *	Manipulate the method access flags.
	 */
	public void makePackage ()
	{
		_access = BCHelper.setFlag (_access, ACCESS_PUBLIC, false);
		_access = BCHelper.setFlag (_access, ACCESS_PRIVATE, false);
		_access = BCHelper.setFlag (_access, ACCESS_PROTECTED, false);
	}


	/**
	 *	Manipulate the method access flags.
	 */
	public boolean isFinal ()
	{
		return BCHelper.hasFlag (_access, ACCESS_FINAL);
	}


	/**
	 *	Manipulate the method access flags.
	 */
	public void setFinal (boolean on)
	{
		_access = BCHelper.setFlag (_access, ACCESS_FINAL, on);
	}


	/**
	 *	Manipulate the method access flags.
	 */
	public boolean isStatic ()
	{
		return BCHelper.hasFlag (_access, ACCESS_STATIC);
	}


	/**
	 *	Manipulate the method access flags.
	 */
	public void setStatic (boolean on)
	{
		_access = BCHelper.setFlag (_access, ACCESS_STATIC, on);
	}


	/**
	 *	Manipulate the method access flags.
	 */
	public boolean isVolatile ()
	{
		return BCHelper.hasFlag (_access, ACCESS_VOLATILE);
	}


	/**
	 *	Manipulate the method access flags.
	 */
	public void setVolatile (boolean on)
	{
		_access = BCHelper.setFlag (_access, ACCESS_VOLATILE, on);
	}


	/**
	 *	Manipulate the method access flags.
	 */
	public boolean isTransient ()
	{
		return BCHelper.hasFlag (_access, ACCESS_TRANSIENT);
	}


	/**
	 *	Manipulate the method access flags.
	 */
	public void setTransient (boolean on)
	{
		_access = BCHelper.setFlag (_access, ACCESS_TRANSIENT, on);
	}


	/**
	 *	Get the index in the constant pool of the UTF entry holding the name
	 *	of this field.
	 */
	public int getNameIndex ()
	{
		return _nameIndex;
	}


	/**
	 *	Set the index in the constant pool of the UTF entry holding the name
	 *	of this field.
	 */
	public void setNameIndex (int index)
	{
		_nameIndex = index;
	}


	/**
	 *	Get the index in the constant pool of the UTF entry holding the 
	 *	descriptor of this field.
	 */
	public int getDescriptorIndex ()
	{
		return _descriptorIndex;
	}


	/**
	 *	Set the index in the constant pool of the UTF entry holding the 
	 *	descriptor of this field.
	 */
	public void setDescriptorIndex (int index)
	{
		_descriptorIndex = index;
	}


	/**
	 *	Get the name of this field.
	 */
	public String getName ()
	{
		return getPool ().getUTF (_nameIndex);
	}


	/**
	 *	Set the name of this field.
	 */
	public void setName (String name)
	{
		String origName = getName ();

		// reset the name
		_nameIndex = getPool ().setUTF (0, name);

		// find the ComplexEntry matching this field, if any
		String internalDesc = getPool ().getUTF (_descriptorIndex);
		String internalOwner = getPool ().getClassName (_owner.getIndex ());

		int index = getPool ().getComplexIndex
			(origName, internalDesc, internalOwner, FieldEntry.class);

		// change the ComplexEntry to match the new name; this is dones so
		// that refs to the field in code will still be valid after the name
		// change, without changing any other constants that happened to match
		// the old field name
		if (index != 0)
			getPool ().setComplex
				(index, name, internalDesc, internalOwner, FieldEntry.class);
	}


	/**
	 *	Get the index in the constant pool of the UTF entry holding the 
	 *	descriptor of this field.
	 */
	public int getTypeIndex ()
	{
		return _descriptorIndex;
	}


	/**
	 *	Set the index in the constant pool of the UTF entry holding the
	 *	descriptor of this field.
	 */
	public void setTypeIndex (int index)
	{
		_descriptorIndex = index;
	}


	/**
	 *	Get the name of the type of this field.
	 */
	public String getTypeName ()
	{
		return BCHelper.getExternalForm 
			(getPool ().getUTF (_descriptorIndex), true);
	}


	/**
	 *	Set the type name for this field.
	 */
	public void setTypeName (String name)
	{
		String origDesc = getPool ().getUTF (_descriptorIndex);

		// reset the desc
		String internalDesc = BCHelper.getInternalForm (name, true);
		_descriptorIndex = getPool ().setUTF (0, internalDesc);

		// find the ComplexEntry matching this field, if any
		String internalName = getName ();
		String internalOwner = getPool ().getClassName (_owner.getIndex ());

		int index = getPool ().getComplexIndex
			(internalName, origDesc, internalOwner, FieldEntry.class);

		// change the ComplexEntry to match the new desc; this is dones so
		// that refs to the field in code will still be valid after the name
		// change, without changing any other constants that happened to match
		// the old field name
		if (index != 0)
			getPool ().setComplex (index, internalName, internalDesc, 
				internalOwner, FieldEntry.class);
	}


	/**
	 *	Get the Class object for the type of this field.
	 */
	public Class getType ()
		throws ClassNotFoundException
	{
		return BCHelper.classForName (getPool ().getUTF (_descriptorIndex));
	}


	/**
	 *	Set the type of this field.
	 */
	public void setType (Class type)
	{
		setTypeName (type.getName ());
	}


	/**
	 *	Get the class constant pool; this method delegates to the
 	 *	owning class.
	 */
	public ConstantPool getPool ()
	{
		return getOwner ().getPool ();
	}


	protected void readData (DataInput in)
		throws IOException
	{
		setAccessFlags (in.readUnsignedShort ());	
		setNameIndex (in.readUnsignedShort ());	
		setDescriptorIndex (in.readUnsignedShort ());	

		readAttributes (in);
	}


	protected void writeData (DataOutput out)
		throws IOException
	{
		out.writeShort (getAccessFlags ());
		out.writeShort (getNameIndex ());
		out.writeShort (getDescriptorIndex ());

		writeAttributes (out);
	}


	public void acceptVisit (BCVisitor visit)
	{
		visit.enterBCField (this);
		visitAttributes (visit);
		visit.exitBCField (this);
	}
}
