            <%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
            <%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
            <form name="frmSupervisor" action="supervisors" method="post" class="required-form">
                <div>
                    <p>
                        <label for="supervisor_code"><spring:message code="supervisors.supervisor_code" /></label><br/>
                        <input type="text" name="supervisor_code" id="supervisor_code" value="<c:out value="${data.supervisor_code}" />" class="uniform" />
                        <span class="required-icon tooltip"><spring:message code="global.required" /></span>
                    </p>
                    <p>
                        <label for="supervisor_pass"><spring:message code="supervisors.supervisor_pass" /></label><br/>
                        <input type="password" name="supervisor_pass" id="supervisor_pass" value="<c:out value="${data.supervisor_pass}" />" class="uniform" />
                        <span class="required-icon tooltip"><spring:message code="global.required" /></span>
                    </p>
                    <p class="clr float-left">
                        <input type="submit" name="submit" id="submit" value="<spring:message code="supervisors.submit" />" class="uniform" />
                        <input type="button" name="cancel" id="cancel" value="<spring:message code="supervisors.cancel" />" class="uniform" />
                    </p>
                </div>
            </form>
