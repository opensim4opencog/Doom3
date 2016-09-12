package com.techtrader.modules.tools.bytecode;


import java.io.*;

import com.techtrader.modules.tools.bytecode.visitor.*;


/**
 *	MONITORENTER
 *
 *	@author		Abe White
 */
public class MonitorEnterInstruction
	extends MonitorInstruction
{
	protected MonitorEnterInstruction (Code owner)
	{
		super (owner);
		_opcode = MONITORENTER;
	}


	public void acceptVisit (BCVisitor visit)
	{
		visit.enterMonitorEnterInstruction (this);
		visit.exitMonitorEnterInstruction (this);
	}
}
