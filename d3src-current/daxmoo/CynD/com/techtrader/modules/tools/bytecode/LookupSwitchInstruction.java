package com.techtrader.modules.tools.bytecode;


import java.util.*;
import java.io.*;

import com.techtrader.modules.tools.bytecode.visitor.*;


/**
 *	The LOOKUPSWITCH instruction.
 *	
 *	@author		Abe White
 */
public class LookupSwitchInstruction
	extends JumpInstruction
{
	// case info
	private List _matches	= new LinkedList ();
	private List _offsets	= new LinkedList ();
	private List _targets	= new LinkedList ();


	protected LookupSwitchInstruction (Code owner)
	{
		super (owner, LOOKUPSWITCH);
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
	public void setDefaultTarget (Instruction ins)
	{
		setTarget (ins);
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


	/**
	 *	Set the match-jumppt pairs for this switch.
	 */
	public void setCases (int[] matches, Instruction[] targets)
	{
		_matches.clear ();
		_targets.clear ();
		_offsets.clear ();

		if (matches != null)
			for (int i = 0; i < matches.length; i++)
				_matches.add (new Integer (matches[i]));

		if (targets != null)
			for (int i = 0; i < targets.length; i++)
				_targets.add (targets[i]);
	}


	/**
	 *	Set the match-offset pairs for this switch.
	 */
	public void setCases (int[] matches, int[] offsets)
	{
		_matches.clear ();
		_targets.clear ();
		_offsets.clear ();

		if (matches != null)
			for (int i = 0; i < matches.length; i++)
				_matches.add (new Integer (matches[i]));

		if (offsets != null)
			for (int i = 0; i < offsets.length; i++)
				_offsets.add (new Integer (offsets[i]));
	}


	public int[] getMatches ()
	{
		int[] matches = new int[_matches.size ()];

		Iterator matchesItr = _matches.iterator ();
		for (int i = 0; i < matches.length; i++)
			matches[i] = ((Integer) matchesItr.next ()).intValue ();

		return matches;
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


	public void addCase (int match, Instruction target)
	{
		_matches.add (new Integer (match));
		_targets.add (target);
	}


	public int getLength ()
	{
		// don't call super.getLength(), cause JumpInstruction will return
		// value assuming this is an 'if' or 'goto' instruction
		int length = 1;

		// make the first byte of the 'default' a multiple of 4 from the
		// start of the method
		int byteIndex = getByteIndex () + 1;
		for (; byteIndex % 4 != 0; byteIndex++, length++);

		// default, npairs
		length += 8;

		// pairs
		length += 8 * _matches.size ();

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

		_matches.clear ();
		_offsets.clear ();
		_targets.clear ();
		for (int i = 0, pairCount = in.readInt (); i < pairCount; i++)
		{
			_matches.add (new Integer (in.readInt ()));
			_offsets.add (new Integer (in.readInt ()));
		}
	}


	public void writeData (DataOutput out)
		throws IOException
	{
		// don't call super

		for (int byteIndex = getByteIndex ()+1; byteIndex % 4 != 0; byteIndex++)
			out.writeByte (0);

		out.writeInt (getOffset ());
		out.writeInt (_matches.size ());

		int[] matches = getMatches ();
		int[] offsets = getOffsets ();
		for (int i = 0; i < matches.length; i++)
		{
			out.writeInt (matches[i]);
			out.writeInt (offsets[i]);
		}
	}


	private Instruction findJumpPoint (int jumpByteIndex, List inss)
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
			_targets.add (findJumpPoint (jumpByteIndex, inss));
		}
	}


	public void acceptVisit (BCVisitor visit)
	{
		visit.enterLookupSwitchInstruction (this);
		visit.exitLookupSwitchInstruction (this);
	}
}
