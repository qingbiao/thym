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
package org.eclipse.thym.blackberry.ui;

import java.util.List;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.debug.core.ILaunchConfiguration;
import org.eclipse.debug.core.ILaunchConfigurationWorkingCopy;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.thym.blackberry.core.bdt.BlackBerryDevice;
import org.eclipse.thym.blackberry.core.bdt.BlackBerryLaunchConstants;
import org.eclipse.thym.blackberry.core.bdt.BlackBerrySDK;
import org.eclipse.thym.blackberry.core.bdt.BlackBerrySDKManager;
import org.eclipse.thym.core.HybridProject;
import org.eclipse.thym.ui.launch.HybridProjectLaunchShortcut;
import org.eclipse.ui.PlatformUI;

public class BlackBerryDeviceLaunchShortcut extends HybridProjectLaunchShortcut {

	private BlackBerryDevice deviceToRun;

	@Override
	protected boolean validateBuildToolsReady() throws CoreException {
		BlackBerrySDKManager sdkManager = BlackBerrySDKManager.getManager();
		List<BlackBerrySDK> targets = sdkManager.listTargets();
		if(targets == null || targets.isEmpty() ){
			throw new CoreException(new Status(IStatus.ERROR, BlackBerryUI.PLUGIN_ID, "No targets to build against"));
		}
		return true;
		
		
	}

	@Override
	protected boolean shouldProceedWithLaunch(HybridProject project) {
		if (getDeviceToRun() == null) {
			MessageDialog
					.openError(PlatformUI.getWorkbench()
									.getActiveWorkbenchWindow().getShell(),
							"No Device attached",
							"No developer enabled BlackBerry device is attached to this computer please attach your device and try again.");
			return false;
		}
		return super.shouldProceedWithLaunch(project);
	}
	
	@Override
	protected void updateLaunchConfiguration(ILaunchConfigurationWorkingCopy wc) {
		wc.setAttribute(BlackBerryLaunchConstants.ATTR_IS_DEVICE_LAUNCH, true);
		BlackBerryDevice device = getDeviceToRun();
		if(device != null ){
			wc.setAttribute(BlackBerryLaunchConstants.ATTR_DEVICE_SERIAL, device.getSerialNumber());
		}
	}
	
	@Override
	protected boolean isCorrectLaunchConfiguration(IProject project,
			ILaunchConfiguration config) throws CoreException {
		if(config.getAttribute(BlackBerryLaunchConstants.ATTR_IS_DEVICE_LAUNCH, false)){
			return super.isCorrectLaunchConfiguration(project, config);
		}
		return false;
	}
	
	@Override
	protected String getLaunchConfigurationTypeID() {
		return BlackBerryLaunchConstants.ID_LAUNCH_CONFIG_TYPE;
	}

	private BlackBerryDevice getDeviceToRun() {
		if (deviceToRun == null) {
			try {
				BlackBerrySDKManager sdkManager = BlackBerrySDKManager.getManager();
				List<BlackBerryDevice> devices = sdkManager.listDevices();
				for (BlackBerryDevice blackberryDevice : devices) {
					if (!blackberryDevice.isEmulator()) {
						deviceToRun = blackberryDevice;
						break;
					}
				}
			} catch (CoreException e) {
				return null;
			}
		}
		return deviceToRun;
	}

	@Override
	protected String getLaunchConfigurationNamePrefix(IProject project) {
		return project.getName() + " (BlackBerry Device)";
	}
}
