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
import java.util.Iterator;
import java.util.List;

import com.hp.hpl.jena.datatypes.RDFDatatype;
import com.hp.hpl.jena.datatypes.TypeMapper;
import com.hp.hpl.jena.datatypes.xsd.XSDDatatype;
import com.hp.hpl.jena.ontology.AllValuesFromRestriction;
import com.hp.hpl.jena.ontology.CardinalityRestriction;
import com.hp.hpl.jena.ontology.DataRange;
import com.hp.hpl.jena.ontology.HasValueRestriction;
import com.hp.hpl.jena.ontology.IntersectionClass;
import com.hp.hpl.jena.ontology.MaxCardinalityRestriction;
import com.hp.hpl.jena.ontology.OntClass;
import com.hp.hpl.jena.ontology.OntModel;
import com.hp.hpl.jena.ontology.OntProperty;
import com.hp.hpl.jena.ontology.Restriction;
import com.hp.hpl.jena.ontology.SomeValuesFromRestriction;
import com.hp.hpl.jena.ontology.UnionClass;
import com.hp.hpl.jena.rdf.model.Literal;
import com.hp.hpl.jena.rdf.model.Resource;
import com.hp.hpl.jena.rdf.model.impl.ResourceImpl;
import com.hp.hpl.jena.rdql.Query;
import com.hp.hpl.jena.rdql.QueryEngine;
import com.hp.hpl.jena.rdql.QueryResults;
import com.hp.hpl.jena.rdql.ResultBinding;
import com.hp.hpl.jena.rdql.ResultBindingImpl;
import com.hp.hpl.jena.vocabulary.OWL;
import com.hp.hpl.jena.vocabulary.RDF;
import com.hp.hpl.jena.vocabulary.RDFS;
import com.ibm.adtech.jastor.JastorContext;
import com.ibm.adtech.jastor.JastorException;
import com.ibm.adtech.jastor.JavaIdentifierEncoder;

/**
 * 
 * @author Ben Szekely ( <a href="mailto:bhszekel@us.ibm.com">bhszekel@us.ibm.com </a>)
 *  
 */
public class OntologyProperty {
    
    public static final String copyright = "(C) Copyright IBM Corporation 2005  All Rights Reserved.";
    
    /**
     * this uri is a placeholder for the default range of a property that is returned by 
     * getAllReturnTypes.  We don't use the actual default return type URI in that call because
     * it might be null.
     */
    public static final Resource DEFAULT_RANGE = new ResourceImpl("http://jastor.adtech.ibm.com/defaultRange");    
    
    public static final int ROLE_HERE = 0; // this property is defined in the class we are generating
    public static final int ROLE_EXTENSIONCLASS = 1; // this property is defined in an extensino class of the generating class
    public static final int ROLE_LOOSE_RESTRICTION = 2; // this property is a placeholder for a loose restriction to send downstream

    List restrictions = new ArrayList(); // the end of the list contains the most downstream restriction

    List ranges = null; // this list stores multiple ranges in the case that the the range of the property
					   // is defined as a Union.  Otherwise, the list will not be used.
					   // These will considered as coming from the active class.
    				   // we may eventually clean this up when we see more clearly how these multiple
                       // ranges are used.
    					
    
    /**
     * The ontology class where this property was orginally defined, i.e. the top of the hierarchy.
     */
    private OntologyClass ontologyClass;

    private OntProperty ontProperty;
    
    private OntClass ontClass;
    
    private OntModel ontModel;
    
    private JastorContext ctx;
    
    private OntologyComment comment;
    
    /** 
     * the active class is the ontology class that this property is being
     * used to generate, not to be confused with the ontologyClass where
     * the property was originally defined
     */
    private OntologyClass activeClass;
    
    /**
     * The role of this property class, see above for the different options
     */
    private int role = ROLE_HERE;
    
    /**
     * If this flag is ture, it means that this property exists in the frame 
     * of an ontology class that has another property with the same local name
     */
    private boolean duplicateProperty = false;

    /**
     *  
     */
    public OntologyProperty(OntProperty ontProperty, OntologyClass ontologyClass) throws JastorException {
        this.ontProperty = ontProperty;
        this.ontologyClass = ontologyClass;
        this.ontClass = ontologyClass.getOntClass();
        this.activeClass = ontologyClass;
        ctx = ontologyClass.getContext();
        this.ontModel = ctx.getOntModel();
        computePrimaryRestrictions();
        checkForMultipleBaseRanges();
        this.comment = new OntologyComment(this.ontProperty);
    }
    
    /**
     * This version of the constructor should only be use if the instance is used for
     * utility methods on the property..i.e. getName, etc...
     * @param ontProperty
     * @throws JastorEception
     */
    public OntologyProperty(OntProperty ontProperty, JastorContext ctx) throws JastorException {
    	this.ontProperty = ontProperty;
    	this.ctx = ctx;
    }
    
    /**
     * This constructor is used to create a place holder for loose restriciton on a property that might not actually
     * be found in the search because of the visited list.  OntologyClasses should return instances of these 
     * properties if they have restrictions on properties not found in their recursive search.
     * 
     * @param ontProperty
     */
    OntologyProperty(OntProperty ontProperty, Restriction restriction) {
        this.ontProperty = ontProperty;
        restrictions.add(restriction);
        role = ROLE_LOOSE_RESTRICTION;
    }
    
//    /**
//     * Set the ontology class of this property.  Used when a loose restriction is promoted to an actualy property.
//     * This happens when a seemingly loose restriction is the only attachment of a property to a class. I.e. the
//     * type/union hierarchy is screwed up.
//     * @return
//     */
//    public void setOntologyClass(OntologyClass oc) {
//        this.ontologyClass = oc;
//        this.ontClass = oc.getOntClass();
//        this.ctx
//    }
    
    /**
     * Returns whether or not this instance is a placeholder for a restriction on a property
     * @return
     */
    public boolean isLooseRestriction() {
        return (ontologyClass == null);
    }

    /**
     * return the role of the property's participation in the owning class.
     * @return
     */
    public int getRole() {
        return role;
    }
    
    /**
     * set the role of the property
     * @param role
     */
    public void setRole(int role) {
        this.role = role;
    }
        
    /**
     * Get the onotology class begin generated.
     * @return
     */
    public OntologyClass getActiveClass() {
        return activeClass;
    }
    
    public void setActiveClass(OntologyClass activeClass) {
        this.activeClass = activeClass;
    }
    
    /**
     * Get the ontology class that declares this property
     * @return
     */
    public OntologyClass getOntologyClass() {
        return ontologyClass;
    }

    /**
     * Get the ontProperty declaration
     * @return
     */
    public OntProperty getOntProperty() {
        return ontProperty;
    }
    
    /**
     * Get the comment info for this property
     * @return
     */
    public OntologyComment getComment() {
        return comment;
    }

    /**
     * yields the restriction declared with the original declaration of the property
     * @return
     */
    public Restriction getPrimaryRestriction() {
        return (Restriction)restrictions.get(0);
    }
    
    /**
     * 
     * @param res
     */
    public void addRestriction(Restriction res) {
        // must check if we aready have this restriction
        if (!restrictions.contains(res))
            restrictions.add(res);
    } 
    
    /**
     * 
     * @param res
     */
    public void addRestrictions(List res) {
        Iterator it = res.iterator();
        while (it.hasNext()) {
            Restriction r = (Restriction)it.next();
            addRestriction(r);
        }
    }
    
    /**
     * 
     * @return
     */
    public Iterator listRestrictions() {
        return restrictions.iterator();
    }
    
    /**
     * 
     * @return
     */
    public List getRestrictions() {
        return restrictions;
    }
    
    /**
     * Determine if this property can take on multiple values
     * @return
     */
    public boolean isMultiValued() {
        // find the first cardinality restriction we see
    	if (ontProperty.isFunctionalProperty())
    		return false;
        for (int i=restrictions.size()-1;i>=0;i--) {
            Restriction res = (Restriction)restrictions.get(i);
            if (res.isCardinalityRestriction()) {
                CardinalityRestriction cr = (CardinalityRestriction)res.as(CardinalityRestriction.class);
                if (cr.getCardinality() > 1)
                    return true;
                else
                    return false;
            }
            if (res.isMaxCardinalityRestriction()) {
                MaxCardinalityRestriction cr = (MaxCardinalityRestriction)res.as(MaxCardinalityRestriction.class);
                if (cr.getMaxCardinality() > 1)
                    return true;
                else
                    return false;
            }
        }
        
        return true;
    }
    
    public boolean isSingleValued() {
        return !isMultiValued();
    }
    
    public boolean isObjectProperty() {
        return ontProperty.isObjectProperty();
    }  
    
    public boolean isDatatypeProperty() {
        return ontProperty.isDatatypeProperty();
    }
    
    /**
     * Return the name of the property to be used for variable names and as a prefix for 
     * vocabulary property constants
     * @return
     */
    public String getPropertyName() {
    	if (ctx == null) {
    		System.err.println(getURI());
    		System.err.println(isLooseRestriction());
    	}
    	if (ctx.isUseEntireURIForIdentifiers())
            return JavaIdentifierEncoder.encode(ontProperty.getURI());
        else {
        	String name = JavaIdentifierEncoder.encode(ontProperty.getLocalName());
        	if (duplicateProperty) {
        		name = addPrefix(name);
        	}
        	return name;
        }
    }
    
    /**
     * Return the name of the proerty to be used for variable names and constants using 
     * the restricted range argument
     * @return
     */
    public String getPropertyName(Resource restrictedRange) {
        // handle the case where we are being called with the default range
        if (restrictedRange != null && restrictedRange.equals(DEFAULT_RANGE))
            return getPropertyName();
        String resrange = getReturnType(restrictedRange);
        if (ctx.isUsePackageNameForRestrictedRanges()) 
            resrange = resrange.replace('.','_');
        else
            resrange = resrange.substring(resrange.lastIndexOf('.')+1);
        String name = getPropertyName() + "_as" + resrange;
        if (duplicateProperty) {
    		name = addPrefix(name);
    	}
    	return name;
    }
    
    /**
     * Return the name of the property capitalized, sould be prepended for use
     * in method names
     * @return
     */
    public String getPropertyCapped()  {
        if (ctx.isUseEntireURIForIdentifiers())
            return capitalize(JavaIdentifierEncoder.encode(ontProperty.getURI()));
        else {
            String name = capitalize(JavaIdentifierEncoder.encode(ontProperty.getLocalName()));
            if (duplicateProperty) {
        		name = capitalize(addPrefix(name));
        	}
        	return name;
        }
    }
    
    public String getPropertyCapped(Resource restrictedRange) {
        // handle the case where we are being called with the default range
        if (restrictedRange != null && restrictedRange.equals(DEFAULT_RANGE))
            return getPropertyCapped();
        String resrange = getReturnType(restrictedRange);
        if (ctx.isUsePackageNameForRestrictedRanges()) 
            resrange = resrange.replace('.','_');
        else
            resrange = resrange.substring(resrange.lastIndexOf('.')+1);
        
        String toappend = "";
        if (restrictedRange != null) {
            toappend = "_as" + resrange;
        }
        if (ctx.isUseEntireURIForIdentifiers())
            return capitalize(JavaIdentifierEncoder.encode(ontProperty.getURI()) + toappend);
        else {
            String name = capitalize(JavaIdentifierEncoder.encode(ontProperty.getLocalName()) + toappend);
            if (duplicateProperty) {
        		name = capitalize(addPrefix(name));
        	}
        	return name;
        }
    }    
    
    // We need both of the next methods because some template sections will operate on all ranges 
    // separately while others will operate on the default and alt together.  Also, some will be interested in
    // alt ranges for the entire hierarchy, or just the activeClass
    
    /**
     * Return an Iterator of ranges for this property
     * @param all whether or not to include the default range in addition to all default ranges
     * @param activeClassOnly whether or not to include ranges in the activeClass only
     */
    public Iterator listRanges(boolean all, boolean activeClassOnly) {
        if (all)
            return listAllRanges(activeClassOnly);
        else
            return listAlternativeRanges(activeClassOnly);
    }
     
    /**
     * Return an an interator or Resources that represent additional
     * ranges for this property. The activeClassOnly flag determines if 
     * we consider only those restrictions declared in the active class
     */
    public Iterator listAlternativeRanges(boolean activeClassOnly) {
        List list = listAlternativeRangesList(activeClassOnly);
        return list.iterator();
    }
    
    /**
     * Return an an interator or Resources that represent all possible
     * ranges for this property gathered from the entire hierarchy.
     * An element in the iterator will be a special Resource indicating the default
     * return type.  As such, the items in the Iterator may be used in calls to 
     * getReturnType(), getPropertyName(), and getPropertyCapped() but should not 
     * be treated as verbatim.
     */
    public Iterator listAllRanges() {
        return listAllRanges(false);
    }
    
    /**
     * Return an an interator or Resources that represent all possible
     * ranges for this property.  
     * the boolean parameter indicates whether we should include return types
     * declared in the activeClass only or in the entire hiearchy.  The active
     * class is the class we are generating for. 
     */
    public Iterator listAllRanges(boolean activeClassOnly) {
        List list = listAlternativeRangesList(activeClassOnly);
        //list.addAll(list);
        // we have to make sure that ranges don't get added twice because some ontologies
        // might cause this.
        if (ranges != null) {
        	Iterator it = ranges.iterator();
        	while (it.hasNext()) {
        		Object o = it.next();
        		if (!list.contains(o))
        			list.add(o);
        	}
        } else {
        	if (!list.contains(DEFAULT_RANGE))       
        		list.add(0,DEFAULT_RANGE);
        }
        
        return list.iterator();
    }
    
    /**
     * Get the string representation of the full java name of the return type for this property. 
     * The return type is based on the original range declaration of the property.
     * @return
     */
    public String getReturnType() { 
    	Resource range = null;
//    	 check for multiple base ranges.  
    	if (ranges != null) {
    		range = (Resource)ranges.get(0);
    	} else {
    		range = ontProperty.getRange();
    	}        
        return getReturnType(range);        
    }
    
    /**
     * Get the string representation of the full java name of the return type for this property. 
     * @param range indicates an alternative range or the DEFAULT_RANGE
     * @return
     */
    public String getReturnType(Resource range) {
        if (range != null && range.equals(DEFAULT_RANGE))
            return getReturnType();
        try {
	        if (isObjectProperty()) {            
	            if (range == null || range.equals(RDFS.Resource) || range.equals(OWL.Thing) || range.isAnon()) {
	                return ctx.getThingInterface().getName();
	            } else {
	                OntClass rangeclass = ontModel.getOntClass(range.getURI());
	                if (rangeclass == null)
	                    return ctx.getThingInterface().getName();
	                OntologyClass oc = new OntologyClass(rangeclass,ctx);
	                return oc.getInterfaceFullClassname();
	            }
	        } else {
	            if (range == null || range.equals(RDFS.Literal))
	                return Literal.class.getName();
	            if (range.hasProperty(RDF.type,OWL.DataRange)) {
	            	// for now, assume that the oneOf contains all the same time.
            		DataRange dr = (DataRange)range.as(DataRange.class);
            		Literal lit = (Literal)dr.getOneOf().iterator().next();
            		range = ontModel.getResource(lit.getDatatypeURI());
            	}
	            TypeMapper mapper = TypeMapper.getInstance();
				mapper.registerDatatype(new XSDDatatype("anyURI",String.class));
	            RDFDatatype type = mapper.getTypeByName(range.toString());
	            if (type == null) {
	            	throw new JastorException ("An invalid datatype was specified for property: " + getURI() + " | " + range.toString());
	            }
	            Class cls = type.getJavaClass();
	            /* what we had here seems decidedly wrong!
	            if (cls == null) {
	                cls = type.getClass();
	            }
	            */
	            // not sure if this is better, but it seems to be what Jena 
	            // does if it doesn't have a java class registered, ala XMLLiteral
	            if (cls == null)
	            	cls = String.class;
	            return cls.getName();
	        }
        } catch (JastorException e) {
            e.printStackTrace();
            return isDatatypeProperty() ? Literal.class.getName() : ctx.getThingInterface().getName();
        }
    }
    
    /**
     * Get the string representation of the full java name of the return type for this property. 
     * The return type is based on the original range declaration of the property.
     * @return
     */
    public String getRangeURI() { 
    	Resource range = null;
//    	 check for multiple base ranges.  
    	if (ranges != null) {
    		range = (Resource)ranges.get(0);
    	} else {
    		range = ontProperty.getRange();
//    		if (range == null) {
//    			ontProperty.getSuperProperty()
//    		}
    	}        
        return getReturnType(range);        
    }
    
    /**
     * Get the string representation of the full range datatype URI
     * @return
     */
    public String getRangeURI(Resource res) { 
    	if (res != null && res.equals(DEFAULT_RANGE)) {
    		Resource range = null;
    		if (ranges != null) {
        		return ((Resource)ranges.get(0)).getURI();
        	} else {
        		return ontProperty.getRange().getURI();
        	} 
    	} else
    		return res.getURI();
    }
    
    
    
    /**
     * Get the ontology class for the default range of this property
     * @return
     */
    public OntologyClass getRangeOntologyClass() {       
        Resource range = ontProperty.getRange();
        return getRangeOntologyClass(range);        
    }
    
    /**
     * Get the ontology class for the given range of this property 
     * @param range indicates an alternative range or the DEFAULT_RANGE
     * @return
     */
    public OntologyClass getRangeOntologyClass(Resource range) {
        if (range != null && range.equals(DEFAULT_RANGE))
            return getRangeOntologyClass();
        if (isObjectProperty()) {
            if (range == null || range.equals(RDFS.Resource) || range.equals(OWL.Thing) || range.isAnon()) {
                return new OntologyClass(ctx); // OntologyClass for Thing
            } else {
                OntClass rangeclass = ontModel.getOntClass(range.getURI());
                if (rangeclass == null)
                    return new OntologyClass(ctx);
                OntologyClass oc = new OntologyClass(rangeclass,ctx);
                return oc;
            }
        } else {
            return null;
        }        
    } 
    
    /**
     * Return a list of RDFNodes that are values of hasValue for this property
     * @return
     */
    public List getHasValueValues() {
        List list = new ArrayList();
        Iterator it = restrictions.iterator();
        while (it.hasNext()) {
            Restriction res = (Restriction)it.next();
            if (res.isHasValueRestriction()) {
                HasValueRestriction hvr = (HasValueRestriction)res.as(HasValueRestriction.class);
                list.add(hvr.getHasValue());
            }
        }
        return list;
    }
    
    public void setDuplicateProperty(boolean duplicateProperty) {
    	this.duplicateProperty = duplicateProperty;
    }
    
    public String getURI() {
        return ontProperty.getURI();
    }    
    
    public String toString() {
        return ontProperty.getURI();
    }  
    
    public boolean equals(Object o) {
        if (isLooseRestriction())
            return super.equals(o); // test equality only on actual properties
        if (!(o instanceof OntologyProperty)) {
            return false;
        }
        OntologyProperty other = (OntologyProperty)o;
        if (other.isLooseRestriction())
            return super.equals(o);
        else 
            return getURI().equals(other.getURI());
    }
    
    public static String capitalize (String string) {
        return Character.toUpperCase(string.charAt(0)) + string.substring(1);
    }
    
    /**
     * list all possible return types other than the originally defined range. 
     * @param activeClassOnly determines whether we consider range restrictions declared
     * in the activeClass or the entire hierarchy
     * @return
     */
    private List listAlternativeRangesList(boolean activeClassOnly) {
        List list = new ArrayList();
           
        for (int i=restrictions.size()-1;i>=0;i--) {
            Restriction res = (Restriction)restrictions.get(i);
            if (activeClassOnly) {
                // check if the restriction belongs to the active class
                if (!activeClass.getOntClass().hasProperty(RDFS.subClassOf,res)) {
                    if (activeClass.getOntClass().isIntersectionClass()) {
                        IntersectionClass ic = (IntersectionClass)activeClass.getOntClass().as(IntersectionClass.class);
                        if (!ic.hasOperand(res))
                            continue;
                    } else
                        continue;
                }
            }
            if (res.isAllValuesFromRestriction()) {
                AllValuesFromRestriction avfr = (AllValuesFromRestriction)res.as(AllValuesFromRestriction.class);
                Resource range = avfr.getAllValuesFrom();
                if (range.equals(ontProperty.getRange()))
                	continue; // in the weird case that there is an AVF restriction that is the same as the range
                if (range.hasProperty(RDF.type,OWL.DataRange))
                    continue; // don't support DataRange yet
                if (range.hasProperty(OWL.oneOf))
                	continue; // don't support owl:oneOf yet
                if (range.canAs(UnionClass.class))
                	addUnionOperands(range,list);
                else {
                	if (!list.contains(range))
                		list.add(range);
                }
            } else if (res.isSomeValuesFromRestriction()) {
                SomeValuesFromRestriction svfr = (SomeValuesFromRestriction)res.as(SomeValuesFromRestriction.class);
                Resource range = svfr.getSomeValuesFrom();
                if (range.equals(ontProperty.getRange()))
                	continue;
                if (range.hasProperty(RDF.type,OWL.DataRange))
                    continue; // don't support DataRange yet
                if (range.canAs(UnionClass.class))
                	addUnionOperands(range,list);
                else {
                	if (!list.contains(range))
                		list.add(range);
                }
            }      
        }
        return list;
    }
    
    private void addUnionOperands(Resource range, List list) {
		UnionClass uc = (UnionClass)range.as(UnionClass.class);
		Iterator it = uc.listOperands();
		while (it.hasNext()) {
			Object o = it.next();
			if (!list.contains(o))
				list.add(o);
		}
    }
    
    /**
     * Find the restriction(s) on this property in the enclosing class.  Will also
     * look for a cardinality restriction in the subclasses if one is not found here
     * @throws JastorException
     */
    private void computePrimaryRestrictions() throws JastorException {
        Query query = new Query("SELECT ?res WHERE (?class,rdfs:subClassOf,?res)" + "(?res,owl:onProperty,?prop)" + 
                               " USING rdfs FOR <http://www.w3.org/2000/01/rdf-schema#>, owl FOR <http://www.w3.org/2002/07/owl#>");
        query.setSource(ontProperty.getModel());
        QueryEngine qe = new QueryEngine(query);
        ResultBindingImpl initialBinding = new ResultBindingImpl();
        initialBinding.add("class",ontClass);
        initialBinding.add("prop",ontProperty);
        QueryResults results = null;
        Restriction cardrestriction = null;
        try {
             try {
                results = qe.exec(initialBinding);
                while (results.hasNext()) {
                    ResultBinding binding = (ResultBinding) results.next();
                    Resource restrictionRes = (Resource) binding.get("res");
                    Restriction restriction = (Restriction)restrictionRes.as(Restriction.class);
                    if (restriction.isCardinalityRestriction())
                        cardrestriction = restriction;
                    restrictions.add(restriction);
                } 
            } finally {
                if (results != null)
                    results.close();
            }
        } catch (Exception e) {
            throw new JastorException(e,"Error querying for restriction");
        }
        if (cardrestriction == null) {
            cardrestriction = ontologyClass.findCardinalityRestrictionInSubClassHierarchy(this);
        }
        
        if (cardrestriction != null) {
            restrictions.add(cardrestriction);
        }
        
    }
    
    private String addPrefix(String name) {
    	String prefix = ctx.getNamespacePrefix(ontProperty.getNameSpace());
    	return prefix + "_" + name;
    }
    
    /**
     * Determine if this property has multiple ranges defined in the base declaration of the property.
     * Note, this is different from multiple ranges via-all values from.
     */
    private void checkForMultipleBaseRanges() {
    	Resource range = ontProperty.getRange();
    	if (range != null && range.getURI() == null) {
    		if (range.canAs(UnionClass.class)) {
    			ranges = new ArrayList();
    			UnionClass uc = (UnionClass)range.as(UnionClass.class);
    			Iterator it = uc.listOperands();
    			while (it.hasNext()) {
    				ranges.add(it.next());
    			}
    		}
    	}
    }
    
}