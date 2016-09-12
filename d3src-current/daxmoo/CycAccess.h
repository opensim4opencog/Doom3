#ifndef __INC_CYCACCESS_H__
#define __INC_CYCACCESS_H__

//#include <afx.h>
//#include <afxwin.h>
//#include <afxinet.h>
#include <windows.h>
#include <winsock.h>

#if _MSC_VER > 1000
#pragma once
#endif // _MSC_VER > 1000

//#include "stdlib.h"
//#include "stdio.h"
//#include "string.h"
//#include "math.h"

//#include "support.h"
#include "CycConnection.h"

#ifndef CYC_ASCII_MODE

#define CYC_ASCII_MODE  1
#define CYC_BINARY_MODE  2
#define DEFAULT_CYC_HOSTNAME "cycserver"
#define DEFAULT_CYC_PORT 3600
#define CYC_HTTP_PORT_OFFSET  0
#define CYC_ASCII_PORT_OFFSET  1
#define CYC_CFASL_PORT_OFFSET  14

#endif

class CycAccess
{
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

public:
	CycAccess();
  ~CycAccess();

// Operations
public:
	CycConnection theConnection;	

// Methods
void CycAccess::setConnection(const char *hname,int pnum);
void CycAccess::open();
void CycAccess::open(const char *hname,int pnum);
void CycAccess::close();
CycConnection* CycAccess::getCycConnection ();

idStr /*c*/ CycAccess::converseString(idStr /*c*/ message);
CycList *CycAccess::converseList(idStr /*c*/ message);
CycList *CycAccess::converseObject (idStr /*c*/ message);
bool CycAccess::converseBoolean(idStr /*c*/ message);

idStr /*c*/ CycAccess::assertString (idStr /*c*/ cyclString, idStr /*c*/ mt);

idStr /*c*/ CycAccess::getConstantByName(idStr /*c*/ CConst);
idStr /*c*/ CycAccess::getKnownConstantByName(idStr /*c*/ CConst);

bool CycAccess::isa(idStr /*c*/ term, idStr /*c*/ collection);
bool CycAccess::isGenlOf(idStr /*c*/ genl,idStr /*c*/ spec);
bool CycAccess::isSpecOf(idStr /*c*/ spec,idStr /*c*/ genls);

bool CycAccess::isGenlPredOf(idStr /*c*/ genlPred, idStr /*c*/ specPred, idStr /*c*/ mt);
bool CycAccess::isGenlPredOf(idStr /*c*/ genlPred, idStr /*c*/ specPred);
bool CycAccess::isGenlInverseOf(idStr /*c*/ genlPred, idStr /*c*/ specPred, idStr /*c*/ mt);
bool CycAccess::isGenlInverseOf(idStr /*c*/ genlPred, idStr /*c*/ specPred);
bool CycAccess::isGenlMtOf(idStr /*c*/ genlMt, idStr /*c*/ specMt);
bool CycAccess::areTacitCoextensional(idStr /*c*/ collection1, idStr /*c*/ collection2);
bool CycAccess::areTacitCoextensional(idStr /*c*/ collection1, idStr /*c*/ collection2, idStr /*c*/ mt);
bool CycAccess::areIntersecting(idStr /*c*/ collection1, idStr /*c*/ collection2);
bool CycAccess::areIntersecting(idStr /*c*/ collection1, idStr /*c*/ collection2, idStr /*c*/ mt);
bool CycAccess::areHierarchical(idStr /*c*/ collection1, idStr /*c*/ collection2);
bool CycAccess::areHierarchical(idStr /*c*/ collection1, idStr /*c*/ collection2, idStr /*c*/ mt);
bool CycAccess::areDisjoint(idStr /*c*/ collection1, idStr /*c*/ collection2, idStr /*c*/ mt);
bool CycAccess::areDisjoint(idStr /*c*/ collection1, idStr /*c*/ collection2);
bool CycAccess::isMicrotheory(idStr /*c*/ CConst);
bool CycAccess::isCollection(idStr /*c*/ CConst);
bool CycAccess::isIndividual(idStr /*c*/ CConst);
bool CycAccess::isFunction(idStr /*c*/ CConst);
bool CycAccess::isPredicate(idStr /*c*/ CConst);
bool CycAccess::isUnaryPredicate(idStr /*c*/ CConst);
bool CycAccess::isBinaryPredicate(idStr /*c*/ CConst);
bool CycAccess::isValidConstantName(idStr /*c*/ candidateName);
bool CycAccess::isConstantNameAvailable(idStr /*c*/ candidateName);
bool CycAccess::isPublicConstant(idStr /*c*/ CConst);
bool CycAccess::isFormulaWellFormed(idStr /*c*/ formula,idStr /*c*/ mt);
bool CycAccess::isCycLNonAtomicReifableTerm(idStr /*c*/ formula);
bool CycAccess::isCycLNonAtomicUnreifableTerm(idStr /*c*/ formula);

long CycAccess::numIndex(idStr /*c*/ formula);


CycList *CycAccess::askWithVariable (idStr /*c*/ query,idStr /*c*/ variable,idStr /*c*/ mt );
CycList *CycAccess::getIsas(idStr /*c*/ cycObject);
CycList *CycAccess::getGenls(idStr /*c*/ cycObject);
CycList *CycAccess::getDenotationList(idStr /*c*/ cycObject);
CycList *CycAccess::askList(idStr /*c*/ cycObject);

CycList *CycAccess::getIsas(idStr /*c*/ cycFort, idStr /*c*/ mt);
CycList *CycAccess::getGenls(idStr /*c*/ cycFort, idStr /*c*/ mt);
CycList *CycAccess::getMinGenls(idStr /*c*/ cycFort);
CycList *CycAccess::getMinGenls(idStr /*c*/ cycFort, idStr /*c*/ mt);
CycList *CycAccess::getSpecs(idStr /*c*/ cycFort);
CycList *CycAccess::getSpecs(idStr /*c*/ cycFort, idStr /*c*/ mt);
CycList *CycAccess::getMaxSpecs(idStr /*c*/ cycFort);
CycList *CycAccess::getMaxSpecs(idStr /*c*/ cycFort, idStr /*c*/ mt);
CycList *CycAccess::getGenlSiblings(idStr /*c*/ cycFort);
CycList *CycAccess::getGenlSiblings(idStr /*c*/ cycFort, idStr /*c*/ mt);
CycList *CycAccess::getSiblings(idStr /*c*/ cycFort);
CycList *CycAccess::getSiblings(idStr /*c*/ cycFort, idStr /*c*/ mt);
CycList *CycAccess::getSpecSiblings(idStr /*c*/ cycFort);
CycList *CycAccess::getSpecSiblings(idStr /*c*/ cycFort, idStr /*c*/ mt);
CycList *CycAccess::getAllGenls(idStr /*c*/ cycFort);
CycList *CycAccess::getAllGenls(idStr /*c*/ cycFort, idStr /*c*/ mt);
CycList *CycAccess::getAllSpecs(idStr /*c*/ cycFort);
CycList *CycAccess::getAllSpecs(idStr /*c*/ cycFort, idStr /*c*/ mt);
CycList *CycAccess::getAllGenlsWrt(idStr /*c*/ spec, idStr /*c*/ genl);
CycList *CycAccess::getAllGenlsWrt(idStr /*c*/ spec, idStr /*c*/ genl, idStr /*c*/ mt);
CycList *CycAccess::getAllDependentSpecs(idStr /*c*/ cycFort);
CycList *CycAccess::getAllDependentSpecs(idStr /*c*/ cycFort, idStr /*c*/ mt);
CycList *CycAccess::getSampleLeafSpecs(idStr /*c*/ cycFort, int numberOfSamples);
CycList *CycAccess::getSampleLeafSpecs(idStr /*c*/ cycFort, int numberOfSamples, idStr /*c*/ mt);
CycList *CycAccess::getCollectionLeaves(idStr /*c*/ cycFort);
CycList *CycAccess::getCollectionLeaves(idStr /*c*/ cycFort, idStr /*c*/ mt);
CycList *CycAccess::getLocalDisjointWith(idStr /*c*/ cycFort);
CycList *CycAccess::getLocalDisjointWith(idStr /*c*/ cycFort, idStr /*c*/ mt);
CycList *CycAccess::getMinIsas(idStr /*c*/ cycFort);
CycList *CycAccess::getMinIsas(idStr /*c*/ cycFort, idStr /*c*/ mt);
CycList *CycAccess::getInstances(idStr /*c*/ cycFort);
CycList *CycAccess::getInstances(idStr /*c*/ cycFort, idStr /*c*/ mt);
CycList *CycAccess::getInstanceSiblings(idStr /*c*/ cycFort);
CycList *CycAccess::getInstanceSiblings(idStr /*c*/ cycFort, idStr /*c*/ mt);
CycList *CycAccess::getAllIsa(idStr /*c*/ cycFort);
CycList *CycAccess::getAllIsa(idStr /*c*/ cycFort, idStr /*c*/ mt);
CycList *CycAccess::getAllInstances(idStr /*c*/ cycFort);
CycList *CycAccess::getAllInstances(idStr /*c*/ cycFort, idStr /*c*/ mt);
CycList *CycAccess::getGenlPreds(idStr /*c*/ predicate);
CycList *CycAccess::getGenlPreds(idStr /*c*/ predicate, idStr /*c*/ mt);
CycList *CycAccess::getAllGenlPreds(idStr /*c*/ predicate);
CycList *CycAccess::getAllGenlPreds(idStr /*c*/ predicate, idStr /*c*/ mt);
CycList *CycAccess::getAllSpecPreds(idStr /*c*/ cycFort);
CycList *CycAccess::getAllSpecPreds(idStr /*c*/ cycFort, idStr /*c*/ mt);
CycList *CycAccess::getAllSpecInverses(idStr /*c*/ cycFort);
CycList *CycAccess::getAllSpecInverses(idStr /*c*/ cycFort, idStr /*c*/ mt);
CycList *CycAccess::getAllSpecMts(idStr /*c*/ mt);
CycList *CycAccess::getArg1Isas(idStr /*c*/ predicate);
CycList *CycAccess::getArg1Isas(idStr /*c*/ predicate, idStr /*c*/ mt);
CycList *CycAccess::getArg2Isas(idStr /*c*/ predicate);
CycList *CycAccess::getArg2Isas(idStr /*c*/ predicate, idStr /*c*/ mt);
CycList *CycAccess::getArg3Isas(idStr /*c*/ predicate);
CycList *CycAccess::getArg3Isas(idStr /*c*/ predicate, idStr /*c*/ mt);
CycList *CycAccess::getArg4Isas(idStr /*c*/ predicate);
CycList *CycAccess::getArg4Isas(idStr /*c*/ predicate, idStr /*c*/ mt);
CycList *CycAccess::getArgNIsas(idStr /*c*/ predicate, int argPosition);
CycList *CycAccess::getArgNIsas(idStr /*c*/ predicate, int argPosition, idStr /*c*/ mt);
CycList *CycAccess::getInterArgIsa1_2s(idStr /*c*/ predicate);
CycList *CycAccess::getInterArgIsa1_2s(idStr /*c*/ predicate, idStr /*c*/ mt);
CycList *CycAccess::getResultIsas(idStr /*c*/ function);
CycList *CycAccess::getResultIsas(idStr /*c*/ function, idStr /*c*/ mt);
CycList *CycAccess::getArgNGenls(idStr /*c*/ predicate, int argPosition);
CycList *CycAccess::getArgNGenls(idStr /*c*/ predicate, int argPosition, idStr /*c*/ mt);
CycList *CycAccess::getArg1Formats(idStr /*c*/ predicate);
CycList *CycAccess::getArg1Formats(idStr /*c*/ predicate, idStr /*c*/ mt);
CycList *CycAccess::getArg2Formats(idStr /*c*/ predicate);
CycList *CycAccess::getArg2Formats(idStr /*c*/ predicate, idStr /*c*/ mt);
CycList *CycAccess::getDisjointWiths(idStr /*c*/ cycObject);
CycList *CycAccess::getDisjointWiths(idStr /*c*/ cycFort, idStr /*c*/ mt);

CycList *CycAccess::getWhyIsa(idStr /*c*/ spec, idStr /*c*/ genl);
CycList *CycAccess::getWhyIsa(idStr /*c*/ spec, idStr /*c*/ genl, idStr /*c*/ mt);
CycList *CycAccess::getWhyGenl(idStr /*c*/ spec, idStr /*c*/ genl);
CycList *CycAccess::getWhyGenl(idStr /*c*/ spec, idStr /*c*/ genl, idStr /*c*/ mt);
CycList *CycAccess::getWhyCollectionsIntersect(idStr /*c*/ collection1, idStr /*c*/ collection2);
CycList *CycAccess::getWhyCollectionsIntersect(idStr /*c*/ collection1, idStr /*c*/ collection2, idStr /*c*/ mt);

idStr /*c*/ CycAccess::getParaphrase(idStr /*c*/ assertion);
idStr /*c*/ CycAccess::getImpreciseParaphrase(idStr /*c*/ assertionString);
idStr /*c*/ CycAccess::getWhyGenlParaphrase(idStr /*c*/ spec, idStr /*c*/ genl);
idStr /*c*/ CycAccess::paraphrase(CycList *assertionList);
idStr /*c*/ CycAccess::convertBoolean(idStr /*c*/ message);
bool CycAccess::isQueryTrue(idStr /*c*/ query, idStr /*c*/ mt);

};

#endif // __INC_CYCACCESS_H__
