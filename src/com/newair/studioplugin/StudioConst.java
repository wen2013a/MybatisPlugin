package com.newair.studioplugin;

import java.util.regex.Pattern;

public class StudioConst {
	public static final String MYBATIS_VIEW = "com.newair.studioplugin.views.MybatisView";
	public static final String NAVIGATOR_VIEW = "com.newair.studioplugin.views.NavigatorView";
	public static final String MYBATIS_EDIT = "com.newair.studioplugin.editors.MybatisEditor";

	public static final String ICON_OPEN_FILE = "icons/button/open.gif";
	public static final String ICON_OPEN_MAP_FILE = "icons/open_map2.gif";
	public static final String ICON_CREATE_MAP_FILE = "icons/etool16/new_wiz.gif";
	public static final String ICON_MAP_FILE = "icons/open_map2.gif";
	public static final String ICON_MAP_FOLDER = "icons/obj16/activity_category.gif";
	public static final String ICON_SOURCE_EDITOR = "icons/editor/source-editor.png";
	public static final String ICON_VISUAL_EDITOR = "icons/editor/visual-editor.png";
	
	public static final String ICON_NODE = "icons/obj16/generic_elements.gif";
	public static final String ICON_NODE_SELECT = "icons/button/lookin.gif";
	public static final String ICON_NODE_INSERT = "icons/button/add.gif";
	public static final String ICON_NODE_UPDATE = "icons/button/update.gif";
	public static final String ICON_NODE_DELETE = "icons/button/del.gif";
	public static final String ICON_NODE_SQL = "icons/button/file_obj.gif";
	public static final String ICON_NODE_RESULT_MAP = "icons/eview16/prop_ps.gif";
	public static final String ICON_NODE_PARAMETER_MAP = "icons/eview16/prop_ps.gif";
	public static final String ICON_NODE_CFG_CDATA = "icons/node/servModule.gif";
	public static final String ICON_PARAM_IN = "icons/etool16/import_wiz.gif";
	public static final String ICON_PARAM_OUT = "icons/etool16/export_wiz.gif";
	public static final String ICON_PARAM_FIELD_KEY = "icons/obj/java.png";
	public static final String ICON_RESULT_MAP_NORMAL = "icons/obj16/generic_element.gif";
	public static final String ICON_RESULT_MAP_1 = "icons/obj16/keygroups_obj.gif";
	
	public static final String ICON_RESULT_MAP = "icons/obj16/generic_elements.gif";
	public static final String ICON_RESULT_OBJECT = "icons/obj/type.png";
	public static final String ICON_REFRESH = "icons/button/refresh.gif";
	
	public static final String ICON_DATASOURCE = "icons/obj/default.gif";
	public static final String ICON_INTF_PROJECT = "icons/obj16/activity_category.gif";
	
	public static final String NODE_CFG_CDATA_NAME = "配置结点";
	public static final String MAPPER_FOLDER_NAME = "/dao/mapper";
	public static final String EXT_MAPPER_NAME = "xml";

	public static final String NODE_SELECT = "select";
	public static final String NODE_INSERT = "insert";
	public static final String NODE_UPDATE = "update";
	public static final String NODE_DELETE = "delete";
	public static final String NODE_SQL = "sql";
	public static final String NODE_RESULT_MAP = "resultMap";
	public static final String NODE_PARAMETER_MAP = "parameterMap";
	public static final String NODE_CDATA_CFG_FLAG = "***This is mpper file config part,Please Don't Edit this section!***";

	public static final String PARAM_MAP_ATTR_MODE = "mode";
	public static final String PARAM_MAP_ATTR_PROPERTY = "property";
	public static final String PARAM_MAP_ATTR_JDBCTYPE = "jdbcType";
	public static final String PARAM_MAP_ATTR_JAVATYPE = "javaType";
	
	public static final String NODE_SELECT_NAME = "查询";
	public static final String NODE_INSERT_NAME = "插入";
	public static final String NODE_UPDATE_NAME = "更新";
	public static final String NODE_DELETE_NAME = "删除";
	public static final String NODE_SQL_NAME = "引用";
	public static final String NODE_RESULT_MAP_NAME = "结果";
	public static final String NODE_PARAMETER_MAP_NAME = "映射";
	public static final String NODE_CDATA_CFG_FLAG_NAME = "配置";
	
	public static final String XML_PREFIX_STR = "  "; // "\t"; XML缩写字符串
	//匹配HTML标记的正则表达式：
	public static final Pattern XMLFLAG_BEGIN_PAT = Pattern.compile(StudioConst.XMLFLAG_BEGIN_REGEX);
	public static final Pattern XMLFLAG_BEGIN_PAT1 = Pattern.compile(StudioConst.XMLFLAG_BEGIN_REGEX1);
	public static final Pattern XMLFLAG_END_PAT = Pattern.compile(StudioConst.XMLFLAG_END_REGEX);
	public static final String XMLFLAG_BEGIN_REGEX = "<[A-Za-z](.[^>]*)>"; //如：<foreach item="item" index="index" collection="list" open="(" separator="," close=")">
	public static final String XMLFLAG_BEGIN_REGEX1 = "<[A-Za-z](.[^>]*)/>"; //如：<foreach item="item" index="index" collection="list" open="(" separator="," close=")" />
	public static final String XMLFLAG_END_REGEX = "</[A-Za-z](\\w[^\\s>]*)>";  // 如</foreach>
	
	//映射文件名称正则表达式：
	public static final Pattern MAPPER_FILENAME_PAT = Pattern.compile(StudioConst.MAPPER_FILENAME_REGEX);
	public static final String MAPPER_FILENAME_REGEX = "^[a-zA-Z0-9_]+\\.(xml)|(XML)$";
	
	public static final Pattern MAPPER_PARAM_PAT = Pattern.compile(StudioConst.MAPPER_PARAM_REGEX);
	public static final Pattern MAPPER_PARAM_PAT2 = Pattern.compile(StudioConst.MAPPER_PARAM_REGEX2);
	public static final Pattern MAPPER_PARAM_PAT3 = Pattern.compile(StudioConst.MAPPER_PARAM_REGEX3);
	public static final String MAPPER_PARAM_REGEX = "[#|$]\\{[\\S][^#|$]+\\}"; //"#\\{[\\S]+\\}"; //参数正则表达式，如#{userId,jdbcType=INTEGER} 或 ${userId}
	public static final String MAPPER_PARAM_REGEX2 = "[$]\\{[\\S][^#|$]+\\}"; //参数正则表达式，如${userId}
	public static final String MAPPER_PARAM_REGEX3 = "[#]\\{[\\S][^#|$]+\\}"; //参数正则表达式，如#{userId,jdbcType=INTEGER}
	
	public static final int BLOB_DATA_SIZE = 4096; //对于字符串数据类型，宽度大于BLOB_DATA_SIZE的值时为大数据类型

	public static final Pattern MAPPER_REMARK_PAT = Pattern.compile("\\/\\*(.|\\n)*?\\*\\/"); // 注释如： /* 我的查询 */
	
	//Mapper映射文件中命名关键字
	public static final String MAP_ID_BaseResultMap = "BaseResultMap";
	public static final String MAP_ID_ResultMapWithBlob = "ResultMapWithBlob";
	public static final String MAP_ID_AdapterUpdateWhereCondition = "AdapterUpdateWhereCondition";
	public static final String MAP_ID_AdapterWhereCondition = "AdapterWhereCondition";
	public static final String MAP_ID_BaseColumnList = "BaseColumnList";
	public static final String MAP_ID_BlobColumnList = "BlobColumnList";
	public static final String MAP_ID_countByAdapter = "countByAdapter";
	public static final String MAP_ID_selectByAdapter = "selectByAdapter";
	public static final String MAP_ID_selectByAdapterWithBlob = "selectByAdapterWithBlob";
	public static final String MAP_ID_selectByPrimaryKey = "selectByPrimaryKey";
	public static final String MAP_ID_deleteByAdapter = "deleteByAdapter";
	public static final String MAP_ID_deleteByPrimaryKey = "deleteByPrimaryKey";
	public static final String MAP_ID_insert = "insert";
	public static final String MAP_ID_insertSelective = "insertSelective";
	public static final String MAP_ID_updateByAdapter = "updateByAdapter";
	public static final String MAP_ID_updateByAdapterWithBlob = "updateByAdapterWithBlob";
	public static final String MAP_ID_updateByAdapterSelective = "updateByAdapterSelective";
	public static final String MAP_ID_updateByPrimaryKey = "updateByPrimaryKey";
	public static final String MAP_ID_updateByPrimaryKeyWithBlob = "updateByPrimaryKeyWithBlob";
	public static final String MAP_ID_updateByPrimaryKeySelective = "updateByPrimaryKeySelective";
	public static final String MAP_ID_CFGNODE = "CONFIG_NODE";
}
