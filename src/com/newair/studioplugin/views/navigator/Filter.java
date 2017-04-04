package com.newair.studioplugin.views.navigator;

import org.eclipse.core.resources.IResource;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.viewers.ViewerFilter;

public class Filter extends ViewerFilter {

	@Override
	public boolean select(Viewer viewer, Object parentElement, Object element) {
		//过滤掉以.开头的资源
		if (element instanceof IResource) {
			IResource res = (IResource)element;
			return !res.getName().startsWith(".");
		}
		return true;
	}
}
