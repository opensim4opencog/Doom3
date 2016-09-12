package daxclr.bsf;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Vector;

import bsh.EvalError;
import bsh.UtilEvalError;

public class InvocationHandlerChain implements InvocationHandler {

    Vector<InvocationHandler> handlers = new Vector<InvocationHandler>(); 

    interface Always extends InvocationHandler {}
    interface Fallback extends InvocationHandler {}

    InvocationHandler first;
    InvocationHandler next;
    public InvocationHandlerChain(InvocationHandler first1,InvocationHandler fallback) {
        this.first = first1;
        this.next = fallback;
    }

    public Object invokeHandler(InvocationHandler h1, Object proxy,Method method,Object[] args)throws NoSuchMethodException,Throwable {
        if (h1==null) throw new NoSuchMethodException("Missing invokeHandler durring " + method);
        Throwable resulterr = null;
        try {
            return h1.invoke(proxy,method,args);
        } catch ( NoSuchMethodException missing ) {
            resulterr = missing;
        } catch ( java.lang.reflect.UndeclaredThrowableException missing ) {
            resulterr = missing.getUndeclaredThrowable();
            if (resulterr==null) {
                resulterr = missing;
            }
        } catch ( EvalError missing ) {
            resulterr = new NoSuchMethodException(missing.getClass().getName() + ": " + missing.toString());
        } catch ( UtilEvalError missing ) {
            resulterr = new NoSuchMethodException(missing.getClass().getName() + ": " + missing.toString());
        } catch ( Error missing ) {
            resulterr = new NoSuchMethodException(missing.getClass().getName() + ": " + missing.toString());
        } catch ( Throwable missing ) {
            if (missing.getClass().getPackage().getName().startsWith("bsh")) {
                resulterr = new NoSuchMethodException(missing.getClass().getName() + ": " + missing.toString());
            } else {
                resulterr = missing;
            }
        }
        throw resulterr;
    }
    public Object invoke(Object proxy,Method method,Object[] args) throws Throwable {
        Throwable resulterr = null;
        List<Object> results = null;
        if (first!=null) {
            try {
                if (!(first instanceof Fallback)) {
                    return invokeHandler(first,proxy,method,args);
                } else {
                    results = new ArrayList<Object>(1);
                    results.add(invokeHandler(first,proxy,method,args));
                }
            } catch (Throwable ex) {
                resulterr = ex;
            }
        }
        if (next!=null) {
            try {
                if (results==null) {
                    return invokeHandler(next,proxy,method,args);
                } else {
                    results.add(invokeHandler(next,proxy,method,args));
                }
            } catch (Throwable ex) {
                resulterr = ex;
            }
        }
        if (results!=null && results.size()>0) {
            Iterator<Object> its = results.iterator();
            if (its.hasNext()) {
                Object it = its.next();
                if (it!=null) {
                    if (it instanceof Void || void.class.isInstance(it)) {
                    } else {
                        return it;
                    }
                }
            }
            return results.get(0);
        }
        if (resulterr!=null) throw resulterr;
        throw new NoSuchMethodException(""+method);
    }
}



