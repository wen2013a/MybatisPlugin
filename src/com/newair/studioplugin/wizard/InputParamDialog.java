package com.newair.studioplugin.wizard;

import org.apache.commons.lang.StringUtils;
import org.eclipse.swt.widgets.Dialog;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.MessageBox;
import org.eclipse.swt.widgets.Text;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.events.MouseAdapter;
import org.eclipse.swt.events.MouseEvent;

public class InputParamDialog extends Dialog {

	protected Object result;
	protected Shell shell;
	private Text textParamName;
	private Button butOk;
	private Button butCancel;
	private Combo cbJdbcType;

	/**
	 * Create the dialog.
	 * @param parent
	 * @param style
	 */
	public InputParamDialog(Shell parent, int style) {
		super(parent, style);
		setText("插入参数");
	}

	/**
	 * Open the dialog.
	 * @return the result
	 */
	public Object open() {
		createContents();
		shell.open();
		shell.layout();
		Display display = getParent().getDisplay();
		while (!shell.isDisposed()) {
			if (!display.readAndDispatch()) {
				display.sleep();
			}
		}
		return result;
	}

	/**
	 * Create contents of the dialog.
	 */
	private void createContents() {
		shell = new Shell(getParent(), SWT.DIALOG_TRIM | SWT.PRIMARY_MODAL);
		shell.setSize(262, 155);
		shell.setText("插入参数");
		shell.setLayout(null);
		
		Label label = new Label(shell, SWT.NONE);
		label.setToolTipText("column");
		label.setBounds(22, 10, 60, 17);
		label.setText("参数名称：");
		
		Label lblJdbc = new Label(shell, SWT.RIGHT);
		lblJdbc.setToolTipText("jdbcType");
		lblJdbc.setBounds(10, 42, 72, 17);
		lblJdbc.setText("JDBC类型：");
		
		textParamName = new Text(shell, SWT.BORDER);
		textParamName.setBounds(82, 5, 140, 27);
		
		cbJdbcType = new Combo(shell, SWT.NONE);
		cbJdbcType.setItems(new String[] {"BIGINT", "BINARY", "BIT", "BLOB", "BOOLEAN", "CHAR", "CLOB", "CURSOR", "DATE", "DECIMAL", "DOUBLE", "FLOAT", "INTEGER", "LONGVARBINARY", "LONGVARCHAR", "NCHAR", "NCLOB", "NULL", "NUMERIC", "NVARCHAR", "OTHER", "REAL", "SMALLINT", "TIME", "TIMESTAMP", "TINYINT", "UNDEFINED", "VARBINARY", "VARCHAR"});
		cbJdbcType.setBounds(82, 38, 140, 25);
		
		butOk = new Button(shell, SWT.NONE);
		butOk.addMouseListener(new ButOkMouseAdapter());
		butOk.setBounds(54, 85, 65, 27);
		butOk.setText("  确 定  ");
		
		butCancel = new Button(shell, SWT.NONE);
		butCancel.addMouseListener(new ButCancelMouseAdapter());
		butCancel.setBounds(141, 85, 65, 27);
		butCancel.setText("  取 消  ");
	}
	
	private class ButOkMouseAdapter extends MouseAdapter {
		@Override
		public void mouseUp(MouseEvent e) {
			MessageBox mess = new MessageBox(shell, SWT.ICON_ERROR);
			if (StringUtils.isBlank(textParamName.getText())) {
				mess.setMessage("参数名称不能为空！");
				mess.open();
				return;
			}
			String s = "#{" + textParamName.getText();
			if (StringUtils.isNotBlank(cbJdbcType.getText())) {
				s += ",jdbcType=" + cbJdbcType.getText();
			}
			s += "}";
			result = s;
			shell.close();
		}
	}
	
	private class ButCancelMouseAdapter extends MouseAdapter {
		@Override
		public void mouseUp(MouseEvent e) {
			result = null;
			shell.close();
		}
	}
}
