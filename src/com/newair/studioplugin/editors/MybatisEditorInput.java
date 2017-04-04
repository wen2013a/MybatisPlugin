package com.newair.studioplugin.editors;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IStorage;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IFileEditorInput;
import org.eclipse.ui.IPathEditorInput;
import org.eclipse.ui.IPersistableElement;
import org.eclipse.ui.ISharedImages;
import org.eclipse.ui.PlatformUI;

public class MybatisEditorInput implements IEditorInput, IFileEditorInput, IPathEditorInput {
	private String name;
	private IFile file;
	private IPath path;

	public MybatisEditorInput(String name, IPath path, IFile file) {
		this.name = name;
		this.file = file;
		this.path = path;
	}

	public MybatisEditorInput() {

	}

	public boolean exists() {
		return (file != null) ? file.exists() : false;
	}

	public ImageDescriptor getImageDescriptor() {
		return PlatformUI.getWorkbench().getSharedImages().getImageDescriptor(ISharedImages.IMG_OBJ_ELEMENT);
	}

	public String getName() {
		return name;
	}

	public IPersistableElement getPersistable() {
		return null;
	}

	public String getToolTipText() {
		return getName();
	}

	public Object getAdapter(Class arg0) {
		return null;
	}

	public IFile getFile() {
		return file;
	}

	public IStorage getStorage() throws CoreException {
		// TODO Auto-generated method stub
		return null;
	}

	public IPath getPath() {
		return path;
	}

}
