package com.techtrader.modules.tools.bytecode;


import java.util.*;
import java.io.*;

import com.techtrader.modules.tools.bytecode.visitor.*;


/**
 *	Represents a try {} catch() {} statement in bytecode.
 *
 *	@author		Abe White
 */
public class ExceptionHandler
	implements InstructionPtr
{
	private int	_startPc 		= 0;
	private int	_endPc			= 0;
	private int	_handlerPc 		= 0;
	private int	_catchTypeIndex = 0;

	private Code 		_owner 		= null;
	private Instruction _start		= null;
	private Instruction _end		= null;
	private Instruction _handler	= null;


	/**
	 *	Protected constructor.
	 */
	protected ExceptionHandler (Code owner)
	{
		_owner = owner;
	}


	/**
	 *	Used to invalidate the handler when it is removed from the
	 *	code block.
	 */
	protected void invalidate ()
	{
		_owner = null;
	}


	/**
	 *	Get the program counter start position for this exception handler.
	 *	This represents an index into the code byte array.
	 */
	public int getStartPc ()
	{
		if (_start != null)
			return _start.getByteIndex ();
	
		return _startPc;
	}


	/**
	 *	Set the program counter start position for this exception handler.
	 *	This represents an index into the code byte array.
	 */
	public void setStartPc (int startPc)
	{
		_startPc = startPc;
		_start = null;
	}


	/**
	 *	Set the Instruction marking the beginning of the try block.  The 
	 *	Instruction must already be a part of the method.
	 *	WARNING: if this instruction is deleted, the results are undefined.
	 */
	public void setTryStart (Instruction instruction)
	{
		_start = instruction;
	}


	/**
 	 *	Get the instruction marking the beginning of the try {} block.
	 *	WARNING: if this instruction is deleted, the results are undefined.
	 */
	public Instruction getTryStart ()
	{
		return _start;
	}


	/**
	 *	Get the program counter end position for this exception handler.
	 *	This represents an index into the code byte array.
	 */
	public int getEndPc ()
	{
		if (_end != null)
			return _end.getByteIndex () + _end.getLength ();

		return _endPc;
	}


	/**
	 *	Set the program counter end position for this exception handler.
	 *	This represents an index into the code byte array.
	 */
	public void setEndPc (int endPc)
	{
		_endPc = endPc;
		_end = null;
	}


	/**
	 *	Set the Instruction at the end of the try block.  The 
	 *	Instruction must already be a part of the method.
	 *	WARNING: if this instruction is deleted, the results are undefined.
	 */
	public void setTryEnd (Instruction instruction)
	{
		_end = instruction;
	}


	/**
 	 *	Get the instruction at the end of the try {} block.
	 *	WARNING: if this instruction is deleted, the results are undefined.
	 */
	public Instruction getTryEnd ()
	{
		return _end;
	}


	/**
	 *	Get the start of the actual exception handler code.
	 *	This represents an index into the code byte array.
	 */
	public int getHandlerPc ()
	{
		if (_handler != null)
			return _handler.getByteIndex ();

		return _handlerPc;
	}


	/**
	 *	Get the start of the actual exception handler code.
	 *	This represents an index into the code byte array.
	 */
	public void setHandlerPc (int handlerPc)
	{
		_handlerPc = handlerPc;
		_handler =  null;
	}


	/**
	 *	Set the Instruction marking the beginning of the catch block.  The 
	 *	Instruction must already be a part of the method.
	 *	WARNING: if this instruction is deleted, the results are undefined.
	 */
	public void setHandlerStart (Instruction instruction)
	{
		_handler = instruction;
	}


	/**
 	 *	Get the instruction marking the beginning of the catch {} block.
	 *	WARNING: if this instruction is deleted, the results are undefined.
	 */
	public Instruction getHandlerStart ()
	{
		return _handler;
	}


	/**
	 *	Get the index into the constant pool of the ClassEntry describing
	 *	the Exception type this handler catches.
	 */
	public int getCatchTypeIndex ()
	{
		return _catchTypeIndex;
	}


	/**
	 *	Set the index into the constant pool of the ClassEntry describing
	 *	the Exception type this handler catches.
	 */
	public void setCatchTypeIndex (int catchTypeIndex)
	{
		_catchTypeIndex = catchTypeIndex;
	}


	/**
	 *	Get the class of the catch() type; returns null for catch-all
	 *	clauses used to implement finally blocks.
	 */
	public String getCatchTypeName ()
	{
		if (_catchTypeIndex == 0)
			return null;

		return BCHelper.getExternalForm (_owner.getPool ().getClassName
			(_catchTypeIndex), true);
	}


	/**
	 *	Set the class of the catch() type, or null for catch-all clauses used
	 *	with finally blocks.
	 */
	public void setCatchTypeName (String name)
	{
		if (name == null)
			_catchTypeIndex = 0;
		else
			_catchTypeIndex = _owner.getPool ().setClassName 
				(0, BCHelper.getInternalForm (name, false));
	}


	/**
	 *	Get the class of the catch() type; returns null for catch-all
	 *	clauses used to implement finally blocks.
	 */
	public Class getCatchType ()
		throws ClassNotFoundException
	{
		if (_catchTypeIndex == 0)
			return null;

		return BCHelper.classForName (_owner.getPool ().getClassName 
			(_catchTypeIndex));
	}


	/**
	 *	Set the class of the catch() type, or null for catch-all clauses used
	 *	for finally blocks.
	 */
	public void setCatchType (Class type)
	{
		if (type == null)
			setCatchTypeName (null);
		else
			setCatchTypeName (type.getName ());
	}


	public void setMarkers (List opcodes)
	{
		Instruction ins = null;
		Instruction previous;
		for (Iterator i = opcodes.iterator (); i.hasNext ();)
		{
			previous = ins;
			ins = (Instruction) i.next ();

			if (ins.getByteIndex () == _startPc)
				setTryStart (ins);
			if (ins.getByteIndex () == _endPc)
				setTryEnd (previous);
			if (ins.getByteIndex () == _handlerPc)
				setHandlerStart (ins);
		}
	}


	protected void copy (ExceptionHandler orig)
	{
		setStartPc (orig.getStartPc ());
		setEndPc (orig.getEndPc ());
		setHandlerPc (orig.getHandlerPc ());

		// done at a high level so that if the name isn't in our constant pool,
		// it will be added
		setCatchTypeName (orig.getCatchTypeName ());
	}


	protected void readData (DataInput in)
		throws IOException
	{
		setStartPc (in.readUnsignedShort ());
		setEndPc (in.readUnsignedShort ());
		setHandlerPc (in.readUnsignedShort ());
		setCatchTypeIndex (in.readUnsignedShort ());
	}


	protected void writeData (DataOutput out)
		throws IOException
	{
		out.writeShort (getStartPc ());
		out.writeShort (getEndPc ());
		out.writeShort (getHandlerPc ());
		out.writeShort (getCatchTypeIndex ());
	}


	public void acceptVisit (BCVisitor visit)
	{
		visit.enterExceptionHandler (this);	
		visit.exitExceptionHandler (this);	
	}
}
