package com.newair.studioplugin.wizard;

import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.events.MouseAdapter;
import org.eclipse.swt.events.MouseEvent;

public class SelectSqlWizardPage extends WizardPage {
	private Button btnselect;
	private Button btninsert;
	private Button btnupdate;
	private Button btndelete;
	private Button btnjava;

	/**
	 * Create the wizard.
	 */
	public SelectSqlWizardPage() {
		super("SelectSqlWizardPage");
		setTitle("生成映射");
		setDescription("选择映射内容");
		setPageComplete(true);
	}

	/**
	 * Create contents of the wizard.
	 * 
	 * @param parent
	 */
	public void createControl(Composite parent) {
		Composite container = new Composite(parent, SWT.NULL);

		setControl(container);

		btnselect = new Button(container, SWT.CHECK);
		btnselect.setSelection(true);
		btnselect.addMouseListener(new SelectMouseAdapter());
		btnselect.setBounds(23, 28, 121, 17);
		btnselect.setText("生成查询(select)");

		btninsert = new Button(container, SWT.CHECK);
		btninsert.setSelection(true);
		btninsert.setBounds(23, 71, 121, 17);
		btninsert.setText("生成插入(insert)");

		btnupdate = new Button(container, SWT.CHECK);
		btnupdate.setSelection(true);
		btnupdate.setBounds(23, 119, 121, 17);
		btnupdate.setText("生成更新(update)");

		btndelete = new Button(container, SWT.CHECK);
		btndelete.setSelection(true);
		btndelete.setBounds(23, 164, 121, 17);
		btndelete.setText("生成删除(delete)");

		btnjava = new Button(container, SWT.CHECK);
		btnjava.setSelection(true);
		btnjava.setBounds(23, 213, 98, 17);
		btnjava.setText("生成Java代码");
	}
	
	/**
	 * 是否创建查询
	 */
	public boolean isBuildSelect() {
		return btnselect.getSelection();
	}
	
	/**
	 * 是否创建插入
	 */
	public boolean isBuildInsert() {
		return btninsert.getSelection();
	}

	/**
	 * 是否创建更新
	 */
	public boolean isBuildUpdate() {
		return btnupdate.getSelection();
	}

	/**
	 * 是否创建删除
	 */
	public boolean isBuildDelete() {
		return btndelete.getSelection();
	}

	/**
	 * 是否创建 JAVA代码
	 */
	public boolean isBuildJava() {
		return btnjava.getSelection();
	}

	/**
	 * 判断是否完成
	 */
	private void decidePageComplete() {
		if (btnselect.getSelection() || btninsert.getSelection() || btnupdate.getSelection() || btndelete.getSelection()) {
			setPageComplete(true);
		} else {
			setPageComplete(false);
		}
	}

	private class SelectMouseAdapter extends MouseAdapter {
		@Override
		public void mouseUp(MouseEvent e) {
			decidePageComplete();
		}
	}
}
