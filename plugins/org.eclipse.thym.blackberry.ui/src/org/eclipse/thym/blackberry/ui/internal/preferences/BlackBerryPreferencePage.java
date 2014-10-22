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
package org.eclipse.thym.blackberry.ui.internal.preferences;

import java.io.File;

import org.eclipse.core.runtime.IStatus;
import org.eclipse.jface.preference.DirectoryFieldEditor;
import org.eclipse.jface.preference.FieldEditorPreferencePage;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.thym.blackberry.core.BlackBerryConstants;
import org.eclipse.thym.ui.HybridUI;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPreferencePage;

/**
 * This class represents a preference page that
 * is contributed to the Preferences dialog. By 
 * subclassing <samp>FieldEditorPreferencePage</samp>, we
 * can use the field support built into JFace that allows
 * us to create a page that is small and knows how to 
 * save, restore and apply itself.
 * <p>
 * This page is used to modify preferences only. They
 * are stored in the preference store that belongs to
 * the main plug-in class. That way, preferences can
 * be accessed directly via the preference store.
 */

public class BlackBerryPreferencePage
	extends FieldEditorPreferencePage
	implements IWorkbenchPreferencePage {
	
	public static final String PAGE_ID = "org.eclipse.thym.blackberry.ui.BlackBerryPreferencePages";

	private static class BlackBerrySDKDirectoryFieldEditor extends DirectoryFieldEditor{
		public BlackBerrySDKDirectoryFieldEditor(String prefBlackBerrySdkLocation,
				String string, Composite fieldEditorParent) {
			super(prefBlackBerrySdkLocation, string, fieldEditorParent);
			setEmptyStringAllowed(true);
		}

		@Override
		protected boolean doCheckState() {			
			String filename = getTextControl().getText();
			filename = filename.trim();
			if(filename.isEmpty()){
				this.getPage().setMessage("A location for the BlackBerry SDK must be specified", IStatus.WARNING);
				return true;
			}else{
				// clear the warning message
				this.getPage().setMessage(null);
			}
			
			if(!filename.endsWith(File.separator)){
				filename = filename+ File.separator;
			}
			
				
			File file = new File(filename);
			if (!file.isDirectory()){
				setErrorMessage("A directory must be specified");
				return false;
			}
			// Check the tools folder
//			File toolsFolder = new File(file,"tools");
//			File platformToolsFolder = new File(file, "platform-tools");
//			if(!toolsFolder.isDirectory() || !platformToolsFolder.isDirectory()){
//				setErrorMessage("Not a valid BlackBerry SDK directory");
//				return false;
//			}
			return true;
		}
		
		@Override
		public void setValidateStrategy(int value) {
			super.setValidateStrategy(VALIDATE_ON_KEY_STROKE);
		}
	}
	
	public BlackBerryPreferencePage() {
		super(GRID);
		setPreferenceStore(HybridUI.getDefault().getPreferenceStore());
		setDescription("BlackBerry settings for Hybrid Mobile Application development");
	}
	
	/**
	 * Creates the field editors. Field editors are abstractions of
	 * the common GUI blocks needed to manipulate various types
	 * of preferences. Each field editor knows how to save and
	 * restore itself.
	 */
	public void createFieldEditors() {
		BlackBerrySDKDirectoryFieldEditor editor = new BlackBerrySDKDirectoryFieldEditor(BlackBerryConstants.PREF_BLACKBERRY_SDK_LOCATION, 
				"BlackBerry SDK Directory:", getFieldEditorParent());
		addField(editor);
	}
	
	/* (non-Javadoc)
	 * @see org.eclipse.ui.IWorkbenchPreferencePage#init(org.eclipse.ui.IWorkbench)
	 */
	public void init(IWorkbench workbench) {
		//nothing to do 
	}
	
}