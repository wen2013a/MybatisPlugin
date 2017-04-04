package com.newair.studioplugin.wizard;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.core.runtime.Assert;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.resource.JFaceResources;
import org.eclipse.jface.wizard.IWizardPage;
import org.eclipse.jface.wizard.Wizard;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Composite;

public abstract class DynamicPageWizard extends Wizard {
	/**
	 * 向导中的所有向导页，注意：指的是增加或者减少之后的向导页
	 */
	private List<IWizardPage> pages = new ArrayList<IWizardPage>();
	
    /**
     * The default page image for pages without one of their one;
     * <code>null</code> if none.
     */
    private Image defaultImage = null;
    
    /**
     * The default page image descriptor, used for creating a default page image
     * if required; <code>null</code> if none.
     */
    private ImageDescriptor defaultImageDescriptor = JFaceResources.getImageRegistry().getDescriptor(DEFAULT_IMAGE);

    /**
     * Indicates whether this wizard needs previous and next buttons even if the
     * wizard has only one page.
     */
    private boolean forcePreviousAndNextButtons = false;
    
	/**
	 * 构造函数，创建一个空的向导
	 */
	protected DynamicPageWizard() {
		super();
	}

	public void addPage(IWizardPage page) {
		// 重写父类方法，添加向导页，并将向导页的向导设置为当前对象
		pages.add(page);
		page.setWizard(this);
	}

	/**
	 * 在指定的向导页前插入向导页
	 * 
	 * @param page
	 * @param nextPage
	 * @return
	 */
	public boolean addPage(IWizardPage page, IWizardPage nextPage) {
		for (int i = 0; i < pages.size(); i++) {
			if (pages.get(i) == nextPage) {
				return addPage(page, i);
			}
		}
		return false;
	}

	/**
	 * 在指定的位置插入向导页
	 * 
	 * @param page
	 * @param location
	 */
	public boolean addPage(IWizardPage page, int location) {
		// Invalid location
		if (location < 0 || location > pages.size())
			return false;
		// Create the new page list
		List<IWizardPage> newPages = new ArrayList<IWizardPage>();
		for (int i = 0; i < location; i++) {
			newPages.add(pages.get(i));
		}
		page.setWizard(this);
		newPages.add(page);
		for (int i = location; i < pages.size(); i++) {
			newPages.add(pages.get(i));
		}
		// Set the relationship
		if (location != pages.size())
			((IWizardPage)newPages.get(location + 1)).setPreviousPage(page);
		((IWizardPage)page).setPreviousPage((IWizardPage)newPages.get(location - 1));
		pages = newPages;
		return true;
	}

	/**
	 * 删除指定位置的向导页
	 * 
	 * @param number
	 */
	public void removePage(int number) {
		if (number < 0)
			return;
		if (number > pages.size() - 1)
			return;
		if (number == 0)
			pages.remove(0);
		else if (number == pages.size() - 1)
			pages.remove(number);
		else {
			IWizardPage wizarPage = (IWizardPage)pages.get(number + 1);
			wizarPage.setPreviousPage((IWizardPage)pages.get(number - 1));
			pages.remove(number);
		}
	}

	/**
	 * 删除指定的向导页
	 * 
	 * @param page
	 */
	public void removePage(IWizardPage page) {
		int number = -1;
		for (int i = 0; i < pages.size(); i++) {
			if (pages.get(i) == page)
				number = i;
		}
		removePage(number);
	}

	/**
	 * 删除向导中某种类名的所有向导页
	 * 
	 * @param number
	 */
	public void removePage(String className) {
		for (int i = 0; i < pages.size(); i++) {
			if (pages.get(i).getClass().getCanonicalName().equalsIgnoreCase(className))
				removePage(i);
		}
	}

	public void addPages() {
		// 重写父类方法
	}

	public boolean canFinish() {
		// 重写父类方法，检测是否所有向导页的设置都结束
        for (int i = 0; i < pages.size(); i++) {
            if (!((IWizardPage) pages.get(i)).isPageComplete()) {
				return false;
			}
        }
        return true;
	}

	public void createPageControls(Composite pageContainer) {
		// 重写父类方法
        // the default behavior is to create all the pages controls
        for (int i = 0; i < pages.size(); i++) {
            IWizardPage page = (IWizardPage) pages.get(i);
            page.createControl(pageContainer);
            // page is responsible for ensuring the created control is
            // accessable
            // via getControl.
            Assert.isNotNull(page.getControl());
        }
	}

	public void dispose() {
		// 重写父类方法
        // notify pages
        for (int i = 0; i < pages.size(); i++) {
            ((IWizardPage) pages.get(i)).dispose();
        }
        // dispose of image
        if (defaultImage != null) {
            JFaceResources.getResources().destroyImage(defaultImageDescriptor);
            defaultImage = null;
        }
	}

	public Image getDefaultPageImage() {
		// 重写父类方法
        if (defaultImage == null) {
            defaultImage = JFaceResources.getResources().createImageWithDefault(defaultImageDescriptor);
        }
        return defaultImage;
	}

	public IWizardPage getNextPage(IWizardPage page) {
		// 重写父类方法，获取下一个向导页
        int index = pages.indexOf(page);
        if (index == pages.size() - 1 || index == -1) {
			// last page or page not found
            return null;
		}
        return (IWizardPage) pages.get(index + 1);
	}

	public IWizardPage getPage(String name) {
		// 重写父类方法，获取指定名字的向导页
        for (int i = 0; i < pages.size(); i++) {
            IWizardPage page = (IWizardPage) pages.get(i);
            String pageName = page.getName();
            if (pageName.equals(name)) {
				return page;
			}
        }
        return null;
	}

	public int getPageCount() {
		// 重写父类方法
		return pages.size();
	}

	public IWizardPage[] getPages() {
		// 重写父类方法
		return (IWizardPage[]) pages.toArray(new IWizardPage[pages.size()]);
	}

	public IWizardPage getPreviousPage(IWizardPage page) {
		// 重写父类方法，获取某个向导页之前的向导页
        int index = pages.indexOf(page);
        if (index == 0 || index == -1) {
			// first page or page not found
            return null;
		} 
		return (IWizardPage) pages.get(index - 1);
	}

	public IWizardPage getStartingPage() {
		// 重写父类方法，获取起始向导页
        if (pages.size() == 0) {
			return null;
		}
        return (IWizardPage) pages.get(0);
	}

	public boolean performCancel() {
		// 重写父类方法
		return true;
	}

	public boolean needsPreviousAndNextButtons() {
		// 重写父类方法
		return forcePreviousAndNextButtons || pages.size() > 1;
	}

	public void setForcePreviousAndNextButtons(boolean b) {
		// 重写父类方法
		forcePreviousAndNextButtons = b;
	}

	public abstract boolean performFinish();

	public void setDefaultPageImageDescriptor(ImageDescriptor imageDescriptor) {
		// 重写父类方法
		defaultImageDescriptor = imageDescriptor;
	}
}