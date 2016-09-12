package daxclr.doom.modules;

import java.io.Serializable;
import java.rmi.RemoteException;

import javax.naming.NameAlreadyBoundException;

import bsh.NameSource.Listener;
import daxclr.bsf.ConsoleChannel;
import daxclr.bsf.IScriptObject;
import daxclr.bsf.IScriptObjectProxy;
import daxclr.bsf.IScriptObjectRemote;
import daxclr.doom.IEntity;
import daxclr.doom.IVector;

//jload positionCmd daxclr.ext.PositionDoomCommand
public class PositionDoomCommand extends RemoteDoomModule implements IScriptObjectProxy {

	public PositionDoomCommand() throws NameAlreadyBoundException {
		super("position");
	}

	public Serializable invokeMethod(String cmd, Object[] cmdArgs)
			throws NoSuchMethodException {
		if (cmdArgs != null && cmdArgs.length > 1) {
			if ("position".equalsIgnoreCase(cmd)) {
				for (int i = 0; i < cmdArgs.length; i++) {
					printPosition("" + cmdArgs[i]);
				}
				return cmd;
			}
		}
		return RemoteDoomModule.eventMissing(this, cmd, cmdArgs);
	}

	static public void printPosition(String objname) {
		// IdGameLocal.safeCommandAdd("position",this);
		IEntity ent;
		try {
			ent = (IEntity) gameLocal.toObject(objname);
		} catch (RemoteException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			throw new RuntimeException(e);
		} catch (Throwable e) {
			throw new RuntimeException(e);

		}
		if (ent != null) {
			IVector vec = ent.getWorldOrigin();
			ConsoleChannel.debug("the " + ent.getName() + " is at "
					+ vec.toString());
		}
	}

	public void addNameSourceListener(Listener listener) {
		// TODO Auto-generated method stub
	}

	public String[] getAllNames() {
		return new String[] { "position" };
	}




	public IScriptObject getNameSpace() {
		try {
			return gameLocal.getRepository().put(getName(), this);
		} catch (RemoteException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			throw new RuntimeException(e);
		} catch (Throwable e) {
			throw new RuntimeException(e);

		}
	}

	public IScriptObjectRemote toRemote() throws RemoteException {
		// TODO Auto-generated method stub
		return getNameSpace().toRemote();
	}

	public void run() {
		// TODO Auto-generated method stub
		
	}

}
