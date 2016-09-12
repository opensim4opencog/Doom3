package daxclr.inference;

import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Iterator;
import org.opencyc.cycobject.CycList;
import org.opencyc.cycobject.CycNart;
import org.opencyc.cycobject.CycSymbol;
import org.opencyc.cycobject.CycVariable;

import daxclr.bsf.IScriptObject;

/**
 * A objectInfo for beans used in the bean bowl system.
 * It holds an object, a name, and info about whether it is selected or not.
 * The "name" and "selected" properties are bound and constrained, i.e.
 * you can listen to changes using addPropertyChangeListener, and you can
 * also prevent changes in some cases if you use addVetoableChangeListener.
 *
 * @see IScriptObject
 */
public class CycSaver {


    String hostname = "CycServer";
    int port =  3600;

    static public void main(String[] args) {
        String host = args[0];
        int port = Integer.parseInt(args[1]);
        CycSaver saver = new CycSaver(host,port);
        if (args.length>2) {
            saver.cursor =  Integer.parseInt(args[2]);
            saver.last =  saver.cursor+10000;
        }
        if (args.length>3) {
            saver.last = Integer.parseInt(args[3]);
        }
        saver.filename = saver.hostname+"."+saver.port+"."+saver.cursor+"-"+saver.last+".txt";
        saver.save(true);
        System.exit(0);
    }

    public CycAPI cycAccess=null;
    String filename = "output.txt";

    public CycSaver(String hostname, int port) {
        try {
            this.hostname = hostname;
            this.port = port;
            filename = hostname+"."+port+".txt";
            resetCycAccess();
        } catch (Throwable e) {
            e.printStackTrace(System.err);
        }
    }
    int cursor = 0;
    CycSymbol NIL = new CycSymbol("NIL");
    CycSymbol ASRT = new CycSymbol("ASSERT");
    int missed = 0;
    int canmiss = 1000;
    int last = 97000000;

    PrintWriter pw = null;

    public void save(boolean exitif) {
      //  if (pw==null) {
      //      try {
      //          pw = new PrintWriter(new java.io.FileOutputStream(new File(filename)));
      //      } catch (Throwable e) {
      //          e.printStackTrace(System.err);
      //      }
      //  }
        pw = new PrintWriter(System.out);

        while (missed<canmiss && cursor<last) {
            CycList term = cycAccess.getAssertionMetadata(cursor++);
            if (cursor%1000==((cursor/1000)%1)) {
                pw.flush();
                System.err.println("%... " + cursor + " ...");
                System.err.flush();
                resetCycAccess();
            }            
            if (term==null) {
                missed++;
                if (missed%100==((missed/100)%1)) {
                    System.err.println("% missed " + missed);
                    System.err.flush();
                }
            } else {
                missed=0;
                pw.println(toPrologString(term)+".");                
            }
        }
        pw.close();
        pw = null;
        if (exitif) {
            if (!(missed<canmiss)) {
                System.exit(1);
            }
        }
    }

    public void resetCycAccess() {
        if (cycAccess!=null) {
            cycAccess.close();
            cycAccess = null;
        }
        try {
            cycAccess =  daxclr.inference.CycAPI.current(hostname,port);
            System.err.println("new cycAccess " + cycAccess.hashCode());
        } catch (Throwable e) {
            throw new RuntimeException(e);
        }
    }

    public String toPrologString(Object term) {
        if (term instanceof CycList) {
            CycList list = (CycList)term;
            String o = "[";
            Iterator its = ((ArrayList)list).iterator();
            if (its.hasNext()) o = o + toPrologString(its.next());
            while (its.hasNext()) {
                o = o + "," + toPrologString(its.next());
            }          
            if (!list.isProperList()) {
                o = o+"|"+toPrologString(list.getDottedElement());
            }
            return o+"]";
        }
        if (term instanceof CycVariable) {
            CycVariable v = (CycVariable)term;
            return v.cyclify().substring(1);
        }
        if (term instanceof String) {
            return "\""+term.toString().replace("\"","'")+"\"";
        }
        if (term instanceof CycNart) {
            CycNart nart = (CycNart)term;
            return "nart("+toPrologString(nart.toCycList())+")";
        }
        if (term instanceof Number) {
            return ""+term;
        }
        return "'"+term+"'";
    }
}