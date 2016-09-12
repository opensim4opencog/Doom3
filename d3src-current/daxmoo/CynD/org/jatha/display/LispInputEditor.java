/*
 * Jatha - a Common LISP-compatible LISP library in Java.
 * Copyright (C) 1997-2005 Micheal Scott Hewett
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 2.1 of the License, or (at your option) any later version.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA
 *
 *
 * For further information, please contact Micheal Hewett at
 *   hewett@cs.stanford.edu
 *
 */

package org.jatha.display;

import javax.swing.plaf.basic.BasicComboBoxEditor;
import javax.swing.*;
import javax.swing.text.JTextComponent;
import java.awt.*;

/**
 * LispInputEditor Is a ComboxBoxEditor for the LispInput area.
 * The default editor (BasicComboBoxEditor) uses a JTextField
 * as an editing area.  This one uses a JTextArea if the number
 * of lines sent in is greater than 1.
 */
public class LispInputEditor extends BasicComboBoxEditor
{
  protected int f_numberOfRows     = 15;
  protected int f_numberOfColumns  = 60;
  protected JTextComponent f_textComponent = null;

  public LispInputEditor()
  {
    this(1);
  }

  public LispInputEditor(int numberOfRows)
  {
    super();

    f_numberOfRows = numberOfRows;
    if (f_numberOfRows > 1)
      f_textComponent = new JTextArea(f_numberOfRows, f_numberOfColumns);
    else
      f_textComponent = new JTextField();
  }

  public Component getEditorComponent()
  {
    // return super.getEditorComponent();    //To change body of overridden methods use File | Settings | File Templates.
    return f_textComponent;
  }

  public Object getItem()
  {
    // return super.getItem();    //To change body of overridden methods use File | Settings | File Templates.
    return f_textComponent.getText();
  }

  public void setItem(Object o)
  {
    //super.setItem(o);    //To change body of overridden methods use File | Settings | File Templates.
    if (o != null)
      f_textComponent.setText(o.toString());
  }

  public int getNumberOfTextLines()
  {
    if (f_textComponent instanceof JTextArea)
      return ((JTextArea)f_textComponent).getRows();
    else
      return 1;
  }

  public void incrementTextLines(int increment)
  {
    int newRows = getNumberOfTextLines() + increment;
    if (newRows < 1)
      return;

    if (f_textComponent instanceof JTextArea)
      ((JTextArea)f_textComponent).setRows(newRows);

    else if (newRows > 1)
      f_textComponent = new JTextArea(newRows, 80);

    f_numberOfRows = newRows;

    //this.invalidate();

  }
}
