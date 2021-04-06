$(document).ready(function() {
    var socket = new SockJS('/updateDevices');
    var stompClient = Stomp.over(socket);

    stompClient.connect({}, function (frame) {
        $(".badge").each(function (i, badge) {

            var id = badge.querySelector(".badge_status").id;
            console.log("ID: " + id);
            var deviceId = id.split("_")[1];

            id = id.replace(".", "\\\.");
            stompClient.subscribe('/topic/update/' + deviceId, function (msg) {
                console.log(msg);
                var status = JSON.parse(msg.body).message;
                badge.querySelector(".badge_status").innerHTML = status;

                console.log("Updated: " + status)


            });

        });
    });

});