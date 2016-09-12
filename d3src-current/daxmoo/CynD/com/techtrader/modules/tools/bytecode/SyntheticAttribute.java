package com.techtrader.modules.tools.bytecode;


import com.techtrader.modules.tools.bytecode.visitor.*;


/**
 *	Attribute that should be present for .class files with generated 
 *	source code.
 *	
 *	@author		Abe White
 */
public class SyntheticAttribute
	extends Attribute
{
	/**
	 *	Protected constructor.
	 */
	public SyntheticAttribute (int nameIndex, BCEntity owner)
	{
		super (nameIndex, owner);
	}


	public void acceptVisit (BCVisitor visit)
	{
		visit.enterSyntheticAttribute (this);
		visit.exitSyntheticAttribute (this);
	}
}
