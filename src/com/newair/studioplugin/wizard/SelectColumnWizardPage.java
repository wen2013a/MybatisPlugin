package com.newair.studioplugin.wizard;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.eclipse.core.resources.IFolder;
import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.MouseAdapter;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.FormAttachment;
import org.eclipse.swt.layout.FormData;
import org.eclipse.swt.layout.FormLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.MessageBox;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.swt.widgets.TableItem;

import com.newair.studioplugin.ProjectConfig;
import com.newair.studioplugin.StudioUtil;
import com.newair.studioplugin.db.DbUtil;
import com.newair.studioplugin.db.Field;
import com.newair.studioplugin.db.IDbEngine;

import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;

public class SelectColumnWizardPage extends WizardPage {
	private Table table;
	private IFolder folder;
	
	private String tablename = "";
	private String[] initSelectColumn = null;
	private Text txtPackageName;
	
	/**
	 * Create the wizard.
	 */
	public SelectColumnWizardPage() {
		super("SelectColumnWizardPage");
		setTitle("选择列");
		setDescription("选择列名称");
		setPageComplete(false);
	}
	
	/**
	 * Create contents of the wizard.
	 * @param parent
	 */
	public void createControl(Composite parent) {
		Composite container = new Composite(parent, SWT.NULL);

		setControl(container);
		container.setLayout(new FormLayout());
		
		table = new Table(container, SWT.BORDER | SWT.CHECK | SWT.FULL_SELECTION | SWT.MULTI);
		table.addSelectionListener(new TableSelectionAdapter());
		FormData fd_table = new FormData();
		fd_table.bottom = new FormAttachment(100, -30);
		fd_table.right = new FormAttachment(100, 0);
		fd_table.top = new FormAttachment(0);
		fd_table.left = new FormAttachment(0);
		table.setLayoutData(fd_table);
		table.setHeaderVisible(true);
		table.setLinesVisible(true);
		
		TableColumn tableColumn = new TableColumn(table, SWT.NONE);
		tableColumn.setWidth(150);
		tableColumn.setText("字段名称");
		
		TableColumn tableColumn_1 = new TableColumn(table, SWT.NONE);
		tableColumn_1.setWidth(118);
		tableColumn_1.setText("字段类型");
		
		TableColumn tableColumn_4 = new TableColumn(table, SWT.NONE);
		tableColumn_4.setWidth(58);
		tableColumn_4.setText("长度");
		
		TableColumn tableColumn_5 = new TableColumn(table, SWT.NONE);
		tableColumn_5.setWidth(63);
		tableColumn_5.setText("小数位数");
		
		TableColumn tableColumn_6 = new TableColumn(table, SWT.NONE);
		tableColumn_6.setWidth(40);
		tableColumn_6.setText("主键");
		
		TableColumn tableColumn_2 = new TableColumn(table, SWT.NONE);
		tableColumn_2.setWidth(61);
		tableColumn_2.setText("可否为空");
		
		TableColumn tableColumn_7 = new TableColumn(table, SWT.NONE);
		tableColumn_7.setWidth(61);
		tableColumn_7.setText("是否自增");
		
		TableColumn tableColumn_3 = new TableColumn(table, SWT.NONE);
		tableColumn_3.setWidth(100);
		tableColumn_3.setText("描述");
		
		Button butSelectAll = new Button(container, SWT.NONE);
		butSelectAll.addMouseListener(new ButSelectAllMouseAdapter());
		FormData fd_butSelectAll = new FormData();
		fd_butSelectAll.bottom = new FormAttachment(100);
		fd_butSelectAll.left = new FormAttachment(table, 0, SWT.LEFT);
		butSelectAll.setLayoutData(fd_butSelectAll);
		butSelectAll.setText("全选");
		
		Button btuSelectTurn = new Button(container, SWT.NONE);
		btuSelectTurn.addMouseListener(new ButSelectTurnMouseAdapter());
		FormData fd_btuSelectTurn = new FormData();
		fd_btuSelectTurn.top = new FormAttachment(butSelectAll, 0, SWT.TOP);
		fd_btuSelectTurn.bottom = new FormAttachment(butSelectAll, 0, SWT.BOTTOM);
		fd_btuSelectTurn.left = new FormAttachment(butSelectAll, 6);
		btuSelectTurn.setLayoutData(fd_btuSelectTurn);
		btuSelectTurn.setText("反选");
		
		Label label = new Label(container, SWT.NONE);
		FormData fd_label = new FormData();
		fd_label.top = new FormAttachment(butSelectAll, 5, SWT.TOP);
		label.setLayoutData(fd_label);
		label.setText("包名：");
		
		txtPackageName = new Text(container, SWT.BORDER);
		fd_label.right = new FormAttachment(txtPackageName, -6);
		FormData fd_txtPackageName = new FormData();
		fd_txtPackageName.right = new FormAttachment(table, 0, SWT.RIGHT);
		fd_txtPackageName.top = new FormAttachment(table, 3);
		fd_txtPackageName.left = new FormAttachment(0, 165);
		txtPackageName.setLayoutData(fd_txtPackageName);
		
		bindColumnData(tablename);
	}
	
	/**
	 * 绑定字段数据
	 */
	public void bindColumnData(String tablename) {
		setPageComplete(false);
		this.tablename = tablename;
		if (table == null || StringUtils.isBlank(tablename))
			return;
		table.clearAll();
		table.setItemCount(0);
		IDbEngine engine = DbUtil.getEngine();
		Field[] fields = engine.getColumnDefine(tablename);
		for (int i = 0; i < fields.length; i++) {
			Field field = fields[i];
			TableItem item = new TableItem(table, SWT.NONE);
			item.setData(field);
			item.setText(0, field.getColumnName());
			item.setText(1, field.getJdbcType());
			item.setText(2, String.valueOf(field.getColumnSize()));
			item.setText(3, (field.getDecimalDigits() == 0) ? "" : String.valueOf(field.getDecimalDigits()));
			item.setText(4, field.isPrimaryKey() ? "是" : "");
			item.setText(5, field.isNullable() ? "是" :"");
			item.setText(6, field.isIdentity() ? "是" : "");
			item.setText(7, StringUtils.isBlank(field.getRemarks()) ? "" : field.getRemarks());
			if (initSelectColumn != null) {
				item.setChecked(false);
				for (int j = 0; j < initSelectColumn.length; j++) {
					if (initSelectColumn[j].equals(field.getColumnName())) {
						item.setChecked(true);
						break;
					}
				}
			} else {
				item.setChecked(true);
			}
		}
		//初始化包名
		if (folder != null) {
			ProjectConfig config = StudioUtil.getConfig();
			String mapperroot = config.getMapperRoot();
			if (StringUtils.isNotBlank(mapperroot) && !mapperroot.endsWith("/")) {
				mapperroot += "/";
			}
			IFolder parentFolder = (IFolder)folder.getParent().getParent();
			String parentpath = parentFolder.getProjectRelativePath().toString();
			String parentPackagename = parentpath.replaceAll(mapperroot, "").replaceAll("/", ".");     // 父包名称
			String beanclass = parentPackagename + ".entity." + DbUtil.getTableBeanName(tablename); // Bean类全名
			txtPackageName.setText(beanclass);
		}
		decidePageComplete();
	}
	
	public void setFolder(IFolder folder) {
		this.folder = folder;
	}
	
	public void setBeanClass(String beanclass) {
		this.txtPackageName.setText(beanclass);
	}
	
	/**
	 * 获取实体类名
	 */
	public String getBeanClass() {
		return txtPackageName.getText();
	}
	
	/**
	 * 获取所有选择的字段(主键自增字段排序前)
	 */
	public Field[] getAllFields() {
		List<Field> list = new ArrayList<Field>();
		for (int i = 0; i < table.getItemCount(); i++) {
			TableItem item = table.getItem(i);
			if (item.getChecked()) {
				Field field = (Field)item.getData();
				if (field.isPrimaryKey() || field.isIdentity()) {
					list.add(0, field);
				} else {
					list.add(field);
				}
			}
		}
		return list.toArray(new Field[] {});
	}
	
	/**
	 * 获取所有选择的字段(主键自增字段排序前)
	 */
	public Field[] getFields() {
		List<Field> list = new ArrayList<Field>();
		for (int i = 0; i < table.getItemCount(); i++) {
			TableItem item = table.getItem(i);
			if (item.getChecked()) {
				Field field = (Field)item.getData();
				if (field.isPrimaryKey() || field.isIdentity()) {
					list.add(0, field);
				} else {
					list.add(field);
				}
			}
		}
		return list.toArray(new Field[] {});
	}
	
	/**
	 * 是否选择了的所有的字段
	 */
	public boolean isSelectAllField() {
		boolean flag = true;
		for (int i = 0; i < table.getItemCount(); i++) {
			TableItem item = table.getItem(i);
			if (!item.getChecked()) {
				flag = false;
				break;
			}
		}
		return flag;
	}
	
	/**
	 * 判断是否完成
	 */
	private void decidePageComplete() {
		boolean flag = false;
		for (int i = 0; i < table.getItemCount(); i++) {
			TableItem item = table.getItem(i);
			Field field = (Field)item.getData();
			if (field.isPrimaryKey() && (!item.getChecked())) { //主键必选
				setPageComplete(false);
				MessageBox mess = new MessageBox(getShell(), SWT.ICON_ERROR | SWT.CANCEL);
				mess.setMessage("必须要选择主键！");
				mess.open();
				return;
			}
			if (item.getChecked()) {
				flag = true;
			}
		}
		setPageComplete(flag);
	}
	
	/**
	 * 初始化选择的列
	 */
	public void InitSelectColumn(String[] selectColumn) {
		this.initSelectColumn = selectColumn;
	}
	
	/**
	 * 列表选择
	 */
	private class TableSelectionAdapter extends SelectionAdapter {
		@Override
		public void widgetSelected(SelectionEvent e) {
			decidePageComplete();
		}
	}
	
	/**
	 * 全选
	 */
	private class ButSelectAllMouseAdapter extends MouseAdapter {
		@Override
		public void mouseUp(MouseEvent e) {
			for (int i = 0; i < table.getItemCount(); i++) {
				TableItem item = table.getItem(i);
				item.setChecked(true);
			}
			decidePageComplete();
		}
	}
	
	/**
	 * 反选
	 */
	private class ButSelectTurnMouseAdapter extends MouseAdapter {
		@Override
		public void mouseUp(MouseEvent e) {
			for (int i = 0; i < table.getItemCount(); i++) {
				TableItem item = table.getItem(i);
				item.setChecked(!item.getChecked());
			}
			decidePageComplete();
		}
	}
}
