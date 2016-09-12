package com.techtrader.modules.tools.bytecode;


import java.lang.reflect.*;
import java.io.*;

import com.techtrader.modules.tools.bytecode.lowlevel.*;
import com.techtrader.modules.tools.bytecode.visitor.*;


/**
 *	Represents an instruction that takes as an argument a method to operate on.
 *	Examples include INVOKEINTERFACE, INVOKEVIRTUAL, etc.
 *
 *	@author		Abe White
 */
public class MethodInstruction
	extends Instruction
{
	private int _index = 0;


	protected MethodInstruction (Code owner, int opcode)
	{
		super (owner);
		_opcode = opcode;
	}


	/**
	 *	Get the index of the ComplexEntry in the constant pool describing
 	 *	the method to operate on.
	 */
	public int getMethodIndex ()
	{
		return _index;
	}


	/**
	 *	Set the index of the ComplexEntry in the constant pool describing
 	 *	the method to operate on.
	 */
	public void setMethodIndex (int index)
	{
		_index = index;
	}


	/**
 	 *	Set the method to operate on.
	 *
	 *	@return		this Instruction, for method chaining
	 */
	public MethodInstruction setMethod (BCMethod method)
	{
		BCClass ownerType = method.getOwner ();
		return setMethod (method.getName (), method.getReturnTypeName (), 
			method.getParamTypeNames (), ownerType.getName ());
	}


	/**
	 *	Set the method to operate on.
	 *
	 *	@return		this Instruction, for method chaining
	 */
	public MethodInstruction setMethod (Method method)
	{
		Class[] paramTypes = method.getParameterTypes ();
		String[] paramNames = new String[paramTypes.length];
		for (int i = 0; i < paramTypes.length; i++)
			paramNames[i] = paramTypes[i].getName ();

		return setMethod (method.getName (), method.getReturnType ().getName (),
			paramNames, method.getDeclaringClass ().getName ());
	}


	/**
	 *	Set the method to operate on.
	 *
	 *	@return		this Instruction, for method chaining
	 */
	public MethodInstruction setMethod (Constructor method)
	{
		Class[] paramTypes = method.getParameterTypes ();
		String[] paramNames = new String[paramTypes.length];
		for (int i = 0; i < paramTypes.length; i++)
			paramNames[i] = paramTypes[i].getName ();

		return setMethod ("<init>", "void", paramNames, 
			method.getDeclaringClass ().getName ());
	}


	/**
	 *	Set the method to operate on.
	 *
	 *	@return		this Instruction, for method chaining
	 */
	public MethodInstruction setMethod (String name, String returnType, 
		String[] paramTypes, String ownerType)
	{
		if (paramTypes == null)
			paramTypes = new String[0];

		String[] internalParams = new String[paramTypes.length];
		for (int i = 0; i < internalParams.length; i++)
			internalParams[i] = BCHelper.getInternalForm 
				(paramTypes[i], true);

		String internalOwner = BCHelper.getInternalForm (ownerType, false);
		String internalType = BCHelper.getDescriptor 
			(BCHelper.getInternalForm (returnType, true), internalParams);

		Class entryType;
		if (_opcode == INVOKEINTERFACE)
			entryType = InterfaceMethodEntry.class;
		else
			entryType = MethodEntry.class;

		_index = _owner.getPool ().setComplex 
			(0, name, internalType, internalOwner, entryType);

		return this;
	}

	
	/**
	 *	Set the method to operate on.
	 *
	 *	@return		this Instruction, for method chaining
	 */
	public MethodInstruction setMethod (String name, Class returnType, 
		Class[] paramTypes, String ownerType)
	{
		String[] paramNames = null;
		if (paramTypes != null)
		{
			paramNames = new String[paramTypes.length];
			for (int i = 0; i < paramTypes.length; i++)
				paramNames[i] = paramTypes[i].getName ();
		}
		String returnName = (returnType == null) ? null : returnType.getName ();

		return setMethod (name, returnName, paramNames, ownerType);
	}


	/**
	 *	Set the method to operate on.
	 *
	 *	@return		this Instruction, for method chaining
	 */
	public MethodInstruction setMethod (String name, Class returnType, 
		Class[] paramTypes, Class ownerType)
	{
		String[] paramNames = null;
		if (paramTypes != null)
		{
			paramNames = new String[paramTypes.length];
			for (int i = 0; i < paramTypes.length; i++)
				paramNames[i] = paramTypes[i].getName ();
		}
		String returnName = (returnType == null) ? null : returnType.getName ();
		String ownerName = (ownerType == null) ? null : ownerType.getName ();

		return setMethod (name, returnName, paramNames, ownerName);
	}


	/**
	 *	Change the method name.
	 *
	 *	@return		this Instruction, for method chaining
	 */
	public MethodInstruction setMethodName (String name)
	{
		return setMethod (name, getMethodReturnTypeName (), 
			getMethodParamTypeNames (), getMethodOwnerTypeName ());
	}


	/**
	 *	Change the method owner type.
	 *
	 *	@return		this Instruction, for method chaining
	 */
	public MethodInstruction setMethodOwnerTypeName (String name)
	{
		return setMethod (getMethodName (), getMethodReturnTypeName (), 
			getMethodParamTypeNames (), name);
	}


	/**
	 *	Change the method return type.
	 *
	 *	@return		this Instruction, for method chaining
	 */
	public MethodInstruction setMethodReturnTypeName (String name)
	{
		return setMethod (getMethodName (), name,
			getMethodParamTypeNames (), getMethodOwnerTypeName ());
	}


	/**
	 *	Change the method param types.
	 *
	 *	@return		this Instruction, for method chaining
	 */
	public MethodInstruction setMethodParamTypeNames (String[] names)
	{
		return setMethod (getMethodName (), getMethodReturnTypeName (),
			names, getMethodOwnerTypeName ());
	}


	/**
	 *	Return true if the method is a member of the current class.
	 */
	public boolean isMethodInCurrentClass ()
	{
		BCClass type = ((BCMethod) _owner.getOwner ()).getOwner ();
		return type.getName ().equals (getMethodOwnerTypeName ());
	}


	/**
	 *	If the method is a member of the current class, then this method will
	 *	retrieve the BCMethod object for it; otherwise it will return null.
	 */
	public BCMethod getMethod ()
	{
		if (!isMethodInCurrentClass ())
			return null;

		return ((BCMethod) _owner.getOwner ()).getOwner ().
			getMethod (getMethodName (), getMethodParamTypeNames ());
	}


	/**
 	 *	Get the nam of the method to operate on.
	 */
	public String getMethodName ()
	{
		return _owner.getPool ().getComplexName (_index);
	}	


	/**
	 *	Get the return type of the method.
	 */
	public String getMethodReturnTypeName ()
	{
		return BCHelper.getExternalForm (BCHelper.getReturnType 
			(_owner.getPool ().getComplexTypeName (_index)), true);
	}


	/**
	 *	Get the return type of the method.
	 */
	public Class getMethodReturnType ()
		throws ClassNotFoundException
	{
		return BCHelper.classForName (BCHelper.getReturnType 
			(_owner.getPool ().getComplexTypeName (_index)));
	}


	/**
	 *	Get the param types of the method.
	 */
	public String[] getMethodParamTypeNames ()
	{
		String[] paramTypes = BCHelper.getParamTypes
			(_owner.getPool ().getComplexTypeName (_index));

		String[] externalTypes = new String[paramTypes.length];
		for (int i = 0; i < paramTypes.length; i++)
			externalTypes[i] = BCHelper.getExternalForm (paramTypes[i], true);

		return externalTypes;
	}


	/**
	 *	Get the param types of the method.
	 */
	public Class[] getMethodParamTypes ()
		throws ClassNotFoundException
	{
		String[] paramTypes = BCHelper.getParamTypes
			(_owner.getPool ().getComplexTypeName (_index));

		Class[] types = new Class[paramTypes.length];
		for (int i = 0; i < paramTypes.length; i++)
			types[i] = BCHelper.classForName (paramTypes[i]); 

		return types;
	}


	/**
	 *	Get the type of the method.
	 */
	public String getMethodOwnerTypeName ()
	{
		return BCHelper.getExternalForm 
			(_owner.getPool ().getComplexOwnerTypeName (_index), true);
	}


	/**
	 *	Get the type of the method.
	 */
	public Class getMethodOwnerType ()
		throws ClassNotFoundException
	{
		return BCHelper.classForName 
			(_owner.getPool ().getComplexOwnerTypeName (_index));
	}


	/**
	 *	Method instructions are equal if the method of either is unset,
	 *	or if they refer to the same method.
	 */
	public boolean equals (Object other)
	{
		if (other == this)
			return true;
		if (!(other instanceof MethodInstruction))
			return false;
		if (!super.equals (other))
			return false;

		MethodInstruction ins = (MethodInstruction) other;

		String s1 = getMethodName ();
		String s2 = ins.getMethodName ();
		if (!(s1.length () == 0 || s2.length () == 0 || s1.equals (s2)))
			return false;

		s1 = getMethodReturnTypeName ();
		s2 = ins.getMethodReturnTypeName ();
		if (!(s1.length () == 0 || s2.length () == 0 || s1.equals (s2)))
			return false;

		s1 = getMethodOwnerTypeName ();
		s2 = ins.getMethodOwnerTypeName ();
		if (!(s1.length () == 0 || s2.length () == 0 || s1.equals (s2)))
			return false;

		String[] params = getMethodParamTypeNames ();
		String[] insParams = ins.getMethodParamTypeNames ();
		if (params.length != insParams.length)
			return false;

		for (int i = 0; i < params.length; i++)
			if (!params[i].equals (insParams[i]))
				return false;

		return true;	
	}


	public int getLength ()
	{
		if (_opcode == INVOKEINTERFACE)
			return super.getLength () + 4;
		return super.getLength () + 2;
	}

	
	public int getStackChange ()
	{
		int stack = 0;

		// subtract a stack pos for the this ptr
		if (_opcode != INVOKESTATIC)
			stack--;

		// and for each arg
		String[] params = getMethodParamTypeNames ();
		for (int i = 0; i < params.length; i++, stack--)
			if (params[i].equals ("long") || params[i].equals ("double"))
				stack--;

		// add for the return value, if any
		String ret = getMethodReturnTypeName ();
		if (!ret.equals ("void"))
			stack++;
		if (ret.equals ("long") || ret.equals ("double"))
			stack++;

		return stack;
	}


	protected void copy (Instruction orig)
	{
		super.copy (orig);
		MethodInstruction origMI = (MethodInstruction) orig;

		setMethod (origMI.getMethodName (), origMI.getMethodReturnTypeName (), 
			origMI.getMethodParamTypeNames (), origMI.getMethodOwnerTypeName());
	}


	protected void readData (DataInput in)
		throws IOException
	{
		_index = in.readUnsignedShort ();
		if (_opcode == INVOKEINTERFACE)
		{
			in.readByte ();
			in.readByte ();
		}
	}


	protected void writeData (DataOutput out)
		throws IOException
	{
		out.writeShort (_index);
		if (_opcode == INVOKEINTERFACE)
		{
			String[] args = getMethodParamTypeNames ();
			int count = 1;
			for (int i = 0; i < args.length; i++, count++)
				if (args[i].equals ("long") || args[i].equals ("double"))
					count++;

			out.writeByte (count);
			out.writeByte (0);
		}
	}


	public void acceptVisit (BCVisitor visit)
	{
		visit.enterMethodInstruction (this);
		visit.exitMethodInstruction (this);
	}
}
