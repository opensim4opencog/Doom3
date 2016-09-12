package com.techtrader.modules.tools.bytecode.lowlevel;


import java.io.*;

import com.techtrader.modules.tools.bytecode.visitor.*;


/**
 *	Represents a method in the constant pool.
 *	
 *	@author		Abe White
 */
public class MethodEntry
	extends ComplexEntry
{
	public int getType ()
	{
		return ENTRY_METHOD;
	}


	public void acceptVisit (BCVisitor visit)
	{
		visit.enterMethodEntry (this);
		visit.exitMethodEntry (this);
	}
}
