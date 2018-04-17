/* Copyright 2015 ESRI
* 
* All rights reserved under the copyright laws of the United States
* and applicable international laws, treaties, and conventions.
* 
* You may freely redistribute and use this sample code, with or
* without modification, provided you include the original copyright
* notice and use restrictions.
* 
* See the use restrictions at <your ArcGIS install location>/DeveloperKit10.4/userestrictions.txt.
* 
*/
package cn.decom.customertoolbox;

import java.io.File;
import java.lang.reflect.Method;
import java.net.URL;
import java.net.URLClassLoader;

public class Main{
	
    public static void main(String[] args)throws Exception {
        bootstrapArcobjectsJar();
        CustomerToolbox.main(args);
    }

    public static void bootstrapArcobjectsJar(){
    	//Get the ArcGIS Engine runtime, if it is available
    	String arcObjectsHome = System.getenv("AGSENGINEJAVA");
    	
    	//If the ArcGIS Engine runtime is not available, then we can try ArcGIS Desktop runtime
    	if(arcObjectsHome == null){
    		arcObjectsHome = System.getenv("AGSDESKTOPJAVA");
    	}
    	
    	//If no runtime is available, exit application gracefully
    	if(arcObjectsHome == null){
    		if(System.getProperty("os.name").toLowerCase().indexOf("win") > -1){
    			System.err.println("You must have ArcGIS Engine Runtime or ArcGIS Desktop " + 
    					"installed in order to execute this sample.");
    			System.err.println("Install one of the products above, then re-run this sample.");
    			System.err.println("Exiting execution of this sample...");
    			System.exit(0);	
    		}else{
    			System.err.println("You must have ArcGIS Engine Runtime installed " + 
    					"in order to execute this sample.");
    			System.err.println("Install the product above, then re-run this sample.");
    			System.err.println("Exiting execution of this sample...");
    			System.exit(0);	
    		}
    	}
        
    	//Obtain the relative path to the arcobjects.jar file
        String jarPath = arcObjectsHome + "java" + File.separator + "lib" +
            			 File.separator + "arcobjects.jar";

        //Create a new file
        File jarFile = new File(jarPath);
        
        //Test for file existence
        if(!jarFile.exists()){
        	System.err.println("The arcobjects.jar was not found in the following location: " +
                                                jarFile.getParent());
            System.err.println("Verify that arcobjects.jar can be located in the specified folder.");
            System.err.println("If not present, try uninstalling your ArcGIS software and reinstalling it.");
            System.err.println("Exiting execution of this sample...");
            System.exit(0);
        }

        //Helps load classes and resources from a search path of URLs
        URLClassLoader sysloader = (URLClassLoader) ClassLoader.getSystemClassLoader();
        Class<URLClassLoader> sysclass = URLClassLoader.class;

        try{
        	Method method = sysclass.getDeclaredMethod("addURL", new Class[]{URL.class});
            method.setAccessible(true);
            method.invoke(sysloader, new Object[]{jarFile.toURI().toURL()});
        }catch (Throwable throwable){
            throwable.printStackTrace();
            System.err.println("Could not add arcobjects.jar to system classloader");
            System.err.println("Exiting execution of this sample...");
            System.exit(0);
        }
    }
}