package com.techtrader.modules.tools.bytecode;


import java.io.*;
import java.util.*;

import com.techtrader.modules.tools.bytecode.visitor.*;


/**
 *	A LineNumberTableAttributs holds a table of line number to program counter
 *	mappings, so that errors can be reported with the correct line number.
 *	TODO: Allow high-level manipulation of the line numbers.
 *	
 *	@author		Abe White
 */
public class LineNumberTableAttribute
	extends Attribute
{
	private List _lineNumbers = new LinkedList ();


	/**
	 *	Protected constructor.
	 */
	public LineNumberTableAttribute (int nameIndex, BCEntity owner)
	{
		super (nameIndex, owner);
	}


	/**
	 *	Get the line numbers held in this table.
	 */
	public LineNumber[] getLineNumbers ()
	{
		return (LineNumber[]) _lineNumbers.toArray 
			(new LineNumber[_lineNumbers.size ()]);
	}


	/**
	 *	Import a line number from another method.
	 */
	public LineNumber importLineNumber (LineNumber ln)
	{
		if (ln == null)
			throw new NullPointerException ();

		LineNumber newLine = addLineNumber ();
		newLine.setStartPc (ln.getStartPc ());
		newLine.setLineNumber (ln.getLineNumber ());

		return newLine;
	}


	/**
	 *	Import all line numbers from another method.
	 */
	public void importLineNumbers (LineNumberTableAttribute lns)
	{
		LineNumber[] ln = lns.getLineNumbers ();
		for (int i = 0; i < ln.length; i++)
			importLineNumber (ln[i]);
	}


	/**
	 *	Add a new line number to this table.
	 *
	 *	@return		the index into the list at which the line number was added
	 */
	public LineNumber addLineNumber ()
	{
		LineNumber lineNumber = new LineNumber (this);
		_lineNumbers.add (lineNumber);

		return lineNumber;
	}


	/**
	 *	Clear the line numbers.
	 */
	public void clearLineNumbers ()
	{
		_lineNumbers.clear ();
	}


	/**
	 *	Remove the given LineNumber.
	 */
	public boolean removeLineNumber (LineNumber ln)
	{
		if (ln == null || !_lineNumbers.remove (ln))
			return false;
		
		ln.invalidate ();
		return true;
	}	


	public int getLength ()
	{
		return 2 + 4 * _lineNumbers.size ();
	}


	protected void copy (Attribute other)
	{
		importLineNumbers ((LineNumberTableAttribute) other);
	}


	protected void readData (DataInput in, int length)
		throws IOException
	{
		_lineNumbers.clear ();
		int numLines = in.readUnsignedShort ();

		LineNumber lineNumber;
		for (int i = 0; i < numLines; i++)
		{
			lineNumber = addLineNumber ();
			lineNumber.readData (in);
		}
	}


	protected void writeData (DataOutput out, int length)
		throws IOException
	{
		out.writeShort (_lineNumbers.size ());
		for (Iterator i = _lineNumbers.iterator (); i.hasNext ();)
			((LineNumber) i.next ()).writeData (out);
	}


	public void acceptVisit (BCVisitor visit)
	{
		visit.enterLineNumberTableAttribute (this);

		for (Iterator i = _lineNumbers.iterator (); i.hasNext ();)
			((LineNumber) i.next ()).acceptVisit (visit);

		visit.exitLineNumberTableAttribute (this);
	}
}
