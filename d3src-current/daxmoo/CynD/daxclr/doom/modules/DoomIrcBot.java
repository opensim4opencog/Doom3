package daxclr.doom.modules;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.io.Serializable;
import java.io.StringReader;
import java.net.ServerSocket;
import java.net.Socket;
import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import javax.naming.NameAlreadyBoundException;

import org.opencyc.cycobject.CycConstant;
import org.opencyc.cycobject.CycFort;
import org.opencyc.cycobject.CycList;
import org.opencyc.cycobject.CycNart;
import org.opencyc.cycobject.CycSymbol;
import org.opencyc.cycobject.CycVariable;
import org.opencyc.util.Log;

import daxclr.bsf.ConsoleChannel;
import daxclr.bsf.IScriptObjectProxy;
import daxclr.doom.IGameLocal;
import daxclr.inference.CycAPI;
import daxclr.inference.LispReader;
import daxclr.inference.PrologAPI;

public class DoomIrcBot extends RemoteDoomModule implements IDoomModule,
		IScriptObjectProxy {
	// return 0L if there is no C++ pointer needed to delete
	public long getPointer() {
		return 0L;
	}

	public boolean isMapSpecific() {
		return false;
	}

	/*
	 * public String toString() { return ircNick + "@" + ircServer; }
	 */

	public Serializable invokeMethod(String cmd, Object[] cmdArgs)
			throws NoSuchMethodException {
		if (cmd.equalsIgnoreCase("irc") || cmd.equalsIgnoreCase(ircNick)) {
			try {
				String params = "";
				for (int i = 1; i < cmdArgs.length; i++) {
					params = params.concat(" ").concat("" + cmdArgs[i]);
				}
				return onPublicMessage("invokeScript", "", "#logicmoo", params);
			} catch (Exception e) {
			}
		}
		return false;
	}

	String lastDest;

	public OutputStream getOutputStream() {
		return getOutputStream(lastDest);
	}

	public OutputStream getOutputStream(String channel) {
		return new IRCOutputStream(channel, this);
	}

	static public class IRCOutputStream extends OutputStream {
		// Appendable buffer = new StringBuffer(100);
		public StringBuffer buffer = new StringBuffer(100);

		public String channel = "#logicmoo";

		public DoomIrcBot bot;

		public IRCOutputStream(String chan, DoomIrcBot abot) {
			channel = chan; // buffer = new StringBuffer(100);
			bot = abot;
		}

		public StringBuffer getBuffer() {
			return buffer;
		}

		public void clear() {
			buffer = new StringBuffer(100);
		}

		public void write(int c) throws IOException {
			if (c == 10) {
				flush();
			} else {
				if (buffer == null) {
					buffer = new StringBuffer(100);
				}
				buffer.append((char) c);
			}
		}

		public void finalize() {
			if (buffer != null) {
				flush();
			}
			buffer = null;
		}

		public void close() {
			flush();
		}

		public void flush() {
			if (buffer != null) {
				bot.privmsg(channel, ((String) buffer.toString())); // System.out
			}
			buffer = new StringBuffer(100);
		}
	}

	static public DoomIrcBot makeNewBot(String botname, String servname)
			throws NameAlreadyBoundException {
		DoomIrcBot aBot = new DoomIrcBot();
		aBot.ircNick = botname;
		aBot.ircServer = servname;
		aBot.ircPort = 6667;
		// aBot.LocalClientHolder = LocalClientHolder;
		// set(aBot.ircNick,aBot);
		return aBot;
	}

	/**
	 * IRC Bot details
	 */
	// Name Bot goes by on IRC
	public String ircNick = "aBot_Of_Doom";

	// WHOIS Information
	public String ircComment = "class daxclr.modules.DoomIrcBot";

	// IRC Auto-join
	public String ircChannel = "#logicmoo";

	/**
	 * IRC Sever details
	 */
	public String ircServer =
	// "sw2.de.quakenet.org";
	// "Enterprise.NJ.US.StarChat.Net";
	"irc.freenode.net";

	public int ircPort = 6667;

	// IRC Unkown message replies sentence to
	public String ircDestination = "#gamedevelopers";// "#logicmoo";

	// ArrayList of paraphrased writable locations
	public ArrayList<String> paraphrased = new ArrayList<String>();

	// IRC Debug messages sentence to ( may send to an IRC username instead of
	// channel )
	public String ircDebug = "dmiles_afk";

	/**
	 * IRC Server comunication
	 */
	private Socket ircServerSocket = null;

	private BufferedReader ircInputReader = null;

	private BufferedWriter ircOutputWriter = null;

	/**
	 * Telent DCC Chat Server
	 */
	// public DccServerThread dccServer = null;
	/**
	 * reference to ChatterBot
	 */
	// public org.opencyc.chat.ChatterBot chatterBot = null;
	public boolean running = false;

	/**
	 * Creates a basic unstarted IRC Bot
	 * 
	 * @throws NameAlreadyBoundException
	 */
	public DoomIrcBot() throws NameAlreadyBoundException {
		super("ircbot");
	}

	// CycLBot daxclr.doom.agent.DoomIrcBot doomStart CycLBot irc.freenode.net
	// #logicmoo
	// CycLBot daxclr.modules.DoomIrcBot CycLBot kornbluth.freenode.net
	// #logicmoo
	public void initializeModule(IGameLocal shell, Object[] config)
			throws RemoteException {
		shell.addCommand("irc", this);
		ircNick = config[0].toString();
		// ircNick = config[1].toString();
		if (config.length > 2) {
			ircNick = config[2].toString();
		}
		if (config.length > 3) {
			ircServer = config[3].toString();
		}
		if (config.length > 4) {
			ircChannel = config[4].toString();
		}
		if (config.length > 5) {
			ircChannel = config[4].toString() + " " + config[5].toString();
		}
		shell.addCommand(ircNick, this);
		start();
	}

	/**
	 * Creates a full running IRC Bot
	 * 
	 * @param access
	 * @param nick
	 * @param comment
	 * @param server
	 * @param port
	 * @param channel
	 * @throws NameAlreadyBoundException
	 */
	public DoomIrcBot(CycAPI access, String nick, String comment,
			String server, int port, String channel, boolean strt)
			throws NameAlreadyBoundException {
		this();
		// set the bot's nickname and description
		ircNick = nick;
		ircComment = comment;
		ircServer = server;
		ircPort = port;
		ircChannel = channel;
		start();
	}

	public PrologAPI getProlog() {
		PrologAPI prologapi = (PrologAPI) getRepository().get("prologapi");
		if (prologapi == null) {
			try {
				prologapi = PrologAPI.current();
				getGameLocal().set("prologapi", prologapi);
			} catch (Exception ee) {
				debug(ee);
			}
		}
		return prologapi;
	}

	/**
	 * Provide a command line function to launch the DoomIrcBot application.
	 */
	public static void main(String[] args) {
		try {
			debug("Lauching DoomIrcBot");
			DoomIrcBot ircBot = new DoomIrcBot();
			if (args.length > 0) {
				ircBot.ircNick = args[0];
			}
			if (args.length > 1) {
				ircBot.ircChannel = args[1];
			}
			if (args.length > 2) {
				ircBot.ircServer = args[2];
			}
			if (args.length > 3) {
				ircBot.ircPort = Integer.parseInt((args[3]));
			}
			debug("Lauching DoomIrcBot: n\nick='" + ircBot.ircNick
					+ "' \nchannel='" + ircBot.ircChannel + "' \nserver='"
					+ ircBot.ircServer + ":'" + ircBot.ircPort + "");
			ircBot.run();
			// System.exit(0);
		} catch (Exception e) {
			e.printStackTrace(System.err);
			// System.exit(1);
		}
	}

	public synchronized boolean isConnected() {
		if (ircServerSocket == null)
			return false;
		try {
			if (ircServerSocket.isClosed()) {
				ircShutdown();
				return false;
			}
			if (!ircServerSocket.isConnected()) {
				ircShutdown();
				return false;
			}
			ircOutputWriter = new BufferedWriter(new OutputStreamWriter(
					ircServerSocket.getOutputStream()));
			ircInputReader = new BufferedReader(new InputStreamReader(
					ircServerSocket.getInputStream()));
			ircInputReader.ready();
			return true;
		} catch (IOException io) {
			debug(io);
			ircShutdown();
			return false;
		}
	}

	public synchronized void ircShutdown() {
		if (ircInputReader != null) {
			try {
				ircInputReader.close();
			} catch (IOException e) {
			}
			ircInputReader = null;
		}
		if (ircOutputWriter != null) {
			try {
				ircOutputWriter.close();
			} catch (IOException e) {
			}
			ircOutputWriter = null;
		}
		if (ircServerSocket != null) {
			try {
				ircServerSocket.close();
			} catch (IOException e) {
			}
			ircServerSocket = null;
		}
	}

	/**
	 * Disconnct Bot from an IRC server
	 */
	public void ircDisconnect() {
		if (ircServerSocket != null) {
			try {
				// chatterBot.finalize();
				// chatterBot = null;
				ircOutputWriter.write("QUIT this.ircDisconnect();");
				ircOutputWriter.newLine();
				ircOutputWriter.flush();
			} catch (Exception e) {
				debug("ircLogOff error: " + e);
				debug(e);
			}
			// close the IO streams to the IRC server
			try {
				ircInputReader.close();
				ircOutputWriter.close();
				ircInputReader = null;
				ircOutputWriter = null;
				ircServerSocket.close();
				ircServerSocket = null;
			} catch (IOException e) {
				debug("Error ircDisconnecting from IRC server");
				debug(e);
			}
		}
		ircShutdown();
	}

	static {
		if (Log.current == null) {
			try {
				Log.makeLog();
			} catch (Throwable e) {
			}
		}
	}

	/**
	 * Connects Bot to an IRC server
	 */
	public synchronized void ensureConnected() {
		while (!isConnected()) {
			ircShutdown();
			ConsoleChannel.waitmsecs(2000);
			try {
				if (ircServerSocket != null)
					ircServerSocket.close();
				ircServerSocket = new Socket(ircServer, ircPort);
			} catch (Exception e) {
				debug("IRC Error Connecting to " + ircServer + ":" + ircPort
						+ " " + e);
			}
			try {
				if (isConnected()) {
					// send user info
					ConsoleChannel.waitmsecs(3000);
					ircOutputWriter.write("USER " + ircNick + " 0 * :"
							+ ircComment);
					ircOutputWriter.newLine();
					ConsoleChannel.waitmsecs(1000);
					ircOutputWriter.write("NICK " + ircNick);
					ircOutputWriter.newLine();
					ircOutputWriter.flush();
				}
				onLogonComplete();
				// } catch ( Exception ee ) {
				// debugln("ircLogOn error: " + ee);
				// }
				// } catch (NullPointerException e) {
			} catch (Exception e) {
				debug("IRC Stream Error to " + ircServer + ":" + ircPort + " "
						+ e);
			}
		}
	}

	public void run() {
		running = true;
		boolean interrupted = interrupted();
		while (!interrupted && running) {
			try {
				serviceIRCServer();
			} catch (Exception e) {
				debug(e);
			}
		}
	}

	/**
	 * Sends a raw string to the IRC server
	 */
	public boolean putsrv(String message) {
		ensureConnected();
		debug("irc: '" + message + "'");
		try {
			ircOutputWriter.write(message);
			ircOutputWriter.newLine();
			ircOutputWriter.flush();
		} catch (IOException e) {
			return false;
		}
		return true;
	}

	public void join(String channel) {
		if (channel != null) {
			channel = channel.trim();
			if (!channel.startsWith("#"))
				channel = "#" + channel;
			putsrv("JOIN " + channel);
			lastDest = channel;
		}
	}

	public void part(String channel) {
		if (channel != null) {
			channel = channel.trim();
			if (!channel.startsWith("#"))
				channel = "#" + channel;
			putsrv("PART " + channel);
			lastDest = channel;
		}
	}

	/**
	 * Send a notice to an IRC user
	 * 
	 * @param returnpath
	 *            String
	 * @param message
	 *            String
	 */
	public void notice(String returnpath, String message) {
		putsrv("notice " + returnpath + " :" + message);
	}

	List answers = null;

	/**
	 * Send a public message to an IRC user
	 * 
	 * @param returnpath
	 *            String
	 * @param message
	 *            String
	 */
	public boolean privmsg(String returnpath, Object message) {
		// Wait 2 sec (Keeps from flooding off server)
		if (returnpath == null) {
			return false;
		}
		if (message == null) {
			privmsg(returnpath, "<$null>");
			return true;
		}
		if (message instanceof Object[]) {
			return privmsg(returnpath, new ArrayList(Arrays
					.asList((Object[]) message)));
		}
		if (message instanceof Iterator) {
			while (((Iterator) message).hasNext()) {
				try {
					if (ircInputReader.ready()) {
						if (ircInputReader.readLine().trim().endsWith(".")) {
							return true;
						}
					}
				} catch (Exception e) {
				}
				privmsg(returnpath, ((Iterator) message).next());
			}
			return true;
		}
		if (message instanceof CycSymbol) {
			if (message.equals(SYMBOL_NIL)) {
				return privmsg(returnpath, "NIL");
			}
		}
		if (message instanceof CycList && isParaphrased(returnpath)) {
			return privmsg(returnpath, attemptParaphrase((CycList) message)
					+ " (" + ((CycList) message).toString() + ")");
		}
		if (message instanceof Collection) {
			int retSofar = 0;
			answers = new ArrayList();
			answers.addAll((Collection) message);
			if (answers.size() == 1
					&& answers.get(0).equals(DoomIrcBot.SYMBOL_NIL)) {
				return privmsg(returnpath, "true sentence");
			}
			int osize = answers.size();
			if (osize == 0) {
				return privmsg(returnpath, "()");
			}
			String output = "";
			while (retSofar < answersMax) {
				output = answers.remove(0) + " ";
				while (answers.size() > 0 && output.length() < 120) {
					output += answers.remove(0) + " ";
				}
				privmsg(returnpath, output);
				retSofar++;
				if (answers.size() == 0)
					return true;
			}
			if (answers.size() == 0)
				return true;
			output = answers.toString();
			if (output.length() < 120) {
				return privmsg(returnpath, output);
			}
			privmsg(returnpath, "Returned " + (int) (osize - answers.size())
					+ "/" + osize + " answers (use 'more' command)");
			return false;
		}
		if (message instanceof BufferedReader) {
			String line = null;
			try {
				while ((line = ((BufferedReader) message).readLine()) != null) {
					privmsg(returnpath, line);
				}
			} catch (Exception e) {
				debug(e);
				return false;
			}
			return true;
		}
		// return privmsg(returnpath,answers.toString());
		String smessage = message.toString().trim();
		if (ViolinStrings.Strings.contains(smessage, "\n")
				|| ViolinStrings.Strings.contains(smessage, "\r")) {
			return privmsg(returnpath, new BufferedReader(new StringReader(
					smessage)));
		}
		if (smessage.length() > 200) {
			int justify = smessage.substring(190).indexOf(' ') + 190;
			putsrv("privmsg " + returnpath + " :"
					+ smessage.substring(0, justify - 1));
			return privmsg(returnpath, smessage.substring(justify));
		}
		putsrv("privmsg " + returnpath + " :" + smessage);
		ConsoleChannel.waitmsecs(1500);
		return true;
	}

	public String attemptParaphrase(Object message) {
		// Log.current.println("attemptParaphrase=" + message);
		if (message == null) {
			return null;
		}
		try {
			if (message instanceof Iterator) {
				if (!(((Iterator) message).hasNext())) {
					return "none.";
				}
				StringBuffer sb = new StringBuffer(
						attemptParaphrase(((Iterator) message).next()));
				while (((Iterator) message).hasNext()) {
					sb.append(", ").append(
							attemptParaphrase(((Iterator) message).next()));
				}
				return sb.toString();
			}
			if (message instanceof CycConstant) {
				return ((CycAPI) CycAPI.current())
						.converseString("(generate-phrase "
								+ ((CycConstant) message).stringApiValue()
								+ ")");
			}
			if (message instanceof CycNart) {
				return ((CycAPI) CycAPI.current())
						.converseString("(generate-phrase '"
								+ ((CycNart) message).cyclify() + ")");
			}
			if (message instanceof CycVariable) {
				return (((CycVariable) message).stringApiValue());
			}
			if (message instanceof CycList) {
				if (((CycList) message).isEmpty()) {
					return "an empty list ";
				}
				if (!((CycList) message).isProperList()) {
					// return attemptParaphrase(((CycList)message).first()) + "
					// = " + (((CycList)message).rest());
					return attemptParaphrase(((CycList) message).first())
							+ " = "
							+ attemptParaphrase(((CycList) message).rest());
				}
				if (((CycList) message).first() instanceof CycList) {
					return attemptParaphrase(((CycList) message).iterator());
				}
				return ((CycAPI) CycAPI.current())
						.converseString("(generate-phrase '"
								+ ((CycList) message).cyclify() + ")");
			}
		} catch (Exception e) {
			debug(e);
		}
		return message.toString();
	}

	/**
	 * Receives and parses IRC Server messages
	 */
	public void serviceIRCServer() throws Exception {
		// waitmsecs a 1/10th sec
		ConsoleChannel.waitmsecs(100);
		ensureConnected();
		// Data ?
		if (!ircInputReader.ready()) {
			return;
		}
		try {
			String message; // = ircInputReader.readLine();
			try {
				message = ircInputReader.readLine();
			} catch (Exception ez) {
				ircShutdown();
				// debugln(ez);
				return;
			}
			if (message.substring(0, 4).equalsIgnoreCase("ping")) {
				// send a pong back
				putsrv("PONG" + message.substring(4));
				return;
			}
			onIRCSession(message);
		} catch (Exception e) {
			debug(e);
			ConsoleChannel.waitmsecs(2000);
			return;
		}
	}

	/**
	 * Receives and parses IRC Session messages
	 */
	public void onIRCSession(String message) {
		String prefix = null;
		String command = null;
		String params = null;
		String user = null;
		// check for the prefix
		if (message.substring(0, 1).equalsIgnoreCase(":")) {
			prefix = message.substring(1, message.indexOf(' '));
			message = message.substring(message.indexOf(' ') + 1);
		} else {
			prefix = message;
		}
		// extract the command
		command = message.substring(0, message.indexOf(' '));
		// get the parameters (the rest of the message)
		params = message.substring(message.indexOf(' ') + 1);
		if (params.toLowerCase().startsWith(":closing")) {
			ircShutdown();
			return;
		}
		try {
			int col = params.indexOf(':');
			String returnpath = params.substring(0, col - 1).trim();
			ircDestination = returnpath;
			params = params.substring(col + 1).trim();
			onIRCTransaction(prefix.substring(0, prefix.indexOf('!')), prefix,
					command, returnpath, params);
		} catch (Exception e) {
		}
	}

	/**
	 * Process an IRC Transaction
	 * 
	 * @param returnpath
	 *            String nickname of the user who sentence the message
	 * @param message
	 *            String the command params are in the form <my nick> :<message>
	 *            or <my nick> <message>
	 */
	public void onIRCTransaction(String from, String hostmask, String command,
			String returnpath, String params) {
		if (hostmask.startsWith(ircNick)) {
			return;
		}
		if (command.equalsIgnoreCase("PRIVMSG")) {
			onPublicMessage(from, hostmask, returnpath, params.trim());
		}
	}

	/**
	 * Process a Public message (PRIVMSG to Channel)
	 * 
	 * @param returnpath
	 *            String nickname of the user who sentence the message
	 * @param message
	 *            String the command params are in the form <my nick> :<message>
	 *            or <my nick> <message>
	 */
	public boolean onPublicMessage(String from, String hostmask,
			String returnpath, String params) {
		if (!returnpath.startsWith("#")) {
			returnpath = from;
		}
		String lcparams = params.toLowerCase().trim();
		int ccol = params.indexOf(':');
		if (ccol < 0) {
			ccol = params.indexOf(' ');
		}
		lastDest = returnpath;
		if (ccol > 1) {
			String token = lcparams.substring(0, ccol).trim();
			params = params.substring(ccol + 1).trim();
			if (onToken(from, hostmask, returnpath, token, params)) {
				return true;
			}
		} else {
			if (onToken(from, hostmask, returnpath, lcparams, params)) {
				return true;
			}
		}
		onChatter(from, hostmask, params, returnpath);
		return false;
	}

	public static boolean emptyString(String str) {
		if (str == null || str.trim().length() == 0)
			return true;
		return false;
	}

	/**
	 * Process a Token message
	 * 
	 * @param returnpath
	 *            String nickname of the user who sentence the message
	 * @param message
	 *            String the command params are in the form <my nick> :<message>
	 *            or <my nick> <message>
	 */
	public boolean onToken(String from, String hostmask, String returnpath,
			String token, String params) {
		if (emptyString(token)) {
			if (emptyString(params))
				return false;
			return onPublicMessage(from, hostmask, returnpath, params);
		}
		lastDest = returnpath;
		if (params.toLowerCase().startsWith(token.toLowerCase()))
			params = params.substring(token.length() + 1).trim();
		debug("token: '" + token + "' params: '" + params + "'");
		if (token.equalsIgnoreCase("hello")) {
			privmsg(returnpath, "hello " + from);
			return true;
		}
		if (token.equalsIgnoreCase("time")) {
			privmsg(returnpath, "the time was " + (new Date()).toString());
			return true;
		}
		if (token.equalsIgnoreCase("bsh")) {
			onBeanShell(from, returnpath, params);
			return true;
		}
		if (token.equalsIgnoreCase("more")) {
			privmsg(returnpath, (Object) answers);
			return true;
		}
		if (token.equalsIgnoreCase("help")) {
			sendHelp(returnpath, params);
			return true;
		}
		if (token.equalsIgnoreCase("echo")) {
			privmsg(returnpath, params);
			return true;
		}
		if (token.equalsIgnoreCase("cyclify")) {
			privmsg(returnpath, toCycListString(params));
			return true;
		}
		if (token.equalsIgnoreCase("paraphrase")) {
			if (params.startsWith("#")) {
				paraphrased.add(params);
				return true;
			}
			privmsg(returnpath, attemptParaphrase(toCycList(params)));
			return true;
		}
		if (token.equalsIgnoreCase("noparaphrase")) {
			paraphrased.remove(params);
			return true;
		}
		if (token.equalsIgnoreCase("subl")) {
			onSubL(from, returnpath, params);
			return true;
		}
		if (token.equalsIgnoreCase("swi")) {
			onSWI(from, returnpath, params);
			return true;
		}
		if (token.equalsIgnoreCase("console")) {
			try {
				privmsg(returnpath, getGameLocal().invokeDoomConsole(params));
			} catch (RemoteException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				throw new RuntimeException(e);
			} catch (Throwable e) {
				throw new RuntimeException(e);

			}
			return true;
		}
		if (token.equalsIgnoreCase("jinni")) {
			onProlog(from, returnpath, params);
			return true;
		}
		if (token.equalsIgnoreCase("debug")) {
			ircDebug = params;
			return true;
		}
		if (token.equalsIgnoreCase("ask")) {
			onQuery(from, returnpath, params);
			return true;
		}
		if (token.equalsIgnoreCase("aiml")) {
			privmsg(returnpath, CycAIMLModule.generateResponse(from, params));
			return true;
		}
		if (token.equalsIgnoreCase("mt")) {
			try {
				if (emptyString(params)) {
					privmsg(returnpath, mtUser.get(from));
				} else {
					CycConstant mt = CycAPI.current().makeCycConstant(params);
					CycAPI.current().assertIsa(mt,
							CycAPI.current().makeCycConstant("#$Microtheory"),
							CycAPI.current().baseKB);
					mtUser.put(from, mt);
				}
			} catch (Exception e) {
			}
			return true;
		}
		if (token.equalsIgnoreCase("prove")) {
			onProve(from, returnpath, params);
			return true;
		}
		if (token.equalsIgnoreCase("query")) {
			onQueryUser(from, returnpath, params);
			return true;
		}
		if (token.equalsIgnoreCase("assert")) {
			onAssert(from, returnpath, params);
			return true;
		}
		if (token.equalsIgnoreCase("join")) {
			join(params);
			return true;
		}
		if (token.startsWith("part")) {
			part(params);
			return true;
		}
		if (token.startsWith("putserv")) {
			putsrv(params);
			return true;
		}
		if (token.equalsIgnoreCase("cmd")) {
			try {
				privmsg(returnpath, getGameLocal().invokeCommand(
						params.split(" ")));
			} catch (NoSuchMethodException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (RemoteException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				throw new RuntimeException(e);
			} catch (Throwable e) {
				throw new RuntimeException(e);

			}
			return true;
		}
		return false;
	}

	// [04:26] -brown.freenode.net- *** Banned: micah; Please don't run clone
	// bots on freenode. If you were k-lined by mistake, please mail
	// staff@freenode.net
	public void onLogonComplete() {
		ConsoleChannel.waitmsecs(1000);
		if (ircDebug != null && ircDebug.startsWith("#"))
			join(ircDebug);
		ConsoleChannel.waitmsecs(5000);
		if (ircChannel != null)
			join(ircChannel);
		// paraphrased.add(ircChannel);
		this.running = true;
	}

	public void addObj(String name, Object val) {
		try {
			getGameLocal().set(name, val);
		} catch (Exception e) {
		}
	}

	public void sendHelp(String returnpath, String params) {
		privmsg(returnpath,
				"commands: <bsh|paraphrase|hello|time|join|part|ask|query|assert|cyclify>");
	}

	/**
	 * Process a SubL command
	 * 
	 * @param returnpath
	 *            String nickname of the user who sent the message
	 * @param message
	 *            String the command params are in the form <my nick> :<message>
	 *            or <my nick> <message>
	 */
	public void onBeanShell(String cyclist, String returnpath, String params) {
		try {
			debug("onBeanShell " + " " + cyclist + " " + returnpath + " "
					+ params);
			;// ScriptManager.set("client", this);
		} catch (Exception e) {
		}
		try {
			// addObj("client",this);
			privmsg(returnpath, getGameLocal().eval(params));
		} catch (Throwable e) {
			debug(e);
			privmsg(returnpath, "" + e);

		}
	}

	/**
	 * Process a SubL command returnpath String nickname of the user who sent
	 * the message
	 * 
	 * @param cyclist
	 *            String nickname of the user who sentence the message
	 * @param returnpath
	 *            String the returnpath to print results
	 * @param subl
	 */
	public void onSubL(String cyclist, String returnpath, String subl) {
		try {
			privmsg(returnpath, CycAPI.current().converseObject(subl));
		} catch (Exception e) {
			privmsg(returnpath, "" + e /*
										 * + "\" " + " trying to eval \"" + subl +
										 * "\" " + "from \"" + cyclist + "\"
										 * with returnpath \""+returnpath + "\""
										 */);
		}
	}

	/**
	 * Process a SubL command returnpath String nickname of the user who sent
	 * the message
	 * 
	 * @param cyclist
	 *            String nickname of the user who sentence the message
	 * @param returnpath
	 *            String the returnpath to print results
	 * @param subl
	 */
	public void onProlog(String cyclist, String returnpath, String subl) {
		try {
			privmsg(returnpath, getProlog().evalStringInProlog(subl));
		} catch (Exception e) {
			privmsg(returnpath, "" + e /*
										 * + "\" " + " trying to eval \"" + subl +
										 * "\" " + "from \"" + cyclist + "\"
										 * with returnpath \""+returnpath + "\""
										 */);
		}
	}

	public void onSWI(String cyclist, String returnpath, String subl) {
		try {
			privmsg(returnpath, getProlog().evalStringInProlog(subl));
		} catch (Exception e) {
			privmsg(returnpath, "" + e /*
										 * + "\" " + " trying to eval \"" + subl +
										 * "\" " + "from \"" + cyclist + "\"
										 * with returnpath \""+returnpath + "\""
										 */);
		}
	}

	/**
	 * Process a Query command params are in the form <my nick> :<message> or
	 * <my nick> <message>
	 * 
	 * @param cyclist
	 *            String nickname of the user who sentence the message
	 * @param returnpath
	 *            String the returnpath to print results
	 * @param query
	 */
	public void onQuery(String cyclist, String returnpath, String query) {
		try {
			privmsg(returnpath, CycAPI.current().converseObject(
					"(cyc-query '" + toCycListString(query)
							+ " #$DoomCurrentStateMt)"));
		} catch (Exception e) {
			privmsg(returnpath, "" + e);
		}
	}

	/**
	 * Process a Prove command (Query with proof)
	 * 
	 * @param returnpath
	 *            String nickname of the user who sent the message
	 * @param message
	 *            String the command params are in the form <my nick> :<message>
	 *            or <my nick> <message>
	 */
	public void onProve(String cyclist, String returnpath, String query) {
		try {
			privmsg(returnpath, CycAPI.current()
					.converseObject(
							"(fi-prove '" + toCycListString(query)
									+ " #$InferencePSC)"));
		} catch (Exception e) {
			privmsg(returnpath, "" + e);
		}
	}

	/**
	 * Process an Ask command
	 * 
	 * @param returnpath
	 *            String nickname of the user who sentence the message
	 * @param message
	 *            String the command params are in the form <my nick> :<message>
	 *            or <my nick> <message>
	 */
	public void onQueryUser(String cyclist, String returnpath, String query) {
		try {
			privmsg(returnpath, CycAPI.current().converseObject(
					"(cyc-query '" + toCycListString(query) + " "
							+ mtForUser(cyclist).stringApiValue() + ")"));
		} catch (Exception e) {
			privmsg(returnpath, "" + e);
		}
	}

	/**
	 * Process an Assert command
	 * 
	 * @param returnpath
	 *            String nickname of the user who sentence the message
	 * @param message
	 *            String the command params are in the form assert:<message> or
	 *            <my nick> <message>
	 */
	public void onAssert(String cyclist, String returnpath, String sentence) {
		onSubL(cyclist, returnpath, "(cyc-assert '" + toCycListString(sentence)
				+ " " + mtForUser(cyclist).stringApiValue() + ")");
	}

	/**
	 * Process an Assert command
	 * 
	 * @param returnpath
	 *            String nickname of the user who sentence the message
	 * @param message
	 *            String the command params are in the form assert:<message> or
	 *            <my nick> <message>
	 */
	public void onChatter(String cyclist, String identity, String message,
			String returnpath) {
		ircDestination = returnpath;
		return;
		// try {
		// if ( chatterBot!=null ) chatterBot.receiveChatMessage(cyclist,
		// cyclist /*identity hostmask?*/,message);
		// } catch ( Exception e ) {
		// e.printStackTrace(System.out);
		// sendDebug(""+e);
		// }
	}

	public HashMap<String, CycConstant> mtUser = new HashMap<String, CycConstant>();

	/**
	 * Returns a Mt for a user
	 */
	public CycFort mtForUser(String cyclist) {
		CycConstant mt = mtUser.get(cyclist);
		if (mt == null) {
			try {
				mt = CycAPI.current()
						.makeCycConstant("#$" + cyclist + "ChatMt");
				CycAPI.current().assertIsa(mt,
						CycAPI.current().makeCycConstant("#$Microtheory"),
						CycAPI.current().baseKB);
			} catch (Exception e) {
				// mt = CycAPI.current().baseKB;
			}
			privmsg(ircDestination, "Using microtheory: " + mt.cyclify());
			privmsg(ircDestination, " for assertions until " + cyclist
					+ " types \"mt <something>\"");
			mtUser.put(cyclist, mt);
		}
		return (CycFort) mt;
	}

	/**
	 * Returns true if Paraphrased for a returnpath/returnpath
	 */
	public boolean isParaphrased(String returnpath) {
		return (paraphrased.contains(returnpath));
	}

	/**
	 * Cyclifys a sentence a string
	 */
	public CycList toCycList(String sentence) {
		try {
			return (((CycList) ((new LispReader(CycAPI.current()))
					.read(sentence))));
		} catch (Exception e) {
			return null;
		}
	}

	/**
	 * Cyclifys a sentence to a string
	 */
	public String toCycListString(String sentence) {
		try {
			return (((CycList) ((new LispReader(CycAPI.current()))
					.read(sentence))).cyclify());
		} catch (Exception e) {
			return null;
		}
	}

	public static CycSymbol SYMBOL_NIL = new CycSymbol("NIL");

	public static int answersMax = 7;

	public static int answersMin = 7;

	public void sendDebug(String message) {
		privmsg(ircDebug, message);
	}

	/**
	 * Sends the chat message from Cyc into the chat system.
	 */
	public void sendChatMessage(String chatMessage) {
		// privmsg(ircDestination,chatMessage);
	}

	/**
	 * Receives chat messages from the user.
	 */
	private String receiveChatMessage() throws IOException {
		System.out.print("user> ");
		return "i can see you";
	}

	public void recievedConsoleMsg(DccClientHandlerThread client, String message) {
		System.out.print("recievedConsoleMsg " + client + ": " + message);
	}

	public void listenForConnections(int port) {
		try {
			// dccServer = new DccServerThread(this,port);
			// dccServer.start();
		} catch (Exception e) {
		}
	}

	public class DccServerThread extends Thread {
		public boolean listening = true;

		private ServerSocket serverSocket = null;

		private int serverPort = 4444;

		private DoomIrcBot DoomIrcBot = null;

		public HashMap<String, DccClientHandlerThread> clients = null;

		public DccServerThread(DoomIrcBot ircBot, int port) throws IOException {
			DoomIrcBot = ircBot;
			if (port > 1)
				serverPort = port;
			clients = new HashMap<String, DccClientHandlerThread>();
			serverSocket = new ServerSocket(serverPort);
			serverPort = serverSocket.getLocalPort();
			this.start();
		}

		public void run() {
			try {
				while (listening) {
					Socket thisClient = serverSocket.accept();
					String username = getLogin(thisClient);
					DccClientHandlerThread clientThread = new DccClientHandlerThread(
							DoomIrcBot, thisClient);
					clientThread.run();
					clients.put(username, clientThread);
				}
				serverSocket.close();
			} catch (Exception e) {
			}
		}

		public String getLogin(Socket thisClient) {
			return thisClient.toString();
		}
	}

	public class DccClientHandlerThread extends Thread {
		private Socket socket = null;

		private DoomIrcBot DoomIrcBot = null;

		private PrintWriter out = null;

		private BufferedReader in = null;

		public DccClientHandlerThread(DoomIrcBot ircBot, Socket socket) {
			super("DccClientThread");
			this.socket = socket;
		}

		public void println(String message) {
			out.println(message);
		}

		public void run() {
			String inputLine = null;
			try {
				out = new PrintWriter(socket.getOutputStream(), true);
				in = new BufferedReader(new InputStreamReader(socket
						.getInputStream()));
				while ((inputLine = in.readLine()) != null) {
					DoomIrcBot.recievedConsoleMsg(this, inputLine);
				}
			} catch (IOException e) {
				debug(e);
			}
		}

		public void disconnect() {
			try {
				out.close();
				in.close();
				socket.close();
			} catch (IOException e) {
				debug(e);
			}
		}
	}

	/**
	 * @return the answersMax
	 */
	public static int getAnswersMax() {
		return answersMax;
	}

	/**
	 * @param answersMax
	 *            the answersMax to set
	 */
	public static void setAnswersMax(int answersMax) {
		DoomIrcBot.answersMax = answersMax;
	}

	/**
	 * @return the answersMin
	 */
	public static int getAnswersMin() {
		return answersMin;
	}

	/**
	 * @param answersMin
	 *            the answersMin to set
	 */
	public static void setAnswersMin(int answersMin) {
		DoomIrcBot.answersMin = answersMin;
	}

	/**
	 * @return the sYMBOL_NIL
	 */
	public static CycSymbol getSYMBOL_NIL() {
		return SYMBOL_NIL;
	}

	/**
	 * @param symbol_nil
	 *            the sYMBOL_NIL to set
	 */
	public static void setSYMBOL_NIL(CycSymbol symbol_nil) {
		SYMBOL_NIL = symbol_nil;
	}

	/**
	 * @return the answers
	 */
	public List getAnswers() {
		return this.answers;
	}

	/**
	 * @param answers
	 *            the answers to set
	 */
	public void setAnswers(List answers) {
		this.answers = answers;
	}

	/**
	 * @return the ircChannel
	 */
	public String getIrcChannel() {
		return this.ircChannel;
	}

	/**
	 * @param ircChannel
	 *            the ircChannel to set
	 */
	public void setIrcChannel(String ircChannel) {
		this.ircChannel = ircChannel;
	}

	/**
	 * @return the ircComment
	 */
	public String getIrcComment() {
		return this.ircComment;
	}

	/**
	 * @param ircComment
	 *            the ircComment to set
	 */
	public void setIrcComment(String ircComment) {
		this.ircComment = ircComment;
	}

	/**
	 * @return the ircDebug
	 */
	public String getIrcDebug() {
		return this.ircDebug;
	}

	/**
	 * @param ircDebug
	 *            the ircDebug to set
	 */
	public void setIrcDebug(String ircDebug) {
		this.ircDebug = ircDebug;
	}

	/**
	 * @return the ircDestination
	 */
	public String getIrcDestination() {
		return this.ircDestination;
	}

	/**
	 * @param ircDestination
	 *            the ircDestination to set
	 */
	public void setIrcDestination(String ircDestination) {
		this.ircDestination = ircDestination;
	}

	/**
	 * @return the ircInputReader
	 */
	public BufferedReader getIrcInputReader() {
		return this.ircInputReader;
	}

	/**
	 * @param ircInputReader
	 *            the ircInputReader to set
	 */
	public void setIrcInputReader(BufferedReader ircInputReader) {
		this.ircInputReader = ircInputReader;
	}

	/**
	 * @return the ircNick
	 */
	public String getIrcNick() {
		return this.ircNick;
	}

	/**
	 * @param ircNick
	 *            the ircNick to set
	 */
	public void setIrcNick(String ircNick) {
		this.ircNick = ircNick;
	}

	/**
	 * @return the ircOutputWriter
	 */
	public BufferedWriter getIrcOutputWriter() {
		return this.ircOutputWriter;
	}

	/**
	 * @param ircOutputWriter
	 *            the ircOutputWriter to set
	 */
	public void setIrcOutputWriter(BufferedWriter ircOutputWriter) {
		this.ircOutputWriter = ircOutputWriter;
	}

	/**
	 * @return the ircPort
	 */
	public int getIrcPort() {
		return this.ircPort;
	}

	/**
	 * @param ircPort
	 *            the ircPort to set
	 */
	public void setIrcPort(int ircPort) {
		this.ircPort = ircPort;
	}

	/**
	 * @return the ircServer
	 */
	public String getIrcServer() {
		return this.ircServer;
	}

	/**
	 * @param ircServer
	 *            the ircServer to set
	 */
	public void setIrcServer(String ircServer) {
		this.ircServer = ircServer;
	}

	/**
	 * @return the ircServerSocket
	 */
	public Socket getIrcServerSocket() {
		return this.ircServerSocket;
	}

	/**
	 * @param ircServerSocket
	 *            the ircServerSocket to set
	 */
	public void setIrcServerSocket(Socket ircServerSocket) {
		this.ircServerSocket = ircServerSocket;
	}

	/**
	 * @return the lastDest
	 */
	public String getLastDest() {
		return this.lastDest;
	}

	/**
	 * @param lastDest
	 *            the lastDest to set
	 */
	public void setLastDest(String lastDest) {
		this.lastDest = lastDest;
	}

	/**
	 * @return the mtUser
	 */
	public HashMap<String, CycConstant> getMtUser() {
		return this.mtUser;
	}

	/**
	 * @param mtUser
	 *            the mtUser to set
	 */
	public void setMtUser(HashMap<String, CycConstant> mtUser) {
		this.mtUser = mtUser;
	}

	/**
	 * @return the paraphrased
	 */
	public ArrayList<String> getParaphrased() {
		return this.paraphrased;
	}

	/**
	 * @param paraphrased
	 *            the paraphrased to set
	 */
	public void setParaphrased(ArrayList<String> paraphrased) {
		this.paraphrased = paraphrased;
	}

	/**
	 * @return the running
	 */
	public boolean isRunning() {
		return this.running;
	}

	/**
	 * @param running
	 *            the running to set
	 */
	public void setRunning(boolean running) {
		this.running = running;
	}
}
