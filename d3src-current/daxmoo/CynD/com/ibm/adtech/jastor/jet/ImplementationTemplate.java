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

public class ImplementationTemplate implements OntologyClassTemplate {
  protected static String nl;
  public static synchronized ImplementationTemplate create(String lineSeparator)
  {
    nl = lineSeparator;
    ImplementationTemplate result = new ImplementationTemplate();
    nl = null;
    return result;
  }

  protected final String NL = nl == null ? (System.getProperties().getProperty("line.separator")) : nl;
  protected final String TEXT_1 = "";
  protected final String TEXT_2 = NL + NL + "package ";
  protected final String TEXT_3 = ";" + NL + "" + NL + "/*" + NL + "import com.hp.hpl.jena.datatypes.xsd.*;" + NL + "import com.hp.hpl.jena.datatypes.xsd.impl.*;" + NL + "*/" + NL + "import com.hp.hpl.jena.rdf.model.*;" + NL + "import com.hp.hpl.jena.rdf.listeners.StatementListener;" + NL + "import com.hp.hpl.jena.vocabulary.RDF;" + NL + "import com.ibm.adtech.jastor.*;" + NL + "import com.ibm.adtech.jastor.util.*;" + NL + "" + NL + "" + NL + "/**" + NL + " * Implementation of {@link ";
  protected final String TEXT_4 = "}" + NL + " * Use the ";
  protected final String TEXT_5 = " to create instances of this class." + NL + " * <p>(URI: ";
  protected final String TEXT_6 = ")</p>" + NL + " * <br>" + NL + " */" + NL + "public class ";
  protected final String TEXT_7 = " extends ";
  protected final String TEXT_8 = " implements ";
  protected final String TEXT_9 = " {" + NL + "\t" + NL;
  protected final String TEXT_10 = NL + "\tprivate static com.hp.hpl.jena.rdf.model.Property ";
  protected final String TEXT_11 = "Property = ResourceFactory.createProperty(\"";
  protected final String TEXT_12 = "\");";
  protected final String TEXT_13 = NL + "\tprivate ";
  protected final String TEXT_14 = " ";
  protected final String TEXT_15 = ";";
  protected final String TEXT_16 = NL + "\tprivate java.util.ArrayList ";
  protected final String TEXT_17 = ";";
  protected final String TEXT_18 = NL + "\tprivate java.util.ArrayList ";
  protected final String TEXT_19 = ";";
  protected final String TEXT_20 = NL;
  protected final String TEXT_21 = NL + "\tprivate static java.util.Set oneOfClasses = new java.util.HashSet();" + NL + "\tstatic {";
  protected final String TEXT_22 = NL + "\t\toneOfClasses.add(";
  protected final String TEXT_23 = ");";
  protected final String TEXT_24 = NL + "\t}";
  protected final String TEXT_25 = " " + NL + "" + NL + "\t";
  protected final String TEXT_26 = "(Resource resource, Model model) throws JastorException {" + NL + "\t\tsuper(resource, model);";
  protected final String TEXT_27 = NL + "\t\tsetupModelListener();";
  protected final String TEXT_28 = NL + "\t}     " + NL + "    \t" + NL + "\tstatic ";
  protected final String TEXT_29 = " get";
  protected final String TEXT_30 = "(Resource resource, Model model) throws JastorException {";
  protected final String TEXT_31 = NL + "\t\tif (!model.contains(resource,RDF.type,";
  protected final String TEXT_32 = ".TYPE))" + NL + "\t\t\treturn null;";
  protected final String TEXT_33 = NL + "\t\treturn new ";
  protected final String TEXT_34 = "(resource, model);" + NL + "\t}" + NL + "\t    " + NL + "\tstatic ";
  protected final String TEXT_35 = " create";
  protected final String TEXT_36 = "(Resource resource, Model model) throws JastorException {";
  protected final String TEXT_37 = NL + "\t\tif (!oneOfClasses.contains(resource)) {" + NL + "\t\t\tthrow new JastorException(\"Resource \" + resource + \" not a member of enumeration class ";
  protected final String TEXT_38 = "\"); " + NL + "\t\t}";
  protected final String TEXT_39 = " " + NL + "\t\t";
  protected final String TEXT_40 = " impl = new ";
  protected final String TEXT_41 = "(resource, model);" + NL + "\t\t" + NL + "\t\tif (!impl._model.contains(new com.hp.hpl.jena.rdf.model.impl.StatementImpl(impl._resource, RDF.type, ";
  protected final String TEXT_42 = ".TYPE)))" + NL + "\t\t\timpl._model.add(impl._resource, RDF.type, ";
  protected final String TEXT_43 = ".TYPE);";
  protected final String TEXT_44 = NL + "\t\timpl.addSuperTypes();";
  protected final String TEXT_45 = NL + "\t\timpl.addHasValueValues();" + NL + "\t\treturn impl;" + NL + "\t}" + NL + "\t" + NL + "\tvoid addSuperTypes() {";
  protected final String TEXT_46 = NL + "\t\tif (!_model.contains(_resource, RDF.type, ";
  protected final String TEXT_47 = ".TYPE))" + NL + "\t\t\t_model.add(new com.hp.hpl.jena.rdf.model.impl.StatementImpl(_resource, RDF.type, ";
  protected final String TEXT_48 = ".TYPE));     ";
  protected final String TEXT_49 = NL + "\t}" + NL + "   " + NL + "\tvoid addHasValueValues() {";
  protected final String TEXT_50 = NL + "\t\tif (!_model.contains(_resource, ";
  protected final String TEXT_51 = "Property, _model.getResource(\"";
  protected final String TEXT_52 = "\")))" + NL + "\t\t\t_model.add(new com.hp.hpl.jena.rdf.model.impl.StatementImpl(_resource, ";
  protected final String TEXT_53 = "Property, _model.getResource(\"";
  protected final String TEXT_54 = "\")));";
  protected final String TEXT_55 = NL + "\t\tif (!_model.contains(_resource, ";
  protected final String TEXT_56 = "Property, createLiteral(\"";
  protected final String TEXT_57 = "\")))" + NL + "\t\t\t_model.add(new com.hp.hpl.jena.rdf.model.impl.StatementImpl(_resource, ";
  protected final String TEXT_58 = "Property, createLiteral(\"";
  protected final String TEXT_59 = "\")));";
  protected final String TEXT_60 = NL + "\t}" + NL + "    ";
  protected final String TEXT_61 = NL + "    private void setupModelListener() {";
  protected final String TEXT_62 = NL + "    \tlisteners = new java.util.ArrayList();";
  protected final String TEXT_63 = NL + "    \t";
  protected final String TEXT_64 = ".registerThing(this);" + NL + "    }";
  protected final String TEXT_65 = NL + NL + "\tpublic java.util.List listStatements() {" + NL + "\t\tjava.util.List list = new java.util.ArrayList();" + NL + "\t\tStmtIterator it = null;";
  protected final String TEXT_66 = NL + "\t\tit = _model.listStatements(_resource,";
  protected final String TEXT_67 = "Property,(RDFNode)null);" + NL + "\t\twhile (it.hasNext()) {" + NL + "\t\t\tlist.add(it.next());" + NL + "\t\t}";
  protected final String TEXT_68 = NL + "\t\tit = _model.listStatements(_resource,RDF.type, ";
  protected final String TEXT_69 = ".TYPE);" + NL + "\t\twhile (it.hasNext()) {" + NL + "\t\t\tlist.add(it.next());" + NL + "\t\t}";
  protected final String TEXT_70 = NL + "\t\tit = _model.listStatements(_resource,RDF.type, ";
  protected final String TEXT_71 = ".TYPE);" + NL + "\t\twhile (it.hasNext()) {" + NL + "\t\t\tlist.add(it.next());" + NL + "\t\t}";
  protected final String TEXT_72 = NL + "\t\treturn list;" + NL + "\t}" + NL + "\t";
  protected final String TEXT_73 = NL + "\tpublic void clearCache() {";
  protected final String TEXT_74 = NL + "\t\t";
  protected final String TEXT_75 = " = null;";
  protected final String TEXT_76 = NL + "\t}";
  protected final String TEXT_77 = NL + NL + "\tprivate com.hp.hpl.jena.rdf.model.Literal createLiteral(Object obj) {";
  protected final String TEXT_78 = NL + "\t\treturn _model.createTypedLiteral(obj);";
  protected final String TEXT_79 = NL + "\t\treturn _model.createLiteral(obj.toString(),false);";
  protected final String TEXT_80 = NL + "\t}" + NL;
  protected final String TEXT_81 = NL + "\tpublic ";
  protected final String TEXT_82 = " get";
  protected final String TEXT_83 = "() throws JastorException {";
  protected final String TEXT_84 = NL + "\t\tif (";
  protected final String TEXT_85 = " != null)" + NL + "\t\t\treturn ";
  protected final String TEXT_86 = ";";
  protected final String TEXT_87 = NL + "\t\tcom.hp.hpl.jena.rdf.model.Statement stmt = _model.getProperty(_resource, ";
  protected final String TEXT_88 = "Property);" + NL + "\t\tif (stmt == null)" + NL + "\t\t\treturn null;" + NL + "\t\tif (!stmt.getObject().canAs(com.hp.hpl.jena.rdf.model.Literal.class))" + NL + "\t\t\tthrow new JastorInvalidRDFNodeException(uri() + \": ";
  protected final String TEXT_89 = " getProperty() in ";
  protected final String TEXT_90 = " model not Literal\", stmt.getObject());" + NL + "\t\tcom.hp.hpl.jena.rdf.model.Literal literal = (com.hp.hpl.jena.rdf.model.Literal) stmt.getObject().as(com.hp.hpl.jena.rdf.model.Literal.class);";
  protected final String TEXT_91 = NL + "\t\t";
  protected final String TEXT_92 = " = literal;";
  protected final String TEXT_93 = NL + "\t\treturn literal;";
  protected final String TEXT_94 = NL + "\t\tObject obj = Util.fixLiteral(";
  protected final String TEXT_95 = ",literal,\"";
  protected final String TEXT_96 = "\",\"";
  protected final String TEXT_97 = "\");";
  protected final String TEXT_98 = NL + "\t\t";
  protected final String TEXT_99 = " = (";
  protected final String TEXT_100 = ")obj;" + NL + "\t\treturn ";
  protected final String TEXT_101 = ";";
  protected final String TEXT_102 = NL + "\t\treturn (";
  protected final String TEXT_103 = ")obj;";
  protected final String TEXT_104 = NL + "\t}" + NL + "\t" + NL + "\tpublic void set";
  protected final String TEXT_105 = "(";
  protected final String TEXT_106 = " ";
  protected final String TEXT_107 = ") throws JastorException {" + NL + "\t\tif (_model.contains(_resource,";
  protected final String TEXT_108 = "Property)) {" + NL + "\t\t\t_model.removeAll(_resource,";
  protected final String TEXT_109 = "Property,null);" + NL + "\t\t}";
  protected final String TEXT_110 = NL + "\t\tthis.";
  protected final String TEXT_111 = " = ";
  protected final String TEXT_112 = ";";
  protected final String TEXT_113 = "                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                   " + NL + "\t\tif (";
  protected final String TEXT_114 = " != null) {";
  protected final String TEXT_115 = NL + "\t\t\t_model.add(_model.createStatement(_resource,";
  protected final String TEXT_116 = "Property, ";
  protected final String TEXT_117 = "));";
  protected final String TEXT_118 = NL + "\t\t\t_model.add(_model.createStatement(_resource,";
  protected final String TEXT_119 = "Property, createLiteral(";
  protected final String TEXT_120 = ")));";
  protected final String TEXT_121 = NL + "\t\t}\t" + NL + "\t}";
  protected final String TEXT_122 = NL;
  protected final String TEXT_123 = NL + "\tprivate void init";
  protected final String TEXT_124 = "() throws JastorException {" + NL + "\t\t";
  protected final String TEXT_125 = " = new java.util.ArrayList();" + NL + "\t\t" + NL + "\t\tStmtIterator it = _model.listStatements(_resource, ";
  protected final String TEXT_126 = "Property, (RDFNode)null);" + NL + "\t\twhile(it.hasNext()) {" + NL + "\t\t\tcom.hp.hpl.jena.rdf.model.Statement stmt = (com.hp.hpl.jena.rdf.model.Statement)it.next();" + NL + "\t\t\tif (!stmt.getObject().canAs(com.hp.hpl.jena.rdf.model.Literal.class))" + NL + "\t\t\t\tthrow new JastorInvalidRDFNodeException (uri() + \": One of the ";
  protected final String TEXT_127 = " properties in ";
  protected final String TEXT_128 = " model not a Literal\", stmt.getObject());" + NL + "\t\t\tcom.hp.hpl.jena.rdf.model.Literal literal = (com.hp.hpl.jena.rdf.model.Literal) stmt.getObject().as(com.hp.hpl.jena.rdf.model.Literal.class);";
  protected final String TEXT_129 = NL + "\t\t\t";
  protected final String TEXT_130 = ".add(literal);";
  protected final String TEXT_131 = NL + "\t\t\tObject obj = Util.fixLiteral(";
  protected final String TEXT_132 = ",literal,\"";
  protected final String TEXT_133 = "\",\"";
  protected final String TEXT_134 = "\");" + NL + "\t\t\tif (obj != null)" + NL + "\t\t\t\t";
  protected final String TEXT_135 = ".add(obj);";
  protected final String TEXT_136 = NL + "\t\t}" + NL + "\t}";
  protected final String TEXT_137 = NL + NL + "\tpublic java.util.Iterator get";
  protected final String TEXT_138 = "() throws JastorException {";
  protected final String TEXT_139 = NL + "\t\tif (";
  protected final String TEXT_140 = " == null)" + NL + "\t\t\tinit";
  protected final String TEXT_141 = "();" + NL + "\t\treturn new com.ibm.adtech.jastor.util.CachedPropertyIterator(";
  protected final String TEXT_142 = ",_resource,";
  protected final String TEXT_143 = "Property,false);";
  protected final String TEXT_144 = NL + "\t\treturn new com.ibm.adtech.jastor.util.PropertyIterator(_resource,";
  protected final String TEXT_145 = "Property,_model.getResource(\"";
  protected final String TEXT_146 = "\")) {" + NL + "\t\t\tpublic Object getPropertyValue(RDFNode value) {" + NL + "\t\t\t\tcom.hp.hpl.jena.rdf.model.Literal literal = (com.hp.hpl.jena.rdf.model.Literal) value.as(com.hp.hpl.jena.rdf.model.Literal.class);";
  protected final String TEXT_147 = NL + "\t\t\treturn literal;";
  protected final String TEXT_148 = NL + "\t\t\treturn Util.fixLiteral(";
  protected final String TEXT_149 = ",literal,\"";
  protected final String TEXT_150 = "\",\"";
  protected final String TEXT_151 = "\");";
  protected final String TEXT_152 = NL + "\t\t\t}" + NL + "\t\t};";
  protected final String TEXT_153 = NL + "\t}" + NL + "" + NL + "\tpublic void add";
  protected final String TEXT_154 = "(";
  protected final String TEXT_155 = " ";
  protected final String TEXT_156 = ") throws JastorException {";
  protected final String TEXT_157 = NL + "\t\tif (this.";
  protected final String TEXT_158 = " == null)" + NL + "\t\t\tinit";
  protected final String TEXT_159 = "();" + NL + "\t\tif (this.";
  protected final String TEXT_160 = ".contains(";
  protected final String TEXT_161 = "))" + NL + "\t\t\treturn;";
  protected final String TEXT_162 = NL + "\t\tif (_model.contains(_resource, ";
  protected final String TEXT_163 = "Property, createLiteral(";
  protected final String TEXT_164 = ")))" + NL + "\t\t\treturn;";
  protected final String TEXT_165 = NL + "\t\tthis.";
  protected final String TEXT_166 = ".add(";
  protected final String TEXT_167 = ");";
  protected final String TEXT_168 = NL + "\t\t_model.add(_resource, ";
  protected final String TEXT_169 = "Property, ";
  protected final String TEXT_170 = ");";
  protected final String TEXT_171 = NL + "\t\t_model.add(_resource, ";
  protected final String TEXT_172 = "Property, createLiteral(";
  protected final String TEXT_173 = "));";
  protected final String TEXT_174 = NL + "\t}" + NL + "\t" + NL + "\tpublic void remove";
  protected final String TEXT_175 = "(";
  protected final String TEXT_176 = " ";
  protected final String TEXT_177 = ") throws JastorException {";
  protected final String TEXT_178 = NL + "\t\tif (this.";
  protected final String TEXT_179 = " == null)" + NL + "\t\t\tinit";
  protected final String TEXT_180 = "();" + NL + "\t\tif (!this.";
  protected final String TEXT_181 = ".contains(";
  protected final String TEXT_182 = "))" + NL + "\t\t\treturn;";
  protected final String TEXT_183 = NL + "\t\tif (!_model.contains(_resource, ";
  protected final String TEXT_184 = "Property, createLiteral(";
  protected final String TEXT_185 = ")))" + NL + "\t\t\treturn;";
  protected final String TEXT_186 = NL + "\t\tthis.";
  protected final String TEXT_187 = ".remove(";
  protected final String TEXT_188 = ");";
  protected final String TEXT_189 = NL + "\t\t_model.removeAll(_resource, ";
  protected final String TEXT_190 = "Property, createLiteral(";
  protected final String TEXT_191 = "));" + NL + "\t}" + NL;
  protected final String TEXT_192 = NL + "\tpublic ";
  protected final String TEXT_193 = " get";
  protected final String TEXT_194 = "() throws JastorException {";
  protected final String TEXT_195 = NL + "\t\tif (";
  protected final String TEXT_196 = " != null)" + NL + "\t\t\treturn ";
  protected final String TEXT_197 = ";";
  protected final String TEXT_198 = NL + "\t\tcom.hp.hpl.jena.rdf.model.Statement stmt = _model.getProperty(_resource, ";
  protected final String TEXT_199 = "Property);" + NL + "\t\tif (stmt == null)" + NL + "\t\t\treturn null;" + NL + "\t\tif (!stmt.getObject().canAs(com.hp.hpl.jena.rdf.model.Resource.class))" + NL + "\t\t\tthrow new JastorInvalidRDFNodeException(uri() + \": ";
  protected final String TEXT_200 = " getProperty() in ";
  protected final String TEXT_201 = " model not Resource\", stmt.getObject());" + NL + "\t\tcom.hp.hpl.jena.rdf.model.Resource resource = (com.hp.hpl.jena.rdf.model.Resource) stmt.getObject().as(com.hp.hpl.jena.rdf.model.Resource.class);";
  protected final String TEXT_202 = NL + "\t\tif (!_model.contains(resource,RDF.type,";
  protected final String TEXT_203 = ".TYPE))" + NL + "\t\t\treturn null;";
  protected final String TEXT_204 = NL + "\t\t";
  protected final String TEXT_205 = " = ";
  protected final String TEXT_206 = ".get";
  protected final String TEXT_207 = "(resource,_model);" + NL + "\t\treturn ";
  protected final String TEXT_208 = ";";
  protected final String TEXT_209 = NL + "\t\treturn ";
  protected final String TEXT_210 = ".get";
  protected final String TEXT_211 = "(resource,_model);";
  protected final String TEXT_212 = NL + "\t}" + NL + "" + NL + "\tpublic void set";
  protected final String TEXT_213 = "(";
  protected final String TEXT_214 = " ";
  protected final String TEXT_215 = ") throws JastorException {" + NL + "\t\tif (_model.contains(_resource,";
  protected final String TEXT_216 = "Property)) {" + NL + "\t\t\t_model.removeAll(_resource,";
  protected final String TEXT_217 = "Property,null);" + NL + "\t\t}";
  protected final String TEXT_218 = NL + "\t\tthis.";
  protected final String TEXT_219 = " = ";
  protected final String TEXT_220 = ";";
  protected final String TEXT_221 = NL + "\t\tif (";
  protected final String TEXT_222 = " != null) {" + NL + "\t\t\t_model.add(_model.createStatement(_resource,";
  protected final String TEXT_223 = "Property, ";
  protected final String TEXT_224 = ".resource()));" + NL + "\t\t}\t\t\t" + NL + "\t}" + NL + "\t\t" + NL + "\tpublic ";
  protected final String TEXT_225 = " set";
  protected final String TEXT_226 = "() throws JastorException {" + NL + "\t\tif (_model.contains(_resource,";
  protected final String TEXT_227 = "Property)) {" + NL + "\t\t\t_model.removeAll(_resource,";
  protected final String TEXT_228 = "Property,null);" + NL + "\t\t}" + NL + "\t\t";
  protected final String TEXT_229 = " ";
  protected final String TEXT_230 = " = ";
  protected final String TEXT_231 = ".create";
  protected final String TEXT_232 = "(_model.createResource(),_model);";
  protected final String TEXT_233 = NL + "\t\tthis.";
  protected final String TEXT_234 = " = ";
  protected final String TEXT_235 = ";";
  protected final String TEXT_236 = NL + "\t\t_model.add(_model.createStatement(_resource,";
  protected final String TEXT_237 = "Property, ";
  protected final String TEXT_238 = ".resource()));" + NL + "\t\treturn ";
  protected final String TEXT_239 = ";" + NL + "\t}" + NL + "\t" + NL + "\tpublic ";
  protected final String TEXT_240 = " set";
  protected final String TEXT_241 = "(com.hp.hpl.jena.rdf.model.Resource resource) throws JastorException {";
  protected final String TEXT_242 = NL + "\t\tif (!_model.contains(resource,RDF.type,";
  protected final String TEXT_243 = ".TYPE))" + NL + "\t\t\tthrow new JastorException(\"Resource \" + resource + \" not of type \" + ";
  protected final String TEXT_244 = ".TYPE);";
  protected final String TEXT_245 = NL + "\t\tif (_model.contains(_resource,";
  protected final String TEXT_246 = "Property)) {" + NL + "\t\t\t_model.removeAll(_resource,";
  protected final String TEXT_247 = "Property,null);" + NL + "\t\t}" + NL + "\t\t";
  protected final String TEXT_248 = " ";
  protected final String TEXT_249 = " = ";
  protected final String TEXT_250 = ".get";
  protected final String TEXT_251 = "(resource,_model);";
  protected final String TEXT_252 = NL + "\t\tthis.";
  protected final String TEXT_253 = " = ";
  protected final String TEXT_254 = ";";
  protected final String TEXT_255 = NL + "\t\t_model.add(_model.createStatement(_resource,";
  protected final String TEXT_256 = "Property, ";
  protected final String TEXT_257 = ".resource()));" + NL + "\t\treturn ";
  protected final String TEXT_258 = ";" + NL + "\t}" + NL + "\t";
  protected final String TEXT_259 = NL + "\tprivate void init";
  protected final String TEXT_260 = "() throws JastorException {" + NL + "\t\tthis.";
  protected final String TEXT_261 = " = new java.util.ArrayList();" + NL + "\t\tStmtIterator it = _model.listStatements(_resource, ";
  protected final String TEXT_262 = "Property, (RDFNode)null);" + NL + "\t\twhile(it.hasNext()) {" + NL + "\t\t\tcom.hp.hpl.jena.rdf.model.Statement stmt = (com.hp.hpl.jena.rdf.model.Statement)it.next();" + NL + "\t\t\tif (!stmt.getObject().canAs(com.hp.hpl.jena.rdf.model.Resource.class))" + NL + "\t\t\t\tthrow new JastorInvalidRDFNodeException (uri() + \": One of the ";
  protected final String TEXT_263 = " properties in ";
  protected final String TEXT_264 = " model not a Resource\", stmt.getObject());" + NL + "\t\t\tcom.hp.hpl.jena.rdf.model.Resource resource = (com.hp.hpl.jena.rdf.model.Resource) stmt.getObject().as(com.hp.hpl.jena.rdf.model.Resource.class);";
  protected final String TEXT_265 = NL + "\t\t\tif (_model.contains(resource,RDF.type,";
  protected final String TEXT_266 = ".TYPE)) {";
  protected final String TEXT_267 = NL + "\t\t\tif (true) { // don't check resource type if the property range is Resource";
  protected final String TEXT_268 = NL + "\t\t\t\t";
  protected final String TEXT_269 = " ";
  protected final String TEXT_270 = " = ";
  protected final String TEXT_271 = ".get";
  protected final String TEXT_272 = "(resource,_model);" + NL + "\t\t\t\tthis.";
  protected final String TEXT_273 = ".add(";
  protected final String TEXT_274 = ");" + NL + "\t\t\t}" + NL + "\t\t}" + NL + "\t}";
  protected final String TEXT_275 = NL + NL + "\tpublic java.util.Iterator get";
  protected final String TEXT_276 = "() throws JastorException {";
  protected final String TEXT_277 = NL + "\t\tif (";
  protected final String TEXT_278 = " == null)" + NL + "\t\t\tinit";
  protected final String TEXT_279 = "();" + NL + "\t\treturn new com.ibm.adtech.jastor.util.CachedPropertyIterator(";
  protected final String TEXT_280 = ",_resource,";
  protected final String TEXT_281 = "Property,true);";
  protected final String TEXT_282 = NL + "\t\treturn new com.ibm.adtech.jastor.util.PropertyIterator(_resource,";
  protected final String TEXT_283 = "Property,";
  protected final String TEXT_284 = ".TYPE) {\t";
  protected final String TEXT_285 = NL + "\t\treturn new com.ibm.adtech.jastor.util.PropertyIterator(_resource,";
  protected final String TEXT_286 = "Property,null) {";
  protected final String TEXT_287 = NL + "\t\t\tpublic Object getPropertyValue(RDFNode value) {" + NL + "\t\t\t\tcom.hp.hpl.jena.rdf.model.Resource resource = (com.hp.hpl.jena.rdf.model.Resource) value.as(com.hp.hpl.jena.rdf.model.Resource.class);" + NL + "\t\t\t\ttry {" + NL + "\t\t\t\t\treturn ";
  protected final String TEXT_288 = ".get";
  protected final String TEXT_289 = "(resource,_model);" + NL + "\t\t\t\t} catch (JastorException e) {" + NL + "\t\t\t\t\tthrow new java.util.NoSuchElementException(e.getMessage());" + NL + "\t\t\t\t}" + NL + "\t\t\t}" + NL + "\t\t\t};";
  protected final String TEXT_290 = NL + "\t}" + NL + "" + NL + "\tpublic void add";
  protected final String TEXT_291 = "(";
  protected final String TEXT_292 = " ";
  protected final String TEXT_293 = ") throws JastorException {";
  protected final String TEXT_294 = NL + "\t\tif (this.";
  protected final String TEXT_295 = " == null)" + NL + "\t\t\tinit";
  protected final String TEXT_296 = "();" + NL + "\t\tif (this.";
  protected final String TEXT_297 = ".contains(";
  protected final String TEXT_298 = ")) {" + NL + "\t\t\tthis.";
  protected final String TEXT_299 = ".remove(";
  protected final String TEXT_300 = ");" + NL + "\t\t\tthis.";
  protected final String TEXT_301 = ".add(";
  protected final String TEXT_302 = ");" + NL + "\t\t\treturn;" + NL + "\t\t}" + NL + "\t\tthis.";
  protected final String TEXT_303 = ".add(";
  protected final String TEXT_304 = ");";
  protected final String TEXT_305 = NL + "\t\t_model.add(_model.createStatement(_resource,";
  protected final String TEXT_306 = "Property,";
  protected final String TEXT_307 = ".resource()));" + NL + "\t}" + NL + "\t" + NL + "\tpublic ";
  protected final String TEXT_308 = " add";
  protected final String TEXT_309 = "() throws JastorException {" + NL + "\t\t";
  protected final String TEXT_310 = " ";
  protected final String TEXT_311 = " = ";
  protected final String TEXT_312 = ".create";
  protected final String TEXT_313 = "(_model.createResource(),_model);";
  protected final String TEXT_314 = NL + "\t\tif (this.";
  protected final String TEXT_315 = " == null)" + NL + "\t\t\tinit";
  protected final String TEXT_316 = "();" + NL + "\t\tthis.";
  protected final String TEXT_317 = ".add(";
  protected final String TEXT_318 = ");";
  protected final String TEXT_319 = NL + "\t\t_model.add(_model.createStatement(_resource,";
  protected final String TEXT_320 = "Property,";
  protected final String TEXT_321 = ".resource()));" + NL + "\t\treturn ";
  protected final String TEXT_322 = ";" + NL + "\t}" + NL + "\t" + NL + "\tpublic ";
  protected final String TEXT_323 = " add";
  protected final String TEXT_324 = "(com.hp.hpl.jena.rdf.model.Resource resource) throws JastorException {";
  protected final String TEXT_325 = NL + "\t\tif (!_model.contains(resource,RDF.type,";
  protected final String TEXT_326 = ".TYPE))" + NL + "\t\t\tthrow new JastorException(\"Resource \" + resource + \" not of type \" + ";
  protected final String TEXT_327 = ".TYPE);";
  protected final String TEXT_328 = NL + "\t\t";
  protected final String TEXT_329 = " ";
  protected final String TEXT_330 = " = ";
  protected final String TEXT_331 = ".get";
  protected final String TEXT_332 = "(resource,_model);";
  protected final String TEXT_333 = NL + "\t\tif (this.";
  protected final String TEXT_334 = " == null)" + NL + "\t\t\tinit";
  protected final String TEXT_335 = "();" + NL + "\t\tif (this.";
  protected final String TEXT_336 = ".contains(";
  protected final String TEXT_337 = "))" + NL + "\t\t\treturn ";
  protected final String TEXT_338 = ";" + NL + "\t\tthis.";
  protected final String TEXT_339 = ".add(";
  protected final String TEXT_340 = ");";
  protected final String TEXT_341 = NL + "\t\t_model.add(_model.createStatement(_resource,";
  protected final String TEXT_342 = "Property,";
  protected final String TEXT_343 = ".resource()));" + NL + "\t\treturn ";
  protected final String TEXT_344 = ";" + NL + "\t}" + NL + "\t" + NL + "\tpublic void remove";
  protected final String TEXT_345 = "(";
  protected final String TEXT_346 = " ";
  protected final String TEXT_347 = ") throws JastorException {";
  protected final String TEXT_348 = NL + "\t\tif (this.";
  protected final String TEXT_349 = " == null)" + NL + "\t\t\tinit";
  protected final String TEXT_350 = "();" + NL + "\t\tif (!this.";
  protected final String TEXT_351 = ".contains(";
  protected final String TEXT_352 = "))" + NL + "\t\t\treturn;";
  protected final String TEXT_353 = NL + "\t\tif (!_model.contains(_resource, ";
  protected final String TEXT_354 = "Property, ";
  protected final String TEXT_355 = ".resource()))" + NL + "\t\t\treturn;";
  protected final String TEXT_356 = NL + "\t\tthis.";
  protected final String TEXT_357 = ".remove(";
  protected final String TEXT_358 = ");";
  protected final String TEXT_359 = NL + "\t\t_model.removeAll(_resource, ";
  protected final String TEXT_360 = "Property, ";
  protected final String TEXT_361 = ".resource());" + NL + "\t}" + NL + "\t\t";
  protected final String TEXT_362 = " ";
  protected final String TEXT_363 = " " + NL;
  protected final String TEXT_364 = NL + NL + "\tprivate java.util.ArrayList listeners;" + NL + "\t" + NL + "\tpublic void registerListener(ThingListener listener) {" + NL + "\t\tif (!(listener instanceof ";
  protected final String TEXT_365 = "Listener))" + NL + "\t\t\tthrow new IllegalArgumentException(\"ThingListener must be instance of ";
  protected final String TEXT_366 = "\"); " + NL + "\t\tif (listeners == null)" + NL + "\t\t\tsetupModelListener();" + NL + "\t\tif(!this.listeners.contains(listener)){" + NL + "\t\t\tthis.listeners.add((";
  protected final String TEXT_367 = ")listener);" + NL + "\t\t}" + NL + "\t}" + NL + "\t" + NL + "\tpublic void unregisterListener(ThingListener listener) {" + NL + "\t\tif (!(listener instanceof ";
  protected final String TEXT_368 = "Listener))" + NL + "\t\t\tthrow new IllegalArgumentException(\"ThingListener must be instance of ";
  protected final String TEXT_369 = "\"); " + NL + "\t\tif (listeners == null)" + NL + "\t\t\treturn;" + NL + "\t\tif (this.listeners.contains(listener)){" + NL + "\t\t\tlisteners.remove(listener);" + NL + "\t\t}" + NL + "\t}" + NL;
  protected final String TEXT_370 = NL;
  protected final String TEXT_371 = NL + NL + "\t" + NL + "\t\tpublic void addedStatement(com.hp.hpl.jena.rdf.model.Statement stmt) {" + NL;
  protected final String TEXT_372 = NL + "\t\t\tif (stmt.getPredicate().equals(";
  protected final String TEXT_373 = "Property)) {";
  protected final String TEXT_374 = NL + "\t\t\t\tif (!stmt.getObject().canAs(com.hp.hpl.jena.rdf.model.Literal.class))" + NL + "\t\t\t\t\treturn;" + NL + "\t\t\t\tcom.hp.hpl.jena.rdf.model.Literal literal = (com.hp.hpl.jena.rdf.model.Literal) stmt.getObject().as(com.hp.hpl.jena.rdf.model.Literal.class);";
  protected final String TEXT_375 = NL + "\t\t\t\t";
  protected final String TEXT_376 = " = literal;";
  protected final String TEXT_377 = NL + "\t\t\t\t";
  protected final String TEXT_378 = " = (";
  protected final String TEXT_379 = ")Util.fixLiteral(";
  protected final String TEXT_380 = ",literal,\"";
  protected final String TEXT_381 = "\",\"";
  protected final String TEXT_382 = "\");";
  protected final String TEXT_383 = NL + "\t\t\t\tif (listeners != null) {" + NL + "\t\t\t\t\tjava.util.ArrayList consumers;" + NL + "\t\t\t\t\tsynchronized (listeners) {" + NL + "\t\t\t\t\t\tconsumers = (java.util.ArrayList) listeners.clone();" + NL + "\t\t\t\t\t}" + NL + "\t\t\t\t\tfor(java.util.Iterator iter=consumers.iterator();iter.hasNext();){" + NL + "\t\t\t\t\t\t";
  protected final String TEXT_384 = "Listener listener=(";
  protected final String TEXT_385 = "Listener)iter.next();" + NL + "\t\t\t\t\t\tlistener.";
  protected final String TEXT_386 = "Changed(";
  protected final String TEXT_387 = ".this);" + NL + "\t\t\t\t\t}" + NL + "\t\t\t\t}";
  protected final String TEXT_388 = NL + "\t\t\t\tif (!stmt.getObject().canAs(com.hp.hpl.jena.rdf.model.Literal.class))" + NL + "\t\t\t\t\treturn;" + NL + "\t\t\t\tcom.hp.hpl.jena.rdf.model.Literal literal = (com.hp.hpl.jena.rdf.model.Literal) stmt.getObject().as(com.hp.hpl.jena.rdf.model.Literal.class);" + NL + "\t\t\t\t//Object obj = literal.getValue();";
  protected final String TEXT_389 = NL + "\t\t\t\tif (";
  protected final String TEXT_390 = " == null)" + NL + "\t\t\t\t\ttry {" + NL + "\t\t\t\t\t\tinit";
  protected final String TEXT_391 = "();" + NL + "\t\t\t\t\t} catch (JastorException e) {" + NL + "\t\t\t\t\t\te.printStackTrace();" + NL + "\t\t\t\t\t\treturn;" + NL + "\t\t\t\t\t}" + NL + "\t\t\t\tif (!";
  protected final String TEXT_392 = ".contains(literal))" + NL + "\t\t\t\t\t";
  protected final String TEXT_393 = ".add(literal);";
  protected final String TEXT_394 = NL + "\t\t\t\tif (listeners != null) {" + NL + "\t\t\t\t\tjava.util.ArrayList consumersFor";
  protected final String TEXT_395 = ";" + NL + "\t\t\t\t\tsynchronized (listeners) {" + NL + "\t\t\t\t\t\tconsumersFor";
  protected final String TEXT_396 = " = (java.util.ArrayList) listeners.clone();" + NL + "\t\t\t\t\t}" + NL + "\t\t\t\t\tfor(java.util.Iterator iter=consumersFor";
  protected final String TEXT_397 = ".iterator();iter.hasNext();){" + NL + "\t\t\t\t\t\t";
  protected final String TEXT_398 = "Listener listener=(";
  protected final String TEXT_399 = "Listener)iter.next();" + NL + "\t\t\t\t\t\tlistener.";
  protected final String TEXT_400 = "Added(";
  protected final String TEXT_401 = ".this,literal);" + NL + "\t\t\t\t\t}" + NL + "\t\t\t\t}";
  protected final String TEXT_402 = NL + "\t\t\t\tObject obj = Util.fixLiteral(";
  protected final String TEXT_403 = ",literal,\"";
  protected final String TEXT_404 = "\",\"";
  protected final String TEXT_405 = "\");";
  protected final String TEXT_406 = NL + "\t\t\t\tif (";
  protected final String TEXT_407 = " == null) {" + NL + "\t\t\t\t\ttry {" + NL + "\t\t\t\t\t\tinit";
  protected final String TEXT_408 = "();" + NL + "\t\t\t\t\t} catch (JastorException e) {" + NL + "\t\t\t\t\t\te.printStackTrace();" + NL + "\t\t\t\t\t\treturn;" + NL + "\t\t\t\t\t}" + NL + "\t\t\t\t}" + NL + "\t\t\t\tif (obj != null && !";
  protected final String TEXT_409 = ".contains(obj))" + NL + "\t\t\t\t\t";
  protected final String TEXT_410 = ".add(obj);";
  protected final String TEXT_411 = NL + "\t\t\t\tjava.util.ArrayList consumersFor";
  protected final String TEXT_412 = ";" + NL + "\t\t\t\tif (listeners != null) {" + NL + "\t\t\t\t\tsynchronized (listeners) {" + NL + "\t\t\t\t\t\tconsumersFor";
  protected final String TEXT_413 = " = (java.util.ArrayList) listeners.clone();" + NL + "\t\t\t\t\t}" + NL + "\t\t\t\t\tfor(java.util.Iterator iter=consumersFor";
  protected final String TEXT_414 = ".iterator();iter.hasNext();){" + NL + "\t\t\t\t\t\t";
  protected final String TEXT_415 = "Listener listener=(";
  protected final String TEXT_416 = "Listener)iter.next();" + NL + "\t\t\t\t\t\tlistener.";
  protected final String TEXT_417 = "Added(";
  protected final String TEXT_418 = ".this,(";
  protected final String TEXT_419 = ")obj);" + NL + "\t\t\t\t\t}" + NL + "\t\t\t\t}";
  protected final String TEXT_420 = NL + "\t\t\t\tif (!stmt.getObject().canAs(com.hp.hpl.jena.rdf.model.Resource.class))" + NL + "\t\t\t\t\treturn;" + NL + "\t\t\t\tcom.hp.hpl.jena.rdf.model.Resource resource = (com.hp.hpl.jena.rdf.model.Resource) stmt.getObject().as(com.hp.hpl.jena.rdf.model.Resource.class);";
  protected final String TEXT_421 = NL + "\t\t\t\t";
  protected final String TEXT_422 = " = null;";
  protected final String TEXT_423 = NL + "\t\t\t\tif (_model.contains(resource,RDF.type,";
  protected final String TEXT_424 = ".TYPE)) {";
  protected final String TEXT_425 = NL + "\t\t\t\tif (true) { // don't check resource type if the property range is Resource";
  protected final String TEXT_426 = NL + "\t\t\t\t\ttry {" + NL + "\t\t\t\t\t\t";
  protected final String TEXT_427 = " = ";
  protected final String TEXT_428 = ".get";
  protected final String TEXT_429 = "(resource,_model);" + NL + "\t\t\t\t\t} catch (JastorException e) {" + NL + "\t\t\t\t\t\t//e.printStackTrace();" + NL + "\t\t\t\t\t}" + NL + "\t\t\t\t}";
  protected final String TEXT_430 = NL + "\t\t\t\tif (listeners != null) {" + NL + "\t\t\t\t\tjava.util.ArrayList consumers;" + NL + "\t\t\t\t\tsynchronized (listeners) {" + NL + "\t\t\t\t\t\tconsumers = (java.util.ArrayList) listeners.clone();" + NL + "\t\t\t\t\t}" + NL + "\t\t\t\t\tfor(java.util.Iterator iter=consumers.iterator();iter.hasNext();){" + NL + "\t\t\t\t\t\t";
  protected final String TEXT_431 = "Listener listener=(";
  protected final String TEXT_432 = "Listener)iter.next();" + NL + "\t\t\t\t\t\tlistener.";
  protected final String TEXT_433 = "Changed(";
  protected final String TEXT_434 = ".this);" + NL + "\t\t\t\t\t}" + NL + "\t\t\t\t}";
  protected final String TEXT_435 = NL + "\t\t\t\tif (!stmt.getObject().canAs(com.hp.hpl.jena.rdf.model.Resource.class))" + NL + "\t\t\t\t\treturn;" + NL + "\t\t\t\tcom.hp.hpl.jena.rdf.model.Resource resource = (com.hp.hpl.jena.rdf.model.Resource) stmt.getObject().as(com.hp.hpl.jena.rdf.model.Resource.class);";
  protected final String TEXT_436 = NL + "\t\t\t\tif (_model.contains(resource,RDF.type,";
  protected final String TEXT_437 = ".TYPE)) {";
  protected final String TEXT_438 = NL + "\t\t\t\tif (true) { // don't check resource type if the property range is Resource";
  protected final String TEXT_439 = NL + "\t\t\t\t\t";
  protected final String TEXT_440 = " _";
  protected final String TEXT_441 = " = null;" + NL + "\t\t\t\t\ttry {" + NL + "\t\t\t\t\t\t_";
  protected final String TEXT_442 = " = ";
  protected final String TEXT_443 = ".get";
  protected final String TEXT_444 = "(resource,_model);" + NL + "\t\t\t\t\t} catch (JastorException e) {" + NL + "\t\t\t\t\t\t//e.printStackTrace();" + NL + "\t\t\t\t\t}";
  protected final String TEXT_445 = NL + "\t\t\t\t\tif (";
  protected final String TEXT_446 = " == null) {" + NL + "\t\t\t\t\t\ttry {" + NL + "\t\t\t\t\t\t\tinit";
  protected final String TEXT_447 = "();" + NL + "\t\t\t\t\t\t} catch (JastorException e) {" + NL + "\t\t\t\t\t\t\te.printStackTrace();" + NL + "\t\t\t\t\t\t\treturn;" + NL + "\t\t\t\t\t\t}" + NL + "\t\t\t\t\t}" + NL + "\t\t\t\t\tif (!";
  protected final String TEXT_448 = ".contains(_";
  protected final String TEXT_449 = "))" + NL + "\t\t\t\t\t\t";
  protected final String TEXT_450 = ".add(_";
  protected final String TEXT_451 = ");";
  protected final String TEXT_452 = NL + "\t\t\t\t\tif (listeners != null) {" + NL + "\t\t\t\t\t\tjava.util.ArrayList consumersFor";
  protected final String TEXT_453 = ";" + NL + "\t\t\t\t\t\tsynchronized (listeners) {" + NL + "\t\t\t\t\t\t\tconsumersFor";
  protected final String TEXT_454 = " = (java.util.ArrayList) listeners.clone();" + NL + "\t\t\t\t\t\t}" + NL + "\t\t\t\t\t\tfor(java.util.Iterator iter=consumersFor";
  protected final String TEXT_455 = ".iterator();iter.hasNext();){" + NL + "\t\t\t\t\t\t\t";
  protected final String TEXT_456 = "Listener listener=(";
  protected final String TEXT_457 = "Listener)iter.next();" + NL + "\t\t\t\t\t\t\tlistener.";
  protected final String TEXT_458 = "Added(";
  protected final String TEXT_459 = ".this,_";
  protected final String TEXT_460 = ");" + NL + "\t\t\t\t\t\t}" + NL + "\t\t\t\t\t}";
  protected final String TEXT_461 = NL + "\t\t\t\t}";
  protected final String TEXT_462 = NL + "\t\t\t\treturn;" + NL + "\t\t\t}";
  protected final String TEXT_463 = NL + "\t\t}" + NL + "\t\t" + NL + "\t\tpublic void removedStatement(com.hp.hpl.jena.rdf.model.Statement stmt) {" + NL + "//\t\t\tif (!stmt.getSubject().equals(_resource))" + NL + "//\t\t\t\treturn;";
  protected final String TEXT_464 = NL + "\t\t\tif (stmt.getPredicate().equals(";
  protected final String TEXT_465 = "Property)) {";
  protected final String TEXT_466 = NL + "\t\t\t\tif (!stmt.getObject().canAs(com.hp.hpl.jena.rdf.model.Literal.class))" + NL + "\t\t\t\t\treturn;" + NL + "\t\t\t\tcom.hp.hpl.jena.rdf.model.Literal literal = (com.hp.hpl.jena.rdf.model.Literal) stmt.getObject().as(com.hp.hpl.jena.rdf.model.Literal.class);" + NL + "\t\t\t\t//Object obj = literal.getValue();";
  protected final String TEXT_467 = NL + "\t\t\t\tif (";
  protected final String TEXT_468 = " != null && ";
  protected final String TEXT_469 = ".equals(literal))" + NL + "\t\t\t\t\t";
  protected final String TEXT_470 = " = null;";
  protected final String TEXT_471 = NL + "\t\t\t\tObject obj = Util.fixLiteral(";
  protected final String TEXT_472 = ",literal,\"";
  protected final String TEXT_473 = "\",\"";
  protected final String TEXT_474 = "\");" + NL + "\t\t\t\tif (";
  protected final String TEXT_475 = " != null && ";
  protected final String TEXT_476 = ".equals(obj))" + NL + "\t\t\t\t\t";
  protected final String TEXT_477 = " = null;";
  protected final String TEXT_478 = NL + "\t\t\t\tif (listeners != null) {" + NL + "\t\t\t\t\tjava.util.ArrayList consumers;" + NL + "\t\t\t\t\tsynchronized (listeners) {" + NL + "\t\t\t\t\t\tconsumers = (java.util.ArrayList) listeners.clone();" + NL + "\t\t\t\t\t}" + NL + "\t\t\t\t\tfor(java.util.Iterator iter=consumers.iterator();iter.hasNext();){" + NL + "\t\t\t\t\t\t";
  protected final String TEXT_479 = "Listener listener=(";
  protected final String TEXT_480 = "Listener)iter.next();" + NL + "\t\t\t\t\t\tlistener.";
  protected final String TEXT_481 = "Changed(";
  protected final String TEXT_482 = ".this);" + NL + "\t\t\t\t\t}" + NL + "\t\t\t\t}";
  protected final String TEXT_483 = NL + "\t\t\t\tif (!stmt.getObject().canAs(com.hp.hpl.jena.rdf.model.Literal.class))" + NL + "\t\t\t\t\treturn;" + NL + "\t\t\t\tcom.hp.hpl.jena.rdf.model.Literal literal = (com.hp.hpl.jena.rdf.model.Literal) stmt.getObject().as(com.hp.hpl.jena.rdf.model.Literal.class);" + NL + "\t\t\t\t//Object obj = literal.getValue();";
  protected final String TEXT_484 = NL + "\t\t\t\tif (";
  protected final String TEXT_485 = " != null) {" + NL + "\t\t\t\t\tif (";
  protected final String TEXT_486 = ".contains(literal))" + NL + "\t\t\t\t\t\t";
  protected final String TEXT_487 = ".remove(literal);" + NL + "\t\t\t\t}";
  protected final String TEXT_488 = NL + "\t\t\t\tif (listeners != null) {" + NL + "\t\t\t\t\tjava.util.ArrayList consumersFor";
  protected final String TEXT_489 = ";" + NL + "\t\t\t\t\tsynchronized (listeners) {" + NL + "\t\t\t\t\t\tconsumersFor";
  protected final String TEXT_490 = " = (java.util.ArrayList) listeners.clone();" + NL + "\t\t\t\t\t}" + NL + "\t\t\t\t\tfor(java.util.Iterator iter=consumersFor";
  protected final String TEXT_491 = ".iterator();iter.hasNext();){" + NL + "\t\t\t\t\t\t";
  protected final String TEXT_492 = "Listener listener=(";
  protected final String TEXT_493 = "Listener)iter.next();" + NL + "\t\t\t\t\t\tlistener.";
  protected final String TEXT_494 = "Removed(";
  protected final String TEXT_495 = ".this,literal);" + NL + "\t\t\t\t\t}" + NL + "\t\t\t\t}";
  protected final String TEXT_496 = NL + "\t\t\t\tObject obj = Util.fixLiteral(";
  protected final String TEXT_497 = ",literal,\"";
  protected final String TEXT_498 = "\",\"";
  protected final String TEXT_499 = "\");";
  protected final String TEXT_500 = NL + "\t\t\t\tif (";
  protected final String TEXT_501 = " != null) {" + NL + "\t\t\t\t\tif (";
  protected final String TEXT_502 = ".contains(obj))" + NL + "\t\t\t\t\t\t";
  protected final String TEXT_503 = ".remove(obj);" + NL + "\t\t\t\t}";
  protected final String TEXT_504 = NL + "\t\t\t\tif (listeners != null) {" + NL + "\t\t\t\t\tjava.util.ArrayList consumers;" + NL + "\t\t\t\t\tsynchronized (listeners) {" + NL + "\t\t\t\t\t\tconsumers = (java.util.ArrayList) listeners.clone();" + NL + "\t\t\t\t\t}" + NL + "\t\t\t\t\tfor(java.util.Iterator iter=consumers.iterator();iter.hasNext();){" + NL + "\t\t\t\t\t\t";
  protected final String TEXT_505 = "Listener listener=(";
  protected final String TEXT_506 = "Listener)iter.next();" + NL + "\t\t\t\t\t\tlistener.";
  protected final String TEXT_507 = "Removed(";
  protected final String TEXT_508 = ".this,(";
  protected final String TEXT_509 = ")obj);" + NL + "\t\t\t\t\t}" + NL + "\t\t\t\t}";
  protected final String TEXT_510 = NL + "\t\t\t\tif (!stmt.getObject().canAs(com.hp.hpl.jena.rdf.model.Resource.class))" + NL + "\t\t\t\t\treturn;" + NL + "\t\t\t\tcom.hp.hpl.jena.rdf.model.Resource resource = (com.hp.hpl.jena.rdf.model.Resource) stmt.getObject().as(com.hp.hpl.jena.rdf.model.Resource.class);";
  protected final String TEXT_511 = NL + "\t\t\t\t\tif (";
  protected final String TEXT_512 = " != null && ";
  protected final String TEXT_513 = ".resource().equals(resource))" + NL + "\t\t\t\t\t\t";
  protected final String TEXT_514 = " = null;\t\t\t\t";
  protected final String TEXT_515 = NL + "\t\t\t\tif (listeners != null) {" + NL + "\t\t\t\t\tjava.util.ArrayList consumers;" + NL + "\t\t\t\t\tsynchronized (listeners) {" + NL + "\t\t\t\t\t\tconsumers = (java.util.ArrayList) listeners.clone();" + NL + "\t\t\t\t\t}" + NL + "\t\t\t\t\tfor(java.util.Iterator iter=consumers.iterator();iter.hasNext();){" + NL + "\t\t\t\t\t\t";
  protected final String TEXT_516 = "Listener listener=(";
  protected final String TEXT_517 = "Listener)iter.next();" + NL + "\t\t\t\t\t\tlistener.";
  protected final String TEXT_518 = "Changed(";
  protected final String TEXT_519 = ".this);" + NL + "\t\t\t\t\t}" + NL + "\t\t\t\t}";
  protected final String TEXT_520 = NL + "\t\t\t\tif (!stmt.getObject().canAs(com.hp.hpl.jena.rdf.model.Resource.class))" + NL + "\t\t\t\t\treturn;" + NL + "\t\t\t\tcom.hp.hpl.jena.rdf.model.Resource resource = (com.hp.hpl.jena.rdf.model.Resource) stmt.getObject().as(com.hp.hpl.jena.rdf.model.Resource.class);";
  protected final String TEXT_521 = NL + "\t\t\t\tif (_model.contains(resource,RDF.type,";
  protected final String TEXT_522 = ".TYPE)) {";
  protected final String TEXT_523 = NL + "\t\t\t\tif (true) { // don't check resource type if the property range is Resource";
  protected final String TEXT_524 = NL + "\t\t\t\t\t";
  protected final String TEXT_525 = " _";
  protected final String TEXT_526 = " = null;" + NL + "\t\t\t\t\tif (";
  protected final String TEXT_527 = " != null) {" + NL + "\t\t\t\t\t\tboolean found = false;" + NL + "\t\t\t\t\t\tfor (int i=0;i<";
  protected final String TEXT_528 = ".size();i++) {" + NL + "\t\t\t\t\t\t\t";
  protected final String TEXT_529 = " __item = (";
  protected final String TEXT_530 = ") ";
  protected final String TEXT_531 = ".get(i);" + NL + "\t\t\t\t\t\t\tif (__item.resource().equals(resource)) {" + NL + "\t\t\t\t\t\t\t\tfound = true;" + NL + "\t\t\t\t\t\t\t\t_";
  protected final String TEXT_532 = " = __item;" + NL + "\t\t\t\t\t\t\t\tbreak;" + NL + "\t\t\t\t\t\t\t}" + NL + "\t\t\t\t\t\t}" + NL + "\t\t\t\t\t\tif (found)" + NL + "\t\t\t\t\t\t\t";
  protected final String TEXT_533 = ".remove(_";
  protected final String TEXT_534 = ");" + NL + "\t\t\t\t\t\telse {" + NL + "\t\t\t\t\t\t\ttry {" + NL + "\t\t\t\t\t\t\t\t_";
  protected final String TEXT_535 = " = ";
  protected final String TEXT_536 = ".get";
  protected final String TEXT_537 = "(resource,_model);" + NL + "\t\t\t\t\t\t\t} catch (JastorException e) {" + NL + "\t\t\t\t\t\t\t}" + NL + "\t\t\t\t\t\t}" + NL + "\t\t\t\t\t} else {" + NL + "\t\t\t\t\t\ttry {" + NL + "\t\t\t\t\t\t\t_";
  protected final String TEXT_538 = " = ";
  protected final String TEXT_539 = ".get";
  protected final String TEXT_540 = "(resource,_model);" + NL + "\t\t\t\t\t\t} catch (JastorException e) {" + NL + "\t\t\t\t\t\t}" + NL + "\t\t\t\t\t}";
  protected final String TEXT_541 = NL + "\t\t\t\t\t";
  protected final String TEXT_542 = " _";
  protected final String TEXT_543 = " = null;" + NL + "\t\t\t\t\ttry {" + NL + "\t\t\t\t\t\t_";
  protected final String TEXT_544 = " = ";
  protected final String TEXT_545 = ".get";
  protected final String TEXT_546 = "(resource,_model);" + NL + "\t\t\t\t\t} catch (JastorException e) {" + NL + "\t\t\t\t\t}";
  protected final String TEXT_547 = NL + "\t\t\t\t\tif (listeners != null) {" + NL + "\t\t\t\t\t\tjava.util.ArrayList consumersFor";
  protected final String TEXT_548 = ";" + NL + "\t\t\t\t\t\tsynchronized (listeners) {" + NL + "\t\t\t\t\t\t\tconsumersFor";
  protected final String TEXT_549 = " = (java.util.ArrayList) listeners.clone();" + NL + "\t\t\t\t\t\t}" + NL + "\t\t\t\t\t\tfor(java.util.Iterator iter=consumersFor";
  protected final String TEXT_550 = ".iterator();iter.hasNext();){" + NL + "\t\t\t\t\t\t\t";
  protected final String TEXT_551 = "Listener listener=(";
  protected final String TEXT_552 = "Listener)iter.next();" + NL + "\t\t\t\t\t\t\tlistener.";
  protected final String TEXT_553 = "Removed(";
  protected final String TEXT_554 = ".this,_";
  protected final String TEXT_555 = ");" + NL + "\t\t\t\t\t\t}" + NL + "\t\t\t\t\t}";
  protected final String TEXT_556 = NL + "\t\t\t\t}";
  protected final String TEXT_557 = NL + "\t\t\t\treturn;" + NL + "\t\t\t}";
  protected final String TEXT_558 = NL + "\t\t}" + NL + "" + NL + "\t//}" + NL + "\t";
  protected final String TEXT_559 = NL + NL + NL + "}";

	public static final String copyright = "(C) Copyright IBM Corporation 2005  All Rights Reserved.";
	
	OntologyClassFileProvider fileProvider;

	public ImplementationTemplate(OntologyClassFileProvider fileProvider) {
		this.fileProvider = fileProvider;
	}
	
	public ImplementationTemplate() {
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
    stringBuffer.append(oc.getFactoryFullClassname());
    stringBuffer.append(TEXT_5);
    stringBuffer.append(oc.getURI() );
    stringBuffer.append(TEXT_6);
    stringBuffer.append(oc.getImplClassname());
    stringBuffer.append(TEXT_7);
    stringBuffer.append(ctx.getThingImpl().getName());
    stringBuffer.append(TEXT_8);
    stringBuffer.append(oc.getInterfaceFullClassname());
    stringBuffer.append(TEXT_9);
     for (java.util.Iterator iter = oc.listProperties(true).iterator(); iter.hasNext();) {
     		OntologyProperty prop = (OntologyProperty)iter.next(); 
     // have to add the properties here to so we have no ambiguity with multiple inheritance 
    stringBuffer.append(TEXT_10);
    stringBuffer.append(prop.getPropertyName());
    stringBuffer.append(TEXT_11);
    stringBuffer.append(prop.getURI());
    stringBuffer.append(TEXT_12);
     	if (ctx.isGeneratePropertyCache()) { 
           if (prop.isSingleValued()) { 
    				for (java.util.Iterator iter2 = prop.listAllRanges();iter2.hasNext();) { 
        			Resource res = (Resource)iter2.next();
    stringBuffer.append(TEXT_13);
    stringBuffer.append(prop.getReturnType(res));
    stringBuffer.append(TEXT_14);
    stringBuffer.append(prop.getPropertyName(res));
    stringBuffer.append(TEXT_15);
       			} 
           } 
           if (prop.isMultiValued() && prop.isDatatypeProperty()) { 
    				for (java.util.Iterator iter2 = prop.listAllRanges();iter2.hasNext();) { 
        			Resource res = (Resource)iter2.next();
    stringBuffer.append(TEXT_16);
    stringBuffer.append(prop.getPropertyName(res));
    stringBuffer.append(TEXT_17);
       			} 
           } 
           if (prop.isMultiValued() && prop.isObjectProperty()) { 
    				for (java.util.Iterator iter2 = prop.listAllRanges();iter2.hasNext();) { 
        			Resource res = (Resource)iter2.next();
    stringBuffer.append(TEXT_18);
    stringBuffer.append(prop.getPropertyName(res));
    stringBuffer.append(TEXT_19);
       			} 
           } 
     	} 
     } 
    stringBuffer.append(TEXT_20);
     if (oc.isEnumeratedClass()) { 
    stringBuffer.append(TEXT_21);
     Iterator oneOfClassesItr = oc.listOneOfClasses().iterator(); 
       while (oneOfClassesItr.hasNext()) { 
    		Resource oneOfClass = (Resource)oneOfClassesItr.next(); 
    stringBuffer.append(TEXT_22);
    stringBuffer.append(oc.getIndividualIdentifierName(oneOfClass));
    stringBuffer.append(TEXT_23);
       } 
    stringBuffer.append(TEXT_24);
     } 
    stringBuffer.append(TEXT_25);
    stringBuffer.append(oc.getImplClassname());
    stringBuffer.append(TEXT_26);
     if (ctx.isGeneratePropertyCache()) { 
    stringBuffer.append(TEXT_27);
     } 
    stringBuffer.append(TEXT_28);
    stringBuffer.append(oc.getImplClassname());
    stringBuffer.append(TEXT_29);
    stringBuffer.append(oc.getInterfaceClassname());
    stringBuffer.append(TEXT_30);
     if (ctx.isUseStrictTypeChecking()) { 
    stringBuffer.append(TEXT_31);
    stringBuffer.append(oc.getInterfaceClassname());
    stringBuffer.append(TEXT_32);
     } 
    stringBuffer.append(TEXT_33);
    stringBuffer.append(oc.getImplClassname());
    stringBuffer.append(TEXT_34);
    stringBuffer.append(oc.getImplClassname());
    stringBuffer.append(TEXT_35);
    stringBuffer.append(oc.getInterfaceClassname());
    stringBuffer.append(TEXT_36);
     if (oc.isEnumeratedClass()) { 
    stringBuffer.append(TEXT_37);
    stringBuffer.append(oc.getURI());
    stringBuffer.append(TEXT_38);
     } 
    stringBuffer.append(TEXT_39);
    stringBuffer.append(oc.getImplClassname());
    stringBuffer.append(TEXT_40);
    stringBuffer.append(oc.getImplClassname());
    stringBuffer.append(TEXT_41);
    stringBuffer.append(oc.getInterfaceClassname());
    stringBuffer.append(TEXT_42);
    stringBuffer.append(oc.getInterfaceClassname());
    stringBuffer.append(TEXT_43);
    	if (ctx.isAddAllRDFTypesInHierarchy()) { 
    stringBuffer.append(TEXT_44);
    	}
    stringBuffer.append(TEXT_45);
    	java.util.Iterator superit = oc.listAllExtensionClasses().iterator(); 
    	while (superit.hasNext()) { 
    		OntologyClass ocl = (OntologyClass)superit.next(); 
    stringBuffer.append(TEXT_46);
    stringBuffer.append(ocl.getInterfaceFullClassname());
    stringBuffer.append(TEXT_47);
    stringBuffer.append(ocl.getInterfaceFullClassname());
    stringBuffer.append(TEXT_48);
    	} 
    stringBuffer.append(TEXT_49);
     	for (java.util.Iterator iter = oc.listProperties(true).iterator(); iter.hasNext();) {
     		OntologyProperty prop = (OntologyProperty)iter.next(); 
    			List list = prop.getHasValueValues(); 
    			for (int i=0;i<list.size();i++) { 
    				RDFNode node = (RDFNode)list.get(i); 
    				if (prop.isObjectProperty()) { 
    stringBuffer.append(TEXT_50);
    stringBuffer.append(prop.getPropertyName());
    stringBuffer.append(TEXT_51);
    stringBuffer.append(node);
    stringBuffer.append(TEXT_52);
    stringBuffer.append(prop.getPropertyName());
    stringBuffer.append(TEXT_53);
    stringBuffer.append(node);
    stringBuffer.append(TEXT_54);
    				} else { 
    stringBuffer.append(TEXT_55);
    stringBuffer.append(prop.getPropertyName());
    stringBuffer.append(TEXT_56);
    stringBuffer.append(node);
    stringBuffer.append(TEXT_57);
    stringBuffer.append(prop.getPropertyName());
    stringBuffer.append(TEXT_58);
    stringBuffer.append(node);
    stringBuffer.append(TEXT_59);
    				} 
    			} 
    		} 
    stringBuffer.append(TEXT_60);
     if (ctx.isGeneratePropertyCache() || ctx.isGenerateListeners()) { 
    stringBuffer.append(TEXT_61);
    		if (ctx.isGenerateListeners()) { 
    stringBuffer.append(TEXT_62);
    		} 
    stringBuffer.append(TEXT_63);
    stringBuffer.append(oc.getFactoryFullClassname());
    stringBuffer.append(TEXT_64);
     } 
    stringBuffer.append(TEXT_65);
     for (java.util.Iterator iter = oc.listProperties(true).iterator(); iter.hasNext();) {
     	OntologyProperty prop = (OntologyProperty)iter.next(); 
    stringBuffer.append(TEXT_66);
    stringBuffer.append(prop.getPropertyName());
    stringBuffer.append(TEXT_67);
     } 
    stringBuffer.append(TEXT_68);
    stringBuffer.append(oc.getInterfaceFullClassname());
    stringBuffer.append(TEXT_69);
    	for (java.util.Iterator iter = oc.listAllExtensionClasses().iterator(); iter.hasNext();) { 
    		OntologyClass ocl = (OntologyClass)iter.next(); 
    stringBuffer.append(TEXT_70);
    stringBuffer.append(ocl.getInterfaceFullClassname());
    stringBuffer.append(TEXT_71);
    	} 
    stringBuffer.append(TEXT_72);
     if (ctx.isGeneratePropertyCache()) { 
    stringBuffer.append(TEXT_73);
     	for (java.util.Iterator iter = oc.listProperties(true).iterator(); iter.hasNext();) {
     	OntologyProperty prop = (OntologyProperty)iter.next(); 
    		for (java.util.Iterator iter2 = prop.listAllRanges();iter2.hasNext();) { 
              Resource res = (Resource)iter2.next();
    stringBuffer.append(TEXT_74);
    stringBuffer.append(prop.getPropertyName(res));
    stringBuffer.append(TEXT_75);
    		} 
     	} 
    stringBuffer.append(TEXT_76);
     } 
    stringBuffer.append(TEXT_77);
     if (ctx.isUseTypedLiterals()) { 
    stringBuffer.append(TEXT_78);
     } else { 
    stringBuffer.append(TEXT_79);
     } 
    stringBuffer.append(TEXT_80);
     for (java.util.Iterator iter = oc.listProperties(true).iterator(); iter.hasNext();) {
     	OntologyProperty prop = (OntologyProperty)iter.next(); 
    		if (prop.isSingleValued() && prop.isDatatypeProperty()) { 
    			for (java.util.Iterator iter2 = prop.listAllRanges();iter2.hasNext();) { 
              Resource res = (Resource)iter2.next();
    stringBuffer.append(TEXT_81);
    stringBuffer.append(prop.getReturnType(res));
    stringBuffer.append(TEXT_82);
    stringBuffer.append(prop.getPropertyCapped(res));
    stringBuffer.append(TEXT_83);
     			if (ctx.isGeneratePropertyCache()) { 
    stringBuffer.append(TEXT_84);
    stringBuffer.append(prop.getPropertyName(res));
    stringBuffer.append(TEXT_85);
    stringBuffer.append(prop.getPropertyName(res));
    stringBuffer.append(TEXT_86);
     			} 
    stringBuffer.append(TEXT_87);
    stringBuffer.append(prop.getPropertyName());
    stringBuffer.append(TEXT_88);
    stringBuffer.append(prop.getPropertyName(res));
    stringBuffer.append(TEXT_89);
    stringBuffer.append(oc.getInterfaceFullClassname());
    stringBuffer.append(TEXT_90);
     			if (prop.getReturnType(res).equals("com.hp.hpl.jena.rdf.model.Literal")) { 
     				if (ctx.isGeneratePropertyCache()) { 
    stringBuffer.append(TEXT_91);
    stringBuffer.append(prop.getPropertyName(res));
    stringBuffer.append(TEXT_92);
    				} 
    stringBuffer.append(TEXT_93);
     			} else { 
    stringBuffer.append(TEXT_94);
    stringBuffer.append(ctx.isUseTypedLiterals());
    stringBuffer.append(TEXT_95);
    stringBuffer.append(prop.getReturnType(res));
    stringBuffer.append(TEXT_96);
    stringBuffer.append(prop.getRangeURI(res));
    stringBuffer.append(TEXT_97);
     				if (ctx.isGeneratePropertyCache())  { 
    stringBuffer.append(TEXT_98);
    stringBuffer.append(prop.getPropertyName(res));
    stringBuffer.append(TEXT_99);
    stringBuffer.append(prop.getReturnType(res));
    stringBuffer.append(TEXT_100);
    stringBuffer.append(prop.getPropertyName(res));
    stringBuffer.append(TEXT_101);
    				} else { 
    stringBuffer.append(TEXT_102);
    stringBuffer.append(prop.getReturnType(res));
    stringBuffer.append(TEXT_103);
    				} 
     			} 
    stringBuffer.append(TEXT_104);
    stringBuffer.append(prop.getPropertyCapped());
    stringBuffer.append(TEXT_105);
    stringBuffer.append(prop.getReturnType(res));
    stringBuffer.append(TEXT_106);
    stringBuffer.append(prop.getPropertyName());
    stringBuffer.append(TEXT_107);
    stringBuffer.append(prop.getPropertyName());
    stringBuffer.append(TEXT_108);
    stringBuffer.append(prop.getPropertyName());
    stringBuffer.append(TEXT_109);
     			if (ctx.isGeneratePropertyCache()) { 
    stringBuffer.append(TEXT_110);
    stringBuffer.append(prop.getPropertyName(res));
    stringBuffer.append(TEXT_111);
    stringBuffer.append(prop.getPropertyName());
    stringBuffer.append(TEXT_112);
     			} 
    stringBuffer.append(TEXT_113);
    stringBuffer.append(prop.getPropertyName());
    stringBuffer.append(TEXT_114);
     			if (prop.getReturnType(res).equals("com.hp.hpl.jena.rdf.model.Literal")) {
    stringBuffer.append(TEXT_115);
    stringBuffer.append(prop.getPropertyName());
    stringBuffer.append(TEXT_116);
    stringBuffer.append(prop.getPropertyName());
    stringBuffer.append(TEXT_117);
     			} else { 
    stringBuffer.append(TEXT_118);
    stringBuffer.append(prop.getPropertyName());
    stringBuffer.append(TEXT_119);
    stringBuffer.append(prop.getPropertyName());
    stringBuffer.append(TEXT_120);
    				} 
    stringBuffer.append(TEXT_121);
           } // end allRanges 
       } // end single-data 
    stringBuffer.append(TEXT_122);
    	  if (prop.isMultiValued() && prop.isDatatypeProperty()) { 
    			for (java.util.Iterator iter2 = prop.listAllRanges();iter2.hasNext();) { 
              Resource res = (Resource)iter2.next();
     			if (ctx.isGeneratePropertyCache()) { 
    stringBuffer.append(TEXT_123);
    stringBuffer.append(prop.getPropertyCapped(res));
    stringBuffer.append(TEXT_124);
    stringBuffer.append(prop.getPropertyName(res));
    stringBuffer.append(TEXT_125);
    stringBuffer.append(prop.getPropertyName());
    stringBuffer.append(TEXT_126);
    stringBuffer.append(prop.getURI());
    stringBuffer.append(TEXT_127);
    stringBuffer.append(oc.getInterfaceClassname());
    stringBuffer.append(TEXT_128);
     				if (prop.getReturnType(res).equals("com.hp.hpl.jena.rdf.model.Literal")) { 
    stringBuffer.append(TEXT_129);
    stringBuffer.append(prop.getPropertyName(res));
    stringBuffer.append(TEXT_130);
                 } else { 
    stringBuffer.append(TEXT_131);
    stringBuffer.append(ctx.isUseTypedLiterals());
    stringBuffer.append(TEXT_132);
    stringBuffer.append(prop.getReturnType(res));
    stringBuffer.append(TEXT_133);
    stringBuffer.append(prop.getRangeURI(res));
    stringBuffer.append(TEXT_134);
    stringBuffer.append(prop.getPropertyName(res));
    stringBuffer.append(TEXT_135);
                 } 
    stringBuffer.append(TEXT_136);
     			}  
    stringBuffer.append(TEXT_137);
    stringBuffer.append(prop.getPropertyCapped(res));
    stringBuffer.append(TEXT_138);
     			if (ctx.isGeneratePropertyCache()) { 
    stringBuffer.append(TEXT_139);
    stringBuffer.append(prop.getPropertyName(res));
    stringBuffer.append(TEXT_140);
    stringBuffer.append(prop.getPropertyCapped(res));
    stringBuffer.append(TEXT_141);
    stringBuffer.append(prop.getPropertyName(res));
    stringBuffer.append(TEXT_142);
    stringBuffer.append(prop.getPropertyName());
    stringBuffer.append(TEXT_143);
     			} else { 
    stringBuffer.append(TEXT_144);
    stringBuffer.append(prop.getPropertyName());
    stringBuffer.append(TEXT_145);
    stringBuffer.append(prop.getRangeURI(res));
    stringBuffer.append(TEXT_146);
     				if (prop.getReturnType(res).equals("com.hp.hpl.jena.rdf.model.Literal")) { 
    stringBuffer.append(TEXT_147);
                  } else { 
    stringBuffer.append(TEXT_148);
    stringBuffer.append(ctx.isUseTypedLiterals());
    stringBuffer.append(TEXT_149);
    stringBuffer.append(prop.getReturnType(res));
    stringBuffer.append(TEXT_150);
    stringBuffer.append(prop.getRangeURI(res));
    stringBuffer.append(TEXT_151);
    			    } 
    stringBuffer.append(TEXT_152);
              } 
    stringBuffer.append(TEXT_153);
    stringBuffer.append(prop.getPropertyCapped());
    stringBuffer.append(TEXT_154);
    stringBuffer.append(prop.getReturnType(res));
    stringBuffer.append(TEXT_155);
    stringBuffer.append(prop.getPropertyName());
    stringBuffer.append(TEXT_156);
     			if (ctx.isGeneratePropertyCache()) { 
    stringBuffer.append(TEXT_157);
    stringBuffer.append(prop.getPropertyName(res));
    stringBuffer.append(TEXT_158);
    stringBuffer.append(prop.getPropertyCapped(res));
    stringBuffer.append(TEXT_159);
    stringBuffer.append(prop.getPropertyName(res));
    stringBuffer.append(TEXT_160);
    stringBuffer.append(prop.getPropertyName());
    stringBuffer.append(TEXT_161);
    				} 
    stringBuffer.append(TEXT_162);
    stringBuffer.append(prop.getPropertyName());
    stringBuffer.append(TEXT_163);
    stringBuffer.append(prop.getPropertyName());
    stringBuffer.append(TEXT_164);
     			if (ctx.isGeneratePropertyCache()) { 
    stringBuffer.append(TEXT_165);
    stringBuffer.append(prop.getPropertyName(res));
    stringBuffer.append(TEXT_166);
    stringBuffer.append(prop.getPropertyName());
    stringBuffer.append(TEXT_167);
    				} 
     			if (prop.getReturnType(res).equals("com.hp.hpl.jena.rdf.model.Literal")) {
    stringBuffer.append(TEXT_168);
    stringBuffer.append(prop.getPropertyName());
    stringBuffer.append(TEXT_169);
    stringBuffer.append(prop.getPropertyName());
    stringBuffer.append(TEXT_170);
     			} else { 
    stringBuffer.append(TEXT_171);
    stringBuffer.append(prop.getPropertyName());
    stringBuffer.append(TEXT_172);
    stringBuffer.append(prop.getPropertyName());
    stringBuffer.append(TEXT_173);
    			} 
    stringBuffer.append(TEXT_174);
    stringBuffer.append(prop.getPropertyCapped());
    stringBuffer.append(TEXT_175);
    stringBuffer.append(prop.getReturnType(res));
    stringBuffer.append(TEXT_176);
    stringBuffer.append(prop.getPropertyName());
    stringBuffer.append(TEXT_177);
     			if (ctx.isGeneratePropertyCache()) { 
    stringBuffer.append(TEXT_178);
    stringBuffer.append(prop.getPropertyName(res));
    stringBuffer.append(TEXT_179);
    stringBuffer.append(prop.getPropertyCapped(res));
    stringBuffer.append(TEXT_180);
    stringBuffer.append(prop.getPropertyName(res));
    stringBuffer.append(TEXT_181);
    stringBuffer.append(prop.getPropertyName());
    stringBuffer.append(TEXT_182);
    				} 
    stringBuffer.append(TEXT_183);
    stringBuffer.append(prop.getPropertyName());
    stringBuffer.append(TEXT_184);
    stringBuffer.append(prop.getPropertyName());
    stringBuffer.append(TEXT_185);
     			if (ctx.isGeneratePropertyCache()) { 
    stringBuffer.append(TEXT_186);
    stringBuffer.append(prop.getPropertyName(res));
    stringBuffer.append(TEXT_187);
    stringBuffer.append(prop.getPropertyName());
    stringBuffer.append(TEXT_188);
    				} 
    stringBuffer.append(TEXT_189);
    stringBuffer.append(prop.getPropertyName());
    stringBuffer.append(TEXT_190);
    stringBuffer.append(prop.getPropertyName());
    stringBuffer.append(TEXT_191);
       		} 
       } 
    	 if (prop.isSingleValued() && prop.isObjectProperty()) { 
    			for (java.util.Iterator iter2 = prop.listAllRanges();iter2.hasNext();) { 
              Resource res = (Resource)iter2.next();
    stringBuffer.append(TEXT_192);
    stringBuffer.append(prop.getReturnType(res));
    stringBuffer.append(TEXT_193);
    stringBuffer.append(prop.getPropertyCapped(res));
    stringBuffer.append(TEXT_194);
     			if (ctx.isGeneratePropertyCache()) { 
    stringBuffer.append(TEXT_195);
    stringBuffer.append(prop.getPropertyName(res));
    stringBuffer.append(TEXT_196);
    stringBuffer.append(prop.getPropertyName(res));
    stringBuffer.append(TEXT_197);
     			} 
    stringBuffer.append(TEXT_198);
    stringBuffer.append(prop.getPropertyName());
    stringBuffer.append(TEXT_199);
    stringBuffer.append(prop.getPropertyName(res));
    stringBuffer.append(TEXT_200);
    stringBuffer.append(oc.getInterfaceFullClassname());
    stringBuffer.append(TEXT_201);
              if (res != OntologyProperty.DEFAULT_RANGE && !prop.getRangeOntologyClass(res).getURI().equals(com.hp.hpl.jena.vocabulary.RDFS.Resource.getURI())) { 
    stringBuffer.append(TEXT_202);
    stringBuffer.append(prop.getRangeOntologyClass(res).getInterfaceFullClassname());
    stringBuffer.append(TEXT_203);
    			} 
     			if (ctx.isGeneratePropertyCache())  { 
    stringBuffer.append(TEXT_204);
    stringBuffer.append(prop.getPropertyName(res));
    stringBuffer.append(TEXT_205);
    stringBuffer.append(prop.getRangeOntologyClass(res).getFactoryFullClassname());
    stringBuffer.append(TEXT_206);
    stringBuffer.append(prop.getRangeOntologyClass(res).getInterfaceClassname());
    stringBuffer.append(TEXT_207);
    stringBuffer.append(prop.getPropertyName(res));
    stringBuffer.append(TEXT_208);
    			} else { 
    stringBuffer.append(TEXT_209);
    stringBuffer.append(prop.getRangeOntologyClass(res).getFactoryFullClassname());
    stringBuffer.append(TEXT_210);
    stringBuffer.append(prop.getRangeOntologyClass(res).getInterfaceClassname());
    stringBuffer.append(TEXT_211);
    			} 
    stringBuffer.append(TEXT_212);
    stringBuffer.append(prop.getPropertyCapped());
    stringBuffer.append(TEXT_213);
    stringBuffer.append(prop.getReturnType(res));
    stringBuffer.append(TEXT_214);
    stringBuffer.append(prop.getPropertyName());
    stringBuffer.append(TEXT_215);
    stringBuffer.append(prop.getPropertyName());
    stringBuffer.append(TEXT_216);
    stringBuffer.append(prop.getPropertyName());
    stringBuffer.append(TEXT_217);
     			if (ctx.isGeneratePropertyCache()) { 
    stringBuffer.append(TEXT_218);
    stringBuffer.append(prop.getPropertyName(res));
    stringBuffer.append(TEXT_219);
    stringBuffer.append(prop.getPropertyName());
    stringBuffer.append(TEXT_220);
     			} 
    stringBuffer.append(TEXT_221);
    stringBuffer.append(prop.getPropertyName());
    stringBuffer.append(TEXT_222);
    stringBuffer.append(prop.getPropertyName());
    stringBuffer.append(TEXT_223);
    stringBuffer.append(prop.getPropertyName());
    stringBuffer.append(TEXT_224);
    stringBuffer.append(prop.getReturnType(res));
    stringBuffer.append(TEXT_225);
    stringBuffer.append(prop.getPropertyCapped(res));
    stringBuffer.append(TEXT_226);
    stringBuffer.append(prop.getPropertyName());
    stringBuffer.append(TEXT_227);
    stringBuffer.append(prop.getPropertyName());
    stringBuffer.append(TEXT_228);
    stringBuffer.append(prop.getReturnType(res));
    stringBuffer.append(TEXT_229);
    stringBuffer.append(prop.getPropertyName());
    stringBuffer.append(TEXT_230);
    stringBuffer.append(prop.getRangeOntologyClass(res).getFactoryFullClassname());
    stringBuffer.append(TEXT_231);
    stringBuffer.append(prop.getRangeOntologyClass(res).getInterfaceClassname());
    stringBuffer.append(TEXT_232);
     			if (ctx.isGeneratePropertyCache()) { 
    stringBuffer.append(TEXT_233);
    stringBuffer.append(prop.getPropertyName(res));
    stringBuffer.append(TEXT_234);
    stringBuffer.append(prop.getPropertyName());
    stringBuffer.append(TEXT_235);
     			} 
    stringBuffer.append(TEXT_236);
    stringBuffer.append(prop.getPropertyName());
    stringBuffer.append(TEXT_237);
    stringBuffer.append(prop.getPropertyName());
    stringBuffer.append(TEXT_238);
    stringBuffer.append(prop.getPropertyName());
    stringBuffer.append(TEXT_239);
    stringBuffer.append(prop.getReturnType(res));
    stringBuffer.append(TEXT_240);
    stringBuffer.append(prop.getPropertyCapped(res));
    stringBuffer.append(TEXT_241);
     			if (ctx.isUseStrictTypeChecking() && !prop.getRangeOntologyClass(res).getURI().equals(com.hp.hpl.jena.vocabulary.RDFS.Resource.getURI())) { 
    stringBuffer.append(TEXT_242);
    stringBuffer.append(prop.getRangeOntologyClass(res).getInterfaceFullClassname());
    stringBuffer.append(TEXT_243);
    stringBuffer.append(prop.getRangeOntologyClass(res).getInterfaceFullClassname());
    stringBuffer.append(TEXT_244);
     			} 
    stringBuffer.append(TEXT_245);
    stringBuffer.append(prop.getPropertyName());
    stringBuffer.append(TEXT_246);
    stringBuffer.append(prop.getPropertyName());
    stringBuffer.append(TEXT_247);
    stringBuffer.append(prop.getReturnType(res));
    stringBuffer.append(TEXT_248);
    stringBuffer.append(prop.getPropertyName());
    stringBuffer.append(TEXT_249);
    stringBuffer.append(prop.getRangeOntologyClass(res).getFactoryFullClassname());
    stringBuffer.append(TEXT_250);
    stringBuffer.append(prop.getRangeOntologyClass(res).getInterfaceClassname());
    stringBuffer.append(TEXT_251);
     			if (ctx.isGeneratePropertyCache()) { 
    stringBuffer.append(TEXT_252);
    stringBuffer.append(prop.getPropertyName(res));
    stringBuffer.append(TEXT_253);
    stringBuffer.append(prop.getPropertyName());
    stringBuffer.append(TEXT_254);
     			} 
    stringBuffer.append(TEXT_255);
    stringBuffer.append(prop.getPropertyName());
    stringBuffer.append(TEXT_256);
    stringBuffer.append(prop.getPropertyName());
    stringBuffer.append(TEXT_257);
    stringBuffer.append(prop.getPropertyName());
    stringBuffer.append(TEXT_258);
           } 
      } 
    	 if (prop.isMultiValued() && prop.isObjectProperty()) { 
    			for (java.util.Iterator iter2 = prop.listAllRanges();iter2.hasNext();) { 
              Resource res = (Resource)iter2.next();
     			if (ctx.isGeneratePropertyCache()) { 
    stringBuffer.append(TEXT_259);
    stringBuffer.append(prop.getPropertyCapped(res));
    stringBuffer.append(TEXT_260);
    stringBuffer.append(prop.getPropertyName(res));
    stringBuffer.append(TEXT_261);
    stringBuffer.append(prop.getPropertyName());
    stringBuffer.append(TEXT_262);
    stringBuffer.append(prop.getURI());
    stringBuffer.append(TEXT_263);
    stringBuffer.append(oc.getInterfaceClassname());
    stringBuffer.append(TEXT_264);
              	if (res != OntologyProperty.DEFAULT_RANGE && !prop.getRangeOntologyClass(res).getURI().equals(com.hp.hpl.jena.vocabulary.RDFS.Resource.getURI())) { 
    stringBuffer.append(TEXT_265);
    stringBuffer.append(prop.getRangeOntologyClass(res).getInterfaceFullClassname());
    stringBuffer.append(TEXT_266);
    				} else { 
    stringBuffer.append(TEXT_267);
    			    } 
    stringBuffer.append(TEXT_268);
    stringBuffer.append(prop.getReturnType(res));
    stringBuffer.append(TEXT_269);
    stringBuffer.append(prop.getPropertyName());
    stringBuffer.append(TEXT_270);
    stringBuffer.append(prop.getRangeOntologyClass(res).getFactoryFullClassname());
    stringBuffer.append(TEXT_271);
    stringBuffer.append(prop.getRangeOntologyClass(res).getInterfaceClassname());
    stringBuffer.append(TEXT_272);
    stringBuffer.append(prop.getPropertyName(res));
    stringBuffer.append(TEXT_273);
    stringBuffer.append(prop.getPropertyName());
    stringBuffer.append(TEXT_274);
     			} 
    stringBuffer.append(TEXT_275);
    stringBuffer.append(prop.getPropertyCapped(res));
    stringBuffer.append(TEXT_276);
     			if (ctx.isGeneratePropertyCache()) { 
    stringBuffer.append(TEXT_277);
    stringBuffer.append(prop.getPropertyName(res));
    stringBuffer.append(TEXT_278);
    stringBuffer.append(prop.getPropertyCapped(res));
    stringBuffer.append(TEXT_279);
    stringBuffer.append(prop.getPropertyName(res));
    stringBuffer.append(TEXT_280);
    stringBuffer.append(prop.getPropertyName());
    stringBuffer.append(TEXT_281);
     			} else { 
    				if (res != OntologyProperty.DEFAULT_RANGE) { 
    stringBuffer.append(TEXT_282);
    stringBuffer.append(prop.getPropertyName());
    stringBuffer.append(TEXT_283);
    stringBuffer.append(prop.getRangeOntologyClass(res).getInterfaceFullClassname());
    stringBuffer.append(TEXT_284);
    				} else  { 
    stringBuffer.append(TEXT_285);
    stringBuffer.append(prop.getPropertyName());
    stringBuffer.append(TEXT_286);
    		        } 
    stringBuffer.append(TEXT_287);
    stringBuffer.append(prop.getRangeOntologyClass(res).getFactoryFullClassname());
    stringBuffer.append(TEXT_288);
    stringBuffer.append(prop.getRangeOntologyClass(res).getInterfaceClassname());
    stringBuffer.append(TEXT_289);
    			} 
    stringBuffer.append(TEXT_290);
    stringBuffer.append(prop.getPropertyCapped());
    stringBuffer.append(TEXT_291);
    stringBuffer.append(prop.getReturnType(res));
    stringBuffer.append(TEXT_292);
    stringBuffer.append(prop.getPropertyName());
    stringBuffer.append(TEXT_293);
     			if (ctx.isGeneratePropertyCache()) { 
    stringBuffer.append(TEXT_294);
    stringBuffer.append(prop.getPropertyName(res));
    stringBuffer.append(TEXT_295);
    stringBuffer.append(prop.getPropertyCapped(res));
    stringBuffer.append(TEXT_296);
    stringBuffer.append(prop.getPropertyName(res));
    stringBuffer.append(TEXT_297);
    stringBuffer.append(prop.getPropertyName());
    stringBuffer.append(TEXT_298);
    stringBuffer.append(prop.getPropertyName(res));
    stringBuffer.append(TEXT_299);
    stringBuffer.append(prop.getPropertyName());
    stringBuffer.append(TEXT_300);
    stringBuffer.append(prop.getPropertyName(res));
    stringBuffer.append(TEXT_301);
    stringBuffer.append(prop.getPropertyName());
    stringBuffer.append(TEXT_302);
    stringBuffer.append(prop.getPropertyName(res));
    stringBuffer.append(TEXT_303);
    stringBuffer.append(prop.getPropertyName());
    stringBuffer.append(TEXT_304);
     			} 
    stringBuffer.append(TEXT_305);
    stringBuffer.append(prop.getPropertyName());
    stringBuffer.append(TEXT_306);
    stringBuffer.append(prop.getPropertyName());
    stringBuffer.append(TEXT_307);
    stringBuffer.append(prop.getReturnType(res));
    stringBuffer.append(TEXT_308);
    stringBuffer.append(prop.getPropertyCapped(res));
    stringBuffer.append(TEXT_309);
    stringBuffer.append(prop.getReturnType(res));
    stringBuffer.append(TEXT_310);
    stringBuffer.append(prop.getPropertyName());
    stringBuffer.append(TEXT_311);
    stringBuffer.append(prop.getRangeOntologyClass(res).getFactoryFullClassname());
    stringBuffer.append(TEXT_312);
    stringBuffer.append(prop.getRangeOntologyClass(res).getInterfaceClassname());
    stringBuffer.append(TEXT_313);
     			if (ctx.isGeneratePropertyCache()) { 
    stringBuffer.append(TEXT_314);
    stringBuffer.append(prop.getPropertyName(res));
    stringBuffer.append(TEXT_315);
    stringBuffer.append(prop.getPropertyCapped(res));
    stringBuffer.append(TEXT_316);
    stringBuffer.append(prop.getPropertyName(res));
    stringBuffer.append(TEXT_317);
    stringBuffer.append(prop.getPropertyName());
    stringBuffer.append(TEXT_318);
    				} 
    stringBuffer.append(TEXT_319);
    stringBuffer.append(prop.getPropertyName());
    stringBuffer.append(TEXT_320);
    stringBuffer.append(prop.getPropertyName());
    stringBuffer.append(TEXT_321);
    stringBuffer.append(prop.getPropertyName());
    stringBuffer.append(TEXT_322);
    stringBuffer.append(prop.getReturnType(res));
    stringBuffer.append(TEXT_323);
    stringBuffer.append(prop.getPropertyCapped(res));
    stringBuffer.append(TEXT_324);
     			if (ctx.isUseStrictTypeChecking() && !prop.getRangeOntologyClass(res).getURI().equals(com.hp.hpl.jena.vocabulary.RDFS.Resource.getURI())) { 
    stringBuffer.append(TEXT_325);
    stringBuffer.append(prop.getRangeOntologyClass(res).getInterfaceFullClassname());
    stringBuffer.append(TEXT_326);
    stringBuffer.append(prop.getRangeOntologyClass(res).getInterfaceFullClassname());
    stringBuffer.append(TEXT_327);
     			} 
    stringBuffer.append(TEXT_328);
    stringBuffer.append(prop.getReturnType(res));
    stringBuffer.append(TEXT_329);
    stringBuffer.append(prop.getPropertyName());
    stringBuffer.append(TEXT_330);
    stringBuffer.append(prop.getRangeOntologyClass(res).getFactoryFullClassname());
    stringBuffer.append(TEXT_331);
    stringBuffer.append(prop.getRangeOntologyClass(res).getInterfaceClassname());
    stringBuffer.append(TEXT_332);
     			if (ctx.isGeneratePropertyCache()) { 
    stringBuffer.append(TEXT_333);
    stringBuffer.append(prop.getPropertyName(res));
    stringBuffer.append(TEXT_334);
    stringBuffer.append(prop.getPropertyCapped(res));
    stringBuffer.append(TEXT_335);
    stringBuffer.append(prop.getPropertyName(res));
    stringBuffer.append(TEXT_336);
    stringBuffer.append(prop.getPropertyName());
    stringBuffer.append(TEXT_337);
    stringBuffer.append(prop.getPropertyName());
    stringBuffer.append(TEXT_338);
    stringBuffer.append(prop.getPropertyName(res));
    stringBuffer.append(TEXT_339);
    stringBuffer.append(prop.getPropertyName());
    stringBuffer.append(TEXT_340);
    				} 
    stringBuffer.append(TEXT_341);
    stringBuffer.append(prop.getPropertyName());
    stringBuffer.append(TEXT_342);
    stringBuffer.append(prop.getPropertyName());
    stringBuffer.append(TEXT_343);
    stringBuffer.append(prop.getPropertyName());
    stringBuffer.append(TEXT_344);
    stringBuffer.append(prop.getPropertyCapped());
    stringBuffer.append(TEXT_345);
    stringBuffer.append(prop.getReturnType(res));
    stringBuffer.append(TEXT_346);
    stringBuffer.append(prop.getPropertyName());
    stringBuffer.append(TEXT_347);
     			if (ctx.isGeneratePropertyCache()) { 
    stringBuffer.append(TEXT_348);
    stringBuffer.append(prop.getPropertyName(res));
    stringBuffer.append(TEXT_349);
    stringBuffer.append(prop.getPropertyCapped(res));
    stringBuffer.append(TEXT_350);
    stringBuffer.append(prop.getPropertyName(res));
    stringBuffer.append(TEXT_351);
    stringBuffer.append(prop.getPropertyName());
    stringBuffer.append(TEXT_352);
    				} 
    stringBuffer.append(TEXT_353);
    stringBuffer.append(prop.getPropertyName());
    stringBuffer.append(TEXT_354);
    stringBuffer.append(prop.getPropertyName());
    stringBuffer.append(TEXT_355);
     			if (ctx.isGeneratePropertyCache()) { 
    stringBuffer.append(TEXT_356);
    stringBuffer.append(prop.getPropertyName(res));
    stringBuffer.append(TEXT_357);
    stringBuffer.append(prop.getPropertyName());
    stringBuffer.append(TEXT_358);
    				} 
    stringBuffer.append(TEXT_359);
    stringBuffer.append(prop.getPropertyName());
    stringBuffer.append(TEXT_360);
    stringBuffer.append(prop.getPropertyName());
    stringBuffer.append(TEXT_361);
        } // end alt return types 
    stringBuffer.append(TEXT_362);
      }  // end mulitvalued-object 
     } // end all props
    stringBuffer.append(TEXT_363);
     if (ctx.isGenerateListeners()) { 
    stringBuffer.append(TEXT_364);
    stringBuffer.append(oc.getInterfaceClassname());
    stringBuffer.append(TEXT_365);
    stringBuffer.append(oc.getListenerClassname());
    stringBuffer.append(TEXT_366);
    stringBuffer.append(oc.getListenerClassname());
    stringBuffer.append(TEXT_367);
    stringBuffer.append(oc.getInterfaceClassname());
    stringBuffer.append(TEXT_368);
    stringBuffer.append(oc.getListenerClassname());
    stringBuffer.append(TEXT_369);
     } 
    stringBuffer.append(TEXT_370);
     if (ctx.isGeneratePropertyCache() || ctx.isGenerateListeners()) { 
    stringBuffer.append(TEXT_371);
     	for (java.util.Iterator iter = oc.listProperties(true).iterator(); iter.hasNext();) { 
     		OntologyProperty prop = (OntologyProperty)iter.next(); 
    stringBuffer.append(TEXT_372);
    stringBuffer.append(prop.getPropertyName());
    stringBuffer.append(TEXT_373);
    			if (prop.isSingleValued() && prop.isDatatypeProperty()) { 
    stringBuffer.append(TEXT_374);
    				if (ctx.isGeneratePropertyCache()) { 
    					for (java.util.Iterator iter2 = prop.listAllRanges();iter2.hasNext();) { 
              		Resource res = (Resource)iter2.next();
     					if (prop.getReturnType(res).equals("com.hp.hpl.jena.rdf.model.Literal")) { 
    stringBuffer.append(TEXT_375);
    stringBuffer.append(prop.getPropertyName(res));
    stringBuffer.append(TEXT_376);
    						}  else {
    stringBuffer.append(TEXT_377);
    stringBuffer.append(prop.getPropertyName(res));
    stringBuffer.append(TEXT_378);
    stringBuffer.append(prop.getReturnType(res));
    stringBuffer.append(TEXT_379);
    stringBuffer.append(ctx.isUseTypedLiterals());
    stringBuffer.append(TEXT_380);
    stringBuffer.append(prop.getReturnType(res));
    stringBuffer.append(TEXT_381);
    stringBuffer.append(prop.getRangeURI(res));
    stringBuffer.append(TEXT_382);
    						} 
    					} 
    				} 
    				if (ctx.isGenerateListeners()) { 
    stringBuffer.append(TEXT_383);
    stringBuffer.append(oc.getInterfaceClassname());
    stringBuffer.append(TEXT_384);
    stringBuffer.append(oc.getInterfaceClassname());
    stringBuffer.append(TEXT_385);
    stringBuffer.append(prop.getPropertyName());
    stringBuffer.append(TEXT_386);
    stringBuffer.append(oc.getImplFullClassname());
    stringBuffer.append(TEXT_387);
    				} 
    			} 
    			if (prop.isMultiValued() && prop.isDatatypeProperty()) { 
    stringBuffer.append(TEXT_388);
    				for (java.util.Iterator iter2 = prop.listAllRanges();iter2.hasNext();) { 
              	Resource res = (Resource)iter2.next();
     				if (prop.getReturnType(res).equals("com.hp.hpl.jena.rdf.model.Literal")) { 
     					if (ctx.isGeneratePropertyCache()) { 
    stringBuffer.append(TEXT_389);
    stringBuffer.append(prop.getPropertyName(res));
    stringBuffer.append(TEXT_390);
    stringBuffer.append(prop.getPropertyCapped(res));
    stringBuffer.append(TEXT_391);
    stringBuffer.append(prop.getPropertyName(res));
    stringBuffer.append(TEXT_392);
    stringBuffer.append(prop.getPropertyName(res));
    stringBuffer.append(TEXT_393);
    					} 
    						if (ctx.isGenerateListeners()) { 
    stringBuffer.append(TEXT_394);
    stringBuffer.append(prop.getPropertyCapped(res));
    stringBuffer.append(TEXT_395);
    stringBuffer.append(prop.getPropertyCapped(res));
    stringBuffer.append(TEXT_396);
    stringBuffer.append(prop.getPropertyCapped(res));
    stringBuffer.append(TEXT_397);
    stringBuffer.append(oc.getInterfaceClassname());
    stringBuffer.append(TEXT_398);
    stringBuffer.append(oc.getInterfaceClassname());
    stringBuffer.append(TEXT_399);
    stringBuffer.append(prop.getPropertyName());
    stringBuffer.append(TEXT_400);
    stringBuffer.append(oc.getImplFullClassname());
    stringBuffer.append(TEXT_401);
    						} 
    				}  else {
    stringBuffer.append(TEXT_402);
    stringBuffer.append(ctx.isUseTypedLiterals());
    stringBuffer.append(TEXT_403);
    stringBuffer.append(prop.getReturnType(res));
    stringBuffer.append(TEXT_404);
    stringBuffer.append(prop.getRangeURI(res));
    stringBuffer.append(TEXT_405);
     					if (ctx.isGeneratePropertyCache()) { 
    stringBuffer.append(TEXT_406);
    stringBuffer.append(prop.getPropertyName(res));
    stringBuffer.append(TEXT_407);
    stringBuffer.append(prop.getPropertyCapped(res));
    stringBuffer.append(TEXT_408);
    stringBuffer.append(prop.getPropertyName(res));
    stringBuffer.append(TEXT_409);
    stringBuffer.append(prop.getPropertyName(res));
    stringBuffer.append(TEXT_410);
    					} 
    						if (ctx.isGenerateListeners()) { 
    stringBuffer.append(TEXT_411);
    stringBuffer.append(prop.getPropertyCapped(res));
    stringBuffer.append(TEXT_412);
    stringBuffer.append(prop.getPropertyCapped(res));
    stringBuffer.append(TEXT_413);
    stringBuffer.append(prop.getPropertyCapped(res));
    stringBuffer.append(TEXT_414);
    stringBuffer.append(oc.getInterfaceClassname());
    stringBuffer.append(TEXT_415);
    stringBuffer.append(oc.getInterfaceClassname());
    stringBuffer.append(TEXT_416);
    stringBuffer.append(prop.getPropertyName());
    stringBuffer.append(TEXT_417);
    stringBuffer.append(oc.getImplFullClassname());
    stringBuffer.append(TEXT_418);
    stringBuffer.append(prop.getReturnType(res));
    stringBuffer.append(TEXT_419);
    						} 
    					} 
    				} 
    			} 
    			if (prop.isSingleValued() && prop.isObjectProperty()) { 
    stringBuffer.append(TEXT_420);
    				if (ctx.isGeneratePropertyCache()) { 
    					for (java.util.Iterator iter2 = prop.listAllRanges();iter2.hasNext();) { 
              			Resource res = (Resource)iter2.next(); 
    stringBuffer.append(TEXT_421);
    stringBuffer.append(prop.getPropertyName(res));
    stringBuffer.append(TEXT_422);
              			if (res != OntologyProperty.DEFAULT_RANGE && !prop.getRangeOntologyClass(res).getURI().equals(com.hp.hpl.jena.vocabulary.RDFS.Resource.getURI())) { 
    stringBuffer.append(TEXT_423);
    stringBuffer.append(prop.getRangeOntologyClass(res).getInterfaceFullClassname());
    stringBuffer.append(TEXT_424);
    						} else { 
    stringBuffer.append(TEXT_425);
    			    		} 
    stringBuffer.append(TEXT_426);
    stringBuffer.append(prop.getPropertyName(res));
    stringBuffer.append(TEXT_427);
    stringBuffer.append(prop.getRangeOntologyClass(res).getFactoryFullClassname());
    stringBuffer.append(TEXT_428);
    stringBuffer.append(prop.getRangeOntologyClass(res).getInterfaceClassname());
    stringBuffer.append(TEXT_429);
    					} 
    				} 
    				if (ctx.isGenerateListeners()) { 
    stringBuffer.append(TEXT_430);
    stringBuffer.append(oc.getInterfaceClassname());
    stringBuffer.append(TEXT_431);
    stringBuffer.append(oc.getInterfaceClassname());
    stringBuffer.append(TEXT_432);
    stringBuffer.append(prop.getPropertyName());
    stringBuffer.append(TEXT_433);
    stringBuffer.append(oc.getImplFullClassname());
    stringBuffer.append(TEXT_434);
    				} 
    			} 
    			if (prop.isMultiValued() && prop.isObjectProperty()) { 
    stringBuffer.append(TEXT_435);
    				for (java.util.Iterator iter2 = prop.listAllRanges();iter2.hasNext();) { 
    					Resource res = (Resource)iter2.next(); 
              		if (res != OntologyProperty.DEFAULT_RANGE && !prop.getRangeOntologyClass(res).getURI().equals(com.hp.hpl.jena.vocabulary.RDFS.Resource.getURI())) { 
    stringBuffer.append(TEXT_436);
    stringBuffer.append(prop.getRangeOntologyClass(res).getInterfaceFullClassname());
    stringBuffer.append(TEXT_437);
    					} else { 
    stringBuffer.append(TEXT_438);
    			   		} 
    stringBuffer.append(TEXT_439);
    stringBuffer.append(prop.getReturnType(res));
    stringBuffer.append(TEXT_440);
    stringBuffer.append(prop.getPropertyName(res));
    stringBuffer.append(TEXT_441);
    stringBuffer.append(prop.getPropertyName(res));
    stringBuffer.append(TEXT_442);
    stringBuffer.append(prop.getRangeOntologyClass(res).getFactoryFullClassname());
    stringBuffer.append(TEXT_443);
    stringBuffer.append(prop.getRangeOntologyClass(res).getInterfaceClassname());
    stringBuffer.append(TEXT_444);
     					if (ctx.isGeneratePropertyCache()) { 
    stringBuffer.append(TEXT_445);
    stringBuffer.append(prop.getPropertyName(res));
    stringBuffer.append(TEXT_446);
    stringBuffer.append(prop.getPropertyCapped(res));
    stringBuffer.append(TEXT_447);
    stringBuffer.append(prop.getPropertyName(res));
    stringBuffer.append(TEXT_448);
    stringBuffer.append(prop.getPropertyName(res));
    stringBuffer.append(TEXT_449);
    stringBuffer.append(prop.getPropertyName(res));
    stringBuffer.append(TEXT_450);
    stringBuffer.append(prop.getPropertyName(res));
    stringBuffer.append(TEXT_451);
    					} 
    					if (ctx.isGenerateListeners()) { 
    stringBuffer.append(TEXT_452);
    stringBuffer.append(prop.getPropertyCapped(res));
    stringBuffer.append(TEXT_453);
    stringBuffer.append(prop.getPropertyCapped(res));
    stringBuffer.append(TEXT_454);
    stringBuffer.append(prop.getPropertyCapped(res));
    stringBuffer.append(TEXT_455);
    stringBuffer.append(oc.getInterfaceClassname());
    stringBuffer.append(TEXT_456);
    stringBuffer.append(oc.getInterfaceClassname());
    stringBuffer.append(TEXT_457);
    stringBuffer.append(prop.getPropertyName());
    stringBuffer.append(TEXT_458);
    stringBuffer.append(oc.getImplFullClassname());
    stringBuffer.append(TEXT_459);
    stringBuffer.append(prop.getPropertyName(res));
    stringBuffer.append(TEXT_460);
    					} 
    stringBuffer.append(TEXT_461);
    				} 
    			} 
    stringBuffer.append(TEXT_462);
    	} 
    stringBuffer.append(TEXT_463);
     	for (java.util.Iterator iter = oc.listProperties(true).iterator(); iter.hasNext();) { 
     		OntologyProperty prop = (OntologyProperty)iter.next(); 
    stringBuffer.append(TEXT_464);
    stringBuffer.append(prop.getPropertyName());
    stringBuffer.append(TEXT_465);
    			if (prop.isSingleValued() && prop.isDatatypeProperty()) { 
    stringBuffer.append(TEXT_466);
    				if (ctx.isGeneratePropertyCache()) { 
    					for (java.util.Iterator iter2 = prop.listAllRanges();iter2.hasNext();) { 
              		Resource res = (Resource)iter2.next();
     					if (prop.getReturnType(res).equals("com.hp.hpl.jena.rdf.model.Literal")) { 
    stringBuffer.append(TEXT_467);
    stringBuffer.append(prop.getPropertyName(res));
    stringBuffer.append(TEXT_468);
    stringBuffer.append(prop.getPropertyName(res));
    stringBuffer.append(TEXT_469);
    stringBuffer.append(prop.getPropertyName(res));
    stringBuffer.append(TEXT_470);
    						}  else {
    stringBuffer.append(TEXT_471);
    stringBuffer.append(ctx.isUseTypedLiterals());
    stringBuffer.append(TEXT_472);
    stringBuffer.append(prop.getReturnType(res));
    stringBuffer.append(TEXT_473);
    stringBuffer.append(prop.getRangeURI(res));
    stringBuffer.append(TEXT_474);
    stringBuffer.append(prop.getPropertyName(res));
    stringBuffer.append(TEXT_475);
    stringBuffer.append(prop.getPropertyName(res));
    stringBuffer.append(TEXT_476);
    stringBuffer.append(prop.getPropertyName(res));
    stringBuffer.append(TEXT_477);
    						} 
    					} 
    				} 
    				if (ctx.isGenerateListeners()) { 
    stringBuffer.append(TEXT_478);
    stringBuffer.append(oc.getInterfaceClassname());
    stringBuffer.append(TEXT_479);
    stringBuffer.append(oc.getInterfaceClassname());
    stringBuffer.append(TEXT_480);
    stringBuffer.append(prop.getPropertyName());
    stringBuffer.append(TEXT_481);
    stringBuffer.append(oc.getImplFullClassname());
    stringBuffer.append(TEXT_482);
    				} 
    			} 
    			if (prop.isMultiValued() && prop.isDatatypeProperty()) { 
    stringBuffer.append(TEXT_483);
    				for (java.util.Iterator iter2 = prop.listAllRanges();iter2.hasNext();) { 
              	Resource res = (Resource)iter2.next();
     				if (prop.getReturnType(res).equals("com.hp.hpl.jena.rdf.model.Literal")) { 
     					if (ctx.isGeneratePropertyCache()) { 
    stringBuffer.append(TEXT_484);
    stringBuffer.append(prop.getPropertyName(res));
    stringBuffer.append(TEXT_485);
    stringBuffer.append(prop.getPropertyName(res));
    stringBuffer.append(TEXT_486);
    stringBuffer.append(prop.getPropertyName(res));
    stringBuffer.append(TEXT_487);
    						} 
    						if (ctx.isGenerateListeners()) { 
    stringBuffer.append(TEXT_488);
    stringBuffer.append(prop.getPropertyCapped(res));
    stringBuffer.append(TEXT_489);
    stringBuffer.append(prop.getPropertyCapped(res));
    stringBuffer.append(TEXT_490);
    stringBuffer.append(prop.getPropertyCapped(res));
    stringBuffer.append(TEXT_491);
    stringBuffer.append(oc.getInterfaceClassname());
    stringBuffer.append(TEXT_492);
    stringBuffer.append(oc.getInterfaceClassname());
    stringBuffer.append(TEXT_493);
    stringBuffer.append(prop.getPropertyName());
    stringBuffer.append(TEXT_494);
    stringBuffer.append(oc.getImplFullClassname());
    stringBuffer.append(TEXT_495);
    						} 
    					} else {
    stringBuffer.append(TEXT_496);
    stringBuffer.append(ctx.isUseTypedLiterals());
    stringBuffer.append(TEXT_497);
    stringBuffer.append(prop.getReturnType(res));
    stringBuffer.append(TEXT_498);
    stringBuffer.append(prop.getRangeURI(res));
    stringBuffer.append(TEXT_499);
     				if (ctx.isGeneratePropertyCache()) { 
    stringBuffer.append(TEXT_500);
    stringBuffer.append(prop.getPropertyName(res));
    stringBuffer.append(TEXT_501);
    stringBuffer.append(prop.getPropertyName(res));
    stringBuffer.append(TEXT_502);
    stringBuffer.append(prop.getPropertyName(res));
    stringBuffer.append(TEXT_503);
    				} 
    						if (ctx.isGenerateListeners()) { 
    stringBuffer.append(TEXT_504);
    stringBuffer.append(oc.getInterfaceClassname());
    stringBuffer.append(TEXT_505);
    stringBuffer.append(oc.getInterfaceClassname());
    stringBuffer.append(TEXT_506);
    stringBuffer.append(prop.getPropertyName());
    stringBuffer.append(TEXT_507);
    stringBuffer.append(oc.getImplFullClassname());
    stringBuffer.append(TEXT_508);
    stringBuffer.append(prop.getReturnType(res));
    stringBuffer.append(TEXT_509);
    						} 
    					} 
    				} 
    			} 
    			if (prop.isSingleValued() && prop.isObjectProperty()) { 
    stringBuffer.append(TEXT_510);
    				if (ctx.isGeneratePropertyCache()) { 
    					for (java.util.Iterator iter2 = prop.listAllRanges();iter2.hasNext();) { 
              		Resource res = (Resource)iter2.next(); 
    stringBuffer.append(TEXT_511);
    stringBuffer.append(prop.getPropertyName(res));
    stringBuffer.append(TEXT_512);
    stringBuffer.append(prop.getPropertyName(res));
    stringBuffer.append(TEXT_513);
    stringBuffer.append(prop.getPropertyName(res));
    stringBuffer.append(TEXT_514);
    					} 
    				} 
    				if (ctx.isGenerateListeners()) { 
    stringBuffer.append(TEXT_515);
    stringBuffer.append(oc.getInterfaceClassname());
    stringBuffer.append(TEXT_516);
    stringBuffer.append(oc.getInterfaceClassname());
    stringBuffer.append(TEXT_517);
    stringBuffer.append(prop.getPropertyName());
    stringBuffer.append(TEXT_518);
    stringBuffer.append(oc.getImplFullClassname());
    stringBuffer.append(TEXT_519);
    				} 
    			} 
    			if (prop.isMultiValued() && prop.isObjectProperty()) { 
    stringBuffer.append(TEXT_520);
    				for (java.util.Iterator iter2 = prop.listAllRanges();iter2.hasNext();) { 
    					Resource res = (Resource)iter2.next(); 
              		if (res != OntologyProperty.DEFAULT_RANGE && !prop.getRangeOntologyClass(res).getURI().equals(com.hp.hpl.jena.vocabulary.RDFS.Resource.getURI())) { 
    stringBuffer.append(TEXT_521);
    stringBuffer.append(prop.getRangeOntologyClass(res).getInterfaceFullClassname());
    stringBuffer.append(TEXT_522);
    					} else { 
    stringBuffer.append(TEXT_523);
    			   		} 
     					if (ctx.isGeneratePropertyCache()) { 
    stringBuffer.append(TEXT_524);
    stringBuffer.append(prop.getReturnType(res));
    stringBuffer.append(TEXT_525);
    stringBuffer.append(prop.getPropertyName(res));
    stringBuffer.append(TEXT_526);
    stringBuffer.append(prop.getPropertyName(res));
    stringBuffer.append(TEXT_527);
    stringBuffer.append(prop.getPropertyName(res));
    stringBuffer.append(TEXT_528);
    stringBuffer.append(prop.getReturnType(res));
    stringBuffer.append(TEXT_529);
    stringBuffer.append(prop.getReturnType(res));
    stringBuffer.append(TEXT_530);
    stringBuffer.append(prop.getPropertyName(res));
    stringBuffer.append(TEXT_531);
    stringBuffer.append(prop.getPropertyName(res));
    stringBuffer.append(TEXT_532);
    stringBuffer.append(prop.getPropertyName(res));
    stringBuffer.append(TEXT_533);
    stringBuffer.append(prop.getPropertyName(res));
    stringBuffer.append(TEXT_534);
    stringBuffer.append(prop.getPropertyName(res));
    stringBuffer.append(TEXT_535);
    stringBuffer.append(prop.getRangeOntologyClass(res).getFactoryFullClassname());
    stringBuffer.append(TEXT_536);
    stringBuffer.append(prop.getRangeOntologyClass(res).getInterfaceClassname());
    stringBuffer.append(TEXT_537);
    stringBuffer.append(prop.getPropertyName(res));
    stringBuffer.append(TEXT_538);
    stringBuffer.append(prop.getRangeOntologyClass(res).getFactoryFullClassname());
    stringBuffer.append(TEXT_539);
    stringBuffer.append(prop.getRangeOntologyClass(res).getInterfaceClassname());
    stringBuffer.append(TEXT_540);
    					} else { 
    stringBuffer.append(TEXT_541);
    stringBuffer.append(prop.getReturnType(res));
    stringBuffer.append(TEXT_542);
    stringBuffer.append(prop.getPropertyName(res));
    stringBuffer.append(TEXT_543);
    stringBuffer.append(prop.getPropertyName(res));
    stringBuffer.append(TEXT_544);
    stringBuffer.append(prop.getRangeOntologyClass(res).getFactoryFullClassname());
    stringBuffer.append(TEXT_545);
    stringBuffer.append(prop.getRangeOntologyClass(res).getInterfaceClassname());
    stringBuffer.append(TEXT_546);
    					} 
    					if (ctx.isGenerateListeners()) { 
    stringBuffer.append(TEXT_547);
    stringBuffer.append(prop.getPropertyCapped(res));
    stringBuffer.append(TEXT_548);
    stringBuffer.append(prop.getPropertyCapped(res));
    stringBuffer.append(TEXT_549);
    stringBuffer.append(prop.getPropertyCapped(res));
    stringBuffer.append(TEXT_550);
    stringBuffer.append(oc.getInterfaceClassname());
    stringBuffer.append(TEXT_551);
    stringBuffer.append(oc.getInterfaceClassname());
    stringBuffer.append(TEXT_552);
    stringBuffer.append(prop.getPropertyName());
    stringBuffer.append(TEXT_553);
    stringBuffer.append(oc.getImplFullClassname());
    stringBuffer.append(TEXT_554);
    stringBuffer.append(prop.getPropertyName(res));
    stringBuffer.append(TEXT_555);
    					} 
    stringBuffer.append(TEXT_556);
    				} 
    			} 
    stringBuffer.append(TEXT_557);
    	} 
    stringBuffer.append(TEXT_558);
     } 
    stringBuffer.append(TEXT_559);
    return stringBuffer.toString();
  }
}