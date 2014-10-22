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

import org.eclipse.ant.launching.IAntLaunchConstants;
import org.eclipse.core.externaltools.internal.IExternalToolConstants;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.SubProgressMonitor;
import org.eclipse.debug.core.DebugPlugin;
import org.eclipse.debug.core.ILaunchConfiguration;
import org.eclipse.debug.core.ILaunchConfigurationType;
import org.eclipse.debug.core.ILaunchConfigurationWorkingCopy;
import org.eclipse.debug.core.ILaunchManager;
import org.eclipse.debug.core.IStreamListener;
import org.eclipse.debug.core.model.IStreamMonitor;
import org.eclipse.thym.blackberry.core.BlackBerryConstants;
import org.eclipse.thym.blackberry.core.BlackBerryCore;
import org.eclipse.thym.core.HybridProject;
import org.eclipse.thym.core.internal.util.ExternalProcessUtility;
import org.eclipse.thym.core.platform.AbstractNativeBinaryBuildDelegate;

public class BuildDelegate extends AbstractNativeBinaryBuildDelegate {

	private File binaryDirectory;
	public BuildDelegate() {
	}

	@Override
	public void buildNow(IProgressMonitor monitor) throws CoreException {
		if(monitor.isCanceled())
			return;
		
		//TODO: use extension point to create
		// the generator.
//		BlackBerryProjectGenerator creator = new BlackBerryProjectGenerator(this.getProject(), getDestination(),"blackberry"); 
//           		
//		SubProgressMonitor generateMonitor = new SubProgressMonitor(monitor, 1);
//		File projectDirectory = creator.generateNow(generateMonitor);
//		monitor.worked(1);
//		if(monitor.isCanceled() ){
//			return;
//		}
		
//		File platformDir = getProject().getLocation().append("platforms").toFile();
//		if (!platformDir.exists()) {
//			boolean success = platformDir.mkdir();
//			if (!success) {
//				BlackBerryCore.log(IStatus.ERROR, "Failed to create platforms directory.", null);
//				return;
//			}
//		}
		
//		File blackberryDir = getProject().getLocation().append("platforms").append("blackberry10").toFile();
//		if (!blackberryDir.exists()) {
//			boolean success = blackberryDir.mkdir();
//			if (!success) {
//				BlackBerryCore.log(IStatus.ERROR, "Failed to create platforms/blackberry10 directory.", null);
//				return;
//			}
//		}
		
		File projectDirectory = getProject().getLocation().toFile();
		buildProject(projectDirectory, monitor);
		monitor.done();
	}
	
	public void buildProject(File projectLocation,IProgressMonitor monitor) throws CoreException{
//		doBuildProject(projectLocation, false, monitor);
		doGruntBuildProject(projectLocation, false, monitor);
	}
	
	public void buildLibraryProject(File projectLocation,IProgressMonitor monitor) throws CoreException{
		doBuildProject(projectLocation, true, monitor);
	}

	private void doGruntBuildProject(File projectLocation, boolean isLibrary, IProgressMonitor monitor) throws CoreException{
		ExternalProcessUtility processUtility = new ExternalProcessUtility();
		StringBuilder command = new StringBuilder();
		command.append("/Users/dmeng/node-v0.10.32-darwin-x64/lib/node_modules/grunt-cli/bin/grunt build:blackberry10");
		String[] env = {"PATH=/Applications/Momentics.app/host_10_3_1_5/darwin/x86/usr/bin:/Users/dmeng/node-v0.10.32-darwin-x64/bin:/usr/local/bin:/usr/bin:/bin:/usr/sbin:/sbin", "HOME=/Users/dmeng"};

		GruntResultParser parser = new GruntResultParser();
		System.out.println("---->command:" + command.toString());
		System.out.println("---->working dir:" + projectLocation);
		try {
			processUtility.execSync(command.toString(), projectLocation, parser, parser, monitor, env, null);
		} catch (CoreException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		String result = parser.getResult();
		System.out.println("---->Grunt Result:" + result);
	}

	private static class GruntResultParser implements IStreamListener{
		private StringBuffer buffer = new StringBuffer();
		
		@Override
		public void streamAppended(String text, IStreamMonitor monitor) {
			buffer.append(text);
			
		}
		
		public String getResult() {
			return buffer.toString();
		}
		
		/**
		 * Returns an error string or null if it is OK 				
		 * @return
		 */
		public String getErrorString(){
			return null;
		}
	}
	
	private void doBuildProject(File projectLocation, boolean isLibrary, IProgressMonitor monitor) throws CoreException{
		ILaunchManager launchManager = DebugPlugin.getDefault().getLaunchManager();
		ILaunchConfigurationType antLaunchConfigType = launchManager.getLaunchConfigurationType(IAntLaunchConstants.ID_ANT_LAUNCH_CONFIGURATION_TYPE);
		if(antLaunchConfigType == null ){
			throw new CoreException(new Status(IStatus.ERROR, BlackBerryCore.PLUGIN_ID, "Ant launch configuration type is not available"));
		}
		ILaunchConfigurationWorkingCopy wc = antLaunchConfigType.newInstance(null, "Android project builder"); //$NON-NLS-1$
		wc.setContainer(null);
		File buildFile = new File(projectLocation, BlackBerryConstants.FILE_XML_BUILD);
		if(!buildFile.exists()){
			throw new CoreException(new Status(IStatus.ERROR, BlackBerryCore.PLUGIN_ID, "build.xml does not exist in "+ projectLocation.getPath()));
		}
		wc.setAttribute(IExternalToolConstants.ATTR_LOCATION, buildFile.getPath());
		String target = null;
		if(isLibrary){
			target = "jar";
		}else{
			target = "debug";
			if(isRelease()){
				target = "release";
			}
		}
		wc.setAttribute(IAntLaunchConstants.ATTR_ANT_TARGETS, target);
		wc.setAttribute(IAntLaunchConstants.ATTR_DEFAULT_VM_INSTALL, true);

		wc.setAttribute(IExternalToolConstants.ATTR_LAUNCH_IN_BACKGROUND, false);
		wc.setAttribute(DebugPlugin.ATTR_CAPTURE_OUTPUT, true);
		ILaunchConfiguration launchConfig = wc.doSave();
        if (monitor.isCanceled()){
        	return;
        }
		
        launchConfig.launch(ILaunchManager.RUN_MODE, monitor, true, true);
        
        binaryDirectory = new File(projectLocation, BlackBerryConstants.DIR_BIN);
        if(isLibrary){
        	//no checks for libs
        }else{
        	HybridProject hybridProject = HybridProject.getHybridProject(getProject());
        	if(isRelease()){
        		setBuildArtifact(new File(binaryDirectory,hybridProject.getBuildArtifactAppName()+"-release-unsigned.apk" ));
        	}else{
        		setBuildArtifact(new File(binaryDirectory,hybridProject.getBuildArtifactAppName()+"-debug.apk" ));
        	}
        	if(!getBuildArtifact().exists()){
        		throw new CoreException(new Status(IStatus.ERROR, BlackBerryCore.PLUGIN_ID, "Build failed... Build artifact does not exist"));
        	}
        }
	}
	

	/**
	 * Returns the directory where build artifacts are stored. 
	 * Will return null if the build is not yet complete or 
	 * {@link #buildNow(IProgressMonitor)} is not called yet for this instance.
	 * @return
	 */
	public File getBinaryDirectory() {
		return binaryDirectory;
	}

}
