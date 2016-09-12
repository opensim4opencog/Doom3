package daxclr.doom.server;

import java.beans.PropertyChangeEvent;
import java.lang.reflect.Proxy;
import java.rmi.RemoteException;

import javax.naming.NameAlreadyBoundException;

import bsh.ClassIdentifier;
import daxclr.bsf.IScriptObject;
import daxclr.doom.ISys;
import daxclr.doom.IUserInterface;

class IdUserInterface extends IdClass {


	/**
	 * 
	 */
	private static final long serialVersionUID = 1895001596894504892L;

	public void run() {
	}
	public void propertyChange(PropertyChangeEvent evt) {
		getNameSpace().propertyChange(evt);
	}

	// private String name;
	public IdUserInterface(final String naim) throws RemoteException,
			ClassNotFoundException, NameAlreadyBoundException {
		super(naim, 0L, (short) 0);
		importClass(IUserInterface.class);
	}
	
	/*
	 * public IUserInterface getValue() { return (IUserInterface) proxysys; }
	 */
	public IUserInterface toUserInterface() {
		return (IUserInterface) getNameSpace().coerceTo(IUserInterface.class);
	}

	@Override
	public long getPointer() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public Class getSpawnClass() {
		return IUserInterface.class;
	}

	@Override
	public void resetName(String newName) {
		// TODO Auto-generated method stub
	}

	public String getClassDef() {
		return "idUserInterface";
	}

	public boolean isMapSpecific() {
		return true;
	}
}
