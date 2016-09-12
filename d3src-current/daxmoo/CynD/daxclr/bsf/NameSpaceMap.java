package daxclr.bsf;

import java.util.AbstractMap;
import java.util.AbstractSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import bsh.EvalError;
import bsh.Interpreter;
import bsh.NameSpace;

public class NameSpaceMap extends AbstractMap<String,Object> { //Dictionary
	final String[] identity = new String[0];
	private Interpreter interpreter = new Interpreter();
	public NameSpaceMap(NameSpace ns) {		
		interpreter.setNameSpace(ns);		
	}
	public Object get(Object name) {
		try {
			return getInterpreter().eval(""+name);
		} catch (EvalError e) {
			throw new RuntimeException("NameSpaceMap get",e);
		}
	}
	public Object put(String name, Object value) {
		Object o = get(name);
		try {
			getInterpreter().set(name,value);
		} catch (EvalError e) {
			throw new RuntimeException("NameSpaceMap put",e);
		}
		return o;
	}
	
		
	/**
	 * @return the names
	 */
	public String[] getAllNames() {
		String[] s = getNameSpace().getVariableNames();
		if (s==null || s.length==0) return identity;
		return s;
	}
	
	@Override
	public int size() {
		return getAllNames().length;
	}

	@Override
	public Set<Entry<String, Object>> entrySet() {
		return new AbstractSet<Map.Entry<String, Object>>() {
			public Iterator<Map.Entry<String, Object>> iterator() {
				return new NameSpaceEntryIterator();
			};
			@SuppressWarnings("unused")
			public boolean contains(final Map.Entry<String, Object> entry) {
				return true;
			}

			public boolean add(Map.Entry<String, Object> entry) {
				String name = entry.getKey();
				boolean modified = !contains(name);
				try {
					getInterpreter().set(entry.getKey(), entry.getValue());
				} catch (EvalError e) {
					e.printStackTrace();
					modified = false;
				}
				return modified;
			}
			@Override
			public int size() {
				// TODO Auto-generated method stub
				return NameSpaceMap.this.size();
			}
		};
	}

	/**
	 * @param interpreter1
	 *            the interpreter to set
	 */
	void setInterpreter(Interpreter interpreter1) {
		interpreter = new Interpreter(interpreter1,getNameSpace());
	}

	/**
	 * @return the interpreter
	 */
	Interpreter getInterpreter() {
		return interpreter;		
	}

	/**
	 * @param nameSpace1
	 *            the nameSpace to set
	 */
	void setNameSpace(NameSpace nameSpace1) {
		interpreter = new Interpreter(getInterpreter(),nameSpace1);
	}

	/**
	 * @return the nameSpace
	 */
	NameSpace getNameSpace() {
		return interpreter.getNameSpace();
	}

	class NameSpaceEntryIterator implements
			java.util.Iterator<Map.Entry<String, Object>> {
		int keyindex = 0;

		NameSpaceEntryIterator() {}

		public void remove() {
			if (keyindex == 0) throw new IllegalStateException(
					"NameSpaceMap called rove before next()");
			try {
				getInterpreter().unset(getAllNames()[keyindex - 1]);
			} catch (EvalError e) {
				throw new RuntimeException("NameSpaceMap remove "
						+ getAllNames()[keyindex - 1], e);
			}
		}

		public boolean hasNext() {
			return keyindex < getAllNames().length;
		}

		public NameSpaceEntry next() {
			return new NameSpaceEntry(getAllNames()[keyindex++]);
		}

		class NameSpaceEntry implements Map.Entry<String, Object> {
			final String key;
			public NameSpaceEntry(String k) {
				this.key = k;
			}
			public String getKey() {
				return key;
			}
			public Object getValue() {
				try {
					return getInterpreter().get(key);
				} catch (EvalError e) {
					if (true) return null;
					throw new RuntimeException("NameSpaceMap getValue "
							+ getAllNames()[keyindex - 1], e);
				}
			}
			public Object setValue(Object value) {
				Object o = getValue();
				try {
					getInterpreter().set(key, value);
				} catch (EvalError e) {
					throw new RuntimeException("NameSpaceMap setValue "
							+ getAllNames()[keyindex - 1], e);
				}
				return o;
			}
			public boolean equals(Object o) {
				if (!(o instanceof Map.Entry)) return false;
				return key.equals(((Entry)o).getKey());
			}

			public int hashCode() {
				return key.hashCode() + getNameSpace().hashCode();
			}
		}
	}
}