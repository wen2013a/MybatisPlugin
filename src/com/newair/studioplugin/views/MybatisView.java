package com.newair.studioplugin.views;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.sql.Connection;
import java.util.List;
import java.util.Properties;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.ResourcesPlugin;
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
import org.eclipse.swt.widgets.FileDialog;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.MenuItem;
import org.eclipse.swt.widgets.MessageBox;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.IMemento;
import org.eclipse.ui.IViewSite;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.part.ViewPart;

import com.newair.studioplugin.DataSourceCfg;
import com.newair.studioplugin.ProjectConfig;
import com.newair.studioplugin.StudioConst;
import com.newair.studioplugin.StudioUtil;
import com.newair.studioplugin.db.DbUtil;
import com.swtdesigner.ResourceManager;

public class MybatisView extends ViewPart {
	
	private static final Logger log = Logger.getLogger(MybatisView.class);

	private ProjectConfig config;
	private Text textDbConfigFile;
	private Text textDriverClassName;
	private Text textDatabaseUrl;
	private Text textDriverFile;
	private Text textUserName;
	private Text textPassword;
	private Text textMapperRoot;
	private Text textJavaSrc;
	private Text textIntfProject;
	private Text textIntfSrc;
	private Text textDataSource;
	private Menu menuNew;
	private Menu menuInftProject;
	private Menu menuDbConfigFile;
	private MenuItem menuLoadDbConfig;

	public MybatisView() {
	}

	@Override
	public void createPartControl(Composite arg0) {
		arg0.setLayout(new FormLayout());

		Label label_6 = new Label(arg0, SWT.RIGHT);
		FormData fd_label_6 = new FormData();
		fd_label_6.top = new FormAttachment(0, 10);
		fd_label_6.left = new FormAttachment(0, 0);
		fd_label_6.right = new FormAttachment(0, 100);
		label_6.setLayoutData(fd_label_6);
		label_6.setText("数据库配置文件：");

		textDbConfigFile = new Text(arg0, SWT.BORDER);
		FormData fd_textDbConfigFile = new FormData();
		fd_textDbConfigFile.top = new FormAttachment(label_6, -5, SWT.TOP);
		fd_textDbConfigFile.bottom = new FormAttachment(label_6, 20, SWT.TOP);
		fd_textDbConfigFile.left = new FormAttachment(label_6, 0, SWT.RIGHT);
		fd_textDbConfigFile.right = new FormAttachment(100, -35);
		textDbConfigFile.setLayoutData(fd_textDbConfigFile);

		Button butSelectDbConfigFile = new Button(arg0, SWT.CENTER);
		butSelectDbConfigFile.addMouseListener(new ButSelectDbConfigFileMouseAdapter());
		butSelectDbConfigFile.setImage(ResourceManager.getPluginImage("StudioPlugin", "icons/editor/source-editor.png"));
		FormData fd_butSelectDbConfigFile = new FormData();
		fd_butSelectDbConfigFile.top = new FormAttachment(label_6, -5, SWT.TOP);
		fd_butSelectDbConfigFile.bottom = new FormAttachment(label_6, 20, SWT.TOP);
		fd_butSelectDbConfigFile.left = new FormAttachment(textDbConfigFile, 0, SWT.RIGHT);
		fd_butSelectDbConfigFile.right = new FormAttachment(100, -10);
		butSelectDbConfigFile.setLayoutData(fd_butSelectDbConfigFile);

		Label label_3 = new Label(arg0, SWT.RIGHT);
		FormData fd_label_3 = new FormData();
		fd_label_3.top = new FormAttachment(0, 40);
		fd_label_3.left = new FormAttachment(0, 10);
		fd_label_3.right = new FormAttachment(0, 100);
		label_3.setLayoutData(fd_label_3);
		label_3.setText("数据库驱动：");

		textDriverFile = new Text(arg0, SWT.BORDER);
		FormData fd_textDriverFile = new FormData();
		fd_textDriverFile.top = new FormAttachment(label_3, -5, SWT.TOP);
		fd_textDriverFile.bottom = new FormAttachment(label_3, 20, SWT.TOP);
		fd_textDriverFile.left = new FormAttachment(label_3, 0, SWT.RIGHT);
		fd_textDriverFile.right = new FormAttachment(100, -35);
		textDriverFile.setLayoutData(fd_textDriverFile);

		Button butSelectDriver = new Button(arg0, SWT.CENTER);
		butSelectDriver.addMouseListener(new SelectDriveMouseAdapterr());
		butSelectDriver.setImage(ResourceManager.getPluginImage("StudioPlugin", "icons/editor/source-editor.png"));
		FormData fd_butSelectDriver = new FormData();
		fd_butSelectDriver.top = new FormAttachment(label_3, -5, SWT.TOP);
		fd_butSelectDriver.bottom = new FormAttachment(label_3, 20, SWT.TOP);
		fd_butSelectDriver.left = new FormAttachment(textDriverFile, 0, SWT.RIGHT);
		fd_butSelectDriver.right = new FormAttachment(100, -10);
		butSelectDriver.setLayoutData(fd_butSelectDriver);

		Label label_2 = new Label(arg0, SWT.RIGHT);
		FormData fd_label_2 = new FormData();
		fd_label_2.top = new FormAttachment(0, 70);
		fd_label_2.left = new FormAttachment(0, 10);
		fd_label_2.right = new FormAttachment(0, 100);
		label_2.setLayoutData(fd_label_2);
		label_2.setText("驱动类名称：");

		textDriverClassName = new Text(arg0, SWT.BORDER);
		FormData fd_textDriverClassName = new FormData();
		fd_textDriverClassName.top = new FormAttachment(label_2, -5, SWT.TOP);
		fd_textDriverClassName.bottom = new FormAttachment(label_2, 20, SWT.TOP);
		fd_textDriverClassName.left = new FormAttachment(label_2, 0, SWT.RIGHT);
		fd_textDriverClassName.right = new FormAttachment(100, -10);
		textDriverClassName.setLayoutData(fd_textDriverClassName);

		Label lblurl = new Label(arg0, SWT.RIGHT);
		FormData fd_lblurl = new FormData();
		fd_lblurl.top = new FormAttachment(0, 100);
		fd_lblurl.left = new FormAttachment(0, 10);
		fd_lblurl.right = new FormAttachment(0, 100);
		lblurl.setLayoutData(fd_lblurl);
		lblurl.setText("数据连接串：");

		textDatabaseUrl = new Text(arg0, SWT.BORDER);
		FormData fd_textDatabaseUrl = new FormData();
		fd_textDatabaseUrl.top = new FormAttachment(lblurl, -5, SWT.TOP);
		fd_textDatabaseUrl.bottom = new FormAttachment(lblurl, 20, SWT.TOP);
		fd_textDatabaseUrl.left = new FormAttachment(lblurl, 0, SWT.RIGHT);
		fd_textDatabaseUrl.right = new FormAttachment(100, -10);
		textDatabaseUrl.setLayoutData(fd_textDatabaseUrl);

		Label label = new Label(arg0, SWT.RIGHT);
		FormData fd_label = new FormData();
		fd_label.top = new FormAttachment(0, 130);
		fd_label.left = new FormAttachment(0, 10);
		fd_label.right = new FormAttachment(0, 100);
		label.setLayoutData(fd_label);
		label.setText("用户名：");

		textUserName = new Text(arg0, SWT.BORDER);
		FormData fd_textUserName = new FormData();
		fd_textUserName.top = new FormAttachment(label, -5, SWT.TOP);
		fd_textUserName.bottom = new FormAttachment(label, 20, SWT.TOP);
		fd_textUserName.left = new FormAttachment(label, 0, SWT.RIGHT);
		fd_textUserName.right = new FormAttachment(label, 120, SWT.RIGHT);
		textUserName.setLayoutData(fd_textUserName);

		Label label_1 = new Label(arg0, SWT.RIGHT);
		FormData fd_label_1 = new FormData();
		fd_label_1.top = new FormAttachment(0, 160);
		fd_label_1.left = new FormAttachment(0, 0);
		fd_label_1.right = new FormAttachment(0, 100);
		label_1.setLayoutData(fd_label_1);
		label_1.setText("密码：");

		textPassword = new Text(arg0, SWT.BORDER | SWT.PASSWORD);
		FormData fd_textPassword = new FormData();
		fd_textPassword.top = new FormAttachment(label_1, -5, SWT.TOP);
		fd_textPassword.bottom = new FormAttachment(label_1, 20, SWT.TOP);
		fd_textPassword.left = new FormAttachment(label_1, 0, SWT.RIGHT);
		fd_textPassword.right = new FormAttachment(label_1, 120, SWT.RIGHT);
		textPassword.setLayoutData(fd_textPassword);

		Label label_4 = new Label(arg0, SWT.RIGHT);
		FormData fd_label_4 = new FormData();
		fd_label_4.top = new FormAttachment(0, 190);
		fd_label_4.left = new FormAttachment(0, 0);
		fd_label_4.right = new FormAttachment(0, 100);
		label_4.setLayoutData(fd_label_4);
		label_4.setText("映射文件目录：");

		textMapperRoot = new Text(arg0, SWT.BORDER);
		FormData fd_textMapperRoot = new FormData();
		fd_textMapperRoot.top = new FormAttachment(label_4, -5, SWT.TOP);
		fd_textMapperRoot.bottom = new FormAttachment(label_4, 20, SWT.TOP);
		fd_textMapperRoot.left = new FormAttachment(label_4, 0, SWT.RIGHT);
		fd_textMapperRoot.right = new FormAttachment(label_4, 120, SWT.RIGHT);
		textMapperRoot.setLayoutData(fd_textMapperRoot);

		Label lblJava = new Label(arg0, SWT.RIGHT);
		FormData fd_lblJava = new FormData();
		fd_lblJava.top = new FormAttachment(0, 220);
		fd_lblJava.left = new FormAttachment(0, 0);
		fd_lblJava.right = new FormAttachment(0, 100);
		lblJava.setLayoutData(fd_lblJava);
		lblJava.setText("Java代码目录：");

		textJavaSrc = new Text(arg0, SWT.BORDER);
		FormData fd_textJavaSrc = new FormData();
		fd_textJavaSrc.top = new FormAttachment(lblJava, -5, SWT.TOP);
		fd_textJavaSrc.bottom = new FormAttachment(lblJava, 20, SWT.TOP);
		fd_textJavaSrc.left = new FormAttachment(lblJava, 0, SWT.RIGHT);
		fd_textJavaSrc.right = new FormAttachment(lblJava, 120, SWT.RIGHT);
		textJavaSrc.setLayoutData(fd_textJavaSrc);

		Label label_7 = new Label(arg0, SWT.RIGHT);
		FormData fd_label_7 = new FormData();
		fd_label_7.top = new FormAttachment(0, 250);
		fd_label_7.left = new FormAttachment(0, 0);
		fd_label_7.right = new FormAttachment(0, 100);
		label_7.setLayoutData(fd_label_7);
		label_7.setText("Java接口工程：");

		textIntfProject = new Text(arg0, SWT.BORDER);
		textIntfProject.setEditable(false);
		FormData fd_textItfProject = new FormData();
		fd_textItfProject.top = new FormAttachment(label_7, -5, SWT.TOP);
		fd_textItfProject.bottom = new FormAttachment(label_7, 20, SWT.TOP);
		fd_textItfProject.left = new FormAttachment(label_7, 0, SWT.RIGHT);
		fd_textItfProject.right = new FormAttachment(label_7, 120, SWT.RIGHT);
		textIntfProject.setLayoutData(fd_textItfProject);

		Label label_IntfPath = new Label(arg0, SWT.RIGHT);
		FormData fd_label_IntfPath = new FormData();
		fd_label_IntfPath.top = new FormAttachment(0, 280);
		fd_label_IntfPath.left = new FormAttachment(0, 0);
		fd_label_IntfPath.right = new FormAttachment(0, 100);
		label_IntfPath.setLayoutData(fd_label_IntfPath);
		label_IntfPath.setText("Java接口目录：");

		textIntfSrc = new Text(arg0, SWT.BORDER);
		FormData fd_textItfPath = new FormData();
		fd_textItfPath.top = new FormAttachment(label_IntfPath, -5, SWT.TOP);
		fd_textItfPath.bottom = new FormAttachment(label_IntfPath, 20, SWT.TOP);
		fd_textItfPath.left = new FormAttachment(label_IntfPath, 0, SWT.RIGHT);
		fd_textItfPath.right = new FormAttachment(label_IntfPath, 120, SWT.RIGHT);
		textIntfSrc.setLayoutData(fd_textItfPath);

		Button butIntfProject = new Button(arg0, SWT.CENTER);
		butIntfProject.addMouseListener(new SelectIntfProjectAdapterr());
		butIntfProject.setImage(ResourceManager.getPluginImage("StudioPlugin", "icons/editor/source-editor.png"));
		FormData fd_butIntfProject = new FormData();
		fd_butIntfProject.top = new FormAttachment(label_7, -5, SWT.TOP);
		fd_butIntfProject.bottom = new FormAttachment(label_7, 20, SWT.TOP);
		fd_butIntfProject.left = new FormAttachment(textIntfProject, 0, SWT.RIGHT);
		fd_butIntfProject.right = new FormAttachment(textIntfProject, 25, SWT.RIGHT);
		butIntfProject.setLayoutData(fd_butIntfProject);

		Label label_5 = new Label(arg0, SWT.RIGHT);
		FormData fd_label_5 = new FormData();
		fd_label_5.top = new FormAttachment(0, 310);
		fd_label_5.left = new FormAttachment(0, 0);
		fd_label_5.right = new FormAttachment(0, 100);
		label_5.setLayoutData(fd_label_5);
		label_5.setText("数据源：");

		textDataSource = new Text(arg0, SWT.BORDER);
		FormData fd_text = new FormData();
		fd_text.top = new FormAttachment(label_5, -5, SWT.TOP);
		fd_text.bottom = new FormAttachment(label_5, 20, SWT.TOP);
		fd_text.left = new FormAttachment(label_5, 0, SWT.RIGHT);
		fd_text.right = new FormAttachment(label_5, 120, SWT.RIGHT);
		textDataSource.setLayoutData(fd_text);

		Button button = new Button(arg0, SWT.NONE);
		button.addMouseListener(new InitSelectDataSourceMenuMouseAdapter());
		button.setToolTipText("选择当前数据源");
		FormData fd_button = new FormData();
		fd_button.top = new FormAttachment(0, 340);
		fd_button.left = new FormAttachment(0, 10);
		fd_button.right = new FormAttachment(0, 80);
		button.setLayoutData(fd_button);
		button.setText("选数据源");

		Button button_1 = new Button(arg0, SWT.NONE);
		button_1.addMouseListener(new SaveConfigMouseAdapter());
		FormData fd_button_1 = new FormData();
		fd_button_1.top = new FormAttachment(button, 0, SWT.TOP);
		fd_button_1.bottom = new FormAttachment(button, 0, SWT.BOTTOM);
		fd_button_1.left = new FormAttachment(button, 10, SWT.RIGHT);
		fd_button_1.right = new FormAttachment(button, 80, SWT.RIGHT);
		button_1.setLayoutData(fd_button_1);
		button_1.setText("保存配置");

		Button button_2 = new Button(arg0, SWT.NONE);
		button_2.addMouseListener(new ConnectTestMouseAdapter());
		FormData fd_button_2 = new FormData();
		fd_button_2.top = new FormAttachment(button_1, 0, SWT.TOP);
		fd_button_2.bottom = new FormAttachment(button_1, 0, SWT.BOTTOM);
		fd_button_2.left = new FormAttachment(button_1, 10, SWT.RIGHT);
		fd_button_2.right = new FormAttachment(button_1, 80, SWT.RIGHT);
		button_2.setLayoutData(fd_button_2);
		button_2.setText("测试连接");

		menuNew = new Menu(button);
		button.setMenu(menuNew);

		menuInftProject = new Menu(butIntfProject);
		butIntfProject.setMenu(menuInftProject);

		menuDbConfigFile = new Menu(butSelectDbConfigFile);
		butSelectDbConfigFile.setMenu(menuDbConfigFile);

		MenuItem menuOpenDbConfigFile = new MenuItem(menuDbConfigFile, SWT.NONE);
		menuOpenDbConfigFile.addSelectionListener(new SelectDbConfigFileAdapter());
		menuOpenDbConfigFile.setText("选择配置文件");

		menuLoadDbConfig = new MenuItem(menuDbConfigFile, SWT.NONE);
		menuLoadDbConfig.addSelectionListener(new SelectLoadDbConfigAdapterr());
		menuLoadDbConfig.setText("加载配置");

		ProjectConfig config = StudioUtil.getConfig();
		showConfig(config);
	}

	@Override
	public void setFocus() {
		config = StudioUtil.getConfig();
		showConfig(config);
	}

	@Override
	public void init(IViewSite site) throws PartInitException {
		super.init(site);
	}

	@Override
	public void init(IViewSite site, IMemento memento) throws PartInitException {
		super.init(site, memento);
	}

	/**
	 * 配置显示到窗口上
	 */
	private void showConfig(ProjectConfig config) {
		textDbConfigFile.setText(getValue(config.getDbConfigFile()));
		textDriverFile.setText(getValue(config.getDriverFile()));
		textDriverClassName.setText(getValue(config.getDriverClass()));
		textDatabaseUrl.setText(getValue(config.getUrl()));
		textUserName.setText(getValue(config.getUsername()));
		textPassword.setText(getValue(config.getPassword()));
		textMapperRoot.setText(getValue(config.getMapperRoot()));
		textJavaSrc.setText(getValue(config.getJavaSrc()));
		textIntfProject.setText(getValue(config.getIntfProject()));
		textIntfSrc.setText(getValue(config.getIntfSrc()));
		textDataSource.setText(getValue(config.getDataSource()));

		textDataSource.setToolTipText(textDataSource.getText());
		textDriverFile.setToolTipText(textDriverFile.getText());
		textDriverClassName.setToolTipText(textDriverClassName.getText());
		textDatabaseUrl.setToolTipText(textDatabaseUrl.getText());
		textUserName.setToolTipText(textUserName.getText());
		textJavaSrc.setToolTipText(textJavaSrc.getText());
		textMapperRoot.setToolTipText(textMapperRoot.getText());
		textDbConfigFile.setToolTipText(textDbConfigFile.getText());
		textIntfProject.setToolTipText(textIntfProject.getText());
		textIntfSrc.setToolTipText(textIntfSrc.getText());
	}

	/**
	 * 更新窗口界面上的值到配置对象
	 */
	private void updateConfig() {
		boolean setDefault = false;
		IProject proj = StudioUtil.getCurrentProject();
		boolean isMavenPrj = StudioUtil.isMavenProject(proj);

		config.setDbConfigFile(getValue(textDbConfigFile.getText()));
		config.setMapperRoot(getValue(textMapperRoot.getText()));
		config.setJavaSrc(getValue(textJavaSrc.getText()));
		config.setIntfProject(getValue(textIntfProject.getText()));
		config.setIntfSrc(getValue(textIntfSrc.getText()));
		config.setDataSource(getValue(textDataSource.getText()));

		if (StringUtils.isBlank(config.getDataSource())) {
			config.setDataSource("MyDataSource");
			setDefault = true;
		}
		if (StringUtils.isBlank(config.getMapperRoot())) {
			config.setMapperRoot(isMavenPrj ? "src/main/java" : "src");
			setDefault = true;
		}
		if (StringUtils.isBlank(config.getJavaSrc())) {
			config.setJavaSrc(isMavenPrj ? "src/main/java" : "src");
			setDefault = true;
		}
		if (StringUtils.isBlank(config.getIntfSrc())) {
			config.setIntfSrc(isMavenPrj ? "src/main/java" : "src");
			setDefault = true;
		}
		if (StringUtils.isNotBlank(textDataSource.getText())) {
			DataSourceCfg datasourcecfg = new DataSourceCfg();
			datasourcecfg.setDatasource(getValue(textDataSource.getText()));
			datasourcecfg.setDriverFile(getValue(textDriverFile.getText()));
			datasourcecfg.setDriverClass(getValue(textDriverClassName.getText()));
			datasourcecfg.setUrl(getValue(textDatabaseUrl.getText()));
			datasourcecfg.setUsername(getValue(textUserName.getText()));
			datasourcecfg.setPassword(getValue(textPassword.getText()));
			config.addDataSource(datasourcecfg);
		}
		if (setDefault) {
			showConfig(config);
		}
	}

	private String getValue(String value) {
		return StringUtils.isNotBlank(value) ? StringUtils.trim(value) : "";
	}
	
	/**
	 * 加载数据库配置
	 */
	private void loadDbConfgFile(String filename) {
		try {
			IProject proj = StudioUtil.getCurrentProject();
			if (filename.startsWith(".settings/")) {
				IFile cfgfile1 = proj.getFile(filename);
				filename = ((IFile) cfgfile1).getLocation().makeAbsolute().toFile().getAbsolutePath();
			}
			log.debug("数据库配置文件:" + filename);
			File file = new File(filename);
			if (!file.exists()) {
				return;
			}
			InputStream input = new FileInputStream(filename);
			Properties prop = new Properties();
			prop.load(input);
			String olddatasource = config.getDataSource();
			String driverFile = "";
			for (int i = 0; i < config.getDataSources().size(); i++) {
				DataSourceCfg datasource = config.getDataSources().get(i);
				if (StringUtils.isNotBlank(datasource.getDriverFile())) {
					driverFile = datasource.getDriverFile();
					break;
				}
			}
			config.clearDataSources();
			boolean flag = false;
			String DATASOURCES = prop.getProperty("DATASOURCES");
			if (StringUtils.isNotEmpty(DATASOURCES)) {
				String[] dataSources = DATASOURCES.split(",");
				for (int i = 0; i < dataSources.length; i++) {
					String ds = dataSources[i];
					DataSourceCfg datasource = config.getDataSource(ds);
					if (datasource == null) { 
						datasource = new DataSourceCfg();
					}
					datasource.setDatasource(ds);
					datasource.setDriverClass(prop.getProperty(ds + ".driverClassName", ""));
					datasource.setUrl(prop.getProperty(ds + ".url", ""));
					datasource.setUsername(prop.getProperty(ds + ".username", ""));
					datasource.setPassword(prop.getProperty(ds + ".password", ""));
					if (StringUtils.isBlank(datasource.getDriverFile())) {
						datasource.setDriverFile(driverFile);
					}
					config.addDataSource(datasource);
					if (datasource.getDatasource().equals(olddatasource)) {
						flag = true;
					}
				}
			}
			DataSourceCfg currdatasource = null;
			if (flag) {
				currdatasource = config.getDataSource(olddatasource);
			} else {
				if (config.getDataSources().size() > 0) {
					currdatasource = config.getDataSources().get(0);
				}
			}
			if (currdatasource != null) {
				config.setDataSource(currdatasource.getDatasource());
				textDataSource.setText(currdatasource.getDatasource());
				textDriverClassName.setText(currdatasource.getDriverClass());
				textDatabaseUrl.setText(currdatasource.getUrl());
				textUserName.setText(currdatasource.getUsername());
				textPassword.setText(currdatasource.getPassword());
			}
			input.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
			log.error(e.getMessage());
		} catch (Exception e) {
			e.printStackTrace();
			log.error(e.getMessage());
		}
	}

	/**
	 * 连接测试
	 */
	private class ConnectTestMouseAdapter extends MouseAdapter {
		@Override
		public void mouseUp(MouseEvent e) {
			updateConfig();
			try {
				Connection con = DbUtil.getConnection(config.getDriverFile(), config.getDriverClass(),
						config.getUrl(), config.getUsername(), config.getPassword());
				con.close();
			} catch (Exception e1) {
				MessageBox mess = new MessageBox(getSite().getShell(), SWT.ICON_ERROR | SWT.CANCEL);
				mess.setMessage("建立JDBC连接错误！\n" + e1.getMessage());
				mess.open();
				return;
			}
			MessageBox mess = new MessageBox(getSite().getShell(), SWT.ICON_INFORMATION | SWT.OK);
			mess.setMessage("建立JDBC连接成功！");
			mess.open();
		}
	}

	/**
	 * 初始化数据源选择弹出菜单
	 */
	private class InitSelectDataSourceMenuMouseAdapter extends MouseAdapter {
		@Override
		public void mouseUp(MouseEvent e) {
			MenuItem[] items = menuNew.getItems();
			for (int i = 0; i < items.length; i++) {
				items[i].dispose();
			}
			List<DataSourceCfg> dataSources = config.getDataSources();
			for (int i = 0; i < dataSources.size(); i++) {
				DataSourceCfg datasource = dataSources.get(i);
				MenuItem menuItem = new MenuItem(menuNew, SWT.NONE);
				menuItem.setImage(ResourceManager.getPluginImage("StudioPlugin", StudioConst.ICON_DATASOURCE));
				menuItem.addSelectionListener(new BtnNewMenuItemSelectionAdapter());
				menuItem.setText(datasource.getDatasource());
			}
			menuNew.setLocation(e.display.getCursorLocation());
			menuNew.setVisible(true);
		}
	}
	
	/**
	 * 数据库配置文件按钮点击事件
	 */
	private class ButSelectDbConfigFileMouseAdapter extends MouseAdapter {
		@Override
		public void mouseUp(MouseEvent e) {
			menuDbConfigFile.setLocation(e.display.getCursorLocation());
			menuDbConfigFile.setVisible(true);
			String dbconfigfile = textDbConfigFile.getText();
			if (StringUtils.isNotBlank(dbconfigfile)) {
				if (dbconfigfile.startsWith(".settings/")) {
					IProject proj = StudioUtil.getCurrentProject();
					IFile cfgfile1 = proj.getFile(dbconfigfile);
					dbconfigfile = ((IFile) cfgfile1).getLocation().makeAbsolute().toFile().getAbsolutePath();
				}
				File file = new File(dbconfigfile);
				menuLoadDbConfig.setEnabled(file.exists());
			} else {
				menuLoadDbConfig.setEnabled(false);
			}
		}
	}

	/**
	 * 数据源选择菜单事件
	 */
	private class BtnNewMenuItemSelectionAdapter extends SelectionAdapter {
		@Override
		public void widgetSelected(SelectionEvent e) {
			if (!(e.getSource() instanceof MenuItem)) {
				return;
			}
			MenuItem menuItem = (MenuItem) e.getSource();
			String datasource = menuItem.getText();
			config.setDataSource(datasource);
			showConfig(config);
			StudioUtil.saveConfig(config);
		}
	}

	/**
	 * 初始化接口工程选择弹出菜单
	 */
	private class SelectIntfProjectAdapterr extends MouseAdapter {
		@Override
		public void mouseUp(MouseEvent e) {
			MenuItem[] items = menuInftProject.getItems();
			for (int i = 0; i < items.length; i++) {
				items[i].dispose();
			}
			IProject[] allProjects = ResourcesPlugin.getWorkspace().getRoot().getProjects();
			for (int i = 0; i < allProjects.length; i++) {
				IProject project = allProjects[i];
				MenuItem menuItem = new MenuItem(menuInftProject, SWT.NONE);
				menuItem.setImage(ResourceManager.getPluginImage("StudioPlugin", StudioConst.ICON_INTF_PROJECT));
				menuItem.addSelectionListener(new BtnSelectIntfProjectAdapterr());
				menuItem.setText(project.getName());
			}
			if (StringUtils.isNotBlank(textIntfProject.getText())) {
				MenuItem menuItem = new MenuItem(menuInftProject, SWT.NONE);
				menuItem.addSelectionListener(new BtnSelectIntfProjectAdapterr());
				menuItem.setText("清除");
				menuItem.setData(new Integer(100));
			}
			menuInftProject.setLocation(e.display.getCursorLocation());
			menuInftProject.setVisible(true);
			showConfig(config);
		}
	}

	/**
	 * 接口工程选择菜单事件
	 */
	private class BtnSelectIntfProjectAdapterr extends SelectionAdapter {
		@Override
		public void widgetSelected(SelectionEvent e) {
			if (!(e.getSource() instanceof MenuItem)) {
				return;
			}
			MenuItem menuItem = (MenuItem) e.getSource();
			if ((menuItem.getData() instanceof Integer) && ((Integer)menuItem.getData()).intValue() == 100 ) {
				textIntfProject.setText("");
				return;
			}
			String projectname = menuItem.getText();
			config.setIntfProject(projectname);
			textIntfProject.setText(projectname);
		}
	}

	/**
	 * 保存配置
	 */
	private class SaveConfigMouseAdapter extends MouseAdapter {
		@Override
		public void mouseUp(MouseEvent e) {
			updateConfig();
			StudioUtil.saveConfig(config);
		}
	}

	/**
	 * 选择驱动程序文件事件
	 */
	private class SelectDriveMouseAdapterr extends MouseAdapter {
		@Override
		public void mouseUp(MouseEvent e) {
			IProject proj = StudioUtil.getCurrentProject();
			FileDialog fileDialog = new FileDialog(getSite().getShell(), SWT.OPEN);
			fileDialog.setFilterPath(proj.getLocation().toString());
			fileDialog.setFilterExtensions(new String[] { "*.jar" });
			String filename = fileDialog.open();
			if (StringUtils.isNotBlank(filename)) {
				textDriverFile.setText(filename);
				DataSourceCfg dataSourceCfg = config.getDataSource(config.getDataSource());
				if (dataSourceCfg != null) {
					dataSourceCfg.setDriverFile(filename);
				}
			}
		}
	}

	/**
	 * 选择数据库配置文件事件
	 */
	private class SelectDbConfigFileAdapter extends SelectionAdapter {
		@Override
		public void widgetSelected(SelectionEvent e) {
			IProject proj = StudioUtil.getCurrentProject();
			FileDialog fileDialog = new FileDialog(getSite().getShell(), SWT.OPEN);
			String defaultpath = textDbConfigFile.getText();
			if (StringUtils.isBlank(defaultpath)) {
				fileDialog.setFilterPath(proj.getLocation().toString());
			} else {
				fileDialog.setFilterPath(defaultpath);
			}
			fileDialog.setFilterExtensions(new String[] { "*.properties" });
			String filename = fileDialog.open();
			if (StringUtils.isNotBlank(filename)) {
				textDbConfigFile.setText(filename);
				loadDbConfgFile(filename);
				updateConfig();
				/*MessageBox messageBox = new MessageBox(getSite().getShell(), SWT.ICON_QUESTION | SWT.YES | SWT.NO);
				messageBox.setMessage("是否保存数据库连接配置？");
				int rc = messageBox.open();
				if (rc == SWT.YES) {
					StudioUtil.saveConfig(config);
				}*/
			}
		}
	}
	
	/**
	 * 加载数据库配置文件事件
	 */
	private class SelectLoadDbConfigAdapterr extends SelectionAdapter {
		@Override
		public void widgetSelected(SelectionEvent e) {
			if (StringUtils.isNotBlank(config.getDbConfigFile())) {
				loadDbConfgFile(config.getDbConfigFile());
				updateConfig();
			}
		}
	}
}
