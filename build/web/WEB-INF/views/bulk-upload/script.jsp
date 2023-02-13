    <%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
    <%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>
    <script type="text/javascript" src="resources/<c:out value="${layout}" />/js/date.format.js"></script>
    <script type="text/javascript" src="resources/<c:out value="${layout}" />/js/jquery.timer.js"></script>
    <script type="text/javascript">
        var oTable;
        var giRedraw = false;
        var amt = 0;
        var numUploadRows = 0;
        var numUploadComplete = 0;
        var numUploadFailed = 0;
        var numUploadSkipped = 0;
        var uploadRows = null;
        var uploadErrorLog = '';
        var uploadSkippedLog = '';
        var startstopTimer, startstopCurrent, startstopOutput;
        (function($) {
            $(function () {
                $('body').append('<div id="upload-log-dialog"></div>');
                var $upload_log = $('<div id="upload-log-dialog"></div>').dialog({
                    autoOpen: false,
                    resizable: true,
                    modal: true,
                    closeOnEscape: true,
                    position: ['center', 'center'],
                    width: 480,
                    height: 220,
                    resize: function(event, ui) {
                        $(this).children('div.css-scroll').css('height', ($(this).height() - 4) + 'px');
                    }
                });
                
                $('#sundry-table tbody').livequery('click', function(event) {
                    $(oTable.fnSettings().aoData).each(function (){
                        $(this.nTr).removeClass('row_selected');
                    });
                    $(event.target.parentNode).addClass('row_selected');
                });
                
                $('#delete-item').livequery('click', function() {
                    var anSelected = fnGetSelected(oTable);
                    if (anSelected.length == 0) {
                        alert('<fmt:message key="bulk_upload.delete_no_selected" />');
                        return;
                    }
                    
                    if (confirm('<fmt:message key="bulk_upload.delete_confirm" />')) {
                        var row = oTable.fnGetData(anSelected[0]);
                        amt -= parseInt($.parseNumber(row[7]));
                        
                        oTable.fnDeleteRow(anSelected[0]);
                        
                        displayJurnal();
                    }
                });
                
                $('#get-items').livequery('click', function() {
                    var nNodes = oTable.fnGetData();
                    for (var i = 0; i < nNodes.length; i++) {
                        alert(nNodes[i].length);
                    }
                });
                
                $('#sundry-table').livequery(function () {
                    var aoColumnDefs = [{'bSearchable': false, 'bVisible': false, 'aTargets': [5]}];
                    if ($('#txtAccount').length > 0) {
                        aoColumnDefs = [
                            {'bSearchable': false, 'bVisible': false, 'aTargets': [1]},
                            {'bSearchable': false, 'bVisible': false, 'aTargets': [2]},
                            {'bSearchable': false, 'bVisible': false, 'aTargets': [5]}
                        ];
                    }
                    
                    oTable = $(this).dataTable({
                        'bJQueryUI': true,
                        'bSort': false,
                        'sPaginationType': 'full_numbers',
                        'aLengthMenu': [[10, 25, 50, 100], [10, 25, 50, 100]],
                        'aoColumnDefs': aoColumnDefs,
                        'fnRowCallback': function(nRow, aData, iDisplayIndex, iDisplayIndexFull) {
                            var index = iDisplayIndex + 1;
                            $('td:eq(0)',nRow).html(index);
                            return nRow;
                        }
                    });
                });
                
                var oldValue = null;
                $('td:eq(1), td:eq(3), td:eq(5), td:eq(6), td:eq(7), td:eq(8), td:eq(9)', '#sundry-table tbody tr').livequery(function() {
                    $(this).editable('index/validate-sundry', {
                        'method': 'POST',
                        'event': 'dblclick',
                        'style': 'inherit',
                        'onblur': 'submit',
                        'ajaxoptions': {'dataType': 'json'},
                        'indicator': '<img src="resources/<c:out value="${layout}" />/img/mini-ajax-loader.gif" />',
                        'callback': function(value, settings) {
                            var aPos = oTable.fnGetPosition(this);
                            if (value['status'] == 'success') {
                                var row = value['message'].split('|');
                                oTable.fnUpdate([null, row[6], row[7], row[0], row[1], row[2], row[3], $.formatNumber(row[4], {format: '#,###', locale: 'us', nanForceZero: false}), row[5], row[8], row[9]], aPos[0], 0);
                                if (aPos[1] == 6) {
                                    amt = (amt - parseFloat(oldValue)) + parseFloat(row[4]);
                                    displayJurnal();
                                }
                            } else {
                                $('.notification > a.close').trigger('click');
                                var notification = '<div class="closable notification ' + value['status'] + '">'+value['message']+'<a class="close" href="#">close</a></div>';
                                $('#content').before(notification);
                                $('.notification').fadeIn('normal');
                                oTable.fnUpdate(oldValue, aPos[0], aPos[1]);
                            }
                        },
                        'submitdata': function(value, settings) {
                            var aPos = oTable.fnGetPosition(this);
                            var row = oTable.fnGetData(aPos[0]);
                            var newVal = $(this).find('form').find('input[name="value"]').val();
                            oldValue = value;
                            return {
                                'txtAccount': aPos[1] == 1 ? newVal : row[1],
                                'txtAccountCR': aPos[1] == 3 ? newVal : row[3],
                                'txtTransAmount': aPos[1] == 6 ? newVal : row[7],
                                'txtDesc': aPos[1] == 7 ? newVal : row[8],
                                'txtKodeKantor': aPos[1] == 8 ? newVal : row[9],
                                'txtUser': aPos[1] == 9 ? newVal : row[10],
                                'txtNoRef': aPos[1] == 10 ? newVal : row[11],
                                'mode': $('#txtAccount').length > 0 ? 'OTM' : 'OTO'
                            };
                        },
                        'height': '14px'
                    });
                });
                
                $('#upload-error-log, #upload-skipped-log').livequery('click', function () {
                    $upload_log
                        .html('<div style="margin-bottom: 5px; text-align: right;"><a class="export-log" href="index/export-log"><fmt:message key="bulk_upload.export" /></a></div>' + ($(this).attr('id') == 'upload-error-log' ? uploadErrorLog : uploadSkippedLog))
                        .dialog('option', 'width', 480)
                        .dialog('option', 'height', 220)
                        .dialog('option', 'position', ['center', 'center'])
                        .dialog('option', 'title', $(this).attr('original-title'))
                        .dialog('open');
                });
                
                $('form[name="frmUploadItems"]').livequery('submit', function() {
                    var form = $(this);
                    
                    $('.notification > a.close').trigger('click');
                    
                    if (!form.valid()) return false;
                    form.append('<input type="hidden" name="mode" value="' + ($('#txtAccount').length > 0 ? 'OTM' : 'OTO') + '" />');
                    
                    startstopOutput = '#upload-progress-timer';
                    startstopTimer.play();
                    
                    form.ajaxSubmit({
                        dataType: 'json',
                        success: function(data) {
                            if(data.status == 'success') {
                                $('#upload-progress').parent().show();
                                $('.ui-dialog-titlebar-close').hide();
                                $('#ajax-dialog').dialog('option', 'closeOnEscape', false);
                                $('#upload').attr('disabled', true);
                                $.uniform.update('#upload');
                                
                                numUploadRows = 0;
                                for (o in data.rows) {
                                    if (data.rows.hasOwnProperty(o)) {
                                        numUploadRows++;
                                    }
                                }
                                
                                if (numUploadRows > 0) {
                                    numUploadComplete = 0;
                                    numUploadFailed = 0;
                                    numUploadSkipped = 0;
                                    uploadErrorLog = '';
                                    uploadRows = data.rows;
                                    
                                    uploadProcess(0);
                                }
                            } else {
                                //prepare the html notification
                                var notification = '<div class="closable notification ' + data.status + '">'+data.message+'<a class="close" href="#">close</a></div>';

                                // add the notification message to the DOM
                                form.before(notification);
                            }

                            $('.notification').fadeIn('normal');
                        }
                    });

                    return false;
                });
                
                $('form[name="frmBulkUpload"]').livequery('submit', function() {
                    jConfirm('<fmt:message key="bulk_upload.submit_confirm" />', '<fmt:message key="bulk_upload.submit_title_confirm" />', function(r) {
                        if (r) {
                            $('.notification > a.close').trigger('click');

                            numUploadComplete = 0;
                            numUploadFailed = 0;
                            numUploadSkipped = 0;
                            uploadErrorLog = '';
                            uploadRows = oTable.fnGetData();
                            numUploadRows = uploadRows.length;

                            if (numUploadRows < 1) {
                                alert('<fmt:message key="bulk_upload.empty_jurnal" />');
                                return false;
                            } else if (amt == 0) {
                                alert('<fmt:message key="bulk_upload.empty_amount" />');
                                return false;
                            }
                            
                            startstopOutput = '#submit-progress-timer';
                            startstopTimer.play();
                            
                            $('#log-success table tbody').html('');

                            $('#submit-progress').parent().show();
                            $('#datatable-actions').hide();
                            $('#submit, #clear, #cancel').attr('disabled', true);
                            $.uniform.update('#submit, #clear, #cancel');
                            submitProcess(0);
                        }
                    });

                    return false;
                });
                
                $('a.export-log').livequery('click', function() {
                    var now = new Date();
                    var content_elname = $(this).parent().next();
                    var style = '<style language="text/css">ul {margin-left: 0;} li {list-style: none;} body > ul > li {border-bottom: solid 1px #777; margin-bottom: 5px;}</style>';
                    var title = $(this).parent().parent().attr('id') == 'upload-log-dialog' ? $(this).parent().parent().prev().children('span').text() : 'Submit Report';
                    var content = '<html><head><title>' + title + '</title>' + ($(this).parent().parent().attr('id') == 'upload-log-dialog' ? style : '') + '</head><body><h3>' + title + '<div style="font-size: 10px;">' + now.format('dd-mm-yyyy hh:MM:ss') + '</div></h3>' + content_elname.html() + '</body></html>';
                    
                    var f = $('<form method="post" action="' + $(this).attr('href') + '" target="_blank"></form>');
                    var c = $('<textarea name="content"></textarea>');
                    c.val(content);
                    f.append(c);
                    var t = $('<input type="text" name="title" />');
                    t.val(title);
                    f.append(t);
                    f.submit();
                    
                    return false;
                });
                
                $('#clear').livequery('click', function() {
                    $('.notification > a.close').trigger('click');
                    resetForm();
                    $('#log-success table tbody').html('');
                    return false;
                });
                
                $('#cancel').livequery('click', function() {
                    location.reload();
                    return false;
                });
                
                $('form[name="frmIndex"]').livequery('submit', function() {
                    $('.notification > a.close').trigger('click');
                    var form = $(this);
                    
                    $('#content').slideUp('normal', function() {
                        $.post(
                            form.attr('action'),
                            form.serialize(),
                            function(data) {
                                if(data.status == 'success') {
                                    $('#content').html(data.content);
                                } else {
                                    //prepare the html notification
                                    var notification = '<div class="closable notification ' + data.status + '">'+data.message+'<a class="close" href="#">close</a></div>';

                                    // add the notification message to the DOM
                                    $('#content').before(notification);

                                    $('.notification').fadeIn('normal');
                                }
                                $('#content').slideDown('normal');
                            }, 'json'
                        );
                    });

                    return false;
                });
                
                startstopCurrent = 0;
                startstopTimer = $.timer(function() {
                    var hour = parseInt(startstopCurrent / 360000);
                    var min = parseInt(startstopCurrent / 6000);
                    var sec = parseInt(startstopCurrent / 100) - (min * 60);
                    var micro = pad(startstopCurrent - (sec * 100) - (min * 6000), 2);
                    var output = '';
                    
                    if (hour > 0) {
                        output += pad(hour, 2) + ':';
                    } else {
                        output += '00:';
                    }
                    
                    if (min > 0) {
                        output += pad(min, 2) + ':';
                    } else {
                        output += '00:';
                    }
                    
                    if (sec > 0) {
                        output += pad(sec, 2) + '.';
                    } else {
                        output += '00.';
                    }
                    
                    $(startstopOutput).text(output + micro);
                    console.log(startstopOutput + ' = ' + output + micro);
                    startstopCurrent += 7;
                }, 70, true);
            });
        })(jQuery);
                
        var uploadProcess = function (idxRow) {
            var v = uploadRows[idxRow];
            var isOTM = $('#txtAccount').length > 0;
            var isUnique = !$('#isUnique').is(':checked');
            if (isUnique && checkRows(isOTM ? null : v['txtAccount'], v['txtAccountCR']) > 0) {
                numUploadSkipped++;
                uploadSkippedLog += '<li><ul><li>' + v['txtNoRef'] + ', ' + (isOTM ? '' : v['txtAccount'] + ', ') + v['txtAccountCR'] + ', ' + v['txtTransAmount'] + ', ' + v['txtDesc'] + ', ' + v['txtKodeKantor'] + ', ' + v['txtUser'] + '</li></ul></li>';
                uploadNextProcess(idxRow);
            } else {
                $.post(
                    'index/validate-sundry',
                    $.param(v) + '&' + (isOTM ? $('#txtAccount').serialize() : '') + '&mode=' + (isOTM ? 'OTM' : 'OTO'),
                    function(data) {
                        if(data.status == 'success') {
                            var arrOuput = data.message.split('|');
                            $('#sundry-table').dataTable().fnAddData([
                                '',
                                arrOuput[6],
                                arrOuput[7],
                                arrOuput[0],
                                arrOuput[1],
                                arrOuput[2],
                                arrOuput[3],
                                $.formatNumber(arrOuput[4], {format: '#,###', locale: 'us', nanForceZero: false}),
                                arrOuput[5],
                                arrOuput[8],
                                arrOuput[9],
                                arrOuput[10]
                            ]);

                            amt += parseInt(arrOuput[4]);
                            displayJurnal();

                            numUploadComplete++;
                        } else {
                            numUploadFailed++;
                            uploadErrorLog += '<li>' + data.message + '</li>';
                        }

                        uploadNextProcess(idxRow);
                    },
                    'json'
                )
                .error(function(jqXHR, textStatus) {
                    var message = v['txtNoRef'] + ', ' + (isOTM ? '' : v['txtAccount'] + ', ') + v['txtAccountCR'] + ', ' + v['txtTransAmount'] + ', ' + v['txtDesc'] + ', ' + v['txtKodeKantor'] + ', ' + v['txtUser'] + '<br />' + convertAjaxError(jqXHR, textStatus);
                    numUploadFailed++;
                    uploadErrorLog += '<li>' + message + '</li>';

                    uploadNextProcess(idxRow);
                });
            }
        }

        var uploadNextProcess = function (idxRow) {
            var percent = ((idxRow + 1) / numUploadRows) * 100;
            $('#upload-progress').progressbar('value', percent);
            $('#upload-progress-percent').text(parseInt(percent) + '%');
            if ((idxRow + 1) >= numUploadRows) {
                startstopCurrent = 0;
                startstopTimer.stop().once();
                
                $dialog.dialog('close');
                $('#upload-progress').parent().hide();
                $('.ui-dialog-titlebar-close').show();
                $('#ajax-dialog').dialog('option', 'closeOnEscape', true);
                $('#upload').attr('disabled', false);
                $.uniform.update('#upload');

                uploadErrorLog = uploadErrorLog != '' ? '<div class="css-scroll" style="height: 150px;"><ul>' + uploadErrorLog + '</ul></div>' : '';
                uploadSkippedLog = uploadSkippedLog != '' ? '<div class="css-scroll" style="height: 150px;"><ul>' + uploadSkippedLog + '</ul></div>' : '';

                // prepare the html notification
                var notification = '<div class="closable notification ' + (numUploadFailed > 0 ? 'attention' : (numUploadSkipped > 0 ? 'information' : 'success')) + '">' +
                    numUploadRows + ' <fmt:message key="bulk_upload.upload_msg1" /> ' +
                    numUploadComplete + ' <fmt:message key="bulk_upload.upload_msg2" />' +
                    (numUploadFailed > 0 ? ', ' + numUploadFailed + ' <fmt:message key="bulk_upload.upload_msg3" />' + ' <a id="upload-error-log" href="javascript:void(0)" title="<fmt:message key="bulk_upload.upload_error_log" />" class="tooltip"><fmt:message key="bulk_upload.upload_error_log" /></a>' : '') +
                    (numUploadSkipped > 0 ? ', ' + numUploadSkipped + ' <fmt:message key="bulk_upload.upload_msg4" />' + ' <a id="upload-skipped-log" href="javascript:void(0)" title="<fmt:message key="bulk_upload.upload_skipped_log" />" class="tooltip"><fmt:message key="bulk_upload.upload_skipped_log" /></a>' : '') +
                    '<a class="close" href="#">close</a></div>';

                // add the notification message to the DOM
                $('#content').before(notification);

                $('.notification').fadeIn('normal');
            } else {
                if ((idxRow + 1) % 500 === 0) {
                    setTimeout('uploadProcess(' + (idxRow + 1) + ')', 5000);
                } else {
                    uploadProcess(idxRow + 1);
                }
            }
        }

        var checkRows = function(RD, RK) {
            var same = 0;
            var test = false;

            $('#sundry-table tr:has(td)').each(function() {
                test = $(this).find('td').eq(3).text() == RK;
                test = RD != null ? test && $(this).find('td').eq(1).text() == RD : test;
                same = test ? same + 1 : same;
            });

            return same;
        }
                
        var submitProcess = function (idxRow) {
            var v = uploadRows[idxRow];
            var isOTM = $('#txtAccount').length > 0;
            $.post(
                'index/form-post',
                'acccr=' + v[3] + '&accnamecr=' + v[4] + '&amo=' + v[7] + '&dsc=' + v[8] + '&accdb=' + v[1] + '&accnamedb=' + v[2] + '&kodekantor=' + v[9] + '&user=' + v[10] + '&trf=' + v[11] + '&mode=' + (isOTM ? 'OTM' : 'OTO'),
                function(data) {
                    if(data.status == 'success') {
                        numUploadComplete++;
                    } else {
                        numUploadFailed++;
                        uploadErrorLog += '<li>' + data.message + '</li>';
                    }

                    var tolog = '<tr>' +
                        '<td>' + (idxRow+1) + '</td>' +
                        '<td>' + v[1] + '</td>' +
                        '<td>' + v[2] + '</td>' +
                        '<td>' + v[3] + '</td>' +
                        '<td>' + v[4] + '</td>' +
                        '<td>' + v[7] + '</td>' +
                        '<td>' + v[8] + '</td>' +
                        '<td>' + v[9] + '</td>' +
                        '<td>' + v[10] + '</td>' +
                        '<td>' + v[11] + '</td>' +
                        '<td>' + data.status + '</td>' +
                        '</tr>';
                    $('#log-success table tbody').append(tolog);

                    var percent = ((idxRow + 1) / numUploadRows) * 100;
                    $('#submit-progress').progressbar('value', percent);
                    $('#submit-progress-percent').text(parseInt(percent) + '%');
                    if ((idxRow + 1) >= numUploadRows) {
                        startstopCurrent = 0;
                        startstopTimer.stop().once();
                        
                        $('#submit-progress').parent().hide();
                        $('#datatable-actions').show();
                        $('#submit, #clear, #cancel').attr('disabled', false);
                        $.uniform.update('#submit, #clear, #cancel');

                        uploadErrorLog = uploadErrorLog != '' ? '<div class="css-scroll" style="height: 150px;"><ul>' + uploadErrorLog + '</ul></div>' : '';

                        // prepare the html notification
                        var notification = '<div class="closable notification ' + (numUploadFailed > 0 ? 'attention' : 'success') + '">' +
                            numUploadRows + ' <fmt:message key="bulk_upload.upload_msg1" /> ' +
                            numUploadComplete + ' <fmt:message key="bulk_upload.upload_msg2" />' +
                            (numUploadFailed > 0 ? ', ' + numUploadFailed + ' <fmt:message key="bulk_upload.upload_msg3" />.' + ' <a id="upload-error-log" href="javascript:void(0)" title="<fmt:message key="bulk_upload.upload_view_log" />" class="tooltip"><fmt:message key="bulk_upload.upload_view_log" /></a>' : '') +
                            '<a class="close" href="#">close</a></div>';

                        // add the notification message to the DOM
                        $('#content').before(notification);

                        $('.notification').fadeIn('normal', function() {
                            resetForm();
                        });
                    } else {
                        if ((idxRow + 1) % 500 === 0) {
                            setTimeout('submitProcess(' + (idxRow + 1) + ')', 5000);
                        } else {
                            submitProcess(idxRow + 1);
                        }
                    }
                },
                'json'
            )
            .error(function(jqXHR, textStatus) {
                var message = v[11] + ', ' + (isOTM ? '' : v[3] + ', ') + v[4] + ', ' + v[7] + ', ' + v[8] + ', ' + v[1] + ', ' + v[2] + ', ' + v[9] + ', ' + v[10] + '<br />' + convertAjaxError(jqXHR, textStatus);
                numUploadFailed++;
                uploadErrorLog += '<li>' + message + '</li>';

                submitProcess(idxRow + 1);
            });
        }
        
        function fnGetSelected(oTableLocal) {
            var aReturn = new Array();
            var aTrs = oTableLocal.fnGetNodes();

            for (var i = 0 ; i < aTrs.length ; i++) {
                if ($(aTrs[i]).hasClass('row_selected')) {
                    aReturn.push(aTrs[i]);
                }
            }
            return aReturn;
        }
        
        function displayJurnal() {
            var oTable = $('#sundry-table').dataTable();
            var rows = oTable.fnGetData();

            $('#current-items').text(rows.length);
            $('#current-amount').text(amt > 0 ? $.formatNumber(amt, {format: '#,###', locale: 'us', nanForceZero: false}) : '0');
        }
        
        function resetForm() {
            oTable.fnClearTable(0);
            oTable.fnDraw();
            
            numUploadRows = 0;
            numUploadComplete = 0;
            numUploadFailed = 0;
            numUploadSkipped = 0;
            uploadRows = null;
            uploadErrorLog = '';
            uploadSkippedLog = '';

            amt = 0;
            $('#current-items').text('0');
            $('#current-amount').text('0');
        }
        
        function convertAjaxError(jqXHR, textStatus) {
            if (jqXHR.status === 0) {
                return 'Not connect. Verify Network.';
            } else if (jqXHR.status == 404) {
                return 'Requested page not found [404].';
            } else if (jqXHR.status == 500) {
                return 'Internal Server Error [500].';
            } else if (textStatus === 'parsererror') {
                return 'Requested JSON parse failed.';
            } else if (textStatus === 'timeout') {
                return 'Time out error.';
            } else if (textStatus === 'abort') {
                return 'Ajax request aborted.';
            } else {
                return 'Uncaught Error: ' + jqXHR.responseText;
            }
        }
        
        function pad(number, length) {
            var str = '' + number;
            while (str.length < length) {str = '0' + str;}
            return str;
        }
    </script>
