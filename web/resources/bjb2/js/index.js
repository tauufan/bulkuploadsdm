(function($) {

    $(function () {
        
        // initialize scrollable and return the programming API
        var api = $("#scroll").scrollable({
            items: '#tools',
            size: 1,
            clickable: false
        // use the navigator plugin
        }).navigator({
            api: true
        });


        // this callback does the special handling of our "intro page"
        api.onStart(function(e, i) {
            // when on the first item: hide the intro
            if (i) {
                $("#intro").fadeOut("slow");
            // otherwise show the intro
            } else {
                $("#intro").fadeIn(1000);
            }
            // toggle activity for the intro thumbnail
            $("#t0").toggleClass("active", i == 0);
        });

        // a dedicated click event for the intro thumbnail
        $("#t0").click(function() {
            // seek to the beginning (the hidden first item)
            $("#scroll").scrollable().begin();

        });

    });

})(jQuery);
