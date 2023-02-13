/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.bankbjb.itcore.bulkupload.service;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

import org.apache.log4j.Logger;

/**
 *
 * @author C994
 */
public class SQLConnectionService {
    
    protected final Logger logger = Logger.getLogger(this.getClass());
    
    String prefixConfig = "";
    Connection connection = null;

    public SQLConnectionService(String pc) {
        prefixConfig = pc;
    }
    
    public boolean createConnection() {
        try {
            String driver = ConfigService.getProperty(prefixConfig + ".driver");
            String url = ConfigService.getProperty(prefixConfig + ".url");
            String userid = ConfigService.getProperty(prefixConfig + ".userid");
            String password = ConfigService.getProperty(prefixConfig + ".password");
            
            Class.forName(driver);
            connection = DriverManager.getConnection(url, userid, password);
            
            return true;
        } catch (java.lang.ClassNotFoundException e) {
            System.err.print("ClassNotFoundException: ");
            System.err.println(e.getMessage());
            return false;
        } catch(SQLException ex) {
            System.err.println("SQLException: " + ex.getMessage());
            return false;
        }
    }
    
    public boolean closeConnection() {
        try {
            connection.close();
            return true;
        } catch (SQLException e) {
            System.err.println("SQLException: " + e.getMessage());
            return false;
        }
    }
    
    public Connection getConnection() {
        return connection;
    }
}
