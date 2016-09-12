/*
 * CycQuerySpecification.java
 *
 * Created on August 11, 2004, 10:49 AM
 */

package org.opencyc.util.query;

import org.opencyc.cycobject.CycList;

/**
 * @version $Id: CycQuerySpecification.java,v 1.1 2004/08/12 15:41:58 mreimers Exp $
 * @author  mreimers
 */
public interface CycQuerySpecification extends QuerySpecification {
  
  public CycList getQueryFormula();
}
