package daxclr.doom.server;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.ObjectStreamException;
import java.io.Serializable;
import java.lang.reflect.Method;
import java.rmi.Remote;
import java.rmi.RemoteException;

import org.apache.bsf.BSFException;

import org.opencyc.cycobject.CycFort;

import bsh.EvalError;

import daxclr.bsf.IScriptObject;
import daxclr.bsf.IScriptObjectProxy;
import daxclr.bsf.IScriptObjectRemote;
import daxclr.bsf.ObjectRepository;
import daxclr.doom.IClass;
import daxclr.doom.IGameLocal;

abstract class IdClass implements Remote,IClass, IScriptObjectProxy {
	/**
	 * 
	 */
	private static final long serialVersionUID = 6030403818626834654L;

	protected static IGameLocal getGameServer() {
		return ObjectRepository.getGameLocal();
	}

	public void run() {
	}

	public long getPointer() {
		return pointer;
	}

	public short typeDefIndex = -1;

	public String typeDef = null;

	public String spawnDef = null;

	public IdClass(String name, long pointer, short typeNum) {
		this.name = name;
		typeDefIndex = typeNum;
		this.pointer = pointer;
		try {
			getNameSpace().importMap(getSpawnArgs());
		} catch (Exception e) {
			e.printStackTrace();
		}
		getNameSpace().importClass(IClass.class);
		getNameSpace().importObject(this);
	}

	protected void importClass(Class<?> name) {
		getNameSpace().importClass(name);
	}

	public Class getSpawnClass() {
		// firePropertyChange("name", null, entname);
		// String oldName = getName();
		// typeDef = getGameServer().entityType(classNumber);
		// classDef = getGameServer().entityClass(classNumber);
		Class clazz = null;
		try {
			getGameServer().toClass(getClassDef());
			getNameSpace().importClass(clazz);
			return clazz;
		} catch (ClassNotFoundException e1) {
			e1.printStackTrace();
		} catch (RemoteException e) {
			e.printStackTrace();
		} catch (Throwable e) {
		}
		return getNameSpace().getBeanClass();
	}

	public String getTypeDef() {// idTypeInfo
		if (typeDef == null) {
			try {
				typeDef = getGameServer().entityType(typeDefIndex);
			} catch (RemoteException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				throw new RuntimeException(e);
			} catch (Throwable e) {
				throw new RuntimeException(e);
			}
		}
		return typeDef;
	}

	public int hashCode() {
		if (typeDefIndex > 0)
			return typeDefIndex;
		return super.hashCode();
	}

	public void resetName(String newName) {
		if (newName != null && !newName.equals(getName())) {
			try {
				invokeMethod("setName",
						new Serializable[] { getName(), newName });
			} catch (Throwable t) {
				t.printStackTrace();
			}
			newName = getName();
		}
	}

	public transient IdDict spawnArgs = null;

	public IdDict getSpawnArgs() {
		return spawnArgs;
	}

	public String getClassDef() {
		if (spawnDef != null) {
			return spawnDef;
		}
		try {
			spawnDef = getGameServer().entityClass(typeDefIndex);
		} catch (RemoteException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			throw new RuntimeException(e);
		} catch (Throwable e) {
			throw new RuntimeException(e);

		}
		return spawnDef;
	}

	// "DoomVocabMt"
	public boolean isMapSpecific() {
		return true;
	}

	/*
	 * public Serializable getValue() { if (true) return getValue(); if (bean ==
	 * null) { bean = java.lang.reflect.Proxy.newProxyInstance(IAI.class
	 * .getClassLoader(), new Class[] { IEntity.class, IAI.class, IPlayer.class,
	 * IDoor.class, IMover.class, IActor.class, ICamera.class, Comparable.class },
	 * this); } return bean; }
	 */
	// abstract public ClassIdentifier getSpawnClass()
	// public String getName() {
	// return toString();
	// }
	protected long pointer;

	public String name;

	public String getName() {
		return name;
	}

	public void setName(String name) {
		ObjectRepository.resolverMap.put(name, this);
	}

	@Override
	public String toString() {
		return name;
	}

	public Serializable invokeMethod(String cmd, Object[] params)
			throws RemoteException, NoSuchMethodException, BSFException {
		Serializable[] cmdArgs = ObjectRepository.toObjects(params);
		try {
			return getGameServer().invokeEvent(pointer, cmd, cmdArgs);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		try {
			return getGameServer().invokeDoomObject(this, cmd, cmdArgs);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		try {
			return getNameSpace().invokeMethod(cmd, params);
		} catch (NoSuchMethodException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			throw e;
		} catch (Throwable e) {
			throw new RuntimeException(e);
		}
	}

	static CycFort cycInstanceNamedFn = null;

	public boolean respondsTo(String eventname) {
		try {
			return getGameServer().respondsTo(pointer, eventname);
		} catch (RemoteException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			throw new RuntimeException(e);
		} catch (Throwable e) {
			throw new RuntimeException(e);

		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see daxclr.java.IObjectInfo#getInsanceNamedFn()
	 */
	/*
	 * (non-Javadoc)
	 * 
	 * @see daxclr.java.IObjectInfo#getInsanceNamedFn()
	 */
	public CycFort getCycObject() {
		return getNameSpace().getCycObject();
	}

	public String getEmptyName() {
		return "$null";
	}

	public IScriptObject getNameSpace() {
		return ObjectRepository.getResolverMap().findOrCreateEntry(getName());
	}

	public IScriptObjectRemote toRemote() throws RemoteException {
		return getNameSpace().toRemote();
	}

	// public String getClassDef() {
	// TODO Auto-generated method stub
	// return null;
	// }
	public boolean equals(Object keyOrValue) {
		return getNameSpace().valueEquals(keyOrValue);

	}

	private void writeObject(ObjectOutputStream out) throws IOException {
		out.writeObject(getName());
		out.writeObject(getClassDef());
	}

	private void readObject(ObjectInputStream in) throws IOException,
			ClassNotFoundException {
		name = "" + in.readObject();
		spawnDef = "" + in.readObject();
		getNameSpace();
	}

	public Object readResolve() throws ObjectStreamException {
		return this;
	}

	public Object invoke(Object proxy, Method method, Object[] args)
			throws NoSuchMethodException, RemoteException, EvalError,
			BSFException {
		try {
			return invokeMethod(method.getName(), args);
		} catch (Throwable e) {
			e.printStackTrace();
			throw new RuntimeException(e);
		}
	}

}
