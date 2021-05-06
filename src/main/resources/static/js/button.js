$(document).ready(function() {
    var socket = new SockJS('/updateDevices');
    var stompClient = Stomp.over(socket);

    stompClient.connect({}, function (frame) {
        $(".devicecard").each(function (i, card) {

            console.log(card.querySelector("div"))
            var id = card.querySelector("div").id;
            var deviceId = id.split("_")[1];

            var id = id.replace(".", "\\\.");
            stompClient.subscribe('/topic/update/' + deviceId, function (msg) {
                var status = JSON.parse(msg.body).message;
                var el = $("#" + id);

                if (status === "ON") {
                    el.removeClass("button_off");
                    el.addClass("button_on");
                } else if (status === "OFF") {
                    el.removeClass("button_on");
                    el.addClass("button_off");
                }
            });

        });
    });


    $("[id^=BUTTON]").click(function () {
        var deviceID = $(this).attr("id").split("_")[1];

        var selected = $(this).attr("class");

        var status = "on";
        if (selected.includes("button_on")) {
            status = "off"
        }

        // $(this).removeClass("button_off");
        // $(this).removeClass("button_on");
        // $(this).addClass("button_" + status);

        stompClient.send("/ws/socket", {}, JSON.stringify({"deviceID": deviceID, "message": status.toUpperCase()}));
    })


});