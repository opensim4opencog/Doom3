/*******************************************************************************
 * Copyright (c) 2005 IBM Corporation 
 * All rights reserved.   This program and the accompanying materials
 * are made available under the terms of the Common Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.opensource.org/licenses/cpl.php
 * 
 ******************************************************************************/

package com.ibm.adtech.jastor;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;


import com.hp.hpl.jena.rdf.listeners.StatementListener;
import com.hp.hpl.jena.rdf.model.Statement;


public class JastorModelListener extends StatementListener {
	
	public static final String copyright = "(C) Copyright IBM Corporation 2005  All Rights Reserved.";
	
	private Map map = new HashMap();

	public void addThing(Thing thing) {
		List list = (List)map.get(thing.resource().toString());
		if (list == null) {
			list = new ArrayList();
			map.put(thing.resource().toString(),list);
		}
		list.add(thing);
	}
	
	public void addedStatement(Statement s) {
		List list = (List)map.get(s.getSubject().toString());
		if (list == null)
			return;
		Iterator it = list.iterator();
		while (it.hasNext()) {
			ThingImpl thing = (ThingImpl)it.next();
			thing.addedStatement(s);
		}
	}
	
	public void removedStatement(Statement s) {
		List list = (List)map.get(s.getSubject().toString());
		if (list == null)
			return;
		Iterator it = list.iterator();
		while (it.hasNext()) {
			ThingImpl thing = (ThingImpl)it.next();
			thing.removedStatement(s);
		}
	}

}
