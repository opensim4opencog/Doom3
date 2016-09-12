package com.techtrader.modules.tools.bytecode.lowlevel;


import java.io.*;

import com.techtrader.modules.tools.bytecode.visitor.*;


/**
 *	Represents an interface method in the constant pool.  
 *	Referenced by opcodes.
 *	
 *	@author		Abe White
 */
public class InterfaceMethodEntry
	extends ComplexEntry
{
	public int getType ()
	{
		return ENTRY_INTMETHOD;
	}


	public void acceptVisit (BCVisitor visit)
	{
		visit.enterInterfaceMethodEntry (this);
		visit.exitInterfaceMethodEntry (this);
	}
}
