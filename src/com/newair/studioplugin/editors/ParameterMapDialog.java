package com.newair.studioplugin.editors;

import java.awt.Toolkit;

import org.apache.commons.lang.StringUtils;
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

public class ParameterMapDialog extends Dialog {

	protected Object result;
	protected Shell shell;
	private boolean newNodeFlag = true;
	private Element paraElement = null;
	private Text textProperty;
	private Text textTypeHandler;
	private Button butOk;
	private Button butCancel;
	private Combo textJavaType;
	private Combo textJdbcType;
	private Label lblScale;
	private Text textScale;
	private Text textResultMap;
	private Combo textMode;

	/**
	 * Create the dialog.
	 * 
	 * @param parent
	 * @param style
	 */
	public ParameterMapDialog(Shell parent, int style) {
		super(parent, style);
		setText("SWT Dialog");
	}

	/**
	 * Open the dialog.
	 * 
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
		shell.setSize(464, 206);
		shell.setText("结果映射配置");
		shell.setLayout(null);

		Label label = new Label(shell, SWT.RIGHT);
		label.setToolTipText("property");
		label.setBounds(22, 10, 60, 17);
		label.setText("参数名称：");

		Label label_1 = new Label(shell, SWT.RIGHT);
		label_1.setToolTipText("mode");
		label_1.setBounds(234, 10, 60, 17);
		label_1.setText("模式：");

		Label lblJdbc = new Label(shell, SWT.RIGHT);
		lblJdbc.setToolTipText("jdbcType");
		lblJdbc.setBounds(10, 42, 72, 17);
		lblJdbc.setText("JDBC类型：");

		Label lblJava = new Label(shell, SWT.RIGHT);
		lblJava.setToolTipText("javaType");
		lblJava.setBounds(229, 42, 65, 17);
		lblJava.setText("JAVA类型：");

		Label label_2 = new Label(shell, SWT.RIGHT);
		label_2.setToolTipText("resultMap");
		label_2.setBounds(21, 75, 61, 17);
		label_2.setText("映射对象：");

		lblScale = new Label(shell, SWT.RIGHT);
		lblScale.setToolTipText("scale");
		lblScale.setBounds(233, 75, 61, 17);
		lblScale.setText("比例：");

		Label label_3 = new Label(shell, SWT.RIGHT);
		label_3.setBounds(10, 108, 72, 17);
		label_3.setText("类型处理器：");

		textProperty = new Text(shell, SWT.BORDER);
		textProperty.setBounds(82, 6, 140, 25);

		textMode = new Combo(shell, SWT.NONE);
		textMode.setItems(new String[] { "IN", "OUT" });
		textMode.setBounds(300, 6, 140, 25);

		textJdbcType = new Combo(shell, SWT.NONE);
		textJdbcType.setItems(new String[] { "BIGINT", "BINARY", "BIT", "BLOB", "BOOLEAN", "CHAR", "CLOB", "CURSOR", "DATE", "DECIMAL", "DOUBLE",
				"FLOAT", "INTEGER", "LONGVARBINARY", "LONGVARCHAR", "NCHAR", "NCLOB", "NULL", "NUMERIC", "NVARCHAR", "OTHER", "REAL", "SMALLINT",
				"TIME", "TIMESTAMP", "TINYINT", "UNDEFINED", "VARBINARY", "VARCHAR" });
		textJdbcType.setBounds(82, 38, 140, 25);

		textJavaType = new Combo(shell, SWT.NONE);
		textJavaType.setItems(new String[] { "ArrayList", "BigDecimal", "boolean", "Boolean", "byte", "Byte", "Collection", "Date", "double",
				"Double", "float", "Float", "HashMap", "int", "Integer", "Iterator", "List", "long", "Long", "Map", "Object", "short", "Short",
				"string", "String" });
		textJavaType.setBounds(300, 38, 140, 25);

		textResultMap = new Text(shell, SWT.BORDER);
		textResultMap.setBounds(82, 71, 140, 25);

		textScale = new Text(shell, SWT.BORDER);
		textScale.setBounds(300, 71, 140, 25);

		textTypeHandler = new Text(shell, SWT.BORDER);
		textTypeHandler.setBounds(82, 104, 358, 25);

		butOk = new Button(shell, SWT.NONE);
		butOk.addMouseListener(new ButOkMouseAdapter());
		butOk.setBounds(159, 141, 65, 27);
		butOk.setText("  确 定  ");

		butCancel = new Button(shell, SWT.NONE);
		butCancel.addMouseListener(new ButCancelMouseAdapter());
		butCancel.setBounds(246, 141, 65, 27);
		butCancel.setText("  取 消  ");

		bindData();
	}

	private void bindData() {
		if ((paraElement != null) && (paraElement instanceof Element)) {
			Element element = (Element)paraElement;
			StudioUtil.bindAttributeData(element, "property", textProperty);
			StudioUtil.bindAttributeData(element, "mode", textMode);
			StudioUtil.bindAttributeData(element, "jdbcType", textJdbcType);
			StudioUtil.bindAttributeData(element, "javaType", textJavaType);
			StudioUtil.bindAttributeData(element, "resultMap", textResultMap);
			StudioUtil.bindAttributeData(element, "scale", textScale);
			StudioUtil.bindAttributeData(element, "typeHandler", textTypeHandler);
		}
		// 居中设置
		int screenH = Toolkit.getDefaultToolkit().getScreenSize().height;
		int screenW = Toolkit.getDefaultToolkit().getScreenSize().width;
		int shellW = 200;
		int shellH = 300;
		shell.setLocation(((screenW - shellW) / 2), ((screenH - shellH) / 2));
	}

	private class ButOkMouseAdapter extends MouseAdapter {
		@Override
		public void mouseUp(MouseEvent e) {
			MessageBox mess = new MessageBox(shell, SWT.ICON_ERROR);
			if (StringUtils.isBlank(textProperty.getText())) {
				mess.setMessage("参数名称不能为空！");
				mess.open();
				return;
			}
			if (newNodeFlag) { // 新增的
				paraElement = DocumentHelper.createElement("result");
			}
			StudioUtil.updateAttribute(paraElement, "property", textProperty);
			StudioUtil.updateAttribute(paraElement, "mode", textMode);
			StudioUtil.updateAttribute(paraElement, "jdbcType", textJdbcType);
			StudioUtil.updateAttribute(paraElement, "javaType", textJavaType);
			StudioUtil.updateAttribute(paraElement, "resultMap", textResultMap);
			StudioUtil.updateAttribute(paraElement, "scale", textScale);
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
