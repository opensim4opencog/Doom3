/* $Id: InferenceWorker.java,v 1.9 2005/12/22 23:32:41 jantos Exp $
 *
 * Copyright (c) 2004 - 2006 Cycorp, Inc.  All rights reserved.
 * This software is the proprietary information of Cycorp, Inc.
 * Use is subject to license terms.
 */

package org.opencyc.inference;

//// Internal Imports
import org.opencyc.api.*;

//// External Imports
import java.util.*;

/**
 * <P>InferenceWorker is designed to...
 *
 * <p>Copyright 2005 Cycorp, Inc., license is open source GNU LGPL.
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
 *
 * @author tbrussea, zelal
 * @date July 27, 2005, 11:40 AM
 * @version $Id: InferenceWorker.java,v 1.9 2005/12/22 23:32:41 jantos Exp $
 */
public interface InferenceWorker extends SubLWorker {

  int getInferenceId();
  
  int getProblemStoreId();
  
  void releaseInferenceResources(long timeoutMsecs) throws java.io.IOException, 
    org.opencyc.util.TimeOutException, CycApiException;

  int getAnswersCount();
  
  Object getAnswerAt(int index);
  
  List getAnswers();
  
  List getAnswers(int startIndex, int endIndex);
  
  void interruptInference(); // with infinite patience
  
  void interruptInference(int patience); // with some amount of patience

  void continueInference(InferenceParameters queryProperties);
  
  InferenceStatus getInferenceStatus();
  
  InferenceWorkerSuspendReason getSuspendReason();
  
  public Object[] getInferenceListeners();
  
  void addInferenceListener(InferenceWorkerListener inferenceListener);
  
  void removeInferenceListener(InferenceWorkerListener inferenceListener);
  
  void removeAllInferenceListeners();
  
}
