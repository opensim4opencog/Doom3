package com.techtrader.modules.tools.bytecode.visitor;


import java.io.*;

import com.techtrader.modules.tools.bytecode.*;
import com.techtrader.modules.tools.bytecode.lowlevel.*;


/**
 *	Visitor type that outputs a detailed, formatted document of the 
 *	visited entity; similar to the <i>javap -c</i> command but more detailed.
 *	
 *	@author		Abe White
 */
public class PrettyPrintVisitor
	extends BCVisitor
	implements Constants
{
	private PrintWriter _out 		= null;
	private String		_prefix		= "";
	private int 		_entryCount	= 0;


	/**
	 *	Invoke with the class or file names to pretty print; the 
	 *	functionality is similar to the <i>javap -c</i> command, but more 
	 *	detailed. 
	 */
	public static void main (String[] args)
		throws ClassNotFoundException, IOException
	{
		if (args.length == 0)
		{
			System.err.println ("Usage: PrettyPrintVisitor <classname>+");
			System.exit (1);
		}

		PrettyPrintVisitor ppv = new PrettyPrintVisitor ();
		BCClass type;
		for (int i = 0; i < args.length; i++)
		{
			if (args[i].endsWith (".class"))
				type = new BCClass (new File (args[i]));
			else
				type = new BCClass (Class.forName (args[i]));
			ppv.visit (type);	
		}
	}


	/**
	 *	Constructor; all pritning will go to stdout.
	 */
	public PrettyPrintVisitor ()
	{
		_out = new PrintWriter (System.out);
	}


	/**
	 *	Constructor.
	 *
	 *	@param	out		the stream to print to
	 */
	public PrettyPrintVisitor (PrintWriter out)
	{
		_out = out;
	}


	public void visit (VisitAcceptor entity)
	{
		super.visit (entity);
		_out.flush ();
	}


	public void enterBCClass (BCClass obj)
	{
		openBlock ("Class");

		println ("magic=" + obj.getMagic ());
		println ("minor=" + obj.getMinorVersion ());
		println ("major=" + obj.getMajorVersion ());
		println ("access=" + obj.getAccessFlags ());
		println ("name=" + obj.getIndex () + " <" + obj.getName () + ">");
		println ("super=" + obj.getSuperclassIndex () 
			+ " <" + obj.getSuperclassName () + ">");

		int[] indexes = obj.getInterfaceIndexes ();
		String[] names = obj.getInterfaceNames ();
		for (int i = 0; i < indexes.length; i++)
			println ("interface=" + indexes[i] + " <" + names[i] + ">");
	}


	public void exitBCClass (BCClass obj)
	{
		closeBlock ();
	}


	public void enterBCField (BCField obj)
	{
		openBlock ("Field");
		println ("access=" + obj.getAccessFlags ());
		println ("name=" + obj.getNameIndex () + " <" + obj.getName () + ">");
		println ("descriptor=" + obj.getDescriptorIndex () 
			+ " <" + obj.getTypeName () + ">");
	}


	public void exitBCField (BCField obj)
	{
		closeBlock ();
	}


	public void enterBCMethod (BCMethod obj)
	{
		openBlock ("Method");
		println ("access=" + obj.getAccessFlags ());
		println ("name=" + obj.getNameIndex () + " <" + obj.getName () + ">");
		println ("descriptor=" + obj.getDescriptorIndex ());
		println ("return=" + obj.getReturnTypeName ()); 
		String[] params = obj.getParamTypeNames ();
		for (int i = 0; i < params.length; i++)
			println ("param=" + params[i]);
	}


	public void exitBCMethod (BCMethod obj)
	{
		closeBlock ();
	}


	public void enterAttribute (Attribute obj)
	{
		openBlock (obj.getName ());
	}


	public void exitAttribute (Attribute obj)
	{
		closeBlock ();
	}


	public void enterConstantValueAttribute (ConstantValueAttribute obj)
	{
		println ("value=" + obj.getValueIndex () + " <" + obj.getTypeName ()
			+ "=" + obj.getValue () + ">");	
	}


	public void enterExceptionsAttribute (ExceptionsAttribute obj)
	{
		int[] indexes = obj.getExceptionIndexes ();
		String[] names = obj.getExceptionTypeNames ();
		for (int i = 0; i < indexes.length; i++)
			println ("exception=" + indexes[i] + " <" + names[i] + ">");
	}


	public void enterSourceFileAttribute (SourceFileAttribute obj)
	{
		println ("source=" + obj.getSourceFileIndex () + " <"
			+ obj.getSourceFile () + ">");
	}


	public void enterUnknownAttribute (UnknownAttribute obj)
	{
		println ("value=" + new String (obj.getValue ()));
	}


	public void enterCode (Code obj)
	{
		println ("maxStack=" + obj.getMaxStack ());
		println ("maxLocals=" + obj.getMaxLocals ());
		println ("");
	}


	public void enterExceptionHandler (ExceptionHandler obj)
	{
		openBlock ("ExceptionHandler");
		println ("startPc=" + obj.getStartPc ());
		println ("endPc=" + obj.getEndPc ());
		println ("handlerPc=" + obj.getHandlerPc ());
		println ("catch=" + obj.getCatchTypeIndex () + " <"
			+ obj.getCatchTypeName () + ">");
	}


	public void exitExceptionHandler (ExceptionHandler obj)
	{
		closeBlock ();
	}


	public void enterInnerClass (InnerClass obj)
	{
		openBlock ("InnerClass");
		println ("access=" + obj.getAccessFlags ());
		println ("name=" + obj.getNameIndex () + " <" + obj.getName () + ">");
		println ("index=" + obj.getIndex ());
		println ("outer=" + obj.getOuterClassIndex ());
	}


	public void exitInnerClass (InnerClass obj)
	{
		closeBlock ();
	}


	public void enterLineNumber (LineNumber obj)
	{
		openBlock ("LineNumber");
		println ("startPc=" + obj.getStartPc ());
		println ("line=" + obj.getLineNumber ());
	}


	public void exitLineNumber (LineNumber obj)
	{
		closeBlock ();
	}


	public void enterLocalVariable (LocalVariable obj)
	{
		openBlock ("LocalVariable");
		println ("startPc=" + obj.getStartPc ());
		println ("length=" + obj.getLength ());
		println ("index=" + obj.getIndex ());
		println ("name=" + obj.getNameIndex () + " <" + obj.getName () + ">");
		println ("descriptor=" + obj.getDescriptorIndex () 
			+ " <" + obj.getTypeName () + ">");
	}


	public void exitLocalVariable (LocalVariable obj)
	{
		closeBlock ();
	}


	public void enterInstruction (Instruction obj)
	{
		_out.print (_prefix + obj.getByteIndex () + " " + obj.getName () + " ");
	}


	public void exitInstruction (Instruction obj)
	{
		_out.println ();
	}


	public void enterClassInstruction (ClassInstruction obj)
	{
		_out.print (obj.getClassIndex () + " <" + obj.getClassName () + ">");
	}


	public void enterConstantInstruction (ConstantInstruction obj)
	{
		switch (obj.getOpCode ())
		{
		case BIPUSH:
		case SIPUSH:
		case LDC:
		case LDC_W:
		case LDC2_W:
			_out.print (obj.getConstant ().toString ());
		}
	}


	public void enterGetFieldInstruction (GetFieldInstruction obj)
	{
		_out.print (obj.getFieldIndex () + " <" + obj.getFieldOwnerTypeName ()
			+ "." + obj.getFieldName () + ">");
	}


	public void enterIIncInstruction (IIncInstruction obj)
	{
		_out.print (obj.getIndex () + " ");
		if (obj.getIncrement () > 0)
			_out.print ("+");
		_out.print (obj.getIncrement ());
	}


	public void enterJumpInstruction (JumpInstruction obj)
	{
		_out.print (obj.getOffset ());
	}


	public void enterLoadInstruction (LoadInstruction obj)
	{
		switch (obj.getOpCode ())
		{
		case ILOAD:
		case LLOAD:
		case FLOAD:
		case DLOAD:
		case ALOAD:
			_out.print (obj.getIndex ());
		}
	}


	public void enterLookupSwitchInstruction (LookupSwitchInstruction obj)
	{
		_out.println ();
		_prefix += "  ";

		int[] offsets = obj.getOffsets ();
		int[] matches = obj.getMatches ();
		for (int i = 0; i < offsets.length; i++)
			println ("case " + matches[i] + "=" + offsets[i]);
		_out.print (_prefix + "default=" + obj.getDefaultOffset ());

		_prefix = _prefix.substring (2);
	}


	public void enterMethodInstruction (MethodInstruction obj)
	{
		_out.print (obj.getMethodIndex () + " <" + obj.getMethodOwnerTypeName ()
			+ "." + obj.getMethodName () + "(");
		String[] params = obj.getMethodParamTypeNames ();
		int dotIndex;
		for (int i = 0; i < params.length; i++)
		{
			dotIndex = params[i].lastIndexOf ('.');
			if (dotIndex != -1)
				params[i] = params[i].substring (dotIndex + 1);

			_out.print (params[i]);
			if (i != params.length - 1)
				_out.print (", ");
		}

		_out.print (")>");
	}


	public void enterMultiANewArrayInstruction (MultiANewArrayInstruction obj)
	{
		_out.print (obj.getClassIndex () + " " + obj.getDimensions ()
			+ " <" + obj.getClassName ());
		String post = "";
		for (int i = 0; i < obj.getDimensions (); i++)
			post += "[]";
		_out.print (post + ">");	
	}


	public void enterNewArrayInstruction (NewArrayInstruction obj)
	{
		_out.print (obj.getArrayTypeCode () + " <" 
			+ obj.getArrayTypeName () + "[]>");
	}


	public void enterPutFieldInstruction (PutFieldInstruction obj)
	{
		_out.print (obj.getFieldIndex () + " <" + obj.getFieldOwnerTypeName ()
			+ "." + obj.getFieldName () + ">");
	}


	public void enterRetInstruction (RetInstruction obj)
	{
		_out.print (obj.getIndex ());
	}


	public void enterStoreInstruction (StoreInstruction obj)
	{
		switch (obj.getOpCode ())
		{
		case ISTORE:
		case LSTORE:
		case FSTORE:
		case DSTORE:
		case ASTORE:
			_out.print (obj.getIndex ());
		}
	}


	public void enterTableSwitchInstruction (TableSwitchInstruction obj)
	{
		_out.println ();
		_prefix += "  ";

		println ("low=" + obj.getLow ());
		println ("high=" + obj.getHigh ());
		int[] offsets = obj.getOffsets ();
		for (int i = 0; i < offsets.length; i++)
			println ("case=" + offsets[i]);
		_out.print (_prefix + "default=" + obj.getDefaultOffset ());

		_prefix = _prefix.substring (2);
	}


	public void enterWideInstruction (WideInstruction obj)
	{
		int ins = obj.getInstruction ();
		_out.print (ins + " <" + OPCODE_NAMES[ins] + ">");
	}


	public void enterConstantPool (ConstantPool obj)
	{
		_entryCount = 0;
		openBlock ("ConstantPool");
	}


	public void exitConstantPool (ConstantPool obj)
	{
		closeBlock ();
	}


	public void enterEntry (Entry obj)
	{
		String name = obj.getClass ().getName ();
		openBlock (++_entryCount + ": " 
			+ name.substring (name.lastIndexOf ('.') + 1));
	}


	public void exitEntry (Entry obj)
	{
		closeBlock ();
	}


	public void enterClassEntry (ClassEntry obj)
	{
		println ("name=" + obj.getNameIndex ());
	}


	public void enterDoubleEntry (DoubleEntry obj)
	{
		println ("value=" + obj.getValue ());
	}


	public void enterFieldEntry (FieldEntry obj)
	{
		println ("class=" + obj.getClassIndex ());
		println ("nameAndType=" + obj.getNameAndTypeIndex ());
	}


	public void enterFloatEntry (FloatEntry obj)
	{
		println ("value=" + obj.getValue ());
	}


	public void enterIntEntry (IntEntry obj)
	{
		println ("value=" + obj.getValue ());
	}


	public void enterInterfaceMethodEntry (InterfaceMethodEntry obj)
	{
		println ("class=" + obj.getClassIndex ());
		println ("nameAndType=" + obj.getNameAndTypeIndex ());
	}


	public void enterLongEntry (LongEntry obj)
	{
		println ("value=" + obj.getValue ());
	}


	public void enterMethodEntry (MethodEntry obj)
	{
		println ("class=" + obj.getClassIndex ());
		println ("nameAndType=" + obj.getNameAndTypeIndex ());
	}


	public void enterNameAndTypeEntry (NameAndTypeEntry obj)
	{
		println ("name=" + obj.getNameIndex ());
		println ("descriptor=" + obj.getDescriptorIndex ());
	}


	public void enterStringEntry (StringEntry obj)
	{
		println ("index=" + obj.getStringIndex ());
	}


	public void enterUTF8Entry (UTF8Entry obj)
	{
		println ("value=" + obj.getValue ());
	}


	private void println (String ln)
	{
		_out.print (_prefix);
		_out.println (ln);
	}


	private void openBlock (String name)
	{
		println (name + " {");
		_prefix += "  ";
	}

	
	private void closeBlock ()
	{
		_prefix = _prefix.substring (2);
		println ("}");
	}
}
