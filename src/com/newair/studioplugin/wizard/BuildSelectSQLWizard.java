package com.newair.studioplugin.wizard;

import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.dom4j.Attribute;
import org.dom4j.DocumentException;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;
import org.eclipse.core.resources.IFile;
import org.eclipse.jface.wizard.IWizardPage;

import com.newair.studioplugin.StudioConst;
import com.newair.studioplugin.StudioUtil;
import com.newair.studioplugin.db.Field;
import com.newair.studioplugin.editors.XMLMapperDocument;

public class BuildSelectSQLWizard extends DynamicPageWizard {

	private BuildSelectSQLPage sqlPage;
	private BuildSelectSQLResultMapPage sqlResultMapPage;
	private XMLMapperDocument xmldoc;
	private Element sqlelement;
	private IBuildSQL buildSQL;
	private boolean newFlag;
	private IFile mapperFile;
	
	public BuildSelectSQLWizard(XMLMapperDocument xmldoc, Element sqlelement, boolean newFlag, IFile mapperFile, IBuildSQL buildSQL) {
		super();
		setWindowTitle("新建查询向导");
		this.xmldoc = xmldoc;
		this.sqlelement = sqlelement;
		this.buildSQL = buildSQL;
		this.newFlag = newFlag;
		this.mapperFile = mapperFile;
	}
	
	@Override
	public void addPages() {
		sqlPage = new BuildSelectSQLPage();
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
		String resultMapName = sqlPage.getResultMapName();
		String sql = sqlPage.getSql();
		try {
			StudioUtil.setElementContent(sqlelement, StudioUtil.delBlankLine(sql));
		} catch (DocumentException e) {
			e.printStackTrace();
		}
		StudioUtil.updateAttribute(sqlelement, "id", id);
		if (sqlPage.isResultType()) { //是结果对象
			String resultType = sqlPage.getResutlType();
			StudioUtil.updateAttribute(sqlelement, "parameterType", parameterType);
			StudioUtil.updateAttribute(sqlelement, "resultType", resultType);
			Attribute attr = sqlelement.attribute("resultMap");
			if (attr != null)
				sqlelement.remove(attr);
			xmldoc.parseData();
			buildSQL.successBuild();
			return true;
		}
		//是结果映射
		if (getSQLResultMapPage() == null) {
			return false;
		}
		StudioUtil.updateAttribute(sqlelement, "resultMap", resultMapName);
		BuildSelectSQLResultMapPage sqlResultPage = getSQLResultMapPage();
		List<Field> fieldList = sqlResultPage.getResultFields();
		String resultMapType = sqlResultPage.getResultMapType();
		if (sqlResultPage.isNewMap()) { //新增
			Element resultmap = DocumentHelper.createElement("resultMap");
			resultmap.addAttribute("id", resultMapName);
			resultmap.addAttribute("type", resultMapType);
			List<Element> ls = xmldoc.getRootElement().elements();
			ls.add(resultmap);
			int level = StudioUtil.getNodeLevel(resultmap);
			resultmap.addText("\n" + StringUtils.repeat(StudioConst.XML_PREFIX_STR, level));
			for (int i = 0; i < fieldList.size(); i++) {
				Field field = fieldList.get(i);
				Element fieldElement;
				if (field.isPrimaryKey())
					fieldElement = DocumentHelper.createElement("id");
				else
					fieldElement = DocumentHelper.createElement("result");
				StudioUtil.updateAttribute(fieldElement, "column", field.getColumnName());
				StudioUtil.updateAttribute(fieldElement, "property", field.getProperty());
				StudioUtil.updateAttribute(fieldElement, "jdbcType", field.getJdbcType());
				resultmap.add(fieldElement);
				resultmap.addText("\n" + StringUtils.repeat(StudioConst.XML_PREFIX_STR, level));
			}
			xmldoc.parseData();
			buildSQL.successBuild();
			return true;
		} else { //修改
			for (int i = 0; i < xmldoc.resultMapNodes.size(); i++) {
				Element resultmap = xmldoc.resultMapNodes.get(i);
				String mapid = resultmap.attributeValue("id", "");
				if (mapid.equals(resultMapName)) {
					StudioUtil.updateAttribute(resultmap, "type", resultMapType);
					resultmap.clearContent();
					int level = StudioUtil.getNodeLevel(resultmap);
					resultmap.addText("\n" + StringUtils.repeat(StudioConst.XML_PREFIX_STR, level));
					for (int j = 0; j < fieldList.size(); j++) {
						Field field = fieldList.get(j);
						Element fieldElement;
						if (field.isPrimaryKey())
							fieldElement = DocumentHelper.createElement("id");
						else
							fieldElement = DocumentHelper.createElement("result");
						StudioUtil.updateAttribute(fieldElement, "column", field.getColumnName());
						StudioUtil.updateAttribute(fieldElement, "property", field.getProperty());
						StudioUtil.updateAttribute(fieldElement, "jdbcType", field.getJdbcType());
						resultmap.add(fieldElement);
						resultmap.addText("\n" + StringUtils.repeat(StudioConst.XML_PREFIX_STR, level));
					}
					xmldoc.parseData();
					buildSQL.successBuild();
					return true;
				}
			}
			return false;
		}
	}
	
	public void removeSQLResultMapPage() {
		if (sqlResultMapPage != null) {
			this.removePage(sqlResultMapPage);
			sqlResultMapPage = null;
		}
	}
	
	public void addSQLResultMapPage() {
		if (sqlResultMapPage == null) {
			sqlResultMapPage = new BuildSelectSQLResultMapPage();
			addPage(sqlResultMapPage);
		}
	}
	
	public BuildSelectSQLResultMapPage getSQLResultMapPage() {
		return sqlResultMapPage;
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
