package daxclr.bsf;

public interface IPropertySourceImpl {
    public Object getValueImpl(Object source) throws UnsupportedOperationException;      
    public Object invokeImpl(Object source,Object[] params) throws UnsupportedOperationException, ClassCastException, IllegalArgumentException, NullPointerException, IllegalStateException;    
    public void setValueImpl(Object source,Object value)throws UnsupportedOperationException,ClassCastException,IllegalArgumentException,NullPointerException,IllegalStateException, IllegalAccessException;
}
