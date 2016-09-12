package com.techtrader.modules.tools.bytecode;


/**
 *	A MONITORENTER or MONITOREXIT instruction.
 *
 *	@author		Abe White
 */
public abstract class MonitorInstruction
	extends Instruction
{
	protected MonitorInstruction (Code owner)
	{
		super (owner);
	}


	public int getStackChange ()
	{
		return -1;
	}
}
