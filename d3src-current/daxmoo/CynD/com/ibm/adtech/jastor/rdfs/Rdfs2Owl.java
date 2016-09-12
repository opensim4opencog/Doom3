/*******************************************************************************
 * Copyright (c) 2005 IBM Corporation 
 * All rights reserved.   This program and the accompanying materials
 * are made available under the terms of the Common Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.opensource.org/licenses/cpl.php
 * 
 ******************************************************************************/


package com.ibm.adtech.jastor.rdfs;

import java.util.ArrayList;
import java.util.Iterator;

import com.hp.hpl.jena.ontology.OntClass;
import com.hp.hpl.jena.ontology.OntModel;
import com.hp.hpl.jena.ontology.OntModelSpec;
import com.hp.hpl.jena.ontology.OntProperty;
import com.hp.hpl.jena.ontology.OntResource;
import com.hp.hpl.jena.rdf.model.ModelFactory;
import com.hp.hpl.jena.rdf.model.ResIterator;
import com.hp.hpl.jena.rdf.model.Resource;
import com.hp.hpl.jena.rdf.model.ResourceFactory;
import com.hp.hpl.jena.util.iterator.ExtendedIterator;
import com.hp.hpl.jena.vocabulary.OWL;
import com.hp.hpl.jena.vocabulary.RDF;
import com.hp.hpl.jena.vocabulary.RDFS;

public class Rdfs2Owl {
	public static final String copyright = "(C) Copyright IBM Corporation 2005  All Rights Reserved.";
	
	public static final Resource RDF_XML_LITERAL = ResourceFactory.createProperty(RDF.getURI(), "XMLLiteral");
	
	public static OntModel convertToOwl(OntModel rdfsModel, String ontologyUri) {
		// add the ontology
		OntModel owlModel = ModelFactory.createOntologyModel(OntModelSpec.OWL_MEM, rdfsModel);
		rdfsModel.add(owlModel.createResource(ontologyUri), RDF.type, OWL.Ontology);

		// find all the rdfs classes and turn them into owl classes
		ExtendedIterator propertyIter = owlModel.listSubjectsWithProperty(RDF.type, RDFS.Class);
		try {
			while (propertyIter.hasNext()) {
				Resource oc = (Resource) propertyIter.next();
				owlModel.remove(owlModel.listStatements(oc, RDF.type, RDFS.Class));
				oc.addProperty(RDF.type, OWL.Class);
			}
		} finally {
			propertyIter.close();
		}
		
		propertyIter = owlModel.listSubjectsWithProperty(RDF.type, RDFS.Datatype);
		try {
			while (propertyIter.hasNext()) {
				Resource oc = (Resource) propertyIter.next();
				owlModel.remove(owlModel.listStatements(oc, RDF.type, RDFS.Datatype));
				oc.addProperty(RDF.type, OWL.Class);
			}
		} finally {
			propertyIter.close();
		}

		// find all the rdfs properties and turn them into owl properties
		// the the necessary restrictions
		propertyIter = owlModel.listOntProperties();
		try {
			while (propertyIter.hasNext()) {
				OntProperty op = (OntProperty) propertyIter.next();

				ExtendedIterator domainIter = op.listDomain();
				try {
					while (domainIter.hasNext()) {
						OntResource domain = (OntResource) domainIter.next();

						OntClass oc = domain.asClass();
						Resource restriction = owlModel.createRestriction(op);
						oc.addSuperClass(restriction);
					}
					OntResource range = op.getRange();
					if (range != null) {
						
						/*
						 * This used to be a test for range.isClass(), but rdfs:Literal is
						 * a class, so instead I just test for either Literal, XMLLiteral
						 * and anything else that might extend it. -Elias
						 */
						if (range.equals(RDFS.Literal) || range.equals(RDF_XML_LITERAL) || 
								range.hasRDFType(RDFS.Literal, true) || 
								range.hasRDFType(RDF_XML_LITERAL, true)) {
							op.addProperty(RDF.type, OWL.DatatypeProperty);
						} else {
							op.addProperty(RDF.type, OWL.ObjectProperty);
						}
					} else {
						op.addProperty(RDF.type, OWL.ObjectProperty);
					}

				} finally {
					if (domainIter != null)
						domainIter.close();
				}
			}
		} finally {
			if (propertyIter != null)
				propertyIter.close();
		}

		return owlModel;
	}

	/**
	 * Creates a RDFS Class for the given domain class URI and sets it as the domain for all RDFS Properties that do not already have a domain.
	 * 
	 * @param ontModel
	 * @param domainClassUri
	 * @return
	 */
	public static OntModel specifyClassForOrphanProperties(OntModel ontModel, String domainClassUri) {
		Resource domainClass = ontModel.createResource(domainClassUri);
		ontModel.add(domainClass, RDF.type, RDFS.Class);

		ExtendedIterator propertyIter = ontModel.listOntProperties();
		try {
			while (propertyIter.hasNext()) {
				OntProperty op = (OntProperty) propertyIter.next();

				ExtendedIterator domainIter = op.listDomain();
				boolean hasDomain = false;
				try {
					while (domainIter.hasNext()) {
						hasDomain = true;
						break;
					}
				} finally {
					if (domainIter != null)
						domainIter.close();
				}

				if (!hasDomain) {
					op.addDomain(domainClass);
				}
			}
		} finally {
			if (propertyIter != null)
				propertyIter.close();
		}
		return ontModel;
	}

	/**
	 * Sets the domain of all RDFS Properties that do not already have to domain
	 * to all the classes in the RDFS Model.
	 * 
	 * @param ontModel
	 * @return
	 */
	public static OntModel specifyAllDomainsForOrphanProperties(OntModel ontModel) {
		ResIterator resIterator = ontModel.listSubjectsWithProperty(RDF.type, RDFS.Class);
		ArrayList classList = new ArrayList();
		try {
			while (resIterator.hasNext()) {
				classList.add(resIterator.nextResource());
			}
		} finally {
			resIterator.close();
		}

		ExtendedIterator propertyIter = ontModel.listOntProperties();
		try {
			while (propertyIter.hasNext()) {
				OntProperty op = (OntProperty) propertyIter.next();

				ExtendedIterator domainIter = op.listDomain();
				boolean hasDomain = false;
				try {
					while (domainIter.hasNext()) {
						hasDomain = true;
						break;
					}
				} finally {
					if (domainIter != null)
						domainIter.close();
				}

				if (!hasDomain) {
					for (Iterator classIter = classList.iterator(); classIter.hasNext();) {
						Resource domainClass = (Resource) classIter.next();
						op.addDomain(domainClass);
					}

				}
			}
		} finally {
			if (propertyIter != null)
				propertyIter.close();
		}
		return ontModel;
	}
}
