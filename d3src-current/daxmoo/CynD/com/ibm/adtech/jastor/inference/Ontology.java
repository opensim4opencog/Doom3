/*******************************************************************************
 * Copyright (c) 2005 IBM Corporation 
 * All rights reserved.   This program and the accompanying materials
 * are made available under the terms of the Common Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.opensource.org/licenses/cpl.php
 * 
 ******************************************************************************/

package com.ibm.adtech.jastor.inference;

import java.io.File;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import com.hp.hpl.jena.ontology.OntModel;
import com.hp.hpl.jena.ontology.impl.OntologyImpl;
import com.ibm.adtech.jastor.JastorContext;
import com.ibm.adtech.jastor.util.graph.DFS;
import com.ibm.adtech.jastor.util.graph.EdgeMem;
import com.ibm.adtech.jastor.util.graph.GraphMem;
import com.ibm.adtech.jastor.util.graph.IEdge;
import com.ibm.adtech.jastor.util.graph.IGraph;
import com.ibm.adtech.jastor.util.graph.INode;
import com.ibm.adtech.jastor.util.graph.NodeMem;

/**
 * 
 * Simple class to encapsulate an ontology to be generated.
 * 
 * @author Ben Szekely (<a href="mailto:bhszekel@us.ibm.com">bhszekel@us.ibm.com</a>)
 *
 */
public class Ontology {
    
    public static final String copyright = "(C) Copyright IBM Corporation 2005  All Rights Reserved.";
    
    String pkg;
    JastorContext ctx;
    OntologyClass witness;
    OntologyComment comment;
    com.hp.hpl.jena.ontology.Ontology ont;
    String uri;
    List classes;

    /**
     * @param pkg
     * @param ctx
     * @param List a list of String uri's of the classes in this ontology
     */
    public Ontology(String pkg, JastorContext ctx, List classes) {
        super();
        this.pkg = pkg;
        this.ctx = ctx;
        this.classes = classes;
        // possible speed up could be done here
        witness = (OntologyClass)getClasses().get(0);
        OntModel gm = ctx.getOntModel();
        uri = ctx.getOntologyForPackage(pkg);
        ont = gm.getOntology(uri);
        if (ont != null)
            this.comment = new OntologyComment(ont);
        else
            this.comment = new OntologyComment(gm.createResource(uri));
    }
    
    public List getClasses() {        
        Iterator it = classes.iterator();
        ArrayList alist = new ArrayList();
        while (it.hasNext()) {
            String uri = (String)it.next();
            OntologyClass oc = new OntologyClass(ctx.getOntModel().getOntClass(uri),ctx);
            alist.add(oc);
        }
        return alist;
    }
    
    /**
     * Return a topologically sorted list of the classes in this ontology based on the extension class-hierarchy.
     * @return
     */
    public List getClassesSorted() {
    	List classes = getClasses();
    	IGraph graph = new GraphMem("classes");
    	Iterator it = classes.iterator();
    	while (it.hasNext()) {
    		OntologyClass oc = (OntologyClass)it.next();
    		INode node = new NodeMem(oc.getURI());
    		node.setData(oc);
    		graph.addNode(node);
    	}
    	it = classes.iterator();
    	while (it.hasNext()) {
    		OntologyClass oc = (OntologyClass)it.next();
    		INode src = graph.getNodeByName(oc.getURI());
    		Iterator it2 = oc.listImmediateExtensionClasses().iterator();
    		while (it2.hasNext()) {
    			OntologyClass oc2 = (OntologyClass)it2.next();
    			INode dst = graph.getNodeByName(oc2.getURI());
    			if (dst == null)
    				continue;
    			IEdge edge = new EdgeMem(oc.getURI() + "->" + oc2.getURI(),src,dst);
    			graph.addEdge(edge);
    		}
    	}
    	DFS dfs = new DFS();
    	dfs.setGraph(graph);
    	dfs.execute();
    	List sorted = new ArrayList();
    	Iterator nodes = dfs.getNodesByFinishTime().iterator();
    	while (nodes.hasNext()) {
    		INode node = (INode)nodes.next();
    		OntologyClass oc = (OntologyClass)node.getData();
    		sorted.add(0,oc);
    	}    	
    	return sorted;
    }
    
    public String getPackage() {
        return pkg;
    }
    
    public JastorContext getContext() {
        return ctx;
    }
    
    public com.hp.hpl.jena.ontology.Ontology getOntology() {
    	return ont;
    }
    
    public String getLocalName() {
    	if (ont != null)
    		return ont.getLocalName();
    	return ctx.getOntModel().getResource(uri).getLocalName();
    }
    
    public String getURI() {
        return uri;
    }
    
    public OntologyComment getComment() {
        return comment;
    }
    
    public String getFactoryClassname() {
        return witness.getFactoryClassname();
    }
    
    public String getFactoryFullClassname() {
        return witness.getFactoryFullClassname();
    }
    
    public File getFactoryFile(File basedir) {
        return witness.getFactoryFile(basedir);
    }
}
