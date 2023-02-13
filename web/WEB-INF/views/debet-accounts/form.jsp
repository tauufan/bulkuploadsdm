            <%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
            <%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
            <form name="frmDebetAccount" action="debet-accounts/<c:out value="${method}" />" method="post" class="required-form">
                <div>
                    <p>
                        <label for="debet_acc_no"><spring:message code="debet_accounts.debet_acc_no" /></label><br/>
                        <input type="text" name="debet_acc_no" id="debet_acc_no" value="<c:out value="${debet_acc_no}" />" class="uniform" />
                        <span class="required-icon tooltip"><spring:message code="global.required" /></span>
                    </p>
                    <p>
                        <label for="debet_acc_name"><spring:message code="debet_accounts.debet_acc_name" /></label><br/>
                        <input type="text" name="debet_acc_name" id="debet_acc_name" value="<c:out value="${debet_acc_name}" />" class="uniform" />
                        <span class="required-icon tooltip"><spring:message code="global.required" /></span>
                    </p>
                    <p>
                        <label for="is_active"><spring:message code="debet_accounts.is_active" /></label><br/>
                        <select id="is_active" name="is_active" size="1">
                            <option value="1"<c:if test='${is_active == "1"}'> selected="selected"</c:if>><spring:message code="debet_accounts.active" /></option>
                            <option value="0"<c:if test='${is_active == "0"}'> selected="selected"</c:if>><spring:message code="debet_accounts.inactive" /></option>
                        </select>
                        <span class="required-icon tooltip"><spring:message code="global.required" /></span>
                    </p>
                    <p class="clr float-left">
                        <input type="submit" name="submit" id="submit" value="<spring:message code="debet_accounts.submit" />" class="uniform" />
                        <input type="button" name="cancel" id="cancel" value="<spring:message code="debet_accounts.cancel" />" class="uniform" />
                    </p>
                </div>
                <input type="hidden" name="debet_acc_id" value="<c:out value="${debet_acc_id}" />" />
            </form>
