package com.newair.studioplugin.wizard;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.IProject;
import org.eclipse.jface.wizard.IWizardPage;
import org.eclipse.jface.wizard.Wizard;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.MessageBox;

import com.newair.studioplugin.ConfigCDATA;
import com.newair.studioplugin.ProjectConfig;
import com.newair.studioplugin.StudioUtil;
import com.newair.studioplugin.buildcode.BuildUtil;
import com.newair.studioplugin.db.DbUtil;
import com.newair.studioplugin.db.Field;

public class CreateTableMapperWizard extends Wizard {
	private static final Logger log = Logger.getLogger(CreateTableMapperWizard.class);
	
	private IFolder folder;
	private SelectTableWizardPage selectTablePage;
	private SelectColumnWizardPage selectColumnPage;
	private SelectSqlWizardPage selectSqlPage;

	public CreateTableMapperWizard(IFolder folder) {
		this.folder = folder;
		setWindowTitle("新建映射文件向导");
	}

	@Override
	public void addPages() {
		selectTablePage = new SelectTableWizardPage();
		selectColumnPage = new SelectColumnWizardPage();
		selectSqlPage = new SelectSqlWizardPage();
		this.addPage(selectTablePage);
		this.addPage(selectColumnPage);
		this.addPage(selectSqlPage);
		selectColumnPage.setFolder(folder);
	}

	@Override
	public boolean needsPreviousAndNextButtons() {
		return true;
	}

	@Override
	public boolean canFinish() {
		IWizardPage[] pages = getPages();
		boolean flag = true;
		for (int i = 0; i < pages.length; i++) {
			if (!pages[i].isPageComplete()) {
				flag = false;
				break;
			}
		}
		return flag;
	}

	@Override
	public boolean performFinish() {
		ProjectConfig config = StudioUtil.getConfig();
		IProject project = StudioUtil.getCurrentProject();                     //当前工程
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
		IFolder daofolder = (IFolder)folder.getParent();                       // Dao目录
		String mapperpath = folder.getProjectRelativePath().toString();        // 当前mapper目录相对路径
		String srcdaopath = daofolder.getProjectRelativePath().toString();     // 当前dao目录相对路径
		if (srcdaopath.startsWith(mapperRoot)) {
			srcdaopath = srcdaopath.replaceFirst(mapperRoot, javaSrc);
		}
		String tablename = selectTablePage.getTableName();                     // 表名称
		String datasource = config.getDataSource();                            // 当前数据源名称
		String daoname = DbUtil.getTableBeanName(tablename) + "Dao";           // Dao类名称
		String daopackagename = srcdaopath.replaceAll(javaSrc, "").replaceAll("/", "."); // Dao包名称
		String beanclass = selectColumnPage.getBeanClass();                    // Bean类全名
		String blobbeanclass = beanclass + "WithBlob";                         // BlobBean类全名
		String keybean = DbUtil.getTableBeanName(tablename) + "Key";
		String keybeanclass = beanclass + "Key"; // KeyBean类全名
		String adapterclass = daopackagename + "." + config.getModelDir() + "." + DbUtil.getTableBeanName(tablename) + "Adapter"; // 适配器类名称
		String intermapperclass = daopackagename + "." + config.getInterDir() + "." + DbUtil.getTableBeanName(tablename) + "Mapper"; // Inter接口全名
		String mapperNamespace = intermapperclass;                                   // 映射文件命名空间(namespace)
		String daoclass = daopackagename + "." + daoname;
		
		String mapperfilename = DbUtil.getTableBeanName(tablename) + "Mapper.xml"; // Mapper映射文件名称
		String adapterfilename = adapterclass.substring(adapterclass.lastIndexOf(".") + 1, adapterclass.length()) + ".java";
		String beanfilename = beanclass.substring(beanclass.lastIndexOf(".") + 1, beanclass.length()) + ".java";
		String blobbeanfilename = blobbeanclass.substring(blobbeanclass.lastIndexOf(".") + 1, blobbeanclass.length()) + ".java";
		String keybeanfilename = keybeanclass.substring(keybeanclass.lastIndexOf(".") + 1, keybeanclass.length()) + ".java";
		String intermapperfilename = intermapperclass.substring(intermapperclass.lastIndexOf(".") + 1, intermapperclass.length()) + ".java";
		String daofilename = daoclass.substring(daoclass.lastIndexOf(".") + 1, daoclass.length()) + ".java";

		IFile mapperfile = project.getFile(mapperpath + "/" + mapperfilename);
		IFile adapterfile = project.getFile(srcdaopath + "/" + config.getModelDir() + "/" + adapterfilename);
		IFile intermapperfile = project.getFile(srcdaopath + "/" + config.getInterDir() + "/" + intermapperfilename);
		IFile daofile = project.getFile(srcdaopath + "/" + daofilename);
		IFile beanfile = intfproject.getFile(intfJavaSrc + beanclass.replace('.', '/') + ".java");
		IFile blobbeanfile = intfproject.getFile(intfJavaSrc + blobbeanclass.replace('.', '/') + ".java");
		IFile keybeanfile = intfproject.getFile(intfJavaSrc + keybeanclass.replace('.', '/') + ".java");
		
		String fullMapperFilename = mapperfile.getLocation().toString();         // 映射文件全名
		String fullAdapterfile = adapterfile.getLocation().toString();           //适配器文件全名
		String fullInterMapperfile = intermapperfile.getLocation().toString();   //接口类文件全名
		String fullDaofile = daofile.getLocation().toString();                   //接口类文件全名
		String fullBeanfile = beanfile.getLocation().toString();                 //Bean类文件全名
		String fullBlobBeanfile = blobbeanfile.getLocation().toString();         //BlobBean类文件全名
		String fullKeyBeanfile = keybeanfile.getLocation().toString();           //KeyBean类文件全名
		
		// 获取主键,基本字段,大数据字段
		Field[] fields = selectColumnPage.getFields();
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
		//boolean isSelectAllField = selectColumnPage.isSelectAllField();
		boolean isBuildDelete = selectSqlPage.isBuildDelete();
		boolean isBuildInsert = selectSqlPage.isBuildInsert();
		boolean isBuildSelect = selectSqlPage.isBuildSelect();
		boolean isBuildUpdate = selectSqlPage.isBuildUpdate();
		boolean isBuildJava = selectSqlPage.isBuildJava();
		boolean[] buildJavaCfg = new boolean[] { isBuildSelect, isBuildInsert, isBuildUpdate, isBuildDelete };

		boolean isKeys = keys.size() > 1;
		boolean isBlob = blobfields.length > 0;
		try {
			ConfigCDATA configCDATA = new ConfigCDATA(datasource, tablename, beanclass, intermapperclass, daoclass, identitycolumn);
			String s_UpdateByPrimaryKeyWithBlob = null;
			String s_ConfigCDATA = BuildUtil.buildConfigCDATA(configCDATA);
			String s_BaseResultMap = BuildUtil.buildBaseResultMap(basefields, beanclass);
			String s_BlobResultMap = BuildUtil.buildResultMapWithBlob(blobfields, blobbeanclass);
			String s_BaseColumnList = BuildUtil.buildBaseColumnList(basefields);
			String s_BlobColumnList = BuildUtil.buildBlobColumnList(blobfields);
			
			String s_AdapterUpdateWhereCondition = BuildUtil.buildAdapterUpdateWhereCondition();
			String s_AdapterWhereCondition = BuildUtil.buildAdapterWhereCondition();
			String s_CountByAdapter = BuildUtil.buildCountByAdapter(tablename, adapterclass);
			String s_DeleteByAdapter = BuildUtil.buildDeleteByAdapter(tablename, adapterclass);
			String s_DeleteByPrimaryKey = BuildUtil.buildDeleteByPrimaryKey(tablename, primaryKeys, keybeanclass);
			String s_Insert = BuildUtil.buildInsert(tablename, (isBlob ? blobbeanclass : beanclass), fields);
			String s_InsertSelective = BuildUtil.buildInsertSelective(tablename, (isBlob ? blobbeanclass : beanclass), fields);
			String s_SelectByAdapter = BuildUtil.buildSelectByAdapter(tablename, adapterclass);
			String s_SelectByAdapterWithBlob = (!isBlob) ? "" : BuildUtil.buildSelectByAdapterWithBlob(tablename, adapterclass);
			String s_SelectByPrimaryKey = BuildUtil.buildSelectByPrimaryKey(tablename, primaryKeys, keybeanclass, isBlob);
			String s_UpdateByAdapter = BuildUtil.buildUpdateByAdapter(tablename, basefields);
			String s_UpdateByAdapterWithBlob = (!isBlob) ? "" : BuildUtil.buildUpdateByAdapterWithBlob(tablename, fields);
			String s_UpdateByAdapterSelective = BuildUtil.buildUpdateByAdapterSelective(tablename, fields);
			String s_UpdateByPrimaryKey = BuildUtil.buildUpdateByPrimaryKey(tablename, basefields, primaryKeys, beanclass);
			if (isBlob) {
				s_UpdateByPrimaryKeyWithBlob = BuildUtil.buildUpdateByPrimaryKeyWithBlob(tablename, fields, primaryKeys, blobbeanclass);
			}
			String s_UpdateByPrimaryKeySelective = BuildUtil.buildUpdateByPrimaryKeySelective(tablename, fields, primaryKeys, (isBlob ? blobbeanclass : beanclass));

			Map<String, Object> root = new HashMap<String, Object>();
			root.put("Namespace", mapperNamespace);
			root.put("ConfigCDATA", s_ConfigCDATA);
			root.put("BaseResultMap", s_BaseResultMap);
			root.put("BlobResultMap", s_BlobResultMap);
			root.put("BaseColumnList", s_BaseColumnList);
			root.put("BlobColumnList", s_BlobColumnList);
			root.put("AdapterWhereCondition", s_AdapterWhereCondition);
			root.put("AdapterUpdateWhereCondition", s_AdapterUpdateWhereCondition);
			if (isBuildSelect) {
				if (blobfields.length > 0) {
					root.put("selectByAdapterWithBlob", s_SelectByAdapterWithBlob);
				}
				root.put("selectByAdapter", s_SelectByAdapter);
				root.put("selectByPrimaryKey", s_SelectByPrimaryKey);
				root.put("countByAdapter", s_CountByAdapter);
			}
			if (isBuildDelete) {
				root.put("deleteByAdapter", s_DeleteByAdapter);
				root.put("deleteByPrimaryKey", s_DeleteByPrimaryKey);
			}
			if (isBuildInsert) {
				root.put("insert", s_Insert);
				root.put("insertSelective", s_InsertSelective);
			}
			if (isBuildUpdate) {
				root.put("updateByAdapter", s_UpdateByAdapter);
				root.put("updateByAdapterWithBlob", s_UpdateByAdapterWithBlob);
				root.put("updateByAdapterSelective", s_UpdateByAdapterSelective);
				root.put("updateByPrimaryKey", s_UpdateByPrimaryKey);
				root.put("updateByPrimaryKeyWithBlob", s_UpdateByPrimaryKeyWithBlob);
				root.put("updateByPrimaryKeySelective", s_UpdateByPrimaryKeySelective);
			}
			String xml = BuildUtil.buildTableMapper(root);
			// 映射文件保存
			BufferedWriter output;
			if (mapperfile.exists() || daofile.exists() || intermapperfile.exists()) {
				StringBuffer sb = new StringBuffer();
				sb.append("下列源文件已经存在，是否覆盖？选择是则覆盖原来的代码！\n");
				if (mapperfile.exists()) {
					sb.append("映射文件：").append(mapperfilename).append("\n");
				}
				if (isBuildJava && intermapperfile.exists()) {
					sb.append("接口文件：").append(intermapperfilename).append("\n");
				}
				if (isBuildJava && adapterfile.exists()) {
					sb.append("Adapter文件：").append(adapterfilename).append("\n");
				}
				if (isBuildJava && beanfile.exists()) {
					sb.append("Bean文件：").append(beanfilename).append("\n");
				}
				if (isBuildJava && isBlob && blobbeanfile.exists()) {
					sb.append("BlobBean文件：").append(blobbeanfilename).append("\n");
				}
				if (isBuildJava && isBlob && keybeanfile.exists()) {
					sb.append("KeyBean文件：").append(keybeanfilename).append("\n");
				}
				if (isBuildJava && daofile.exists()) {
					sb.append("DAO文件：").append(daofilename).append("\n");
				}
				MessageBox messageBox = new MessageBox(getShell(), SWT.ICON_WARNING | SWT.YES | SWT.NO);
				messageBox.setMessage(sb.toString());
				int rc = messageBox.open();
				if (rc != SWT.YES) {
					return true;
				}
			}
			
			StudioUtil.createResourceFile(mapperfile);
			output = new BufferedWriter(new FileWriter(fullMapperFilename));
			output.write(xml);
			output.close();
			mapperfile.refreshLocal(0, null);
			// 创建Java代码
			if (isBuildJava) {
				String s_Key = null, s_BlobBean = null;
				if (isKeys) {
					s_Key = BuildUtil.buildBean(keybeanclass, primaryKeys); 
				}
				if (isBlob) {
					s_BlobBean = BuildUtil.buildBlobBean(blobbeanclass, beanclass, blobfields); 
				}
				String s_Bean = BuildUtil.buildBean(beanclass, basefields);
				String s_MapperInter = BuildUtil.buildMapperInter(beanclass, intermapperclass, adapterclass, keybeanclass, blobbeanclass, primaryKeys, buildJavaCfg, isBlob);
				String s_Adapter = BuildUtil.buildAdapter(adapterclass, basefields);
				String s_Dao = BuildUtil.buildMapperDao(datasource, beanclass, intermapperclass, adapterclass, keybeanclass, blobbeanclass, daoclass, primaryKeys, buildJavaCfg, isBlob);

				StudioUtil.createResourceFile(beanfile);
				output = new BufferedWriter(new FileWriter(fullBeanfile));
				output.write(s_Bean);
				output.close();
				beanfile.refreshLocal(0, null);
				
				StudioUtil.createResourceFile(intermapperfile);
				output = new BufferedWriter(new FileWriter(fullInterMapperfile));
				output.write(s_MapperInter);
				output.close();
				intermapperfile.refreshLocal(0, null);
				
				StudioUtil.createResourceFile(adapterfile);
				output = new BufferedWriter(new FileWriter(fullAdapterfile));
				output.write(s_Adapter);
				output.close();
				adapterfile.refreshLocal(0, null);
				
				if (StringUtils.isNotEmpty(s_Key)) {
					StudioUtil.createResourceFile(keybeanfile);
					output = new BufferedWriter(new FileWriter(fullKeyBeanfile));
					output.write(s_Key);
					output.close();
					keybeanfile.refreshLocal(0, null);
				}
				if (StringUtils.isNotEmpty(s_BlobBean)) {
					StudioUtil.createResourceFile(blobbeanfile);
					output = new BufferedWriter(new FileWriter(fullBlobBeanfile));
					output.write(s_BlobBean);
					output.close();
					blobbeanfile.refreshLocal(0, null);
				}
				if (StringUtils.isNotEmpty(s_Dao)) {
					StudioUtil.createResourceFile(daofile);
					output = new BufferedWriter(new FileWriter(fullDaofile));
					output.write(s_Dao);
					output.close();
					daofile.refreshLocal(0, null);
				}
			}
			return true;
		} catch (Exception e) {
			log.error(e.getMessage(), e);
			MessageBox messageBox = new MessageBox(getShell(), SWT.ICON_WARNING | SWT.YES);
			messageBox.setMessage(e.getMessage());
		}
		return false;
	}

}
