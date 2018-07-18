$(function() {

    if (!$("#fetch-prime").length) {
        return;
    }

    var index = $("#fetch-prime").data("index");

    var id = setInterval(function() {

        $.get("/prime/fetch", {
            index : index
        }).done(function(data) {
            clearInterval(id);
            $("#fetch-prime").text(data.number);
        }).fail(function(error) {
            if (error.status != 102 /* HttpStatus.PROCESSING */) {
                clearInterval(id);
            }
        })

    }, 1000)
})