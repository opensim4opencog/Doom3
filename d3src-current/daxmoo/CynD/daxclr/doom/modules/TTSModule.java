package daxclr.doom.modules;

import java.io.Serializable;
import java.rmi.RemoteException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Properties;

import javax.naming.NameAlreadyBoundException;

import org.apache.bsf.BSFException;

import bsh.NameSource.Listener;
import daxclr.bsf.ConsoleChannel;
import daxclr.doom.IGameLocal;

/**
 * Summary description for TTSModule. in doom use "jload aimltest
 * daxclr.ext.TTSModule"
 */
public class TTSModule extends RemoteDoomModule implements
		daxclr.bsf.IScriptMethodHandler, IDoomModule, IDoomMapListener {
	static public String DEFAULT_VOICE = "Microsoft Sam";
	static public TokensTTS ttsmod = null;

	static public void showSystemProps() {
		Properties sp = System.getProperties();
		Iterator it = sp.entrySet().iterator();
		while (it.hasNext()) {
			Map.Entry prop = (Map.Entry) it.next();
			debugln("" + prop.getKey() + "==" + prop.getValue());
		}
	}
	public void run() {		
	}

	public String toString() {
		return "ttsmod";
	}

	public TTSModule() throws NameAlreadyBoundException {
		super("ttsmodule");
		if (ttsmod == null) {
			ttsmod = new daxclr.doom.modules.TokensTTS();
		}
	}

	// Doom calls this while loading before the map is loaded
	public void initializeModule(IGameLocal shell, Object[] config) throws RemoteException {
		// String file = (args.length > 0 ? args[0] :
		// "daxclr.ext.QuadmoreTTS.properties.xml");
		if (ttsmod == null) {
			ttsmod = new daxclr.doom.modules.TokensTTS();
		}
		debugln(ttsmod.getVoiceToken());
		shell.set("ttsmod", ttsmod);
		shell.addCommand("speak", this);
		shell.addCommand("voice", this);
		shell.addCommand("showjava", this);
	}

	// Doom calls this after the map was unloaded and the server is about to be
	// shutdown
	public void removeModule() {}

	// Doom calls this when the map is first loaded
	public void onMapLoad() {
	// TODO Initialize the AIML proccesses
	}

	// Doom calls this when before the map is unloaded (and sometimes before a
	// map is loaded)
	public void onMapUnload() {}

	/**
	 * just makes printing easier
	 */
	static public void println(String s) {
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
	public Serializable invokeMethod(String cmd, Object[] cmdArgs)
			throws NoSuchMethodException {
		String debug = "TTSModule Received: " + cmd + " "
				+ ConsoleChannel.joinString(cmdArgs, " ");
		debugln(debug);
		if (cmd.equalsIgnoreCase("showjava")) {
			showSystemProps();
			return  cmd;
		}
		if (cmd.equalsIgnoreCase("speak")) {
			heardInDoom("cyc_bot_1", ConsoleChannel.joinString(cmdArgs, " "));
			return  cmd;
		}
		if (cmd.equalsIgnoreCase("voice")) {
			if (cmdArgs.length < 2) {
				debugln("use: voice cyc_bot_1 Microsoft Sam");
			} else {
				setVoiceForName("" + cmdArgs[0], daxclr.bsf.ConsoleChannel.joinString(
						cmdArgs, " ", 1, -1));
				debugln("TTSModule set voice for '" + cmdArgs[0] + "' as '"
						+ ConsoleChannel.joinString(cmdArgs, " ", 1, -1) + "'");
			}
			return  cmd;
		}

		if (cmd.equalsIgnoreCase("event")) {
			if (("" + cmdArgs[1]).equalsIgnoreCase("say")
					|| ("" + cmdArgs[1]).equalsIgnoreCase("say")) {
				debugln("TTSModule Received: "
						+ daxclr.bsf.ConsoleChannel.joinString(cmdArgs, " "));
				String speaker = "" + cmdArgs[2];
				String textSaid = ConsoleChannel.joinString(cmdArgs, " ", 3, -1);
				heardInDoom(speaker, textSaid);
				// this module handled the event?
				return  cmd;
			}
		}
		// otherwise no
		return eventMissing(this, cmd, cmdArgs);
	}

	// Handle the request
	static public void heardInDoom(String speaker, String textSaid) {
		if (speaker.equalsIgnoreCase("Player")
				|| speaker.equalsIgnoreCase("Player1")) return;
		sayFor(speaker, textSaid);
	}

	static public Map<String, String> nameToVoice = new HashMap<String, String>(
			10);

	static public void setVoiceForName(String who, String voice) {
		nameToVoice.put(who, voice);
	}

	static public String getVoiceForName(String who) {
		String voice = nameToVoice.get(who);
		if (voice == null) {
			return DEFAULT_VOICE;
		} else {
			return voice;
		}
	}

	static public void sayFor(String who, String textSaid) {
		QuadmoreTTS tts = new daxclr.doom.modules.QuadmoreTTS();
		tts.setVoiceToken(getVoiceForName(who));
		tts.SpeakDarling(textSaid.replace("#$", "").replace("?REPLY", "")
				.replace("(", " ").replace(")", " ").replace(".", " ").replace(
						"  ", " "));
	}

	public void addNameSourceListener(Listener listener) {
	// TODO Auto-generated method stub
	}

	public String[] getAllNames() {
		return new String[]
			{ "speak", "void", "showjava" };
	}

	@Override
	public boolean isMapSpecific() {
		// TODO Auto-generated method stub
		return false;
	}
	public ClassLoader getContextClassLoader() {
		// TODO Auto-generated method stub
		return null;
	}
}
