package daxclr.doom.modules;

import java.rmi.RemoteException;

import daxclr.doom.IGameLocal;


public interface IDoomModule {
    public void initializeModule(IGameLocal shell, Object[] config) throws RemoteException;
    public void removeModule();
	public void setConfigLine(String line);
	public String getConfigLine();
}
