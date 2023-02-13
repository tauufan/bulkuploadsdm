        <%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
        <div style="text-align: right;"><a href="debet-accounts/add" class="ajax-link button"><spring:message code="debet_accounts.create_new" /></a></div>
        <div style="margin-top: 10px;">
            <table cellpadding="0" cellspacing="0" border="0" id="debetacc-table" class="display">
                <thead>
                    <tr>
                        <th width="150"><spring:message code="debet_accounts.debet_acc_no" /></th>
                        <th><spring:message code="debet_accounts.debet_acc_name" /></th>
                        <th width="200"><spring:message code="debet_accounts.is_active" /></th>
                        <th width="100"><spring:message code="debet_accounts.action" /></th>
                    </tr>
                </thead>
                <tbody id="result-container"></tbody>
            </table>
        </div>
