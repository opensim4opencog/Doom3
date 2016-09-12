/*
 * CycQuery.java
 *
 * Created on August 11, 2004, 10:48 AM
 */

package org.opencyc.util.query;

import org.opencyc.inference.InferenceStatus;
/**
 * @version $Id: CycQuery.java,v 1.1 2004/08/12 15:41:56 mreimers Exp $
 * @author  mreimers
 */
public interface CycQuery extends Query {
  
  public InferenceStatus getInferenceStatus();
}
