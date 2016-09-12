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
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.Iterator;
import java.util.Map;

import com.ibm.adtech.jastor.inference.Ontology;
import com.ibm.adtech.jastor.inference.OntologyClass;
import com.ibm.adtech.jastor.jet.OntologyClassTemplate;
import com.ibm.adtech.jastor.jet.OntologyTemplate;

/**
 * 
 * @author Ben Szekely ( <a
 *         href="mailto:bhszekel@us.ibm.com">bhszekel@us.ibm.com </a>)
 *  
 */
public class JastorGenerator {
    public static final String copyright = "(C) Copyright IBM Corporation 2005  All Rights Reserved.";
    
    File outputDir;

    JastorContext ctx;

    public JastorGenerator(File outputDir, JastorContext ctx) {
        this.ctx = ctx;
        this.outputDir = outputDir;
    }

    public void run() throws JastorException {
        try {
            ctx.finalize();

            Map map = ctx.getOntologyTemplates();
            Iterator it = map.keySet().iterator();
            while (it.hasNext()) {
                String name = (String) it.next();
                OntologyTemplate ot = (OntologyTemplate) map.get(name);
                Iterator itr = ctx.listOntologiesToGenerate().iterator();
                while (itr.hasNext()) {
                    Ontology ont = (Ontology) itr.next();
                    System.err.println("Generating " + name + " : " + ont.getURI());
                    String genstr = ot.generate(ont);
                    File genfile = ot.getFileProvider().getFile(ont,outputDir);
                    genfile.getParentFile().mkdirs();
                    OutputStream out = null;
                    try {
                        out = new FileOutputStream(genfile);
                        out.write(genstr.getBytes());
                        out.flush();
                    } finally {
                        if (out != null)
                            out.close();
                    }
                }
            }
            
            map = ctx.getOntologyClassTemplates();
            it = map.keySet().iterator();
            while (it.hasNext()) {
                String name = (String) it.next();
                OntologyClassTemplate oct = (OntologyClassTemplate) map.get(name);
                Iterator itr = ctx.listOntologyClassesToGenerate().iterator();
                while (itr.hasNext()) {
                    OntologyClass ont = (OntologyClass) itr.next();
                    System.err.println("Generating " + name + " : " + ont.getURI());
                    String genstr = oct.generate(ont);
                    File genfile = oct.getFileProvider().getFile(ont,outputDir);
                    genfile.getParentFile().mkdirs();
                    OutputStream out = null;
                    try {
                        out = new FileOutputStream(genfile);
                        out.write(genstr.getBytes());
                        out.flush();
                    } finally {
                        if (out != null)
                            out.close();
                    }
                }
            }
            
            
        } catch (IOException e) {
            throw new JastorException(e, "IO Error generating code");
        }
    }

}