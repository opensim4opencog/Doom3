package com.techtrader.modules.tools.bytecode;


/**
 *	Pseudo-instruction used to place Class types onto the stack.  This
 *	logical instruction may actually involve a large chunk of code, and
 *	may even add static synthetic fields and methods to the owning class.
 *	Therefore, once the type of Class being loaded is set, it cannot
 *	be chanced.  Also, this instruction is invalid as the target of
 *	any jump instruction or exception handler.
 *
 *	@author		Abe White
 */
public class ClassConstantInstruction
{
	private Instruction _ins 		= null;
	private Code		_code		= null;
	private BCClass		_class		= null;
	private boolean		_invalid	= false;


	/**
	 *	Protected constructor.
	 */
	protected ClassConstantInstruction (BCClass bc, Code code, Instruction nop)
	{
		_class = bc;
		_code = code;
		_ins = nop;
	}

	
	/**
	 *	Set the type of class being loaded.
	 *	
	 *	@return		the first Instruction of the block added by setting
	 *				the type
	 *	@throws		IllegalStateException if type has already been set
	 */
	public Instruction setClassType (Class type)
	{
		setClassName (type.getName ());
		return _ins;
	}	


	/**
	 *	Set the type of class being loaded.
	 *	
	 *	@return		the first Instruction of the block added by setting
	 *				the type
	 *	@throws		IllegalStateException if type has already been set
	 */
	public Instruction setClassName (String name)
	{
		setClassName (name, BCHelper.getWrapperClass (name)); 
		return _ins;
	}


	private void setClassName (String name, Class wrapper)
	{
		if (_invalid)
			throw new IllegalStateException ();

		// remember the position of the code iterator
		Instruction before = (_code.hasNext ()) ? _code.next () : null;
		_code.before (_ins);
		_code.next ();

		if (wrapper != null)
			_code.getstatic ().setField ("TYPE", Class.class, wrapper);
		else
			setObject (name);

		// move to the old position
		if (before != null)
			_code.before (before);
		else
			_code.afterLast ();
		_invalid = true;
	}


	private void setObject (String name)
	{
		BCField field = addClassField (name);
		BCMethod method = addClassLoadMethod ();

		// copied from the way jikes loads classes
		_code.getstatic ().setField (field);
		JumpInstruction ifnull = _code.if_null ();

		_code.getstatic ().setField (field);
		JumpInstruction go2 = _code.go2 ();
			
		ifnull.setTarget (_code.constant ().setStringConstant (name));
		_code.invokestatic ().setMethod (method);
		_code.dup ();
		_code.putstatic ().setField (field);
		
		go2.setTarget (_code.nop ());
	}


	private BCField addClassField (String name)
	{
		String fieldName = "class$L" + name.replace ('.', '$').
			replace ('[', '$').replace (';', '$');

		BCField field = _class.getField (fieldName);
		if (field == null)
		{
			field = _class.addField (fieldName, Class.class);
			field.makePackage ();
			field.setStatic (true);
			field.addAttribute ("Synthetic");
		}

		return field;
	}


	private BCMethod addClassLoadMethod ()
	{
		BCMethod method = _class.getMethod ("class$");
		if (method != null)
			return method;

		// add the special synthetic method
		method = _class.addMethod ("class$", Class.class, 
			new Class[] { String.class });
		method.setStatic (true);
		method.makePackage ();
		method.addAttribute ("Synthetic");

		// copied directly from the output of the jikes compiler
		Code code = method.addCode ();
		code.setMaxStack (3);
		code.setMaxLocals (2);
		
		Instruction tryStart = code.aload_0 ();
		code.invokestatic ().setMethod ("forName", Class.class, 
			new Class[] { String.class }, Class.class);
		Instruction tryEnd = code.areturn ();
		Instruction handlerStart = code.astore_1 ();
		code.newins ().setClassType (NoClassDefFoundError.class);
		code.dup ();
		code.aload_1 ();
		code.invokevirtual ().setMethod ("getMessage", String.class,
			null, Throwable.class);
		code.invokespecial ().setMethod ("<init>", void.class, 
			new Class[] { String.class }, NoClassDefFoundError.class);
		code.athrow ();
		
		code.addExceptionHandler (tryStart, tryEnd, handlerStart, 
			ClassNotFoundException.class);	

		return method;
	}
}
