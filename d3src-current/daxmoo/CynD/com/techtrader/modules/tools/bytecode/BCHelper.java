package com.techtrader.modules.tools.bytecode;


import java.util.*;


/**
 *	Utility methods for dealing with the internal form of type names.
 *	
 *	@author		Abe White
 */
class BCHelper
{
	private static final Object[][] _codes = new Object[][] {
		{ byte.class, "B" },
		{ char.class, "C" },
		{ double.class, "D" },
		{ float.class, "F" },
		{ int.class, "I" },
		{ long.class, "J" },
		{ short.class, "S" },
		{ boolean.class, "Z" },
		{ void.class, "V" },
	};
	private static final Map _wrappers = new HashMap ();
	static
	{
		_wrappers.put (byte.class.getName (), Byte.class);
		_wrappers.put (boolean.class.getName (), Boolean.class);
		_wrappers.put (char.class.getName (), Character.class);
		_wrappers.put (double.class.getName (), Double.class);
		_wrappers.put (float.class.getName (), Float.class);
		_wrappers.put (int.class.getName (), Integer.class);
		_wrappers.put (long.class.getName (), Long.class);
		_wrappers.put (short.class.getName (), Short.class);
	}


	/**
	 *	Return the wrapper type for the given primitive class, or null
	 *	if the given name is not a primitive type.  The given name should
	 *	be in external form.
	 */
	public static Class getWrapperClass (String name)
	{
		if (name == null)
			return null;
		return (Class) _wrappers.get (name);
	}


	/**
	 *	Return the Class object for the given class name in intenal form.
	 */
	public static Class classForName (String name)
		throws ClassNotFoundException
	{
		if (name == null)
			throw new ClassNotFoundException ("null");

		for (int i = 0; i < _codes.length; i++)
			if (name.equals (_codes[i][1].toString ()))
				return (Class) _codes[i][0];

		return Class.forName (getExternalForm (name, false));
	}


	/**	
 	 *	Converts the given class name to its internal form.
	 *	
	 *	@param	className	the name to convert
	 *	@param	descriptor	true if the name is to be used for a descriptor
	 *						section -- the difference seems to be that for
	 *						descriptors, non-primitives are prefixed with 'L'
	 *						and ended with ';'	
	 */
	public static String getInternalForm (String className, boolean descriptor)
	{
		if (className == null || className.length () == 0) 
			return className;

		// handle array types, whether already in internal form or not
		String prefix = "";
		while (true)
		{
			if (className.endsWith ("[]"))
			{
				prefix += "[";
				className = className.substring (0, className.length () - 2);
			}
			else if (className.startsWith ("["))
			{
				prefix += "[";
				className = className.substring (1);
			}
			else
				break;
		}

		// handle primitive array types
		for (int i = 0; i < _codes.length; i++)
			if (className.equals (_codes[i][1].toString ()) 
				|| className.equals (_codes[i][0].toString ()))
				return prefix + _codes[i][1];

		// if in descriptor form, strip leading 'L' and trailing ';'
		if (className.startsWith ("L") && className.endsWith (";"))
			className = className.substring (1, className.length () - 1);

		// non-primitive	
		className = className.replace ('.', '/');
		if (descriptor || prefix.length () > 0)
			return prefix + "L" + className + ";";
		return prefix + className;
	}


	/**
	 *	Given the internal name of the class, return the 'normal' java name.
	 *
	 *	@param	internalName	the internal name being used
	 *	@param	humanReadable	if the returned name should be in human-readable
	 *							form, or a form suitable for a Class.forName()
	 *							call -- the difference lies in the handling of
	 *							array types
	 */
	public static String getExternalForm (String internalName, 
		boolean humanReadable)
	{
		if (internalName == null || internalName.length () == 0)
			return internalName;

		if (!humanReadable)
		{
			internalName = getInternalForm (internalName, false);
			return internalName.replace ('/', '.');
		}

		// handle arrays
		String postfix = "";
		while (internalName.startsWith ("["))
		{
			internalName = internalName.substring (1);
			postfix += "[]";
		}

		// strip off leading 'L' and trailing ';'
		if (internalName.endsWith (";"))
			internalName = internalName.substring (1, internalName.length ()-1);

		// check primitives
		for (int i = 0; i < _codes.length; i++)
			if (internalName.equals (_codes[i][1].toString ()))
					return _codes[i][0].toString () + postfix;

		return internalName.replace ('/', '.') + postfix;
	}


	/**
	 *	Return true if the given value contains the given flag.
	 */
	public static boolean hasFlag (int value, int flag)
	{
		return (value & flag) > 0;
	}


	/**
	 *	Returns the given value after setting the given flag to 
	 *	'on' or 'off', as specified.
	 */
	public static int setFlag (int value, int flag, boolean on)
	{
		if (on)
			value |= flag;
		else
			value &= ~flag;

		return value;	
	}


	/**
	 *	Get the return type, in internal form, for the given method 
	 *	descriptor string.
	 */
	public static String getReturnType (String descriptor)
	{
		return descriptor.substring (descriptor.indexOf (')') + 1);
	}


	/**
	 *	Get the parameter types, in internal form, for the given method 
	 *	descriptor string.
	 */
	public static String[] getParamTypes (String descriptor)
	{
		if (descriptor == null || descriptor.length () == 0)
			return new String[0];

		// get rid of the parans and the return type
		descriptor = descriptor.substring (1, descriptor.indexOf (')'));
		
		// break the param string into individual params
		List tokens = new LinkedList ();
		int index;
		while (descriptor.length () > 0)
		{
			index = 0;

			// skip the '[' up to the first letter code
			while (!Character.isLetter (descriptor.charAt (index)))
				index++;

			// non-primitives always start with 'L' and end with ';'
			if (descriptor.charAt (index) == 'L')
				index = descriptor.indexOf (';');

			tokens.add (descriptor.substring (0, index + 1));
			descriptor = descriptor.substring (index + 1);
		}

		return (String[]) tokens.toArray (new String[0]);
	}


	/**
	 *	Constructs a method descriptor from the given return and parameter
	 *	types, which should be in internal form.
	 */
	public static String getDescriptor (String returnType, String[] paramTypes)
	{
		StringBuffer buf = new StringBuffer ();

		buf.append ("(");
		for (int i = 0; i < paramTypes.length; i++)
			buf.append (paramTypes[i]);
		buf.append (")");

		buf.append (returnType);

		return buf.toString ();
	}
}
