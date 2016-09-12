package com.techtrader.modules.tools.bytecode;


import com.techtrader.modules.tools.bytecode.visitor.*;


/**
 *	Attribute signifying that a method or class is deprecated.
 *	
 *	@author		Abe White
 */
public class DeprecatedAttribute
	extends Attribute
{
	/**
	 *	Protected constructor.
	 */
	public DeprecatedAttribute (int nameIndex, BCEntity owner)
	{
		super (nameIndex, owner);
	}


	public void acceptVisit (BCVisitor visit)
	{
		visit.enterDeprecatedAttribute (this);	
		visit.exitDeprecatedAttribute (this);	
	}
}
