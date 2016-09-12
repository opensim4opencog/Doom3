package daxclr.doom.modules;

import java.io.Serializable;
import java.rmi.Remote;
import java.rmi.RemoteException;

import javax.naming.NameAlreadyBoundException;

import jinni.kernel.JavaIO;
import org.apache.bsf.BSFException;
import bitoflife.chatterbean.ChatterBean;
import daxclr.bsf.ConsoleChannel;
import daxclr.doom.IGameLocal;
import daxclr.java.*;


/**
 * Summary description for CycAIMLModule. in doom use "jload aimltest
 * daxclr.inference.CycAIMLModule"
 */
public class CycAIMLModule extends RemoteDoomModule implements
		daxclr.bsf.IScriptObjectProxy, IDoomModule, IDoomMapListener {
	// return 0 if there is no C++ pointer needed to delete
	public long getPointer() {
		return 0L;
	}

	public boolean isMapSpecific() {
		return false;
	}

	public String toString() {
		return "cycaiml";
	}

	public CycAIMLModule() throws NameAlreadyBoundException {
		super("cycaiml");
		cycaiml = this;
	}

	static CycAIMLModule cycaiml;

	static CycAIMLModule currentInstance() throws NameAlreadyBoundException {
		if (cycaiml == null) {
			cycaiml = new CycAIMLModule();
		}
		return cycaiml;
	}

	public void run() {
	}

	static ChatterBean bot;

	// Doom calls this while loading before the map is loaded
	public void initializeModule(IGameLocal shell, Object[] config) throws RemoteException{
		// Print debug message in doom
		debugln("Loading the CycAIMLModule");
		// Register this into doom engine to be in the scripting bean shell
		// objects as "cycaiml"
		try {
			shell.set("cycaiml", this);
		} catch (RemoteException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			throw new RuntimeException(e);
			} catch (Throwable e) {
			throw new RuntimeException(e);
			
		}
		// Register this into doom engine to be called when someone types
		// "testaiml"
		shell.addPlugin(toString(), (IDoomModule) this);
		shell.addCommand("testaiml", this);
		shell.addCommand("aiml", this);
		shell.addCommand("reloadaiml", this);
		bot = new ChatterBean(file);
		shell.set("chatbot", bot);
		// String file = (args.length > 0 ? args[0] :
		// "chatterbean.properties.xml");
	}

	public static String file = "chatterbean.properties.xml";

	// Doom calls this after the map was unloaded and the server is about to be
	// shutdown
	public void removeModule() {
	}

	// Doom calls this when the map is first loaded
	public void onMapLoad() {
		// TODO Initialize the AIML proccesses
	}

	// Doom calls this when before the map is unloaded (and sometimes before a
	// map is loaded)
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

	public ChatterBean getChatterBean() {
		return bot;
	}

	public void reload() {
		bot = new ChatterBean(file);
	}

	// Doom Map Events
	public Serializable invokeMethod(String cmd, Object[] cmdArgs)
			throws NoSuchMethodException {
		// debugln("CycAIMLModule Received: " + IdGameLocal.joinString(cmdArgs,
		// " "));
		// 
		if (cmd.equalsIgnoreCase("testaiml")) {
			reload();
			return new Boolean(true);
		}
		if (cmd.equalsIgnoreCase("event")) {
			if (("" + cmdArgs[0]).equalsIgnoreCase("say")) {
				String speaker = "" + cmdArgs[1];
				String textSaid = "" + cmdArgs[2];
				heardInDoom(speaker, textSaid);
				// this module handled the event?
				return textSaid;
			}
		}
		// otherwise no
		return RemoteDoomModule.eventMissing(this,cmd, cmdArgs);
	}

	// Handle the request
	static public void heardInDoom(String speaker, String textSaid) {
		if (speaker.equalsIgnoreCase(myName))
			return; // do not respond to
		// self
		String response = generateResponse(speaker, textSaid);
		if (response.equals(textSaid)) {
			return;
			// throw new RuntimeException("respose=textSaid");
		}
		sayInDoom(response);
	}

	static String myName = "cyc_bot_1";

	static public void sayInDoom(String textSaid) {
		try {
			getGameLocal().invokeDoomObject("sys", "sayFrom",
					new Serializable[] { myName, textSaid });
		} catch (RemoteException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			throw new RuntimeException(e);
		} catch (NoSuchMethodException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			throw new RuntimeException(e);
		} catch (Throwable e) {
			throw new RuntimeException(e);
		}
	}

	// respond is here so it can be called by programmatic things later like the
	// scripting engine: print cycaiml.generateResponse("player1","Hello");
	static public String generateResponse(String speaker, String textSaid) {
		debugln("CycAIMLModule heardInDoom in: " + speaker + " \"" + textSaid
				+ "\"");
		String botresponse = bot.respond(textSaid);
		return botresponse;
	}
}
