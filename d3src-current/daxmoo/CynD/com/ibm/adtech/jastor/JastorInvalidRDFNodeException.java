/*******************************************************************************
 * Copyright (c) 2005 IBM Corporation 
 * All rights reserved.   This program and the accompanying materials
 * are made available under the terms of the Common Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.opensource.org/licenses/cpl.php
 * 
 ******************************************************************************/

package com.ibm.adtech.jastor;

import com.hp.hpl.jena.rdf.model.RDFNode;

/**
 * @author Joe Betz (<a href="mailto:betz@us.ibm.com">betz@us.ibm.com</a>)
 * @author Ben Szekely (<a href="mailto:bhszekel@us.ibm.com">bhszekel@us.ibm.com</a>)
 */
public class JastorInvalidRDFNodeException extends JastorException {
	public static final String copyright = "(C) Copyright IBM Corporation 2005  All Rights Reserved.";
	
    RDFNode object;
	public JastorInvalidRDFNodeException(
		Exception e,
		int errorCode,
		String errorDescription,
        RDFNode object) {
		super(e, errorCode, errorDescription);
        this.object = object;
	}

	public JastorInvalidRDFNodeException(int errorCode, String errorDescription, RDFNode object) {
		super(errorCode, errorDescription);
        this.object = object;
	}

	public JastorInvalidRDFNodeException(Exception e, String errorDescription, RDFNode object) {
		super(e, errorDescription);
        this.object = object;
	}

	public JastorInvalidRDFNodeException(String errorDescription, RDFNode object) {
		super(errorDescription);
         this.object = object;
	}

	public JastorInvalidRDFNodeException(RDFNode object) {
		super();
        this.object = object;
	}
    
    public RDFNode getRDFNode () {
        return object;
    }
}
