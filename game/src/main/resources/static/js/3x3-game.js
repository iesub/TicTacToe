var stompClient
var subscription
var figureType
var sessionid
var yourMove = false
var matchStoped = false

$(document).ready(function () {
    var socket = new SockJS('/ws');
    stompClient = Stomp.over(socket);
    stompClient.connect({}, onConnected, onError);

    sessionid = $("#sessionId").text()
    $("#sessionId").remove()

})

function onConnected() {
    subscription = stompClient.subscribe('/client/game/'+sessionid+'/'+gameType, onReply);
    subscription = stompClient.subscribe('/user/client/game/'+sessionid+'/'+gameType, onReply);

    subscribe()
    buildTable(tableSize, tableSize)
}

function onError(){}

function onReply(payload){
    var answer = JSON.parse(payload.body);
    switch (answer.messageType){
        case "GAME_FULL":
            setTimeout(function()
            {
                $("#loosing-modal-body").text("Все поля заняты. Победивших нет!")
                $("#loosing-modal").modal("toggle")
            }, 1000);
            matchStoped = true;
            break;
        case "WINNING_MESSAGE":
            setTimeout(function()
            {
                $("#winning-modal").modal("toggle")
            }, 1000);
            matchStoped = true;
            break
        case "WINNING_MESSAGE_D":
            if (matchStoped){break}
            $("#winning-modal-body").text("Ваш противник отключился. Но вы победили!")
            $("#winning-modal").modal("toggle")
            break
        case "SUBSCRIBE":
            figureType = answer.message
            if (answer.message == "cross"){
                yourMove = true
            } else {
                yourMove = false
            }
            changeWalking()
            break
        case "CHECK_POSITION":
            $("#"+answer.message).removeClass(tableClass).addClass("disabled-" + tableClass)
            posImage($("#"+answer.message), false)
            yourMove = true
            changeWalking()
            break
        case "LOOSE_MESSAGE_D":
            $("#loosing-modal-body").text("Вы отключились. Техническое поражение!")
            $("#loosing-modal").modal("toggle")
            break
        case "LOOSE_MESSAGE":
            setTimeout(function()
            {
                $("#loosing-modal").modal("toggle")
            }, 1000);
            matchStoped = true;
            break
    }
}

$( window ).on("beforeunload",function() {
    if(stompClient) {
        stompClient.send("/app/game/"+sessionid+"/"+gameType+".message"
            ,{},
            JSON.stringify({messageType: "DISCONNECT"}))
    }
    stompClient.unsubscribe(subscription)
})

function subscribe(){
    if(stompClient) {
        stompClient.send("/app/game/"+sessionid+"/"+gameType+".message"
            ,{},
            JSON.stringify({messageType: "SUBSCRIBE"}))
    }
}

function buildTable(size, tableSize){
    for (j = 0; j < size; j++){
        $("#table-container").append("<tr id = 'row-"+j+"'> </tr>")
        for (i = 0; i < size; i++){
            if ( i == 0){
                $("#row-"+j).append('<th scope="row" class='+tableClass+' id="'+j+'-'+i+'"></th>')
            } else {
                $("#row-"+j).append('<td class='+tableClass+' id="'+j+'-'+i+'"></td>')
            }
        }
    }

    $("."+tableClass).on('click', function (e){
        if (!$(this).hasClass(tableClass)){return}
        if (!yourMove){return}
        if(stompClient) {
            stompClient.send("/app/game/"+sessionid+"/"+gameType+".message"
                ,{},
                JSON.stringify({messageType: "CHECK_POSITION", message: e.target.id}))
        }

        $(this).removeClass(tableClass).addClass("disabled-" + tableClass)
        posImage($(this), true)
        yourMove = false
        changeWalking()
    })
}

function posImage(object, yourTime){
    var imageSize;
    if (tableSize == 3){
        imageSize = 75
    } else {
        imageSize = 15
    }
    if (yourTime) {
        if (figureType == "cross") {
            object.append($(".cross-image").clone().removeClass("cross-image").css("display", "").width(imageSize).height(imageSize))
        } else {
            object.append($(".circle-image").clone().removeClass("circle-image").css("display", "").width(imageSize).height(imageSize))
        }
    } else {
        if (figureType == "circle") {
            object.append($(".cross-image").clone().removeClass("cross-image").css("display", "").width(imageSize).height(imageSize))
        } else {
            object.append($(".circle-image").clone().removeClass("circle-image").css("display", "").width(imageSize).height(imageSize))
        }
    }
}

function changeWalking(){
    if (yourMove){
        $("#who-walk").text("Ваш ход")
    } else {
        $("#who-walk").text("Противник делает ход")
    }
}