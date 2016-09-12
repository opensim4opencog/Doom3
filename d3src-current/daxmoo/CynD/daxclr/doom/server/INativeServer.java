package daxclr.doom.server;

import java.io.Serializable;
import java.rmi.Remote;
import java.rmi.RemoteException;

import org.apache.bsf.BSFException;


import daxclr.bsf.IScriptObjectProxy;
import daxclr.doom.IClass;
import daxclr.doom.IGameLocal;
import daxclr.doom.IVector;

public interface INativeServer extends Remote,IScriptObjectProxy {
//	public static final long serialVersionUID = -6720547789456092754L;
	
	/**
	 * // Returns the entity number (+1 from its array location) for name but
	 * returns 0 if the entity not found
	 * 
	 * @param name
	 * @return
	 */
	/* private until refactor */
	public IClass resolveDoomObject(String s) throws RemoteException;

	// public Serializable invokeFunction(int o_function_index, Serializable[]
	// args) throws RemoteException;
	public INativeServer initCompletedCallback(IGameLocal obj)	throws RemoteException;

	public int scriptNumber(String fnClass, String fnName)
			throws RemoteException;

	public int scriptArity(int func) throws RemoteException;

	public String scriptClass(int func) throws RemoteException;

	public String scriptFullname(int func) throws RemoteException;

	public String scriptName(int func) throws RemoteException;

	// final static public Category cat = Category.getInstance(class) throws
	// RemoteException;
	public int scriptNumber(String typename, long pointer, String method)
			throws RemoteException;

	public String scriptParameterType(int func, int num) throws RemoteException;

	public String scriptParameterName(int func, int num) throws RemoteException;

	public String scriptReturnType(int func) throws RemoteException;

	public String scriptSignature(int func) throws RemoteException;

	public void commandAdd(String name, String help) throws RemoteException;

	/**
	 * Returns the entity name for name but returns null if the entity not found
	 * 
	 * @param entnum
	 * @return
	 */
	public String entityName(int entnum) throws RemoteException;

	public long threadSpawnArgs(int threadnum) throws RemoteException;

	public long createEntityPointer(String classname, String entityname,
			String locationname) throws RemoteException;

	public long entityPointer(int entnum) throws RemoteException;

	/**
	 * Returns the entity name for name but returns null if the entity not found
	 * 
	 * @param entnum
	 * @return
	 */
	public String entityClass(int entnum) throws RemoteException;

	/**
	 * Returns the entity name for name but returns null if the entity not found
	 * 
	 * @param entnum
	 * @return
	 */
	public String entityType(int entnum) throws RemoteException;

	/**
	 * Returns Entity spawnArgs: return 0L if the Entity does not exist
	 * 
	 * @param entnum
	 * @return Map
	 */
	public long entitySpawnArgs(int entnum) throws RemoteException;

	public int entityNumber(String name) throws RemoteException;

	public Serializable invokeEntity(int entnum, String cmd, Serializable[] args)
			throws NoSuchMethodException, RemoteException;

	public Serializable invokeThread(int threadnum, String cmd,
			Serializable[] args) throws NoSuchMethodException, RemoteException;

	public long createThreadPointer(String classname) throws RemoteException;

	public long threadPointer(int threadnum) throws RemoteException;

	/**
	 * Returns the thread name for name but returns null if the thread not found
	 * 
	 * @param threadnum
	 * @return
	 */
	public int threadEntity(int threadnum) throws RemoteException;

	/**
	 * Returns the thread name for name but returns null if the thread not found
	 * 
	 * @param threadnum
	 * @return
	 */
	public String threadName(int threadnum) throws RemoteException;

	/**
	 * // Returns the thread number (+1 from its array location) for name but
	 * returns 0 if the thread not found
	 * 
	 * @param name
	 * @return
	 */
	public int threadNumber(String name) throws RemoteException;

	/**
	 * Returns the thread name for name but returns null if the thread not found
	 * 
	 * @param threadnum
	 * @return
	 */
	public Serializable threadState(int threadnum) throws RemoteException;

	public long getIdPointer(short type, int ordinal) throws RemoteException;

	public boolean respondsTo(long pointer1, String eventname)
			throws RemoteException;

	public Serializable invokeEvent(long pointer, String eventname,
			Serializable[] params) throws NoSuchMethodException, BSFException,
			RemoteException;

	// public void printLocal(String string) throws RemoteException;

	// public void debugLocal(String s) throws RemoteException;

	public void deletePointer(long pointer) throws RemoteException;

	public String setSpawnArg(long pointer, String string, String string2)
			throws RemoteException;

	public String removeSpawnArg(long pointer, String string)
			throws RemoteException;

	public String getSpawnArg(long pointer, String string, String defaultstr)
			throws RemoteException;

	public String[] getSpawnKeys(long pointer) throws RemoteException;

	public long allocateIdDict() throws RemoteException;

	public long findGUI(String name) throws RemoteException;

	public boolean setGUI(long pointer) throws RemoteException;

	public Serializable invokeDoomConsole(String params) throws RemoteException;

	public long classSpawnArgs(String typeDef) throws RemoteException;

	public Serializable getLastClickedObject() throws RemoteException;

	public Serializable invokeDoomObject(Serializable name, String cmd,
			Serializable[] args) throws NoSuchMethodException, RemoteException;

	public long resolveVarDef(String s) throws RemoteException;

	public long setCurrentGUI(String name) throws RemoteException;

	public IVector getLastClickedXY() throws RemoteException;

	public IVector getLastXY() throws RemoteException;

	public void setMouseXY(float x, float y) throws RemoteException;

	public void setPixel(float x, float y, float r, float g, float b, float a)
			throws RemoteException;

	public void setMouseImage(String target) throws RemoteException;

	public String getCurrentGUI() throws RemoteException;

	public boolean isMapLoaded() throws RemoteException;

	public String getBaseDirectory() throws RemoteException;

	public Serializable invokeFunction(int o_function_index, Serializable[] args)
			throws RemoteException;

	public int defineFunction(String typeclazz, String name,
			String return_plus_params) throws RemoteException;

	public int defineEvent(String idclazz, String name,
			String return_plus_params) throws RemoteException;

	public void println(String string) throws RemoteException;

	public boolean isDoomServerAvailable()throws RemoteException;

}