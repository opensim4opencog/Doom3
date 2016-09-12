/*******************************************************************************
 * Copyright (c) 2005 IBM Corporation 
 * All rights reserved.   This program and the accompanying materials
 * are made available under the terms of the Common Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.opensource.org/licenses/cpl.php
 * 
 ******************************************************************************/

package com.ibm.adtech.jastor.resource;

import com.hp.hpl.jena.graph.Node;
import com.hp.hpl.jena.rdf.model.AnonId;
import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.Property;
import com.hp.hpl.jena.rdf.model.RDFNode;
import com.hp.hpl.jena.rdf.model.RDFVisitor;
import com.hp.hpl.jena.rdf.model.Resource;
import com.hp.hpl.jena.rdf.model.Statement;
import com.hp.hpl.jena.rdf.model.StmtIterator;
import com.ibm.adtech.jastor.JastorException;
import com.ibm.adtech.jastor.ThingImpl;

/**
 * Passes all Resource related calls directly to the underlying Resource instance.
 * 
 * @see com.hp.hpl.jena.rdf.model.Resource
 * 
 * @author Ben Szekely (<a href="mailto:bhszekel@us.ibm.com">bhszekel@us.ibm.com</a>)
 * @author Rob Gonzalez (<a href="mailto:gonzo@us.ibm.com">gonzo@us.ibm.com</a>)
 */
public class ResourceThingImpl extends ThingImpl implements ResourceThing {
    public static final String copyright = "(C) Copyright IBM Corporation 2005  All Rights Reserved.";
    
    public ResourceThingImpl(Resource resource, Model model) throws JastorException {
        super(resource, model);
    }

	public AnonId getId() {
		return this._resource.getId();
	}

	public Node getNode() {
		return this._resource.getNode();
	}

	public boolean hasURI(String uri) {
		return this._resource.hasURI(uri);
	}

	public String getURI() {
		return this._resource.getURI();
	}

	public String getNameSpace() {
		return this._resource.getNameSpace();
	}

	public String getLocalName() {
		return this._resource.getLocalName();
	}

	public boolean isAnon() {
		return this._resource.isAnon();
	}

	public Statement getRequiredProperty(Property p) {
		return this._resource.getRequiredProperty(p);
	}

	public Statement getProperty(Property p) {
		return this._resource.getProperty(p);
	}

	public StmtIterator listProperties(Property p) {
		return this._resource.listProperties(p);
	}

	public StmtIterator listProperties() {
		return this._resource.listProperties();
	}

	public Resource addProperty(Property p, boolean o) {
		return this._resource.addProperty(p, o);
	}

	public Resource addProperty(Property p, long o) {
		return this._resource.addProperty(p, o);
	}

	public Resource addProperty(Property p, char o) {
		return this._resource.addProperty(p, o);
	}

	public Resource addProperty(Property p, float o) {
		return this._resource.addProperty(p, o);
	}

	public Resource addProperty(Property p, double o) {
		return this._resource.addProperty(p, o);
	}

	public Resource addProperty(Property p, String o) {
		return this._resource.addProperty(p, o);
	}

	public Resource addProperty(Property p, String o, String l) {
		return this._resource.addProperty(p, o);
	}

	public Resource addProperty(Property p, Object o) {
		return this._resource.addProperty(p, o);
	}

	public Resource addProperty(Property p, RDFNode o) {
		return this._resource.addProperty(p, o);
	}

	public boolean hasProperty(Property p) {
		return this._resource.hasProperty(p);
	}

	public boolean hasProperty(Property p, boolean o) {
		return this._resource.hasProperty(p, o);
	}

	public boolean hasProperty(Property p, long o) {
		return this._resource.hasProperty(p, o);
	}

	public boolean hasProperty(Property p, char o) {
		return this._resource.hasProperty(p, o);
	}

	public boolean hasProperty(Property p, float o) {
		return this._resource.hasProperty(p, o);
	}

	public boolean hasProperty(Property p, double o) {
		return this._resource.hasProperty(p, o);
	}

	public boolean hasProperty(Property p, String o) {
		return this._resource.hasProperty(p, o);
	}

	public boolean hasProperty(Property p, String o, String l) {
		return this._resource.hasProperty(p, o, l);
	}

	public boolean hasProperty(Property p, Object o) {
		return this._resource.hasProperty(p, o);
	}

	public boolean hasProperty(Property p, RDFNode o) {
		return this._resource.hasProperty(p, o);
	}

	public Resource removeProperties() {
		return this._resource.removeProperties();
	}

	public Resource removeAll(Property p) {
		return this._resource.removeAll(p);
	}

	public Resource begin() {
		return this._resource.begin();
	}

	public Resource abort() {
		return this._resource.abort();
	}

	public Resource commit() {
		return this._resource.commit();
	}

	public Model getModel() {
		return this._resource.getModel();
	}

	public RDFNode as(Class view) {
		return this._resource.as(view);
	}

	public boolean canAs(Class view) {
		return this._resource.canAs(view);
	}

	public RDFNode inModel(Model m) {
		return this._resource.inModel(m);
	}

	public Object visitWith(RDFVisitor rv) {
		return this._resource.visitWith(rv);
	}

	public Node asNode() {
		return this._resource.asNode();
	}

	public boolean isLiteral() {
		return this._resource.isLiteral();
	}

	public boolean isResource() {
		return this._resource.isResource();
	}

	public boolean isURIResource() {
		return this._resource.isURIResource();
	}
	
	
}
