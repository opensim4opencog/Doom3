/*
 * Justification.java
 *
 * Created on August 10, 2004, 2:06 PM
 */

package org.opencyc.util.query;

/**
 * @version $Id: Justification.java,v 1.1 2004/08/12 15:42:01 mreimers Exp $
 * @author  mreimers
 */
public interface Justification {
  public QueryResultSet getQueryResultSet();
  public int getQueryResultSetIndex();
  public String toPrettyString();
}
