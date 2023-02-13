<%-- 
    BJB Cash Portal
    Copyright © 2012 BJB IT Core Banking
--%>

<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn" %>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
    <head>
        <c:set var="layout" value="bjb2" scope="application"/>

        <%@include file="partials/meta.jsp" %>
        <%@include file="partials/css.jsp" %>
        <%@include file="partials/js.jsp" %>

        <link rel="stylesheet" type="text/css" href="resources/bjb2/css/simplemodal.basic.css">
        <link rel="stylesheet" type="text/css" href="resources/bjb2/css/jquery.tipsy.css">
        <link rel="stylesheet" type="text/css" href="resources/bjb2/css/login/reset.css">
        <link rel="stylesheet" type="text/css" href="resources/bjb2/css/login/structure.css">

        <script type="text/javascript" src="resources/bjb2/js/jquery-1.7.1.js"></script>
        <script type="text/javascript" src="resources/bjb2/js/jquery.simplemodal.js"></script>
        <script type="text/javascript" src="resources/bjb2/js/jquery.livequery.js"></script>
        <script type="text/javascript" src="resources/bjb2/js/jquery.validate.js"></script>
        <script type="text/javascript" src="resources/bjb2/js/jquery.tipsy.js"></script>
        <script type="text/javascript" src="resources/bjb2/js/login.js"></script>

        <script type="text/javascript">
        var LANG_GLOBAL_ALL = '<fmt:message key="global.all" />';
        </script>
        <c:if test="${not empty jsfile}">
            <jsp:include page="../../${jsfile}"></jsp:include>
        </c:if>

        <title><fmt:message key="menus.${pgtitle}" /></title>
    </head>
    <body>
        <div id="frame-body">

            <div id="wrapper">

                <%@include file="partials/header.jsp" %>

                <div id="container">

                    <div class="block">

                        <div class="block_head">

                            <div class="bheadl"></div>
                            <div class="bheadr"></div>

                            <h1><fmt:message key="menus.${pgtitle}" /></h1>

                        </div><!-- .block_head ends -->

                        <div class="block_body" style="position: relative;">
                            
                            <div style="position: absolute; width: 210px; bottom: 20px; left: 0;">
                                <div class="pictcustomer"></div>
                                <div class="pictmitra"></div>
                            </div>

                            <div class="menu-bar float-left" style="width: 210px;">
                                <div class="tulisan">
                                    <p align="center"><fmt:message key="login.apphintmsg" /></p>
                                </div>
                            </div>

                            <c:if test="${not empty pgdata}">
                                <c:set var="data" value="${pgdata}"/>
                            </c:if>

                            <div id="content" class="block_content">

                                <div id="divBox" >
                                    <div class="brandlogin"><fmt:message key="global.appname" /></div>
                                    <div class="clear"></div>
                                    <div align="center" class="tulis" style="padding: 0 0 10px;"><fmt:message key="login.hintmsg" /></div>  
                                    <form name="frmLogin" class="box login" method="post">
                                        <c:if test="${!empty status}">
                                            <div class="closable notification <c:out value="${status}" default="" />"><c:out value="${message}" default="" /><a class="close" href="#">close</a></div>
                                        </c:if>
                                        <fieldset class="boxBody">
                                            <label for="username"><fmt:message key="login.username" /></label>
                                            <input type="text" name="username" id="username" tabindex="1" placeholder="<fmt:message key="login.username" />" title="<fmt:message key="login.username" />" class="tooltip required">
                                            <label for="password"><fmt:message key="login.password" /></label>
                                            <input type="password" name="password" id="password" tabindex="2" title="<fmt:message key="login.password" />" class="tooltip">
                                        </fieldset>
                                        <footer>
                                            <input type="submit" class="btnLogin" value="<fmt:message key="login.submit" />" tabindex="4">
                                        </footer>
                                    </form>
                                </div>

                            </div><!-- .block_content ends -->

                        </div>

                        <div class="bendl"></div>
                        <div class="bendr"></div>
                    </div>

                </div>

                <%@include file="partials/footer.jsp" %>

            </div>

        </div>
    </body>
</html>
