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

public class ListenerTemplate implements OntologyClassTemplate {
  protected static String nl;
  public static synchronized ListenerTemplate create(String lineSeparator)
  {
    nl = lineSeparator;
    ListenerTemplate result = new ListenerTemplate();
    nl = null;
    return result;
  }

  protected final String NL = nl == null ? (System.getProperties().getProperty("line.separator")) : nl;
  protected final String TEXT_1 = "";
  protected final String TEXT_2 = NL + NL + "package ";
  protected final String TEXT_3 = ";" + NL + "" + NL + "/*" + NL + "import com.hp.hpl.jena.datatypes.xsd.*;" + NL + "import com.hp.hpl.jena.datatypes.xsd.impl.*;" + NL + "import com.hp.hpl.jena.rdf.model.*;" + NL + "import com.ibm.adtech.jastor.*;" + NL + "import java.util.*;" + NL + "import java.math.*;" + NL + "*/" + NL + "" + NL + "" + NL + "/**" + NL + " * Implementations of this listener may be registered with instances of ";
  protected final String TEXT_4 = " to " + NL + " * receive notification when properties changed, added or removed." + NL + " * <br>" + NL + " */" + NL + "public interface ";
  protected final String TEXT_5 = " extends com.ibm.adtech.jastor.ThingListener {" + NL;
  protected final String TEXT_6 = NL;
  protected final String TEXT_7 = NL + "\t/**" + NL + "\t * Called when ";
  protected final String TEXT_8 = " has changed" + NL + "\t * @param source the affected instance of ";
  protected final String TEXT_9 = NL + "\t */" + NL + "\tpublic void ";
  protected final String TEXT_10 = "Changed(";
  protected final String TEXT_11 = " source);" + NL;
  protected final String TEXT_12 = NL + "\t/**" + NL + "\t * Called when a value of ";
  protected final String TEXT_13 = " has been added" + NL + "\t * @param source the affected instance of ";
  protected final String TEXT_14 = NL + "\t * @param newValue the object representing the new value" + NL + "\t */\t" + NL + "\tpublic void ";
  protected final String TEXT_15 = "Added(";
  protected final String TEXT_16 = " source, ";
  protected final String TEXT_17 = " newValue);" + NL + "" + NL + "\t/**" + NL + "\t * Called when a value of ";
  protected final String TEXT_18 = " has been removed" + NL + "\t * @param source the affected instance of ";
  protected final String TEXT_19 = NL + "\t * @param oldValue the object representing the removed value" + NL + "\t */\t" + NL + "\tpublic void ";
  protected final String TEXT_20 = "Removed(";
  protected final String TEXT_21 = " source, ";
  protected final String TEXT_22 = " oldValue);" + NL;
  protected final String TEXT_23 = NL + "\t/**" + NL + "\t * Called when ";
  protected final String TEXT_24 = " has changed" + NL + "\t * @param source the affected instance of ";
  protected final String TEXT_25 = NL + "\t */" + NL + "\tpublic void ";
  protected final String TEXT_26 = "Changed(";
  protected final String TEXT_27 = " source);" + NL;
  protected final String TEXT_28 = NL + "\t/**" + NL + "\t * Called when a value of ";
  protected final String TEXT_29 = " has been added" + NL + "\t * @param source the affected instance of ";
  protected final String TEXT_30 = NL + "\t * @param newValue the object representing the new value" + NL + "\t */\t" + NL + "\tpublic void ";
  protected final String TEXT_31 = "Added(";
  protected final String TEXT_32 = " source, ";
  protected final String TEXT_33 = " newValue);" + NL + "" + NL + "\t/**" + NL + "\t * Called when a value of ";
  protected final String TEXT_34 = " has been removed" + NL + "\t * @param source the affected instance of ";
  protected final String TEXT_35 = NL + "\t * @param oldValue the object representing the removed value" + NL + "\t */" + NL + "\tpublic void ";
  protected final String TEXT_36 = "Removed(";
  protected final String TEXT_37 = " source, ";
  protected final String TEXT_38 = " oldValue);" + NL + "\t\t";
  protected final String TEXT_39 = NL + "}";

	public static final String copyright = "(C) Copyright IBM Corporation 2005  All Rights Reserved.";
	
	OntologyClassFileProvider fileProvider;

	public ListenerTemplate(OntologyClassFileProvider fileProvider) {
		this.fileProvider = fileProvider;
	}
	
	public ListenerTemplate() {
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
     JastorContext ctx = oc.getContext(); 
    stringBuffer.append(TEXT_2);
    stringBuffer.append(oc.getPackageName());
    stringBuffer.append(TEXT_3);
    stringBuffer.append(oc.getInterfaceFullClassname());
    stringBuffer.append(TEXT_4);
    stringBuffer.append(oc.getListenerClassname());
    stringBuffer.append(TEXT_5);
     if (!ctx.isGenerateVocabularyOnly()) { 
    stringBuffer.append(TEXT_6);
     	for (Iterator iter = oc.listProperties(true).iterator(); iter.hasNext();) {
     		OntologyProperty prop = (OntologyProperty)iter.next(); 
    			if (prop.isSingleValued() && prop.isDatatypeProperty()) { 
    				// if (!(prop.getRole() == OntologyProperty.ROLE_HERE || prop.getRole() == OntologyProperty.ROLE_BOOLEAN)) { 
    					// continue; 
    				// } 
    	// one method regardless of the number of return types 
    stringBuffer.append(TEXT_7);
    stringBuffer.append(prop.getPropertyName());
    stringBuffer.append(TEXT_8);
    stringBuffer.append(oc.getInterfaceFullClassname());
    stringBuffer.append(TEXT_9);
    stringBuffer.append(prop.getPropertyName());
    stringBuffer.append(TEXT_10);
    stringBuffer.append(oc.getInterfaceFullClassname());
    stringBuffer.append(TEXT_11);
           } 
    			if (prop.isMultiValued() && prop.isDatatypeProperty()) { 
    				// boolean all = prop.getRole() == OntologyProperty.ROLE_HERE || prop.getRole() == OntologyProperty.ROLE_BOOLEAN; 
    				for (Iterator iter2 = prop.listRanges(true,false);iter2.hasNext();) { 
              	Resource res = (Resource)iter2.next();
    stringBuffer.append(TEXT_12);
    stringBuffer.append(prop.getPropertyName());
    stringBuffer.append(TEXT_13);
    stringBuffer.append(oc.getInterfaceFullClassname());
    stringBuffer.append(TEXT_14);
    stringBuffer.append(prop.getPropertyName());
    stringBuffer.append(TEXT_15);
    stringBuffer.append(oc.getInterfaceFullClassname());
    stringBuffer.append(TEXT_16);
    stringBuffer.append(prop.getReturnType(res));
    stringBuffer.append(TEXT_17);
    stringBuffer.append(prop.getPropertyName());
    stringBuffer.append(TEXT_18);
    stringBuffer.append(oc.getInterfaceFullClassname());
    stringBuffer.append(TEXT_19);
    stringBuffer.append(prop.getPropertyName());
    stringBuffer.append(TEXT_20);
    stringBuffer.append(oc.getInterfaceFullClassname());
    stringBuffer.append(TEXT_21);
    stringBuffer.append(prop.getReturnType(res));
    stringBuffer.append(TEXT_22);
              } 
           } 
    			if (prop.isSingleValued() && prop.isObjectProperty()) { 
    				// if (!(prop.getRole() == OntologyProperty.ROLE_HERE || prop.getRole() == OntologyProperty.ROLE_BOOLEAN)) { 
    					// continue; 
    				// } 
    	// one method regardless of the number of return types 
    stringBuffer.append(TEXT_23);
    stringBuffer.append(prop.getPropertyName());
    stringBuffer.append(TEXT_24);
    stringBuffer.append(oc.getInterfaceFullClassname());
    stringBuffer.append(TEXT_25);
    stringBuffer.append(prop.getPropertyName());
    stringBuffer.append(TEXT_26);
    stringBuffer.append(oc.getInterfaceFullClassname());
    stringBuffer.append(TEXT_27);
           } 
    			if (prop.isMultiValued() && prop.isObjectProperty()) { 
    				//boolean all = prop.getRole() == OntologyProperty.ROLE_HERE || prop.getRole() == OntologyProperty.ROLE_BOOLEAN; 
    				for (Iterator iter2 = prop.listRanges(true,false);iter2.hasNext();) { 
              	Resource res = (Resource)iter2.next();
    stringBuffer.append(TEXT_28);
    stringBuffer.append(prop.getPropertyName());
    stringBuffer.append(TEXT_29);
    stringBuffer.append(oc.getInterfaceFullClassname());
    stringBuffer.append(TEXT_30);
    stringBuffer.append(prop.getPropertyName());
    stringBuffer.append(TEXT_31);
    stringBuffer.append(oc.getInterfaceFullClassname());
    stringBuffer.append(TEXT_32);
    stringBuffer.append(prop.getReturnType(res));
    stringBuffer.append(TEXT_33);
    stringBuffer.append(prop.getPropertyName());
    stringBuffer.append(TEXT_34);
    stringBuffer.append(oc.getInterfaceFullClassname());
    stringBuffer.append(TEXT_35);
    stringBuffer.append(prop.getPropertyName());
    stringBuffer.append(TEXT_36);
    stringBuffer.append(oc.getInterfaceFullClassname());
    stringBuffer.append(TEXT_37);
    stringBuffer.append(prop.getReturnType(res));
    stringBuffer.append(TEXT_38);
              } 
           } 
     	} 
     } 
    stringBuffer.append(TEXT_39);
    return stringBuffer.toString();
  }
}