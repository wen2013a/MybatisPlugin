package com.newair.studioplugin.editors;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IEditorSite;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.forms.editor.FormEditor;
import org.eclipse.ui.texteditor.IElementStateListener;

import com.newair.studioplugin.StudioConst;
import com.newair.studioplugin.StudioUtil;
import com.newair.studioplugin.editors.xmleditor.XMLDocumentProvider;

public class MybatisEditor extends FormEditor {

	private SqlMapperEditPage sqlEditPage;
	private XmlSourceEditPage xmlSourceEditPage;
	private XMLDocumentProvider provider;

	@Override
	protected void addPages() {
		try {
			provider = new XMLDocumentProvider();
			provider.addElementStateListener(new DocProviderElementStateListener());
			// 增加可视页
			int index = 0;
			sqlEditPage = new SqlMapperEditPage(provider);
			addPage(index, sqlEditPage, getEditorInput());
			setPageImage(index, StudioUtil.getImage(StudioConst.ICON_VISUAL_EDITOR));
			setPageText(index, "SQL映射");
			// 增加源文件编辑页
			index++;
			xmlSourceEditPage = new XmlSourceEditPage(provider);
			addPage(index, xmlSourceEditPage, getEditorInput());
			setPageImage(index, StudioUtil.getImage(StudioConst.ICON_SOURCE_EDITOR));
			setPageText(index, "源文件");
		} catch (PartInitException e) {
			e.printStackTrace();
		}
	}

	@Override
	public void init(IEditorSite site, IEditorInput input) throws PartInitException {
		setPartName(input.getName());
		super.init(site, input);
	}

	@Override
	public void doSave(IProgressMonitor progressMonitor) {
		int idx = -1;
		for (int index = 0; index < pages.size(); index++) {
			Object page = pages.get(index);
			if (((page instanceof IEditorPart)) && (((IEditorPart)page).isDirty())) {
				((IEditorPart)page).doSave(progressMonitor);
				idx = index;
			}
		}
		// 同步到SqlMapperEditPage
		if (idx == 1) {
			SqlMapperEditPage page = (SqlMapperEditPage)pages.get(0);
			page.sysncEdit();
		}
		editorDirtyStateChanged();
	}

	@Override
	public void doSaveAs() {
	}

	@Override
	public boolean isSaveAsAllowed() {
		return false;
	}

	@Override
	protected void pageChange(int newPageIndex) {
		super.pageChange(newPageIndex);
	}

	private class DocProviderElementStateListener implements IElementStateListener {
		public void elementContentAboutToBeReplaced(Object element) {
		}

		public void elementContentReplaced(Object element) {
			sqlEditPage.sysncEdit();
		}

		public void elementDeleted(Object element) {
			close(false);
		}

		public void elementDirtyStateChanged(Object element, boolean isDirty) {
		}

		public void elementMoved(Object originalElement, Object movedElement) {
			close(false);
		}
	}
}
