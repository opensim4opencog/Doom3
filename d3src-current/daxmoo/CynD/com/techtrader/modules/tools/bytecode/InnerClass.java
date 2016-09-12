package com.techtrader.modules.tools.bytecode;


import java.io.*;

import com.techtrader.modules.tools.bytecode.visitor.*;


/**
 *	Represents an inner class.
 *	TODO: add high-level operations for manipulating the type of the 
 *	inner class.
 *	
 *	@author		Abe White
 */
public class InnerClass
	implements Constants
{
	private int	_nameIndex 		= 0;
	private int	_index 			= 0;
	private int	_ownerIndex		= 0;
	private int	_access			= ACCESS_PRIVATE;

	private InnerClassesAttribute _owner = null;


	/**
	 *	Protected constructor.  Used when reading from a .class file.
	 */
	protected InnerClass (InnerClassesAttribute owner)
	{
		_owner = owner;
	}


	/**
	 *	Protected constructor.  Used when adding inner classes programmatically.
	 */
	protected InnerClass (String name, InnerClassesAttribute owner)
	{
		_owner = owner;

		if (name != null && name.length () > 0)
			_nameIndex = _owner.getPool ().setUTF (0, name);
	}


	/**
	 *	Used to invalidate an inner class when removed, so that it can
	 *	no longer affect the constant pool.	
 	 */
	protected void invalidate ()
	{
		_owner = null;
	}


	/**
	 *	Inner classes are owned by InnerClassesAttributes.
	 */
	public InnerClassesAttribute getOwner ()
	{
		return _owner;
	}


	/**	
 	 *	Get the access flags on the inner class.
	 */
	public int getAccessFlags ()
	{
		return _access;
	}


	/**	
 	 *	Set the access flags on the inner class.
	 */
	public void setAccessFlags (int accessFlags)
	{
		_access = accessFlags;	
	}


	/**
	 *	Manipulate the inner class access flags.
	 */
	public boolean isPublic ()
	{
		return BCHelper.hasFlag (_access, ACCESS_PUBLIC);
	}


	/**
	 *	Manipulate the inner class access flags.
	 */
	public void makePublic ()
	{
		_access = BCHelper.setFlag (_access, ACCESS_PUBLIC, true);
		_access = BCHelper.setFlag (_access, ACCESS_PRIVATE, false);
		_access = BCHelper.setFlag (_access, ACCESS_PROTECTED, false);
	}


	/**
	 *	Manipulate the inner class access flags.
	 */
	public boolean isProtected ()
	{
		return BCHelper.hasFlag (_access, ACCESS_PROTECTED);
	}


	/**
	 *	Manipulate the inner class access flags.
	 */
	public void makeProtected ()
	{
		_access = BCHelper.setFlag (_access, ACCESS_PUBLIC, false);
		_access = BCHelper.setFlag (_access, ACCESS_PRIVATE, false);
		_access = BCHelper.setFlag (_access, ACCESS_PROTECTED, true);
	}


	/**
	 *	Manipulate the inner class access flags.
	 */
	public boolean isPrivate ()
	{
		return BCHelper.hasFlag (_access, ACCESS_PRIVATE);
	}


	/**
	 *	Manipulate the inner class access flags.
	 */
	public void makePrivate ()
	{
		_access = BCHelper.setFlag (_access, ACCESS_PUBLIC, false);
		_access = BCHelper.setFlag (_access, ACCESS_PRIVATE, true);
		_access = BCHelper.setFlag (_access, ACCESS_PROTECTED, false);
	}


	/**
	 *	Manipulate the inner class access flags.
	 */
	public boolean isFinal ()
	{
		return BCHelper.hasFlag (_access, ACCESS_FINAL);
	}


	/**
	 *	Manipulate the inner class access flags.
	 */
	public void setFinal (boolean on)
	{
		_access = BCHelper.setFlag (_access, ACCESS_FINAL, on);
	}


	/**
	 *	Manipulate the inner class access flags.
	 */
	public boolean isStatic ()
	{
		return BCHelper.hasFlag (_access, ACCESS_STATIC);
	}


	/**
	 *	Manipulate the inner class access flags.
	 */
	public void setStatic (boolean on)
	{
		_access = BCHelper.setFlag (_access, ACCESS_STATIC, on);
	}


	/**
	 *	Manipulate the class access flags.
	 */
	public boolean isInterface ()
	{
		return BCHelper.hasFlag (_access, ACCESS_INTERFACE);
	}


	/**
	 *	Manipulate the class access flags.
	 */
	public void setInterface (boolean on)
	{
		_access = BCHelper.setFlag (_access, ACCESS_INTERFACE, on);
		if (on)
			setAbstract (true);
	}


	/**
	 *	Manipulate the class access flags.
	 */
	public boolean isAbstract ()
	{
		return BCHelper.hasFlag (_access, ACCESS_ABSTRACT);
	}


	/**
	 *	Manipulate the class access flags.
	 */
	public void setAbstract (boolean on)
	{
		_access = BCHelper.setFlag (_access, ACCESS_INTERFACE, on);
	}


	/**
	 *	Get the index into the constant pool of the ClassEntry that describeds
	 *	this class.
	 */
	public int getIndex ()
	{
		return _index;
	}


	/**
	 *	Set the index into the constant pool of the ClassEntry that describeds
	 *	this class.
	 */
	public void setIndex (int index)
	{
		_index = index;
	}


	/**
	 *	Get the index into the constant pool of the UTF8Entry that holds
	 *	the name of the class.
	 */
	public int getNameIndex ()
	{
		return _nameIndex;
	}


	/**
	 *	Set the index into the constant pool of the UTF8Entry that holds
	 *	the name of the class.
	 */
	public void setNameIndex (int nameIndex)
	{
		_nameIndex = nameIndex;
	}


	/**
	 *	Get the name of this field.
	 */
	public String getName ()
	{
		return _owner.getPool ().getUTF (_nameIndex);
	}


	/**
	 *	Set the name of this field.
	 */
	public void setName (String name)
	{
		_nameIndex = _owner.getPool ().setUTF (0, name);
	}


	/**
	 *	Get the index into the constant pool of the ClassEntry describing 
	 *	the outer class.
	 */
	public int getOuterClassIndex ()
	{
		return _ownerIndex;
	}


	/**
	 *	Set the index into the constant pool of the ClassEntry describing 
	 *	the outer class.
	 */
	public void setOuterClassIndex (int ownerIndex)
	{
		_ownerIndex = ownerIndex;
	}


	protected void readData (DataInput in)
		throws IOException
	{
		setIndex (in.readUnsignedShort ());
		setOuterClassIndex (in.readUnsignedShort ());
		setNameIndex (in.readUnsignedShort ());
		setAccessFlags (in.readUnsignedShort ());
	}


	protected void writeData (DataOutput out)
		throws IOException
	{
		out.writeShort (getIndex ());
		out.writeShort (getOuterClassIndex ());
		out.writeShort (getNameIndex ());
		out.writeShort (getAccessFlags ());
	}


	public void acceptVisit (BCVisitor visit)
	{
		visit.enterInnerClass (this);	
		visit.exitInnerClass (this);	
	}
}
