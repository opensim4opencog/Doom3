package daxclr.doom;
import java.io.Serializable;

import daxclr.bsf.INamedObject;
import daxclr.bsf.IScriptMethodHandler;
import daxclr.bsf.IScriptObjectProxy;

// Proxy version of idClass
public interface IClass extends IDoomObject,INamedObject,Serializable, IScriptObjectProxy,IScriptMethodHandler {   //public int compareTo(Object obj);
    // public boolean equals(Object obj);
    // public int hashCode();
    //  public String toString();
    //  public Object invoke(Object proxy, Method method, Object[] args) throws Throwable;

    public Class getSpawnClass() throws ClassNotFoundException;

    public String getName();

    public boolean respondsTo(String eventname);

    public String getClassDef();

/*
    public IAI toAI() throws ClassCastException;

    public IActor toActor() throws ClassCastException;

    public ICamera toCamera() throws ClassCastException;

    public IDoor toDoor() throws ClassCastException;

    public IEntity toEntity();

    public IMover toMover() throws ClassCastException;
*/
    //  public IPlayer toPlayer() throws ClassCastException;


}
