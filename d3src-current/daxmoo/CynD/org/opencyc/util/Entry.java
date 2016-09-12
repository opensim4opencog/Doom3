/* $Id: Entry.java,v 1.3 2005/12/22 23:32:44 jantos Exp $
 *
 * Copyright (c) 2004 - 2006 Cycorp, Inc.  All rights reserved.
 * This software is the proprietary information of Cycorp, Inc.
 * Use is subject to license terms.
 */

package org.opencyc.util;

//// Internal Imports

//// External Imports

/**
 * <P>Entry is designed to...
 *
 * <P>Copyright (c) 2004 - 2006 Cycorp, Inc.  All rights reserved.
 * <BR>This software is the proprietary information of Cycorp, Inc.
 * <P>Use is subject to license terms.
 *
 * @author mreimers
 * @date August 11, 2004, 1:00 PM
 * @version $Id: Entry.java,v 1.3 2005/12/22 23:32:44 jantos Exp $
 */
public interface Entry {
  
  public Object getKey();
  public Object getValue();
  
}
