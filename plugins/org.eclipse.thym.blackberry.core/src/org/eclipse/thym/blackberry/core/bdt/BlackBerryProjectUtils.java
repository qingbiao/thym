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

import static org.eclipse.thym.blackberry.core.BlackBerryConstants.DIR_ASSETS;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.List;
import java.util.Properties;

import org.eclipse.core.runtime.Assert;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Path;
import org.eclipse.core.runtime.Status;
import org.eclipse.osgi.util.NLS;
import org.eclipse.thym.blackberry.core.BlackBerryCore;
import org.eclipse.thym.core.engine.HybridMobileLibraryResolver;
import org.eclipse.thym.core.platform.PlatformConstants;

public class BlackBerryProjectUtils {
	
	public static File getPlatformWWWDirectory(File projectDirectory) {
		Assert.isNotNull(projectDirectory);
		return new File(projectDirectory, DIR_ASSETS + File.separator +PlatformConstants.DIR_WWW);
	}
	
	/**
	 * Returns the most suitable target defined on the system to be used with the projects.
	 * It returns the highest api level target that is better than minimum requirement.
	 * 
	 * @return android SDK 
 	 * @throws CoreException
 	 * 	<ul>
 	 * 		<li>If there are no targets defined</li>
 	 * 		<li>If no target has a higher than or equal to minimum required API level.</li>
 	 * ,</ul>
	 */
	public static BlackBerrySDK selectBestValidTarget(HybridMobileLibraryResolver resolver) throws CoreException {
		
		String file = resolver.getTemplateFile(new Path("framework/project.properties")).getFile();
		if(file == null){
			throw new CoreException(new Status(IStatus.ERROR, BlackBerryCore.PLUGIN_ID,
					"Active Cordova engine does not have a project.properties file"));
		}
		File projProps = new File(file);

		try {
			FileReader reader = new FileReader(projProps);
			Properties props = new Properties();
			props.load(reader);
			String targetValue = props.getProperty("target");
			int splitIndex = targetValue.indexOf('-');
			if(targetValue != null && splitIndex >-1){
				BlackBerryAPILevelComparator alc = new BlackBerryAPILevelComparator();
				targetValue = targetValue.substring(splitIndex+1);
				BlackBerrySDKManager sdkManager = BlackBerrySDKManager.getManager();
				List<BlackBerrySDK> targets = sdkManager.listTargets();
				for (BlackBerrySDK androidSDK : targets) {
					if(alc.compare(targetValue, androidSDK.getApiLevel())==0){
						return androidSDK;
					}
				}
				// if we are here we failed to find a target.
				throw new CoreException(new Status(IStatus.ERROR, BlackBerryCore.PLUGIN_ID, 
						NLS.bind("Please install Android API level {0}. Use the Android SDK Manager to install or upgrade any missing SDKs to tools."
								,targetValue)));

			}
		} catch (FileNotFoundException e) {
			BlackBerryCore.log(IStatus.WARNING, "Missing project.properties for library", e);
		} catch (IOException e) {
			BlackBerryCore.log(IStatus.WARNING, "Failed to read target API level from library", e);
		}
		// We could not determine a targetValue	
		throw new CoreException(new Status(IStatus.ERROR, BlackBerryCore.PLUGIN_ID,
				"Could not determine required Android level for the Cordova engine, please use a different one"));
	}

}
