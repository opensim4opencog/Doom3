package com.techtrader.modules.tools.bytecode;


import java.io.*;
import java.util.*;

import com.techtrader.modules.tools.bytecode.visitor.*;


/**
 *	Represents a local variable table for a method.
 *	TODO: Variables of type long or double should occupy two indeces;
 *	this is not taken into account here.
 *	
 *	@author		Abe White
 */
public class LocalVariableTableAttribute
	extends Attribute
{
	private List _localVariables = new LinkedList ();


	/**
	 *	Protected constructor.
	 */
	public LocalVariableTableAttribute (int nameIndex, BCEntity owner)
	{
		super (nameIndex, owner);
	}


	/**
	 *	Get all the locals of this method.
	 */
	public LocalVariable[] getLocalVariables ()
	{
		return (LocalVariable[]) _localVariables.toArray 
			(new LocalVariable[_localVariables.size ()]);
	}

	
	/**
	 *	Get the local with the given name.
	 */
	public LocalVariable getLocalVariable (String name)
	{
		LocalVariable next;
		for (Iterator i = _localVariables.iterator (); i.hasNext ();)
		{
			next = (LocalVariable) i.next ();
			if (next.getName ().equals (name))
				return next;
		}

		return null;
	}


	/**
	 *	Import a local variable from another method/class.  Note that
	 *	the program counter and length from the given local is copied 
 	 *	directly, and thus will be incorrect unless this method is the same
 	 *	as the one the local is copied from, or the pc and length are reset.
	 */
	public LocalVariable importLocalVariable (LocalVariable local)
	{
		LocalVariable newLocal= addLocalVariable 
			(local.getName (), local.getTypeName ());
		newLocal.setStartPc (local.getStartPc ());
		newLocal.setLength (local.getLength ());

		return newLocal;
	}


	/**
	 *	Import all locals from another method.
	 */
	public void importLocalVariables (LocalVariableTableAttribute other)
	{
		LocalVariable[] locals = other.getLocalVariables ();
		for (int i = 0; i < locals.length; i++)
			importLocalVariable (locals[i]);
	}


	/**
	 *	Add a local to this method.
	 */
	public LocalVariable addLocalVariable ()
	{
		LocalVariable local = new LocalVariable (this);
		local.setIndex (_localVariables.size ());
		_localVariables.add (local);

		return local;
	}


	/**
	 *	Add a local to this method.
	 */
	public LocalVariable addLocalVariable (String name, String type)
	{
		LocalVariable local = addLocalVariable ();
		local.setName (name);
		local.setTypeName (type);

		return local;
	}


	/**
	 *	Add a local to this method.
	 */
	public LocalVariable addLocalVariable (String name, Class type)
	{
		return addLocalVariable (name, type.getName ());
	}


	/**
	 *	Clear all locals from this method.
	 */
	public void clearLocalVariables ()
	{
		_localVariables.clear ();
	}


	/**
	 *	Removes the local with the given name from this method.
	 */
	public boolean removeLocalVariable (String name)
	{
		return removeLocalVariable (getLocalVariable (name));
	}


	/**	
 	 *	Removes a local from this method.  After this method, the local
	 *	will be invalid, and the result of any operations on it is undefined.
	 */
	public boolean removeLocalVariable (LocalVariable local)
	{
		if (local == null || !_localVariables.remove (local))
			return false;

		local.invalidate ();
		return true;
	}


	public int getLength ()
	{
		return 2 + 10 * _localVariables.size ();
	}


	protected void copy (Attribute other)
	{
		importLocalVariables ((LocalVariableTableAttribute) other);
	}


	protected void readData (DataInput in, int length)
		throws IOException
	{
		_localVariables.clear ();
		int numLocals = in.readUnsignedShort ();

		LocalVariable localVariable;
		for (int i = 0; i < numLocals; i++)
		{
			localVariable = addLocalVariable ();
			localVariable.readData (in);
		}
	}


	protected void writeData (DataOutput out, int length)
		throws IOException
	{
		out.writeShort (_localVariables.size ());
		for (Iterator i = _localVariables.iterator (); i.hasNext ();)
			((LocalVariable) i.next ()).writeData (out);
	}


	public void acceptVisit (BCVisitor visit)
	{
		visit.enterLocalVariableTableAttribute (this);

		for (Iterator i = _localVariables.iterator (); i.hasNext ();)
			((LocalVariable) i.next ()).acceptVisit (visit);

		visit.exitLocalVariableTableAttribute (this);
	}
}
