/*******************************************************************************
 * Copyright (c) 2005 IBM Corporation 
 * All rights reserved.   This program and the accompanying materials
 * are made available under the terms of the Common Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.opensource.org/licenses/cpl.php
 * 
 ******************************************************************************/

package com.ibm.adtech.jastor;

import java.io.File;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import com.hp.hpl.jena.ontology.EnumeratedClass;
import com.hp.hpl.jena.ontology.OntClass;
import com.hp.hpl.jena.ontology.OntDocumentManager;
import com.hp.hpl.jena.ontology.OntModel;
import com.hp.hpl.jena.ontology.OntModelSpec;
import com.hp.hpl.jena.ontology.OntProperty;
import com.hp.hpl.jena.ontology.UnionClass;
import com.hp.hpl.jena.rdf.model.ModelFactory;
import com.hp.hpl.jena.rdf.model.Resource;
import com.hp.hpl.jena.util.iterator.ExtendedIterator;
import com.hp.hpl.jena.vocabulary.RDF;
import com.hp.hpl.jena.vocabulary.RDFS;
import com.ibm.adtech.jastor.inference.Ontology;
import com.ibm.adtech.jastor.inference.OntologyClass;
import com.ibm.adtech.jastor.inference.OntologyProperty;
import com.ibm.adtech.jastor.jet.FactoryTemplate;
import com.ibm.adtech.jastor.jet.ImplementationTemplate;
import com.ibm.adtech.jastor.jet.InterfaceTemplate;
import com.ibm.adtech.jastor.jet.ListenerTemplate;
import com.ibm.adtech.jastor.jet.OntologyClassFileProvider;
import com.ibm.adtech.jastor.jet.OntologyClassTemplate;
import com.ibm.adtech.jastor.jet.OntologyFileProvider;
import com.ibm.adtech.jastor.jet.OntologyTemplate;
import com.ibm.adtech.jastor.rdfs.Rdfs2Owl;

/**
 * 
 * This class holds context information for code generation: ontModels, options,
 * names, etc...
 * 
 * @author Ben Szekely ( <a
 *         href="mailto:bhszekel@us.ibm.com">bhszekel@us.ibm.com </a>)
 *  
 */
public class JastorContext {
	
    public static final String copyright = "(C) Copyright IBM Corporation 2005  All Rights Reserved.";
    
    public static final String ONT_LANG_OWL = "owl";
    public static final String ONT_LANG_RDFS = "rdfs";
    public static final List ONT_LANGS = new ArrayList();
    static {
    	ONT_LANGS.add(ONT_LANG_OWL);
    	ONT_LANGS.add(ONT_LANG_RDFS);
    }
    
    
    public static final String GEN_NS = "http://jastor.adtech.ibm.com/gen#";
    
    private OntModel ontModel;  
    
    private HashMap ontologyClassMap = new HashMap(); // map from ontclass URI to the containing ontology String->String
    private HashMap ontologyPropertyMap = new HashMap(); // map from ontpropery uri to the containing onotology
    private HashMap ontologyClassPackageMap = new HashMap(); // from from ontclass URI to package String->String
    private HashMap ontologyPackageMap = new HashMap(); // from the ontology URI to package String->String
    private HashMap packageOntologyMap = new HashMap(); // from the package to ontology URI String->String
    
    private HashMap unionTable = new HashMap(); // from classuri to a List(OntologyClass) that are classes that classuri should extend
                                                // due to unions
    private HashMap unionDomainTable = new HashMap(); // from classuri to a List(OntProperty) that are properties
                                                      // whose domain comes from a union.  We must compute this table
    												  // initially because some such properties won't have restrictions
     										          // so they would be ignored.
    
    private List classesToGenerate; // List of String
    private List ontologiesToGenerate = new ArrayList(); // List of Ontology
    
    private int curPrefixNumber = 1;
    private HashMap namespacePrefixes = new HashMap(); // String -> String
    
    // generation flags
    
    private boolean generateStandardCode = true;
    private boolean generateListeners = true;
    private boolean generatePropertyCache = true;   
    private boolean generateVocabularyOnly = false;
    private boolean useEntireURIForIdentifiers = false;
    private boolean generateCacheInFactory = true;
    private boolean usePackageNameForRestrictedRanges = false;
    private boolean useStrictTypeChecking = false;
    private boolean useTypedLiterals = true;
    private boolean addAllRDFTypesInHierarchy = true;
    
    // Thing implementation
    private Class thingInterface = Thing.class;
    private Class thingImpl = ThingImpl.class;
    private Class thingFactory = ThingFactory.class;
    
    // generation templates
    private Map ontologyTemplates = new HashMap();
    private Map ontologyClassTemplates = new HashMap();
    
    /**
     * This constructor should be used when the ontModel is fully assembled. 
     * A future version might allow packages to be specified per class
     * @param ontModel
     * @param classesToGenerate
     * @param ontologyURI
     * @param packageName
     */
    public JastorContext(OntModel ontModel, List classesToGenerate, String ontologyURI, String packageName) {
        this.ontModel = ontModel;
        this.classesToGenerate = classesToGenerate;
        registerOntology(ontModel,ontologyURI,packageName,false);
        //packageOntologyClassMap.put(packageName,classesToGenerate);
    }
    
    /**
     * Contruct an empty context with initially nothing to generate
     *
     */
    public JastorContext() {       
        ontModel = createOntModel(false, null);
        classesToGenerate = new ArrayList();
    }
    
    /**
     * Should be called to finalize the context after all ontologies have been added but before generation
     */
    public void finalize() {
        buildUnionTable();
        buildUnionDomainTable();
        if (isGenerateStandardCode())
            setupDefaultTemplates();
        fillInDomainRangeFromSuperProperties();
        addRDFTypeToEnumerationClassMembers();
    }
    
    /**
     * Set a custom thing implementation.
     * @param thingInterface - the classname of an extension of Thing
     * @param thingImpl - the classname of an extension of ThingImpl
     * @param thingFactory the classname of an extension of ThingFactory
     * @throws ClassNotFoundException
     */
    public void setCustomThing(String thingInterface, String thingImpl, String thingFactory) throws JastorException {
        try {
	        this.thingInterface = Class.forName(thingInterface);
	        this.thingImpl = Class.forName(thingImpl);
	        this.thingFactory = Class.forName(thingFactory);
	        /*if (!Thing.class.isAssignableFrom(this.thingInterface))
	            throw new JastorException(thingInterface + " does not extend " + Thing.class.getName());
	        if (!ThingImpl.class.isAssignableFrom(this.thingImpl))
	            throw new JastorException(thingImpl + " does not extend " + ThingImpl.class.getName());
	        if (!ThingFactory.class.isAssignableFrom(this.thingFactory))
	            throw new JastorException(thingFactory + " does not extend " + ThingFactory.class.getName());*/
        } catch (ClassNotFoundException e) {
            throw new JastorException(e,"Error loading custom thing class");
        }
    }
    
    /**
     * Return the Class of the base Thing interface
     * @return
     */
    public Class getThingInterface() {
        return thingInterface;
    }
    
    /**
     * Return the Class of the base Thing implementation
     * @return
     */
    public Class getThingImpl() {
        return thingImpl;
    }
    
    /**
     * Return the Class of the base Thing factory
     * @return
     */
    public Class getThingFactory() {
        return thingFactory;
    }
    
    /**
     * Specify an OWL ontology to generate
     * @param ontologyFile The InputStream containing the ontology document
     * @param ontologyURI The URI of the ontology
     * @param packagename The Java package that generate classes should be in.
     */
    public void addOntologyToGenerate(InputStream ontologyFile, String ontologyURI, String packagename) {
        addOntologyToGenerate(ontologyFile,null,ontologyURI,packagename);
    }
    
    /**
     * Specify an OWL ontology to generate
     * @param ontologyFile The InputStream containing the ontology document
     * @param rdflang The seriazation format of the ontology file (N3,RDF/XM,...all Jena formats)
     * @param ontologyURI The URI of the ontology
     * @param packagename The Java package that generate classes should be in.
     */
    public void addOntologyToGenerate(InputStream ontologyFile, String rdflang, String ontologyURI, String packagename) {
        addOntologyToGenerate(ontologyFile,ONT_LANG_OWL,rdflang,ontologyURI,packagename);
    }
    
    /**
     * Specify an ontology to generate
     * @param ontologyFile The InputStream containing the ontology document
     * @param ontlang The Ontology Language, owl or rdfs.  
     * @param rdflang The seriazation format of the ontology file (N3,RDF/XM,...all Jena formats)
     * @param ontologyURI The URI of the ontology
     * @param packagename The Java package that generate classes should be in.
     */
    public void addOntologyToGenerate(InputStream ontologyFile, String ontLang, String rdflang, String ontologyURI, String packagename) {
        OntModel temp = createOntModel(false,ontLang);
        if (rdflang != null)
            temp.read(ontologyFile,"",rdflang);
        else
            temp.read(ontologyFile,"");
        if (ontLang != null && ontLang.equals(ONT_LANG_RDFS)) {
        	temp = Rdfs2Owl.convertToOwl(temp,ontologyURI);
        }
        addConcreteEnumClasses(temp);
        ontModel.add(temp);
        registerOntology(temp,ontologyURI,packagename,true);
    }
    
    /**
     * Specify an ontology to generate
     * @param ontModel A Jena OntModel containing the ontology
     * @param ontologyURI The URI of the ontology
     * @param packagename The Java package that generate classes should be in.
     */
    public void addOntologyToGenerate(OntModel ontModel, String ontologyURI, String packagename) {
        this.ontModel.add(ontModel);
        registerOntology(ontModel,ontologyURI,packagename,true);
    }
    
    /**
     * Specify an ontology to generate
     * @param ontModel A Jena OntModel containing the ontology
     * @param ontlang The Ontology Language, owl or rdfs.  
     * @param ontologyURI The URI of the ontology
     * @param packagename The Java package that generate classes should be in.
     */
    public void addOntologyToGenerate(OntModel ontModel, String ontlang, String ontologyURI, String packagename) {
    	if (ontlang != null && ontlang.equals(ONT_LANG_RDFS))
    		ontModel = Rdfs2Owl.convertToOwl(ontModel,ontologyURI);
        this.ontModel.add(ontModel);
        registerOntology(ontModel,ontologyURI,packagename,true);
    }
    
    /**
     * Specify an ontology needed by one of the generation ontologies.  One of these
     * entreis should be added for every import in every ontology to generate.  
     * We have this call because imports in Jena don't work all that well.
     * @param ontologyFile The InputStream containing the ontology document
     * @param ontologyURI The URI of the ontology
     * @param packagename The Java package that generate classes should be in.
     */
    public void addOntologyDependency(InputStream ontologyFile, String ontologyURI, String packagename) {
        addOntologyDependency(ontologyFile,null,ontologyURI,packagename);
    }
    
    /**
     * Specify an ontology needed by one of the generation ontologies.  One of these
     * entreis should be added for every import in every ontology to generate.  
     * We have this call because imports in Jena don't work all that well.
     * @param ontologyFile The InputStream containing the ontology document
     * @param rdflang The seriazation format of the ontology file (N3,RDF/XM,...all Jena formats)
     * @param ontologyURI The URI of the ontology
     * @param packagename The Java package that generate classes should be in.
     */
    public void addOntologyDependency(InputStream ontologyFile, String rdflang, String ontologyURI, String packagename) {
    	addOntologyDependency(ontologyFile,ONT_LANG_OWL,rdflang,ontologyURI,packagename);
    }
    
    /**
     * Specify an ontology needed by one of the generation ontologies.  One of these
     * entreis should be added for every import in every ontology to generate.  
     * We have this call because imports in Jena don't work all that well.
     * @param ontologyFile The InputStream containing the ontology document
     * @param ontlang The Ontology Language, owl or rdfs.  
     * @param rdflang The seriazation format of the ontology file (N3,RDF/XM,...all Jena formats)
     * @param ontologyURI The URI of the ontology
     * @param packagename The Java package that generate classes should be in.
     */
    public void addOntologyDependency(InputStream ontologyFile, String ontlang, String rdflang, String ontologyURI, String packagename) {
        OntModel temp = createOntModel(false,ontlang);
        if (rdflang != null)
            temp.read(ontologyFile,ontologyURI,rdflang);
        else
            temp.read(ontologyFile,ontologyURI);
        if (ontlang != null && ontlang.equals(ONT_LANG_RDFS))
        	temp = Rdfs2Owl.convertToOwl(temp,ontologyURI);
        ontModel.add(temp);      
        registerOntology(temp,ontologyURI,packagename,false);
    }
    
    /**
     * Return a List of OntologyClass objects representing all the classes to be generated.
     * @return
     */
    public List listOntologyClassesToGenerate() {
        Iterator it = classesToGenerate.iterator();
        List classes = new ArrayList();
        while (it.hasNext()) {
            String uri = (String)it.next();           
            OntClass oc = ontModel.getOntClass(uri);
            classes.add(new OntologyClass(oc,this));
        }
        return classes;
    }
    
    /**
     * Return a list of Ontology objects representing all the ontologies to be generated
     * @return
     */
    public List listOntologiesToGenerate() {
        return ontologiesToGenerate;
    }
    
    /**
     * Determine if we generate listeners for the beans
     * @return
     */
    public boolean isGenerateListeners() {
        return generateListeners;
    }
    
    /**
     * Indicate whether or not to generate listeners, default true
     * @param generateListeners
     */
    public void setGenerateListeners(boolean generateListeners) {
        this.generateListeners = generateListeners;
    }
    
    public boolean isGeneratePropertyCache() {
        return generatePropertyCache;
    }
    
    /**
     * default true;
     * @param generatePropertyCache
     */
    public void setGeneratePropertyCache(boolean generatePropertyCache) {
        this.generatePropertyCache = generatePropertyCache;
    }
    
    public boolean isGenerateVocabularyOnly() {
        return generateVocabularyOnly;
    }
    
    /**
     * default false;
     * @param generateVocabularyOnly
     */
    public void setGenerateVocabularyOnly(boolean generateVocabularyOnly) {
        this.generateVocabularyOnly = generateVocabularyOnly;
    }    
    
    public boolean isUseEntireURIForIdentifiers() {
        return useEntireURIForIdentifiers;
    }
    
    /**
     * defualt true;
     * @return
     */
    public boolean isGenerateCacheInFactory() {
        return generateCacheInFactory;
    }
    
    public void setGenerateCacheInFactory(boolean generateCacheInFactory) {
        this.generateCacheInFactory = generateCacheInFactory;
    }
    
    /**
     * default false
     * @param useEntireURIForIdentifiers
     */
    public void setUseEntireURIForIdentifiers(boolean useEntireURIForIdentifiers) {
        this.useEntireURIForIdentifiers = useEntireURIForIdentifiers;
    }    
    
    /**
     * default false
     * @return
     */
    public boolean isUsePackageNameForRestrictedRanges() {
        return usePackageNameForRestrictedRanges;
    }
    
    public void setUsePackageNameForRestrictedRanges(boolean usePackageNameForRestrictedRanges) {
        this.usePackageNameForRestrictedRanges = usePackageNameForRestrictedRanges;
    }
    
    public boolean isUseStrictTypeChecking() {
        return useStrictTypeChecking;
    }
    
    public void setUseStrictTypeChecking(boolean useStrictTypeChecking) {
        this.useStrictTypeChecking = useStrictTypeChecking;
    }
        
    public boolean isGenerateStandardCode() {
        return generateStandardCode;
    }
    
    public void setGenerateStandardCode(boolean generateStandardCode) {
        this.generateStandardCode = generateStandardCode;
    }
    
    public boolean isUseTypedLiterals() {
		return useTypedLiterals;
	}

	public void setUseTypedLiterals(boolean useTypedLiterals) {
		this.useTypedLiterals = useTypedLiterals;
	}

	public boolean isAddAllRDFTypesInHierarchy() {
		return addAllRDFTypesInHierarchy;
	}

	public void setAddAllRDFTypesInHierarchy(boolean addAllRDFTypesInHierarchy) {
		this.addAllRDFTypesInHierarchy = addAllRDFTypesInHierarchy;
	}

	public OntModel getOntModel() {
        return ontModel;
    }
    
    public String getOntologyForClass(String ontClassURI) {
        return (String)ontologyClassMap.get(ontClassURI);
    }
    
    public String getPackageForClass(String ontClassURI) {
        return (String)ontologyClassPackageMap.get(ontClassURI);
    }
    
    public String getPackageForOntology(String ontologyURI) {
        return (String)ontologyPackageMap.get(ontologyURI);
    }
    
    public String getOntologyForPackage(String pkg) {
        return (String)packageOntologyMap.get(pkg);
    }
    
    /**
     * Return a list of OntologyClass that the given class should extend because the returned
     * classes are unions of classuri. Returns an empty list if no extensions exist.  We pre-compute
     * this table because the corresponding per-class query is messy and innefficient.
     * @param uri
     * @return
     */
    public List getUnionClassExtensions(String classuri) {
        List list = (List)unionTable.get(classuri);
        if (list == null)
            return new ArrayList();
        return list;
    }
    
    /**
     * Return a list or OntProperty that are properies declared with a domain that is a union of classes.
     * Jena does not provide this for us.
     * @param classuri
     * @return
     */
    public List getUnionDomainProperties(String classuri) {
        List list = (List)unionDomainTable.get(classuri);
        if (list == null)
            return new ArrayList();
        return list;
    }
    
    /**
     * Add a generation template to be run for each ontology class
     * @param ontgen
     */
    public void addOntologyClassTemplate(String name, OntologyClassTemplate ontgen) {
        ontologyClassTemplates.put(name,ontgen);
    }
    
    /**
     * Add a generation template to be run for each ontology
     * @param ontgen
     */
    public void addOntologyTemplate(String name, OntologyTemplate ontgen) {
        ontologyTemplates.put(name,ontgen);
    }    
    
    public Map getOntologyClassTemplates() {
        return ontologyClassTemplates;
    }
    
    public Map getOntologyTemplates() {
        return ontologyTemplates;
    }
    
    public void setNamespacePrefix(String ns, String prefix) {
    	namespacePrefixes.put(ns,prefix);
    }
    
    public String getNamespacePrefix(String ns) {
    	String prefix = (String)namespacePrefixes.get(ns);
    	if (prefix == null) {
    		prefix = "ns" + curPrefixNumber++;
    		namespacePrefixes.put(ns,prefix);
    	}
    	return prefix;
    }
    
    public boolean isPropetyAndClassDefinedInSameOntology(String ontPropertyUri, String ontClassUri) {
    	String propOnt = (String)ontologyPropertyMap.get(ontPropertyUri);
    	String classOnt = (String)ontologyClassMap.get(ontClassUri);
    	return propOnt.equals(classOnt);
    }
    
    private void registerOntology(OntModel tempont, String ontologyURI, String packagename, boolean generate) {
    	Iterator it = tempont.listClasses();
        ontologyPackageMap.put(ontologyURI,packagename);
        packageOntologyMap.put(packagename,ontologyURI);
        List classuris = new ArrayList();
        while (it.hasNext()) {
            OntClass oc = (OntClass)it.next();
            if (!oc.isAnon()) {
                ontologyClassPackageMap.put(oc.getURI(),packagename);
            	ontologyClassMap.put(oc.getURI(),ontologyURI);
            	if (generate) {
            	    classuris.add(oc.getURI());
            	    classesToGenerate.add(oc.getURI());
            	}
            }
        }
        
        it = tempont.listObjectProperties();
        while (it.hasNext()) {
        	OntProperty prop = (OntProperty)it.next();
        	if (ontologyPropertyMap.containsKey(prop.getURI())) {
        		System.err.println("Warning: " + prop.getURI() + " defined in multiple ontologies");
        	} else {
        		ontologyPropertyMap.put(prop.getURI(),ontologyURI);
        	}
        }
        
        it = tempont.listDatatypeProperties();
        while (it.hasNext()) {
        	OntProperty prop = (OntProperty)it.next();
        	if (ontologyPropertyMap.containsKey(prop.getURI())) {
        		System.err.println("Warning: " + prop.getURI() + " defined in multiple ontologies");
        	} else {
        		ontologyPropertyMap.put(prop.getURI(),ontologyURI);
        	}
        }
        
        if (generate) {
            ontologiesToGenerate.add(new Ontology(packagename,this,classuris));
        }
    }
    
    private OntModel createOntModel(boolean processImports, String ontLang) {
        OntModelSpec s = null;
        if (ontLang != null && ontLang.equals(ONT_LANG_RDFS))
        	s = new OntModelSpec(OntModelSpec.RDFS_MEM);
        else
        	s = new OntModelSpec(OntModelSpec.OWL_MEM);
        OntModel ontModel = ModelFactory.createOntologyModel(s, null);
        OntDocumentManager dm = ontModel.getDocumentManager();
        dm.setProcessImports(processImports);
        return ontModel;        
    }
    
    private void buildUnionTable() {
        List list = listOntologyClassesToGenerate();
        for (int i=0;i<list.size();i++) {
            OntologyClass oc = (OntologyClass)list.get(i);
            OntClass ontClass = oc.getOntClass();
            if (!ontClass.isAnon() && ontClass.isUnionClass()) {
                UnionClass uc = (UnionClass)ontClass.as(UnionClass.class);
                Iterator it = uc.listOperands();
                while (it.hasNext()) {
                    OntClass op = (OntClass)it.next();
                    List l = (List)unionTable.get(op.getURI());
                    if (l == null) {
                        l = new ArrayList();
                        unionTable.put(op.getURI(),l);
                    }
                    l.add(oc);    
                }
            }
        }
    }
    
    private void buildUnionDomainTable() {
        buildUnionDomainTable(ontModel.listDatatypeProperties());
        buildUnionDomainTable(ontModel.listObjectProperties());
    }
    
    private void buildUnionDomainTable(Iterator it) {
        while (it.hasNext()) {
            OntProperty prop = (OntProperty)it.next();
            if (prop.getDomain() != null && prop.getDomain().isAnon() && prop.getDomain().canAs(UnionClass.class)) {
                UnionClass unionClass = (UnionClass)prop.getDomain().as(UnionClass.class);
                ExtendedIterator itr = unionClass.listOperands();
                while (itr.hasNext()) {
                    OntClass oc = (OntClass)itr.next();
                    if (!oc.isAnon()) {
                        List list = (List)unionDomainTable.get(oc.getURI());
                        if (list == null) {
                            list = new ArrayList();
                            unionDomainTable.put(oc.getURI(),list);
                        }
                        list.add(prop);         
                    }
                }
            }
        }
    }
    
    private void fillInDomainRangeFromSuperProperties() {
    	ExtendedIterator it = ontModel.listDatatypeProperties();    	
    	while (it.hasNext()) {
    		OntProperty prop = (OntProperty)it.next();
    		fillIn(prop,new ArrayList());
    	}
    	it = ontModel.listObjectProperties();    	
    	while (it.hasNext()) {
    		OntProperty prop = (OntProperty)it.next();
    		fillIn(prop,new ArrayList());
    	}
    }
    
    private Resource[] fillIn(OntProperty prop, List visited) {
    	if (visited.contains(prop))
    		return new Resource[2];
    	visited.add(prop);
    	// while the proper behavior of this method is undefined,
    	// use the following guidelines:
    	// - if a property defines it own domain/range, use it.
    	// - take a the domain or range from a super-property.
    	// - super-prop domain/ranges yield to a complete
        //   complete d/r range pairs from super property
    	if (prop.getDomain() == null || prop.getRange() == null) {
    		final int DOMAIN = 0;
    		final int RANGE = 1;
    		boolean domainSet = prop.getDomain() != null;
    		boolean rangeSet = prop.getRange() != null;
    		Iterator it = prop.listSuperProperties();
    		Resource[] vals = new Resource[]{prop.getDomain(),prop.getRange()};
    		while (it.hasNext()) {
    			OntProperty ontProp = (OntProperty)it.next();
    			Resource[] parVals = fillIn(ontProp,visited);
    			if (!domainSet && parVals[DOMAIN] != null)
    				vals[DOMAIN] = parVals[DOMAIN];
    			if (!rangeSet && parVals[RANGE] != null)
    				vals[RANGE] = parVals[RANGE];
    			if ((domainSet && vals[RANGE] != null) || 
    				(rangeSet && vals[DOMAIN] != null) ||
    				(vals[DOMAIN] != null && vals[RANGE] != null)) {
    				break;
    			}
    		}
    		if (!domainSet && vals[DOMAIN] != null)
    			prop.setDomain(vals[DOMAIN]);
    		if (!rangeSet && vals[RANGE] != null)
    			prop.setRange(vals[RANGE]);
    		return vals;
    	} else {
    		return new Resource[]{prop.getDomain(),prop.getRange()};
    	}
    }
    
    private void setupDefaultTemplates() {
        addOntologyClassTemplate("Interface",new InterfaceTemplate(new OntologyClassFileProvider() {
            public File getFile(OntologyClass oc, File outputDir) {
                return oc.getInterfaceFile(outputDir);
            }
        }));
        if (!isGenerateVocabularyOnly()) {
            addOntologyClassTemplate("Implementation",new ImplementationTemplate(new OntologyClassFileProvider() {
                public File getFile(OntologyClass oc, File outputDir) {
                    return oc.getImplFile(outputDir);
                }
            }));
            
            addOntologyTemplate("Factory",new FactoryTemplate(new OntologyFileProvider() {
                public File getFile(Ontology ont, File outputDir) {
                    return ont.getFactoryFile(outputDir);
                }
            }));
            
            if (isGenerateListeners()) {
                addOntologyClassTemplate("Listener",new ListenerTemplate(new OntologyClassFileProvider() {
                    public File getFile(OntologyClass oc, File outputDir) {
                        return oc.getListenerFile(outputDir);
                    }
                }));
            }
        }
    }
    
    private void addRDFTypeToEnumerationClassMembers() {
    	Iterator itr = ontModel.listEnumeratedClasses();
    	while (itr.hasNext()) {
    		EnumeratedClass ec = (EnumeratedClass)itr.next();
    		Iterator it = ec.listOneOf();
    		while (it.hasNext()) {
    			Resource res = (Resource)it.next();
    			if (!res.hasProperty(RDF.type,ec))
    				res.addProperty(RDF.type,ec);
    		}
    	}
    }
    
    private void addConcreteEnumClasses(OntModel tempOnt) {
    	Iterator itr = tempOnt.listEnumeratedClasses();
    	List list = new LinkedList();
    	while (itr.hasNext()) {
    		list.add(itr.next());
    	}
    	itr = list.iterator();
    	while (itr.hasNext()) {
    		EnumeratedClass ec = (EnumeratedClass)itr.next();
    		if (ec.isAnon()) {
    			try {
	    			Resource prop = tempOnt.listStatements(null,RDFS.range,ec).nextStatement().getSubject();
		    		OntProperty ontProp = (OntProperty)prop.as(OntProperty.class);
	    			OntologyProperty op = new OntologyProperty(ontProp, this);
	    			String uri = GEN_NS + op.getPropertyCapped() + "Enum";
	    			Iterator it = ec.listOneOf();
	    			EnumeratedClass ec2 = tempOnt.createEnumeratedClass(uri,tempOnt.createList());
	    			ec2.addOneOf(it);
	    			ontProp.setRange(ec2);    
    			} catch (Exception e) {
    				System.err.println("Warning, bad anonymous Enum (oneOf) class found");
    			}
    		}
    	}
    }

}
