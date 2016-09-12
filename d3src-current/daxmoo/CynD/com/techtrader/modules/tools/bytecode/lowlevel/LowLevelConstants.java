package com.techtrader.modules.tools.bytecode.lowlevel;


import java.util.*;


/**
 *	Interface to track constants used in lowlevel bytecode.  
 *	Entities can access these
 *	constants using the static <code>LowLevelConstants.</code> field prefix, 
 *	or implement this interface themselves to conveniently import the 
 *	constants into their own namespace.
 *
 *	@author		Abe White
 */
public interface LowLevelConstants
{
	public static final int ENTRY_PLACEHOLDER	= 0;
	public static final int ENTRY_UTF8			= 1;
	public static final int ENTRY_INT 			= 3;
	public static final int ENTRY_FLOAT 		= 4;
	public static final int ENTRY_LONG 			= 5;
	public static final int ENTRY_DOUBLE		= 6;
	public static final int ENTRY_CLASS 		= 7;
	public static final int ENTRY_STRING 		= 8;
	public static final int ENTRY_FIELD 		= 9;
	public static final int ENTRY_METHOD 		= 10;
	public static final int ENTRY_INTMETHOD 	= 11;
	public static final int ENTRY_NAME_AND_TYPE	= 12;
}
