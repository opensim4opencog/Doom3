package daxclr.doom.server;

import java.rmi.RemoteException;
import java.util.Map;


import daxclr.bsf.IScriptMethodHandler;
import daxclr.bsf.IScriptObject;
import daxclr.bsf.ObjectRepository;
import daxclr.doom.IEntity;

class IdDeclEntityDef {
	public String _javaClassName = "daxclr.doom.IEntity";
	public Class<IScriptObject> _clazz = IScriptObject.class;
	public String _doomClassname = null;
	public String o_scriptSignature = null;
	public Map spawnDict = null;
	/**
	 * Used to represent a Java Class type such is required to support
	 * non-static java functions.
	 * 
	 * @param javaClassName
	 *            name of the class such as 'com.foo.Processor'
	 */
	protected IdDeclEntityDef(String javaClassName) {
		_javaClassName = javaClassName;
	}
	protected IdDeclEntityDef(Class<IScriptObject> clazz) {
		_clazz = clazz;
		_javaClassName = clazz.getName();
	}
	public int hashCode() {
		return toString().hashCode();
	}
	public String getJavaClassName() {
		return _javaClassName;
	}
	public String toString() {
		return _doomClassname;
	}
	public boolean equals(Object other) {
		return toString().equals(other.toString());
	}
	public String toSignature() {
		final StringBuffer result = new StringBuffer("L");
		result.append(_javaClassName.replace('.', '/')).append(';');
		return result.toString();
	}
	
	public IScriptMethodHandler newInstance(Map dict) {
		Map sd = null;// new IdDict();
		sd.putAll(getSpawnArgs());
		sd.putAll(dict);
		try {
			return ObjectRepository.getGameLocal().spawnEntity(_doomClassname, "" + dict.get("name"), "" + dict.get("location"));
		} catch (RemoteException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			throw new RuntimeException(e);
			} catch (Throwable e) {
			throw new RuntimeException(e);
			
		}
	}
	/**
	 * Returns a <code>Class</code> object that represents the formal return
	 * type of the method represented by this <code>Method</code> object.
	 * 
	 * @return the return type for the method this object represents
	 */
	// public Class getJavaClass() throws ClassNotFoundException {
	// if (_clazz==null) {
	// _clazz = getGameLocal().forName(_javaClassName);
	// }
	// return _clazz;
	// }
	/**
	 * Returns the name of the method represented by this <code>Method</code>
	 * object, as a <code>String</code>.
	 */
	public String getName() {
		return _javaClassName;
	}
	/**
	 * Returns an array of <code>Class</code> objects that represent the
	 * formal parameter types, in declaration order, of the method represented
	 * by this <code>Method</code> object. Returns an array of length 0 if the
	 * underlying method takes no parameters.
	 * 
	 * @return the parameter types for the method this object represents
	 */
	public Map getSpawnArgs() {
		return spawnDict;
	}
	/**
	 * Returns the Class object representing the class or interface that
	 * declares the member or constructor represented by this Member.
	 * 
	 * @return an object representing the declaring class of the underlying
	 *         member
	 */
	public Class getDeclaringClass() {
		return IEntity.class;
	}
}
