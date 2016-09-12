package daxclr.inference;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import org.opencyc.api.CycAccess;
import org.opencyc.api.CycApiException;
import org.opencyc.api.CycConnection;
import org.opencyc.api.CycConnectionInterface;
import org.opencyc.api.CycObjectFactory;
import org.opencyc.cycobject.CycConstant;
import org.opencyc.cycobject.CycFort;
import org.opencyc.cycobject.CycList;
import org.opencyc.cycobject.CycNart;
import org.opencyc.cycobject.CycObject;
import org.opencyc.cycobject.CycSymbol;
import org.opencyc.cycobject.CycVariable;
import org.opencyc.util.Log;

import ubc.cs.JLog.Terms.iObjectToTerm;
import ubc.cs.JLog.Terms.iTermToObject;
import ubc.cs.JLog.Terms.jTerm;
import daxclr.bsf.ConsoleChannel;


/**
 * Summary description for CycSyncronizerModule.
 */
public class CycAPI extends org.opencyc.api.CycAccess implements InferenceAPI {
    public static String hostname = "CycServer";
    public static int port =  3600;
    public static final int ASCII_MODE = 1;
    public static final int BINARY_MODE = 2;

    public static boolean useAscii() {
        //if (true) return true;
        //if (true) return false;
        if (hostname.equals("CycServer")) return false;
        if (hostname.equals("pandor3")) return false;
        return false;
    }

    static public Object getCdr(Object list) {//DefaultCycObject
        if (list instanceof CycList) {
            return getCdr((CycList)list);
        }
        try {
            return current().converseCyc("(cdr '"+ cyclifyWithEscapeChars(list)+")");
        } catch (Exception e) {
            debugln("CDR: "+cyclifyWithEscapeChars(list));
            debugln(e);
            return list;
        }
    }

    static public Object getCdr(CycList list) {
        int len = ((ArrayList)list).size();
        if (len==1) {
            return list.getDottedElement();
        }
        list = (CycList)list.clone();
        list.remove(0);
        return list;            
    }

    static public CycFort getCdrFort(Object list) {
        Object fort = getCdr(list);
        if (fort instanceof CycFort) {
            return(CycFort)fort;
        }
        if (fort instanceof CycList) {
            try {
                return new CycNart((CycList)fort);
            } catch (Exception e) {
                debugln("FORT = "+fort);
            }
        }
        return(CycFort)list;
    }


    public static String cyclifyWithEscapeChars(Object resultFort) {
        if (resultFort instanceof CycList) {
            return((CycList)resultFort).cyclifyWithEscapeChars();
        }
        if (resultFort instanceof CycObject) {
            return((CycObject)resultFort).cyclifyWithEscapeChars();        
        }
        //        if (resultFort instanceof String) {
        //            return "\""+((String)resultFort).replace("\\\"","\"").replace("\"","\\\"")+"\"";
        //        }
        return ""+resultFort;
    }

    public  boolean isQueryTrue(CycList gaf, CycObject mt) throws IOException, UnknownHostException, CycApiException {
        boolean isQueryTrue = true;
        String qresult = ""+converseCyc("(cyc-query '"+gaf.cyclifyWithEscapeChars()+" "+mt.cyclifyWithEscapeChars()+")");
        if (qresult.equals("NIL") || qresult.equals("()") || qresult.equals("0")|| qresult.equals("0.0")) {
            isQueryTrue  = false;
        }
        debugln(""+gaf +" in mt " + mt+ " is "+isQueryTrue);
        return isQueryTrue;
    }

    /**
     * Constructs a new CycList object by parsing a string.
     * 
     * @param string the string in CycL external (EL). For example: (#$isa #$Dog #$TameAnimal)
     * 
     * @return the new CycListParser object from parsing the given string
     * 
     * @throws CycApiException if the api request results in a cyc server error
     */
    public Object readLisp(String string) throws CycApiException {
        return(new LispReader()).read(string);
    }

    Socket asciiclient = null;
    Object asciiclientlock = new Object();

    public  Object[] converseSocket(Object message) {
        Object[] result = {Boolean.TRUE,null};
        if (message instanceof String) {
            try {
                result[1] = converseCyc((String)message);
            } catch (CycApiException api) {
                result[0] = Boolean.FALSE;
                result[1] = ""+api.getMessage();
            }
            return result;
        }
        try {
            result[1] = converseCyc(cyclifyWithEscapeChars(message));
        } catch (CycApiException api) {
            result[0] = Boolean.FALSE;
            result[1] = ""+api.getMessage();
        }
        return result;
    }

    public  Object converseCyc(String message) throws CycApiException {
        String result = converseTelnet(message);
        String result2 = result;
        // result2 = result2.replace(".#<","");
        //  result2 = result2.replace(". #<","");
        result2 = result2.replace("#<","");
        if (!result.equals(result2)) {
            result = result2.replace(">","");
        }
        if (result.startsWith("200")) {
            return cyclify(result.substring(3));
        }throw new CycApiException(result.substring(4));
    }

    public  void closeTelnet() {
        if (asciiclient!=null) {
            try {
                OutputStream out = asciiclient.getOutputStream();
                out.write("(API-QUIT)".getBytes());
                out.flush();
            } catch (IOException e) {
            }
            try {
                asciiclient.close();
            } catch (IOException e) {
            }
            asciiclient = null;
        }
    }

    public String converseTelnet(String message) {
        synchronized (asciiclientlock) {
            try {
                if (asciiclient==null) {
                    asciiclient = new Socket(hostname,port+1);
                }
                OutputStream out = asciiclient.getOutputStream();
                out.write(message.getBytes());
                out.write(10);
                out.flush();
                // byte[] ACK = new byte[4]
                //is.read(ACK,0,4);
                BufferedReader isr = new BufferedReader(new InputStreamReader(asciiclient.getInputStream()));
                String result = isr.readLine();
                closeTelnet();
                return result;
            } catch (Exception e) {
                asciiclient = null;
                return "500 "+e;
            }
        }
    }
    /*
public void rebuildConnection() throws Exception {
  cycConnection = new CycConnection(hostname, port,this) {
      synchronized protected Object[] converseAscii(String message,Timer timeout)
      throws IOException, TimeOutException, CycApiException {
          isSymbolicExpression = false;

          Object[] response = converseUsingAsciiStrings(message, timeout);

          if (response[0].equals(Boolean.TRUE)) {
              String answer = ((String) response[1]).trim();

              if (StringUtils.isDelimitedString(answer)) {
                  response[1] = StringUtils.removeDelimiters(answer);

                  // Return the string.
                  return response;
              }

              if (isSymbolicExpression) {
                  // Recurse to complete contained CycConstant, CycNart objects.
                  if (cycAccess == null) {
                      throw new RuntimeException("CycAccess is required to process commands in string form");
                  }

                  response[1] = CycAccess.current().makeCycList(answer);

                  // Return the CycList object.
                  return response;
              }

              if (answer.equals("NIL")) {
                  response[1] = CycObjectFactory.nil;

                  // Return the symbol nil.
                  return response;
              }

              if (answer.startsWith("#$")) {
                  if (cycAccess == null) {
                      throw new RuntimeException("CycAccess is required to process commands in string form");
                  }

                  response[1] = CycAccess.current().makeCycConstant(answer);

                  // Return the constant.
                  return response;
              }

              if (answer.startsWith("?")) {
                  response[1] = CycObjectFactory.makeCycVariable(answer);

                  // Return the variable.
                  return response;
              }

              if (StringUtils.isNumeric(answer)) {
                  response[1] = new Integer(answer);

                  // Return the number.
                  return response;
              }

              if (CycSymbol.isValidSymbolName(answer)) {
                  response[1] = CycObjectFactory.makeCycSymbol(answer);

                  // Return the symbol.
                  return response;
              }

              try {
                  double doubleAnswer = Double.parseDouble(answer);
                  response[1] = new Double(doubleAnswer);

                  // Return the double.
                  return response;
              } catch (NumberFormatException e) {
              }

              if (answer.endsWith("d0") && 
                  (Strings.indexOfAnyOf(answer.substring(0,answer.length() - 2), "0123456789") > -1)) {
                  String floatPart = answer.substring(0,answer.length() - 2);
                  response[1] = new Double(floatPart);

                  // Return the double.
                  return response;
              }
              //return response;
              throw new CycApiException("Ascii api response not understood " + answer);
          } else {
              return response;
          }
      }
  };
}          */
    //public CycAccess(String hostName, int basePort, int communicationMode, int persistentConnection, boolean isLegacyMode)throws IOException, UnknownHostException, CycApiException {

    private CycAPI(String hostname, int port) throws IOException, UnknownHostException, CycApiException {      // Cycquestion
        //  super(hostname,port,useAscii()?CycConnection.ASCII_MODE:CycConnection.BINARY_MODE,CycAccess.PERSISTENT_CONNECTION,CycConnection.CONCURRENT_MESSAGING_MODE);
        super(hostname,port);
        CycConnection.DEFAULT_HOSTNAME  = hostname;
        this.hostname = hostname;
        this.port = port;
        //rebuildConnection();
        //CycConnection.DEFAULT_BASE_PORT  = port;
        setInstance(Thread.currentThread(),this);
        // super.commonInitialization();
    }


    static public CycAPI current(String hostname,int baseport3600) {
        setHost(hostname);
        setPort(baseport3600);
        return currentInstance();
    }

    static public int MAX_THREADS = 1;

    synchronized static public CycAPI currentInstance() {
        return current();
    }
    /**
    * reference to CycAPI (OpenCyc server)
    */
    synchronized static public CycAPI current() {
        if (cycAccessInstances==null) {
            cycAccessInstances = new HashMap<Thread, CycAPI>();
        }
        Thread thread = Thread.currentThread();
        Object cyc = cycAccessInstances.get(thread);
        if (cyc instanceof CycAPI) {
            setInstance(thread,(CycAPI)cyc);
            return(CycAPI)cyc;
        }
        if (CycAccess.sharedCycAccessInstance==null) {
            CycAccess.sharedCycAccessInstance = (CycAccess)cycAccessInstances.get(thread);
        }
        if (CycAccess.sharedCycAccessInstance==null) {
            try {
                CycAccess.sharedCycAccessInstance = new CycAPI(hostname,port);
                setInstance(thread,(CycAPI)CycAccess.sharedCycAccessInstance);
                return(CycAPI)CycAccess.sharedCycAccessInstance;
            } catch (Throwable e) {
                debugln(e);
            }
        }
        if (cycAccessInstances.size()<MAX_THREADS) {
            try {
                CycAPI cycapi = new CycAPI(hostname,port);
                setInstance(thread,cycapi);
                return cycapi;
            } catch (Throwable e) {
                throw new RuntimeException(e);
            }
        }
        return(CycAPI)CycAccess.sharedCycAccessInstance;
    }

    public static void setSharedCycAccessInstance(CycAccess sharedCycAccessInstance) {
        if (sharedCycAccessInstance!=null) {
            CycAccess.sharedCycAccessInstance = sharedCycAccessInstance;
        }
    }

    static public String getHost() {
        return hostname;
    }
    static public int getPort() {
        return port;
    }
    static public void setHost(String h) {
        if (!h.equals(hostname)) {
            hostname = h;
            //CycAccess.sharedCycAccessInstance = null;
        }
    }
    static public void setPort(int p) {
        if (p!=port) {
            //CycAccess.sharedCycAccessInstance = null;
            port = p;
        }
    }

    public void close() {
        // super.close();
    }

    /*
static public CycAPI current() {
 CycAccess cycAccess = null;
 try {
      cycAccess = CycAccess.current();
      return (CycAPI)cycAccess;
 } catch (Throwable e) {
     cycAccess = currentInstance();
 }
 return (CycAPI)cycAccess;

}        */


    /*(FI-REASSERT '(#$ist #$BaseKB (#$implies (#$and (#$doom:gameCollection (#$TLVariableFn 0 "?COL1") (#$TLVariableFn 1 "?STRING")) (#$doom:gameType (#$TLVariableFn 2 "?OBJ") (#$TLVariableFn 1 "?STRING"))) (#$ist #$DoomCurrentStateMt (#$isa (#$TLVariableFn 2 "?OBJ") (#$TLVariableFn 0 "?COL1"))))) '#$BaseKB ':DEFAULT ':FORWARD)*/

    // Tries to paraphrase the sentence
    public static String paraphrase(Object cycl) {
        String para = "";
        if (cycl instanceof CycObject) {
            try {
                para = (String)current().converseObject("(generate-phrase '"+((CycObject)cycl).cyclifyWithEscapeChars()+")");
                if (para.length()>2) {
                    return para;
                }
            } catch (Exception e) {
            }
        }
        if (cycl instanceof CycList) {
            CycList cyclst = (CycList)cycl;
            try {
                para = (String)current().converseObject("(generate-phrase '"+cyclst.cyclifyWithEscapeChars()+")");
                if (para.length()>2) {
                    return para;
                }
            } catch (Exception e) {
            }
            Object[] lst = cyclst.toArray();
            para = "";
            for (int i=0;i<lst.length;i++) {
                Object ele = lst[i];
                if (ele instanceof CycVariable) {
                } else {
                    para = para + paraphrase(ele)+" ";
                }
            }
            cycl = cyclst.getDottedElement();
            if (cycl!=null) {
                return para + paraphrase(cycl);
            }
            return para + "";
        }
        return ""+cycl;
    }


    static public CycList newCycList(Object a,Object b, Object c) {
        CycList list = new CycList();
        list.add(a);
        list.add(b);
        list.add(c);
        return list;
    }

    static public void findPlanTest() {
        try {
            CycList plan = findPlans(newCycList(c("doFuelDevice"),c("Trucker001"),c("SemiTrailer-Truck-001")),c("OpenCycExampleTransportationPlanningDomaingetDataMt()"));
            debugln(""+ plan);
        } catch (Throwable e) {
            debugln(e);
        }
    }

    static public CycList findPlans(CycList goal, CycObject mt) throws CycApiException {
        return(currentInstance().converseList("(shop-find-plans '"+goal.cyclify()+ " " + mt.cyclify()+ " 9)"));
    }



    public CycConstant findOrCreate(String constantName) {
        try {
            CycConstant constant =  findConstant(constantName);
            if (constant==null) {
                if (constantName.startsWith("#$")) {
                    constantName = constantName.substring(2);
                }
                constant = makeCycConstant(constantName);
            }
            return constant;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public CycConstant findConstant(String constantName) {
        Object constant = null;
        try {
            constant = getConstantByName(constantName);
            if (!(constant instanceof CycConstant)) {
                if (constantName.startsWith("#$")) {
                    constantName = constantName.substring(2);
                }
                CycList list = converseList("(old-constant-names \"" + constantName + "\")");
                if (list.size()>0) {
                    constant = list.first();
                    debugln("findConstant: "+constantName+" => " + list);
                }
            }
        } catch (Throwable e) {
            debugln(e);
        }
        return(CycConstant)constant;        
    }


    /**
     * Returns a new <tt>CycConstant</tt> object using the constant name, recording bookkeeping
     * information and archiving to the Cyc transcript.
     * 
     * @param name Name of the constant. If prefixed with "#$", then the prefix is removed for
     *        canonical representation.
     * 
     * @return a new <tt>CycConstant</tt> object using the constant name, recording bookkeeping
     *         information and archiving to the Cyc transcript
     * 
     * @throws UnknownHostException if cyc server host not found on the network
     * @throws IOException if a data communication error occurs
     * @throws CycApiException if the api request results in a cyc server error
     */
    public CycConstant makeCycConstant(String name)
    throws UnknownHostException, IOException, CycApiException {
        String constantName = name;

        if (constantName.startsWith("#$")) {
            constantName = constantName.substring(2);
        }

        CycConstant cycConstant = getConstantByName(name);

        if (cycConstant != null) {
            return cycConstant;
        }

        String command = wrapBookkeeping("(ke-create-now \"" + constantName + "\")");
        Object object = converseObject(command);

        if (object instanceof CycConstant) {
            cycConstant = (CycConstant) object;
        } else {
            throw new CycApiException("Cannot create new constant for " + name);
        }
        CycObjectFactory.addCycConstantCache(cycConstant);

        return cycConstant;
    }

    public CycConstant getKnownConstantByName(String constantName)
    throws IOException, UnknownHostException, CycApiException {
        CycConstant cycConstant = findConstant(constantName);

        if (cycConstant == null) {
            throw new CycApiException("Expected constant not found " + constantName);
        }

        return cycConstant;
    }


    //static public Hashtable cycConstantByName = new Hashtable(400);

    static public CycConstant c(String named) {
        String originalName = named;
        CycAccess.sharedCycAccessInstance =  daxclr.inference.CycAPI.current();
        if (named.startsWith("#$")) named=named.substring(2);
        //  Character char = new Character(named.charAt(0));
        named = cleanName(named);
        CycConstant cycname = null;
        try {
            cycname = currentInstance().findOrCreate(named);
        } catch (Exception e) {
            debugln(e);
        }
        return cycname;
    }

    /*
   public CycConstant makeCycConstant(String name) {
      try {
          debugln("makeCycConstant: "+name);
          return super.makeCycConstant(name);
      } catch (Throwable e) {
          debugln(e);
      }
      return null;        
  }
      */

    public void destroy() {
        closeTelnet();
    }

    static void setInstance(Thread thread, CycAPI cycapi) {
        if (cycAccessInstances == null) {
            cycAccessInstances = new HashMap<Thread, CycAPI>();
        }

        if (!(CycAccess.sharedCycAccessInstance instanceof CycAPI)) {
            CycAccess.sharedCycAccessInstance = cycapi;
        }
        cycAccessInstances.put(thread,cycapi);
    }

    protected void commonInitialization_Unused() { //throws IOException, CycApiException
        setInstance(Thread.currentThread(),this);
        if (Log.current == null) {
            try {
                Log.makeLog("cyc-api.log");
            } catch (Throwable e) {
            }
        }
        try {
            // initializeConstants();
        } catch (Throwable e) {
            debugln(e);
        }
        System.err.println("commonInitialization() threads = " + cycAccessInstances.size());
    }

    static void debugln(Throwable e) {
        try {
            e.printStackTrace(System.err);
        } catch (Throwable eeee) {
        }
    }
    static void debugln(String e) {
        try {
            System.err.println(e);
        } catch (Throwable eeee) {
        }
    }


    /**
     * Assert that the cycFort is a collection in the UniversalVocabularyMt. The operation will be
     * added to the KB transcript for replication and archive.
     * 
     * @param cycFortName the collection element name
     * @param collectionName the collection name
     * 
     * @throws UnknownHostException if cyc server host not found on the network
     * @throws IOException if a data communication error occurs
     * @throws CycApiException if the api request results in a cyc server error
     */
    public void assertIsa(String cycFortName, 
                          String collectionName)
    throws IOException, UnknownHostException, CycApiException {
        assertGaf(universalVocabularyMt, 
                  isa, 
                  getKnownConstantByName(cycFortName), 
                  getKnownConstantByName(collectionName));
    }

    public void assertIsa(CycFort what, CycFort type,CycObject mt) {
        assertWithTranscriptNoWffCheckJava(newCycList(c("isa"),what,type),mt);

    }
    public void assertIsa(CycFort what, CycFort type) {
    	assertIsa(what,type,getVocabMt());
    }

    static public CycObject findOrCreateMt(String c) {
        try {
            CycObject mt = current().findOrCreate(c);
            current().assertIsa(c,"Microtheory","BaseKB");
            return mt;
        } catch (Exception e) {
            throw new RuntimeException(e);             // readAssertion
        }
    }

    public void assertWithTranscriptAndBookkeeping(CycList sentence, CycObject mt)
    throws IOException, UnknownHostException, CycApiException {
        try {
            super.assertWithTranscriptAndBookkeeping(sentence,mt);
        } catch (CycApiException e) {
            throw new CycApiException(""+converseObject("(HL-EXPLANATION-OF-WHY-NOT-WFF "+sentence.stringApiValue()+" "+ makeELMt(mt).stringApiValue()+ ")"),e);
        }
    }

    /**
     * Asserts the given sentence, and then places it on the transcript queue.
     * 
     * @param sentence the given sentence for assertion
     * @param mt the microtheory in which the assertion is placed
     * 
     * @throws UnknownHostException if cyc server host not found on the network
     * @throws IOException if a data communication error occurs
     * @throws CycApiException if the api request results in a cyc server error
     */

    public void assertWithTranscript(CycList sentence, CycObject mt)
    throws IOException, UnknownHostException, CycApiException {
        try {
            super.assertWithTranscript(sentence,mt);
        } catch (CycApiException e) {
            throw new CycApiException(""+converseObject("(HL-EXPLANATION-OF-WHY-NOT-WFF "+sentence.stringApiValue()+" "+ makeELMt(mt).stringApiValue()+ ")"),e);
        }
    }


    public CycList getAsserions(CycFort mt) {
        return(CycList)assertIdsToIsts(getAsserionIds(mt));
    }

    public CycList getAsserionIds(CycFort str) {
        String term = str.stringApiValue(); 
        CycList list = new CycList();
        int nth=0;
        try {
            while (true) {
                list.add(this.converseInt("(assertion-id (nth "+(nth++)+" (all-term-assertions "+term+")))"));
            }
        } catch (Exception e) {
        }
        return list;
    }
    public CycList assertIdsToIsts(CycList ids) {
        CycList list = new CycList(ids.size());
        Iterator its = ids.iterator();
        while (its.hasNext()) {
            int I = ((Integer)its.next()).intValue();
            list.add(getAssertion(I));
        }
        return list;
    }




    public CycList getAssertion(int id) {
        try {
            CycList list = converseList("(ASSERTION-EL-IST-FORMULA (find-assertion-by-id "+id+"))");
            if (list.size()==0) return null;
            return list;
        } catch (Throwable e) {
            e.printStackTrace(System.err);
            return null;
        }
    }

    public void setCycConnection(CycConnection c) {
        super.cycConnection = c;
    }

    public CycConnectionInterface getCycConnection() {
        return cycConnection;
    }

    //  public void setCommunicationMode(int mode) {
    //    getConnection().communicationMode = mode;
    // }

    public CycConnection getConnection() {
        return(CycConnection)getCycConnection();
    }

    public CycList getAssertionMetadata(int id) {
        try {
            // setCommunicationMode(CycConnection.BINARY_MODE);
            CycList list = converseList("(ASSERTION-EL-IST-FORMULA (find-assertion-by-id "+id+"))");
            if (list.size()==0) return null;
            CycList asrt = new CycList();                
            asrt.add(list.get(2));
            asrt.add(list.get(1));
            asrt.add(converseObject("(ASSERTION-DIRECTION (find-assertion-by-id "+id+"))"));
            asrt.add(converseObject("(ASSERTION-STRENGTH (find-assertion-by-id "+id+"))"));
            asrt.add(converseObject("(DEDUCED-ASSERTION? (find-assertion-by-id "+id+"))"));
            asrt.add(converseObject("(ASSERTED-WHEN (find-assertion-by-id "+id+"))"));
            asrt.add(converseObject("(ASSERTED-BY (find-assertion-by-id "+id+"))"));
            return asrt;
        } catch (Throwable e) {
            e.printStackTrace(System.err);
            return null;
        }
    }

    static public String removeChars(String originalName,String chars) {
        return replaceChars(originalName,chars,"");
    }
    static public String replaceChars(String originalName,String chars,String repl) {
        for (int i=0;i<chars.length();i++)
            originalName = originalName.replace(""+chars.charAt(i),repl);
        return originalName;
    }
    static public String cleanName(String name) {
        name = "" + name;
        if (name.startsWith("#$")) {
            name = name.substring(2);
        }
        name = name.replace('.', ':');
        return replaceChars(name,"$() @!$%'^&*+","_");
        //name = prefixUpper("JAVA:", name);
    }



    //    getConnection().isValidBinaryConnection()

    static String NEWPREFIX = "doom:";


    static public void converseVoidSafe(String safe) {
        try {
            current().converseVoid(safe);
        } catch (Exception e) {
            debugln(e);
        }
    }


    /**
     * cyclify a  string
     */
    static public Object cyclify(String sentence) throws CycApiException {
        return new LispReader(CycAccess.sharedCycAccessInstance).read(sentence);
    }

    static public void assertWithTranscriptNoWffCheckJava(CycList sentence,CycObject mt) {
        try {
            debugln(sentence + " " + mt);
            current().converseObject("(without-wff-semantics (cyc-assert '"+sentence.cyclify()+" #$"+mt + " '(:FORWARD)))");
        } catch (Exception e) {
            debugln(e);
        }
    }

    static public void assertWithTranscriptNoWffCheckJava(String sentence,CycObject mt) {
        try {
            assertWithTranscriptNoWffCheckJava((CycList)cyclify(sentence), mt);
        } catch (Exception e) {
            debugln(e);
        }
    }

    public Object assertGafSingleValued(CycObject mt, CycObject pred, CycObject arg1, Object newvalue) {
        Object oldval = null;                                                                           //mt.cyclify()
        try {
            String message = " (without-wff-semantics ";
            message +="(cyc-query '(" + pred.cyclify() + " " + arg1.cyclify() +" ?PREV) #$EverythingPSC '(:backchain 0 :number 1 :time 30))";
            message +=")";
            CycList al =  converseList(message);
            Iterator its = al.iterator();
            if (!its.hasNext()) {
            }
            if (its.hasNext()) {
                CycList bs = (CycList)its.next();
                oldval = getCdr((CycList)bs.get(0));
                debugln("oldvalue " + oldval );
                clearSlot(arg1,pred,mt);
            } else {
                debugln("no answers "+message);
            }
            if (newvalue!=null && !newvalue.equals(CYC_NIL)) {
                assertWithTranscriptNoWffCheckJava(newCycList(pred,arg1,newvalue),mt);
            }
        } catch (Exception e) {
            debugln(e);
        }
        return oldval;
    }

    public Object assertGafSingleValued2(CycObject pred, CycObject arg1, Object newvalue, CycFort mt) {
        Object oldval = null;
        CycList gaf = newCycList(pred,arg1,new CycSymbol("NIL"));
        try {
            String message = " (without-wff-semantics ";
            message +="(cyc-query '(#$ist ?MT (" + pred.cyclify() + " " + arg1.cyclify() +" ?PREV)) #$EverythingPSC '(:backchain 0 :time 30))";
            message +=")";
            CycList al =  converseList(message.toString());
            Iterator its = al.iterator();
            CycObject oldmt = null;
            if (!its.hasNext()) {
                debugln("no answers "+message);
            }
            if (its.hasNext()) {
                CycList bs = (CycList)its.next();
                oldmt = (CycObject)getCdr((CycList)bs.get(0));
                oldval = getCdr((CycList)bs.get(1));
                debugln("oldvalue " + oldval + " in  "+oldmt);
            }
            if (newvalue!=null && !newvalue.equals(CYC_NIL)) {
                gaf.set(2,newvalue);
                assertWithTranscriptNoWffCheckJava(gaf,mt);
            }
        } catch (Exception e) {
            debugln(e);
        }
        return oldval;
    }

    //                                         Mt : DoomCurrentStateMt
    // (cyc-query '(#$doom:anglesOf #$doom:idLight_light_51 (#$doom:Point3Fn 19.056974411010742 124.34951782226563 100.32577514648438)) #$DoomCurrentStateMt)
    // (without-wff-semantics (cyc-query '(#$doom:anglesOf #$doom:idLight_light_51 (#$doom:Point3Fn X ?Y ?Z)) #$DoomCurrentStateMt))
    // (without-wff-semantics (cyc-query '(#$doom:anglesOf #$doom:idLight_light_51 ?XYZ) #$DoomCurrentStateMt))

    // (nth 0 (without-wff-semantics (cyc-query '(#$doom:anglesOf #$doom:idLight_light_51 ?XYZ) #$DoomCurrentStateMt)))
    //(cyc-unassert (list #$doom:anglesOf #$doom:idLight_light_51 (cdr (nth 0 (nth 0 (without-wff-semantics (cyc-query '(#$doom:anglesOf #$doom:idLight_light_51 ?XYZ) #$DoomCurrentStateMt)))))) #$DoomCurrentStateMt)

    //(cyc-unassert (list #$doom:anglesOf #$doom:idLight_light_51 (cdr (nth 0 (nth 0 (without-wff-semantics (cyc-query '(#$doom:anglesOf #$doom:idLight_light_51 ?XYZ) #$DoomCurrentStateMt)))))) #$DoomCurrentStateMt)

    //(cdolist (bset (without-wff-semantics (cyc-query '(#$doom:anglesOf #$doom:idLight_light_51 ?XYZ) #$DoomCurrentStateMt))) (unassert (cdar bset))))
    //
    /*     (clet (resultbuf nil)  (cdolist (bset (without-wff-semantics (cyc-query '
                                                                       (#$doom:originOf #$doom:idLight_light_51 ?XYZ) #$DoomCurrentStateMt)))
          (cyc-unassert (list #$doom:originOf #$doom:idLight_light_51 (cdar bset)) #$DoomCurrentStateMt) (csetq resultbuf (cons (cdar bset) resultbuf)
    
      */
    // doom:originOf  



    //(cdolist (bset (without-wff-semantics (cyc-query '(#$doom:originOf #$doom:idLight_light_51 ?XYZ) #$DoomCurrentStateMt))) (cyc-unassert (list #$doom:originOf #$doom:idLight_light_51 (cdar bset)) #$DoomCurrentStateMt) (cdar bset))
    static public void removeSlot(CycFort cycobject, CycObject cycslot,CycObject cycmt) {
        String message = "(cdolist (bset (without-wff-semantics (cyc-query '("+cycslot.cyclify()+" "+cycobject.cyclify()+" ?XYZ) "+cycmt.cyclify()+"))) (cyc-unassert (list "+cycslot.cyclify()+" "+cycobject.cyclify()+" (cdar bset)) "+cycmt.cyclify()+") (cdar bset))";
        try {
            current().converseVoid(message);
        } catch (Exception e) {
            debugln(e);
        }
    }

    //(cdolist (bset (without-wff-semantics (cyc-query '(#$doom:originOf #$doom:idLight_light_51 ?XYZ) #$DoomCurrentStateMt))) (cyc-unassert (list #$doom:originOf #$doom:idLight_light_51 (cdar bset)) #$DoomCurrentStateMt))
    static public void clearSlot(CycObject cycobject, CycObject cycslot,CycObject cycmt) {
        if (cycobject==null||cycslot==null||cycmt==null) {
            return;
        }
        String message = "(cdolist (bset (without-wff-semantics (cyc-query '("+cycslot.cyclify()+" "+cycobject.cyclify()+" ?XYZ) "+cycmt.cyclify()+"))) (cyc-unassert (list "+cycslot.cyclify()+" "+cycobject.cyclify()+" (cdar bset)) "+cycmt.cyclify()+"))";
        try {
            current().converseVoid(message);
        } catch (Exception e) {
            debugln(e);
        }
    }

    public void unassertGaf(CycList sentence, CycObject mt) {
        String message = "(cyc-unassert '"+sentence.cyclify()+" "+mt.cyclify()+")";
        try {
            debugln(message);
            converseObject(message);
        } catch (Exception e) {
            debugln(e);
        }
    }


    //public void assertGaf(CycObject mt, CycFort predicate, CycFort arg1, CycFort arg2)throws IOException, UnknownHostException, CycApiException {

    static public void assertGaf(CycObject mt, CycObject pred, CycObject arg1, Object value ) {
        CycList assertme = new CycList(pred); //"#$hasFieldValue"
        assertme.add(arg1);
        assertme.add(value);
        assertWithTranscriptNoWffCheckJava(assertme, mt);
    }


    static public CycSymbol CYC_NIL = new CycSymbol("NIL");
    static public CycSymbol CYC_T = new CycSymbol("T");

    /**
      * Preforms query in mt with parameters
      *   then unasserts the insanciated gafs derived from the query
      *
      *  NOTE:  Only if they exist in the same microtheory as the query is in.
      *   see queryMatch for how these insanciated gafs are produced
      *
      */
    public ArrayList<CycList> deleteMatchGaf(CycList query, CycFort mt,int maxBackchains, int maxAnswers,int maxSeconds) {
        ArrayList<CycList> al = queryMatch(query, mt, maxBackchains, maxAnswers,maxSeconds);
        Iterator<CycList> its = al.iterator();
        while (its.hasNext()) {
            try {
                unassertGaf(its.next(), mt);
            } catch (Exception e) {
            }
        }
        return al;
    }

    /**
     * Preforms query in mt with parameters
     *   returns the insanciated gafs derived from the query
     *
     *   a query of (#$isa ?X #$Dog) in #$BiologyMt
     *     will return a ArrayList with a single CycList formula containing:
     *     (#$isa (#$GenericInstanceFn #$Dog) #$Dog)
     *
     */

    public ArrayList<CycList> queryMatch(CycList query, CycFort mt, int maxBackchains, int maxAnswers, int maxSeconds) {

        ArrayList<CycList> match = new CycList();
        try {
            CycList results = queryWithMaximums(query, mt, maxBackchains, maxAnswers, maxSeconds);
            Iterator its = results.iterator();
            while (its.hasNext()) {
                try {
                    CycList bindingset = (CycList) its.next();
                    CycList result = null;// replaceVarsWithBindingSet(query,bindingset).getFormula();
                    debugln("" + result);
                    match.add(result);
                } catch (Exception e) {
                    debugln(e);
                }
            }
        } catch (Exception ee) {
            debugln(ee);
        }
        return match;
    }

    public CycList queryWithMaximums(CycList query, CycFort mt,
                                     int maxBackchains, int maxAnswers,
                                     int maxSeconds) throws IOException,
    CycApiException {
        CycList message = new CycList();
        message.add("(clet ((*cache-inference-results* nil) ");
        message.add("       (*compute-inference-results* nil) ");
        message.add("       (*unique-inference-result-bindings* t) ");
        message.add("       (*generate-readable-fi-results* nil)) ");
        message.add("  (without-wff-semantics ");
        message.add("    (cyc-query '" + query.cyclify() + " ");
        message.add("                  " + mt.cyclify() + " ");
        message.add("                  '(:backchain " + maxBackchains +
                    " :number " + maxAnswers + " :time " + maxSeconds +
                    "))))");
        return converseList(message.toString());
    }

    //static public class QueryLiteral extends CycList
    //{
    //}
    /*
static public QueryLiteral replaceVarsWithBindingSet(CycList query, ArrayList bindingset) {
QueryLiteral querylit = new QueryLiteral(query);
Iterator bindings = bindingset.iterator();
while (bindings.hasNext()) {
CycList binding = (CycList) bindings.next();
CycVariable variable = (CycVariable) binding.first();
Object value = binding.rest();
querylit.substituteVariable(variable, value);
}
return querylit;
}
              */

    static public CycFort getVocabMt() {
        return c("DoomVocabularyMt");
    }
    static public CycFort getDataMt() {
        return c("DoomSituationMt");
    }

    static public CycConstant makeTypedCycConstant(String type, String name) {
        CycConstant nameC = null;
        CycConstant typeC = null;
        try {
            nameC = c(name);
            typeC = c(type);
            current().assertIsa(typeC, c("Collection"), getVocabMt());
        } catch (Exception e) {
            debugln(e);
        }
        try {
            current().assertIsa(nameC, typeC, getVocabMt());
        } catch (Exception e) {
            debugln(e);
        }
        return nameC;
    }

    public Object[] converse(Object command)
    throws IOException, UnknownHostException, CycApiException {
        return super.converse(command);
    }

    public Object converseObject(Object command) {
        try {
            return super.converseObject(command);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public boolean converseBoolean(Object command) {
        try {
            return super.converseBoolean(command);
        } catch (Exception e) {
            debugln(e);
            throw new RuntimeException(e);
        }
    }

    public CycList converseList(Object command) {
        try {
            Object res = super.converseObject(command);
            if (res instanceof CycSymbol) {
                if (CYC_NIL.equals(res)) return new CycList();
                if (CYC_T.equals(res)) return new CycList(CYC_NIL);
                return new CycList(res);
            }
            if (res instanceof CycList) return(CycList)res;
            return(CycList)res;    
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

	static Set<Object> getVariableSet(String varname, CycList results) {
		if (results == null)
			return new HashSet<Object>(0);
		Set<Object> bindings = new HashSet<Object>(results.size());
		Iterator its = results.iterator();
		while (its.hasNext()) {
			Object cycpred = its.next();
			if (cycpred instanceof List) {
				Iterator sits = ((List) cycpred).iterator();
				while (sits.hasNext()) {
					cycpred = sits.next();
					if (cycpred instanceof CycList) {
						CycList vs = (CycList) cycpred;
						String varcanidate = "" + vs.get(0);
						if (varcanidate.contains(varname)) {
							vs.remove(0);
							bindings.add(getValueOf(vs));
						}
						;
					}
				}
			}
		}
		return bindings;
	}

	static public Object getValueOf(CycList vs) {
		switch (vs.size()) {
		case 0:
			return null;
		case 1: {
			if (!vs.isProperList()) {
				return vs.getDottedElement();
			}
			return vs.get(0);
		}
		}
		return vs;
	}
/*
	static String toPropName(Object pred) {
		return getResolverMap().toString(pred);
	}
*/
	static CycObject getDynamicMt() {
		return findOrCreateMt("DoomCurrentStateMt");
	}

	static CycObject getQueryMt() {
		return findOrCreateMt("EverythingPSC");
	}

	static CycObject getStaticMt() {
		return findOrCreateMt("DoomStaticStateMt");
	}

	public InferenceAPI getTermToObjectConverter(Class<? extends CycObject> object) {
		return this;
	}
	public InferenceAPI getObjectToTermConverter(Class<? extends Object> class1) {
		return this;
	}

	public Object createObjectFromTerm(Object term) {
		// TODO Auto-generated method stub
		return term;
	}

	public Object createTermFromObject(Object term) {
		// TODO Auto-generated method stub
		return term;
	}


}








