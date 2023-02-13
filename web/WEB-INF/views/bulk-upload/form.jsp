            <%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
            <%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
            <form name="frmBulkUpload" action="index/form-post" method="post" class="required-form">
                <div>
                    <c:if test="${mode == 'OTM'}">
                    <p>
                        <label for="txtAccount"><spring:message code="bulk_upload.account_no" /></label><br/>
                        <select name="txtAccount" id="txtAccount" size="1">
                            <c:forEach items="${reks}" var="rek">
                            <option value="${rek['rekening']}"><c:out value="${rek['rekening']}" /> - <c:out value="${rek['namaNasabah']}" /></option>
                            </c:forEach>
                        </select>
                        <span class="required-icon tooltip float-left" style="margin-left: -16px;"><spring:message code="global.required" /></span>
                    </p>
                    </c:if>
                    <p>
                        <label for="isUnique"><input type="checkbox" name="isUnique" id="isUnique" class="uniform"><spring:message code="bulk_upload.isunique" /></label>
                    </p>
                    <div id="datatable-actions">
                        <a href="index/upload-form" id="upload-items" class="tooltip modal-dialog" title="<spring:message code="bulk_upload.upload_excel" />"><spring:message code="bulk_upload.upload_excel" /></a> |
                        <a href="javascript:void(0)" id="delete-item" class="tooltip" title="<spring:message code="bulk_upload.delete_item" />"><spring:message code="bulk_upload.delete_item" /></a>
                    </div>
                    <table id="sundry-table" cellpadding="0" cellspacing="0" border="0" class="display">
                        <thead>
                            <tr>
                                <th width="10"><spring:message code="bulk_upload.item" /></th>
                                <th width="120"><spring:message code="bulk_upload.account_no" /></th>
                                <th><spring:message code="bulk_upload.short_name" /></th>
                                <th width="120"><spring:message code="bulk_upload.account_no_cr" /></th>
                                <th><spring:message code="bulk_upload.short_name_cr" /></th>
                                <th width="160"><spring:message code="bulk_upload.tcd" /></th>
                                <th width="50"><spring:message code="bulk_upload.ccy" /></th>
                                <th width="100"><spring:message code="bulk_upload.amount" /></th>
                                <th><spring:message code="bulk_upload.desc" /></th>
                                <th width="100"><spring:message code="bulk_upload.kodekantor" /></th>
                                <th width="100"><spring:message code="bulk_upload.user" /></th>
                                <th width="100"><spring:message code="bulk_upload.no_ref" /></th>
                            </tr>
                        </thead>
                        <tbody></tbody>
                    </table>
                                
                    <hr style="margin-top: 20px;" />
                    
                    <div class="float-left" style="width: 120px;">
                        <p>&nbsp;</p>
                        <p><label><spring:message code="bulk_upload.totals" /></label></p>
                    </div>
                    <div class="float-left" style="width: 160px;">
                        <p><label><spring:message code="bulk_upload.items" /></label></p>
                        <p id="current-items" style="padding-right: 32px;">0</p>
                    </div>
                    <div class="float-left" style="width: 160px;">
                        <p><label><spring:message code="bulk_upload.amount" /></label></p>
                        <p id="current-amount" style="padding-right: 32px;">0</p>
                    </div>
                    <p class="clr float-left">
                        <input type="submit" name="submit" id="submit" value="<spring:message code="bulk_upload.submit" />" class="uniform" />
                        <input type="button" name="clear" id="clear" value="<spring:message code="bulk_upload.clear" />" class="uniform" />
                        <input type="button" name="cancel" id="cancel" value="<spring:message code="bulk_upload.cancel" />" class="uniform" />
                    </p>
                    <div class="float-left" style="padding-left: 20px; display: none;">
                        <div><spring:message code="global.loading" /> <span id="submit-progress-percent"></span> - <span id="submit-progress-timer"></span></div>
                        <div id="submit-progress" class="progressbar" style="height: 15px; width: 200px;"></div>
                    </div>
                </div>
            </form>

            <div class="clr float-right" style="margin-bottom: 5px;"><a class="export-log" href="index/export-log"><spring:message code="bulk_upload.export" /></a></div>
            <div id="log-success" class="clr" style="width: 100%; max-height: 200px; overflow: auto;">
                <table cellpadding="2" cellspacing="0" border="1" style="width: 100%; font-size: 9px;">
                    <thead>
                        <tr>
                            <th width="10"><spring:message code="bulk_upload.item" /></th>
                            <th width="120"><spring:message code="bulk_upload.account_no" /></th>
                            <th><spring:message code="bulk_upload.short_name" /></th>
                            <th width="120"><spring:message code="bulk_upload.account_no_cr" /></th>
                            <th><spring:message code="bulk_upload.short_name_cr" /></th>
                            <th width="100"><spring:message code="bulk_upload.amount" /></th>
                            <th><spring:message code="bulk_upload.desc" /></th>
                            <th width="80"><spring:message code="bulk_upload.kodekantor" /></th>
                            <th width="80"><spring:message code="bulk_upload.user" /></th>
                            <th width="80"><spring:message code="bulk_upload.no_ref" /></th>
                            <th width="80"><spring:message code="bulk_upload.status" /></th>
                        </tr>
                    </thead>
                    <tbody></tbody>
                </table>
            </div>
