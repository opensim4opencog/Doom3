package com.techtrader.modules.tools.bytecode;


import java.io.*;

import com.techtrader.modules.tools.bytecode.visitor.*;


/**
 *	Represents a linenumber; used for error reporting.
 *	TODO: Allow high-level setting of line numbers for Instruction 
 * 	groups.
 *	
 *	@author		Abe White
 */
public class LineNumber
{
	private int	_startPc 	= 0;
	private int	_lineNumber = 0;

	private LineNumberTableAttribute _owner = null;


	/**
	 *	Protected constructor.
	 */
	protected LineNumber (LineNumberTableAttribute owner)
	{
		_owner = owner;
	}


	/**
	 *	Line numbers are stored in a LineNumberTableAttribute.
	 */
	public LineNumberTableAttribute getOwner ()
	{
		return _owner;
	}


	/**
	 *	Invaildate the line number after it is removed from the 
	 *	LineNumberTableAttribute.
	 */
	protected void invalidate ()
	{
		_owner = null;
	}


	/**	
 	 *	Get the index into the code byte array at which this line starts.
	 */
	public int getStartPc ()
	{
		return _startPc;
	}


	/**	
 	 *	Set the index into the code byte array at which this line starts.
	 */
	public void setStartPc (int startPc)
	{
		_startPc = startPc;
	}


	/**
	 *	Get the line number this entity represents.
	 */
	public int getLineNumber ()
	{
		return _lineNumber;
	}


	/**
	 *	Get the line number this entity represents.
	 */
	public void setLineNumber (int lineNumber)
	{
		_lineNumber = lineNumber;
	}


	protected void readData (DataInput in)
		throws IOException
	{
		setStartPc (in.readUnsignedShort ());
		setLineNumber (in.readUnsignedShort ());
	}


	protected void writeData (DataOutput out)
		throws IOException
	{
		out.writeShort (getStartPc ());
		out.writeShort (getLineNumber ());
	}


	public void acceptVisit (BCVisitor visit)
	{
		visit.enterLineNumber (this);
		visit.exitLineNumber (this);
	}
}
