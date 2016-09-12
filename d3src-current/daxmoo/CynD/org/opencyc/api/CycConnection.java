package org.opencyc.api;

import java.io.*;

import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.UnknownHostException;

import java.util.Hashtable;
import java.util.Iterator;
import java.util.List;
import java.util.logging.Logger;

import org.opencyc.cycobject.CycFort;
import org.opencyc.cycobject.CycConstant;
import org.opencyc.cycobject.CycObject;
import org.opencyc.cycobject.CycList;
import org.opencyc.cycobject.CycSymbol;
import org.opencyc.cycobject.DefaultCycObject;

import org.opencyc.util.Log;
import org.opencyc.util.StringUtils;
import org.opencyc.util.TimeOutException;
import org.opencyc.util.Timer;
import org.opencyc.util.UUID;

/**
 * Provides a binary connection and an ascii connection to the OpenCyc server. The ascii connection
 * is legacy and its use is deprecated.
 * 
 * <p>
 * Collaborates with the <tt>CycAccess</tt> class which wraps the api functions.  CycAccess may be
 * specified as null in the CycConnection constructors when the binary api is used. Concurrent api
 * requests are supported for binary (cfasl) mode. This is implemented by two socket connections,
 * the first being for asynchronous api requests sent to Cyc, and the second for the asychronous
 * api responses received from Cyc.
 * </p>
 * 
 * @version $Id: CycConnection.java,v 1.77.2.1 2006/05/03 20:08:15 jantos Exp $
 * @author Stephen L. Reed <p><p><p><p><p>
 */
public class CycConnection implements CycConnectionInterface {
  /** Default host name for the OpenCyc server. */
  public static String DEFAULT_HOSTNAME = "127.0.0.1";

  //public static String DEFAULT_HOSTNAME = "207.207.8.185";

  /** Default base tcp port for the OpenCyc server. */
  public static final int DEFAULT_BASE_PORT = 3600;

  /** HTTP port offset for the OpenCyc server. */
  public static final int HTTP_PORT_OFFSET = 0;

  /** CFASL (binary) port offset for the OpenCyc server. */
  public static final int CFASL_PORT_OFFSET = 14;

  /** No api trace. */
  public static final int API_TRACE_NONE = 0;

  /** Message-level api trace. */
  public static final int API_TRACE_MESSAGES = 1;

  /** Detailed api trace. */
  public static final int API_TRACE_DETAILED = 2;

  /** Parameter that, when true, causes a trace of the messages to and from the server. */
  protected int trace = API_TRACE_NONE;

//  protected int trace = API_TRACE_MESSAGES;
//  protected int trace = API_TRACE_DETAILED;

  /** CFASL (binary) mode connnection to the Cyc server (preferred). */
  public static final int BINARY_MODE = 2;

  /** The binary interface input stream. */
  protected CfaslInputStream cfaslInputStream;

  /** The binary interface output stream. */
  protected CfaslOutputStream cfaslOutputStream;

  /** The name of the computer hosting the OpenCyc server. */
  protected String hostName;

  /** The tcp port from which the asciiPort and cfaslPorts are derived. */
  protected int basePort;

  /** The tcp port assigned to the binary connection to the OpenCyc server. */
  protected int cfaslPort;

  /** The tcp socket assigned to the binary connection to the OpenCyc server. */
  protected Socket cfaslSocket;

  /** The timer which optionally monitors the duration of requests to the OpenCyc server. */
  protected static final Timer notimeout = new Timer();

  /**
   * Indicates if the response from the OpenCyc server is a symbolic expression (enclosed in
   * parentheses).
   */
  protected boolean isSymbolicExpression = false;

  /**
   * A reference to the parent CycAccess object for dereferencing constants in ascii symbolic
   * expressions.
   */
  protected CycAccess cycAccess;

  /** outbound request serial id */
  static private int apiRequestId = 0;

  /** The default priority of a task-processor request. */
  public static final int DEFAULT_PRIORITY = 3;

  /** name of my api client */
  protected String myClientName = "api client";

  /**
   * Implements an association:  apiRequestId --> waiting thread info, where waiting thread info is
   * an array of two objects: 1.  the latch waiting for the response from the Cyc server
   * (number 1 in no longer valid @todo fix this description) 2.  the
   * api-request in CycList form Used when submitting concurrent requests to the task-processor.
   */
  protected Hashtable waitingReplyThreads = new Hashtable();

  /** handles responses from task-processor requests in binary communication mode. */
  protected TaskProcessorBinaryResponseHandler taskProcessorBinaryResponseHandler;

  /** Indicates to the taskProcessor response handlers that the server connection is closed. */
  protected boolean taskProcessingEnded = false;

  /** Indicates that the task processing thread is dead */
  protected boolean taskProcessingThreadDead = false;

  /**
   * Universally Unique ID that identifies this CycConnection to the Cyc server. It is used when
   * establishing the (second) asychronous socket connection.
   */
  protected UUID uuid;

  /** the logger */
  protected final Logger logger;
  
  /**
   * Constructs a new CycConnection using the given socket obtained from the parent AgentManager
   * listener.
   * 
   * @param cfaslSocket tcp socket which forms the binary connection to the OpenCyc server
   * 
   * @throws IOException when communication error occurs
   */
  public CycConnection(Socket cfaslSocket) throws IOException {
    logger = Logger.getLogger("org.opencyc.CycConnection");
    this.cfaslSocket = cfaslSocket;
    hostName = cfaslSocket.getInetAddress().getHostName();
    basePort = cfaslSocket.getPort() - CFASL_PORT_OFFSET;
    cycAccess = null;
    cfaslInputStream = new CfaslInputStream(cfaslSocket.getInputStream());
    cfaslInputStream.trace = trace;
    cfaslOutputStream = new CfaslOutputStream(cfaslSocket.getOutputStream());
    cfaslOutputStream.trace = trace;
  }

  /**
   * Constructs a new CycConnection object using the default host name and default base port numbe.  
   * When CycAccess is null as in this case, diagnostic output is reduced.
   * 
   * @throws UnknownHostException when the cyc server cannot be found
   * @throws IOException when communications error occurs
   * @throws CycApiException when an api error occurs
   */
  public CycConnection() throws IOException, UnknownHostException, CycApiException {
    this(DEFAULT_HOSTNAME, DEFAULT_BASE_PORT, null);
  }

  /**
   * Constructs a new CycConnection object using the default host name, default base port number
   * and the given CycAccess object.
   * 
   * @param cycAccess the given CycAccess object which provides api services over this
   *        CycConnection object
   * 
   * @throws CycApiException when a Cyc api exception occurs
   * @throws IOException when communication error occurs
   * @throws UnknownHostException when the cyc server cannot be found
   */
  public CycConnection(CycAccess cycAccess) throws IOException, UnknownHostException, CycApiException {
    this(DEFAULT_HOSTNAME, DEFAULT_BASE_PORT, cycAccess);
  }

  /**
   * Constructs a new CycConnection object using a given host name, the given base port number, the
   * given communication mode, and the given CycAccess object
   * 
   * @param hostName the cyc server host name
   * @param basePort the base tcp port on which the OpenCyc server is listening for connections.
   * @param cycAccess the given CycAccess object which provides api services over this
   *        CycConnection object
   * 
   * @throws IOException when a communications error occurs
   * @throws UnknownHostException when the cyc server cannot be found
   * @throws CycApiException when a Cyc API error occurs
   */
  public CycConnection(String hostName, int basePort, CycAccess cycAccess) throws IOException, UnknownHostException, CycApiException {
    logger = Logger.getLogger("org.opencyc.CycConnection");
    this.hostName = hostName;
    this.basePort = basePort;
    cfaslPort = basePort + CFASL_PORT_OFFSET;
    final ConnectionTimer connectionTimer = new ConnectionTimer();
    connectionTimer.start();
    this.cycAccess = cycAccess;
    initializeApiConnections();

    if (trace > API_TRACE_NONE) {
      Log.current.println("CFASL connection " + cfaslSocket);
    }

    uuid = UUID.randomUUID();
    initializeConcurrentProcessing();

    
    /** for testing the connection timer 
    try {
      Thread.sleep(100000);
    }
    catch (InterruptedException e) {
    }
    */
    connectionTimer.isCycConnectionEstablished = true;
  }

  public int getConnectionType() {
    return CycAccess.PERSISTENT_CONNECTION;
  }
  
  /**
   * Initializes the OpenCyc CFASL socket connections.
   * 
   * @throws IOException when a communications error occurs
   * @throws UnknownHostException when the cyc server cannot be found
   */
  private void initializeApiConnections()
                                 throws IOException, UnknownHostException {
    if (Log.current == null) {
      Log.makeLog("cyc-api.log");
    }
    cfaslSocket = new Socket(hostName, cfaslPort);
    int val = cfaslSocket.getReceiveBufferSize();
    cfaslSocket.setReceiveBufferSize(val * 2);
    cfaslSocket.setTcpNoDelay(true);
    cfaslSocket.setKeepAlive(true);
    cfaslInputStream = new CfaslInputStream(cfaslSocket.getInputStream());
    cfaslInputStream.trace = trace;
    cfaslOutputStream = new CfaslOutputStream(cfaslSocket.getOutputStream());
    cfaslOutputStream.trace = trace;
  }

  /**
   * Initializes the concurrent processing mode.  Use serial messaging mode to ensure the Cyc task
   * processors are initialized, then start this connection's taskProcessor response handler
   * thread.
   * 
   * @throws IOException when a communications error occurs
   * @throws UnknownHostException when the cyc server cannot be found
   * @throws CycApiException when a Cyc API error occurs
   */
  protected void initializeConcurrentProcessing()
                                         throws IOException, UnknownHostException, CycApiException {
    taskProcessorBinaryResponseHandler = 
      new TaskProcessorBinaryResponseHandler(Thread.currentThread(), this);
    
    // the start method will not return until the inbound socket
    // has had time to initialize 
    taskProcessorBinaryResponseHandler.start();
  }

  /**
   * Ensures that the api socket connections are closed when this object is garbage collected.
   */
  protected void finalize() {
    close();
  }

  /**
   * Close the api sockets and streams.
   */
  public void close() {
    if (isValidBinaryConnection(true)) {
      if (cfaslOutputStream != null) {
        CycList command;
        if (trace > API_TRACE_NONE) {
          Log.current.println("Closing server's api response socket associated with uuid: " + uuid);
        }
        command = new CycList();
        command.add(CycObjectFactory.makeCycSymbol("RELEASE-RESOURCES-FOR-JAVA-API-CLIENT"));
        command.add(uuid);
        try {
          cfaslOutputStream.writeObject(command);
        }
         catch (Exception e) {
          Log.current.printStackTrace(e);
          Log.current.println("Error closing server's api response socket " + e.getMessage());
        }
        if (trace > API_TRACE_NONE) {
          Log.current.println("Sending API-QUIT to server that will close its api request socket and its handling thread");
        }
        command = new CycList();
        command.add(CycObjectFactory.makeCycSymbol("API-QUIT"));

        try {
          cfaslOutputStream.writeObject(command);
        }
         catch (Exception e) {
          Log.current.printStackTrace(e);
          Log.current.println("Error quitting the api connection " + e.getMessage());
        }

        try {
          cfaslOutputStream.flush();
        }
         catch (Exception e) {
        }
      }
    }
    if (cfaslInputStream != null) {
      if (trace > API_TRACE_NONE) {
        Log.current.println("Closing cfaslInputStream");
      }

      try {
        cfaslInputStream.close();
      }
       catch (Exception e) {
        Log.current.printStackTrace(e);
        Log.current.println("Error finalizing the api connection " + e.getMessage());
      }
    }

    if (cfaslSocket != null) {
      if (trace > API_TRACE_NONE) {
        Log.current.println("Closing cfaslSocket");
      }

      try {
        cfaslSocket.close();
      }
       catch (Exception e) {
        Log.current.printStackTrace(e);
        Log.current.println("Error closing the api connection " + e.getMessage());
      }
    }

    taskProcessingEnded = true;

    if (trace > API_TRACE_NONE) {
      Log.current.println("Interrupting any threads awaiting replies");
    }

    interruptAllWaitingReplyThreads();

    try {
      taskProcessorBinaryResponseHandler.interrupt();
      taskProcessorBinaryResponseHandler.close();
      if (trace > API_TRACE_NONE) {
        Log.current.println("Waiting at most 500 milliseconds for the taskProcessorBinaryResponseHandler thread to die");
      }

      taskProcessorBinaryResponseHandler.join(500);

      if (!taskProcessingThreadDead) {
        if (trace > API_TRACE_NONE) {
          Log.current.println("The taskProcessorBinaryResponseHandler thread has not died, so continuing");
        }
      }
    }
    catch (Exception e) {
    }

    if (trace > API_TRACE_NONE) {
      Log.current.println("Connection closed for " + connectionInfo());
    }
  }

  /**
   * Return the name of the host to which the CycConnection is established.
   * 
   * @return the name of the Host to which this <tt>CycConnection</tt> is connected.
   */
  public String getHostName() {
    return this.hostName;
  }

  /**
   * Return the base port to which the CycConnection is established.
   * 
   * @return the port to which this <tt>CycConnection</tt> is connected.
   */
  public int getBasePort() {
    return this.basePort;
  }

  /**
   * Return the CFASL port to which the CycConnection is established.
   * 
   * @return the CFASL port to which this <tt>CycConnection</tt> is connected.
   */
  public int getCfaslPort() {
    return this.cfaslPort;
  }

  /**
   * Send a message to Cyc and return the <tt>Boolean</tt> true as the first element of an object
   * array, and the cyc response Symbolic Expression as the second element.  If an error occurs
   * the first element is <tt>Boolean</tt> false and the second element is the error message
   * string.
   * 
   * @param message the api command
   * 
   * @return an array of two objects, the first is an response status object either a Boolean
   *         (binary mode) or Integer (ascii mode), and the second is the response object or error
   *         string.
   * 
   * @throws IOException when a commuications error occurs
   * @throws CycApiException when a Cyc API error occurs
   */
  public Object[] converse(Object message)
                    throws IOException, CycApiException {
    return converse(message, 
                    notimeout);
  }

  /**
   * Send a message to Cyc and return the response code as the first element of an object array,
   * and the cyc response Symbolic Expression as the second element, spending no less time than
   * the specified timer allows but throwing a <code>TimeOutException</code> at the first
   * opportunity where that time limit is exceeded. If an error occurs the second element is the
   * error message string.
   * 
   * @param message the api command which must be a String or a CycList
   * @param timeout a <tt>Timer</tt> object giving the time limit for the api call
   * 
   * @return an array of two objects, the first is a Boolean response status object, and the second
   *         is the response object or error string.
   * 
   * @throws IOException when a communications error occurs
   * @throws TimeOutException when the time limit is exceeded
   * @throws CycApiException when a Cyc api error occurs
   * @throws RuntimeException if CycAccess is not present
   */
  public Object[] converse(Object message, 
                           Timer timeout)
                    throws IOException, TimeOutException, CycApiException {
    CycList messageCycList;
    if (message instanceof CycList) {
      messageCycList = (CycList) message;
    }
    else if (message instanceof String) {
      if (cycAccess == null) {
        throw new RuntimeException("CycAccess is required to process commands in string form");
      }
      messageCycList = cycAccess.makeCycList((String) message);
    }
    else {
      throw new CycApiException("Invalid class for message " + message);
    }
    messageCycList = substituteForBackquote(messageCycList, 
                                            timeout);
    return converseBinary(messageCycList, timeout);
  }

  /**
   * Substitute a READ-FROM-STRING expression for expressions directly containing a backquote
   * symbol.  This transformation is only required for the binary api, which does not parse the
   * backquoted expression.
   * 
   * @param messageCycList the given expression
   * @param timeout a <tt>Timer</tt> object giving the time limit for the api call
   * 
   * @return the expression with a READ-FROM-STRING expression substituted for expressions directly
   *         containing a backquote symbol
   * 
   * @throws IOException when a communication error occurs
   * @throws CycApiException when a Cyc api error occurs
   */
  protected CycList substituteForBackquote(CycList messageCycList, 
                                           Timer timeout)
                                    throws IOException, CycApiException {
    if (messageCycList.treeContains(CycObjectFactory.backquote)) {
      CycList substituteCycList = new CycList();
      substituteCycList.add(CycObjectFactory.makeCycSymbol(
                                  "read-from-string"));
      String tempString = messageCycList.cyclify();
      tempString = tempString.replaceAll("\\|\\,\\|", ",");
      substituteCycList.add(tempString);
      Object[] response = converseBinary(substituteCycList, 
                                         timeout);
      if ((response[0].equals(Boolean.TRUE)) && (response[1] instanceof CycList)) {
        CycList backquoteExpression = (CycList) response[1];
        return backquoteExpression.subst(CycObjectFactory.makeCycSymbol(
                                               "api-bq-list"), 
                                         CycObjectFactory.makeCycSymbol(
                                               "bq-list"));
      }
      throw new CycApiException("Invalid backquote substitution in " + messageCycList + 
                                "\nstatus" + response[0] + "\nmessage " + response[1]);
    }
    return messageCycList;
  }
  
  private class WaitingWorkerInfo {

    final SubLWorker worker;
    final boolean isReturnWholeTaskProcessorResponse;
    final CycList taskProcessorRequest;
    
    WaitingWorkerInfo(final SubLWorker worker, final CycList taskProcessorRequest, final boolean isReturnWholeTaskProcessorResponse) {
      this.worker = worker;
      this.taskProcessorRequest = taskProcessorRequest;
      this.isReturnWholeTaskProcessorResponse = isReturnWholeTaskProcessorResponse;
    }
    
    SubLWorker getWorker() {
      return worker;
    }
    
    CycObject getMessage() {
      return (CycObject)taskProcessorRequest.get(1);
    }
    
  }
  
  /**
   * Send a message to Cyc and return the response code as the first element of an object array,
   * and the cyc response Symbolic Expression as the second element, spending no less time than
   * the specified timer allows but throwing a <code>TimeOutException</code> at the first
   * opportunity where that time limit is exceeded. If an error occurs the second element is the
   * error message string. The concurrent mode of Cyc server communication is supported by Cyc's
   * pool of transaction processor threads, each of which can concurrently process an api request.
   * 
   * @param message the api command
   * @param timeout a <tt>Timer</tt> object giving the time limit for the api call
   * 
   * @return an array of two objects, the first is an Boolean response code, and the second is the
   *         response object or error string.
   * 
   * @throws IOException when a communication error occurs
   * @throws TimeOutException when the time limit is exceeded
   * @throws CycApiException when a Cyc api error occurs
   */
  public Object[] converseBinary(CycList message, Timer timeout) throws IOException, TimeOutException, CycApiException {
    DefaultSubLWorkerSynch worker = new DefaultSubLWorkerSynch(message, cycAccess, 
      false, timeout.getAllotedMSecs());
    Object[] result = new Object[2];
    try {
      result[1] = worker.getWork();
    } catch (IOException xcpt) {
      throw xcpt;
    } catch (TimeOutException xcpt) {
      throw xcpt;      
    } catch (CycApiServerSideException xcpt) {
      // @note: this implements a legacy API of converseBinary()
      result[0] = Boolean.FALSE;
      result[1] = xcpt.getMessage();
      return result;
    } catch (CycApiException xcpt) {
      throw xcpt;
    } catch (RuntimeException re) {
      throw re;
    } catch (Exception xcpt) {
      throw new RuntimeException(xcpt);
    }
    result[0] = worker.getStatus() == SubLWorkerStatus.FINISHED_STATUS ? Boolean.TRUE : Boolean.FALSE;
    return result;
  }
  
  public void cancelCommunication(SubLWorker worker) throws java.io.IOException {
    Integer id = worker.getId();
    if (id.intValue() < 0) {
      //@note serial communications cannot be canceled right now
      return;
    }
    String command = "(fif (" + "terminate-active-task-process"
        + " " + worker.getId() + " \"" + uuid + "\" " + ":cancel" + 
        ") '(ignore) '(ignore))";
    sendBinary(cycAccess.makeCycList(command));
    // the SubL implementation of CANCEL will send a CANCEL event back,
    // which will cleanup the waiting thread info and signal the termination 
    // event, so no need to perform event signaling and cleanup
  }
  
  
  public void abortCommunication(SubLWorker worker) throws java.io.IOException {
    Integer id = worker.getId();
    if (id.intValue() < 0) {
      //@note serial communications cannot be canceled right now
      return;
    }
    try {
      String command = "(fif (" + "terminate-active-task-process"
        + " " + worker.getId() + " \"" + uuid + "\" " + ":abort" + 
        ") '(ignore) '(ignore))";
      sendBinary(cycAccess.makeCycList(command));
    } finally {
    // the SubL implementation of ABORT will not send anything back,
    // so we do need to perform event signaling and cleanup
      worker.fireSubLWorkerTerminatedEvent(new SubLWorkerEvent(worker,
        SubLWorkerStatus.ABORTED_STATUS, null));
      waitingReplyThreads.remove(id);
    }
  }
  
  private boolean inAWTEventThread() {
    try {
	return false;
  //    return javax.swing.SwingUtilities.isEventDispatchThread();
    } catch (Throwable e) { return false; }
  }

  /**
   * Send a message to Cyc spending no less time than the specified timer allows but throwing a <code>TimeOutException</code> 
   * at the first opportunity where that time limit is exceeded. The concurrent mode of Cyc server communication 
   * is supported by Cyc's pool of transaction processor threads, each of which can concurrently process an api request.  The
   * SubLWorker object notifies the caller when the api response is aschronously received.
   * 
   * @param worker a <tt>SubLWorker</tt> object that notifies the caller when work is done
   * 
   * @throws IOException when a communication error occurs
   * @throws TimeOutException when the time limit is exceeded
   * @throws CycApiException when a Cyc api error occurs
   */
  public void converseBinary(final SubLWorker worker)
  throws IOException, TimeOutException, CycApiException {
    logger.finest("API request: " + worker.toString());
    if (cycAccess.isClosed()) {
      throw new CycApiException("Attempt to communicate to Cyc using a closed connection.");
    }
    /*if ((!worker.shouldIgnoreInvalidLeases()) && (!cycAccess.hasValidLease())) {
      throw new CycApiException("Attempt to communicate to Cyc using a connection with an invalid lease." +
                                "\nSubLCommand: " + worker.getSubLCommand().toPrettyCyclifiedString(""));
    }*/
    //System.out.println("worker: " + worker);
    if (false && (worker instanceof SubLWorkerSynch) && inAWTEventThread()) {
      throw new CycApiException("Invalid attempt to synchronously communicate with Cyc " + 
      "from the AWT event thread.\n\n" + worker);
    }
    CycSymbol taskProcessorRequestSymbol = CycObjectFactory.
      makeCycSymbol("task-processor-request");
    Integer id = null;
    CycList taskProcessorRequest = null;
    boolean isReturnWholeTaskProcessorResponse = false;
    CycList subLCommand = worker.getSubLCommand();
    if (subLCommand.first().equals(CycObjectFactory.makeCycSymbol("return-whole-task-processor-response"))) {
      isReturnWholeTaskProcessorResponse = true;
      subLCommand = (CycList)subLCommand.second();
    }
    if (subLCommand.first().equals(taskProcessorRequestSymbol)) {
      // client has supplied the task-processor-request form
      taskProcessorRequest = subLCommand;
      id = (Integer)subLCommand.third();
      taskProcessorRequest.set(6, uuid.toString());  // override the uuid to identify this client
    } else {
      id = nextApiRequestId();
      taskProcessorRequest = new CycList();
      taskProcessorRequest.add(taskProcessorRequestSymbol); // function
      taskProcessorRequest.add(subLCommand); // request
      taskProcessorRequest.add(id); // id
      taskProcessorRequest.add(new Integer(DEFAULT_PRIORITY)); // priority
      taskProcessorRequest.add(myClientName); // requestor
      taskProcessorRequest.add(CycObjectFactory.nil); // client-bindings
      taskProcessorRequest.add(uuid.toString()); // uuid to identify this client
    }
    final CycList actualRequest = (CycList) taskProcessorRequest.get(1);
    if (actualRequest.toString().startsWith("(FIF (TERMINATE-ACTIVE-TASK-PROCESS ")) {
      // override the uuid used to identify this client
      // (fif (terminate-active-task-process id uuid :cancel) (quote (ignore)) (quote (ignore)))
      final CycList temp = (CycList) actualRequest.second();
      temp.set(2, uuid.toString());
    }
    logger.finest("taskProcessorRequest: " + taskProcessorRequest.toPrettyCyclifiedString(""));
    WaitingWorkerInfo waitingWorkerInfo = new WaitingWorkerInfo(worker, taskProcessorRequest, isReturnWholeTaskProcessorResponse);
    // tell everyone this is getting started
    waitingReplyThreads.put(id, waitingWorkerInfo);
    SubLWorkerEvent event = new SubLWorkerEvent(worker, id); 
    worker.fireSubLWorkerStartedEvent(event);
    //start communication
    sendBinary(taskProcessorRequest);
  }

  /**
   * Returns the next apiRequestId.
   * 
   * @return the next apiRequestId
   */
  static public synchronized Integer nextApiRequestId() {
    return new Integer(++apiRequestId);
  }

  /**
   * Sends an object to the CYC server.  If the connection is not already open, it is opened.  The
   * object must be a valid CFASL-translatable: an Integer, Float, Double, Boolean, String, or cyc
   * object.
   * 
   * @param message the api command
   * 
   * @throws IOException when a communication error occurs
   */
  public synchronized void sendBinary(Object message)
                               throws IOException {
    logger.finest("sendBinary: " + message.toString());
    cfaslOutputStream.writeObject(message);
    cfaslOutputStream.flush();
  }

  /**
   * Receives an object from the CYC server.
   * 
   * @return an array of three objects, the first is a Boolean response, the second is the
   *         response object or error string, and the third is an indication that the otherwise
   *         good response contains an invalid object.
   * 
   * @throws IOException when a communications error occurs
   * @throws CycApiException when a Cyc API error occurs
   */
  public synchronized Object[] receiveBinary()
                                      throws IOException, CycApiException {
    cfaslInputStream.resetIsInvalidObject();
    final Object status = cfaslInputStream.readObject();
    cfaslInputStream.resetIsInvalidObject();
    final Object response = cfaslInputStream.readObject();
    final Object[] answer = { null, null, null };
    answer[1] = response;
    // TODO handle the invalid object in the callers of this seldom-used method. 
    answer[2] = new Boolean(cfaslInputStream.isInvalidObject());
    if ((status == null) || status.equals(CycObjectFactory.nil)) {
      answer[0] = Boolean.FALSE;

      if (trace > API_TRACE_NONE) {
        final String responseString = response.toString();
        Log.current.println("received error = (" + status + ") " + responseString);
      }
      return answer;
    }
    answer[0] = Boolean.TRUE;
    return answer;
  }

  /**
   * Receives a binary (cfasl) api request from a cyc server.  Unlike the api response handled by
   * the receiveBinary method, this method does not expect an input status object.
   * 
   * @return the api request expression.
   * 
   * @throws IOException when a communication error occurs
   * @throws CycApiException when a Cyc API exception occurs
   */
  public CycList receiveBinaryApiRequest()
                                  throws IOException, CycApiException {
    cfaslInputStream.resetIsInvalidObject();
    CycList apiRequest = (CycList) cfaslInputStream.readObject();
    return apiRequest;
  }

  /**
   * Sends a binary (cfasl) api response to a cyc server.  This method prepends a status object
   * (the symbol T) to the message.
   * 
   * @param message the given binary api response
   * 
   * @throws IOException when a communication error occurs
   * @throws CycApiException when a Cyc API error occurs
   */
  public void sendBinaryApiResponse(Object message)
                             throws IOException, CycApiException {
    CycList apiResponse = new CycList();
    apiResponse.add(CycObjectFactory.t);
    apiResponse.add(message);
    cfaslOutputStream.writeObject(apiResponse);
  }

  /**
   * Turns on the diagnostic trace of socket messages.
   */
  public void traceOn() {
    trace = API_TRACE_MESSAGES;
    cfaslInputStream.trace = trace;
    cfaslOutputStream.trace = trace;
  }

  /**
   * Turns on the detailed diagnostic trace of socket messages.
   */
  public void traceOnDetailed() {
    setTrace(API_TRACE_DETAILED);
  }

  /**
   * Turns off the diagnostic trace of socket messages.
   */
  public void traceOff() {
    setTrace(API_TRACE_NONE);
  }

  /**
   * Returns the trace value.
   * 
   * @return the trace value
   */
  public int getTrace() {
    return trace;
  }

  /**
   * Sets the socket messages diagnostic trace value.
   * 
   * @param trace the new socket messages diagnostic trace value
   */
  public void setTrace(int trace) {
    this.trace = trace;
    cfaslInputStream.trace = trace;
    cfaslOutputStream.trace = trace;
    if (taskProcessorBinaryResponseHandler != null)
      taskProcessorBinaryResponseHandler.inboundStream.trace = trace;
  }

  /** Answers true iff this is a valid binary (cfasl) connection to Cyc.
   * 
   * @return true iff this is a valid binary (cfasl) connection to Cyc
   */
  public boolean isValidBinaryConnection() {
    return isValidBinaryConnection(false);
  }

  /** Answers true iff this is a valid binary (cfasl) connection to Cyc.
   * 
   * @param isQuiet the indicator for no informational logging
   * @return true iff this is a valid binary (cfasl) connection to Cyc
   */
  public boolean isValidBinaryConnection(final boolean isQuiet) {
    if (cfaslSocket == null) {
      if (! isQuiet)
        Log.current.println("Invalid binary connection because cfaslSocket is null");
      return false;
    }

    if (! cfaslSocket.isConnected()) {
      if (! isQuiet)
        Log.current.println("Invalid binary connection because cfaslSocket is not connected");
      return false;
    }
    if ((taskProcessorBinaryResponseHandler == null) || 
            (taskProcessorBinaryResponseHandler.inboundSocket == null)) {
      if (! isQuiet)
        Log.current.println("Invalid binary connection because taskProcessorBinaryResponseHandler.inboundSocket is null");
      return false;
    }
    if (! taskProcessorBinaryResponseHandler.inboundSocket.isConnected()) {
      if (! isQuiet)
        Log.current.println("Invalid binary connection because taskProcessorBinaryResponseHandler.inboundSocket is not connected");
      return false;
    }
    return true;
  }

  /**
   * Returns connection information, suitable for diagnostics.
   * 
   * @return connection information, suitable for diagnostics
   */
  public String connectionInfo() {
    return "host " + hostName + ", cfaslPort " + cfaslPort;
  }

  /**
   * Gets the UUID that identifies this java api client connection.
   * 
   * @return the UUID that identifies this java api client connection
   */
  public UUID getUuid() {
    return uuid;
  }

  /**
   * Sets the client name of this api connection.
   * 
   * @param myClientName the client name of this api connection
   */
  public void setMyClientName(String myClientName) {
    this.myClientName = myClientName;
  }

  /**
   * Gets the client name of this api connection.
   * 
   * @return the client name of this api connection
   */
  public String getMyClientname() {
    return myClientName;
  }

  /**
   * Recovers from a socket error by interrupting all the waiting reply threads.  Each awakened
   * thread will detect the error condition and throw an IOExecption.
   */
  protected void interruptAllWaitingReplyThreads() {
    Iterator iter = waitingReplyThreads.values().iterator();

    while (iter.hasNext()) {
      WaitingWorkerInfo waitingWorkerInfo = (WaitingWorkerInfo) iter.next();
      if (trace > API_TRACE_NONE) {
        Log.current.println("Interrupting reply worker " + waitingWorkerInfo.getWorker());
      }
      try {
        waitingWorkerInfo.worker.cancel();
      } catch (java.io.IOException xcpt) {
        if (trace > API_TRACE_NONE) {
          Log.current.println("Could not interrupt reply worker " + waitingWorkerInfo.getWorker()
            + ": exception: " + xcpt);
      }
      }
    }
  }

  /**
   * Gets the dictionary of waiting reply thread information objects.
   * 
   * @return the dictionary of waiting reply thread information objects
   */
  public Hashtable getWaitingReplyThreadInfos() {
    return waitingReplyThreads;
  }

  /**
   * Resets the Cyc task processor which is currently processing the api-request specified by the
   * given id.  If none of the task processors is currently processessing the specified
   * api-request, then the reset request is ignored.  When reset, the Cyc task processor returns
   * an error message to the waiting client thread.  The error message consists of
   * "reset\nTHE-API-REQUEST".
   * 
   * @param id the id of the api-request which is to be interrupted and cancelled
   * 
   * @throws CycApiException when a Cyc API error occurs
   * @throws IOException when a communication error occurs
   */
  public void resetTaskProcessorById(Integer id)
                              throws CycApiException, IOException {
    resetTaskProcessorById(id.intValue());
  }

  /**
   * Resets the Cyc task processor which is currently processing the api-request specified by the
   * given id.  If none of the task processors is currently processessing the specified
   * api-request, then the reset request is ignored.  When reset, the Cyc task processor returns
   * an error message to the waiting client thread.
   * 
   * @param id the id of the api-request which is to be interrupted and cancelled
   * 
   * @throws CycApiException when a Cyc API error occurs
   * @throws IOException when a communications error occurs
   */
  public void resetTaskProcessorById(int id)
                              throws CycApiException, IOException {
    CycList command = new CycList();
    command.add(CycObjectFactory.makeCycSymbol("reset-api-task-processor-by-id"));
    command.add(myClientName);
    command.add(new Integer(id));
    cycAccess.converseCycObject(command);
  }

  /**
   * Class TaskProcessorBinaryResponseHandler handles responses from task-processor requests in
   * binary communication mode.
   */
  protected class TaskProcessorBinaryResponseHandler extends Thread {
    
    /** Maximum number of local cyc clients supported by this listener. */
    public static final int MAX_LOCAL_CLIENT_CLIENTS = 50;

    /** The socket which listens for new connections. */
    protected ServerSocket listenerSocket = null;

    /** The socket which receives asychronous inbound messages from the Cyc server. */
    protected Socket inboundSocket = null;

    /** The binary interface input stream which receives asychronous messages from the Cyc server */
    public CfaslInputStream inboundStream;

    /** 
     * The binary interface output stream, which is the output side of the bidirectional socket, is
     * used only to start up and close down the socket.
     */
    protected CfaslOutputStream inboundOutputStream;
    
    /** Reference to the parent thread which will sleep until this handler is initialized. */
    protected Thread parentThread;

    /** The (ignore) message from the Cyc server to test if the connection is alive. */
    protected CycList ignoreMessage;

    /** the parent CycConnection */
    protected CycConnection cycConnection;
    
    /** the synchronization object to ensure that the streams are ready */
    private Object lockObject;
    private boolean initialized;
    
    /** the indices into the task processor response object, which is a list */
    final static int TASK_PROCESSOR_RESPONSE_ID = 2;
    final static int TASK_PROCESSOR_RESPONSE_RESPONSE = 5;
    final static int TASK_PROCESSOR_RESPONSE_STATUS = 6;
    final static int TASK_PROCESSOR_RESPONSE_FINISHED_FLAG = 7;
          
    /**
     * Constructs a TaskProcessorBinaryResponseHandler object.
     * 
     * @param parentThread the parent thread of this thread
     * @param cycConnection the parent CycConnection
     */
    public TaskProcessorBinaryResponseHandler(Thread parentThread, CycConnection cycConnection) {
      this.parentThread = parentThread;
      this.cycConnection = cycConnection;
      ignoreMessage = new CycList();
      ignoreMessage.add(new CycSymbol("IGNORE"));
    }
    
    public void start() {
      initializeSynchronization();
      super.start();
      waitOnSetupToComplete();
    }

    /**
     * Opens the response socket with Cyc, blocks until the next task-processor response is available, 
     * then awakens the client thread that made the request.
     */
    public void run() {
      Thread.currentThread().setName("TaskProcessorBinaryResponseHandler");
      try {
        // Open a second api socket connection and use it for asychronous api responses.
        inboundSocket = new Socket(hostName, cfaslPort);
        int val = inboundSocket.getReceiveBufferSize();
        inboundSocket.setReceiveBufferSize(val * 2);
        inboundSocket.setTcpNoDelay(true);
        inboundSocket.setKeepAlive(true);
        inboundStream = new CfaslInputStream(inboundSocket.getInputStream());
        inboundStream.trace = trace;
        inboundOutputStream = new CfaslOutputStream(inboundSocket.getOutputStream());
        // send a one-time request the to Cyc server to configure this connection for subsequent api reponses
        CycList request = new CycList();
        request.add(new CycSymbol("INITIALIZE-JAVA-API-PASSIVE-SOCKET"));
        request.add(cycConnection.uuid.toString());
        inboundOutputStream.writeObject(request);
        inboundOutputStream.flush();
      }
      catch (IOException e) {
        Log.current.printStackTrace(e);
        System.exit(1);
      }
      // signal that we are ready to go
      notifySetupCompleted();
      // Handle messsages received on the asychronous inbound Cyc connection.
      while (true) {
        Object status = null;
        CycList taskProcessorResponse = null;
        boolean isInvalidObject = false;
        
        try {
          // read status
          inboundStream.resetIsInvalidObject();
          status = inboundStream.readObject();
          // read task processor response 
          inboundStream.resetIsInvalidObject();
          taskProcessorResponse = (CycList) inboundStream.readObject();
          logger.finest("API response: " + taskProcessorResponse.toString());
          isInvalidObject = inboundStream.isInvalidObject();
        }
        catch (Exception e) {
          logger.fine("Exception: " + e.getMessage());
          if (taskProcessingEnded) {
            if (trace > API_TRACE_NONE) {
              Log.current.println("Ending binary mode task processor handler.");
            }
          }
          
          if (e instanceof CfaslInputStreamClosedException) {
            if (trace > API_TRACE_NONE) {
              Log.current.errorPrintln(e.getMessage());
              //Log.current.printStackTrace(e);
            }
          }
          else if (e instanceof RuntimeException) {
            Log.current.errorPrintln(e.getMessage());
            Log.current.printStackTrace(e);
            continue;
          }
          else if (trace > API_TRACE_NONE) {
            Log.current.println("Cyc Server ended binary mode task processor handler.");
          }
          
          taskProcessingThreadDead = true;
          
          return;
        }
        
        logger.finest("API status: " + status);
        if (trace == API_TRACE_DETAILED) {
          String responseString = null;
          Log.current.println("cyc --> (" + status + ") " + taskProcessorResponse.toString());
        }
        
        if (taskProcessorResponse.equals(ignoreMessage)) {
          continue;
        }
        
        try {
          Integer id = (Integer) taskProcessorResponse.get(TASK_PROCESSOR_RESPONSE_ID);
          Object response = taskProcessorResponse.get(TASK_PROCESSOR_RESPONSE_RESPONSE);
          Object taskStatus = taskProcessorResponse.get(TASK_PROCESSOR_RESPONSE_STATUS);
          // handle Cyc images that either support or do not support (legacy) the finished flag
          Object finishedFlag = 
            (taskProcessorResponse.size() > TASK_PROCESSOR_RESPONSE_FINISHED_FLAG) ? 
            taskProcessorResponse.get(TASK_PROCESSOR_RESPONSE_FINISHED_FLAG) : 
            CycObjectFactory.t;

          boolean finished = !(finishedFlag == CycObjectFactory.nil);
          
          final WaitingWorkerInfo waitingWorkerInfo = (WaitingWorkerInfo) waitingReplyThreads.get(id);
          if (waitingWorkerInfo == null) {
            continue;
          }
          SubLWorker worker = waitingWorkerInfo.getWorker();
          if (waitingWorkerInfo.isReturnWholeTaskProcessorResponse)
            // used for example in the XML soap service where there is an upstream SOAPBinaryCycConnection object that 
            // needs the whose task processor response.
            response = taskProcessorResponse;

          if (taskStatus == CycObjectFactory.nil) {
            if (! isInvalidObject) {
              // no error occurred, no exception
              worker.fireSubLWorkerDataAvailableEvent(new SubLWorkerEvent(worker, response, -1.0f));
              if (finished) {
                //System.out.println("Exiting normally");
                worker.fireSubLWorkerTerminatedEvent(new SubLWorkerEvent(worker,
                SubLWorkerStatus.FINISHED_STATUS, null));
              }
            }
            else {    
              // no API error sent from the server but the response contains an invalid object
              worker.fireSubLWorkerTerminatedEvent(
                new SubLWorkerEvent(worker,
                                    SubLWorkerStatus.EXCEPTION_STATUS,
                                    new CycApiServerSideException("API response contains an invalid object: " + response.toString())));              
            }
          } else {
            // Error, status contains the error message
            
            //@ToDo need to diferrentiate between exceptions and cancel messages!!!!!!!!!
            finished = true;
            if (taskStatus instanceof String) {
              worker.fireSubLWorkerTerminatedEvent(new SubLWorkerEvent(worker,
                SubLWorkerStatus.EXCEPTION_STATUS,
                new CycApiServerSideException(taskStatus.toString())));
            } else if (taskStatus instanceof CycSymbol) {
              worker.fireSubLWorkerTerminatedEvent(new SubLWorkerEvent(worker,
                SubLWorkerStatus.CANCELED_STATUS, null));
            }
          }
          
          if (worker.isDone()) {
            waitingReplyThreads.remove(id);
          }
        } catch (Exception xcpt) {
          Log.current.errorPrintln(xcpt.getMessage());
          Log.current.printStackTrace(xcpt);
          continue;
        }
      } // while-forever 
    }
    
    /** Closes the passive inbound api response socket. */
    public void close () throws IOException {
      inboundOutputStream.close();
      if (trace > API_TRACE_NONE)
        Log.current.println("closed inbound socket associated with " + uuid);
    }
    
    private void waitOnSetupToComplete() {
      // avoid blocking on this ptr, which would stop the
      // notifySetupCompleted method from working correctly
      synchronized (lockObject) {
        boolean isInitialized = false;
        synchronized (this) {
          isInitialized = this.initialized;
        }
        while (!isInitialized) {
          try {
            lockObject.wait();
          } catch (InterruptedException xcpt) {
            System.err.println( "Interrupted during wait(): "
            + xcpt);
          }
          synchronized (this) {
            isInitialized = this.initialized;
          }
        }
      }
    }
    
    private void initializeSynchronization() {
      synchronized (this) {
        initialized = false;
        lockObject = new String("Lock object");
      }
    }
    
    private void notifySetupCompleted() {
      synchronized (this) {
       initialized = true;
      }
      synchronized (lockObject) {
       lockObject.notify(); 
      }
    }
  }
  
  /** Provides a timer thread for cancelling the connection if it takes too long to establish. */
  private class ConnectionTimer extends Thread {
    
    /** Constucts a new ConnectionTimer instance. */
    ConnectionTimer() {
    }
    
    /** Waits for either the CycConnection constructor thread to set the done indicator, or kills the
     * connection after the timeout is exceeded. */
    public void run() {
      try {
        while (! isCycConnectionEstablished) {
          Thread.sleep(WAIT_TIME_INCREMENT);
          timerMillis = timerMillis + WAIT_TIME_INCREMENT;
          if (timerMillis > TIMEOUT_MILLIS)
            throw new InterruptedException();
        }
      }
      catch (InterruptedException e) {
        Log.current.println("Timeout while awaiting Cyc connection establishment, closing sockets");
        // close the socket connections to Cyc and kill any awaiting api request threads
        if (trace == CycConnection.API_TRACE_NONE)
          trace = CycConnection.API_TRACE_MESSAGES;
        close();
      }
    }
    /** the timeout duration in milliseconds (one minute) */
    final long TIMEOUT_MILLIS = 60000;
    
    /** the wait time increment */
    final long WAIT_TIME_INCREMENT = 1000;
    
    /** the wait time so far in milliseconds */
    long timerMillis = 0;
    
    /** set by the CycConnection constructor process to indicate that the connection to Cyc is established */
    volatile boolean isCycConnectionEstablished = false;
    
  }
  
}
