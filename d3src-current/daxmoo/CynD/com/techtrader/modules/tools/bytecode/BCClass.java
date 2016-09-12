package com.techtrader.modules.tools.bytecode;


import java.io.*;
import java.util.*;

import com.techtrader.modules.tools.bytecode.lowlevel.*;
import com.techtrader.modules.tools.bytecode.visitor.*;


/**
 *	Start here to understand this package.
 *	A BCClass is a representation of a bytecode class.  It contains
 *	methods to manipulate the class object itself as well as methods to
 *	manage the fields and methods of the class.  As with most entities in the
 *	bytecode framework, there are methods to manipulate the low-level state
 *	of the class (constant pool indexes, etc), but these methods can safely
 *	be ignored in favor of the available high-level methods.
 *	
 *	@author		Abe White
 */
public class BCClass
	extends BCEntity
	implements Constants
{
	private int 	_magic				= VALID_MAGIC;
	private int 	_minorVersion 		= 3;
	private int 	_majorVersion 		= 45;
	private int 	_access 			= ACCESS_PUBLIC | ACCESS_SUPER;
	private int		_classIndex			= 0;
	private int		_superclassIndex 	= 0;

	private ConstantPool	_pool				= new ConstantPool (this);
	private List			_interfaceIndexes	= new LinkedList ();
	private List			_methods			= new LinkedList ();
	private List			_fields				= new LinkedList ();


	/**
	 *	Default constructor.  Creates a new empty class.
	 */
	public BCClass ()
	{
	}


	/**
	 *	Create a BCClass that is an exact copy of the given one.
	 */
	public BCClass (BCClass orig)
	{
		copy (orig);
	}


	/**	
 	 *	Create a BCClass for the given Class type.
	 */
	public BCClass (Class type)
		throws IOException
	{
		read (type);
	}


	/**
	 *	Create a new BCClass with the given name. 
	 */
	public BCClass (String name)
	{
		setName (name);
		setSuperclassName (Object.class.getName ());
	}


	/**
	 *	Create a BCClass from the given .class file.
	 */
	public BCClass (File classFile)
		throws IOException
	{
		read (classFile);
	}


	/**
	 *	Create a BCClass from the given stream representing a .class file.
	 */
	public BCClass (InputStream in)
		throws IOException
	{
		read (in);
	}


	/**
	 *	Read the class definition from the given file.
	 *	This method resets all information in this instance.
	 */
	public void read (File classFile)
		throws IOException
	{
		InputStream in = new FileInputStream (classFile);
		try { read (in); } finally { in.close (); }
	}


	/**
	 *	Read the class definition from the given stream.
	 *	This method resets all information in this instance.
	 */
	public void read (InputStream instream)
		throws IOException
	{
		DataInput in = new DataInputStream (instream);

		// header information
		setMagic (in.readInt ());
		setMinorVersion (in.readUnsignedShort ());
		setMajorVersion (in.readUnsignedShort ());

		// constant pool
		_pool.readData (in);

		// access flags
		setAccessFlags (in.readUnsignedShort ());

		// class, super class, interfaces
		setIndex (in.readUnsignedShort ());
		setSuperclassIndex (in.readUnsignedShort ());

		_interfaceIndexes.clear ();
		int interfaceCount = in.readUnsignedShort ();
		int interfaceIndex;
		for (int i = 0; i < interfaceCount; i++)
			_interfaceIndexes.add (new Integer (in.readUnsignedShort ()));

		// fields
		_fields.clear ();
		int fieldCount = in.readUnsignedShort ();
		BCField field;
		for (int i = 0; i < fieldCount; i++)
		{
			field = addField ();
			field.readData (in);
		}	

		// methods
		_methods.clear ();
		int methodCount = in.readUnsignedShort ();
		BCMethod method;
		for (int i = 0; i < methodCount; i++)
		{
			method = addMethod ();
			method.readData (in);
		}	

		readAttributes (in);
	}


	/**
	 *	Read the definition of the given class.
	 *	This method resets all information in this instance.
	 */
	public void read (Class type)
		throws IOException
	{
		// find out the length of the package name
		int dotIndex = type.getName ().lastIndexOf ('.') + 1;

		// strip the package off of the class name
		String className = type.getName ().substring (dotIndex);

		// attempt to get the class file for the class as a stream
		InputStream in = type.getResourceAsStream (className + ".class");
		try { read (in); } finally { in.close (); }
	}


	/**	
 	 *	For existing classes, write the new bytecode to the same .class file
	 *	the class was laoded from.
	 */
	public void write ()
		throws IOException
	{
		String name = getName ();
		int dotIndex = name.lastIndexOf ('.') + 1;
		name = name.substring (dotIndex);

		Class type = null;
		try
		{
			type = getType ();
		}
		catch (ClassNotFoundException cnfe)
		{
			throw new IOException (cnfe.getMessage ());
		}

		// attempt to get the class file for the class as a stream
		OutputStream out = new FileOutputStream (type.getResource 
			(name + ".class").getFile ());
		try { write (out); } finally { out.close (); }
	}


	/**
	 *	Write the class to the specified file.
	 */
	public void write (File classFile)
		throws IOException
	{
		OutputStream out = new FileOutputStream (classFile);
		try { write (out); } finally { out.close (); }
	}


	/**
	 *	Write the class to the specified stream.
	 */
	public void write (OutputStream outstream)
		throws IOException
	{
		DataOutput out = new DataOutputStream (outstream);

		// header information
		out.writeInt (getMagic ());
		out.writeShort (getMinorVersion ());
		out.writeShort (getMajorVersion ());

		// constant pool
		_pool.writeData (out);

		// access flags
		out.writeShort (getAccessFlags ());

		// class, super class, interfaces
		out.writeShort (getIndex ());
		out.writeShort (getSuperclassIndex ());
		out.writeShort (_interfaceIndexes.size ());
		for (Iterator i = _interfaceIndexes.iterator (); i.hasNext ();)
			out.writeShort (((Number) i.next ()).intValue ());

		// fields
		out.writeShort (_fields.size ());
		for (Iterator i = _fields.iterator (); i.hasNext ();)
			((BCField) i.next ()).writeData (out);

		// methods
		out.writeShort (_methods.size ());
		for (Iterator i = _methods.iterator (); i.hasNext ();)
			((BCMethod) i.next ()).writeData (out);

		// attributes
		writeAttributes (out);
	}


	/**
	 *	Get the contents of this class as a byte array, possibly for use
	 *	in a custom ClassLoader.
	 */
	public byte[] toByteArray ()
		throws IOException
	{
		ByteArrayOutputStream out = new ByteArrayOutputStream ();
		try
		{
			write (out);
			out.flush ();

			return out.toByteArray ();
		}
		finally	
		{
			out.close ();
		}
	}


	/**
	 *	Copy all of the data from the given original BCClass to this one,
	 *	recursing into fields, methods, code, the constant pool, etc.
	 */
	public void copy (BCClass orig)
	{
		try
		{
			ByteArrayInputStream in = new ByteArrayInputStream 
				(orig.toByteArray ());
			read (in);
			in.close ();
		}
		catch (IOException ioe)
		{
			throw new RuntimeException (ioe.getMessage ());
		}
	}


	/**
	 *	Get the magic number for this .class; if this is a valid class, this
	 *	should be equal to the VALID_MAGIC constant.
	 */
	public int getMagic ()
	{
		return _magic;
	}


	/**
	 *	Set the magic number for this .class; if this is a valid class, this
	 *	should be equal to the VALID_MAGIC constant (the default value).
	 */
	public void setMagic (int magic)
	{
		_magic = magic;
	}


	/**
	 *	Get the major version of the bytecode spec used for this class;
	 *	JVMs are only required to operate with versions that they understand;
	 *	leaving the default value (45) is safe.
	 */
	public int getMajorVersion ()
	{
		return _majorVersion;
	}


	/**
	 *	Set the major version of the bytecode spec used for this class;
	 *	JVMs are only required to operate with versions that they understand;
	 *	leaving the default value (45) is safe.
	 */
	public void setMajorVersion (int majorVersion)
	{
		_majorVersion = majorVersion;	
	}


	/**
	 *	Get the minor version of the bytecode spec used for this class;
	 *	JVMs are only required to operate with versions that they understand;
	 *	leaving the default value (3) is safe.
	 */
	public int getMinorVersion ()
	{
		return _minorVersion;
	}


	/**
	 *	Set the minor version of the bytecode spec used for this class;
	 *	JVMs are only required to operate with versions that they understand;
	 *	leaving the default value (3) is safe.
	 */
	public void setMinorVersion (int minorVersion)
	{
		_minorVersion = minorVersion;	
	}


	/**
	 *	Return the access flags for this class as a bit array of 
 	 *	ACCESS_XXX constants.  This can be used to transfer access flags
	 *	between classes without getting/setting each possible access flag.
	 */
	public int getAccessFlags ()
	{
		return _access;
	}


	/**
	 *	Set the access flags for this class as a bit array of 
 	 *	ACCESS_XXX constants.  This can be used to transfer access flags
	 *	between classes without getting/setting each possible access flag.
	 */
	public void setAccessFlags (int access)
	{
		_access = access;	
	}


	/**
	 *	Manipulate the class access flags.
	 */
	public boolean isPublic ()
	{
		return BCHelper.hasFlag (_access, ACCESS_PUBLIC);
	}


	/**
	 *	Manipulate the class access flags.
	 */
	public void makePublic ()
	{
		_access = BCHelper.setFlag (_access, ACCESS_PUBLIC, true);
	}


	/**
	 *	Manipulate the class access flags.
	 */
	public boolean isPackage ()
	{
		return !isPublic ();
	}


	/**
	 *	Manipulate the class access flags.
	 */
	public void makePackage ()
	{
		_access = BCHelper.setFlag (_access, ACCESS_PUBLIC, false);
	}


	/**
	 *	Manipulate the class access flags.
	 */
	public boolean isFinal ()
	{
		return BCHelper.hasFlag (_access, ACCESS_FINAL);
	}


	/**
	 *	Manipulate the class access flags.
	 */
	public void setFinal (boolean on)
	{
		_access = BCHelper.setFlag (_access, ACCESS_FINAL, on);
	}


	/**
	 *	Manipulate the class access flags.
	 */
	public boolean isInterface ()
	{
		return BCHelper.hasFlag (_access, ACCESS_INTERFACE);
	}


	/**
	 *	Manipulate the class access flags.
	 */
	public void setInterface (boolean on)
	{
		_access = BCHelper.setFlag (_access, ACCESS_INTERFACE, on);
		if (on)
			setAbstract (true);
	}


	/**
	 *	Manipulate the class access flags.
	 */
	public boolean isAbstract ()
	{
		return BCHelper.hasFlag (_access, ACCESS_ABSTRACT);
	}


	/**
	 *	Manipulate the class access flags.
	 */
	public void setAbstract (boolean on)
	{
		_access = BCHelper.setFlag (_access, ACCESS_INTERFACE, on);
	}


	/**
	 *	Get the index in the constant pool of the ClassEntry for this class.
	 */
	public int getIndex ()
	{
		return _classIndex;
	}


	/**
	 *	Set the constant pool index of the ClassEntry for this class.
	 */
	public void setIndex (int index)
	{
		_classIndex = index;
	}

	
	/**
	 *	Get the name of this class, including package name.
	 */
	public String getName ()
	{
		return BCHelper.getExternalForm 
			(_pool.getClassName (_classIndex), true);
	}


	/**
	 *	Set the name of this class.
	 */
	public void setName (String name)
	{
		_classIndex = _pool.setClassName (_classIndex, 
			BCHelper.getInternalForm (name, false));
	}


	/**
	 *	Get the Class object for this class.
	 */
	public Class getType ()
		throws ClassNotFoundException
	{
		return BCHelper.classForName (_pool.getClassName (_classIndex));
	}


	/**
	 *	Get the index in the constant pool of the ClassEntry for the
	 *	superclass.
	 */
	public int getSuperclassIndex ()
	{
		return _superclassIndex;
	}


	/**
	 *	Set the constant pool index of the ClassEntry for the superclass.
	 */
	public void setSuperclassIndex (int index)
	{
		_superclassIndex = index;
	}


	/**
	 *	Get the name of the superclass for this class, including package name.
	 */
	public String getSuperclassName ()
	{
		return BCHelper.getExternalForm 
			(_pool.getClassName (_superclassIndex), true);
	}


	/**
	 *	Set the name of the superclass to this class.
	 */
	public void setSuperclassName (String name)
	{
		_superclassIndex = _pool.setClassName (_superclassIndex, 
			BCHelper.getInternalForm (name, false));
	}


	/**
	 *	Get the Class object for the superclass of this class.
	 */
	public Class getSuperclassType ()
		throws ClassNotFoundException
	{
		return BCHelper.classForName (_pool.getClassName (_superclassIndex));
	}


	/**
	 *	Set the Class object for the superclass of this class.
	 */
	public void setSuperclassType (Class type)
	{
		if (type == null)
			setSuperclassName (Object.class.getName ());
		else
			setSuperclassName (type.getName ());
	}


	/**
	 *	Get the list of indexes into the constant pool of the ClassEntrys 
	 *	describing all the interfaces this class implements/extends.
	 *	
	 *	@return		the implmented interfaces, or empty array if none
	 */
	public int[] getInterfaceIndexes ()
	{
		int[] indexes = new int[_interfaceIndexes.size ()];

		Iterator intItr = _interfaceIndexes.iterator ();
		for (int i = 0, max = _interfaceIndexes.size (); i < max; i++)
			indexes[i] = ((Integer) intItr.next ()).intValue ();

		return indexes;
	}


	/**
	 *	Set the list of indexes into the constant pool of the ClassEntrys 
	 *	describing all the interfaces this class implements/extends; set to 
	 *	null if none.
	 */
	public void setInterfaceIndexes (int[] interfaceIndexes)
	{
		_interfaceIndexes.clear ();

		if (interfaceIndexes != null)
			for (int i = 0; i < interfaceIndexes.length; i++)
				_interfaceIndexes.add (new Integer (interfaceIndexes[i]));
	}


	/**
	 *	Get the names of the interfaces for this class, including package names.
	 */
	public String[] getInterfaceNames ()
	{
		String[] names = new String[_interfaceIndexes.size ()];

		Iterator interfaces = _interfaceIndexes.iterator ();
		for (int i = 0; i < names.length; i++)
			names[i] = BCHelper.getExternalForm (_pool.getClassName
				(((Integer) interfaces.next ()).intValue ()), true);

		return names;
	}


	/**
	 *	Get the Class objects for the interfaces of this class.
	 */
	public Class[] getInterfaceTypes ()
		throws ClassNotFoundException
	{
		Class[] types = new Class[_interfaceIndexes.size ()];

		Iterator interfaces = _interfaceIndexes.iterator ();
		for (int i = 0; i < types.length; i++)
			types[i] = BCHelper.classForName (_pool.getClassName 
				(((Integer) interfaces.next ()).intValue ()));

		return types;
	}


	/**
	 *	Set the interfaces implemented by this class.
	 */
	public void setInterfaceNames (String[] interfaces)
	{
		_interfaceIndexes.clear ();
		if (interfaces != null)
			for (int i = 0; i < interfaces.length; i++)
				addInterfaceName (interfaces[i]);
	}


	/**
	 *	Set the interfaces implemented by this class.
	 */
	public void setInterfaceTypes (Class[] interfaces)
	{
		String[] names = null;
		if (interfaces != null)
		{
			names = new String[interfaces.length];
			for (int i = 0; i < interfaces.length; i++)
				names[i] = interfaces[i].getName ();
		}

		setInterfaceNames (names);
	}


	/**
	 *	Clear this class of all interface declarations.
	 */
	public void clearInterfaces ()
	{
		_interfaceIndexes.clear ();
	}


	/**
	 *	Remove an interface implmented by this class.
	 */
	public boolean removeInterfaceName (String name)
	{
		if (name == null)
			return false;
		
		String internalForm = BCHelper.getInternalForm (name, false);
		for (Iterator i = _interfaceIndexes.iterator (); i.hasNext ();)
		{
			if (_pool.getClassName (((Integer) i.next ()).intValue ()).
				equals (internalForm))
			{
				i.remove ();
				return true;
			}
		}
		return false;
	}


	/**
	 *	Remove an interface implemented by this class.
	 */
	public boolean removeInterfaceType (Class type)
	{
		return removeInterfaceName (type.getName ());
	}


	/**	
 	 *	Add an interface to those implemented by this class.
	 */
	public void addInterfaceName (String name)
	{
		int index = _pool.setClassName (0,
			BCHelper.getInternalForm (name, false));

		_interfaceIndexes.add (new Integer (index));
	}


	/**	
 	 *	Add a Class to those implemented by this interface.
	 */
	public void addInterfaceType (Class type)
	{
		addInterfaceName (type.getName ());
	}


	/**
	 *	Return true if the class declares that it implements the given 
	 *	interface.
	 */
	public boolean implementsInterface (String name)
	{
		String[] interfaces = getInterfaceNames ();
		for (int i = 0; i < interfaces.length; i++)
			if (interfaces[i].equals (name))
				return true;

		return false;
	}


	/**
	 *	Return true if the class declares that it implements the given 
	 *	interface.
	 */
	public boolean implementsInterface (Class type)
	{
		return implementsInterface (type.getName ());
	}
	

	/**
	 *	Get all the fields of this class.
	 */
	public BCField[] getFields ()
	{
		return (BCField[]) _fields.toArray (new BCField[_fields.size ()]);
	}

	
	/**
	 *	Get the field with the given name.
	 */
	public BCField getField (String name)
	{
		BCField next;
		for (Iterator i = _fields.iterator (); i.hasNext ();)
		{
			next = (BCField) i.next ();
			if (next.getName ().equals (name))
				return next;
		}

		return null;
	}


	/**
	 *	Import the given field from another class, or, if the field belongs
	 *	to this class, add a duplicate of it (in this case, it is an error
	 *	not to change the field name).
	 */
	public BCField importField (BCField field)
	{
		BCField newField = addField (field.getName (), field.getTypeName ());
		newField.setAccessFlags (field.getAccessFlags ());
		newField.importAttributes (field);

		return newField;
	}


	/**
	 *	Import all fields from another class.
	 */
	public void importFields (BCClass other)
	{
		BCField[] fields = other.getFields ();
		for (int i = 0; i < fields.length; i++)
			importField (fields[i]);
	}


	/**
	 *	Add a field to this class.
	 */
	public BCField addField ()
	{
		BCField field = new BCField (this);
		_fields.add (field);

		return field;
	}


	/**
	 *	Add a field to this class.
	 */
	public BCField addField (String name, String type)
	{
		BCField field = addField ();
		field.setName (name);
		field.setTypeName (type);

		return field;
	}


	/**
	 *	Add a field to this class.
	 */
	public BCField addField (String name, Class type)
	{
		return addField (name, type.getName ());
	}


	/**
	 *	Clear all fields from this class.
	 */
	public void clearFields ()
	{
		_fields.clear ();
	}


	/**
	 *	Removes the field with the given name from this class.
	 */
	public boolean removeField (String name)
	{
		return removeField (getField (name));
	}


	/**	
 	 *	Removes a field from this class.  After this method, the field
	 *	will be invalid, and the result of any operations on it is undefined.
	 */
	public boolean removeField (BCField field)
	{
		if (field == null || !_fields.remove (field))
			return false;

		field.invalidate ();
		return true;
	}


	/**
	 *	Get all the methods of this class.
	 */
	public BCMethod[] getMethods ()
	{
		return (BCMethod[]) _methods.toArray (new BCMethod[_methods.size ()]);
	}

	
	/**
	 *	Get the method with the given name.  If multiple methods in this class
	 *	have this name, which is returned is not defined.
	 */
	public BCMethod getMethod (String name)
	{
		BCMethod next;
		for (Iterator i = _methods.iterator (); i.hasNext ();)
		{
			next = (BCMethod) i.next ();
			if (next.getName ().equals (name))
				return next;
		}

		return null;
	}


	/**
	 *	Get all methods with the given name.
	 *
	 *	@return		the matching methods, or empty array if none
	 */
	public BCMethod[] getMethods (String name)
	{
		List matches = new LinkedList ();
		BCMethod next;
		for (Iterator i = _methods.iterator (); i.hasNext ();)
		{
			next = (BCMethod) i.next ();
			if (next.getName ().equals (name))
				matches.add (next);
		}

		return (BCMethod[]) matches.toArray (new BCMethod[matches.size ()]);
	}


	/**
	 *	Get the method with the given name and param types.
	 */
	public BCMethod getMethod (String name, String[] params)
	{
		if (params == null)
			params = new String[0];

		String[] curParams;
		boolean match;
		BCMethod next;
		for (Iterator i = _methods.iterator (); i.hasNext ();)
		{
			next = (BCMethod) i.next ();
			if (next.getName ().equals (name))
			{
				curParams = next.getParamTypeNames ();
				if (curParams.length != params.length)
					continue;

				match = true;
				for (int j = 0; j < params.length; j++)
				{
					if (!curParams[j].equals (BCHelper.getExternalForm 
						(params[j], true)))
					{
						match = false;
						break;
					}
				}

				if (match)
					return next;
			}
		}

		return null;
	}


	/**
	 *	Get the method with the given name and param types.
	 */
	public BCMethod getMethod (String name, Class[] params)
	{
		String[] paramTypeNames;
		if (params == null)
			paramTypeNames = new String[0];
		else
		{
			paramTypeNames = new String[params.length];
			for (int i = 0; i < params.length; i++)
				paramTypeNames[i] = params[i].getName ();
		}

		return getMethod (name, paramTypeNames);
	}


	/**
	 *	Import the given method from another class, or, if the method belongs
	 *	to this class, add a duplicate of it (in this case, it is an error
	 *	not to change the method name or parameter types).
	 */
	public BCMethod importMethod (BCMethod method)
	{
		BCMethod newMethod = addMethod (method.getName (), 
			method.getReturnTypeName (), method.getParamTypeNames ());
		newMethod.setAccessFlags (method.getAccessFlags ());
		newMethod.importAttributes (method);

		return newMethod;
	}


	/**
	 *	Import all methods from the given class; note that this includes
	 *	constructors, static initializers, etc.
	 */
	public void importMethods (BCClass other)
	{
		BCMethod[] methods = other.getMethods ();
		for (int i = 0; i < methods.length; i++)
			importMethod (methods[i]);
	}


	/**
	 *	Add a method to this class.
	 */
	public BCMethod addMethod ()
	{
		BCMethod method = new BCMethod (this);
		_methods.add (method);

		return method;
	}

	
	/**
	 *	Add a method to this class.
	 */
	public BCMethod addMethod (String name, String returnType, 
		String[] paramTypes)
	{
		BCMethod method = addMethod ();
		method.setName (name);
		method.setDescriptor (returnType, paramTypes);

		return method;
	}


	/**
	 *	Add a method to this class.
	 */
	public BCMethod addMethod (String name, Class returnType, 
		Class[] paramTypes)
	{
		BCMethod method = addMethod ();
		method.setName (name);
		method.setDescriptor (returnType, paramTypes);

		return method;
	}


	/**
	 *	Remove all methods from this class; note that this includes 
	 *	constructors, static initializers, etc.
	 */
	public void clearMethods ()
	{
		_methods.clear ();
	}


	/**	
 	 *	Removes a method from this class.  After this method, the method
	 *	will be invalid, and the result of any operations on it is undefined.
	 */
	public boolean removeMethod (BCMethod method)
	{
		if (method == null || !_methods.remove (method))
			return false;

		method.invalidate ();
		return true;
	}


	/**
	 *	Removes the method with the given name from the class.  If multiple
	 *	methods have the name, they will all be removed.
	 */
	public boolean removeMethod (String name)
	{
		BCMethod[] matches = getMethods (name);
		for (int i = 0; i < matches.length; i++)
			removeMethod (matches[i]);

		return (matches.length > 0);
	}


	/**
	 *	Removes the method with the given signature.
	 */
	public boolean removeMethod (String name, String[] params)
	{
		return removeMethod (getMethod (name, params));
	}


	/**
	 *	Removes the method with the given signature.
	 */
	public boolean removeMethod (String name, Class[] params)
	{
		return removeMethod (getMethod (name, params));
	}


	/**
	 *	Return the constant pool for this class.
	 */
	public ConstantPool getPool ()
	{
		return _pool;
	}


	/**
	 *	Add a default constructor to this class.  This method can only be
	 *	called if the superclass has been set.
	 */
	public BCMethod addDefaultConstructor ()
	{
		BCMethod method = addMethod ("<init>", void.class, null);
		Code code = method.addCode ();
		code.setMaxStack (1);
		code.setMaxLocals (1);
	
		code.aload_0 ();
		code.invokespecial ().setMethod 
			("<init>", void.class, null, getSuperclassName ());
		code.vreturn ();

		return method;
	}


	public void acceptVisit (BCVisitor visit)
	{
		visit.enterBCClass (this);

		_pool.acceptVisit (visit);
		for (Iterator i = _fields.iterator (); i.hasNext ();)
			((BCField) i.next ()).acceptVisit (visit);
		for (Iterator i = _methods.iterator (); i.hasNext ();)
			((BCMethod) i.next ()).acceptVisit (visit);
		visitAttributes (visit);

		visit.exitBCClass (this);
	}
}
