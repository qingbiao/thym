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
import java.util.List;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.Assert;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.debug.core.ILaunch;
import org.eclipse.debug.core.ILaunchConfiguration;
import org.eclipse.debug.core.model.ILaunchConfigurationDelegate2;
import org.eclipse.osgi.util.NLS;
import org.eclipse.thym.blackberry.core.BlackBerryConstants;
import org.eclipse.thym.blackberry.core.BlackBerryCore;
import org.eclipse.thym.core.HybridMobileStatus;
import org.eclipse.thym.core.HybridProject;
import org.eclipse.thym.core.HybridProjectLaunchConfigConstants;
import org.eclipse.thym.core.config.Widget;
import org.eclipse.thym.core.config.WidgetModel;

public class BlackBerryLaunchDelegate implements ILaunchConfigurationDelegate2 {

	private File artifactsDir;
	private BlackBerryDevice device;
	
	@Override
	public void launch(ILaunchConfiguration configuration, String mode,
			ILaunch launch, IProgressMonitor monitor) throws CoreException {
//		if(device == null ){
//			throw new CoreException(new Status(IStatus.ERROR, BlackBerryCore.PLUGIN_ID, 
//					"Failed to connect with the device or emulator. We will attempt to reconnect, please try running your application again."));
//		}
//		BlackBerrySDKManager sdk = BlackBerrySDKManager.getManager();
//	
//		HybridProject project = HybridProject.getHybridProject(getProject(configuration));
//		WidgetModel model = WidgetModel.getModel(project);
//		Widget widget = model.getWidgetForRead();
//		String packageName = widget.getId();
//		String name = project.getBuildArtifactAppName();
//		
//		sdk.installApk(new File(artifactsDir,name+"-debug.apk" ), device.getSerialNumber(),monitor);
//		
//		sdk.startApp(packageName+"/."+name, device.getSerialNumber(),monitor);
//		String logcatFilter = configuration.getAttribute(BlackBerryLaunchConstants.ATTR_LOGCAT_FILTER, BlackBerryLaunchConstants.VAL_DEFAULT_LOGCAT_FILTER);
//		sdk.logcat(logcatFilter,null,null, device.getSerialNumber());

		System.err.println("=====>Launching....");
	}

	@Override
	public ILaunch getLaunch(ILaunchConfiguration configuration, String mode)
			throws CoreException {
		return null;
	}

	@Override
	public boolean buildForLaunch(ILaunchConfiguration configuration,
			String mode, IProgressMonitor monitor) throws CoreException {
		if(monitor.isCanceled() ){
			return false;
		}
		BuildDelegate buildDelegate = new BuildDelegate();
		buildDelegate.init(getProject(configuration), null);
		buildDelegate.buildNow(monitor);
		artifactsDir = buildDelegate.getBinaryDirectory();
		return true;
	}

	@Override
	public boolean finalLaunchCheck(ILaunchConfiguration configuration,
			String mode, IProgressMonitor monitor) throws CoreException {
		monitor.done();
		return true;
	}

	@Override
	public boolean preLaunchCheck(ILaunchConfiguration configuration,
			String mode, IProgressMonitor monitor) throws CoreException {
		// Start ADB Server
//		boolean runOnDevice = configuration.getAttribute(BlackBerryLaunchConstants.ATTR_IS_DEVICE_LAUNCH, false);
//		BlackBerrySDKManager sdk = BlackBerrySDKManager.getManager();
//		if(!runOnDevice){
//			sdk.killADBServer();
//		}
//		sdk.startADBServer();
//		
//		if(runOnDevice){
//			String  serial = configuration.getAttribute(BlackBerryLaunchConstants.ATTR_DEVICE_SERIAL, (String)null);
//			Assert.isNotNull(serial);
//			List<BlackBerryDevice> devices = sdk.listDevices();
//			for (BlackBerryDevice androidDevice : devices) {
//				if( !androidDevice.isEmulator()){ // We want a device (not emulator)
//					this.device = androidDevice;
//				}
//				//Prefer the device with given serial if available.
//				//This is probably important if there are multiple devices that are 
//				//connected.
//				if(serial.equals(androidDevice.getSerialNumber()))
//				{                                                 
//					this.device = androidDevice;
//					break;
//				}
//			}
//			if(this.device != null )
//			{
//				monitor.done();
//				return true;
//			}else{
//				throw new CoreException(new Status(IStatus.ERROR, BlackBerryCore.PLUGIN_ID, "Could not establish connection with the device. Please try again."));
//			}
//		}
		
		//Run emulator
//		BlackBerryDevice emulator = getEmulator();
//		// Do we have any emulators to run on?
//		if ( emulator == null ){
//			// No emulators lets start an emulator.
//			// Check if we have an AVD
//			String avdName = selectAVD(configuration, sdk);
//			if(monitor.isCanceled()){
//				return false;
//			}
//			//start the emulator.
//			sdk.startEmulator(avdName);
//			// wait for it to come online 
//			sdk.waitForEmulator();
//		}
//		this.device = getEmulator();
//		if(this.device == null ){// This is non-sense so is adb
//			sdk.killADBServer();
//			sdk.startADBServer();
//			this.device = getEmulator();
//		}
//		monitor.done();
		return true;
	}
	
//	private String selectAVD(ILaunchConfiguration configuration, BlackBerrySDKManager sdk) throws CoreException{
//		List<BlackBerryAVD> avds = sdk.listAVDs();
//		if (avds == null || avds.isEmpty()){
//			throw new CoreException(new HybridMobileStatus(IStatus.ERROR, BlackBerryCore.PLUGIN_ID, BlackBerryConstants.STATUS_CODE_ANDROID_AVD_ISSUE, 
//					"No Android AVDs are available",null));
//		}
//		String avdName = configuration.getAttribute(BlackBerryLaunchConstants.ATTR_AVD_NAME, (String)null);
//		BlackBerryAPILevelComparator alc = new BlackBerryAPILevelComparator();
//		for (BlackBerryAVD androidAVD : avds) {
//			if(avdName == null ){
//				if( alc.compare(androidAVD.getApiLevel(),BlackBerryConstants.REQUIRED_MIN_API_LEVEL) >-1){
//					avdName = androidAVD.getName();
//					break;
//				}
//			}
//			else if(androidAVD.getName().equals(avdName)){
//					if(alc.compare(androidAVD.getApiLevel(),BlackBerryConstants.REQUIRED_MIN_API_LEVEL) <0){
//						throw new CoreException(new HybridMobileStatus(IStatus.ERROR, BlackBerryCore.PLUGIN_ID, BlackBerryConstants.STATUS_CODE_ANDROID_AVD_ISSUE, 
//								NLS.bind("Selected Android AVD {0} does not satisfy the satisfy the minimum API level({1})",
//									new String[]{avdName, BlackBerryConstants.REQUIRED_MIN_API_LEVEL}),null));
//						
//					}
//				}
//			
//			}
//		if(avdName == null ){
//			throw new CoreException(new HybridMobileStatus(IStatus.ERROR, BlackBerryCore.PLUGIN_ID, BlackBerryConstants.STATUS_CODE_ANDROID_AVD_ISSUE, 
//					NLS.bind("Defined Android AVDs do not satisfy the minimum API level({0})",BlackBerryConstants.REQUIRED_MIN_API_LEVEL),null));
//		}
//		return avdName; 
//	}

	private BlackBerryDevice getEmulator() throws CoreException{
		BlackBerrySDKManager sdk = BlackBerrySDKManager.getManager();
		List<BlackBerryDevice> devices = sdk.listDevices();
		for (BlackBerryDevice androidDevice : devices) {
			if ( androidDevice.isEmulator() && androidDevice.getState() == BlackBerryDevice.STATE_DEVICE )
				return androidDevice;
		}
		return null;
	}
	
	//TODO: duplicated form IOSLaunchDelegate... move both to a common utility.
	private IProject getProject(ILaunchConfiguration configuration){
		try{
			String projectName = configuration.getAttribute(HybridProjectLaunchConfigConstants.ATTR_BUILD_SCOPE, (String)null);
			if(projectName != null ){
				 return ResourcesPlugin.getWorkspace().getRoot().getProject(projectName);
			}
		}catch(CoreException e){
			return null;
		}
		return null;
	}
}
