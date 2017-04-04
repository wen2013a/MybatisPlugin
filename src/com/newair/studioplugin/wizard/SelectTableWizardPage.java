package com.newair.studioplugin.wizard;

import org.apache.commons.lang.StringUtils;
import org.eclipse.jface.wizard.IWizardPage;
import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.TableItem;
import org.eclipse.swt.widgets.Text;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;

import com.newair.studioplugin.db.DbUtil;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.KeyAdapter;
import org.eclipse.swt.events.KeyEvent;
import org.eclipse.swt.layout.FormLayout;
import org.eclipse.swt.layout.FormData;
import org.eclipse.swt.layout.FormAttachment;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.ModifyEvent;

public class SelectTableWizardPage extends WizardPage {
	private Text textTabelName;
	private Table tableList;
	private String[] allTableName;
	private String tableName;

	/**
	 * Create the wizard.
	 */
	public SelectTableWizardPage() {
		super("SelectTableWizardPage");
		setTitle("选择表名称");
		setDescription("选择表名称");
		allTableName = DbUtil.getEngine().getAllTable();
		setPageComplete(false);
	}

	/**
	 * Create contents of the wizard.
	 * 
	 * @param parent
	 */
	public void createControl(Composite parent) {
		Composite container = new Composite(parent, SWT.NULL);
		setControl(container);
		container.setLayout(new FormLayout());

		Label label = new Label(container, SWT.NONE);
		FormData fd_label = new FormData();
		fd_label.top = new FormAttachment(0, 10);
		fd_label.left = new FormAttachment(0, 10);
		label.setLayoutData(fd_label);
		label.setText("表名称过滤：");

		textTabelName = new Text(container, SWT.BORDER);
		textTabelName.addModifyListener(new TextTableNameModifyListener());
		FormData fd_textTabelName = new FormData();
		fd_textTabelName.left = new FormAttachment(label, 6);
		fd_textTabelName.right = new FormAttachment(0, 256);
		fd_textTabelName.top = new FormAttachment(0, 7);
		textTabelName.setLayoutData(fd_textTabelName);
		textTabelName.addKeyListener(new TableNameKeyAdapter());

		tableList = new Table(container, SWT.BORDER | SWT.FULL_SELECTION);
		FormData fd_tableList = new FormData();
		fd_tableList.bottom = new FormAttachment(100, -5);
		fd_tableList.right = new FormAttachment(100, -5);
		fd_tableList.top = new FormAttachment(0, 36);
		fd_tableList.left = new FormAttachment(0, 10);
		tableList.setLayoutData(fd_tableList);
		tableList.addSelectionListener(new TableListSelectionAdapter());
		tableList.setHeaderVisible(true);
		tableList.setLinesVisible(true);

		TableColumn tableColumn = new TableColumn(tableList, SWT.NONE);
		tableColumn.setWidth(265);
		tableColumn.setText("表名称");

		bindData();
	}

	/**
	 * 获取表名称
	 */
	public String getTableName() {
		return tableName;
	}

	private void bindData() {
		tableList.clearAll();
		tableList.setItemCount(0);
		String s = StringUtils.trim(textTabelName.getText()).toUpperCase();
		for (int i = 0; i < allTableName.length; i++) {
			String ss = allTableName[i].toUpperCase();
			if ((StringUtils.isBlank(s)) || (ss.indexOf(s) >= 0)) {
				TableItem item = new TableItem(tableList, SWT.NONE);
				item.setText(allTableName[i]);
			}
		}
	}
	
	/**
	 * 绑定下一页的数据
	 */
	private void bindNextPageData() {
		IWizardPage page = getNextPage();
		if (page instanceof SelectColumnWizardPage) {
			((SelectColumnWizardPage)page).bindColumnData(getTableName());
		}
	}

	private class TableListSelectionAdapter extends SelectionAdapter {
		@Override
		public void widgetSelected(SelectionEvent e) {
			TableItem item = (TableItem)e.item;
			tableName = item.getText();
			setPageComplete(true);
			bindNextPageData();
		}
	}

	private class TableNameKeyAdapter extends KeyAdapter {
		@Override
		public void keyReleased(KeyEvent e) {
			bindData();
		}
	}
	
	private class TextTableNameModifyListener implements ModifyListener {
		public void modifyText(ModifyEvent e) {
			setPageComplete(false);
		}
	}
}
