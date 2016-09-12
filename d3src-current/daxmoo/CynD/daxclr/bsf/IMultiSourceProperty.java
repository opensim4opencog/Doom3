package daxclr.bsf;

import java.lang.reflect.Field;
import java.lang.reflect.Method;

public interface IMultiSourceProperty extends IPropertySource {
    public IMultiSourceProperty getParent();
    public Method toSetMethod();
    public Method toMethod();
    public Field toField();
}