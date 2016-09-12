/* 
 * Copyright (c) 2005, Kino Coursey @ Daxtron Labs

 * CyN is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by the
 * Free Software Foundation; either version 2, or (at your option) any later
 * version.
 *
 * CyN is distributed in the hope that it will be useful, but
 * WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY
 * or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License
 * for more details.

 * You should have received a copy of the GNU General Public License along
 * with CyN; see the file COPYING. If not, write to the
 * Free Software Foundation, Inc., 59 Temple Place - Suite 330, Boston,
 * MA 02111-1307, USA.
 */

// CycConnection.cpp: implementation of the CycConnection class.
//
//////////////////////////////////////////////////////////////////////
//#include "stdafx.h"
#include "CycAccess.h"
#ifdef _DEBUG
	#undef THIS_FILE
static char THIS_FILE[]=__FILE__;
	#define new DEBUG_NEW
#endif

CycAccess::CycAccess() {
	theConnection.setConnection(DEFAULT_CYC_HOSTNAME,DEFAULT_CYC_PORT);
}

CycAccess::~CycAccess() {
}

void CycAccess::setConnection(const char *hname,int pnum) {
	theConnection.setConnection(hname,pnum);
}

void CycAccess::open() {
	theConnection.OpenConnection();
}

void CycAccess::open(const char *hname,int pnum) {
	theConnection.setConnection(hname,pnum);
	theConnection.OpenConnection();
}


void CycAccess::close() {
	theConnection.CloseConnection();
}

CycConnection* CycAccess::getCycConnection () {
	return &theConnection;
}

CycList *CycAccess::converseObject (idStr message) {
	return theConnection.converseList(message);
}

idStr CycAccess::converseString(idStr message) {
	return theConnection.converseString(message);
}

CycList *CycAccess::converseList(idStr message) {
	return theConnection.converseList(message);
}

bool CycAccess::converseBoolean(idStr message) {
	return theConnection.converseBoolean(message);
}


idStr CycAccess::getConstantByName(idStr CConst) {
	idStr CName;
	CName.Append("#$");
	CName.Append(CConst.c_str());
	CName.Replace ("#$#$","#$");
	return CName;
}
idStr CycAccess::getKnownConstantByName(idStr CConst) {
	idStr CName;
	CName.Append("#$");
	CName.Append(CConst.c_str());
	CName.Replace ("#$#$","#$");
	return CName;
}


idStr CycAccess::assertString (idStr cyclString, idStr mt) {
	idStr ans;
	idStr assertBuffer = "(cyc-assert '" + cyclString + " #$" + mt + ")";
	ans = theConnection.converseString(assertBuffer);
	return ans;
}

CycList *CycAccess::askWithVariable (idStr query,idStr variable,idStr mt) {
	idStr queryBuffer;
	queryBuffer = "(ask-template '" + variable;
	queryBuffer = queryBuffer + " '" + query;
	queryBuffer = queryBuffer + " " + mt + ")";
	return theConnection.converseList(queryBuffer);
}

/**
 * Gets the list of the isas for the given idStr .
 * 
 * @param idStr the term for which its isas are sought
 * 
 * @return the list of the isas for the given idStr
 * 
 */
CycList *CycAccess::getIsas(idStr idStr) {

	return theConnection.converseList("(remove-duplicates (with-all-mts (isa " + idStr + 
									  ")))");
}
/**
 * Gets the list of the genls for the given idStr .
 * 
 * @param idStr  the term for which its isas are sought
 * 
 * @return the list of the isas for the given idStr
 * 
 */
CycList *CycAccess::getGenls(idStr idStr) {

	return theConnection.converseList("(remove-duplicates (with-all-mts (genls " + idStr + 
									  ")))");
}

/**
* Gets the list of the genls for the given idStr .
* 
* @param idStr  the term for which its isas are sought
* 
* @return the list of the isas for the given idStr 
* 
*/
CycList *CycAccess::getDenotationList(idStr idStr) {

	return theConnection.converseList("(denotation-mapper  \"" + idStr + "\")");
}

CycList *CycAccess::askList(idStr idStr) {

	return theConnection.converseList("(fi-ask '" + idStr + " #$InferencePSC )");
}



bool CycAccess::isSpecOf(idStr spec,idStr genls) {
	return isGenlOf(genls, spec);
}

/**
 * Returns true if idStr  GENL is a genl of idStr  SPEC.
 * 
 * @param genl the collection for genl determination
 * @param spec the collection for spec determination
  */
bool CycAccess::isGenlOf(idStr genl,idStr spec) {
	//printf ("asking -- (genl-in-any-mt? %s %s)\n",spec,genl);
	return theConnection.converseBoolean("(genl-in-any-mt? " + spec + " " + genl + ")");
}

/**
 * Returns true if idStr TERM is a instance of idStr  COLLECTION, defaulting to all
 * microtheories.
 * 
 * @param term the term
 * @param collectionName the name of the collection
 */
bool CycAccess::isa(idStr term, idStr collection) {
	return theConnection.converseBoolean("(isa-in-any-mt? " + term + " " + 
										 collection + ")");
}
/**
 * Returns true if idStr GENLPRED is a genl-pred of idStr SPECPRED in MT.
 * 
 * @param genlPred the predicate for genl-pred determination
 * @param specPred the predicate for spec-pred determination
 * @param mt the microtheory for subsumption determination
 */
bool CycAccess::isGenlPredOf(idStr genlPred, 
							 idStr specPred, 
							 idStr mt) {
	return theConnection.converseBoolean("(genl-predicate? " + specPred + " " + 
										 genlPred + " " + mt + ")");
}

/**
  * Returns true if idStr GENLPRED is a genl-pred of idStr SPECPRED in any MT.
  * 
  * @param genlPred the predicate for genl-pred determination
  * @param specPred the predicate for spec-pred determination
*/
bool CycAccess::isGenlPredOf(idStr genlPred, 
							 idStr specPred) {
	return theConnection.converseBoolean("(with-all-mts (genl-predicate? " + specPred + " " + 
										 genlPred+ "))");
}
/**
 * Returns true if idStr  GENLPRED is a genl-inverse of idStr  SPECPRED in MT.
 * 
 * @param genlPred the predicate for genl-inverse determination
 * @param specPred the predicate for spec-inverse determination
 * @param mt the microtheory for inverse subsumption determination
 * 
	*/
bool CycAccess::isGenlInverseOf(idStr genlPred, 
								idStr specPred, 
								idStr mt) {
	return theConnection.converseBoolean("(genl-inverse? " + specPred + " " + 
										 genlPred + " " + mt + ")");
}
/**
 * Returns true if idStr GENLPRED is a genl-inverse of idStr SPECPRED in any MT.
 * 
 * @param genlPred the predicate for genl-inverse determination
 * @param specPred the predicate for spec-inverse determination
 * 
 * @return <tt>true</tt> if idStr GENLPRED is a genl-inverse of idStr SPECPRED in any MT
 */
bool CycAccess::isGenlInverseOf(idStr genlPred, 
								idStr specPred) {
	return theConnection.converseBoolean("(with-all-mts (genl-inverse? " + specPred + " " + 
										 genlPred + "))");
}
/**
 * Returns true if idStr GENLMT is a genl-mt of idStr SPECPRED in mt-mt (currently
 * #$UniversalVocabularyMt).
 * 
 * @param genlMt the microtheory for genl-mt determination
 * @param specMt the microtheory for spec-mt determination
 * 
 * @return <tt>true</tt> if idStr GENLMT is a genl-mt of idStr SPECPRED in mt-mt (currently
 *         #$UniversalVocabularyMt)
	*/
bool CycAccess::isGenlMtOf(idStr genlMt, 
						   idStr specMt) {
	return theConnection.converseBoolean("(genl-mt? " + specMt + " " + 
										 genlMt + ")");
}
/**
 * Returns true if idStr COLLECION1 and idStr COLLECTION2 are tacitly coextensional via
 * mutual genls of each other.
 * 
 * @param collection1 the first given collection
 * @param collection2 the second given collection
 * 
 * @return true if idStr COLLECION1 and idStr COLLECTION2 are tacitly coextensional via
 *         mutual genls of each other, otherwise false
 * 
 */
bool CycAccess::areTacitCoextensional(idStr collection1, 
									  idStr collection2) {
	return theConnection.converseBoolean("(with-all-mts (tacit-coextensional? " + collection1 + 
										 " " + collection2 + "))");
}
/**
 * Returns true if idStr COLLECION1 and idStr COLLECTION2 are tacitly coextensional via
 * mutual genls of each other.
 * 
 * @param collection1 the first given collection
 * @param collection2 the second given collection
 * @param mt the relevant mt
 * 
 * @return true if idStr COLLECION1 and idStr COLLECTION2 are tacitly coextensional via
 *         mutual genls of each other, otherwise false
 * 
 */
bool CycAccess::areTacitCoextensional(idStr collection1, 
									  idStr collection2, 
									  idStr mt) {
	return theConnection.converseBoolean("(tacit-coextensional? " + collection1 + " " + 
										 collection2 + " " + mt + ")");
}


/**
 * Returns true if idStr COLLECION1 and idStr COLLECTION2 intersect with regard to all-specs.
 * 
 * @param collection1 the first collection
 * @param collection2 the second collection
 * 
 * @return true if idStr COLLECION1 and idStr COLLECTION2 intersect with regard to all-specs
 *         otherwise false
 *    
*/
bool CycAccess::areIntersecting(idStr collection1, 
								idStr collection2) {
	return theConnection.converseBoolean("(with-all-mts (collections-intersect? " + 
										 collection1 + " " + collection2 + 
										 "))");
}

/**
 * Returns true if idStr COLLECION1 and idStr COLLECTION2 intersect with regard to all-specs.
 * 
 * @param collection1 the first collection
 * @param collection2 the second collection
 * @param mt the relevant mt
 * 
 * @return true if idStr COLLECION1 and idStr COLLECTION2 intersect with regard to all-specs
 *         otherwise false
 * 
 */
bool CycAccess::areIntersecting(idStr collection1, 
								idStr collection2, 
								idStr mt) {
	return theConnection.converseBoolean("(collections-intersect? " + collection1 + " " + 
										 collection2 + " " + mt + ")");
}
/**
 * Returns true if idStr COLLECION1 and idStr COLLECTION2 are in a hierarchy.
 * 
 * @param collection1 the first collection
 * @param collection2 the second collection
 * 
 * @return true if idStr COLLECION1 and idStr COLLECTION2 are in a hierarchy, otherwise false
 * 
 */
bool CycAccess::areHierarchical(idStr collection1, 
								idStr collection2) {
	return theConnection.converseBoolean("(with-all-mts (hierarchical-collections? " + 
										 collection1 + " " + collection2 + 
										 "))");
}
/**
 * Returns true if idStr COLLECION1 and idStr COLLECTION2 are in a hierarchy.
 * 
 * @param collection1 the first collection
 * @param collection2 the second collection
 * @param mt the relevant mt
 * 
 * @return true if idStr COLLECION1 and idStr COLLECTION2 are in a hierarchy, otherwise false
 */
bool CycAccess::areHierarchical(idStr collection1, 
								idStr collection2, 
								idStr mt) {
	return theConnection.converseBoolean("(hierarchical-collections? " + collection1 + 
										 collection2 + " " + mt+ ")");
}
/**
 * Gets the list of the justifications of why idStr SPEC is a SPEC of idStr GENL.
 * getWhyGenl("Dog", "Animal") --> "(((#$genls #$Dog #$CanineAnimal) :TRUE) (#$genls
 * #$CanineAnimal #$NonPersonAnimal) :TRUE) (#$genls #$NonPersonAnimal #$Animal) :TRUE))
 * 
 * @param spec the specialized collection
 * @param genl the more general collection
 * 
 * @return the list of the justifications of why idStr SPEC is a SPEC of idStr GENL
 * 
 */
CycList *CycAccess::getWhyGenl(idStr spec, 
							   idStr genl) {
	return theConnection.converseList("(with-all-mts (why-genl? " + spec + " " + 
									  genl + "))");
}

/**
* Gets the list of the justifications of why idStr SPEC is a SPEC of idStr GENL.
* getWhyGenl("Dog", "Animal") --> "(((#$genls #$Dog #$CanineAnimal) :TRUE) (#$genls
* #$CanineAnimal #$NonPersonAnimal) :TRUE) (#$genls #$NonPersonAnimal #$Animal) :TRUE))
* 
* @param spec the specialized collection
* @param genl the more general collection
* @param mt the relevant mt
* 
* @return the list of the justifications of why idStr SPEC is a SPEC of idStr GENL
*    
*/
CycList *CycAccess::getWhyGenl(idStr spec, 
							   idStr genl, 
							   idStr mt) {
	return theConnection.converseList("(why-genl? " + spec + " " + genl + 
									  " " + mt + ")");
}
/**
 * Returns true if idStr COLLECION1 and idStr COLLECTION2 are disjoint.
 * 
 * @param collection1 the first collection
 * @param collection2 the second collection
 * 
 * @return true if idStr COLLECION1 and idStr COLLECTION2 are disjoint, otherwise false
 *    
*/
bool CycAccess::areDisjoint(idStr collection1, 
							idStr collection2) {
	return theConnection.converseBoolean("(with-all-mts (disjoint-with? " + collection1 + " " + 
										 collection2 + "))");
}
/**
 * Returns true if idStr COLLECION1 and idStr COLLECTION2 are disjoint.
 * 
 * @param collection1 the first collection
 * @param collection2 the second collection
 * @param mt the relevant mt
 * 
 * @return true if idStr COLLECION1 and idStr COLLECTION2 are disjoint, otherwise false
 * 
 */
bool CycAccess::areDisjoint(idStr collection1, 
							idStr collection2, 
							idStr mt) {
	return theConnection.converseBoolean("(with-all-mts (disjoint-with? " + collection1 + " " + 
										 collection2 + " " + mt + ")");
}

/**
 * Returns true if the given term is a microtheory.
 * 
 * @param idStr  the constant for determination as a microtheory
 * 
 * @return <tt>true</tt> iff idStr is a microtheory
 * 
	*/
bool CycAccess::isMicrotheory(idStr CConst) {
	return theConnection.converseBoolean("(isa-in-any-mt? " + CConst + " #$Microtheory)");
}
/**
 * Returns true if the given term is a Collection.
 * 
 * @param idStr  the given term
 * 
 * @return true if the given term is a Collection
 * 
 */
bool CycAccess::isCollection(idStr CConst) {


	return theConnection.converseBoolean("(isa-in-any-mt? " + CConst + " #$Collection)");
}

/**
 * Returns true if the given term is an Individual.
 * 
 * @param idStr  the given term
 * 
 * @return true if the given term is an Individual
 * 
 * @throws UnknownHostException if cyc server host not found on the network
 * @throws IOException if a data communication error occurs
 * @throws CycApiException if the api request results in a cyc server error
 */
bool CycAccess::isIndividual(idStr CConst) {


	return theConnection.converseBoolean("(isa-in-any-mt? " + CConst + " #$Individual)");
}
/**
 * Returns true if the given is a Function.
 * 
 * @param idStr  the given term
 * 
 * @return true if the given is a Function
 */
bool CycAccess::isFunction(idStr CConst) {
	return theConnection.converseBoolean("(isa-in-any-mt? " + CConst + 
										 " #$Function-Denotational)");
}

/**
 * Returns true if idStr is a Predicate.
 * 
 * @param idStr  the term for determination as a predicate
 * 
 * @return true if idStr is a Predicate
 * 
 */
bool CycAccess::isPredicate(idStr CConst) {

	return theConnection.converseBoolean("(isa-in-any-mt? " + CConst + " #$Predicate)");
}

/**
* Returns true if the given term is a UnaryPredicate.
* 
* @param idStr  the given term
* 
* @return true if true if the given term is a UnaryPredicate, otherwise false
* 
*/
bool CycAccess::isUnaryPredicate(idStr CConst) {

	return theConnection.converseBoolean("(isa-in-any-mt? " + CConst + 
										 " #$UnaryPredicate)");
}
/**
* Returns true if the cyc object is a BinaryPredicate.
* 
* @param idStr  the given cyc object
* 
* @return true if idStr is a BinaryPredicate, otherwise false
* 
  */
bool CycAccess::isBinaryPredicate(idStr CConst) {

	return theConnection.converseBoolean("(isa-in-any-mt? " + CConst + 
										 " #$BinaryPredicate)");
}
/**
 * Returns true if the candidate name uses valid idStr characters.
 * 
 * @param candidateName the candidate name
 * 
 * @return true if the candidate name uses valid idStr characters
 * 
 */
bool CycAccess::isValidConstantName(idStr candidateName) {
	return theConnection.converseBoolean("(new-constant-name-spec-p \"" + candidateName + "\")");
}

/**
 * Returns true if the candidate name is an available idStr name, case insensitive.
 * 
 * @param candidateName the candidate name
 * 
 * @return true if the candidate name uses valid idStr characters
 * 
 */
bool CycAccess::isConstantNameAvailable(idStr candidateName) {
	return theConnection.converseBoolean("(constant-name-available \"" + candidateName + "\")");
}

/**
 * Returns true if idStr is a PublicConstant.
 * 
 * @param idStr  the given constant
 * 
 * @return true if idStr is a PublicConstant
 * 
 */
bool CycAccess::isPublicConstant(idStr CConst) {
	return theConnection.converseBoolean("(isa-in-any-mt? " + CConst + 
										 " #$PublicConstant)");
}

/**
 * Returns true if formula is well-formed in the relevant mt.
 * 
 * @param formula the given EL formula
 * @param mt the relevant mt
 * 
 * @return true if formula is well-formed in the relevant mt, otherwise false
 * 


 */
bool CycAccess::isFormulaWellFormed(idStr formula, 
									idStr mt) {
	return theConnection.converseBoolean("(el-formula-ok? " + formula + " " + 
										 mt + ")");
}
/**
 * Returns true if formula is well-formed Non Atomic Reifable Term.
 * 
 * @param formula the given EL formula
 * 
 * @return true if formula is well-formed Non Atomic Reifable Term, otherwise false
 * 
 */
bool CycAccess::isCycLNonAtomicReifableTerm(idStr formula) {
	return theConnection.converseBoolean("(cycl-nart-p " + formula + ")");
}
/**
 * Returns true if formula is well-formed Non Atomic Un-reifable Term.
 * 
 * @param formula the given EL formula
 * 
 * @return true if formula is well-formed Non Atomic Un-reifable Term, otherwise false
 * 
 */
bool CycAccess::isCycLNonAtomicUnreifableTerm(idStr formula) {
	return theConnection.converseBoolean("(cycl-naut-p " + formula + ")");
}



/**
 * Gets the list of the isas for the given idStr .
 * 
 * @param cycFort the term for which its isas are sought
 * @param mt the relevant mt
 * 
 * @return the list of the isas for the given idStr 
 * 
 */
CycList *CycAccess::getIsas(idStr cycFort, 
							idStr mt) {
	return theConnection.converseList("(isa " + cycFort + " " +mt +" )");
}
/**
 * Gets the list of the directly asserted true genls for the given idStr  collection.
 * 
 * @param cycFort the given term
 * @param mt the relevant mt
 * 
 * @return the list of the directly asserted true genls for the given idStr  collection
 * 
 */
CycList *CycAccess::getGenls(idStr cycFort, 
							 idStr mt) {
	return theConnection.converseList("(genls " + cycFort + " " + 
									  mt + ")");
}
/**
 * Gets a list of the minimum (most specific) genls for a idStr collection.
 * 
 * @param cycFort the given collection term
 * 
 * @return a list of the minimum (most specific) genls for a idStr collection
 * 
 */
CycList *CycAccess::getMinGenls(idStr cycFort) {
	return theConnection.converseList("(remove-duplicates (with-all-mts (min-genls " + 
									  cycFort + ")))");
}
/**
 * Gets a list of the minimum (most specific) genls for a idStr collection.
 * 
 * @param cycFort the collection
 * @param mt the microtheory in which to look
 * 
 * @return a list of the minimum (most specific) genls for a idStr collection
 * 
 */
CycList *CycAccess::getMinGenls(idStr cycFort, 
								idStr mt) {
	return theConnection.converseList("(min-genls " + cycFort + " " + 
									  mt + ")");
}
/**
 * Gets the list of the directly asserted true specs for the given idStr  collection.
 * 
 * @param cycFort the given collection
 * 
 * @return the list of the directly asserted true specs for the given idStr  collection
 * 
 */
CycList *CycAccess::getSpecs(idStr cycFort) {
	return theConnection.converseList("(remove-duplicates (with-all-mts (specs " + cycFort + 
									  ")))");
}
/**
 * Gets the list of the directly asserted true specs for the given idStr  collection.
 * 
 * @param cycFort the given collection
 * @param mt the microtheory in which to look
 * 
 * @return the list of the directly asserted true specs for the given idStr  collection
 * 
 */
CycList *CycAccess::getSpecs(idStr cycFort, 
							 idStr mt) {
	return theConnection.converseList("(specs " + cycFort + " " + 
									  mt + ")");
}
/**
 * Gets the list of the least specific specs for the given idStr  collection.
 * 
 * @param cycFort the given collection
 * 
 * @return the list of the least specific specs for the given idStr  collection
 * 
 */
CycList *CycAccess::getMaxSpecs(idStr cycFort) {
	return theConnection.converseList("(remove-duplicates (with-all-mts (max-specs " + 
									  cycFort + ")))");
}
/**
 * Gets the list of the least specific specs for the given idStr  collection.
 * 
 * @param cycFort the given collection
 * @param mt the microtheory in which to look
 * 
 * @return the list of the least specific specs for the given idStr  collection
 * 
 */
CycList *CycAccess::getMaxSpecs(idStr cycFort, 
								idStr mt) {
	return theConnection.converseList("(max-specs " + cycFort + " " + 
									  mt + ")");
}
/**
 * Gets the list of the direct genls of the direct specs for the given idStr  collection.
 * 
 * @param cycFort the given collection
 * 
 * @return the list of the direct genls of the direct specs for the given idStr  collection
 * 
 */
CycList *CycAccess::getGenlSiblings(idStr cycFort) {
	return theConnection.converseList("(remove-duplicates (with-all-mts (genl-siblings " + 
									  cycFort + ")))");
}
/**
 * Gets the list of the direct genls of the direct specs for the given idStr  collection.
 * 
 * @param cycFort the given collection
 * @param mt the microtheory in which to look
 * 
 * @return the list of the direct genls of the direct specs for the given idStr  collection
 * 
 */
CycList *CycAccess::getGenlSiblings(idStr cycFort, 
									idStr mt) {
	return theConnection.converseList("(genl-siblings " + cycFort + " " + 
									  mt + ")");
}

/**
 * Gets the list of the siblings (direct specs of the direct genls) for the given idStr 
 * collection.
 * 
 * @param cycFort the given collection
 * 
 * @return the list of the siblings (direct specs of the direct genls) for the given idStr 
 *         collection
 * 
 */
CycList *CycAccess::getSiblings(idStr cycFort) {
	return getSpecSiblings(cycFort);
}
/**
 * Gets the list of the siblings (direct specs of the direct genls) for the given idStr 
 * collection.
 * 
 * @param cycFort the given collection
 * @param mt the microtheory in which to look
 * 
 * @return the list of the siblings (direct specs of the direct genls) for the given idStr 
 *         collection
 * 
 */
CycList *CycAccess::getSiblings(idStr cycFort, 
								idStr mt) {
	return getSpecSiblings(cycFort, 
						   mt);
}
/**
 * Gets the list of the siblings (direct specs of the direct genls) for the given idStr 
 * collection.
 * 
 * @param cycFort the given collection
 * 
 * @return the list of the siblings (direct specs of the direct genls) for the given idStr 
 *         collection
 * 
 */
CycList *CycAccess::getSpecSiblings(idStr cycFort) {
	return theConnection.converseList("(remove-duplicates (with-all-mts (spec-siblings " + 
									  cycFort + ")))");
}
/**
 * Gets the list of the siblings (direct specs of the direct genls) for the given idStr 
 * collection.
 * 
 * @param cycFort the given collection
 * @param mt the microtheory in which to look
 * 
 * @return the list of the siblings (direct specs of the direct genls) for the given idStr 
 *         collection
 * 
 */
CycList *CycAccess::getSpecSiblings(idStr cycFort, 
									idStr mt) {
	return theConnection.converseList("(spec-siblings " + cycFort + " " + 
									  mt + ")");
}

/**
 * Gets the list of all of the direct and indirect genls for the given idStr  collection.
 * 
 * @param cycFort the collection
 * 
 * @return the list of all of the direct and indirect genls for a idStr collection
 * 
 */
CycList *CycAccess::getAllGenls(idStr cycFort) {
	return theConnection.converseList("(all-genls-in-any-mt " + cycFort + ")");
}

/**
 * Gets the list of all of the direct and indirect genls for a idStr collection given a
 * relevant microtheory.
 * 
 * @param cycFort the collection
 * @param mt the relevant mt
 * 
 * @return the list of all of the direct and indirect genls for a idStr collection given a
 *         relevant microtheory
 * 
 */
CycList *CycAccess::getAllGenls(idStr cycFort, 
								idStr mt) {
	return theConnection.converseList("(all-genls " + cycFort + " " + 
									  mt + ")");
}
/**
 * Gets a list of all of the direct and indirect specs for a idStr collection.
 * 
 * @param cycFort the collection
 * 
 * @return the list of all of the direct and indirect specs for the given collection
 * 
 */
CycList *CycAccess::getAllSpecs(idStr cycFort) {
	return theConnection.converseList("(remove-duplicates (with-all-mts (all-specs " + 
									  cycFort + ")))");
}
/**
 * Gets the list of all of the direct and indirect specs for the given collection in the given
 * microtheory.
 * 
 * @param cycFort the collection
 * @param mt the microtheory
 * 
 * @return the list of all of the direct and indirect specs for the given collection
 * 
 */
CycList *CycAccess::getAllSpecs(idStr cycFort, 
								idStr mt) {
	return theConnection.converseList("(all-specs " + cycFort + " " + 
									  mt + ")");
}
/**
 * Gets the list of all of the direct and indirect genls for a idStr SPEC which are also specs
 * of idStr GENL.
 * 
 * @param spec the given collection
 * @param genl the more general collection
 * 
 * @return the list of all of the direct and indirect genls for a idStr SPEC which are also
 *         specs of idStr GENL
 * 
 */
CycList *CycAccess::getAllGenlsWrt(idStr spec, 
								   idStr genl) {
	return theConnection.converseList("(remove-duplicates (with-all-mts (all-genls-wrt " + 
									  spec + " " + genl + ")))");
}
/**
 * Gets the list of all of the direct and indirect genls for a idStr SPEC which are also specs
 * of idStr GENL.
 * 
 * @param spec the given collection
 * @param genl the more general collection
 * @param mt the relevant mt
 * 
 * @return the list of all of the direct and indirect genls for a idStr SPEC which are also
 *         specs of idStr GENL
 * 
 */
CycList *CycAccess::getAllGenlsWrt(idStr spec, 
								   idStr genl, 
								   idStr mt) {
	return theConnection.converseList("(all-genls-wrt " + spec + " " + genl + 
									  " " + mt + ")");
}
/**
 * Gets the list of all of the dependent specs for a idStr collection.  Dependent specs are
 * those direct and indirect specs of the collection such that every path connecting the spec to
 * a genl of the collection passes through the collection.  In a typical taxomonmy it is
 * expected that all-dependent-specs gives the same result as all-specs.
 * 
 * @param cycFort the given collection
 * 
 * @return the list of all of the dependent specs for the given idStr  collection
 * 
 */
CycList *CycAccess::getAllDependentSpecs(idStr cycFort) {
	return theConnection.converseList("(remove-duplicates (with-all-mts (all-dependent-specs " + 
									  cycFort + ")))");
}
/**
 * Gets the list of all of the dependent specs for a idStr collection.  Dependent specs are
 * those direct and indirect specs of the collection such that every path connecting the spec to
 * a genl of the collection passes through the collection.  In a typical taxomonmy it is
 * expected that all-dependent-specs gives the same result as all-specs.
 * 
 * @param cycFort the given collection
 * @param mt the relevant mt
 * 
 * @return the list of all of the dependent specs for the given idStr  collection
 * 
 */
CycList *CycAccess::getAllDependentSpecs(idStr cycFort, 
										 idStr mt) {
	return theConnection.converseList("(all-dependent-specs " + cycFort + " " + 
									  mt + ")");
}
/**
 * Gets the list with the specified number of sample specs of the given idStr  collection.
 * Attempts to return leaves that are maximally differet with regard to their all-genls.
 * 
 * @param cycFort the given collection
 * @param numberOfSamples the maximum number of sample specs returned
 * 
 * @return the list with the specified number of sample specs of the given idStr  collection
 * 
 */
CycList *CycAccess::getSampleLeafSpecs(idStr cycFort,int numberOfSamples) {
	//	 idStr noS;
//	 noS.Format("%d",numberOfSamples);
	// noS.("%d",numberOfSamples);
	return theConnection.converseList("(with-all-mts (sample-leaf-specs " + cycFort + " " + 
									  numberOfSamples + "))");
}
/**
 * Gets the list with the specified number of sample specs of the given idStr  collection.
 * Attempts to return leaves that are maximally differet with regard to their all-genls.
 * 
 * @param cycFort the given collection
 * @param numberOfSamples the maximum number of sample specs returned
 * @param mt the relevant mt
 * 
 * @return the list with the specified number of sample specs of the given idStr  collection
 * 
 */
CycList *CycAccess::getSampleLeafSpecs(idStr cycFort, 
									   int numberOfSamples, 
									   idStr mt) {
	//	 idStr noS;
//	 noS.Format("%d",numberOfSamples);
	return theConnection.converseList("(sample-leaf-specs " + cycFort + " " + numberOfSamples + 
									  " " + mt + ")");
}
/**
 * Gets the list of the justifications of why idStr COLLECTION1 and a idStr COLLECTION2
 * intersect. see getWhyGenl
 * 
 * @param collection1 the first collection
 * @param collection2 the second collection
 * 
 * @return the list of the justifications of why idStr COLLECTION1 and a idStr COLLECTION2
 *         intersect
 * 
 */
CycList *CycAccess::getWhyCollectionsIntersect(idStr collection1, 
											   idStr collection2) {
	return theConnection.converseList("(with-all-mts (why-collections-intersect? " + 
									  collection1 + " " + collection2 + "))");
}
/**
 * Gets the list of the justifications of why idStr COLLECTION1 and a idStr COLLECTION2
 * intersect. see getWhyGenl
 * 
 * @param collection1 the first collection
 * @param collection2 the second collection
 * @param mt the relevant mt
 * 
 * @return the list of the justifications of why idStr COLLECTION1 and a idStr COLLECTION2
 *         intersect
 * 
 */
CycList *CycAccess::getWhyCollectionsIntersect(idStr collection1, 
											   idStr collection2, 
											   idStr mt) {
	return theConnection.converseList("(why-collections-intersect? " + collection1 + " " + 
									  collection2 + " " + mt + ")");
}

/**
 * Gets the list of the collection leaves (most specific of the all-specs) for a idStr
 * collection.
 * 
 * @param cycFort the given collection
 * 
 * @return the list of the collection leaves (most specific of the all-specs) for a idStr
 *         collection
 * 
 */
CycList *CycAccess::getCollectionLeaves(idStr cycFort) {
	return theConnection.converseList("(with-all-mts (collection-leaves " + cycFort + "))");
}

/**
 * Gets the list of the collection leaves (most specific of the all-specs) for a idStr
 * collection.
 * 
 * @param cycFort the given collection
 * @param mt the relevant mt
 * 
 * @return the list of the collection leaves (most specific of the all-specs) for a idStr
 *         collection
 * 
 */
CycList *CycAccess::getCollectionLeaves(idStr cycFort, 
										idStr mt) {
	return theConnection.converseList("(collection-leaves " + cycFort + " " + 
									  mt + ")");
}
/**
 * Gets the list of the collections asserted to be disjoint with a idStr collection.
 * 
 * @param cycFort the given collection
 * 
 * @return the list of the collections asserted to be disjoint with a idStr collection
 * 
 */
CycList *CycAccess::getLocalDisjointWith(idStr cycFort) {
	return theConnection.converseList("(with-all-mts (local-disjoint-with " + cycFort + "))");
}
/**
 * Gets the list of the collections asserted to be disjoint with a idStr collection.
 * 
 * @param cycFort the given collection
 * @param mt the relevant mt
 * 
 * @return the list of the collections asserted to be disjoint with a idStr collection
 * 
 */
CycList *CycAccess::getLocalDisjointWith(idStr cycFort, 
										 idStr mt) {
	return theConnection.converseList("(local-disjoint-with " + cycFort + " " + 
									  mt + ")");
}

/**
 * Gets the list of the most specific collections (having no subsets) which contain a idStr
 * term.
 * 
 * @param cycFort the given term
 * 
 * @return the list of the most specific collections (having no subsets) which contain a idStr
 *         term
 * 
 */
CycList *CycAccess::getMinIsas(idStr cycFort) {
	return theConnection.converseList("(with-all-mts (min-isa " + cycFort + "))");
}
/**
 * Gets the list of the most specific collections (having no subsets) which contain a idStr
 * term.
 * 
 * @param cycFort the given term
 * @param mt the relevant mt
 * 
 * @return the list of the most specific collections (having no subsets) which contain a idStr
 *         term
 * 
 */
CycList *CycAccess::getMinIsas(idStr cycFort, 
							   idStr mt) {
	return theConnection.converseList("(min-isa " + cycFort + " " + 
									  mt + ")");
}
/**
 * Gets the list of the instances (who are individuals) of a idStr collection.
 * 
 * @param cycFort the given collection
 * 
 * @return the list of the instances (who are individuals) of a idStr collection
 * 
 */
CycList *CycAccess::getInstances(idStr cycFort) {
	return theConnection.converseList("(with-all-mts (instances " + cycFort + "))");
}
/**
 * Gets the list of the instances (who are individuals) of a idStr collection.
 * 
 * @param cycFort the given collection
 * @param mt the relevant mt
 * 
 * @return the list of the instances (who are individuals) of a idStr collection
 * 
 */
CycList *CycAccess::getInstances(idStr cycFort, 
								 idStr mt) {
	return theConnection.converseList("(instances " + cycFort + " " + 
									  mt + ")");
}
/**
 * Gets the list of the instance siblings of a idStr, for all collections of which it is an
 * instance.
 * 
 * @param cycFort the given term
 * 
 * @return the list of the instance siblings of a idStr, for all collections of which it is an
 *         instance
 * 
 */
CycList *CycAccess::getInstanceSiblings(idStr cycFort) {
	return theConnection.converseList("(with-all-mts (instance-siblings " + cycFort + "))");
}
/**
 * Gets the list of the instance siblings of a idStr, for all collections of which it is an
 * instance.
 * 
 * @param cycFort the given term
 * @param mt the relevant mt
 * 
 * @return the list of the instance siblings of a idStr, for all collections of which it is an
 *         instance
 * 
 */
CycList *CycAccess::getInstanceSiblings(idStr cycFort, 
										idStr mt) {
	return theConnection.converseList("(instance-siblings " + cycFort + " " + 
									  mt + ")");
}
/**
 * Gets the list of the collections of which the idStr is directly and indirectly an instance.
 * 
 * @param cycFort the given term
 * 
 * @return the list of the collections of which the idStr is directly and indirectly an
 *         instance
 * 
 */
CycList *CycAccess::getAllIsa(idStr cycFort) {
	return theConnection.converseList("(all-isa-in-any-mt " + cycFort + ")");
}
/**
 * Gets the list of the collections of which the idStr is directly and indirectly an instance.
 * 
 * @param cycFort the given term
 * @param mt the relevant mt
 * 
 * @return the list of the collections of which the idStr is directly and indirectly an
 *         instance
 * 
 */
CycList *CycAccess::getAllIsa(idStr cycFort, 
							  idStr mt) {
	return theConnection.converseList("(all-isa " + cycFort + " " + 
									  mt + ")");
}
/**
 * Gets a list of all the direct and indirect instances (individuals) for a idStr collection.
 * 
 * @param cycFort the collection for which all the direct and indirect instances (individuals)
 *        are sought
 * 
 * @return the list of all the direct and indirect instances (individuals) for the given
 *         collection
 * 
 */
CycList *CycAccess::getAllInstances(idStr cycFort) {
	return theConnection.converseList("(all-instances-in-all-mts " + cycFort + ")");
}
/**
 * Gets a list of all the direct and indirect instances (individuals) for a idStr collection in
 * the given microtheory.
 * 
 * @param cycFort the collection for which all the direct and indirect instances (individuals)
 *        are sought
 * @param mt the relevant mt
 * 
 * @return the list of all the direct and indirect instances (individuals) for the
 * 
 */
CycList *CycAccess::getAllInstances(idStr cycFort, 
									idStr mt) {
	return theConnection.converseList("(all-instances " + cycFort + " " + 
									  mt + ")");
}
/**
 * Gets the list of the justifications of why idStr TERM is an instance of idStr COLLECTION.
 * getWhyIsa("Brazil", "Country") --> "(((#$isa #$Brazil #$IndependentCountry) :TRUE) (#$genls
 * #$IndependentCountry #$Country) :TRUE))
 * 
 * @param spec the specialized collection
 * @param genl the more general collection
 * 
 * @return the list of the justifications of why idStr TERM is an instance of idStr
 *         COLLECTION
 * 
 */
CycList *CycAccess::getWhyIsa(idStr spec, 
							  idStr genl) {
	return theConnection.converseList("(with-all-mts (why-isa? " + spec + " " + 
									  genl + "))");
}
/**
 * Gets the list of the justifications of why idStr TERM is an instance of idStr COLLECTION.
 * getWhyIsa("Brazil", "Country") --> "(((#$isa #$Brazil #$IndependentCountry) :TRUE) (#$genls
 * #$IndependentCountry #$Country) :TRUE))
 * 
 * @param spec the specialized collection
 * @param genl the more general collection
 * @param mt the relevant mt
 * 
 * @return the list of the justifications of why idStr TERM is an instance of idStr
 *         COLLECTION
 * 
 */
CycList *CycAccess::getWhyIsa(idStr spec, 
							  idStr genl, 
							  idStr mt) {
	return theConnection.converseList("(why-isa? " + spec + " " + genl + " " + 
									  mt + ")");
}

/**
 * Gets the list of the genlPreds for a CycConstant predicate.
 * 
 * @param predicate the given predicate term
 * 
 * @return the list of the more general predicates for the given predicate
 * 
 */
CycList *CycAccess::getGenlPreds(idStr predicate) {
	return theConnection.converseList("(remove-duplicates (with-all-mts (genl-predicates " + 
									  predicate + ")))");
}
/**
  * Gets the list of the genlPreds for a CycConstant predicate.
  * 
  * @param predicate the given predicate term
  * @param mt the relevant mt
  * 
  * @return the list of the more general predicates for the given predicate
  * 
  */
CycList *CycAccess::getGenlPreds(idStr predicate, 
								 idStr mt) {
	return theConnection.converseList("(genl-predicates " + predicate + " " + 
									  mt + ")");
}
/**
 * Gets the list of all of the genlPreds for a CycConstant predicate, using an upward closure.
 * 
 * @param predicate the predicate for which all the genlPreds are obtained
 * 
 * @return a list of all of the genlPreds for a CycConstant predicate, using an upward closure
 * 
 */
CycList *CycAccess::getAllGenlPreds(idStr predicate) {
	return theConnection.converseList("(remove-duplicates (with-all-mts (all-genl-predicates " + 
									  predicate + ")))");
}
/**
 * Gets the list of all of the genlPreds for a CycConstant predicate, using an upward closure.
 * 
 * @param predicate the predicate for which all the genlPreds are obtained
 * @param mt the relevant mt
 * 
 * @return a list of all of the genlPreds for a CycConstant predicate, using an upward closure
 * 
 */
CycList *CycAccess::getAllGenlPreds(idStr predicate, 
									idStr mt) {
	return theConnection.converseList("(all-genl-predicates " + predicate + " " + 
									  mt + ")");
}
/**
 * Gets the list of all of the direct and indirect specs-preds for the given predicate in all
 * microtheories.
 * 
 * @param cycFort the predicate
 * 
 * @return the list of all of the direct and indirect spec-preds for the given predicate in all
 *         microtheories.
 * 
 */
CycList *CycAccess::getAllSpecPreds(idStr cycFort) {
	return theConnection.converseList("(remove-duplicates (with-all-mts (all-spec-predicates " + 
									  cycFort + ")))");
}
/**
 * Gets the list of all of the direct and indirect specs-preds for the given predicate in the
 * given microtheory.
 * 
 * @param cycFort the predicate
 * @param mt the microtheory
 * 
 * @return the list of all of the direct and indirect spec-preds for the given predicate
 * 
 */
CycList *CycAccess::getAllSpecPreds(idStr cycFort, 
									idStr mt) {
	return theConnection.converseList("(all-spec-predicates " + cycFort + " " + 
									  mt + ")");
}
/**
 * Gets the list of all of the direct and indirect specs-inverses for the given predicate in all
 * microtheories.
 * 
 * @param cycFort the predicate
 * 
 * @return the list of all of the direct and indirect spec-inverses for the given predicate in
 *         all microtheories.
 * 
 */
CycList *CycAccess::getAllSpecInverses(idStr cycFort) {
	return theConnection.converseList("(remove-duplicates (with-all-mts (all-spec-inverses " + 
									  cycFort + ")))");
}
/**
 * Gets the list of all of the direct and indirect specs-inverses for the given predicate in the
 * given microtheory.
 * 
 * @param cycFort the predicate
 * @param mt the microtheory
 * 
 * @return the list of all of the direct and indirect spec-inverses for the given predicate
 * 
 */
CycList *CycAccess::getAllSpecInverses(idStr cycFort, 
									   idStr mt) {
	return theConnection.converseList("(all-spec-inverses " + cycFort + " " + 
									  mt + ")");
}
/**
 * Gets the list of all of the direct and indirect specs-mts for the given microtheory in mt-mt
 * (currently #$UniversalVocabularyMt).
 * 
 * @param mt the microtheory
 * 
 * @return the list of all of the direct and indirect specs-mts for the given microtheory in
 *         mt-mt (currently #$UniversalVocabularyMt)
 * 
 */
CycList *CycAccess::getAllSpecMts(idStr mt) {
	return theConnection.converseList("(all-spec-mts " + mt + ")");
}

/**
 * Gets a list of the arg1Isas for a CycConstant predicate.
 * 
 * @param predicate the predicate for which argument 1 contraints are sought.
 * 
 * @return the list of the arg1Isas for a CycConstant predicate
 * 
 */
CycList *CycAccess::getArg1Isas(idStr predicate) {

	return theConnection.converseList("(remove-duplicates (with-all-mts (arg1-isa " + 
									  predicate + ")))");
}
/**
 * Gets the list of the arg1Isas for a CycConstant predicate given an mt.
 * 
 * @param predicate the predicate for which argument 1 contraints are sought.
 * @param mt the relevant microtheory
 * 
 * @return the list of the arg1Isas for a CycConstant predicate given an mt
 * 
 */
CycList *CycAccess::getArg1Isas(idStr predicate, 
								idStr mt) {
	return theConnection.converseList("(arg1-isa " + predicate + " " + 
									  mt + ")");
}
/**
 * Gets a list of the arg2Isas for a CycConstant predicate.
 * 
 * @param predicate the predicate for which argument 2 contraints are sought.
 * 
 * @return the list of the arg1Isas for a CycConstant predicate
 * 
 */
CycList *CycAccess::getArg2Isas(idStr predicate) {

	return theConnection.converseList("(remove-duplicates (with-all-mts (arg2-isa " + 
									  predicate + ")))");
}
/**
 * Gets the list of the arg2Isas for a CycConstant predicate given an mt.
 * 
 * @param predicate the predicate for which argument 2 contraints are sought.
 * @param mt the relevant microtheory
 * 
 * @return the list of the arg2Isas for a CycConstant predicate given an mt
 * 
 */
CycList *CycAccess::getArg2Isas(idStr predicate, 
								idStr mt) {
	return theConnection.converseList("(arg2-isa " + predicate + " " + 
									  mt + ")");
}
/**
 * Gets a list of the arg3Isas for a CycConstant predicate.
 * 
 * @param predicate the predicate for which argument 3 contraints are sought.
 * 
 * @return the list of the arg1Isas for a CycConstant predicate
 * 
 */
CycList *CycAccess::getArg3Isas(idStr predicate) {
	return theConnection.converseList("(remove-duplicates (with-all-mts (arg3-isa " + 
									  predicate + ")))");
}
/**
 * Gets the list of the arg3Isas for a CycConstant predicate given an mt.
 * 
 * @param predicate the predicate for which argument 3 contraints are sought.
 * @param mt the relevant microtheory
 * 
 * @return the list of the arg1Isas for a CycConstant predicate given an mt
 * 
 */
CycList *CycAccess::getArg3Isas(idStr predicate, 
								idStr mt) {
	return theConnection.converseList("(arg3-isa " + predicate + " " + 
									  mt + ")");
}
/**
 * Gets a list of the arg4Isas for a CycConstant predicate.
 * 
 * @param predicate the predicate for which argument 4 contraints are sought.
 * 
 * @return the list of the arg4Isas for a CycConstant predicate
 * 
 */
CycList *CycAccess::getArg4Isas(idStr predicate) {
	return theConnection.converseList("(remove-duplicates (with-all-mts (arg4-isa " + 
									  predicate + ")))");
}
/**
 * Gets the list of the arg4Isas for a CycConstant predicate given an mt.
 * 
 * @param predicate the predicate for which argument 4 contraints are sought.
 * @param mt the relevant microtheory
 * 
 * @return the list of the arg4Isas for a CycConstant predicate given an mt
 * 
 */
CycList *CycAccess::getArg4Isas(idStr predicate, 
								idStr mt) {
	return theConnection.converseList("(arg4-isa " + predicate + " " + 
									  mt + ")");
}
/**
 * Gets a list of the argNIsas for a CycConstant predicate.
 * 
 * @param predicate the predicate for which argument N contraints are sought.
 * @param argPosition the argument position of argument N
 * 
 * @return the list of the argNIsas for a CycConstant predicate
 * 
 */
CycList *CycAccess::getArgNIsas(idStr predicate, 
								int argPosition) {
	//idStr argP;
	idStr command;
//	 argP.Format("%d",argPosition);

	command = "(remove-duplicates   (with-all-mts  (argn-isa " + 
			  predicate + " " + argPosition + ")))";

	return theConnection.converseList(command);
}
/**
 * Gets the list of the argNIsas for a CycConstant predicate given an mt.
 * 
 * @param predicate the predicate for which argument contraints are sought.
 * @param argPosition the argument position of argument N
 * @param mt the relevant microtheory
 * 
 * @return the list of the arg1Isas for a CycConstant predicate given an mt
 * 
 */
CycList *CycAccess::getArgNIsas(idStr predicate, 
								int argPosition, 
								idStr mt) {
	//idStr argP;
	//argP.Format("%d",argPosition);
	idStr command = "(remove-duplicates (with-all-mts (argn-isa " + predicate + "  " 
					+argPosition + " " + mt +")))";
	return theConnection.converseList(command);
}

/**
 * Gets the list of the interArgIsa1-2 isa constraint pairs for the given predicate.  Each item
 * of the returned list is a pair (arg1-isa arg2-isa) which means that when (#$isa arg1
 * arg1-isa) holds, (#$isa arg2 arg2-isa) must also hold for (predicate arg1 arg2 ..) to be well
 * formed.
 * 
 * @param predicate the predicate for interArgIsa1-2 contraints are sought.
 * 
 * @return the list of the interArgIsa1-2 isa constraint pairs for the given predicate
 * 
 */
CycList *CycAccess::getInterArgIsa1_2s(idStr predicate) {
	idStr command = "(remove-duplicates (with-all-mts (inter-arg-isa1-2 " + 
					predicate + ")))";

	return theConnection.converseList(command);
}
/**
 * Gets the list of the interArgIsa1-2 isa constraint pairs for the given predicate.  Each item
 * of the returned list is a pair (arg1-isa arg2-isa) which means that when (#$isa arg1
 * arg1-isa) holds, (#$isa arg2 arg2-isa) must also hold for (predicate arg1 arg2 ..) to be well
 * formed.
 * 
 * @param predicate the predicate for interArgIsa1-2 contraints are sought.
 * @param mt the relevant inference microtheory
 * 
 * @return the list of the interArgIsa1-2 isa constraint pairs for the given predicate
 * 
 */
CycList *CycAccess::getInterArgIsa1_2s(idStr predicate, 
									   idStr mt) {
	idStr command = "(remove-duplicates (with-all-mts   (inter-arg-isa1-2 " + predicate + "      " + 
					mt + ")))";

	return theConnection.converseList(command);
}

/**
 * Gets the list of the resultIsa for a CycConstant function.
 * 
 * @param function the given function term
 * 
 * @return the list of the resultIsa for a CycConstant function
 * 
 */
CycList *CycAccess::getResultIsas(idStr function) {
	return theConnection.converseList("(remove-duplicates (with-all-mts (result-isa " + 
									  function + ")))");
}
/**
 * Gets the list of the resultIsa for a CycConstant function.
 * 
 * @param function the given function term
 * @param mt the relevant mt
 * 
 * @return the list of the resultIsa for a CycConstant function
 * 
 */
CycList *CycAccess::getResultIsas(idStr function, 
								  idStr mt) {
	return theConnection.converseList("(result-isa " + function + " " + 
									  mt + ")");
}
/**
 * Gets the list of the argNGenls for a CycConstant predicate.
 * 
 * @param predicate the given predicate term
 * @param argPosition the argument position for which the genls argument constraints are sought
 *        (position 1 = first argument)
 * 
 * @return the list of the argNGenls for a CycConstant predicate
 * 
 */
CycList *CycAccess::getArgNGenls(idStr predicate, 
								 int argPosition) {
// 	 idStr argP;
//	 argP.Format("%d",argPosition);
	return theConnection.converseList("(remove-duplicates (with-all-mts (argn-genl " + 
									  predicate + " " + argPosition + ")))");
}
/**
 * Gets the list of the argNGenls for a CycConstant predicate.
 * 
 * @param predicate the given predicate term
 * @param argPosition the argument position for which the genls argument constraints are sought
 *        (position 1 = first argument)
 * @param mt the relevant mt
 * 
 * @return the list of the argNGenls for a CycConstant predicate
 * 
 */
CycList *CycAccess::getArgNGenls(idStr predicate, 
								 int argPosition, 
								 idStr mt) {
	// idStr argP;
//	 argP.Format("%d",argPosition);
	return theConnection.converseList("(argn-genl " + predicate + " " + argPosition + " " + 
									  mt + ")");
}
/**
 * Gets a list of the arg1Formats for a CycConstant predicate.
 * 
 * @param predicate the given predicate term
 * 
 * @return a list of the arg1Formats for a CycConstant predicate
 * 
 */
CycList *CycAccess::getArg1Formats(idStr predicate) {

	return theConnection.converseList("(with-all-mts (arg1-format " + predicate + "))");
}
/**
 * Gets a list of the arg1Formats for a CycConstant predicate.
 * 
 * @param predicate the given predicate term
 * @param mt the relevant mt
 * 
 * @return a list of the arg1Formats for a CycConstant predicate
 * 
 */
CycList *CycAccess::getArg1Formats(idStr predicate, 
								   idStr mt) {
	return theConnection.converseList("(arg1-format " + predicate + " " + 
									  mt + ")");
}
/**
 * Gets a list of the arg2Formats for a CycConstant predicate.
 * 
 * @param predicate the given predicate term
 * 
 * @return a list of the arg2Formats for a CycConstant predicate
 * 
 */
CycList *CycAccess::getArg2Formats(idStr predicate) {

	return theConnection.converseList("(with-all-mts (arg2-format " + predicate + "))");
}
/**
 * Gets a list of the arg2Formats for a CycConstant predicate.
 * 
 * @param predicate the given predicate term
 * @param mt the relevant mt
 * 
 * @return a list of the arg2Formats for a CycConstant predicate
 * 
 */
CycList *CycAccess::getArg2Formats(idStr predicate, 
								   idStr mt) {
	return theConnection.converseList("(arg2-format " + predicate + " " + 
									  mt + ")");
}
/**
 * Gets a list of the disjointWiths for a idStr.
 * 
 * @param cycObject the given collection term
 * 
 * @return a list of the disjointWiths for a idStr
 * 
 */
CycList *CycAccess::getDisjointWiths(idStr cycObject) {


	return theConnection.converseList("(remove-duplicates (with-all-mts (local-disjoint-with " + 
									  cycObject + ")))");
}
/**
 * Gets a list of the disjointWiths for a idStr.
 * 
 * @param cycFort the given collection term
 * @param mt the relevant mt
 * 
 * @return a list of the disjointWiths for a idStr
 * 
 */
CycList *CycAccess::getDisjointWiths(idStr cycFort, 
									 idStr mt) {
	return theConnection.converseList("(local-disjoint-with " + cycFort + " " + 
									  mt + ")");
}
/**
 * Returns <tt>true</tt> iff the query is true in the knowledge base.
 * 
 * @deprecated
 * @param query the query to be asked in the knowledge base
 * @param mt the microtheory in which the query is asked
 * 
 * @return <tt>true</tt> iff the query is true in the knowledge base
 * 
 */
bool CycAccess::isQueryTrue(idStr query, idStr mt) {
	idStr  cQuery;
	idStr  sReply;
	CycList *cResponse;
	bool  cReply;
	cQuery.Append("(cyc-query '");
	cQuery.Append(query);
	cQuery.Append(" '");
	cQuery.Append(mt);
	cQuery.Append("'(:backchain 1 :number 1 :time 3 :depth10))");
	cResponse = converseObject(cQuery);
	sReply = convertBoolean(cResponse->toString());
	cReply = (strstr(sReply,"TRUE")) ? TRUE : FALSE;
	cReply=cReply || (cResponse->size()>1);
	delete cResponse;
	return cReply;
}

long CycAccess::numIndex(idStr formula) {
	idStr  cQuery;
	CycList *cResponse;
	idStr  sReply;
	int response=0;
	long num=0;
	cQuery.Append("(num-index ");
	cQuery.Append(formula);
	cQuery.Append(")");
	cResponse=converseList(cQuery);
	sReply = cResponse->toString();
	sReply.Replace("(200 ","");
	sReply.Replace(")","");
	sscanf(sReply,"%ld",&num);
	delete cResponse;
	return num;
}

//------------------------------------------------------------------
/**
 * Gets the paraphrase for a Cyc assertion.
 * 
 * @param assertion the assertion formula
 * 
 * @return the paraphrase for a Cyc assertion
 *  
*/
idStr CycAccess::getParaphrase(idStr assertion) {
	//return theConnection.converseString("(with-precise-paraphrase-on (generate-phrase " + assertion + 
	//                      "))");
	idStr paraphrase;
	CycList *pList;
	//pList=theConnection.converseList("(generate-text-w/sentential-force '" + assertion + 
	//                      " :declarative)");
	pList=theConnection.converseList("(with-precise-paraphrase-on (generate-phrase '" + assertion +
									 " ))");

	paraphrase =pList->cellToString((cell_t *)_Cdr(pList->List));
	delete pList;
	return paraphrase;
}


idStr CycAccess::paraphrase(CycList *assertionList) {
	//return theConnection.converseString("(with-precise-paraphrase-on (generate-phrase " + assertion + 
	//                      "))");
	idStr retParaphrase;
	idStr assertion;
	int listSize;
	int i;

	retParaphrase="";
	listSize=assertionList->size();
	for (i=1;i<listSize;i++) {
		assertion = assertionList->cellToString( assertionList->cellN(i));
		assertion.Replace ("(?REPLY .","(");
		assertion.Replace ("(","");
		assertion.Replace (")","");
//		printf("--- paraphrase(%d) assertion:[%s]",i,assertion);
		retParaphrase=retParaphrase+" "+getParaphrase(assertion);
	}
	return retParaphrase;
}

idStr CycAccess::convertBoolean(idStr message) {
	idStr retVal;
	retVal=message;
	if (!idStr::Cmpn(message,"(NIL . NIL)",9)) retVal="FALSE";
	if (!idStr::Cmpn(message,"(T . T )",9))	retVal="TRUE";
	if (!idStr::Cmpn(message,"(200 T)",8)) retVal="TRUE";
	if (!idStr::Cmpn(message,"(200 (NIL))",9)) retVal="TRUE";
	if (!idStr::Cmpn(message,"(200 ((",7)) retVal="TRUE";
	if (!idStr::Cmpn(message,"((#$200 NIL)",7))	retVal="FALSE";
	if (!idStr::Cmpn(message,"((#$200 NIL)",7))	retVal="FALSE";
	if (!idStr::Cmpn(message,"(200 NIL)",9)) retVal="FALSE";

	return retVal;
}

/**
 * Gets the imprecise paraphrase for a Cyc assertion.
 * 
 * @param assertionString the assertion formula
 * 
 * @return the imprecise paraphrase for a Cyc assertion
 * 
 */
idStr CycAccess::getImpreciseParaphrase(idStr assertionString) {
	return theConnection.converseString("(with-precise-paraphrase-off (generate-phrase '" + 
										assertionString + "))");
}

/**
 * Gets the English parapharse of the justifications of why CycFort SPEC is a SPEC of CycFort
 * GENL. getWhyGenlParaphrase("Dog", "Animal") --> "a dog is a kind of canine" "a canine is a
 * kind of non-human animal" "a non-human animal is a kind of animal"
 * 
 * @param spec the specialized collection
 * @param genl the more general collection
 * 
 * @return the English parapharse of the justifications of why CycFort SPEC is a SPEC of CycFort
 *         GENL
 * 
 * @throws UnknownHostException if cyc server host not found on the network
 * @throws IOException if a data communication error occurs
 * @throws CycApiException if the api request results in a cyc server error
 */











