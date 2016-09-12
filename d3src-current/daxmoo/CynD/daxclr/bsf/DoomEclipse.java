package daxclr.bsf;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.MalformedURLException;
import java.net.URL;
import org.eclipse.core.resources.IWorkspace;
import org.eclipse.core.resources.ResourcesPlugin;

import bsh.classpath.BshClassLoader;
import bsh.classpath.BshClassPath;
import bsh.classpath.ClassManagerImpl;

public class DoomEclipse implements Runnable {
	static {
		ScriptingSecurityManager.install();
	}

	static Thread initEclipseThread = null;

	public static BshClassPath bshClassPath = null;

	public static transient ClassManagerImpl bshClassManager = null;

	public static transient BshClassLoader bshClassLoader = null;

	public static transient Class eclipseStarterClass = null;

	public static transient Class eclipseMainClass = null;

	public static transient Method method_EclipseStarter_isRunning = null;

	public static transient Method method_EclipseStarter_run = null;

	public static transient Method method_Main_run = null;

	public static transient Object emain = null;

	public static transient Object retcode = null;

	public static transient Object retObject = null;

	/* Bootfile when starting */
	public static String PLUGIN_FILENAME = "c:/doom3/base/classlib/plugins.cfg";

	public static String[] cmdArgs = new String[] {};// "-application",

	// getDoomServer().getBaseDirectory()
	// "c:\doom3\base\classlib"
	// + "classlib");
	public static Runnable splashHandler = new DoomEclipse();

	static {
		initStaticTransient();
	}

	public static void main(String[] args) {
		cmdArgs = args;
		if (args != null && args.length > 0) {
			PLUGIN_FILENAME = args[0];
		}
		init();
	}

	public static IWorkspace getWorkspace() {
		return ResourcesPlugin.getWorkspace();
	}

	public static ResourcesPlugin getResourcesPlugin() {
		return ResourcesPlugin.getPlugin();
	}

	public static void initStaticTransient() {
		bshClassManager = new ClassManagerImpl();
		bshClassPath = new BshClassPath("c:/doom3/base/classlib");

		try {
			bshClassPath.add(new URL[] { new URL("file:c:/doom3/base/bin/"),
					new URL("file:c:/doom3/base/classlib/") });
		} catch (MalformedURLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		DoomEclipse.bshClassManager.setClassPath(DoomEclipse.bshClassPath
				.getPathComponents());

		bshClassLoader = new BshClassLoader(bshClassManager, bshClassPath);
		// Thread.currentThread().setContextClassLoader(DoomEclipse.bshClassLoader);
		// /bsfManager.setObjectRegistry(objectRegistry);

		// Register .doo files
		// bsfManager.registerBean("theNameSpaceMap",nameSpaceMap);

		try {
			eclipseStarterClass = Class
					.forName("org.eclipse.core.runtime.adaptor.EclipseStarter");
			eclipseMainClass = Class.forName("org.eclipse.core.launcher.Main");
			try {
				method_EclipseStarter_isRunning = eclipseStarterClass
						.getMethod("isRunning", new Class[0]);
				method_EclipseStarter_run = eclipseStarterClass.getMethod(
						"run", new Class[] { String[].class, Runnable.class });
				method_Main_run = eclipseStarterClass.getMethod("run",
						new Class[] { String[].class, Runnable.class });
			} catch (SecurityException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (NoSuchMethodException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		} catch (ClassNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	final static public void init() {
		status();
		IWorkspace workspace = null;
		try {
			workspace = getWorkspace();
		} catch (RuntimeException rte) {
			rte.printStackTrace();
		}
		if (workspace != null) {
			System.out.println("Eclipse seems to be running.");
			initEclipseThread = new Thread(splashHandler,
					"DoomEclipse SplashHandler thread");
			initEclipseThread.start();
		} else {
			System.out.println("Eclipse will be starting soon.");
			if (initEclipseThread != null) {
				System.out.println("Don't you believe it?");
				if (!initEclipseThread.isAlive()) {
					System.out.println("Ok, you've been lied to.");
				} else {
					// initMainThread.setPriority(Thread.MAX_PRIORITY);
					System.out.println("Just wait and see.");
				}
			} else {
				initEclipseThread = new Thread("Eclipse Starter thread") {
					public void run() {
						// if (!isRunning())initMain();
						initAdapter();
					}
				};
				if (!initEclipseThread.isAlive())
					initEclipseThread.start();
			}
		}
		status();
	}

	static void status() {
		System.out
				.println("org.eclipse.core.runtime.adaptor.EclipseStarter.isRunning()="
						+ isRunning());

	}

	/**
	 * Starts an instance of a DoomServer from commandline Call only if doom
	 * hasn't started
	 * 
	 * @param args[]
	 */

	// public void setDictionary(ResolverMap sc) {
	// resolverMap = sc;
	// / }
	public DoomEclipse() {
	}

	public static Class<?> findClass(String name) throws ClassNotFoundException {
		// org.eclipse.core.runtime.adaptor.EclipseStarter.setInitialProperties();
		return Class.forName(name);
	}

	public static boolean isRunning() {
		try {
			return (Boolean) method_EclipseStarter_isRunning.invoke(null,
					new Object[0]);
		} catch (IllegalArgumentException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (InvocationTargetException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return false;
	}

	public static void initAdapter() {
		status();
		if (splashHandler == null) {
			splashHandler = new DoomEclipse();
		}
		if (!isRunning()) {
			try {
				retcode = method_EclipseStarter_run.invoke(null, new Object[] {
						cmdArgs, splashHandler });
			} catch (Exception e) {
				retcode = e;
				// if (isRunning())DoomConsoleChannel.initRan();
				e.printStackTrace();
			}
		}
		status();
	}

	public static void initMain() {
		status();
		try {
			if (emain == null)
				emain = eclipseMainClass.newInstance();
			retcode = method_Main_run.invoke(emain, new Object[] { cmdArgs,
					splashHandler });
		} catch (Exception e) {
			e.printStackTrace();
		}
		status();
	}

	public void run() {
		System.out.println("RemoteOp: " + ObjectRepository.isLinked());
	}

	public static void executeDoomApplication() {
		if (ObjectRepository.isRunningDoom()) {
			System.out.println("Patience please!");
			try {
				Thread.sleep(30);
			} catch (InterruptedException e) {
				e.printStackTrace();
				throw new RuntimeException(e);
			} catch (Throwable e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				throw new RuntimeException(e);
			}
			return;
		}

		Runtime runtime = Runtime.getRuntime();

		try {
			System.out.println("executeDoomApplication()");
			ObjectRepository.setRunningDoom(runtime.exec("c:/doom3/loop.bat"));
		} catch (IOException e) {
			e.printStackTrace();
			throw new RuntimeException(e);
		}

		runtime.addShutdownHook(new Thread("ShutdownHook runningDoom") {
			public void run() {
				if (ObjectRepository.getDoomProcess() != null)
					ObjectRepository.getDoomProcess().destroy();
			}
		});

	}

}