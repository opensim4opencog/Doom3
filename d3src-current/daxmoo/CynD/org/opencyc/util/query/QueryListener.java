/*
 * QueryListener.java
 *
 * Created on August 10, 2004, 2:06 PM
 */

package org.opencyc.util.query;

import java.util.EventListener;

/**
 * @version $Id: QueryListener.java,v 1.1 2004/08/12 15:42:06 mreimers Exp $
 * @author  mreimers
 */
public interface QueryListener extends EventListener {
  public void queryChanged(QueryChangeEvent e);
}
