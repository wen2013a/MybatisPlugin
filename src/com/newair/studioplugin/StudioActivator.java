package com.newair.studioplugin;

import java.io.File;
import java.io.IOException;
import java.net.URL;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.PropertyConfigurator;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.FileLocator;
import org.eclipse.jdt.core.IJavaElement;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.internal.ui.packageview.PackageFragmentRootContainer;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.swt.graphics.Image;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.ISelectionService;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.internal.Workbench;
import org.eclipse.ui.plugin.AbstractUIPlugin;
import org.osgi.framework.BundleContext;

import com.newair.studioplugin.resource.loader.StudioClassLoader;

public class StudioActivator extends AbstractUIPlugin {
	private static ClassLoader init_loader;
	private static File baseDir = null;
	private static File pkgDir = null;
	private static File configDir = null;
	private static String configFile = "";
	private static String PROJECT_TYPE = "PLUGIN";
	private static String PROJECT_TYPE_PRODUCT = "PRODUCT";
	private long lastLoadTime = System.currentTimeMillis();
	// The plug-in ID
	public static final String PLUGIN_ID = "StudioPlugin";
	// The shared instance
	private static StudioActivator plugin;
	private static String resourcePath;

	/**
	 * The constructor
	 */
	public StudioActivator() {
		init_loader = Thread.currentThread().getContextClassLoader();
		plugin = this;
		resourcePath = getResPath();
		loadLog();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.eclipse.ui.plugin.AbstractUIPlugin#start(org.osgi.framework.BundleContext
	 * )
	 */
	public void start(BundleContext context) throws Exception {
		super.start(context);
		plugin = this;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.eclipse.ui.plugin.AbstractUIPlugin#stop(org.osgi.framework.BundleContext
	 * )
	 */
	public void stop(BundleContext context) throws Exception {
		plugin = null;
		super.stop(context);
	}

	/**
	 * Returns the shared instance
	 * 
	 * @return the shared instance
	 */
	public static StudioActivator getDefault() {
		return plugin;
	}

	/**
	 * Returns an image descriptor for the image file at the given plug-in
	 * relative path
	 * 
	 * @param path
	 *            the path
	 * @return the image descriptor
	 */
	public static ImageDescriptor getImageDescriptor(String path) {
		return imageDescriptorFromPlugin(PLUGIN_ID, path);
	}

	public Image getImage(String key) {
		Image img = getImageRegistry().get(key);
		if (img == null) {
			img = imageDescriptorFromPlugin(PLUGIN_ID, key).createImage();
			getImageRegistry().put(key, img);
		}
		return img;	
	}
	
	public static String getResourcePath() {
		return resourcePath;
	}
	
	private String getResPath() {
		
		
		URL url = this.getClass().getResource("/");
		try {
			url = FileLocator.toFileURL(url);
		} catch (IOException e1) {
			e1.printStackTrace();
		}
		String path = "";
		String[] ss = url.getPath().split("/");
		for (int i = 0; i < ss.length - 1; i++) {
			if (StringUtils.isBlank(ss[i])) {
				continue;
			}
			if (StringUtils.isBlank(path)) {
				path = ss[i];
			} else {
				path = path + "/" + ss[i]; 
			}
		}
		return path + "/";
	}
	
	/**
	 * 加载日志
	 */
	private void loadLog() {
		URL confURL = getBundle().getEntry("log4j.properties");
		try {
			String logcfgfile = FileLocator.toFileURL(confURL).getFile();
			PropertyConfigurator.configure(logcfgfile);
		} catch (IOException e) {
			e.printStackTrace();
		}
		//String logcfgfile = resourcePath + "log4j.properties";
		//PropertyConfigurator.configure(logcfgfile);
	}

	/**
	 * 获取当前工程
	 */
	@SuppressWarnings("restriction")
	public static IProject getCurrentProject() {
		IProject project = null;
		IWorkbench workbench = PlatformUI.getWorkbench(); // 取得工作台
		IWorkbenchWindow window = workbench.getActiveWorkbenchWindow(); // 取得工作台窗口
		IWorkbenchPage page = window.getActivePage(); // 取得工作台页面
		IEditorPart part = page.getActiveEditor(); // 取得当前处于活动状态的编辑器窗口
		if (part != null) {
			Object object = part.getEditorInput().getAdapter(IFile.class);
			if (object != null) {
				project = ((IFile)object).getProject();
			}
		}
		if (project == null) {
			ISelectionService selectionService = Workbench.getInstance().getActiveWorkbenchWindow().getSelectionService();
			ISelection selection = selectionService.getSelection();

			if (selection instanceof IStructuredSelection) {
				Object element = ((IStructuredSelection)selection).getFirstElement();
				if (element instanceof IResource) {
					project = ((IResource)element).getProject();
				} else if (element instanceof PackageFragmentRootContainer) {
					IJavaProject jProject = ((PackageFragmentRootContainer)element).getJavaProject();
					project = jProject.getProject();
				} else if (element instanceof IJavaElement) {
					IJavaProject jProject = ((IJavaElement)element).getJavaProject();
					project = jProject.getProject();
				}
			}
		}
		return project;
	}

	public ClassLoader getInitClassLoader() {
		return init_loader;
	}

	public void updateThreadClassLoader(URL[] urls) {
		if (urls != null) {
			ClassLoader loader = new StudioClassLoader(urls, init_loader);
			Thread.currentThread().setContextClassLoader(loader);
		}
	}

	public void updateThreadClassLoader(URL[] urls, ClassLoader parentLoader) {
		if ((urls != null) && (parentLoader != null)) {
			ClassLoader loader = new StudioClassLoader(urls, parentLoader);
			Thread.currentThread().setContextClassLoader(loader);
		}
	}

	public void setBaseDir(File dir) {
		if (dir != null)
			baseDir = dir;
	}

	public File getBaseDir() {
		return baseDir;
	}

	public void setPkgDir(File dir) {
		if (dir != null)
			pkgDir = dir;
	}

	public File getPkgDir() {
		return pkgDir;
	}

	public void setConfigDir(File dir) {
		if (dir != null)
			configDir = dir;
	}

	public File getConfigDir() {
		return configDir;
	}

	public void setLoadTime(long time) {
		lastLoadTime = time;
	}

	public long getLoadTime() {
		return lastLoadTime;
	}

	public void setConfigFile(String file) {
		configFile = file;
	}

	public String getConfigFile() {
		if (configFile == null) {
			return "";
		}
		return configFile;
	}

	public void setProjectProd() {
		PROJECT_TYPE = PROJECT_TYPE_PRODUCT;
	}

	public boolean isProjectProd() {
		return PROJECT_TYPE.equals(PROJECT_TYPE_PRODUCT);
	}
}
