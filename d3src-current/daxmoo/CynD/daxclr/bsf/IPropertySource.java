package daxclr.bsf;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

import sun.reflect.FieldAccessor;
import sun.reflect.MethodAccessor;

public interface IPropertySource extends java.util.Map.Entry<String, Object>, FieldAccessor, MethodAccessor, PropertyChangeListener,IPropertySourceImpl {
    public void changeValue(IScriptObject source, Object old, Object value) throws NoSuchFieldException, UnsupportedOperationException,ClassCastException, IllegalArgumentException, NullPointerException,IllegalStateException;
    public void firePropertyChange(PropertyChangeEvent evt);
    public IScriptObject getSource();
    public boolean isFinal();
}
