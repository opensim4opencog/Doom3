package com.techtrader.modules.tools.bytecode;


import java.lang.reflect.*;
import java.io.*;

import com.techtrader.modules.tools.bytecode.lowlevel.*;
import com.techtrader.modules.tools.bytecode.visitor.*;


/**
 *	Representation of an attribute in a .class file.  Attributes
 *	are used to represent constants and the code of methods, among other things.
 *	All attributes contain at a minimum a name, which is immutable, as it
 *	determines the attribute's type.
 *
 *	@author		Abe White
 */
public abstract class Attribute
	extends BCEntity
	implements Constants, VisitAcceptor
{
	protected int		_nameIndex 	= 0;
	protected BCEntity 	_owner 		= null;


	/**
	 *	Create an attribute of the appropriate type based on the
	 *	the attribute name.
	 */
	protected static Attribute createAttribute (String name, BCEntity owner)
	{
		int nameIndex = owner.getPool ().setUTF (0, name);

		// special handling for code
		if (name.equals (ATTR_CODE))
			return new Code (nameIndex, owner);
		
		try
		{
			Class type = Class.forName ("com.techtrader.modules.tools.bytecode."
				+ name + "Attribute");
			Constructor cons = type.getConstructor (new Class[]
				{ int.class, BCEntity.class });

			return (Attribute) cons.newInstance (new Object[]
				{ new Integer (nameIndex), owner });
		}
		catch (Throwable t)
		{
			return new UnknownAttribute (nameIndex, owner);
		}			
	}


	/**
	 *	Protected Constructor.
	 */
	protected Attribute (int nameIndex, BCEntity owner)
	{
		_owner = owner;
		_nameIndex = nameIndex;
	}


	/**
	 *	Invalidate this Attribute.
	 */
	protected void invalidate ()
	{
		_owner = null;
	}


	/**
	 *	Each Attribute references the entity that owns it.
	 */
	public BCEntity getOwner ()
	{
		return _owner;
	}


	/**
	 *	Return the constant pool index of the UTF entry holding the name
	 *	of this attribute.
	 */
	public int getNameIndex ()
	{
		return _nameIndex;
	}


	/**
 	 *	Return the name of this attribute.
	 */
	public String getName ()
	{
		return _owner.getPool ().getUTF (_nameIndex);
	}


	/**
	 *	Implementation of the BCEntity abstract method; delegates to the 
	 *	owning entity.
	 */
	public ConstantPool getPool ()
	{
		return _owner.getPool ();
	}


	/**
	 *	Return the length of the .class representation of this attribute,
	 *	in bytes.
	 */
	public int getLength ()
	{
		return 0;
	}


	/**
	 *	Copy the information from the given attribute to this one.
	 */
	protected void copy (Attribute other)
	{
	}


	/**
	 *	Should be overridden by subclasses to read their internal data from
	 *	the given stream, up to length bytes, excluding the name index.
	 */
	protected void readData (DataInput in, int length)
		throws IOException
	{
	}


	/**
	 *	Should be overridden by subclasses to write their internal data to
	 *	the given stream, up to length bytes, excluding the name index.
	 */
	protected void writeData (DataOutput out, int length)
		throws IOException
	{
	}
}
