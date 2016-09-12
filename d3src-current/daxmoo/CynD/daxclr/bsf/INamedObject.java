package daxclr.bsf;

import java.io.IOException;
import java.rmi.Remote;

public interface INamedObject extends Remote{

	/* returns an IObjectInfo (which is the InvocationHandler) for this instance */
	// public IScriptObjectRemote getNameSpace() throws RemoteException;
	public String getName() throws IOException;

}