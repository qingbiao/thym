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
package org.eclipse.thym.blackberry.core;

public interface BlackBerryConstants {
	
	public static final String REQUIRED_MIN_API_LEVEL = "17";

	
	public static final String DIR_ASSETS = "assets";
	public static final String DIR_LIBS = "libs";
	public static final String DIR_RES = "res";
	public static final String DIR_SRC = "src";
	public static final String DIR_XML = "xml";
	public static final String DIR_VALUES = "values";
	public static final String DIR_BIN = "bin";
	
	public static final String FILE_JAR_CORDOVA = "cordova.jar";
	public static final String FILE_XML_ANDROIDMANIFEST = "AndroidManifest.xml";
	public static final String FILE_XML_STRINGS = "strings.xml";
	public static final String FILE_XML_BUILD ="build.xml";
	public static final String PREF_BLACKBERRY_SDK_LOCATION = "BlackBerry_SDK_Loc";
	
	public static final int STATUS_CODE_BLACKBERRY_SDK_NOT_DEFINED= 200;
	public static final int STATUS_CODE_BLACKBERRY_AVD_ISSUE= 210;

}
