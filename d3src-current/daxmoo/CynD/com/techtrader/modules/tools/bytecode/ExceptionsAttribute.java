package com.techtrader.modules.tools.bytecode;


import java.io.*;
import java.util.*;

import com.techtrader.modules.tools.bytecode.visitor.*;


/**
 *	Attribute indicating  what checked exceptions a method can throw; 
 *	referenced from a BCMethod.
 *	
 *	@author		Abe White
 */
public class ExceptionsAttribute
	extends Attribute
{
	private List _exceptionIndexes = new LinkedList ();


	/**
	 *	Protected constructor.
	 */
	public ExceptionsAttribute (int nameIndex, BCEntity owner)
	{
		super (nameIndex, owner);
	}


	/**	
 	 *	Get the indexes into the constant pool referencing the ClassEntrys 
	 *	that describe the exception types thrown by this method.
	 */
	public int[] getExceptionIndexes ()
	{
		int[] indexes = new int[_exceptionIndexes.size ()];

		Iterator indexItr =  _exceptionIndexes.iterator ();
		for (int i = 0; i < indexes.length; i++)
			indexes[i] = ((Integer) indexItr.next ()).intValue ();

		return indexes;
	}


	/**	
 	 *	Set the indexes into the constant pool referencing the ClassEntrys 
	 *	that describe the exception types thrown by this method.
	 */
	public void setExceptionIndexes (int[] exceptionIndexes)
	{
		_exceptionIndexes.clear ();

		if (exceptionIndexes != null)
			for (int i = 0; i < exceptionIndexes.length; i++)
				_exceptionIndexes.add (new Integer (exceptionIndexes[i]));
	}


	/**
	 *	Get the names of the exception types for this method.
	 */
	public String[] getExceptionTypeNames ()
	{
		String[] names = new String[_exceptionIndexes.size ()];

		Iterator exceptions = _exceptionIndexes.iterator ();
		for (int i = 0; i < names.length; i++)
			names[i] = BCHelper.getExternalForm (getPool ().getClassName
				(((Integer) exceptions.next ()).intValue ()), true);

		return names;
	}


	/**
	 *	Get the Class objects for the exceptions of this method.
	 */
	public Class[] getExceptionTypes ()
		throws ClassNotFoundException
	{
		Class[] types = new Class[_exceptionIndexes.size ()];

		Iterator exceptions = _exceptionIndexes.iterator ();
		for (int i = 0; i < types.length; i++)
			types[i] = BCHelper.classForName (getPool ().getClassName
				(((Integer) exceptions.next ()).intValue ()));

		return types;
	}


	/**
	 *	Set the checked exceptions thrown by this method.
	 */
	public void setExceptionTypeNames (String[] exceptions)
	{
		_exceptionIndexes.clear ();
		if (exceptions != null)
			for (int i = 0; i < exceptions.length; i++)
				addExceptionTypeName (exceptions[i]);
	}


	/**
	 *	Set the checked exceptions thrown by this method.
	 */
	public void setExceptionTypes (Class[] exceptions)
	{
		String[] names = null;
		if (exceptions != null)
		{
			names = new String[exceptions.length];
			for (int i = 0; i < exceptions.length; i++)
				names[i] = exceptions[i].getName ();
		}

		setExceptionTypeNames (names);
	}


	/**
	 *	Clear this method of all exception declarations.
	 */
	public void clearExceptions ()
	{
		_exceptionIndexes.clear ();
	}


	/**
	 *	Remove an exception thrown by this method.
	 */
	public boolean removeExceptionTypeName (String name)
	{
		String internalForm = BCHelper.getInternalForm (name, false);
		for (Iterator i = _exceptionIndexes.iterator (); i.hasNext ();)
		{
			if (getPool ().getClassName (((Integer) i.next ()).intValue ()).
				equals (internalForm))
			{
				i.remove ();
				return true;
			}
		}
		return false;
	}


	/**
	 *	Remove an exception thrown by this method.
	 */
	public boolean removeExceptionType (Class type)
	{
		return removeExceptionTypeName (type.getName ());
	}


	/**	
 	 *	Add an exception to those thrown by this method.
	 */
	public void addExceptionTypeName (String name)
	{
		int index = getPool ().setClassName (0,
			BCHelper.getInternalForm (name, false));

		_exceptionIndexes.add (new Integer (index));
	}


	/**	
 	 *	Add an exception to those thrown by this method.
	 */
	public void addExceptionType (Class type)
	{
		addExceptionTypeName (type.getName ());
	}


	/**
	 *	Return true if the method declares that it throws the given
	 *	exception.
	 */
	public boolean throwsException (String name)
	{
		String[] exceptions = getExceptionTypeNames ();
		for (int i = 0; i < exceptions.length; i++)
			if (exceptions[i].equals (name))
				return true;

		return false;
	}


	/**
	 *	Return true if the method declares that it throws the given
	 *	exception.
	 */
	public boolean throwsException (Class type)
	{
		return throwsException (type.getName ());
	}
	

	public int getLength ()
	{
		return 2 + 2 * _exceptionIndexes.size ();
	}


	protected void copy (Attribute other)
	{
		setExceptionTypeNames (((ExceptionsAttribute) other).
			getExceptionTypeNames ());
	}


	protected void readData (DataInput in, int length)
		throws IOException
	{
		_exceptionIndexes.clear ();
		int exceptionCount = in.readUnsignedShort ();
		for (int i = 0; i < exceptionCount; i++)
			_exceptionIndexes.add (new Integer (in.readUnsignedShort ()));
	}


	protected void writeData (DataOutput out, int length)
		throws IOException
	{
		out.writeShort (_exceptionIndexes.size ());
		for (Iterator i = _exceptionIndexes.iterator (); i.hasNext ();)
			out.writeShort (((Number) i.next ()).shortValue ());
	}


	public void acceptVisit (BCVisitor visit)
	{
		visit.enterExceptionsAttribute (this);	
		visit.exitExceptionsAttribute (this);	
	}
}
