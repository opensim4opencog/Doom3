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
//	jDebugProver
//#########################################################################

package ubc.cs.JLog.Foundation;

import java.lang.*;
import java.util.*;
import ubc.cs.JLog.Terms.*;

public class jDebugProver extends jProver
{
 protected jPrologServiceBroadcaster 		debugmessages;
 protected boolean 				step;

 public 			jDebugProver(jKnowledgeBase kb,jPrologServiceBroadcaster debugm)
 {
  super(kb);
  debugmessages = debugm;

  debugmessages.broadcastEvent(new jDebugProverStartedEvent(this));
 };
 
 public boolean 	prove(jPredicateTerms goal)
 {
  step = false;
  return super.prove(goal);
 };

 // one prove is performed when step is true. stepping requires debug listeners
 public synchronized void 	step()
 {
  step = true;
  notify();
 };
 
 protected synchronized void 	waitForSingleStep()
 {
  while (!step)
  {
   try
   {
    wait();   
   }
   catch (InterruptedException e)
   {
   }
  }
  step = false;
 };
  
 protected boolean 	internal_prove()
 {
  try
  {jGoal 	tryg,nextg,retryg;
  
   while (!goals.empty())
   {
    tryg = goals.pop();
    nextg = ((jDebugGoalStack) goals).peekTopGoal();
       
    debugmessages.broadcastEvent(new jDebugTryGoalEvent(tryg,nextg));
    
    waitForSingleStep();

    if (!tryg.prove(goals,proved))
    {
     debugmessages.broadcastEvent(new jDebugFailGoalEvent(tryg));
     do
     {
      retryg = proved.pop();
      debugmessages.broadcastEvent(new jDebugRetryGoalEvent(retryg));
     } while (!retryg.retry(goals,proved));
    }
    else
    {Vector 					sub_goals;
     jDebugProvedGoalStack.jDebugGoalItem 	item;
     
     sub_goals = ((jDebugGoalStack) goals).getTopGoals(nextg);
     try
     {
      item = ((jDebugProvedGoalStack) proved).getDebugItem(tryg);
     }
     catch (DebugStackException e)
     {
      item = ((jDebugProvedGoalStack) proved).peekDebugItem();
     }
     
     item.setNextGoal(nextg);
     item.setSubGoals(sub_goals);
     
     debugmessages.broadcastEvent(new jDebugProveGoalEvent(tryg,sub_goals));
    
     {jGoal 		topg = ((jDebugGoalStack) goals).peekTopGoal();
      Vector 	proved_goals = ((jDebugProvedGoalStack) proved).getProvedDebugItems(topg);
    
      debugmessages.broadcastEvent(new jDebugProvedGoalsEvent(proved_goals));
     }  
    }
   }
   return true;
  }
  catch (EmptyStackException e)
  {// this should only occur when the proved stack is empty
   return false;
  }
 };
 
 protected boolean 	internal_retry()
 {
  while (!proved.empty())
  {jGoal 	retryg = proved.pop();
  
   debugmessages.broadcastEvent(new jDebugRetryGoalEvent(retryg));
      
   if (retryg.retry(goals,proved))
    return true;
  }
  return false;
 };
 
 protected iGoalStack 		createGoalsStack()
 {iDebugGoalStack 	gs = new jDebugGoalStack();
 
  debugmessages.broadcastEvent(new jDebugProverGoalStackEvent(this,gs,false));

  return gs;
 };

 protected iGoalStack 		createProvedStack()
 {iDebugGoalStack 	gs = new jDebugProvedGoalStack();
 
  debugmessages.broadcastEvent(new jDebugProverGoalStackEvent(this,gs,true));

  return gs;
 };
};
