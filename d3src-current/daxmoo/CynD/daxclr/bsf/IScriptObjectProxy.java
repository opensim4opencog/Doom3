package daxclr.bsf;

import java.io.*;
import java.rmi.Remote;
import java.rmi.RemoteException;

/**
 * Serializability of a class is enabled by the class implementing the
 * java.io.Serializable interface. Classes that do not implement this interface
 * will not have any of their state serialized or deserialized. All subtypes of
 * a serializable class are themselves serializable. The serialization interface
 * has no methods or fields and serves only to identify the semantics of being
 * serializable.
 * <p>
 * 
 * To allow subtypes of non-serializable classes to be serialized, the subtype
 * may assume responsibility for saving and restoring the state of the
 * supertype's public, protected, and (if accessible) package fields. The
 * subtype may assume this responsibility only if the class it extends has an
 * accessible no-arg constructor to initialize the class's state. It is an error
 * to declare a class Serializable if this is not the case. The error will be
 * detected at runtime.
 * <p>
 * 
 * During deserialization, the fields of non-serializable classes will be
 * initialized using the public or protected no-arg constructor of the class. A
 * no-arg constructor must be accessible to the subclass that is serializable.
 * The fields of serializable subclasses will be restored from the stream.
 * <p>
 * 
 * When traversing a graph, an object may be encountered that does not support
 * the Serializable interface. In this case the NotSerializableException will be
 * thrown and will identify the class of the non-serializable object.
 * <p>
 * 
 * Classes that require special handling during the serialization and
 * deserialization process must implement special methods with these exact
 * signatures:
 * <p>
 * 
 * <PRE>
 * 
 * private void writeObject(java.io.ObjectOutputStream out) throws IOException
 * private void readObject(java.io.ObjectInputStream in) throws IOException,
 * ClassNotFoundException;
 * 
 * </PRE>
 * 
 * <p>
 * 
 * The writeObject method is responsible for writing the state of the object for
 * its particular class so that the corresponding readObject method can restore
 * it. The default mechanism for saving the Object's fields can be invoked by
 * calling out.defaultWriteObject. The method does not need to concern itself
 * with the state belonging to its superclasses or subclasses. State is saved by
 * writing the individual fields to the ObjectOutputStream using the writeObject
 * method or by using the methods for primitive data types supported by
 * DataOutput.
 * <p>
 * 
 * The readObject method is responsible for reading from the stream and
 * restoring the classes fields. It may call in.defaultReadObject to invoke the
 * default mechanism for restoring the object's non-static and non-transient
 * fields. The defaultReadObject method uses information in the stream to assign
 * the fields of the object saved in the stream with the correspondingly named
 * fields in the current object. This handles the case when the class has
 * evolved to add new fields. The method does not need to concern itself with
 * the state belonging to its superclasses or subclasses. State is saved by
 * writing the individual fields to the ObjectOutputStream using the writeObject
 * method or by using the methods for primitive data types supported by
 * DataOutput.
 * <p>
 * 
 * Serializable classes that need to designate an alternative object to be used
 * when writing an object to the stream should implement this special method
 * with the exact signature:
 * <p>
 * 
 * <PRE>
 * 
 * ANY-ACCESS-MODIFIER Object writeReplace() throws ObjectStreamException;
 * 
 * </PRE>
 * 
 * <p>
 * 
 * This writeReplace method is invoked by serialization if the method exists and
 * it would be accessible from a method defined within the class of the object
 * being serialized. Thus, the method can have private, protected and
 * package-private access. Subclass access to this method follows java
 * accessibility rules.
 * <p>
 * 
 * Classes that need to designate a replacement when an instance of it is read
 * from the stream should implement this special method with the exact
 * signature.
 * <p>
 * 
 * <PRE>
 * 
 * ANY-ACCESS-MODIFIER Object readResolve() throws ObjectStreamException;
 * 
 * </PRE>
 * 
 * <p>
 * 
 * This readResolve method follows the same invocation rules and accessibility
 * rules as writeReplace.
 * <p>
 * 
 * The serialization runtime associates with each serializable class a version
 * number, called a serialVersionUID, which is used during deserialization to
 * verify that the sender and receiver of a serialized object have loaded
 * classes for that object that are compatible with respect to serialization. If
 * the receiver has loaded a class for the object that has a different
 * serialVersionUID than that of the corresponding sender's class, then
 * deserialization will result in an {@link InvalidClassException}. A
 * serializable class can declare its own serialVersionUID explicitly by
 * declaring a field named <code>"serialVersionUID"</code> that must be
 * static, final, and of type <code>long</code>:
 * <p>
 * 
 * <PRE>
 * 
 * ANY-ACCESS-MODIFIER static final long serialVersionUID = 42L;
 * 
 * </PRE>
 * 
 * The DoomConsole calls invokeScript each time a known java command is used
 * 
 * For a command to become 'known' IdGameLocal.commandAdd(String);
 * 
 * example:
 * 
 * package daxclr.ext;
 * 
 * import daxclr.doom.*;
 * 
 * public class PositionDoomCommand implements AbstractDoomCommand {
 * 
 * public PositionDoomCommand() { IdGameLocal.commandAdd(&quot;position&quot;); }
 * 
 * public boolean invokeScript(Object[] cmdArgs) { if (cmdArgs!=null &amp;&amp;
 * cmdArgs.length&gt;1) { if (&quot;position&quot;.equals(cmdArgs[0])) { for
 * (int i=1;i&lt;cmdArgs.length;i++) { printPosition(cmdArgs[i]); } return true; } }
 * return false; }
 * 
 * 
 * static public void printPosition(String objname) { IEntity ent =
 * getGameLocal().toEntity(objname); if (ent!=null) { IdVector vec =
 * ent.getWorldOrigin(); getGameLocal().debugln(&quot;the &quot;+ent.getName()+
 * &quot; is at &quot;+vec.toString()); } } }
 * 
 * 
 * to load this class from Doom engine in the console type:
 * 
 * jload positionCmd daxclr.modules.PositionDoomCommand
 * 
 * </pre>
 */
public interface IScriptObjectProxy extends Remote, INamedObject {
	// public IScriptObjectRemote getNameSpace() throws RemoteException;
	public IScriptObjectRemote toRemote() throws RemoteException;

	// public Object readResolve() throws ObjectStreamException,RemoteException;

}