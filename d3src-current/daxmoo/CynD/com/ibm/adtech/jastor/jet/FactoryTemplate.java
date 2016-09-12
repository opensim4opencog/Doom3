package com.ibm.adtech.jastor.jet;

import java.util.*;
import com.ibm.adtech.jastor.*;
import com.ibm.adtech.jastor.inference.*;

/*******************************************************************************
 * Copyright (c) 2005 IBM Corporation 
 * All rights reserved.   This program and the accompanying materials
 * are made available under the terms of the Common Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.opensource.org/licenses/cpl.php
 * 
 ******************************************************************************/

public class FactoryTemplate implements OntologyTemplate {
  protected static String nl;
  public static synchronized FactoryTemplate create(String lineSeparator)
  {
    nl = lineSeparator;
    FactoryTemplate result = new FactoryTemplate();
    nl = null;
    return result;
  }

  protected final String NL = nl == null ? (System.getProperties().getProperty("line.separator")) : nl;
  protected final String TEXT_1 = "";
  protected final String TEXT_2 = NL + NL + "package ";
  protected final String TEXT_3 = ";" + NL + "" + NL + "import com.ibm.adtech.jastor.*;" + NL + "import com.hp.hpl.jena.rdf.model.Resource;" + NL + "import com.hp.hpl.jena.rdf.model.Model;" + NL + "import com.hp.hpl.jena.rdf.model.Statement;" + NL + "import com.hp.hpl.jena.rdf.model.StmtIterator;" + NL + "import com.hp.hpl.jena.vocabulary.RDF;" + NL + "" + NL + "/**" + NL + " * Factory for instantiating objects for ontology classes in the ";
  protected final String TEXT_4 = " ontology.  The" + NL + " * get methods leave the model unchanged and return a Java view of the object in the model.  The create methods" + NL + " * may add certain baseline properties to the model such as rdf:type and any properties with hasValue restrictions." + NL + " * <p>(URI: ";
  protected final String TEXT_5 = ")</p>" + NL + " * <br>";
  protected final String TEXT_6 = NL + " * RDF Schema Standard Properties <br>";
  protected final String TEXT_7 = NL + " * \t";
  protected final String TEXT_8 = " <br>";
  protected final String TEXT_9 = NL + " * <br>";
  protected final String TEXT_10 = NL + " * Dublin Core Standard Properties <br>";
  protected final String TEXT_11 = NL + " * \t";
  protected final String TEXT_12 = " <br>";
  protected final String TEXT_13 = NL + " * <br>";
  protected final String TEXT_14 = NL + " *\t@version ";
  protected final String TEXT_15 = NL + " */" + NL + "public class ";
  protected final String TEXT_16 = " extends ";
  protected final String TEXT_17 = " { " + NL + NL;
  protected final String TEXT_18 = NL + NL + "\t/**" + NL + "\t * Create a new instance of ";
  protected final String TEXT_19 = ".  Adds the rdf:type property for the given resource to the model." + NL + "\t * @param resource The resource of the ";
  protected final String TEXT_20 = NL + "\t * @param model the Jena Model." + NL + "\t */" + NL + "\tpublic static ";
  protected final String TEXT_21 = " create";
  protected final String TEXT_22 = "(Resource resource, Model model) throws JastorException {" + NL + "\t\treturn ";
  protected final String TEXT_23 = ".create";
  protected final String TEXT_24 = "(resource,model);" + NL + "\t}" + NL + "\t" + NL + "\t/**" + NL + "\t * Create a new instance of ";
  protected final String TEXT_25 = ".  Adds the rdf:type property for the given resource to the model." + NL + "\t * @param uri The uri of the ";
  protected final String TEXT_26 = NL + "\t * @param model the Jena Model." + NL + "\t */" + NL + "\tpublic static ";
  protected final String TEXT_27 = " create";
  protected final String TEXT_28 = "(String uri, Model model) throws JastorException {" + NL + "\t\t";
  protected final String TEXT_29 = " obj = ";
  protected final String TEXT_30 = ".create";
  protected final String TEXT_31 = "(model.createResource(uri), model);" + NL + "\t\treturn obj;" + NL + "\t}" + NL + "\t" + NL + "\t/**" + NL + "\t * Create a new instance of ";
  protected final String TEXT_32 = ".  Leaves the model unchanged." + NL + "\t * @param uri The uri of the ";
  protected final String TEXT_33 = NL + "\t * @param model the Jena Model." + NL + "\t */" + NL + "\tpublic static ";
  protected final String TEXT_34 = " get";
  protected final String TEXT_35 = "(String uri, Model model) throws JastorException {" + NL + "\t\treturn get";
  protected final String TEXT_36 = "(model.createResource(uri),model);" + NL + "\t}" + NL + "\t" + NL + "\t/**" + NL + "\t * Create a new instance of ";
  protected final String TEXT_37 = ".  Leaves the model unchanged." + NL + "\t * @param resource The resource of the ";
  protected final String TEXT_38 = NL + "\t * @param model the Jena Model." + NL + "\t */" + NL + "\tpublic static ";
  protected final String TEXT_39 = " get";
  protected final String TEXT_40 = "(Resource resource, Model model) throws JastorException {";
  protected final String TEXT_41 = NL + "\t\tString code = (model.hashCode()*17 + ";
  protected final String TEXT_42 = ".class.hashCode()) + resource.toString();" + NL + "\t\t";
  protected final String TEXT_43 = " obj = (";
  protected final String TEXT_44 = ")objects.get(code);" + NL + "\t\tif (obj == null) {" + NL + "\t\t\tobj = ";
  protected final String TEXT_45 = ".get";
  protected final String TEXT_46 = "(resource, model);" + NL + "\t\t\tif (obj == null)" + NL + "\t\t\t\treturn null;" + NL + "\t\t\tobjects.put(code, obj);" + NL + "\t\t}" + NL + "\t\treturn obj;";
  protected final String TEXT_47 = NL + "\t\t";
  protected final String TEXT_48 = " obj = ";
  protected final String TEXT_49 = ".get";
  protected final String TEXT_50 = "(resource, model);" + NL + "\t\treturn obj;";
  protected final String TEXT_51 = NL + "\t}" + NL + "\t" + NL + "\t/**" + NL + "\t * Return an instance of ";
  protected final String TEXT_52 = " for every resource in the model with rdf:Type ";
  protected final String TEXT_53 = NL + "\t * @param model the Jena Model" + NL + "\t * @return a List of ";
  protected final String TEXT_54 = NL + "\t */" + NL + "\tpublic static java.util.List getAll";
  protected final String TEXT_55 = "(Model model) throws JastorException {" + NL + "\t\tStmtIterator it = model.listStatements(null,RDF.type,";
  protected final String TEXT_56 = ".TYPE);" + NL + "\t\tjava.util.List list = new java.util.ArrayList();" + NL + "\t\twhile (it.hasNext()) {" + NL + "\t\t\tStatement stmt = it.nextStatement();" + NL + "\t\t\tlist.add(get";
  protected final String TEXT_57 = "(stmt.getSubject(),model));" + NL + "\t\t}" + NL + "\t\treturn list;" + NL + "\t}" + NL + "\t";
  protected final String TEXT_58 = NL + "\t" + NL + "\t/**" + NL + "\t * Returns an instance of an interface for the given Resource.  The return instance is guaranteed to " + NL + "\t * implement the most specific interface in *some* hierarchy in which the Resource participates.  The behavior" + NL + "\t * is unspecified for resources with RDF types from different hierarchies." + NL + "\t * @return an instance of Thing" + NL + "\t */" + NL + "\tpublic static Thing getThing(com.hp.hpl.jena.rdf.model.Resource res, com.hp.hpl.jena.rdf.model.Model model) throws JastorException {";
  protected final String TEXT_59 = NL + "\t\tif (res.hasProperty(RDF.type,model.getResource(\"";
  protected final String TEXT_60 = "\"))) {" + NL + "\t\t\treturn get";
  protected final String TEXT_61 = "(res,model);" + NL + "\t\t}";
  protected final String TEXT_62 = NL + "\t\treturn new ThingImpl(res,model);" + NL + "\t}" + NL + "\t" + NL + "\t/**" + NL + "\t * Returns an instance of an interface for the given Resource URI.  The return instance is guaranteed to " + NL + "\t * implement the most specific interface in *some* hierarchy in which the Resource participates.  The behavior" + NL + "\t * is unspecified for resources with RDF types from different hierarchies." + NL + "\t * @return an instance of Thing" + NL + "\t */" + NL + "\tpublic static Thing getThing(String uri, com.hp.hpl.jena.rdf.model.Model model) throws JastorException {" + NL + "\t\treturn getThing(model.getResource(uri),model);" + NL + "\t}" + NL + "" + NL + "\t/**" + NL + "\t * Return a list of compatible interfaces for the given type.  Searches through all ontology classes" + NL + "\t * in the ";
  protected final String TEXT_63 = " ontology.  The list is sorted according to the topological sort" + NL + "\t * of the class hierarchy" + NL + "\t * @return a List of type java.lang.Class" + NL + "\t */" + NL + "\tpublic static java.util.List listCompatibleInterfaces (com.hp.hpl.jena.rdf.model.Resource type) {" + NL + "\t\tjava.util.List types = new java.util.ArrayList();";
  protected final String TEXT_64 = NL + "\t\tif (type.equals(";
  protected final String TEXT_65 = ".TYPE)) {" + NL + "\t\t\ttypes.add(";
  protected final String TEXT_66 = ".class);" + NL + "\t\t}";
  protected final String TEXT_67 = NL + "\t\treturn types;" + NL + "\t}" + NL + "}";

	public static final String copyright = "(C) Copyright IBM Corporation 2005  All Rights Reserved.";

	OntologyFileProvider fileProvider;

	public FactoryTemplate(OntologyFileProvider fileProvider) {
		this.fileProvider = fileProvider;
	}
	
	public FactoryTemplate() {
	}
	
	public OntologyFileProvider getFileProvider() {
		return fileProvider;
	}
	
	public void setFileProvider(OntologyFileProvider fileProvider) {
		this.fileProvider = fileProvider;
	}

	public String generate(Ontology ont)
  {
    final StringBuffer stringBuffer = new StringBuffer();
     //(C) Copyright IBM Corporation 2005  All Rights Reserved. 
    stringBuffer.append(TEXT_1);
     // author: Ben Szekely (<a href="mailto:bhszekel@us.ibm.com">bhszekel@us.ibm.com</a>) 
     String pkgstr = ont.getPackage(); 
     JastorContext ctx = ont.getContext(); 
    stringBuffer.append(TEXT_2);
    stringBuffer.append(pkgstr);
    stringBuffer.append(TEXT_3);
    stringBuffer.append(ont.getLocalName());
    stringBuffer.append(TEXT_4);
    stringBuffer.append(ont.getURI() );
    stringBuffer.append(TEXT_5);
     if (ont.getComment().listRDFSPropertyNames().length > 0) { 
    stringBuffer.append(TEXT_6);
     	String[] names = ont.getComment().listRDFSPropertyNames(); 
     	for (int i=0;i<names.length;i++) { 
    stringBuffer.append(TEXT_7);
    stringBuffer.append(names[i] + " : " + ont.getComment().getRDFSProperty(names[i]));
    stringBuffer.append(TEXT_8);
     	} 
     } 
    stringBuffer.append(TEXT_9);
     if (ont.getComment().listDCPropertyNames().length > 0) { 
    stringBuffer.append(TEXT_10);
     	String[] names = ont.getComment().listDCPropertyNames(); 
     	for (int i=0;i<names.length;i++) { 
    stringBuffer.append(TEXT_11);
    stringBuffer.append(names[i] + " : " + ont.getComment().getDCProperty(names[i]));
    stringBuffer.append(TEXT_12);
     	} 
     } 
    stringBuffer.append(TEXT_13);
     if (ont.getComment().getVersionInfo() != null) { 
    stringBuffer.append(TEXT_14);
    stringBuffer.append(ont.getComment().getVersionInfo());
     } 
    stringBuffer.append(TEXT_15);
    stringBuffer.append(ont.getFactoryClassname());
    stringBuffer.append(TEXT_16);
    stringBuffer.append(ctx.getThingFactory().getName());
    stringBuffer.append(TEXT_17);
     for(Iterator iter = ont.getClasses().iterator(); iter.hasNext();) {
     OntologyClass oc = (OntologyClass)iter.next();
    stringBuffer.append(TEXT_18);
    stringBuffer.append(oc.getInterfaceClassname());
    stringBuffer.append(TEXT_19);
    stringBuffer.append(oc.getInterfaceClassname());
    stringBuffer.append(TEXT_20);
    stringBuffer.append(oc.getInterfaceClassname());
    stringBuffer.append(TEXT_21);
    stringBuffer.append(oc.getInterfaceClassname());
    stringBuffer.append(TEXT_22);
    stringBuffer.append(oc.getImplFullClassname());
    stringBuffer.append(TEXT_23);
    stringBuffer.append(oc.getInterfaceClassname());
    stringBuffer.append(TEXT_24);
    stringBuffer.append(oc.getInterfaceClassname());
    stringBuffer.append(TEXT_25);
    stringBuffer.append(oc.getInterfaceClassname());
    stringBuffer.append(TEXT_26);
    stringBuffer.append(oc.getInterfaceClassname());
    stringBuffer.append(TEXT_27);
    stringBuffer.append(oc.getInterfaceClassname());
    stringBuffer.append(TEXT_28);
    stringBuffer.append(oc.getInterfaceClassname());
    stringBuffer.append(TEXT_29);
    stringBuffer.append(oc.getImplFullClassname());
    stringBuffer.append(TEXT_30);
    stringBuffer.append(oc.getInterfaceClassname());
    stringBuffer.append(TEXT_31);
    stringBuffer.append(oc.getInterfaceClassname());
    stringBuffer.append(TEXT_32);
    stringBuffer.append(oc.getInterfaceClassname());
    stringBuffer.append(TEXT_33);
    stringBuffer.append(oc.getInterfaceClassname());
    stringBuffer.append(TEXT_34);
    stringBuffer.append(oc.getInterfaceClassname());
    stringBuffer.append(TEXT_35);
    stringBuffer.append(oc.getInterfaceClassname());
    stringBuffer.append(TEXT_36);
    stringBuffer.append(oc.getInterfaceClassname());
    stringBuffer.append(TEXT_37);
    stringBuffer.append(oc.getInterfaceClassname());
    stringBuffer.append(TEXT_38);
    stringBuffer.append(oc.getInterfaceClassname());
    stringBuffer.append(TEXT_39);
    stringBuffer.append(oc.getInterfaceClassname());
    stringBuffer.append(TEXT_40);
     	if (ctx.isGenerateCacheInFactory()) { 
    stringBuffer.append(TEXT_41);
    stringBuffer.append(oc.getInterfaceFullClassname());
    stringBuffer.append(TEXT_42);
    stringBuffer.append(oc.getImplFullClassname());
    stringBuffer.append(TEXT_43);
    stringBuffer.append(oc.getImplFullClassname());
    stringBuffer.append(TEXT_44);
    stringBuffer.append(oc.getImplFullClassname());
    stringBuffer.append(TEXT_45);
    stringBuffer.append(oc.getInterfaceClassname());
    stringBuffer.append(TEXT_46);
     	} else { 
    stringBuffer.append(TEXT_47);
    stringBuffer.append(oc.getInterfaceClassname());
    stringBuffer.append(TEXT_48);
    stringBuffer.append(oc.getImplFullClassname());
    stringBuffer.append(TEXT_49);
    stringBuffer.append(oc.getInterfaceClassname());
    stringBuffer.append(TEXT_50);
     	} 
    stringBuffer.append(TEXT_51);
    stringBuffer.append(oc.getInterfaceClassname());
    stringBuffer.append(TEXT_52);
    stringBuffer.append(oc.getURI());
    stringBuffer.append(TEXT_53);
    stringBuffer.append(oc.getInterfaceClassname());
    stringBuffer.append(TEXT_54);
    stringBuffer.append(oc.getInterfaceClassname());
    stringBuffer.append(TEXT_55);
    stringBuffer.append(oc.getInterfaceClassname());
    stringBuffer.append(TEXT_56);
    stringBuffer.append(oc.getInterfaceClassname());
    stringBuffer.append(TEXT_57);
     } 
    stringBuffer.append(TEXT_58);
     for(Iterator iter = ont.getClassesSorted().iterator(); iter.hasNext();) {
     OntologyClass oc = (OntologyClass)iter.next();
    stringBuffer.append(TEXT_59);
    stringBuffer.append(oc.getURI());
    stringBuffer.append(TEXT_60);
    stringBuffer.append(oc.getInterfaceClassname());
    stringBuffer.append(TEXT_61);
     } 
    stringBuffer.append(TEXT_62);
    stringBuffer.append(ont.getLocalName());
    stringBuffer.append(TEXT_63);
     for(Iterator iter = ont.getClassesSorted().iterator(); iter.hasNext();) {
     OntologyClass oc = (OntologyClass)iter.next();
    stringBuffer.append(TEXT_64);
    stringBuffer.append(oc.getInterfaceFullClassname());
    stringBuffer.append(TEXT_65);
    stringBuffer.append(oc.getInterfaceFullClassname());
    stringBuffer.append(TEXT_66);
     } 
    stringBuffer.append(TEXT_67);
    return stringBuffer.toString();
  }
}