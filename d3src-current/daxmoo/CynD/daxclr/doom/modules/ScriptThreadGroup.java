package daxclr.doom.modules;

import daxclr.bsf.ScriptingSecurityManager;


public class ScriptThreadGroup extends java.lang.ThreadGroup implements Runnable,Thread.UncaughtExceptionHandler {
	static {
		ScriptingSecurityManager.install();
	}

	final static Thread loaderThread = Thread.currentThread();

	final static ScriptThreadGroup doomThreadGroup = new ScriptThreadGroup();

	Thread creatorThread = null;
	ThreadGroup threadGroup0 = null;
	ThreadGroup top;

	public ScriptThreadGroup() {
		super("DoomThreadGroup");
		creatorThread = Thread.currentThread();
		threadGroup0 = creatorThread.getThreadGroup();
		top = threadGroup0;
		while (top.getParent() != null)
			top = top.getParent();
	}

	/**
	 * Adds the specified Thread to this group.
	 * 
	 * @param t
	 *            the Thread to be added
	 * @exception IllegalThreadStateException
	 *                If the Thread group has been destroyed.
	 */

	public void uncaughtException(Thread t, Throwable e) {
		// if (parent != null) parent.uncaughtException(t, e);
		Thread.UncaughtExceptionHandler ueh = Thread.getDefaultUncaughtExceptionHandler();
		if (ueh != null) {
			ueh.uncaughtException(t, e);
		} else if (!(e instanceof ThreadDeath)) {
			System.err.print("Exception in thread \"" + t.getName() + "\" ");
			e.printStackTrace(System.err);
		}
	}

	final static public ThreadGroup getThreadGroup() {
		return importThread(Thread.currentThread());
	}
	
	final static public ThreadGroup importThread(final Thread thread) {
		thread.setUncaughtExceptionHandler(doomThreadGroup);
		//javaDoom.add(thread);
		return thread.getThreadGroup();
	}

	public void run() {
		Thread.currentThread();
	}

}