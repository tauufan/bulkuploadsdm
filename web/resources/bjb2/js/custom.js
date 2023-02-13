$.metadata.setType('attr', 'validate');
var ajax_modal = true;
var $dialog = null;

(function($) {

    $(function () {
        
        var objid_prompt = '';
        var colidx_prompt = 0;

        $('body').append('<div id="ajax-overlay"><span></span></div>');
        //$('<div id="ajax-overlay"><span></span></div>').insertAfter('#hld');
        
        $dialog = $('<div id="ajax-dialog"></div>').dialog({
            autoOpen: false,
            resizable: true,
            modal: true,
            closeOnEscape: true,
            position: ['center', 'center'],
            width: 750
        });
        
        $('.progressbar').livequery(function () {
            $(this).progressbar({
                    value: 0
            });
        });
        
        $('a.modal-dialog').livequery('click', function() {
            objid_prompt = $(this).attr('objid');
            colidx_prompt = $(this).attr('colidx');
            
            $dialog
                .html('')
                .load($(this).attr('href'), $(this).attr('params'), function(response, status, xhr) {
                    $(this)
                        .dialog('option', 'width', 750)
                        .dialog('option', 'position', ['center', 'center'])
                        .dialog('open');
                })
                .dialog('option', 'title', $(this).attr('original-title'));

            return false;
        });
        
        $('.datatable-container a.prompt-select').livequery('click', function() {
            var arrObjID = objid_prompt.split(',');
            var arrColIdx = colidx_prompt.split(',');
            var prompt_result = '';
            var objid = '';
            var colidx = 0;
            
            for (var i = 0; i < arrObjID.length; i++) {
                objid = arrObjID[i];
                colidx = arrColIdx.length == 1 ? arrColIdx[0] : arrColIdx[i];
                
                prompt_result = $(this).parent().parent().children('td').eq(colidx).children('a').eq(0).text();
                if ($('#' + objid).is('span') || $('#' + objid).is('div') || $('#' + objid).is('p') || $('#' + objid).is('label')) {
                    $('#' + objid).html(prompt_result);
                } else {
                    $('#' + objid).val(prompt_result);
                }
            }
            $dialog.dialog('close');
            if ($('#' + arrObjID[0]).attr('oncomplete')) {
                ajax_modal = false;
                eval($('#' + arrObjID[0]).attr('oncomplete'));
            }
            $('#' + arrObjID[0]).focus();

            return false;
        });
        
        $('input[type=text].prompt-search').livequery('keyup', function(e) {
            if (e.which == 115) {
                $('a[objid^="' + $(this).attr('id') + '"].modal-dialog').trigger('click');
                return false;
            }
        });
        
        $('.datatable-container').livequery(function () {
            $(this).dataTable({
                'bJQueryUI': true,
                'bSort': false,
                'sPaginationType': 'full_numbers',
                'sDom': '<"H"lr>t<"F"ip>',
                'bProcessing': true,
                'bServerSide': true,
                'sAjaxSource': 'prompt/' + $(this).attr('source'),
                'sServerMethod': 'POST',
                'aLengthMenu': [[10, 25, 50, 100], [10, 25, 50, 100]],
                "fnServerParams": function ( aoData ) {
                    aoData.push({});
                },
                'fnServerData': function (sSource, aoData, fnCallback) {
                    ajax_modal = false;
                    $.post(sSource, aoData, function (json) {
                        ajax_modal = true;
                        fnCallback(json);
                    }, 'json');
                }
            }).columnFilter();
        });

        // Close the notifications when the close link is clicked
        $("a.close").livequery('click', function () {
            $(this).fadeTo(200, 0); // This is a hack so that the close link fades out in IE
            $(this).parent().fadeTo(200, 0);
            $(this).parent().slideUp(400, function() {$(this).remove()});
            return false;
        });

        // Fade in the notifications
        $(".notification").livequery(function () {
            $(this).fadeIn("slow");
        });

        //$('textarea, input[type=text], input[type=password], input[type=file]').livequery(function () {
        $('input.uniform, textarea.uniform, button.uniform').livequery(function () {
            // Update uniform if enabled
            $.uniform && $(this).uniform();
        });

        $('form input[type="text"]').livequery('keypress', function (event) {
            if (event.keyCode == 13) {
                $('form :input:eq('+ ($('form :input').index(this)+1) +')').focus();
                return false;
            }
        });

        $('.datepicker').livequery(function () {
            var aField = $(this).attr('altField') ? $(this).attr('altField') : '';
            var aFormat = $(this).attr('altFormat') ? $(this).attr('altFormat') : ($(this).attr('altField') ? 'yy-mm-dd' : '');
            
            $(this).attr('readonly', true);
            
            $(this).datepicker({
                dateFormat: $(this).attr('dateFormat') ? $(this).attr('dateFormat') : 'yy-mm-dd',
                changeYear: $(this).attr('changeYear') ? $(this).attr('changeYear') : false,
                changeMonth: $(this).attr('changeMonth') ? $(this).attr('changeMonth') : false,
                altField: aField,
                altFormat: aFormat,
                defaultDate: aField && $(aField).val() != '' ? $.datepicker.parseDate(aFormat, $(aField).val()) : new Date()
            });
            
            $(this).datepicker("setDate", aField && $(aField).val() != '' ? $.datepicker.parseDate(aFormat, $(aField).val()) : new Date());
        });
        
        $('.datepicker[groupdt]').livequery(function () {
            var dates = $($(this).attr('groupdt')).datepicker();
            var frselect = $(this).attr('frselect');
            
            $(this).datepicker('option', 'onSelect', function (selectedDate) {
                var option = this.id == frselect ? 'minDate' : 'maxDate',
                            instance = $(this).data('datepicker'),
                            date = $.datepicker.parseDate(
                                    instance.settings.dateFormat ||
                                    $.datepicker._defaults.dateFormat,
                                    selectedDate, instance.settings);
                dates.not(this).datepicker('option', option, date);
            });
        });
        
        $('.uppercase').livequery('keypress', function(e) {
            if (((e.which >= 97 && e.which <= 122) || (e.which >= 65 && e.which <= 90) || (e.which >= 48 && e.which <= 57)) && $(this).val().length < $(this).attr('maxlength')) {
                var str = String.fromCharCode(e.which);
                $(this).val($(this).val() + str.toUpperCase());
                return false;
            }
	});

        $('form img').livequery(function() {
            $(this).attr('src', $(this).attr('src') + '?' + (new Date()).getTime());
        });
        
        $('.tooltip').livequery(function() {
            $(this).tipsy({fade: true});
        });
        
        $('input.numeric').livequery(function() {
            $(this).val($.trim($(this).val()));
            $(this).autoNumeric({mDec: 0, vMax: '999999999999.99'});
            if ($(this).val() == '') {
                $(this).autoNumericSet(0);
            }
        });
        
        $('input.numeric').livequery('keydown', function(e) {
            var inc = 0;
            if (e.which == 84) {
                inc = 1000;
            } else if (e.which == 77) {
                inc = 1000000;
            } else if (e.which == 66) {
                inc = 1000000000;
            }
            
            if (inc > 0) {
                var newval = $(this).autoNumericGet({mDec: 0}) * inc;
                $(this).autoNumericSet(newval > 0 ? newval : $(this).autoNumericGet({mDec: 0}));
            }
        });
        
        $('input.numeric').livequery('keyup', function(e) {
            if ($(this).val() == '') {
                $(this).val('0');
            }
        });
        
        $('input').livequery('keyup', function(e) {
            if ($(this).val().length >= $(this).attr('maxlength')) {
                if ((e.which >= 65 && e.which <= 90) || (e.which >= 48 && e.which <= 57)) {
                    if ($(this).attr('oncomplete')) {
                        ajax_modal = false;
                        eval($(this).attr('oncomplete'));
                    }
                    $('form :input:eq('+ ($('form :input').index(this)+1) +')').focus();
                }
            }
        });
        
        $(':input[oncomplete]').livequery('change', function() {
            ajax_modal = false;
            eval($(this).attr('oncomplete'));
        });
        
        $('form.required-form').livequery(function() {
            $(this).validate({
                errorClass: 'invalid',
                validClass: 'valid',
                errorElement: 'div',
                errorPlacement: function(error, element) {
                    var $parent = element.parent();
                    if (element.attr('type').toLowerCase() == 'radio' || element.attr('type').toLowerCase() == 'checkbox') {
                        $parent.parent().parent().parent().append(error);
                    } else if (!$parent.is('p')) {
                        $parent.parent().append(error);
                    } else {
                        $parent.append(error);
                        
                    }
                }
            });
        });

        $('form :input:first').livequery(function () {
            if (!$(this).hasClass('hasDatepicker')) {
                $(this).focus();
            }
        });
        
        $('a.ajax-link').livequery('click', function() {
            $('.notification > a.close').trigger('click');
            var url = $(this).attr('href');
            var param = $(this).attr('param') ? $(this).attr('param') : '';
            
            $('#content').slideUp('normal', function() {
                $.post(
                    url,
                    param,
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
        
        $('a.pdf-view').livequery('click', function () {
            var w = $(this).attr('wndWidth') ? $(this).attr('wndWidth') : '800';
            var h = $(this).attr('wndHeight') ? $(this).attr('wndHeight') : '600';
            var s = $(this).attr('wndScrollbars') ? $(this).attr('wndScrollbars') : '1';
            
            var form = $('<form></form>').attr({
                action: $(this).attr('href'),
                method: $(this).attr('method') ? $(this).attr('method') : 'post',
                name: 'formPdfView',
                id: 'formPdfView',
                target: 'cmsPopupWindow'
            });
            $('body').append(form);
            
            var arrParam = $(this).attr('param') ? $(this).attr('param').split('&') : new Array();
            var arrAttr = null;
            if (arrParam.length > 0) {
                for (var i = 0; i < arrParam.length; i++) {
                    arrAttr = arrParam[i].split('=');
                    $('<input>').attr({
                        type: 'hidden',
                        id: arrAttr[0],
                        name: arrAttr[0],
                        value: arrAttr[1]
                    }).appendTo(form);
                }
            }
            
            window.open('about:blank', 'cmsPopupWindow', 'width=' + w + ', height=' + h + ', scrollbars=' + s + ", status=1");
            form.submit();
            
            return false;
        });
        
        $(this).bind('contextmenu', function(e) {
            e.preventDefault();
        });

        // IE6 PNG fix
        $(document).pngFix();

        $(document).ajaxStart(function() {
            $('div.tipsy').remove();
            if (ajax_modal) {
                $('#ajax-overlay').modal({close: false, escClose: false});
            }
        }).ajaxSuccess(function() {
            if (ajax_modal) {
                $.modal.close();
            } else {
                ajax_modal = true;
            }
        }).ajaxComplete(function(e, xhr, settings) {
            if (ajax_modal) {
                $.modal.close();
            } else {
                ajax_modal = true;
            }
            
            if (xhr.responseText.indexOf('frmLogin') > -1) {
                console.log('redirect to login page');
                alert('session expired !');
                document.location.reload();
            }
        }).ajaxStop(function() {
            if (ajax_modal) {
                $.modal.close();
            } else {
                ajax_modal = true;
            }
        }).ajaxError(function(e, jqxhr, settings, exception) {
            if (ajax_modal) {
                $.modal.close();
            } else {
                ajax_modal = true;
            }
            
            if (jqxhr.status == 401) {
                console.log('redirect to login page');
                alert('session expired !');
                document.location.reload();
            }
        });

    });

})(jQuery);
