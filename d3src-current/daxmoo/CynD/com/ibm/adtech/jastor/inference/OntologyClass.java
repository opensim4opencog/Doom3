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
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import com.hp.hpl.jena.Jena;
import com.hp.hpl.jena.ontology.ConversionException;
import com.hp.hpl.jena.ontology.EnumeratedClass;
import com.hp.hpl.jena.ontology.IntersectionClass;
import com.hp.hpl.jena.ontology.OntClass;
import com.hp.hpl.jena.ontology.OntModel;
import com.hp.hpl.jena.ontology.OntProperty;
import com.hp.hpl.jena.ontology.Restriction;
import com.hp.hpl.jena.ontology.UnionClass;
import com.hp.hpl.jena.query.QueryExecution;
import com.hp.hpl.jena.query.QueryExecutionFactory;
import com.hp.hpl.jena.query.QueryFactory;
import com.hp.hpl.jena.query.QuerySolution;
import com.hp.hpl.jena.query.QuerySolutionMap;
import com.hp.hpl.jena.query.ResultSet;
import com.hp.hpl.jena.rdf.model.Resource;
import com.hp.hpl.jena.rdf.model.Statement;
import com.hp.hpl.jena.rdf.model.StmtIterator;
import com.hp.hpl.jena.rdql.Query;
import com.hp.hpl.jena.rdql.QueryEngine;
import com.hp.hpl.jena.rdql.QueryResults;
import com.hp.hpl.jena.rdql.ResultBinding;
import com.hp.hpl.jena.rdql.ResultBindingImpl;
import com.hp.hpl.jena.util.iterator.ExtendedIterator;
import com.hp.hpl.jena.vocabulary.OWL;
import com.hp.hpl.jena.vocabulary.RDF;
import com.hp.hpl.jena.vocabulary.RDFS;
import com.ibm.adtech.jastor.JastorContext;
import com.ibm.adtech.jastor.JastorException;
import com.ibm.adtech.jastor.JavaIdentifierEncoder;

/**
 * 
 * This class is a wrapper for an ontology class.  Most of the time, the containing ont class
 * is a name classes but occasionally, instances are internall used as placeholders for restrictions.
 * 
 * @author Ben Szekely ( <a href="mailto:bhszekel@us.ibm.com">bhszekel@us.ibm.com </a>)
 *  
 */
public class OntologyClass {
    
    public static final String copyright = "(C) Copyright IBM Corporation 2005  All Rights Reserved.";

    OntClass ontClass;

    OntModel ontModel;

    JastorContext ctx;

    OntologyComment comment;
    
    static com.hp.hpl.jena.query.Query nullDomainQuery = null;
    static {
    	String sparql = "SELECT ?res " + 
		 " { ?res a ?proptype . " + 
		 " OPTIONAL { ?res <" + RDFS.domain.getURI()+ "> ?prop . } " + 
		 " FILTER (!bound(?prop)) . " +
		 " } ";
    	nullDomainQuery = QueryFactory.create(sparql);
    }

    /**
     * Construct a new OntologyClass to represent a Thing. Such instances will
     * not contain an OntClass, but will return basic naming questions. Several
     * methods such as listProperties will never be called on such an instance
     * so we leave them unmodified. In paticular, the file name generation
     * routines will generate a file for Thing as though it resided with the
     * generated classes. This is wrong, but OK, since such methods will never
     * be called.
     * 
     * @param ctx
     */
    public OntologyClass(JastorContext ctx) {
        this.ctx = ctx;
        this.ontModel = ctx.getOntModel();
        
    }

    /**
     * Construct an OntologyClass wrapper around the given OntClass.  This OntClass can be an anonymous
     * restriction on a property in which case the listProperties call will return the restricted property
     * 
     * @param ontClass
     * @param ctx
     */
    public OntologyClass(OntClass ontClass, JastorContext ctx) {
    	
        this.ontClass = ontClass;
        this.ctx = ctx;
        ontModel = ctx.getOntModel();
        this.comment = new OntologyComment(ontClass);
    }

    public JastorContext getContext() {
        return ctx;
    }

    public OntClass getOntClass() {
        return ontClass;
    }

    public String getURI() {
        if (ontClass == null)
            return RDFS.Resource.getURI();
        return ontClass.getURI();
    }

    public String getLocalName() {
        if (ontClass == null)
            return RDFS.Resource.toString();
        return ontClass.getLocalName();
    }

    public OntologyComment getComment() {
        return comment;
    }

    public String toString() {
        if (ontClass == null)
            return RDFS.Resource.toString();
        return ontClass.getURI();
    }

    public boolean equals(Object o) {
        if (!(o instanceof OntologyClass))
            return false;
        OntologyClass oc = (OntologyClass) o;
        return oc.getOntClass().equals(getOntClass());
    }

    /**
     * Return a string that can be used to represent one of these as an instance
     * variable
     * 
     * @return
     */
    public String getVariableName() {
        String ret = getInterfaceClassname();
        String first = ret.substring(0, 1).toLowerCase();
        return first + ret.substring(1);
    }

    public String getInterfaceFullClassname() {
        if (ontClass == null) 
            return ctx.getThingInterface().getName();
        String uri = getURI();
        String pkg = ctx.getPackageForClass(uri);
        if (ctx.isUseEntireURIForIdentifiers())
            return pkg + "." + JavaIdentifierEncoder.encode(uri);
        else
            return pkg + "." + JavaIdentifierEncoder.encode(getLocalName());
    }

    public String getImplFullClassname() {
        return getInterfaceFullClassname() + "Impl";
    }

    public String getListenerFullClassname() {
        return getInterfaceFullClassname() + "Listener";
    }

    public File getInterfaceFile(File baseDir) {
        return new File(baseDir, getInterfaceFullClassname().replace('.', '/') + ".java");
    }

    public File getImplFile(File baseDir) {
        return new File(baseDir, getImplFullClassname().replace('.', '/') + ".java");
    }

    public File getListenerFile(File baseDir) {
        return new File(baseDir, getListenerFullClassname().replace('.', '/') + ".java");
    }

    public String getInterfaceClassname() {
        return getInterfaceFullClassname().substring(getInterfaceFullClassname().lastIndexOf('.') + 1);
    }

    public String getImplClassname() {
        return getImplFullClassname().substring(getImplFullClassname().lastIndexOf('.') + 1);
    }

    public String getListenerClassname() {
        return getListenerFullClassname().substring(getListenerFullClassname().lastIndexOf('.') + 1);
    }

    public String getPackageName() {
        return ctx.getPackageForClass(getURI());
    }

    public String getFactoryFullClassname() {
        if (ontClass == null) {
            return ctx.getThingFactory().getName();
        }
        String onturi = ctx.getOntologyForClass(getURI());
        Resource ontres = ontModel.getResource(onturi);
        //String pkg = ctx.getPackageForOntology(onturi);
        String pkg = getPackageName();
        String classname = null;
        if (ctx.isUseEntireURIForIdentifiers())
            classname = pkg + "." + JavaIdentifierEncoder.encode(onturi) + "Factory";
        else {
        	if (ontres.getLocalName() == null) {
        		System.err.println("ontres null: " + ontres);
        		System.err.println(ontres.getProperty(RDF.type));
        	}
            classname = pkg + "." + JavaIdentifierEncoder.encode(ontres.getLocalName()) + "Factory";
        }
        return classname;
    }

    public String getFactoryClassname() {
        return getFactoryFullClassname().substring(getFactoryFullClassname().lastIndexOf('.') + 1);
    }

    public File getFactoryFile(File baseDir) {
        return new File(baseDir, getFactoryFullClassname().replace('.', '/') + ".java");
    }
    
    /**
     * Returns whether or not the given class is an ancestor of this class.
     */
    public boolean isAncestor(OntologyClass oc) {
    	return listAllExtensionClasses().contains(oc);
    }
    
    private List propertyListWithExtensionClasses = null;
    private List propertyListNoExtensionClasses = null;
    
    /**
     * TODO: we might be able to speed-up code generation by caching the results of this
     * call
     * List properties of this class
     * @param includeExtensionClasses whether or not to include properties in extensions from
     * subClassOf, interesectionOf, and unionOf
     * @return
     */
    public List listProperties(boolean includeExtensionClasses) {
    	if (includeExtensionClasses && propertyListWithExtensionClasses != null)
    		return propertyListWithExtensionClasses;
    	if (!includeExtensionClasses && propertyListNoExtensionClasses != null)
    		return propertyListNoExtensionClasses;
        try {
            List props = listProperties(includeExtensionClasses, new ArrayList());
            List allprops = null;
            if (includeExtensionClasses)
            	allprops = props;
            else {
            	allprops = listProperties(true,new ArrayList());
            	purgeLooseRestrictions(allprops);
            }
            purgeLooseRestrictions(props);
            markDuplicates(props, allprops);
            if (includeExtensionClasses)
            	propertyListWithExtensionClasses = props;
            else 
            	propertyListNoExtensionClasses = props;
            return props;
        } catch (JastorException e) {
            e.printStackTrace();
            return new ArrayList();
        }
    }

    /**
     * provides a deep listing of all named extension classes. Unfortunately, we can't use
     * this routine to do much, but we do use to generate type-closure code.
     * 
     * @return
     */
    public List listAllExtensionClasses() {
        try {
	        List visited = new ArrayList();
	        return listAllExtensionClasses(visited);
        } catch (JastorException e) {
            e.printStackTrace();
            return new ArrayList();
        }
    }

    /**
     * Resturn a list of immediate extension classes derived from
     * subClassOf, intersectionOf and unionOf
     * 
     * @return
     */
    public List listImmediateExtensionClasses() {
        try {
            return listImmediateExtensionClasses(new ArrayList(), false);
        } catch (JastorException e) {
            e.printStackTrace();
            return new ArrayList();
        }
    }

    /**
     * Finds a cardinality restriction in sub-class hierarchy for the given
     * property. This method is needed because some super-classes won't specify
     * a range, but all of there subclasses will have the same (hopefully)
     * restriction. If not, this approach will surely break.
     * 
     * @param prop
     * @return
     */
    public Restriction findCardinalityRestrictionInSubClassHierarchy(OntologyProperty prop) throws JastorException {
        Iterator it = ontClass.listSubClasses(true);
        
        // if this a union class, one of its operands might have
        // a cardinality restriction.  Since they are subclassing us,
        // we should have the same restriction as well.
        if (ontClass.isUnionClass()) {
            List list = new ArrayList();
	        while (it.hasNext()) {
	            list.add(it.next());
	        }
	        UnionClass uc = (UnionClass)ontClass.as(UnionClass.class);
	        it = uc.listOperands();
	        while (it.hasNext()) {
	            OntClass oc = (OntClass)it.next();
	            if (!oc.isAnon())
	                list.add(oc);
	        }
	        it = list.iterator();
        }     
        
        List visited = new ArrayList();
        visited.add(ontClass);
        Restriction res = null;
        while (it.hasNext() && res == null) {
            OntClass c = (OntClass) it.next();
            if (!visited.contains(c)) {
                visited.add(c);
                OntologyClass oc = new OntologyClass(c, ctx);
                res = oc.findCardinalityRestrictionInSubClassHierarchy(prop, visited);
            }
        }
        
        return res;
    }   
    
    public List listIndividuals() {
    	ArrayList list = new ArrayList();
    	StmtIterator itr = ontModel.listStatements(null,RDF.type,ontModel.getResource(getURI()));
    	while (itr.hasNext()) {
    		Statement stmt = (Statement)itr.next();
    		Resource subject = stmt.getSubject();
    		list.add(subject);
    	}
    	return list;
    } 
    
    public String getIndividualIdentifierName(Resource ind) {
    	if (individualDuplicateLocalName(ind)) {
    		return JavaIdentifierEncoder.encode(ind.getURI());
    	} else
    		return JavaIdentifierEncoder.encode(ind.getLocalName());
    }
    
    public boolean isEnumeratedClass() {
    	return ontClass.isEnumeratedClass();
    }
    
    public List listOneOfClasses() {
    	EnumeratedClass ec = ontClass.asEnumeratedClass();
    	ExtendedIterator itr = ec.listOneOf();
    	List list = new ArrayList();
    	while (itr.hasNext()) {
    		list.add(itr.next());
    	}
    	return list;
    }
    
    private boolean individualDuplicateLocalName(Resource ind) {
    	StmtIterator itr = ontModel.listStatements(null,RDF.type,ontModel.getResource(getURI()));
    	boolean found = false;
    	while (itr.hasNext()) {
    		Statement stmt = (Statement)itr.next();
    		Resource subject = stmt.getSubject();
    		if (subject.getLocalName().equals(ind.getLocalName())) {
    			if (found)
    				return true;
    			else
    				found = true;
    		}
    	}
    	return false;
    }

    private Restriction findCardinalityRestrictionInSubClassHierarchy(OntologyProperty prop, List visited) throws JastorException {
        Query query = new Query("SELECT ?res WHERE (?class,rdfs:subClassOf,?res)" + "(?res,owl:onProperty,?prop)" + " USING rdfs FOR <http://www.w3.org/2000/01/rdf-schema#>, owl FOR <http://www.w3.org/2002/07/owl#>");
        query.setSource(ctx.getOntModel());
        QueryEngine qe = new QueryEngine(query);
        ResultBindingImpl initialBinding = new ResultBindingImpl();
        initialBinding.add("class", ontClass);
        initialBinding.add("prop", prop.getOntProperty());
        QueryResults results = null;
        Resource restrictionRes = null;
        try {
            try {
                results = qe.exec(initialBinding);
                while (results.hasNext()) {
                    ResultBinding binding = (ResultBinding) results.next();
                    restrictionRes = (Resource) binding.get("res");
                    if (restrictionRes != null) {
                        Restriction r = (Restriction)restrictionRes.as(Restriction.class);
                    	if (r.isCardinalityRestriction() || r.isMaxCardinalityRestriction())
                    	    return r;
                    }
                }
            } finally {
                if (results != null)
                    results.close();
            }
        } catch (Exception e) {
            throw new JastorException(e, "Error querying for restriction");
        }        
        
        ExtendedIterator it = ontClass.listSubClasses(true);
        Restriction restriction = null;
        while (it.hasNext() && restriction == null) {
            OntClass c = (OntClass) it.next();
            if (!visited.contains(c)) {
                OntologyClass oc = new OntologyClass(c, ctx);
                restriction = oc.findCardinalityRestrictionInSubClassHierarchy(prop, visited);
            }
        }
        if (it.hasNext())
            it.close();
        return restriction;            
    }
    
    private HashMap listCache = new HashMap();

    private List listProperties(boolean includeExtensionClasses, List visitedClasses) throws JastorException {
        String cacheKey = String.valueOf(includeExtensionClasses + visitedClasses.toString());
        List cachedList = (List)listCache.get(cacheKey);
        if (cachedList != null) {
        	return cachedList;
        }
    	
    	ArrayList list = new ArrayList();
        // we may have visited this class since it was queued up to search. So just add
        // a final cycle check as a catch-all
        if (visitedClasses.contains(ontClass))
            return list;        
        visitedClasses.add(ontClass);
        // add properties from immediate superclasses and unions (which will
        // themselves, recurse)
        if (includeExtensionClasses) {
            List extClasses = listImmediateExtensionClasses(visitedClasses,false);
            Iterator it = extClasses.iterator();
            while (it.hasNext()) {
                OntologyClass extClass = (OntologyClass) it.next();
                if (!extClass.getOntClass().isAnon()) {
	                List extClassProperties = extClass.listProperties(true, visitedClasses);
	                // this call overwrites the role made in any recursive call
	                // because we only care about role from the perspective of the root
	                // caller
	                setRoleOfProperties(extClassProperties, OntologyProperty.ROLE_EXTENSIONCLASS);
	                // must check for duplicates 
	                Iterator itr = extClassProperties.iterator();
	                while (itr.hasNext()) {
	                    OntologyProperty extprop = (OntologyProperty)itr.next();
	                    if (!list.contains(extprop))
	                        list.add(extprop);
	                }
                }
            }
            
            // merge loose restrictions with actual properties
            mergeProperties(list);
            
            
        }
        
        
        // check for any restrictions declared here that may apply to properties
        // from extension classes.  Restrictions are contained in anonymous 
        // extension classes
        // note, we moved this outside the include extension class case because want to get union domain properties
        // in both cases.  Even though union domains are handled elsewhere, we keep this code outside that condition
        Iterator it = listImmediateExtensionClasses(visitedClasses,true).iterator();
        while (it.hasNext()) {
            OntologyClass extClass = (OntologyClass) it.next();
            if (extClass.getOntClass().isRestriction()) {

                Restriction res = (Restriction) extClass.getOntClass().asRestriction();
                
                OntProperty prop = null;
                try {
                	prop = res.getOnProperty();
                } catch (ConversionException e) {
                	System.err.println("Warning: class " + getURI() + " has restrictions on unknown properties");
                	continue;
                }
                Iterator itr2 = list.iterator();
                OntologyProperty restrictedProp = null;
                // iterate through the list of properties found so far and see if there is one
                // the restriction applies to
                while (itr2.hasNext()) {
                    OntologyProperty op = (OntologyProperty) itr2.next();
                    if (op.getOntProperty().getURI().equals(prop.getURI())) {
                        restrictedProp = op;
                        break;
                    }
                }
                // add the restriction to the proprety..will be dealt with by
                // the property
                if (restrictedProp != null)
                    restrictedProp.addRestriction(res);
                // if we didn't find a property in the extension classes for the restriction, 
                // we have a few choices to make...
                else {
                    if (prop.getDomain() != null && prop.getDomain().isAnon() && prop.getDomain().canAs(UnionClass.class)) {
                        // ignore restrictions on union domains, they are taken care of below
                    } else if (prop.getDomain() == null || prop.getDomain().equals(RDFS.Resource)) {
                    	// if the domain is null, then we have a property with no declared domain.  Although, we suspect
                    	// this is not good OWL, we allow the user to do it anyway and assume the user wants to have
                    	// this class as a possible domain.
                    	list.add(new OntologyProperty(prop,this));
                    } else if (prop.getDomain().isAnon() || !prop.getDomain().getURI().equals(getURI())) {
                    	// add a placeholder for this restriction if it doesn't belong to us.  This restriction
                    	// will get added to the property from another branch of the type-hierarchy.
                        list.add(new OntologyProperty(prop, res));
                    } else {
                        // In rare cases, a restriction might be added to this property through
                        // an Intersection, not subclass.  This handles that case.
                        if (getOntClass().isIntersectionClass()) {
                            IntersectionClass ic = (IntersectionClass)getOntClass().as(IntersectionClass.class);
                            Iterator ops = ic.listOperands();
                            // if one of the operands is the restriction we are considering
                            // then add the restricted property and the restriction
                            while (ops.hasNext()) {
                                OntClass op = (OntClass)ops.next();
                                if (op.equals(res)) {
                                    
			                        OntologyProperty property = new OntologyProperty(prop,this);
			                        // add the restriction because getPrimaryRestriction in the constructor won't 
			                        // find it.
			                        property.addRestriction(res);
			                        list.add(property);
                                }
                            }
                        }
                    } // otherwise, we have a normal restriction on a declared or union domain property, 
                      // handeled below
                }

            }
        } // end restriction search
        
        // finally, add properties declared here
        List declprops = new ArrayList();
        
        // default for version 2.1
        boolean declaredPropsArgument = false;
        if (Integer.parseInt(Jena.MINOR_VERSION) >= 2)
        	declaredPropsArgument = true;
        Iterator itr = ontClass.listDeclaredProperties(declaredPropsArgument);
        while (itr.hasNext()) {
            declprops.add(itr.next());
        }
        itr = ctx.getUnionDomainProperties(getURI()).iterator();
        while (itr.hasNext()) {
            declprops.add(itr.next());
        }
        
        addOpenDomainProperties(declprops);
        
        itr = declprops.iterator();
        while (itr.hasNext()) {
            OntProperty ontProperty = (OntProperty) itr.next();
            // make sure that if we have an open domain property
            // that it is defined in this ontology..skip it otherwise
            if (ontProperty.getDomain() == null || ontProperty.getDomain().equals(RDFS.Resource) || ontProperty.getDomain().equals(OWL.Thing)) {
            	if (!ctx.isPropetyAndClassDefinedInSameOntology(ontProperty.getURI(),getURI()))
            		continue;
            }
            // more efficient to scan the list than construct an OntologyProperty unnecessarily
            // to check for List membership because the constructor does a query.
            boolean found = false;
            it = list.iterator();
            while (it.hasNext() && !found) {
                OntologyProperty p = (OntologyProperty)it.next();
                if (p.getURI().equals(ontProperty.getURI()))
                    found = true;
            }
            if (!found) {
                OntologyProperty prop = new OntologyProperty(ontProperty, this); 
                list.add(prop);
            }
        }
        listCache.put(cacheKey,list);
        return list;
    }
    
    /**
     * compute a list of OntProperty that have no domain, or domain of Resource or Thing
     * @return
     */
    private void addOpenDomainProperties(List propertyList) {
    	 // Jena has inconsistent behavior when adding open domain properties to the 
         // class frames..so, we basically have to do it ourselves, and rule out duplicates
         // in the case that jena provides them
    	Resource[] openDomainResources = new Resource[]{RDFS.Resource,OWL.Thing};
    	
    	for (int i=0;i<openDomainResources.length;i++) {
    	StmtIterator itr = ctx.getOntModel().listStatements(null,RDFS.domain,openDomainResources[i]);
	    	while (itr.hasNext()) {
	    		Statement stmt = itr.nextStatement();
	    		Resource propres = stmt.getSubject();
	    		OntProperty prop = ctx.getOntModel().getDatatypeProperty(propres.getURI());
	    		if (prop == null)
	    			prop = ctx.getOntModel().getObjectProperty(propres.getURI());
	    			if (!propertyList.contains(prop))
	    				propertyList.add(prop);
	    	 }
    	}
    	
    	Resource[] propTypes = new Resource[]{OWL.DatatypeProperty,OWL.ObjectProperty};
    	
    	for (int i=0;i<propTypes.length;i++) {
//	    	String sparql = "SELECT ?res " + 
//	    			 " { ?res a <" + propTypes[i].getURI() + "> . " + 
//	    			 " OPTIONAL { ?res <" + RDFS.domain.getURI()+ "> ?prop . } " + 
//	    			 " FILTER (!bound(?prop)) . " +
//	    			 " } ";
	    	QuerySolutionMap initialBinding = new QuerySolutionMap();
	    	initialBinding.add("proptype",propTypes[i]);
	    	QueryExecution qe = QueryExecutionFactory.create(nullDomainQuery,ctx.getOntModel(),initialBinding);
	    	ResultSet rs = qe.execSelect();
	    	while (rs.hasNext()) {
	    		QuerySolution qs = rs.nextSolution();
	    		Resource propres = qs.getResource("res");
	    		OntProperty prop = ctx.getOntModel().getDatatypeProperty(propres.getURI());
	    		if (prop == null)
	    			prop = ctx.getOntModel().getObjectProperty(propres.getURI());
	    			if (!propertyList.contains(prop))
	    				propertyList.add(prop);
	    	}
    	}
    }

    private List listAllExtensionClasses(List visited) throws JastorException {
        visited.add(ontClass);
        List scs = listImmediateExtensionClasses(visited,false);
        List recur = new ArrayList();
        Iterator it = scs.iterator();
        while (it.hasNext()) {
            OntologyClass oc = (OntologyClass) it.next();
            if (!visited.contains(oc.getOntClass())) {
                List list = oc.listAllExtensionClasses(visited);
                for (int i=0;i<list.size();i++) {
                    if (!recur.contains(list.get(i))) {
                        recur.add(list.get(i));
                    }
                }
            }
        }
        recur.addAll(scs);
        return recur;
    }
    
    private List listImmediateExtensionClasses(List visited, boolean includeAnon) throws JastorException {
        // get classes from subClassOf
        List list = listImmediateSuperClasses(visited, includeAnon);
        // get classes from other side of unionOf
        List list2 = listImmediateUnionClasses(visited);
        for (int i=0;i<list2.size();i++) {
            if (!list.contains(list2.get(i)))
                list.add(list2.get(i));
        }
        // get classes from intersectionOf
        list2 = listImmediateIntersectionClasses(visited, includeAnon);
        for (int i=0;i<list2.size();i++) {
            if (!list.contains(list2.get(i)))
                list.add(list2.get(i));
        }
        return list;
    }

    private List listImmediateSuperClasses(List visited, boolean includeAnon) {
        ExtendedIterator itr = ontClass.listSuperClasses(true);
        ArrayList list = new ArrayList();
        while (itr.hasNext()) {
            OntClass ontClass = (OntClass) itr.next();
            if (!includeAnon && ontClass.isAnon())
                continue;
            if (ontClass.getLocalName() != null && ontClass.getLocalName().equals("Resource"))
                continue;
            if (ontClass.getURI() != null && ontClass.getURI().equals("http://www.w3.org/2002/07/owl#Thing"))
            	continue;
            if (visited.contains(ontClass))
                continue;
            OntologyClass oc = new OntologyClass(ontClass, ctx);
            list.add(oc);
        }
        return list;
    }

    /**
     * This method not returns a list of classes that are a union of us because those are 
     * what we want to extend
     * @param visited
     * @return
     */
    private List listImmediateUnionClasses(List visited) throws JastorException {
        Iterator itr = ctx.getUnionClassExtensions(getURI()).iterator();
        ArrayList list = new ArrayList();
        while (itr.hasNext()) {
            OntologyClass ontologyClass = (OntologyClass) itr.next();
            if (visited.contains(ontologyClass.getOntClass()))
                continue;
            list.add(ontologyClass);
        }
        return list;
    }
    
    private List listImmediateIntersectionClasses(List visited, boolean includeAnon) {
        List list = new ArrayList();
        if (ontClass.isIntersectionClass()) {
            IntersectionClass ic = (IntersectionClass) ontClass.as(IntersectionClass.class);

            ExtendedIterator it = ic.listOperands();
            while (it.hasNext()) {
                Object next = it.next();
                OntClass ontClass = (OntClass) next;
                if (!includeAnon && ontClass.isAnon())
                    continue;
                if (!visited.contains(ontClass)) { // not sure if this is correct for intersection
                    OntologyClass oc = new OntologyClass(ontClass, ctx);
                    list.add(oc);
                }
            }
        }
        return list;
    }

    /**
     * Merge any loose restriction properties into the property it comes from
     * 
     * @param props
     */
    private void mergeProperties(List props) {
        // this is innefficient, but the size of these lists should be small.

        // this list stores loose restrictions that cannot be merged at this
        // level and must be passed down to
        // the calling subclass or union class.
        List unboundlr = new ArrayList();
        OntologyProperty lr = findLooseRestriction(props);

        while (lr != null) {
            props.remove(lr);
            OntologyProperty prop = findRealProp(props, lr);
            if (prop == null) // not sure about this...will follow up later.
                unboundlr.add(lr);
            else
                prop.addRestrictions(lr.getRestrictions());
            lr = findLooseRestriction(props);
        }
        props.addAll(unboundlr);
    }

    /**
     * Find a real property for a loose restriction
     */
    private OntologyProperty findRealProp(List props, OntologyProperty lr) {
        Iterator it = props.iterator();
        while (it.hasNext()) {
            OntologyProperty prop = (OntologyProperty) it.next();
            if (prop.isLooseRestriction())
                continue;
            if (prop.getURI().equals(lr.getURI())) {
                return prop;
            }
        }
        return null;
    }

    /**
     * returns a loose restriction if one exists
     * 
     * @return
     */
    private OntologyProperty findLooseRestriction(List props) {
        Iterator it = props.iterator();
        while (it.hasNext()) {
            OntologyProperty prop = (OntologyProperty) it.next();
            if (prop.isLooseRestriction()) {
                return prop;
            }
        }
        return null;
    }

    /**
     * remove any loose restrictions from this property list
     * 
     * @param props
     */
    private void purgeLooseRestrictions(List props) {
        Iterator it = props.iterator();
        List toRemove = new ArrayList();
        while (it.hasNext()) {
            OntologyProperty prop = (OntologyProperty) it.next();
            if (prop.isLooseRestriction()) {
//            	/System.err.println("removing loose res: " + prop);
                toRemove.add(prop);
            }
        }
        props.removeAll(toRemove);
    }

    /**
     * Set the role of the all the given properties. Also make this
     * OntologyClass the activeClass
     * 
     * @param props
     * @param role
     */
    private void setRoleOfProperties(List props, int role) {
        Iterator it = props.iterator();
        while (it.hasNext()) {
            OntologyProperty prop = (OntologyProperty) it.next();
            prop.setRole(role);
            prop.setActiveClass(this);
        }
    }
    
    /**
     * Go through the list of properties and mark any properties as duplicates...
     */
    private void markDuplicates(List props, List allprops) {
    	for (int i=0;i<allprops.size();i++) {
    		OntologyProperty propi = (OntologyProperty)allprops.get(i);
    		String namei = propi.getPropertyName();
    		for (int j=0;j<props.size();j++) {
    			OntologyProperty propj = (OntologyProperty)props.get(j);
        		String namej = propj.getPropertyName();
        		if (!propi.toString().equals(propj.toString()) && namei.equals(namej)) {
        			propj.setDuplicateProperty(true);
        		}
    		}
    	}
    }

}