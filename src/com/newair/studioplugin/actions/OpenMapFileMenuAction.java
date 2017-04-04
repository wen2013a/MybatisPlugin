package com.newair.studioplugin.actions;

import org.eclipse.core.filesystem.EFS;
import org.eclipse.core.filesystem.IFileInfo;
import org.eclipse.core.filesystem.IFileStore;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.Path;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.osgi.util.NLS;
import org.eclipse.swt.widgets.FileDialog;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.IWorkbenchWindowActionDelegate;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.ide.IDE;
import org.eclipse.ui.internal.ide.IDEWorkbenchMessages;
import org.eclipse.ui.internal.ide.IDEWorkbenchPlugin;

import com.newair.studioplugin.StudioConst;
import com.newair.studioplugin.editors.MybatisEditorInput;

/**
 * Our sample action implements workbench action delegate. The action proxy will
 * be created by the workbench and shown in the UI. When the user tries to use
 * the action, this delegate will be created and execution will be delegated to
 * it.
 * 
 * @see IWorkbenchWindowActionDelegate
 */
public class OpenMapFileMenuAction implements IWorkbenchWindowActionDelegate {
	private IWorkbenchWindow window;
	private String filterPath;

	protected void openEditor(IEditorInput input, String editorId) {
		IWorkbenchPage page = window.getActivePage();
		try {
			page.openEditor(input, editorId);
		} catch (PartInitException e) {
			System.out.println(e);
		}
	}

	/**
	 * The constructor.
	 */
	public OpenMapFileMenuAction() {
		//
	}

	/**
	 * The action has been activated. The argument of the method represents the
	 * 'real' action sitting in the workbench UI.
	 * 
	 * @see IWorkbenchWindowActionDelegate#run
	 */
	public void run(IAction action) {
		openMapping();
		// MessageDialog.openInformation(window.getShell(), "MybatisPlugin",
		// "Hello, Eclipse world");
		// ResourcesPlugin.getWorkspace().getRoot().getp
		// MybatisEditorInput fileInput = new MybatisEditorInput("Mybatis编辑器",
		// null, null);
		// try {
		// IWorkbenchPage page = window.getActivePage();
		// IDE.openEditor(page, fileInput, MyBatisConst.MYBATIS_EDIT);
		// } catch (PartInitException e) {
		// e.printStackTrace();
		// }
		// openEditor(new MybatisEditorInput("Mybatis编辑器"),
		// MyBatisConst.MYBATIS_EDIT);

		/*
		 * try { IWorkbenchPage page =
		 * PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage();
		 * IProject project =
		 * ResourcesPlugin.getWorkspace().getRoot().getProject("TestProject");
		 * IFile java_file = project.getFile(new Path("/java_file.txt"));
		 * 
		 * //打开方式一：Eclipse默认计算对应的editor id，会用default text editor打开
		 * IDE.openEditor(page, java_file);
		 * 
		 * // 打开方式二：指定java源码编辑器打开，会用java源码编辑器打开 IDE.openEditor(page, java_file,
		 * "org.eclipse.jdt.ui.CompilationUnitEditor");
		 * 
		 * //打开方式三：设定editor id属性，该文件以后默认都用此editor id打开
		 * java_file.setPersistentProperty(IDE.EDITOR_KEY,
		 * "org.eclipse.jdt.ui.CompilationUnitEditor"); IDE.openEditor(page,
		 * java_file); } catch (CoreException e) { IStatus status = new
		 * Status(IStatus.ERROR, "myplugin", 102, "打开工作区内文件出错", e); }
		 */
	}

	@SuppressWarnings("restriction")
	private void openMapping() {
		FileDialog dialog = new FileDialog(window.getShell(), 4098);
		dialog.setText(IDEWorkbenchMessages.OpenLocalFileAction_title);
		dialog.setFilterPath(filterPath);
		dialog.open();
		String names[] = dialog.getFileNames();
		if (names != null) {
			filterPath = dialog.getFilterPath();
			int numberOfFilesNotFound = 0;
			StringBuffer notFound = new StringBuffer();
			for (int i = 0; i < names.length; i++) {
				IFileStore fileStore = EFS.getLocalFileSystem().getStore(new Path(filterPath));
				fileStore = fileStore.getChild(names[i]);
				IFileInfo fetchInfo = fileStore.fetchInfo();
				if (!fetchInfo.isDirectory() && fetchInfo.exists()) {
					IWorkbenchPage page = window.getActivePage();
					try {
						Path path = new Path(filterPath);
						IFile file = ResourcesPlugin.getWorkspace().getRoot().getFile(Path.fromOSString(fileStore.toString()));
						MybatisEditorInput fileInput = new MybatisEditorInput("Mybatis编辑器", path, file);
						IDE.openEditor(page, fileInput, StudioConst.MYBATIS_EDIT);
					} catch (PartInitException e) {
						String msg = NLS.bind(IDEWorkbenchMessages.OpenLocalFileAction_message_errorOnOpen, fileStore.getName());
						IDEWorkbenchPlugin.log(msg, e.getStatus());
						MessageDialog.open(1, window.getShell(), IDEWorkbenchMessages.OpenLocalFileAction_title, msg, 268435456);
					}
				} else {
					if (++numberOfFilesNotFound > 1)
						notFound.append('\n');
					notFound.append(fileStore.getName());
				}
			}
			if (numberOfFilesNotFound > 0) {
				String msgFmt = numberOfFilesNotFound != 1 ? IDEWorkbenchMessages.OpenLocalFileAction_message_filesNotFound : IDEWorkbenchMessages.OpenLocalFileAction_message_fileNotFound;
				String msg = NLS.bind(msgFmt, notFound.toString());
				MessageDialog.open(1, window.getShell(), IDEWorkbenchMessages.OpenLocalFileAction_title, msg, 268435456);
			}
		}
	}

	private void myopen() {
		FileDialog dialog = new FileDialog(window.getShell(), 4098);
		dialog.setText(IDEWorkbenchMessages.OpenLocalFileAction_title);
		dialog.setFilterPath(filterPath);
		dialog.open();
		String names[] = dialog.getFileNames();
		if (names != null) {
			filterPath = dialog.getFilterPath();
			int numberOfFilesNotFound = 0;
			StringBuffer notFound = new StringBuffer();
			for (int i = 0; i < names.length; i++) {
				IFileStore fileStore = EFS.getLocalFileSystem().getStore(new Path(filterPath));
				fileStore = fileStore.getChild(names[i]);
				IFileInfo fetchInfo = fileStore.fetchInfo();
				if (!fetchInfo.isDirectory() && fetchInfo.exists()) {
					IWorkbenchPage page = window.getActivePage();
					try {
						IDE.openEditorOnFileStore(page, fileStore);
					} catch (PartInitException e) {
						String msg = NLS.bind(IDEWorkbenchMessages.OpenLocalFileAction_message_errorOnOpen, fileStore.getName());
						IDEWorkbenchPlugin.log(msg, e.getStatus());
						MessageDialog.open(1, window.getShell(), IDEWorkbenchMessages.OpenLocalFileAction_title, msg, 268435456);
					}
				} else {
					if (++numberOfFilesNotFound > 1)
						notFound.append('\n');
					notFound.append(fileStore.getName());
				}
			}
			if (numberOfFilesNotFound > 0) {
				String msgFmt = numberOfFilesNotFound != 1 ? IDEWorkbenchMessages.OpenLocalFileAction_message_filesNotFound : IDEWorkbenchMessages.OpenLocalFileAction_message_fileNotFound;
				String msg = NLS.bind(msgFmt, notFound.toString());
				MessageDialog.open(1, window.getShell(), IDEWorkbenchMessages.OpenLocalFileAction_title, msg, 268435456);
			}
		}
	}

	/**
	 * Selection in the workbench has been changed. We can change the state of
	 * the 'real' action here if we want, but this can only happen after the
	 * delegate has been created.
	 * 
	 * @see IWorkbenchWindowActionDelegate#selectionChanged
	 */
	public void selectionChanged(IAction action, ISelection selection) {

	}

	/**
	 * We can use this method to dispose of any system resources we previously
	 * allocated.
	 * 
	 * @see IWorkbenchWindowActionDelegate#dispose
	 */
	public void dispose() {
		window = null;
		filterPath = null;
	}

	/**
	 * We will cache window object in order to be able to provide parent shell
	 * for the message dialog.
	 * 
	 * @see IWorkbenchWindowActionDelegate#init
	 */
	public void init(IWorkbenchWindow window) {
		this.window = window;
		this.filterPath = System.getProperty("user.home");
	}
}