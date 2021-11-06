var stompClient
var subscription

var sessionid

$(document).ready(function () {
    var socket = new SockJS('/ws');
    stompClient = Stomp.over(socket);
    stompClient.connect({}, onConnected, onError);

    sessionid = $("#sessionId").text()
    $("#sessionId").remove()
})

$( window ).on("beforeunload",function() {
    stompClient.unsubscribe(subscription)
})

function onConnected() {
    subscription = stompClient.subscribe('/client/game/'+sessionid+'/no-rank-3x3', onReply);
    subscription = stompClient.subscribe('/user/client/game/'+sessionid+'/no-rank-3x3', onReply);

    sendMessage()
}

function onError(){}

function onReply(){

}

function sendMessage() {
    if(stompClient) {
        stompClient.send("/app/game/"+sessionid+"/no-rank-game-3x3.message"
            ,{},
            JSON.stringify(1))
    }
}