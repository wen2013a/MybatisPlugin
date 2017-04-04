package com.newair.studioplugin.actions;

import org.eclipse.core.resources.IFolder;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.jface.wizard.WizardDialog;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.IObjectActionDelegate;
import org.eclipse.ui.IWorkbenchPart;

import com.newair.studioplugin.StudioUtil;
import com.newair.studioplugin.wizard.CreateTableMapperWizard;

/**
 * 新建Mapper映射文件向导
 */
public class CreateTableMapperFileAction implements IObjectActionDelegate {
	
	private Shell shell;
	private Object selectionNode;
	
	public CreateTableMapperFileAction() {
		super();
	}

	public void setActivePart(IAction action, IWorkbenchPart targetPart) {
		shell = targetPart.getSite().getShell();
	}

	public void selectionChanged(IAction action, ISelection selection) {
		selectionNode = null;
		if (!selection.isEmpty()) {
			Object[] list = ((StructuredSelection)selection).toArray();
			for (int i = 0; i < list.length; i++) {
				if (StudioUtil.isMapperFolder(list[i])) {
					selectionNode = list[i];
					action.setEnabled(true);
					return;
				}
			}
		}
		action.setEnabled(false);
	}

	public void run(IAction action) {
		//Display.getCurrent().getActiveShell()
		WizardDialog dlg = new WizardDialog(shell, new CreateTableMapperWizard((IFolder)selectionNode));
		dlg.open();
	}
}
