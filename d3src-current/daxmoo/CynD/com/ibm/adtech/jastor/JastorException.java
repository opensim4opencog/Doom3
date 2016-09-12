/*******************************************************************************
 * Copyright (c) 2005 IBM Corporation 
 * All rights reserved.   This program and the accompanying materials
 * are made available under the terms of the Common Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.opensource.org/licenses/cpl.php
 * 
 ******************************************************************************/
package com.ibm.adtech.jastor;

/**
 * @author Joe Betz (<a href="mailto:betz@us.ibm.com">betz@us.ibm.com</a>)
 * @author Ben Szekely (<a href="mailto:bhszekel@us.ibm.com">bhszekel@us.ibm.com</a>)
 */
public class JastorException extends RuntimeException {
    public static final String copyright = "(C) Copyright IBM Corporation 2005  All Rights Reserved.";
	
	/**
     * Exception that caused this Exception to be created and thrown
     */
	private Exception originalException = null;

	/**
	 * String describing the cause of this error or information related to it
	 */
	private String errorDescription = "";

	/**
	 * Constructs a new JastorException using the original exception that caused this new
	 * exception to be thrown, an error code associated with the exception and the error's description
	 * @param Exception The exception that caused this exception to be created
	 * @param int The error code associated with this excecpion
	 * @param String The description of the error 
	 */
	public JastorException(
		Exception e,
		int errorCode,
		String errorDescription) {
		super(errorDescription);
		this.originalException = e;
		this.errorDescription = errorDescription;
	} // JastorException

	/**
	 * Constructs a new JastorException using an error code associated with the exception and
	 * the error's description
	 * @param int The error code associated with this exception
	 * @param String The description of the error
	 */
	public JastorException(int errorCode, String errorDescription) {
		super(errorDescription);
		this.errorDescription = errorDescription;
	} // JastorException

	/**
	 * Constructs a new JastorException using the original exception that caused this new exception
	 * to be thrown and the error's description
	 * @param Exception The exception that caused this exception to be created
	 * @param String The description of the error
	 */
	public JastorException(Exception e, String errorDescription) {
		super(errorDescription);
		this.originalException = e;
		this.errorDescription = errorDescription;
	} // JastorException

	/**
	 * Constructs a new JastorException using the error's description
	 * @param String The description of the error
	 */
	public JastorException(String errorDescription) {
		super(errorDescription);
		this.errorDescription = errorDescription;
	} // JastorException

	/**
	 * Constructs a new JastorException using default values
	 */
	public JastorException() {
		super("Error occurred within Jastor");
		this.errorDescription = "Error occurred within Jastor.";
	} // JastorException

	/**
	 * Return the exception that caused this exception to be created
	 * @return The exception that caused this exception to occur
	 */
	public Exception getOriginalException() {
		return this.originalException;
	} // getOriginalException


	/**
	 * Return the description for this exception
	 * @return The description belonging to this exception
	 */
	public String getErrorDescription() {
		return this.errorDescription;
	} // getErrorDescription

	/**
	 * Returns string contaning both the error code and the error description
	 * @return Message describing the error
	 */
	public String getMessage() {
		return this.errorDescription
			+ (this.originalException != null
				? " Root Cause: "
					+ originalException.getClass().getName()
					+ " "
					+ originalException.getMessage()
				: "");
	} // get Message

	/**
	 * Print the stack trace of the original exception
	 */
	public void printStackTrace() {
		super.printStackTrace();
		System.err.println("printStackTrace(): " + this.getClass().getName());
		if (this.originalException != null) {
			System.err.println(
				"STACK TRACE FOR INNER EXCEPTION: "
					+ this.originalException.getClass().getName());
			this.originalException.printStackTrace();
		} else {
			System.err.println(
				"No stack trace to print, as there was no original exception thrown.");
		} // if
	} // printStackTrace

}
