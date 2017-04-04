package com.newair.studioplugin.wizard;

import org.apache.commons.lang.StringUtils;
import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.ModifyEvent;

import com.newair.studioplugin.ProjectConfig;
import com.newair.studioplugin.StudioUtil;
import org.eclipse.wb.swt.SWTResourceManager;

public class CreateMapperWizardPage extends WizardPage {
	private Text textFileName;
	private Text textNameSpace;
	private String relativePath;

	/**
	 * Create the wizard.
	 */
	public CreateMapperWizardPage() {
		super("CreateMapperWizardPage");
		setTitle("新建映射");
		setDescription("新建一般映射");
	}

	/**
	 * Create contents of the wizard.
	 * @param parent
	 */
	public void createControl(Composite parent) {
		Composite container = new Composite(parent, SWT.NULL);

		setControl(container);
		
		Label label = new Label(container, SWT.NONE);
		label.setBounds(20, 27, 61, 17);
		label.setText("文件名称：");
		
		textFileName = new Text(container, SWT.BORDER);
		textFileName.addModifyListener(new FileNameModifyListener());
		textFileName.setBounds(87, 24, 208, 23);
		
		Label label_1 = new Label(container, SWT.NONE);
		label_1.setBounds(20, 64, 61, 17);
		label_1.setText("命名空间：");
		
		textNameSpace = new Text(container, SWT.BORDER);
		textNameSpace.setBounds(87, 61, 444, 23);
		
		Label lblmapper = new Label(container, SWT.NONE);
		lblmapper.setForeground(SWTResourceManager.getColor(SWT.COLOR_WIDGET_FOREGROUND));
		lblmapper.setFont(SWTResourceManager.getFont("微软雅黑", 9, SWT.BOLD));
		lblmapper.setBounds(301, 27, 61, 20);
		lblmapper.setText("+Mapper");
	}
	
	public String getFileName() {
		String name = StringUtils.trim(textFileName.getText());
		String[] s = name.split("\\.");
		if (s.length >= 1) {
			name = s[0];
		}
		if (name.toLowerCase().indexOf("mapper") > 0) {
			name = name.substring(0, name.length() - 6) + "Mapper";
		} else {
			name = name + "Mapper";
		}
		if (StringUtils.isNotBlank(name)) {
			String[] ss = name.split("\\.");
			if (ss.length >= 1) {
				name = ss[0];
			}
			name = name + ".xml";
		}
		return name;
	}
	
	public String getNameSpace() {
		return StringUtils.trim(textNameSpace.getText());
	}

	public void setPath(String path) {
		this.relativePath = path;
	}

	public String getPath() {
		return relativePath;
	}
	
	private String getPackageName() {
		ProjectConfig config = StudioUtil.getConfig();
		String javaSrc = config.getJavaSrc();
		if (StringUtils.isNotBlank(javaSrc) && !javaSrc.endsWith("/")) {
			javaSrc += "/";
		}
		String path = relativePath.substring(javaSrc.length());
		String packagename = path.replaceAll("/", ".");
		packagename = StringUtils.replace(packagename, ".dao.mapper", ".dao." + config.getInterDir());
		return packagename;
	}
	
	private class FileNameModifyListener implements ModifyListener {
		public void modifyText(ModifyEvent e) {
			String packagename = getPackageName();
			String name = StringUtils.trim(textFileName.getText());
			String[] s = name.split("\\.");
			if (s.length >= 1) {
				name = s[0];
			}
			if (name.toLowerCase().indexOf("mapper") > 0) {
				name = name.substring(0, name.length() - 6) + "Mapper";
			} else {
				name = name + "Mapper";
			}
			textNameSpace.setText(packagename + "." + name);
		}
	}
}
