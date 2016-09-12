package com.techtrader.modules.tools.bytecode;


import com.techtrader.modules.tools.bytecode.visitor.*;


/**
 *	MONITOREXIT
 *
 *	@author		Abe White
 */
public class MonitorExitInstruction
	extends MonitorInstruction
{
	protected MonitorExitInstruction (Code owner)
	{
		super (owner);
		_opcode = MONITOREXIT;
	}


	public void acceptVisit (BCVisitor visit)
	{
		visit.enterMonitorExitInstruction (this);
		visit.exitMonitorExitInstruction (this);
	}
}
