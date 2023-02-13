    <%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
    <%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>
    <script type="text/javascript">
        $(document).ready(function() {
            $('form[name=frmSupervisor]').livequery('submit', function() {
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
                            $('#content').slideDown('normal');
                        }, 'json'
                    );
                });
                
                return false;
            });
            
            $('#cancel').livequery('click', function() {
                window.location = 'index';
                return false;
            });
        });
    </script>
