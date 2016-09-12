package com.techtrader.modules.tools.bytecode;


import java.io.*;

import com.techtrader.modules.tools.bytecode.lowlevel.*;
import com.techtrader.modules.tools.bytecode.visitor.*;


/**
 *	Representation of a bytecode method of a class; a BCMethod can only
 *	be obtained from a BCClass.  Note that this class has method to manipulate
 *	its declared excptions and code for convenience only; they can be
 *	manipulated directly through the ATTR_EXCEPTIONS and ATTR_CODE attributes.
 *	
 *	@author		Abe White
 */
public class BCMethod
	extends BCEntity
	implements Constants
{
	private BCClass	_owner				= null;
	private int 	_access 			= ACCESS_PUBLIC;
	private int		_nameIndex			= 0;
	private int		_descriptorIndex 	= 0;


	/**
	 *	Protected constructor.
	 */
	protected BCMethod (BCClass owner)
	{
		_owner = owner;
	}


	/**
	 *	Used when this field is deleted from its class.
	 */
	protected void invalidate ()
	{
		_owner = null;
	}


	/**
	 *	Get the BCClass that owns this method.
	 */
	public BCClass getOwner ()
	{
		return _owner;
	}


	/**
	 *	Return the access flags for this class as a bit array of 
 	 *	ACCESS_XXX constants.  This can be used to transfer access flags
	 *	between methods without getting/setting each possible access flag.
	 */
	public int getAccessFlags ()
	{
		return _access;
	}


	/**
	 *	Set the access flags for this class as a bit array of 
 	 *	ACCESS_XXX constants.  This can be used to transfer access flags
	 *	between methods without getting/setting each possible access flag.
	 */
	public void setAccessFlags (int access)
	{
		_access = access;	
	}


	/**
	 *	Manipulate the method access flags.
	 */
	public boolean isPublic ()
	{
		return BCHelper.hasFlag (_access, ACCESS_PUBLIC);
	}


	/**
	 *	Manipulate the method access flags.
	 */
	public void makePublic ()
	{
		_access = BCHelper.setFlag (_access, ACCESS_PUBLIC, true);
		_access = BCHelper.setFlag (_access, ACCESS_PRIVATE, false);
		_access = BCHelper.setFlag (_access, ACCESS_PROTECTED, false);
	}


	/**
	 *	Manipulate the method access flags.
	 */
	public boolean isProtected ()
	{
		return BCHelper.hasFlag (_access, ACCESS_PROTECTED);
	}


	/**
	 *	Manipulate the method access flags.
	 */
	public void makeProtected ()
	{
		_access = BCHelper.setFlag (_access, ACCESS_PUBLIC, false);
		_access = BCHelper.setFlag (_access, ACCESS_PRIVATE, false);
		_access = BCHelper.setFlag (_access, ACCESS_PROTECTED, true);
	}


	/**
	 *	Manipulate the method access flags.
	 */
	public boolean isPrivate ()
	{
		return BCHelper.hasFlag (_access, ACCESS_PRIVATE);
	}


	/**
	 *	Manipulate the method access flags.
	 */
	public void makePrivate ()
	{
		_access = BCHelper.setFlag (_access, ACCESS_PUBLIC, false);
		_access = BCHelper.setFlag (_access, ACCESS_PRIVATE, true);
		_access = BCHelper.setFlag (_access, ACCESS_PROTECTED, false);
	}


	/**
	 *	Manipulate the method access flags.
	 */
	public boolean isPackage ()
	{
		boolean hasAccess = false;
		hasAccess = hasAccess ||  BCHelper.hasFlag (_access, ACCESS_PRIVATE);
		hasAccess = hasAccess ||  BCHelper.hasFlag (_access, ACCESS_PROTECTED);
		hasAccess = hasAccess ||  BCHelper.hasFlag (_access, ACCESS_PUBLIC);

		return !hasAccess;
	}


	/**
	 *	Manipulate the method access flags.
	 */
	public void makePackage ()
	{
		_access = BCHelper.setFlag (_access, ACCESS_PUBLIC, false);
		_access = BCHelper.setFlag (_access, ACCESS_PRIVATE, false);
		_access = BCHelper.setFlag (_access, ACCESS_PROTECTED, false);
	}


	/**
	 *	Manipulate the method access flags.
	 */
	public boolean isFinal ()
	{
		return BCHelper.hasFlag (_access, ACCESS_FINAL);
	}


	/**
	 *	Manipulate the method access flags.
	 */
	public void setFinal (boolean on)
	{
		_access = BCHelper.setFlag (_access, ACCESS_FINAL, on);
	}


	/**
	 *	Manipulate the method access flags.
	 */
	public boolean isStatic ()
	{
		return BCHelper.hasFlag (_access, ACCESS_STATIC);
	}


	/**
	 *	Manipulate the method access flags.
	 */
	public void setStatic (boolean on)
	{
		_access = BCHelper.setFlag (_access, ACCESS_STATIC, on);
	}


	/**
	 *	Manipulate the method access flags.
	 */
	public boolean isSynchronized ()
	{
		return BCHelper.hasFlag (_access, ACCESS_SYNCHRONIZED);
	}


	/**
	 *	Manipulate the method access flags.
	 */
	public void setSynchronized (boolean on)
	{
		_access = BCHelper.setFlag (_access, ACCESS_SYNCHRONIZED, on);
	}


	/**
	 *	Manipulate the method access flags.
	 */
	public boolean isNative ()
	{
		return BCHelper.hasFlag (_access, ACCESS_NATIVE);
	}


	/**
	 *	Manipulate the method access flags.
	 */
	public void setNative (boolean on)
	{
		_access = BCHelper.setFlag (_access, ACCESS_NATIVE, on);
	}


	/**
	 *	Manipulate the method access flags.
	 */
	public boolean isAbstract ()
	{
		return BCHelper.hasFlag (_access, ACCESS_ABSTRACT);
	}


	/**
	 *	Manipulate the method access flags.
	 */
	public void setAbstract (boolean on)
	{
		_access = BCHelper.setFlag (_access, ACCESS_ABSTRACT, on);
	}


	/**
	 *	Manipulate the method access flags.
	 */
	public boolean isStrict ()
	{
		return BCHelper.hasFlag (_access, ACCESS_STRICT);
	}


	/**
	 *	Manipulate the method access flags.
	 */
	public void setStrict (boolean on)
	{
		_access = BCHelper.setFlag (_access, ACCESS_STRICT, on);
	}


	/**
	 *	Get the index in the constant pool of the UTF entry holding the name
	 *	of this method.
	 */
	public int getNameIndex ()
	{
		return _nameIndex;
	}


	/**
	 *	Set the index in the constant pool of the UTF entry holding the name
	 *	of this method.
	 */
	public void setNameIndex (int index)
	{
		_nameIndex = index;
	}


	/**
	 *	Get the index in the constant pool of the UTF entry holding the 
	 *	descriptor of this method.
	 */
	public int getDescriptorIndex ()
	{
		return _descriptorIndex;
	}


	/**
	 *	Set the index in the constant pool of the UTF entry holding the 
	 *	descriptor of this method.
	 */
	public void setDescriptorIndex (int index)
	{
		_descriptorIndex = index;
	}


	/**
	 *	Get the name of this method.
	 */
	public String getName ()
	{
		return getPool ().getUTF (_nameIndex);
	}


	/**
	 *	Set the name of this method.
	 */
	public void setName (String name)
	{
		String origName = getName ();

		// reset the name
		_nameIndex = getPool ().setUTF (0, name);

		// find the ComplexEntry matching this field, if any
		String internalDesc = getPool ().getUTF (_descriptorIndex);
		String internalOwner = getPool ().getClassName (_owner.getIndex ());

		Class entry = MethodEntry.class;
		if (_owner.isInterface ())
			entry = InterfaceMethodEntry.class;

		int index = getPool ().getComplexIndex
			(origName, internalDesc, internalOwner, entry);

		// change the ComplexEntry to match the new name; this is dones so
		// that refs to the method in code will still be valid after the name
		// change, without changing any other constants that happened to match
		// the old method name
		if (index != 0)
			getPool ().setComplex (index, name, 
				internalDesc, internalOwner, entry);
	}



	/**
	 *	Get the name of the class type returned by this method.
	 */
	public String getReturnTypeName ()
	{
		return BCHelper.getExternalForm (BCHelper.getReturnType 
			(getPool ().getUTF (_descriptorIndex)), true);
	}


	/**
	 *	Get the Class of the return type of this method.
	 */
	public Class getReturnType ()
		throws ClassNotFoundException
	{
		return BCHelper.classForName (BCHelper.getReturnType 
			(getPool ().getUTF (_descriptorIndex)));
	}


	/**
	 *	Get the names of all the parameter types for this method.
	 */
	public String[] getParamTypeNames ()
	{
		// get the parameter types from the descriptor
		String[] params = BCHelper.getParamTypes
			(getPool ().getUTF (_descriptorIndex));

		// convert them to external form
		for (int i = 0; i < params.length; i++)
			params[i] = BCHelper.getExternalForm (params[i], true);

		return params;
	}


	/**
	 *	Get the types of parameters this method takes.
	 */
	public Class[] getParamTypes ()
		throws ClassNotFoundException
	{
		// get the parameter types from the descriptor
		String[] params = BCHelper.getParamTypes 
			(getPool ().getUTF (_descriptorIndex));

		// convert them
		Class[] externalParams = new Class[params.length];
		for (int i = 0; i < params.length; i++)
			externalParams[i] = BCHelper.classForName (params[i]);

		return externalParams;
	}


	/**	
 	 *	Set the return type of this method.
	 */
	public void setReturnTypeName (String name)
	{
		setDescriptorInternal (BCHelper.getInternalForm (name, true), 
			BCHelper.getParamTypes (getPool ().getUTF (_descriptorIndex)));
	}


	/**
	 *	Set the return type of this method.
	 */
	public void setReturnType (Class type)
	{
		setReturnTypeName (type.getName ());
	}


	/**	
 	 *	Set the parameter types of this method.
	 */
	public void setParamTypeNames (String[] names)
	{
		String returnName = BCHelper.getReturnType 
			(getPool ().getUTF (_descriptorIndex));
		if (returnName.length () == 0)
			returnName = "V";

		String[] internalNames;
		if (names == null)
			internalNames = new String[0];
		else
			internalNames = new String[names.length];

		for (int i = 0; i < internalNames.length; i++)
			internalNames[i] = BCHelper.getInternalForm (names[i], true);

		setDescriptorInternal (returnName, internalNames);
	}


	/**
	 *	Set the parameter type of this method.
	 */
	public void setParamTypes (Class[] types)
	{
		if (types == null)
			setParamTypeNames (null);

		String[] names = new String[types.length];
		for (int i = 0; i < types.length; i++)
			names[i] = types[i].getName ();

		setParamTypeNames (names);
	}


	/**
	 *	Add a parameter type to this method.
	 */
	public void addParamTypeName (String name)
	{
		String[] params = getParamTypeNames ();
		String[] newParams = new String[params.length + 1];

		for (int i = 0; i < params.length; i++)
			newParams[i] = params[i];
		newParams[params.length] = name;

		setParamTypeNames (newParams);
	}


	/**
	 *	Remove a parameter from this method.
	 */
	public boolean removeParamTypeName (String name)
	{
		String[] names = getParamTypeNames ();
		if (names.length == 0)
			return false;

		String[] newNames = new String[names.length - 1];

		boolean skip = false;
		for (int i = 0, count = 0; count < newNames.length; i++)
		{
			if (names[i].equals (name))
				skip = true;
			else
				newNames[count++] = names[i];
		}

		if (!skip && newNames.length > 0)
			return false;

		setParamTypeNames (newNames);
		return true;
	}


	/**
	 *	Remove a parameter from this method.
	 */
	public boolean removeParamType (Class type)
	{
		return removeParamTypeName (type.getName ());
	}


	/**
	 *	Add a parameter type to this method.
	 */
	public void addParamType (Class type)
	{
		addParamTypeName (type.getName ());
	}


	/**
	 *	Set this method descriptor; using this method is much more
	 *	efficient than setting the return type and param types separately.
	 */
	public void setDescriptor (String returnType, String[] paramTypes)
	{
		returnType = BCHelper.getInternalForm (returnType, true);

		String[] internalNames;
		if (paramTypes == null)
			internalNames = new String[0];
		else
			internalNames = new String[paramTypes.length];

		for (int i = 0; i < internalNames.length; i++)
			internalNames[i] = BCHelper.getInternalForm (paramTypes[i], true);

		setDescriptorInternal (returnType, internalNames);
	}


	/**
	 *	Set this method descriptor; using this method is much more
	 *	efficient than setting the return type and param types separately.
	 */
	public void setDescriptor (Class returnType, Class[] paramTypes)
	{
		String returnStr = BCHelper.getInternalForm 
			(returnType.getName (), true);

		String[] paramStr;
		if (paramTypes == null)
			paramStr = new String[0];
		else
		{
			paramStr = new String[paramTypes.length];
			for (int i = 0; i < paramTypes.length; i++)
				paramStr[i] = BCHelper.getInternalForm 
					(paramTypes[i].getName (), true);
		}

		setDescriptorInternal (returnStr, paramStr);
	}


	/**
	 *	Internal helper method to set the descriptor of this method,
	 *	using the internal form of the method return type and params
	 */
	private void setDescriptorInternal (String returnType, String[] paramTypes)
	{
		String origDesc = getPool ().getUTF (_descriptorIndex);

		// reset the descriptor
		String internalDesc = BCHelper.getDescriptor (returnType, paramTypes);
		_descriptorIndex = getPool ().setUTF (0, internalDesc);

		// find the ComplexEntry matching this method, if any
		String internalOwner = getPool ().getClassName (_owner.getIndex ());

		Class entry = MethodEntry.class;
		if (_owner.isInterface ())
			entry = InterfaceMethodEntry.class;

		int index = getPool ().getComplexIndex 
			(getName (), origDesc, internalOwner, entry);

		// change the ComplexEntry to match the new type; this is dones so
		// that refs to the method in code will still be valid after the type
		// change, without changing any other constants that happened to match
		// the old method type
		if (index != 0)
			getPool ().setComplex (index, getName (), 
				internalDesc, internalOwner, entry);
	}


	/**
	 *	Get the exception types thrown by this method.
	 */
	public String[] getExceptionTypeNames ()
	{
		ExceptionsAttribute exceptionTable = (ExceptionsAttribute)
			getAttribute (ATTR_EXCEPTIONS);
		if (exceptionTable == null)
			return new String[0];

		return exceptionTable.getExceptionTypeNames ();
	}


	/**
	 *	Get the exception types thrown by this method.
	 */
	public Class[] getExceptionTypes ()
		throws ClassNotFoundException
	{
		ExceptionsAttribute exceptionTable = (ExceptionsAttribute)
			getAttribute (ATTR_EXCEPTIONS);
		if (exceptionTable == null)
			return new Class[0];

		return exceptionTable.getExceptionTypes ();
	}


	/**
	 *	Remove all declared exceptions from this method.
	 */
	public void clearExceptionTypes ()
	{
		removeAttribute (ATTR_EXCEPTIONS);
	}


	/**
	 *	Remove the given exception type from those that this method 
	 *	declares in its throws clause.
	 */
	public boolean removeExceptionTypeName (String name)
	{
		ExceptionsAttribute exceptionTable = (ExceptionsAttribute)
			getAttribute (ATTR_EXCEPTIONS);
		if (exceptionTable == null)
			return false;

		return exceptionTable.removeExceptionTypeName (name);
	}


	/**
	 *	Remove the given exception type from those that this method 
	 *	declares in its throws clause.
	 */
	public boolean removeExceptionType (Class type)
	{
		return removeExceptionTypeName (type.getName ());
	}


	/**
	 *	Set the exception types for this method.
	 */
	public void setExceptionTypeNames (String[] types)
	{
		if (types == null || types.length == 0)
			removeAttribute (ATTR_EXCEPTIONS);
		else
		{
			ExceptionsAttribute exceptionTable = (ExceptionsAttribute)
				getAttribute (ATTR_EXCEPTIONS);
			if (exceptionTable == null)
				exceptionTable = (ExceptionsAttribute) addAttribute 
					(ATTR_EXCEPTIONS);

			exceptionTable.setExceptionTypeNames (types);
		}
	}


	/**
	 *	Set the exception types for this method.
	 */
	public void setExceptionTypes (Class[] types)
	{
		if (types == null)
			setExceptionTypeNames ((String[]) null);
		else
		{
			String[] names = new String[types.length];
			for (int i = 0; i < names.length; i++)
				names[i] = types[i].getName ();
			
			setExceptionTypeNames (names);
		}
	}


	/**	
 	 *	Add an exception to those declared by this method.
	 */
	public void addExceptionTypeName (String name)
	{
		ExceptionsAttribute exceptionTable = (ExceptionsAttribute)
			getAttribute (ATTR_EXCEPTIONS);
		if (exceptionTable == null)
			exceptionTable = (ExceptionsAttribute) addAttribute 
				(ATTR_EXCEPTIONS);

		exceptionTable.addExceptionTypeName (name);
	}


	/**	
 	 *	Add an exception to those declared by this method.
	 */
	public void addExceptionType (Class type)
	{
		addExceptionTypeName (type.getName ());
	}


	/**
     *  Get the code for this method; returns null if none.
	 *	Note that each time the code is fetched, the position of the code
	 *	iterator is reset to before the first opcode.
     */ 
    public Code getCode ()   
	{
        Code code = (Code) getAttribute (ATTR_CODE);
		if (code != null)
			code.beforeFirst ();

		return code;
	}


	/**
	 *	Add a code block to this method; replaces the old block if it exists.
	 */
	public Code addCode ()
	{
		removeAttribute (ATTR_CODE);
		return (Code) addAttribute (ATTR_CODE);
	}


	/**
	 *	Remove the code from this method; note that this actually removes the
	 *	Code attribute completely; if you want to make an empty code block use
	 *	the Code.clear() method.
	 */
	public boolean removeCode ()
	{
		return removeAttribute (ATTR_CODE);
	}


	/**
	 *	Import a code block from another method.  The given method can be of
	 *	this class or a different one.  This will cause the code of this method
	 *	to become an exact duplicate of the given code block.
	 */
	public Code importCode (Code code)
	{
		removeAttribute (ATTR_CODE);
		return (Code) importAttribute (code);
	}


	/**
	 *	Get the class constant pool; this method delegates to the
 	 *	owning class.
	 */
	public ConstantPool getPool ()
	{
		return getOwner ().getPool ();
	}


	protected void readData (DataInput in)
		throws IOException
	{
		setAccessFlags (in.readUnsignedShort ());	
		setNameIndex (in.readUnsignedShort ());	
		setDescriptorIndex (in.readUnsignedShort ());	

		readAttributes (in);
	}


	protected void writeData (DataOutput out)
		throws IOException
	{
		out.writeShort (getAccessFlags ());
		out.writeShort (getNameIndex ());
		out.writeShort (getDescriptorIndex ());

		writeAttributes (out);
	}


	public void acceptVisit (BCVisitor visit)
	{
		visit.enterBCMethod (this);
		visitAttributes (visit);
		visit.exitBCMethod (this);
	}
}
