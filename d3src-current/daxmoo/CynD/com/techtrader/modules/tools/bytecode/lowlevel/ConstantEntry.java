package com.techtrader.modules.tools.bytecode.lowlevel;


/**
 *	Interface implemented by Entries representing constant values to
 *	generically access/mutate the constant value.
 *
 *	@author		Abe White
 */
public interface ConstantEntry
	extends Entry
{
	public Object getConstantValue ();


	public void setConstantValue (Object value);
}
