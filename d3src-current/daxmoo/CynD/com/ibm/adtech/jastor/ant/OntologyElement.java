/*******************************************************************************
 * Copyright (c) 2005 IBM Corporation 
 * All rights reserved.   This program and the accompanying materials
 * are made available under the terms of the Common Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.opensource.org/licenses/cpl.php
 * 
 ******************************************************************************/

package com.ibm.adtech.jastor.ant;

import java.security.InvalidParameterException;

import org.apache.tools.ant.Task;
import org.apache.tools.ant.types.Path;


/**
 * 
 * @author Ben Szekely (<a href="mailto:bhszekel@us.ibm.com">bhszekel@us.ibm.com</a>)
 * @author Elias Torres (<a href="mailto:eliast@us.ibm.com">eliast@us.ibm.com</a>) *
 */
public class OntologyElement extends Task {
	public static final String copyright = "(C) Copyright IBM Corporation 2005  All Rights Reserved.";
	
	private boolean generate;
	private Path path;
	private String uri;
	private String javaPackage;
	private String ontlang;
	private String lang;
	
	public void validate() {
		if(uri == null) throw new InvalidParameterException("OntologyElement uri must not be null.");
		if(javaPackage == null) throw new InvalidParameterException("OntologyElement javaPackage must not be null.");
		if(path == null) throw new InvalidParameterException("OntologyElement path must not be null.");
	}
	
	public Path getPath() {
		return path;
	}
	
	public boolean isGenerate() {
	    return generate;
	}
	
	public void setGenerate(boolean generate) {
	    this.generate = generate;
	}
	
	public void setPath(Path path) {
		this.path = path;
	}
	
	public String getJavaPackage() {
		return javaPackage;
	}
	
	public void setJavaPackage(String javaPackage) {
	    this.javaPackage = javaPackage;
	}
	
	public String getUri() {
		return uri;
	}
	
	public void setUri(String uri) {
	    this.uri = uri;
	}	
	
    public String getLang() {
        return lang;
    }
    
    public void setLang(String lang) {
        this.lang = lang;
    }

	public String getOntlang() {
		return ontlang;
	}

	public void setOntlang(String ontlang) {
		this.ontlang = ontlang;
	}
    
}
