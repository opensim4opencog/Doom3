package daxclr.doom;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.Serializable;
import java.rmi.Remote;
import java.rmi.RemoteException;


import daxclr.bsf.IScriptMethodHandler;
import daxclr.bsf.IObjectRepository;
import daxclr.bsf.IScriptObjectRemote;
import daxclr.doom.modules.IDoomModule;
import daxclr.doom.server.INativeServer;

public interface IGameLocal extends Remote, INativeServer,IScriptMethodHandler {
	
	public final int REGISTRY_PORT_GAMELOCAL = 38222;

	public final int REGISTRY_PORT_NATIVESERVER = 28111;

	public INativeServer getDoomServer() throws RemoteException;

	public IGameLocal getGameLocal() throws RemoteException;

	public boolean isDoomServerAvailable() throws RemoteException;

	public boolean isGameLocalAvailable() throws RemoteException;

	/**
	 * 
	 * @return
	 * @param a
	 * @param i
	 */
	public float floatArrayValue(Object a, int i) throws RemoteException;

	/**
	 * 
	 * @param target
	 * @return
	 */
	public float floatValue(Object target) throws RemoteException;

	/**
	 * 
	 * @param target
	 * @return
	 */
	public int intValue(Object target) throws RemoteException;

	public boolean isEqual(final Object o1, final Object o2)
			throws RemoteException;

	/**
	 * Displays the given error message somehow
	 * 
	 * @param msg
	 *            TODO
	 * @param err
	 *            TODO
	 */
	public void showError(final String msg, final Throwable error)
			throws RemoteException;

	/**
	 * 
	 * @param tf
	 * @return
	 */
	public Boolean toBoolean(boolean tf) throws RemoteException;

	public Class toClass(Object object) throws RemoteException,
			ClassNotFoundException;

	public IVector toColor(final float x, final float y, final float z,
			final float a) throws RemoteException;

	/*
	 * (non-Javadoc)
	 * 
	 * @see daxclr.doom.server.IGameLocal#toEntity(int)
	 */
	public IEntity toEntity(int entnum) throws RemoteException;

	/**
	 * 
	 * @return
	 * @param value
	 */
	public Float toFloat(float value) throws RemoteException;

	/**
	 * 
	 * @param value
	 * @return
	 */
	public Integer toInteger(int value) throws RemoteException;

	/**
	 * 
	 * @return
	 */
	public Object toNull() throws RemoteException;


	public Class classForName(String name) throws RemoteException;

	/**
	 * Returns idTypeDef spawnArgs: return null if the IdTypeDef does not exist
	 * 
	 * @param typeDef
	 * @return Map of the IdDict
	 */
	/* private until refactor */
	public IScriptMethodHandler eventMissing(String why) throws NoSuchMethodException,
			RemoteException;

	// public IDoomServer getDoomServer() throws RemoteException;

	/**
	 * 
	 * @return
	 * @param cmdArgs
	 */
	public Serializable invokeCommand(final Object[] cmdArgs)
			throws NoSuchMethodException, RemoteException;

	public Serializable invokeJavaFn(String fnClass, String fnString,
			IClass entity, ISys thread, Object[] params) throws RemoteException;

	/**
	 * 
	 * @param scope
	 * @param fnname
	 * @return
	 */
	public boolean isJavaFn(String scope, String fnname) throws RemoteException;

	public IDoomModule loadPlugin(String name) throws Exception;

	public void loadPluginFile(String plugin_filename)
			throws FileNotFoundException, IOException;

	public void onMapLoad() throws RemoteException;

	public void onMapUnload() throws RemoteException;

	public boolean startObject(String name) throws Exception;

	public String toArrayIntValue(Object entnum, int ord)
			throws RemoteException;

	public IVector toPoint3D(float x, float y, float z) throws RemoteException;

	public String toString(Object entnum) throws RemoteException;

	public ISys toThread(int vals) throws RemoteException;

	public IVector toVector(String vals) throws RemoteException;

	public IObjectRepository getRepository() throws RemoteException;

	public Serializable eval(String string) throws RemoteException;

	/*public IScriptObjectProxy invokeObject(Object target, String cmd, Object[] cmdArgs)
	throws RemoteException, NoSuchMethodException, Exception, Error,
			Throwable;*/

	public void set(String string, Object module)throws RemoteException;

	public void addCommand(String string, IScriptMethodHandler module)throws RemoteException;

	public void addPlugin(String string, IDoomModule module)throws RemoteException;

	public Serializable toObject(Object object)throws RemoteException;

	public void beanRemoved(Object obj)throws RemoteException;

	public void beanAdded(Object obj)throws RemoteException;

	public Serializable[] toObjects(Object[] args)throws RemoteException;

	public IEntity spawnEntity(String classname, String string, String location)throws RemoteException;

	public IScriptObjectRemote getNameSpace()throws RemoteException;

}
