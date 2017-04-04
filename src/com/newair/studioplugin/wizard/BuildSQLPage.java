package com.newair.studioplugin.wizard;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.lang.StringUtils;
import org.dom4j.DocumentException;
import org.dom4j.Element;
import org.dom4j.Node;
import org.eclipse.jface.text.DefaultUndoManager;
import org.eclipse.jface.text.Document;
import org.eclipse.jface.text.IUndoManager;
import org.eclipse.jface.text.source.IVerticalRuler;
import org.eclipse.jface.text.source.SourceViewer;
import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.StyledText;
import org.eclipse.swt.events.FocusAdapter;
import org.eclipse.swt.events.FocusEvent;
import org.eclipse.swt.events.MouseAdapter;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.FormAttachment;
import org.eclipse.swt.layout.FormData;
import org.eclipse.swt.layout.FormLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.MenuItem;
import org.eclipse.swt.widgets.MessageBox;
import org.eclipse.swt.widgets.Text;

import com.newair.studioplugin.StudioConst;
import com.newair.studioplugin.StudioUtil;
import com.newair.studioplugin.db.DbUtil;
import com.newair.studioplugin.db.IDoResult;
import com.newair.studioplugin.editors.XMLMapperDocument;
import com.newair.studioplugin.editors.syntextcolor.TextEditorConfiguration;

public class BuildSQLPage extends WizardPage {
	private Text txtMethodName;
	private Combo cbParameterType;
	private StyledText sqlText;
	private SourceViewer sqlTextViewer;
	private Menu menu;
	private Text text;
	private Button butCommit;
	private Button butRollback;
	private Button butPerform;
	
	private Connection dbConn;

	protected BuildSQLPage() {
		super("BuildSQLPage");
		setTitle("SQL语句");
		setDescription("编辑SQL查询语句");
		setPageComplete(true);
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
		fd_cbParameterType.right = new FormAttachment(100, -10);
		cbParameterType.setLayoutData(fd_cbParameterType);

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
		fd_sqlText.bottom = new FormAttachment(100, -150);
		sqlText.setLayoutData(fd_sqlText);

		butPerform = new Button(container, SWT.NONE);
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
		
		butCommit = new Button(container, SWT.NONE);
		butCommit.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseUp(MouseEvent e) {
				if (dbConn != null) {
					try {
						dbConn.commit();
						dbConn.close();
						dbConn = null;
					} catch (SQLException e1) {
						e1.printStackTrace();
					}
				}
			}
		});
		butCommit.setText("事务提交");
		FormData fd_butCommit = new FormData();
		fd_butCommit.top = new FormAttachment(sqlText, 6);
		fd_butCommit.left = new FormAttachment(butPerform, 16);
		fd_butCommit.right = new FormAttachment(butPerform, 86, SWT.RIGHT);
		butCommit.setLayoutData(fd_butCommit);
		
		butRollback = new Button(container, SWT.NONE);
		butRollback.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseUp(MouseEvent e) {
				if (dbConn != null) {
					try {
						dbConn.rollback();
						dbConn.close();
						dbConn = null;
					} catch (SQLException e1) {
						e1.printStackTrace();
					}
				}
			}
		});
		butRollback.setText("事务回滚");
		FormData fd_butRollback = new FormData();
		fd_butRollback.right = new FormAttachment(butCommit, 86, SWT.RIGHT);
		fd_butRollback.left = new FormAttachment(butCommit, 16);
		fd_butRollback.top = new FormAttachment(butPerform, 0, SWT.TOP);
		butRollback.setLayoutData(fd_butRollback);
		
		text = new Text(container, SWT.BORDER | SWT.MULTI);
		FormData fd_text = new FormData();
		fd_text.bottom = new FormAttachment(butPerform, 117, SWT.BOTTOM);
		fd_text.left = new FormAttachment(label1, 0, SWT.LEFT);
		fd_text.right = new FormAttachment(100, -10);
		fd_text.top = new FormAttachment(butPerform, 6);
		text.setLayoutData(fd_text);

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

		String s = StudioUtil.delPerfixBlank(StudioUtil.getNodeText(sqlelement), StudioUtil.getNodeLevel(sqlelement));
		s = StudioUtil.delBlankLine(s);
		Document doc = new Document(s);
		sqlTextViewer.setDocument(doc);

		txtMethodName.setText(id);
		cbParameterType.setText(parameterType);
		
		butCommit.setEnabled(false);
		butRollback.setEnabled(false);
		
		XMLMapperDocument xmldoc = getXMLDoc();
		List<String> paramTypeList = new ArrayList<String>();
		paramTypeList.add("");
		for (int i = 0; i < xmldoc.resultMapNodes.size(); i++) {
			Element resultmap = xmldoc.resultMapNodes.get(i);
			String type = resultmap.attributeValue("type");
			if ("java.util.HashMap".equals(type))
				continue;
			paramTypeList.add(type);
		}
		paramTypeList.add("java.lang.String");
		paramTypeList.add("java.lang.Boolean");
		paramTypeList.add("java.lang.Short");
		paramTypeList.add("java.lang.Integer");
		paramTypeList.add("java.lang.Long");
		paramTypeList.add("java.lang.Float");
		paramTypeList.add("java.lang.Double");
		paramTypeList.add("java.math.BigDecimal");
		paramTypeList.add("java.sql.Timestamp");
		paramTypeList.add("java.util.Date");
		paramTypeList.add("java.sql.Time");
		paramTypeList.add("java.util.HashMap");
		paramTypeList.add("java.lang.Object");
		String[] types = paramTypeList.toArray(new String[]{});
		cbParameterType.setItems(types);
	}

	public XMLMapperDocument getXMLDoc() {
		BuildSQLWizard wizard = (BuildSQLWizard)getWizard();
		return wizard.getXMLDoc();
	}

	public Element getSqlElement() {
		BuildSQLWizard wizard = (BuildSQLWizard)getWizard();
		return wizard.getSqlElement();
	}

	public boolean isNewSql() {
		BuildSQLWizard wizard = (BuildSQLWizard)getWizard();
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
	
	public Connection getDbConn() {
		return dbConn;
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
			if (dbConn != null) {
				try {
					dbConn.close();
					dbConn = null;
				} catch (SQLException e1) {
					e1.printStackTrace();
				}
			}
			butCommit.setEnabled(false);
			butRollback.setEnabled(false);
			String sql = sqlText.getText();
			//去除xml内容
			try {
				sql = StudioUtil.formatXMLContext(sql);
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
				InputParamValueDialog dialog2 = new InputParamValueDialog(getShell(), SWT.ICON_INFORMATION | SWT.YES | SWT.NO  | SWT.CENTER, params);
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
			DbUtil.getEngine().ExecteUpdate(sql, paramvalues.toArray(), false, new IDoResult() {
				public void DoUpdate(Connection conn, int reflectCount) {
					try {
						dbConn = conn;
						text.append("影响记录数:" + reflectCount + "\n");
						butCommit.setEnabled(true);
						butRollback.setEnabled(true);
						setPageComplete(true);
					} catch (Exception e) {
						MessageBox mess = new MessageBox(getShell(), SWT.ICON_ERROR | SWT.CANCEL);
						mess.setMessage("SQL错误:\n" + e.getMessage());
						mess.open();
					}
				}
				public void DoResutl(ResultSet rs) {
				}
				public void DoError(Exception e) {
					MessageBox mess = new MessageBox(getShell(), SWT.ICON_ERROR | SWT.CANCEL);
					mess.setMessage("SQL错误:\n" + e.getMessage());
					mess.open();
				}
			});
		}
	}
}
