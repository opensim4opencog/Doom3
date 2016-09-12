package com.techtrader.modules.tools.bytecode.visitor;


import com.techtrader.modules.tools.bytecode.*;
import com.techtrader.modules.tools.bytecode.lowlevel.*;


/**
 *	Base class for visitors on a bytecode class.  The public visit
 *	method will traverse the object graph of the given entity, calling the
 *	enter* and exit* methods as it visits each object.  The traversal
 *	is done depth-first.  Subclasses should override only the methods
 *	for visiting the entities they are interested in.  Whenever there is
 *	a general method (i.e.  enter/exitEntry) as well as a more specific one
 *	(i.e. enter/exitStringEntry), the more general method will be called first,
 *	followed by a call on the correct specific method.  Most subclasses will
 *	override either the general or specific cases, but not both.
 *
 *	@author		Abe White
 */
public class BCVisitor
{
	/**
	 *	Visit the given entity.
	 */
	public void visit (VisitAcceptor obj)
	{
		if (obj == null)
			return;

		obj.acceptVisit (this);
	}


	public void enterBCClass (BCClass obj)
	{
	}


	public void exitBCClass (BCClass obj)
	{
	}


	public void enterBCField (BCField obj)
	{
	}


	public void exitBCField (BCField obj)
	{
	}


	public void enterBCMethod (BCMethod obj)
	{
	}


	public void exitBCMethod (BCMethod obj)
	{
	}


	public void enterAttribute (Attribute obj)
	{
	}


	public void exitAttribute (Attribute obj)
	{
	}


	public void enterConstantValueAttribute (ConstantValueAttribute obj)
	{
	}


	public void exitConstantValueAttribute (ConstantValueAttribute obj)
	{
	}


	public void enterDeprecatedAttribute (DeprecatedAttribute obj)
	{
	}


	public void exitDeprecatedAttribute (DeprecatedAttribute obj)
	{
	}


	public void enterExceptionsAttribute (ExceptionsAttribute obj)
	{
	}


	public void exitExceptionsAttribute (ExceptionsAttribute obj)
	{
	}


	public void enterInnerClassesAttribute (InnerClassesAttribute obj)
	{
	}


	public void exitInnerClassesAttribute (InnerClassesAttribute obj)
	{
	}


	public void enterLineNumberTableAttribute (LineNumberTableAttribute obj)
	{
	}


	public void exitLineNumberTableAttribute (LineNumberTableAttribute obj)
	{
	}


	public void enterLocalVariableTableAttribute 
		(LocalVariableTableAttribute obj)
	{
	}


	public void exitLocalVariableTableAttribute 
		(LocalVariableTableAttribute obj)
	{
	}


	public void enterSourceFileAttribute (SourceFileAttribute obj)
	{
	}


	public void exitSourceFileAttribute (SourceFileAttribute obj)
	{
	}


	public void enterSyntheticAttribute (SyntheticAttribute obj)
	{
	}


	public void exitSyntheticAttribute (SyntheticAttribute obj)
	{
	}


	public void enterUnknownAttribute (UnknownAttribute obj)
	{
	}


	public void exitUnknownAttribute (UnknownAttribute obj)
	{
	}


	public void enterCode (Code obj)
	{
	}


	public void exitCode (Code obj)
	{
	}


	public void enterExceptionHandler (ExceptionHandler obj)
	{
	}


	public void exitExceptionHandler (ExceptionHandler obj)
	{
	}


	public void enterInnerClass (InnerClass obj)
	{
	}


	public void exitInnerClass (InnerClass obj)
	{
	}


	public void enterLineNumber (LineNumber obj)
	{
	}


	public void exitLineNumber (LineNumber obj)
	{
	}


	public void enterLocalVariable (LocalVariable obj)
	{
	}


	public void exitLocalVariable (LocalVariable obj)
	{
	}


	public void enterInstruction (Instruction obj)
	{
	}


	public void exitInstruction (Instruction obj)
	{
	}


	public void enterArrayLoadInstruction (ArrayLoadInstruction obj)
	{
	}


	public void exitArrayLoadInstruction (ArrayLoadInstruction obj)
	{
	}


	public void enterArrayStoreInstruction (ArrayStoreInstruction obj)
	{
	}


	public void exitArrayStoreInstruction (ArrayStoreInstruction obj)
	{
	}


	public void enterClassInstruction (ClassInstruction obj)
	{
	}


	public void exitClassInstruction (ClassInstruction obj)
	{
	}


	public void enterConstantInstruction (ConstantInstruction obj)
	{
	}


	public void exitConstantInstruction (ConstantInstruction obj)
	{
	}


	public void enterConvertInstruction (ConvertInstruction obj)
	{
	}


	public void exitConvertInstruction (ConvertInstruction obj)
	{
	}


	public void enterGetFieldInstruction (GetFieldInstruction obj)
	{
	}


	public void exitGetFieldInstruction (GetFieldInstruction obj)
	{
	}


	public void enterIIncInstruction (IIncInstruction obj)
	{
	}


	public void exitIIncInstruction (IIncInstruction obj)
	{
	}


	public void enterJumpInstruction (JumpInstruction obj)
	{
	}


	public void exitJumpInstruction (JumpInstruction obj)
	{
	}


	public void enterLoadInstruction (LoadInstruction obj)
	{
	}


	public void exitLoadInstruction (LoadInstruction obj)
	{
	}


	public void enterLookupSwitchInstruction (LookupSwitchInstruction obj)
	{
	}


	public void exitLookupSwitchInstruction (LookupSwitchInstruction obj)
	{
	}


	public void enterMathInstruction (MathInstruction obj)
	{
	}


	public void exitMathInstruction (MathInstruction obj)
	{
	}


	public void enterMethodInstruction (MethodInstruction obj)
	{
	}


	public void exitMethodInstruction (MethodInstruction obj)
	{
	}


	public void enterMultiANewArrayInstruction (MultiANewArrayInstruction obj)
	{
	}


	public void exitMultiANewArrayInstruction (MultiANewArrayInstruction obj)
	{
	}


	public void enterNewArrayInstruction (NewArrayInstruction obj)
	{
	}


	public void exitNewArrayInstruction (NewArrayInstruction obj)
	{
	}


	public void enterPutFieldInstruction (PutFieldInstruction obj)
	{
	}


	public void exitPutFieldInstruction (PutFieldInstruction obj)
	{
	}


	public void enterRetInstruction (RetInstruction obj)
	{
	}


	public void exitRetInstruction (RetInstruction obj)
	{
	}


	public void enterReturnInstruction (ReturnInstruction obj)
	{
	}


	public void exitReturnInstruction (ReturnInstruction obj)
	{
	}


	public void enterStackInstruction (StackInstruction obj)
	{
	}


	public void exitStackInstruction (StackInstruction obj)
	{
	}


	public void enterStoreInstruction (StoreInstruction obj)
	{
	}


	public void exitStoreInstruction (StoreInstruction obj)
	{
	}


	public void enterTableSwitchInstruction (TableSwitchInstruction obj)
	{
	}


	public void exitTableSwitchInstruction (TableSwitchInstruction obj)
	{
	}


	public void enterWideInstruction (WideInstruction obj)
	{
	}


	public void exitWideInstruction (WideInstruction obj)
	{
	}


	public void enterMonitorEnterInstruction (MonitorEnterInstruction obj)
	{
	}


	public void exitMonitorEnterInstruction (MonitorEnterInstruction obj)
	{
	}


	public void enterMonitorExitInstruction (MonitorExitInstruction obj)
	{
	}


	public void exitMonitorExitInstruction (MonitorExitInstruction obj)
	{
	}


	public void enterCmpInstruction (CmpInstruction obj)
	{
	}


	public void exitCmpInstruction (CmpInstruction obj)
	{
	}


	public void enterConstantPool (ConstantPool obj)
	{
	}


	public void exitConstantPool (ConstantPool obj)
	{
	}


	public void enterEntry (Entry obj)
	{
	}


	public void exitEntry (Entry obj)
	{
	}


	public void enterClassEntry (ClassEntry obj)
	{
	}


	public void exitClassEntry (ClassEntry obj)
	{
	}


	public void enterDoubleEntry (DoubleEntry obj)
	{
	}


	public void exitDoubleEntry (DoubleEntry obj)
	{
	}


	public void enterFieldEntry (FieldEntry obj)
	{
	}


	public void exitFieldEntry (FieldEntry obj)
	{
	}


	public void enterFloatEntry (FloatEntry obj)
	{
	}


	public void exitFloatEntry (FloatEntry obj)
	{
	}


	public void enterIntEntry (IntEntry obj)
	{
	}


	public void exitIntEntry (IntEntry obj)
	{
	}


	public void enterInterfaceMethodEntry (InterfaceMethodEntry obj)
	{
	}


	public void exitInterfaceMethodEntry (InterfaceMethodEntry obj)
	{
	}


	public void enterLongEntry (LongEntry obj)
	{
	}


	public void exitLongEntry (LongEntry obj)
	{
	}


	public void enterMethodEntry (MethodEntry obj)
	{
	}


	public void exitMethodEntry (MethodEntry obj)
	{
	}


	public void enterNameAndTypeEntry (NameAndTypeEntry obj)
	{
	}


	public void exitNameAndTypeEntry (NameAndTypeEntry obj)
	{
	}


	public void enterPlaceHolderEntry (PlaceHolderEntry obj)
	{
	}


	public void exitPlaceHolderEntry (PlaceHolderEntry obj)
	{
	}


	public void enterStringEntry (StringEntry obj)
	{
	}


	public void exitStringEntry (StringEntry obj)
	{
	}


	public void enterUTF8Entry (UTF8Entry obj)
	{
	}


	public void exitUTF8Entry (UTF8Entry obj)
	{
	}
}
