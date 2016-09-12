package com.techtrader.modules.tools.bytecode.lowlevel;


import java.util.*;
import java.io.*;

import com.techtrader.modules.tools.bytecode.*;
import com.techtrader.modules.tools.bytecode.visitor.*;


/**
 *	Represents a class constant pool, containing entries for all strings,
 *	constants, classes, etc referenced in the class structure and opcodes.
 *	In keeping with the low-level bytecode representation, all pool indexes 
 *	are 1-based.  
 *	<p>
 *	NOTE: Entries are not meant to be manipulated manually.  If you change
 *	entries by hand, make sure to call the {@link #rehash} method of the
 *	ConstantPool so that the entry is hashed correctly for quick lookups and
 *	to avoid duplicates.
 *	<p>
 *	NOTE: LongEntries and DoubleEntries are always followed by a 
 *	PlaceHolderEntry in the pool, as they take up 2 pool indeces.
 *	When manually adding a new entry of these types, it is <b>not</b>
 *	necessary to insert the PlaceHolder as well; the system will do this
 *	automatically.
 *
 *	@author		Abe White
 */
public class ConstantPool
	implements LowLevelConstants, VisitAcceptor
{
	// map constant types to codes
	private static final Map _consts = new HashMap ();
	static
	{
		_consts.put (Integer.class, new Integer (ENTRY_INT));
		_consts.put (Long.class, new Integer (ENTRY_LONG));
		_consts.put (Double.class, new Integer (ENTRY_DOUBLE));
		_consts.put (Float.class, new Integer (ENTRY_FLOAT));
		_consts.put (String.class, new Integer (ENTRY_STRING));
	}

	private List 	_entries	= new LinkedList ();
	private Map		_lookup		= new HashMap ();
	private BCClass	_owner		= null;


	/**
	 *	Create an Entry based on its one-byte code: one of the constants in
 	 *	the LowLevelConstants class.
	 */
	public static Entry createEntry (int type)
	{
		switch (type)
		{
		case ENTRY_PLACEHOLDER:
			return new PlaceHolderEntry ();
		case ENTRY_CLASS:
			return new ClassEntry ();
		case ENTRY_FIELD:
			return new FieldEntry ();
		case ENTRY_METHOD:
			return new MethodEntry ();
		case ENTRY_INTMETHOD:
			return new InterfaceMethodEntry ();
		case ENTRY_STRING:
			return new StringEntry ();
		case ENTRY_INT:
			return new IntEntry ();
		case ENTRY_FLOAT:
			return new FloatEntry ();
		case ENTRY_LONG:
			return new LongEntry ();
		case ENTRY_DOUBLE:
			return new DoubleEntry ();
		case ENTRY_NAME_AND_TYPE:
			return new NameAndTypeEntry ();
		case ENTRY_UTF8:
			return new UTF8Entry ();
		default:
			return null;
		}
	}


	public ConstantPool (BCClass owner)
	{
		_owner = owner;
	}

 
	/**
 	 *	Get the entries in the pool.
	 */
	public Entry[] getEntries ()
	{
		return (Entry[]) _entries.toArray (new Entry[_entries.size ()]);
	}


	/**
	 *	Retrieve the entry at the specified 1-based index.
	 *
	 *	@return		the specified entry, or null if invalid index
	 */
	public Entry getEntry (int index)
	{
		if (index < 1 || index > _entries.size ())
			return null;

		return (Entry) _entries.get (index - 1);
	}


	/**
	 *	Set the entry at the given 1-based index.
	 *
	 *	@return		the entry that was replaced
	 */
	public Entry setEntry (int index, Entry entry)
	{
		if (entry == null)
			throw new NullPointerException ();

		Entry old = (Entry) _entries.set (index - 1, entry);
		removeHash (old);

		// remove placeholders
		if (old != null && (old.getType () == ENTRY_LONG
			|| old.getType () == ENTRY_DOUBLE))
			_entries.remove (index);

		// add placeholder
		if (entry.getType () == ENTRY_LONG || entry.getType () == ENTRY_DOUBLE)
			_entries.add (index, new PlaceHolderEntry ());

		hash (entry, index);

		return old;
	}


	/**
	 *	Set the entry at the given 1-based index.
	 */
	public void addEntry (int index, Entry entry)
	{
		if (entry == null)
			throw new NullPointerException ();

		_entries.add (index - 1, entry);
		hash (entry, index);

		// add placeholder
		if (entry.getType () == ENTRY_LONG || entry.getType () == ENTRY_DOUBLE)
			_entries.add (index, new PlaceHolderEntry ());
	}


	/**
	 *	Add an entry to the pool.
 	 *
	 *	@return		the index at which the entry was added
	 */
	public int addEntry (Entry entry)
	{
		if (entry == null)
			throw new NullPointerException ();

		_entries.add (entry);
		int index = _entries.size ();
		hash (entry, index);

		// add placeholder
		if (entry.getType () == ENTRY_LONG || entry.getType () == ENTRY_DOUBLE)
			_entries.add (new PlaceHolderEntry ());

		return index;
	}


	/**
	 *	Remove the entry at the given index.
	 *
	 *	@return		the removed entry, or null if none
	 */
	public Entry removeEntry (int index)
	{
		if (index < 1 || index > _entries.size ())
			return null;

		Entry old = (Entry) _entries.remove (index - 1);
		removeHash (old);

		// remove placeholders
		if (old != null && (old.getType () == ENTRY_LONG
			|| old.getType () == ENTRY_DOUBLE))
			_entries.remove (index - 1);
	
		return old;
	}


	/**
	 *	Remove the given entry from the pool.
	 *
	 *	@return		false if the entry is not in the pool, true otherwise
	 */
	public boolean removeEntry (Entry entry)
	{
		if (entry == null)
			return false;

		int index = _entries.indexOf (entry);
		if (index != -1)
		{
			_entries.remove (index);
			removeHash (entry);

			// remove placeholders
			if (entry.getType () == ENTRY_LONG 
				|| entry.getType () == ENTRY_DOUBLE)
				_entries.remove (index);

			return true;
		}
		return false;	
	}


	/**
	 *	Return the number of entries in the pool, including placeholder 
	 *	entries.
	 */
	public int size ()
	{
		return _entries.size ();
	}


	/**
	 *	Get the constant pool index of the entry for the given UTF value.
	 *
	 *	@return 	the index of the matching entry, or 0 if no match
 	 */
	public int getUTFIndex (String name)
	{
		if (name == null)
			name = "";

		return find (ENTRY_UTF8 + "|" + name);	
	}


	/**
	 *	Get the value of the entry at the given index.
	 *
	 *	@return		the value of the given entry, or empty string if
	 *				the entry does not exist
	 */
	public String getUTF (int index)
	{
		UTF8Entry entry = (UTF8Entry) getEntry (index);
		if (entry == null)
			return "";

		return entry.getValue ();
	}


	/**
	 *	Set the entry at the given index; if the given index is &lt;= 0,
	 *	a search will be performed for an entry with the given value, and, if
	 *	it fails, a new entry will be added to the pool.
	 *
	 *	@return		the index of the entry with the given value, whether an
	 *				existing one was found/modified or a new one was added
	 */
	public int setUTF (int index, String name)
	{
		if (name == null)
			name = "";

		if (index <= 0)
		{
			index = getUTFIndex (name);
			if (index > 0)
				return index;
		}

		UTF8Entry entry = (UTF8Entry) getEntry (index);
		if (entry == null)
		{
			entry = new UTF8Entry ();
			entry.setValue (name);
			return addEntry (entry);
		}
		
		entry.setValue (name);
		rehash (entry, index);

		return index;
	}


	/**
	 *	Get the constant pool index of the entry for the given class name.
	 *
	 *	@return 	the index of the matching entry, or 0 if no match
 	 */
	public int getClassIndex (String name)
	{
		int nameIdx = getUTFIndex (name);
		if (nameIdx == 0)
			return 0;

		return find (ENTRY_CLASS + "|" + nameIdx);	
	}


	/**
	 *	Get the value of the entry at the given index.
	 *
	 *	@return		the value of the given entry, or empty string if
	 *				the entry does not exist
	 */
	public String getClassName (int index)
	{
		ClassEntry entry = (ClassEntry) getEntry (index);
		if (entry == null)
			return "";

		return getUTF (entry.getNameIndex ());
	}


	/**
	 *	Set the entry at the given index; if the given index is &lt;= 0,
	 *	a search will be performed for an entry with the given value, and, if
	 *	it fails, a new entry will be added to the pool.
	 *
	 *	@return		the index of the entry with the given value, whether an
	 *				existing one was found/modified or a new one was added
	 */
	public int setClassName (int index, String name)
	{
		if (index <= 0)
		{
			index = getClassIndex (name);
			if (index > 0)
				return index;
		}

		ClassEntry entry = (ClassEntry) getEntry (index);
		if (entry == null)
		{
			entry = new ClassEntry ();
			entry.setNameIndex (setUTF (0, name));
			return addEntry (entry);
		}
		
		entry.setNameIndex (setUTF (0, name));
		rehash (entry, index);

		return index;
	}


	/**
	 *	Get the constant pool index of the entry for the given name+type.
	 *
	 *	@return 	the index of the matching entry, or 0 if no match
 	 */
	public int getNameAndTypeIndex (String name, String desc)
	{
		int nameIdx = getUTFIndex (name);
		if (nameIdx == 0)
			return 0;

		int descIdx = getUTFIndex (desc);
		if (descIdx == 0)
			return 0;

		return find (ENTRY_NAME_AND_TYPE + "|" + nameIdx + "|" + descIdx);
	}


	/**
	 *	Set the entry at the given index; if the given index is &lt;= 0,
	 *	a search will be performed for an entry with the given value, and, if
	 *	it fails, a new entry will be added to the pool.
	 *
	 *	@return		the index of the entry with the given value, whether an
	 *				existing one was found/modified or a new one was added
	 */
	public int setNameAndType (int index, String name, String desc)
	{
		if (index <= 0)
		{
			index = getNameAndTypeIndex (name, desc);
			if (index > 0)
				return index;
		}

		NameAndTypeEntry entry = (NameAndTypeEntry) getEntry (index);
		if (entry == null)
		{
			entry = new NameAndTypeEntry ();
			entry.setNameIndex (setUTF (0, name));
			entry.setDescriptorIndex (setUTF (0, desc));
			return addEntry (entry);
		}
		
		entry.setNameIndex (setUTF (0, name));
		entry.setDescriptorIndex (setUTF (0, desc));
		rehash (entry, index);

		return index;
	}


	/**
	 *	Get the constant pool index of the entry for the given complex entry.
	 *
	 *	@return 	the index of the matching entry, or 0 if no match
 	 */
	public int getComplexIndex (String name, String desc, String owner,
		Class type)
	{
		int classIdx = getClassIndex (owner);
		if (classIdx == 0)
			return 0;

		int descIdx = getNameAndTypeIndex (name, desc);
		if (descIdx == 0)
			return 0;

		int code;
		if (type.equals (FieldEntry.class))
			code = ENTRY_FIELD;
		else if (type.equals (MethodEntry.class))
			code = ENTRY_METHOD;
		else
			code = ENTRY_INTMETHOD;

		return find (code + "|" + classIdx + "|" + descIdx);
	}


	/**
	 *	Set the entry at the given index; if the given index is &lt;= 0,
	 *	a search will be performed for an entry with the given value, and, if
	 *	it fails, a new entry will be added to the pool.
	 *
	 *	@return		the index of the entry with the given value, whether an
	 *				existing one was found/modified or a new one was added
	 */
	public int setComplex (int index, String name, String desc, 
		String owner, Class entryType)
	{
		if (index <= 0)
		{
			index = getComplexIndex (name, desc, owner, entryType);
			if (index > 0)
				return index;
		}

		ComplexEntry entry = (ComplexEntry) getEntry (index);
		if (entry == null)
		{
			try
			{
				entry = (ComplexEntry) entryType.newInstance ();
			}
			catch (Throwable t)
			{
				throw new RuntimeException (t.getMessage ());
			}

			entry.setClassIndex (setClassName (0, owner));
			entry.setNameAndTypeIndex (setNameAndType (0, name, desc));
			return addEntry (entry);
		}
		
		entry.setClassIndex (setClassName (0, owner));
		entry.setNameAndTypeIndex (setNameAndType (0, name, desc));
		rehash (entry, index);

		return index;
	}


	/**
	 *	Get the value of the entry at the given index.
	 *
	 *	@return		the value of the given entry, or empty string if
	 *				the entry does not exist
	 */
	public String getComplexName (int index)
	{
		ComplexEntry complex = (ComplexEntry) getEntry (index);
		if (complex == null)
			return "";

		NameAndTypeEntry nt = (NameAndTypeEntry) getEntry 
			(complex.getNameAndTypeIndex ());
		if (nt == null)
			return "";

		return getUTF (nt.getNameIndex ()); 
	}


	/**
	 *	Get the value of the entry at the given index.
	 *
	 *	@return		the value of the given entry, or empty string if
	 *				the entry does not exist
	 */
	public String getComplexTypeName (int index)
	{
		ComplexEntry complex = (ComplexEntry) getEntry (index);
		if (complex == null)
			return "";

		NameAndTypeEntry nt = (NameAndTypeEntry) getEntry 
			(complex.getNameAndTypeIndex ());
		if (nt == null)
			return "";

		return getUTF (nt.getDescriptorIndex ()); 
	}


	/**
	 *	Get the value of the entry at the given index.
	 *
	 *	@return		the value of the given entry, or empty string if
	 *				the entry does not exist
	 */
	public String getComplexOwnerTypeName (int index)
	{
		ComplexEntry complex = (ComplexEntry) getEntry (index);
		if (complex == null)
			return "";

		return getClassName (complex.getClassIndex ());
	}


	/**
	 *	Get the constant pool index of the entry for the given constant value.
	 *
	 *	@return 	the index of the matching entry, or 0 if no match
 	 */
	public int getConstantIndex (Object value)
	{
		if (value == null)
			return 0;

		// get the constant code for the given type	
		int code = ((Integer) _consts.get (value.getClass ())).intValue ();
		if (code == ENTRY_STRING)
			return find (code + "|" + getUTFIndex (value.toString ()));

		return find (code + "|" + value);
	}


	/**
	 *	Get the value of the entry at the given index.
	 *
	 *	@return		the value of the given entry, or null if the entry
	 *				does not exist
	 */
	public Object getConstant (int index)
	{
		ConstantEntry entry = (ConstantEntry) getEntry (index);
		if (entry == null)
			return null;

		if (entry instanceof StringEntry)
			return getUTF (((StringEntry) entry).getStringIndex ());

		return ((ConstantEntry) entry).getConstantValue ();
	}


	/**
	 *	Set the entry at the given index; if the given index is &lt;= 0,
	 *	a search will be performed for an entry with the given value, and, if
	 *	it fails, a new entry will be added to the pool.
	 *
	 *	@return		the index of the entry with the given value, whether an
	 *				existing one was found/modified or a new one was added
	 */
	public int setConstant (int index, Object value)
	{
		if (index <= 0)
		{
			index = getConstantIndex (value);
			if (index > 0)
				return index;
		}

		ConstantEntry entry = (ConstantEntry) getEntry (index);
		if (entry == null)
		{
			int code = ((Integer) _consts.get (value.getClass ())).intValue ();
			entry = (ConstantEntry) createEntry (code);
			if (entry instanceof StringEntry)
				((StringEntry) entry).setStringIndex 
					(setUTF (0, value.toString ()));
			else
				entry.setConstantValue (value);
			return addEntry (entry);
		}
		
		if (entry instanceof StringEntry)
			((StringEntry) entry).setStringIndex (setUTF(0, value.toString ()));
		else
			entry.setConstantValue (value);
		rehash (entry, index);

		return index;
	}


	/**
	 *	Rehash the given entry after modification; this allows for quick
	 *	lookups and guarantees that entries won't be repeated in the pool.
	 */
	public void rehash (Entry entry, int poolIndex)
	{
		if (entry != null)
		{
			// remove the modified entry from the map
			for (Iterator i = _lookup.values ().iterator (); i.hasNext ();)
				if (((HashedEntry) i.next ()).entry.equals (entry))
					i.remove ();

			// rehash with its new values
			hash (entry, poolIndex);
		}
	}


	public void readData (DataInput in)
		throws IOException
	{
		_entries.clear ();
		_lookup.clear ();

		int entryCount = in.readUnsignedShort ();
		Entry entry;
		for (int i = 1; i < entryCount; i++)
		{
			entry = createEntry (in.readUnsignedByte ());
			entry.readData (in);
			addEntry (entry);

			// bytecode 'feature': long and double entries
			// are counted twice in the entryCount
			if (entry.getType () == ENTRY_LONG
				|| entry.getType () == ENTRY_DOUBLE)
				i++;
		}
	}


	public void writeData (DataOutput out)
		throws IOException
	{
		out.writeShort (_entries.size () + 1);
		Entry entry;
		for (Iterator i = _entries.iterator (); i.hasNext ();)
		{
			entry = (Entry) i.next ();

			// compensate for weird long/double entry behavior
			if (entry.getType () == ENTRY_PLACEHOLDER)
				continue;

			out.writeByte (entry.getType ());
			entry.writeData (out);
		}
	}


	private int find (String key)
	{
		HashedEntry he = (HashedEntry) _lookup.get (key);
		if (he == null)
			return 0;

		return he.index;	
	}


	private void hash (Entry entry, int index)
	{
		if (entry != null)
			_lookup.put (entry.getKey (), new HashedEntry (entry, index));
	}


	private void removeHash (Entry entry)
	{
		if (entry != null)
			_lookup.remove (entry.getKey ());
	}


	public void acceptVisit (BCVisitor visit)
	{
		visit.enterConstantPool (this);

		Entry next;
		for (Iterator i = _entries.iterator (); i.hasNext ();)
		{
			next = (Entry) i.next ();

			visit.enterEntry (next);
			next.acceptVisit (visit);
			visit.exitEntry (next);
		}

		visit.exitConstantPool (this);
	}


	private static class HashedEntry
	{
		public Entry 	entry 	= null;
		public int 		index	= 0;


		public HashedEntry (Entry entry, int index)
		{
			this.entry = entry;
			this.index = index;
		}
	}
}

