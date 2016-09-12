package com.ibm.adtech.jastor.ant;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.taskdefs.MatchingTask;

import com.ibm.adtech.jastor.JastorContext;
import com.ibm.adtech.jastor.JastorException;
import com.ibm.adtech.jastor.JastorGenerator;
import com.ibm.adtech.jastor.inference.Ontology;
import com.ibm.adtech.jastor.inference.OntologyClass;
import com.ibm.adtech.jastor.jet.OntologyClassFileProvider;
import com.ibm.adtech.jastor.jet.OntologyClassTemplate;
import com.ibm.adtech.jastor.jet.OntologyFileProvider;
import com.ibm.adtech.jastor.jet.OntologyTemplate;

/**
 * @author Ben Szekely ( <a
 *         href="mailto:bhszekel@us.ibm.com">bhszekel@us.ibm.com </a>)
 * @author Elias Torres ( <a href="mailto:eliast@us.ibm.com">eliast@us.ibm.com
 *         </a>)
 */
public class JastorTask extends MatchingTask {

    public static final String copyright = "(C) Copyright IBM Corporation 2005  All Rights Reserved.";

    private List ontologies = new ArrayList();

    private List customThing = new ArrayList();
    
    private List templates = new ArrayList();
    
    private List prefixes = new ArrayList();

    private File destdir;
    
    private boolean generateStandardCode = true;

    private boolean generateListeners = true;

    private boolean generatePropertyCache = true;

    private boolean generateVocublaryOnly = false;

    private boolean useEntireURIForIdentifiers = false;

    private boolean generateCacheInFactory = true;

    private boolean usePackageNamesForRestrictedRanges = false;
    
    private boolean useStrictTypeChecking = false;
    
    private boolean useTypedLiterals = true;
    
    private boolean addAllRDFTypesInHierarchy = true;
    
	
	/**
	 * Whether the Thing interface should extend Jena's Resource interface
	 * in the generated source.
	 * 
	 * WARNING: this could cause collisions in ontologies with property names
	 * that collide with get-methods in Resource.  e.g. getModel().
	 */
	private boolean thingExtendsResource = false;
	

    /*
     * (non-Javadoc)
     * 
     * @see org.apache.tools.ant.Task#execute()
     */
    public void execute() throws BuildException {

        JastorGenerator gen = null;
        JastorContext ctx = new JastorContext();
        ctx.setGenerateCacheInFactory(isGenerateCacheInFactory());
        ctx.setGenerateListeners(isGenerateListeners());
        ctx.setGeneratePropertyCache(isGeneratePropertyCache());
        ctx.setGenerateVocabularyOnly(isGenerateVocublaryOnly());
        ctx.setUseEntireURIForIdentifiers(isUseEntireURIForIdentifiers());
        ctx.setUsePackageNameForRestrictedRanges(isUsePackageNamesForRestrictedRanges());
        ctx.setUseStrictTypeChecking(isUseStrictTypeChecking());
        ctx.setGenerateStandardCode(isGenerateStandardCode());
        ctx.setUseTypedLiterals(isUseTypedLiterals());
        ctx.setAddAllRDFTypesInHierarchy(isAddAllRDFTypesInHierarchy());
		
        try {
			if (this.thingExtendsResource) {
				ctx.setCustomThing(
						"com.ibm.adtech.jastor.resource.ResourceThing",
						"com.ibm.adtech.jastor.resource.ResourceThingImpl",
						"com.ibm.adtech.jastor.resource.ResourceThingFactory"
						);
			} else {
				CustomThingElement cte = getCustomThing();
				if (cte != null)
					ctx.setCustomThing(cte.getThingInterface(), cte.getThingImpl(), cte.getThingFactory());
			}
        } catch (JastorException e) {
            throw new BuildException(e);
        }

        if (getDestdir() != null) {
            gen = new JastorGenerator(getDestdir(), ctx);
        } else {
            throw new BuildException("destDir is a required property.");
        }
        
        Iterator it = ontologies.iterator();
        while (it.hasNext()) {
            OntologyElement ont = (OntologyElement)it.next();
            ont.validate();
            try {
                if (ont.isGenerate()) {
                    ctx.addOntologyToGenerate(new FileInputStream(ont.getPath().toString()), ont.getOntlang(), ont.getLang(), ont.getUri(), ont.getJavaPackage());
                } else {
                    ctx.addOntologyDependency(new FileInputStream(ont.getPath().toString()), ont.getOntlang(), ont.getLang(), ont.getUri(), ont.getJavaPackage());
                }
            } catch (Exception e) {
            	e.printStackTrace();
                throw new BuildException(e);
            }
        }
        
        it = prefixes.iterator();
        while (it.hasNext()) {
        	PrefixElement elt = (PrefixElement)it.next();
        	ctx.setNamespacePrefix(elt.getNs(),elt.getPrefix());
        }
        
        it = templates.iterator();
        while (it.hasNext()) {
        	TemplateElement tmp = (TemplateElement)it.next();
        	try {
	        	String className = tmp.getClassName();
	        	Class c = Class.forName(className);
	        	final String name = tmp.getName();
	        	if (OntologyTemplate.class.isAssignableFrom(c)) {
	        		OntologyTemplate ot = (OntologyTemplate)c.newInstance();
	        		ot.setFileProvider(new OntologyFileProvider() {
	        		    public static final String copyright = "(C) Copyright IBM Corporation 2005  All Rights Reserved.";
	        			public File getFile(Ontology ont, File outputDir) {
	        				return new File(ont.getFactoryFile(outputDir).getParentFile(),ont.getLocalName() + name + ".java");
	        			}
	        		});
	        		ctx.addOntologyTemplate(name,ot);
	        	} else if (OntologyClassTemplate.class.isAssignableFrom(c)) {
	        		OntologyClassTemplate oct = (OntologyClassTemplate)c.newInstance();
	        		oct.setFileProvider(new OntologyClassFileProvider() {
	        		    public static final String copyright = "(C) Copyright IBM Corporation 2005  All Rights Reserved.";
	        			public File getFile(OntologyClass ont, File outputDir) {
	        				return new File(ont.getFactoryFile(outputDir).getParentFile(),ont.getLocalName() + name + ".java");
	        			}
	        		});
	        		ctx.addOntologyClassTemplate(name,oct);
	        	} else throw new BuildException("template: " + name + " does not implement suitable interface");
        	} catch (ClassNotFoundException e) {
        		throw new BuildException(e);
        	} catch (InstantiationException e) {
        		throw new BuildException(e);
        	} catch (IllegalAccessException e) {
        		throw new BuildException(e);
        	}
        }

        try {
            gen.run();
        } catch (Exception e) {
            e.printStackTrace();
            throw new BuildException(e);
        }

    }

    public File getDestdir() {
        return destdir;
    }

    public void setDestdir(File destdir) {
        this.destdir = destdir;
    }

    public boolean isGenerateCacheInFactory() {
        return generateCacheInFactory;
    }

    public void setGenerateCacheInFactory(boolean generateCacheInFactory) {
        this.generateCacheInFactory = generateCacheInFactory;
    }

    public boolean isGenerateListeners() {
        return generateListeners;
    }

    public void setGenerateListeners(boolean generateListeners) {
        this.generateListeners = generateListeners;
    }

    public boolean isGeneratePropertyCache() {
        return generatePropertyCache;
    }

    public void setGeneratePropertyCache(boolean generatePropertyCache) {
        this.generatePropertyCache = generatePropertyCache;
    }

    public boolean isGenerateVocublaryOnly() {
        return generateVocublaryOnly;
    }

    public void setGenerateVocublaryOnly(boolean generateVocublaryOnly) {
        this.generateVocublaryOnly = generateVocublaryOnly;
    }

    public List getOntologies() {
        return ontologies;
    }

    public void setOntologies(List ontologies) {
        this.ontologies = ontologies;
    }

    public CustomThingElement getCustomThing() throws JastorException {
        if (customThing.isEmpty())
            return null;
        if (customThing.size() > 1)
            throw new JastorException("Only one custom Thing may be defined");
        return (CustomThingElement) customThing.get(0);
    }

    public boolean isUseEntireURIForIdentifiers() {
        return useEntireURIForIdentifiers;
    }

    public void setUseEntireURIForIdentifiers(boolean useEntireURIForIdentifiers) {
        this.useEntireURIForIdentifiers = useEntireURIForIdentifiers;
    }

    public boolean isUsePackageNamesForRestrictedRanges() {
        return usePackageNamesForRestrictedRanges;
    }

    public void setUsePackageNamesForRestrictedRanges(boolean usePackageNamesForRestrictedRanges) {
        this.usePackageNamesForRestrictedRanges = usePackageNamesForRestrictedRanges;
    }

    public boolean isUseStrictTypeChecking() {
        return useStrictTypeChecking;
    }
	
    public void setUseStrictTypeChecking(boolean useStrictTypeChecking) {
        this.useStrictTypeChecking = useStrictTypeChecking;
    }
	
	public boolean isThingExtendsResource() {
		return this.thingExtendsResource;
	}
	
	public void setThingExtendsResource(boolean thingExtendsResource) {
		this.thingExtendsResource = thingExtendsResource;
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

	public void addOntology(OntologyElement ont) {
        ontologies.add(ont);
    }

    public void addCustomThing(CustomThingElement thing) {
        customThing.add(thing);
    }
    
    public void addTemplate(TemplateElement tmp) {
    	templates.add(tmp);
    }
    
    public void addPrefix(PrefixElement tmp) {
    	prefixes.add(tmp);
    }

}