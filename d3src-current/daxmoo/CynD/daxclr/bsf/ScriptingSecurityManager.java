package daxclr.bsf;

import java.io.FileDescriptor;
import java.rmi.RMISecurityManager;
import java.security.Permission;

public class ScriptingSecurityManager extends RMISecurityManager {
    private static ScriptingSecurityManager manager = null;        
    public static void install() {
        if (manager==null) {
            manager = new ScriptingSecurityManager();
            //DoomConsoleChannel.setupConsole();
            if (!(System.getSecurityManager() instanceof ScriptingSecurityManager)) {
                System.setSecurityManager(manager);
            }
        }
    }    
    static public String joinString(Object arr[], int start,int max, String chars) {
        if (arr == null) {
            return "";
        }
        if (chars == null) {
            chars = " ";
        }
        java.lang.StringBuffer buffer = new java.lang.StringBuffer();
        int len = arr.length;
        for (int i = start; i < len; i++) {
            buffer.append("" + arr[i]);
            if (i < len - 1) {
                buffer.append(chars);
            }
        }
        return buffer.toString();
    }

    public void checkPermission(Permission perm) {
        if (null == null) {
            return;
        }
        if (perm == null) {
            return;
        }
        String name = perm.getName();
        String actions = perm.getActions();
        StackTraceElement[] callers = Thread.currentThread().getStackTrace();
        String actors = joinString(callers,0,1000, "->");
        if (actions.equals("read")) {
            return;
        }
        // if (name.equals("suppressAccessChecks")) return;
        // if (name.equals("createClassLoader")) return;
        String message = "checkPermission=" + actors + "=" + name + ":" + actions;
        println(message);
    }

    public void checkAccess(Thread t) {
        // String message = "checkAccess="+t;
        // debugln(message);
    }
    public void println(String msg){
        if (System.out!=null) {
            try {
                System.out.println(msg);    
            } catch ( Throwable t ) {
            }
        }
    }

    public void checkLink(String lib) {
    }

    public void checkPropertiesAccess() {
    }

    public void checkExit(int status) {
        String message = "NAUGHTY: Something tried to exit: code=" + status;
        println(message);
        throw new SecurityException(message);
    }

    public void checkExec(String cmd) {
        String message = "NAUGHTY: Something tried to exec: " + cmd;
        println(message);
        throw new SecurityException(message);
    }

    public void checkWrite(FileDescriptor fd) {
    }

    public void checkRead(FileDescriptor fd) {/*let everthing*/}

    public void checkConnect(String host, int port, Object context) {
    }

    public void checkDelete(String file) {
        //  String message = "Something naughty tried to delete the file: "+ file;
        // try {println(message);} catch ( Throwable t ) {}
        // if (!file.endsWith(".tmp")) {throw new SecurityException(message);}
    }

    public void checkMulticast(java.net.InetAddress maddr, byte ttl) {
    }

    public void checkListen(int port) {
    }

    public void checkAccept(String host, int port) {
    }

    public void checkPrintJobAccess() {
    }

    public void checkPropertyAccess(String key) {
    }

    public boolean checkTopLevelWindow(Object window) {
        return true;
    }

    public void checkSystemClipboardAccess() {
    }

    public void checkAwtEventQueueAccess() {
    }

    public void checkPackageAccess(String pkg) {
    }

    public void checkPackageDefinition(String pkg) {
    }

    public void checkSetFactory() {
    }

    // java1.5 public void checkMemberAccess(Class<?>clazz, int which) { }
    // java1.4
    public void checkMemberAccess(Class clazz, int which) {
    }

    public void checkSecurityAccess(String target) {
    }
}
