package daxclr.doom.modules;

import java.io.Serializable;
import java.rmi.RemoteException;

import javax.naming.NameAlreadyBoundException;

import jinni.kernel.JavaIO;
import bsh.NameSource.Listener;
import daxclr.bsf.ConsoleChannel;
import daxclr.bsf.ObjectRepository;
import daxclr.doom.IGameLocal;


/**
 * Summary description for SpeechRecognitionModule.
 in doom use "jload aimltest daxclr.ext.SpeechRecognitionModule"
 */
public class SpeechRecognitionModule extends RemoteDoomModule implements IDoomModule,IDoomMapListener {

    static public QuadmoreSR sprecog = null;

    public String toString() {
        return "sprecog";
    }

	public void run() {		
	}


    public SpeechRecognitionModule() throws NameAlreadyBoundException {
    	super("sprecogmod");
        if (sprecog == null) sprecog = new daxclr.doom.modules.QuadmoreSR();
    }

    // Doom calls this while loading before the map is loaded
    public void initializeModule(IGameLocal shell, Object[] config) throws RemoteException {
        clearDictation();
        resumeDictation();
        if (sprecog == null) sprecog = new daxclr.doom.modules.QuadmoreSR();
        try {
			shell.set("sprecog", this);
		} catch (RemoteException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			throw new RuntimeException(e);
			} catch (Throwable e) {
			throw new RuntimeException(e);
			
		}
        // Register this into doom engine to be called when someone types "speak"
        shell.addCommand("listen",this);
        shell.addCommand("heard",this);
        // Register this into doom engine to be in the scripting bean shell objects as "sprecog"
        //sayFor("server","Loading the sprecog module");
        //String file = (args.length > 0 ? args[0] : "daxclr.ext.QuadmoreSR.properties.xml");
    }

// Doom calls this after the map was unloaded and the server is about to be shutdown
    public void removeModule() {
    }

// Doom calls this when the map is first loaded
    public void onMapLoad() {
        // TODO Initialize the AIML proccesses
    }

// Doom calls this when before the map is unloaded (and sometimes before a map is loaded)
    public void onMapUnload() {
    }

/**
 * just makes printing easier
 */
    static public void println(String s) {
        JavaIO.dump(s);
        debugln(s);
    }

// easy way to print stack traces to exceptions
    static public void debugln(Throwable e) {
        try {
            ConsoleChannel.debug(e);
        } catch (Throwable ee) {
            e.printStackTrace(System.err);
        }
    }

// easy way to print messages in doom console
    static public void debugln(String e) {
        try {
            ConsoleChannel.debug(e);
        } catch (Throwable ee) {
            System.err.println(e);
        }
    }                    

// Doom Map Events
    public Serializable invokeMethod(String cmd, Object[] cmdArgs) {
        debugln("SpeechRecognitionModule Received: " + cmd + " " + ConsoleChannel.joinString(cmdArgs, " "));
        if (cmd.equalsIgnoreCase("listen")) {
            pauseDictation();
            String heard = getDictation();
            clearDictation();
            @SuppressWarnings("unused")
			int time = 5;
            if (cmdArgs.length>1) {
                int maybe = Integer.getInteger(""+cmdArgs[0],5);
                if (maybe>0) {
                    time = maybe;
                }
            }
            clearDictation();
            resumeDictation();
            //System.wait(time*1000);
            pauseDictation();
            sayInDoom("Player",getDictation());
            setDictation(heard);
            resumeDictation();
            return true;
        } else if (cmd.equalsIgnoreCase("heard")) {
            pauseDictation();
            debugln(getDictation());
            clearDictation();
            resumeDictation();
            return true;
        }
        return false;
    }


    // Handle the request
    static public void sayInDoom(String speaker, String textSaid) {
        try {
			getGameLocal().invokeDoomConsole("say "+speaker+ " " + textSaid);
		} catch (RemoteException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			throw new RuntimeException(e);
			} catch (Throwable e) {
			throw new RuntimeException(e);
			
		}
    }

    static public String dictationBuffer = "";

    static public void setDictation(String heard) {
        dictationBuffer = heard;
    }

    static public String getDictation() {
        return dictationBuffer;
    }

    public QuadmoreSR current() {
        if (sprecog == null) sprecog = new daxclr.doom.modules.QuadmoreSR();
        return sprecog;
    }

    public void resumeDictation() {
    }
    public void pauseDictation() {
    }
    public void clearDictation() {
    }

	public void addNameSourceListener(Listener listener) {
		// TODO Auto-generated method stub
		
	}

	public String[] getAllNames() {
		// TODO Auto-generated method stub
		return new String[]{"listen","heard"};
	}

	@Override
	public boolean isMapSpecific() {
		// TODO Auto-generated method stub
		return false;
	}

}

