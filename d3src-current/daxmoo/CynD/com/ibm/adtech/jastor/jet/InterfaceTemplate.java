package com.ibm.adtech.jastor.jet;

import java.util.*;
import com.ibm.adtech.jastor.*;
import com.ibm.adtech.jastor.inference.*;
import com.hp.hpl.jena.rdf.model.*;;

/*******************************************************************************
 * Copyright (c) 2005 IBM Corporation 
 * All rights reserved.   This program and the accompanying materials
 * are made available under the terms of the Common Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.opensource.org/licenses/cpl.php
 * 
 ******************************************************************************/

public class InterfaceTemplate implements OntologyClassTemplate {
  protected static String nl;
  public static synchronized InterfaceTemplate create(String lineSeparator)
  {
    nl = lineSeparator;
    InterfaceTemplate result = new InterfaceTemplate();
    nl = null;
    return result;
  }

  protected final String NL = nl == null ? (System.getProperties().getProperty("line.separator")) : nl;
  protected final String TEXT_1 = "";
  protected final String TEXT_2 = NL + NL + "package ";
  protected final String TEXT_3 = ";" + NL + "" + NL + "import com.hp.hpl.jena.rdf.model.*;" + NL + "" + NL + "/**" + NL + " * Interface for ";
  protected final String TEXT_4 = " ontology class<br>" + NL + " * Use the ";
  protected final String TEXT_5 = " to create instances of this interface." + NL + " * <p>(URI: ";
  protected final String TEXT_6 = ")</p>" + NL + " * <br>";
  protected final String TEXT_7 = NL + " * RDF Schema Standard Properties <br>";
  protected final String TEXT_8 = NL + " * \t";
  protected final String TEXT_9 = " <br>";
  protected final String TEXT_10 = NL + " * <br>";
  protected final String TEXT_11 = NL + " * Dublin Core Standard Properties <br>";
  protected final String TEXT_12 = NL + " * \t";
  protected final String TEXT_13 = " <br>";
  protected final String TEXT_14 = NL + " * <br>";
  protected final String TEXT_15 = NL + " *\t@version ";
  protected final String TEXT_16 = NL + " */" + NL + "public interface ";
  protected final String TEXT_17 = " extends ";
  protected final String TEXT_18 = ", ";
  protected final String TEXT_19 = " {" + NL + "\t" + NL + "\t/**" + NL + "\t * The rdf:type for this ontology class" + NL + "     */" + NL + "\tpublic static final Resource TYPE = ResourceFactory.createResource(\"";
  protected final String TEXT_20 = "\");" + NL + "\t";
  protected final String TEXT_21 = NL + NL + "\t/**" + NL + "\t * The Jena Property for ";
  protected final String TEXT_22 = " " + NL + "\t * <p>(URI: ";
  protected final String TEXT_23 = ")</p>" + NL + "\t * <br>";
  protected final String TEXT_24 = NL + "\t * <br>" + NL + "\t * RDF Schema Standard Properties <br>";
  protected final String TEXT_25 = NL + "\t * \t";
  protected final String TEXT_26 = " <br>";
  protected final String TEXT_27 = NL + "\t * <br>";
  protected final String TEXT_28 = NL + "\t * Dublin Core Standard Properties <br>";
  protected final String TEXT_29 = NL + "\t * \t";
  protected final String TEXT_30 = " <br>";
  protected final String TEXT_31 = NL + "\t * <br>";
  protected final String TEXT_32 = "  ";
  protected final String TEXT_33 = NL + "\t * @version ";
  protected final String TEXT_34 = NL + "\t */" + NL + "\tpublic static com.hp.hpl.jena.rdf.model.Property ";
  protected final String TEXT_35 = "Property = ResourceFactory.createProperty(\"";
  protected final String TEXT_36 = "\");" + NL;
  protected final String TEXT_37 = NL;
  protected final String TEXT_38 = NL + NL + "\t/**" + NL + "\t * Individual for URI: ";
  protected final String TEXT_39 = NL + "\t */" + NL + "\tpublic static com.hp.hpl.jena.rdf.model.Resource ";
  protected final String TEXT_40 = " = ResourceFactory.createResource(\"";
  protected final String TEXT_41 = "\");" + NL;
  protected final String TEXT_42 = NL;
  protected final String TEXT_43 = NL;
  protected final String TEXT_44 = NL + "\t/**" + NL + "\t * Gets the '";
  protected final String TEXT_45 = "' property value" + NL + "\t * @return\t\t{@link ";
  protected final String TEXT_46 = "}" + NL + "\t * @see\t\t\t#";
  protected final String TEXT_47 = "Property" + NL + "\t */" + NL + "\tpublic ";
  protected final String TEXT_48 = " get";
  protected final String TEXT_49 = "() throws com.ibm.adtech.jastor.JastorException;" + NL + "" + NL + "\t/**" + NL + "\t * Sets the '";
  protected final String TEXT_50 = "' property value" + NL + "\t * @param\t\t{@link ";
  protected final String TEXT_51 = "}" + NL + "\t * @see\t\t\t#";
  protected final String TEXT_52 = "Property" + NL + "\t */" + NL + "\tpublic void set";
  protected final String TEXT_53 = "(";
  protected final String TEXT_54 = " ";
  protected final String TEXT_55 = ") throws com.ibm.adtech.jastor.JastorException;" + NL;
  protected final String TEXT_56 = NL + "\t/**" + NL + "\t * Iterates through the '";
  protected final String TEXT_57 = "' property values.  This Iteartor" + NL + "\t * may be used to remove all such values." + NL + "\t * @return\t\t{@link java.util.Iterator} of {@link ";
  protected final String TEXT_58 = "}" + NL + "\t * @see\t\t\t#";
  protected final String TEXT_59 = "Property" + NL + "\t */" + NL + "\tpublic java.util.Iterator get";
  protected final String TEXT_60 = "() throws com.ibm.adtech.jastor.JastorException;" + NL + "" + NL + "\t/**" + NL + "\t * Add a '";
  protected final String TEXT_61 = "' property value" + NL + "\t * @param\t\t{@link ";
  protected final String TEXT_62 = "}, the value to add" + NL + "\t * @see\t\t\t#";
  protected final String TEXT_63 = "Property" + NL + "\t */" + NL + "\tpublic void add";
  protected final String TEXT_64 = "(";
  protected final String TEXT_65 = " ";
  protected final String TEXT_66 = ") throws com.ibm.adtech.jastor.JastorException;" + NL + "\t" + NL + "\t/**" + NL + "\t * Remove a '";
  protected final String TEXT_67 = "' property value. This method should not" + NL + "\t * be invoked while iterator through values.  In that case, the remove() method of the Iterator" + NL + "\t * itself should be used." + NL + "\t * @param\t\t{@link ";
  protected final String TEXT_68 = "}, the value to remove" + NL + "\t * @see\t\t\t#";
  protected final String TEXT_69 = "Property" + NL + "\t */" + NL + "\tpublic void remove";
  protected final String TEXT_70 = "(";
  protected final String TEXT_71 = " ";
  protected final String TEXT_72 = ") throws com.ibm.adtech.jastor.JastorException;" + NL;
  protected final String TEXT_73 = NL + "\t/**" + NL + "\t * Gets the '";
  protected final String TEXT_74 = "' property value" + NL + "\t * @return\t\t{@link ";
  protected final String TEXT_75 = "}" + NL + "\t * @see\t\t\t#";
  protected final String TEXT_76 = "Property" + NL + "\t */" + NL + "\tpublic ";
  protected final String TEXT_77 = " get";
  protected final String TEXT_78 = "() throws com.ibm.adtech.jastor.JastorException;" + NL + "\t" + NL + "\t/**" + NL + "\t * Sets the '";
  protected final String TEXT_79 = "' property value" + NL + "\t * @param\t\t{@link ";
  protected final String TEXT_80 = "}" + NL + "\t * @see\t\t\t#";
  protected final String TEXT_81 = "Property" + NL + "\t */" + NL + "\tpublic void set";
  protected final String TEXT_82 = "(";
  protected final String TEXT_83 = " ";
  protected final String TEXT_84 = ") throws com.ibm.adtech.jastor.JastorException;" + NL + "\t" + NL + "\t/**" + NL + "\t * Sets the '";
  protected final String TEXT_85 = "' property value to an anonymous node" + NL + "\t * @return\t\t{@link ";
  protected final String TEXT_86 = "}, the created value" + NL + "\t * @see\t\t\t#";
  protected final String TEXT_87 = "Property" + NL + "\t */\t" + NL + "\tpublic ";
  protected final String TEXT_88 = " set";
  protected final String TEXT_89 = "() throws com.ibm.adtech.jastor.JastorException;" + NL + "\t" + NL + "\t/**" + NL + "\t * Sets the '";
  protected final String TEXT_90 = "' property value to the given resource" + NL + "\t * The resource argument should have rdf:type ";
  protected final String TEXT_91 = ".  That is, this method" + NL + "\t * should not be used as a shortcut for creating new objects in the model." + NL + "\t * @param\t\t{@link com.hp.hpl.jena.rdf.model.Resource} must not be be null." + NL + "\t * @return\t\t{@link ";
  protected final String TEXT_92 = "}, the newly created value" + NL + "\t * @see\t\t\t#";
  protected final String TEXT_93 = "Property" + NL + "\t */" + NL + "\tpublic ";
  protected final String TEXT_94 = " set";
  protected final String TEXT_95 = "(com.hp.hpl.jena.rdf.model.Resource resource) throws com.ibm.adtech.jastor.JastorException;" + NL + "\t";
  protected final String TEXT_96 = NL + "\t/**" + NL + "\t * Get an Iterator the '";
  protected final String TEXT_97 = "' property values.  This Iteartor" + NL + "\t * may be used to remove all such values." + NL + "\t * @return\t\t{@link java.util.Iterator} of {@link ";
  protected final String TEXT_98 = "}" + NL + "\t * @see\t\t\t#";
  protected final String TEXT_99 = "Property" + NL + "\t */" + NL + "\tpublic java.util.Iterator get";
  protected final String TEXT_100 = "() throws com.ibm.adtech.jastor.JastorException;" + NL + "" + NL + "\t/**" + NL + "\t * Adds a value for the '";
  protected final String TEXT_101 = "' property" + NL + "\t * @param\t\tThe {@link ";
  protected final String TEXT_102 = "} to add" + NL + "\t * @see\t\t\t#";
  protected final String TEXT_103 = "Property" + NL + "\t */" + NL + "\tpublic void add";
  protected final String TEXT_104 = "(";
  protected final String TEXT_105 = " ";
  protected final String TEXT_106 = ") throws com.ibm.adtech.jastor.JastorException;" + NL + "\t" + NL + "\t/**" + NL + "\t * Adds an anonymous value for the '";
  protected final String TEXT_107 = "' property" + NL + "\t * @return\t\tThe anoymous {@link ";
  protected final String TEXT_108 = "} created" + NL + "\t * @see\t\t\t#";
  protected final String TEXT_109 = "Property" + NL + "\t */" + NL + "\tpublic ";
  protected final String TEXT_110 = " add";
  protected final String TEXT_111 = "() throws com.ibm.adtech.jastor.JastorException;" + NL + "\t" + NL + "\t/**" + NL + "\t * " + NL + "\t * The resource argument have rdf:type ";
  protected final String TEXT_112 = ".  That is, this method" + NL + "\t * should not be used as a shortcut for creating new objects in the model." + NL + "\t * @param\t\tThe {@link om.hp.hpl.jena.rdf.model.Resource} to add" + NL + "\t * @see\t\t\t#";
  protected final String TEXT_113 = "Property" + NL + "\t */" + NL + "\tpublic ";
  protected final String TEXT_114 = " add";
  protected final String TEXT_115 = "(com.hp.hpl.jena.rdf.model.Resource resource) throws com.ibm.adtech.jastor.JastorException;" + NL + "\t" + NL + "\t/**" + NL + "\t * Removes a value for the '";
  protected final String TEXT_116 = "' property.  This method should not" + NL + "\t * be invoked while iterator through values.  In that case, the remove() method of the Iterator" + NL + "\t * itself should be used." + NL + "\t * @param\t\tThe {@link ";
  protected final String TEXT_117 = "} to remove" + NL + "\t * @see\t\t\t#";
  protected final String TEXT_118 = "Property" + NL + "\t */" + NL + "\tpublic void remove";
  protected final String TEXT_119 = "(";
  protected final String TEXT_120 = " ";
  protected final String TEXT_121 = ") throws com.ibm.adtech.jastor.JastorException;" + NL + "\t\t";
  protected final String TEXT_122 = NL + "}";

	public static final String copyright = "(C) Copyright IBM Corporation 2005  All Rights Reserved.";
	
	OntologyClassFileProvider fileProvider;

	public InterfaceTemplate(OntologyClassFileProvider fileProvider) {
		this.fileProvider = fileProvider;
	}
	
	public InterfaceTemplate() {
	}
	
	public OntologyClassFileProvider getFileProvider() {
		return fileProvider;
	}	
	
	public void setFileProvider(OntologyClassFileProvider fileProvider) {
		this.fileProvider = fileProvider;
	}

	public String generate(OntologyClass oc)
  {
    final StringBuffer stringBuffer = new StringBuffer();
     //(C) Copyright IBM Corporation 2005  All Rights Reserved. 
    stringBuffer.append(TEXT_1);
     // author: Ben Szekely (<a href="mailto:bhszekel@us.ibm.com">bhszekel@us.ibm.com</a>) 
     //OntologyClass oc = (OntologyClass)argument; 
     JastorContext ctx = oc.getContext(); 
    stringBuffer.append(TEXT_2);
    stringBuffer.append(oc.getPackageName());
    stringBuffer.append(TEXT_3);
    stringBuffer.append(oc.getComment().getLabel());
    stringBuffer.append(TEXT_4);
    stringBuffer.append(oc.getFactoryFullClassname());
    stringBuffer.append(TEXT_5);
    stringBuffer.append(oc.getURI() );
    stringBuffer.append(TEXT_6);
     if (oc.getComment().listRDFSPropertyNames().length > 0) { 
    stringBuffer.append(TEXT_7);
     	String[] names = oc.getComment().listRDFSPropertyNames(); 
     	for (int i=0;i<names.length;i++) { 
    stringBuffer.append(TEXT_8);
    stringBuffer.append(names[i] + " : " + oc.getComment().getRDFSProperty(names[i]));
    stringBuffer.append(TEXT_9);
     	} 
     } 
    stringBuffer.append(TEXT_10);
     if (oc.getComment().listDCPropertyNames().length > 0) { 
    stringBuffer.append(TEXT_11);
     	String[] names = oc.getComment().listDCPropertyNames(); 
     	for (int i=0;i<names.length;i++) { 
    stringBuffer.append(TEXT_12);
    stringBuffer.append(names[i] + " : " + oc.getComment().getDCProperty(names[i]));
    stringBuffer.append(TEXT_13);
     	} 
     } 
    stringBuffer.append(TEXT_14);
     if (oc.getComment().getVersionInfo() != null) { 
    stringBuffer.append(TEXT_15);
    stringBuffer.append(oc.getComment().getVersionInfo());
     } 
    stringBuffer.append(TEXT_16);
    stringBuffer.append(oc.getInterfaceClassname());
    stringBuffer.append(TEXT_17);
     for (Iterator iter = oc.listImmediateExtensionClasses().iterator();iter.hasNext();) { 
     OntologyClass extClass = (OntologyClass)iter.next();
    stringBuffer.append(extClass.getInterfaceFullClassname());
    stringBuffer.append(TEXT_18);
     } 
    stringBuffer.append(ctx.getThingInterface().getName());
    stringBuffer.append(TEXT_19);
    stringBuffer.append(oc.getURI());
    stringBuffer.append(TEXT_20);
     for (Iterator iter = oc.listProperties(false).iterator(); iter.hasNext();) {
     OntologyProperty prop = (OntologyProperty)iter.next(); 
    stringBuffer.append(TEXT_21);
    stringBuffer.append(prop.getPropertyName());
    stringBuffer.append(TEXT_22);
    stringBuffer.append(prop.getURI());
    stringBuffer.append(TEXT_23);
     	if (prop.getComment().listRDFSPropertyNames().length > 0) { 
    stringBuffer.append(TEXT_24);
     		String[] names = prop.getComment().listRDFSPropertyNames(); 
     		for (int i=0;i<names.length;i++) { 
    stringBuffer.append(TEXT_25);
    stringBuffer.append(names[i] + " : " + prop.getComment().getRDFSProperty(names[i]));
    stringBuffer.append(TEXT_26);
     		} 
    stringBuffer.append(TEXT_27);
    		} 
     	if (prop.getComment().listDCPropertyNames().length > 0) { 
    stringBuffer.append(TEXT_28);
     		String[] names = prop.getComment().listDCPropertyNames(); 
     		for (int i=0;i<names.length;i++) { 
    stringBuffer.append(TEXT_29);
    stringBuffer.append(names[i] + " : " + prop.getComment().getDCProperty(names[i]));
    stringBuffer.append(TEXT_30);
     		} 
    stringBuffer.append(TEXT_31);
     	} 
    stringBuffer.append(TEXT_32);
    		if (prop.getComment().getVersionInfo() != null) { 
    stringBuffer.append(TEXT_33);
    stringBuffer.append(prop.getComment().getVersionInfo());
    		} 
    stringBuffer.append(TEXT_34);
    stringBuffer.append(prop.getPropertyName());
    stringBuffer.append(TEXT_35);
    stringBuffer.append(prop.getURI());
    stringBuffer.append(TEXT_36);
     } 
    stringBuffer.append(TEXT_37);
     for (Iterator iter = oc.listIndividuals().iterator(); iter.hasNext();) {
         Resource individual = (Resource)iter.next(); 
    stringBuffer.append(TEXT_38);
    stringBuffer.append(individual.getURI());
    stringBuffer.append(TEXT_39);
    stringBuffer.append(oc.getIndividualIdentifierName(individual));
    stringBuffer.append(TEXT_40);
    stringBuffer.append(individual.getURI());
    stringBuffer.append(TEXT_41);
     } 
    stringBuffer.append(TEXT_42);
     if (!ctx.isGenerateVocabularyOnly()) { 
    stringBuffer.append(TEXT_43);
     	for (Iterator iter = oc.listProperties(true).iterator(); iter.hasNext();) {
     		OntologyProperty prop = (OntologyProperty)iter.next(); 
    			if (prop.isSingleValued() && prop.isDatatypeProperty()) { 
    		   	boolean all = (prop.getRole() == OntologyProperty.ROLE_HERE); 
    				for (Iterator iter2 = prop.listRanges(all,true);iter2.hasNext();) { 
              	Resource res = (Resource)iter2.next();
    stringBuffer.append(TEXT_44);
    stringBuffer.append(prop.getPropertyName());
    stringBuffer.append(TEXT_45);
    stringBuffer.append(prop.getReturnType(res));
    stringBuffer.append(TEXT_46);
    stringBuffer.append(prop.getPropertyName());
    stringBuffer.append(TEXT_47);
    stringBuffer.append(prop.getReturnType(res));
    stringBuffer.append(TEXT_48);
    stringBuffer.append(prop.getPropertyCapped(res));
    stringBuffer.append(TEXT_49);
    stringBuffer.append(prop.getPropertyName());
    stringBuffer.append(TEXT_50);
    stringBuffer.append(prop.getReturnType(res));
    stringBuffer.append(TEXT_51);
    stringBuffer.append(prop.getPropertyName());
    stringBuffer.append(TEXT_52);
    stringBuffer.append(prop.getPropertyCapped());
    stringBuffer.append(TEXT_53);
    stringBuffer.append(prop.getReturnType(res));
    stringBuffer.append(TEXT_54);
    stringBuffer.append(prop.getPropertyName());
    stringBuffer.append(TEXT_55);
              } 
           } 
    			if (prop.isMultiValued() && prop.isDatatypeProperty()) { 
    		   	boolean all = (prop.getRole() == OntologyProperty.ROLE_HERE); 
    				for (Iterator iter2 = prop.listRanges(all,true);iter2.hasNext();) { 
              	Resource res = (Resource)iter2.next();
    stringBuffer.append(TEXT_56);
    stringBuffer.append(prop.getPropertyName());
    stringBuffer.append(TEXT_57);
    stringBuffer.append(prop.getReturnType(res));
    stringBuffer.append(TEXT_58);
    stringBuffer.append(prop.getPropertyName());
    stringBuffer.append(TEXT_59);
    stringBuffer.append(prop.getPropertyCapped(res));
    stringBuffer.append(TEXT_60);
    stringBuffer.append(prop.getPropertyName());
    stringBuffer.append(TEXT_61);
    stringBuffer.append(prop.getReturnType(res));
    stringBuffer.append(TEXT_62);
    stringBuffer.append(prop.getPropertyName());
    stringBuffer.append(TEXT_63);
    stringBuffer.append(prop.getPropertyCapped());
    stringBuffer.append(TEXT_64);
    stringBuffer.append(prop.getReturnType(res));
    stringBuffer.append(TEXT_65);
    stringBuffer.append(prop.getPropertyName());
    stringBuffer.append(TEXT_66);
    stringBuffer.append(prop.getPropertyName());
    stringBuffer.append(TEXT_67);
    stringBuffer.append(prop.getReturnType(res));
    stringBuffer.append(TEXT_68);
    stringBuffer.append(prop.getPropertyName());
    stringBuffer.append(TEXT_69);
    stringBuffer.append(prop.getPropertyCapped());
    stringBuffer.append(TEXT_70);
    stringBuffer.append(prop.getReturnType(res));
    stringBuffer.append(TEXT_71);
    stringBuffer.append(prop.getPropertyName());
    stringBuffer.append(TEXT_72);
              } 
           } 
    			if (prop.isSingleValued() && prop.isObjectProperty()) { 
    		   	boolean all = (prop.getRole() == OntologyProperty.ROLE_HERE); 
    				for (Iterator iter2 = prop.listRanges(all,true);iter2.hasNext();) { 
              	Resource res = (Resource)iter2.next();
    stringBuffer.append(TEXT_73);
    stringBuffer.append(prop.getPropertyName());
    stringBuffer.append(TEXT_74);
    stringBuffer.append(prop.getReturnType(res));
    stringBuffer.append(TEXT_75);
    stringBuffer.append(prop.getPropertyName());
    stringBuffer.append(TEXT_76);
    stringBuffer.append(prop.getReturnType(res));
    stringBuffer.append(TEXT_77);
    stringBuffer.append(prop.getPropertyCapped(res));
    stringBuffer.append(TEXT_78);
    stringBuffer.append(prop.getPropertyName());
    stringBuffer.append(TEXT_79);
    stringBuffer.append(prop.getReturnType(res));
    stringBuffer.append(TEXT_80);
    stringBuffer.append(prop.getPropertyName());
    stringBuffer.append(TEXT_81);
    stringBuffer.append(prop.getPropertyCapped());
    stringBuffer.append(TEXT_82);
    stringBuffer.append(prop.getReturnType(res));
    stringBuffer.append(TEXT_83);
    stringBuffer.append(prop.getPropertyName());
    stringBuffer.append(TEXT_84);
    stringBuffer.append(prop.getPropertyName());
    stringBuffer.append(TEXT_85);
    stringBuffer.append(prop.getReturnType(res));
    stringBuffer.append(TEXT_86);
    stringBuffer.append(prop.getPropertyName());
    stringBuffer.append(TEXT_87);
    stringBuffer.append(prop.getReturnType(res));
    stringBuffer.append(TEXT_88);
    stringBuffer.append(prop.getPropertyCapped(res));
    stringBuffer.append(TEXT_89);
    stringBuffer.append(prop.getPropertyName());
    stringBuffer.append(TEXT_90);
    stringBuffer.append(prop.getRangeOntologyClass(res).getURI());
    stringBuffer.append(TEXT_91);
    stringBuffer.append(prop.getReturnType(res));
    stringBuffer.append(TEXT_92);
    stringBuffer.append(prop.getPropertyName());
    stringBuffer.append(TEXT_93);
    stringBuffer.append(prop.getReturnType(res));
    stringBuffer.append(TEXT_94);
    stringBuffer.append(prop.getPropertyCapped(res));
    stringBuffer.append(TEXT_95);
              } 
           } 
    			if (prop.isMultiValued() && prop.isObjectProperty()) { 
    		   	boolean all = (prop.getRole() == OntologyProperty.ROLE_HERE); 
    				for (Iterator iter2 = prop.listRanges(all,true);iter2.hasNext();) { 
              	Resource res = (Resource)iter2.next();
    stringBuffer.append(TEXT_96);
    stringBuffer.append(prop.getPropertyName());
    stringBuffer.append(TEXT_97);
    stringBuffer.append(prop.getReturnType(res));
    stringBuffer.append(TEXT_98);
    stringBuffer.append(prop.getPropertyName());
    stringBuffer.append(TEXT_99);
    stringBuffer.append(prop.getPropertyCapped(res));
    stringBuffer.append(TEXT_100);
    stringBuffer.append(prop.getPropertyName());
    stringBuffer.append(TEXT_101);
    stringBuffer.append(prop.getReturnType(res));
    stringBuffer.append(TEXT_102);
    stringBuffer.append(prop.getPropertyName());
    stringBuffer.append(TEXT_103);
    stringBuffer.append(prop.getPropertyCapped());
    stringBuffer.append(TEXT_104);
    stringBuffer.append(prop.getReturnType(res));
    stringBuffer.append(TEXT_105);
    stringBuffer.append(prop.getPropertyName());
    stringBuffer.append(TEXT_106);
    stringBuffer.append(prop.getPropertyName());
    stringBuffer.append(TEXT_107);
    stringBuffer.append(prop.getReturnType(res));
    stringBuffer.append(TEXT_108);
    stringBuffer.append(prop.getPropertyName());
    stringBuffer.append(TEXT_109);
    stringBuffer.append(prop.getReturnType(res));
    stringBuffer.append(TEXT_110);
    stringBuffer.append(prop.getPropertyCapped(res));
    stringBuffer.append(TEXT_111);
    stringBuffer.append(prop.getRangeOntologyClass(res).getURI());
    stringBuffer.append(TEXT_112);
    stringBuffer.append(prop.getPropertyName());
    stringBuffer.append(TEXT_113);
    stringBuffer.append(prop.getReturnType(res));
    stringBuffer.append(TEXT_114);
    stringBuffer.append(prop.getPropertyCapped(res));
    stringBuffer.append(TEXT_115);
    stringBuffer.append(prop.getPropertyName());
    stringBuffer.append(TEXT_116);
    stringBuffer.append(prop.getReturnType(res));
    stringBuffer.append(TEXT_117);
    stringBuffer.append(prop.getPropertyName());
    stringBuffer.append(TEXT_118);
    stringBuffer.append(prop.getPropertyCapped());
    stringBuffer.append(TEXT_119);
    stringBuffer.append(prop.getReturnType(res));
    stringBuffer.append(TEXT_120);
    stringBuffer.append(prop.getPropertyName());
    stringBuffer.append(TEXT_121);
              } 
           } 
     	} 
     } 
    stringBuffer.append(TEXT_122);
    return stringBuffer.toString();
  }
}