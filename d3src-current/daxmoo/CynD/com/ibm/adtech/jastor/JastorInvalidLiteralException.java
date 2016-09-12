/*******************************************************************************
 * Copyright (c) 2005 IBM Corporation 
 * All rights reserved.   This program and the accompanying materials
 * are made available under the terms of the Common Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.opensource.org/licenses/cpl.php
 * 
 ******************************************************************************/

package com.ibm.adtech.jastor;

import com.hp.hpl.jena.rdf.model.Literal;

/**
 * @author Joe Betz (<a href="mailto:betz@us.ibm.com">betz@us.ibm.com</a>)
 * @author Ben Szekely (<a href="mailto:bhszekel@us.ibm.com">bhszekel@us.ibm.com</a>)
 */
public class JastorInvalidLiteralException extends JastorException {
	public static final String copyright = "(C) Copyright IBM Corporation 2005  All Rights Reserved.";
	
    Literal literal;
    public JastorInvalidLiteralException(
        Exception e,
        int errorCode,
        String errorDescription,
        Literal object) {
        super(e, errorCode, errorDescription);
        this.literal = object;
    }

    public JastorInvalidLiteralException(int errorCode, String errorDescription, Literal object) {
        super(errorCode, errorDescription);
        this.literal = object;
    }

    public JastorInvalidLiteralException(Exception e, String errorDescription, Literal object) {
        super(e, errorDescription);
        this.literal = object;
    }

    public JastorInvalidLiteralException(String errorDescription, Literal object) {
        super(errorDescription);
         this.literal = object;
    }

    public JastorInvalidLiteralException(Literal object) {
        super();
        this.literal = object;
    }
    
    public Literal getLiteral() {
        return literal;
    }
}
