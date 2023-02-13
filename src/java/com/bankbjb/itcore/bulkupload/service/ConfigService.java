/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.bankbjb.itcore.bulkupload.service;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Properties;
import org.apache.log4j.Logger;

/**
 *
 * @author C994
 */
public class ConfigService {
    
    protected static final Logger logger = Logger.getLogger(ConfigService.class);
    private static String configpath = null;
    private static HashMap customprop = null;
    
    private static void loadConfig() {
        Properties prop = new Properties();
    	try {
            //load a properties file
            prop.load(new FileInputStream(configpath));
            customprop = new HashMap();
            for (Enumeration e = prop.keys(); e.hasMoreElements();) {
                String k = (String) e.nextElement();
                customprop.put(k, prop.getProperty(k));
            }
    	} catch (IOException ex) {
            logger.error("error load 'custom.properties': " + ex.getStackTrace());
        }
    }
    
    public static String getConfigPath() {
        return configpath;
    }
    
    public static void setConfigPath(String path) {
        configpath = path;
    }
    
    public static String getProperty(String key) {
        if (customprop == null) {
            loadConfig();
        }
        
        return (String) customprop.get(key);
    }
    
    public static void setProperty(String key, String value) {
        customprop.put(key, value);
    }
    
}
