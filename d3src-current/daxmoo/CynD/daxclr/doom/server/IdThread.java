package daxclr.doom.server;

import java.beans.PropertyChangeEvent;
import java.io.Serializable;
import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.Collection;

import javax.naming.NameAlreadyBoundException;
import javax.naming.NameNotFoundException;

import org.apache.bsf.BSFException;

import bsh.ClassIdentifier;
import daxclr.bsf.ConsoleChannel;
import daxclr.bsf.IScriptObject;
import daxclr.bsf.ObjectRepository;
import daxclr.doom.IEntity;
import daxclr.doom.IGameLocal;
import daxclr.doom.ISys;

class IdThread extends IdClass {
	public class ThreadInterpreter {
		public ThreadInterpreter() {
		}
	}

	public void run() {
	}

	private static final long serialVersionUID = -2083975950770705063L;

	/**
	 * Returns the Set of threads that are in the game
	 * 
	 *//*
	final public Collection<IScriptObject> getThreadSet() {
		return getThreadMap().values();
	}*/

	/**
	 * Returns idTypeDef spawnArgs: return null if the IObjectInfo does not
	 * exist
	 * 
	 * @param threadmm
	 * @return Map of the IdDict
	 */
	public transient IdDict spawnArgs = null;

	public IdDict getSpawnArgs() {
		if (spawnArgs == null)
			try {
				spawnArgs = new IdDict(getGameServer().threadSpawnArgs(
						getThreadNumber()));
			} catch (RemoteException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				throw new RuntimeException(e);
			} catch (Throwable e) {
				throw new RuntimeException(e);

			}
		return spawnArgs;
	}

	public String classDef = null;

	final private ThreadInterpreter threadInterpreter;

	private IEntity threadEntity;

	public int threadNumber = -1;

	public String threadState = null;

	public IdThread(final int threadnum1) throws ClassNotFoundException,
			RemoteException, NameAlreadyBoundException {
		this(getGameServer().threadName(threadnum1), threadnum1);
	}

	// public boolean respondsTo(String eventname) {
	// return IdGameLocal.respondsTo(this,eventname);
	//
	public IdThread(final String name) throws ClassNotFoundException,
			RemoteException, NameAlreadyBoundException {
		this(name, getGameServer().threadNumber(name));
	}

	public IdThread(final String entname, final int threadnum1)
			throws ClassNotFoundException, RemoteException,
			NameAlreadyBoundException {
		super(entname, 0L, (short) 0);
		threadNumber = threadnum1;
		threadInterpreter = new ThreadInterpreter();
	}

	public String getClassDef() {
		// TODO Auto-generated method stub
		return "idThread";
	}

	@Override
	public String getEmptyName() {
		return "";
	}

	public IEntity getEntity() {
		if (threadEntity == null)
			try {
				threadEntity = getGameServer().toEntity(
						getGameServer().threadEntity(threadNumber));
			} catch (RemoteException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				throw new RuntimeException(e);
			} catch (Throwable e) {
				throw new RuntimeException(e);

			}
		return threadEntity;
	}

	@Override
	public long getPointer() {
		try {
			return getGameServer().threadPointer(getThreadNumber());
		} catch (RemoteException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			throw new RuntimeException(e);
		} catch (Throwable e) {
			throw new RuntimeException(e);

		}
	}

	@Override
	// InvocationHandler
	public Class getSpawnClass() {
		// firePropertyChange("name", null, entname);
		final String oldName = getName();
		if (threadNumber < 1) {
			try {
				threadNumber = getGameServer().threadNumber(oldName);
			} catch (RemoteException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				throw new RuntimeException(e);
				} catch (Throwable e) {
				throw new RuntimeException(e);
				
			}
			if (threadNumber < 1)
				throw new RuntimeException(new NameNotFoundException(oldName));
		} else
			try {
				setName(getGameServer().threadName(threadNumber));
			} catch (RemoteException e2) {
				// TODO Auto-generated catch block
				e2.printStackTrace();
				throw new RuntimeException(e2);
				} catch (Throwable e2) {
				throw new RuntimeException(e2);
				
			}
		try {
			threadEntity = getGameServer().toEntity(
					getGameServer().threadEntity(threadNumber));
		} catch (RemoteException e2) {
			// TODO Auto-generated catch block
			e2.printStackTrace();
			throw new RuntimeException(e2);
			} catch (Throwable e2) {
			throw new RuntimeException(e2);
			
		}
		importClass(ISys.class);
		try {
			importClass(ObjectRepository.resolverMap.toClass(getClassDef()));
		} catch (ClassNotFoundException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		// addInterface(new Class[] {IThread.class,IAI.class, IPlayer.class,
		// IDoor.class, IMover.class, IActor.class, ICamera.class});
		try {
			getNameSpace().importMap(getSpawnArgs());
		} catch (final Exception e) {
			e.printStackTrace();
		}
		// return Class.forName(threadEntity(threadNumber));
		return (ISys.class);
	}

	public int getThreadNumber() {
		if (threadNumber < 1)
			try {
				threadNumber = getGameServer().threadNumber(getName());
			} catch (RemoteException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				throw new RuntimeException(e);
			} catch (Throwable e) {
				throw new RuntimeException(e);

			}
		return threadNumber;
	}

	public Serializable threadState() {// idTypeInfo
		try {
			return getGameServer().threadState(threadNumber);
		} catch (RemoteException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			throw new RuntimeException(e);
		} catch (Throwable e) {
			throw new RuntimeException(e);

		}
	}

	@Override
	public int hashCode() {
		if (threadNumber > 0)
			return threadNumber;
		return super.hashCode();
	}

	public Serializable invokeObject(final String mn,
			final Serializable[] args) throws RemoteException, Exception, Error {
		try {
			try {
				return getGameServer()
						.invokeThread(getThreadNumber(), mn, args);
			} catch (RemoteException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				throw new RuntimeException(e);
			}
		} catch (NoSuchMethodException ex) {
			// TODO Auto-generated catch block
			return super.invokeMethod( mn, args);
		}
	}

	public boolean isMapSpecific() {
		return true;
	}

	static void debug(Throwable t) {
		ConsoleChannel.debug(t);
	}

	public void setThreadNumber(final int number) {
		threadNumber = number;
	}

	public ThreadInterpreter threadInterpreter() {
		return threadInterpreter;
	}
	public void propertyChange(PropertyChangeEvent evt) {
		getNameSpace().propertyChange(evt);
	}
	
}
