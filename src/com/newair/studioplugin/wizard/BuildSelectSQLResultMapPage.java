package com.newair.studioplugin.wizard;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.dom4j.Element;
import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.MessageBox;
import org.eclipse.swt.widgets.TableItem;
import org.eclipse.swt.widgets.Text;
import org.eclipse.swt.layout.FormLayout;
import org.eclipse.swt.layout.FormData;
import org.eclipse.swt.layout.FormAttachment;
import org.eclipse.swt.widgets.Table;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.jface.viewers.TableViewerColumn;
import org.eclipse.swt.widgets.Button;

import com.newair.studioplugin.StudioConst;
import com.newair.studioplugin.db.Field;
import com.newair.studioplugin.editors.XMLMapperDocument;
import com.swtdesigner.ResourceManager;
import org.eclipse.swt.events.MouseAdapter;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.FocusAdapter;
import org.eclipse.swt.events.FocusEvent;
import com.swtdesigner.SWTResourceManager;

public class BuildSelectSQLResultMapPage extends WizardPage {
	private Table table;
	private Text cbResultMapName;
	private Combo cbResultMapType;
	private Button butSelectMapType;

	private boolean isNewMap;
	private String mapName = "";
	private String resultMapType = "";
	private List<Field> resultFields;

	protected BuildSelectSQLResultMapPage() {
		super("BuildSQLResultMapPage");
		setTitle("查询结果映射");
		setDescription("查询结果映射");
		isNewMap = true;
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
		label1.setText("返回结果：");

		cbResultMapName = new Text(container, SWT.BORDER | SWT.READ_ONLY);
		cbResultMapName.setBackground(SWTResourceManager.getColor(SWT.COLOR_WIDGET_BACKGROUND));
		FormData fd_cbResultMapName = new FormData();
		fd_cbResultMapName.top = new FormAttachment(0, 7);
		fd_cbResultMapName.bottom = new FormAttachment(0, 30);
		fd_cbResultMapName.left = new FormAttachment(label1, 0, SWT.RIGHT);
		fd_cbResultMapName.right = new FormAttachment(label1, 140, SWT.RIGHT);
		cbResultMapName.setLayoutData(fd_cbResultMapName);

		Button button_1 = new Button(container, SWT.NONE);
		button_1.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseUp(MouseEvent e) {
				String mapname = cbResultMapName.getText();
				if (StringUtils.isBlank(mapname)) {
					return;
				}
				if (mapname.equals(StudioConst.MAP_ID_BaseResultMap) || mapname.equals(StudioConst.MAP_ID_BlobColumnList)) {
					MessageBox messageBox = new MessageBox(getShell(), SWT.ERROR | SWT.NO);
					messageBox.setMessage("不允许修改！");
					messageBox.open();
					return;
				}
				BuildSelectSQLPage buildSQLPage = (BuildSelectSQLPage)getPreviousPage();
				List<Field> queryFieldList = buildSQLPage.getQueryFieldList();
				if (queryFieldList == null) {
					MessageBox messageBox = new MessageBox(getShell(), SWT.ERROR | SWT.NO);
					messageBox.setMessage("请返回上一步执行SQL查询语句！");
					messageBox.open();
					return;
				}
				BuildSelectSQLWizard wizard = (BuildSelectSQLWizard)getWizard();
				XMLMapperDocument xmldoc = wizard.getXMLDoc();
				Element sqlelement = wizard.getSqlElement();
				for (int i = 0; i < xmldoc.selectNodes.size(); i++) {
					Element ele = xmldoc.selectNodes.get(i);
					String resultMap = ele.attributeValue("resultMap", "");
					if (ele != sqlelement && resultMap.equals(mapName)) {
						MessageBox messageBox = new MessageBox(getShell(), SWT.ICON_WARNING | SWT.YES | SWT.NO);
						messageBox.setMessage("还有其他查询引用到这个结果映射对象，如果更新将会爱到影响。\n请确定要更新结果映射[ " + cbResultMapName.getText() + " ]吗？");
						int rc = messageBox.open();
						if (rc != SWT.YES) {
							return;
						}
					}
				}
				resultFields.clear();
				for (int i = 0; i < queryFieldList.size(); i++) {
					Field field = queryFieldList.get(i);
					resultFields.add(field);
				}
				bindData();
			}
		});
		button_1.setToolTipText("使用查询结果更新映射关系表");
		button_1.setImage(ResourceManager.getPluginImage("StudioPlugin", StudioConst.ICON_REFRESH));
		FormData fd_button_1 = new FormData();
		fd_button_1.left = new FormAttachment(cbResultMapName, 2);
		fd_button_1.top = new FormAttachment(0, 5);
		button_1.setLayoutData(fd_button_1);

		Label label2 = new Label(container, SWT.NONE);
		FormData fd_label2 = new FormData();
		fd_label2.left = new FormAttachment(button_1, 12);
		fd_label2.top = new FormAttachment(label1, 0, SWT.TOP);
		label2.setLayoutData(fd_label2);
		label2.setText("映射实体类：");

		cbResultMapType = new Combo(container, SWT.NONE);
		cbResultMapType.addFocusListener(new FocusAdapter() {
			@Override
			public void focusLost(FocusEvent e) {
				resultMapType = cbResultMapType.getText();
			}
		});
		cbResultMapType.addModifyListener(new ModifyListener() {
			public void modifyText(ModifyEvent e) {
				if (StringUtils.isNotBlank(cbResultMapType.getText())) {
					setPageComplete(true);
				} else {
					setPageComplete(false);
				}
			}
		});
		cbResultMapType.setItems(new String[] {"", "java.util.HashMap"});
		FormData fd_cbResultMapType = new FormData();
		fd_cbResultMapType.left = new FormAttachment(label2, 0, SWT.RIGHT);
		fd_cbResultMapType.right = new FormAttachment(100, -35);
		fd_cbResultMapType.top = new FormAttachment(0, 7);
		cbResultMapType.setLayoutData(fd_cbResultMapType);

		butSelectMapType = new Button(container, SWT.NONE);
		butSelectMapType.setImage(ResourceManager.getPluginImage("StudioPlugin", "icons/editor/source-editor.png"));
		FormData fd_butSelectMapType = new FormData();
		fd_butSelectMapType.top = new FormAttachment(0, 5);
		fd_butSelectMapType.right = new FormAttachment(100, -5);
		butSelectMapType.setLayoutData(fd_butSelectMapType);

		TableViewer tableViewer = new TableViewer(container, SWT.BORDER | SWT.FULL_SELECTION);
		table = tableViewer.getTable();
		table.setLinesVisible(true);
		table.setHeaderVisible(true);
		FormData fd_table = new FormData();
		fd_table.top = new FormAttachment(label1, 6);
		fd_table.left = new FormAttachment(0, 0);
		fd_table.right = new FormAttachment(100, 0);
		fd_table.bottom = new FormAttachment(100, -12);
		table.setLayoutData(fd_table);

		TableViewerColumn tableViewerColumn = new TableViewerColumn(tableViewer, SWT.NONE);
		TableColumn tableColumn = tableViewerColumn.getColumn();
		tableColumn.setWidth(200);
		tableColumn.setText("字段名称");

		TableViewerColumn tableViewerColumn_1 = new TableViewerColumn(tableViewer, SWT.NONE);
		TableColumn tableColumn_1 = tableViewerColumn_1.getColumn();
		tableColumn_1.setWidth(200);
		tableColumn_1.setText("映射名称");

		TableViewerColumn tableViewerColumn_2 = new TableViewerColumn(tableViewer, SWT.NONE);
		TableColumn tableColumn_2 = tableViewerColumn_2.getColumn();
		tableColumn_2.setWidth(131);
		tableColumn_2.setText("JDBC数据类型");
		
		bindData();
	}

	public void bindData() {
		if (table != null) {
			cbResultMapName.setText(mapName);
			cbResultMapType.setItem(0, resultMapType);
			cbResultMapType.setText(resultMapType);
			table.clearAll();
			table.setItemCount(0);
		}
		if (resultFields != null) {
			for (int i = 0; i < resultFields.size(); i++) {
				Field field = resultFields.get(i);
				TableItem item = new TableItem(table, SWT.NONE);
				item.setText(0, field.getColumnName());
				item.setText(1, field.getProperty());
				item.setText(2, field.getJdbcType());
			}
		}
	}

	/**
	 * 上一页的查询结果刷新映射列表
	 */
	public void refreshQueryField() {
		BuildSelectSQLPage buildSQLPage = (BuildSelectSQLPage)getPreviousPage();
		List<Field> queryFieldList = buildSQLPage.getQueryFieldList();
		if (queryFieldList != null) {		
			if (resultFields != null) {
				resultFields.clear();
			} else {
				resultFields = new ArrayList<Field>();
			}
			for (int i = 0; i < queryFieldList.size(); i++) {
				Field field = queryFieldList.get(i);
				resultFields.add(field);
			}
		}
		bindData();
	}

	public String getResultMapType() {
		return resultMapType;
	}
	
	public void setResultMapType(String resultmapType) {
		this.resultMapType = resultmapType;
		if (table != null) {
			bindData();
		}
	}

	public boolean isNewMap() {
		return isNewMap;
	}

	public void setNewMap(boolean isNewMap) {
		this.isNewMap = isNewMap;		
		if (table != null) {
			bindData();
		}
	}

	public String getMapName() {
		return mapName;
	}

	public void setMapName(String mapName) {
		this.mapName = mapName;
		if (table != null) {
			bindData();
		}
	}

	public List<Field> getResultFields() {
		return resultFields;
	}

	public void setResultFields(List<Field> resultFields) {
		this.resultFields = resultFields;
		if (table != null) {
			bindData();
		}
	}
}
