

package com.netbreeze.util;
import java.util.*;
import java.io.*;

public class PairTable {
  //Note: a "pair" is simply an array of 2 objects

  //object -> its brother
  Hashtable hash = new Hashtable();

  public PairTable() {
  }

  public void add(Object o1, Object o2) {
    if (o1 != null && o2 != null) {
      remove(o1);
      remove(o2);
      hash.put(o1, o2);
      hash.put(o2, o1);
    }
  }

  public int getPairCount() {
    return hash.size()/2;
  }

  public boolean contains(Object o) {
    return hash.containsKey(o);
  }

  public Object findBrother(Object o1) {
    if (o1 == null)
      return null;
    else
      return hash.get(o1);
  }

  public void remove(Object o1) {
    Object o2 = findBrother(o1);
    if (o2 != null) {
      hash.remove(o1);
      hash.remove(o2);
    }
  }

  public Vector getPairs() {
    int size = hash.size() / 2;

    Vector pairs = new Vector();
    Vector accounted = new Vector();

    Enumeration keys = hash.keys();
    Object o1, o2;
    Object[] pair;
    while (keys.hasMoreElements()) {
      o1 = keys.nextElement();
      if (!accounted.contains(o1)) {
        o2 = findBrother(o1);
        pair = new Object[] {o1, o2};
        pairs.addElement(pair);
        accounted.addElement(o2);
      }
    }
    return pairs;
  }

  public void list(PrintStream ps) {
    Enumeration enumer = getPairs().elements();
    while(enumer.hasMoreElements()) {
      Object[] pair = (Object[]) enumer.nextElement();
      ps.println("[" + pair[0] + "] [" + pair[1] + "]");
    }
  }

  public static boolean isEqual(Object o1, Object o2) {
    if (o1 == null)
      return (o2 == null);
    else
      return (o1.equals(o2));
  }
}