package com.newair.studioplugin.views.navigator;

import org.eclipse.core.resources.IFile;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IFileEditorInput;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.navigator.ILinkHelper;
import org.eclipse.ui.part.FileEditorInput;

public class LinkHelper implements ILinkHelper {

	public void activateEditor(IWorkbenchPage page, IStructuredSelection selection) {
		Object obj = selection.getFirstElement();
		if (obj instanceof IFile) {
			FileEditorInput input = new FileEditorInput((IFile)obj);
			IEditorPart editor = page.findEditor(input);
			if (editor != null) {
				page.bringToTop(editor);
			}
		}
	}

	public IStructuredSelection findSelection(IEditorInput anInput) {
		if (anInput instanceof IFileEditorInput) {
			IFile file = ((IFileEditorInput)anInput).getFile();
			StructuredSelection selection = new StructuredSelection(file);
			return selection;
		}
		return null;
	}

}
