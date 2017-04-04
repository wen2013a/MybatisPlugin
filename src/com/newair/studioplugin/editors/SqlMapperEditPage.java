package com.newair.studioplugin.editors;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.regex.Matcher;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.dom4j.Attribute;
import org.dom4j.CDATA;
import org.dom4j.CharacterData;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;
import org.dom4j.Node;
import org.dom4j.io.OutputFormat;
import org.dom4j.io.XMLWriter;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jface.text.DefaultUndoManager;
import org.eclipse.jface.text.Document;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.IUndoManager;
import org.eclipse.jface.text.source.SourceViewer;
import org.eclipse.jface.text.source.VerticalRuler;
import org.eclipse.jface.util.OpenStrategy;
import org.eclipse.jface.viewers.IStructuredContentProvider;
import org.eclipse.jface.viewers.ITableLabelProvider;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.TableViewerColumn;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.wizard.WizardDialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.StackLayout;
import org.eclipse.swt.custom.StyledText;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.MouseAdapter;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.layout.FormAttachment;
import org.eclipse.swt.layout.FormData;
import org.eclipse.swt.layout.FormLayout;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.MenuItem;
import org.eclipse.swt.widgets.MessageBox;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.swt.widgets.TableItem;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IEditorSite;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.forms.widgets.FormToolkit;
import org.eclipse.ui.ide.IDE;
import org.eclipse.ui.part.EditorPart;
import org.eclipse.ui.part.FileEditorInput;
import org.eclipse.ui.texteditor.IDocumentProvider;

import com.newair.studioplugin.ConfigCDATA;
import com.newair.studioplugin.ProjectConfig;
import com.newair.studioplugin.StudioConst;
import com.newair.studioplugin.StudioUtil;
import com.newair.studioplugin.buildcode.BuildUtil;
import com.newair.studioplugin.db.DbUtil;
import com.newair.studioplugin.db.Field;
import com.newair.studioplugin.editors.syntextcolor.TextEditorConfiguration;
import com.newair.studioplugin.editors.xmleditor.XMLDocumentProvider;
import com.newair.studioplugin.wizard.BuildSQLWizard;
import com.newair.studioplugin.wizard.BuildSelectSQLWizard;
import com.newair.studioplugin.wizard.IBuildSQL;
import com.newair.studioplugin.wizard.ModifyColumnWizard;
import com.swtdesigner.ResourceManager;
import com.swtdesigner.SWTResourceManager;

public class SqlMapperEditPage extends EditorPart {
	
	@SuppressWarnings("unused")
	private static final Logger log = Logger.getLogger(SqlMapperEditPage.class);
	
	private IDocumentProvider docProvider;
	private XMLMapperDocument xmlDoc;
	private boolean dirty = false;
	private Node preSelectSqlNode; // 先前选择的结点
	private Boolean sortResultMapByTypeAsc = null; // 结果映射按名称排序
	private Boolean sortPermeterMapByMode = null; // 参数映射列表按类型排序
	private Boolean sortPermeterMapByProperty = null; // 参数映射列表按名称排序

	private final FormToolkit formToolkit = new FormToolkit(Display.getDefault());
	private SourceViewer sqlTextViewer;
	private StyledText sqlText;
	private Table mapperTable;
	private TableViewer mapperTableViewer;
	private Button radMapperTable;
	private Button radMapperQuery;
	private StackLayout sl_composite;
	private Composite composite_sqlnode;
	private Composite composite_resultmap;
	private Composite composite;
	private Table resultMapTable;
	private TableViewer resultMapTableViewer;
	private Text textResultMapName;
	private Text textResultMapType;
	private Table parameterMapTable;
	private TableViewer parameterMapTableViewer;
	private Composite composite_parametermap;
	private Text textParaMapName;
	private Text textParaMapType;
	private Text tsqlId;
	private Text tsqlParameterType;
	private Text tsqlResultMap;
	private Menu menuNew;
	private Button btnNew;
	private Button btnDelete;
	private Button butSave;
	private MenuItem menuItemConfig;
	private MenuItem menuItemSqlInclude;
	private MenuItem menuItemResultMap;
	private MenuItem menuItemParaMap;
	private MenuItem menuItemSelect;
	private MenuItem menuItemInsert;
	private MenuItem menuItemUpdate;
	private MenuItem menuItemDelete;
	private Text textNamespace;
	private Text textInterMapperClass;
	private Text textTableName;
	private Text textDaoClass;
	private Text textBeanClass;
	private Composite composite_config;
	private Label lblTable;
	private Text tsqlDatabaseId;
	private Text tsqlParameterMap;
	private Text tsqlResultType;
	private Text tsqlResultSets;
	private Text tsqlResultOrdered;
	private Text tsqlTimeout;
	private Text tsqlFetchSize;
	private Text tsqlLang;
	private Text tsqlKeyColumn;
	private Text tsqlKeyProperty;
	private Label lblId;
	private Label lblResultmap;
	private Label lblTimeout;
	private Label lblResulttype;
	private Label lblDatabaseid;
	private Label lblParametermap;
	private Label lblParametertype;
	private Label lblUsegeneratedkeys;
	private Label lblStatementtype;
	private Label lblUsecache;
	private Label lblFlushcache;
	private Label lblLang;
	private Label lblResultsettype;
	private Label lblFetchsize;
	private Label lblResultordered;
	private Label lblResultsets;
	private Label lblKeycolumn;
	private Label lblKeyproperty;
	private Combo tsqlStatementType;
	private Combo tsqlUseCache;
	private Combo tsqlUseGeneratedKeys;
	private Combo tsqlResultSetType;
	private Combo tsqlFlushCache;
	private Label label_5;
	private Text textDataSource;
	private Button butBaseResultMapRefresh;

	/**
	 * @wbp.parser.constructor
	 * 
	 */
	public SqlMapperEditPage() {
		super();
	}

	/**
	 * 
	 */
	public SqlMapperEditPage(XMLDocumentProvider provider) {
		super();
		docProvider = provider;
	}

	@Override
	public void createPartControl(Composite parent) {
		parent.setLayout(new FormLayout());

		btnNew = new Button(parent, SWT.NONE);
		btnNew.addMouseListener(new ButNewMouseAdapter());
		FormData fd_btnNew = new FormData();
		fd_btnNew.left = new FormAttachment(0, 10);
		fd_btnNew.right = new FormAttachment(0, 80);
		fd_btnNew.top = new FormAttachment(100, -35);
		fd_btnNew.bottom = new FormAttachment(100, -7);
		btnNew.setLayoutData(fd_btnNew);
		formToolkit.adapt(btnNew, true, true);
		btnNew.setText("新增");

		btnDelete = new Button(parent, SWT.NONE);
		btnDelete.addMouseListener(new BtnDeleteMouseAdapter());
		FormData fd_btnDelete = new FormData();
		fd_btnDelete.left = new FormAttachment(0, 110);
		fd_btnDelete.right = new FormAttachment(0, 180);
		fd_btnDelete.top = new FormAttachment(btnNew, 0, SWT.TOP);
		fd_btnDelete.bottom = new FormAttachment(btnNew, 0, SWT.BOTTOM);
		btnDelete.setLayoutData(fd_btnDelete);
		formToolkit.adapt(btnDelete, true, true);
		btnDelete.setText("删除");

		butSave = new Button(parent, SWT.NONE);
		butSave.addMouseListener(new ButSaveMouseAdapter());
		FormData fd_butSave = new FormData();
		fd_butSave.left = new FormAttachment(btnNew, 210, SWT.LEFT);
		fd_butSave.right = new FormAttachment(btnNew, 280, SWT.LEFT);
		fd_butSave.top = new FormAttachment(btnNew, 0, SWT.TOP);
		fd_butSave.bottom = new FormAttachment(btnNew, 0, SWT.BOTTOM);		
		butSave.setLayoutData(fd_butSave);
		formToolkit.adapt(butSave, true, true);
		butSave.setText("保存");
		
		Button btnBuildInterCode = new Button(parent, SWT.NONE);
		FormData fd_btnBuildInterCode = new FormData();
		fd_btnBuildInterCode.left = new FormAttachment(btnNew, 310, SWT.LEFT);
		fd_btnBuildInterCode.right = new FormAttachment(btnNew, 390, SWT.LEFT);
		fd_btnBuildInterCode.top = new FormAttachment(btnNew, 0, SWT.TOP);
		fd_btnBuildInterCode.bottom = new FormAttachment(btnNew, 0, SWT.BOTTOM);
		btnBuildInterCode.setLayoutData(fd_btnBuildInterCode);
		btnBuildInterCode.addMouseListener(new BtnBuildInterCodeMouseAdapter());
		btnBuildInterCode.setText("生成接口代码");
		
		Button btnBuildDaoCode = new Button(parent, SWT.NONE);
		btnBuildDaoCode.setEnabled(false);
		btnBuildDaoCode.addMouseListener(new BtnBuildDaoCodeMouseAdapter());
		FormData fd_btnBuildDaoCode = new FormData();
		fd_btnBuildDaoCode.left = new FormAttachment(btnNew, 420, SWT.LEFT);
		fd_btnBuildDaoCode.right = new FormAttachment(btnNew, 500, SWT.LEFT);
		fd_btnBuildDaoCode.top = new FormAttachment(btnNew, 0, SWT.TOP);
		fd_btnBuildDaoCode.bottom = new FormAttachment(btnNew, 0, SWT.BOTTOM);
		btnBuildDaoCode.setLayoutData(fd_btnBuildDaoCode);
		btnBuildDaoCode.setText("生成DAO层");

		menuNew = new Menu(btnNew);
		btnNew.setMenu(menuNew);

		menuItemConfig = new MenuItem(menuNew, SWT.NONE);
		menuItemConfig.setImage(ResourceManager.getPluginImage("StudioPlugin", StudioConst.ICON_NODE_CFG_CDATA));
		menuItemConfig.addSelectionListener(new BtnNewMenuItemSelectionAdapter());
		menuItemConfig.setText("配置节点");

		menuItemSqlInclude = new MenuItem(menuNew, SWT.NONE);
		menuItemSqlInclude.setImage(ResourceManager.getPluginImage("StudioPlugin", StudioConst.ICON_NODE_SQL));
		menuItemSqlInclude.addSelectionListener(new BtnNewMenuItemSelectionAdapter());
		menuItemSqlInclude.setText("SQL引用");

		menuItemResultMap = new MenuItem(menuNew, SWT.NONE);
		menuItemResultMap.setImage(ResourceManager.getPluginImage("StudioPlugin", StudioConst.ICON_NODE_RESULT_MAP));
		menuItemResultMap.addSelectionListener(new BtnNewMenuItemSelectionAdapter());
		menuItemResultMap.setText("结果映射");

		menuItemParaMap = new MenuItem(menuNew, SWT.NONE);
		menuItemParaMap.setImage(ResourceManager.getPluginImage("StudioPlugin", StudioConst.ICON_NODE_PARAMETER_MAP));
		menuItemParaMap.addSelectionListener(new BtnNewMenuItemSelectionAdapter());
		menuItemParaMap.setText("参数映射");

		menuItemInsert = new MenuItem(menuNew, SWT.NONE);
		menuItemInsert.setImage(ResourceManager.getPluginImage("StudioPlugin", StudioConst.ICON_NODE_INSERT));
		menuItemInsert.addSelectionListener(new BtnNewMenuItemSelectionAdapter());
		menuItemInsert.setText("插入");

		menuItemDelete = new MenuItem(menuNew, SWT.NONE);
		menuItemDelete.setImage(ResourceManager.getPluginImage("StudioPlugin", StudioConst.ICON_NODE_DELETE));
		menuItemDelete.addSelectionListener(new BtnNewMenuItemSelectionAdapter());
		menuItemDelete.setText("删除");

		menuItemUpdate = new MenuItem(menuNew, SWT.NONE);
		menuItemUpdate.setImage(ResourceManager.getPluginImage("StudioPlugin", StudioConst.ICON_NODE_UPDATE));
		menuItemUpdate.addSelectionListener(new BtnNewMenuItemSelectionAdapter());
		menuItemUpdate.setText("更新");

		menuItemSelect = new MenuItem(menuNew, SWT.NONE);
		menuItemSelect.setImage(ResourceManager.getPluginImage("StudioPlugin", StudioConst.ICON_NODE_SELECT));
		menuItemSelect.addSelectionListener(new BtnNewMenuItemSelectionAdapter());
		menuItemSelect.setText("查询");

		mapperTableViewer = new TableViewer(parent, SWT.BORDER | SWT.FULL_SELECTION);
		mapperTable = mapperTableViewer.getTable();
		mapperTable.addMouseListener(new MapperTableMouseAdapter());
		mapperTable.setSelection(0);
		mapperTable.addSelectionListener(new MapperTableSelection());
		mapperTable.setLinesVisible(true);
		mapperTable.setHeaderVisible(true);
		FormData fd_mapperTable = new FormData();
		fd_mapperTable.left = new FormAttachment(0, 10);
		fd_mapperTable.right = new FormAttachment(0, 308);
		fd_mapperTable.top = new FormAttachment(0, 10);
		fd_mapperTable.bottom = new FormAttachment(100, -40);
		mapperTable.setLayoutData(fd_mapperTable);
		formToolkit.paintBordersFor(mapperTable);

		TableViewerColumn tableViewerColumn = new TableViewerColumn(mapperTableViewer, SWT.NONE);
		TableColumn colObjectName = tableViewerColumn.getColumn();
		colObjectName.addSelectionListener(new ObjectNamSelectionAdapter());
		colObjectName.setWidth(159);
		colObjectName.setText("对象名称");
				
		TableViewerColumn tableViewerColumn_2 = new TableViewerColumn(mapperTableViewer, SWT.NONE);
		TableColumn tableColumn_2 = tableViewerColumn_2.getColumn();
		tableColumn_2.addSelectionListener(new ObjectTypeSelectionAdapter());
		tableColumn_2.setWidth(38);
		tableColumn_2.setText("类型");
		
		TableViewerColumn tableViewerColumn_1 = new TableViewerColumn(mapperTableViewer, SWT.NONE);
		TableColumn tableColumn_1 = tableViewerColumn_1.getColumn();
		tableColumn_1.setWidth(500);
		tableColumn_1.setText("描述");
		
		Composite composite_split = new Composite(parent, SWT.NONE);
		composite_split.setLayout(new FormLayout());
		FormData fd_composite_split = new FormData();
		fd_composite_split.left = new FormAttachment(mapperTable, 2, SWT.RIGHT);
		fd_composite_split.right = new FormAttachment(mapperTable, 20, SWT.RIGHT);
		fd_composite_split.top = new FormAttachment(mapperTable, 0, SWT.TOP);
		fd_composite_split.bottom = new FormAttachment(mapperTable, 0, SWT.BOTTOM);
		composite_split.setLayoutData(fd_composite_split);

		Button butUp = new Button(composite_split, SWT.NONE);
		FormData fd_butUp = new FormData();
		fd_butUp.top = new FormAttachment(40, 0);
		butUp.setLayoutData(fd_butUp);
		butUp.addMouseListener(new MapperTableUpMouseAdapter());
		formToolkit.adapt(butUp, true, true);
		butUp.setText("↑");

		Button butDown = new Button(composite_split, SWT.NONE);
		butDown.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
			}
		});
		FormData fd_butDown = new FormData();
		fd_butDown.top = new FormAttachment(butUp, 5, SWT.BOTTOM);
		butDown.setLayoutData(fd_butDown);
		butDown.addMouseListener(new MapperTableDownMouseAdapter());
		formToolkit.adapt(butDown, true, true);
		butDown.setText("↓");

		composite = new Composite(parent, SWT.BORDER);
		sl_composite = new StackLayout();
		composite.setLayout(sl_composite);
		FormData fd_composite = new FormData();
		fd_composite.left = new FormAttachment(mapperTable, 22);
		fd_composite.right = new FormAttachment(100, -8);
		fd_composite.top = new FormAttachment(mapperTable, 0, SWT.TOP);
		fd_composite.bottom = new FormAttachment(100, -40);
		composite.setLayoutData(fd_composite);
		formToolkit.paintBordersFor(composite);

		composite_config = new Composite(composite, SWT.NONE);
		formToolkit.paintBordersFor(composite_config);
		composite_config.setLayout(new FormLayout());

		Label label = new Label(composite_config, SWT.NONE);
		FormData fd_label = new FormData();
		fd_label.top = new FormAttachment(0, 10);
		fd_label.left = new FormAttachment(0, 10);
		label.setLayoutData(fd_label);
		label.setText("映射文件类型：");

		Label lblNamespace = new Label(composite_config, SWT.NONE);
		FormData fd_lblNamespace = new FormData();
		fd_lblNamespace.right = new FormAttachment(0, 71);
		fd_lblNamespace.top = new FormAttachment(0, 84);
		fd_lblNamespace.left = new FormAttachment(0, 10);
		lblNamespace.setLayoutData(fd_lblNamespace);
		lblNamespace.setText("命名空间：");

		Label lblPackage = new Label(composite_config, SWT.NONE);
		FormData fd_lblPackage = new FormData();
		fd_lblPackage.top = new FormAttachment(0, 121);
		fd_lblPackage.left = new FormAttachment(0, 10);
		lblPackage.setLayoutData(fd_lblPackage);
		lblPackage.setText("映射类名：");

		lblTable = new Label(composite_config, SWT.NONE);
		FormData fd_lblTable = new FormData();
		fd_lblTable.top = new FormAttachment(0, 46);
		fd_lblTable.left = new FormAttachment(0, 230);
		lblTable.setLayoutData(fd_lblTable);
		lblTable.setText("表名称：");

		Label lblDao = new Label(composite_config, SWT.NONE);
		FormData fd_lblDao = new FormData();
		fd_lblDao.right = new FormAttachment(0, 71);
		fd_lblDao.top = new FormAttachment(0, 156);
		fd_lblDao.left = new FormAttachment(0, 10);
		lblDao.setLayoutData(fd_lblDao);
		lblDao.setText("Bean类名：");

		Label lblBean = new Label(composite_config, SWT.NONE);
		FormData fd_lblBean = new FormData();
		fd_lblBean.right = new FormAttachment(0, 71);
		fd_lblBean.top = new FormAttachment(0, 189);
		fd_lblBean.left = new FormAttachment(0, 10);
		lblBean.setLayoutData(fd_lblBean);
		lblBean.setText("DAO类名：");

		radMapperTable = new Button(composite_config, SWT.RADIO);
		FormData fd_radMapperTable = new FormData();
		fd_radMapperTable.top = new FormAttachment(0, 10);
		fd_radMapperTable.left = new FormAttachment(0, 100);
		radMapperTable.setLayoutData(fd_radMapperTable);
		radMapperTable.setEnabled(false);
		radMapperTable.setSelection(true);
		radMapperTable.setText("数据库表");

		radMapperQuery = new Button(composite_config, SWT.RADIO);
		FormData fd_radMapperQuery = new FormData();
		fd_radMapperQuery.top = new FormAttachment(0, 10);
		fd_radMapperQuery.left = new FormAttachment(0, 172);
		radMapperQuery.setLayoutData(fd_radMapperQuery);
		radMapperQuery.setEnabled(false);
		radMapperQuery.setText("自定义查询");
		
		textDataSource = new Text(composite_config, SWT.BORDER);
		textDataSource.setEnabled(false);
		textDataSource.setEditable(false);
		FormData fd_textName = new FormData();
		fd_textName.top = new FormAttachment(0, 44);
		fd_textName.left = new FormAttachment(0, 100);
		fd_textName.right = new FormAttachment(0, 220);
		textDataSource.setLayoutData(fd_textName);
		
		textTableName = new Text(composite_config, SWT.BORDER);
		textTableName.setEnabled(false);
		textTableName.setEditable(false);
		textTableName.addModifyListener(new ConfigModifyListener());
		FormData fd_textTableName = new FormData();
		fd_textTableName.right = new FormAttachment(100, -40);
		fd_textTableName.top = new FormAttachment(0, 44);
		fd_textTableName.left = new FormAttachment(0, 278);
		textTableName.setLayoutData(fd_textTableName);
		formToolkit.adapt(textTableName, true, true);

		textNamespace = new Text(composite_config, SWT.BORDER);
		textNamespace.addModifyListener(new ConfigModifyListener());
		FormData fd_textNamespace = new FormData();
		fd_textNamespace.right = new FormAttachment(100, -40);
		fd_textNamespace.top = new FormAttachment(0, 81);
		fd_textNamespace.left = new FormAttachment(0, 100);
		textNamespace.setLayoutData(fd_textNamespace);
		formToolkit.adapt(textNamespace, true, true);

		textInterMapperClass = new Text(composite_config, SWT.BORDER);
		textInterMapperClass.addModifyListener(new ConfigModifyListener());
		FormData fd_textInterMapperClass = new FormData();
		fd_textInterMapperClass.right = new FormAttachment(100, -40);
		fd_textInterMapperClass.top = new FormAttachment(0, 118);
		fd_textInterMapperClass.left = new FormAttachment(0, 100);
		textInterMapperClass.setLayoutData(fd_textInterMapperClass);
		formToolkit.adapt(textInterMapperClass, true, true);

		textDaoClass = new Text(composite_config, SWT.BORDER);
		textDaoClass.addModifyListener(new ConfigModifyListener());
		FormData fd_textDaoClass = new FormData();
		fd_textDaoClass.right = new FormAttachment(100, -40);
		fd_textDaoClass.top = new FormAttachment(0, 186);
		fd_textDaoClass.left = new FormAttachment(0, 100);
		textDaoClass.setLayoutData(fd_textDaoClass);
		formToolkit.adapt(textDaoClass, true, true);

		textBeanClass = new Text(composite_config, SWT.BORDER);
		textBeanClass.addModifyListener(new ConfigModifyListener());
		FormData fd_textBeanClass = new FormData();
		fd_textBeanClass.right = new FormAttachment(100, -40);
		fd_textBeanClass.top = new FormAttachment(0, 153);
		fd_textBeanClass.left = new FormAttachment(0, 100);
		textBeanClass.setLayoutData(fd_textBeanClass);
		formToolkit.adapt(textBeanClass, true, true);

		Button butOpenInterMapper = new Button(composite_config, SWT.NONE);
		butOpenInterMapper.addMouseListener(new BtnOpenInterMapperClassMouseAdapter());
		butOpenInterMapper.setImage(ResourceManager.getPluginImage("StudioPlugin", StudioConst.ICON_OPEN_FILE));
		FormData fd_butOpenInterMapper = new FormData();
		fd_butOpenInterMapper.left = new FormAttachment(textInterMapperClass, 2);
		fd_butOpenInterMapper.top = new FormAttachment(textInterMapperClass, -2, SWT.TOP);
		butOpenInterMapper.setLayoutData(fd_butOpenInterMapper);
		formToolkit.adapt(butOpenInterMapper, true, true);

		Button btnOpenBean = new Button(composite_config, SWT.NONE);
		btnOpenBean.addMouseListener(new BtnOpenBeanClassMouseAdapter());
		btnOpenBean.setImage(ResourceManager.getPluginImage("StudioPlugin", StudioConst.ICON_OPEN_FILE));
		FormData fd_btnOpenBean = new FormData();
		fd_btnOpenBean.top = new FormAttachment(textBeanClass, -2, SWT.TOP);
		fd_btnOpenBean.left = new FormAttachment(textBeanClass, 2, SWT.RIGHT);
		btnOpenBean.setLayoutData(fd_btnOpenBean);
		formToolkit.adapt(btnOpenBean, true, true);

		Button btnOpenDao = new Button(composite_config, SWT.NONE);
		btnOpenDao.addMouseListener(new BtnOpenDaoClassMouseAdapter());
		btnOpenDao.setImage(ResourceManager.getPluginImage("StudioPlugin", StudioConst.ICON_OPEN_FILE));
		FormData fd_btnOpenDao = new FormData();
		fd_btnOpenDao.left = new FormAttachment(textDaoClass, 2);
		fd_btnOpenDao.top = new FormAttachment(textDaoClass, -2, SWT.TOP);
		btnOpenDao.setLayoutData(fd_btnOpenDao);
		formToolkit.adapt(btnOpenDao, true, true);
		
		label_5 = new Label(composite_config, SWT.NONE);
		FormData fd_label_5 = new FormData();
		fd_label_5.top = new FormAttachment(0, 46);
		fd_label_5.left = new FormAttachment(0, 10);
		label_5.setLayoutData(fd_label_5);
		label_5.setText("数据源：");

		composite_resultmap = new Composite(composite, SWT.NONE);
		formToolkit.paintBordersFor(composite_resultmap);
		composite_resultmap.setLayout(new FormLayout());

		resultMapTableViewer = new TableViewer(composite_resultmap, SWT.BORDER | SWT.FULL_SELECTION);
		resultMapTable = resultMapTableViewer.getTable();
		resultMapTable.addMouseListener(new ResultMapMouseAdapter());
		FormData fd_resultMapTable = new FormData();
		fd_resultMapTable.bottom = new FormAttachment(100, -40);
		fd_resultMapTable.right = new FormAttachment(100, -25);
		fd_resultMapTable.top = new FormAttachment(0, 37);
		fd_resultMapTable.left = new FormAttachment(0);
		resultMapTable.setLayoutData(fd_resultMapTable);
		resultMapTable.setLinesVisible(true);
		resultMapTable.setHeaderVisible(true);
		formToolkit.paintBordersFor(resultMapTable);

		TableViewerColumn tableViewerColumn_3 = new TableViewerColumn(resultMapTableViewer, SWT.NONE);
		TableColumn colResultMapFieldName = tableViewerColumn_3.getColumn();
		colResultMapFieldName.addSelectionListener(new ResultMapFieldNameSelectionAdapter());
		colResultMapFieldName.setWidth(200);
		colResultMapFieldName.setText("字段名称");

		TableViewerColumn tableViewerColumn_4 = new TableViewerColumn(resultMapTableViewer, SWT.NONE);
		TableColumn tableColumn_3 = tableViewerColumn_4.getColumn();
		tableColumn_3.setWidth(200);
		tableColumn_3.setText("映射名称");

		TableViewerColumn tableViewerColumn_5 = new TableViewerColumn(resultMapTableViewer, SWT.NONE);
		TableColumn tableColumn_4 = tableViewerColumn_5.getColumn();
		tableColumn_4.setWidth(100);
		tableColumn_4.setText("数据类型");

		Label label_1 = new Label(composite_resultmap, SWT.NONE);
		FormData fd_label_1 = new FormData();
		fd_label_1.top = new FormAttachment(0, 10);
		fd_label_1.left = new FormAttachment(0, 10);
		label_1.setLayoutData(fd_label_1);
		label_1.setText("名称：");

		textResultMapName = new Text(composite_resultmap, SWT.BORDER);
		textResultMapName.addModifyListener(new ModifyListener() {
			public void modifyText(ModifyEvent e) {
				setDirty(true);
				firePropertyChange(PROP_DIRTY);
			}
		});
		textResultMapName.setBackground(SWTResourceManager.getColor(204, 204, 255));
		FormData fd_textResultMapName = new FormData();
		fd_textResultMapName.left = new FormAttachment(label_1);
		fd_textResultMapName.right = new FormAttachment(35, 0);
		fd_textResultMapName.top = new FormAttachment(0, 8);
		fd_textResultMapName.bottom = new FormAttachment(0, 30);
		textResultMapName.setLayoutData(fd_textResultMapName);
		formToolkit.adapt(textResultMapName, true, true);

		Label label_2 = new Label(composite_resultmap, SWT.NONE);
		FormData fd_label_2 = new FormData();
		fd_label_2.left = new FormAttachment(textResultMapName, 5, SWT.RIGHT);
		fd_label_2.top = new FormAttachment(0, 10);
		label_2.setLayoutData(fd_label_2);
		label_2.setText("映射类型：");

		textResultMapType = new Text(composite_resultmap, SWT.BORDER);
		textResultMapType.addModifyListener(new ModifyListener() {
			public void modifyText(ModifyEvent e) {
				setDirty(true);
				firePropertyChange(PROP_DIRTY);
			}
		});
		textResultMapType.setBackground(SWTResourceManager.getColor(204, 204, 255));
		FormData fd_textResultMapType = new FormData();
		fd_textResultMapType.left = new FormAttachment(label_2);
		fd_textResultMapType.top = new FormAttachment(0, 8);
		fd_textResultMapType.bottom = new FormAttachment(0, 30);
		fd_textResultMapType.right = new FormAttachment(100, -25);
		textResultMapType.setLayoutData(fd_textResultMapType);
		formToolkit.adapt(textResultMapType, true, true);

		Button btnResultMapType = new Button(composite_resultmap, SWT.NONE);
		btnResultMapType.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseUp(MouseEvent e) {
				String classname = StringUtils.trim(textResultMapType.getText());
				if (StringUtils.isBlank(classname)) {
					return;
				}
				openIntfProjectFileByClassName(classname);
			}
		});
		btnResultMapType.setImage(ResourceManager.getPluginImage("StudioPlugin", StudioConst.ICON_OPEN_FILE));
		FormData fd_btnResultMapType = new FormData();
		fd_btnResultMapType.top = new FormAttachment(0, 8);
		fd_btnResultMapType.bottom = new FormAttachment(0, 30);
		fd_btnResultMapType.left = new FormAttachment(100, -25);
		fd_btnResultMapType.right = new FormAttachment(100, -3);
		btnResultMapType.setLayoutData(fd_btnResultMapType);
		formToolkit.adapt(btnResultMapType, true, true);

		Button butResultMapUp = new Button(composite_resultmap, SWT.NONE);
		butResultMapUp.addMouseListener(new ResultMapUpMouseAdapter());
		FormData fd_butResultMapUp = new FormData();
		fd_butResultMapUp.left = new FormAttachment(resultMapTable, 3);
		fd_butResultMapUp.top = new FormAttachment(40);
		butResultMapUp.setLayoutData(fd_butResultMapUp);
		butResultMapUp.setText("↑");
		formToolkit.adapt(butResultMapUp, true, true);

		Button butResultMapDown = new Button(composite_resultmap, SWT.NONE);
		butResultMapDown.addMouseListener(new ResultMapDownMouseAdapter());
		FormData fd_butResultMapDown = new FormData();
		fd_butResultMapDown.left = new FormAttachment(resultMapTable, 3);
		fd_butResultMapDown.top = new FormAttachment(butResultMapUp, 5);
		butResultMapDown.setLayoutData(fd_butResultMapDown);
		butResultMapDown.setText("↓");
		formToolkit.adapt(butResultMapDown, true, true);

		Button butResultMapNew = new Button(composite_resultmap, SWT.NONE);
		butResultMapNew.addMouseListener(new BtnResultMapNewMouseAdapter());
		FormData fd_butResultMapNew = new FormData();
		fd_butResultMapNew.left = new FormAttachment(0, 10);
		fd_butResultMapNew.right = new FormAttachment(0, 80);
		fd_butResultMapNew.top = new FormAttachment(100, -35);
		fd_butResultMapNew.bottom = new FormAttachment(100, -7);
		butResultMapNew.setLayoutData(fd_butResultMapNew);
		butResultMapNew.setText("新增");
		formToolkit.adapt(butResultMapNew, true, true);

		Button butResultMapModify = new Button(composite_resultmap, SWT.NONE);
		butResultMapModify.addMouseListener(new BtnResultMapModifyMouseAdapter());
		FormData fd_butResultMapModify = new FormData();
		fd_butResultMapModify.top = new FormAttachment(butResultMapNew, 0, SWT.TOP);
		fd_butResultMapModify.bottom = new FormAttachment(butResultMapNew, 0, SWT.BOTTOM);
		fd_butResultMapModify.left = new FormAttachment(0, 110);
		fd_butResultMapModify.right = new FormAttachment(0, 180);
		butResultMapModify.setLayoutData(fd_butResultMapModify);
		butResultMapModify.setText("修改");
		formToolkit.adapt(butResultMapModify, true, true);

		Button butResultMapDelete = new Button(composite_resultmap, SWT.NONE);
		butResultMapDelete.addMouseListener(new BtnResultMapDeleteMouseAdapter());
		FormData fd_butResultMapDelete = new FormData();
		fd_butResultMapDelete.bottom = new FormAttachment(butResultMapNew, 0, SWT.BOTTOM);
		fd_butResultMapDelete.top = new FormAttachment(butResultMapNew, 0, SWT.TOP);
		fd_butResultMapDelete.left = new FormAttachment(0, 210);
		fd_butResultMapDelete.right = new FormAttachment(0, 280);
		butResultMapDelete.setLayoutData(fd_butResultMapDelete);
		butResultMapDelete.setText("删除");
		formToolkit.adapt(butResultMapDelete, true, true);
		
		butBaseResultMapRefresh = new Button(composite_resultmap, SWT.NONE);
		butBaseResultMapRefresh.addMouseListener(new BtnBaseResultMapRefreshMouseAdapter());
		FormData fd_butBaseResultMapRefresh = new FormData();
		fd_butBaseResultMapRefresh.left = new FormAttachment(0, 310);
		fd_butBaseResultMapRefresh.right = new FormAttachment(0, 380);
		fd_butBaseResultMapRefresh.top = new FormAttachment(butResultMapNew, 0, SWT.TOP);
		fd_butBaseResultMapRefresh.bottom = new FormAttachment(butResultMapNew, 0, SWT.BOTTOM);
		butBaseResultMapRefresh.setLayoutData(fd_butBaseResultMapRefresh);
		butBaseResultMapRefresh.setText("刷新");
		formToolkit.adapt(butBaseResultMapRefresh, true, true);

		composite_parametermap = new Composite(composite, SWT.NONE);
		formToolkit.paintBordersFor(composite_parametermap);
		composite_parametermap.setLayout(new FormLayout());

		parameterMapTableViewer = new TableViewer(composite_parametermap, SWT.BORDER | SWT.FULL_SELECTION);
		parameterMapTable = parameterMapTableViewer.getTable();
		parameterMapTable.addMouseListener(new ParameterMapMouseAdapter());
		FormData fd_parameterMapTable = new FormData();
		fd_parameterMapTable.bottom = new FormAttachment(100, -40);
		fd_parameterMapTable.right = new FormAttachment(100, -25);
		fd_parameterMapTable.top = new FormAttachment(0, 37);
		fd_parameterMapTable.left = new FormAttachment(0);
		parameterMapTable.setLayoutData(fd_parameterMapTable);
		parameterMapTable.setHeaderVisible(true);
		parameterMapTable.setLinesVisible(true);
		formToolkit.paintBordersFor(parameterMapTable);

		TableViewerColumn tableViewerColumn_6 = new TableViewerColumn(parameterMapTableViewer, SWT.NONE);
		TableColumn colPermeterMapMode = tableViewerColumn_6.getColumn();
		colPermeterMapMode.addSelectionListener(new PermeterMapModeSelectionAdapter());
		colPermeterMapMode.setWidth(100);
		colPermeterMapMode.setText("参数类型");

		TableViewerColumn tableViewerColumn_7 = new TableViewerColumn(parameterMapTableViewer, SWT.NONE);
		TableColumn colPermeterMapProperty = tableViewerColumn_7.getColumn();
		colPermeterMapProperty.addSelectionListener(new PermeterMapPropertySelectionAdapter());
		colPermeterMapProperty.setWidth(150);
		colPermeterMapProperty.setText("参数名称");

		TableViewerColumn tableViewerColumn_8 = new TableViewerColumn(parameterMapTableViewer, SWT.NONE);
		TableColumn tableColumn_7 = tableViewerColumn_8.getColumn();
		tableColumn_7.setWidth(150);
		tableColumn_7.setText("数据库类型");

		TableViewerColumn tableViewerColumn_9 = new TableViewerColumn(parameterMapTableViewer, SWT.NONE);
		TableColumn tableColumn_8 = tableViewerColumn_9.getColumn();
		tableColumn_8.setWidth(150);
		tableColumn_8.setText("映射类型");

		Label label_3 = new Label(composite_parametermap, SWT.NONE);
		FormData fd_label_3 = new FormData();
		fd_label_3.top = new FormAttachment(0, 10);
		fd_label_3.left = new FormAttachment(0, 10);
		label_3.setLayoutData(fd_label_3);
		label_3.setText("名称：");

		textParaMapName = new Text(composite_parametermap, SWT.BORDER);
		FormData fd_textParaMapName = new FormData();
		fd_textParaMapName.left = new FormAttachment(label_3);
		fd_textParaMapName.right = new FormAttachment(35, 0);
		fd_textParaMapName.top = new FormAttachment(0, 8);
		fd_textParaMapName.bottom = new FormAttachment(0, 30);
		textParaMapName.setLayoutData(fd_textParaMapName);
		formToolkit.adapt(textParaMapName, true, true);

		Label label_4 = new Label(composite_parametermap, SWT.NONE);
		FormData fd_label_4 = new FormData();
		fd_label_4.left = new FormAttachment(textParaMapName, 5, SWT.RIGHT);
		fd_label_4.top = new FormAttachment(0, 10);
		label_4.setLayoutData(fd_label_4);
		label_4.setText("映射类型：");

		textParaMapType = new Text(composite_parametermap, SWT.BORDER);
		FormData fd_textParaMapType = new FormData();
		fd_textParaMapType.left = new FormAttachment(label_4);
		fd_textParaMapType.top = new FormAttachment(0, 8);
		fd_textParaMapType.bottom = new FormAttachment(0, 30);
		fd_textParaMapType.right = new FormAttachment(100, -25);
		textParaMapType.setLayoutData(fd_textParaMapType);
		formToolkit.adapt(textParaMapType, true, true);

		Button butParaMapType = new Button(composite_parametermap, SWT.NONE);
		butParaMapType.setImage(ResourceManager.getPluginImage("StudioPlugin", StudioConst.ICON_OPEN_FILE));
		FormData fd_butParaMapType = new FormData();
		fd_butParaMapType.top = new FormAttachment(0, 8);
		fd_butParaMapType.bottom = new FormAttachment(0, 30);
		fd_butParaMapType.left = new FormAttachment(100, -25);
		fd_butParaMapType.right = new FormAttachment(100, -3);
		butParaMapType.setLayoutData(fd_butParaMapType);
		formToolkit.adapt(butParaMapType, true, true);

		Button butParameterMapUp = new Button(composite_parametermap, SWT.NONE);
		butParameterMapUp.addMouseListener(new ParameterMapUpMouseAdapter());
		FormData fd_butParameterMapUp = new FormData();
		fd_butParameterMapUp.left = new FormAttachment(parameterMapTable, 3);
		fd_butParameterMapUp.top = new FormAttachment(40);
		butParameterMapUp.setLayoutData(fd_butParameterMapUp);
		formToolkit.adapt(butParameterMapUp, true, true);
		butParameterMapUp.setText("↑");

		Button butParameterMapDown = new Button(composite_parametermap, SWT.NONE);
		butParameterMapDown.addMouseListener(new ParameterMapDownMouseAdapter());
		FormData fd_butParameterMapDown = new FormData();
		fd_butParameterMapDown.left = new FormAttachment(parameterMapTable, 3);
		fd_butParameterMapDown.top = new FormAttachment(butParameterMapUp, 5);
		butParameterMapDown.setLayoutData(fd_butParameterMapDown);
		formToolkit.adapt(butParameterMapDown, true, true);
		butParameterMapDown.setText("↓");

		Button butParaNew = new Button(composite_parametermap, SWT.NONE);
		butParaNew.addMouseListener(new BtnParameterMapNewMouseAdapter());
		FormData fd_butParaNew = new FormData();
		fd_butParaNew.left = new FormAttachment(0, 10);
		fd_butParaNew.right = new FormAttachment(0, 80);
		fd_butParaNew.top = new FormAttachment(100, -35);
		fd_butParaNew.bottom = new FormAttachment(100, -7);
		butParaNew.setLayoutData(fd_butParaNew);
		formToolkit.adapt(butParaNew, true, true);
		butParaNew.setText("新增");

		Button butParaDelete = new Button(composite_parametermap, SWT.NONE);
		butParaDelete.addMouseListener(new BtnParameterMapDeleteMouseAdapter());
		FormData fd_butParaDelete = new FormData();
		fd_butParaDelete.bottom = new FormAttachment(butParaNew, 0, SWT.BOTTOM);
		fd_butParaDelete.top = new FormAttachment(butParaNew, 0, SWT.TOP);
		fd_butParaDelete.left = new FormAttachment(0, 100);
		fd_butParaDelete.right = new FormAttachment(0, 170);
		butParaDelete.setLayoutData(fd_butParaDelete);
		formToolkit.adapt(butParaDelete, true, true);
		butParaDelete.setText("删除");

		composite_sqlnode = new Composite(composite, SWT.NONE);
		formToolkit.paintBordersFor(composite_sqlnode);
		composite_sqlnode.setLayout(new FormLayout());

		Composite composite_1 = new Composite(composite_sqlnode, SWT.NONE);
		GridLayout gl_composite_1 = new GridLayout(8, false);
		gl_composite_1.horizontalSpacing = 2;
		gl_composite_1.verticalSpacing = 2;
		composite_1.setLayout(gl_composite_1);
		FormData fd_composite_1 = new FormData();
		fd_composite_1.left = new FormAttachment(0, 5);
		fd_composite_1.right = new FormAttachment(100, -5);
		fd_composite_1.top = new FormAttachment(0, 6);
		fd_composite_1.bottom = new FormAttachment(0, 150);
		composite_1.setLayoutData(fd_composite_1);
		formToolkit.paintBordersFor(composite_1);

		lblId = new Label(composite_1, SWT.NONE);
		lblId.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false, false, 1, 1));
		lblId.setToolTipText("id：命名空间中唯一的标识符");
		lblId.setText("id");

		tsqlId = new Text(composite_1, SWT.BORDER);
		tsqlId.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
		tsqlId.setToolTipText("");
		formToolkit.adapt(tsqlId, true, true);

		lblResultmap = new Label(composite_1, SWT.NONE);
		GridData gd_lblResultmap = new GridData(SWT.LEFT, SWT.CENTER, false, false, 1, 1);
		gd_lblResultmap.widthHint = 50;
		lblResultmap.setLayoutData(gd_lblResultmap);
		lblResultmap.setToolTipText("resultMap：查询结果映射，命名引用外部的resultMap\r\n（resultType、resultMap不能同时使用）。");
		lblResultmap.setText("resultMap");

		tsqlResultMap = new Text(composite_1, SWT.BORDER);
		tsqlResultMap.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
		formToolkit.adapt(tsqlResultMap, true, true);

		lblParametertype = new Label(composite_1, SWT.NONE);
		GridData gd_lblParametertype = new GridData(SWT.LEFT, SWT.CENTER, false, false, 1, 1);
		gd_lblParametertype.widthHint = 50;
		lblParametertype.setLayoutData(gd_lblParametertype);
		lblParametertype.setToolTipText("parameterType：传入参数类名或别名");
		lblParametertype.setText("parameterType");

		tsqlParameterType = new Text(composite_1, SWT.BORDER);
		GridData gd_tsqlParameterType = new GridData(SWT.FILL, SWT.CENTER, true, false, 3, 1);
		gd_tsqlParameterType.widthHint = 123;
		tsqlParameterType.setLayoutData(gd_tsqlParameterType);
		formToolkit.adapt(tsqlParameterType, true, true);

		lblDatabaseid = new Label(composite_1, SWT.NONE);
		lblDatabaseid.setToolTipText("databaseId");
		GridData gd_lblDatabaseid = new GridData(SWT.RIGHT, SWT.CENTER, false, true, 1, 1);
		gd_lblDatabaseid.widthHint = 50;
		lblDatabaseid.setLayoutData(gd_lblDatabaseid);
		lblDatabaseid.setText("databaseId");

		tsqlDatabaseId = new Text(composite_1, SWT.BORDER);
		tsqlDatabaseId.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
		formToolkit.adapt(tsqlDatabaseId, true, true);

		lblResulttype = new Label(composite_1, SWT.NONE);
		lblResulttype.setToolTipText("resultType：返回的结果映射的类名或别名\r\n（resultType、resultMap不能同时使用）");
		GridData gd_lblResulttype = new GridData(SWT.RIGHT, SWT.CENTER, false, false, 1, 1);
		gd_lblResulttype.widthHint = 50;
		lblResulttype.setLayoutData(gd_lblResulttype);
		lblResulttype.setText("resultType");

		tsqlResultType = new Text(composite_1, SWT.BORDER);
		tsqlResultType.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
		formToolkit.adapt(tsqlResultType, true, true);

		lblParametermap = new Label(composite_1, SWT.NONE);
		lblParametermap.setForeground(SWTResourceManager.getColor(SWT.COLOR_LIST_SELECTION));
		lblParametermap.setFont(SWTResourceManager.getFont("微软雅黑", 9, SWT.ITALIC));
		lblParametermap.setToolTipText("parameterMap：引用外部parameterMap的（废弃的方法）\r\n使用内联参数映射和parameterType属性。");
		GridData gd_lblParametermap = new GridData(SWT.RIGHT, SWT.CENTER, false, false, 1, 1);
		gd_lblParametermap.widthHint = 50;
		lblParametermap.setLayoutData(gd_lblParametermap);
		lblParametermap.setText("parameterMap");

		tsqlParameterMap = new Text(composite_1, SWT.BORDER);
		tsqlParameterMap.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
		formToolkit.adapt(tsqlParameterMap, true, true);

		lblUsegeneratedkeys = new Label(composite_1, SWT.NONE);
		lblUsegeneratedkeys
				.setToolTipText("useGeneratedKeys：这会告诉MyBatis使用JDBC的getGeneratedKeys方法来取出由数据内部生成的主键。\r\n（比如：像MySQL和SQL Server这样的数据库管理系统的自动递增字段）\r\n（仅对insert有用）默认值：false。");
		GridData gd_lblUsegeneratedkeys = new GridData(SWT.RIGHT, SWT.CENTER, false, false, 1, 1);
		gd_lblUsegeneratedkeys.widthHint = 50;
		lblUsegeneratedkeys.setLayoutData(gd_lblUsegeneratedkeys);
		lblUsegeneratedkeys.setText("useGeneratedKeys");

		tsqlUseGeneratedKeys = new Combo(composite_1, SWT.NONE);
		tsqlUseGeneratedKeys.setItems(new String[] { "true", "false" });
		tsqlUseGeneratedKeys.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
		formToolkit.adapt(tsqlUseGeneratedKeys);
		formToolkit.paintBordersFor(tsqlUseGeneratedKeys);

		lblUsecache = new Label(composite_1, SWT.NONE);
		lblUsecache.setToolTipText("useCache：本条语句的结果被缓存。默认值：true。");
		GridData gd_lblUsecache = new GridData(SWT.RIGHT, SWT.CENTER, false, false, 1, 1);
		gd_lblUsecache.widthHint = 50;
		lblUsecache.setLayoutData(gd_lblUsecache);
		lblUsecache.setText("useCache");

		tsqlUseCache = new Combo(composite_1, SWT.NONE);
		tsqlUseCache.setItems(new String[] { "true", "false" });
		tsqlUseCache.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
		formToolkit.adapt(tsqlUseCache);
		formToolkit.paintBordersFor(tsqlUseCache);

		lblFlushcache = new Label(composite_1, SWT.NONE);
		lblFlushcache.setToolTipText("flushCache：设置为true，无论语句什么时候被调用，都会导致缓存被清空。\r\n默认值：false。");
		GridData gd_lblFlushcache = new GridData(SWT.RIGHT, SWT.CENTER, false, false, 1, 1);
		gd_lblFlushcache.widthHint = 50;
		lblFlushcache.setLayoutData(gd_lblFlushcache);
		lblFlushcache.setText("flushCache");

		tsqlFlushCache = new Combo(composite_1, SWT.NONE);
		tsqlFlushCache.setItems(new String[] { "true", "false" });
		tsqlFlushCache.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
		formToolkit.adapt(tsqlFlushCache);
		formToolkit.paintBordersFor(tsqlFlushCache);

		lblResultsettype = new Label(composite_1, SWT.NONE);
		lblResultsettype.setToolTipText("FORWARD_ONLY|SCROLL_SENSITIVE|SCROLL_INSENSITIVE中的一种。\r\n默认是不设置（驱动自行处理）。");
		GridData gd_lblResultsettype = new GridData(SWT.RIGHT, SWT.CENTER, false, false, 1, 1);
		gd_lblResultsettype.widthHint = 50;
		lblResultsettype.setLayoutData(gd_lblResultsettype);
		lblResultsettype.setText("resultSetType");

		tsqlResultSetType = new Combo(composite_1, SWT.NONE);
		tsqlResultSetType.setItems(new String[] { "FORWARD_ONLY", "SCROLL_SENSITIVE", "SCROLL_INSENSITIVE" });
		tsqlResultSetType.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
		formToolkit.adapt(tsqlResultSetType);
		formToolkit.paintBordersFor(tsqlResultSetType);

		lblLang = new Label(composite_1, SWT.NONE);
		lblLang.setToolTipText("lang");
		lblLang.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false, false, 1, 1));
		lblLang.setText("lang");

		tsqlLang = new Text(composite_1, SWT.BORDER);
		tsqlLang.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
		formToolkit.adapt(tsqlLang, true, true);

		lblStatementtype = new Label(composite_1, SWT.NONE);
		lblStatementtype
				.setToolTipText("statementType：STATEMENT,PREPARED或CALLABLE的一种。\r\n这会让MyBatis使用选择使用Statement，PreparedStatement或CallableStatement。\r\n默认值：PREPARED。");
		GridData gd_lblStatementtype = new GridData(SWT.RIGHT, SWT.CENTER, false, false, 1, 1);
		gd_lblStatementtype.widthHint = 50;
		lblStatementtype.setLayoutData(gd_lblStatementtype);
		lblStatementtype.setText("statementType");

		tsqlStatementType = new Combo(composite_1, SWT.NONE);
		tsqlStatementType.setItems(new String[] { "PREPARED", "STATEMENT", "CALLABLE" });
		tsqlStatementType.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
		formToolkit.adapt(tsqlStatementType);
		formToolkit.paintBordersFor(tsqlStatementType);
		lblResultsets = new Label(composite_1, SWT.NONE);
		lblResultsets.setToolTipText("resultSets");
		GridData gd_lblResultsets = new GridData(SWT.RIGHT, SWT.CENTER, false, false, 1, 1);
		gd_lblResultsets.widthHint = 50;
		lblResultsets.setLayoutData(gd_lblResultsets);
		lblResultsets.setText("resultSets");

		tsqlResultSets = new Text(composite_1, SWT.BORDER);
		tsqlResultSets.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
		formToolkit.adapt(tsqlResultSets, true, true);

		lblResultordered = new Label(composite_1, SWT.NONE);
		lblResultordered.setToolTipText("resultOrdered");
		GridData gd_lblResultordered = new GridData(SWT.RIGHT, SWT.CENTER, false, false, 1, 1);
		gd_lblResultordered.widthHint = 50;
		lblResultordered.setLayoutData(gd_lblResultordered);
		lblResultordered.setText("resultOrdered");

		tsqlResultOrdered = new Text(composite_1, SWT.BORDER);
		tsqlResultOrdered.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
		formToolkit.adapt(tsqlResultOrdered, true, true);

		lblTimeout = new Label(composite_1, SWT.NONE);
		GridData gd_lblTimeout = new GridData(SWT.LEFT, SWT.CENTER, false, false, 1, 1);
		gd_lblTimeout.widthHint = 50;
		lblTimeout.setLayoutData(gd_lblTimeout);
		lblTimeout.setToolTipText("timeout：设置驱动程序等待数据库返回请求结果，\r\n并抛出异常时间的最大等待值。\r\n默认不设置（驱动自行处理）。");
		lblTimeout.setText("timeout");

		tsqlTimeout = new Text(composite_1, SWT.BORDER);
		tsqlTimeout.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
		tsqlTimeout.setToolTipText("");
		formToolkit.adapt(tsqlTimeout, true, true);

		lblKeycolumn = new Label(composite_1, SWT.NONE);
		lblKeycolumn.setToolTipText("keyColumn");
		GridData gd_lblKeycolumn = new GridData(SWT.RIGHT, SWT.CENTER, false, false, 1, 1);
		gd_lblKeycolumn.widthHint = 50;
		lblKeycolumn.setLayoutData(gd_lblKeycolumn);
		lblKeycolumn.setText("keyColumn");

		tsqlKeyColumn = new Text(composite_1, SWT.BORDER);
		tsqlKeyColumn.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
		formToolkit.adapt(tsqlKeyColumn, true, true);

		lblKeyproperty = new Label(composite_1, SWT.NONE);
		lblKeyproperty.setToolTipText("keyProperty：标记一个属性，MyBatis会通过getGeneratedKeys\r\n或者通过insert语句的selectKey子元素设置它的值。\r\n（仅对insert有用）默认：不设置。");
		GridData gd_lblKeyproperty = new GridData(SWT.RIGHT, SWT.CENTER, false, false, 1, 1);
		gd_lblKeyproperty.widthHint = 50;
		lblKeyproperty.setLayoutData(gd_lblKeyproperty);
		lblKeyproperty.setText("keyProperty");

		tsqlKeyProperty = new Text(composite_1, SWT.BORDER);
		tsqlKeyProperty.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
		formToolkit.adapt(tsqlKeyProperty, true, true);

		lblFetchsize = new Label(composite_1, SWT.NONE);
		lblFetchsize.setToolTipText("fetchSize：驱动程序每次批量返回的结果行数。\r\n默认不设置（驱动自行处理）。");
		GridData gd_lblFetchsize = new GridData(SWT.RIGHT, SWT.CENTER, false, false, 1, 1);
		gd_lblFetchsize.widthHint = 50;
		lblFetchsize.setLayoutData(gd_lblFetchsize);
		lblFetchsize.setText("fetchSize");

		tsqlFetchSize = new Text(composite_1, SWT.BORDER);
		tsqlFetchSize.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
		formToolkit.adapt(tsqlFetchSize, true, true);
		new Label(composite_1, SWT.NONE);
		new Label(composite_1, SWT.NONE);

		Composite composite_2 = new Composite(composite_sqlnode, SWT.NONE);
		FormData fd_composite_2 = new FormData();
		fd_composite_2.top = new FormAttachment(composite_1, 0, SWT.BOTTOM);
		fd_composite_2.bottom = new FormAttachment(100, 0);
		fd_composite_2.right = new FormAttachment(100);
		fd_composite_2.left = new FormAttachment(0);
		composite_2.setLayoutData(fd_composite_2);
		composite_2.setLayout(new FillLayout(SWT.HORIZONTAL));
		formToolkit.paintBordersFor(composite_2);

		sqlTextViewer = new SourceViewer(composite_2, new VerticalRuler(0), SWT.V_SCROLL | SWT.H_SCROLL);
		sqlTextViewer.setEditable(true);
		sqlText = sqlTextViewer.getTextWidget();
		sqlText.addModifyListener(new SqlNodeModifyListener());
		formToolkit.paintBordersFor(sqlText);

		bindData();
	}

	@Override
	public void init(IEditorSite site, IEditorInput input) throws PartInitException {
		setSite(site);
		setInput(input);
	}

	@Override
	public boolean isDirty() {
		return dirty;
	}

	@Override
	public boolean isSaveAsAllowed() {
		return false;
	}

	@Override
	public void doSave(IProgressMonitor monitor) {
		if (!getDirty()) {
			MessageBox mess = new MessageBox(getSite().getShell(), SWT.ICON_INFORMATION | SWT.CANCEL);
			mess.setMessage("没有进行修改，不用保存！");
			mess.open();
			return;
		}
		try {
			if (mapperTable.getItemCount() > 0) {
				int i = mapperTable.getSelectionIndex();
				if (i >= 0) {
					Node node = (Node)mapperTable.getItem(i).getData();
					if (StudioUtil.isConfigNode(node)) {
						buildCfgNode(node);
					} else if ((StudioConst.NODE_SELECT.equals(node.getName())) || (StudioConst.NODE_INSERT.equals(node.getName()))
							|| (StudioConst.NODE_UPDATE.equals(node.getName())) || (StudioConst.NODE_DELETE.equals(node.getName()))
							|| (StudioConst.NODE_SQL.equals(node.getName()))) {
						updateNodeData((Element)node, sqlText.getText());
					} else if (StudioConst.NODE_RESULT_MAP.equals(node.getName())) {
						if (!saveResultMap((Element)node)) {
							return;
						}
					} else if (StudioConst.NODE_PARAMETER_MAP.equals(node.getName())) {
						//
					}
					mapperTableViewer.refresh();
				}
			}
			saveDocumentFile();
			setDirty(false);
			firePropertyChange(PROP_DIRTY);
		} catch (Exception e1) {
			e1.printStackTrace();
			MessageBox mess = new MessageBox(getSite().getShell(), SWT.ICON_ERROR | SWT.CANCEL);
			mess.setMessage("保存结点信息发生错误！");
			mess.open();
			return;
		}
	}

	@Override
	public void doSaveAs() {
	}

	@Override
	public void setFocus() {
		mapperTable.setFocus();
	}

	public void setDirty(boolean dirty) {
		this.dirty = dirty;
	}

	public boolean getDirty() {
		return dirty;
	}

	public IDocumentProvider getDocumentProvider() {
		return docProvider;
	}

	@Override
	protected void setInput(IEditorInput input) {
		super.setInput(input);
	}

	/**
	 * 初始化数据绑定显示
	 */
	private void bindData() {
		try {
			docProvider.connect(getEditorInput());
			IDocument doc = docProvider.getDocument(getEditorInput());
			xmlDoc = new XMLMapperDocument(StudioUtil.parseXmlText(doc.get()));
		} catch (Exception e) {
			e.printStackTrace();
		}
		sl_composite.topControl = null;
		sqlTextViewer.configure(new TextEditorConfiguration());  // 设置编辑着色和辅助对象
		IUndoManager undoManager = new DefaultUndoManager(100);  // 初始化撤销管理器对象，默认可撤销100次
		undoManager.connect(sqlTextViewer);// 将该撤销管理器应用于文档

		mapperTableViewer.setContentProvider(new MapperTableContentProvider()); // 设置内容器
		mapperTableViewer.setLabelProvider(new MapperTableLabelProvider()); // 设置标签器
		mapperTableViewer.setInput(xmlDoc); // 把数据集合给tableView

		resultMapTableViewer.setContentProvider(new ResultMapTableContentProvider());
		resultMapTableViewer.setLabelProvider(new ResultMapTableLabelProvider());

		parameterMapTableViewer.setContentProvider(new ParaMapTableContentProvider());
		parameterMapTableViewer.setLabelProvider(new ParaMapTableLabelProvider());

		tsqlId.addModifyListener(new SqlNodeModifyListener());
		tsqlResultMap.addModifyListener(new SqlNodeModifyListener());
		tsqlParameterType.addModifyListener(new SqlNodeModifyListener());
		tsqlTimeout.addModifyListener(new SqlNodeModifyListener());
		tsqlDatabaseId.addModifyListener(new SqlNodeModifyListener());
		tsqlResultType.addModifyListener(new SqlNodeModifyListener());
		tsqlParameterMap.addModifyListener(new SqlNodeModifyListener());
		tsqlUseGeneratedKeys.addModifyListener(new SqlNodeModifyListener());
		tsqlUseCache.addModifyListener(new SqlNodeModifyListener());
		tsqlFlushCache.addModifyListener(new SqlNodeModifyListener());
		tsqlResultSetType.addModifyListener(new SqlNodeModifyListener());
		tsqlLang.addModifyListener(new SqlNodeModifyListener());
		tsqlStatementType.addModifyListener(new SqlNodeModifyListener());
		tsqlResultSets.addModifyListener(new SqlNodeModifyListener());
		tsqlResultOrdered.addModifyListener(new SqlNodeModifyListener());
		tsqlFetchSize.addModifyListener(new SqlNodeModifyListener());
		tsqlKeyColumn.addModifyListener(new SqlNodeModifyListener());
		tsqlKeyProperty.addModifyListener(new SqlNodeModifyListener());
		
		//初始化选择
		if (mapperTable.getItemCount() > 0) {
			mapperTable.setSelection(0);
			selectShowSqlNodeData((Node)mapperTable.getSelection()[0].getData());
		}
	}

	/**
	 * 当源文件修改后，同步本编辑页的内容
	 */
	public void sysncEdit() {
		try {
			IDocument doc = docProvider.getDocument(getEditorInput());
			xmlDoc = new XMLMapperDocument(StudioUtil.parseXmlText(doc.get()));
		} catch (Exception e) {
			e.printStackTrace();
		}
		mapperTableViewer.setInput(xmlDoc);
		mapperTableViewer.refresh();
		resultMapTableViewer.refresh();
		parameterMapTableViewer.refresh();
		sl_composite.topControl = null;
		Node node = null;
		if (mapperTable.getItemCount() > 0) {
			int i = mapperTable.getSelectionIndex();
			if (i < 0) {
				i = 0;
			}
			mapperTable.setSelection(i);
			node = (Node)mapperTable.getItem(i).getData();
			selectShowSqlNodeData(node);
		}
	}

	/**
	 * 保存整个格式化文档
	 */
	private void saveDocumentFile() throws Exception {
		ByteArrayOutputStream buf = new ByteArrayOutputStream(10240);
		OutputFormat format = OutputFormat.createPrettyPrint(); // createPrettyPrint()缩减型格式,createCompactFormat()紧凑型格式
		format.setEncoding("UTF-8");
		format.setNewlines(true);
		format.setTrimText(false);
		format.setIndent(StudioConst.XML_PREFIX_STR); // 缩进字符
		XMLWriter writer = new XMLWriter(buf, format);
		writer.write(xmlDoc);
		IDocument doc = docProvider.getDocument(getEditorInput());
		byte[] lens = buf.toByteArray();
		String result = new String(lens, "UTF-8"); //防止中文乱码
		String s = StudioUtil.delBlankLine(result); // 去除空行
		doc.set(s);
		docProvider.saveDocument(null, getEditorInput(), doc, true);
	}

	/**
	 * 选择并显示SQL结点的数据
	 */
	private void selectShowSqlNodeData(Node node1) {
		Node node = node1;
		// 保存先前选择的结点
		if (preSelectSqlNode != null && isDirty()) {
			MessageBox messageBox = new MessageBox(this.getSite().getShell(), SWT.ICON_QUESTION | SWT.YES | SWT.NO);
			messageBox.setMessage("是否保存所做的修改？");
			int rc = messageBox.open();
			if (rc == SWT.YES) {
				try {
					if (StudioUtil.isConfigNode(preSelectSqlNode)) { // 配置节点
						buildCfgNode(preSelectSqlNode);
						saveDocumentFile();
						mapperTableViewer.refresh();
					} else if (preSelectSqlNode instanceof Element) {
						if ((StudioConst.NODE_SELECT.equals(preSelectSqlNode.getName()))
								|| (StudioConst.NODE_INSERT.equals(preSelectSqlNode.getName()))
								|| (StudioConst.NODE_UPDATE.equals(preSelectSqlNode.getName()))
								|| (StudioConst.NODE_DELETE.equals(preSelectSqlNode.getName()))
								|| (StudioConst.NODE_SQL.equals(preSelectSqlNode.getName()))) {
							updateNodeData((Element)preSelectSqlNode, sqlText.getText());
							saveDocumentFile();
							mapperTableViewer.refresh();
						} else if (StudioConst.NODE_RESULT_MAP.equals(preSelectSqlNode.getName())) {
							if (saveResultMap((Element)preSelectSqlNode)) {
								saveDocumentFile();
								mapperTableViewer.refresh();
							} else {
								rc = SWT.NO;
							}
						} else if (StudioConst.NODE_PARAMETER_MAP.equals(preSelectSqlNode.getName())) {
							saveDocumentFile();
							mapperTableViewer.refresh();
						}
					} 
				} catch (Exception e) {
					e.printStackTrace();
					MessageBox mess = new MessageBox(this.getSite().getShell(), SWT.ICON_ERROR | SWT.CANCEL);
					mess.setMessage("展示结点信息错误！");
					mess.open();
					return;
				}
			}
			if (rc != SWT.YES) { // 不保存
				if (StudioUtil.isConfigNode(node)) {
					try {
						IDocument doc = docProvider.getDocument(getEditorInput());
						xmlDoc = new XMLMapperDocument(StudioUtil.parseXmlText(doc.get()));
					} catch (Exception e) {
						e.printStackTrace();
					}
					mapperTableViewer.setInput(xmlDoc);
					node = xmlDoc.cfgNode;
				} else if (node instanceof Element) {
					String id = StudioUtil.getNodeId((Element)node);
					try {
						IDocument doc = docProvider.getDocument(getEditorInput());
						xmlDoc = new XMLMapperDocument(StudioUtil.parseXmlText(doc.get()));
					} catch (Exception e) {
						e.printStackTrace();
					}
					mapperTableViewer.setInput(xmlDoc);
					node = null;
					for (int i = 0; i < xmlDoc.getMapNodes().size(); i++) {
						Node tmpnode = xmlDoc.getMapNodes().get(i);
						if ((tmpnode instanceof Element) && (StudioUtil.getNodeId((Element)tmpnode).equals(id))) {
							node = tmpnode;
							for (int j = 0; j < mapperTable.getItemCount(); j++) {
								if (node == mapperTable.getItem(j).getData()) {
									mapperTable.select(j);
									break;
								}
							}
							break;
						}
					}
				}
			}
			setDirty(false);
			firePropertyChange(PROP_DIRTY);
		}
		// 然后显示选中的结点
		if (node == null) {
			sqlTextViewer.setDocument(new Document());
			return;
		}
		String s = StudioUtil.delPerfixBlank(StudioUtil.getNodeText(node), StudioUtil.getNodeLevel(node));
		s = StudioUtil.delBlankLine(s);
		Document doc = new Document(s);
		sqlTextViewer.setDocument(doc);
		switch (node.getNodeType()) {
			case Node.ELEMENT_NODE:
				if (StudioUtil.isConfigNode(node)) {
					showCfgNode(node);
					sl_composite.topControl = composite_config;
				} else if (StudioConst.NODE_SELECT.equals(node.getName()) || StudioConst.NODE_INSERT.equals(node.getName())
						|| StudioConst.NODE_UPDATE.equals(node.getName()) || StudioConst.NODE_DELETE.equals(node.getName())
						|| StudioConst.NODE_SQL.equals(node.getName())) {
					showSqlNodeData((Element)node);
					sl_composite.topControl = composite_sqlnode;
				} else if (StudioConst.NODE_RESULT_MAP.equals(node.getName())) {
					Attribute attr_id = ((Element)node).attribute("id");
					butBaseResultMapRefresh.setVisible(false);
					if (StudioConst.MAP_ID_BaseResultMap.equals(attr_id.getValue())) {
						for (int i = 0; i < mapperTable.getItemCount(); i++) {
							Node node_ = (Node)mapperTable.getItem(i).getData();
							if (StudioUtil.isConfigNode(node_)) {
								showCfgNode(node);
								if (StringUtils.isNotBlank(textTableName.getText())) {
									butBaseResultMapRefresh.setVisible(true);
								}
								break;
							}
						}
					}
					Attribute attr;
					textResultMapName.setText(((attr = ((Element)node).attribute("id")) != null) ? attr.getText() : "");
					textResultMapType.setText(((attr = ((Element)node).attribute("type")) != null) ? attr.getText() : "");
					resultMapTableViewer.setInput(((Element)node).elements());
					sl_composite.topControl = composite_resultmap;
				} else if (StudioConst.NODE_PARAMETER_MAP.equals(node.getName())) {
					Attribute attr;
					textParaMapName.setText(((attr = ((Element)node).attribute("id")) != null) ? attr.getText() : "");
					textParaMapType.setText(((attr = ((Element)node).attribute("type")) != null) ? attr.getText() : "");
					parameterMapTableViewer.setInput(((Element)node).elements());
					sl_composite.topControl = composite_parametermap;
				}
				break;
			case Node.CDATA_SECTION_NODE:
				if (StudioUtil.isConfigNode(node)) {
					showCfgNode(node);
					sl_composite.topControl = composite_config;
				}
				break;
			default:
				sl_composite.topControl = null;
				break;
		}
		// 要重新设置一下修改的状态
		setDirty(false);
		firePropertyChange(PROP_DIRTY);
		// 记住先前选择的接点
		preSelectSqlNode = node;
		composite.layout();
	}

	/**
	 * 显示SQL接点的数据到页面上（sql,select,insert,update,delete）
	 */
	private void showSqlNodeData(Element node) {
		lblDatabaseid.setEnabled(false);
		lblFetchsize.setEnabled(false);
		lblFlushcache.setEnabled(false);
		lblId.setEnabled(false);
		lblKeycolumn.setEnabled(false);
		lblKeyproperty.setEnabled(false);
		lblLang.setEnabled(false);
		lblParametermap.setEnabled(false);
		lblParametertype.setEnabled(false);
		lblResultmap.setEnabled(false);
		lblResultordered.setEnabled(false);
		lblResultsets.setEnabled(false);
		lblResultsettype.setEnabled(false);
		lblResulttype.setEnabled(false);
		lblStatementtype.setEnabled(false);
		lblTimeout.setEnabled(false);
		lblUsecache.setEnabled(false);
		lblUsegeneratedkeys.setEnabled(false);

		tsqlDatabaseId.setEnabled(false);
		tsqlFetchSize.setEnabled(false);
		tsqlFlushCache.setEnabled(false);
		tsqlId.setEnabled(false);
		tsqlKeyColumn.setEnabled(false);
		tsqlKeyProperty.setEnabled(false);
		tsqlLang.setEnabled(false);
		tsqlParameterMap.setEnabled(false);
		tsqlParameterType.setEnabled(false);
		tsqlResultMap.setEnabled(false);
		tsqlResultOrdered.setEnabled(false);
		tsqlResultSets.setEnabled(false);
		tsqlResultSetType.setEnabled(false);
		tsqlResultType.setEnabled(false);
		tsqlStatementType.setEnabled(false);
		tsqlTimeout.setEnabled(false);
		tsqlUseCache.setEnabled(false);
		tsqlUseGeneratedKeys.setEnabled(false);

		tsqlDatabaseId.setText("");
		tsqlFetchSize.setText("");
		tsqlFlushCache.setText("");
		tsqlId.setText("");
		tsqlKeyColumn.setText("");
		tsqlKeyProperty.setText("");
		tsqlLang.setText("");
		tsqlParameterMap.setText("");
		tsqlParameterType.setText("");
		tsqlResultMap.setText("");
		tsqlResultOrdered.setText("");
		tsqlResultSets.setText("");
		tsqlResultSetType.setText("");
		tsqlResultType.setText("");
		tsqlStatementType.setText("");
		tsqlTimeout.setText("");
		tsqlUseCache.setText("");
		tsqlUseGeneratedKeys.setText("");

		Attribute attr;
		if (StudioConst.NODE_SQL.equals(node.getName())) { // sql
			if (!StudioUtil.isConfigNode(node)) {
				lblDatabaseid.setEnabled(true);
				lblId.setEnabled(true);
	
				tsqlDatabaseId.setEnabled(true);
				tsqlId.setEnabled(true);
	
				tsqlDatabaseId.setText(((attr = node.attribute("databaseId")) != null) ? attr.getText() : "");
				tsqlId.setText(((attr = node.attribute("id")) != null) ? attr.getText() : "");
			}
		} else if (StudioConst.NODE_SELECT.equals(node.getName())) { // select
			lblDatabaseid.setEnabled(true);
			lblFetchsize.setEnabled(true);
			lblFlushcache.setEnabled(true);
			lblId.setEnabled(true);
			lblLang.setEnabled(true);
			lblParametermap.setEnabled(true);
			lblParametertype.setEnabled(true);
			lblResultmap.setEnabled(true);
			lblResultordered.setEnabled(true);
			lblResultsets.setEnabled(true);
			lblResultsettype.setEnabled(true);
			lblResulttype.setEnabled(true);
			lblStatementtype.setEnabled(true);
			lblTimeout.setEnabled(true);
			lblUsecache.setEnabled(true);

			tsqlDatabaseId.setEnabled(true);
			tsqlFetchSize.setEnabled(true);
			tsqlFlushCache.setEnabled(true);
			tsqlId.setEnabled(true);
			tsqlLang.setEnabled(true);
			tsqlParameterMap.setEnabled(true);
			tsqlParameterType.setEnabled(true);
			tsqlResultMap.setEnabled(true);
			tsqlResultOrdered.setEnabled(true);
			tsqlResultSets.setEnabled(true);
			tsqlResultSetType.setEnabled(true);
			tsqlResultType.setEnabled(true);
			tsqlStatementType.setEnabled(true);
			tsqlTimeout.setEnabled(true);
			tsqlUseCache.setEnabled(true);

			tsqlDatabaseId.setText(((attr = node.attribute("databaseId")) != null) ? attr.getText() : "");
			tsqlFetchSize.setText(((attr = node.attribute("fetchSize")) != null) ? attr.getText() : "");
			tsqlFlushCache.setText(((attr = node.attribute("flushCache")) != null) ? attr.getText() : "");
			tsqlId.setText(((attr = node.attribute("id")) != null) ? attr.getText() : "");
			tsqlLang.setText(((attr = node.attribute("lang")) != null) ? attr.getText() : "");
			tsqlParameterMap.setText(((attr = node.attribute("parameterMap")) != null) ? attr.getText() : "");
			tsqlParameterType.setText(((attr = node.attribute("parameterType")) != null) ? attr.getText() : "");
			tsqlResultMap.setText(((attr = node.attribute("resultMap")) != null) ? attr.getText() : "");
			tsqlResultOrdered.setText(((attr = node.attribute("resultOrdered")) != null) ? attr.getText() : "");
			tsqlResultSets.setText(((attr = node.attribute("resultSets")) != null) ? attr.getText() : "");
			tsqlResultSetType.setText(((attr = node.attribute("resultSetType")) != null) ? attr.getText() : "");
			tsqlResultType.setText(((attr = node.attribute("resultType")) != null) ? attr.getText() : "");
			tsqlStatementType.setText(((attr = node.attribute("statementType")) != null) ? attr.getText() : "");
			tsqlTimeout.setText(((attr = node.attribute("timeout")) != null) ? attr.getText() : "");
			tsqlUseCache.setText(((attr = node.attribute("useCache")) != null) ? attr.getText() : "");
		} else if (StudioConst.NODE_INSERT.equals(node.getName())) { // insert
			lblDatabaseid.setEnabled(true);
			lblFlushcache.setEnabled(true);
			lblId.setEnabled(true);
			lblKeycolumn.setEnabled(true);
			lblKeyproperty.setEnabled(true);
			lblLang.setEnabled(true);
			lblParametermap.setEnabled(true);
			lblParametertype.setEnabled(true);
			lblStatementtype.setEnabled(true);
			lblTimeout.setEnabled(true);
			lblUsegeneratedkeys.setEnabled(true);

			tsqlDatabaseId.setEnabled(true);
			tsqlFlushCache.setEnabled(true);
			tsqlId.setEnabled(true);
			tsqlKeyColumn.setEnabled(true);
			tsqlKeyProperty.setEnabled(true);
			tsqlLang.setEnabled(true);
			tsqlParameterMap.setEnabled(true);
			tsqlParameterType.setEnabled(true);
			tsqlStatementType.setEnabled(true);
			tsqlTimeout.setEnabled(true);
			tsqlUseGeneratedKeys.setEnabled(true);

			tsqlDatabaseId.setText(((attr = node.attribute("databaseId")) != null) ? attr.getText() : "");
			tsqlFlushCache.setText(((attr = node.attribute("flushCache")) != null) ? attr.getText() : "");
			tsqlId.setText(((attr = node.attribute("id")) != null) ? attr.getText() : "");
			tsqlKeyColumn.setText(((attr = node.attribute("keyColumn")) != null) ? attr.getText() : "");
			tsqlKeyProperty.setText(((attr = node.attribute("keyProperty")) != null) ? attr.getText() : "");
			tsqlLang.setText(((attr = node.attribute("lang")) != null) ? attr.getText() : "");
			tsqlParameterMap.setText(((attr = node.attribute("parameterMap")) != null) ? attr.getText() : "");
			tsqlParameterType.setText(((attr = node.attribute("parameterType")) != null) ? attr.getText() : "");
			tsqlStatementType.setText(((attr = node.attribute("statementType")) != null) ? attr.getText() : "");
			tsqlTimeout.setText(((attr = node.attribute("timeout")) != null) ? attr.getText() : "");
			tsqlUseGeneratedKeys.setText(((attr = node.attribute("useGeneratedKeys")) != null) ? attr.getText() : "");
		} else if (StudioConst.NODE_UPDATE.equals(node.getName())) { // update
			lblDatabaseid.setEnabled(true);
			lblId.setEnabled(true);
			lblLang.setEnabled(true);
			lblParametermap.setEnabled(true);
			lblParametertype.setEnabled(true);
			lblStatementtype.setEnabled(true);
			lblTimeout.setEnabled(true);

			tsqlDatabaseId.setEnabled(true);
			tsqlId.setEnabled(true);
			tsqlLang.setEnabled(true);
			tsqlParameterMap.setEnabled(true);
			tsqlParameterType.setEnabled(true);
			tsqlStatementType.setEnabled(true);
			tsqlTimeout.setEnabled(true);

			tsqlDatabaseId.setText(((attr = node.attribute("databaseId")) != null) ? attr.getText() : "");
			tsqlId.setText(((attr = node.attribute("id")) != null) ? attr.getText() : "");
			tsqlLang.setText(((attr = node.attribute("lang")) != null) ? attr.getText() : "");
			tsqlParameterMap.setText(((attr = node.attribute("parameterMap")) != null) ? attr.getText() : "");
			tsqlParameterType.setText(((attr = node.attribute("parameterType")) != null) ? attr.getText() : "");
			tsqlStatementType.setText(((attr = node.attribute("statementType")) != null) ? attr.getText() : "");
			tsqlTimeout.setText(((attr = node.attribute("timeout")) != null) ? attr.getText() : "");
		} else if (StudioConst.NODE_DELETE.equals(node.getName())) { // delete
			lblDatabaseid.setEnabled(true);
			lblFlushcache.setEnabled(true);
			lblId.setEnabled(true);
			lblLang.setEnabled(true);
			lblParametermap.setEnabled(true);
			lblParametertype.setEnabled(true);
			lblStatementtype.setEnabled(true);
			lblTimeout.setEnabled(true);

			tsqlDatabaseId.setEnabled(true);
			tsqlFlushCache.setEnabled(true);
			tsqlId.setEnabled(true);
			tsqlLang.setEnabled(true);
			tsqlParameterMap.setEnabled(true);
			tsqlParameterType.setEnabled(true);
			tsqlStatementType.setEnabled(true);
			tsqlTimeout.setEnabled(true);

			tsqlDatabaseId.setText(((attr = node.attribute("databaseId")) != null) ? attr.getText() : "");
			tsqlFlushCache.setText(((attr = node.attribute("flushCache")) != null) ? attr.getText() : "");
			tsqlId.setText(((attr = node.attribute("id")) != null) ? attr.getText() : "");
			tsqlLang.setText(((attr = node.attribute("lang")) != null) ? attr.getText() : "");
			tsqlParameterMap.setText(((attr = node.attribute("parameterMap")) != null) ? attr.getText() : "");
			tsqlParameterType.setText(((attr = node.attribute("parameterType")) != null) ? attr.getText() : "");
			tsqlStatementType.setText(((attr = node.attribute("statementType")) != null) ? attr.getText() : "");
			tsqlTimeout.setText(((attr = node.attribute("timeout")) != null) ? attr.getText() : "");
		}
	}

	/**
	 * 更新单个节点的数据
	 * 
	 * @throws Exception
	 */
	private void updateNodeData(Element node, String content) throws Exception {
		StudioUtil.setElementContent(node, StudioUtil.delBlankLine(content));
		if (StudioConst.NODE_SQL.equals(node.getName())) { // sql
			updateAttribute(node, "databaseId", tsqlDatabaseId);
			updateAttribute(node, "id", tsqlId);
		} else if (StudioConst.NODE_SELECT.equals(node.getName())) { // select
			updateAttribute(node, "databaseId", tsqlDatabaseId);
			updateAttribute(node, "fetchSize", tsqlFetchSize);
			updateAttribute(node, "flushCache", tsqlFlushCache);
			updateAttribute(node, "id", tsqlId);
			updateAttribute(node, "lang", tsqlLang);
			updateAttribute(node, "parameterMap", tsqlParameterMap);
			updateAttribute(node, "parameterType", tsqlParameterType);
			updateAttribute(node, "resultMap", tsqlResultMap);
			updateAttribute(node, "resultOrdered", tsqlResultOrdered);
			updateAttribute(node, "resultSets", tsqlResultSets);
			updateAttribute(node, "resultSetType", tsqlResultSetType);
			updateAttribute(node, "resultType", tsqlResultType);
			updateAttribute(node, "statementType", tsqlStatementType);
			updateAttribute(node, "timeout", tsqlTimeout);
			updateAttribute(node, "useCache", tsqlUseCache);
		} else if (StudioConst.NODE_INSERT.equals(node.getName())) { // insert
			updateAttribute(node, "databaseId", tsqlDatabaseId);
			updateAttribute(node, "flushCache", tsqlFlushCache);
			updateAttribute(node, "id", tsqlId);
			updateAttribute(node, "keyColumn", tsqlKeyColumn);
			updateAttribute(node, "keyProperty", tsqlKeyProperty);
			updateAttribute(node, "lang", tsqlLang);
			updateAttribute(node, "parameterMap", tsqlParameterMap);
			updateAttribute(node, "parameterType", tsqlParameterType);
			updateAttribute(node, "statementType", tsqlStatementType);
			updateAttribute(node, "timeout", tsqlTimeout);
			updateAttribute(node, "useGeneratedKeys", tsqlUseGeneratedKeys);
		} else if (StudioConst.NODE_UPDATE.equals(node.getName())) { // update
			updateAttribute(node, "databaseId", tsqlDatabaseId);
			updateAttribute(node, "id", tsqlId);
			updateAttribute(node, "lang", tsqlLang);
			updateAttribute(node, "parameterMap", tsqlParameterMap);
			updateAttribute(node, "parameterType", tsqlParameterType);
			updateAttribute(node, "statementType", tsqlStatementType);
			updateAttribute(node, "timeout", tsqlTimeout);
		} else if (StudioConst.NODE_DELETE.equals(node.getName())) { // delete
			updateAttribute(node, "databaseId", tsqlDatabaseId);
			updateAttribute(node, "flushCache", tsqlFlushCache);
			updateAttribute(node, "id", tsqlId);
			updateAttribute(node, "lang", tsqlLang);
			updateAttribute(node, "parameterMap", tsqlParameterMap);
			updateAttribute(node, "parameterType", tsqlParameterType);
			updateAttribute(node, "statementType", tsqlStatementType);
			updateAttribute(node, "timeout", tsqlTimeout);
		}
	}

	private void updateAttribute(Element node, String attrName, Object edit) {
		StudioUtil.updateAttribute(node, attrName, edit);
	}

	/**
	 * 置一个接点为选中状态
	 */
	private boolean setSelectNode(Node node) {
		for (int i = 0; i < mapperTable.getItemCount(); i++) {
			Node node1 = (Node)mapperTable.getItem(i).getData();
			if (node1 == node) {
				mapperTable.setSelection(i);
				selectShowSqlNodeData(node1);
				return true;
			}
		}
		return false;
	}

	/**
	 * 显示配置接点的数据
	 */
	private void showCfgNode(Node node) {
		if (node.getNodeType() == Node.CDATA_SECTION_NODE) {
			showCfgNode((CDATA)node);
		} else if (node.getNodeType() == Node.ELEMENT_NODE){
			Element element = (Element)node;
			for (Iterator<?> i = element.nodeIterator(); i.hasNext();) {
				Node node1 = (Node)i.next();
				if (node1.getNodeType() == Node.CDATA_SECTION_NODE) {
					showCfgNode((CDATA)node1);
				} 
			}
		}
	}
	
	/**
	 * 显示配置接点的数据
	 */
	private void showCfgNode(CDATA cdata) {
		radMapperQuery.setSelection(false);
		radMapperTable.setSelection(false);
		lblTable.setVisible(false);
		textTableName.setVisible(false);
		textTableName.setText("");
		textDataSource.setText("");
		textNamespace.setText("");
		textDaoClass.setText("");
		textBeanClass.setText("");
		
		Element element = cdata.getParent();
		if (!"mapper".equals(element.getName())) {
			element = element.getParent();
		}
		textNamespace.setText(element.attributeValue("namespace"));
		
		ConfigCDATA cfg = StudioUtil.getCDATAConfig(cdata);
		if (cdata != null) {
			textDaoClass.setText(cfg.getDaoclass());
			textBeanClass.setText(cfg.getBeanclass());
			textTableName.setText(cfg.getTablename());
			textInterMapperClass.setText(cfg.getInterclass());
			textDataSource.setText(cfg.getDatasource());
		}
		if (StringUtils.isNotBlank(textTableName.getText())) {
			lblTable.setVisible(true);
			textTableName.setVisible(true);
			radMapperTable.setSelection(true);
		}
	}

	/**
	 * 构建配置接点的数据
	 */
	private void buildCfgNode(Node node) {
		if (!StudioUtil.isConfigNode(node)) {
			return;
		}
		String tablename = StringUtils.trim(textTableName.getText());
		String interclass = StringUtils.trim(textInterMapperClass.getText());
		String daoclass = StringUtils.trim(textDaoClass.getText());
		String beanclass = StringUtils.trim(textBeanClass.getText());
		String datasource = StringUtils.trim(textDataSource.getText());
		String namespace = textNamespace.getText();
		ConfigCDATA cfg = new ConfigCDATA();
		cfg.setNamespace(namespace);
		cfg.setDatasource(datasource);
		cfg.setBeanclass(beanclass);
		cfg.setDaoclass(daoclass);
		cfg.setInterclass(interclass);
		cfg.setTablename(tablename);
		StudioUtil.updateCDATAConfig(xmlDoc, cfg);
	}

	/**
	 * 根据类名称打开Java文件
	 */
	private void openFileByClassName(String classname) {
		IProject project = StudioUtil.getCurrentProject();
		ProjectConfig config = StudioUtil.getConfig();
		boolean activate = OpenStrategy.activateOnOpen();
		IFile file = project.getFile(config.getJavaSrc() + "/" + classname.replaceAll("\\.", "/") + ".java");
		if (!file.exists()) {
			MessageBox mess = new MessageBox(getSite().getShell(), SWT.ICON_ERROR | SWT.CANCEL);
			mess.setMessage("在源文件中找不到，打开失败！");
			mess.open();
			return;
		}
		try {
			IDE.openEditor(getSite().getWorkbenchWindow().getActivePage(), file, activate);
		} catch (PartInitException e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * 根据类名称打开Java文件
	 */
	private void openIntfProjectFileByClassName(String classname) {
		ProjectConfig config = StudioUtil.getConfig();
		IProject project = StudioUtil.getCurrentProject();
		IProject intfproject = StudioUtil.getProject(config.getIntfProject());
		if (intfproject == null) {
			intfproject = project;
		}
		String intfJavaSrc = config.getIntfSrc();
		if (StringUtils.isBlank(intfJavaSrc)) {
			intfJavaSrc = config.getJavaSrc();
		}
		boolean activate = OpenStrategy.activateOnOpen();
		IFile file = intfproject.getFile(intfJavaSrc + "/" + classname.replaceAll("\\.", "/") + ".java");
		if (!file.exists()) {
			MessageBox mess = new MessageBox(getSite().getShell(), SWT.ICON_ERROR | SWT.CANCEL);
			mess.setMessage("在源文件中找不到，打开失败！");
			mess.open();
			return;
		}
		try {
			IDE.openEditor(getSite().getWorkbenchWindow().getActivePage(), file, activate);
		} catch (PartInitException e) {
			e.printStackTrace();
		}
	}

	/**
	 * 保存结果映射
	 */
	private boolean saveResultMap(Element mapElement) {
		String type = StringUtils.trim(textResultMapType.getText());
		String newmapname = StringUtils.trim(textResultMapName.getText());
		String oldmapname = mapElement.attributeValue("id", "");
		if (StringUtils.isBlank(newmapname)) {
			MessageBox mess = new MessageBox(getSite().getShell(), SWT.ICON_ERROR | SWT.CANCEL);
			mess.setMessage("结果映射名称不能为空！");
			mess.open();
			return false;
		}
		for (int i = 0; i < xmlDoc.resultMapNodes.size(); i++) {
			Element ele = xmlDoc.resultMapNodes.get(i);
			if (ele != mapElement && ele.attributeValue("id", "").equals(newmapname)) {
				MessageBox mess = new MessageBox(getSite().getShell(), SWT.ICON_ERROR | SWT.CANCEL);
				mess.setMessage("结果映射名称不能重复！");
				mess.open();
				return false;
			}
		}
		if (!oldmapname.equals(newmapname)) {
			MessageBox mess = new MessageBox(getSite().getShell(), SWT.ICON_WARNING | SWT.YES | SWT.NO);
			mess.setMessage("结果映射名称已经更名，引用该结果映射的所有查询都要更新，是否继续！");
			if (mess.open() != SWT.YES) {
				return false;
			}
			for (int i = 0; i < xmlDoc.selectNodes.size(); i++) {
				Element ele = xmlDoc.selectNodes.get(i);
				if (ele.attributeValue("resultMap", "").equals(oldmapname)) {
					StudioUtil.updateAttribute(ele, "resultMap", newmapname);
				}
			}
		}
		StudioUtil.updateAttribute(mapElement, "id", newmapname);
		StudioUtil.updateAttribute(mapElement, "type", type);
		return true;
	}
	
	/**
	 *  排序文档元素
	 */
	private void sortXMLDoc(){
		StudioUtil.sortRootContentByType(xmlDoc, true);
		mapperTableViewer.setInput(xmlDoc);
		setDirty(true);
		firePropertyChange(PROP_DIRTY);
	}

	// =================================内部类===============================

	/**
	 * 新增按钮事件
	 */
	private class ButNewMouseAdapter extends MouseAdapter {
		@Override
		public void mouseUp(MouseEvent e) {
			menuNew.setLocation(e.display.getCursorLocation());
			menuNew.setVisible(true);
		}
	}

	/**
	 * 删除按钮事件
	 */
	private class BtnDeleteMouseAdapter extends MouseAdapter {
		@Override
		public void mouseUp(MouseEvent e) {
			if (mapperTable.getSelectionCount() == 0) {
				MessageBox mess = new MessageBox(getSite().getShell(), SWT.ICON_WARNING | SWT.CANCEL);
				mess.setMessage("请选择要删除的节点！");
				mess.open();
				return;
			}
			//判断结果映射引用
			TableItem[] items = mapperTable.getSelection();
			for (int i = 0; i < items.length; i++) {
				Node node = (Node)items[i].getData();
				if (node instanceof Element) {
					Element ele = (Element)node;
					if (StudioConst.NODE_RESULT_MAP.equals(ele.getName())) {
						String mapName = ele.attributeValue("id");
						for (int j = 0; j < xmlDoc.selectNodes.size(); j++) {
							Element selenode = xmlDoc.selectNodes.get(j);
							String sid = selenode.attributeValue("id", "");
							if (mapName.equals(selenode.attributeValue("resultMap", ""))) {
								MessageBox mess = new MessageBox(getSite().getShell(), SWT.ICON_WARNING | SWT.CANCEL);
								mess.setMessage("已经有查询[" + sid + "]引用此结果映射，不能删除！");
								mess.open();
								return;
							}
						}
					}
				}
			}
			//删除确认
			MessageBox messageBox = new MessageBox(getSite().getShell(), SWT.ICON_QUESTION | SWT.YES | SWT.NO);
			messageBox.setMessage("是否删除选择的节点？");
			int rc = messageBox.open();
			if (rc == SWT.YES) {
				for (int i = 0; i < items.length; i++) {
					Node node = (Node)items[i].getData();
					xmlDoc.getRootElement().remove(node);
				}
				xmlDoc.parseData();
				mapperTableViewer.setInput(xmlDoc);
				setDirty(true);
				firePropertyChange(PROP_DIRTY);
			}
		}
	}

	/**
	 * 按名称排序事件
	 */
	private class ObjectNamSelectionAdapter extends SelectionAdapter {
		@Override
		public void widgetSelected(SelectionEvent e) {
			sortXMLDoc();
		}
	}

	/**
	 * 按类型排序事件
	 */
	private class ObjectTypeSelectionAdapter extends SelectionAdapter {
		@Override
		public void widgetSelected(SelectionEvent e) {
			sortXMLDoc();
		}
	}

	/**
	 * ResultMap字段排序事件
	 */
	private class ResultMapFieldNameSelectionAdapter extends SelectionAdapter {
		@Override
		public void widgetSelected(SelectionEvent e) {
			if (sortResultMapByTypeAsc == null) {
				sortResultMapByTypeAsc = false;
			}
			sortResultMapByTypeAsc = !sortResultMapByTypeAsc;
			boolean asc = sortResultMapByTypeAsc; // 递增排序标记
			TableItem[] items = mapperTable.getSelection();
			if (items != null && items.length > 0) {
				Element resultmap = (Element)items[0].getData();
				Node[] nodes = (Node[])resultmap.elements().toArray(new Node[0]);
				for (int i = 0; i < nodes.length - 1; i++) {
					for (int j = 0; j < nodes.length - (i + 1); j++) {
						Node node1 = nodes[j];
						Node node2 = nodes[j + 1];
						if ("id".equals(node1.getName())) {
							continue;
						} else if ("id".equals(node2.getName())) { // 主键接点排前
							Node tmpnode = nodes[j];
							nodes[j] = nodes[j + 1];
							nodes[j + 1] = tmpnode;
							continue;
						}
						if (!"result".equals(node1.getName()) && !"result".equals(node2.getName())) {
							continue;
						} else if ("result".equals(node1.getName()) && !"result".equals(node2.getName())) {
							continue;
						} else if (!"result".equals(node1.getName()) && "result".equals(node2.getName())) {
							Node tmpnode = nodes[j];
							nodes[j] = nodes[j + 1];
							nodes[j + 1] = tmpnode;
							continue;
						}
						String id1 = ((Element)node1).attributeValue("column");
						String id2 = ((Element)node2).attributeValue("column");
						if (asc) {
							if (id1.compareToIgnoreCase(id2) > 0) {
								Node tmpnode = nodes[j];
								nodes[j] = nodes[j + 1];
								nodes[j + 1] = tmpnode;
							}
						} else {
							if (id1.compareToIgnoreCase(id2) < 0) {
								Node tmpnode = nodes[j];
								nodes[j] = nodes[j + 1];
								nodes[j + 1] = tmpnode;
							}
						}
					}
				}
				int level = StudioUtil.getNodeLevel(resultmap);
				resultmap.clearContent();
				for (int i = 0; i < nodes.length; i++) {
					resultmap.addText("\n" + StringUtils.repeat(StudioConst.XML_PREFIX_STR, level));
					resultmap.add(nodes[i]);
				}
				resultmap.addText("\n" + StringUtils.repeat(StudioConst.XML_PREFIX_STR, level));
				resultMapTableViewer.setInput(resultmap.elements());
				setDirty(true);
				firePropertyChange(PROP_DIRTY);
			}
		}
	}

	/**
	 * SQL对象双击
	 */
	private class MapperTableMouseAdapter extends MouseAdapter {
		@Override
		public void mouseDoubleClick(MouseEvent e) {
			TableItem[] items = mapperTable.getSelection();
			if (items != null && items.length > 0) {
				if (items[0].getData() instanceof Element) {
					final Element element = (Element)items[0].getData();
					if ("select".equals(element.getName())) { // 修改查询语句
						FileEditorInput finput = (FileEditorInput)getEditorInput();
						IFile file = finput.getFile();
						WizardDialog dlg = new WizardDialog(getSite().getShell(), new BuildSelectSQLWizard(xmlDoc, element, false, file, new IBuildSQL() {
							public void successBuild() {
								xmlDoc.parseData();
								mapperTableViewer.setInput(xmlDoc);
								setSelectNode(element);
								setDirty(true);
								firePropertyChange(PROP_DIRTY);
							}
						}));
						Rectangle screenSize = Display.getDefault().getClientArea();
						dlg.setPageSize(screenSize.width, screenSize.height);
						dlg.open();
					} else if ("resultMap".equals(element.getName())) { // 结果映射
						String id = element.attributeValue("id", "");
						if (id.equals(StudioConst.MAP_ID_BaseResultMap) || id.equals(StudioConst.MAP_ID_ResultMapWithBlob)) {
							FileEditorInput finput = (FileEditorInput)getEditorInput();
							IFile file = finput.getFile();
							String tablename = textTableName.getText();
							WizardDialog dlg = new WizardDialog(getSite().getShell(), new ModifyColumnWizard(tablename, xmlDoc, file, element, new IBuildSQL() {
								public void successBuild() {
									xmlDoc.parseData();
									mapperTableViewer.setInput(xmlDoc);
									setSelectNode(element);
									setDirty(true);
									firePropertyChange(PROP_DIRTY);
								}
							}));
							Rectangle screenSize = Display.getDefault().getClientArea();
							dlg.setPageSize(screenSize.width, screenSize.height);
							dlg.open();
						}
					} else if ("insert".equals(element.getName())
							|| "update".equals(element.getName())
							|| "delete".equals(element.getName())) { // 插入、修改、删除语句
						FileEditorInput finput = (FileEditorInput)getEditorInput();
						IFile file = finput.getFile();
						WizardDialog dlg = new WizardDialog(getSite().getShell(), new BuildSQLWizard(xmlDoc, element, false, file, new IBuildSQL() {
							public void successBuild() {
								xmlDoc.parseData();
								mapperTableViewer.setInput(xmlDoc);
								setSelectNode(element);
								setDirty(true);
								firePropertyChange(PROP_DIRTY);
							}
						}));
						Rectangle screenSize = Display.getDefault().getClientArea();
						dlg.setPageSize(screenSize.width, screenSize.height);
						dlg.open();
					}
				}
			}
		}
	}

	/**
	 * 双击修改结果映射属性
	 */
	private class ResultMapMouseAdapter extends MouseAdapter {
		@Override
		public void mouseDoubleClick(MouseEvent e) {
			TableItem[] items = mapperTable.getSelection();
			if (items != null && items.length > 0) {
				Element resultmap = (Element)items[0].getData();
				if (!"resultMap".equals(resultmap.getName())) {
					return;
				}
				TableItem[] rlItems = resultMapTable.getSelection();
				if (rlItems.length <= 0) {
					return;
				}
				Element element = (Element)rlItems[0].getData();
				ResultMapDialog dialog = new ResultMapDialog(getSite().getShell(), SWT.ICON_INFORMATION | SWT.YES | SWT.NO);
				element = (Element)dialog.open(element);
				if (element != null) {
					resultMapTableViewer.setInput(resultmap.elements());
					setDirty(true);
					firePropertyChange(PROP_DIRTY);
				}
			}
		}
	}

	/**
	 * 新增结果映射属性
	 */
	private class BtnResultMapNewMouseAdapter extends MouseAdapter {
		@Override
		public void mouseUp(MouseEvent e) {
			TableItem[] items = mapperTable.getSelection();
			if (items != null && items.length > 0) {
				Element resultmap = (Element)items[0].getData();
				if (!"resultMap".equals(resultmap.getName())) {
					return;
				}
				ResultMapDialog dialog = new ResultMapDialog(getSite().getShell(), SWT.ICON_INFORMATION | SWT.YES | SWT.NO);
				Object result = dialog.open();
				if ((result != null) && (result instanceof Element)) {
					Element element = (Element)result;
					int level = StudioUtil.getNodeLevel(resultmap);
					resultmap.add(element);
					resultmap.addText("\n" + StringUtils.repeat(StudioConst.XML_PREFIX_STR, level));
					resultMapTableViewer.setInput(resultmap.elements());
					if (StudioConst.MAP_ID_BaseResultMap.equals(resultmap.attributeValue("id"))) {
						String BaseColumnList = "";
						for (int i = 0; i < resultMapTable.getItems().length; i++) {
							Node node = (Node)resultMapTable.getItem(i).getData();
							if (node instanceof Element) {
								Element elem = (Element)node;
								String column = elem.attributeValue("column");
								if (i % 6 == 0) {
									BaseColumnList += "\n" + StringUtils.repeat(StudioConst.XML_PREFIX_STR, level);
								}
								BaseColumnList += column + (i==resultMapTable.getItems().length-1 ? "" : ",");
							}
						};
						BaseColumnList += "\n" + StringUtils.repeat(StudioConst.XML_PREFIX_STR, level - 1);
						xmlDoc.BaseColumnList.setText(BaseColumnList);
					}
					setDirty(true);
					firePropertyChange(PROP_DIRTY);
				}
			}
		}
	}

	/**
	 * 修改结果映射属性
	 */
	private class BtnResultMapModifyMouseAdapter extends MouseAdapter {
		@Override
		public void mouseUp(MouseEvent e) {
			TableItem[] items = mapperTable.getSelection();
			if (items != null && items.length > 0) {
				Element resultmap = (Element)items[0].getData();
				if (!"resultMap".equals(resultmap.getName())) {
					return;
				}
				TableItem[] rlItems = resultMapTable.getSelection();
				if (rlItems.length <= 0) {
					return;
				}
				Element element = (Element)rlItems[0].getData();
				ResultMapDialog dialog = new ResultMapDialog(getSite().getShell(), SWT.ICON_INFORMATION | SWT.YES | SWT.NO);
				element = (Element)dialog.open(element);
				if (element != null) {
					resultMapTableViewer.setInput(resultmap.elements());
					setDirty(true);
					firePropertyChange(PROP_DIRTY);
				}
			}
		}
	}

	/**
	 * 删除结果映射属性
	 */
	private class BtnResultMapDeleteMouseAdapter extends MouseAdapter {
		@Override
		public void mouseUp(MouseEvent e) {
			TableItem[] items = mapperTable.getSelection();
			if (items != null && items.length > 0) {
				Element resultmap = (Element)items[0].getData();
				if (!"resultMap".equals(resultmap.getName())) {
					return;
				}
				TableItem[] rlItems = resultMapTable.getSelection();
				for (int i = 0; i < rlItems.length; i++) {
					Node node = (Node)rlItems[0].getData();
					resultmap.remove(node);
				}
				resultMapTableViewer.setInput(resultmap.elements());
				if (StudioConst.MAP_ID_BaseResultMap.equals(resultmap.attributeValue("id"))) {
					int level = StudioUtil.getNodeLevel(resultmap);
					String BaseColumnList = "";
					for (int i = 0; i < resultMapTable.getItems().length; i++) {
						Node node = (Node)resultMapTable.getItem(i).getData();
						if (node instanceof Element) {
							Element elem = (Element)node;
							String column = elem.attributeValue("column");
							if (i % 6 == 0) {
								BaseColumnList += "\n" + StringUtils.repeat(StudioConst.XML_PREFIX_STR, level);
							}
							BaseColumnList += column + (i==resultMapTable.getItems().length-1 ? "" : ",");
						}
					};
					BaseColumnList += "\n" + StringUtils.repeat(StudioConst.XML_PREFIX_STR, level - 1);
					xmlDoc.BaseColumnList.setText(BaseColumnList);
				}
				setDirty(true);
				firePropertyChange(PROP_DIRTY);
			}
		}
	}
	
	/**
	 * 刷新表的字段映射属性
	 */
	private class BtnBaseResultMapRefreshMouseAdapter extends MouseAdapter {
		@Override
		public void mouseUp(MouseEvent e) {
			if (xmlDoc == null) {
				return;
			}
			ConfigCDATA cfg = StudioUtil.getCDATAConfig(xmlDoc);
			if (cfg == null || StringUtils.isBlank(cfg.getDatasource()) || StringUtils.isBlank(cfg.getTablename())) {
				return;
			}
			//切换数据源
			ProjectConfig config = StudioUtil.getConfig();
			config.setDataSource(cfg.getDatasource());
			StudioUtil.saveConfig(config);
			try {
				TableItem[] items = mapperTable.getSelection();
				if (items != null && items.length > 0) {
					Element resultmap = (Element)items[0].getData();
					if (!"resultMap".equals(resultmap.getName())) {
						return;
					}
					String tablename = xmlDoc.getTableName();
					String beanclass = xmlDoc.getBeanClass();
					String blobbeanclass = beanclass + "WithBlob";
					//获取表字段
					Field[] fields = DbUtil.getEngine().getColumnDefine(cfg.getTablename());
					List<Field> keys = new ArrayList<Field>();
					for (int i = 0; i < fields.length; i++) {
						if (fields[i].isPrimaryKey()) {
							keys.add(fields[i]);
						}
					}
					String identitycolumn = "";
					List<Field> basefieldlist = new ArrayList<Field>();
					List<Field> blobfieldlist = new ArrayList<Field>();
					for (int i = 0; i < fields.length; i++) {
						Field field = fields[i];
						if (field.isIdentity()) {
							identitycolumn = field.getColumnName();
						}
						if (DbUtil.isBLOBType(field.getDataType(), field.getColumnSize())) {
							blobfieldlist.add(field);
						} else {
							basefieldlist.add(field);
						}
					}
					Field[] primaryKeys = keys.toArray(new Field[] {});
					Field[] basefields = basefieldlist.toArray(new Field[] {});
					Field[] blobfields = blobfieldlist.toArray(new Field[] {});
					boolean isKeys = keys.size() > 1;
					boolean isBlob = blobfields.length > 0;
					//更新BaseResultMap、BaseColumnList
					int level = StudioUtil.getNodeLevel(resultmap);
					String BaseColumnList = "";
					resultmap.clearContent();
					for (int i = 0; i < basefields.length; i++) {
						Field field = basefields[i];
						if (field.isIdentity()) {
							cfg.setIdentitycolumn(field.getColumnName());
						}
						if (i % 6 == 0) {
							BaseColumnList += "\n" + StringUtils.repeat(StudioConst.XML_PREFIX_STR, level);
						}
						BaseColumnList += field.getColumnName() + (i==basefields.length-1 ? "" : ",");
						Element fieldm = DocumentHelper.createElement(field.isPrimaryKey() ? "id" : "result");
						StudioUtil.updateAttribute(fieldm, "column", field.getColumnName());
						StudioUtil.updateAttribute(fieldm, "property", field.getProperty());
						StudioUtil.updateAttribute(fieldm, "jdbcType", field.getJdbcType());
						resultmap.add(fieldm);
						resultmap.addText("\n" + StringUtils.repeat(StudioConst.XML_PREFIX_STR, level));
					}
					BaseColumnList += "\n" + StringUtils.repeat(StudioConst.XML_PREFIX_STR, level - 1);
					xmlDoc.BaseColumnList.setText(BaseColumnList);
					//更新sql语句
					String s_Insert = BuildUtil.buildInsert(tablename, (isBlob ? blobbeanclass : beanclass), basefields);
					String s_InsertSelective = BuildUtil.buildInsertSelective(tablename, (isBlob ? blobbeanclass : beanclass), fields);
					String s_UpdateByAdapter = BuildUtil.buildUpdateByAdapter(tablename, basefields);
					String s_UpdateByAdapterWithBlob = (!isBlob) ? "" : BuildUtil.buildUpdateByAdapterWithBlob(tablename, fields);
					String s_UpdateByAdapterSelective = BuildUtil.buildUpdateByAdapterSelective(tablename, fields);
					String s_UpdateByPrimaryKey = BuildUtil.buildUpdateByPrimaryKey(tablename, basefields, primaryKeys, beanclass);
					String s_UpdateByPrimaryKeySelective = BuildUtil.buildUpdateByPrimaryKeySelective(tablename, fields, primaryKeys, (isBlob ? blobbeanclass : beanclass));
					if (xmlDoc.insert != null) {
						String content = StudioUtil.delFirstAndTailLine(s_Insert);
						StudioUtil.setElementContent(xmlDoc.insert, content);
					}
					if (xmlDoc.insertSelective != null) {
						String content = StudioUtil.delFirstAndTailLine(s_InsertSelective);
						StudioUtil.setElementContent(xmlDoc.insertSelective, content);
					}
					if (xmlDoc.updateByAdapter != null) {
						String content = StudioUtil.delFirstAndTailLine(s_UpdateByAdapter);
						StudioUtil.setElementContent(xmlDoc.updateByAdapter, content);
					}
					if (xmlDoc.updateByAdapterWithBlob != null) {
						String content = StudioUtil.delFirstAndTailLine(s_UpdateByAdapterWithBlob);
						StudioUtil.setElementContent(xmlDoc.updateByAdapterWithBlob, content);
					}
					if (xmlDoc.updateByAdapterSelective != null) {
						String content = StudioUtil.delFirstAndTailLine(s_UpdateByAdapterSelective);
						StudioUtil.setElementContent(xmlDoc.updateByAdapterSelective, content);
					}
					if (xmlDoc.updateByPrimaryKey != null) {
						String content = StudioUtil.delFirstAndTailLine(s_UpdateByPrimaryKey);
						StudioUtil.setElementContent(xmlDoc.updateByPrimaryKey, content);
					}
					if (xmlDoc.updateByPrimaryKeySelective != null) {
						String content = StudioUtil.delFirstAndTailLine(s_UpdateByPrimaryKeySelective);
						StudioUtil.setElementContent(xmlDoc.updateByPrimaryKeySelective, content);
					}
					//-----------------------------------------
					StudioUtil.updateCDATAConfig(xmlDoc, cfg);
					resultMapTableViewer.setInput(resultmap.elements());
					setDirty(true);
					firePropertyChange(PROP_DIRTY);
				}
			} catch (Exception ee) {
				log.error(ee.getMessage(), ee);
				MessageBox messageBox = new MessageBox(getSite().getShell(), SWT.ICON_WARNING | SWT.YES);
				messageBox.setMessage(ee.getMessage());
			}
		}
	}

	/**
	 * 参数映射列表按类型排序
	 */
	private class PermeterMapModeSelectionAdapter extends SelectionAdapter {
		@Override
		public void widgetSelected(SelectionEvent e) {
			if (sortPermeterMapByMode == null) {
				sortPermeterMapByMode = false;
			}
			sortPermeterMapByMode = !sortPermeterMapByMode;
			boolean asc = sortPermeterMapByMode; // 递增排序标记
			TableItem[] items = mapperTable.getSelection();
			if (items != null && items.length > 0) {
				Element parametermap = (Element)items[0].getData();
				Node[] nodes = (Node[])parametermap.elements().toArray(new Node[0]);
				for (int i = 0; i < nodes.length - 1; i++) {
					for (int j = 0; j < nodes.length - (i + 1); j++) {
						Node node1 = nodes[j];
						Node node2 = nodes[j + 1];
						String id1 = ((Element)node1).attributeValue("mode");
						String id2 = ((Element)node2).attributeValue("mode");
						if (asc) {
							if (id1 != null && id1.compareToIgnoreCase(id2) > 0) {
								Node tmpnode = nodes[j];
								nodes[j] = nodes[j + 1];
								nodes[j + 1] = tmpnode;
							}
						} else {
							if (id1 != null && id1.compareToIgnoreCase(id2) < 0) {
								Node tmpnode = nodes[j];
								nodes[j] = nodes[j + 1];
								nodes[j + 1] = tmpnode;
							}
						}
					}
				}
				int level = StudioUtil.getNodeLevel(parametermap);
				parametermap.clearContent();
				for (int i = 0; i < nodes.length; i++) {
					parametermap.addText("\n" + StringUtils.repeat(StudioConst.XML_PREFIX_STR, level));
					parametermap.add(nodes[i]);
				}
				parametermap.addText("\n" + StringUtils.repeat(StudioConst.XML_PREFIX_STR, level));
				parameterMapTableViewer.setInput(parametermap.elements());
				setDirty(true);
				firePropertyChange(PROP_DIRTY);
			}
		}
	}

	/**
	 * 参数映射列表按名称排序
	 */
	private class PermeterMapPropertySelectionAdapter extends SelectionAdapter {
		@Override
		public void widgetSelected(SelectionEvent e) {
			if (sortPermeterMapByProperty == null) {
				sortPermeterMapByProperty = false;
			}
			sortPermeterMapByProperty = !sortPermeterMapByProperty;
			boolean asc = sortPermeterMapByProperty; // 递增排序标记
			TableItem[] items = mapperTable.getSelection();
			if (items != null && items.length > 0) {
				Element parametermap = (Element)items[0].getData();
				Node[] nodes = (Node[])parametermap.elements().toArray(new Node[0]);
				for (int i = 0; i < nodes.length - 1; i++) {
					for (int j = 0; j < nodes.length - (i + 1); j++) {
						Node node1 = nodes[j];
						Node node2 = nodes[j + 1];
						String id1 = ((Element)node1).attributeValue("property");
						String id2 = ((Element)node2).attributeValue("property");
						if (asc) {
							if (id1 != null && id1.compareToIgnoreCase(id2) > 0) {
								Node tmpnode = nodes[j];
								nodes[j] = nodes[j + 1];
								nodes[j + 1] = tmpnode;
							}
						} else {
							if (id1 != null && id1.compareToIgnoreCase(id2) < 0) {
								Node tmpnode = nodes[j];
								nodes[j] = nodes[j + 1];
								nodes[j + 1] = tmpnode;
							}
						}
					}
				}
				int level = StudioUtil.getNodeLevel(parametermap);
				parametermap.clearContent();
				for (int i = 0; i < nodes.length; i++) {
					parametermap.addText("\n" + StringUtils.repeat(StudioConst.XML_PREFIX_STR, level));
					parametermap.add(nodes[i]);
				}
				parametermap.addText("\n" + StringUtils.repeat(StudioConst.XML_PREFIX_STR, level));
				parameterMapTableViewer.setInput(parametermap.elements());
				setDirty(true);
				firePropertyChange(PROP_DIRTY);
			}
		}
	}

	/**
	 * 新增参数映射事件
	 */
	private class BtnParameterMapNewMouseAdapter extends MouseAdapter {
		@Override
		public void mouseUp(MouseEvent e) {
			TableItem[] items = mapperTable.getSelection();
			if (items != null && items.length > 0) {
				Element resultmap = (Element)items[0].getData();
				if (!"parameterMap".equals(resultmap.getName())) {
					return;
				}
				ParameterMapDialog dialog = new ParameterMapDialog(getSite().getShell(), SWT.ICON_INFORMATION | SWT.YES | SWT.NO);
				Object result = dialog.open();
				if ((result != null) && (result instanceof Element)) {
					Element element = (Element)result;
					int level = StudioUtil.getNodeLevel(resultmap);
					resultmap.add(element);
					resultmap.addText("\n" + StringUtils.repeat(StudioConst.XML_PREFIX_STR, level));
					parameterMapTableViewer.setInput(resultmap.elements());
					setDirty(true);
					firePropertyChange(PROP_DIRTY);
				}
			}
		}
	}

	/**
	 * 删除参数映射事件
	 */
	private class BtnParameterMapDeleteMouseAdapter extends MouseAdapter {
		@Override
		public void mouseUp(MouseEvent e) {
			TableItem[] items = mapperTable.getSelection();
			if (items != null && items.length > 0) {
				Element resultmap = (Element)items[0].getData();
				if (!"parameterMap".equals(resultmap.getName())) {
					return;
				}
				TableItem[] rlItems = parameterMapTable.getSelection();
				for (int i = 0; i < rlItems.length; i++) {
					Node node = (Node)rlItems[0].getData();
					resultmap.remove(node);
				}
				parameterMapTableViewer.setInput(resultmap.elements());
				setDirty(true);
				firePropertyChange(PROP_DIRTY);
			}
		}
	}

	/**
	 * 双击修改参数映射属性
	 */
	private class ParameterMapMouseAdapter extends MouseAdapter {
		@Override
		public void mouseDoubleClick(MouseEvent e) {
			TableItem[] items = mapperTable.getSelection();
			if (items != null && items.length > 0) {
				Element resultmap = (Element)items[0].getData();
				if (!"parameterMap".equals(resultmap.getName())) {
					return;
				}
				TableItem[] rlItems = parameterMapTable.getSelection();
				if (rlItems.length <= 0) {
					return;
				}
				Element element = (Element)rlItems[0].getData();
				ParameterMapDialog dialog = new ParameterMapDialog(getSite().getShell(), SWT.ICON_INFORMATION | SWT.YES | SWT.NO);
				element = (Element)dialog.open(element);
				if (element != null) {
					parameterMapTableViewer.setInput(resultmap.elements());
					setDirty(true);
					firePropertyChange(PROP_DIRTY);
				}
			}
		}
	}

	/**
	 * 新增菜单事件
	 */
	private class BtnNewMenuItemSelectionAdapter extends SelectionAdapter {
		@Override
		public void widgetSelected(SelectionEvent e) {
			if (!(e.getSource() instanceof MenuItem)) {
				return;
			}
			MenuItem menu = (MenuItem)e.getSource();
			if (menu == menuItemConfig) { // 新增配置节点
				TableItem[] list = mapperTable.getItems();
				for (int i = 0; i < list.length; i++) {
					Node node = (Node)list[i].getData();
					if (StudioUtil.isConfigNode(node)) {
						MessageBox mess = new MessageBox(getSite().getShell(), SWT.ICON_WARNING | SWT.CANCEL);
						mess.setMessage("已经存在配置节点，不能重复增加");
						mess.open();
						return;
					}
				}
				String prefix = StringUtils.repeat(StudioConst.XML_PREFIX_STR, 1);
				CDATA cdata = DocumentHelper.createCDATA(" " + StudioConst.NODE_CDATA_CFG_FLAG + "\n" + prefix + "\n" + prefix);
				org.dom4j.Text textNode = DocumentHelper.createText("\n" + prefix);
				Element element = DocumentHelper.createElement("sql");
				element.addAttribute("id", StudioConst.MAP_ID_CFGNODE);
				List<CharacterData> lsCont = element.content();
				lsCont.add(0, cdata);
				lsCont.add(0, textNode);
				List<Element> ls = xmlDoc.getRootElement().elements();
				ls.add(element);
				xmlDoc.parseData();
				StudioUtil.sortRootContentByName(xmlDoc, true);
				StudioUtil.sortRootContentByType(xmlDoc, true);
				mapperTableViewer.setInput(xmlDoc);
				setSelectNode(cdata);
			} else if (menu == menuItemSqlInclude) { // 新增SQL引入
				Element element = DocumentHelper.createElement("sql");
				element.addAttribute("id", StudioUtil.getNewNodeId(xmlDoc, "sql"));
				List<Element> ls = xmlDoc.getRootElement().elements();
				ls.add(element);
				xmlDoc.parseData();
				StudioUtil.sortRootContentByName(xmlDoc, true);
				StudioUtil.sortRootContentByType(xmlDoc, true);
				mapperTableViewer.setInput(xmlDoc);
				setSelectNode(element);
			} else if (menu == menuItemResultMap) { // 新增结果映射

			} else if (menu == menuItemParaMap) { // 新增参数映射

			} else if (menu == menuItemSelect) { // 新增查询SQL
				final Element element = DocumentHelper.createElement("select");
				element.addAttribute("id", StudioUtil.getNewNodeId(xmlDoc, "select"));
				FileEditorInput finput = (FileEditorInput)getEditorInput();
				IFile file = finput.getFile();
				WizardDialog dlg = new WizardDialog(getSite().getShell(), new BuildSelectSQLWizard(xmlDoc, element, true, file, new IBuildSQL() {
					public void successBuild() {
						List<Element> ls = xmlDoc.getRootElement().elements();
						ls.add(element);
						xmlDoc.parseData();
						StudioUtil.sortRootContentByName(xmlDoc, true);
						StudioUtil.sortRootContentByType(xmlDoc, true);
						mapperTableViewer.setInput(xmlDoc);
						setSelectNode(element);
						setDirty(true);
						firePropertyChange(PROP_DIRTY);
					}
				}));
				dlg.setPageSize(700, 600);
				dlg.open();
			} else if (menu == menuItemInsert) { // 新增插入SQL
				final Element element = DocumentHelper.createElement("insert");
				element.addAttribute("id", StudioUtil.getNewNodeId(xmlDoc, "insert"));
				FileEditorInput finput = (FileEditorInput)getEditorInput();
				IFile file = finput.getFile();
				WizardDialog dlg = new WizardDialog(getSite().getShell(), new BuildSQLWizard(xmlDoc, element, true, file, new IBuildSQL() {
					public void successBuild() {
						List<Element> ls = xmlDoc.getRootElement().elements();
						ls.add(element);
						xmlDoc.parseData();
						StudioUtil.sortRootContentByName(xmlDoc, true);
						StudioUtil.sortRootContentByType(xmlDoc, true);
						mapperTableViewer.setInput(xmlDoc);
						setSelectNode(element);
						setDirty(true);
						firePropertyChange(PROP_DIRTY);
					}
				}));
				dlg.setPageSize(700, 600);
				dlg.open();
			} else if (menu == menuItemUpdate) { // 新增更新SQL
				final Element element = DocumentHelper.createElement("update");
				element.addAttribute("id", StudioUtil.getNewNodeId(xmlDoc, "update"));
				FileEditorInput finput = (FileEditorInput)getEditorInput();
				IFile file = finput.getFile();
				WizardDialog dlg = new WizardDialog(getSite().getShell(), new BuildSQLWizard(xmlDoc, element, true, file, new IBuildSQL() {
					public void successBuild() {
						List<Element> ls = xmlDoc.getRootElement().elements();
						ls.add(element);
						xmlDoc.parseData();
						StudioUtil.sortRootContentByName(xmlDoc, true);
						StudioUtil.sortRootContentByType(xmlDoc, true);
						mapperTableViewer.setInput(xmlDoc);
						setSelectNode(element);
						setDirty(true);
						firePropertyChange(PROP_DIRTY);
					}
				}));
				dlg.setPageSize(700, 600);
				dlg.open();
			} else if (menu == menuItemDelete) { // 新增删除SQL
				final Element element = DocumentHelper.createElement("delete");
				element.addAttribute("id", StudioUtil.getNewNodeId(xmlDoc, "delete"));
				FileEditorInput finput = (FileEditorInput)getEditorInput();
				IFile file = finput.getFile();
				WizardDialog dlg = new WizardDialog(getSite().getShell(), new BuildSQLWizard(xmlDoc, element, true, file, new IBuildSQL() {
					public void successBuild() {
						List<Element> ls = xmlDoc.getRootElement().elements();
						ls.add(element);
						xmlDoc.parseData();
						StudioUtil.sortRootContentByName(xmlDoc, true);
						StudioUtil.sortRootContentByType(xmlDoc, true);
						mapperTableViewer.setInput(xmlDoc);
						setSelectNode(element);
						setDirty(true);
						firePropertyChange(PROP_DIRTY);
					}
				}));
				dlg.setPageSize(700, 600);
				dlg.open();
			}
			sortXMLDoc();
			setDirty(true);
			firePropertyChange(PROP_DIRTY);
		}
	}

	/**
	 * 保存按钮事件
	 */
	private class ButSaveMouseAdapter extends MouseAdapter {
		@Override
		public void mouseUp(MouseEvent e) {
			doSave(null);
		}
	}

	/**
	 * SQL文本修改事件
	 */
	private class SqlNodeModifyListener implements ModifyListener {
		public void modifyText(ModifyEvent e) {
			setDirty(true);
			firePropertyChange(PROP_DIRTY);
		}
	}

	/**
	 * 配置页修改事件
	 */
	private class ConfigModifyListener implements ModifyListener {
		public void modifyText(ModifyEvent e) {
			setDirty(true);
			firePropertyChange(PROP_DIRTY);
		}
	}
	
	/**
	 * 生成JAVA接口代码
	 */
	private class BtnBuildInterCodeMouseAdapter extends MouseAdapter {
		@Override
		public void mouseUp(MouseEvent e) {
			String interClass = StringUtils.trim(textInterMapperClass.getText());
			if (StringUtils.isBlank(interClass)) {
				interClass = textNamespace.getText();
				textInterMapperClass.setText(interClass);
				setDirty(true);
				firePropertyChange(PROP_DIRTY);
				doSave(null);
			}
			String beanClass = StringUtils.trim(textBeanClass.getText());
			if (StringUtils.isBlank(beanClass)) {
				for (int i = 0; i < xmlDoc.resultMapNodes.size(); i++) {
					String type = xmlDoc.resultMapNodes.get(i).attributeValue("type", "");
					if (!"java.util.HashMap".equals(type)) {
						beanClass = type;
						break;
					}
				}
				textBeanClass.setText(beanClass);
				setDirty(true);
				firePropertyChange(PROP_DIRTY);
				doSave(null);
			}
			String namespace = xmlDoc.namespace;
			String tablename = xmlDoc.tableName;
			boolean IsTable = StringUtils.isNotBlank(xmlDoc.tableName);
			boolean IsBolb = xmlDoc.ResultMapWithBlob != null;
			String beanname = "";
			if (IsTable) {
				beanname = DbUtil.getTableBeanName(xmlDoc.tableName);
			} else {
				beanname = namespace.substring(namespace.lastIndexOf(".") + 1);
				beanname = beanname.replaceAll("Mapper", "");
			}
			//创建JAVA代码
			try {
				FileEditorInput finput = (FileEditorInput)getEditorInput();
				IFile mapperfile = finput.getFile();
				IFolder folder = (IFolder)mapperfile.getParent();
				
				ProjectConfig config = StudioUtil.getConfig();
				IProject project = StudioUtil.getCurrentProject();
				IProject intfproject = StudioUtil.getProject(config.getIntfProject()); //接口工程
				if (intfproject == null) {
					intfproject = project;
				}
				String javaSrc = config.getJavaSrc();
				if (StringUtils.isBlank(javaSrc)) {
					javaSrc = StudioUtil.isMavenProject(project) ? "src/main/java/" : "src";
				}
				if (!javaSrc.endsWith("/")) {
					javaSrc += "/";
				}
				String intfJavaSrc = config.getIntfSrc();
				if (StringUtils.isBlank(intfJavaSrc)) {
					intfJavaSrc = StudioUtil.isMavenProject(intfproject) ? "src/main/java/" : "src";
				}
				if (!intfJavaSrc.endsWith("/")) {
					intfJavaSrc += "/";
				}
				String mapperRoot = config.getMapperRoot();
				if (!mapperRoot.endsWith("/")) {
					mapperRoot += "/";
				}
				IFolder daofolder = (IFolder)folder.getParent();                     // Dao目录
				String srcdaopath = daofolder.getProjectRelativePath().toString();   // 当前dao目录相对路径
				if (srcdaopath.startsWith(mapperRoot)) {
					srcdaopath = srcdaopath.replaceFirst(mapperRoot, javaSrc);
				}
				
				String currentpath = folder.getProjectRelativePath().toString(); // 当前目录相对路径
				String parentPackagename = srcdaopath.replaceAll(javaSrc, "").replaceAll("/", "."); // 父包名称
				String beanclass = xmlDoc.getBeanClass(); // Bean类全名
				String blobbeanclass = StringUtils.isEmpty(beanclass) ? "" : beanclass + "WithBlob"; // BlobBean类全名
				String keybean = beanname + "Key";
				String keybeanclass = StringUtils.isEmpty(beanclass) ? "" :beanclass + "Key"; // KeyBean类全名
				String adapterclass = parentPackagename + "." + config.getModelDir() + "." + beanname + "Adapter"; // 适配器类名称
				String interclass = parentPackagename + "." + config.getInterDir() + "." + beanname + "Mapper"; // Inter接口全名
				String mapperNamespace = interclass; // 映射文件命名空间(namespace)
				
				String mapperfilename = beanname + "Mapper.xml"; // Mapper映射文件名称
				String adapterfilename = adapterclass.substring(adapterclass.lastIndexOf(".") + 1, adapterclass.length()) + ".java";
				String beanfilename = StringUtils.isEmpty(beanclass) ? "" : beanclass.substring(beanclass.lastIndexOf(".") + 1, beanclass.length()) + ".java";
				String blobbeanfilename = StringUtils.isEmpty(beanclass) ? "" :blobbeanclass.substring(blobbeanclass.lastIndexOf(".") + 1, blobbeanclass.length()) + ".java";
				String keybeanfilename = StringUtils.isEmpty(beanclass) ? "" : keybeanclass.substring(keybeanclass.lastIndexOf(".") + 1, keybeanclass.length()) + ".java";
				String interfilename = interclass.substring(interclass.lastIndexOf(".") + 1, interclass.length()) + ".java";
				
				IFile adapterfile = StringUtils.isEmpty(adapterfilename) ? null : project.getFile(srcdaopath + "/" + config.getModelDir() + "/" + adapterfilename);
				IFile intermapperfile = StringUtils.isEmpty(interfilename) ? null :  project.getFile(srcdaopath + "/" + config.getInterDir() + "/" + interfilename);
				IFile beanfile = StringUtils.isEmpty(beanclass) ? null : intfproject.getFile(intfJavaSrc + beanclass.replace('.', '/') + ".java");
				IFile blobbeanfile = StringUtils.isEmpty(blobbeanclass) ? null : intfproject.getFile(intfJavaSrc + blobbeanclass.replace('.', '/') + ".java");
				IFile keybeanFile = StringUtils.isEmpty(keybeanclass) ? null : intfproject.getFile(intfJavaSrc + keybeanclass.replace('.', '/') + ".java");
				
				if (intermapperfile != null) {
					if (intermapperfile.exists()) {
						StringBuffer sb = new StringBuffer();
						sb.append("下列源文件已经存在，是否覆盖？选择是则覆盖原来的代码！\n");
						if (intermapperfile.exists()) {
							sb.append("接口映射文件：").append(interfilename).append("\n");
						}
						MessageBox messageBox = new MessageBox(getSite().getShell(), SWT.ICON_WARNING | SWT.YES | SWT.NO);
						messageBox.setMessage(sb.toString());
						int rc = messageBox.open();
						if (rc != SWT.YES) {
							return;
						}
					} else {
						intermapperfile.create(null, IResource.NONE, null);
					}
				}
				
				Field[] primaryKeys = null, basefields = null, blobfields = null;
				String s_Key = null, s_Bean = null, s_BlobBean = null, s_Adapter = null, s_MapperInter;
				
				if (xmlDoc.BaseResultMap != null) {
					if (!"java.util.HashMap".equals(beanclass)) {
						primaryKeys = StudioUtil.getResultMapKeyFields(xmlDoc.BaseResultMap);
						basefields = StudioUtil.getResultMapFields(xmlDoc.BaseResultMap);
						s_Bean = BuildUtil.buildBean(beanclass, basefields);
						if (primaryKeys.length > 1) {
							s_Key = BuildUtil.buildBean(keybeanclass, primaryKeys);
						}
					}
				}
				if (IsBolb) {
					blobfields = StudioUtil.getResultMapFields(xmlDoc.ResultMapWithBlob);
					s_BlobBean = BuildUtil.buildBlobBean(blobbeanclass, beanclass, blobfields);
				}
				if (StringUtils.isNotBlank(tablename)) {
					s_Adapter = BuildUtil.buildAdapter(adapterclass, basefields);
				}
				s_MapperInter = BuildUtil.buildInterClassCodeByMapper(xmlDoc, daofolder);
				// Java文件保存
				if (StringUtils.isNotBlank(s_Key)) {
					StudioUtil.writeFile(keybeanFile, s_Key);
				}
				if (StringUtils.isNotBlank(s_Bean)) {
					StudioUtil.writeFile(beanfile, s_Bean);
				}
				if (StringUtils.isNotBlank(s_BlobBean)) {
					StudioUtil.writeFile(blobbeanfile, s_BlobBean);
				}
				if (StringUtils.isNotBlank(s_Adapter)) {
					StudioUtil.writeFile(adapterfile, s_Adapter);
				}
				if (StringUtils.isNotBlank(s_MapperInter)) {
					StudioUtil.writeFile(intermapperfile, s_MapperInter);
				}
				for (int i = 0; i < xmlDoc.resultMapNodes.size(); i++) {
					Element resultmap = xmlDoc.resultMapNodes.get(i);
					if (resultmap == xmlDoc.BaseResultMap || resultmap == xmlDoc.ResultMapWithBlob) {
						continue;
					}
					String tmpbeanclass = resultmap.attributeValue("type", "");
					if (!"java.util.HashMap".equals(tmpbeanclass)) {
						Field[] fields = StudioUtil.getResultMapFields(resultmap);
						IFile tmpbeanFile = intfproject.getFile(intfJavaSrc + StringUtils.replace(tmpbeanclass, ".", "/") + ".java");
						String s_TempBean = BuildUtil.buildBean(tmpbeanclass, fields);
						StudioUtil.writeFile(tmpbeanFile, s_TempBean);
					}
				}
				MessageBox mess = new MessageBox(getSite().getShell(), SWT.ICON_INFORMATION | SWT.CANCEL);
				mess.setMessage("生成代码成功！");
				mess.open();
			} catch (Exception ee) {
				ee.printStackTrace();
			}
		}
	}
	
	/**
	 * 生成DAO代码
	 */
	private class BtnBuildDaoCodeMouseAdapter extends MouseAdapter {
		@Override
		public void mouseUp(MouseEvent e) {
			MessageBox messageBox = new MessageBox(getSite().getShell(), SWT.ICON_INFORMATION | SWT.YES);
			messageBox.setMessage("这个功能还没有实现^_^");
			messageBox.open();
		}
	}

	/**
	 * 打开接口类文件
	 */
	private class BtnOpenInterMapperClassMouseAdapter extends MouseAdapter {
		@Override
		public void mouseUp(MouseEvent e) {
			String classname = StringUtils.trim(textInterMapperClass.getText());
			if (StringUtils.isBlank(classname)) {
				return;
			}
			openFileByClassName(classname);
		}
	}

	/**
	 * 打开Bean类文件
	 */
	private class BtnOpenBeanClassMouseAdapter extends MouseAdapter {
		@Override
		public void mouseUp(MouseEvent e) {
			String classname = StringUtils.trim(textBeanClass.getText());
			if (StringUtils.isBlank(classname)) {
				return;
			}
			openIntfProjectFileByClassName(classname);
		}
	}

	/**
	 * 打开Dao类文件
	 */
	private class BtnOpenDaoClassMouseAdapter extends MouseAdapter {
		@Override
		public void mouseUp(MouseEvent e) {
			String classname = StringUtils.trim(textDaoClass.getText());
			if (StringUtils.isBlank(classname)) {
				return;
			}
			openFileByClassName(classname);
		}
	}

	/**
	 * SQL脚本对象选择事件
	 */
	private class MapperTableSelection extends SelectionAdapter {
		@Override
		public void widgetSelected(SelectionEvent e) {
			if (e.item.getData() instanceof Node) {
				Node node = (Node)e.item.getData();
				selectShowSqlNodeData(node);
			}
		}
	}

	/**
	 * 映射对象列表向上按钮事件
	 */
	private class MapperTableUpMouseAdapter extends MouseAdapter {
		@Override
		public void mouseUp(MouseEvent e) {
			Node node = null;
			if (mapperTable.getItemCount() > 0) {
				int i = mapperTable.getSelectionIndex() - 1;
				if (i < 0) {
					i = mapperTable.getItemCount() - 1;
				}
				mapperTable.setSelection(i);
				node = (Node)mapperTable.getItem(i).getData();
			}
			selectShowSqlNodeData(node);
		}
	}

	/**
	 * 映射对象列表向下按钮事件
	 */
	private class MapperTableDownMouseAdapter extends MouseAdapter {
		@Override
		public void mouseUp(MouseEvent e) {
			Node node = null;
			if (mapperTable.getItemCount() > 0) {
				int i = mapperTable.getSelectionIndex() + 1;
				if (i == mapperTable.getItemCount()) {
					i = 0;
				}
				mapperTable.setSelection(i);
				node = (Node)mapperTable.getItem(i).getData();
			}
			selectShowSqlNodeData(node);
		}
	}

	/**
	 * 结果映射列表向上按钮事件
	 */
	private class ResultMapUpMouseAdapter extends MouseAdapter {
		@Override
		public void mouseUp(MouseEvent e) {
			if (resultMapTable.getItemCount() > 0) {
				int i = resultMapTable.getSelectionIndex() - 1;
				if (i < 0) {
					i = resultMapTable.getItemCount() - 1;
				}
				resultMapTable.setSelection(i);
			}
		}
	}

	/**
	 * 结果映射列表向下按钮事件
	 */
	private class ResultMapDownMouseAdapter extends MouseAdapter {
		@Override
		public void mouseUp(MouseEvent e) {
			if (resultMapTable.getItemCount() > 0) {
				int i = resultMapTable.getSelectionIndex() + 1;
				if (i == resultMapTable.getItemCount()) {
					i = 0;
				}
				resultMapTable.setSelection(i);
			}
		}
	}

	/**
	 * 参数映射列表向上按钮事件
	 */
	private class ParameterMapUpMouseAdapter extends MouseAdapter {
		@Override
		public void mouseUp(MouseEvent e) {
			if (parameterMapTable.getItemCount() > 0) {
				int i = parameterMapTable.getSelectionIndex() - 1;
				if (i < 0) {
					i = parameterMapTable.getItemCount() - 1;
				}
				parameterMapTable.setSelection(i);
			}
		}
	}

	/**
	 * 参数映射列表向下按钮事件
	 */
	private class ParameterMapDownMouseAdapter extends MouseAdapter {
		@Override
		public void mouseUp(MouseEvent e) {
			if (parameterMapTable.getItemCount() > 0) {
				int i = parameterMapTable.getSelectionIndex() + 1;
				if (i == parameterMapTable.getItemCount()) {
					i = 0;
				}
				parameterMapTable.setSelection(i);
			}
		}
	}

	/**
	 * SQL脚本对象标题
	 */
	private class MapperTableLabelProvider extends LabelProvider implements ITableLabelProvider {

		public String getColumnText(Object element, int columnIndex) {
			try {
				if (element instanceof Node) {
					Node node = (Node)element;
					switch (node.getNodeType()) {
						case Node.CDATA_SECTION_NODE:
							if (StudioUtil.isConfigNode(node)) {
								if (columnIndex == 0) {
									return StudioConst.NODE_CFG_CDATA_NAME;
								} else if (columnIndex == 1) {
									return StudioConst.NODE_CDATA_CFG_FLAG_NAME;
								}
							}
							break;
						case Node.ELEMENT_NODE:
							Element elem = (Element)element;
							if (columnIndex == 0) {
								return elem.selectSingleNode("@id").getText();
							} else if (columnIndex == 1) {
								if (StudioConst.NODE_SELECT.equals(node.getName())) {
									return StudioConst.NODE_SELECT_NAME;
								} else if (StudioConst.NODE_INSERT.equals(node.getName())) {
									return StudioConst.NODE_INSERT_NAME;
								} else if (StudioConst.NODE_UPDATE.equals(node.getName())) {
									return StudioConst.NODE_UPDATE_NAME;
								} else if (StudioConst.NODE_DELETE.equals(node.getName())) {
									return StudioConst.NODE_DELETE_NAME;
								} else if (StudioConst.NODE_SQL.equals(node.getName())) {
									if (StudioUtil.isConfigNode(node)) {
										if (columnIndex == 0) {
											return StudioConst.NODE_CFG_CDATA_NAME;
										} else if (columnIndex == 1) {
											return StudioConst.NODE_CDATA_CFG_FLAG_NAME;
										}
									}
									return StudioConst.NODE_SQL_NAME;
								} else if (StudioConst.NODE_RESULT_MAP.equals(node.getName())) {
									return StudioConst.NODE_RESULT_MAP_NAME;
								} else if (StudioConst.NODE_PARAMETER_MAP.equals(node.getName())) {
									return StudioConst.NODE_PARAMETER_MAP_NAME;
								}
							} else if (columnIndex == 2) {
								Matcher mat = StudioConst.MAPPER_REMARK_PAT.matcher(node.getText());
								if (mat.find()) {
									return node.getText().substring(mat.start() + 2, mat.end() - 2).trim();
								}
							}
							break;
						default:
							break;
					}
					if (columnIndex == 0) {
						return node.getName();
					}
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
			return null;
		}

		public Image getColumnImage(Object element, int columnIndex) {
			try {
				if (element instanceof Node) {
					Node node = (Node)element;
					switch (node.getNodeType()) {
						case Node.CDATA_SECTION_NODE:
							if (columnIndex == 0) {
								if (StudioUtil.isConfigNode(node)) {
									return StudioUtil.getImage(StudioConst.ICON_NODE_CFG_CDATA);
								}
							}
							break;
						case Node.ELEMENT_NODE:
							if (columnIndex == 0) {
								if (StudioConst.NODE_SELECT.equals(node.getName())) {
									return StudioUtil.getImage(StudioConst.ICON_NODE_SELECT);
								} else if (StudioConst.NODE_INSERT.equals(node.getName())) {
									return StudioUtil.getImage(StudioConst.ICON_NODE_INSERT);
								} else if (StudioConst.NODE_UPDATE.equals(node.getName())) {
									return StudioUtil.getImage(StudioConst.ICON_NODE_UPDATE);
								} else if (StudioConst.NODE_DELETE.equals(node.getName())) {
									return StudioUtil.getImage(StudioConst.ICON_NODE_DELETE);
								} else if (StudioConst.NODE_SQL.equals(node.getName())) {
									if (StudioUtil.isConfigNode(node)) {
										return StudioUtil.getImage(StudioConst.ICON_NODE_CFG_CDATA);
									}
									return StudioUtil.getImage(StudioConst.ICON_NODE_SQL);
								} else if (StudioConst.NODE_RESULT_MAP.equals(node.getName())) {
									return StudioUtil.getImage(StudioConst.ICON_NODE_RESULT_MAP);
								} else if (StudioConst.NODE_PARAMETER_MAP.equals(node.getName())) {
									return StudioUtil.getImage(StudioConst.ICON_NODE_PARAMETER_MAP);
								}
							}
							break;
						default:
							break;
					}
					if (columnIndex == 0) {
						return StudioUtil.getImage(StudioConst.ICON_NODE);
					}
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
			return null;
		}
	}

	/**
	 * SQL脚本对象内容
	 */
	private class MapperTableContentProvider implements IStructuredContentProvider {
		public void dispose() {
		}

		public Object[] getElements(Object inputElement) {
			if (inputElement instanceof XMLMapperDocument) {
				Element root = ((XMLMapperDocument)inputElement).getRootElement();
				List<Node> list = new ArrayList<Node>();
				for (int i = 0; i < root.nodeCount(); i++) {
					Node node = root.node(i);
					if (StudioUtil.isConfigNode(node)) {
						list.add(node);
					} else if (node instanceof Element) {
						list.add(node);
					}
				}
				return (list.toArray());
			} else {
				return new Object[0];
			}
		}

		public void inputChanged(Viewer viewer, Object oldInput, Object newInput) {
		}
	}

	private class ResultMapTableLabelProvider extends LabelProvider implements ITableLabelProvider {
		public Image getColumnImage(Object element, int columnIndex) {
			if (element instanceof Element) {
				if (columnIndex == 0) {
					Element node = (Element)element;
					String nodename = node.getName();
					if ("id".equals(nodename)) {
						return StudioUtil.getImage(StudioConst.ICON_PARAM_FIELD_KEY);
					} else if ("result".equals(nodename)) {
						return StudioUtil.getImage(StudioConst.ICON_RESULT_MAP_NORMAL);
					} else if ("constructor".equals(nodename)) {
						return StudioUtil.getImage(StudioConst.ICON_RESULT_MAP_1);
					} else if ("association".equals(nodename)) {
						return StudioUtil.getImage(StudioConst.ICON_RESULT_MAP_1);
					} else if ("collection".equals(nodename)) {
						return StudioUtil.getImage(StudioConst.ICON_RESULT_MAP_1);
					} else if ("discriminator".equals(nodename)) {
						return StudioUtil.getImage(StudioConst.ICON_RESULT_MAP_1);
					}
				}
			}
			return null;
		}

		public String getColumnText(Object element, int columnIndex) {
			if (element instanceof Element) {
				Element node = (Element)element;
				String nodename = node.getName();
				if ("id".equals(nodename) || "result".equals(nodename)) {
					switch (columnIndex) {
						case 0:
							return node.attributeValue("column");
						case 1:
							return node.attributeValue("property");
						case 2:
							return node.attributeValue("jdbcType");
					}
				} else if ("constructor".equals(nodename)) {
					if (columnIndex == 0) {
						return "构造函数";
					}
				} else if ("association".equals(nodename)) {
					if (columnIndex == 0) {
						return "关系映射";
					}
				} else if ("collection".equals(nodename)) {
					if (columnIndex == 0) {
						return "集合映射";
					}
				} else if ("discriminator".equals(nodename)) {
					if (columnIndex == 0) {
						return "鉴别器";
					}
				}
			}
			return null;
		}
	}

	private class ResultMapTableContentProvider implements IStructuredContentProvider {
		public void dispose() {
		}

		public Object[] getElements(Object inputElement) {
			if (inputElement instanceof List<?>) {
				return ((List<?>)inputElement).toArray();
			} else {
				return new Object[0];
			}
		}

		public void inputChanged(Viewer viewer, Object oldInput, Object newInput) {
		}
	}

	private class ParaMapTableLabelProvider extends LabelProvider implements ITableLabelProvider {
		public Image getColumnImage(Object element, int columnIndex) {
			if (element instanceof Element) {
				if (columnIndex == 0) {
					Element node = (Element)element;
					if (node.attributeValue(StudioConst.PARAM_MAP_ATTR_MODE).equals("IN")) {
						return StudioUtil.getImage(StudioConst.ICON_PARAM_IN);
					} else if (node.attributeValue(StudioConst.PARAM_MAP_ATTR_MODE).equals("OUT")) {
						return StudioUtil.getImage(StudioConst.ICON_PARAM_OUT);
					}
				}
			}
			return null;
		}

		public String getColumnText(Object element, int columnIndex) {
			if (element instanceof Element) {
				Element node = (Element)element;
				switch (columnIndex) {
					case 0:
						return node.attributeValue("mode");
					case 1:
						return node.attributeValue("property");
					case 2:
						return node.attributeValue("jdbcType");
					case 3:
						return node.attributeValue("javaType");
				}
			}
			return null;
		}
	}

	private class ParaMapTableContentProvider implements IStructuredContentProvider {
		public void dispose() {
		}

		public Object[] getElements(Object inputElement) {
			if (inputElement instanceof List<?>) {
				return ((List<?>)inputElement).toArray();
			} else {
				return new Object[0];
			}
		}

		public void inputChanged(Viewer viewer, Object oldInput, Object newInput) {
		}
	}
}
