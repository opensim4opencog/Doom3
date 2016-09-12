package daxclr.doom.server;

import java.rmi.RemoteException;
import java.util.Map;
import java.util.Properties;
import java.util.Set;

import daxclr.bsf.ObjectRepository;
import daxclr.doom.IGameLocal;

class IdDict extends java.util.AbstractMap<String, Object> {
	public static INativeServer getDoomServer() {
		try {
			return ObjectRepository.getGameLocal().getDoomServer();
		} catch (Throwable e) {
			throw new RuntimeException(e);
		}
	}

	public long pointer = 0;

	public boolean freeable = false;

	public String[] keyNames = null;

	protected void finalize() throws Throwable {
		if (pointer != 0) {
			if (freeable) {
				getDoomServer().deletePointer(pointer);
			}
		}
	}

	public static String toName(Object key) {
		return "" + key; // IdGameLocal.toName(key);
	}

	public static Object toObject(Object key) {
		return key;// IdGameLocal.toObject(key);
	}

	public Object get(Object key) {
		try {
			return toObject(getDoomServer().getSpawnArg(pointer, toName(key),
					null));
		} catch (RemoteException e) {
			throw new RuntimeException(e);
		}
	}

	public synchronized Object put(String key, Object value) {
		try {
			return toObject(getDoomServer().setSpawnArg(pointer, toName(key),
					toName(value)));
		} catch (RemoteException e) {
			throw new RuntimeException(e);
		}
	}

	public synchronized Object remove(Object key) {
		Object old = get(key);
		try {
			getDoomServer().removeSpawnArg(pointer, toName(key));
		} catch (RemoteException e) {
			throw new RuntimeException(e);
		}
		return old;
	}

	public IdDict() throws RemoteException {
		this(getDoomServer().allocateIdDict());
	}

	public IdDict(long prealloced) {
		pointer = prealloced;
		size();
	}

	public synchronized void clear() {
		try {
			keyNames = getDoomServer().getSpawnKeys(pointer);
			for (int i = 0; i < keyNames.length; i++) {
				getDoomServer().removeSpawnArg(pointer, keyNames[i]);
			}
		} catch (RemoteException e) {
			throw new RuntimeException(e);
		}
	}

	public synchronized void load(java.io.InputStream inStream)
			throws java.io.IOException {
		Properties tempProperties = new Properties();
		tempProperties.load(inStream);
		putAll(tempProperties);
	}

	public void putAll(Map t) {
		for (Map.Entry e : ((Map<?, ?>) t).entrySet()) {
			put(toName(e.getKey()), e.getValue());
		}
	}

	public int size() {
		try {
			keyNames = getDoomServer().getSpawnKeys(pointer);
		} catch (RemoteException e) {
			throw new RuntimeException(e);
		}
		return keyNames.length;
	}

	public int hashCode() {
		return (int) pointer;
	}

	public boolean equals(Object o) {
		if (o == null || (!(o instanceof IdDict)))
			return false;
		return o.hashCode() == hashCode();
	}

	public boolean containsKey(Object key) {
		size();
		String target = toName(key);
		for (int i = 0; i < keyNames.length; i++)
			if (target.equalsIgnoreCase(keyNames[i]))
				return true;
		return false;
	}

	public Set<Map.Entry<String, Object>> entrySet() {
		size();
		return (new java.util.AbstractSet<Map.Entry<String, Object>>() {
			public int size() {
				return IdDict.this.size();
			}

			public IdDictEntryIterator iterator() {
				return new IdDictEntryIterator();
			}
		});
	}

	class IdDictEntryIterator implements java.util.Iterator {
		IdDictEntryIterator() {
		}

		int keyindex = 0;

		public boolean hasNext() {
			return (keyindex < IdDict.this.keyNames.length);
		}

		public void remove() {
			try {
				getDoomServer().removeSpawnArg(pointer,
						IdDict.this.keyNames[keyindex - 1]);
			} catch (RemoteException e) {
				throw new RuntimeException(e);
			}
		}

		public IdDictEntry next() {
			return new IdDictEntry(toName(IdDict.this.keyNames[keyindex++]));
		}

		class IdDictEntry implements Map.Entry<String, Object> {
			private String key = null;

			public IdDictEntry(String k) {
				this.key = k;
			}

			public String getKey() {
				return key;
			}

			public Object getValue() {
				return IdDict.this.get(key);
			}

			public Object setValue(Object value) {
				return IdDict.this.put(key, value);
			}

			public boolean equals(Object o) {
				if (!(o instanceof IdDictEntry))
					return false;
				return (this.hashCode() == o.hashCode());
			}

			public int hashCode() {
				return key.hashCode() + IdDict.this.hashCode();
			}
		}
	}
}
