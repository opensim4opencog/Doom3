/* $Id: EnumerationInferenceParameter.java,v 1.2 2005/12/22 23:32:37 jantos Exp $
 *
 * Copyright (c) 2004 - 2006 Cycorp, Inc.  All rights reserved.
 * This software is the proprietary information of Cycorp, Inc.
 * Use is subject to license terms.
 */

package org.opencyc.inference;

//// Internal Imports
import org.opencyc.cycobject.*;

//// External Imports
import java.util.*;

/**
 * <P>InferenceParameter is designed to...
 *
 * <P>Copyright (c) 2004 - 2006 Cycorp, Inc.  All rights reserved.
 * <BR>This software is the proprietary information of Cycorp, Inc.
 * <P>Use is subject to license terms.
 *
 * @author tbrussea
 * @date August 2, 2005, 10:25 AM
 * @version $Id: EnumerationInferenceParameter.java,v 1.2 2005/12/22 23:32:37 jantos Exp $
 */
public interface EnumerationInferenceParameter extends InferenceParameter {
  public List getPotentialValues();
}