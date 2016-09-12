package com.techtrader.modules.tools.bytecode;


import java.io.*;
import java.util.*;

import com.techtrader.modules.tools.bytecode.visitor.*;


/**
 *	Attribute describing the inner classes of a .class file.  Note: for
 *	methods that take in the name of the inner class, this refers to the
 *	short name it is referred to be within the owning class, not the full
 *	name.  For anonymous inner classes, use the empty String.
 *	TODO: Import and copy methods are broken.
 *	
 *	@author		Abe White
 */
public class InnerClassesAttribute
	extends Attribute
{
	private List _innerClasses = new LinkedList ();


	/**
	 *	Protected constructor.
	 */
	public InnerClassesAttribute (int nameIndex, BCEntity owner)
	{
		super (nameIndex, owner);
	}


	/**
	 *	Get all the inner classes owned by this entity.
	 *
	 *	@return		all owned inner classes, or empty array if none
	 */
	public InnerClass[] getInnerClasses ()
	{
		return (InnerClass[]) _innerClasses.toArray 
			(new InnerClass[_innerClasses.size ()]);
	}


	/**
	 *	Return the inner class with the given name.  If multiple inner classes
	 *	share the name, which is returned is undefined.
	 */
	public InnerClass getInnerClass (String name)
	{
		InnerClass next;
		for (Iterator i = _innerClasses.iterator (); i.hasNext ();)
		{
			next = (InnerClass) i.next ();
			if (next.getName ().equals (name))
				return next;
		}

		return null;
	}

	
	/**
	 *	Returns all inner classes with the given name.
 	 *
	 *	@return		the matching inner classes, or empty array if none
	 */
	public InnerClass[] getInnerClasses (String name)
	{
		List matches = new LinkedList ();

		InnerClass next;
		for (Iterator i = _innerClasses.iterator (); i.hasNext ();)
		{
			next = (InnerClass) i.next ();
			if (next.getName ().equals (name))
				matches.add (next);
		}

		return (InnerClass[]) matches.toArray (new InnerClass[matches.size ()]);
	}


	/**
	 *	Import an inner class from another entity, or make a copy of one
	 *	on this entity.
	 */
	public InnerClass importInnerClass (InnerClass inner)
	{
		InnerClass newInner = addInnerClass (inner.getName ());
		newInner.setAccessFlags (inner.getAccessFlags ());
		
		return newInner;
	}


	/**
	 *	Import all inner classes from another entity.
	 */
	public void importInnerClasses (InnerClassesAttribute other)
	{
		if (other == null)
			return;

		InnerClass[] inners = other.getInnerClasses ();
		for (int i = 0; i < inners.length; i++)
			importInnerClass (inners[i]);
	}


	/**
	 *	Add an inner class.
	 */
	public InnerClass addInnerClass ()
	{
		InnerClass inner = new InnerClass (this);
		_innerClasses.add (inner);

		return inner;
	}


	/**	
 	 *	Add an inner class with the given name.
	 */
	public InnerClass addInnerClass (String name)
	{
		InnerClass inner = new InnerClass (name, this);
		_innerClasses.add (inner);

		return inner;
	}


	/**
	 *	Clear all inner classes from this entity.
	 */
	public void clearInnerClasses ()
	{
		_innerClasses.clear ();
	}


	/**
	 *	Remove all inner classes with the given name from the list.
	 *
	 *	@return		true if an inner class was removed, false otherwise
	 */
	public boolean removeInnerClass (String name)
	{
		InnerClass[] matches = getInnerClasses (name);
		for (int i = 0; i < matches.length; i++)
			removeInnerClass (matches[i]);

		return (matches.length > 0);
	}


	/**
	 *	Remove the given inner class.  After being removed, the given InnerClass
	 *	is invalid, and the result of any operations on it are undefined.
	 *
	 *	@return		true if the inner class was removed, false otherwise
	 */
	public boolean removeInnerClass (InnerClass innerClass)
	{
		if (innerClass == null || !_innerClasses.remove (innerClass))
			return false;

		innerClass.invalidate ();
		return true;
	}


	public int getLength ()
	{
		return 2 + 8 * _innerClasses.size ();
	}


	protected void copy (Attribute other)
	{
		importInnerClasses ((InnerClassesAttribute) other);	
	}


	protected void readData (DataInput in, int length)
		throws IOException
	{
		_innerClasses.clear ();
		int numInnerClasses = in.readUnsignedShort ();

		InnerClass innerClass;
		for (int i = 0; i < numInnerClasses; i++)
		{
			innerClass = addInnerClass ();
			innerClass.readData (in);
		}
	}


	protected void writeData (DataOutput out, int length)
		throws IOException
	{
		out.writeShort (_innerClasses.size ());
		for (Iterator i = _innerClasses.iterator (); i.hasNext ();)
			((InnerClass) i.next ()).writeData (out);
	}


	public void acceptVisit (BCVisitor visit)
	{
		visit.enterInnerClassesAttribute (this);	
		
		for (Iterator i = _innerClasses.iterator (); i.hasNext ();)
			((InnerClass) i.next ()).acceptVisit (visit);

		visit.exitInnerClassesAttribute (this);	
	}
}
