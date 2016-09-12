//<?
package daxclr.doom.ui;

//import javax.vecmath.*;
//import javax.vecmath.Point3f;
//import javax.vecmath.Quat4f;
//import javax.vecmath.Tuple3f;
//import javax.vecmath.Vector3f;
import javax.swing.SwingUtilities;

import daxclr.doom.modules.RunnableDoomModule;

/**
 @version 4.1
 @author Douglas R. Miles
 */
abstract public class AbstractPanelModule extends javax.swing.JFrame implements  RunnableDoomModule {

    public AbstractPanelModule() {
      //  super(name, true, true, true, true);
    }
    
    public String theConfigLine;

	public String getConfigLine() {
		// TODO Auto-generated method stub
		return theConfigLine;
	}

	public void setConfigLine(String line) {
		this.theConfigLine = line;
		
	}
    static public abstract class SwingWorker {
        protected ThreadVar threadVar;

        /**
         * Class to maintain reference to current worker thread
         * under separate synchronization control.
         */
        public class ThreadVar {
            protected Thread thread;
            ThreadVar(Thread t) {
                thread = t;
            }

            public synchronized Thread get() {
                return thread;
            }

            public synchronized void clear() {
                thread = null;
            }
        }


        private Object value; // see getValue(), setValue()

        /**
         * Get the value produced by the worker thread, or null if it
         * hasn't been constructed yet.
         */
        protected synchronized Object getValue() {
            return value;
        }

        /**
         * Set the value produced by worker thread
         */
        private synchronized void setValue(Object x) {
            value = x;
        }

        /**
         * Compute the value to be returned by the <code>get</code> method.
         */
        public abstract Object construct();

        /**
         * Called on the event dispatching thread (not on the worker thread)
         * after the <code>construct</code> method has returned.
         */
        public void finished() {
        }

        /**
         * A new method that interrupts the worker thread.  Call this method
         * to force the worker to stop what it's doing.
         */
        public void interrupt() {
            Thread t = threadVar.get();
            if (t != null) {
                t.interrupt();
            }
            threadVar.clear();
        }

        /**
         * Return the value created by the <code>construct</code> method.
         * Returns null if either the constructing thread or the current
         * thread was interrupted before a value was produced.
         *
         * @return the value created by the <code>construct</code> method
         */
        public Object get() {
            while (true) {
                Thread t = threadVar.get();
                if (t == null) {
                    return getValue();
                }
                try {
                    t.join();
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt(); // propagate
                    return null;
                }
            }
        }


        /**
         * Start a thread that will call the <code>construct</code> method
         * and then exit.
         */
        public SwingWorker() {
            final Runnable doFinished = new Runnable() {
                public void run() {
                    finished();
                }
            };

            Runnable doConstruct = new Runnable() {
                public void run() {
                    try {
                        setValue(construct());
                    } finally {
                        threadVar.clear();
                    }

                    SwingUtilities.invokeLater(doFinished);
                }
            };

            Thread t = new Thread(doConstruct);
            threadVar = new ThreadVar(t);
        }

        /**
         * Start the worker thread.
         */
        public void start() {
            Thread t = threadVar.get();
            if (t != null) {
                t.start();
            }
        }
    }
}
