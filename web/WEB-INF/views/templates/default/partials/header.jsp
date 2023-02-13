            <%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>
            <%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
            <div id="header">

                <div class="top-banner"></div>

                <div class="fg-toolbar ui-toolbar ui-widget-header ui-corner-tl ui-corner-tr ui-helper-clearfix ui-corner-all top-bar">

                    <div class="menu-bar float-left">
                        
                        <c:set var="menu_1" value="" />
                        <c:if test="${activeMenu == 'dashboard'}">
                            <c:set var="menu_1" value="active" />
                        </c:if>
                        <c:set var="menu_2" value="" />
                        <c:if test="${activeMenu == 'deposito-balance' || activeMenu == 'giro-balance' || activeMenu == 'account-balance'}">
                            <c:set var="menu_2" value="active" />
                        </c:if>
                        <c:set var="menu_21" value="" />
                        <c:if test="${activeMenu == 'deposito-balance'}">
                            <c:set var="menu_21" value="active" />
                        </c:if>
                        <c:set var="menu_22" value="" />
                        <c:if test="${activeMenu == 'giro-balance'}">
                            <c:set var="menu_22" value="active" />
                        </c:if>
                        <c:set var="menu_23" value="" />
                        <c:if test="${activeMenu == 'account-balance'}">
                            <c:set var="menu_23" value="active" />
                        </c:if>
                        <c:set var="menu_3" value="" />
                        <c:if test="${activeMenu == 'interbank-transfer'}">
                            <c:set var="menu_3" value="active" />
                        </c:if>
                        <c:set var="menu_5" value="" />
                        <c:if test="${activeMenu == 'standing-order'}">
                            <c:set var="menu_5" value="active" />
                        </c:if>
                        <c:set var="menu_6" value="" />
                        <c:if test="${activeMenu == 'balance-order'}">
                            <c:set var="menu_6" value="active" />
                        </c:if>
                        <c:set var="menu_4" value="" />
                        <c:if test="${activeMenu == 'bulk-upload'}">
                            <c:set var="menu_4" value="active" />
                        </c:if>

                        <ul id="nav">
                            <li class="<c:out value="${menu_1}" />">
                                <a href="dashboard"><fmt:message key="menus.dashboard" /></a>
                            </li>
                            <li class="<c:out value="${menu_2}" />">
                                <a href="javascript:void(0)"><fmt:message key="menus.account_balance_wrap" /></a>
                                <ul>
                                    <li class="<c:out value="${menu_21}" />"><a href="deposito-balance"><fmt:message key="menus.deposito_balance" /></a></li>
                                    <li class="<c:out value="${menu_22}" />"><a href="giro-balance"><fmt:message key="menus.giro_balance" /></a></li>
                                    <li class="<c:out value="${menu_23}" />"><a href="account-balance"><fmt:message key="menus.account_balance" /></a></li>
                                </ul>
                            </li>
                            <li class="<c:out value="${menu_3}" />">
                                <a href="interbank-transfer"><fmt:message key="menus.interbank_transfer" /></a>
                            </li>
                            <li class="<c:out value="${menu_5}" />">
                                <a href="standing-order"><fmt:message key="menus.standing_order" /></a>
                            </li>
                            <li class="<c:out value="${menu_6}" />">
                                <a href="balance-order"><fmt:message key="menus.balance_order" /></a>
                            </li>
                            <li class="<c:out value="${menu_4}" />">
                                <a href="bulk-upload"><fmt:message key="menus.bulk_upload" /></a>
                            </li>
                        </ul>

                    </div>

                    <div class="float-right" style="margin-right: 10px;">
                        <a id="logout" href="logout" title="<fmt:message key="menus.logout" />"><fmt:message key="menus.logout" /></a>
                    </div>

                </div>

            </div>