package com.techtrader.modules.tools.bytecode;


import java.io.*;

import com.techtrader.modules.tools.bytecode.visitor.*;


/**
 *	Attribute naming the source file for this .class file.
 *	
 *	@author		Abe White
 */
public class SourceFileAttribute
	extends Attribute
{
	int	_sourceFileIndex = 0;


	/**
	 *	Protected constructor.
	 */
	public SourceFileAttribute (int nameIndex, BCEntity owner)
	{
		super (nameIndex, owner);
	}


	/**
	 *	Get the index into the constant pool of the UTF8Entry naming the
	 *	source file for this class.
	 */
	public int getSourceFileIndex ()
	{
		return _sourceFileIndex;
	}


	/**
	 *	Set the index into the constant pool of the UTF8Entry naming the
	 *	source file for this class.
	 */
	public void setSourceFileIndex (int sourceFileIndex)
	{
		_sourceFileIndex = sourceFileIndex;
	}


	/**
	 *	Get the name of the source file.
	 */
	public String getSourceFile ()
	{
		return getPool ().getUTF (_sourceFileIndex);
	}


	/**
	 *	Set the source file name.	
 	 */
	public void setSourceFile (String name)
	{
		_sourceFileIndex = getPool ().setUTF (0, name);
	}


	public int getLength ()
	{
		return 2;
	}


	protected void copy (Attribute other)
	{
		setSourceFile (((SourceFileAttribute) other).getSourceFile ());
	}


	protected void readData (DataInput in, int length)
		throws IOException
	{
		setSourceFileIndex (in.readUnsignedShort ());
	}


	protected void writeData (DataOutput out, int length)
		throws IOException
	{
		out.writeShort (getSourceFileIndex ());
	}


	public void acceptVisit (BCVisitor visit)
	{
		visit.enterSourceFileAttribute (this);
		visit.exitSourceFileAttribute (this);
	}
}
