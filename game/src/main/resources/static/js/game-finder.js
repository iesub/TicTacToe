var stompClient
var subscription

$(document).ready(function () {
    var socket = new SockJS('/ws');
    stompClient = Stomp.over(socket);
    stompClient.connect({}, onConnected, onError);
})

$( window ).on("beforeunload",function() {
    stompClient.unsubscribe(subscription)
})

function onConnected() {
    subscription = stompClient.subscribe('/user/client/queue/' + finderType, onReply);

    sendMessage()
}

function onError(error) {
}

function onReply(payload){
    var answer = payload.body;
    document.location.href = answer
}

function sendMessage() {
    if(stompClient) {
        stompClient.send("/app/"+finderType+".add-to-queue"
            ,{},
            JSON.stringify(1))
    }
}

