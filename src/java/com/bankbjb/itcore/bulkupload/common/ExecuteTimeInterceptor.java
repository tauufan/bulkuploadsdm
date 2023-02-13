/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.bankbjb.itcore.bulkupload.common;

import java.util.HashMap;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import com.bankbjb.itcore.bulkupload.service.ConfigService;
import com.bankbjb.itcore.bulkupload.service.LoginService;

import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;

import org.apache.log4j.Logger;
import org.springframework.web.servlet.support.RequestContext;

/**
 *
 * @author C994
 */
public class ExecuteTimeInterceptor extends HandlerInterceptorAdapter {
    
    private static final Logger logger = Logger.getLogger(ExecuteTimeInterceptor.class);
    
    /**
     * In this case intercept the request BEFORE it reaches the controller
     */
    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        try {
            ConfigService.setConfigPath(request.getServletContext().getRealPath("") + "/WEB-INF/configs/custom.properties");
            
            if (!request.getServletPath().contains(ConfigService.getProperty("resources.path"))) {
                // cek session
                if (!request.getServletPath().equalsIgnoreCase("/logout")) {
                    LoginService loginService = new LoginService();
                    boolean haveSession = loginService.checkUserSession(request);
                    String className = ((HandlerMethod) handler).getBean().getClass().getSimpleName();

                    if (!className.equalsIgnoreCase("LoginController")) {
                        if (!haveSession) {
                            logger.warn("Intercepting: credential required");
                            if ("XMLHttpRequest".equals(request.getHeader("X-Requested-With"))) {
                                response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "SESSION_TIMED_OUT");
                            } else {
                                response.sendRedirect(request.getContextPath() + "/login");
                            }
                            return false;
                        }

                        if (className.equalsIgnoreCase("DebetAccountController") && haveSession) {
                            HashMap userData = loginService.getUserSession(request);
                            String role = (String) userData.get("userRole");
                            if (!role.equalsIgnoreCase("supervisor")) {
                                RequestContext ctx = new RequestContext(request);

                                HttpSession session = request.getSession(false);
                                session.setAttribute("once.status", "attention");
                                session.setAttribute("once.message", String.format(ctx.getMessage("global.page_not_authorized"), ctx.getMessage("menus.debet_accounts")));

                                response.sendRedirect(request.getContextPath() + "/index");
                                return false;
                            }
                        }
                    } else {
                        if (haveSession) {
                            response.sendRedirect(request.getContextPath() + "/index");
                            return false;
                        }
                    }
                }

                logger.info("Intercepting: " + request.getRequestURI());

                long startTime = System.currentTimeMillis();
                request.setAttribute("startTime", startTime);

                //log it
                if(logger.isDebugEnabled()){
                   logger.debug("[" + handler + "] startTime : " + startTime);
                }

                // Do some changes to the incoming request object
                updateRequest(request);
            }

            return true;
        } catch (SystemException e) {
            logger.info("request update failed");
            return false;
        }
    }

    // after the handler is executed
    @Override
    public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler, ModelAndView modelAndView) throws Exception {
        response.setHeader("Cache-Control", "no-cache,no-store,must-revalidate");
        response.setHeader("Pragma", "no-cache");
        response.setDateHeader("Expires", 0);
        
        if (!request.getServletPath().contains(ConfigService.getProperty("resources.path"))) {
            logger.info("postHandle: " + request.getRequestURI());

            try {
                if (modelAndView != null) {
                    HttpSession session = request.getSession(false);
                    if (session != null) {
                        if (session.getAttribute("once.status") != null) {
                            modelAndView.addObject("status", session.getAttribute("once.status"));
                            session.setAttribute("once.status", null);
                        }
                        if (session.getAttribute("once.message") != null) {
                            modelAndView.addObject("message", session.getAttribute("once.message"));
                            session.setAttribute("once.message", null);
                        }
                    }

                    long startTime = (Long) request.getAttribute("startTime");
                    long endTime = System.currentTimeMillis();
                    long executeTime = endTime - startTime;

                    //modified the existing modelAndView
                    modelAndView.addObject("executeTime", executeTime);

                    //log it
                    if(logger.isDebugEnabled()){
                       logger.debug("[" + handler + "] executeTime : " + executeTime + "ms");
                    }
                }
            } catch (Exception e) {
                logger.error("postHandle: request.getAttribute(\"startTime\") failed");
                logger.error(e.getMessage());
            }
        }
    }

    /**
     * The data added to the request would most likely come from a database
     */
    private void updateRequest(HttpServletRequest request) {
        logger.info("Updating request object");
//        request.setAttribute("custom.properties", "");
    }

    /** This could be any exception */
    private class SystemException extends RuntimeException {
        private static final long serialVersionUID = 1L;
        // Blank
    }
    
}
