/*
 * Jython Database Specification API 2.0
 *
 * $Id: Sink.java 2414 2005-02-23 04:26:23Z bzimmer $
 *
 * Copyright (c) 2001 brian zimmer <bzimmer@ziclix.com>
 *
 */
package com.ziclix.python.sql.pipe;

import org.python.core.PyObject;

/**
 * A Sink acts as a data consumer.  The Pipe is responsible for pushing data
 * to the Sink as generated by the Source.
 *
 * @author brian zimmer
 * @version $Revision: 2414 $
 */
public interface Sink {

    /**
     * Invoked at the start of the data pipelining session.
     */
    public void start();

    /**
     * Invoked for each row of data.  In general, the first row of data will
     * consist of header information in the format:<br/>
     * &nbsp;&nbsp;[(colName, colType), ...]
     * and in the format:<br/>
     * &nbsp;&nbsp;(colData, colData, ...)
     * for all other data.
     */
    public void row(PyObject row);

    /**
     * Invoked at the end of the data pipelining session.  This is useful for
     * flushing any buffers or handling any cleanup.  This method is guaranteed
     * to be called.
     */
    public void end();
}
