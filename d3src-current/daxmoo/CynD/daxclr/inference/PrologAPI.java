package daxclr.inference;

import java.io.IOException;
import java.io.Serializable;
import java.lang.reflect.Array;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Proxy;
import java.net.UnknownHostException;
import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import javax.naming.NameAlreadyBoundException;

import jinni.kernel.Fun;
import jinni.kernel.JavaIO;
import jinni.kernel.Machine;
import jinni.kernel.OTerm;
import jinni.kernel.Top;
import jinni.kernel.Var;
import org.apache.bsf.BSFException;
import org.opencyc.api.CycApiException;
import org.opencyc.api.CycObjectFactory;
import org.opencyc.cyclobject.CycLTerm;
import org.opencyc.cycobject.ByteArray;
import org.opencyc.cycobject.CycAssertion;
import org.opencyc.cycobject.CycFort;
import org.opencyc.cycobject.CycList;
import org.opencyc.cycobject.CycNart;
import org.opencyc.cycobject.CycObject;
import org.opencyc.cycobject.CycSymbol;
import org.opencyc.cycobject.CycVariable;
import org.opencyc.cycobject.DefaultCycObject;
import org.opencyc.cycobject.Guid;

import daxclr.bsf.ConsoleChannel;
import daxclr.bsf.INamedObject;
import daxclr.bsf.IScriptMethodHandler;
import daxclr.bsf.IScriptObject;
import daxclr.doom.IVector;
import daxclr.doom.modules.RemoteDoomModule;
import daxclr.doom.server.IdVector;
//import daxclr.doom.LocalClientHolder;

/**
 * Summary description for PrologAPI.
 */
public class PrologAPI extends RemoteDoomModule {
	public long getPointer() {
		return 0L;
	}

	public boolean isMapSpecific() {
		return false;
	}

	public void run() {
	}

	static public jinni.kernel.Machine theMachine = null;
	static {
		try {
			debug("initPrologAPI: Init prolog ");
			JavaIO.showOutput = true;
			JavaIO.showTrace = 9;
			JavaIO.showErrors = true;
			// JavaIO.setStdInput(getInputStream());
			// JavaIO.setStdOutput(getConsole().getOut());
			// doom_query(cycquery(isa(A,\\\'Dog\\\'),\\\'BaseKB\\\'),B)
			theMachine = Top
					.initJinni(new String[] { "new_console('doom_query(faceEntity(cyc_bot_1,player1),X)')" });
			// theMachine = new Machine(null, null, null);
			Top.clear_path();
			theMachine.run("consult('base\\doom.pro')");
			Top.add_to_path("base");
			Top.add_to_path("base\\classlib");
			// loadPrologCycSupport(); //
			// doom(X),invoke_java_method(X,loadPrologCycSupport,Y)
			debug("initPrologAPI: WORKED!!!");
		} catch (Throwable e) {
			debug(e);
		}
	}

	static public Machine getNewMachine() {
		theMachine = Top.new_machine();
		theVarMap = new HashMap<Var, CycVariable>();
		getRepository().put("theMachine", theMachine);
		return theMachine;
	}

	static public Machine getMachine() {
		if (theMachine == null)
			return getNewMachine();
		return theMachine;
	}

	// i am working on right now a a java dubbugger shell.. its a swing app that
	// showJava
	// static public jinni.kernel.Main prologMain=null;
	// static public jinni.kernel.Prolog theProlog=null;
	static public PrologAPI prologapi = null;
	static {
		try {
			prologapi = new PrologAPI();
		} catch (NameAlreadyBoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	static public PrologAPI current() {
		if (prologapi == null) {
			try {
				prologapi = new PrologAPI();
				getRepository().put("thePrologAPI", prologapi);
			} catch (NameAlreadyBoundException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		return prologapi;
	}

	/* Creates a non-started Server */
	public PrologAPI() throws NameAlreadyBoundException {
		super("thePrologAPI");
		prologapi = this;
	}

	public String toString() {
		return "thePrologAPI";
	}

	static public void loadPrologCycSupport() {
		Iterator its = null;
		// CycAPI.current().baseKB=c("BaseKB");
		CycFort cycMicrotheory = (CycFort) atomToLisp("Microtheory");
		CycFort baseKB = (CycFort) atomToLisp("Microtheory");
		Map<Var, CycVariable> varmap = new HashMap<Var, CycVariable>(10);
		its = findallArityOne("cycMt");
		while (its.hasNext())
			try {
				CycAPI.current()
						.assertIsa(
								(CycFort) prologToLisp(varmap, atomToString(its
										.next())), cycMicrotheory, baseKB);
			} catch (Throwable e) {
				debug(e);
			}
		its = findallArityOne("cycKb");
		while (its.hasNext()) {
			Fun prolog = (Fun) its.next();
			String mt = atomToString(prolog.args[0]);
			prolog = (Fun) prolog.args[1];
			try {
				prologCycAssert((Fun) prolog, atomToString(mt));
			} catch (Throwable e) {
				debug(e);
			}
		}
	}

	/**
	 * just makes printing easier
	 */
	static public void println(String s) {
		JavaIO.dump(s);
		debug(s);
	}

	static public void prologRun(String prolog) {
		getMachine().run(atomToString(prolog));
		// getNewMachine().get_answer();
	}

	static public void prologConsult(String filename) {
		prologRun("compile('" + filename + "')");
	}

	static public Object evalForProlog(Object o) {
		if (o == null)
			return o;
		Map<Var, CycVariable> varmap = getVarMap(theVarMap);
		return javaToProlog(varmap, prologToJava(varmap, o));
	}

	static public Object toCycTerm(String sentence) {
		sentence = sentence.trim();
		return (new LispReader(CycAPI.current())).read(sentence);
	}

	static public Object toPrologTerm(String sentence) {
		return prologCallArgOne("sread_term", sentence);
	}

	static public String prologToString(Object sentence) {
		return "" + prologCallArgOne("swrite", sentence);
	}

	static String atomToString(Object o) {
		String s = "" + o;
		if (s.indexOf("'") == 0) {
			int len = s.length();
			if (s.charAt(len - 1) == '\'') {
				return s.substring(1, len - 2);
			}
		}
		return s;
	}

	static public Object prologCallArgOne(String name, Object arg1) {
		Machine M = getNewMachine(); // we make a new Prolog engine
		Var avar = new Var(111);
		Fun prolog = new Fun(":-", new Object[] { avar,
				new Fun(name, new Object[] { arg1, avar }) });
		if (!M.load_engine(prolog))
			return null; // we load the (existing)
		// Prolog engine
		Object answer = M.get_answer(); // get an answer
		// M.stop();
		return answer;
	}

	static public Iterator findallArityOne(String prolog) {
		Machine M = getNewMachine();
		Var avar = new Var(1111);
		return prologAsk(avar, new Fun(prolog, new Object[] { avar }))
				.iterator();
	}

	static public Iterator evalStringInProlog(String prolog) {
		Object goal = toPrologTerm(prolog);
		Iterator its = prologAsk(goal, goal).iterator();
		CycList list2 = new CycList();
		while (its.hasNext()) {
			list2.add(prologToString(its.next()));
		}
		return list2.iterator();
	}

	static public CycList prologAsk(Object template, Object goal) {
		Machine M = getMachine(); // we make a new Prolog engine
		// Machine M=theMachine;
		CycList al = new CycList();
		if (!M.load_engine(new Fun(":-", new Object[] { template, goal })))
			return al;
		for (;;) {
			Object answer = M.get_answer(); // get an answer
			if (null == answer) {
				// M.stop();
				break; // exit loop when finished
			}
			al.add((answer));
		}
		if (al.size() == 0) {
		}
		return al;
	}

	// doom_eval(getKey(cyc_bot_1,'origin'),X)
	// doom_query(getWorldOrigin(cyc_bot_1),X)
	// doom_query(getColor(cyc_bot_1),X)
	// 
	static public void setVar(Var thevar, Object value) {
		getMachine().load_engine(new Fun("=", new Object[] { thevar, value }));
		getMachine().get_answer();
	}

	static public Object[] listToArray(Map<Var, CycVariable> varmap, Object o) {
		if (varmap == null)
			varmap = getVarMap(varmap);
		if (o instanceof Fun) {
			Fun prolog = (Fun) o;
			if (prolog.name.equals(".")) {
				return prependArray(prologToJava(varmap, prolog.args[0]),
						listToArray(varmap, prolog.args[1]));
			}
		}
		return new Object[] { prologToJava(varmap, o) };
	}

	static Class toClass(Map<Var, CycVariable> varmap, Object o)
			throws ClassNotFoundException {
		if (o instanceof CharSequence) {
			try {
				return getGameLocal().toClass(o.toString());
			} catch (RemoteException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				throw new RuntimeException(e);
			} catch (Throwable e) {
				throw new RuntimeException(e);
			}
		}
		if (o instanceof Class) {
			return (Class) o;
		}
		o = prologToJava(varmap, o);
		if (o instanceof Class) {
			return (Class) o;
		}
		return o.getClass();
	}

	static public Object evalFunction(Map<Var, CycVariable> varmap,
			String name, Object[] args) {
		if (varmap == null)
			varmap = getVarMap(varmap);
		if (args == null) {
			args = new Object[0];
		}
		int arity = args.length;
		// if (name.equals("list")) return new
		// CycNart(prologListToCycList(varmap,new
		// CycList(prologToJava(varmap,args[0])),args[1]));
		if (name.equals("nart")) {
			Object functor = prologToLisp(varmap, args[0]);
			CycList lisp = prologListToCycList(varmap, new CycList(functor),
					args[1]);
			if (functor instanceof CycFort) {
				return new CycNart(lisp);
			} else {
				return lisp;
			}
		}
		if (name.equals(".") && args.length == 2) {
			try {
				return "" + (char) ((Number) args[0]).intValue()
						+ consToString((Fun) args[1]);
			} catch (Throwable e) {
				return prologListToCycList(varmap, new CycList(prologToJava(
						varmap, args[0])), args[1]);
			}
		}
		if (name.equals("a"))
			return PrologAPI.toArray(varmap, args[2]);
		if (name.equals("s"))
			return ConsoleChannel.joinString(args, " ");
		if (name.equals("e"))
			try {
				return getGameLocal().toObject(args[0]);
			} catch (RemoteException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
				throw new RuntimeException(e1);
			} catch (Throwable e1) {
				throw new RuntimeException(e1);
			}
		if (name.equals("o"))
			return toObject(args[0]);
		if (name.equals("g"))
			return new Guid("" + args[0]);
		if (name.equals("v"))
			return prologToJavaArray(varmap, 0, args);
		if (name.equals("o"))
			return prologToJava(varmap, args[0]);
		if (name.equals("l"))
			return prologToLisp(varmap, args[0]);
		if (name.equals("i"))
			return args[1];
		if (name.equals("args"))
			return args;
		if (name.equals("t")) {
			try {
				return toClass(varmap, args[0]);
			} catch (ClassNotFoundException cnf) {
				throw new RuntimeException("while resolving c("
						+ args[0].toString() + ")", cnf);
			}
		}
		try {
			if (IdVector.nameType(name) != IVector.VECT_UNKNOWN) {
				IVector iv = new IdVector(IdVector.nameType(name), args.length);
				for (int i = 0; i < args.length; i++) {
					iv.set(i, new Double("" + prologToJava(varmap, args[i])));
				}
			}
		} catch (Exception e) {
		}
		if (name.equals("cycunify")) {
			// doom_query(cycquery(isa('BaseKB',X),'BaseKB'),Y)
			try {
				return prologCycUnify((Fun) args[0], atomToString(args[1]),
						args[2]);
			} catch (Throwable e) {
				throw new RuntimeException("cask->converseObject ", e);
			}
		}
		if (name.equals("cycquery")) {
			// doom_query(cycquery(isa('BaseKB',X),'BaseKB'),Y)
			try {
				return prologCycQuery((Fun) args[0], atomToString(args[1]));
			} catch (Throwable e) {
				throw new RuntimeException("cask->converseObject ", e);
			}
		}
		if (name.equals("cycassert")) {
			// doom_query(cycquery(isa('BaseKB',X),'BaseKB'),Y)
			try {
				return prologCycAssert((Fun) args[0], atomToString(args[1]));
			} catch (Throwable e) {
				throw new RuntimeException("cassert->converseObject ", e);
			}
		}
		if (name.equals("cycassertforward")) {
			// doom_query(cycquery(isa('BaseKB',X),'BaseKB'),Y)
			try {
				return prologCycAssertForward((Fun) args[0],
						atomToString(args[1]));
			} catch (Throwable e) {
				throw new RuntimeException("cassert->converseObject ", e);
			}
		}
		if (name.equals("cycretract")) {
			// doom_query(cycquery(isa('BaseKB',X),'BaseKB'),Y)
			try {
				return prologCycUnassert((Fun) args[0], atomToString(args[1]));
			} catch (Throwable e) {
				throw new RuntimeException("cretract->converseObject ", e);
			}
		}
		if (name.equals("subl")) {
			// doom_query(subl(1 + 1),Y)
			try {
				return daxclr.inference.CycAPI.current().converseObject(
						prologToJava(varmap, args[0]));
			} catch (Throwable e) {
				throw new RuntimeException("subl->converseObject ", e);
			}
		}
		if (name.equals("pget")) {
			name = atomToString(prologToJava(varmap, args[0]));
			return toObject(name);
		}
		Object object = null;
		if (name.equals("pset")) { // doom_query(pset(x,1),X),doom_query(pget(x),Y).
			name = atomToString(prologToJava(varmap, args[0]));
			object = prologToJava(varmap, args[1]);
			return getRepository().put(name, object);
		}
		if (name.equals("punset")) { // doom_query(punset(x),X),doom_query(pget(x),Y)
			name = atomToString(prologToJava(varmap, args[0]));
			return getRepository().remove(name);
		}
		if (name.equals("invokeObject")) {
			String fname = atomToString(prologToJava(varmap, args[1]));
			Object o = prologToJava(varmap, args[0]);
			Object[] a = prologToJavaArray(varmap, 2, args);
			try {
				return NativeManager.invokeObject(o, fname, a);
			} catch (Throwable e) {
				throw new RuntimeException(e);
			}
		}
		if (name.equals("invokeClass")) {
			try {
				return NativeManager.invokeStatic((Class) toClass(varmap,
						args[0]), atomToString(args[1]), prologToJavaArray(
						varmap, 2, args));
			} catch (Throwable e) {
				throw new RuntimeException(e);
			}
		}
		Class clz = null;
		if (name.equals("instanceClass")) {
			try {
				clz = toClass(varmap, args[0]);
				return NativeManager.newObject(clz, prologToJavaArray(varmap,
						1, args));
			} catch (NoSuchMethodException e) {
			} catch (Throwable e) {
				throw new RuntimeException("evalFunction->new " + clz, e);
			}
		}
		try {
			return getGameLocal().invokeCommand(
					prologToJavaArray(varmap, 0, args));
		} catch (java.lang.UnsatisfiedLinkError e) {
		} catch (Throwable e) {
			debug(e);
		}
		/*
		 * try { clz=forName(name); } catch (Throwable e) { } if (clz!=null) {
		 * try { return newObject(clz,prologToJavaArray(varmap,0,args)); } catch
		 * (NoSuchMethodException e) { } catch (Throwable e) { throw new
		 * RuntimeException("evalFunction->newObject "+clz,e); } try { return
		 * invokeStatic(clz,atomToString(prologToJava(varmap,args[0])),prologToJavaArray(varmap,1,args)); }
		 * catch (NoSuchMethodException e) { } catch (Throwable e) { throw new
		 * RuntimeException("evalFunction->invokeStatic "+clz,e); } }
		 */
		/*
		 * doom_query(toString(entity(cyc_bot_1)),X)
		 * doom_query(toClass(e(cyc_bot_1)),X)
		 * doom_query(getOrigin(entity(cyc_bot_1)),X)
		 * 
		 * 
		 * object=toObject(name); if (object!=null) { //
		 * doom_query(toString(t),X) clz=object.getClass(); if
		 * (!clz.getPackage().getName().contains("cycobject")) { try { return
		 * invokeSomething(clz,object,atomToString(prologToJava(varmap,args[0])),prologToJavaArray(varmap,1,args)); }
		 * catch (NoSuchMethodException e) { } catch (Throwable e) { throw new
		 * RuntimeException("evalFunction->invokeSomething "+object,e); } } }
		 * object=prologToJava(varmap,args[0]); if (object!=null) {
		 * clz=object.getClass(); try { return
		 * invokeSomething(clz,object,name,prologToJavaArray(varmap,1,args)); }
		 * catch (NoSuchMethodException e) { } catch (Throwable e) { throw new
		 * RuntimeException("evalFunction->invokeSomething "+object,e); } } try {
		 * clz=forName(""+object); } catch (Throwable e) { } if (clz!=null) {
		 * try { return invokeStatic(clz,name,prologToJavaArray(varmap,1,args)); }
		 * catch (NoSuchMethodException e) { } catch (Throwable e) { throw new
		 * RuntimeException("evalFunction->invokeStatic "+clz,e); } }
		 */
		throw new NoSuchMethodError("'" + name + "'('"
				+ ConsoleChannel.joinString(args, "','") + "')");
		// return new Fun(name,args); // loops though
	}


	static public int assertConstantToProlog(String constant) {
		try {
			CycList list = CycAPI.currentInstance().getAsserions(
					(CycFort) atomToLisp(constant));
			Iterator its = list.iterator();
			while (its.hasNext()) {
				Object o = its.next();
				println("" + o);
				if (o instanceof CycList) {
					assertToProlog((CycList) o);
				}
				if (o instanceof CycAssertion) {
					assertToProlog((CycAssertion) o);
				}
			}
		} catch (Throwable e) {
			throw new RuntimeException(e);
		}
		return 1;
	}

	static public int assertToProlog(CycList cyclist) {
		Map<Var, CycVariable> varmap = getVarMap(theVarMap);
		Object prolog = lispToProlog(varmap, cyclist);
		prologAsk(new Var(1000), new Fun("assertCyc", new Object[] { prolog }));
		return 1;
	}

	static public int unassertToProlog(CycList cyclist) {
		Map<Var, CycVariable> varmap = getVarMap(theVarMap);
		Object prolog = lispToProlog(varmap, cyclist);
		prologAsk(new Var(1000),
				new Fun("unassertCyc", new Object[] { prolog }));
		return 1;
	}

	static public boolean queryToProlog(CycList cyclist) {
		Map<Var, CycVariable> varmap = getVarMap(theVarMap);
		Object prolog = lispToProlog(varmap, cyclist);
		return prologAsk(new Var(1000),
				new Fun("queryCyc", new Object[] { prolog })).iterator()
				.hasNext();
	}

	static public int assertToProlog(CycAssertion cyclist) {
		Map<Var, CycVariable> varmap = getVarMap(theVarMap);
		CycList list = cyclist.getFormula();
		CycObject mt = cyclist.getMt();
		Object prolog = lispToProlog(varmap, list);
		Object prologmt = lispToProlog(varmap, mt);
		prologAsk(new Var(1000), new Fun("assertCyc", new Object[] { prolog,
				prologmt }));
		return 1;
	}

	static public Object typeToProlog(Class prolog) {
		if (null == prolog)
			return new Var(6767);
		return new Fun("t", prolog.getName());
	}

	static public Object PROLOG_NULL = "$null";

	// static public Object javaToProlog(Map varmap,Enumeration en) {
	// return prologToJava(varmap,en.nextElement());
	// return en;//prologToJava(varmap,en.nextElement());
	// }
	static public Object javaToProlog(Map<Var, CycVariable> varmap, Object j) {
		if (j == null)
			return PROLOG_NULL;
		if (varmap == null)
			varmap = getVarMap(varmap);
		// if (j==null) return j;
		if (isPrologObject(j))
			return j;
		if (j instanceof Number)
			return (((Number) j).intValue() == ((Number) j).hashCode() ? new Integer(
					((Number) j).intValue())
					: new Double(((Number) j).doubleValue()));
		if (j instanceof CharSequence)
			return new Fun("s", (Object) j.toString());
		if (isLispObject(j))
			return lispToProlog(varmap, j);
		if (j instanceof Map.Entry) {
			Map.Entry me = (Map.Entry) j;
			return new Fun("-", javaToProlog(varmap, me.getKey()),
					javaToProlog(varmap, me.getValue()));
		}
		if (j instanceof Class) {
			return new Fun("t", ((Class) j).getName());
		}
		if (j instanceof INamedObject) {
			try {
				return new Fun("e", ((INamedObject) j).getName());
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				throw new RuntimeException(e);
			} catch (Throwable e) {
				throw new RuntimeException(e);

			}
		}
		if (j instanceof IScriptMethodHandler) {
			// return javaToProlog(varmap,(IObjectInfo)j);
			try {
				return new Fun("o", ((INamedObject) j).getName());
			} catch (RemoteException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				throw new RuntimeException(e);
			} catch (Throwable e) {
				throw new RuntimeException(e);

			}
		}
		Class clazz = j.getClass();
		if (clazz.isArray()) {
			int len = Array.getLength(j);
			// Object args=Array.newInstance(Object.class);
			// for (int i=0;i<len;i++)
			// Array.set(args,i,javaToProlog(varmap,Array.get(j,i)));
			return new Fun("a", len, typeToProlog(clazz), j);
		}
		if (Proxy.isProxyClass(clazz)) {
			InvocationHandler ih = Proxy.getInvocationHandler(j);
			if (ih == j) {
			}
			return new Fun("i", javaToProlog(varmap, ih), j);
		}
		if (clazz.isPrimitive()) {
			return j;
		}
		String kname;
		try {
			kname = getRepository().findOrCreateEntry(j)
					.getName();
		} catch (Throwable e) {
			throw new RuntimeException(e);
		}
		if (kname != null) {
			return new Fun("e", kname);
		}
		return new Fun("i", typeToProlog(clazz), j);
	}

	static CycFort CYCL_NULL = CycAPI.c("ISNull");

	static CycList CYC_PROLOG_NIL = new CycList();

	static CycSymbol LISP_NULL = new CycSymbol("NIL");

	static public Object atomToJava(Map<Var, CycVariable> varmap, String name) {
		if (varmap == null)
			varmap = getVarMap(varmap);
		Object prolog = toObject(name);
		if (prolog != null)
			return prolog;
		return atomToLisp(name);
	}

	static public Object atomToLisp(String name) {
		if (name.equals("[]"))
			return CYC_PROLOG_NIL;
		if (name.equals("NIL"))
			return LISP_NULL;
		if (name.contains(" ") || name.contains(".") || name.contains("/")
				|| name.contains("\\"))
			return name;
		if (name.startsWith("!"))
			return new CycSymbol(name.substring(1));
		if (name.equals(name.toUpperCase()) || name.startsWith(":"))
			return new CycSymbol(name);
		Object prolog = toObject(name);
		if (prolog != null && (!(prolog instanceof String)))
			return prologToLisp(theVarMap, prolog);
		return CycAPI.c(name);
	}

	static public Object[] prologToJavaArray(Map<Var, CycVariable> varmap,
			int start, Object[] args) {
		if (args == null || args.length == 0 || (start >= args.length))
			return new Object[0];
		Object[] retval = new Object[args.length - start];
		for (int i = start; i < args.length; i++) {
			try {
				retval[i - start] = prologToJava(varmap, args[i]);
			} catch (Throwable nf) {
			}
		}
		return retval;// just so you dont think i am a total nut :)
		// http://24.113.145.203/ExpandingAI.wmv the ssytem i am
		// working on
	}// PhUrl, that code you did for openmodality.. i am looking at it

	// again.. cool
	static public Object prologToJava(Map<Var, CycVariable> varmap,
			Object prolog) {
		if (varmap == null)
			varmap = getVarMap(varmap);
		if (prolog == null)
			return null;
		if (prolog instanceof CharSequence)
			return atomToJava(varmap, prolog.toString());
		String name = "" + prolog;
		if (isLispObject(prolog))
			return prolog;
		if (prolog instanceof Var)
			return prologToLisp(varmap, prolog);
		if (prolog instanceof Number)
			return prolog;
		if (prolog instanceof Fun) {
			Fun fun = (Fun) prolog;
			try {
				return evalFunction(varmap, fun.name, fun.args);
			} catch (NoSuchMethodError nsme) { // IncompatibleClassChangeError
			}
			Object functor = atomToLisp(fun.name);
			CycList lisp = new CycList(functor);
			for (int i = 0; i < fun.args.length; i++)
				lisp.add(prologToLisp(varmap, fun.args[i]));
			if (functor instanceof CycFort) {
				return new CycNart(lisp);
			} else {
				return lisp;
			}
		}
		return prolog;
	}

	static public Object javaArrayToCons(Map<Var, CycVariable> varmap,
			Object[] o) {
		if (varmap == null)
			varmap = getVarMap(varmap);
		Object cons = "[]";
		if (o == null || o.length == 0) {
			return cons;
		}
		for (int i = o.length - 1; i >= 0; i--) {
			cons = new Fun(".", javaToProlog(varmap, o[i]), cons);
		}
		return cons;
	}

	static public Set<Object> getPrologVars(Object prolog) {
		if (prolog instanceof Var) {
			Set<Object> set = new HashSet<Object>(1);
			set.add(prolog);
			return set;
		} else if (prolog instanceof Fun) {
			Fun ffun = (Fun) prolog;
			Set<Object> set = new HashSet<Object>(ffun.args.length);
			for (int i = 0; i < ffun.args.length; i++) {
				set.addAll(getPrologVars(ffun.args[i]));
			}
			return set;
		}
		return new HashSet<Object>(0);
	}

	/*
	 * doom_query(cycquery(isa('BaseKB',X),'BaseKB'),Y)
	 */
	static public Object prologCycQuery(Fun prolog, String mt) {
		Map<Var, CycVariable> varmap = new HashMap<Var, CycVariable>(
				prolog.args.length);
		CycList query = (CycList) prologToLisp(varmap, prolog);
		return prologCycQuery(query.cyclify(), mt);
	}

	static public Object prologCycUnify(Fun prolog, String mt, Object vars) {
		Map<Var, CycVariable> varmap = new HashMap<Var, CycVariable>(
				prolog.args.length);
		CycList query = (CycList) prologToLisp(varmap, prolog);
		query = (CycList) prologCycQuery(query.cyclify(), mt);
		return query; // doom_query(cycquery(isa('Person',X),'BaseKB'),OO)
	}

	static public Object prologCycQuery(String cyclified, String mt) {
		String message = "(cyc-query '" + cyclified + " #$" + mt + ")";
		println("prologCycQuery EVAL: " + message);
		try {
			return CycAPI.current().converseObject(message);
		} catch (Throwable e) {
			throw new RuntimeException(message, e);
		}
	}

	static public Object prologCycAssert(Fun prolog, String mt) {
		Map<Var, CycVariable> varmap = new HashMap<Var, CycVariable>(
				prolog.args.length);
		CycList query = (CycList) prologToLisp(varmap, prolog);
		String message = "(cyc-assert '" + query.cyclify() + " #$" + mt
				+ " '(:STRENGTH :MONOTONIC))";
		println("prologCycAssert EVAL: " + message);
		try {
			return CycAPI.current().converseObject(message);
		} catch (Throwable e) {
			debug(e);
			throw new RuntimeException(message, e);
		}
	}

	static public Object prologCycAssertForward(Fun prolog, String mt) {
		Map<Var, CycVariable> varmap = new HashMap<Var, CycVariable>(
				prolog.args.length);
		CycList query = (CycList) prologToLisp(varmap, prolog);
		String message = "(cyc-assert '" + query.cyclify() + " #$" + mt
				+ " '(:DIRECTION :FORWARD :STRENGTH :MONOTONIC))";
		println("prologCycAssertForward EVAL: " + message);
		try {
			return CycAPI.current().converseObject(message);
		} catch (Throwable e) {
			debug(e);
			throw new RuntimeException(message, e);
		}
	}

	static public Object prologCycUnassert(Fun prolog, String mt) {
		Map<Var, CycVariable> varmap = new HashMap<Var, CycVariable>(
				prolog.args.length);
		CycList query = (CycList) prologToLisp(varmap, prolog);
		String message = "(cyc-unassert '" + query.cyclify() + " #$" + mt + ")";
		println("prologCycUnassert EVAL: " + message);
		try {
			return CycAPI.current().converseObject(message);
		} catch (Throwable e) {
			debug(e);
			throw new RuntimeException(message, e);
		}
	}

	// (#$isa #$doom:cyc_bot_1 #$Agent-Generic)
	// (#$isa #$doom:player1 #$Agent-Generic)
	// (#$implies (#$and (#$gameEval (#$TheList "invokeGame" "canSee" ?X ?Y) ?R)
	// (#$equals 1.0 ?R)) (#$sees ?X ?Y))
	// (#$implies (#$and (#$equals ?R 1.0) (#$gameEval (#$TheList "invokeGame"
	// "canSee" #$doom:cyc_bot_1 #$doom:player1) ?R)) (#$sees #$doom:cyc_bot_1
	// #$doom:player1))
	static Fun cons(Object h, Object t) {
		if (t == null)
			t = "[]";
		return new Fun(".", h, t);
	}

	static Object mapToProlog(Map<Var, CycVariable> map) {
		Object lcons = "[]";
		Set set = map.entrySet();
		Iterator it = set.iterator();
		while (it.hasNext()) {
			Map.Entry entry = (Map.Entry) it.next();
			lcons = cons(new Fun("=", (Object) entry.getKey(), (Object) entry
					.getValue()), lcons);
		}
		return lcons;
	}

	static public boolean isPrologAPI(Object o) {
		Object result = prologCallArgOne("isPrologAPI", o);
		return ("" + result).equals("prolog");
		// return false;
	}

	static public Object[] converse(CycList command) throws IOException,
			UnknownHostException, CycApiException {
		return new Object[] { true, evalLisp(command) };
	}

	static public Object evalPrologString(String prolog) {
		return evalProlog(toPrologTerm(prolog));
	}

	static public Object evalLispString(String lisp) {
		return evalLisp(toCycTerm(lisp));
	}

	static public Object evalLisp(Object lisp) {
		// we load to the (existing) Prolog engine
		//  
		Map<Var, CycVariable> varmap = getVarMap(theVarMap);
		return evalProlog(lispToProlog(varmap, lisp));
	}

	static public Object evalProlog(Object prolog) {
		// we load to the (existing) Prolog engine
		Machine M = getMachine();
		CycList al = new CycList();
		Map<Var, CycVariable> varmap = getVarMap(theVarMap);
		Var result = new Var(999);
		if (!M.load_engine(new Fun(":-", new Object[] { result,
				new Fun("evalAPI", prolog, mapToProlog(varmap), result) })))
			return al;
		for (;;) {
			Object answer = M.get_answer(); // get an answer
			if (null == answer) {
				// M.stop();
				// theMachine=null;
				break; // exit loop when finished
			}
			al.add((answer));
		}
		Iterator i = al.iterator();
		while (i.hasNext()) {
			return prologToLisp(varmap, i.next());
		}
		return LISP_NULL;
	}

	static public String consToString(Object o) throws Throwable {
		if (o == null)
			return "";
		if (o instanceof String && o.equals("[]"))
			return "";
		if (o instanceof String && (((String) o).length() == 1))
			return (String) o;
		if (o instanceof Character)
			return o.toString();
		if (o instanceof Number)
			return new Character((char) ((Integer) o).intValue()).toString();
		if (!(o instanceof Fun))
			throw new NumberFormatException("consToString: " + o);
		if (!(o instanceof Fun))
			return "" + o;
		return consToString(((Fun) o).args[0])
				+ consToString(((Fun) o).args[1]);
	}

	static public CycList prologListToCycList(Map<Var, CycVariable> varmap,
			CycList prev, Object o) {
		if (o instanceof Fun) {
			Fun prolog = (Fun) o;
			prev.add(prologToLisp(varmap, prolog.args[0]));
			return prologListToCycList(varmap, prev, prolog.args[1]);
		} else if (o instanceof String) {
			if (o.equals("[]"))
				return prev;
		}
		prev.setDottedElement(prologToLisp(varmap, o));
		return prev;
	}

	static public Map<Var, CycVariable> theVarMap = new HashMap<Var, CycVariable>();

	public static Map<Var, CycVariable> getVarMap(Map<Var, CycVariable> varmap) {
		if (varmap == null) {
			if (theVarMap == null)
				theVarMap = new HashMap<Var, CycVariable>();
		} else if (theVarMap == null) {
			theVarMap = varmap;
		} else {
			varmap.putAll(theVarMap);
			theVarMap = varmap;
		}
		return theVarMap;
	}

	static public Object prologVarResult(Map<Var, CycVariable> varmap,
			Var prolog) {
		if (prolog == null)
			return CycObjectFactory.makeUniqueCycVariable(new CycVariable(
					"?NULL"));
		CycVariable lisp = varmap.get(prolog);
		if (lisp == null) {
			debug("VAR missing  " + prolog);
			// / int id=prolog.getID();
			// if (id<500 ) {
			// lisp=new CycVariable("VAR"+id, new Integer(id));
			// CycObjectFactory.addCycVariableCache(lisp);
			// } else {
			lisp = CycObjectFactory.makeCycVariable(""
					+ prolog.toString().toUpperCase());
			// }
			varmap.put(prolog, lisp);
		}
		return lisp;
	}

	static public Object cyclVarResult(Map<Var, CycVariable> varmap,
			CycVariable lisp) {
		Iterator its = varmap.entrySet().iterator();
		while (its.hasNext()) {
			Map.Entry entry = (Map.Entry) its.next();
			if (lisp.equals(entry.getValue()))
				return entry.getKey();
		}
		Var prolog = null;
		// Integer i=lisp.hlVariableId;
		// if (i!=null) {
		// prolog=new Var(i.intValue());
		// } else {
		prolog = new Var(lisp.hashCode());
		// }
		varmap.put(prolog, lisp);
		return prolog;
	}

	static public Object prologToLisp(Map<Var, CycVariable> varmap,
			Object prolog) {
		if (prolog == null)
			return CYCL_NULL;
		if (prolog instanceof CharSequence)
			return atomToLisp(prolog.toString());
		if (isLispObject(prolog))
			return prolog;
		if (prolog instanceof Fun)
			return prologToJava(varmap, (Fun) prolog);
		if (prolog instanceof Var) {
			Object result = prologVarResult(varmap, (Var) prolog);
			return (result == null) ? CYCL_NULL : ((result == prolog) ? result
					: prologToLisp(varmap, result));
		}
		if (DefaultCycObject.isCycLObject(prolog)
				|| (DefaultCycObject.getCycObjectType(prolog) != CycObject.CYCOBJECT_UNKNOWN))
			return prolog;
		debug("Strange Prolog Object not Understood by Lisp '"
				+ prolog.getClass() + "' \"" + prolog + "\"");
		return "" + prolog;
	}

	static public boolean isLispObject(Object o) {
		if (o == null)
			return false;
		return (o instanceof Number || o instanceof CycLTerm
				|| o instanceof CycList || o instanceof CycObject
				|| o instanceof CycVariable || o instanceof CycFort);
	}

	static public boolean isPrologObject(Object o) {
		if (o == null)
			return false;// CycObject
		return (o instanceof Integer || o instanceof Double
				|| o instanceof OTerm || o instanceof Fun || o instanceof Var);
	}

	// if (lisp instanceof Object list) {
	// return lispToProlog(varmap,list);
	// }
	static public Object lispToProlog(Map<Var, CycVariable> varmap, Object lisp) {
		switch (DefaultCycObject.getCycObjectType(lisp)) {
		case CycObject.CYCOBJECT_CYCLIST: // 4;
			if (lisp instanceof CycList)
				return cyclistToProlog(varmap, (CycList) lisp);
		case CycObject.CYCOBJECT_CYCSYMBOL: // 5;
		{
			String name = lisp.toString();
			return (name.startsWith(":")) ? name : "!" + name;
		}
		case CycObject.CYCOBJECT_CYCVARIABLE: // 6;
		{
			Object result = cyclVarResult(varmap, (CycVariable) lisp);
			return (result == null) ? lisp : ((result == lisp) ? result
					: lispToProlog(varmap, result));
		}
		case CycObject.CYCOBJECT_STRING: // 12;
			return new Fun("s", lisp.toString());
		case CycObject.CYCOBJECT_GUID: // 9;
			return new Fun("g", lisp.toString());
		case CycObject.CYCOBJECT_DOUBLE: // 7;
		case CycObject.CYCOBJECT_FLOAT: // 8;
			return new Double(((Number) lisp).doubleValue());
		case CycObject.CYCOBJECT_INTEGER: // 10;
		case CycObject.CYCOBJECT_LONG: // 11;
		case CycObject.CYCOBJECT_BIGINTEGER: // 13;
			return new Integer(((Number) lisp).intValue());
		case CycObject.CYCOBJECT_CYCASSERTION: // 2;
		{
			CycAssertion assertion = (CycAssertion) lisp;
			try {
				return new Fun("ist", lispToProlog(varmap, assertion.getMt()),
						lispToProlog(varmap, assertion.getFormulaFromCyc(CycAPI
								.current())));
			} catch (Throwable t) {
				debug(t);
				return new Fun("ist", lispToProlog(varmap, assertion.getMt()),
						lispToProlog(varmap, assertion.getFormula()));
			}
		}
		case CycObject.CYCOBJECT_CYCFORT: // 3;
			if (lisp instanceof CycNart) {
				CycNart nart = (CycNart) lisp;
				return new Fun("nart", lispToProlog(varmap, nart.getFunctor()),
						cyclistToProlog(varmap, (CycList) nart.getArguments()));
			} else {
				String name = "" + lisp;
				// name=name.substring(name.lastIndexOf(":")+1);
				return name;
			}
		case CycObject.CYCOBJECT_BYTEARRAY: // 1;
			return new Fun("ByteArray", new Object[] { javaToProlog(varmap,
					((ByteArray) lisp).byteArrayValue()) });
		case CycObject.CYCOBJECT_UNKNOWN: // 0;
			// return javaToProlog(varmap,lisp);
		default:
			break;
		}
		if (lisp instanceof CycLTerm) {
			return new Fun("i", typeToProlog(lisp.getClass()), lisp);
		}
		return javaToProlog(varmap, lisp);
	}

	static public Object cyclistToProlog(Map<Var, CycVariable> varmap,
			CycList list) {
		Object cons = null;
		int len = 0;
		try {
			len = ((Integer) ArrayList.class.getMethod("size", (Class[]) null)
					.invoke(list, (Object[]) null)).intValue(); // will
			// this
			// work
			// on a
			// subclass
			// of
			// arraylist
			// that
			// overloaded
			// size()..
			// to
			// not
			// call
			// the
			// overloaded
			// method?
		} catch (Throwable e) {
			debug(e);
			debug("while in cyclistToProlog ");
		}
		if (!list.isProperList()) {
			cons = lispToProlog(varmap, list.getDottedElement());
			// len--;
		} else {
			cons = "[]";
		}
		while (len-- > 0)
			cons = new Fun(".", lispToProlog(varmap, list.get(len)), cons);
		return cons;
	}

	static public Object toObject(Object obj) {
		return toObject(obj);
		// return null;
	}

	static public String prefixUpper(String PREFIX, String e) {
		String[] sp = e.split("_");
		for (int i = 0; i < sp.length; i++) {
			sp[i] = sp[i].substring(0, 1).toUpperCase() + sp[i].substring(1);
		}
		e = ConsoleChannel.joinString(sp, "_");
		sp = e.split(" ");
		e = ConsoleChannel.joinString(sp, "-");
		return PREFIX + e;
	}

	static public String unprefixUpper(String e) {
		int colon = e.indexOf(":");
		if (colon > -1) {
			e = e.substring(colon + 1);
		}
		if (e.startsWith("#$")) {
			e = e.substring(2);
		}
		String[] sp = e.split("-");
		e = ConsoleChannel.joinString(sp, " ");
		return e;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see daxclr.doom.IDoomConsoleListener#call(java.lang.Object,
	 *      java.lang.String, java.lang.Object[])
	 */
	public Serializable invokeMethod(String cmd, Object[] cmdArgs)
			throws NoSuchMethodException {
		// TODO Auto-generated method stub
		return eventMissing(this, cmd, cmdArgs);
	}

	public static final Object[] prependArray(final Object bot,
			final Object[] args) {
		if (args == null) {
			if (bot == null)
				return new Object[1];
			final Object[] r = (Object[]) Array.newInstance(bot.getClass(), 1);
			r[0] = bot;
			return r;
		}
		final int len = args.length;
		final Class clz = args.getClass().getComponentType();
		final Object[] toret = (Object[]) Array.newInstance(clz, len + 1);
		toret[0] = bot;
		for (int i = 0; i < args.length; i++)
			toret[i + 1] = args[i];
		return toret;
	}

	static public Object[] toArray(Map<Var, CycVariable> varmap, Object o) {
		if (varmap == null)
			varmap = getVarMap(varmap);
		if (o instanceof Fun) {
			Fun f = (Fun) o;
			try {
				Class clz = getRepository().toClass(f.name);
				Object[] oo = (Object[]) Array.newInstance(clz, f.args.length);
				for (int i = 0; i < f.args.length; i++) {
					Array.set(oo, i, prologToJava(varmap, f.args[i]));
				}
				return oo;
			} catch (ClassNotFoundException cnf) {
			}
		}
		o = prologToJava(varmap, o);
		if (o instanceof Object[]) {
			return (Object[]) o;
		}
		if (o instanceof Map) {
			ArrayList<Object> al = new ArrayList<Object>();
			Map map = (Map) o;
			Iterator it = map.entrySet().iterator();
			while (it.hasNext()) {
				al.add(javaToProlog(varmap, it.next()));
			}
			return al.toArray();
		}
		if (o instanceof Collection) {
			Collection it = (Collection) o;
			return it.toArray();
		}
		if (o instanceof Iterator) {
			ArrayList al = new ArrayList();
			Iterator it = (Iterator) o;
			while (it.hasNext()) {
				al.add(it.next());
			}
			return al.toArray();
		}
		if (o instanceof Enumeration) {
			ArrayList al = new ArrayList();
			Enumeration it = (Enumeration) o;
			while (it.hasMoreElements()) {
				al.add(it.nextElement());
			}
			return al.toArray();
		}
		return new Object[] { o };
	}
}
