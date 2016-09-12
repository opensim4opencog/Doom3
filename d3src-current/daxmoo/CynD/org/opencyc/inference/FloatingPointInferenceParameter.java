/* $Id: FloatingPointInferenceParameter.java,v 1.2 2005/12/22 23:32:38 jantos Exp $
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
 * <P>InferenceParameter is designed to...
 *
 * <P>Copyright (c) 2004 - 2006 Cycorp, Inc.  All rights reserved.
 * <BR>This software is the proprietary information of Cycorp, Inc.
 * <P>Use is subject to license terms.
 *
 * @author tbrussea
 * @date August 2, 2005, 10:25 AM
 * @version $Id: FloatingPointInferenceParameter.java,v 1.2 2005/12/22 23:32:38 jantos Exp $
 */
public interface FloatingPointInferenceParameter extends InferenceParameter {
  double getMinValue();
  double getMaxValue();
}