            <%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>
            <form name="frmUploadItems" action="index/upload-form" method="post" enctype="multipart/form-data" class="required-form">
                <div class="float-left" style="width: 400px;">
                    <p>
                        <label for="txtUploadFile"><fmt:message key="bulk_upload.file_txt" /></label><br/>
                        <span class="float-left"><input type="file" name="txtUploadFile" id="txtUploadFile" size="50" value="" class="uniform tooltip required" title="<fmt:message key="bulk_upload.file_txt" />" /></span>
                        <span class="required-icon tooltip float-left" style="margin-left: -16px;"><fmt:message key="global.required" /></span>
                        <div class="clr"></div>
                    </p>
                    <p>
                        <a href="resources/bulk_template.xls" title="<fmt:message key="bulk_upload.download_template" />"><fmt:message key="bulk_upload.download_template" /></a>
                    </p>
                    <p class="float-left">
                        <input type="submit" name="upload" id="upload" value="<fmt:message key="bulk_upload.upload_btn" />" class="uniform" />
                    </p>
                    <div class="float-left" style="padding-left: 20px; display: none;">
                        <div><fmt:message key="global.loading" /> <span id="upload-progress-percent"></span> - <span id="upload-progress-timer"></span></div>
                        <div id="upload-progress" class="progressbar" style="height: 15px; width: 200px;"></div>
                    </div>
                </div>
            </form>
