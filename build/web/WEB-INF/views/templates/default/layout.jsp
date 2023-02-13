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
    <c:set var="layout" value="default" scope="application"/>
    
    <%@include file="partials/meta.jsp" %>
    <%@include file="partials/css.jsp" %>
    <%@include file="partials/js.jsp" %>
    
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

                    <c:if test="${not empty pgdata}">
                    <c:set var="data" value="${pgdata}"/>
                    </c:if>

                    <div id="content" class="block_content">
                        
                        <c:if test="${!empty status}">
                            <div class="closable notification <c:out value="${status}" default="" />"><c:out value="${message}" default="" /><a class="close" href="#">close</a></div>
                        </c:if>

                        <jsp:include page="../../${pgcontent}">
                            <jsp:param name="data" value="${data}" />
                        </jsp:include>

                    </div><!-- .block_content ends -->

                    <div class="bendl"></div>
                    <div class="bendr"></div>
                </div>

            </div>

            <%@include file="partials/footer.jsp" %>

        </div>

    </div>
</body>
</html>
