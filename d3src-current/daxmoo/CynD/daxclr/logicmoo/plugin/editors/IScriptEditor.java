package daxclr.logicmoo.plugin.editors;

import org.eclipse.ui.editors.text.TextEditor;

public class IScriptEditor extends TextEditor {

	private ColorManager colorManager;

	public IScriptEditor() {
		super();
		colorManager = new ColorManager();
		setSourceViewerConfiguration(new XMLConfiguration(colorManager));
		setDocumentProvider(new XMLDocumentProvider());
	}
	public void dispose() {
		colorManager.dispose();
		super.dispose();
	}

}
