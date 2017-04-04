package com.newair.studioplugin.actions;

import java.util.Iterator;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IResource;
import org.eclipse.jface.util.OpenStrategy;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.ISelectionProvider;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.ui.IEditorDescriptor;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.actions.OpenFileAction;
import org.eclipse.ui.ide.IDE;
import org.eclipse.ui.internal.dialogs.DialogUtil;
import org.eclipse.ui.internal.ide.IDEWorkbenchMessages;
import org.eclipse.ui.part.FileEditorInput;

import com.newair.studioplugin.StudioConst;
import com.newair.studioplugin.StudioActivator;
import com.newair.studioplugin.StudioUtil;
import com.newair.studioplugin.editors.MybatisEditor;

public class OpenMapFileAction extends OpenFileAction {

	private IWorkbenchPage workbenchPage;
	private ISelectionProvider provider;
	private IEditorDescriptor editorDescriptor;

	public OpenMapFileAction(IWorkbenchPage page, ISelectionProvider selectionProvider) {
		super(page);
		this.workbenchPage = page;
		this.provider = selectionProvider;
		setText("打开映射文件");
		setDescription("打开映射文件");
		setImageDescriptor(StudioActivator.imageDescriptorFromPlugin(StudioActivator.PLUGIN_ID, StudioConst.ICON_OPEN_MAP_FILE));
	}

	@Override
	public boolean isEnabled() {
		ISelection selection = provider.getSelection();
		if (!selection.isEmpty()) {
			Object[] list = ((StructuredSelection)selection).toArray();
			for (int i = 0; i < list.length; i++) {
				if (StudioUtil.isMapperFile(list[i])) {
					return true;
				}
			}
		}
		return false;
	}

	@Override
	public void run() {
		ISelection selection = provider.getSelection();
		Iterator<?> itr = ((StructuredSelection)selection).iterator(); // getSelectedResources().iterator();
		while (itr.hasNext()) {
			IResource resource = (IResource)itr.next();
			if (resource instanceof IFile) {
				openMapFile((IFile)resource);
			}
		}
	}

	private void openMapFile(IFile file) {
		try {
			boolean activate = OpenStrategy.activateOnOpen();
			if (StudioUtil.isMapperFile(file)) {
				IEditorPart editor = IDE.openEditor(workbenchPage, file, StudioConst.MYBATIS_EDIT, activate);
				if (editor instanceof MybatisEditor) {
					//
				}
			} else if (editorDescriptor == null) {
				IDE.openEditor(workbenchPage, file, activate);
			} else {
				workbenchPage.openEditor(new FileEditorInput(file), editorDescriptor.getId(), activate);
			}
		} catch (PartInitException e) {
			DialogUtil.openError(workbenchPage.getWorkbenchWindow().getShell(), IDEWorkbenchMessages.OpenFileAction_openFileShellTitle, e
					.getMessage(), e);
		}
	}
}
