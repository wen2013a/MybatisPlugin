package com.newair.studioplugin.actions;

import org.eclipse.jface.action.IAction;
import org.eclipse.jface.action.IMenuManager;
import org.eclipse.ui.IActionBars;
import org.eclipse.ui.navigator.CommonActionProvider;
import org.eclipse.ui.navigator.ICommonActionExtensionSite;
import org.eclipse.ui.navigator.ICommonMenuConstants;
import org.eclipse.ui.navigator.ICommonViewerSite;
import org.eclipse.ui.navigator.ICommonViewerWorkbenchSite;

public class NavigatorActionProvider extends CommonActionProvider {
	private IAction openAction;

	public NavigatorActionProvider() {
		//
	}

	@Override
	public void init(ICommonActionExtensionSite site) {
		super.init(site);
		ICommonViewerSite check_site = site.getViewSite();
		if (check_site instanceof ICommonViewerWorkbenchSite) {
			ICommonViewerWorkbenchSite commonViewerWorkbenchSite = (ICommonViewerWorkbenchSite)check_site;
			openAction = new OpenMapFileAction(commonViewerWorkbenchSite.getPage(), commonViewerWorkbenchSite.getSelectionProvider());
		}
	}

	@Override
	public void fillActionBars(IActionBars actionBars) {
		super.fillActionBars(actionBars);
		actionBars.setGlobalActionHandler(openAction.getClass().getName(), openAction);
	}

	@Override
	public void fillContextMenu(IMenuManager menu) {
		super.fillContextMenu(menu);
		if (openAction != null && openAction.isEnabled()) {
			menu.appendToGroup(ICommonMenuConstants.GROUP_OPEN, openAction);
		}
	}
}
