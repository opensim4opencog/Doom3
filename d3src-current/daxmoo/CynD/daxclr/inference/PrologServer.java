package daxclr.inference;

/**
 * Description TODO
 *
 * @version $Id: PrologClientAsciiHandler.java,v 1.9 2002/10/30 15:51:48 stephenreed Exp $
 * @author Douglas R. Miles
 *
 * <p>Copyright 2001 Cycorp, Inc., license is open source GNU LGPL.
 * <p><a href="http://www.opencyc.org/license.txt">the license</a>
 * <p><a href="http://www.opencyc.org">www.opencyc.org</a>
 * <p><a href="http://www.sourceforge.net/projects/opencyc">OpenCyc at SourceForge</a>
 * <p>
 * THIS SOFTWARE AND KNOWLEDGE BASE CONTENT ARE PROVIDED ``AS IS'' AND
 * ANY EXPRESSED OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO,
 * THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A
 * PARTICULAR PURPOSE ARE DISCLAIMED.  IN NO EVENT SHALL THE OPENCYC
 * ORGANIZATION OR ITS CONTRIBUTORS BE LIABLE FOR ANY DIRECT,
 * INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES
 * (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR
 * SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION)
 * HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT,
 * STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE)
 * ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE AND KNOWLEDGE
 * BASE CONTENT, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */

//CycService import java.lang.reflect.*;
import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.io.StreamTokenizer;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.HashMap;
import java.util.Map;
import org.opencyc.api.CfaslInputStream;
import org.opencyc.api.CfaslOutputStream;



public class PrologServer extends Thread {
    static public PrologAPI prologapi;
    static Map pservers = new HashMap(10);
    static public int baseport = 3700;

    static public PrologServer bindServers(int port) {
    	PrologServer thread = (PrologServer)pservers.get(port);
        if (thread==null) {
            try {
                thread = new PrologServer(prologapi,port);
                pservers.put(port,thread);
            } catch ( IOException io ) {
            }
        }
        return thread;
    }

    public int asciiPort = baseport+1;
    public int cfaslPort = baseport+17;
    public Thread asciiSrv = null;
    public Thread cfaslSrv = null;
    String bindhost = "127.0.0.1";

    public PrologServer() throws IOException {
        this(baseport);
    }
    public PrologServer(int port) throws IOException {
        this(PrologAPI.current(),port);
    }
    public PrologServer(PrologAPI pshell, int port) throws IOException {
        if (pshell!=null) {
            prologapi = pshell;
        }
        if (prologapi==null) prologapi = PrologAPI.current();
        asciiPort = port+1;
        cfaslPort = port+17;
        bindhost = InetAddress.getLocalHost().getHostAddress();
        String cychost = InetAddress.getByName(CycAPI.current().getConnection().getHostName()).getHostAddress();
        if (cychost.startsWith("10.1.") && bindhost.startsWith("10.10")) {
            bindhost = "10.0.1.7"; // VPN Host
        }
    }

    public static void main(String[] arg) throws Exception {
        PrologServer jshellserver = new PrologServer(Integer.parseInt(arg[0]));
        jshellserver.run();
    }

    static public void printback(PrintStream outstream,Object result) {
        System.out.println(CycAPI.cyclifyWithEscapeChars(result));
    }

    public void run() {
        try {
            CycAPI.currentInstance().converseVoid("(defparameter *doom-host* \""+bindhost+"\")");
            CycAPI.currentInstance().converseVoid("(defparameter *doom-port* "+asciiPort+")");
            CycAPI.currentInstance().converseVoid("(defparameter *doom-cfasl* "+cfaslPort+")");
            asciiSrv = (new Thread() {
                            public void run() {
                                try {
                                    ServerSocket serverSocket = new ServerSocket(asciiPort);
                                    System.out.println("Doom Java Server Bound on Ascii Port "+asciiPort+ " for host at " + bindhost);
                                    while (!this.interrupted()) {
                                        try {
                                            Socket client = serverSocket.accept();
                                            try {
                                                System.out.println("Ascii accepted " + client);
                                                PrologClientAsciiHandler prologclient = new PrologClientAsciiHandler(client);
                                                prologclient.start();
                                            } catch (Throwable eeee) {
                                                printback(System.out,eeee);
                                            }
                                        } catch (Throwable ee) {
                                            printback(System.out,ee);
                                        }

                                    }
                                } catch (Throwable eie) {
                                    printback(System.out,eie);
                                }
                            }
                        });
            asciiSrv.start();
            cfaslSrv = (new Thread() {
                            public void run() {
                                try {
                                    ServerSocket serverSocket = new ServerSocket(cfaslPort);
                                    System.out.println("Doom Java Server Bound on Cfasl Port "+cfaslPort+ " for host at " + bindhost);
                                    while (!this.interrupted()) {
                                        try {
                                            Socket client = serverSocket.accept();
                                            try {
                                                System.out.println("Cfasl accepted " + client);
                                                PrologClientCFASLHandler prologclient = new PrologClientCFASLHandler(client);
                                                prologclient.start();
                                            } catch (Throwable eeee) {
                                                printback(System.out,eeee);
                                            }
                                        } catch (Throwable ee) {
                                            printback(System.out,ee);
                                        }

                                    }
                                } catch (Throwable eie) {
                                    printback(System.out,eie);
                                }
                            }
                        });
            cfaslSrv.start();
        } catch (Throwable ee) {
            printback(System.out,ee);
        }
    }
    static public class PrologClientAsciiHandler extends Thread {
        boolean requested = false;
        Socket clientSock = null;                                         
        BufferedOutputStream out = null;
        BufferedInputStream in = null;
        String ACK200 = "";
        //private static CycSymbol QUIT_COMMAND = new CycSymbol("API-QUIT");

        public PrologClientAsciiHandler() {
        }

        public PrologClientAsciiHandler(Socket client) throws IOException {
            clientSock = client;
            out = new BufferedOutputStream(clientSock.getOutputStream());
            in = new BufferedInputStream(clientSock.getInputStream());
            //QUIT_COMMAND = null; // close after done
        }

        public void run() {
            (new Thread() {
                 public void run() {
                     while (true) {
                         try {
                             this.sleep(5000);
                             if (!requested) {
                                 PrologClientAsciiHandler.this.destroy();
                             }
                         } catch (InterruptedException i) {
                         }
                     }
                 }
             }).start();
            try {
                Object todo = readObject();
                requested = true;
                System.out.println("TODO: " + CycAPI.cyclifyWithEscapeChars(todo));
                Object result = prologapi.evalLisp(todo);
                writeObject(result);
                System.out.println(ACK200 + CycAPI.cyclifyWithEscapeChars(result));
            } catch (Throwable e) {
                e.printStackTrace(System.out);
                new PrintStream(out).println("500 \"" + e + "\"");
            }
            destroy();
        }

        public void destroy() {
            try {
                try {
                    out.flush();
                    out.close();
                } catch (IOException io) {
                }
                clientSock.close();
            } catch (IOException io) {
            }
        }

        @SuppressWarnings("deprecation")
		public Object readObject() throws IOException{
            LispReader cyclp = new LispReader();
            LispReader.verbosity = 10;
            try {
                Object todo = cyclp.read(new StreamTokenizer(in));
                requested = true;
                return todo;
            } catch (Throwable e) {
                e.printStackTrace(System.out);
                new PrintStream(out).println("500 \"" + e + "\"");
                return "500 \"" + e + "\"";
            }
        }
        public void writeObject(Object result) throws IOException{
            try {
                (new PrintStream(out)).println(ACK200 + CycAPI.cyclifyWithEscapeChars(result));
            } catch (Throwable e) {
                e.printStackTrace(System.err);
                System.err.println("500 \"" + e + "\"");
            }
        }
    }


    static public class PrologClientCFASLHandler extends PrologClientAsciiHandler {
        public Object readObject() throws IOException{
            return((CfaslInputStream)in).readObject();
        }
        public void writeObject(Object result) throws IOException{
            ((CfaslOutputStream)out).writeObject(result);
        }
        public PrologClientCFASLHandler(Socket client) throws IOException {
            clientSock = client;
            out = new CfaslOutputStream(clientSock.getOutputStream());
            in = new CfaslInputStream(clientSock.getInputStream());
            //QUIT_COMMAND = null; // close after done
        }
    }
}





