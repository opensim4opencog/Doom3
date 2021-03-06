/*
    This file is part of JLog.

    Created by Glendon Holst for Alan Mackworth and the 
    "Computational Intelligence: A Logical Approach" text.
    
    Copyright 1998, 2000, 2002 by University of British Columbia and 
    Alan Mackworth.
    
    This notice must remain in all files which belong to, or are derived 
    from JLog.
    
    Check <http://jlogic.sourceforge.net/> or 
    <http://sourceforge.net/projects/jlogic> for further information
    about JLog, or to contact the authors.
     
    JLog is free software; you can redistribute it and/or modify
    it under the terms of the GNU General Public License as published by
    the Free Software Foundation; either version 2 of the License, or
    (at your option) any later version.

    JLog is distributed in the hope that it will be useful,
    but WITHOUT ANY WARRANTY; without even the implied warranty of
    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
    GNU General Public License for more details.

    You should have received a copy of the GNU General Public License
    along with JLog; if not, write to the Free Software
    Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA
    URLs: <http://www.fsf.org> or <http://www.gnu.org>
*/
//#########################################################################
//	Clause
//#########################################################################
 
package ubc.cs.JLog.Terms;

import java.lang.*;
import java.util.*;
import ubc.cs.JLog.Foundation.*;
import ubc.cs.JLog.Terms.Goals.*;

public class jClause extends jBinaryBuiltinPredicate
{
 public jClause(jTerm l,jTerm r)
 {
  super(l,r,TYPE_BUILTINPREDICATE);
 };
  
 public String 		getName()
 {
  return "clause";
 };

 public boolean 	prove(jClauseGoal cg,jKnowledgeBase kb)
 {jTerm 					l,r,t;
  jDynamicRuleDefinitions 	dfd;
  jPredicate 				p;
  jPredicateTerms 			pt;
  
  l = cg.lhs.getTerm();
  r = cg.rhs.getTerm();
  
  p = getPredicate(l);
  
  if ((dfd = cg.getDynamicRules()) == null)
   cg.setDynamicRules(dfd = getDynamicRules(p,kb));
   
  if ((t = dfd.getClause(cg,p)) != null)
  {jTerm 	tnew = t;
   
   if (t instanceof iMakeUnmake)
    tnew = ((iMakeUnmake) t).unmake();
   if (tnew == null)
    tnew = jTrue.TRUE;
   
   return r.unify(tnew,cg.unified);
  }
  
  return false;
 };

 protected jPredicate 				getPredicate(jTerm t)
 {
  if (t instanceof jPredicate)
   return (jPredicate) t;
  else if (t instanceof jAtom)
   return new jPredicate((jAtom) t);
  else 
   throwError(t);
   
  // shouldn't be reached   
  return null; 
 };

 protected jDynamicRuleDefinitions  getDynamicRules(jPredicate h,jKnowledgeBase kb)
 {jRuleDefinitions 		rds;
   
  if ((rds = h.getCachedRuleDefinitions()) == null)
   rds = kb.getRuleDefinitionsMatch(h);
  
  if (rds instanceof jDynamicRuleDefinitions)
   return ((jDynamicRuleDefinitions) rds).copy();
  else
   throwError(h);
  
  // shouldn't be reached   
  return null; 
 };
 
 protected void 		throwError(jTerm t) 	
 {StringBuffer 		sb = new StringBuffer();
 
  sb.append("Clause term '");
  
  if (t instanceof iNameArity)
   sb.append(t.getName()+"/"+String.valueOf(((iNameArity) t).getArity()));
  else
   sb.append(t.getName());
  
  sb.append("' must be dynamic.");
  
  throw new InvalidClauseException(sb.toString());
 };
 
 public void 		addGoals(jGoal g,jVariable[] vars,iGoalStack goals)
 {
  goals.push(new jClauseGoal(this,lhs.duplicate(vars),rhs.duplicate(vars)));
 };

 public void 		addGoals(jGoal g,iGoalStack goals)
 {
  goals.push(new jClauseGoal(this,lhs,rhs));
 };

 protected jBinaryBuiltinPredicate 		duplicate(jTerm l,jTerm r)
 {
  return new jClause(l,r); 
 };
};

