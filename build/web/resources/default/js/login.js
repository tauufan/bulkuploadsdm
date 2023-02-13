(function($) {

    $(function () {
        
        $('body').append('<div id="ajax-overlay"><span></span></div>');

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

        $('form input[type="text"]').livequery('keypress', function (event) {
            if (event.keyCode == 13) {
                $('form :input:eq('+ ($('form :input').index(this)+1) +')').focus();
                return false;
            }
        });

        $('form :input:first').livequery(function () {
            $(this).focus();
        });
        
        $('.tooltip').livequery(function() {
            $(this).tipsy({fade: true});
        });
        
        $('form[name="frmLogin"]').livequery(function() {
            $(this).validate({
                errorClass: 'invalid',
                validClass: 'valid',
                errorElement: 'div'
            });
        });
        
        $('form[name="frmLogin"]').livequery('submit', function() {
            var form = $(this);

            $('.notification > a.close').trigger('click');
            
            if (!form.valid()) return false;

            $.post(
                form.attr('action'),
                form.serialize(),
                function(data) {
                    //prepare the html notification
                    var notification = '<div class="closable notification ' + data.status + '">'+data.message+'<a class="close" href="#">close</a></div>';

                    // add the notification message to the DOM
                    form.prepend(notification);

                    if(data.status == 'success') {
                        $('#ajax-overlay').modal({close: false, escClose: false});
                        $('.notification').fadeIn('normal', function() {
                            window.location.href = "dashboard";
                        });
                        return;
                    }

                    $('.notification').fadeIn('normal');
                }, 'json'
            );

            return false;
        });

        $(document).ajaxStart(function() {
            $('#ajax-overlay').modal({close: false, escClose: false});
        }).ajaxSuccess(function() {
            $.modal.close();
        }).ajaxComplete(function() {
            $.modal.close();
        }).ajaxStop(function() {
            $.modal.close();
        }).ajaxError(function() {
            $.modal.close();
        });

    });

})(jQuery);
