/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.bankbjb.itcore.bulkupload.controller;

import com.bankbjb.itcore.bulkupload.service.ConfigService;
import com.bankbjb.itcore.bulkupload.service.LoginService;
import com.bankbjb.itcore.bulkupload.service.SQLConnectionService;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;

import javax.servlet.http.HttpServletRequest;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.support.RequestContext;

import flexjson.JSONSerializer;
import org.apache.log4j.Logger;

/**
 *
 * @author C994
 */
@Controller
@RequestMapping("/supervisors")
public class SupervisorsController {
    
    protected final Logger logger = Logger.getLogger(this.getClass());
    
    @RequestMapping(method = {RequestMethod.GET})
    public ModelAndView main(HttpServletRequest request) {
        HashMap output = new HashMap();
        
        LoginService loginService = new LoginService();
        HashMap userData = loginService.getUserSession(request);
        String userUID = (String) userData.get("userUID");
        
        SQLConnectionService sqlConnection = new SQLConnectionService("jdbc");
        if (sqlConnection.createConnection()) {
            try {
                String query = "SELECT * FROM supervisors WHERE user_id = ?";
                PreparedStatement pstmt = sqlConnection.getConnection().prepareStatement(query);
                pstmt.setString(1, userUID);

                ResultSet rs = pstmt.executeQuery();
                if (rs.next()) {
                    output.put("user_id", rs.getString("user_id"));
                    output.put("supervisor_code", rs.getString("supervisor_code"));
                    output.put("supervisor_pass", rs.getString("supervisor_pass"));
                }
            } catch (SQLException e) {
                logger.error("SQLException: " + e.getMessage());
            } finally {
                sqlConnection.closeConnection();
            }
        }
        
        ModelAndView modelAndView = new ModelAndView(ConfigService.getProperty("template.path") + ConfigService.getProperty("template.layout") + "/layout");
        modelAndView.addObject("jsfile", "supervisors/script.jsp");
        modelAndView.addObject("pgtitle", "supervisors");
        modelAndView.addObject("pgcontent", "supervisors/form.jsp");
        modelAndView.addObject("activeMenu", "supervisors");
        modelAndView.addObject("data", output);

        return modelAndView;
    }
    
    @RequestMapping(method = {RequestMethod.POST})
    public @ResponseBody String mainPost(@RequestParam(value="supervisor_code", required=false, defaultValue="") String supervisor_code,
                                            @RequestParam(value="supervisor_pass", required=false, defaultValue="") String supervisor_pass,
                                            HttpServletRequest request) {
        boolean success = false;
        RequestContext ctx = new RequestContext(request);
        
        LoginService loginService = new LoginService();
        HashMap userData = loginService.getUserSession(request);
        String userUID = (String) userData.get("userUID");
        
        SQLConnectionService sqlConnection = new SQLConnectionService("jdbc");
        if (sqlConnection.createConnection()) {
            try {
                String query = "SELECT * FROM supervisors WHERE user_id = ?";
                PreparedStatement pstmt = sqlConnection.getConnection().prepareStatement(query);
                pstmt.setString(1, userUID);

                ResultSet rs = pstmt.executeQuery();
                if (rs.next()) {
                    try {
                        query = "UPDATE supervisors SET supervisor_code = ?, supervisor_pass = ? WHERE user_id = ?";
                        pstmt = sqlConnection.getConnection().prepareStatement(query);
                        pstmt.setString(1, supervisor_code);
                        pstmt.setString(2, supervisor_pass);
                        pstmt.setString(3, userUID);

                        success = pstmt.executeUpdate() > 0;
                    } catch (SQLException e) {
                        logger.error("SQLException: " + e.getMessage());
                    }
                } else {
                    try {
                        query = "INSERT INTO supervisors(user_id, supervisor_code, supervisor_pass) VALUES(?, ?, ?)";
                        pstmt = sqlConnection.getConnection().prepareStatement(query);
                        pstmt.setString(1, userUID);
                        pstmt.setString(2, supervisor_code);
                        pstmt.setString(3, supervisor_pass);

                        success = pstmt.executeUpdate() > 0;
                    } catch (SQLException e) {
                        logger.error("SQLException: " + e.getMessage());
                    }
                }
            } catch (SQLException e) {
                logger.error("SQLException: " + e.getMessage());
            } finally {
                sqlConnection.closeConnection();
            }
        }
        
        HashMap output = new HashMap();
        output.put("status", success ? "success" : "attention");
        output.put("message", String.format(ctx.getMessage(success ? "supervisors.save_succeed" : "supervisors.save_failed"), userUID));
        
        JSONSerializer serializer = new JSONSerializer();
        String jsonString = serializer.deepSerialize(output);
        
        return jsonString;
    }
    
}
