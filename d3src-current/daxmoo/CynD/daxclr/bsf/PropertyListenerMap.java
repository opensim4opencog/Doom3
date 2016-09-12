package daxclr.bsf;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.util.AbstractMap;
import java.util.EventListener;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

// 90mv
// 250mv
// 

/* Object is part of the doom game */
public class PropertyListenerMap extends AbstractMap<Object,Object> implements java.beans.PropertyChangeListener {
    final public Map<Object,Object> map;
	public final transient PropertyChangeSupport pcSupport =  new PropertyChangeSupport(this); //propertyListenerMap.getPropertyChangeSupport();// new PropertyChangeSupport(propertyListenerMap);
    // public Object source = this;
    /* True when the needs rebuilt when the map/game is reloaded*/
    //final Set<PropertyChangeListener> listeners = new HashSet<PropertyChangeListener>();
    public PropertyListenerMap(Map<Object,Object> map1) {
        this.map = map1;     
   }

    /**
	 * @param listener
	 */
	public void addListener(EventListener listener) {
        if (listener instanceof PropertyChangeListener)
            pcSupport.addPropertyChangeListener((PropertyChangeListener)listener);
	}

	public boolean equals(Object obj){
        return(obj!=null && (obj instanceof Map) && (obj==this||obj.equals(map)||obj.equals(this)));
    }

    /**
     * This method gets called when a bound property is changed.
     * @param evt A PropertyChangeEvent object describing the event source
     *   	and the property that has changed.
     */
    public void propertyChange(PropertyChangeEvent evt) {
        if ( this != evt.getSource()) {
            String key = evt.getPropertyName();
            Iterator<Object>keys = this.keySet().iterator();
            while (keys.hasNext()) {
                Object k = keys.next();
                if (key.equals(""+k)) {
                    this.map.put(k,evt.getNewValue());
                }
            }
        } else {
        	pcSupport.firePropertyChange(evt);        	
        }
    }


    public Object get(Object key) {
        return map.get(key);
    }
    public Object remove(Object key) {
        Object old =  map.remove(key);
        propertyChange(new PropertyChangeEvent(PropertyListenerMap.this,""+key,old,null));
        return old;
    }
    public Object put(Object key,Object value) {
        Object old = map.put(key, value);
        propertyChange(new PropertyChangeEvent(PropertyListenerMap.this,""+key,old,value));
        return old;
    }

    public Set<Map.Entry<Object,Object>> entrySet() {
        return(new java.util.AbstractSet<Map.Entry<Object,Object>>() {
                   public int size() {
                       return PropertyListenerMap.this.map.size();
                   }
                   public EntryListenerIterator iterator() {
                       return new EntryListenerIterator(map.entrySet().iterator());
                   }
                   @SuppressWarnings("unchecked")
                   public boolean add(Map.Entry entry){
                       if (map.entrySet().add(entry)) {
                           propertyChange(new PropertyChangeEvent(PropertyListenerMap.this, ""+entry.getKey(),null, entry.getValue()));
                           return true;
                       }
                       return false; 
                   }
               });
    }
    class EntryListenerIterator implements java.util.Iterator {
        final Iterator<Map.Entry<Object,Object>> iterator;
        EntryListenerIterator(Iterator<Map.Entry<Object,Object>> its ) {
            iterator = its;
        }
        public boolean hasNext() {
            return iterator.hasNext();
        }

        public EntryListener next() {
            return new EntryListener((Map.Entry<Object,Object>)iterator.next());
        }

        public void remove() {
            iterator.remove();          
        }
        class EntryListener implements java.util.Map.Entry<Object,Object> {
            final private java.util.Map.Entry<Object,Object> entry;
            public EntryListener(Map.Entry<Object,Object> entry1) {
                this.entry = entry1;
            }
            public Object getValue() {
                return entry.getValue();
            }
            public Object setValue(Object value) {
                Object key = (Object) entry.getKey();
                Object put = (Object) put(key,value);
                return put;
            }
            public Object getKey() {
                return entry.getKey();
            }
        }
    }
}


