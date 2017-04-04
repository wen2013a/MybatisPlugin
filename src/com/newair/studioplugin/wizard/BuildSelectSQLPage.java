package com.newair.studioplugin.wizard;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;

import org.apache.commons.lang.StringUtils;
import org.dom4j.DocumentException;
import org.dom4j.Element;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.IProject;
import org.eclipse.jface.text.DefaultUndoManager;
import org.eclipse.jface.text.Document;
import org.eclipse.jface.text.IUndoManager;
import org.eclipse.jface.text.source.IVerticalRuler;
import org.eclipse.jface.text.source.SourceViewer;
import org.eclipse.jface.viewers.ComboViewer;
import org.eclipse.jface.viewers.IStructuredContentProvider;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.StyledText;
import org.eclipse.swt.events.FocusAdapter;
import org.eclipse.swt.events.FocusEvent;
import org.eclipse.swt.events.MouseAdapter;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.layout.FormAttachment;
import org.eclipse.swt.layout.FormData;
import org.eclipse.swt.layout.FormLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.MenuItem;
import org.eclipse.swt.widgets.MessageBox;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.swt.widgets.TableItem;
import org.eclipse.swt.widgets.Text;

import com.newair.studioplugin.ProjectConfig;
import com.newair.studioplugin.StudioConst;
import com.newair.studioplugin.StudioUtil;
import com.newair.studioplugin.db.DbUtil;
import com.newair.studioplugin.db.Field;
import com.newair.studioplugin.db.IDoResult;
import com.newair.studioplugin.editors.XMLMapperDocument;
import com.newair.studioplugin.editors.syntextcolor.TextEditorConfiguration;

public class BuildSelectSQLPage extends WizardPage {
	private Text txtMethodName;
	private Table tbQuery;
	private Combo cbParameterType;
	private StyledText sqlText;
	private SourceViewer sqlTextViewer;

	private Menu menu;
	private List<TableColumn> cloumns = new ArrayList<TableColumn>();
	private Combo resultMapName;
	private ComboViewer resultMapViewer;
	
	private List<Field> queryFieldList = null;
	private List<String> resultTypeList = new ArrayList<String>();
	private String oldResutlName = "";

	protected BuildSelectSQLPage() {
		super("BuildSelectSQLPage");
		setTitle("SQL语句");
		setDescription("编辑SQL查询语句");
		setPageComplete(false);

		resultTypeList.add("java.lang.String");
		resultTypeList.add("java.lang.Boolean");
		resultTypeList.add("java.lang.Short");
		resultTypeList.add("java.lang.Integer");
		resultTypeList.add("java.lang.Long");
		resultTypeList.add("java.lang.Float");
		resultTypeList.add("java.lang.Double");
		resultTypeList.add("java.math.BigDecimal");
		resultTypeList.add("java.sql.Timestamp");
		resultTypeList.add("java.util.Date");
		resultTypeList.add("java.util.HashMap");
		resultTypeList.add("java.lang.Object");
		resultTypeList.add("byte[]");
	}

	public void createControl(Composite parent) {
		Composite container = new Composite(parent, SWT.NULL);
		setControl(container);
		container.setLayout(new FormLayout());

		Label label1 = new Label(container, SWT.NONE);
		FormData fd_label1 = new FormData();
		fd_label1.top = new FormAttachment(0, 10);
		fd_label1.left = new FormAttachment(0, 10);
		label1.setLayoutData(fd_label1);
		label1.setText("查询名称：");

		txtMethodName = new Text(container, SWT.BORDER);
		txtMethodName.addFocusListener(new TxtMethodNameFocusAdapter());
		FormData fd_txtMethodName = new FormData();
		fd_txtMethodName.top = new FormAttachment(0, 7);
		fd_txtMethodName.left = new FormAttachment(label1);
		fd_txtMethodName.right = new FormAttachment(label1, 165, SWT.RIGHT);
		txtMethodName.setLayoutData(fd_txtMethodName);

		Label label3 = new Label(container, SWT.NONE);
		FormData fd_label3 = new FormData();
		fd_label3.top = new FormAttachment(label1, 0, SWT.TOP);
		fd_label3.left = new FormAttachment(txtMethodName, 5, SWT.RIGHT);
		label3.setLayoutData(fd_label3);
		label3.setText("传入参数：");

		cbParameterType = new Combo(container, SWT.NONE);
		FormData fd_cbParameterType = new FormData();
		fd_cbParameterType.left = new FormAttachment(label3);
		fd_cbParameterType.top = new FormAttachment(0, 7);
		fd_cbParameterType.right = new FormAttachment(65, 0);
		cbParameterType.setLayoutData(fd_cbParameterType);
		cbParameterType.setItems(new String[] {"", "java.lang.String", "java.lang.Boolean", "java.lang.Short", "java.lang.Integer", "java.lang.Long", "java.lang.Float", "java.lang.Double", "java.math.BigDecimal", "java.sql.Timestamp", "java.sql.Date", "java.sql.Time", "java.util.HashMap"});

		Label label4 = new Label(container, SWT.NONE);
		FormData fd_label4 = new FormData();
		fd_label4.top = new FormAttachment(label1, 0, SWT.TOP);
		fd_label4.left = new FormAttachment(cbParameterType, 5);
		label4.setLayoutData(fd_label4);
		label4.setText("返回结果：");
		
		resultMapViewer = new ComboViewer(container, SWT.BORDER);
		resultMapName = resultMapViewer.getCombo();
		resultMapName.addFocusListener(new FocusAdapter() {
			public void focusLost(FocusEvent e) {
				resultMapChanged();
			}
		});
		resultMapName.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				resultMapChanged();
			}
		});
		FormData fd_resultMap = new FormData();
		fd_resultMap.left = new FormAttachment(label4, 2);
		fd_resultMap.right = new FormAttachment(100, -24);  
		fd_resultMap.top = new FormAttachment(cbParameterType, 0, SWT.TOP);
		resultMapName.setLayoutData(fd_resultMap);

		sqlTextViewer = new SourceViewer(container, (IVerticalRuler)null, 768);
		sqlTextViewer.setEditable(true);
		sqlText = sqlTextViewer.getTextWidget();
		sqlText.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseUp(MouseEvent e) {
				if (e.button == 0) {
					sqlText.setLocation(e.display.getCursorLocation());
					menu.setLocation(e.display.getCursorLocation());
					menu.setVisible(true);
				}
			}
		});
		FormData fd_sqlText = new FormData();
		fd_sqlText.left = new FormAttachment(0, 10);
		fd_sqlText.right = new FormAttachment(100, -10);
		fd_sqlText.top = new FormAttachment(0, 35);
		fd_sqlText.bottom = new FormAttachment(100, -200);
		sqlText.setLayoutData(fd_sqlText);

		Button butPerform = new Button(container, SWT.NONE);
		butPerform.addMouseListener(new ButPerformMouseAdapter());
		FormData fd_butPerform = new FormData();
		fd_butPerform.top = new FormAttachment(sqlText, 6);
		fd_butPerform.left = new FormAttachment(0, 10);
		fd_butPerform.right = new FormAttachment(0, 80);

		menu = new Menu(sqlText);
		sqlText.setMenu(menu);

		MenuItem menuItem = new MenuItem(menu, SWT.NONE);
		menuItem.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				InputParamDialog dialog = new InputParamDialog(getShell(), SWT.ICON_INFORMATION | SWT.YES | SWT.NO);
				String result = (String)dialog.open();
				if (StringUtils.isNotBlank(result)) {
					sqlText.insert(result);
				}
			}
		});
		menuItem.setText("插入参数");
		butPerform.setLayoutData(fd_butPerform);
		butPerform.setText("执行");

		tbQuery = new Table(container, SWT.BORDER | SWT.FULL_SELECTION);
		FormData fd_tbQuery = new FormData();
		fd_tbQuery.left = new FormAttachment(0, 15);
		fd_tbQuery.right = new FormAttachment(100, -5);
		fd_tbQuery.top = new FormAttachment(butPerform, 3);
		fd_tbQuery.bottom = new FormAttachment(100);
		tbQuery.setLayoutData(fd_tbQuery);
		tbQuery.setHeaderVisible(true);
		tbQuery.setLinesVisible(true);

		bindData();
	}

	/**
	 * 绑定数据显示
	 */
	public void bindData() {
		sqlTextViewer.configure(new TextEditorConfiguration()); // 设置编辑着色和辅助对象
		IUndoManager undoManager = new DefaultUndoManager(100); // 初始化撤销管理器对象，默认可撤销100次
		undoManager.connect(sqlTextViewer); // 将该撤销管理器应用于文档

		Element sqlelement = getSqlElement();
		String id = sqlelement.attributeValue("id", "");
		String parameterType = sqlelement.attributeValue("parameterType", "");
		String resultType = sqlelement.attributeValue("resultType", "");
		String resultMap = sqlelement.attributeValue("resultMap", StudioUtil.getNewNodeId(getXMLDoc(), "NewEntityResultMap"));
		
		String s = StudioUtil.delPerfixBlank(StudioUtil.getNodeText(sqlelement), StudioUtil.getNodeLevel(sqlelement));
		s = StudioUtil.delBlankLine(s);
		Document doc = new Document(s);
		sqlTextViewer.setDocument(doc);

		XMLMapperDocument xmldoc = getXMLDoc();
		resultMapViewer.setLabelProvider(new ViewerLabelProvider());
		resultMapViewer.setContentProvider(new ContentProvider());
		resultMapViewer.setInput(xmldoc);
		
		txtMethodName.setText(id);
		cbParameterType.setText(parameterType);
		resultMapName.setText(StringUtils.isBlank(resultType) ? resultMap : resultType);
		resultMapChanged();
	}

	public XMLMapperDocument getXMLDoc() {
		BuildSelectSQLWizard wizard = (BuildSelectSQLWizard)getWizard();
		return wizard.getXMLDoc();
	}

	public Element getSqlElement() {
		BuildSelectSQLWizard wizard = (BuildSelectSQLWizard)getWizard();
		return wizard.getSqlElement();
	}

	public boolean isNewSql() {
		BuildSelectSQLWizard wizard = (BuildSelectSQLWizard)getWizard();
		return wizard.isNewSql();
	}
	
	public String getSql() {
		return sqlText.getText();
	}

	public String getMethodName() {
		return txtMethodName.getText();
	}

	public String getInputType() {
		return cbParameterType.getText();
	}
	
	public String getResutlType() {
		String type = resultMapName.getText();
		if (isResultType(type)) {
			return type;
		} else {
			return null;
		}
	}

	public List<Field> getQueryFieldList() {
		return queryFieldList;
	}
	
	public String getResultMapName() {
		String type = resultMapName.getText();
		if (isResultType(type)) {
			return null;
		} else {
			return type;
		}
	}
	
	/**
	 * 判断是否为返回结果对象（非返回映射对象）
	 */
	public boolean isResultType() {
		String s = resultMapName.getText().trim();
		return isResultType(s);
	}
	
	/**
	 * 判断是否为返回结果对象（非返回映射对象）
	 */
	private boolean isResultType(String s) {
		for (int i = 0; i < resultTypeList.size(); i++) {
			if (resultTypeList.get(i).equals(s))
				return true;
		}
		return false;
	}
	
	/**
	 * 当查询结果类型发生改变时，生成或删除第二页映射页面，并初始化
	 */
	private void resultMapChanged() {
		String txt = StringUtils.trim(resultMapName.getText());
		if (oldResutlName.equals(txt)) {
			return;
		}
		BuildSelectSQLWizard wizard = (BuildSelectSQLWizard)getWizard();
		if (StringUtils.isBlank(txt)) {
			wizard.removeSQLResultMapPage();
			setPageComplete(false);
			return;
		}
		if (isResultType()) {
			wizard.removeSQLResultMapPage();
			setPageComplete(true);
			return;
		}
		wizard.addSQLResultMapPage();
		String id = wizard.getSqlElement().attributeValue("id", "");
		if (id.equals(txt)) {
			setPageComplete(true);
		} else {
			XMLMapperDocument xmldoc = getXMLDoc();
			List<Field> fieldlist = null;
			for (int i = 0; i < xmldoc.resultMapNodes.size(); i++) {
				Element element = xmldoc.resultMapNodes.get(i);
				id = element.attributeValue("id", "");
				if (id.equals(txt)) { //选择已经有映射
					String resultMapType = element.attributeValue("type", "");
					fieldlist = new ArrayList<Field>();
					List<Element> es = element.elements();
					for (int j = 0; j < es.size(); j++) {
						Field field = new Field();
						Element elem = es.get(j);
						if ("id".equals(elem.getName())) {
							field.setPrimaryKey(true);
						} else if ("result".equals(elem.getName())) {
							field.setPrimaryKey(false);
						} else {
							continue;
						}
						String jdbcType = elem.attributeValue("jdbcType", "");
						field.setDataType(DbUtil.getJdbcTypeByName(jdbcType));
						field.setColumnName(elem.attributeValue("column", ""));
						DbUtil.fillField(field);
						field.setProperty(elem.attributeValue("property", ""));
						fieldlist.add(field);
					}
					BuildSelectSQLResultMapPage SQLResultMapPage = wizard.getSQLResultMapPage();
					SQLResultMapPage.setMapName(txt);
					SQLResultMapPage.setNewMap(false);
					SQLResultMapPage.setResultMapType(resultMapType);
					SQLResultMapPage.setResultFields(fieldlist);
					setPageComplete(true);
					break;
				}
			}
			if (fieldlist == null) { //新增映射,设置映射页面的列表，新增映射状态，设置查询结果字段
				String resultMapType = getNewResultMapType(txt);
				BuildSelectSQLResultMapPage SQLResultMapPage = wizard.getSQLResultMapPage();
				SQLResultMapPage.setMapName(txt);
				SQLResultMapPage.setNewMap(true);
				SQLResultMapPage.setResultMapType(resultMapType);
				SQLResultMapPage.refreshQueryField();
				if (queryFieldList == null || queryFieldList.size() == 0)
					setPageComplete(false);
				else
					setPageComplete(true);
			}
		}
		oldResutlName = txt;
	}
	
	/**
	 * 生成结果映射的Bean类名
	 * @return
	 */
	private String getNewResultMapType(String mapname) {
		String mainname = mapname;
		int index = mapname.indexOf("ResultMap");
		if (index > 0) {
			mainname = mapname.substring(0, index);
		}
		BuildSelectSQLWizard wizard = (BuildSelectSQLWizard)getWizard();
		IFile mapperfile = wizard.getMapperFile();
		ProjectConfig config = StudioUtil.getConfig();
		String javaSrc = config.getJavaSrc();
		if (!javaSrc.endsWith("/")) {
			javaSrc += "/";
		}
		String beanclasspath = ((IFolder)mapperfile.getParent().getParent().getParent()).getProjectRelativePath().toString() + "/entity"; // 当前目录相对路径
		String packagename = beanclasspath.replace(javaSrc, "").replace('/', '.');
		String beanclass = packagename + "." + mainname; 
		IProject project = mapperfile.getProject();
		IFile file = project.getFile(beanclasspath + "/" + mainname + ".java");
		int k = 0;
		while (file.exists()) {
			file = project.getFile(beanclasspath + "/" + mainname + k + ".java");
			beanclass = packagename + "."+ mainname + k;
			k++;
		}
		return beanclass;
	}

	private class TxtMethodNameFocusAdapter extends FocusAdapter {
		@Override
		public void focusLost(FocusEvent e) {
			String methodName = txtMethodName.getText();
			if (StringUtils.isBlank(methodName)) {
				setPageComplete(false);
				return;
			}
			XMLMapperDocument xmldoc = getXMLDoc();
			for (int i = 0; i < xmldoc.allNods.size(); i++) {
				Object o = xmldoc.allNods.get(i);
				if (!(o instanceof Element))
					continue;
				Element element = (Element)o;
				String id = element.attributeValue("id", "");
				if (StringUtils.isNotBlank(id) && (id.equals(methodName))) {
					if (element != getSqlElement()) {
						setPageComplete(false);
						MessageBox mess = new MessageBox(getShell(), SWT.ICON_ERROR | SWT.CANCEL);
						mess.setMessage("命名冲突！");
						mess.open();
						return;
					}
				}
			}
			//命名关键字规则
			if (StudioConst.MAP_ID_BaseResultMap.equals(methodName)
					 || StudioConst.MAP_ID_ResultMapWithBlob.equals(methodName)
					 || StudioConst.MAP_ID_AdapterUpdateWhereCondition.equals(methodName)
					 || StudioConst.MAP_ID_AdapterWhereCondition.equals(methodName)
					 || StudioConst.MAP_ID_BaseColumnList.equals(methodName)
					 || StudioConst.MAP_ID_BlobColumnList.equals(methodName)
					 || StudioConst.MAP_ID_countByAdapter.equals(methodName)
					 || StudioConst.MAP_ID_selectByAdapter.equals(methodName)
					 || StudioConst.MAP_ID_selectByAdapterWithBlob.equals(methodName)
					 || StudioConst.MAP_ID_selectByPrimaryKey.equals(methodName)
					 || StudioConst.MAP_ID_deleteByAdapter.equals(methodName)
					 || StudioConst.MAP_ID_deleteByPrimaryKey.equals(methodName)
					 || StudioConst.MAP_ID_insert.equals(methodName)
					 || StudioConst.MAP_ID_insertSelective.equals(methodName)
					 || StudioConst.MAP_ID_updateByAdapter.equals(methodName)
					 || StudioConst.MAP_ID_updateByAdapterWithBlob.equals(methodName)
					 || StudioConst.MAP_ID_updateByAdapterSelective.equals(methodName)
					 || StudioConst.MAP_ID_updateByPrimaryKey.equals(methodName)
					 || StudioConst.MAP_ID_updateByPrimaryKeyWithBlob.equals(methodName)
					 || StudioConst.MAP_ID_updateByPrimaryKeySelective.equals(methodName)) {
				Element element = getSqlElement();
				String id = element.attributeValue("id", "");
				if (!id.equals(methodName)) {
					setPageComplete(false);
					MessageBox mess = new MessageBox(getShell(), SWT.ICON_ERROR | SWT.CANCEL);
					mess.setMessage("不能使用关键字[" + methodName +"]命名！");
					mess.open();
					return;
				}
			}
		}
	}

	private class ButPerformMouseAdapter extends MouseAdapter {
		@Override
		public void mouseUp(MouseEvent e) {
			String sql = sqlText.getText();
			//去除xml内容
			try {
				sql = StudioUtil.formatXMLContext(sql);
				if (sql.indexOf("<include refid=\"BaseColumnList\"/>") >= 0) {
					
				}
				StringBuffer sb = new StringBuffer(sql);
				sb.insert(0, "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n<xml>\n");
				sb.append("</xml>");
				org.dom4j.Document doc = StudioUtil.parseXmlText(sb.toString());
				Element node = (Element)doc.node(0);
				sql = node.getText();
			} catch (DocumentException e2) {
				e2.printStackTrace();
			}
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
			sql = sql.replaceAll(StudioConst.MAPPER_PARAM_REGEX3, "?");
			
			Object[] result = null;
			if (params.length > 0) {
				InputParamValueDialog dialog2 = new InputParamValueDialog(getShell(), SWT.ICON_INFORMATION | SWT.YES | SWT.NO | SWT.CENTER, params);
				result = (Object[])dialog2.open();
				if (result == null)
					return;
			}
			List<Object> paramvalues = new ArrayList<Object>();
			if (result != null) {
				for (int i = 0; i < params.length; i++) {
					if (params[i].indexOf('$') >= 0) {
						String matchstr = "[$]\\{"+ params[i].substring(2, params[i].length() - 1)  + "\\}";
						sql = sql.replaceAll(matchstr, result[i].toString());
					} else {
						paramvalues.add(result[i]);
					}
				}
			}
			DbUtil.getEngine().ExcuteQuery(sql, paramvalues.toArray(), new IDoResult() {
				public void DoResutl(ResultSet rs) {
					tbQuery.clearAll();
					tbQuery.setItemCount(0);
					for (int i = 0; i < cloumns.size(); i++) {
						TableColumn tableColumn = cloumns.get(i);
						tableColumn.dispose();
					}
					cloumns.clear();
					try {
						queryFieldList = new ArrayList<Field>();
						ResultSetMetaData rsmd = rs.getMetaData();
						for (int i = 1; i <= rsmd.getColumnCount(); i++) {
							Field field = new Field();
							field.setColumnName(rsmd.getColumnName(i));
							field.setDataType(rsmd.getColumnType(i));
							field.setTypeName(rsmd.getColumnTypeName(i));
							field.setColumnSize(rsmd.getColumnDisplaySize(i));
							field.setDecimalDigits(rsmd.getScale(i));
							field.setNullable(rsmd.isNullable(i) != 0);
							//String tab = rsmd.getTableName(i); rsmd.getColumnClassName(i);rsmd.getCatalogName(i);
							DbUtil.fillField(field);
							//System.out.println(field.getJdbcType() + "\t\t" + rsmd.getColumnClassName(i)  + "\t\t" + rsmd.getColumnName(i));
							queryFieldList.add(field);
							TableColumn tableColumn = new TableColumn(tbQuery, SWT.NONE);
							tableColumn.setText(field.getColumnName());
							tableColumn.setWidth(100);
							cloumns.add(tableColumn);
						}
						if (getNextPage() != null) {
							BuildSelectSQLResultMapPage page = (BuildSelectSQLResultMapPage)getNextPage();
							if (page.isNewMap()) {
								page.refreshQueryField();
							}
						}
						int k = 0;
						while (rs.next()) {
							TableItem item = new TableItem(tbQuery, SWT.NONE);
							for (int i = 1; i <= rsmd.getColumnCount(); i++) {
								Object value = rs.getObject(i);
								if (value != null) {
									item.setText(i - 1, value.toString());
								}
							}
							k++;
							if (k > 1000) {
								MessageBox mess = new MessageBox(getShell(), SWT.ICON_INFORMATION | SWT.CANCEL);
								mess.setMessage("返回的数据量大于1000");
								mess.open();
								break;
							}
						}
						setPageComplete(true);
					} catch (SQLException e) {
						MessageBox mess = new MessageBox(getShell(), SWT.ICON_ERROR | SWT.CANCEL);
						mess.setMessage("SQL错误:\n" + e.getMessage());
						mess.open();
					}
				}
				public void DoError(Exception e) {
					MessageBox mess = new MessageBox(getShell(), SWT.ICON_ERROR | SWT.CANCEL);
					mess.setMessage("SQL错误:\n" + e.getMessage());
					mess.open();
				}
				public void DoUpdate(Connection conn, int reflectCount) {
				}
			});
		}
	}
	
	private class ViewerLabelProvider extends LabelProvider {
		public Image getImage(Object element) {
			if (element instanceof String) {
				return StudioUtil.getImage(StudioConst.ICON_RESULT_OBJECT);
			} else if (element instanceof Element) {
				return StudioUtil.getImage(StudioConst.ICON_RESULT_MAP);
			} else {
				return null;
			}
		}
		public String getText(Object element) {
			if (element instanceof String) {
				return (String)element;
			} else if (element instanceof Element) {
				String id = ((Element)element).attributeValue("id", "");
				return id;
			} else {
				return "";
			}
		}
	}
	
	private class ContentProvider implements IStructuredContentProvider {
		public Object[] getElements(Object inputElement) {
			List<Object> slist = new ArrayList<Object>();
			XMLMapperDocument xmldoc = getXMLDoc();
			for (int i = 0; i < xmldoc.resultMapNodes.size(); i++) {
				Element element = xmldoc.resultMapNodes.get(i);
				String sid = element.attributeValue("id", "");
				if (StringUtils.isNotBlank(sid)) {
					slist.add(element);
				}
			}
			for (int i = 0; i < resultTypeList.size(); i++) {
				slist.add(resultTypeList.get(i));
			}
			return slist.toArray(new Object[]{});
		}
		public void dispose() {
		}
		public void inputChanged(Viewer viewer, Object oldInput, Object newInput) {
		}
	}
}
