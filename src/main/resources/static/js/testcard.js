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
                    el.attr("class", "button_on");
                } else if (status === "OFF") {
                    el.attr("class", "button_off");
                } else {
                    console.log("ERROR: Invalid status");
                }
            });

        });
    });


    $("[id^=BUTTON]").click(function () {
        var deviceID = $(this).attr("id").split("_")[1];

        var selected = $(this).attr("class")

        var status = "on";
        if (selected === "button_on") {
            status = "off"
        }
        $(this).attr("class", "button_" + status);

        stompClient.send("/ws/socket", {}, JSON.stringify({"deviceID": deviceID, "message": status.toUpperCase()}));
    })


});