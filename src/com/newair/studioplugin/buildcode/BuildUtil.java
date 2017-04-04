package com.newair.studioplugin.buildcode;

import java.io.StringWriter;
import java.io.Writer;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.dom4j.Element;
import org.eclipse.core.resources.IFolder;

import com.newair.studioplugin.ConfigCDATA;
import com.newair.studioplugin.ProjectConfig;
import com.newair.studioplugin.StudioConst;
import com.newair.studioplugin.StudioUtil;
import com.newair.studioplugin.db.DbUtil;
import com.newair.studioplugin.db.Field;
import com.newair.studioplugin.editors.XMLMapperDocument;
import com.newair.studioplugin.template.TemplateFactory;

import freemarker.template.Template;

public class BuildUtil {
	
	private static final Logger log = Logger.getLogger(BuildUtil.class);
			
	/**
	 * 创建一般查询映射
	 */
	public static String buildGeneralQueryMapper(String datasource, String intermapperclass, String daoClass, String namespace) throws Exception {
		ConfigCDATA configCDATA = new ConfigCDATA(datasource, "", "", intermapperclass, daoClass, "");
		String CfgCDATA = buildConfigCDATA(configCDATA);
		Writer output = new StringWriter();
		Template temp = TemplateFactory.getTagTemplate("sql/NewMapper.ftl");
		Map<String, Object> root = new HashMap<String, Object>();
		root.put("Namespace", namespace);
		root.put("ConfigCDATA", CfgCDATA);
		temp.process(root, output);
		return output.toString();
	}
	
	/**
	 * 创建表映射
	 */
	public static String buildTableMapper(Map<String, Object> root) throws Exception {
		Writer output = new StringWriter();
		Template temp = TemplateFactory.getTagTemplate("sql/NewMapper.ftl");
		temp.process(root, output);
		return output.toString();
	}
	
	/**
	 * 创建配置CDATA
	 */
	public static String buildConfigCDATA(ConfigCDATA cfg) throws Exception {
		Writer output = new StringWriter();
		Template temp = TemplateFactory.getTagTemplate("sql/ConfigCDATA.ftl");
		Map<String, Object> root = new HashMap<String, Object>();
		root.put("CFG_CDATA_FLAG", StudioConst.NODE_CDATA_CFG_FLAG);
		root.put("datasource", cfg.getDatasource());
		root.put("tablename", cfg.getTablename());
		root.put("identitycolumn", cfg.getIdentitycolumn());
		root.put("beanclass", cfg.getBeanclass());
		root.put("interclass", cfg.getInterclass());
		root.put("daoclass", cfg.getDaoclass());
		temp.process(root, output);
		return output.toString();
	}

	/**
	 * 创建AdapterUpdateWhereCondition
	 */
	public static String buildAdapterUpdateWhereCondition() throws Exception {
		Writer output = new StringWriter();
		Template temp = TemplateFactory.getTagTemplate("sql/AdapterUpdateWhereCondition.ftl");
		Map<String, Object> root = new HashMap<String, Object>();
		temp.process(root, output);
		return output.toString();
	}
	
	/**
	 * 创建AdapterWhereCondition
	 */
	public static String buildAdapterWhereCondition() throws Exception {
		Writer output = new StringWriter();
		Template temp = TemplateFactory.getTagTemplate("sql/AdapterWhereCondition.ftl");
		Map<String, Object> root = new HashMap<String, Object>();
		temp.process(root, output);
		return output.toString();
	}
	
	/**
	 * 创建BaseColumnList
	 * @throws IOException 
	 * @throws  
	 */
	public static String buildBaseColumnList(Field[] basefields) throws Exception {
		Writer output = new StringWriter();
		Template temp = TemplateFactory.getTagTemplate("sql/ColumnList.ftl");
		Map<String, Object> root = new HashMap<String, Object>();
		root.put("Fields", basefields);
		root.put("SqlName", "BaseColumnList");
		temp.process(root, output);
		return output.toString();
	}
	
	/**
	 * 创建BlobColumnList
	 * @throws IOException 
	 * @throws  
	 */
	public static String buildBlobColumnList(Field[] blobfields) throws Exception {
		Writer output = new StringWriter();
		Template temp = TemplateFactory.getTagTemplate("sql/ColumnList.ftl");
		Map<String, Object> root = new HashMap<String, Object>();
		root.put("Fields", blobfields);
		root.put("SqlName", "BlobColumnList");
		temp.process(root, output);
		return output.toString();
	}

	/**
	 * 创建BaseResultMap
	 * @throws IOException 
	 * @throws  
	 */
	public static String buildBaseResultMap(Field[] basefields, String beanclass) throws Exception {
		Writer output = new StringWriter();
		Template temp = TemplateFactory.getTagTemplate("sql/BaseResultMap.ftl");
		Map<String, Object> root = new HashMap<String, Object>();
		root.put("Fields", basefields);
		root.put("BeanClass", beanclass);
		temp.process(root, output);
		return output.toString();
	}
	
	/**
	 * 创建BaseResultMap
	 * @throws IOException 
	 * @throws  
	 */
	public static String buildResultMapWithBlob(Field[] blobfields, String blobbeanclass) throws Exception {
		Writer output = new StringWriter();
		Template temp = TemplateFactory.getTagTemplate("sql/ResultMapWithBlob.ftl");
		Map<String, Object> root = new HashMap<String, Object>();
		root.put("Fields", blobfields);
		root.put("BeanClass", blobbeanclass);
		temp.process(root, output);
		return output.toString();
	}
	
	/**
	 * 创建countByAdapter
	 */
	public static String buildCountByAdapter(String tablename, String adapter) throws Exception {
		Writer output = new StringWriter();
		Template temp = TemplateFactory.getTagTemplate("sql/countByAdapter.ftl");
		Map<String, Object> root = new HashMap<String, Object>();
		root.put("TableName", tablename);
		root.put("Adapter", adapter);
		temp.process(root, output);
		return output.toString();
	}
	
	/**
	 * 创建deleteByAdapter
	 */
	public static String buildDeleteByAdapter(String tablename, String adapter) throws Exception {
		Writer output = new StringWriter();
		Template temp = TemplateFactory.getTagTemplate("sql/deleteByAdapter.ftl");
		Map<String, Object> root = new HashMap<String, Object>();
		root.put("TableName", tablename);
		root.put("Adapter", adapter);
		temp.process(root, output);
		return output.toString();
	}
	
	/**
	 * 创建deleteByPrimaryKey
	 */
	public static String buildDeleteByPrimaryKey(String tablename, Field[] primaryKeys, String keyBeanClass) throws Exception {
		Writer output = new StringWriter();
		Template temp = TemplateFactory.getTagTemplate("sql/deleteByPrimaryKey.ftl");
		Map<String, Object> root = new HashMap<String, Object>();
		root.put("TableName", tablename);
		root.put("PrimaryKeys", primaryKeys);
		root.put("KeyBeanClass", keyBeanClass);
		if (primaryKeys.length == 1) {
			root.put("JavaClass", primaryKeys[0].getJavaClass());
			root.put("Column", primaryKeys[0].getColumnName());
			root.put("MapperParameter", primaryKeys[0].getParamStr());
		}
		temp.process(root, output);
		return output.toString();
	}
	
	/**
	 * 创建insert
	 */
	public static String buildInsert(String tablename, String beanclass, Field[] fields) throws Exception {
		Writer output = new StringWriter();
		Template temp = TemplateFactory.getTagTemplate("sql/insert.ftl");
		Map<String, Object> root = new HashMap<String, Object>();
		root.put("BeanClass", beanclass);
		root.put("TableName", tablename);
		root.put("Fields", fields);
		temp.process(root, output);
		return output.toString();
	}

	/**
	 * 创建insertSelective
	 */
	public static String buildInsertSelective(String tablename, String beanclass, Field[] fields) throws Exception {
		Writer output = new StringWriter();
		Template temp = TemplateFactory.getTagTemplate("sql/insertSelective.ftl");
		Map<String, Object> root = new HashMap<String, Object>();
		root.put("BeanClass", beanclass);
		root.put("TableName", tablename);
		root.put("Fields", fields);
		temp.process(root, output);
		return output.toString();
	}

	/**
	 * 创建selectByAdapter
	 */
	public static String buildSelectByAdapter(String tablename, String adapter) throws Exception {
		Writer output = new StringWriter();
		Template temp = TemplateFactory.getTagTemplate("sql/selectByAdapter.ftl");
		Map<String, Object> root = new HashMap<String, Object>();
		root.put("TableName", tablename);
		root.put("Adapter", adapter);
		temp.process(root, output);
		return output.toString();
	}

	/**
	 * 创建selectByAdapterWithBlob
	 */
	public static String buildSelectByAdapterWithBlob(String tablename, String adapter) throws Exception {
		Writer output = new StringWriter();
		Template temp = TemplateFactory.getTagTemplate("sql/selectByAdapterWithBlob.ftl");
		Map<String, Object> root = new HashMap<String, Object>();
		root.put("TableName", tablename);
		root.put("Adapter", adapter);
		temp.process(root, output);
		return output.toString();
	}
	
	/**
	 * 创建selectByPrimaryKey
	 */
	public static String buildSelectByPrimaryKey(String tablename, Field[] primaryKeys, String keyBeanClass, boolean isBlob) throws Exception {
		Writer output = new StringWriter();
		Template temp = TemplateFactory.getTagTemplate("sql/selectByPrimaryKey.ftl");
		Map<String, Object> root = new HashMap<String, Object>();
		root.put("TableName", tablename);
		root.put("PrimaryKeys", primaryKeys);
		root.put("KeyBeanClass", keyBeanClass);
		root.put("IsBlob", isBlob);
		if (primaryKeys.length == 1) {
			root.put("JavaClass", primaryKeys[0].getJavaClass());
			root.put("Column", primaryKeys[0].getColumnName());
			root.put("MapperParameter", primaryKeys[0].getParamStr());
		}
		temp.process(root, output);
		return output.toString();
	}
	
	/**
	 * 创建updateByAdapter
	 */
	public static String buildUpdateByAdapter(String tablename, Field[] basefields) throws Exception {
		Writer output = new StringWriter();
		Template temp = TemplateFactory.getTagTemplate("sql/updateByAdapter.ftl");
		Map<String, Object> root = new HashMap<String, Object>();
		root.put("TableName", tablename);
		root.put("UpdateName", "updateByAdapter");
		root.put("Fields", basefields);
		temp.process(root, output);
		return output.toString();
	}
	
	/**
	 * 创建updateByAdapterWithBlob
	 */
	public static String buildUpdateByAdapterWithBlob(String tablename, Field[] fields) throws Exception {
		Writer output = new StringWriter();
		Template temp = TemplateFactory.getTagTemplate("sql/updateByAdapter.ftl");
		Map<String, Object> root = new HashMap<String, Object>();
		root.put("TableName", tablename);
		root.put("UpdateName", "updateByAdapterWithBlob");
		root.put("Fields", fields);
		temp.process(root, output);
		return output.toString();
	}
	
	/**
	 * 创建updateByAdapterSelective
	 */
	public static String buildUpdateByAdapterSelective(String tablename, Field[] fields) throws Exception {
		Writer output = new StringWriter();
		Template temp = TemplateFactory.getTagTemplate("sql/updateByAdapterSelective.ftl");
		Map<String, Object> root = new HashMap<String, Object>();
		root.put("TableName", tablename);
		root.put("Fields", fields);
		temp.process(root, output);
		return output.toString();
	}
	
	/**
	 * 创建updateByPrimaryKey
	 */
	public static String buildUpdateByPrimaryKey(String tablename, Field[] fields, Field[] primaryKeys, String beanclass) throws Exception {
		Writer output = new StringWriter();
		Template temp = TemplateFactory.getTagTemplate("sql/updateByPrimaryKey.ftl");
		List<Field> list = new ArrayList<Field>();
		for (int i = 0; i < fields.length; i++) {
			boolean flag = true;
			for (int j = 0; j < primaryKeys.length; j++) {
				if (fields[i].getColumnName().equals(primaryKeys[j].getColumnName())) {
					flag = false;
					break;
				}
			}
			if (flag) {
				list.add(fields[i]);
			}
		}
		Map<String, Object> root = new HashMap<String, Object>();
		root.put("UpdateName", "updateByPrimaryKey");
		root.put("TableName", tablename);
		root.put("BeanClass", beanclass);
		root.put("PrimaryKeys", primaryKeys);
		root.put("Fields", list.toArray(new Field[] {}));
		temp.process(root, output);
		return output.toString();
	}
	
	/**
	 * 创建updateByPrimaryKeyWithBlob
	 */
	public static String buildUpdateByPrimaryKeyWithBlob(String tablename, Field[] fields, Field[] primaryKeys, String blobbeanclass) throws Exception {
		Writer output = new StringWriter();
		Template temp = TemplateFactory.getTagTemplate("sql/updateByPrimaryKey.ftl");
		List<Field> list = new ArrayList<Field>();
		for (int i = 0; i < fields.length; i++) {
			boolean flag = true;
			for (int j = 0; j < primaryKeys.length; j++) {
				if (fields[i].getColumnName().equals(primaryKeys[j].getColumnName())) {
					flag = false;
					break;
				}
			}
			if (flag) {
				list.add(fields[i]);
			}
		}
		Map<String, Object> root = new HashMap<String, Object>();
		root.put("UpdateName", "updateByPrimaryKeyWithBlob");
		root.put("TableName", tablename);
		root.put("BeanClass", blobbeanclass);
		root.put("PrimaryKeys", primaryKeys);
		root.put("Fields", list.toArray(new Field[] {}));
		temp.process(root, output);
		return output.toString();
	}
	
	/**
	 * 创建updateByPrimaryKeySelective
	 */
	public static String buildUpdateByPrimaryKeySelective(String tablename, Field[] fields, Field[] primaryKeys, String beanclass) throws Exception {
		Writer output = new StringWriter();
		Template temp = TemplateFactory.getTagTemplate("sql/updateByPrimaryKeySelective.ftl");
		List<Field> list = new ArrayList<Field>();
		for (int i = 0; i < fields.length; i++) {
			boolean flag = true;
			for (int j = 0; j < primaryKeys.length; j++) {
				if (fields[i].getColumnName().equals(primaryKeys[j].getColumnName())) {
					flag = false;
					break;
				}
			}
			if (flag) {
				list.add(fields[i]);
			}
		}
		Map<String, Object> root = new HashMap<String, Object>();
		root.put("TableName", tablename);
		root.put("BeanClass", beanclass);
		root.put("PrimaryKeys", primaryKeys);
		root.put("Fields", list.toArray(new Field[] {}));
		temp.process(root, output);
		return output.toString();
	}
	
	/**
	 * 创建Bean对象
	 */
	public static String buildBean(String beanclass, Field[] fields) throws Exception {
		int k = beanclass.lastIndexOf(".");
		String packagename = beanclass.substring(0, k);
		String beanName = beanclass.substring(k + 1, beanclass.length());
		StringBuffer sbImport = new StringBuffer();
		for (int i = 0; i < fields.length; i++) {
			Field field = fields[i];
			if (field.getJavaClass().equals("byte[]")) {
				continue;
			} else if (field.getJavaClass().indexOf("java.lang.") == 0) {
				continue;
			}
			if (sbImport.indexOf(field.getJavaClass()) < 0) {
				sbImport.append("import ").append(field.getJavaClass()).append(";\n");
			}
		}
		Writer output = new StringWriter();
		Template temp = TemplateFactory.getTagTemplate("java/Bean.ftl");
		Map<String, Object> root = new HashMap<String, Object>();
		root.put("PackageName", packagename);
		root.put("Import", sbImport.toString());
		root.put("BeanName", beanName);
		root.put("Fields", fields);
		temp.process(root, output);
		return output.toString();
	}
	
	/**
	 * 创建BlobBean对象
	 */
	public static String buildBlobBean(String beanclass, String parentbeanclass, Field[] fields) throws Exception {
		int k = beanclass.lastIndexOf(".");
		String packagename = beanclass.substring(0, k);
		String beanName = beanclass.substring(k + 1, beanclass.length());
		StringBuffer sbImport = new StringBuffer();
		for (int i = 0; i < fields.length; i++) {
			Field field = fields[i];
			if (field.getJavaClass().equals("byte[]")) {
				continue;
			} else if (field.getJavaClass().indexOf("java.lang.") == 0) {
				continue;
			}
			if (sbImport.indexOf(field.getJavaClass()) < 0) {
				sbImport.append("import ").append(field.getJavaClass()).append(";\n");
			}
		}
		Writer output = new StringWriter();
		Template temp = TemplateFactory.getTagTemplate("java/BlobBean.ftl");
		Map<String, Object> root = new HashMap<String, Object>();
		root.put("PackageName", packagename);
		root.put("Import", sbImport.toString());
		root.put("BeanName", beanName);
		root.put("ParentBeanName", parentbeanclass);
		root.put("Fields", fields);
		temp.process(root, output);
		return output.toString();
	}
	
	/**
	 * 创建Mapper接口类
	 */
	public static String buildMapperInter(String beanclass, String interclass, String adapterclass, String keyBeanClass, String blobbeanclass, Field[] primaryKeys, boolean[] buildJavaCfg, boolean isBlob) throws Exception {
		boolean isBuildSelect = buildJavaCfg[0]; 
		boolean isBuildInsert = buildJavaCfg[1]; 
		boolean isBuildUpdate = buildJavaCfg[2]; 
		boolean isBuildDelete = buildJavaCfg[3]; 
		
		int k = interclass.lastIndexOf(".");
		String packagename = interclass.substring(0, k);
		String inter = interclass.substring(k + 1, interclass.length());
		k = beanclass.lastIndexOf(".");
		String bean = beanclass.substring(k + 1, beanclass.length());
		k = adapterclass.lastIndexOf(".");
		String adapter = adapterclass.substring(k + 1, adapterclass.length());
		k = keyBeanClass.lastIndexOf(".");
		String keyBean = keyBeanClass.substring(k + 1, keyBeanClass.length());
		k = blobbeanclass.lastIndexOf(".");
		String blobbean = blobbeanclass.substring(k + 1, blobbeanclass.length());

		Writer output = new StringWriter();
		Template temp = TemplateFactory.getTagTemplate("java/TableMapperInter.ftl");
		Map<String, Object> root = new HashMap<String, Object>();
		root.put("PackageName", packagename);
		root.put("BeanClass", beanclass); 
		root.put("AdapterClass", adapterclass);
		root.put("Inter", inter);
		root.put("Bean", bean);
		root.put("BlobBean", blobbean);
		root.put("BlobBeanClass", blobbeanclass);
		root.put("IsBlob", isBlob);
		root.put("Adapter", adapter);
		root.put("PrimaryKeys", primaryKeys);
		root.put("KeyBean", keyBean);
		root.put("KeyBeanClass", keyBeanClass);
		root.put("isBuildSelect", isBuildSelect);
		root.put("isBuildInsert", isBuildInsert);
		root.put("isBuildUpdate", isBuildUpdate);
		root.put("isBuildDelete", isBuildDelete);
		
		root.put("ProBean", DbUtil.formatProperty(bean));
		root.put("ProBlobBean", DbUtil.formatProperty(blobbean));
		temp.process(root, output);
		return output.toString();
	}
	
	/**
	 * 创建Dao类
	 */
	public static String buildMapperDao(String datasource, String beanclass, String interclass, String adapterclass, String keyBeanClass, String blobbeanclass, String daoclass, Field[] primaryKeys, boolean[] buildJavaCfg, boolean isBlob) throws Exception {
		boolean isBuildSelect = buildJavaCfg[0]; 
		boolean isBuildInsert = buildJavaCfg[1]; 
		boolean isBuildUpdate = buildJavaCfg[2]; 
		boolean isBuildDelete = buildJavaCfg[3]; 
		
		int k = interclass.lastIndexOf(".");
		String inter = interclass.substring(k + 1, interclass.length());
		k = daoclass.lastIndexOf(".");
		String packagename = daoclass.substring(0, k);
		k = beanclass.lastIndexOf(".");
		String bean = beanclass.substring(k + 1, beanclass.length());
		k = adapterclass.lastIndexOf(".");
		String adapter = adapterclass.substring(k + 1, adapterclass.length());
		k = keyBeanClass.lastIndexOf(".");
		String keyBean = keyBeanClass.substring(k + 1, keyBeanClass.length());
		k = blobbeanclass.lastIndexOf(".");
		String blobbean = blobbeanclass.substring(k + 1, blobbeanclass.length());
		k = daoclass.lastIndexOf(".");
		String daoname = daoclass.substring(k + 1, daoclass.length());
		String repositoryName = DbUtil.formatProperty(daoname);
		
		Writer output = new StringWriter();
		Template temp = TemplateFactory.getTagTemplate("java/TableMapperDao.ftl");
		Map<String, Object> root = new HashMap<String, Object>();
		root.put("DataSource", datasource);
		root.put("PackageName", packagename);
		root.put("BeanClass", beanclass); 
		root.put("InterClass", interclass);
		root.put("AdapterClass", adapterclass);
		root.put("Inter", inter);
		root.put("Bean", bean);
		root.put("BlobBean", blobbean);
		root.put("DaoName", daoname);
		root.put("RepositoryName", repositoryName);
		root.put("DaoClass", daoclass);
		root.put("BlobBeanClass", blobbeanclass);
		root.put("IsBlob", isBlob);
		root.put("Adapter", adapter);
		root.put("PrimaryKeys", primaryKeys);
		root.put("KeyBean", keyBean);
		root.put("KeyBeanClass", keyBeanClass);
		root.put("isBuildSelect", isBuildSelect);
		root.put("isBuildInsert", isBuildInsert);
		root.put("isBuildUpdate", isBuildUpdate);
		root.put("isBuildDelete", isBuildDelete);
		
		root.put("ProBean", DbUtil.formatProperty(bean));
		root.put("ProBlobBean", DbUtil.formatProperty(blobbean));
		temp.process(root, output);
		return output.toString();
	}
	
	/**
	 * 创建Adapter
	 */
	public static String buildAdapter(String adapterclass, Field[] fields) throws Exception {
		int k = adapterclass.lastIndexOf(".");
		String packagename = adapterclass.substring(0, k);
		String adapter = adapterclass.substring(k + 1, adapterclass.length());
		StringBuffer sbImport = new StringBuffer();
		for (int i = 0; i < fields.length; i++) {
			Field field = fields[i];
			if (field.getJavaClass().equals("byte[]")) {
				continue;
			} else if (field.getJavaClass().indexOf("java.lang.") == 0) {
				continue;
			}
			if (sbImport.indexOf(field.getJavaClass()) < 0) {
				sbImport.append("import ").append(field.getJavaClass()).append(";\n");
			}
		}
		Writer output = new StringWriter();
		Template temp = TemplateFactory.getTagTemplate("java/adapter.ftl");
		Map<String, Object> root = new HashMap<String, Object>();
		root.put("PackageName", packagename);
		root.put("Import", sbImport.toString());
		root.put("Adapter", adapter);
		root.put("Fields", fields);
		temp.process(root, output);
		return output.toString();
	}
	

	
	/**
	 * 根据映射XML文档生成接口类Java代码
	 */
	public static String buildInterClassCodeByMapper(XMLMapperDocument xmlDoc, IFolder parentFolder) throws Exception {
		String beanname = "";
		boolean IsTable = StringUtils.isNotBlank(xmlDoc.tableName);
		boolean IsBolb = xmlDoc.ResultMapWithBlob != null;
		if (IsTable) {
			beanname = DbUtil.getTableBeanName(xmlDoc.tableName);
		} else {
			beanname = xmlDoc.namespace.substring(xmlDoc.namespace.lastIndexOf(".") + 1);
			beanname = beanname.replaceAll("Mapper", "");
		}
		String adapter = beanname + "Adapter";
		String blobbean = beanname + "WithBlob";
		String Inter = beanname + "Mapper";
		String keybean = beanname + "Key";
		
		ProjectConfig config = StudioUtil.getConfig();
		String parentpath = parentFolder.getProjectRelativePath().toString();
		String parentPackagename = parentpath.replaceAll("src/main/java/", "").replaceAll("/", "."); // 父包名称
		String beanclass = xmlDoc.getBeanClass(); // Bean类全名
		String blobbeanclass = beanclass + "WithBlob"; // BlobBean类全名
		String keybeanclass = beanclass + "Key"; // KeyBean类全名
		String adapterclass = parentPackagename + "." + config.getModelDir() + "." + beanname + "Adapter"; // 适配器类名称
		String interclass = parentPackagename + "." + config.getInterDir() + "." + beanname + "Mapper"; // Inter接口全名
		String packagename = interclass.substring(0, interclass.lastIndexOf("."));
		
		Field[] primaryKeys = null, basefields = null, blobfields = null;
		if (xmlDoc.BaseResultMap != null) {
			if (!"java.util.HashMap".equals(beanclass)) {
				primaryKeys = StudioUtil.getResultMapKeyFields(xmlDoc.BaseResultMap);
				basefields = StudioUtil.getResultMapFields(xmlDoc.BaseResultMap);
			}
		}
		
		StringBuffer sbImport = new StringBuffer();
		if (StringUtils.isNotEmpty(beanclass))
			sbImport.append("import ").append(beanclass).append(";\n");
		if (IsTable && (primaryKeys != null) && (primaryKeys.length > 1)  && StringUtils.isNotEmpty(keybeanclass))
			sbImport.append("import ").append(keybeanclass).append(";\n");
		if (IsBolb && StringUtils.isNotEmpty(blobbeanclass))
			sbImport.append("import ").append(blobbeanclass).append(";\n");
		if (IsTable && StringUtils.isNotEmpty(adapterclass))
			sbImport.append("import ").append(adapterclass).append(";\n");
		
		List<Map<?,?>> Methods = new ArrayList<Map<?,?>>();
		for (int i = 0; i < xmlDoc.insertNodes.size(); i++) {
			Element elem = (Element)xmlDoc.insertNodes.get(i);
			String id = elem.attributeValue("id", "");
			String parameterType = elem.attributeValue("parameterType", "");
			String sql = StudioUtil.getNodeText(elem);
			List<String> remarkparams = new ArrayList<String>();
			HashMap<String, Object> map = new HashMap<String, Object>();
			map.put("RETURN_TYPE", "int");
			map.put("METHOD_NAME", id);
			if (IsTable && elem == xmlDoc.insert) {
				if (IsBolb)
					map.put("PARAMETERS", blobbean + " " + DbUtil.formatProperty(blobbean));
				else 
					map.put("PARAMETERS", beanname + " " + DbUtil.formatProperty(beanname));
			} else if (IsTable && elem == xmlDoc.insertSelective) {
				if (IsBolb)
					map.put("PARAMETERS", blobbean + " "+ DbUtil.formatProperty(blobbean));
				else 
					map.put("PARAMETERS", beanname + " "+ DbUtil.formatProperty(beanname));
			} else {
				if (StringUtils.isNotBlank(parameterType)) {
					String classname = parameterType;
					int k = parameterType.lastIndexOf(".");
					if (k > 0)
						classname = parameterType.substring(k + 1);
					map.put("PARAMETERS", classname + " " + DbUtil.formatProperty(classname));
					if (sbImport.indexOf(parameterType) < 0)
						sbImport.append("import ").append(parameterType).append(";\n");
				} else {
					List<String> javaClass = new ArrayList<String>();
					String params = buildSqlMethodParamter(sql, javaClass, remarkparams);
					for (int j = 0; j < javaClass.size(); j++) {
						String classname = javaClass.get(j);
						if ((sbImport.indexOf(classname) < 0) && (classname.indexOf("java.lang.") < 0))
							sbImport.append("import ").append(classname).append(";\n");
					}
					map.put("PARAMETERS", params);
				}
			}
			map.put("PARM_REMARKS", remarkparams);
			Matcher mat = StudioConst.MAPPER_REMARK_PAT.matcher(elem.getText());
			if (mat.find()) {
				String remark = elem.getText().substring(mat.start() + 2, mat.end() - 2).trim().replaceAll("\n", "\n    * ");
				map.put("METHOD_REMARKS", remark);
			}
			Methods.add(map);
		}
		for (int i = 0; i < xmlDoc.deleteNodes.size(); i++) {
			Element elem = (Element)xmlDoc.deleteNodes.get(i);
			String id = elem.attributeValue("id", "");
			String parameterType = elem.attributeValue("parameterType", "");
			String sql = StudioUtil.getNodeText(elem);
			List<String> remarkparams = new ArrayList<String>();
			HashMap<String, Object> map = new HashMap<String, Object>();
			map.put("RETURN_TYPE", "int");
			map.put("METHOD_NAME", id);
			if (IsTable && elem == xmlDoc.deleteByAdapter) {
				map.put("PARAMETERS", adapter + " adapter");
			} else if (IsTable && elem == xmlDoc.deleteByPrimaryKey) {
				if (primaryKeys.length > 1) {
					map.put("PARAMETERS", keybean + " key");
				} else if (primaryKeys.length == 1) {
					primaryKeys[0].getShorJavaClass();
					map.put("PARAMETERS", primaryKeys[0].getShorJavaClass() + " " + primaryKeys[0].getProperty());
				}
			} else {
				if (StringUtils.isNotBlank(parameterType)) {
					String classname = parameterType;
					int k = parameterType.lastIndexOf(".");
					if (k > 0) {
						classname = parameterType.substring(k + 1);
					}
					map.put("PARAMETERS", classname + " " + DbUtil.formatProperty(classname));
					if (sbImport.indexOf(parameterType) < 0)
						sbImport.append("import ").append(parameterType).append(";\n");
				} else {
					List<String> javaClass = new ArrayList<String>();
					String params = buildSqlMethodParamter(sql, javaClass, remarkparams);
					for (int j = 0; j < javaClass.size(); j++) {
						String classname = javaClass.get(j);
						if ((sbImport.indexOf(classname) < 0) && (classname.indexOf("java.lang.") < 0))
							sbImport.append("import ").append(classname).append(";\n");
					}
					map.put("PARAMETERS", params);
				}
			}
			map.put("PARM_REMARKS", remarkparams);
			Matcher mat = StudioConst.MAPPER_REMARK_PAT.matcher(elem.getText());
			if (mat.find()) {
				String remark = elem.getText().substring(mat.start() + 2, mat.end() - 2).trim().replaceAll("\n", "\n    * ");
				map.put("METHOD_REMARKS", remark);
			}
			Methods.add(map);
		}
		for (int i = 0; i < xmlDoc.updateNodes.size(); i++) {
			Element elem = (Element)xmlDoc.updateNodes.get(i);
			String id = elem.attributeValue("id", "");
			String parameterType = elem.attributeValue("parameterType", "");
			String sql = StudioUtil.getNodeText(elem);
			List<String> remarkparams = new ArrayList<String>();
			HashMap<String, Object> map = new HashMap<String, Object>();
			map.put("RETURN_TYPE", "int");
			map.put("METHOD_NAME", id);
			if (IsTable && elem == xmlDoc.updateByAdapter) {
				map.put("PARAMETERS", "@Param(\"record\") " + beanname + " " + DbUtil.formatProperty(beanname) + ", @Param(\"adapter\") " + adapter + " adapter");
			} else if (IsTable && elem == xmlDoc.updateByAdapterSelective) {
				map.put("PARAMETERS", "@Param(\"record\") " + beanname + " " + DbUtil.formatProperty(beanname) + ", @Param(\"adapter\") " + adapter + " adapter");
			} else if (IsTable && elem == xmlDoc.updateByAdapterWithBlob) {
				map.put("PARAMETERS", "@Param(\"record\") " + blobbean + " " + DbUtil.formatProperty(blobbean) + ", @Param(\"adapter\") " + adapter + " adapter");
			} else if (IsTable && elem == xmlDoc.updateByPrimaryKey) {
				map.put("PARAMETERS", beanname + " " + DbUtil.formatProperty(beanname));
			} else if (IsTable && elem == xmlDoc.updateByPrimaryKeySelective) {
				if (IsBolb) {
					map.put("PARAMETERS", blobbean + " " + DbUtil.formatProperty(blobbean));
				} else {
					map.put("PARAMETERS", beanname + " " + DbUtil.formatProperty(beanname));
				}
			} else if (IsTable && elem == xmlDoc.updateByPrimaryKeyWithBlob) {
				map.put("PARAMETERS", blobbean + " " + DbUtil.formatProperty(beanname));
			} else {
				if (StringUtils.isNotBlank(parameterType)) {
					String classname = parameterType;
					int k = parameterType.lastIndexOf(".");
					if (k > 0)
						classname = parameterType.substring(k + 1);
					map.put("PARAMETERS", classname + " " + DbUtil.formatProperty(classname));
					if (sbImport.indexOf(parameterType) < 0)
						sbImport.append("import ").append(parameterType).append(";\n");
				} else {
					List<String> javaClass = new ArrayList<String>();
					String params = buildSqlMethodParamter(sql, javaClass, remarkparams);
					for (int j = 0; j < javaClass.size(); j++) {
						String classname = javaClass.get(j);
						if ((sbImport.indexOf(classname) < 0) && (classname.indexOf("java.lang.") < 0))
							sbImport.append("import ").append(classname).append(";\n");
					}
					map.put("PARAMETERS", params);
				}
			}
			map.put("PARM_REMARKS", remarkparams);
			Matcher mat = StudioConst.MAPPER_REMARK_PAT.matcher(elem.getText());
			if (mat.find()) {
				String remark = elem.getText().substring(mat.start() + 2, mat.end() - 2).trim().replaceAll("\n", "\n    * ");
				map.put("METHOD_REMARKS", remark);
			}
			Methods.add(map);
		}
		for (int i = 0; i < xmlDoc.selectNodes.size(); i++) {
			Element elem = (Element)xmlDoc.selectNodes.get(i);
			String id = elem.attributeValue("id", "");
			String parameterType = elem.attributeValue("parameterType", "");
			String resultMap = elem.attributeValue("resultMap", "");
			String resultType = elem.attributeValue("resultType", "");
			String sql = StudioUtil.getNodeText(elem);
			List<String> remarkparams = new ArrayList<String>();
			HashMap<String, Object> map = new HashMap<String, Object>();
			map.put("METHOD_NAME", id);
			if (elem == xmlDoc.selectByPrimaryKey) {
				map.put("RETURN_TYPE", beanname);
				if (primaryKeys != null && primaryKeys.length > 1) {
					map.put("PARAMETERS", keybean + " key");
				} else if (primaryKeys.length == 1) {
					map.put("PARAMETERS", primaryKeys[0].getShorJavaClass() + " " + primaryKeys[0].getProperty());
				}
			} else if (elem == xmlDoc.selectByAdapter) {
				map.put("RETURN_TYPE", "List<" + beanname + ">");
				map.put("PARAMETERS", adapter + " adapter");
			} else if (elem == xmlDoc.selectByAdapterWithBlob) {
				map.put("RETURN_TYPE", "List<" + blobbean + ">");
				map.put("PARAMETERS", adapter + " adapter");
			} else if (elem == xmlDoc.countByAdapter) {
				map.put("RETURN_TYPE", "int");
				map.put("PARAMETERS", adapter + " adapter");
			} else {
				if (StringUtils.isNotBlank(resultMap)) { //映射列表
					Element rmap = xmlDoc.findNodeByName(resultMap);
					if (rmap != null) {
						String type = rmap.attributeValue("type", "");
						String classname = type;
						int k = type.lastIndexOf(".");
						if (k > 0)
							classname = type.substring(k + 1);
						map.put("RETURN_TYPE", "List<" + classname + ">");
						if ((sbImport.indexOf(type) < 0) && (type.indexOf("java.lang.") < 0))
							sbImport.append("import ").append(type).append(";\n");
					}
				} else if (StringUtils.isNotBlank(resultType)) { //映射类
					String classname = resultType;
					int k = resultType.lastIndexOf(".");
					if (k > 0)
						classname = resultType.substring(k + 1);
					map.put("RETURN_TYPE", classname);
					if ((sbImport.indexOf(resultType) < 0) && (resultType.indexOf("java.lang.") < 0))
						sbImport.append("import ").append(resultType).append(";\n");
				}
				if (StringUtils.isNotBlank(parameterType)) {
					String classname = parameterType;
					int k = parameterType.lastIndexOf(".");
					if (k > 0)
						classname = parameterType.substring(k + 1);
					map.put("PARAMETERS", classname + " " + DbUtil.formatProperty(classname));
					if (sbImport.indexOf(parameterType) < 0)
						sbImport.append("import ").append(parameterType).append(";\n");
				} else {
					List<String> javaClass = new ArrayList<String>();
					String params = buildSqlMethodParamter(sql, javaClass, remarkparams);
					for (int j = 0; j < javaClass.size(); j++) {
						String classname = javaClass.get(j);
						if ((sbImport.indexOf(classname) < 0) && (classname.indexOf("java.lang.") < 0))
							sbImport.append("import ").append(classname).append(";\n");
					}
					map.put("PARAMETERS", params);
				}
			}
			map.put("PARM_REMARKS", remarkparams);
			Matcher mat = StudioConst.MAPPER_REMARK_PAT.matcher(elem.getText());
			if (mat.find()) {
				String remark = elem.getText().substring(mat.start() + 2, mat.end() - 2).trim().replaceAll("\n", "\n    * ");
				map.put("METHOD_REMARKS", remark);
			}
			Methods.add(map);
		}
		Writer output = new StringWriter();
		Template temp = TemplateFactory.getTagTemplate("java/MapperInter.ftl");
		Map<String, Object> root = new HashMap<String, Object>();
		root.put("PackageName", packagename);
		root.put("Import", sbImport.toString());
		root.put("Inter", Inter); 
		root.put("Methods", Methods);
		temp.process(root, output);
		return output.toString();
	}
	
	/**
	 * 构建方法的参数
	 * @param sql SQL语句
	 * @param javaClass 构建方法需要费用的java类列表
	 * @param remarkparams 构建方法注释的参数列表
	 * @return 返回参数串，如： ＠Param("record") MyTest record, ＠Param("adapter") MyTestAdapter adapter
	 */
	public static String buildSqlMethodParamter(String sql, List<String> javaClass, List<String> remarkparams) {
		List<String> paramlist = new ArrayList<String>();
		StringBuffer sb = new StringBuffer();
		List<Integer> indexs = new ArrayList<Integer>();
		// 取foreach里的列表参数
		try {
			int start = sql.indexOf("<foreach");
			int end = sql.indexOf("</foreach>");
			while (start >= 0 && start < end) {
				int start1 = sql.indexOf("collection=\"", start);
				if (start1 > 0) {
					int end1 = sql.indexOf("\"", start1 + "collection=\"".length());
					if (end1 > start1) {
						String paramname = sql.substring(start1 + "collection=\"".length(), end1);
						paramname = "@Param(\"" + paramname + "\") List<Object> " + paramname;
						if (sb.indexOf(paramname) < 0) {
							if (sb.length() > 0)
								sb.append(", ");
							sb.append(paramname);
						}
						indexs.add(start);
						indexs.add(end);
					}
				}
				start = sql.indexOf("<foreach ", end);
				end = sql.indexOf("</foreach>", start);
			}
		} catch (Exception e1) {
			e1.printStackTrace();
		}
		try {
			Matcher m = StudioConst.MAPPER_PARAM_PAT.matcher(sql);
			while (m.find()) {
				String param = sql.substring(m.start(), m.end());
				boolean flag = true;
				for (int i = 0; i < paramlist.size(); i++) {
					if (param.equals(paramlist.get(i))) {
						flag = false;
						break;
					}
				}
				if (flag) {
					boolean inflag = false;
					for (int i = 0; i < indexs.size(); i=i+2) {
						if (m.start() > indexs.get(i) && m.end() < indexs.get(i + 1)) {
							inflag = true;
							break;
						}
					}
					if (!inflag) {
						paramlist.add(param);
					}
				}
			}
		} catch (Exception e1) {
			e1.printStackTrace();
		}
		for (int i = 0; i < paramlist.size(); i++) {
			String s = paramlist.get(i).trim();
			s = s.substring(2);
			s = s.substring(0, s.length() - 1);
			remarkparams.add("@param " + s.replaceAll(",", " "));
			String[] values = new String[] { "", "", "" };
			String[] ss = s.split(",");
			if (ss.length >= 1) {
				values[0] = ss[0];
			}
			if (ss.length >= 2) {
				String[] sss = ss[1].split("=");
				values[1] = sss[1];
			}
			String type = DbUtil.getJavaClass(DbUtil.getJdbcTypeByName(values[1]));
			javaClass.add(type);
			int k = type.lastIndexOf('.');
			String classname = type;
			if (k > 0)
				classname = type.substring(k + 1);
			if (sb.length() > 0)
				sb.append(", ");
			sb.append("@Param(\"").append(values[0]).append("\") ").append(classname).append(" ").append(values[0]);
		}
		return sb.toString();
	}
	
	public static String buildEmptyDao(String DataSource, String interClass) throws Exception {
		int k = interClass.lastIndexOf(".");
		String Inter = interClass.substring(k + 1, interClass.length());
		String mapperPkg = interClass.substring(0, k);
		k = mapperPkg.lastIndexOf(".");
		String PackageName = mapperPkg.substring(0, k);
		String DaoName = Inter.substring(0, Inter.length() - 6) + "Dao";
		String RepositoryName = DbUtil.formatProperty(DaoName);
		
		Writer output = new StringWriter();
		Template temp = TemplateFactory.getTagTemplate("java/MapperDao.ftl");
		Map<String, Object> root = new HashMap<String, Object>();
		root.put("PackageName", PackageName);
		root.put("InterClass", interClass);
		root.put("DataSource", DataSource);
		root.put("RepositoryName", RepositoryName);
		root.put("DaoName", DaoName);
		root.put("Inter", Inter);
		temp.process(root, output);
		return output.toString();
	}
	
	public static String buildEmptyInterMapper(String interClass) throws Exception {
		int k = interClass.lastIndexOf(".");
		String Inter = interClass.substring(k + 1, interClass.length());
		String PackageName = interClass.substring(0, k);
		
		Writer output = new StringWriter();
		Template temp = TemplateFactory.getTagTemplate("java/EmptyMapperInter.ftl");
		Map<String, Object> root = new HashMap<String, Object>();
		root.put("PackageName", PackageName);
		root.put("Inter", Inter);
		temp.process(root, output);
		return output.toString();
	}
}
