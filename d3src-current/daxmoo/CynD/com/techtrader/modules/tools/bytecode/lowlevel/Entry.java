package com.techtrader.modules.tools.bytecode.lowlevel;


import java.io.*;
import java.util.*;

import com.techtrader.modules.tools.bytecode.visitor.*;


/**
 *	Base interface for all constant pool entries.  Every Entry has a one-byte
 *	code representing the type of Entry, where each entry type may contain
 *	different information.
 *	
 *	@author		Abe White
 */
public interface Entry
	extends VisitAcceptor
{
	/**
	 *	Get the constant for the type of entry represented.
	 */
	public int getType ();


	/**
	 *	This is called by the ClassRep after reading the entry type.
	 */
	public void readData (DataInput in)
		throws IOException;


	/**
	 *	This is called by the ClassRep after writing the entry type.
	 */
	public void writeData (DataOutput out)
		throws IOException;


	/**
	 *	Return a suitable hash key for this entry.
	 */
	public String getKey ();
}
