$(function() {

    if (!$("#fetch-prime").length) {
        return;
    }

    var index = $("#fetch-prime").data("index");

    var fetchPrime = function(index) {
        $.get("/prime/fetch", {
            index : index
        }).done(function(data) {
            $("#fetch-prime").text(data.number);
        }).fail(function(error) {
            if (error.status == 302 /* HttpStatus.FOUND */) {
                
                setTimeout(function() {
                    fetchPrime(index);
                }, 500);
                
            }
        })
    }

    fetchPrime(index);
    
})