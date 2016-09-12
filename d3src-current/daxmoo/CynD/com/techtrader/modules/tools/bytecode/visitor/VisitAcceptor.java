package com.techtrader.modules.tools.bytecode.visitor;


/**
 *	Interface denoting an entity that can accept a BCVisitor and provide
 *	its internal state to it.  All entities in the bytecode framework
 *	implement this interface.
 *	
 *	@author		Abe White
 */
public interface VisitAcceptor
{
	/**
	 *	Accept a visit from a BCVisitor, calling the appropriate methods
	 *	to notify the visitor that it has entered this entity, and
	 *	to provide it with the proper callbacks for each sub-entity owned
	 *	by this object.
	 */
	public void acceptVisit (BCVisitor visitor);
}

