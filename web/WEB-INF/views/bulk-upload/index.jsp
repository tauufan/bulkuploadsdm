            <%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
            <%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>
            <form name="frmIndex" action="index/form" method="get" class="required-form">
                <div>
                    <p><fmt:message key="bulk_upload.mode_question" /></p>
                    <p>
                        <label for="mode1"><input type="radio" name="mode" id="mode1" value="OTO" checked="checked" class="uniform" /><fmt:message key="bulk_upload.mode1" /></label><br/>
                        <label for="mode2"><input type="radio" name="mode" id="mode2" value="OTM" class="uniform" /><fmt:message key="bulk_upload.mode2" /></label><br/>
                        <span class="required-icon tooltip float-left" style="margin-left: -16px;"><fmt:message key="global.required" /></span>
                    </p>
                    <p class="clr float-left">
                        <input type="submit" name="next" id="next" value="<fmt:message key="bulk_upload.next" />" class="uniform" />
                    </p>
                </div>
            </form>
