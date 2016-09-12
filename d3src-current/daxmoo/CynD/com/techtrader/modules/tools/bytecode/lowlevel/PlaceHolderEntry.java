package com.techtrader.modules.tools.bytecode.lowlevel;


import java.io.*;

import com.techtrader.modules.tools.bytecode.visitor.*;


/**
 *	The PlaceHolderEntry is inserted into the constant pool after LongEntries
 *	and DoubleEntries to maintain the proper indexing, as these types take
 *	up 2 indeces in the constant pool.
 *	
 *	@author		Abe White
 */
public class PlaceHolderEntry
	implements Entry, LowLevelConstants
{
	public int getType ()
	{
		return ENTRY_PLACEHOLDER;
	}


	public void readData (DataInput in)
		throws IOException
	{
	}


	public void writeData (DataOutput out)
		throws IOException
	{
	}


	public String getKey ()
	{
		return null;
	}


	public void acceptVisit (BCVisitor visit)
	{
		visit.enterPlaceHolderEntry (this);
		visit.exitPlaceHolderEntry (this);
	}
}
