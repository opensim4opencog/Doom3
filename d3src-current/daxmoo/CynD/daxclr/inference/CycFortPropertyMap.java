package daxclr.inference;

import java.util.AbstractMap;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;
import org.opencyc.cycobject.CycFort;
import org.opencyc.cycobject.CycList;
import org.opencyc.cycobject.CycObject;

import daxclr.bsf.AbstractPropertySource;
import daxclr.bsf.ConsoleChannel;
import daxclr.bsf.IScriptObject;

/**
 * 
 * This class is a Map interface of a CycFort
 * 
 */
public class CycFortPropertyMap extends AbstractMap<String, Object> {
	// one minute
	final public static transient long REFRESH_TIME = 60000;

	final public static transient HashMap<CycFort, CycFortPropertyMap> cycFortPropertyMaps = new HashMap<CycFort, CycFortPropertyMap>();

	final public static transient CycAPI cycAccess = CycAPI.current();

	// private CycObject cycFort = null;
	public static CycFortPropertyMap toCycObjectInfo(IScriptObject so) {
		CycFort key = so.getCycObject();
		CycFortPropertyMap info = cycFortPropertyMaps.get(key);
		if (info == null) {
			info = new CycFortPropertyMap(so, key, CycAPI.getDynamicMt());
			cycFortPropertyMaps.put(key, info);
		}
		return info;
	}

	final public Set<java.util.Map.Entry<String, Object>> entries = new HashSet<java.util.Map.Entry<String, Object>>();

	final public CycFort cycFort;

	final public CycObject defaultAssertMt;

	final public CycPropertyThread cycPredsWorkerThread;

	int modcount = 0;

	private IScriptObject scriptObject;

	private CycFortPropertyMap(IScriptObject scriptObject0, CycFort fort,
			CycObject assertMt) {
		scriptObject = scriptObject0;
		cycFort = fort;
		if (assertMt == null) {
			defaultAssertMt = CycAPI.getDynamicMt();
		} else {
			defaultAssertMt = assertMt;
		}
		cycPredsWorkerThread = new CycPropertyThread();
		cycPredsWorkerThread.start();
	}

	public void addPredicates(Collection keys) {
		Iterator its = keys.iterator();
		while (its.hasNext())
			addPredicate(its.next());
	}

	public void addPredicate(Object key) {
		if (containsKey("" + key))
			return;
		if (!(key instanceof CycObject))
			return;
		modcount++;
		synchronized (entries) {
			entries.add(new CycPropertyEntry(scriptObject, (CycObject) key,
					CycAPI.getQueryMt(), defaultAssertMt));
		}
	}

	public class CycPropertyThread extends Thread {
		public CycPropertyThread() {
			super("" + cycFort);
		}

		public void run() {
			String api = getCycObject().cyclify();
			while (true) {
				addPredicates(cycAccess
						.converseList("(ask-template '?PRED '(#$and (#$isa "
								+ api
								+ " ?TYPE) (#$most-GenQuantRelnTo ?PRED ?TYPE ??VALUE)) #$EverythingPSC)"));
				addPredicates(cycAccess
						.converseList("(ask-template '?PRED '(#$and (#$isa "
								+ api
								+ " ?TYPE) (#$most-GenQuantRelnToType ?PRED ?TYPE ??VALUE)) #$EverythingPSC)"));
				// addPredicates(cycAccess.converseList("(ask-template '?PRED
				// '(#$and (#$isa #$Cyc ?TYPE) (#$arg1Isa ?PRED ?TYPE))
				// #$EverythingPSC)"));
				// addPredicates(cycAccess.converseList("(ask-template '?PRED
				// '(?PRED #$Cyc ?TYPE) #$EverythingPSC)"));
				try {
					CycPropertyThread.sleep(REFRESH_TIME);
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}
	}

	public Object get(Object key) {
		Object result = super.get(key);
		if (result == null) {
			if (key instanceof String && !key.toString().startsWith("#$"))
				result = super.get("#$" + key);
		}
		return result;
	}

	public CycObject getCycObject() {
		return cycFort;
	}
	public Object put(String key, Object value) {
		addPredicate(key);
		return super.put((String)key,value);
	}

	@Override
	public Set<java.util.Map.Entry<String, Object>> entrySet() {
		synchronized (entries) {
			return entries;
		}
	}

	public int size() {
		return entries.size();
	}
}