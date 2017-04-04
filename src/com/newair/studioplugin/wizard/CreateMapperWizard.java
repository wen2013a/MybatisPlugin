package com.newair.studioplugin.wizard;

import java.io.BufferedWriter;
import java.io.FileWriter;

import org.apache.commons.lang.StringUtils;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.jface.wizard.IWizardPage;
import org.eclipse.jface.wizard.Wizard;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.MessageBox;

import com.newair.studioplugin.ProjectConfig;
import com.newair.studioplugin.StudioConst;
import com.newair.studioplugin.StudioUtil;
import com.newair.studioplugin.buildcode.BuildUtil;
import com.newair.studioplugin.db.DbUtil;

public class CreateMapperWizard extends Wizard {

	private IFolder folder;

	public CreateMapperWizard(IFolder folder) {
		this.folder = folder;
		setWindowTitle("新建映射");
	}

	@Override
	public void addPages() {
		String relativepath = folder.getProjectRelativePath().toString();
		CreateMapperWizardPage page = new CreateMapperWizardPage();
		page.setPath(relativepath);
		addPage(page);
	}

	@Override
	public boolean performFinish() {
		IWizardPage startPage = getStartingPage();
		if ((startPage != null) && (startPage instanceof CreateMapperWizardPage)) {
			CreateMapperWizardPage page = (CreateMapperWizardPage)startPage;
			String mapperfilename = page.getFileName();
			String namespace = page.getNameSpace();
			if (StringUtils.isBlank(mapperfilename)) {
				MessageBox mess = new MessageBox(getShell(), SWT.ICON_ERROR | SWT.CANCEL);
				mess.setMessage("文件名称不能为空！");
				mess.open();
				return false;
			}
			if (!mapperfilename.matches(StudioConst.MAPPER_FILENAME_REGEX)) {
				MessageBox mess = new MessageBox(getShell(), SWT.ICON_ERROR | SWT.CANCEL);
				mess.setMessage("文件名称不能包括特殊字符！");
				mess.open();
				return false;
			}
			// 取文件绝对路径
			ProjectConfig config = StudioUtil.getConfig();
			String datasource = config.getDataSource();
			IProject project = StudioUtil.getCurrentProject();
			IFile mapperfile = project.getFile(page.getPath() + "/" + mapperfilename);
			if (mapperfile.exists()) {
				MessageBox mess = new MessageBox(getShell(), SWT.ICON_ERROR | SWT.CANCEL);
				mess.setMessage("文件["+mapperfilename+"]已经存在！");
				mess.open();
				return false;
			}
			String interMapperClass = namespace;
			String interfilename = mapperfilename.replace(".xml", ".java");
			
			int k = interMapperClass.lastIndexOf(".");
			String Inter = interMapperClass.substring(k + 1, interMapperClass.length());
			String mapperPkg = interMapperClass.substring(0, k);
			k = mapperPkg.lastIndexOf(".");
			String PackageName = mapperPkg.substring(0, k);
			String DaoName = Inter.substring(0, Inter.length() - 6) + "Dao";
			String daoclass = PackageName+"."+ DaoName;
			String daofilename = (PackageName+"."+DaoName).replace('.', '/') + ".java";
			
			String javaSrc = config.getJavaSrc();
			if (!javaSrc.endsWith("/")) {
				javaSrc += "/";
			}
			IFile interfile = project.getFile(page.getPath() + "/" + interfilename);
			IFile daofile = project.getFile(javaSrc + daofilename);
			String existFile = "";
			if (interfile.exists()) {
				String tmpname = mapperPkg.replace('.', '/') + "/" + interfilename + ".java";
				existFile += (StringUtils.isNotBlank(existFile) ? "\n" : "") + tmpname;
			}
			if (daofile.exists()) {
				existFile += (StringUtils.isNotBlank(existFile) ? "\n" : "") + daofilename;
			}
			if (StringUtils.isNotBlank(existFile)) {
				MessageBox mess = new MessageBox(getShell(), SWT.ICON_WARNING | SWT.OK | SWT.CANCEL);
				mess.setMessage("下列文件已经存在，是否覆盖？\n" + existFile);
				if (mess.open() != SWT.OK) {
					return false;
				}
			}
			String fullmapperfilename = mapperfile.getLocation().toString();
			String fullinterfilename = interfile.getLocation().toString();
			String fulldaofilename = daofile.getLocation().toString();
			try {
				mapperfile.create(null, IResource.NONE, null);
				BufferedWriter output = new BufferedWriter(new FileWriter(fullmapperfilename));
				String xml = BuildUtil.buildGeneralQueryMapper(datasource,interMapperClass, daoclass, namespace);
				output.write(xml);
				output.close();
				mapperfile.refreshLocal(0, null);
				
				output = new BufferedWriter(new FileWriter(fullinterfilename));
				String text = BuildUtil.buildEmptyInterMapper(interMapperClass);
				output.write(text);
				output.close();
				interfile.refreshLocal(0, null);
				
				output = new BufferedWriter(new FileWriter(fulldaofilename));
				text = BuildUtil.buildEmptyDao(datasource, interMapperClass);
				output.write(text);
				output.close();
				daofile.refreshLocal(0, null);
				
				return true;
			} catch (Exception e) {
				e.printStackTrace();
				MessageBox mess = new MessageBox(getShell(), SWT.ICON_ERROR | SWT.CANCEL);
				mess.setMessage(e.getMessage());
				mess.open();
			}
		}
		return false;
	}

}
