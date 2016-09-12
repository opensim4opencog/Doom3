/*******************************************************************************
 * Copyright (c) 2005 IBM Corporation 
 * All rights reserved.   This program and the accompanying materials
 * are made available under the terms of the Common Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.opensource.org/licenses/cpl.php
 * 
 ******************************************************************************/

package com.ibm.adtech.jastor.inference;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import com.hp.hpl.jena.rdf.model.Literal;
import com.hp.hpl.jena.rdf.model.Property;
import com.hp.hpl.jena.rdf.model.RDFNode;
import com.hp.hpl.jena.rdf.model.Resource;
import com.hp.hpl.jena.rdf.model.Statement;
import com.hp.hpl.jena.rdf.model.StmtIterator;
import com.hp.hpl.jena.vocabulary.DC;
import com.hp.hpl.jena.vocabulary.OWL;
import com.hp.hpl.jena.vocabulary.RDFS;

/**
 * 
 * Represents content to be added to the definition of a property or class as a comment, i.e. javadoc.  The
 * methods in this class provide mappings of name-value pairs in various categories DC, RDFS, etc...
 * 
 * @author Joe Betz (<a href="mailto:betz@us.ibm.com">betz@us.ibm.com</a>)
 * @author Ben Szekely (<a href="mailto:bhszekel@us.ibm.com">bhszekel@us.ibm.com</a>)
 *
 */
public class OntologyComment {
    
    public static final String copyright = "(C) Copyright IBM Corporation 2005  All Rights Reserved.";
    
    private static final Property[] DC_PROPERTIES = new Property[]{DC.contributor,DC.coverage,DC.creator,DC.date,DC.description,DC.format,
            													   DC.identifier,DC.language,DC.publisher,DC.relation,DC.rights,DC.source,DC.subject,
            													   DC.title,DC.type};
    private static final Property[] RDFS_PROPERTIES = new Property[]{RDFS.comment,RDFS.isDefinedBy,RDFS.seeAlso,RDFS.label};
    
    private Resource resource;
    private HashMap rdfs = new HashMap();
    private HashMap dc = new HashMap();
    private String label = null;
    private String versionInfo;
    
    
    /**
     * Create a new comment with the given resource
     * @param commentedResource the resource of the property or class being commented
     */
    public OntologyComment(Resource commentedResource) {
        resource = commentedResource;
        
        if(resource.hasProperty(RDFS.label)) {
            label = get(RDFS.label);
        } else if (resource.hasProperty(DC.title)) {
            label = get(DC.title);
        } else {
            label = resource.getLocalName();
        }
        
        for (int i=0;i<DC_PROPERTIES.length;i++) {
            if (resource.hasProperty(DC_PROPERTIES[i]))
                dc.put(DC_PROPERTIES[i].getLocalName(),get(DC_PROPERTIES[i]));
        }
        
        for (int i=0;i<RDFS_PROPERTIES.length;i++) {
            if (resource.hasProperty(RDFS_PROPERTIES[i]))
                rdfs.put(RDFS_PROPERTIES[i].getLocalName(),get(RDFS_PROPERTIES[i]));
        }
        
        if (resource.hasProperty(OWL.versionInfo))
            versionInfo = get(OWL.versionInfo);
        
    }
    
    public String getLabel() {
       return label;
    }
    
    public String getVersionInfo() {
        return versionInfo;
    }
    
    public List getAuthors() {
        ArrayList authors = new ArrayList();
        authors.addAll(getStringList(DC.creator));
        authors.addAll(getStringList(DC.contributor));
        return authors;
    }
    
    public String[] listRDFSPropertyNames() {
        String[] s = new String[rdfs.keySet().size()];
        return (String[])rdfs.keySet().toArray(s);
    }
    
    public String getRDFSProperty(String name) {
        return (String)rdfs.get(name);
    }
    
    public String[] listDCPropertyNames() {
        String[] s = new String[dc.keySet().size()];
        return (String[])dc.keySet().toArray(s);
    }
    
    public String getDCProperty(String name) {
        return (String)dc.get(name);
    }
    
    private String get(Property property) {
        StringBuffer sb = new StringBuffer();
        int i = 0;
        for(StmtIterator iter = resource.listProperties(property); iter.hasNext(); i++) {
            Statement stmt = iter.nextStatement();
            RDFNode obj = stmt.getObject();
            if (i > 0) sb.append(", ");
            sb.append(obj.toString());
        }
        return sb.toString();
    }
    
    private List getStringList (Property property) {
        ArrayList list = new ArrayList();
        if(resource.hasProperty(property)) {
            for(StmtIterator iter = resource.listProperties(property); iter.hasNext();) {
                RDFNode obj = iter.nextStatement().getObject();
                if (obj.canAs(Literal.class)) {
                    Literal l = (Literal)obj.as(Literal.class);
                    Object value = l.getValue();
                    if (value instanceof String) {
                        list.add(value);
                    }
                }
            }
        }
        return list;
    }

}
