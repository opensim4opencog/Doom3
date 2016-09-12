/* $Id: InferenceParameterValueDescription.java,v 1.4 2005/12/22 23:32:39 jantos Exp $
 *
 * Copyright (c) 2004 - 2006 Cycorp, Inc.  All rights reserved.
 * This software is the proprietary information of Cycorp, Inc.
 * Use is subject to license terms.
 */

package org.opencyc.inference;

//// Internal Imports
import org.opencyc.cycobject.*;

//// External Imports

/**
 * <P>InferenceParameterValueDescription is designed to...
 *
 * <P>Copyright (c) 2004 - 2006 Cycorp, Inc.  All rights reserved.
 * <BR>This software is the proprietary information of Cycorp, Inc.
 * <P>Use is subject to license terms.
 *
 * @author zelal
 * @date August 14, 2005, 12:47 PM
 * @version $Id: InferenceParameterValueDescription.java,v 1.4 2005/12/22 23:32:39 jantos Exp $
 */
public interface InferenceParameterValueDescription {
  Object getValue();
  String getShortDescription();
  String getLongDescription();
}