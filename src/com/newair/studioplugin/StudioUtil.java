package com.newair.studioplugin;

import java.awt.Toolkit;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.StringTokenizer;
import java.util.regex.Matcher;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.dom4j.Attribute;
import org.dom4j.CDATA;
import org.dom4j.CharacterData;
import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;
import org.dom4j.Node;
import org.dom4j.io.SAXReader;
import org.eclipse.core.resources.IContainer;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.jdt.core.IJavaElement;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.internal.ui.packageview.PackageFragmentRootContainer;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.ISelectionService;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.internal.Workbench;
import org.xml.sax.InputSource;

import com.newair.studioplugin.db.DbUtil;
import com.newair.studioplugin.db.Field;
import com.newair.studioplugin.editors.IgnoreDTDEntityResolver;
import com.newair.studioplugin.editors.XMLMapperDocument;
import com.newair.studioplugin.views.MybatisView;

public class StudioUtil {
	
	private static final Logger log = Logger.getLogger(StudioUtil.class);

	/**
	 * 获取当前打开的工程
	 */
	public static IProject getCurrentProject() {
		IProject project = null;
		ISelectionService selectionService = Workbench.getInstance().getActiveWorkbenchWindow().getSelectionService();
		ISelection selection = selectionService.getSelection();
		if (selection instanceof IStructuredSelection) {
			Object element = ((IStructuredSelection) selection).getFirstElement();
			if (element instanceof IResource) {
				project = ((IResource) element).getProject();
			} else if (element instanceof PackageFragmentRootContainer) {
				IJavaProject jProject = ((PackageFragmentRootContainer) element).getJavaProject();
				project = jProject.getProject();
			} else if (element instanceof IJavaElement) {
				IJavaProject jProject = ((IJavaElement) element).getJavaProject();
				project = jProject.getProject();
			}
		}
		if (project == null) {
			IWorkbenchPage activePage = Workbench.getInstance().getActiveWorkbenchWindow().getActivePage();
			if (activePage != null) {
				IEditorPart part = activePage.getActiveEditor();
				if (part != null) {
					Object object = part.getEditorInput().getAdapter(IFile.class);
					if (object != null) {
						project = ((IFile) object).getProject();
					}
				}
				if (project == null) {
					// activePage.getViews();
				}

			}
		}
		return project;
	}
	
	/**
	 * 获取工程
	 */
	public static IProject getProject(String projectname) {
		if (StringUtils.isBlank(projectname)) {
			return null;
		}
		return ResourcesPlugin.getWorkspace().getRoot().getProject(projectname);
	}
	
	/**
	 * 判断是否为Maven工程
	 */
	public static boolean isMavenProject(IProject project) {
		boolean isMavenPrj = true;
		if (project != null) {
			IFile cfgfile = project.getFile("pom.xml");
			isMavenPrj = cfgfile.exists();
		}
		return isMavenPrj;
	}

	/**
	 * 获取当前工程下的配置
	 */
	public static ProjectConfig getConfig() {
		ProjectConfig config = new ProjectConfig();
		try {
			IProject proj = StudioUtil.getCurrentProject();
			IFile cfgfile = proj.getFile(".settings/StudioPlugin.properties");

			String filename = ((IFile) cfgfile).getLocation().makeAbsolute().toFile().getAbsolutePath();
			File file = new File(filename);
			if (!file.exists()) {
				file.createNewFile();
				return config;
			}
			InputStream input = new FileInputStream(filename);
			Properties prop = new Properties();
			prop.load(input);
			config.setDataSource(prop.getProperty("datasource"));
			config.setJavaSrc(prop.getProperty("javasrc"));
			config.setMapperRoot(prop.getProperty("mapperroot"));
			config.setMapperDir(prop.getProperty("mapperdir"));
			config.setModelDir(prop.getProperty("modeldir"));
			config.setInterDir(prop.getProperty("interdir"));
			config.setImplDir(prop.getProperty("impldir"));
			config.setDbConfigFile(prop.getProperty("dbConfigFile"));
			config.setIntfProject(prop.getProperty("intfProject"));
			config.setIntfSrc(prop.getProperty("intfSrc"));
			
			IProject intfproj = StudioUtil.getProject(config.getIntfProject());
			if (intfproj == null) {
				intfproj = proj;
			}
			//加载数据库配置
			String DATASOURCES = prop.getProperty("DATASOURCES");
			if (StringUtils.isNotEmpty(DATASOURCES)) {
				String[] dataSources = DATASOURCES.split(",");
				for (int i = 0; i < dataSources.length; i++) {
					String ds = dataSources[i].trim();
					DataSourceCfg datasource = new DataSourceCfg();
					datasource.setDatasource(ds);
					datasource.setDriverFile(prop.getProperty(ds + ".driverFile", ""));
					datasource.setDriverClass(prop.getProperty(ds + ".driverClassName", ""));
					datasource.setUrl(prop.getProperty(ds + ".url", ""));
					datasource.setUsername(prop.getProperty(ds + ".username", ""));
					datasource.setPassword(prop.getProperty(ds + ".password", ""));
					config.addDataSource(datasource);
				}
			}
			if (StringUtils.isBlank(DATASOURCES)) {
				DataSourceCfg datasource = new DataSourceCfg();
				datasource.setDatasource("MyDataSource");
				datasource.setDriverFile("");
				datasource.setDriverClass("com.microsoft.sqlserver.jdbc.SQLServerDriver");
				datasource.setUrl("jdbc:sqlserver://127.0.0.1:1433;databaseName=MyDataSource;useLOBs=false");
				datasource.setUsername("");
				datasource.setPassword("");
				config.addDataSource(datasource);
				config.setDataSource(datasource.getDatasource());
			}

			if (StringUtils.isBlank(config.getMapperRoot())) {
				config.setMapperRoot(StudioUtil.isMavenProject(proj) ? "src/main/java" : "src");
			}
			if (StringUtils.isBlank(config.getJavaSrc())) {
				config.setJavaSrc(StudioUtil.isMavenProject(proj) ? "src/main/java" : "src");
			}
			if (StringUtils.isBlank(config.getIntfSrc())) {
				config.setIntfSrc(StudioUtil.isMavenProject(intfproj) ? "src/main/java" : "src");
			}
			if (StringUtils.isBlank(config.getMapperDir())) {
				config.setMapperDir("mapper");
			}
			if (StringUtils.isBlank(config.getModelDir())) {
				config.setModelDir("mapper");
			}
			if (StringUtils.isBlank(config.getInterDir())) {
				config.setInterDir("mapper");
			}
			if (StringUtils.isBlank(config.getImplDir())) {
				config.setImplDir("mapper");
			}
			input.close();
		} catch (Exception e) {
			log.error(e.getMessage(), e);
		}
		return config;
	}

	/**
	 * 可在当前工程的配置
	 */
	public static void saveConfig(ProjectConfig config) {
		try {
			IProject proj = StudioUtil.getCurrentProject();
			IFile cfgfile = proj.getFile(".settings/StudioPlugin.properties");
			String filename = ((IFile) cfgfile).getLocation().makeAbsolute().toFile().getAbsolutePath();
			File file = new File(filename);
			if (!file.exists()) {
				file.createNewFile();
			}
			OutputStream output = new FileOutputStream(filename);
			Properties prop = new Properties();
			prop.setProperty("datasource", config.getDataSource());
			prop.setProperty("javasrc", config.getJavaSrc());
			prop.setProperty("mapperroot", config.getMapperRoot());
			prop.setProperty("mapperdir", config.getMapperDir());
			prop.setProperty("modeldir", config.getModelDir());
			prop.setProperty("interdir", config.getInterDir());
			prop.setProperty("impldir", config.getImplDir());
			prop.setProperty("dbConfigFile", config.getDbConfigFile());
			prop.setProperty("intfProject", config.getIntfProject());
			prop.setProperty("intfSrc", config.getIntfSrc());
			//保存数据库配置
			List<DataSourceCfg> datasources = config.getDataSources();
			String dss = "";
			for (int i = 0; i < datasources.size(); i++) {
				DataSourceCfg datasource = datasources.get(i);
				String ds = datasource.getDatasource();
				dss += (StringUtils.isBlank(dss) ? "" : ",") + ds;
				prop.setProperty(ds + ".driverFile", datasource.getDriverFile());
				prop.setProperty(ds + ".driverClassName", datasource.getDriverClass());
				prop.setProperty(ds + ".url", datasource.getUrl());
				prop.setProperty(ds + ".username", datasource.getUsername());
				prop.setProperty(ds + ".password", datasource.getPassword());
			}
			prop.setProperty("DATASOURCES", dss);
			prop.store(output, "");
			output.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * 是否是数据库映射文件(如：dao/mapper/*.xml)
	 */
	public static boolean isMapperFile(Object element) {
		if (element != null && element instanceof IFile) {
			IFile file = (IFile) element;
			String fileExtName = file.getFileExtension();
			if (StudioConst.EXT_MAPPER_NAME.equals(fileExtName)) {
				String s = StudioConst.MAPPER_FOLDER_NAME + "/" + file.getName();
				String ss = file.getFullPath().toString();
				int i = ss.indexOf(s);
				if (i == (ss.length() - s.length())) {
					return true;
				}
			}
		}
		return false;
	}

	/**
	 * 是否为映射文件夹(如：dao/mapper)
	 */
	public static boolean isMapperFolder(Object element) {
		if (element != null && element instanceof IFolder) {
			IFolder folder = (IFolder) element;
			String s = StudioConst.MAPPER_FOLDER_NAME;
			String ss = folder.getFullPath().toString();
			int i = ss.indexOf(s);
			if (i == (ss.length() - s.length())) {
				return true;
			}
		}
		return false;
	}

	/**
	 * 获取图标
	 */
	public static Image getImage(String key) {
		return StudioActivator.getDefault().getImage(key);
	}

	/**
	 * 获取一个结点字符串
	 */
	public static String getNodeText(Node node) {
		StringBuffer sb = new StringBuffer();
		if (node.getNodeType() == Node.ELEMENT_NODE) {
			Element element = (Element) node;
			int PRE_NODE_TYPE = -1;
			for (Iterator<?> i = element.nodeIterator(); i.hasNext();) {
				Node subnode = (Node) i.next();
				switch (subnode.getNodeType()) {
				case Node.CDATA_SECTION_NODE:
					sb.append(subnode.getText());
					break;
				case Node.ELEMENT_NODE:
					if (PRE_NODE_TYPE != Node.TEXT_NODE) {
						sb.append("\n");
					}
					sb.append(subnode.asXML());
					break;
				case Node.TEXT_NODE:
					sb.append(subnode.getText());
					break;
				default:
					sb.append(subnode.asXML());
					break;
				}
				PRE_NODE_TYPE = subnode.getNodeType();
			}
		} else if (node.getNodeType() == Node.CDATA_SECTION_NODE) {
			sb.append(node.getText());
		} else {
			sb.append(node.asXML());
		}
		// 去除首尾空行
		return delHeadTailBlankLine(sb.toString());
	}

	/**
	 * 去除缩进前缀 level: 缩进等级
	 */
	public static String delPerfixBlank(String text, int level) {
		String indent = StringUtils.repeat(StudioConst.XML_PREFIX_STR, level);
		StringReader sr = new StringReader(text);
		BufferedReader br = new BufferedReader(sr);
		StringBuffer sb = new StringBuffer();
		String ln;
		try {
			while ((ln = br.readLine()) != null) {
				if (ln.indexOf(indent) == 0) {
					ln = ln.replaceFirst(indent, "");
				}
				sb.append(ln).append("\n");
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return sb.toString();
	}

	/**
	 * 添加缩进前缀 level: 缩进等级
	 */
	public static String addPerfixBlank(String content, int level) {
		String indent = StringUtils.repeat(StudioConst.XML_PREFIX_STR, level);
		StringReader sr = new StringReader(content);
		BufferedReader br = new BufferedReader(sr);
		StringBuffer sb = new StringBuffer();
		String ln;
		try {
			while ((ln = br.readLine()) != null) {
				if (StringUtils.isNotBlank(ln)) {
					sb.append(indent).append(ln).append("\n");
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return sb.toString();
	}

	/**
	 * 判断一个结点包含内容是否为纯文本
	 */
	public static boolean isTextContent(Element element) {
		for (Iterator<?> i = element.nodeIterator(); i.hasNext();) {
			Node node = (Node) i.next();
			if (node.getNodeType() != Node.TEXT_NODE) {
				return false;
			}
		}
		return true;
	}

	/**
	 * 格式化XML的内容，对于<,\,&>等特殊字符进行替换
	 */
	public static String formatXMLContext(String content) {
		boolean[] flag = new boolean[content.length()];
		Matcher mat = StudioConst.XMLFLAG_BEGIN_PAT.matcher(content);
		while (mat.find()) {
			for (int i = mat.start(); i <= mat.end(); i++) {
				flag[i] = true;
			}
		}
		mat = StudioConst.XMLFLAG_BEGIN_PAT1.matcher(content);
		while (mat.find()) {
			for (int i = mat.start(); i <= mat.end(); i++) {
				flag[i] = true;
			}
		}
		mat = StudioConst.XMLFLAG_END_PAT.matcher(content);
		while (mat.find()) {
			for (int i = mat.start(); i <= mat.end(); i++) {
				flag[i] = true;
			}
		}
		StringBuffer sb = new StringBuffer();
		int i = 0, j = 0;
		boolean f = flag[i];
		for (int k = 0; k < flag.length; k++) {
			if (f == flag[k]) {
				j = k;
				if (k == flag.length - 1) {
					if (f) {
						sb.append(content.substring(i, j + 1));
					} else {
						sb.append(getStringWithXML(content.substring(i, j + 1)));
					}
				}
			} else {
				if (f) {
					sb.append(content.substring(i, j + 1));
				} else {
					sb.append(getStringWithXML(content.substring(i, j + 1)));
				}
				f = flag[k];
				i = j = k;
			}
		}
		return sb.toString();
	}

	/**
	 * 设置结点内容
	 * 
	 * @param element
	 *            :设置的节点 content:节点内容字符串
	 */
	public static void setElementContent(Element element, String content) throws DocumentException {
		int level = getNodeLevel(element);
		String prefix = StringUtils.repeat(StudioConst.XML_PREFIX_STR, level);
		if (isSimpleText(content)) { // 使用CDATA包含文本内容
			StringBuffer sb = new StringBuffer(addPerfixBlank(content, level));
			sb.insert(0, "\n").append(prefix);
			element.clearContent();
			element.addText("\n" + prefix);
			element.addCDATA(sb.toString());
			element.addText("\n" + StringUtils.repeat(StudioConst.XML_PREFIX_STR, level - 1));
		} else { // 解析其中XML标签
			boolean[] flag = new boolean[content.length()];
			Matcher mat = StudioConst.XMLFLAG_BEGIN_PAT.matcher(content);
			while (mat.find()) {
				for (int i = mat.start(); i <= mat.end(); i++) {
					flag[i] = true;
				}
			}
			mat = StudioConst.XMLFLAG_BEGIN_PAT1.matcher(content);
			while (mat.find()) {
				for (int i = mat.start(); i <= mat.end(); i++) {
					flag[i] = true;
				}
			}
			mat = StudioConst.XMLFLAG_END_PAT.matcher(content);
			while (mat.find()) {
				for (int i = mat.start(); i <= mat.end(); i++) {
					flag[i] = true;
				}
			}
			StringBuffer sb = new StringBuffer();
			int i = 0, j = 0;
			boolean f = flag[i];
			for (int k = 0; k < flag.length; k++) {
				if (f == flag[k]) {
					j = k;
					if (k == flag.length - 1) {
						if (f) {
							sb.append(content.substring(i, j + 1));
						} else {
							sb.append(getStringWithXML(content.substring(i, j + 1)));
						}
					}
				} else {
					if (f) {
						sb.append(content.substring(i, j + 1));
					} else {
						sb.append(getStringWithXML(content.substring(i, j + 1)));
					}
					f = flag[k];
					i = j = k;
				}
			}
			String formatxml = formatXMLDocContent(sb.toString(), level);
			Element node = (Element)StudioUtil.getXmlNode(formatxml);

			element.clearContent();
			element.addText("\n" + prefix);
			while (node.nodeCount() > 0) {
				Node tmpNode = node.node(0);
				node.remove(tmpNode);
				element.add(tmpNode);
			}
		}
	}
	
	/**
	 * 转换格式化xml文档为Node文档对象
	 */
	public static Node getXmlNode(String formatXml) {
		Document doc;
		try {
			doc = StudioUtil.parseXmlText(formatXml);
			return doc.node(0);
		} catch (DocumentException e) {
			e.printStackTrace();
		}
		return null;
	}
	
	/**
	 * 格式成： <?xml version="1.0" encoding="UTF-8"?><xml>......</xml>
	 */
	public static String formatXMLDocContent(String xml, int level) {
		String prefix1 = StringUtils.repeat(StudioConst.XML_PREFIX_STR, level - 1);
		StringBuffer sb = new StringBuffer(addPerfixBlank(xml.toString(), level));
		sb.insert(0, "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n<xml>\n");
		sb.append(prefix1).append("</xml>");
		return sb.toString();
	}

	/**
	 * XML特殊符号转换
	 */
	public static String getStringWithXML(String contentC) {
		String content = contentC;
		if (content == null) {
			return "";
		}
		content = content.replaceAll("&", "&amp;");
		content = content.replaceAll("<", "&lt;");
		content = content.replaceAll(">", "&gt;");
		content = content.replaceAll("\"", "&quot;");
		// content = content.replaceAll("\n\r", "&#10;");
		// content = content.replaceAll("\r\n", "&#10;");
		// content = content.replaceAll("\n", "&#10;");
		// content = content.replaceAll(" ", "&#032;");
		// content = content.replaceAll("'", "&#039;");
		// content = content.replaceAll("!", "&#033;");
		return content;
	}

	/**
	 * 内容是否是简单文本，即不包含<xxx></xxx> 或<xxx />HTML标示
	 */
	public static boolean isSimpleText(String content) {
		Matcher mat = StudioConst.XMLFLAG_BEGIN_PAT.matcher(content);
		boolean flag = mat.find();
		if (!flag) {
			mat = StudioConst.XMLFLAG_BEGIN_PAT1.matcher(content);
			flag = mat.find();
		}
		return !flag;
	}

	/**
	 * 获取结点在XML结构中处于第几层
	 */
	public static int getNodeLevel(Node node) {
		if (node == null) {
			return 0;
			// } else if ("mapper".equals(node.getName())) {
			// return 1;
		} else {
			return getNodeLevel(node.getParent()) + 1;
		}
	}

	/**
	 * 去除首尾空行 level: 缩进等级
	 */
	public static String delHeadTailBlankLine(String text) {
		StringBuffer sb = new StringBuffer(text);
		int i = 0, j = 0;
		while ((i = sb.indexOf("\n", j)) >= 0) {
			if (StringUtils.isBlank(sb.substring(0, i + 1))) {
				sb.delete(0, i + 1);
				j = 0;
			} else if (StringUtils.isBlank(sb.substring(i))) {
				sb.delete(i + 1, sb.length());
				break;
			} else {
				j = i + 1;
			}
		}
		return sb.toString();
	}
	
	/**
	 * 删除字符串首尾行
	 */
	public static String delFirstAndTailLine(String text) {
		StringBuffer sb = new StringBuffer(text.trim());
		int i = sb.indexOf("\n");
		sb.delete(0, i + 1);
		i = sb.lastIndexOf("\n");
		sb.delete(i + 1, sb.length());
		return sb.toString();
	}

	/**
	 * 去除字符串空行
	 */
	public static String delBlankLine(String s) {
		StringBuffer sb = new StringBuffer();
		StringReader sr = new StringReader(s);
		BufferedReader br = new BufferedReader(sr);
		String ss;
		try {
			while ((ss = br.readLine()) != null) {
				if (StringUtils.isNotBlank(ss)) {
					sb.append(ss).append("\n");
				}
			}
			sr.close();
			br.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return sb.toString();
	}

	/**
	 * 判断是否为配置节点
	 */
	public static boolean isConfigNode(Node node) {
		if (node.getNodeType() == Node.ELEMENT_NODE) {
			if (StudioConst.NODE_SQL.equals(node.getName())) {
				String id = ((Element) node).attributeValue("id", "");
				if (id.equals(StudioConst.MAP_ID_CFGNODE)) {
					return true;
				}
			}
		}
		if (!(node instanceof CDATA)) {
			return false;
		}
		CDATA cdata = (CDATA) node;
		StringReader sr = new StringReader(cdata.getText());
		BufferedReader br = new BufferedReader(sr);
		String cfgFlag;
		try {
			cfgFlag = StringUtils.trim(br.readLine());
		} catch (IOException e) {
			return false;
		}
		if (StudioConst.NODE_CDATA_CFG_FLAG.equals(cfgFlag)) {
			return true;
		} else {
			return false;
		}
	}

	/**
	 * 获取结点的ID
	 */
	public static String getNodeId(Element element) {
		return element.attributeValue("id");
	}

	/**
	 * 获取新的结点ID
	 */
	public static String getNewNodeId(Document doc, String prefix) {
		List<?> ls = doc.getRootElement().elements();
		String id = prefix;
		int k = 0;
		boolean flag = true;
		while (flag) {
			flag = false;
			for (int i = 0; i < ls.size(); i++) {
				Element element = (Element) (ls.get(i));
				String tmpid = element.attributeValue("id");
				if (id.equals(tmpid)) {
					id = prefix + "_" + (k++);
					flag = true;
					break;
				}
			}
		}
		return id;
	}

	/**
	 * 排序根节点下的节点内容根据名称()
	 */
	public static void sortRootContentByName(XMLMapperDocument doc, boolean asc) {
		List<Node> nodes = doc.getMapNodes();
		for (int i = 0; i < nodes.size() - 1; i++) {
			for (int j = 0; j < nodes.size() - (i + 1); j++) {
				Node node1 = nodes.get(j);
				Node node2 = nodes.get(j + 1);
				if (StudioUtil.isConfigNode(node1)) {
					continue;
				} else if (StudioUtil.isConfigNode(node2)) { // 配置接点排前
					nodes.remove(j + 1);
					nodes.add(j, node2);
					continue;
				}
				String id1 = ((Element) node1).attributeValue("id");
				String id2 = ((Element) node2).attributeValue("id");
				if (asc) {
					if (id1.compareToIgnoreCase(id2) > 0) {
						nodes.remove(j + 1);
						nodes.add(j, node2);
					}
				} else {
					if (id1.compareToIgnoreCase(id2) < 0) {
						nodes.remove(j + 1);
						nodes.add(j, node2);
					}
				}
			}
		}
		Element root = doc.getRootElement();
		int level = StudioUtil.getNodeLevel(root);
		root.clearContent();
		for (int i = 0; i < nodes.size(); i++) {
			root.addText("\n" + StringUtils.repeat(StudioConst.XML_PREFIX_STR, level));
			root.add(nodes.get(i));
		}
		root.addText("\n" + StringUtils.repeat(StudioConst.XML_PREFIX_STR, level));
		doc.parseData();
	}

	/**
	 * 排序根节点下的节点内容(根据类型)
	 */
	public static void sortRootContentByType(XMLMapperDocument doc, boolean asc) {
		List<Node> nodes = new ArrayList<Node>();
		if (doc.cfgNode != null) {
			nodes.add(doc.cfgNode);
		} else if (doc.cfgCDATA != null) {
			nodes.add(doc.cfgCDATA);
		}
		if (asc) {
			for (int i = 0; i < doc.resultMapNodes.size(); i++) {
				nodes.add(doc.resultMapNodes.get(i));
			}
			for (int i = 0; i < doc.parameterMapNodes.size(); i++) {
				nodes.add(doc.parameterMapNodes.get(i));
			}
			for (int i = 0; i < doc.sqlNodes.size(); i++) {
				if (StudioUtil.isConfigNode((Node) doc.sqlNodes.get(i))) {
					continue;
				}
				nodes.add(doc.sqlNodes.get(i));
			}
			for (int i = 0; i < doc.insertNodes.size(); i++) {
				nodes.add(doc.insertNodes.get(i));
			}
			for (int i = 0; i < doc.deleteNodes.size(); i++) {
				nodes.add(doc.deleteNodes.get(i));
			}
			for (int i = 0; i < doc.updateNodes.size(); i++) {
				nodes.add(doc.updateNodes.get(i));
			}
			for (int i = 0; i < doc.selectNodes.size(); i++) {
				nodes.add(doc.selectNodes.get(i));
			}
		} else {
			for (int i = 0; i < doc.selectNodes.size(); i++) {
				nodes.add(doc.selectNodes.get(i));
			}
			for (int i = 0; i < doc.updateNodes.size(); i++) {
				nodes.add(doc.updateNodes.get(i));
			}
			for (int i = 0; i < doc.deleteNodes.size(); i++) {
				nodes.add(doc.deleteNodes.get(i));
			}
			for (int i = 0; i < doc.insertNodes.size(); i++) {
				nodes.add(doc.insertNodes.get(i));
			}
			for (int i = 0; i < doc.sqlNodes.size(); i++) {
				if (StudioUtil.isConfigNode((Node) doc.sqlNodes.get(i))) {
					continue;
				}
				nodes.add(doc.sqlNodes.get(i));
			}
			for (int i = 0; i < doc.parameterMapNodes.size(); i++) {
				nodes.add(doc.parameterMapNodes.get(i));
			}
			for (int i = 0; i < doc.resultMapNodes.size(); i++) {
				nodes.add(doc.resultMapNodes.get(i));
			}
		}
		Element root = doc.getRootElement();
		int level = StudioUtil.getNodeLevel(root);
		root.clearContent();
		for (int i = 0; i < nodes.size(); i++) {
			root.addText("\n" + StringUtils.repeat(StudioConst.XML_PREFIX_STR, level));
			root.add(nodes.get(i));
		}
		root.addText("\n" + StringUtils.repeat(StudioConst.XML_PREFIX_STR, level));
		doc.parseData();
	}

	/**
	 * 解析XML(禁止使用DTD)
	 */
	public static Document parseXmlText(String text) throws DocumentException {
		Document result = null;
		SAXReader reader = new SAXReader();
		reader.setEntityResolver(new IgnoreDTDEntityResolver());
		// reader.setValidation(false);
		// reader.setIncludeInternalDTDDeclarations(false);
		// reader.setIncludeExternalDTDDeclarations(false);
		String encoding = getEncoding(text);
		InputSource source = new InputSource(new StringReader(text));
		source.setEncoding(encoding);
		result = reader.read(source);
		if (result.getXMLEncoding() == null) {
			result.setXMLEncoding(encoding);
		}
		return result;
	}

	public static String getEncoding(String text) {
		String result = null;
		String xml = text.trim();
		if (xml.startsWith("<?xml")) {
			int end = xml.indexOf("?>");
			String sub = xml.substring(0, end);
			StringTokenizer tokens = new StringTokenizer(sub, " =\"'");
			while (tokens.hasMoreTokens()) {
				String token = tokens.nextToken();
				if ("encoding".equals(token)) {
					if (!tokens.hasMoreTokens())
						break;
					result = tokens.nextToken();
					break;
				}
			}
		}
		return result;
	}

	/**
	 * 设置窗口位于屏幕中间
	 * 
	 * @param shell
	 *            要调整位置的窗口对象
	 */
	public static void center(Shell shell) {
		// 获取屏幕高度和宽度
		int screenH = Toolkit.getDefaultToolkit().getScreenSize().height;
		int screenW = Toolkit.getDefaultToolkit().getScreenSize().width;
		// 获取对象窗口高度和宽度
		int shellH = shell.getBounds().height;
		int shellW = shell.getBounds().width;
		// 如果对象窗口高度超出屏幕高度，则强制其与屏幕等高
		if (shellH > screenH)
			shellH = screenH;
		// 如果对象窗口宽度超出屏幕宽度，则强制其与屏幕等宽
		if (shellW > screenW)
			shellW = screenW;
		// 定位对象窗口坐标
		shell.setLocation(((screenW - shellW) / 2), ((screenH - shellH) / 2));
	}

	/**
	 * 设置窗口位于屏幕中间
	 * 
	 * @param display
	 *            设备
	 * @param shell
	 *            要调整位置的窗口对象
	 */
	public static void center(Display display, Shell shell) {
		Rectangle bounds = display.getPrimaryMonitor().getBounds();
		Rectangle rect = shell.getBounds();
		int x = bounds.x + (bounds.width - rect.width) / 2;
		int y = bounds.y + (bounds.height - rect.height) / 2;
		shell.setLocation(x, y);
	}

	/**
	 * 从窗口的编辑框或下拉框更新属性值
	 */
	public static void updateAttribute(Element node, String attrName, Object edit) {
		Attribute attr = node.attribute(attrName);
		String attrvalue = "";
		if (edit instanceof Text) {
			attrvalue = StringUtils.trim(((Text) edit).getText());
		} else if (edit instanceof Combo) {
			attrvalue = StringUtils.trim(((Combo) edit).getText());
		} else if (edit instanceof String) {
			attrvalue = (String) edit;
		}
		if ((attr != null) && (StringUtils.isNotBlank(attrvalue))) {
			attr.setValue(attrvalue);
		} else if ((attr != null) && (StringUtils.isBlank(attrvalue))) {
			node.remove(attr);
		} else if ((attr == null) && (StringUtils.isNotBlank(attrvalue))) {
			attr = DocumentHelper.createAttribute(node, attrName, attrvalue);
			node.add(attr);
		}
	}

	/**
	 * 绑定属性值到窗口上的编辑框或下拉框
	 */
	public static void bindAttributeData(Element node, String attrName, Object edit) {
		Attribute attr = node.attribute(attrName);
		if (attr != null) {
			String value = attr.getValue();
			if (edit instanceof Text) {
				((Text) edit).setText(value);
			} else if (edit instanceof Combo) {
				((Combo) edit).setText(value);
			}
		} else {
			if (edit instanceof Text) {
				((Text) edit).setText("");
			} else if (edit instanceof Combo) {
				((Combo) edit).setText("");
			}
		}
	}

	/**
	 * 创建物理文件
	 * 
	 * @throws Exception
	 */
	public static void createResourceFile(IFile file) throws Exception {
		List<IFolder> folders = new ArrayList<IFolder>();
		IContainer o = file.getParent();
		while (o != null) {
			if (o instanceof IFolder) {
				folders.add(0, (IFolder) o);
			}
			o = o.getParent();
		}
		for (int i = 0; i < folders.size(); i++) {
			IFolder folder = folders.get(i);
			if (!folder.exists()) {
				folder.create(true, true, null);
				folder.refreshLocal(0, null);
			}
		}
		file.refreshLocal(0, null);
		if (!file.exists()) {
			file.create(null, IResource.NONE, null);
			file.refreshLocal(0, null);
		}
	}

	/**
	 * 查找语句中的参数
	 */
	public static String[] findSqlParam(String sql) {
		List<String> paramlist = new ArrayList<String>();
		try {
			Matcher m = StudioConst.MAPPER_PARAM_PAT.matcher(sql);
			while (m.find()) {
				String param = sql.substring(m.start(), m.end());
				paramlist.add(param);
			}
		} catch (Exception e1) {
			e1.printStackTrace();
		}
		String[] params = paramlist.toArray(new String[] {});
		return params;
	}

	/**
	 * 获取结果映射列表
	 */
	public static Field[] getResultMapFields(Element resultmap) {
		String identitycolumn = "";
		if (resultmap.getDocument() instanceof XMLMapperDocument) {
			ConfigCDATA cfg = StudioUtil.getCDATAConfig((XMLMapperDocument)resultmap.getDocument());
			identitycolumn = cfg.getIdentitycolumn();
		}
		List<Field> fieldlist = new ArrayList<Field>();
		List<Element> es = resultmap.elements();
		for (int j = 0; j < es.size(); j++) {
			Field field = new Field();
			Element elem = es.get(j);
			String columnname = elem.attributeValue("column", "");
			String jdbcType = elem.attributeValue("jdbcType", "");
			boolean primaryKey = "id".equals(elem.getName());
			boolean identity = columnname.equals(identitycolumn);
			field.setPrimaryKey(primaryKey);
			field.setIdentity(identity);
			field.setDataType(DbUtil.getJdbcTypeByName(jdbcType));
			field.setColumnName(columnname);
			DbUtil.fillField(field);
			field.setProperty(elem.attributeValue("property", ""));
			fieldlist.add(field);
		}
		return fieldlist.toArray(new Field[] {});
	}

	/**
	 * 获取结果映射主键列表
	 */
	public static Field[] getResultMapKeyFields(Element resultmap) {
		String identitycolumn = "";
		if (resultmap.getDocument() instanceof XMLMapperDocument) {
			ConfigCDATA cfg = StudioUtil.getCDATAConfig((XMLMapperDocument)resultmap.getDocument());
			identitycolumn = cfg.getIdentitycolumn();
		}
		List<Field> fieldlist = new ArrayList<Field>();
		List<Element> es = resultmap.elements();
		for (int j = 0; j < es.size(); j++) {
			Field field = new Field();
			Element elem = es.get(j);
			String columnname = elem.attributeValue("column", "");
			String jdbcType = elem.attributeValue("jdbcType", "");
			boolean primaryKey = "id".equals(elem.getName());
			boolean identity = columnname.equals(identitycolumn);
			field.setPrimaryKey(primaryKey);
			field.setIdentity(identity);
			field.setDataType(DbUtil.getJdbcTypeByName(jdbcType));
			field.setColumnName(columnname);
			DbUtil.fillField(field);
			field.setProperty(elem.attributeValue("property", ""));
			if (field.isPrimaryKey())
				fieldlist.add(field);
		}
		return fieldlist.toArray(new Field[] {});
	}

	/**
	 * 写文件
	 * 
	 * @throws Exception
	 */
	public static void writeFile(IFile file, String text) throws Exception {
		StudioUtil.createResourceFile(file);
		File f = new File(file.getLocation().toString());
		if (!f.exists()) {
			f.createNewFile();
		}
		OutputStreamWriter write = new OutputStreamWriter(new FileOutputStream(f), "UTF-8");
		write.write(text);
		write.close();
		file.refreshLocal(0, null);
	}
	
	/**
	 * 获取更新配置结点内容
	 */
	public static void updateCDATAConfig(XMLMapperDocument doc, ConfigCDATA cfg) {
		String perfix = StringUtils.repeat(StudioConst.XML_PREFIX_STR, StudioUtil.getNodeLevel(doc.cfgNode));
		StringBuffer sb = new StringBuffer();
		if (StringUtils.isNotEmpty(cfg.getDatasource())) {
			sb.append(perfix).append("datasource=").append(cfg.getDatasource()).append("\n");
		}
		if (StringUtils.isNotEmpty(cfg.getTablename())) {
			sb.append(perfix).append("tablename=").append(cfg.getTablename()).append("\n");
		}
		if (StringUtils.isNotEmpty(cfg.getIdentitycolumn())) {
			sb.append(perfix).append("identitycolumn=").append(cfg.getIdentitycolumn()).append("\n");
		}
		if (StringUtils.isNotEmpty(cfg.getInterclass())) {
			sb.append(perfix).append("interclass=").append(cfg.getInterclass()).append("\n");
		}
		if (StringUtils.isNotEmpty(cfg.getDaoclass())) {
			sb.append(perfix).append("daoclass=").append(cfg.getDaoclass()).append("\n");
		}
		if (StringUtils.isNotEmpty(cfg.getBeanclass())) {
			sb.append(perfix).append("beanclass=").append(cfg.getBeanclass()).append("\n");
		}
		sb.append(perfix);
		
		String prefix = StringUtils.repeat(StudioConst.XML_PREFIX_STR,  StudioUtil.getNodeLevel(doc.cfgNode));
		String parPrefix = StringUtils.repeat(StudioConst.XML_PREFIX_STR,  StudioUtil.getNodeLevel(doc.cfgNode) - 1);
		CDATA cdata = DocumentHelper.createCDATA(" " + StudioConst.NODE_CDATA_CFG_FLAG + "\n" + prefix + "\n" + sb.toString());
		Element element = (Element)doc.cfgNode;
		List<CharacterData> lsCont = element.content();
		lsCont.clear();
		lsCont.add(DocumentHelper.createText("\n" + prefix));
		lsCont.add(cdata);
		lsCont.add(DocumentHelper.createText("\n" + parPrefix));
		doc.cfgCDATA = cdata;
		doc.getRootElement().attribute("namespace").setText(StringUtils.trim(cfg.getNamespace()));
	}
	
	/**
	 * 获取XML配置结点内容
	 */
	public static ConfigCDATA getCDATAConfig(XMLMapperDocument doc) {
		return getCDATAConfig(doc.cfgNode);
	}
	
	/**
	 * 获取XML配置结点内容
	 */
	public static ConfigCDATA getCDATAConfig(Node cfgNode) {
		ConfigCDATA cd = new ConfigCDATA();
		if (!StudioUtil.isConfigNode(cfgNode)) {
			return null;
		}
		String namespace = cfgNode.getDocument().getRootElement().attribute("namespace").getValue();
		cd.setNamespace(namespace);
		CDATA cdata = null;
		if (cfgNode.getNodeType() == Node.CDATA_SECTION_NODE) {
			cdata = (CDATA)cfgNode;
		} else if (cfgNode.getNodeType() == Node.ELEMENT_NODE){
			Element element = (Element)cfgNode;
			for (Iterator<?> i = element.nodeIterator(); i.hasNext();) {
				Node node1 = (Node)i.next();
				if (node1.getNodeType() == Node.CDATA_SECTION_NODE) {
					cdata = (CDATA)node1;
				} 
			}
		}
		if (cdata == null) {
			return null;
		}
		try {
			StringReader sr = new StringReader(cdata.getText());
			BufferedReader br = new BufferedReader(sr);
			String cfgline;
			while ((cfgline = StringUtils.trim(br.readLine())) != null) {
				if (StringUtils.isBlank(cfgline) || StudioConst.NODE_CDATA_CFG_FLAG.equals(cfgline)) {
					continue;
				}
				String[] ss = cfgline.split("=");
				if (ss.length == 2) {
					String field = StringUtils.trim(ss[0]);
					String value = StringUtils.trim(ss[1]);
					if ("daoclass".equals(field)) {
						cd.setDaoclass(value);
					} else if ("beanclass".equals(field)) {
						cd.setBeanclass(value);
					} else if ("tablename".equals(field)) {
						cd.setTablename(value);
					} else if ("interclass".equals(field)) {
						cd.setInterclass(value);
					} else if ("datasource".equals(field)) {
						cd.setDatasource(value);
					} else if ("identitycolumn".equals(field)) {
						cd.setIdentitycolumn(value);
					}
				}
			}
			sr.close();
			br.close();
		} catch (IOException e) {
			log.error(e.getMessage(), e);
		}
		return cd;
	}
}
