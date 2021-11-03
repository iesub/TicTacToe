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
    subscription = stompClient.subscribe('/client/queue/no-rank-3x3', onReply);
    subscription = stompClient.subscribe('/user/client/queue/no-rank-3x3-redirect', onReply);

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
        stompClient.send("/app/no-rank-queue-3x3.add-to-queue"
            ,{},
            JSON.stringify(1))
    }
}

