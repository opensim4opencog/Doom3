package com.techtrader.modules.tools.bytecode;


import java.util.*;
import java.io.*;

import com.techtrader.modules.tools.bytecode.visitor.*;


/**
 *	A TABLESWITCH instruction.
 *	
 *	@author		Abe White
 */
public class TableSwitchInstruction
	extends JumpInstruction
{
	// case info
	private int		_low		= 0;
	private int		_high		= 0;
	private List	_offsets	= new LinkedList ();
	private List	_targets	= new LinkedList ();


	protected TableSwitchInstruction (Code owner)
	{
		super (owner, TABLESWITCH);
	}


	/**
	 *	Synonymous with getTarget().
	 */
	public Instruction getDefaultTarget ()
	{
		return getTarget ();
	}


	/**
	 *	Synonymous with setTarget().
	 */
	public void setDefaultTarget (Instruction jpt)
	{
		setTarget (jpt);
	}


	/**
	 *	Synonymous with getOffset().
	 */
	public int getDefaultOffset ()
	{
		return getOffset ();
	}


	/**
	 *	Synonymous with setOffset().
	 */
	public void setDefaultOffset (int offset)
	{
		setOffset (offset);
	}


	public int getLow ()
	{
		return _low;
	}


	public void setLow (int low)
	{
		_low = low;
	}


	public int getHigh ()
	{
		return _high;
	}


	public void setHigh (int high)
	{
		_high = high;
	}


	/**
	 *	Set the jumppts for this switch.
	 */
	public void setTargets (Instruction[] targets)
	{
		_targets.clear ();
		_offsets.clear ();

		if (targets != null)
			for (int i = 0; i < targets.length; i++)
				_targets.add (targets[i]);
	}


	/**
	 *	Set the offsets for this switch.
	 */
	public void setOffsets (int[] offsets)
	{
		_targets.clear ();
		_offsets.clear ();

		if (offsets != null)
			for (int i = 0; i < offsets.length; i++)
				_offsets.add (new Integer (offsets[i]));
	}


	public Instruction[] getTargets ()
	{
		return (Instruction[]) _targets.toArray 
			(new Instruction[_targets.size ()]);
	}


	public int[] getOffsets ()
	{
		if (_targets.size () > 0)
		{
			_offsets.clear ();
			for (Iterator i = _targets.iterator (); i.hasNext ();)
				_offsets.add (new Integer (((Instruction) i.next ()).
					getByteIndex () - getByteIndex ()));
		}

		int[] offsets = new int[_offsets.size ()];

		Iterator jpItr = _offsets.iterator ();
		for (int i = 0; i < offsets.length; i++)
			offsets[i] = ((Integer) jpItr.next ()).intValue ();

		return offsets;
	}


	public void addTarget (Instruction target)
	{
		_targets.add (target);
	}


	public int getLength ()
	{
		// don't call super
		int length = 1;

		// make the first byte of the 'default' a multiple of 4 from the
		// start of the method
		int byteIndex = getByteIndex () + 1;
		for (; byteIndex % 4 != 0; byteIndex++, length++);

		// default, low, high
		length += 12;

		// offsets
		if (_targets.size () > 0)
			length += 4 * _targets.size ();
		else
			length += 4 * _offsets.size ();

		return length;
	}


	public int getStackChange ()
	{
		return -1;
	}


	public void readData (DataInput in)
		throws IOException
	{
		// don't call super
	
		for (int byteIndex = getByteIndex ()+1; byteIndex % 4 != 0; byteIndex++)
			in.readByte ();

		setOffset (in.readInt ());
		setLow (in.readInt ());
		setHigh (in.readInt ());

		_offsets.clear ();
		_targets.clear ();
		for (int i = 0; i < (_high - _low + 1); i++)
			_offsets.add (new Integer (in.readInt ()));
	}


	public void writeData (DataOutput out)
		throws IOException
	{
		// don't call super

		for (int byteIndex = getByteIndex ()+1; byteIndex % 4 != 0; byteIndex++)
			out.writeByte (0);

		out.writeInt (getOffset ());
		out.writeInt (getLow ());
		out.writeInt (getHigh ());

		int[] offsets = getOffsets ();
		for (int i = 0; i < offsets.length; i++)
			out.writeInt (offsets[i]);
	}


	private Instruction findTarget (int jumpByteIndex, List inss)
	{
		Instruction ins;
		for (Iterator i = inss.iterator (); i.hasNext ();)
		{
			ins = (Instruction) i.next ();

			if (ins.getByteIndex () == jumpByteIndex)
				return ins;
		}

		return null;
	}


	public void setMarkers (List inss)
	{
		super.setMarkers (inss);
		_targets.clear ();

		int byteIndex = getByteIndex ();
		int jumpByteIndex;
		for (Iterator i = _offsets.iterator (); i.hasNext ();)
		{
			jumpByteIndex = byteIndex + ((Number) i.next ()).intValue ();
			_targets.add (findTarget (jumpByteIndex, inss));
		}
	}


	public void acceptVisit (BCVisitor visit)
	{
		visit.enterTableSwitchInstruction (this);
		visit.exitTableSwitchInstruction (this);
	}
}
