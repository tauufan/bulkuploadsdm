/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.bankbjb.itcore.bulkupload.service;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Enumeration;
import java.util.HashMap;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.apache.log4j.Logger;

/**
 *
 * @author C994
 */
public class LoginService {
    
    private static final Logger logger = Logger.getLogger(LoginService.class);
    private String userID = "";
    private String userPass = "";
    private HttpSession sessionVar = null;
    private String message = "";
    
    public void setUserID(String userID) {
        this.userID = userID;
    }
    
    public String getUserID() {
        return this.userID;
    }
    
    public void setUserPass(String userPass) {
        this.userPass = userPass;
    }
    
    public String getUserPass() {
        return this.userPass;
    }
    
    public void setSessionVar(HttpServletRequest request) {
        this.sessionVar = request.getSession(false);
    }
    
    public HttpSession getSessionVar() {
        return this.sessionVar;
    }
    
    public HashMap<String, String> getUserData() {
        // put user data to hashmap
        HashMap userData = new HashMap();
        
        SQLConnectionService sqlConnection = new SQLConnectionService("uim");
        if (sqlConnection.createConnection()) {
            try {
                String query = "EXEC NCAh3r1UIM ?, ?, ?";
                PreparedStatement pstmt = sqlConnection.getConnection().prepareStatement(query);
                pstmt.setString(1, this.getUserID());
                pstmt.setString(2, this.getUserPass());
                pstmt.setString(3, "bjb Bulk Sistem");

                ResultSet rs = pstmt.executeQuery();
                if (rs.next()) {
                    userData.put("userID", this.getUserID());
                    userData.put("userPass", this.getUserPass());
                    userData.put("userName", rs.getString("nama"));
                    userData.put("userRole", rs.getString("fungsistandaraplikasi"));
                    userData.put("userLevel", rs.getString("Lev"));
                    userData.put("userCabang", rs.getString("kodecabang"));
                    userData.put("userNIP", rs.getString("nip"));
                    userData.put("userUID", rs.getString("id"));
                }
            } catch (SQLException e) {
                logger.error("SQLException: " + e.getMessage());
            } finally {
                sqlConnection.closeConnection();
            }
        }
        
        return userData;
    }
    
    public void setUserSession(HttpServletRequest request) {
        this.setSessionVar(request);
        int sessionLifeTime = Integer.parseInt(ConfigService.getProperty("session.life_time"));
        
        this.setUserID(request.getParameter("username"));
        this.setUserPass(request.getParameter("password"));
        
        // get data from database
        HashMap userData = this.getUserData();
        
        this.getSessionVar().setAttribute("userData", userData);
        this.getSessionVar().setMaxInactiveInterval(sessionLifeTime * 60); // 10 minutes
        
        logger.info("Session " + this.getSessionVar().getId() + " has been created.");
    }
    
    public void clearUserSession(HttpServletRequest request) {
        this.setSessionVar(request);

        if (this.getSessionVar() != null) {
            String sid = this.getSessionVar().getId();
//            this.getSessionVar().invalidate();
            Enumeration userSession = this.getSessionVar().getAttributeNames();
            while (userSession.hasMoreElements()) {
                String s = (String) userSession.nextElement();
                this.getSessionVar().setAttribute(s, null);
            }
            
            logger.info("Session " + sid + " has been destroyed.");
        }
    }
    
    public HashMap<String, String> getUserSession(HttpServletRequest request) {
        this.setSessionVar(request);
        
        try {
            return (HashMap<String, String>) this.getSessionVar().getAttribute("userData");
        } catch (Exception e) {
            return new HashMap<String, String>();
        }
    }
    
    public boolean checkUserSession(HttpServletRequest request) {
        HashMap userData = this.getUserSession(request);
        
        return userData == null || userData.get("userID") == null ? false : true;
    }
    
    public boolean validateCredential(HttpServletRequest request) {
        this.setUserID(request.getParameter("username"));
        this.setUserPass(request.getParameter("password"));
        
        HashMap userData = this.getUserData();
        return !userData.isEmpty();
    }
    
    public String getMessage() {
        return this.message;
    }
}
