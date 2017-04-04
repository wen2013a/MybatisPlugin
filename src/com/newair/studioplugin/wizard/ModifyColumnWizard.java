package com.newair.studioplugin.wizard;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.dom4j.Attribute;
import org.dom4j.Document;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;
import org.dom4j.Node;
import org.eclipse.core.resources.IFile;
import org.eclipse.jface.wizard.Wizard;

import com.newair.studioplugin.StudioConst;
import com.newair.studioplugin.StudioUtil;
import com.newair.studioplugin.buildcode.BuildUtil;
import com.newair.studioplugin.db.DbUtil;
import com.newair.studioplugin.db.Field;
import com.newair.studioplugin.editors.XMLMapperDocument;

/**
 * 修改表的列选择向导
 */
public class ModifyColumnWizard extends Wizard {
	
	private SelectColumnWizardPage selectColumnPage;
	private String tablename;
	private XMLMapperDocument xmldoc;
	private IBuildSQL buildSQL;
	
	public ModifyColumnWizard(String tablename, XMLMapperDocument xmldoc, IFile file, Element resutmap, IBuildSQL buildSQL) {
		setWindowTitle("New Wizard");
		this.xmldoc = xmldoc;
		this.tablename = tablename;
		this.buildSQL = buildSQL;
	}

	@Override
	public void addPages() {
		selectColumnPage = new SelectColumnWizardPage();
		addPage(selectColumnPage);
		List<String> cols = new ArrayList<String>();
		for (int i = 0; i < xmldoc.resultMapNodes.size(); i++) {
			Element elem = xmldoc.resultMapNodes.get(i);
			String id = elem.attributeValue("id", "");
			if (id.equals(StudioConst.MAP_ID_BaseResultMap) || id.equals(StudioConst.MAP_ID_ResultMapWithBlob)) {
				List<Element> list = elem.elements();
				for (int j = 0; j < list.size(); j++) {
					String col = list.get(j).attributeValue("column", "");
					cols.add(col);
				}
			}
		}
		String beanclass = (xmldoc.BaseResultMap == null) ? "" : xmldoc.BaseResultMap.attributeValue("type", "");
		
		selectColumnPage.setBeanClass(beanclass);
		selectColumnPage.InitSelectColumn(cols.toArray(new String[] {}));
		selectColumnPage.bindColumnData(tablename);
	}

	@Override
	public boolean performFinish() {
		Field[] fields = selectColumnPage.getFields();
		List<Field> keys = new ArrayList<Field>();
		for (int i = 0; i < fields.length; i++) {
			if (fields[i].isPrimaryKey()) {
				keys.add(fields[i]);
			}
		}
		List<Field> basefieldlist = new ArrayList<Field>();
		List<Field> blobfieldlist = new ArrayList<Field>();
		for (int i = 0; i < fields.length; i++) {
			Field field = fields[i];
			if (DbUtil.isBLOBType(field.getDataType(), field.getColumnSize())) {
				blobfieldlist.add(field);
			} else {
				basefieldlist.add(field);
			}
		}
		Field[] primaryKeys = keys.toArray(new Field[] {});
		Field[] basefields = basefieldlist.toArray(new Field[] {});
		Field[] blobfields = blobfieldlist.toArray(new Field[] {});
		boolean isBlob = blobfields.length > 0;
		
		String keybean = DbUtil.getTableBeanName(tablename) + "Key";
		String beanclass = (xmldoc.BaseResultMap == null) ? "" : xmldoc.BaseResultMap.attributeValue("type", "");
		String blobbeanclass = beanclass + "WithBlob";
		String adapterclass = (xmldoc.selectByAdapter == null) ? "" : xmldoc.selectByAdapter.attributeValue("parameterType", "");
		String keybeanclass = beanclass.substring(0, beanclass.lastIndexOf(".")) + keybean;
		try {
			String s_UpdateByPrimaryKeyWithBlob = null;
			String s_BaseResultMap = BuildUtil.buildBaseResultMap(basefields, beanclass);
			String s_BlobResultMap = BuildUtil.buildResultMapWithBlob(blobfields, blobbeanclass);
			String s_BaseColumnList = BuildUtil.buildBaseColumnList(basefields);
			String s_BlobColumnList = BuildUtil.buildBlobColumnList(blobfields);
			String s_SelectByAdapterWithBlob = BuildUtil.buildSelectByAdapterWithBlob(tablename, adapterclass);
			String s_SelectByPrimaryKey = BuildUtil.buildSelectByPrimaryKey(tablename, primaryKeys, keybeanclass, isBlob);
			String s_DeleteByPrimaryKey = BuildUtil.buildDeleteByPrimaryKey(tablename, primaryKeys, keybeanclass);
			String s_Inset = BuildUtil.buildInsert(tablename, (isBlob ? blobbeanclass : beanclass), fields);
			String s_InsertSelective = BuildUtil.buildInsertSelective(tablename, (isBlob ? blobbeanclass : beanclass), fields);
			String s_UpdateByAdapter = BuildUtil.buildUpdateByAdapter(tablename, basefields);
			String s_UpdateByAdapterWithBlob = BuildUtil.buildUpdateByAdapterWithBlob(tablename, fields);
			String s_UpdateByAdapterSelective = BuildUtil.buildUpdateByAdapterSelective(tablename, fields);
			String s_UpdateByPrimaryKey = BuildUtil.buildUpdateByPrimaryKey(tablename, basefields, primaryKeys, beanclass);
			if (isBlob) {
				s_UpdateByPrimaryKeyWithBlob = BuildUtil.buildUpdateByPrimaryKeyWithBlob(tablename, fields, primaryKeys, blobbeanclass);
			}
			String s_UpdateByPrimaryKeySelective = BuildUtil.buildUpdateByPrimaryKeySelective(tablename, fields, primaryKeys, (isBlob ? blobbeanclass : beanclass));
			
			//更新 BaseResultMap
			if (xmldoc.BaseResultMap != null) {
				updateNode(xmldoc.BaseResultMap, s_BaseResultMap);
			}
			//更新 ResultMapWithBlob
			if (!isBlob) {
				if (xmldoc.ResultMapWithBlob != null) {
					xmldoc.getRootElement().remove(xmldoc.ResultMapWithBlob);
					xmldoc.ResultMapWithBlob = null;
				}
			} else {
				if (xmldoc.ResultMapWithBlob != null)
					updateNode(xmldoc.ResultMapWithBlob, s_BlobResultMap);
				else
					xmldoc.ResultMapWithBlob = createNode(xmldoc.getRootElement(), s_BlobResultMap);
			}
			//更新 BaseColumnList
			if (xmldoc.BaseColumnList != null) {
				updateNode(xmldoc.BaseColumnList, s_BaseColumnList);
			}
			//更新 BlobColumnList
			if (!isBlob) {
				if (xmldoc.BlobColumnList != null) {
					xmldoc.getRootElement().remove(xmldoc.BlobColumnList);
					xmldoc.BlobColumnList = null;
				}
			} else {
				if (xmldoc.BlobColumnList != null)
					updateNode(xmldoc.BlobColumnList, s_BlobColumnList);
				else
					xmldoc.BlobColumnList = createNode(xmldoc.getRootElement(), s_BlobColumnList);
			}
			//更新 selectByAdapterWithBlob
			boolean isSelect = xmldoc.selectByAdapter != null;
			if (!isBlob || !isSelect) {
				if (xmldoc.selectByAdapterWithBlob != null) {
					xmldoc.getRootElement().remove(xmldoc.selectByAdapterWithBlob);
					xmldoc.selectByAdapterWithBlob = null;
				}
			} else {
				if (xmldoc.selectByAdapterWithBlob != null)
					updateNode(xmldoc.selectByAdapterWithBlob, s_SelectByAdapterWithBlob);
				else if (xmldoc.selectByAdapter != null) 
					xmldoc.selectByAdapterWithBlob = createNode(xmldoc.getRootElement(), s_SelectByAdapterWithBlob);
			}
			//更新 selectByPrimaryKey
			if (xmldoc.selectByPrimaryKey != null) {
				updateNode(xmldoc.selectByPrimaryKey, s_SelectByPrimaryKey);
			}
			//更新 deleteByPrimaryKey
			if (xmldoc.deleteByPrimaryKey != null) {
				updateNode(xmldoc.deleteByPrimaryKey, s_DeleteByPrimaryKey);
			}
			//更新 insert
			if (xmldoc.insert != null) {
				updateNode(xmldoc.insert, s_Inset);
			}
			//更新 insertSelective
			if (xmldoc.insertSelective != null) {
				updateNode(xmldoc.insertSelective, s_InsertSelective);
			}
			//更新 updateByAdapter
			boolean isUpdate = xmldoc.updateByAdapter != null;
			if (xmldoc.updateByAdapter != null) {
				updateNode(xmldoc.updateByAdapter, s_UpdateByAdapter);
			}
			//更新 updateByAdapterWithBlob
			if (!isBlob || !isUpdate) {
				if (xmldoc.updateByAdapterWithBlob != null) {
					xmldoc.getRootElement().remove(xmldoc.updateByAdapterWithBlob);
					xmldoc.updateByAdapterWithBlob = null;
				}
			} else {
				if (xmldoc.updateByAdapterWithBlob != null)
					updateNode(xmldoc.updateByAdapterWithBlob, s_UpdateByAdapterWithBlob);
				else
					xmldoc.updateByAdapterWithBlob = createNode(xmldoc.getRootElement(), s_UpdateByAdapterWithBlob);
			}
			//更新 updateByAdapterSelective
			if (xmldoc.updateByAdapterSelective != null) {
				updateNode(xmldoc.updateByAdapterSelective, s_UpdateByAdapterSelective);
			}
			//更新 updateByPrimaryKey
			if (xmldoc.updateByPrimaryKey != null) {
				updateNode(xmldoc.updateByPrimaryKey, s_UpdateByPrimaryKey);
			}
			//更新 updateByPrimaryKeyWithBlob
			if (!isBlob || !isUpdate) {
				if (xmldoc.updateByPrimaryKeyWithBlob != null) {
					xmldoc.getRootElement().remove(xmldoc.updateByPrimaryKeyWithBlob);
					xmldoc.updateByPrimaryKeyWithBlob = null;
				}
			} else {
				if (xmldoc.updateByPrimaryKeyWithBlob != null)
					updateNode(xmldoc.updateByPrimaryKeyWithBlob, s_UpdateByPrimaryKeyWithBlob);
				else
					xmldoc.updateByPrimaryKeyWithBlob = createNode(xmldoc.getRootElement(), s_UpdateByPrimaryKeyWithBlob);
			}
			//更新 updateByPrimaryKeySelective
			if (xmldoc.updateByPrimaryKeySelective != null) {
				updateNode(xmldoc.updateByPrimaryKeySelective, s_UpdateByPrimaryKeySelective);
			}
			buildSQL.successBuild();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return true;
	}
	
	private void updateNode(Element oldelement, String nodexml) throws Exception {
		int level = StudioUtil.getNodeLevel(oldelement);
		String prefix = StringUtils.repeat(StudioConst.XML_PREFIX_STR, level);
		StringBuffer sb = new StringBuffer();
		sb.append("<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n");
		sb.append(nodexml);
		Document doc = StudioUtil.parseXmlText(sb.toString());
		Element newelement = (Element)doc.node(0);
		
		oldelement.clearContent();
		oldelement.attributes().clear();
		oldelement.addText("\n" + prefix);
		while (newelement.nodeCount() > 0) {
			Node tmpNode = newelement.node(0);
			newelement.remove(tmpNode);
			oldelement.add(tmpNode);
		}
		while (newelement.attributeCount() > 0) {
			Attribute attr = newelement.attribute(0);
			newelement.remove(attr);
			oldelement.attributes().add(attr);
		}
	}
	
	private Element createNode(Element parent, String nodexml) throws Exception {
		int level = StudioUtil.getNodeLevel(parent) + 1;
		String prefix = StringUtils.repeat(StudioConst.XML_PREFIX_STR, level);
		StringBuffer sb = new StringBuffer();
		sb.append("<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n");
		sb.append(nodexml);
		Document doc = StudioUtil.parseXmlText(sb.toString());
		Element tmpelement = (Element)doc.node(0);
		
		Element newelement = DocumentHelper.createElement(tmpelement.getName());
		newelement.addText("\n" + prefix);
		while (tmpelement.nodeCount() > 0) {
			Node tmpNode = tmpelement.node(0);
			tmpelement.remove(tmpNode);
			newelement.add(tmpNode);
		}
		while (tmpelement.attributeCount() > 0) {
			Attribute attr = tmpelement.attribute(0);
			tmpelement.remove(attr);
			newelement.attributes().add(attr);
		}
		parent.add(newelement);
		return newelement;
	}

}
