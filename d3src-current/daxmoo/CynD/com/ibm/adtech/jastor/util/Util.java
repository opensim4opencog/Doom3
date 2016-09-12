/*******************************************************************************
 * Copyright (c) 2005 IBM Corporation 
 * All rights reserved.   This program and the accompanying materials
 * are made available under the terms of the Common Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.opensource.org/licenses/cpl.php
 * 
 ******************************************************************************/

package com.ibm.adtech.jastor.util;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.Iterator;
import java.util.List;

import com.hp.hpl.jena.datatypes.xsd.XSDDateTime;
import com.hp.hpl.jena.datatypes.xsd.impl.XMLLiteralType;
import com.hp.hpl.jena.rdf.model.Literal;
import com.hp.hpl.jena.rdf.model.Resource;
import com.hp.hpl.jena.vocabulary.RDFS;
import com.ibm.adtech.jastor.JastorContext;

public class Util {

	public static String printProperties(Resource res) {
		StringBuffer buf = new StringBuffer();
		Iterator it = res.listProperties();
		while (it.hasNext()) {
			buf.append(it.next() + "\n");
		}
		return buf.toString();
	}
	
	public static List iteratorToList(Iterator it) {
		ArrayList list = new ArrayList();
		while (it.hasNext()) {
			list.add(it.next());
		}
		return list;
	}
	
	public static Object fixLiteral(boolean checkDataType, Literal literal, String returnType, String returnDataTypeURI) {
		if (checkDataType && !returnDataTypeURI.equals(RDFS.Literal.getURI())) { // if we are using a typedLiteral system, we don't want to return values for
							 // literals that aren't of the property type
			// we might have to expand this to check for different types that are compatible..i.e. integer/long
			if ((literal.getDatatypeURI() == null && !returnType.equals(Literal.class)) ||
				!literal.getDatatypeURI().equals(returnDataTypeURI))
				return null;
		}
		if (literal.getDatatypeURI().equals(XMLLiteralType.theXMLLiteralType.getURI()))
			return literal.getLexicalForm();
		
		Object literalValue = literal.getValue();
		// will have to expand this function as cases arise.
		if (!literalValue.getClass().getName().equals(returnType)) {
			if (returnType.equals("java.lang.Long") && literalValue.getClass().getName().equals("java.lang.Integer")) {
				return new java.lang.Long(((Integer)literalValue).longValue());
			}
			if (returnType.equals("java.lang.Integer") && literalValue.getClass().getName().equals("java.lang.String")) {
				return new java.lang.Integer((String)literalValue);
			}
			if (returnType.equals("java.lang.Long") && literalValue.getClass().getName().equals("java.lang.String")) {
				return new java.lang.Long((String)literalValue);
			}
			if (returnType.equals("java.lang.String")) {
				return literalValue.toString();
			}
			if (returnType.equals("com.hp.hpl.jena.datatypes.xsd.XSDDateTime")) {
				Date date = null;
				try {
					date = new SimpleDateFormat("yyyy-MM-dd'T'hh:mm:ss'Z'").parse(literalValue.toString());
				} catch (ParseException e) {
					try {
						date = new SimpleDateFormat("yyyy-MM-dd'T'hh:mm:ss.SSS'Z'").parse(literalValue.toString());
					} catch (ParseException e2) {
						e.printStackTrace();
						return literal;
					}
				}
				GregorianCalendar cal = new GregorianCalendar();
				cal.setTime(date);
				return new XSDDateTime(cal);
			}
			return literalValue;
		} else {
			return literalValue;
		}
	}

}
