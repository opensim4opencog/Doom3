package com.techtrader.modules.tools.bytecode;


import java.util.*;
import java.io.*;

import com.techtrader.modules.tools.bytecode.visitor.*;


/**
 *	Representation of a code block of a class.
 *	The methods of this class mimic those of
 *	the same name in the {@link java.util.ListIterator} class.  Note that 
 *	the size and index information of the Code block will change as opcodes
 *	are added.  
 *	<p>
 *	Code blocks are usually obtained from a BCMethod, but can also be
 *	constructed via the default Constructor.  Blocks created this way can
 *	be used to provide template instructions to the various search/replace
 *	methods in this class.
 *	<p>
 *	The Code class contains methods named after each JVM instruction, each
 *	of which adds the matching opcode to the code block at the 
 *	current iterator position.  There are also many pseudo-instruction
 *	methods that do not have a corresponding JVM opcode, but are provided
 *	for convenience when the exact opcode is difficult to determine at 
 *	compile time.  Unlike the other opcode methods, these convenience 
 *	methods have javadoc comments so that they are easy to pick out; 
 *	they should be skimmed to get an idea of the functionality that each 
 *	provides.  Also note that many Instructions are able to 'morph' their
 *	opcode on the fly as the arguments to the instruction change.  Thus
 *	the developer can initially call, for example, the aload_1 opcode, but
 *	later change the type to load to 'int', and the opcode will automatically
 *	morph to iload_1.
 *	
 *	@author		Abe White
 */
public class Code
	extends Attribute
	implements Constants
{
	private int 			_maxStack	= 0;
	private int 			_maxLocals	= 0;
	private List			_opcodes	= new LinkedList ();
	private List 			_handlers 	= new LinkedList ();
	private ListIterator	_li			= _opcodes.listIterator ();


	protected Code (int nameIndex, BCEntity owner)
	{
		super (nameIndex, owner);
	}


	/**
	 *	The public constructor is for creating template code modules	
 	 *	that can be used to produce Instructions to be used in 
	 *	matching for various search() and replace() methods.
	 */
	public Code ()
	{
		// create a new empty anon class so we can use its
		// constant pool to track the code we create
		super (0, new BCClass ().addMethod ());
		_nameIndex = getPool ().setUTF (0, ATTR_CODE);
	}


	/**
	 *	Get the maximum stack depth for this code block.
	 */
	public int getMaxStack ()
	{
		return _maxStack;
	}


	/**
	 *	Set the maximum stack depth for this code block.
	 */
	public void setMaxStack (int max)
	{
		_maxStack = max;
	}


	/**
	 *	Get the maximum number of local variables (including params)
	 *	in this method.
	 */
	public int getMaxLocals ()
	{
		return _maxLocals;
	}

	
	/**
	 *	Set the maximum number of local variables (including params) in 
	 *	this method.
	 */
	public void setMaxLocals (int max)
	{
		_maxLocals = max;
	}


	/**
	 *	Get the local variable index for the paramIndex'th parameter to 
	 *	the method.	 These numbers are different because 
	 *	a) non-static methods use the 0th local variable for the 'this' ptr, and
	 *	b) double and long values occupy two spots in the local 
	 *	variable array.
	 */
	public int getLocalsIndex (int paramIndex)
	{
		if (paramIndex < 0)
			return -1;

		int pos = 0;
		if (!((BCMethod) _owner).isStatic ())
			pos = 1;

		String[] params = ((BCMethod) _owner).getParamTypeNames ();
		for (int i = 0; i < paramIndex; i++, pos++)
			if (params[i].equals ("long") || params[i].equals ("double"))
				pos++;

		return pos;
	}


	/**
	 *	Get the next next available local variable index.
	 */
	public int getNextLocalsIndex ()
	{
		calculateMaxLocals ();
		return getMaxLocals ();
	}


	/**
	 *	Ask the code to figure out the number of locals it needs based on
	 *	the instructions used and the parameters of the method this code
	 *	block is a part of.
	 */
	public void calculateMaxLocals ()
	{
		// start off assuming the max number needed is the 
		// number for all the params
		String[] params = ((BCMethod) _owner).getParamTypeNames ();
		int max = 0;
		if (params.length == 0 && !((BCMethod) _owner).isStatic ())
			max = 1;
		else if (params.length > 0)
		{
			max = getLocalsIndex (params.length - 1) + 1;
			if (params[params.length-1].equals ("long")
				|| params[params.length-1].equals ("double"))
				max++;
		}	

		// check to see if there are any store instructions that
		// try to reference beyond that point
		Instruction ins;
		StoreInstruction store;
		int current;
		for (Iterator i = _opcodes.iterator (); i.hasNext ();)
		{
			current = 0;
			ins = (Instruction) i.next ();

			if (ins instanceof StoreInstruction)
			{
				store = (StoreInstruction) ins;
				current = store.getIndex () + 1;
				if (store.getType ().equals (long.class) 
					|| store.getType ().equals (double.class))	
					current++;

				if (current > max)
					max = current;
			}
		}

		setMaxLocals (max);
	}


	/**
	 *	Ask the code to figure out the maximum stack depth it needs
	 *	the instructions used.
	 */
	public void calculateMaxStack ()
	{
		int stack = 0;
		int max = 0;

		ExceptionHandler[] handlers = getExceptionHandlers ();
		Instruction ins;
		for (Iterator i = _opcodes.iterator (); i.hasNext ();)
		{
			ins = (Instruction) i.next ();
			stack += ins.getStackChange ();

			// if this is the start of a try, the exception will be placed
			// on the stack
			for (int j = 0; j < handlers.length; j++)
				if (handlers[j].getTryStart () == ins)
					stack++;

			if (stack > max)
				max = stack;
		}

		setMaxStack (max);
	}


	/**
	 *	Get the exception handlers active in this code block, or an
	 *	empty array if none.
	 */
	public ExceptionHandler[] getExceptionHandlers ()
	{
		return (ExceptionHandler[]) _handlers.toArray 
			(new ExceptionHandler[_handlers.size ()]);
	}


	/**
	 *	Get the exception handler that catches the given exception type;
	 *	if multiple handlers catch the given type, which is returned is
	 *	undefined.
	 */
	public ExceptionHandler getExceptionHandler (Class catchType)
	{
		return getExceptionHandler (catchType.getName ());
	}


	/**
	 *	Get the exception handler that catches the given exception type;
	 *	if multiple handlers catch the given type, which is returned is
	 *	undefined.
	 */
	public ExceptionHandler getExceptionHandler (String catchType)
	{
		ExceptionHandler next;
		for (Iterator i = _handlers.iterator (); i.hasNext ();)
		{
			next = (ExceptionHandler) i.next ();
			if (next.getCatchTypeName ().equals (catchType))
				return next;
		}

		return null;
	}


	/**
	 *	Get all exception handlers that catch the given exception type.
	 */
	public ExceptionHandler[] getExceptionHandlers (Class catchType)
	{
		return getExceptionHandlers (catchType.getName ());
	}


	/**
	 *	Get all exception handlers that catch the given exception type.
	 */
	public ExceptionHandler[] getExceptionHandlers (String catchType)
	{
		List matches = new LinkedList ();

		ExceptionHandler next;
		for (Iterator i = _handlers.iterator (); i.hasNext ();)
		{
			next = (ExceptionHandler) i.next ();
			if (next.getCatchTypeName ().equals (catchType))
				matches.add (next);
		}

		return (ExceptionHandler[]) matches.toArray 
			(new ExceptionHandler[matches.size ()]);
	}


	/**
	 *	Add an exception handler to this code block.
	 */
	public ExceptionHandler addExceptionHandler ()	
	{
		ExceptionHandler handler = new ExceptionHandler (this);
		_handlers.add (handler);

		return handler;
	}


	/**
	 *	Add an exception handler to this code block.
	 *
	 *	@param	tryStart		the first instruction of the try {} block
 	 *	@param	tryEnd			the last instruction of the try {} block
	 *	@param	handlerStart	the first instruction of the catch {} block
	 *	@param	catchType		the type of Exception being caught
	 */
	public ExceptionHandler addExceptionHandler (Instruction tryStart,
		Instruction tryEnd, Instruction handlerStart, Class catchType)
	{
		String catchName = null;
		if (catchType != null)
			catchName = catchType.getName ();
			
		return addExceptionHandler (tryStart, tryEnd, handlerStart, catchName);
	}


	/**
	 *	Add an exception handler to this code block.
	 *
	 *	@param	tryStart		the first instruction of the try {} block
 	 *	@param	tryEnd			the last instruction of the try {} block
	 *	@param	handlerStart	the first instruction of the catch {} block
	 *	@param	catchType		the type of Exception being caught
 	 */
	public ExceptionHandler addExceptionHandler (Instruction tryStart,
		Instruction tryEnd, Instruction handlerStart, String catchType)
	{
		ExceptionHandler handler = addExceptionHandler ();
		handler.setTryStart (tryStart);
		handler.setTryEnd (tryEnd);
		handler.setHandlerStart (handlerStart);
		handler.setCatchTypeName (catchType);

		return handler;
	}


	/**
	 *	Clear all exception handlers.
	 */
	public void clearExceptionHandlers ()
	{
		_handlers.clear ();
	}


	/**
	 *	Remove all exception handlers that catch the given type.
	 */
	public boolean removeExceptionHandler (Class catchType)
	{
		ExceptionHandler[] matches = getExceptionHandlers (catchType);
		for (int i = 0; i < matches.length; i++)
			removeExceptionHandler (matches[i]);

		return (matches.length > 0);
	}


	/**
	 *	Remove all exception handlers that catch the given type.
	 */
	public boolean removeExceptionHandler (String catchType)
	{
		ExceptionHandler[] matches = getExceptionHandlers (catchType);
		for (int i = 0; i < matches.length; i++)
			removeExceptionHandler (matches[i]);

		return (matches.length > 0);
	}


	/**
	 *	Remove an exception handler from this code block.
	 */
	public boolean removeExceptionHandler (ExceptionHandler handler)
	{
		if (handler == null || !_handlers.remove (handler))
			return false;

		handler.invalidate ();
		return true;
	}


	/**
	 *	Return the number of instructions in the method.
	 */
	public int size ()
	{
		return _opcodes.size ();
	}


	/**
 	 *	Reset the position of the instruction iterator to the first opcode.
	 */
	public void beforeFirst ()
	{
		_li = _opcodes.listIterator ();
	}


	/**
	 *	Set the position of the instruction iterator to after the last opcode.
	 */
	public void afterLast ()
	{
		_li = _opcodes.listIterator (_opcodes.size ());
	}


	/**
	 *	Position the iterator just before the given instruction.  The 
 	 *	instruction must belong to this method.
	 */
	public void before (Instruction ins)
	{
		Iterator instructions = _opcodes.iterator ();
		int pos = 0;
		for (; instructions.hasNext (); pos++)
			if (instructions.next () == ins)
				break;

		_li = _opcodes.listIterator (pos);
	}


	/**
	 *	Position the iterator just after the given instruction.  The 
 	 *	instruction must belong to this method.
	 */
	public void after (Instruction ins)
	{
		before (ins);
		next ();
	}


	/**
	 *	Return true if a subsequent call to next() will return an instruction.
	 */
	public boolean hasNext ()
	{
		return _li.hasNext ();
	}


	/**
	 *	Return true if a subsequent call to previous() will return an
	 *	instruction.
	 */
	public boolean hasPrevious ()
	{
		return _li.hasPrevious ();
	}


	/**
	 *	Return the next instruction.
	 */
	public Instruction next ()
	{
		return (Instruction) _li.next ();
	}


	/**
	 *	Return the index of the next instruction, or size() if at end.
	 */
	public int nextIndex ()
	{
		return _li.nextIndex ();
	}


	/**
	 *	Return the previous instruction.
	 */
	public Instruction previous ()
	{
		return (Instruction) _li.previous ();
	}


	/**
	 *	Return the index of the previous instruction, or -1 if at beginning.
	 */
	public int previousIndex ()
	{
		return _li.previousIndex ();
	}


	/**
	 *	Place the iterator before the given list index.
	 */
	public void before (int index)
	{
		_li = _opcodes.listIterator (index);
	}


	/**
	 *	Place the iterator after the given list index.
	 */
	public void after (int index)
	{
		before (index);
		next ();
	}


	/**
	 *	Find the next Instruction from the current iterator position that 
	 *	matches the given one, according to	the equals() methods of the 
	 *	Instruction types.  This allows for matching based on template
	 *	instructions, as the equals() methods of most Instructions return
	 *	true if the information for the given Instruction has not been filled
	 *	in.  If a match is found, the iterator is placed after the matching
 	 *	Instruction.  If no match is found, moves the iterator to afterLast().
 	 *
	 *	@return		true if match found	
	 */
	public boolean searchForward (Instruction template)
	{
		if (template == null)
			return false;

		while (hasNext ())
			if (template.equals (next ()))
				return true;

		return false;
	}


	/**
	 *	Find the closest previous Instruction from the current iterator 
	 *	position that matches the given one, according to the equals() 
 	 *	methods of the Instruction types.  This allows for matching based on 
	 *	template instructions, as the equals() methods of most Instructions 
	 *	returns true if the information for the given Instruction has not been 
	 *	filled in.  If a match is found, the iterator is placed before the 
	 *	matching Instruction.  If no match is found, moves the iterator to 
	 *	beforeFirst().
 	 *
	 *	@return		true if match found	
	 */
	public boolean searchBackward (Instruction template)
	{
		if (template == null)
			return false;

		while (hasPrevious ())
			if (template.equals (previous ()))
				return true;

		return false;
	}


	/**
	 *	Replaces the next Instruction with the given one.  It is an error to
	 *	call this method if the iterator is afterLast().  After this 
	 *	method, the iterator will be after the newly added Instruction.
	 *  This method will also make sure that all jump points
	 *	that referenced the old opcode are updated correctly.
 	 *
	 *	@return		the newly added Instruction
	 */
	public Instruction replaceNext (Instruction with)
	{
		Instruction	orig = (Instruction) _li.next ();
		Instruction	ins	 = null;

		_li.remove ();
		if (with != null)
		{
			// create the new Instruction and copy import the given info
			ins = getInstruction (with.getOpCode ());
			ins.copy (with);

			// update all jump points
			updateJumpPoints (orig, ins);
		}

		return ins;
	}


	/**
	 *	Replaces the previous Instruction with the given one.  It is an
	 *	error to call this method if the iterator is beforeFirst().
 	 *	After this method, the iterator will be before the newly added
	 *	Instruction.  This method will also make sure that all jump points
	 *	that referenced the old opcode are updated correctly.
	 *
	 *	@return		the newly added Instruction
	 */
	public Instruction replacePrevious (Instruction with)
	{
		Instruction	orig = (Instruction) _li.previous ();
		Instruction	ins	 = null;

		_li.remove ();
		if (with != null)
		{
			// create the new Instruction and copy import the given info
			ins = getInstruction (with.getOpCode ());
			ins.copy (with);

			// update all jump points
			updateJumpPoints (orig, ins);
		}

		return ins;
	}


	/**
	 *	Replaces all the Instructions in this code block that match the
	 *	given template with the given Instruction.  After this method,
	 *	the iterator will be in its original position.
	 *
	 *	@return		the number of substitutions made
	 */
	public int replaceAll (Instruction template, Instruction with)
	{
		// remember the iterator position
		int pos = nextIndex ();

		beforeFirst ();
		int count;
		for (count = 0; searchForward (template); count++)
			replacePrevious (with);

		before (pos);
		return count;
	}


	/**
	 *	Equivalent to looping over each given template/replacement
	 *	pair and calling replaceAll(Instruction, Instruction) for each.
	 */
	public int replaceAll (Instruction[] templates, Instruction[] with)
	{
		if (templates == null && with == null)
			return 0;

		int count = 0;
		for (int i = 0; i < templates.length; i++)
		{
			if (with == null)
				count += replaceAll (templates[i], null);
			else
				count += replaceAll (templates[i], with[i]);
		}

		return count;
	}


	/**
	 *	Update all jump points that reference the original opcode
	 *	to reference the new one.
	 */
	private void updateJumpPoints (Instruction orig, Instruction ins)
	{
		ListIterator li = _opcodes.listIterator ();

		for (Instruction next; li.hasNext ();)
		{
			next = (Instruction) li.next ();
			if (next instanceof JumpInstruction)
			{
				if (orig.equals (((JumpInstruction) next).getTarget ()))
					((JumpInstruction) next).setTarget (ins);

				if (next.getOpCode () == TABLESWITCH)
					updateJumpPoints (orig, ins, (TableSwitchInstruction) next);
				else if (next.getOpCode () == LOOKUPSWITCH)
					updateJumpPoints (orig, ins, (LookupSwitchInstruction)next);
			}
		}		
	}


	private void updateJumpPoints (Instruction orig, Instruction ins,
		TableSwitchInstruction update)
	{	
		Instruction[] jumps = update.getTargets ();

		for (int i = 0; i < jumps.length; i++)
			if (orig == jumps[i])
				jumps[i] = ins;

		update.setTargets (jumps);
	}


	private void updateJumpPoints (Instruction orig, Instruction ins,
		LookupSwitchInstruction update)
	{	
		Instruction[] jumps = update.getTargets ();

		for (int i = 0; i < jumps.length; i++)
			if (orig == jumps[i])
				jumps[i] = ins;

		update.setCases (update.getMatches (), jumps);
	}


	/**
 	 *	Remove the current instruction.
	 */
	public void remove ()
	{
		_li.remove ();
	}


	public Instruction nop ()
	{
		return addInstruction (NOP);
	}


	/**
	 *	Load some constant onto the stack.  The ConstantInstruction type
	 *	takes any constant and correctly translates it into the proper
	 *	opcode, depending on the constant type and value.  For example, 
	 *	if the constant value is set to 0L, the opcode will be set to
	 *	lconst_0.
	 */
	public ConstantInstruction constant ()
	{
		return (ConstantInstruction) addInstruction 
			(new ConstantInstruction (this));
	}

	
	/**
	 *	Loads a Class constant onto the stack.
	 *	For primitive types, this translates into a 
	 *	getstatic() for the TYPE field of the primitive's wrapper type.
	 *	For non-primitives, things get much more complex.  Suffice it to
	 *	say that the operation involves adding synthetic static fields
	 *	and even methods to the class.  Note that this instruction requires
	 *	up to 3 stack positions to execute.
	 */
	public ClassConstantInstruction classconstant ()
	{
		return new ClassConstantInstruction 
			(((BCMethod) _owner).getOwner (), this, nop ());
	}


	public ConstantInstruction aconst_null ()
	{
		return (ConstantInstruction) addInstruction (new ConstantInstruction 
			(this, ACONST_NULL, null));
	}


	public ConstantInstruction iconst_m1 ()
	{
		return (ConstantInstruction) addInstruction (new ConstantInstruction 
			(this, ICONST_M1, new Integer (-1)));
	}


	public ConstantInstruction iconst_0 ()
	{
		return (ConstantInstruction) addInstruction (new ConstantInstruction 
			(this, ICONST_0, new Integer (0)));
	}


	public ConstantInstruction iconst_1 ()
	{
		return (ConstantInstruction) addInstruction (new ConstantInstruction 
			(this, ICONST_1, new Integer (1)));
	}


	public ConstantInstruction iconst_2 ()
	{
		return (ConstantInstruction) addInstruction (new ConstantInstruction 
			(this, ICONST_2, new Integer (2)));
	}


	public ConstantInstruction iconst_3 ()
	{
		return (ConstantInstruction) addInstruction (new ConstantInstruction 
			(this, ICONST_3, new Integer (3)));
	}


	public ConstantInstruction iconst_4 ()
	{
		return (ConstantInstruction) addInstruction (new ConstantInstruction 
			(this, ICONST_4, new Integer (4)));
	}


	public ConstantInstruction iconst_5 ()
	{
		return (ConstantInstruction) addInstruction (new ConstantInstruction 
			(this, ICONST_5, new Integer (5)));
	}


	public ConstantInstruction lconst_0 ()
	{
		return (ConstantInstruction) addInstruction (new ConstantInstruction 
			(this, LCONST_0, new Long (0)));
	}


	public ConstantInstruction lconst_1 ()
	{
		return (ConstantInstruction) addInstruction (new ConstantInstruction 
			(this, LCONST_1, new Long (1)));
	}


	public ConstantInstruction fconst_0 ()
	{
		return (ConstantInstruction) addInstruction (new ConstantInstruction 
			(this, FCONST_0, new Float (0)));
	}


	public ConstantInstruction fconst_1 ()
	{
		return (ConstantInstruction) addInstruction (new ConstantInstruction 
			(this, FCONST_1, new Float (1)));
	}


	public ConstantInstruction fconst_2 ()
	{
		return (ConstantInstruction) addInstruction (new ConstantInstruction 
			(this, FCONST_2, new Float (2)));
	}


	public ConstantInstruction dconst_0 ()
	{
		return (ConstantInstruction) addInstruction (new ConstantInstruction 
			(this, DCONST_0, new Double (0)));
	}


	public ConstantInstruction dconst_1 ()
	{
		return (ConstantInstruction) addInstruction (new ConstantInstruction 
			(this, DCONST_1, new Double (1)));
	}


	public ConstantInstruction bipush ()
	{
		return (ConstantInstruction) addInstruction (new ConstantInstruction 
			(this, BIPUSH, null));
	}


	public ConstantInstruction sipush ()
	{
		return (ConstantInstruction) addInstruction (new ConstantInstruction 
			(this, SIPUSH, null));
	}


	public ConstantInstruction ldc ()
	{
		return (ConstantInstruction) addInstruction (new ConstantInstruction 
			(this, LDC, null));
	}


	public ConstantInstruction ldc_w ()
	{
		return (ConstantInstruction) addInstruction (new ConstantInstruction 
			(this, LDC_W, null));
	}


	public ConstantInstruction ldc2_w ()
	{
		return (ConstantInstruction) addInstruction (new ConstantInstruction 
			(this, LDC2_W, null));
	}


	/**
	 *	This is a convenience method to load a local variable onto the stack,
	 *	if the type and index to load is not known at compile time.
	 */
	public LoadInstruction load ()
	{
		return (LoadInstruction) addInstruction (new LoadInstruction (this));
	}


	public LoadInstruction iload ()
	{
		return (LoadInstruction) addInstruction (new LoadInstruction
			(this, ILOAD, int.class, -1));
	}


	public LoadInstruction iload_0 ()
	{
		return (LoadInstruction) addInstruction (new LoadInstruction 
			(this, ILOAD_0, int.class, 0));
	}


	public LoadInstruction iload_1 ()
	{
		return (LoadInstruction) addInstruction (new LoadInstruction 
			(this, ILOAD_1, int.class, 1));
	}


	public LoadInstruction iload_2 ()
	{
		return (LoadInstruction) addInstruction (new LoadInstruction 
			(this, ILOAD_2, int.class, 2));
	}


	public LoadInstruction iload_3 ()
	{
		return (LoadInstruction) addInstruction (new LoadInstruction 
			(this, ILOAD_3, int.class, 3));
	}


	public LoadInstruction lload ()
	{
		return (LoadInstruction) addInstruction (new LoadInstruction
			(this, LLOAD, long.class, -1));
	}


	public LoadInstruction lload_0 ()
	{
		return (LoadInstruction) addInstruction (new LoadInstruction
			(this, LLOAD_0, long.class, 0));
	}


	public LoadInstruction lload_1 ()
	{
		return (LoadInstruction) addInstruction (new LoadInstruction
			(this, LLOAD_1, long.class, 1));
	}


	public LoadInstruction lload_2 ()
	{
		return (LoadInstruction) addInstruction (new LoadInstruction
			(this, LLOAD_2, long.class, 2));
	}


	public LoadInstruction lload_3 ()
	{
		return (LoadInstruction) addInstruction (new LoadInstruction
			(this, LLOAD_3, long.class, 3));
	}


	public LoadInstruction fload ()
	{
		return (LoadInstruction) addInstruction (new LoadInstruction
			(this, FLOAD, float.class, -1));
	}


	public LoadInstruction fload_0 ()
	{
		return (LoadInstruction) addInstruction (new LoadInstruction
			(this, FLOAD_0, float.class, 0));
	}


	public LoadInstruction fload_1 ()
	{
		return (LoadInstruction) addInstruction (new LoadInstruction
			(this, FLOAD_1, float.class, 1));
	}


	public LoadInstruction fload_2 ()
	{
		return (LoadInstruction) addInstruction (new LoadInstruction
			(this, FLOAD_2, float.class, 2));
	}


	public LoadInstruction fload_3 ()
	{
		return (LoadInstruction) addInstruction (new LoadInstruction
			(this, FLOAD_3, float.class, 3));
	}


	public LoadInstruction dload ()
	{
		return (LoadInstruction) addInstruction (new LoadInstruction
			(this, DLOAD, double.class, -1));
	}


	public LoadInstruction dload_0 ()
	{
		return (LoadInstruction) addInstruction (new LoadInstruction
			(this, DLOAD_0, double.class, 0));
	}


	public LoadInstruction dload_1 ()
	{
		return (LoadInstruction) addInstruction (new LoadInstruction
			(this, DLOAD_1, double.class, 1));
	}


	public LoadInstruction dload_2 ()
	{
		return (LoadInstruction) addInstruction (new LoadInstruction
			(this, DLOAD_2, double.class, 2));
	}


	public LoadInstruction dload_3 ()
	{
		return (LoadInstruction) addInstruction (new LoadInstruction
			(this, DLOAD_3, double.class, 3));
	}


	public LoadInstruction aload ()
	{
		return (LoadInstruction) addInstruction (new LoadInstruction
			(this, ALOAD, Object.class, -1));
	}


	public LoadInstruction aload_0 ()
	{
		return (LoadInstruction) addInstruction (new LoadInstruction
			(this, ALOAD_0, Object.class, 0));
	}


	public LoadInstruction aload_1 ()
	{
		return (LoadInstruction) addInstruction (new LoadInstruction
			(this, ALOAD_1, Object.class, 1));
	}


	public LoadInstruction aload_2 ()
	{
		return (LoadInstruction) addInstruction (new LoadInstruction
			(this, ALOAD_2, Object.class, 2));
	}


	public LoadInstruction aload_3 ()
	{
		return (LoadInstruction) addInstruction (new LoadInstruction
			(this, ALOAD_3, Object.class, 3));
	}


	/**
	 *	This is a convenience method to store a stack value into a local
	 *	variable if the type and index to store is not known at compile time.
	 */
	public StoreInstruction store ()
	{
		return (StoreInstruction) addInstruction (new StoreInstruction (this));
	}


	public StoreInstruction istore ()
	{
		return (StoreInstruction) addInstruction (new StoreInstruction
			(this, ISTORE, int.class, -1));
	}


	public StoreInstruction istore_0 ()
	{
		return (StoreInstruction) addInstruction (new StoreInstruction 
			(this, ISTORE_0, int.class, 0));
	}


	public StoreInstruction istore_1 ()
	{
		return (StoreInstruction) addInstruction (new StoreInstruction 
			(this, ISTORE_1, int.class, 1));
	}


	public StoreInstruction istore_2 ()
	{
		return (StoreInstruction) addInstruction (new StoreInstruction 
			(this, ISTORE_2, int.class, 2));
	}


	public StoreInstruction istore_3 ()
	{
		return (StoreInstruction) addInstruction (new StoreInstruction 
			(this, ISTORE_3, int.class, 3));
	}


	public StoreInstruction lstore ()
	{
		return (StoreInstruction) addInstruction (new StoreInstruction
			(this, LSTORE, long.class, -1));
	}


	public StoreInstruction lstore_0 ()
	{
		return (StoreInstruction) addInstruction (new StoreInstruction
			(this, LSTORE_0, long.class, 0));
	}


	public StoreInstruction lstore_1 ()
	{
		return (StoreInstruction) addInstruction (new StoreInstruction
			(this, LSTORE_1, long.class, 1));
	}


	public StoreInstruction lstore_2 ()
	{
		return (StoreInstruction) addInstruction (new StoreInstruction
			(this, LSTORE_2, long.class, 2));
	}


	public StoreInstruction lstore_3 ()
	{
		return (StoreInstruction) addInstruction (new StoreInstruction
			(this, LSTORE_3, long.class, 3));
	}


	public StoreInstruction fstore ()
	{
		return (StoreInstruction) addInstruction (new StoreInstruction
			(this, FSTORE, float.class, -1));
	}


	public StoreInstruction fstore_0 ()
	{
		return (StoreInstruction) addInstruction (new StoreInstruction
			(this, FSTORE_0, float.class, 0));
	}


	public StoreInstruction fstore_1 ()
	{
		return (StoreInstruction) addInstruction (new StoreInstruction
			(this, FSTORE_1, float.class, 1));
	}


	public StoreInstruction fstore_2 ()
	{
		return (StoreInstruction) addInstruction (new StoreInstruction
			(this, FSTORE_2, float.class, 2));
	}


	public StoreInstruction fstore_3 ()
	{
		return (StoreInstruction) addInstruction (new StoreInstruction
			(this, FSTORE_3, float.class, 3));
	}


	public StoreInstruction dstore ()
	{
		return (StoreInstruction) addInstruction (new StoreInstruction
			(this, DSTORE, double.class, -1));
	}


	public StoreInstruction dstore_0 ()
	{
		return (StoreInstruction) addInstruction (new StoreInstruction
			(this, DSTORE_0, double.class, 0));
	}


	public StoreInstruction dstore_1 ()
	{
		return (StoreInstruction) addInstruction (new StoreInstruction
			(this, DSTORE_1, double.class, 1));
	}


	public StoreInstruction dstore_2 ()
	{
		return (StoreInstruction) addInstruction (new StoreInstruction
			(this, DSTORE_2, double.class, 2));
	}


	public StoreInstruction dstore_3 ()
	{
		return (StoreInstruction) addInstruction (new StoreInstruction
			(this, DSTORE_3, double.class, 3));
	}


	public StoreInstruction astore ()
	{
		return (StoreInstruction) addInstruction (new StoreInstruction
			(this, ASTORE, Object.class, -1));
	}


	public StoreInstruction astore_0 ()
	{
		return (StoreInstruction) addInstruction (new StoreInstruction
			(this, ASTORE_0, Object.class, 0));
	}


	public StoreInstruction astore_1 ()
	{
		return (StoreInstruction) addInstruction (new StoreInstruction
			(this, ASTORE_1, Object.class, 1));
	}


	public StoreInstruction astore_2 ()
	{
		return (StoreInstruction) addInstruction (new StoreInstruction
			(this, ASTORE_2, Object.class, 2));
	}


	public StoreInstruction astore_3 ()
	{
		return (StoreInstruction) addInstruction (new StoreInstruction
			(this, ASTORE_3, Object.class, 3));
	}


	public RetInstruction ret ()
	{
		return (RetInstruction) addInstruction (new RetInstruction (this));
	}


	public IIncInstruction iinc ()
	{
		return (IIncInstruction) addInstruction (new IIncInstruction (this));
	}


	public WideInstruction wide ()
	{
		return (WideInstruction) addInstruction (new WideInstruction (this));
	}


	/**
	 *	This is a convenience method to invoke the proper array load instruction
	 *	if the type is not known at compile time.
	 */
	public ArrayLoadInstruction arrayload ()
	{
		return (ArrayLoadInstruction) addInstruction 
			(new ArrayLoadInstruction (this));
	}


	public ArrayLoadInstruction iaload ()
	{
		return (ArrayLoadInstruction) addInstruction 
			(new ArrayLoadInstruction (this, IALOAD, int.class));
	}


	public ArrayLoadInstruction laload ()
	{
		return (ArrayLoadInstruction) addInstruction 
			(new ArrayLoadInstruction (this, LALOAD, long.class));
	}


	public ArrayLoadInstruction faload ()
	{
		return (ArrayLoadInstruction) addInstruction 
			(new ArrayLoadInstruction (this, FALOAD, float.class));
	}


	public ArrayLoadInstruction daload ()
	{
		return (ArrayLoadInstruction) addInstruction 
			(new ArrayLoadInstruction (this, DALOAD, double.class));
	}


	public ArrayLoadInstruction aaload ()
	{
		return (ArrayLoadInstruction) addInstruction 
			(new ArrayLoadInstruction (this, AALOAD, Object.class));
	}


	public ArrayLoadInstruction baload ()
	{
		return (ArrayLoadInstruction) addInstruction 
			(new ArrayLoadInstruction (this, BALOAD, byte.class));
	}


	public ArrayLoadInstruction caload ()
	{
		return (ArrayLoadInstruction) addInstruction 
			(new ArrayLoadInstruction (this, CALOAD, char.class));
	}


	public ArrayLoadInstruction saload ()
	{
		return (ArrayLoadInstruction) addInstruction 
			(new ArrayLoadInstruction (this, SALOAD, short.class));
	}


	/**
	 *	This is a convenience method to invoke the proper array store 
	 *	instruction if the type is not known at compile time.
	 */
	public ArrayStoreInstruction arraystore ()
	{
		return (ArrayStoreInstruction) addInstruction 
			(new ArrayStoreInstruction (this));
	}


	public ArrayStoreInstruction iastore ()
	{
		return (ArrayStoreInstruction) addInstruction 
			(new ArrayStoreInstruction (this, IASTORE, int.class));
	}


	public ArrayStoreInstruction lastore ()
	{
		return (ArrayStoreInstruction) addInstruction 
			(new ArrayStoreInstruction (this, LASTORE, long.class));
	}


	public ArrayStoreInstruction fastore ()
	{
		return (ArrayStoreInstruction) addInstruction 
			(new ArrayStoreInstruction (this, FASTORE, float.class));
	}


	public ArrayStoreInstruction dastore ()
	{
		return (ArrayStoreInstruction) addInstruction 
			(new ArrayStoreInstruction (this, DASTORE, double.class));
	}


	public ArrayStoreInstruction aastore ()
	{
		return (ArrayStoreInstruction) addInstruction 
			(new ArrayStoreInstruction (this, AASTORE, Object.class));
	}


	public ArrayStoreInstruction bastore ()
	{
		return (ArrayStoreInstruction) addInstruction 
			(new ArrayStoreInstruction (this, BASTORE, byte.class));
	}


	public ArrayStoreInstruction castore ()
	{
		return (ArrayStoreInstruction) addInstruction 
			(new ArrayStoreInstruction (this, CASTORE, char.class));
	}


	public ArrayStoreInstruction sastore ()
	{
		return (ArrayStoreInstruction) addInstruction 
			(new ArrayStoreInstruction (this, SASTORE, short.class));
	}


	public StackInstruction pop ()
	{
		return (StackInstruction) addInstruction 
			(new StackInstruction (this, POP));
	}


	public StackInstruction pop2 ()
	{
		return (StackInstruction) addInstruction 
			(new StackInstruction (this, POP2));
	}


	public StackInstruction dup ()
	{
		return (StackInstruction) addInstruction 
			(new StackInstruction (this, DUP));
	}


	public StackInstruction dup_x1 ()
	{
		return (StackInstruction) addInstruction 
			(new StackInstruction (this, DUP_X1));
	}


	public StackInstruction dup_x2 ()
	{
		return (StackInstruction) addInstruction 
			(new StackInstruction (this, DUP_X2));
	}


	public StackInstruction dup2 ()
	{
		return (StackInstruction) addInstruction 
			(new StackInstruction (this, DUP2));
	}


	public StackInstruction dup2_x1 ()
	{
		return (StackInstruction) addInstruction 
			(new StackInstruction (this, DUP2_X1));
	}


	public StackInstruction dup2_x2 ()
	{
		return (StackInstruction) addInstruction 
			(new StackInstruction (this, DUP2_X2));
	}


	public StackInstruction swap ()
	{
		return (StackInstruction) addInstruction 
			(new StackInstruction (this, SWAP));
	}


	/**
	 *	This is a convenience method to invoke the proper math
	 *	instruction if neither the type nor operation are known
	 *	at compile time.
	 */
	public MathInstruction math ()
	{
		return (MathInstruction) addInstruction (new MathInstruction (this));
	}


	/**
	 *	This is a convenience method to invoke the proper math
	 *	instruction if the type is not known at compile time.
	 */
	public MathInstruction add ()
	{
		return (MathInstruction) addInstruction 
			(new MathInstruction (this, IADD, MATH_ADD, null));
	}


	public MathInstruction iadd ()
	{
		return (MathInstruction) addInstruction
			(new MathInstruction (this, IADD, MATH_ADD, int.class));
	}


	public MathInstruction ladd ()
	{
		return (MathInstruction) addInstruction
			(new MathInstruction (this, LADD, MATH_ADD, long.class));
	}


	public MathInstruction fadd ()
	{
		return (MathInstruction) addInstruction
			(new MathInstruction (this, FADD, MATH_ADD, float.class));
	}


	public MathInstruction dadd ()
	{
		return (MathInstruction) addInstruction
			(new MathInstruction (this, DADD, MATH_ADD, double.class));
	}


	/**
	 *	This is a convenience method to invoke the proper math
	 *	instruction if the type is not known at compile time.
	 */
	public MathInstruction sub ()
	{
		return (MathInstruction) addInstruction 
			(new MathInstruction (this, ISUB, MATH_SUB, null));
	}


	public MathInstruction isub ()
	{
		return (MathInstruction) addInstruction
			(new MathInstruction (this, ISUB, MATH_SUB, int.class));
	}


	public MathInstruction lsub ()
	{
		return (MathInstruction) addInstruction
			(new MathInstruction (this, LSUB, MATH_SUB, long.class));
	}


	public MathInstruction fsub ()
	{
		return (MathInstruction) addInstruction
			(new MathInstruction (this, FSUB, MATH_SUB, float.class));
	}


	public MathInstruction dsub ()
	{
		return (MathInstruction) addInstruction
			(new MathInstruction (this, DSUB, MATH_SUB, double.class));
	}


	/**
	 *	This is a convenience method to invoke the proper math
	 *	instruction if the type is not known at compile time.
	 */
	public MathInstruction mul ()
	{
		return (MathInstruction) addInstruction 
			(new MathInstruction (this, IMUL, MATH_MUL, null));
	}


	public MathInstruction imul ()
	{
		return (MathInstruction) addInstruction
			(new MathInstruction (this, IMUL, MATH_MUL, int.class));
	}


	public MathInstruction lmul ()
	{
		return (MathInstruction) addInstruction
			(new MathInstruction (this, LMUL, MATH_MUL, long.class));
	}


	public MathInstruction fmul ()
	{
		return (MathInstruction) addInstruction
			(new MathInstruction (this, FMUL, MATH_MUL, float.class));
	}


	public MathInstruction dmul ()
	{
		return (MathInstruction) addInstruction
			(new MathInstruction (this, DMUL, MATH_MUL, double.class));
	}


	/**
	 *	This is a convenience method to invoke the proper math
	 *	instruction if the type is not known at compile time.
	 */
	public MathInstruction div ()
	{
		return (MathInstruction) addInstruction 
			(new MathInstruction (this, IDIV, MATH_DIV, null));
	}


	public MathInstruction idiv ()
	{
		return (MathInstruction) addInstruction
			(new MathInstruction (this, IDIV, MATH_DIV, int.class));
	}


	public MathInstruction ldiv ()
	{
		return (MathInstruction) addInstruction
			(new MathInstruction (this, LDIV, MATH_DIV, long.class));
	}


	public MathInstruction fdiv ()
	{
		return (MathInstruction) addInstruction
			(new MathInstruction (this, FDIV, MATH_DIV, float.class));
	}


	public MathInstruction ddiv ()
	{
		return (MathInstruction) addInstruction
			(new MathInstruction (this, DDIV, MATH_DIV, double.class));
	}


	/**
	 *	This is a convenience method to invoke the proper math
	 *	instruction if the type is not known at compile time.
	 */
	public MathInstruction rem ()
	{
		return (MathInstruction) addInstruction 
			(new MathInstruction (this, IREM, MATH_REM, null));
	}


	public MathInstruction irem ()
	{
		return (MathInstruction) addInstruction
			(new MathInstruction (this, IREM, MATH_REM, int.class));
	}


	public MathInstruction lrem ()
	{
		return (MathInstruction) addInstruction
			(new MathInstruction (this, LREM, MATH_REM, long.class));
	}


	public MathInstruction frem ()
	{
		return (MathInstruction) addInstruction
			(new MathInstruction (this, FREM, MATH_REM, float.class));
	}


	public MathInstruction drem ()
	{
		return (MathInstruction) addInstruction
			(new MathInstruction (this, DREM, MATH_REM, double.class));
	}


	/**
	 *	This is a convenience method to invoke the proper math
	 *	instruction if the type is not known at compile time.
	 */
	public MathInstruction neg ()
	{
		return (MathInstruction) addInstruction 
			(new MathInstruction (this, INEG, MATH_NEG, null));
	}


	public MathInstruction ineg ()
	{
		return (MathInstruction) addInstruction
			(new MathInstruction (this, INEG, MATH_NEG, int.class));
	}


	public MathInstruction lneg ()
	{
		return (MathInstruction) addInstruction
			(new MathInstruction (this, LNEG, MATH_NEG, long.class));
	}


	public MathInstruction fneg ()
	{
		return (MathInstruction) addInstruction
			(new MathInstruction (this, FNEG, MATH_NEG, float.class));
	}


	public MathInstruction dneg ()
	{
		return (MathInstruction) addInstruction
			(new MathInstruction (this, DNEG, MATH_NEG, double.class));
	}


	/**
	 *	This is a convenience method to invoke the proper math
	 *	instruction if the type is not known at compile time.
	 */
	public MathInstruction shl ()
	{
		return (MathInstruction) addInstruction 
			(new MathInstruction (this, ISHL, MATH_SHL, null));
	}


	public MathInstruction ishl ()
	{
		return (MathInstruction) addInstruction
			(new MathInstruction (this, ISHL, MATH_SHL, int.class));
	}


	public MathInstruction lshl ()
	{
		return (MathInstruction) addInstruction
			(new MathInstruction (this, LSHL, MATH_SHL, long.class));
	}


	/**
	 *	This is a convenience method to invoke the proper math
	 *	instruction if the type is not known at compile time.
	 */
	public MathInstruction shr ()
	{
		return (MathInstruction) addInstruction 
			(new MathInstruction (this, ISHR, MATH_SHR, null));
	}


	public MathInstruction ishr ()
	{
		return (MathInstruction) addInstruction
			(new MathInstruction (this, ISHR, MATH_SHR, int.class));
	}


	public MathInstruction lshr ()
	{
		return (MathInstruction) addInstruction
			(new MathInstruction (this, LSHR, MATH_SHR, long.class));
	}


	/**
	 *	This is a convenience method to invoke the proper math
	 *	instruction if the type is not known at compile time.
	 */
	public MathInstruction ushr ()
	{
		return (MathInstruction) addInstruction 
			(new MathInstruction (this, IUSHR, MATH_USHR, null));
	}


	public MathInstruction iushr ()
	{
		return (MathInstruction) addInstruction
			(new MathInstruction (this, IUSHR, MATH_USHR, int.class));
	}


	public MathInstruction lushr ()
	{
		return (MathInstruction) addInstruction
			(new MathInstruction (this, LUSHR, MATH_USHR, long.class));
	}


	/**
	 *	This is a convenience method to invoke the proper math
	 *	instruction if the type is not known at compile time.
	 */
	public MathInstruction and ()
	{
		return (MathInstruction) addInstruction 
			(new MathInstruction (this, IAND, MATH_AND, null));
	}


	public MathInstruction iand ()
	{
		return (MathInstruction) addInstruction
			(new MathInstruction (this, IAND, MATH_AND, int.class));
	}


	public MathInstruction land ()
	{
		return (MathInstruction) addInstruction
			(new MathInstruction (this, LAND, MATH_AND, long.class));
	}


	/**
	 *	This is a convenience method to invoke the proper math
	 *	instruction if the type is not known at compile time.
	 */
	public MathInstruction or ()
	{
		return (MathInstruction) addInstruction 
			(new MathInstruction (this, IOR, MATH_OR, null));
	}


	public MathInstruction ior ()
	{
		return (MathInstruction) addInstruction
			(new MathInstruction (this, IOR, MATH_OR, int.class));
	}


	public MathInstruction lor ()
	{
		return (MathInstruction) addInstruction
			(new MathInstruction (this, LOR, MATH_OR, long.class));
	}


	/**
	 *	This is a convenience method to invoke the proper math
	 *	instruction if the type is not known at compile time.
	 */
	public MathInstruction xor ()
	{
		return (MathInstruction) addInstruction 
			(new MathInstruction (this, IXOR, MATH_XOR, null));
	}


	public MathInstruction ixor ()
	{
		return (MathInstruction) addInstruction
			(new MathInstruction (this, IXOR, MATH_XOR, int.class));
	}


	public MathInstruction lxor ()
	{
		return (MathInstruction) addInstruction
			(new MathInstruction (this, LXOR, MATH_XOR, long.class));
	}


	/**
	 *	This is a convenience method to invoke the proper conversion
	 *	instruction if the types being converted are not known at compile time.
	 */
	public ConvertInstruction convert ()
	{
		return (ConvertInstruction) addInstruction 
			(new ConvertInstruction (this));
	}


	public ConvertInstruction i2l ()
	{
		return (ConvertInstruction) addInstruction 
			(new ConvertInstruction (this, I2L, int.class, long.class));
	}


	public ConvertInstruction i2f ()
	{
		return (ConvertInstruction) addInstruction 
			(new ConvertInstruction (this, I2F, int.class, float.class));
	}


	public ConvertInstruction i2d ()
	{
		return (ConvertInstruction) addInstruction 
			(new ConvertInstruction (this, I2D, int.class, double.class));
	}


	public ConvertInstruction i2b ()
	{
		return (ConvertInstruction) addInstruction 
			(new ConvertInstruction (this, I2B, int.class, byte.class));
	}


	public ConvertInstruction i2c ()
	{
		return (ConvertInstruction) addInstruction 
			(new ConvertInstruction (this, I2C, int.class, char.class));
	}


	public ConvertInstruction i2s ()
	{
		return (ConvertInstruction) addInstruction 
			(new ConvertInstruction (this, I2S, int.class, short.class));
	}


	public ConvertInstruction l2i ()
	{
		return (ConvertInstruction) addInstruction 
			(new ConvertInstruction (this, L2I, long.class, int.class));
	}


	public ConvertInstruction l2f ()
	{
		return (ConvertInstruction) addInstruction 
			(new ConvertInstruction (this, L2F, long.class, float.class));
	}


	public ConvertInstruction l2d ()
	{
		return (ConvertInstruction) addInstruction 
			(new ConvertInstruction (this, L2D, long.class, double.class));
	}


	public ConvertInstruction f2i ()
	{
		return (ConvertInstruction) addInstruction 
			(new ConvertInstruction (this, F2I, float.class, int.class));
	}


	public ConvertInstruction f2l ()
	{
		return (ConvertInstruction) addInstruction 
			(new ConvertInstruction (this, F2L, float.class, long.class));
	}


	public ConvertInstruction f2d ()
	{
		return (ConvertInstruction) addInstruction 
			(new ConvertInstruction (this, F2D, float.class, double.class));
	}


	public ConvertInstruction d2i ()
	{
		return (ConvertInstruction) addInstruction 
			(new ConvertInstruction (this, D2I, double.class, int.class));
	}


	public ConvertInstruction d2l ()
	{
		return (ConvertInstruction) addInstruction 
			(new ConvertInstruction (this, D2L, double.class, long.class));
	}


	public ConvertInstruction d2f ()
	{
		return (ConvertInstruction) addInstruction 
			(new ConvertInstruction (this, D2F, double.class, float.class));
	}


	/**
	 *	Convenience method to use when the types being compared are not known
	 *	at compile time.
	 */
	public CmpInstruction cmp ()
	{
		return (CmpInstruction) addInstruction (new CmpInstruction (this));
	}


	public CmpInstruction lcmp ()
	{
		return (CmpInstruction) addInstruction
			(new CmpInstruction (this, LCMP, long.class, -1));
	}


	public CmpInstruction fcmpl ()
	{
		return (CmpInstruction) addInstruction
			(new CmpInstruction (this, FCMPL, float.class, -1));
	}


	public CmpInstruction fcmpg ()
	{
		return (CmpInstruction) addInstruction
			(new CmpInstruction (this, FCMPG, float.class, 1));
	}


	public CmpInstruction dcmpl ()
	{
		return (CmpInstruction) addInstruction
			(new CmpInstruction (this, DCMPL, double.class, -1));
	}


	public CmpInstruction dcmpg ()
	{
		return (CmpInstruction) addInstruction
			(new CmpInstruction (this, DCMPG, double.class, 1));
	}


	public JumpInstruction if_eq ()
	{
		return (JumpInstruction) addInstruction (new JumpInstruction 
			(this, IF_EQ));
	}


	public JumpInstruction if_ne ()
	{
		return (JumpInstruction) addInstruction (new JumpInstruction 
			(this, IF_NE));
	}


	public JumpInstruction if_lt ()
	{
		return (JumpInstruction) addInstruction (new JumpInstruction 
			(this, IF_LT));
	}


	public JumpInstruction if_ge ()
	{
		return (JumpInstruction) addInstruction (new JumpInstruction 
			(this, IF_GE));
	}


	public JumpInstruction if_gt ()
	{
		return (JumpInstruction) addInstruction (new JumpInstruction 
			(this, IF_GT));
	}


	public JumpInstruction if_le ()
	{
		return (JumpInstruction) addInstruction (new JumpInstruction 
			(this, IF_LE));
	}


	public JumpInstruction if_icmpeq ()
	{
		return (JumpInstruction) addInstruction (new JumpInstruction 
			(this, IF_ICMPEQ));
	}


	public JumpInstruction if_icmpne ()
	{
		return (JumpInstruction) addInstruction (new JumpInstruction 
			(this, IF_ICMPNE));
	}


	public JumpInstruction if_icmplt ()
	{
		return (JumpInstruction) addInstruction (new JumpInstruction 
			(this, IF_ICMPLT));
	}


	public JumpInstruction if_icmpge ()
	{
		return (JumpInstruction) addInstruction (new JumpInstruction 
			(this, IF_ICMPGE));
	}


	public JumpInstruction if_icmpgt ()
	{
		return (JumpInstruction) addInstruction (new JumpInstruction 
			(this, IF_ICMPGT));
	}


	public JumpInstruction if_icmple ()
	{
		return (JumpInstruction) addInstruction (new JumpInstruction 
			(this, IF_ICMPLE));
	}


	public JumpInstruction if_acmpeq ()
	{
		return (JumpInstruction) addInstruction (new JumpInstruction 
			(this, IF_ACMPEQ));
	}


	public JumpInstruction if_acmpne ()
	{
		return (JumpInstruction) addInstruction (new JumpInstruction 
			(this, IF_ACMPNE));
	}


	public JumpInstruction if_null ()
	{
		return (JumpInstruction) addInstruction (new JumpInstruction 
			(this, IF_NULL));
	}


	public JumpInstruction if_nonnull ()
	{
		return (JumpInstruction) addInstruction (new JumpInstruction 
			(this, IF_NONNULL));
	}


	public JumpInstruction go2 ()
	{
		return (JumpInstruction) addInstruction (new JumpInstruction 
			(this, GOTO));
	}


	public JumpInstruction jsr ()
	{
		return (JumpInstruction) addInstruction (new JumpInstruction 
			(this, JSR));
	}


	public JumpInstruction go2_w ()
	{
		return (JumpInstruction) addInstruction (new JumpInstruction 
			(this, GOTO_W));
	}


	public JumpInstruction jsr_w ()
	{
		return (JumpInstruction) addInstruction (new JumpInstruction 
			(this, JSR_W));
	}


	public TableSwitchInstruction tableswitch ()
	{
		return (TableSwitchInstruction) addInstruction 
			(new TableSwitchInstruction (this));
	}


	public LookupSwitchInstruction lookupswitch ()
	{
		return (LookupSwitchInstruction) addInstruction 
			(new LookupSwitchInstruction (this));
	}


	/**
	 *	This is a convenience method to invoke the proper return
	 *	instruction if the type is not known at compile time.
	 */
	public ReturnInstruction returnins ()
	{
		return (ReturnInstruction) addInstruction 
			(new ReturnInstruction (this));
	}


	public ReturnInstruction vreturn ()
	{
		return (ReturnInstruction) addInstruction 
			(new ReturnInstruction (this, RETURN, void.class));
	}


	public ReturnInstruction ireturn ()
	{
		return (ReturnInstruction) addInstruction 
			(new ReturnInstruction (this, IRETURN, int.class));
	}


	public ReturnInstruction lreturn ()
	{
		return (ReturnInstruction) addInstruction 
			(new ReturnInstruction (this, LRETURN, long.class));
	}


	public ReturnInstruction freturn ()
	{
		return (ReturnInstruction) addInstruction 
			(new ReturnInstruction (this, FRETURN, float.class));
	}


	public ReturnInstruction dreturn ()
	{
		return (ReturnInstruction) addInstruction 
			(new ReturnInstruction (this, DRETURN, double.class));
	}


	public ReturnInstruction areturn ()
	{
		return (ReturnInstruction) addInstruction 
			(new ReturnInstruction (this, ARETURN, Object.class));
	}


	public GetFieldInstruction getfield ()
	{
		return (GetFieldInstruction) addInstruction 
			(new GetFieldInstruction (this, GETFIELD));
	}


	public GetFieldInstruction getstatic ()
	{
		return (GetFieldInstruction) addInstruction 
			(new GetFieldInstruction (this, GETSTATIC));
	}


	public PutFieldInstruction putfield ()
	{
		return (PutFieldInstruction) addInstruction 
			(new PutFieldInstruction (this, PUTFIELD));
	}


	public PutFieldInstruction putstatic ()
	{
		return (PutFieldInstruction) addInstruction 
			(new PutFieldInstruction (this, PUTSTATIC));
	}


	public MethodInstruction invokevirtual ()
	{
		return (MethodInstruction) addInstruction 
			(new MethodInstruction (this, INVOKEVIRTUAL));
	}


	public MethodInstruction invokespecial ()
	{
		return (MethodInstruction) addInstruction 
			(new MethodInstruction (this, INVOKESPECIAL));
	}


	public MethodInstruction invokeinterface ()
	{
		return (MethodInstruction) addInstruction 
			(new MethodInstruction (this, INVOKEINTERFACE));
	}


	public MethodInstruction invokestatic ()
	{
		return (MethodInstruction) addInstruction 
			(new MethodInstruction (this, INVOKESTATIC));
	}


	public ClassInstruction newins ()
	{
		return (ClassInstruction) addInstruction 
			(new ClassInstruction (this, NEW));
	}


	public ClassInstruction anewarray ()
	{
		return (ClassInstruction) addInstruction 
			(new ClassInstruction (this, ANEWARRAY));
	}

	
	public ClassInstruction checkcast ()
	{
		return (ClassInstruction) addInstruction 
			(new ClassInstruction (this, CHECKCAST));
	}

	
	public ClassInstruction instanceofins ()
	{
		return (ClassInstruction) addInstruction 
			(new ClassInstruction (this, INSTANCEOF));
	}


	public MultiANewArrayInstruction multianewarray ()
	{
		return (MultiANewArrayInstruction) addInstruction
			(new MultiANewArrayInstruction (this));
	}


	public NewArrayInstruction newarray ()
	{
		return (NewArrayInstruction) addInstruction 
			(new NewArrayInstruction (this));
	}


	public Instruction arraylength ()
	{
		return addInstruction (ARRAYLENGTH);
	}


	public Instruction athrow ()
	{
		return addInstruction (ATHROW);
	}


	public MonitorEnterInstruction monitorenter ()
	{
		return (MonitorEnterInstruction) addInstruction 
			(new MonitorEnterInstruction (this));
	}


	public MonitorExitInstruction monitorexit ()
	{
		return (MonitorExitInstruction) addInstruction 
			(new MonitorExitInstruction (this));
	}


	/**
	 *	Get all the Instructions of this method.
	 */
	public Instruction[] getInstructions ()
	{
		return (Instruction[]) _opcodes.toArray 
			(new Instruction[_opcodes.size ()]);
	}


	/**
	 *	Get the code for this method as a byte array.
	 */
	public byte[] getCode ()
		throws IOException
	{
		ByteArrayOutputStream byteStream = new ByteArrayOutputStream ();
		DataOutputStream stream = new DataOutputStream (byteStream);
		try
		{
			writeCode (stream);
			return byteStream.toByteArray ();
		}
		finally
		{
			try { stream.close (); } catch (Exception e) {}
		}
	}


	/**
	 *	Set the code for this method as a byte array.
	 */
	public void setCode (byte[] code)
		throws IOException
	{
		if (code == null)
			_opcodes.clear ();
		else
		{
			DataInputStream stream = new DataInputStream
				(new ByteArrayInputStream (code));
			try
			{
				readCode (stream, code.length);
			}
			finally
			{
				try { stream.close (); } catch (Exception e) {}
			}
		}
	}


	public int getLength ()
	{
		// covers maxStack, maxLocals, codeLength, exceptionTableLength,
		// attributeCount
		int length = 12;

		// add code
		try
		{
			length += getCode ().length;
		}
		catch (Exception e)
		{
			throw new RuntimeException (e.getMessage ());
		}

		// add exception reps; each is 8 bytes
		length += 8 * _handlers.size ();

		// add all attribute lengths
		Attribute[] attrs = getAttributes ();
		for (int i = 0; i < attrs.length; i++)	
			length += attrs[i].getLength () + 6;	

		return length;
	}


	/**
	 *	Copies the instructions of the given code block to this one; used to
	 *	import methods from other classes or copy methods within a class.
	 */
	protected void copy (Attribute attr)
	{
		Code orig = (Code) attr;

		setMaxStack (orig.getMaxStack ());
		setMaxLocals (orig.getMaxLocals ());

		// clear existing code
		_opcodes.clear ();
		_handlers.clear ();
		_li = _opcodes.listIterator ();

		// copy all instructions
		Instruction origIns;
		Instruction ins;
		for (Iterator i = orig._opcodes.iterator (); i.hasNext ();)
		{
			origIns = (Instruction) i.next ();
			ins = getInstruction (origIns.getOpCode ());
			ins.copy (origIns);
		}

		// copy exception handlers
		ExceptionHandler[] origHandlers = orig.getExceptionHandlers ();
		ExceptionHandler handler;
		for (int i = 0; i < origHandlers.length; i++)
		{
			handler = addExceptionHandler ();
			handler.copy (origHandlers[i]);
			handler.setMarkers (_opcodes);
		}

		// reset all opcode ptrs to the new copied opcodes
		for (Iterator i = _opcodes.iterator (); i.hasNext ();)
		{
			ins = (Instruction) i.next ();
			if (ins instanceof InstructionPtr)
				((InstructionPtr) ins).setMarkers (_opcodes);
		}
		_li = _opcodes.listIterator ();

		importAttributes (orig);
	}


	protected void readData (DataInput in, int length)
		throws IOException
	{
		setMaxStack (in.readUnsignedShort ());
		setMaxLocals (in.readUnsignedShort ());

		readCode (in, in.readInt ());
		
		_handlers.clear ();
		int exceptionCount = in.readUnsignedShort ();
		ExceptionHandler excep;
		for (int i = 0; i < exceptionCount; i++)
		{
			excep = addExceptionHandler ();
			excep.readData (in);	
			excep.setMarkers (_opcodes);
		}

		readAttributes (in);
	}


	protected void writeData (DataOutput out, int length)
		throws IOException
	{
		out.writeShort (getMaxStack ());
		out.writeShort (getMaxLocals ());

		byte[] code = getCode ();
		out.writeInt (code.length);
		out.write (code);

		out.writeShort (_handlers.size ());
		for (Iterator i = _handlers.iterator (); i.hasNext ();)
			((ExceptionHandler) i.next ()).writeData (out);

		writeAttributes (out);
	}


	private void readCode (DataInput in, int len)
		throws IOException
	{
		_opcodes.clear ();
		_li = _opcodes.listIterator ();

		Instruction ins;
		for (int byteIndex = 0; byteIndex < len;)
		{
			ins = getInstruction (in.readUnsignedByte ());
			ins.setByteIndex (byteIndex);
			ins.readData (in);

 			byteIndex += ins.getLength ();
		}

		// make sure all the opcode ptrs are set up
		for (Iterator i = _opcodes.iterator (); i.hasNext ();)
		{
			ins = (Instruction) i.next ();
			if (ins instanceof InstructionPtr)
				((InstructionPtr) ins).setMarkers (_opcodes);
		}		

		beforeFirst ();
	}


	private void writeCode (DataOutput out)
		throws IOException
	{
		int 			byteIndex = 0;
		Instruction		ins;

		// loop once to set all byte indexes
		for (Iterator i = _opcodes.iterator (); i.hasNext ();)
		{
			ins = (Instruction) i.next ();
			ins.setByteIndex (byteIndex);
			byteIndex += ins.getLength ();
		}

		// and loop again to write the data
		for (Iterator i = _opcodes.iterator (); i.hasNext ();)
		{
			ins = (Instruction) i.next ();
			out.writeByte (ins.getOpCode ());
			ins.writeData (out);
		}
	}


	private Instruction addInstruction (Instruction ins)
	{
		_li.add (ins);
		return ins;
	}


	private Instruction addInstruction (int opcode)
	{
		Instruction ins = new Instruction (this);
		ins.setOpCode (opcode);
		_li.add (ins);
		return ins;
	}


	private Instruction getInstruction (int opcode)
	{
		switch (opcode)
		{
		case NOP:
			return nop ();
		case ACONST_NULL:
			return aconst_null ();
		case ICONST_M1:
			return iconst_m1 ();
		case ICONST_0:
			return iconst_0 ();
		case ICONST_1:
			return iconst_1 ();
		case ICONST_2:
			return iconst_2 ();
		case ICONST_3:
			return iconst_3 ();
		case ICONST_4:
			return iconst_4 ();
		case ICONST_5:
			return iconst_5 ();
		case LCONST_0:
			return lconst_0 ();
		case LCONST_1:
			return lconst_1 ();
		case FCONST_0:
			return fconst_0 ();
		case FCONST_1:
			return fconst_1 ();
		case FCONST_2:
			return fconst_2 ();
		case DCONST_0:
			return dconst_0 ();
		case DCONST_1:
			return dconst_1 ();
		case BIPUSH:
			return bipush ();
		case SIPUSH:
			return sipush ();
		case LDC:
			return ldc ();
		case LDC_W:
			return ldc_w ();
		case LDC2_W:
			return ldc2_w ();
		case ILOAD:
			return iload ();
		case LLOAD:
			return lload ();
		case FLOAD:
			return fload ();
		case DLOAD:
			return dload ();
		case ALOAD:
			return aload ();
		case ILOAD_0:
			return iload_0 ();
		case ILOAD_1:
			return iload_1 ();
		case ILOAD_2:
			return iload_2 ();
		case ILOAD_3:
			return iload_3 ();
		case LLOAD_0:
			return lload_0 ();
		case LLOAD_1:
			return lload_1 ();
		case LLOAD_2:
			return lload_2 ();
		case LLOAD_3:
			return lload_3 ();
		case FLOAD_0:
			return fload_0 ();
		case FLOAD_1:
			return fload_1 ();
		case FLOAD_2:
			return fload_2 ();
		case FLOAD_3:
			return fload_3 ();
		case DLOAD_0:
			return dload_0 ();
		case DLOAD_1:
			return dload_1 ();
		case DLOAD_2:
			return dload_2 ();
		case DLOAD_3:
			return dload_3 ();
		case ALOAD_0:
			return aload_0 ();
		case ALOAD_1:
			return aload_1 ();
		case ALOAD_2:
			return aload_2 ();
		case ALOAD_3:
			return aload_3 ();
		case IALOAD:
			return iaload ();
		case LALOAD:
			return laload ();
		case FALOAD:
			return faload ();
		case DALOAD:
			return daload ();
		case AALOAD:
			return aaload ();
		case BALOAD:
			return baload ();
		case CALOAD:
			return caload ();
		case SALOAD:
			return saload ();
		case ISTORE:
			return istore ();
		case LSTORE:
			return lstore ();
		case FSTORE:
			return fstore ();
		case DSTORE:
			return dstore ();
		case ASTORE:
			return astore ();
		case ISTORE_0:
			return istore_0 ();
		case ISTORE_1:
			return istore_1 ();
		case ISTORE_2:
			return istore_2 ();
		case ISTORE_3:
			return istore_3 ();
		case LSTORE_0:
			return lstore_0 ();
		case LSTORE_1:
			return lstore_1 ();
		case LSTORE_2:
			return lstore_2 ();
		case LSTORE_3:
			return lstore_3 ();
		case FSTORE_0:
			return fstore_0 ();
		case FSTORE_1:
			return fstore_1 ();
		case FSTORE_2:
			return fstore_2 ();
		case FSTORE_3:
			return fstore_3 ();
		case DSTORE_0:
			return dstore_0 ();
		case DSTORE_1:
			return dstore_1 ();
		case DSTORE_2:
			return dstore_2 ();
		case DSTORE_3:
			return dstore_3 ();
		case ASTORE_0:
			return astore_0 ();
		case ASTORE_1:
			return astore_1 ();
		case ASTORE_2:
			return astore_2 ();
		case ASTORE_3:
			return astore_3 ();
		case IASTORE:
			return iastore ();
		case LASTORE:
			return lastore ();
		case FASTORE:
			return fastore ();
		case DASTORE:
			return dastore ();
		case AASTORE:
			return aastore ();
		case BASTORE:
			return bastore ();
		case CASTORE:
			return castore ();
		case SASTORE:
			return sastore ();
		case POP:
			return pop ();
		case POP2:
			return pop2 ();
		case DUP:
			return dup ();
		case DUP_X1:
			return dup_x1 ();
		case DUP_X2:
			return dup_x2 ();
		case DUP2:
			return dup2 ();
		case DUP2_X1:
			return dup2_x1 ();
		case DUP2_X2:
			return dup2_x2 ();
		case SWAP:
			return swap ();
		case IADD:
			return iadd ();
		case LADD:
			return ladd ();
		case FADD:
			return fadd ();
		case DADD:
			return dadd ();
		case ISUB:
			return isub ();
		case LSUB:
			return lsub ();
		case FSUB:
			return fsub ();
		case DSUB:
			return dsub ();
		case IMUL:
			return imul ();
		case LMUL:
			return lmul ();
		case FMUL:
			return fmul ();
		case DMUL:
			return dmul ();
		case IDIV:
			return idiv ();
		case LDIV:
			return ldiv ();
		case FDIV:
			return fdiv ();
		case DDIV:
			return ddiv ();
		case IREM:
			return irem ();
		case LREM:
			return lrem ();
		case FREM:
			return frem ();
		case DREM:
			return drem ();
		case INEG:
			return ineg ();
		case LNEG:
			return lneg ();
		case FNEG:
			return fneg ();
		case DNEG:
			return dneg ();
		case ISHL:
			return ishl ();
		case LSHL:
			return lshl ();
		case ISHR:
			return ishr ();
		case LSHR:
			return lshr ();
		case IUSHR:
			return iushr ();
		case LUSHR:
			return lushr ();
		case IAND:
			return iand ();
		case LAND:
			return land ();
		case IOR:
			return ior ();
		case LOR:
			return lor ();
		case IXOR:
			return ixor ();
		case LXOR:
			return lxor ();
		case IINC:
			return iinc ();
		case I2L:
			return i2l ();
		case I2F:
			return i2f ();
		case I2D:
			return i2d ();
		case L2I:
			return l2i ();
		case L2F:
			return l2f ();
		case L2D:
			return l2d ();
		case F2I:
			return f2i ();
		case F2L:
			return f2l ();
		case F2D:
			return f2d ();
		case D2I:
			return d2i ();
		case D2L:
			return d2l ();
		case D2F:
			return d2f ();
		case I2B:
			return i2b ();
		case I2C:
			return i2c ();
		case I2S:
			return i2s ();
		case LCMP:
			return lcmp ();
		case FCMPL:
			return fcmpl ();
		case FCMPG:
			return fcmpg ();
		case DCMPL:
			return dcmpl ();
		case DCMPG:
			return dcmpg ();
		case IF_EQ:
			return if_eq ();
		case IF_NE:
			return if_ne ();
		case IF_LT:
			return if_lt ();
		case IF_GE:
			return if_ge ();
		case IF_GT:
			return if_gt ();
		case IF_LE:
			return if_le ();
		case IF_ICMPEQ:
			return if_icmpeq ();
		case IF_ICMPNE:
			return if_icmpne ();
		case IF_ICMPLT:
			return if_icmplt ();
		case IF_ICMPGE:
			return if_icmpge ();
		case IF_ICMPGT:
			return if_icmpgt ();
		case IF_ICMPLE:
			return if_icmple ();
		case IF_ACMPEQ:
			return if_acmpeq ();
		case IF_ACMPNE:
			return if_acmpne ();
		case GOTO:
			return go2 ();
		case JSR:
			return jsr ();
		case RET:
			return ret ();
		case TABLESWITCH:
			return tableswitch ();
		case LOOKUPSWITCH:
			return lookupswitch ();
		case IRETURN:
			return ireturn ();
		case LRETURN:
			return lreturn ();
		case FRETURN:
			return freturn ();
		case DRETURN:
			return dreturn ();
		case ARETURN:
			return areturn ();
		case RETURN:
			return vreturn ();
		case GETSTATIC:
			return getstatic ();
		case PUTSTATIC:
			return putstatic ();
		case GETFIELD:
			return getfield ();
		case PUTFIELD:
			return putfield ();
		case INVOKEVIRTUAL:
			return invokevirtual ();
		case INVOKESPECIAL:
			return invokespecial ();
		case INVOKESTATIC:
			return invokestatic ();
		case INVOKEINTERFACE:
			return invokeinterface ();
		case NEW:
			return newins ();
		case NEWARRAY:
			return newarray ();
		case ANEWARRAY:
			return anewarray ();
		case ARRAYLENGTH:
			return arraylength ();
		case ATHROW:
			return athrow ();
		case CHECKCAST:
			return checkcast ();
		case INSTANCEOF:
			return instanceofins ();
		case MONITORENTER:
			return monitorenter ();
		case MONITOREXIT:
			return monitorexit ();
		case WIDE:
			return wide ();
		case MULTIANEWARRAY:
			return multianewarray ();
		case IF_NULL:
			return if_null ();
		case IF_NONNULL:
			return if_nonnull ();
		case GOTO_W:
			return go2_w ();
		case JSR_W:
			return jsr_w ();
		default:
			return null;
		}
	}


	public void acceptVisit (BCVisitor visit)
	{
		visit.enterCode (this);

		Instruction next;
		for (Iterator i = _opcodes.iterator (); i.hasNext ();)
		{
			next = (Instruction) i.next ();

			visit.enterInstruction (next);
			next.acceptVisit (visit);
			visit.exitInstruction (next);
		}	
		for (Iterator i = _handlers.iterator (); i.hasNext ();)
			((ExceptionHandler) i.next ()).acceptVisit (visit);
		visitAttributes (visit);

		visit.exitCode (this);
	}
}
