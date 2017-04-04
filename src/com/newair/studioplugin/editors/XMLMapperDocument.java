package com.newair.studioplugin.editors;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.StringReader;
import java.io.Writer;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.dom4j.Branch;
import org.dom4j.CDATA;
import org.dom4j.Comment;
import org.dom4j.Document;
import org.dom4j.DocumentType;
import org.dom4j.Element;
import org.dom4j.InvalidXPathException;
import org.dom4j.Node;
import org.dom4j.ProcessingInstruction;
import org.dom4j.QName;
import org.dom4j.Visitor;
import org.dom4j.XPath;
import org.xml.sax.EntityResolver;

import com.newair.studioplugin.StudioConst;

public class XMLMapperDocument implements Document {

	private Document document;

	public String tableName;  //表名称
	public String interClass; //接口类名称
	public String namespace; //命名空间
	public String datasource;  //数据源名称
	
	public CDATA cfgCDATA; // 配置域
	public Node cfgNode;  //配置结点
	
	public Element BaseResultMap;
	public Element ResultMapWithBlob;
	public Element BaseColumnList;
	public Element BlobColumnList;
	public Element AdapterWhereCondition;
	public Element AdapterUpdateWhereCondition;
	public Element selectByAdapter;
	public Element selectByAdapterWithBlob;
	public Element selectByPrimaryKey;
	public Element deleteByAdapter;
	public Element deleteByPrimaryKey;
	public Element insert;
	public Element insertSelective;
	public Element countByAdapter;
	public Element updateByAdapter;
	public Element updateByAdapterWithBlob;
	public Element updateByAdapterSelective;
	public Element updateByPrimaryKey;
	public Element updateByPrimaryKeyWithBlob;
	public Element updateByPrimaryKeySelective;
	
	public ArrayList<Element> resultMapNodes = new ArrayList<Element>(); // 字段映射域
	public ArrayList<Element> parameterMapNodes = new ArrayList<Element>(); // 参数Map域
	public ArrayList<Element> sqlNodes = new ArrayList<Element>(); // SQL域
	public ArrayList<Element> selectNodes = new ArrayList<Element>(); // SQL查询语句域
	public ArrayList<Element> insertNodes = new ArrayList<Element>(); // SQL插入语句域
	public ArrayList<Element> updateNodes = new ArrayList<Element>(); // SQL更新语句域
	public ArrayList<Element> deleteNodes = new ArrayList<Element>(); // SQL删除语句域
	public ArrayList<Node> allNods = new ArrayList<Node>(); //所有结点

	public XMLMapperDocument(Document document) {
		this.document = document;
		this.document.setXMLEncoding("UTF-8");
		parseData();
	}

	public Object clone() {
		if (isReadOnly())
			return this;
		try {
			Document clone = (Document)document.clone();
			return new XMLMapperDocument(clone);
		} catch (Exception e) {
			throw new RuntimeException("This should never happen. Caught: " + e);
		}
	}

	public Document addComment(String paramString) {
		return document.addComment(paramString);
	}

	public Document addDocType(String paramString1, String paramString2, String paramString3) {
		return document.addDocType(paramString1, paramString2, paramString3);
	}

	public Document addProcessingInstruction(String paramString1, String paramString2) {
		return document.addProcessingInstruction(paramString1, paramString2);
	}

	public Document addProcessingInstruction(String paramString, Map paramMap) {
		return document.addProcessingInstruction(paramString, paramMap);
	}

	public DocumentType getDocType() {
		return document.getDocType();
	}

	public EntityResolver getEntityResolver() {
		return document.getEntityResolver();
	}

	public Element getRootElement() {
		return document.getRootElement();
	}

	public String getXMLEncoding() {
		return document.getXMLEncoding();
	}

	public void setDocType(DocumentType paramDocumentType) {
		document.setDocType(paramDocumentType);
	}

	public void setEntityResolver(EntityResolver paramEntityResolver) {
		document.setEntityResolver(paramEntityResolver);
	}

	public void setRootElement(Element paramElement) {
		document.setRootElement(paramElement);
	}

	public void setXMLEncoding(String paramString) {
		document.setXMLEncoding(paramString);
	}

	public void add(Node paramNode) {
		document.add(paramNode);
		parseData();
	}

	public void add(Comment paramComment) {
		document.add(paramComment);
		parseData();
	}

	public void add(Element paramElement) {
		document.add(paramElement);
		parseData();
	}

	public void add(ProcessingInstruction paramProcessingInstruction) {
		document.add(paramProcessingInstruction);
		parseData();
	}

	public Element addElement(String paramString) {
		Element element = document.addElement(paramString);
		parseData();
		return element;
	}

	public Element addElement(QName paramQName) {
		Element element = document.addElement(paramQName);
		parseData();
		return element;
	}

	public Element addElement(String paramString1, String paramString2) {
		Element element = document.addElement(paramString1, paramString2);
		parseData();
		return element;
	}

	public void appendContent(Branch paramBranch) {
		document.appendContent(paramBranch);
		parseData();
	}

	public void clearContent() {
		document.clearContent();
		parseData();
	}

	public List<?> content() {
		return document.content();
	}

	public Element elementByID(String paramString) {
		return document.elementByID(paramString);
	}

	public int indexOf(Node paramNode) {
		return document.indexOf(paramNode);
	}

	public Node node(int paramInt) throws IndexOutOfBoundsException {
		return document.node(paramInt);
	}

	public int nodeCount() {
		return document.nodeCount();
	}

	public Iterator<?> nodeIterator() {
		return document.nodeIterator();
	}

	public void normalize() {
		document.normalize();
	}

	public ProcessingInstruction processingInstruction(String paramString) {
		return document.processingInstruction(paramString);
	}

	public List<?> processingInstructions() {
		return document.processingInstructions();
	}

	public List<?> processingInstructions(String paramString) {
		return document.processingInstructions(paramString);
	}

	public boolean remove(Node paramNode) {
		boolean result = document.remove(paramNode);
		parseData();
		return result;
	}

	public boolean remove(Comment paramComment) {
		boolean result = document.remove(paramComment);
		parseData();
		return result;
	}

	public boolean remove(Element paramElement) {
		boolean result = document.remove(paramElement);
		parseData();
		return result;
	}

	public boolean remove(ProcessingInstruction paramProcessingInstruction) {
		boolean result = document.remove(paramProcessingInstruction);
		parseData();
		return result;
	}

	public boolean removeProcessingInstruction(String paramString) {
		boolean result = document.removeProcessingInstruction(paramString);
		parseData();
		return result;
	}

	public void setContent(List paramList) {
		document.setContent(paramList);
		parseData();
	}

	public void setProcessingInstructions(List paramList) {
		document.setProcessingInstructions(paramList);
		parseData();
	}

	public void accept(Visitor paramVisitor) {
		document.accept(paramVisitor);
	}

	public String asXML() {
		return document.asXML();
	}

	public Node asXPathResult(Element paramElement) {
		return document.asXPathResult(paramElement);
	}

	public XPath createXPath(String paramString) throws InvalidXPathException {
		return document.createXPath(paramString);
	}

	public Node detach() {
		return document.detach();
	}

	public Document getDocument() {
		return document.getDocument();
	}

	public String getName() {
		return document.getName();
	}

	public short getNodeType() {
		return document.getNodeType();
	}

	public String getNodeTypeName() {
		return document.getNodeTypeName();
	}

	public Element getParent() {
		return document.getParent();
	}

	public String getPath() {
		return document.getPath();
	}

	public String getPath(Element paramElement) {
		return document.getPath(paramElement);
	}

	public String getStringValue() {
		return document.getStringValue();
	}

	public String getText() {
		return document.getText();
	}

	public String getUniquePath() {
		return document.getUniquePath();
	}

	public String getUniquePath(Element paramElement) {
		return document.getUniquePath(paramElement);
	}

	public boolean hasContent() {
		return document.hasContent();
	}

	public boolean isReadOnly() {
		return document.isReadOnly();
	}

	public boolean matches(String paramString) {
		return document.matches(paramString);
	}

	public Number numberValueOf(String paramString) {
		return document.numberValueOf(paramString);
	}

	public List<?> selectNodes(String paramString) {
		return document.selectNodes(paramString);
	}

	public List<?> selectNodes(String paramString1, String paramString2) {
		return document.selectNodes(paramString1, paramString2);
	}

	public List<?> selectNodes(String paramString1, String paramString2, boolean paramBoolean) {
		return document.selectNodes(paramString1, paramString2, paramBoolean);
	}

	public Object selectObject(String paramString) {
		return document.selectNodes(paramString);
	}

	public Node selectSingleNode(String paramString) {
		return document.selectSingleNode(paramString);
	}

	public void setDocument(Document paramDocument) {
		document.setDocument(paramDocument);
	}

	public void setName(String paramString) {
		document.setName(paramString);
	}

	public void setParent(Element paramElement) {
		document.setParent(paramElement);
	}

	public void setText(String paramString) {
		document.setText(paramString);
	}

	public boolean supportsParent() {
		return document.supportsParent();
	}

	public String valueOf(String paramString) {
		return document.valueOf(paramString);
	}

	public void write(Writer paramWriter) throws IOException {
		document.write(paramWriter);
	}
	
	/**
	 * 获取所有结点
	 */
	public List<Node> getMapNodes() {
		return allNods;
	}
	
	/**
	 * 查找映射结点
	 */
	public Element findNodeByName(String id) {
		for (int i = 0; i < allNods.size(); i++) {
			if (allNods.get(i) instanceof Element) {
				String name = ((Element)allNods.get(i)).attributeValue("id", "");
				if (name.equals(id)) {
					return (Element)allNods.get(i);
				}
			}
		}
		return null;
	}

	/**
	 * 初始化结点
	 */
	public void parseData() {
		tableName = null;
		datasource = null;
		interClass = null;
		namespace = null;
		cfgCDATA = null;
		cfgNode = null;
		BaseResultMap = null;
		ResultMapWithBlob = null;
		BaseColumnList = null;
		BlobColumnList = null;
		AdapterWhereCondition = null;
		AdapterUpdateWhereCondition = null;
		selectByAdapter = null;
		selectByAdapterWithBlob = null;
		selectByPrimaryKey = null;
		deleteByAdapter = null;
		deleteByPrimaryKey = null;
		insert = null;
		insertSelective = null;
		countByAdapter = null;
		updateByAdapter = null;
		updateByAdapterWithBlob = null;
		updateByAdapterSelective = null;
		updateByPrimaryKey = null;
		updateByPrimaryKeyWithBlob = null;
		updateByPrimaryKeySelective = null;
		try {
			clearNodes();
			Element root = document.getRootElement();
			namespace = root.attributeValue("namespace", "");
			for (Iterator<?> i = root.nodeIterator(); i.hasNext();) {
				Node node = (Node)i.next();
				switch (node.getNodeType()) {
					case Node.CDATA_SECTION_NODE:  //兼容以前的，暂时保留，今后配置都放在一个sql配置结点，防止DTD验证不过
						CDATA cdata = (CDATA)node;
						StringReader sr = new StringReader(cdata.getText());
						BufferedReader br = new BufferedReader(sr);
						String cfgFlag = StringUtils.trim(br.readLine());
						if (StudioConst.NODE_CDATA_CFG_FLAG.equals(cfgFlag)) {
							cfgCDATA = cdata;
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
										//
									} else if ("beanclass".equals(field)) {
										//
									} else if ("tablename".equals(field)) {
										tableName = value;
									} else if ("interclass".equals(field)) {
										interClass = value;
									} else if ("datasource".equals(field)) {
										datasource = value;
									}
								}
							}
						}
						allNods.add(node);
						break;
					case Node.ELEMENT_NODE:
						if (StudioConst.NODE_SELECT.equals(node.getName())) {
							selectNodes.add((Element)node);
							String id = ((Element)node).attributeValue("id", "");
							if (id.equals(StudioConst.MAP_ID_countByAdapter))
								countByAdapter = (Element)node;
							if (id.equals(StudioConst.MAP_ID_selectByAdapter))
								selectByAdapter = (Element)node;
							if (id.equals(StudioConst.MAP_ID_selectByAdapterWithBlob))
								selectByAdapterWithBlob = (Element)node;
							if (id.equals(StudioConst.MAP_ID_selectByPrimaryKey))
								selectByPrimaryKey = (Element)node;
						} else if (StudioConst.NODE_INSERT.equals(node.getName())) {
							insertNodes.add((Element)node);
							String id = ((Element)node).attributeValue("id", "");
							if (id.equals(StudioConst.MAP_ID_insert))
								insert = (Element)node;
							if (id.equals(StudioConst.MAP_ID_insertSelective))
								insertSelective = (Element)node;
						} else if (StudioConst.NODE_UPDATE.equals(node.getName())) {
							updateNodes.add((Element)node);
							String id = ((Element)node).attributeValue("id", "");
							if (id.equals(StudioConst.MAP_ID_updateByAdapter))
								updateByAdapter = (Element)node;
							if (id.equals(StudioConst.MAP_ID_updateByAdapterWithBlob))
								updateByAdapterWithBlob = (Element)node;
							if (id.equals(StudioConst.MAP_ID_updateByAdapterSelective))
								updateByAdapterSelective = (Element)node;
							if (id.equals(StudioConst.MAP_ID_updateByPrimaryKey))
								updateByPrimaryKey = (Element)node;
							if (id.equals(StudioConst.MAP_ID_updateByPrimaryKeyWithBlob))
								updateByPrimaryKeyWithBlob = (Element)node;
							if (id.equals(StudioConst.MAP_ID_updateByPrimaryKeySelective))
								updateByPrimaryKeySelective = (Element)node;
						} else if (StudioConst.NODE_DELETE.equals(node.getName())) {
							deleteNodes.add((Element)node);
							String id = ((Element)node).attributeValue("id", "");
							if (id.equals(StudioConst.MAP_ID_deleteByAdapter))
								deleteByAdapter = (Element)node;
							if (id.equals(StudioConst.MAP_ID_deleteByPrimaryKey))
								deleteByPrimaryKey = (Element)node;
						} else if (StudioConst.NODE_SQL.equals(node.getName())) {
							sqlNodes.add((Element)node);
							String id = ((Element)node).attributeValue("id", "");
							if (id.equals(StudioConst.MAP_ID_AdapterUpdateWhereCondition))
								AdapterUpdateWhereCondition = (Element)node;
							if (id.equals(StudioConst.MAP_ID_AdapterWhereCondition))
								AdapterWhereCondition = (Element)node;
							if (id.equals(StudioConst.MAP_ID_BaseColumnList))
								BaseColumnList = (Element)node;
							if (id.equals(StudioConst.MAP_ID_BlobColumnList))
								BlobColumnList = (Element)node;
							if (id.equals(StudioConst.MAP_ID_CFGNODE)) {
								cfgNode = (Element)node;
							} 
						} else if (StudioConst.NODE_RESULT_MAP.equals(node.getName())) {
							resultMapNodes.add((Element)node);
							String id = ((Element)node).attributeValue("id", "");
							if (id.equals(StudioConst.MAP_ID_BaseResultMap))
								BaseResultMap = (Element)node;
							if (id.equals(StudioConst.MAP_ID_ResultMapWithBlob))
								ResultMapWithBlob = (Element)node;
						} else if (StudioConst.NODE_PARAMETER_MAP.equals(node.getName())) {
							parameterMapNodes.add((Element)node);
						} 
						allNods.add(node);
						break;
					default:
						break;
				}
			}
			if (cfgNode != null) {
				for (Iterator<?> i = ((Element)cfgNode).nodeIterator(); i.hasNext();) {
					Node node = (Node)i.next();
					if (node.getNodeType() == Node.CDATA_SECTION_NODE) {
						CDATA cdata = (CDATA)node;
						StringReader sr = new StringReader(cdata.getText());
						BufferedReader br = new BufferedReader(sr);
						String cfgFlag = StringUtils.trim(br.readLine());
						if (StudioConst.NODE_CDATA_CFG_FLAG.equals(cfgFlag)) {
							cfgCDATA = cdata;
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
										//
									} else if ("beanclass".equals(field)) {
										//
									} else if ("tablename".equals(field)) {
										tableName = value;
									} else if ("interclass".equals(field)) {
										interClass = value;
									} else if ("datasource".equals(field)) {
										datasource = value;
									}
								}
							}
						}
					}
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public String getBeanClass() {
		try { 
			StringReader sr = new StringReader(this.cfgCDATA.getText());
			BufferedReader br = new BufferedReader(sr);
			String cfgline;
			while ((cfgline = StringUtils.trim(br.readLine())) != null) {
				if (StringUtils.isEmpty(cfgline) || StudioConst.NODE_CDATA_CFG_FLAG.equals(cfgline)) {
					continue;
				}
				String[] ss = cfgline.split("=");
				if (ss.length == 2) {
					String field = StringUtils.trim(ss[0]);
					String value = StringUtils.trim(ss[1]);
					if ("beanclass".equals(field)) {
						return value;
					}
				}
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		return null;
	}
	
	public String getDaoClass() {
		try { 
			StringReader sr = new StringReader(this.cfgCDATA.getText());
			BufferedReader br = new BufferedReader(sr);
			String cfgline;
			while ((cfgline = StringUtils.trim(br.readLine())) != null) {
				if (StringUtils.isEmpty(cfgline) || StudioConst.NODE_CDATA_CFG_FLAG.equals(cfgline)) {
					continue;
				}
				String[] ss = cfgline.split("=");
				if (ss.length == 2) {
					String field = StringUtils.trim(ss[0]);
					String value = StringUtils.trim(ss[1]);
					if ("daoclass".equals(field)) {
						return value;
					}
				}
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		return null;
	}
	
	public String getTableName() {
		try { 
			StringReader sr = new StringReader(this.cfgCDATA.getText());
			BufferedReader br = new BufferedReader(sr);
			String cfgline;
			while ((cfgline = StringUtils.trim(br.readLine())) != null) {
				if (StringUtils.isEmpty(cfgline) || StudioConst.NODE_CDATA_CFG_FLAG.equals(cfgline)) {
					continue;
				}
				String[] ss = cfgline.split("=");
				if (ss.length == 2) {
					String field = StringUtils.trim(ss[0]);
					String value = StringUtils.trim(ss[1]);
					if ("tablename".equals(field)) {
						return value;
					}
				}
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		return null;
	}
	
	public String getSourceName() {
		try { 
			StringReader sr = new StringReader(this.cfgCDATA.getText());
			BufferedReader br = new BufferedReader(sr);
			String cfgline;
			while ((cfgline = StringUtils.trim(br.readLine())) != null) {
				if (StringUtils.isEmpty(cfgline) || StudioConst.NODE_CDATA_CFG_FLAG.equals(cfgline)) {
					continue;
				}
				String[] ss = cfgline.split("=");
				if (ss.length == 2) {
					String field = StringUtils.trim(ss[0]);
					String value = StringUtils.trim(ss[1]);
					if ("datasource".equals(field)) {
						return value;
					}
				}
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		return null;
	}
	
	public String getInterClass() {
		try { 
			StringReader sr = new StringReader(this.cfgCDATA.getText());
			BufferedReader br = new BufferedReader(sr);
			String cfgline;
			while ((cfgline = StringUtils.trim(br.readLine())) != null) {
				if (StringUtils.isEmpty(cfgline) || StudioConst.NODE_CDATA_CFG_FLAG.equals(cfgline)) {
					continue;
				}
				String[] ss = cfgline.split("=");
				if (ss.length == 2) {
					String field = StringUtils.trim(ss[0]);
					String value = StringUtils.trim(ss[1]);
					if ("interclass".equals(field)) {
						return value;
					}
				}
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		return null;
	}
	
	/**
	 * 清除结点列表对象
	 */
	private void clearNodes() {
		tableName = null;
		interClass = null;
		namespace = null;
		cfgCDATA = null;
		BaseResultMap = null;
		ResultMapWithBlob = null;
		BaseColumnList = null;
		BlobColumnList = null;
		AdapterWhereCondition = null;
		AdapterUpdateWhereCondition = null;
		selectByAdapter = null;
		selectByAdapterWithBlob = null;
		selectByPrimaryKey = null;
		deleteByAdapter = null;
		deleteByPrimaryKey = null;
		insert = null;
		insertSelective = null;
		countByAdapter = null;
		updateByAdapter = null;
		updateByAdapterWithBlob = null;
		updateByAdapterSelective = null;
		updateByPrimaryKey = null;
		updateByPrimaryKeyWithBlob = null;
		updateByPrimaryKeySelective = null;
		
		resultMapNodes.clear();
		parameterMapNodes.clear();
		sqlNodes.clear();
		selectNodes.clear();
		insertNodes.clear();
		updateNodes.clear();
		deleteNodes.clear();
		allNods.clear();
	}
}
