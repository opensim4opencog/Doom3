/**
 * 
 */
package daxclr.bsf;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.io.Reader;

import bsh.ConsoleInterface;

public class ConsoleChannelSettings extends InputStream implements
		ConsoleInterface {
	
	public static ConsoleChannelSettings systemSettings = new ConsoleChannelSettings(System.in, System.out, System.err);

	public static ConsoleChannelSettings currentConsoleChannelSettings() {
		return new ConsoleChannelSettings(System.in, System.out, System.err);
	}

	static void setConsoleChannelSettings(ConsoleInterface set) {
		if (set.getIn() != null)
			System.setIn(new ConsoleChannelSettings.ReaderInputStream(set
					.getIn()));
		if (set.getOut() != null)
			System.setOut(set.getOut());
		if (set.getErr() != null)
			System.setErr(set.getErr());
	}

	static class ReaderInputStream extends InputStream {

		Reader src;

		@Override
		public int read() throws IOException {
			return src.read();
		}

		public ReaderInputStream(Reader src) {
			super();
			this.src = src;
		}

	}

	public ConsoleChannelSettings previous = null;

	InputStream prevIn;

	Reader prevReader;

	PrintStream prevOut;

	PrintStream prevErr;

	public void restore() {
		if (previous != null)
			// setConsoleChannelSettings(previous);
			previous.install();
	}

	private void install() {
		System.setErr(getErr());
		System.setOut(getOut());
		System.setIn(this);
	}

	public ConsoleChannelSettings(Reader prevIn, PrintStream prevOut,
			PrintStream prevErr) {
		this.prevReader = prevIn;// new InputStreamReader(prevIn);
		this.prevIn = null;
		this.prevOut = prevOut;
		this.prevErr = prevErr;
	}

	public ConsoleChannelSettings(InputStream prevIn, PrintStream prevOut,
			PrintStream prevErr) {
		super();
		this.prevReader = null;// new InputStreamReader(prevIn);
		this.prevIn = prevIn;
		this.prevOut = prevOut;
		this.prevErr = prevErr;
	}

	public void error(Object o) {
		println(o);
	}

	public Reader getIn() {
		if (prevReader != null)
			return prevReader;
		if (prevIn == null)
			return previous.getIn();
		prevReader = new InputStreamReader(prevIn);
		return prevReader;
	}

	public InputStream getInputStream() {
		if (prevIn != null)
			return prevIn;
		if (prevIn == null)
			return previous.getInputStream();
		prevIn = new ConsoleChannelSettings.ReaderInputStream(prevReader);
		return prevIn;
	}

	public PrintStream getOut() {
		if (prevOut != null)
			return prevOut;
		if (prevErr != null)
			return prevErr;
		return previous.getOut();
	}

	public PrintStream getErr() {
		if (prevErr != null)
			return prevErr;
		if (prevOut != null)
			return prevOut;
		return previous.getErr();
	}

	public void print(Object o) {
		try {
			prevOut.print(o);
		} catch (Throwable t) {
			try {
				prevErr.print(o);
			} catch (Throwable t2) {

			}
		}
	}

	public void println(Object o) {
		print("" + o + "\n");
	}

	@Override
	public int read() throws IOException {
		return getIn().read();
	}

	/**
	 * @return the scriptConsole
	 *//*
	public static ConsoleInterface getConsole() {
		return RemoteConsoleChannel.getConsole();
	}
	*/
}