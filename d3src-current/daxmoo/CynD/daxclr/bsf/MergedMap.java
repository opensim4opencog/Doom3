/**
 * 
 */
package daxclr.bsf;

import java.util.AbstractMap;
import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Map;
import java.util.Set;

/**
 * @author Administrator
 *
 */
class MergedMap extends AbstractMap<Object,Object> {
    final Set<Object> keys = new HashSet<Object>();   
    final Collection<Map<Object,Object>> maps; 

    public MergedMap(Collection<Map<Object,Object>> maps1) {
        if (maps1!=null) {
            this.maps = maps1;
        } else {
            this.maps = new LinkedList<Map<Object,Object>>();
        }
        updateKeys();
    }
    public class MultiMapEntry implements Map.Entry<Object,Object> {
        final Object key;
        MultiMapEntry(Object key1) {
            this.key=key1;
        }
        public Object getKey() {
            return key;
        }
        public Object getValue() {
            return get(key);
        }
        public Object setValue(Object value) {
            return put(key,value);
        }
    }
    public class MultiMapIterator implements Iterator<Entry<Object, Object>> {
        final Iterator<Object> iterator;
        java.util.Map.Entry<Object,Object> last = null;

        MultiMapIterator(Iterator<Object> iterator1) {
            this.iterator = iterator1;
        }
        public boolean hasNext() {
            return iterator.hasNext();
        }
        public java.util.Map.Entry<Object,Object> next() {
            MultiMapEntry multiMapEntry = new MultiMapEntry(iterator.next());
			last = multiMapEntry;;
            return last;
        }
        public void remove() {
            iterator.remove();
            MergedMap.this.remove(last.getKey());
        }
    }
    @SuppressWarnings("unchecked")
	public void putAll(Map map) {
        super.putAll(map);
    }
    @SuppressWarnings("unchecked")
	public void addMap(Map map) {
        if (!maps.contains(map)) {
            Map<Object, Object> map2 = (Map<Object,Object>)map;
			maps.add(map2);
        }
        updateKeys();
    }

    static private Class<?> entryClass(Map<Object, Object> map){
        Iterator its = map.entrySet().iterator();
        if (its.hasNext()) {
            return its.next().getClass();
        }
        return null;
    }
    @SuppressWarnings("unchecked")
	static private Map.Entry<Object,Object> findEntry(Object key, Map<Object, Object> map){
        Iterator entries = map.entrySet().iterator();
        while (entries.hasNext()) {
            Map.Entry<Object,Object> next = (Map.Entry<Object,Object>)entries.next();
			Map.Entry<Object,Object> entry = next;
            if (entry.getKey().equals(key)) return entry;
        }
        return null;
    }
    public Object put(Object key, Object value){
        Object result = null;
        keys.add(key);
        Iterator<Map<Object,Object>> its = maps.iterator();
        while (result==null && its.hasNext()) {
            Map<Object, Object> map = its.next();
            Map.Entry<Object,Object> entry = findEntry(key,map);
            if (entry!=null) {
                result = entry.setValue(value);
            } else {
                Class c = entryClass(map);
                Object k = key;
                if (k!=null) {
                    if (c!=null) {
                        try {
                            k = c.cast(key);
                        } catch ( ClassCastException cce ) {
                            System.err.println("debug: wont convert key='" + k + "' " + k.getClass() + " to " +c );
                        }
                        try {
                            result = map.put(k,value);
                        } catch ( Throwable t ) {
                            t.printStackTrace();
                            System.err.println("debug: wont put key='" + k + "' " + k.getClass() + " to " +c+ "  as " + value );
                        }

                    }
                }
            }
        }       
        return result;
    }

    public Object get(Object key){
        Object result = null;
        Iterator<Map<Object,Object>> its = maps.iterator();
        while (result==null && its.hasNext()) {
        	Map<Object,Object> next = its.next();
			Entry entry = findEntry(key,next);
            if (entry!=null) {
                result = entry.getValue();
            }
        }       
        return result;
    }

    private void updateKeys() {
        keys.clear();
        Iterator<Map<Object,Object>> its = maps.iterator();
        while (its.hasNext()) {
            Set keySet2 = its.next().keySet();
            Iterator kits = keySet2.iterator();
            while (kits.hasNext()) {
                keys.add(kits.next());    
            }
        }       
    }


    public Set<Object> keySet() {
        return(new java.util.AbstractSet<Object>(){
                   @Override
                   public Iterator<Object> iterator() {
                       return keys.iterator();
                   }
                   @Override
                   public int size() {
                       return keys.size();
                   }
               });
    }

    /* (non-Javadoc)
     * @see java.util.AbstractMap#entrySet()
     */
    @Override
    public Set<Entry<Object, Object>> entrySet() {
        return(new java.util.AbstractSet<Entry<Object,Object>>() {
                   public int size() {
                       return keys.size();
                   }
                   public Iterator<Entry<Object, Object>> iterator() {
                       return new MultiMapIterator(keys.iterator());
                   }
                   public boolean add(Entry entry){
                       Object result = put(entry.getKey(),entry.getKey());
                       return(result==null);
                   }
               });
    }

}
