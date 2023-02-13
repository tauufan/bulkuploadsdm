    <%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
    <%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>
    <style type="text/css">
        .dataTables_length {
            width: auto;
        }
        label.width200-fix {
            width: 200px;
            display: block;
            float: left;
            font-weight: bold;
        }
        div.float-right p {text-align: right;}
    </style>
    
    <script type="text/javascript">
        $(document).ready(function() {
            var oTable = null;
            
            $('#debetacc-table').livequery(function() {
                oTable = $(this).dataTable({
                    'bJQueryUI': true,
                    'bSort': false,
                    'sPaginationType': 'full_numbers',
                    'bProcessing': true,
                    'bServerSide': true,
                    'sAjaxSource': 'debet-accounts/data',
                    'sServerMethod': 'POST',
                    'bLengthChange': false,
                    'iDisplayLength': 20,
                    'fnServerData': function (sSource, aoData, fnCallback) {
                        ajax_modal = false;
                        $.post(sSource, aoData, function (json) {
                            ajax_modal = true;
                            fnCallback(json);
                        }, 'json');
                    }
                })
                .columnFilter({
                    aoColumns: [
                        {type: 'text'},
                        {type: 'text'},
                        {type: 'text'}
                    ]
                });
            });
            
            $('form[name=frmDebetAccount]').livequery('submit', function() {
                $('.notification > a.close').trigger('click');
                var url = $(this).attr('action');
                var param = $(this).serialize();

                $('#content').slideUp('normal', function() {
                    $.post(
                        url,
                        param + '&submit=submit',
                        function(data) {
                            var notification = '<div class="closable notification ' + data.status + '">'+data.message+'<a class="close" href="#">close</a></div>';
                            $('#content').before(notification);
                            $('.notification').fadeIn('normal');
                            
                            if(data.status == 'success') {
                                loadIndex();
                            } else {
                                $('#content').slideDown('normal');
                            }
                        }, 'json'
                    );
                });
                
                return false;
            });
            
            $('#cancel').livequery('click', function() {
                loadIndex();
                return false;
            });
            
            $('a.ajax-remove').livequery('click', function() {
                var url = $(this).attr('href');
                var param = $(this).attr('param');
                var confrimMsg = '<fmt:message key="debet_accounts.remove_message" />';
                var arrParam = param.split('&');
                var arrAccNo = arrParam[1].split('=');
                jConfirm(confrimMsg.replace('%s', arrAccNo[1]), '<fmt:message key="debet_accounts.remove_title" />', function(r) {
                    if (r) {
                        $('.notification > a.close').trigger('click');

                        $('#content').slideUp('normal', function() {
                            $.post(
                                url,
                                param,
                                function(data) {
                                    var notification = '<div class="closable notification ' + data.status + '">'+data.message+'<a class="close" href="#">close</a></div>';
                                    $('#content').before(notification);
                                    $('.notification').fadeIn('normal');

                                    if(data.status == 'success') {
                                        loadIndex();
                                    } else {
                                        $('#content').slideDown('normal');
                                    }
                                }, 'json'
                            );
                        });
                    }
                });
                
                return false;
            });
            
            var loadIndex = function() {
                $.post(
                    'debet-accounts',
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
            }
        });
    </script>
