                <%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>
                <%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
                <%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn" %>
                <div class="menu-bar float-left">

                    <c:set var="menu_1" value="" />
                    <c:if test="${activeMenu == 'dashboard'}">
                        <c:set var="menu_1" value="active" />
                    </c:if>
                    <c:set var="menu_2" value="" />
                    <c:if test="${activeMenu == 'debet-accounts'}">
                        <c:set var="menu_2" value="active" />
                    </c:if>
                    <c:set var="menu_3" value="" />
                    <c:if test="${activeMenu == 'supervisors'}">
                        <c:set var="menu_3" value="active" />
                    </c:if>

                    <ul id="nav">
                        <li class="nav-icon-home <c:out value="${menu_1}" />">
                            <a href="index"><fmt:message key="menus.bulk_upload" /></a>
                        </li>
                        <c:if test="${fn:toLowerCase(sessionScope['userData']['userRole']) == 'supervisor'}">
                        <li class="nav-icon-transfer <c:out value="${menu_2}" />">
                            <a href="debet-accounts"><fmt:message key="menus.debet_accounts" /></a>
                        </li>
                        </c:if>
                        <li class="nav-icon-transfer <c:out value="${menu_3}" />">
                            <a href="supervisors"><fmt:message key="menus.supervisors" /></a>
                        </li>
                        <li class="nav-icon-logout">
                            <a id="logout" href="logout" title="<fmt:message key="menus.logout" />"><fmt:message key="menus.logout" /></a>
                        </li>
                    </ul>

                </div>
