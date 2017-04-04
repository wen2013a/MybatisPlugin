package com.newair.studioplugin.editors;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IEditorSite;
import org.eclipse.ui.PartInitException;

import com.newair.studioplugin.editors.xmleditor.XMLDocumentProvider;
import com.newair.studioplugin.editors.xmleditor.XMLEditor;

public class XmlSourceEditPage extends XMLEditor {
	public XmlSourceEditPage() {
	}

	public XmlSourceEditPage(XMLDocumentProvider provider) {
		super(provider);
	}
	
	@Override
	public void doSave(IProgressMonitor progressMonitor) {
		super.doSave(progressMonitor);
	}

	@Override
	public void doSaveAs() {
		super.doSaveAs();
	}

	@Override
	public void init(IEditorSite site, IEditorInput input) throws PartInitException {
		super.init(site, input);
	}

	@Override
	public boolean isDirty() {
		return super.isDirty();
	}

	@Override
	public boolean isSaveAsAllowed() {
		return super.isSaveAsAllowed();
	}

	@Override
	public void createPartControl(Composite parent) {
		super.createPartControl(parent);
	}

	@Override
	public void setFocus() {
		super.setFocus();
	}

}
