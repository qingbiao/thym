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


import static org.eclipse.thym.blackberry.core.BlackBerryConstants.DIR_LIBS;
import static org.eclipse.thym.blackberry.core.BlackBerryConstants.DIR_RES;
import static org.eclipse.thym.blackberry.core.BlackBerryConstants.DIR_SRC;
import static org.eclipse.thym.blackberry.core.BlackBerryConstants.DIR_VALUES;
import static org.eclipse.thym.blackberry.core.BlackBerryConstants.DIR_XML;
import static org.eclipse.thym.blackberry.core.BlackBerryConstants.FILE_JAR_CORDOVA;
import static org.eclipse.thym.blackberry.core.BlackBerryConstants.FILE_XML_STRINGS;
import static org.eclipse.thym.core.internal.util.FileUtils.directoryCopy;
import static org.eclipse.thym.core.internal.util.FileUtils.fileCopy;
import static org.eclipse.thym.core.internal.util.FileUtils.toURL;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.net.URL;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.Source;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpression;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;

import org.apache.commons.io.FileUtils;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Path;
import org.eclipse.core.runtime.Status;
import org.eclipse.osgi.util.NLS;
import org.eclipse.thym.blackberry.core.BlackBerryCore;
import org.eclipse.thym.core.HybridCore;
import org.eclipse.thym.core.HybridProject;
import org.eclipse.thym.core.config.Widget;
import org.eclipse.thym.core.config.WidgetModel;
import org.eclipse.thym.core.engine.HybridMobileLibraryResolver;
import org.eclipse.thym.core.platform.AbstractProjectGeneratorDelegate;
import org.eclipse.thym.core.platform.PlatformConstants;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.xml.sax.SAXException;

public class BlackBerryProjectGenerator extends AbstractProjectGeneratorDelegate{


	private final String[] TARGETS = {"device", "simulator"};
	
	public BlackBerryProjectGenerator(){
		super();
	}
	
	public BlackBerryProjectGenerator(IProject project, File generationFolder,String platform) {
		init(project, generationFolder,platform);
	}

	@Override
	protected void generateNativeFiles(HybridMobileLibraryResolver resolver) throws CoreException {
		
		HybridProject hybridProject = HybridProject.getHybridProject(getProject());
		if(hybridProject == null ){
			throw new CoreException(new Status(IStatus.ERROR, BlackBerryCore.PLUGIN_ID, "Not a hybrid mobile project, can not generate files"));
		}

		File destinationDir = getDestination();
		Path destinationPath = new Path(destinationDir.toString());
		if(destinationDir.exists()){
			try {//Clean the android directory to avoid and "Error:" message 
				 // from the command line tools for using update. Otherwise all create
				// project calls will be recognized as failed.
				FileUtils.cleanDirectory(destinationDir);
			} catch (IOException e) {
				throw new CoreException(new Status(IStatus.WARNING, BlackBerryCore.PLUGIN_ID,
						NLS.bind("Could not clean the android working directory at {0}",destinationDir), e));
			}
		}
		
		String name = hybridProject.getBuildArtifactAppName();
		IPath prjPath = destinationPath.append(name);
		Widget widgetModel = WidgetModel.getModel(hybridProject).getWidgetForRead();
		String packageName = widgetModel.getId();
		
		File prjdir = prjPath.toFile();
		if( !prjdir.exists() ){//create the project directory
			prjdir.mkdirs();
		}

		
//		BlackBerrySDK target = BlackBerryProjectUtils.selectBestValidTarget(resolver);
//		sdkManager.createProject(target, name, destinationDir,name, packageName, new NullProgressMonitor());
		
		try{
			IPath cordovaJarPath = destinationPath.append(DIR_LIBS).append(FILE_JAR_CORDOVA);
			//Move cordova library to /libs/cordova.jar
 			fileCopy(resolver.getTemplateFile(cordovaJarPath.makeRelativeTo(destinationPath)), toURL(cordovaJarPath.toFile()));
 			
 			
 			// //res
 			IPath resPath = destinationPath.append(DIR_RES);
			directoryCopy(resolver.getTemplateFile(resPath.makeRelativeTo(destinationPath)),
					toURL(resPath.toFile()));
			
			IFile configFile = hybridProject.getConfigFile();
			IPath xmlPath = resPath.append(DIR_XML);
			File xmldir = xmlPath.toFile();
			if( !xmldir.exists() ){//only config.xml uses xml 
				xmldir.mkdirs();   //directory make sure it is created
			}
			fileCopy(toURL(configFile.getLocation().toFile()), 
					toURL(xmlPath.append(PlatformConstants.FILE_XML_CONFIG).toFile()));
			
			updateAppName(hybridProject.getAppName());
			
			// Copy templated files 
//			Map<String, String> values = new HashMap<String, String>();
//			values.put("__ID__", packageName);
//			values.put("__PACKAGE__", packageName);// yeap, cordova also uses two different names
//			values.put("__ACTIVITY__", name);
//			values.put("__APILEVEL__", target.getApiLevel());
			
			// /AndroidManifest.xml
//			IPath andrManifestPath = destinationPath.append("AndroidManifest.xml");
//			templatedFileCopy(resolver.getTemplateFile(andrManifestPath.makeRelativeTo(destinationPath)), 
//					toURL(andrManifestPath.toFile()),
//					values);
			// /src/${package_dirs}/Activity.java
			IPath activityPath = new Path(DIR_SRC).append(HybridMobileLibraryResolver.VAR_PACKAGE_NAME).append(HybridMobileLibraryResolver.VAR_APP_NAME+".java");
			IPath resolvedActivityPath = destinationPath.append(DIR_SRC).append(packageName.replace('.', '/')).append(name+".java");
//			templatedFileCopy(resolver.getTemplateFile(activityPath), 
//					toURL(resolvedActivityPath.toFile()),
//					values);
		}
		
		catch(IOException e)
		{
			throw new CoreException(new Status(IStatus.ERROR, BlackBerryCore.PLUGIN_ID, "Error generating the native android project", e));
		}
	}
	
	private void updateAppName( String appName ) throws CoreException{
	    DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
	    dbf.setNamespaceAware(true);
	    DocumentBuilder db;

	    try{
	    	db = dbf.newDocumentBuilder();
	    	IPath stringsPath = new Path(getDestination().toString()).append(DIR_RES).append(DIR_VALUES).append(FILE_XML_STRINGS);
	    	File strings = stringsPath.toFile();
	    	Document configDocument = db.parse( strings); 
	    	XPath xpath = XPathFactory.newInstance().newXPath();
	    	
	    	try {
	    		XPathExpression expr = xpath.compile("//string[@name=\"app_name\"]");
				Node node = (Node) expr.evaluate( configDocument, XPathConstants.NODE);
				node.setTextContent(appName);
				
			    configDocument.setXmlStandalone(true);
			    
			    Source source = new DOMSource(configDocument);

			   
			    StreamResult result = new StreamResult(strings);

			    // Write the DOM document to the file
			    TransformerFactory transformerFactory = TransformerFactory
				    .newInstance();
			    Transformer xformer = transformerFactory.newTransformer();

			    xformer.transform(source, result);
				
			} catch (XPathExpressionException e) {//We continue because this affects the displayed app name
				                                  // which is not a show stopper during development
				BlackBerryCore.log(IStatus.ERROR, "Error when updating the application name", e);
			} catch (TransformerConfigurationException e) {
				BlackBerryCore.log(IStatus.ERROR, "Error when updating the application name", e);
			} catch (TransformerException e) {
				BlackBerryCore.log(IStatus.ERROR, "Error when updating the application name", e);
			}
	    	
	    }
		catch (ParserConfigurationException e) {
			throw new CoreException(new Status(IStatus.ERROR, HybridCore.PLUGIN_ID, "Parser error when parsing /res/values/strings.xml", e));
		} catch (SAXException e) {
			throw new CoreException(new Status(IStatus.ERROR, HybridCore.PLUGIN_ID, "Parsing error on /res/values/strings.xml", e));
		} catch (IOException e) {
			throw new CoreException(new Status(IStatus.ERROR, HybridCore.PLUGIN_ID, "IO error when parsing /res/values/strings.xml", e));
		} 
	}

	@Override
	protected void replaceCordovaPlatformFiles(HybridMobileLibraryResolver resolver) throws IOException {
		IPath cordovaJSPath = new Path(getPlatformWWWDirectory().toString()).append(PlatformConstants.FILE_JS_CORDOVA);
		fileCopy(resolver.getTemplateFile(HybridMobileLibraryResolver.PATH_CORDOVA_JS),
				toURL(cordovaJSPath.toFile()));
	}


	
	@Override
	protected File getPlatformWWWDirectory() {
		return BlackBerryProjectUtils.getPlatformWWWDirectory(getDestination());
	}

	private void copyFilesToProject(HybridMobileLibraryResolver resolver, IPath project_path) throws IOException {
	    IPath nodeModulesDest = project_path.append("cordova").append("node_modules");
	    IPath bbtoolsBinDest = project_path.append("cordova").append("dependencies").append("bb-tools").append("bin");
	    IPath bbtoolsLibDest = project_path.append("cordova").append("dependencies").append("bb-tools").append("lib");
	    String bbNativePackager = "blackberry-nativepackager";
	    String bbSigner = "blackberry-signer";
	    String bbDeploy = "blackberry-deploy";
	    String bbDebugTokenRequest= "blackberry-debugtokenrequest";
	    
	    // create project using template directory
	    if (!project_path.toFile().exists()) {
	    	project_path.toFile().mkdir();
	    }
	    directoryCopy(resolver.getTemplateFile(new Path("templateDir")), toURL(project_path.toFile()));

	    // copy repo level target tool to project
	    fileCopy(resolver.getTemplateFile(new Path("target")), toURL(project_path.append("cordova").toFile()));
	    fileCopy(resolver.getTemplateFile(new Path("target.bat")), toURL(project_path.append("cordova").toFile()));
	    fileCopy(resolver.getTemplateFile(new Path("lib/target.js")), toURL(project_path.append("cordova").append("lib").toFile()));
	    fileCopy(resolver.getTemplateFile(new Path("lib.config.js")), toURL(project_path.append("cordova").append("lib").toFile()));

		
	    // copy repo level init script to project
	    fileCopy(resolver.getTemplateFile(new Path("whereis.cmd")), toURL(project_path.append("cordova").toFile()));
	    fileCopy(resolver.getTemplateFile(new Path("init.bat")), toURL(project_path.append("cordova").toFile()));
	    fileCopy(resolver.getTemplateFile(new Path("init")), toURL(project_path.append("cordova").toFile()));

	    //copy VERSION file [used to identify corresponding ~/.cordova/lib directory for dependencies]
	    URL versionFile = resolver.getTemplateFile(new Path("VERSION"));
	    fileCopy(versionFile, toURL(project_path.toFile()));
	    String version = "";
	    BufferedReader r = null;
	    try {
	    	r = new BufferedReader(new FileReader(versionFile.getFile()));
	    	String line = r.readLine();
	    	version = line.replaceAll("([^\\x00-\\xFF]|\\s)*", "");
	    } finally {
	    	if (r!= null) {
	    		r.close();
	    	}
	    }

	    // copy repo level check_reqs script to project
	    fileCopy(resolver.getTemplateFile(new Path("check_reqs.bat")), toURL(project_path.append("cordova").toFile()));
	    fileCopy(resolver.getTemplateFile(new Path("check_reqs")), toURL(project_path.append("cordova").toFile()));

	    // change file permission for cordova scripts because ant copy doesn't preserve file permissions
	    project_path.toFile().setExecutable(true);
	    project_path.toFile().setWritable(true);
	    project_path.toFile().setReadable(true);

	    //copy cordova-*version*.js to www
	    fileCopy(resolver.getTemplateFile(new Path("cordova.js")), toURL(project_path.append("www").toFile()));

	    //copy node modules to cordova build directory
	    File nodeModules = project_path.append("cordova").append("node_modules").toFile();
	    if (!nodeModules.exists()) {
	    	nodeModules.mkdir();
	    }
	    nodeModules.setExecutable(true, false);
	    nodeModules.setWritable(true, false);
	    nodeModules.setReadable(true, false);
	    directoryCopy(resolver.getTemplateFile(new Path("node_modules")), toURL(nodeModules));

	    //copy framework bootstrap
	    for (String target : TARGETS) {
	    	IPath chromeDir = project_path.append("native").append(target).append("chrome");
	    	IPath frameworkLibDir = chromeDir.append("lib");
	    	if (!frameworkLibDir.toFile().exists()) {
	    		frameworkLibDir.toFile().mkdir();
	    	}
	    	directoryCopy(resolver.getTemplateFile(new Path("bootstrapDir")), toURL(chromeDir.toFile()));
	    	directoryCopy(resolver.getTemplateFile(new Path("frameworkLibDir")), toURL(frameworkLibDir.toFile()));
	    }

	    // save release
	    IPath updateDir = project_path.append("lib").append("cordova.").append(version);
	    if (!updateDir.toFile().exists()) {
	    	updateDir.toFile().mkdir();
	    }
    	directoryCopy(resolver.getTemplateFile(new Path("buildDir")), toURL(updateDir.toFile()));
	}
	
	
}
