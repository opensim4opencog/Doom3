package com.netbreeze.swing;

import javax.swing.*;

import daxclr.bsf.IScriptObject;

import java.util.*;
import java.lang.reflect.*;
import java.beans.*;

/**
 * A GUI component that shows all the static methods for a given class,
 * and provides ways of executing these methods.
 *
 * @author Henrik Kniberg
 */
 public class StaticMethodList extends JList {
  private Model model;
  private Class cls;

  public StaticMethodList(Class cls) throws Exception {
    super();
    this.cls = cls;
    this.model = new Model();
    setModel(model);
  }

  public Method getSelectedMethod() {
    return model.getMethodAt(getSelectedIndex());
  }

  class Model extends AbstractListModel {
    List methods;

    public Model() throws Exception {
      if (cls==null) cls = IScriptObject.class;
      Method[] methodsArray = cls.getMethods();

      methods = new LinkedList();

      for (int i = 0; i < methodsArray.length; ++i) {
        Method method = methodsArray[i];
        if (Modifier.isStatic(method.getModifiers())) {
          methods.add(method);
        }
      }

      Collections.sort(methods, new MethodComparator());
    }

    public Method getMethodAt(int index) {
      try {
        return (Method) methods.get(index);
      } catch (Exception err) {
        return null;
      }
    }

    public Object getElementAt(int index) {
      try {
        return getMethodAt(index).getName();
      } catch (Exception err) {
        return null;
      }
    }

    public int getSize() {
      return methods.size();
    }
  }

  class MethodComparator implements Comparator {
    public int compare(Object first, Object second) {
      Method a = (Method) first;
      Method b = (Method) second;
      String nameA = a.getName();
      String nameB = b.getName();
      return nameA.compareTo(nameB);
    }

    public boolean equals(Object o) {
      return (o instanceof MethodComparator);
    }
  }
}
