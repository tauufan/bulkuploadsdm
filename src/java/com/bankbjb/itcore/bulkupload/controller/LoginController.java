/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.bankbjb.itcore.bulkupload.controller;

import java.util.HashMap;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.support.RequestContext;

import com.bankbjb.itcore.bulkupload.service.ConfigService;
import com.bankbjb.itcore.bulkupload.service.EquationService;
import com.bankbjb.itcore.bulkupload.service.LoginService;

import flexjson.JSONSerializer;
import org.apache.log4j.Logger;

/**
 *
 * @author C994
 */
@Controller
public class LoginController {
    
    protected final Logger logger = Logger.getLogger(this.getClass());
    
    @RequestMapping(value = "/login", method = {RequestMethod.GET})
    public ModelAndView showForm() {
        ModelAndView modelAndView = new ModelAndView(ConfigService.getProperty("template.path") + ConfigService.getProperty("template.layout") + "/login-form");
        modelAndView.addObject("pgtitle", "login");

        return modelAndView;
    }
    
    @RequestMapping(value = "/login", method = {RequestMethod.POST}, headers = "x-requested-with=XMLHttpRequest")
    public @ResponseBody String validateCredential(String pageId, HttpSession session, HttpServletRequest request, ModelMap model) {
        String status = "";
        String message = "";
        
        RequestContext ctx = new RequestContext(request);
        
        LoginService loginService = new LoginService();
        boolean loginSuccess = loginService.validateCredential(request);
        
        if (loginSuccess) {
            loginService.setUserSession(request);
            status = "success";
            message = ctx.getMessage("login.success");
        } else {
            status = "error";
            message = ctx.getMessage("login.failed");
        }
        
        HashMap outputObject = new HashMap();
        outputObject.put("status", status);
        outputObject.put("message", message);

        JSONSerializer serializer = new JSONSerializer();
        String jsonString = serializer.serialize(outputObject);
        
        return jsonString;
    }
    
    @RequestMapping(value = "/login", method = {RequestMethod.POST})
    public void validateCredential(String pageId, HttpSession session, HttpServletRequest request, HttpServletResponse response, ModelMap model) {
        String status = "";
        String message = "";
        String redirect = "";
        
        RequestContext ctx = new RequestContext(request);
        
        LoginService loginService = new LoginService();
        boolean loginSuccess = loginService.validateCredential(request);
        
        if (loginSuccess) {
            loginService.setUserSession(request);
            status = "success";
            message = ctx.getMessage("login.success");
            redirect = "index";
        } else {
            status = "error";
            message = ctx.getMessage("login.failed");
            redirect = "login";
            
            try {
                session.setAttribute("once.status", status);
                session.setAttribute("once.message", message);
            } catch (Exception e) {
                logger.error("set temporary session failed: " + e.getStackTrace());
            }
        }
        
        try {
            response.sendRedirect(request.getContextPath() + "/" + redirect);
        } catch (Exception e) {
            logger.error("redirect error: " + e.getStackTrace());
        }
    }
    
    @RequestMapping(value = "/logout", method = {RequestMethod.POST})
    public @ResponseBody String clearCredential(String pageId, HttpSession session, HttpServletRequest request, ModelMap model) {
        String status = "";
        String message = "";
        
        RequestContext ctx = new RequestContext(request);
        
        try {
            LoginService loginService = new LoginService();
            loginService.clearUserSession(request);
            
            if (EquationService.getEQTransaction(false) != null) {
                EquationService.getEQTransaction().getSession().logOff();
                EquationService.setEQTransaction(null);
            }
            
            status = "success";
            message = ctx.getMessage("logout.success");
        } catch (Exception e) {
            status = "error";
            message = ctx.getMessage("logout.failed");
        }
        
        HashMap outputObject = new HashMap();
        outputObject.put("status", status);
        outputObject.put("message", message);

        JSONSerializer serializer = new JSONSerializer();
        String jsonString = serializer.serialize(outputObject);
        
        return jsonString;
    }
    
    @RequestMapping(value = "/logout", method = {RequestMethod.GET})
    public void clearCredential(String pageId, HttpSession session, HttpServletRequest request, HttpServletResponse response, ModelMap model) {
        String redirect = "";
        String status = "";
        String message = "";
        
        RequestContext ctx = new RequestContext(request);

        try {
            LoginService loginService = new LoginService();
            loginService.clearUserSession(request);
            
            if (EquationService.getEQTransaction(false) != null) {
                EquationService.getEQTransaction().getSession().logOff();
                EquationService.setEQTransaction(null);
            }
            
            redirect = "login";
            status = "success";
            message = ctx.getMessage("logout.success");
        } catch (Exception e) {
            redirect = "login";
            status = "error";
            message = ctx.getMessage("logout.failed");
        }
        
        try {
            session.setAttribute("once.status", status);
            session.setAttribute("once.message", message);
        } catch (Exception e) {
            logger.error("set temporary session failed: " + e.getStackTrace());
        }
        
        try {
            response.sendRedirect(request.getContextPath() + "/" + redirect);
        } catch (Exception e) {
            logger.error("redirect error: " + e.getStackTrace());
        }
    }
    
}
