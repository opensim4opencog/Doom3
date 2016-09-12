package daxclr.bsf;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.PrintStream;
import java.io.PrintWriter;
import java.io.Reader;
import java.lang.Thread.UncaughtExceptionHandler;
import java.nio.CharBuffer;

import org.opencyc.cycobject.CycObject;

import daxclr.doom.modules.ScriptThreadGroup;
import bsh.ConsoleInterface;

abstract public class ConsoleChannel extends ConsoleChannelSettings implements
		ConsoleInterface, java.io.Flushable, Appendable, Readable,
		UncaughtExceptionHandler {

	protected void destroy() {
		restore();
	}

	abstract protected void printLocal(String msg) throws Error;

	abstract protected void debugLocal(String msg) throws Error;

	abstract public boolean isLinked();

	static protected transient ConsoleChannel ISTREAM;

	public static int levelDebug = 2;

	public/* static */void saveNonScript() {
		prevIn = System.in instanceof ScriptInputStream ? null : System.in;
		prevOut = System.out instanceof ScriptPrintStream ? null : System.out;
		prevErr = System.err instanceof ScriptPrintStream ? System.out
				: System.err;
	}

	// protected /*static*/ ScriptConsoleChannel ISTREAM = new
	// ScriptConsoleChannel();

	// /*static*/ InputStream IOSTREAM; // = IOSTREAM;
	protected static ScriptOutputStream OSTREAM = new ScriptOutputStream();

	protected static ScriptOutputStream ESTREAM = new ScriptErrorStream(); // new

	// ScriptErrorStream();

	public static class  ScriptPrintStream extends PrintStream {
		public ScriptPrintStream(final ScriptOutputStream os) {
			super(os);
		}
	}

	public static  class  ScriptInputStreamReader extends BufferedReader {
		InputStream IS;

		public ScriptInputStreamReader(final InputStream is) {
			super(new InputStreamReader(is));
			IS = is;
		}

		public InputStream getInputStream() {
			return IS;
		}
	}

	public static  class  ScriptInputStream extends BufferedInputStream {
		InputStream IS;

		public ScriptInputStream(final InputStream is) {
			super(is);
			IS = is;
		}

		public InputStream getInputStream() {
			return IS;
		}
	}

	public/* static */ConsoleInterface getConsole() {
		return ISTREAM;
	}

	public ScriptInputStream getInputStream() {
		if (System.in instanceof ScriptInputStream)
			return (ScriptInputStream) System.in;
		return new ScriptInputStream(ISTREAM);
	}

	public static ScriptPrintStream getPrintStream() {
		if (System.out instanceof ScriptPrintStream)
			return (ScriptPrintStream) System.out;
		return new ScriptPrintStream(OSTREAM);
	}

	public static ScriptPrintStream getErrorStream() {
		if (System.err instanceof ScriptPrintStream)
			return (ScriptPrintStream) System.err;
		return new ScriptPrintStream(ESTREAM);
	}

	public/* static */ScriptOutputStream getOutputStream() {
		return OSTREAM;
	}

	public/* static */PrintWriter getWriter() {
		return new PrintWriter(getOutputStream());
	}

	public/* static */int getLevelDebug() {
		return levelDebug;
	}

	public void setLevelDebug(int level) {
		levelDebug = level;
	}

	/**
	 * 
	 * @param e
	 */
	public/* static */void warnEvent(final Throwable e) {
		if (getLevelDebug() > 0) {
			debug("warning: " + e);
			debug(e);
		}
	}

	public static/* static */void tryPrint(final java.lang.Object name) {
		try {
			ISTREAM.printLocal(toString(name));
		} catch (final Throwable ule) {
			printPrevOut(toString(name));
		}
	}

	public static/* static */void tryDebugLocal(final java.lang.Object name) {
		String s = toString(name);
		if (!s.toLowerCase().startsWith("debug"))
			s = "DEBUG: " + s;
		s = s.trim();
		/*
		 * int len = s.length(); while (len>0 && s.charAt(--len)=='\n') { s =
		 * s.substring(0,len); }
		 */
		try {
			ISTREAM.debugLocal(s);
		} catch (final Throwable ule) {
			printPrevOut(s);
		}
	}

	public static void printPrevOut(final String msg) {
		try {
			if (ISTREAM != null && !(ISTREAM.getOut() instanceof ScriptPrintStream)) {
				ISTREAM.getOut().println("PREVOUT: " + msg);
				return;
			}
		} catch (final Throwable t) {
		}
		try {
			if (ISTREAM != null && !(ISTREAM.getErr() instanceof ScriptPrintStream)) {
				ISTREAM.getErr().println("PREVOUT: " + msg);
				return;
			}
		} catch (final Throwable t2) {
		}
	}

	/**
	 * 
	 * @param clasparams
	 * @return
	 */
	public static/* static */String listToStringDebug(final Object[] clasparams) {
		if (clasparams == null)
			return "null";
		final StringBuffer buffer = new StringBuffer(" {");
		for (final Object element : clasparams)
			buffer.append(" ").append(toString(element));
		return buffer.append("} ").toString();
	}

	public static String joinString(final Object arr[], String chars,
			int start, int max) {
		if (arr == null)
			return "";
		if (chars == null)
			chars = " ";
		final java.lang.StringBuffer buffer = new java.lang.StringBuffer();
		int dir = 1;
		final int len = arr.length;
		if (start < 0) {
			dir = -1;
			start = len + start;
		}
		for (int i = start; 0 <= i && i < len; i = i + dir) {
			buffer.append(toString(arr[i]));
			if (i < len - 1)
				buffer.append(chars);
			if (max-- == 0)
				return buffer.toString();
		}
		return buffer.toString();
	}

	public static/* static */String toString(Object target) {
		if (target == null)
			return "null";
		if (target instanceof CycObject) {
			CycObject new_name = (CycObject) target;
			return new_name.cyclify();
		}
		if (target instanceof IScriptObject) {
			IScriptObject new_name = (IScriptObject) target;
			return toString(new_name.getCycObject());
		}
		// return ScriptManager.objectKey(target);
		return "" + target;
		/*
		 * if (target.getClass().isArray()){ int len = Array.getLength(target);
		 * String[] s = Array.newInstance(String.class, len); for (int i=0;i<len;i++)
		 * Array.set(s,i,""+Array.getDouble(target, i));) return s; }
		 */
	}

	public static/* static */String joinString(final Object arr[], final String chars) {
		return joinString(arr, chars, 0, -1);
	}

	/**
	 * 
	 * @param msecs
	 */
	public static/* static */void waitmsecs(final long msecs) {
		try {
			Thread.sleep(msecs);
		} catch (final Throwable e) {
		}
	}

	public static void debug(final Object e) {
		try {
		if (e instanceof Throwable) {
			((Throwable)e).printStackTrace();
		}
		System.err.println("debug: SC " + e);
		} catch (final Throwable e2) {
		}
	}

	public/* static */void debug(final String string, final Throwable err) {
		debug(err);
		debug(string);
	}

	// ==== Static variables ===================
	// public /*static*/ org.apache.log4j.Category cat =
	// Category.getInstance(class);
	public/* static */void warn(final String ln, final Throwable err) {
		debug(err);
		warn(ln);
	}

	public/* static */void warn(final String ln) {
		debug("WARN: " + ln);
	}

	protected static/* static */int CONSOLE_WIDTH = 2048;

	public/* static */ScriptThreadGroup threadGroup = new ScriptThreadGroup();

	public static class ScriptErrorStream extends ScriptOutputStream {
		public void flushHasBug() {
			tryDebugLocal(OBUFFER.toString());
			clear();
		}
	}

	public static class ScriptOutputStream extends OutputStream implements
			java.io.Flushable {
		StringBuffer OBUFFER = new StringBuffer(CONSOLE_WIDTH + 1);

		public ScriptOutputStream() {
			super();
		}

		/**
		 * Calls {@link #write} repeatedly until <var>len</var> bytes are
		 * written.
		 * 
		 * @param theBytes
		 *            array from which to read bytes
		 * @param off
		 *            offset for array
		 * @param len
		 *            max number of bytes to read into array
		 * @since 1.3
		 */
		public void writeMAybe(final byte[] b, final int offset, int len)
				throws java.io.IOException {
			int room = CONSOLE_WIDTH - OBUFFER.length();
			if (room <= 0) {
				flush();
				room = CONSOLE_WIDTH;
			}
			while (len-- > 0)
				if (b[offset] == '\n') {
					flush();
					room = CONSOLE_WIDTH;
				} else {
					room--;
					if (room <= 0) {
						flush();
						room = CONSOLE_WIDTH;
					} else
						OBUFFER.append((char) b[offset]);
				}
		}

		public StringBuffer getBuffer() {
			return OBUFFER;
		}

		public void clear() {
			OBUFFER.setLength(0);
		}

		/**
		 * 
		 * @param c
		 * @throws java.io.IOException
		 */
		@Override
		public void write(final int c) throws IOException {
			if (OBUFFER.length() >= CONSOLE_WIDTH)
				flush();
			else {
				OBUFFER.append((char) c);
				if (c == 10)
					flush();
			}
		}

		@Override
		public void finalize() {
			flush();
		}

		@Override
		public void close() {
			flush();
		}

		@Override
		public void flush() {
			tryPrint(OBUFFER.toString());
			clear();
		}
	}

	protected /* static */ThreadGroup getThreadGroup() {
		// TODO Auto-generated method stub
		return threadGroup;
	}

	protected/* static */Runnable getRunnable() {
		// TODO Auto-generated method stub
		return threadGroup;
	}

	StringBuffer IBUFFER = new StringBuffer("");

	ScriptInputStreamReader BREADER = null;

	protected ConsoleChannel() {
		super((Reader) null, getPrintStream(), getErrorStream());
		ISTREAM = this;
		//install();
	}

	public void install() {
		if (!isLinked()) {
			if (prevIn != null)
				System.setIn(prevIn);
			if (prevErr != null)
				System.setErr(prevErr);
			if (prevOut != null)
				System.setOut(prevOut);
		} else {
			if (!(System.out instanceof ScriptPrintStream))
				System.setOut(getPrintStream());
			if (!(System.err instanceof ScriptPrintStream))
				System.setErr(getErrorStream());
			if (!(System.in instanceof ScriptInputStream))
				System.setIn(getInputStream());
		}
		Thread.setDefaultUncaughtExceptionHandler(this);
	}

	public ScriptInputStreamReader getIn() {
		if (BREADER == null)
			BREADER = new ScriptInputStreamReader(this);
		return BREADER;
	}

	// public /*static*/ transient Thread delayedInitThread = null;
	public void flush() throws IOException {
		if (OSTREAM != null)
			OSTREAM.flush();
	}

	public int read(final CharBuffer cb) throws IOException {
		final int len = IBUFFER.length();
		cb.append(IBUFFER);
		IBUFFER = new StringBuffer();
		return len;
	}

	public ScriptPrintStream getOut() {
		return getPrintStream();
	}

	public void error(final java.lang.Object name) {
		print("error:" + name);
	}

	public ScriptPrintStream getErr() {
		return getErrorStream();
	}

	public void println(final java.lang.Object name) {
		System.out.println(name);
	}

	public void print(final java.lang.Object name) {
		System.out.print(name);
	}

	@Override
	public int read() throws IOException {
		if (IBUFFER == null || IBUFFER.length() == 0)
			return -1;
		final int c = IBUFFER.codePointAt(0);
		IBUFFER = new StringBuffer(IBUFFER.toString().substring(1));
		return c;
	}

	public Appendable append(final CharSequence csq) throws IOException {
		return IBUFFER.append(csq);
	}

	public Appendable append(final char c) throws IOException {
		return IBUFFER.append(c);
	}

	public Appendable append(final CharSequence csq, final int start,
			final int end) throws IOException {
		return IBUFFER.append(csq, start, end);
	}

	/**
	 * Returns the bean with the given name, or null if none.
	 */
	// ======== BeanUtils methods ==================================
	/**
	 * get class name without package info
	 * 
	 * @param _class
	 *            Class which name is searched
	 * @return class name
	 */
	public/* static */void TODO(Exception ex) {
		debug(ex);
	}

	public void uncaughtException(Thread t, Throwable e) {
		if (e != null)
			e.printStackTrace(getPrintStream());
		if (t != null)
			println("uncaughtException on:" + t.getName());
	}

	/**
	 * 
	 * @param s
	 * @return
	 */
	public static String[] splitString(String s) {
		// if (s.contains(" ")) {
		return ("" + s).replace("\t", " ").replace(" ", " ").trim().split(" ");
		// }
		/*
		 * Object[] object = tokenizeString(s); String[] rs = new
		 * String[object.length]; for (int i = 0; i < object.length; i++) {
		 * rs[i] = "" + object[i]; } return rs;
		 */
	}
}