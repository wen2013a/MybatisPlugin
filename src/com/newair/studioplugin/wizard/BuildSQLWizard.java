package com.newair.studioplugin.wizard;

import org.dom4j.Element;
import org.eclipse.core.resources.IFile;
import org.eclipse.jface.wizard.IWizardPage;
import org.eclipse.jface.wizard.Wizard;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.MessageBox;

import com.newair.studioplugin.StudioUtil;
import com.newair.studioplugin.editors.XMLMapperDocument;

/**
 * 新增或修改insert,update,delete语句向导
 */
public class BuildSQLWizard extends Wizard {
	
	private BuildSQLPage sqlPage;
	private XMLMapperDocument xmldoc;
	private Element sqlelement;
	private IBuildSQL buildSQL;
	private boolean newFlag;
	private IFile mapperFile;

	public BuildSQLWizard(XMLMapperDocument xmldoc, Element sqlelement, boolean newFlag, IFile mapperFile, IBuildSQL buildSQL) {
		setWindowTitle("新建SQL语句向导");
		this.xmldoc = xmldoc;
		this.sqlelement = sqlelement;
		this.buildSQL = buildSQL;
		this.newFlag = newFlag;
		this.mapperFile = mapperFile;
	}

	@Override
	public void addPages() {
		sqlPage = new BuildSQLPage();
		addPage(sqlPage);
	}	
	
	@Override
	public boolean needsPreviousAndNextButtons() {
		return true;
	}
	
	@Override
	public boolean canFinish() {
		IWizardPage[] pages = getPages();
		boolean flag = true;
		for (int i = 0; i < pages.length; i++) {
			if (!pages[i].isPageComplete()) {
				flag = false;
				break;
			}
		}
		return flag;
	}
	
	@Override
	public boolean performFinish() {
		String id = sqlPage.getMethodName();
		String parameterType = sqlPage.getInputType();
		String sql = sqlPage.getSql();
		try {
			StudioUtil.setElementContent(sqlelement, StudioUtil.delBlankLine(sql));
			StudioUtil.updateAttribute(sqlelement, "id", id);
			StudioUtil.updateAttribute(sqlelement, "parameterType", parameterType);
			xmldoc.parseData();
			buildSQL.successBuild();
			if (sqlPage.getDbConn() != null){
				MessageBox messageBox = new MessageBox(getShell(), SWT.ICON_QUESTION | SWT.YES | SWT.NO);
				messageBox.setMessage("是否提交事务？");
				int rc = messageBox.open();
				if (rc == SWT.YES) {
					sqlPage.getDbConn().commit();
				}
				sqlPage.getDbConn().close();
			}
			return true;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return false;
	}

	public XMLMapperDocument getXMLDoc() {
		return xmldoc;
	}

	public Element getSqlElement() {
		return sqlelement;
	}
	
	public boolean isNewSql() {
		return newFlag;
	}
	
	/**
	 * 获取Mapper文件
	 */
	public IFile getMapperFile() {
		return mapperFile;
	}
	
}
