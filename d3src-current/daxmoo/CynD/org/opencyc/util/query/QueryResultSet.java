/*
 * ResultSet.java
 *
 * Created on August 10, 2004, 2:04 PM
 */

package org.opencyc.util.query;

import java.util.Date;
import java.util.Iterator;

/**
 * @version $Id: QueryResultSet.java,v 1.1 2004/08/12 15:42:07 mreimers Exp $
 * @author  mreimers
 */
public interface QueryResultSet {
  public Iterator getResultSetIterator();
  public Query getQuery();
  public Date getTimeStamp();
  public void addQueryResult(Object queryResult);
  public Justification getJustificationForIndex(int i);
  
}
