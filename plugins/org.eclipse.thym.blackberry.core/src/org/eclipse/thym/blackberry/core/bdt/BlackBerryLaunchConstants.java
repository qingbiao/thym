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

import org.eclipse.thym.blackberry.core.BlackBerryCore;

public interface BlackBerryLaunchConstants {
	
	public static final String ID_LAUNCH_CONFIG_TYPE = "org.eclipse.thym.blackberry.core.BlackBerryLaunchConfigurationType";
	
	/**
	 * Attribute for the name of the AVD to be used for the launch
	 */
	public static final String ATTR_SIMULATOR_NAME = BlackBerryCore.PLUGIN_ID +".ATTR_SIMULATOR_NAME";
	
	/**
	 * Attribute for the serial name for the device to be run 
	 */
	public static final String ATTR_DEVICE_SERIAL = BlackBerryCore.PLUGIN_ID+".ATTR_DEVICE_SERIAL";
	
	/**
	 * Attribute for filter passed to the logcat during launch
	 */
	public static final String ATTR_LOGCAT_FILTER =  BlackBerryCore.PLUGIN_ID + ".ATTR_LOGCAT_FILTER";
	/**
	 * Attribute to determine launching on a real device
	 */
	public static final String ATTR_IS_DEVICE_LAUNCH = BlackBerryCore.PLUGIN_ID + ".ATTR_IS_DEVICE_LAUNCH";
	/**
	 * The default value for the logcat filter
	 */
	public static final String VAL_DEFAULT_LOGCAT_FILTER = "Cordova:V DroidGap:V CordovaLog:V CordovaPlugin:V CordovaChromeClient:V CordovaWebView:V *:S";
}
