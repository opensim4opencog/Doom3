package com.ibm.adtech.jastor.util;

import java.util.Iterator;
import java.util.NoSuchElementException;

import com.hp.hpl.jena.rdf.model.StmtIterator;
import com.hp.hpl.jena.util.iterator.ClosableIterator;

/**
 * 
 * @author Ben Szekely ( <a
 *         href="mailto:bhszekel@us.ibm.com">bhszekel@us.ibm.com </a>)
 *  
 */
public abstract class FilteredMappedIterator implements ClosableIterator {

    Iterator src;

    Object next;
    
    StmtIterator si;
    
    FilteredMappedIterator(Iterator src) {
        this.src = src;
    }

    public void close() {
    	if (src instanceof ClosableIterator)
    		((ClosableIterator) src).close();
    }
    
    public Object next() {
        if (next != null) {
            Object temp = next;
            next = null;
            return temp;
        } else {
            Object temp = seekNext();
            if (temp == null)
                throw new NoSuchElementException();
            return temp;
        }
    }

    public boolean hasNext() {
        // check if hasNext has already been called
        if (next != null)
            return true;
        // check if the src iterator is dry
        if (!src.hasNext())
            return false;

        next = seekNext();
        if (next == null)
            return false;
        return true;
    }

    public void remove() {
        src.remove();
    }

    private Object seekNext() {
        if (!src.hasNext())
            return null;
        Object object = premap(src.next());
        while (!accept(object)) {
        	// if there are no more, then we return null
        	if (!src.hasNext())
        		return null;
            object = premap(src.next());
        }
        return postmap(object);
    }
    
    /**
     * Map method called before accept is tested.
     * @param obj
     * @return
     */
    protected Object premap(Object obj) {
        return obj;
    }
    
    /**
     * Map method called before after accept is tested.
     * @param obj
     * @return
     */
    protected Object postmap(Object obj) {
        return obj;
    }

    /**
     * 
     * @param object
     * @return
     */
    protected boolean accept(Object object) {
        return true;
    }

}