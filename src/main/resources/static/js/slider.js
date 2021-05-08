$(document).ready(function() {
    var socket = new SockJS('/updateDevices');
    var stompClient = Stomp.over(socket);

    stompClient.connect({}, function (frame) {
        $("[id^=SLIDER]").each(function (i, card) {

            console.log(card)
            var id = card.id;
            var deviceId = id.split("_")[1];

            var id = id.replace(".", "\\\.");
            stompClient.subscribe('/topic/update/' + deviceId, function (msg) {
                var status = JSON.parse(msg.body).message;
                var el = $("#" + id);

                console.log("Status: " + status)
                if (status === "ON") {
                    el.find("label>input").prop("checked", true);
                } else {
                    el.find("label>input").prop("checked", false);
                }
            });

        });
    });


    $("[id^=SLIDER]").click(function () {
        console.log("Ran")
        var deviceID = $(this).attr("id").split("_")[1];
        $(this).prop("checked", true);

        var selected = $(this).find("label>input").prop("checked")
        console.log("Selected: " + selected);

        var status = "on";
        if (selected) {
            $(this).prop("checked", true);
        } else {
            status = "off"
            $(this).prop("checked", false);
        }

        stompClient.send("/ws/socket", {}, JSON.stringify({"deviceID": deviceID, "message": status.toUpperCase()}));
    })


});