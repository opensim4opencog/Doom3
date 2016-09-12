package com.techtrader.modules.tools.bytecode;


import java.util.*;


/**
 *	An InstructionPtr represents an entity that maintains ptrs to instructions
 *	in a code block. 
 *
 *	@author		Abe White
 */
public interface InstructionPtr
{
	/**
 	 *	Use the byte indexes read from the .class file to calculate and
	 *	set references to the target instruction(s) for this ptr.
	 *	This method will be called after the byte code
 	 *	has been read in for the first time.
	 *
	 *	@param	codes	the list of opcodes in the method
	 */
	public void setMarkers (List instructions);
}
