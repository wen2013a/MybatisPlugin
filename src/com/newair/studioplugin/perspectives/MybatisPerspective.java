/*******************************************************************************
 * Copyright (c) 2006 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *******************************************************************************/
package com.newair.studioplugin.perspectives;

import org.eclipse.ui.IFolderLayout;
import org.eclipse.ui.IPageLayout;
import org.eclipse.ui.IPerspectiveFactory;
import org.eclipse.ui.actions.ActionFactory;
import org.eclipse.ui.console.IConsoleConstants;
import org.eclipse.jdt.ui.JavaUI;

import com.newair.studioplugin.StudioConst;

/**
 * This class is meant to serve as an example for how various contributions are
 * made to a perspective. Note that some of the extension point id's are
 * referred to as API constants while others are hardcoded and may be subject to
 * change.
 */
public class MybatisPerspective implements IPerspectiveFactory {

	private IPageLayout factory;

	public MybatisPerspective() {
		super();
	}

	public void createInitialLayout(IPageLayout factory) {
		this.factory = factory;
		addViews();
		addNewWizardShortcuts();
		addPerspectiveShortcuts();
		addViewShortcuts();
	}

	private void addViews() {
		IFolderLayout topLeft = factory.createFolder("topLeft", IPageLayout.LEFT, 0.25f, factory.getEditorArea());
		topLeft.addView(IPageLayout.ID_PROJECT_EXPLORER);
		topLeft.addView(StudioConst.NAVIGATOR_VIEW);
		topLeft.addView(StudioConst.MYBATIS_VIEW);
		/*
		IFolderLayout topRight = factory.createFolder("topRight", IPageLayout.RIGHT, 0.25f, factory.getEditorArea());
		topRight.addView("com.newair.mybatisplugin.views.MybatisView");
		
		IFolderLayout bottom = factory.createFolder("bottomRight", IPageLayout.BOTTOM, 0.75f, factory.getEditorArea());
		bottom.addView(IPageLayout.ID_PROBLEM_VIEW);
		bottom.addView("org.eclipse.team.ui.GenericHistoryView");
		bottom.addPlaceholder(IConsoleConstants.ID_CONSOLE_VIEW);

		factory.addFastView("org.eclipse.team.ccvs.ui.RepositoriesView", 0.50f);
		factory.addFastView("org.eclipse.team.sync.views.SynchronizeView", 0.50f);
		*/
	}

	private void addPerspectiveShortcuts() {
		factory.addPerspectiveShortcut("org.eclipse.team.ui.TeamSynchronizingPerspective");
		factory.addPerspectiveShortcut("org.eclipse.team.cvs.ui.cvsPerspective");
		factory.addPerspectiveShortcut("org.eclipse.ui.resourcePerspective");
	}

	private void addNewWizardShortcuts() {
		factory.addNewWizardShortcut("org.eclipse.team.cvs.ui.newProjectCheckout");
		factory.addNewWizardShortcut("org.eclipse.ui.wizards.new.folder");
		factory.addNewWizardShortcut("org.eclipse.ui.wizards.new.file");
		factory.addActionSet(ActionFactory.DELETE.getId());
	}

	private void addViewShortcuts() {
		factory.addShowViewShortcut("org.eclipse.ant.ui.views.AntView");
		factory.addShowViewShortcut("org.eclipse.team.ccvs.ui.AnnotateView");
		factory.addShowViewShortcut("org.eclipse.pde.ui.DependenciesView");
		factory.addShowViewShortcut("org.eclipse.jdt.junit.ResultView");
		factory.addShowViewShortcut("org.eclipse.team.ui.GenericHistoryView");
		factory.addShowViewShortcut(IConsoleConstants.ID_CONSOLE_VIEW);
		factory.addShowViewShortcut(JavaUI.ID_PACKAGES);
		//factory.addShowViewShortcut(IPageLayout.ID_RES_NAV);
		factory.addShowViewShortcut(IPageLayout.ID_PROBLEM_VIEW);
		factory.addShowViewShortcut(IPageLayout.ID_OUTLINE);
	}
}
