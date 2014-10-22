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

import static org.eclipse.thym.core.internal.util.FileUtils.directoryCopy;
import static org.eclipse.thym.core.internal.util.FileUtils.fileCopy;
import static org.eclipse.thym.core.internal.util.FileUtils.toURL;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.net.URL;
import java.util.HashMap;
import java.util.Iterator;

import org.eclipse.core.runtime.Assert;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Path;
import org.eclipse.core.runtime.Status;
import org.eclipse.osgi.util.NLS;
import org.eclipse.thym.blackberry.core.BlackBerryCore;
import org.eclipse.thym.core.HybridCore;
import org.eclipse.thym.core.HybridMobileStatus;
import org.eclipse.thym.core.engine.HybridMobileLibraryResolver;
import org.eclipse.thym.core.internal.util.FileUtils;

public class BlackBerryLibraryResolver extends
		HybridMobileLibraryResolver {

//	public static final String DIR_LIBS = "libs";
	public static final String DIR_RES = "res";
//	public static final String DIR_SRC = "src";
	public static final String TEMPALTE_DIR = "templateDir";
	
//	public static final String FILE_JAR_CORDOVA = "cordova.jar";
//	private static final IPath KEY_PATH_CORDOVA_JAR = new Path(DIR_LIBS +"/" + FILE_JAR_CORDOVA);
//	public static final String FILE_XML_ANDROIDMANIFEST = "AndroidManifest.xml";

	HashMap<IPath, URL> files = new HashMap<IPath, URL>();
	
	private void initFiles() {
		Assert.isNotNull(libraryRoot, "Library resolver is not initialized. Call init before accessing any other functions.");
		if(version == null ){
			return;
		}
//		IPath templatePrjRoot = libraryRoot.append("bin/templates/project");
//		files.put(new Path("templateDir"),getEngineFile(templatePrjRoot));
//		files.put(new Path(DIR_RES),getEngineFile(templatePrjRoot.append(DIR_RES)));
//		files.put(PATH_CORDOVA_JS, getEngineFile(libraryRoot.append("framework/assets/www/cordova.js")));
//		files.put(new Path("framework/project.properties"), getEngineFile(libraryRoot.append("framework/project.properties")));
//		
//		// BlackBerry specific
//	    files.put(new Path("target"), getEngineFile(libraryRoot.append("bin").append("target")));
//	    files.put(new Path("target.bat"), getEngineFile(libraryRoot.append("bin").append("target.bat")));
//	    files.put(new Path("lib/target.js"), getEngineFile(libraryRoot.append("bin").append("lib").append("target.js")));
//	    files.put(new Path("lib/config.js"), getEngineFile(libraryRoot.append("bin").append("lib").append("config.js")));
//	    
//	    // copy repo level init script to project
//	    files.put(new Path("whereis.cmd"), getEngineFile(libraryRoot.append("bin").append("whereis.cmd")));
//	    files.put(new Path("init.bat"), getEngineFile(libraryRoot.append("bin").append("init.bat")));
//	    files.put(new Path("init"), getEngineFile(libraryRoot.append("bin").append("init")));
//
//	    //copy VERSION file [used to identify corresponding ~/.cordova/lib directory for dependencies]
//	    files.put(new Path("VERSION"), getEngineFile(libraryRoot.append("VERSION")));
//
//	    // copy repo level check_reqs script to project
//	    files.put(new Path("check_reqs.bat"), getEngineFile(libraryRoot.append("bin").append("check_reqs.bat")));
//	    files.put(new Path("chek_reqs"), getEngineFile(libraryRoot.append("bin").append("check_reqs")));
//
//	    // copy cordova.blackberry10.js
//	    File jsFile = libraryRoot.append("build").append("javascript").toFile();
//	    if (!jsFile.exists()) {
//	    	jsFile.mkdir();
//	    }
//		try {
//			fileCopy(
//					toURL(libraryRoot.append("javascript")
//							.append("cordova.blackberry10.js").toFile()),
//					getEngineFile(libraryRoot.append("build")
//							.append("javascript").append("cordova.js")));
//		} catch (IOException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}
//	    files.put(new Path("cordova.js"), getEngineFile(libraryRoot.append("buid").append("javascript").append("cordova.js")));
//	    
//	    files.put(new Path("bootStrapDir"), getEngineFile(libraryRoot.append("framework").append("bootstrap")));
//	    files.put(new Path("frameworkLibDir"), getEngineFile(libraryRoot.append("framework").append("lib")));
//	    files.put(new Path("buildDir"), getEngineFile(libraryRoot.append("bin").append("build")));
	}


	@Override
	public URL getTemplateFile(IPath destination) {
		if(files.isEmpty()) initFiles();
		Assert.isNotNull(destination);
		Assert.isTrue(!destination.isAbsolute());
		return files.get(destination);
	}

	@Override
	public IStatus isLibraryConsistent() {
		if(version == null ){
			return new Status(IStatus.ERROR, HybridCore.PLUGIN_ID, "Library for Android platform is not compatible with this tool. File for path {0} is missing.");
		}
		if(files.isEmpty()) initFiles();
		Iterator<IPath> paths = files.keySet().iterator();
		while (paths.hasNext()) {
			IPath key = paths.next();
			URL url = files.get(key);
			if(url != null  ){
				File file = new File(url.getFile());
				if( file.exists() ){
					continue;
				}
			}
			return new Status(IStatus.ERROR, HybridCore.PLUGIN_ID, NLS.bind("Library for Android platform is not compatible with this tool. File for path {0} is missing.",key.toString()));
		}
		return Status.OK_STATUS;
	}
	
	public void preCompile(IProgressMonitor monitor) throws CoreException{
//		File projectDir = libraryRoot.append("framework").toFile();
//		if(!projectDir.isDirectory()){
//			throw new CoreException(HybridMobileStatus.newMissingEngineStatus(null, "Library for the Active Hybrid Mobile Engine for Android is incomplete. No framework directory is present."));
//		}
//		BlackBerrySDK sdk = BlackBerryProjectUtils.selectBestValidTarget(this);
//		BlackBerrySDKManager sdkManager = BlackBerrySDKManager.getManager();
//		sdkManager.updateProject(sdk, null, true, projectDir,monitor);
//		BuildDelegate buildDelegate = new BuildDelegate();
//		if(monitor.isCanceled())
//			return;
//		buildDelegate.buildLibraryProject(projectDir, monitor);
	}
	
	public boolean needsPreCompilation(){
//		IPath cordovaJar = libraryRoot.append("framework").append(NLS.bind("cordova-{0}.jar",version));
//		return !cordovaJar.toFile().exists();
		return false;
	}
	
	private URL getEngineFile(IPath path){
		File file = path.toFile();
		if(!file.exists()){
			HybridCore.log(IStatus.ERROR, "missing Android engine file " + file.toString(), null );
		}
		return FileUtils.toURL(file);
	}


	@Override
	public String detectVersion() {
		File versionFile = this.libraryRoot.append("VERSION").toFile();
		if(versionFile.exists()){
			BufferedReader reader = null;
			try{
				try {
					reader = new BufferedReader(new FileReader(versionFile));
					String version = reader.readLine();
					return version;
				} 
				finally{
					if(reader != null ) reader.close();
				}
			}catch (IOException e) {
				BlackBerryCore.log(IStatus.ERROR, "Can not detect version on library", e);
			}
		}else{
			BlackBerryCore.log(IStatus.ERROR, NLS.bind("Can not detect version. VERSION file {0} is missing",versionFile.toString()), null);
		}
		return null;
	}

}
