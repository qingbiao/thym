/*******************************************************************************
 * Copyright (c) 2013, 2014 Red Hat, Inc. 
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * 	Contributors:
 * 		 Red Hat Inc. - initial API and implementation and/or initial documentation
 *******************************************************************************/
package org.eclipse.thym.blackberry.core.bdt;

import java.io.File;
import java.io.IOException;

import org.apache.commons.io.FileUtils;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.thym.blackberry.core.BlackBerryCore;
import org.eclipse.thym.core.HybridProject;
import org.eclipse.thym.core.engine.HybridMobileLibraryResolver;
import org.eclipse.thym.core.platform.AbstractPluginInstallationActionsFactory;
import org.eclipse.thym.core.platform.IPluginInstallationAction;
import org.eclipse.thym.core.platform.PlatformConstants;
import org.eclipse.thym.core.plugin.actions.CopyFileAction;
import org.eclipse.thym.core.plugin.actions.CreateFileAction;
import org.eclipse.thym.core.plugin.actions.JSModuleAction;
import org.eclipse.thym.core.plugin.actions.XMLConfigFileAction;

public class BlackBerryPluginInstallationActionsFactory extends AbstractPluginInstallationActionsFactory
{
	//same as copy only removes empty directories. 
	public static class BlackBerrySourceFileAction extends CopyFileAction{
		private File target;

		public BlackBerrySourceFileAction(File source, File target) {
			super(source, target);
			this.target = target;
		}
		
		@Override
		public void unInstall() throws CoreException {
			super.unInstall();
			
			//Remove the empty package directory structure
			File dir = findHighestEmptyParent(target);
			try {
				if( dir.isDirectory() )
					FileUtils.deleteDirectory(dir);
			} catch (IOException e) {
				throw new CoreException(new Status(IStatus.ERROR, BlackBerryCore.PLUGIN_ID, "Error deleting package name structure",e));
			}
			
		}

		private File findHighestEmptyParent(File file) {
			File parent = file.getParentFile();
			if(parent == null ){
				return file;
			}
			File[] children = parent.listFiles();
			if(children.length>1){//count the empty dir as 1
				return file;
			}
			return findHighestEmptyParent(parent);
		}
		
	}

	@Override
	public IPluginInstallationAction getSourceFileAction(String src,
			String targetDir, String framework, String pluginId, String compilerFlags) {
		File source = new File(getPluginDirectory(), src);
		File target = new File(getProjectDirectory(), targetDir);
		return new BlackBerrySourceFileAction(source, target);
	}

	@Override
	public IPluginInstallationAction getResourceFileAction(String src, String target) {
		File source = new File(getPluginDirectory(),src);
		File targetDir = new File(getProjectDirectory(),target);
		return new CopyFileAction(source, targetDir);
	}

	@Override
	public IPluginInstallationAction getHeaderFileAction(String src, String targetDir, String pluginId) {
		throw new UnsupportedOperationException("Not implemented for BlackBerry");
	}

	@Override
	public IPluginInstallationAction getAssetAction(String src, String target) {
		File source = new File(getPluginDirectory(),src);
		File targetDir = new File(BlackBerryProjectUtils.getPlatformWWWDirectory(getProjectDirectory()),target);
		return new CopyFileAction(source, targetDir);
	}

	@Override
	public IPluginInstallationAction getConfigFileAction(String target,
			String parent, String value) {
		File[] files = org.eclipse.thym.core.internal.util.FileUtils.resolveFile(getProjectDirectory(), target);
		return new XMLConfigFileAction(files[0], parent, value);
	}

	@Override
	public IPluginInstallationAction getLibFileAction(String src, String arch) {
		File source  = new File(getPluginDirectory(),src);
		File target = new File (getProjectDirectory(), "libs");
		if( !target.isDirectory()){//it should be created during project generation but just in case.
			target.mkdir();
		}
		CopyFileAction action = new CopyFileAction(source, target);
		return action;
	}

	@Override
	public IPluginInstallationAction getFrameworkAction(String src, String weak,String pluginId, String custom, String type, String parent) {
		if(src == null ){
			throw new IllegalArgumentException("src not specified in framework element");
		}
		HybridProject hybridProject = HybridProject.getHybridProject(getProject());
		HybridMobileLibraryResolver resolver = hybridProject.getActiveEngine().getPlatformLib("blackberry").getPlatformLibraryResolver();
		BlackBerrySDK sdk=null;
		try {
			sdk = BlackBerryProjectUtils.selectBestValidTarget(resolver);
		} catch (CoreException e) {
			BlackBerryCore.log(IStatus.ERROR, "Framework action fails to select a target", e);
		}
		return new BlackBerryFrameworkAction(src, custom, parent, pluginId,getProjectDirectory(),getPluginDirectory(),sdk);
	}

	@Override
	public IPluginInstallationAction getJSModuleAction(String src,
			String pluginId, String jsModuleName) {
		File source = new File(getPluginDirectory(), src);
		File target = new File(BlackBerryProjectUtils.getPlatformWWWDirectory(getProjectDirectory()), 
				PlatformConstants.DIR_PLUGINS+File.separator+pluginId+File.separator+src );

		JSModuleAction action  = new JSModuleAction(source, target, jsModuleName);
		return action;
	}

	@Override
	public IPluginInstallationAction getCreatePluginJSAction(String content) {
		File pluginJs = new File(BlackBerryProjectUtils.getPlatformWWWDirectory(getProjectDirectory()), PlatformConstants.FILE_JS_CORDOVA_PLUGIN);
		CreateFileAction action = new CreateFileAction(content, pluginJs);
		return action;
	}
}
