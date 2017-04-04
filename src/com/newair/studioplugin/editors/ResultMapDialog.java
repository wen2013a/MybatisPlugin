package com.newair.studioplugin.editors;

import java.awt.Toolkit;

import org.apache.commons.lang.StringUtils;
import org.dom4j.Attribute;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;
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

import com.newair.studioplugin.StudioUtil;

public class ResultMapDialog extends Dialog {

	protected Object result;
	protected Shell shell;
	private boolean newNodeFlag = true;
	private Element paraElement = null;
	private Text textColumn;
	private Text textProperty;
	private Text textTypeHandler;
	private Button butOk;
	private Button butCancel;
	private Button butIsPrimaryKey;
	private Combo cbJavaType;
	private Combo cbJdbcType;

	/**
	 * Create the dialog.
	 * @param parent
	 * @param style
	 */
	public ResultMapDialog(Shell parent, int style) {
		super(parent, style);
		setText("SWT Dialog");
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
	
	public Object open(Element param) {
		newNodeFlag = false;
		paraElement = param;
		return open();
	}

	/**
	 * Create contents of the dialog.
	 */
	private void createContents() {
		shell = new Shell(getParent(), SWT.DIALOG_TRIM | SWT.PRIMARY_MODAL);
		shell.setSize(464, 212);
		shell.setText("结果映射配置");
		shell.setLayout(null);
		
		Label label = new Label(shell, SWT.NONE);
		label.setToolTipText("column");
		label.setBounds(22, 10, 60, 17);
		label.setText("参数名称：");
		
		Label label_1 = new Label(shell, SWT.NONE);
		label_1.setToolTipText("property");
		label_1.setBounds(234, 10, 60, 17);
		label_1.setText("映射名称：");
		
		Label lblJdbc = new Label(shell, SWT.RIGHT);
		lblJdbc.setToolTipText("jdbcType");
		lblJdbc.setBounds(10, 42, 72, 17);
		lblJdbc.setText("JDBC类型：");
		
		Label lblJava = new Label(shell, SWT.NONE);
		lblJava.setToolTipText("javaType");
		lblJava.setBounds(229, 42, 65, 17);
		lblJava.setText("JAVA类型：");
		
		Label label_3 = new Label(shell, SWT.NONE);
		label_3.setToolTipText("typeHandler");
		label_3.setBounds(10, 74, 72, 17);
		label_3.setText("类型处理器：");
		
		Label label_4 = new Label(shell, SWT.NONE);
		label_4.setBounds(10, 105, 61, 17);
		label_4.setText("是否主键：");
		
		textColumn = new Text(shell, SWT.BORDER);
		textColumn.setBounds(82, 5, 140, 27);
		
		textProperty = new Text(shell, SWT.BORDER);
		textProperty.setBounds(300, 5, 140, 27);
		
		cbJdbcType = new Combo(shell, SWT.NONE);
		cbJdbcType.setItems(new String[] {"BIGINT", "BINARY", "BIT", "BLOB", "BOOLEAN", "CHAR", "CLOB", "CURSOR", "DATE", "DECIMAL", "DOUBLE", "FLOAT", "INTEGER", "LONGVARBINARY", "LONGVARCHAR", "NCHAR", "NCLOB", "NULL", "NUMERIC", "NVARCHAR", "OTHER", "REAL", "SMALLINT", "TIME", "TIMESTAMP", "TINYINT", "UNDEFINED", "VARBINARY", "VARCHAR"});
		cbJdbcType.setBounds(82, 38, 140, 25);
		
		cbJavaType = new Combo(shell, SWT.NONE);
		cbJavaType.setItems(new String[] {"ArrayList", "BigDecimal", "boolean", "Boolean", "byte", "Byte", "Collection", "Date", "double", "Double", "float", "Float", "HashMap", "int", "Integer", "Iterator", "List", "long", "Long", "Map", "Object", "short", "Short", "String", "Timestamp"});
		cbJavaType.setBounds(300, 38, 140, 25);
		
		textTypeHandler = new Text(shell, SWT.BORDER);
		textTypeHandler.setBounds(82, 69, 358, 25);
		
		butIsPrimaryKey = new Button(shell, SWT.CHECK);
		butIsPrimaryKey.setBounds(82, 105, 22, 17);
		
		butOk = new Button(shell, SWT.NONE);
		butOk.addMouseListener(new ButOkMouseAdapter());
		butOk.setBounds(160, 136, 65, 27);
		butOk.setText("  确 定  ");
		
		butCancel = new Button(shell, SWT.NONE);
		butCancel.addMouseListener(new ButCancelMouseAdapter());
		butCancel.setBounds(247, 136, 65, 27);
		butCancel.setText("  取 消  ");
		
		bindData();
	}
	
	private void bindData() {
		if ((paraElement != null) && (paraElement instanceof Element)) {
			Element element = (Element)paraElement;
			if ("id".equals(element.getName())) {
				butIsPrimaryKey.setSelection(true);
			} else {
				butIsPrimaryKey.setSelection(false);
			}
			Attribute attr; 
			textColumn.setText(((attr = element.attribute("column")) != null) ? attr.getValue() : "");
			textProperty.setText(((attr = element.attribute("property")) != null) ? attr.getValue() : "");
			cbJdbcType.setText(((attr = element.attribute("jdbcType")) != null) ? attr.getValue() : "");
			cbJavaType.setText(((attr = element.attribute("javaType")) != null) ? attr.getValue() : "");
			textTypeHandler.setText(((attr = element.attribute("typeHandler")) != null) ? attr.getValue() : "");
		}
		//剧中设置
		int screenH = Toolkit.getDefaultToolkit().getScreenSize().height;
        int screenW = Toolkit.getDefaultToolkit().getScreenSize().width;
        int shellW = 100;
        int shellH = 50;
        shell.setLocation(((screenW - shellW) / 2), ((screenH - shellH) / 2));
	}
	
	private class ButOkMouseAdapter extends MouseAdapter {
		@Override
		public void mouseUp(MouseEvent e) {
			MessageBox mess = new MessageBox(shell, SWT.ICON_ERROR);
			if (StringUtils.isBlank(textColumn.getText())) {
				mess.setMessage("参数名称不能为空！");
				mess.open();
				return;
			} else if (StringUtils.isBlank(textProperty.getText())) {
				mess.setMessage("映射名称不能为空！");
				mess.open();
				return;
			}
			if (newNodeFlag) { //新增的
				paraElement = DocumentHelper.createElement("result");
			}
			if (butIsPrimaryKey.getSelection()) {
				paraElement.setName("id");
			} else {
				paraElement.setName("result");
			}
			StudioUtil.updateAttribute(paraElement, "column", textColumn);
			StudioUtil.updateAttribute(paraElement, "property", textProperty);
			StudioUtil.updateAttribute(paraElement, "jdbcType", cbJdbcType);
			StudioUtil.updateAttribute(paraElement, "javaType", cbJavaType);
			StudioUtil.updateAttribute(paraElement, "typeHandler", textTypeHandler);
			result = paraElement;
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
