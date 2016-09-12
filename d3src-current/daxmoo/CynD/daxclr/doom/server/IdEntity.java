package daxclr.doom.server;

import java.beans.PropertyChangeEvent;
import java.io.ObjectStreamException;
import java.io.Serializable;
import java.lang.reflect.Method;
import java.rmi.RemoteException;

import javax.naming.NameAlreadyBoundException;
import javax.naming.NameNotFoundException;

import org.apache.bsf.BSFException;
import org.opencyc.cycobject.CycFort;

import bsh.EvalError;

import daxclr.bsf.ObjectRepository;
import daxclr.doom.IEntity;
import daxclr.inference.CycAPI;

// @see IEntity //ObjectInfo
class IdEntity extends IdClass {
	/**
	 * 
	 */
	private static final long serialVersionUID = -6739873739220163332L;

	public void run() {
	}

	public long getPointer() {
		try {
			return getGameServer().entityPointer(getEntityNumber());
		} catch (RemoteException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			throw new RuntimeException(e);
		} catch (Throwable e) {
			throw new RuntimeException(e);

		}
	}

	public int entityNumber = -1;

	public String typeDef = null;

	public String classDef = null;

	public IdEntity(int ordinal) throws RemoteException {
		this(getGameServer().entityName(ordinal), ordinal);
	}

	public IdEntity(String entname, int ordinal) throws RemoteException {
		super(entname, 0L, (short) 0);
		importClass(IEntity.class);
		entityNumber = ordinal;
		typeDef = getGameServer().entityType(entityNumber);
		classDef = getGameServer().entityClass(entityNumber);
		try {
			importClass(getGameServer().toClass(getClassDef()));
		} catch (ClassNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public Class getSpawnClass() {
		// firePropertyChange("name", null, entname);
		String oldName = getName();
		if (entityNumber < 1) {
			try {
				entityNumber = getGameServer().entityNumber(oldName);
			} catch (RemoteException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				throw new RuntimeException(e);
			} catch (Throwable e) {
				throw new RuntimeException(e);

			}
			if (entityNumber < 1)
				throw new RuntimeException(new NameNotFoundException(oldName));
		} else {
			try {
				setName(getGameServer().entityName(entityNumber));
			} catch (RemoteException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				throw new RuntimeException(e);
			} catch (Throwable e) {
				throw new RuntimeException(e);

			}
		}
		try {
			return (getGameServer().toClass(getGameServer().entityClass(
					entityNumber)));
		} catch (ClassNotFoundException e) {
			return super.getSpawnClass();
		} catch (RemoteException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			throw new RuntimeException(e);
		} catch (Throwable e) {
			throw new RuntimeException(e);

		}
	}

	public String getTypeDef() {// idTypeInfo
		if (typeDef == null) {
			try {
				typeDef = getGameServer().entityType(entityNumber);
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
		if (entityNumber > 0)
			return entityNumber;
		return super.hashCode();
	}

	public int getEntityNumber() {
		if (entityNumber < 1) {
			try {
				entityNumber = getGameServer().entityNumber(getName());
			} catch (RemoteException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				throw new RuntimeException(e);
			} catch (Throwable e) {
				throw new RuntimeException(e);

			}
		}
		return entityNumber;
	}

	public void setEntityNumber(int number) {
		entityNumber = number;
	}

	public String getEmptyName() {
		return "$null_entity";
	}

	final public static CycFort functor = CycAPI.c("DoomItemFn");

	public CycFort getFunctor() {
		return functor;
	}

	public void resetName(String newName) {
		if (newName != null && !newName.equals(getName())) {
			try {
				invokeMethod("entity_setName", new Serializable[] { getName(),
						newName });
			} catch (Throwable t) {
				t.printStackTrace();
			}
			newName = getName();
		}
	}

	public Serializable invokeMethod(String cmd, Object[] cmdArgs)
			throws RemoteException, NoSuchMethodException, BSFException {
		try {
			return getGameServer().invokeEntity(getEntityNumber(), cmd,
					ObjectRepository.toObjects(cmdArgs));
		} catch (RemoteException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			throw new RuntimeException(e);
		} catch (NoSuchMethodException e) {
			try {
				return super.invokeMethod(cmd, cmdArgs);
			} catch (Error e1) {
				// TODO Auto-generated catch block
				throw new RuntimeException(e1);
			} catch (Exception e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
				throw new RuntimeException(e1);
			} catch (Throwable e1) {
				throw new RuntimeException(e1);
			}
		}
	}

	public transient IdDict spawnArgs = null;

	public IdDict getSpawnArgs() {
		if (spawnArgs == null)
			try {
				spawnArgs = new IdDict(getGameServer().entitySpawnArgs(
						getEntityNumber()));
			} catch (RemoteException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				throw new RuntimeException(e);
			} catch (Throwable e) {
				throw new RuntimeException(e);

			}
		return spawnArgs;
	}

	public String getClassDef() {
		if (classDef != null) {
			return classDef;
		}
		try {
			classDef = getGameServer().entityClass(entityNumber);
		} catch (RemoteException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			throw new RuntimeException(e);
		} catch (Throwable e) {
			throw new RuntimeException(e);

		}
		return classDef;
	}

	// "DoomVocabMt"
	public boolean isMapSpecific() {
		return true;
	}

	/*
	 * Serializable bean = null; public Serializable getValue() { if (bean ==
	 * null) { bean =
	 * java.lang.reflect.Proxy.newProxyInstance(IAI.class.getClassLoader(), new
	 * Class[] { IEntity.class, IAI.class, IPlayer.class, IDoor.class,
	 * IMover.class, IActor.class, ICamera.class, Comparable.class }, this); }
	 * return bean; }
	 */
	/*
	 * public boolean equals(Serializable obj) { if (obj == null) { return
	 * false; } if (obj instanceof IObjectInfo) { return obj.hashCode() ==
	 * hashCode(); } return false; }
	 */

	public void propertyChange(PropertyChangeEvent evt) {
		getNameSpace().propertyChange(evt);
	}


}
