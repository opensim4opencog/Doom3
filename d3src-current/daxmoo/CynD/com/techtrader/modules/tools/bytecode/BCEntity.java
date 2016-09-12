package com.techtrader.modules.tools.bytecode;


import java.io.*;
import java.util.*;

import com.techtrader.modules.tools.bytecode.lowlevel.*;
import com.techtrader.modules.tools.bytecode.visitor.*;


/**
 *	Abstract superclass for all complex bytecode entities.  All bytecode
 *	entities contain attributes and are able to access the constant pool.
 *	
 *	@author		Abe White
 */
public abstract class BCEntity
	implements VisitAcceptor
{
	private List _attributes = new LinkedList ();


	/**
	 *	Return the constant pool for the current class.
	 */
	public abstract ConstantPool getPool ();


	/**
	 *	Get all the attributes owned by this entity.
	 *
	 *	@return		all owned attributes, or empty array if none
	 */
	public Attribute[] getAttributes ()
	{
		return (Attribute[]) _attributes.toArray 
			(new Attribute[_attributes.size ()]);
	}


	/**
	 *	Return the attribute with the given name.  If multiple attributes
	 *	share the name, which is returned is undefined.
	 */
	public Attribute getAttribute (String name)
	{
		Attribute next;
		for (Iterator i = _attributes.iterator (); i.hasNext ();)
		{
			next = (Attribute) i.next ();
			if (next.getName ().equals (name))
				return next;
		}

		return null;
	}

	
	/**
	 *	Returns all attributes with the given name.
 	 *
	 *	@return		the matching attributes, or empty array if none
	 */
	public Attribute[] getAttributes (String name)
	{
		List matches = new LinkedList ();

		Attribute next;
		for (Iterator i = _attributes.iterator (); i.hasNext ();)
		{
			next = (Attribute) i.next ();
			if (next.getName ().equals (name))
				matches.add (next);
		}

		return (Attribute[]) matches.toArray (new Attribute[matches.size ()]);	
	}


	/**
	 *	Import an attribute from another entity, or make a copy of one
	 *	on this entity.
	 */
	public Attribute importAttribute (Attribute attr)
	{
		Attribute newAttr = addAttribute (attr.getName ());
		newAttr.copy (attr);

		return newAttr;
	}


	/**
	 *	Import all attributes from another entity.
	 */
	public void importAttributes (BCEntity other)
	{
		Attribute[] attrs = other.getAttributes ();
		for (int i = 0; i < attrs.length; i++)
			importAttribute (attrs[i]);
	}


	/**	
 	 *	Add an attribute of the given type.
	 */
	public Attribute addAttribute (String name)
	{
		Attribute attr = Attribute.createAttribute (name, this);
		_attributes.add (attr);

		return attr;
	}


	/**
	 *	Clear all attributes from this entity.
	 */
	public void clearAttributes ()
	{
		_attributes.clear ();
	}


	/**
	 *	Remove all attributes with the given name from the list.
	 *
	 *	@return		true if an attribute was removed, false otherwise
	 */
	public boolean removeAttribute (String name)
	{
		Attribute[] matches = getAttributes (name);
		for (int i = 0; i < matches.length; i++)
			removeAttribute (matches[i]);

		return (matches.length > 0);
	}


	/**
	 *	Remove the given attribute.  After being removed, the given Attribute
	 *	is invalid, and the result of any operations on it are undefined.
	 *
	 *	@return		true if the attribute was removed, false otherwise
	 */
	public boolean removeAttribute (Attribute attribute)
	{
		if (attribute == null || !_attributes.remove (attribute))
			return false;

		attribute.invalidate ();
		return true;
	}


	/**
	 *	Clears the attribute list and rebuilds it from the given stream.
	 *	Relies on the ability of attributes to read themselves, and 
	 *	requires access to the constant pool, which must already by read.
	 */
	protected void readAttributes (DataInput in)
		throws IOException
	{
		_attributes.clear ();

		Attribute attribute;
		String name;
		for (int i = in.readUnsignedShort (); i > 0; i--)
		{
			name = getPool ().getUTF (in.readUnsignedShort ());
			attribute = addAttribute (name);
			attribute.readData (in, in.readInt ());
		}	
	}


	/**
	 *	Writes all the owned attributes to the given stream.
	 *	Relies on the ability of attributes to write themselves.
	 */
	protected void writeAttributes (DataOutput out)
		throws IOException
	{
		out.writeShort (_attributes.size ());
		Attribute attribute;
		int length;
		for (Iterator i = _attributes.iterator (); i.hasNext ();)
		{
			attribute = (Attribute) i.next ();
			out.writeShort (attribute.getNameIndex ());
			length = attribute.getLength ();
			out.writeInt (length);
			attribute.writeData (out, length);
		}
	}


	/**
	 *	Convenience method to be called by BCEntities when being visited
	 *	by a BCVisitor; this method will allow the visitor to visit all 
	 *	attributes of this entity.
	 */
	public void visitAttributes (BCVisitor visit)
	{
		Attribute attr;
		for (Iterator i = _attributes.iterator (); i.hasNext ();)
		{
			attr = (Attribute) i.next ();

			visit.enterAttribute (attr);
			attr.acceptVisit (visit);
			visit.exitAttribute (attr);
		}
	}
}
