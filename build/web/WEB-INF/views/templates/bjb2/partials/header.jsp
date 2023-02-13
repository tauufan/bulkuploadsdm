            <%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>
            <%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
            <%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn" %>
            <div id="header">

                <div class="top-banner"></div>
                
                <c:if test="${!fn:endsWith(requestScope['javax.servlet.forward.servlet_path'], 'login')}">
                <div class="fg-toolbar ui-toolbar ui-widget-header ui-corner-tl ui-corner-tr ui-helper-clearfix ui-corner-all top-bar">
                    <%@include file="navigation.jsp" %>
                </div>
                </c:if>

            </div>