/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.bankbjb.itcore.bulkupload.controller;

import com.bankbjb.itcore.bulkupload.common.SwallowingJspRenderer;
import com.bankbjb.itcore.bulkupload.service.ConfigService;
import com.bankbjb.itcore.bulkupload.service.LoginService;
import com.bankbjb.itcore.bulkupload.service.SQLConnectionService;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Locale;

import javax.servlet.http.HttpServletRequest;

import org.springframework.stereotype.Controller;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.support.RequestContext;

import flexjson.JSONSerializer;
import java.util.Map;
import org.apache.log4j.Logger;

/**
 *
 * @author C994
 */
@Controller
@RequestMapping("/debet-accounts")
public class DebetAccountController {
    
    protected final Logger logger = Logger.getLogger(this.getClass());
    
    @RequestMapping(method = {RequestMethod.GET})
    public ModelAndView main(HttpServletRequest request) {
        ModelAndView modelAndView = new ModelAndView(ConfigService.getProperty("template.path") + ConfigService.getProperty("template.layout") + "/layout");
        modelAndView.addObject("jsfile", "debet-accounts/script.jsp");
        modelAndView.addObject("pgtitle", "debet_accounts");
        modelAndView.addObject("pgcontent", "debet-accounts/main.jsp");
        modelAndView.addObject("activeMenu", "debet-accounts");

        return modelAndView;
    }
    
    @RequestMapping(method = {RequestMethod.POST})
    public @ResponseBody String mainPost(HttpServletRequest request) {
        String out = "";
        RequestContext ctx = new RequestContext(request);
        
        try {
            Locale locale = new java.util.Locale("id", "ID");
            String requestedLocale = (String) request.getAttribute("locale");

            // If the user passes in a ?locale=foo line, parse it and 
            // use that as a locale
            if(requestedLocale != null && StringUtils.hasText(requestedLocale)) {
                if(requestedLocale.contains("_")) {
                    String[] parts = requestedLocale.split("_", 2);
                    locale = new Locale(parts[0], parts[1]);
                } else {
                    locale = new Locale(requestedLocale);
                }
            }

            SwallowingJspRenderer jspRenderer = new SwallowingJspRenderer();
            jspRenderer.setServletContext(request.getServletContext());
            out = jspRenderer.render("debet-accounts/main", new HashMap(), locale);
        } catch (Exception e) {
            logger.error(e.getStackTrace());
        }
        
        HashMap output = new HashMap();
        output.put("status", out.length() > 0 ? "success" : "attention");
        output.put("message", out.length() > 0 ? "" : ctx.getMessage("global.load_page_failed"));
        output.put("content", out);
        
        JSONSerializer serializer = new JSONSerializer();
        String jsonString = serializer.deepSerialize(output);
        
        return jsonString;
    }
    
    @RequestMapping(value = "data", method = {RequestMethod.POST})
    public @ResponseBody String data(@RequestParam(value="sEcho", required=true) String sEcho,
                                            @RequestParam(value="iDisplayStart", required=true) String iDisplayStart,
                                            @RequestParam(value="iDisplayLength", required=true) String iDisplayLength,
                                            HttpServletRequest request) {
        ArrayList result = new ArrayList();
        ArrayList row = null;
        
        RequestContext ctx = new RequestContext(request);
        
        LoginService loginService = new LoginService();
        HashMap userData = loginService.getUserSession(request);
        String owner = (String) userData.get("userCabang");
        
        int promptNumRows = 0;
        SQLConnectionService sqlConnection = new SQLConnectionService("jdbc");
        if (sqlConnection.createConnection()) {
            try {
                String query = "SELECT * FROM debet_accounts WHERE debet_acc_owner = ?";
                PreparedStatement pstmt = sqlConnection.getConnection().prepareStatement(query);
                pstmt.setString(1, owner);

                ResultSet rs = pstmt.executeQuery();
                while (rs.next()) {
                    row = new ArrayList();
                    row.add("<a href=\"debet-accounts/edit\" class=\"ajax-link\" param=\"debet_acc_id=" + rs.getString("debet_acc_id") + "\">" + rs.getString("debet_acc_no") + "</a>");
                    row.add(rs.getString("debet_acc_name"));
                    row.add(ctx.getMessage("debet_accounts." + (rs.getInt("is_active") == 1 ? "active" : "inactive")));
                    row.add("<a href=\"debet-accounts/edit\" class=\"ajax-link\" param=\"debet_acc_id=" + rs.getString("debet_acc_id") + "\">" + ctx.getMessage("debet_accounts.edit") + "</a> | " +
                            "<a href=\"debet-accounts/remove\" class=\"ajax-remove\" param=\"debet_acc_id=" + rs.getString("debet_acc_id") + "&debet_acc_no=" + rs.getString("debet_acc_no") + "\">" + ctx.getMessage("debet_accounts.remove") + "</a>");

                    result.add(row);
                    promptNumRows++;
                }
            } catch (SQLException e) {
                logger.error("SQLException: " + e.getMessage());
            } finally {
                sqlConnection.closeConnection();
            }
        }

        HashMap outputObject = new HashMap();
        outputObject.put("sEcho", sEcho);
        outputObject.put("iTotalRecords", promptNumRows);
        outputObject.put("iTotalDisplayRecords", promptNumRows);
        outputObject.put("aaData", result);
        
        JSONSerializer serializer = new JSONSerializer();
        String jsonString = serializer.deepSerialize(outputObject);
        
        return jsonString;
    }
    
    @RequestMapping(value = "add", method = {RequestMethod.POST})
    public @ResponseBody String add(HttpServletRequest request) {
        HashMap output = new HashMap();
        String out = "";
        
        RequestContext ctx = new RequestContext(request);
            
        Map pPost = request.getParameterMap();
        
        if (pPost.containsKey("submit")) {
            boolean success = false;
            String debet_acc_no = ((String[]) pPost.get("debet_acc_no"))[0];
            String debet_acc_name = ((String[]) pPost.get("debet_acc_name"))[0];
            String is_active = ((String[]) pPost.get("is_active"))[0];
            
            LoginService loginService = new LoginService();
            HashMap userData = loginService.getUserSession(request);
            String owner = (String) userData.get("userCabang");
            
            SQLConnectionService sqlConnection = new SQLConnectionService("jdbc");
            if (sqlConnection.createConnection()) {
                try {
                    String query = "INSERT INTO debet_accounts(debet_acc_no, debet_acc_name, debet_acc_owner, is_active) VALUES(?, ?, ?, ?)";
                    PreparedStatement pstmt = sqlConnection.getConnection().prepareStatement(query);
                    pstmt.setString(1, debet_acc_no);
                    pstmt.setString(2, debet_acc_name);
                    pstmt.setString(3, owner);
                    pstmt.setString(4, is_active);

                    success = pstmt.executeUpdate() > 0;
                } catch (SQLException e) {
                    logger.error("SQLException: " + e.getMessage());
                } finally {
                    sqlConnection.closeConnection();
                }
            }
            
            output = new HashMap();
            output.put("status", success ? "success" : "attention");
            output.put("message", String.format(ctx.getMessage(success ? "debet_accounts.save_succeed" : "debet_accounts.save_failed"), debet_acc_no));
        } else {
            output.put("method", "add");
            out = this.renderForm(request, output);

            output = new HashMap();
            output.put("status", out.length() > 0 ? "success" : "attention");
            output.put("message", out.length() > 0 ? "" : ctx.getMessage("debet_accounts.account_not_found"));
            output.put("content", out);
        }
        
        JSONSerializer serializer = new JSONSerializer();
        String jsonString = serializer.deepSerialize(output);
        
        return jsonString;
    }
    
    @RequestMapping(value = "edit", method = {RequestMethod.POST})
    public @ResponseBody String edit(@RequestParam(value="debet_acc_id", required=false, defaultValue="") String debet_acc_id, HttpServletRequest request) {
        HashMap output = new HashMap();
        String out = "";
        
        RequestContext ctx = new RequestContext(request);
        
        Map pPost = request.getParameterMap();
        
        LoginService loginService = new LoginService();
        HashMap userData = loginService.getUserSession(request);
        String owner = (String) userData.get("userCabang");
        
        if (pPost.containsKey("submit")) {
            boolean success = false;
            String debet_acc_no = ((String[]) pPost.get("debet_acc_no"))[0];
            String debet_acc_name = ((String[]) pPost.get("debet_acc_name"))[0];
            String is_active = ((String[]) pPost.get("is_active"))[0];
            
            SQLConnectionService sqlConnection = new SQLConnectionService("jdbc");
            if (sqlConnection.createConnection()) {
                try {
                    String query = "UPDATE debet_accounts SET debet_acc_no = ?, debet_acc_name = ?, is_active = ? WHERE debet_acc_id = ? AND debet_acc_owner = ?";
                    PreparedStatement pstmt = sqlConnection.getConnection().prepareStatement(query);
                    pstmt.setString(1, debet_acc_no);
                    pstmt.setString(2, debet_acc_name);
                    pstmt.setString(3, is_active);
                    pstmt.setString(4, debet_acc_id);
                    pstmt.setString(5, owner);

                    success = pstmt.executeUpdate() > 0;
                } catch (SQLException e) {
                    logger.error("SQLException: " + e.getMessage());
                } finally {
                    sqlConnection.closeConnection();
                }
            }
            
            output = new HashMap();
            output.put("status", success ? "success" : "attention");
            output.put("message", String.format(ctx.getMessage(success ? "debet_accounts.update_succeed" : "debet_accounts.update_failed"), debet_acc_no));
        } else {
            SQLConnectionService sqlConnection = new SQLConnectionService("jdbc");
            if (sqlConnection.createConnection()) {
                try {
                    String query = "SELECT * FROM debet_accounts WHERE debet_acc_id = ? AND debet_acc_owner = ?";
                    PreparedStatement pstmt = sqlConnection.getConnection().prepareStatement(query);
                    pstmt.setString(1, debet_acc_id);
                    pstmt.setString(2, owner);

                    ResultSet rs = pstmt.executeQuery();
                    if (rs.next()) {
                        output.put("debet_acc_id", rs.getString("debet_acc_id"));
                        output.put("debet_acc_no", rs.getString("debet_acc_no"));
                        output.put("debet_acc_name", rs.getString("debet_acc_name"));
                        output.put("is_active", rs.getString("is_active"));
                        output.put("method", "edit");

                        out = this.renderForm(request, output);
                    }
                } catch (SQLException e) {
                    logger.error("SQLException: " + e.getMessage());
                } finally {
                    sqlConnection.closeConnection();
                }
            }

            output = new HashMap();
            output.put("status", out.length() > 0 ? "success" : "attention");
            output.put("message", out.length() > 0 ? "" : ctx.getMessage("debet_accounts.account_not_found"));
            output.put("content", out);
        }
        
        JSONSerializer serializer = new JSONSerializer();
        String jsonString = serializer.deepSerialize(output);
        
        return jsonString;
    }
    
    @RequestMapping(value = "remove", method = {RequestMethod.POST})
    public @ResponseBody String remove(@RequestParam(value="debet_acc_id", required=false, defaultValue="") String debet_acc_id,
                                        @RequestParam(value="debet_acc_no", required=false, defaultValue="") String debet_acc_no,
                                        HttpServletRequest request) {
        HashMap output = new HashMap();
        String out = "";
        boolean success = false;
        
        RequestContext ctx = new RequestContext(request);
        
        LoginService loginService = new LoginService();
        HashMap userData = loginService.getUserSession(request);
        String owner = (String) userData.get("userCabang");
        
        SQLConnectionService sqlConnection = new SQLConnectionService("jdbc");
        if (sqlConnection.createConnection()) {
            try {
                String query = "DELETE FROM debet_accounts WHERE debet_acc_id = ? AND debet_acc_owner = ?";
                PreparedStatement pstmt = sqlConnection.getConnection().prepareStatement(query);
                pstmt.setString(1, debet_acc_id);
                pstmt.setString(2, owner);

                success = pstmt.executeUpdate() > 0;
            } catch (SQLException e) {
                logger.error("SQLException: " + e.getMessage());
            } finally {
                sqlConnection.closeConnection();
            }

            output = new HashMap();
            output.put("status", success ? "success" : "attention");
            output.put("message", success ? String.format(ctx.getMessage("debet_accounts.account_removed"), debet_acc_no) : ctx.getMessage("debet_accounts.account_not_found"));
        }
        
        JSONSerializer serializer = new JSONSerializer();
        String jsonString = serializer.deepSerialize(output);
        
        return jsonString;
    }
    
    private String renderForm(HttpServletRequest request, HashMap output) {
        String out = "";
        
        try {
            Locale locale = new java.util.Locale("id", "ID");
            String requestedLocale = (String) request.getAttribute("locale");

            // If the user passes in a ?locale=foo line, parse it and 
            // use that as a locale
            if(requestedLocale != null && StringUtils.hasText(requestedLocale)) {
                if(requestedLocale.contains("_")) {
                    String[] parts = requestedLocale.split("_", 2);
                    locale = new Locale(parts[0], parts[1]);
                } else {
                    locale = new Locale(requestedLocale);
                }
            }

            SwallowingJspRenderer jspRenderer = new SwallowingJspRenderer();
            jspRenderer.setServletContext(request.getServletContext());
            out = jspRenderer.render("debet-accounts/form", output, locale);
        } catch (Exception e) {
            logger.error(e.getStackTrace());
        }
        
        return out;
    }
    
}
