package com.techtrader.modules.tools.bytecode.lowlevel;


import java.io.*;

import com.techtrader.modules.tools.bytecode.visitor.*;


/**
 *	Represents a reference to a class field.
 *	
 *	@author		Abe White
 */
public class FieldEntry
	extends ComplexEntry
{
	public int getType ()
	{
		return ENTRY_FIELD;
	}


	public void acceptVisit (BCVisitor visit)
	{
		visit.enterFieldEntry (this);
		visit.exitFieldEntry (this);
	}
}
