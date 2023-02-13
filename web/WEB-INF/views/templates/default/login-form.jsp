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
    <%@include file="partials/meta.jsp" %>
    
    <link rel="stylesheet" type="text/css" href="resources/default/css/simplemodal.basic.css">
    <link rel="stylesheet" type="text/css" href="resources/default/css/jquery.tipsy.css">
    <link rel="stylesheet" type="text/css" href="resources/default/css/login/reset.css">
    <link rel="stylesheet" type="text/css" href="resources/default/css/login/structure.css">
    
    <script type="text/javascript" src="resources/default/js/jquery-1.7.1.js"></script>
    <script type="text/javascript" src="resources/default/js/jquery.simplemodal.js"></script>
    <script type="text/javascript" src="resources/default/js/jquery.livequery.js"></script>
    <script type="text/javascript" src="resources/default/js/jquery.validate.js"></script>
    <script type="text/javascript" src="resources/default/js/jquery.tipsy.js"></script>
    <script type="text/javascript" src="resources/default/js/login.js"></script>
    
    <title><fmt:message key="menus.${pgtitle}" /></title>
</head>
<body>
    <div class="wrapper">
        <div class="banner-bg"></div>
        <div id="divBox">
            <form name="frmLogin" class="box login">
                <c:if test="${!empty status}">
                    <div class="closable notification <c:out value="${status}" default="" />"><c:out value="${message}" default="" /><a class="close" href="#">close</a></div>
                </c:if>
                <fieldset class="boxBody">
                  <label for="username"><fmt:message key="login.username" /></label>
                  <input type="text" name="username" id="username" tabindex="1" placeholder="<fmt:message key="login.username" />" title="<fmt:message key="login.username" />" class="tooltip required">
                  <label for="password"><a href="#" class="rLink" tabindex="5"><fmt:message key="login.forget" /></a><fmt:message key="login.password" /></label>
                  <input type="password" name="password" id="password" tabindex="2" title="<fmt:message key="login.password" />" class="tooltip">
                </fieldset>
                <footer>
                  <input type="submit" class="btnLogin" value="<fmt:message key="login.submit" />" tabindex="4">
                </footer>
            </form>
        </div>
    </div>
    
    <%@include file="partials/footer.jsp" %>
</body>
</html>
