var stompClient
var rollUpAmount = 0;

$(document).ready(function () {
    $('#3x3-position-bot').text($('#3x3-pos-of').text())
    $("#3x3-pos-of").parent().css("border-top","2px solid #22A7F0")
    $("#3x3-pos-of").parent().css("border-bottom","2px solid #22A7F0")
    $('#15x15-position-bot').text($('#15x15-pos-of').text())
    $("#15x15-pos-of").parent().css("border-top","2px solid #22A7F0")
    $("#15x15-pos-of").parent().css("border-bottom","2px solid #22A7F0")
    $('.preloader').fadeOut().end().delay(10000).fadeOut('slow');

    var socket = new SockJS('/ws');
    stompClient = Stomp.over(socket);
    stompClient.connect({}, onConnected, onError);


    $("#chat-message-sender").submit(function (e){
        e.preventDefault()
        if ($("#chat-input").val().length > 1000){
            $("#chat-message-helper").text("Сообщение должно быть меньше 1000 символов в длину!")
            $("#chat-input").val("")
            return
        }
        if ($("#chat-input").val().length <=0){
            $("#chat-message-helper").text("Нельзя отправить пустое сообщение!")
            return
        }
        $("#chat-message-helper").text("")
        sendMessage()
    })

    $("#messages-container").scroll(function (){
        if ($("#messages-container").scrollTop()== 0){
            loadLast20Messages()
        }
    })

    $('#myTab a').on('click', function (e) {
        e.preventDefault()
        $(this).tab('show')
    })

})

function onConnected() {
    stompClient.subscribe('/client/main-chat', onReply);
    stompClient.subscribe('/user/client/main-chat-load', onChatDownload);
    stompClient.subscribe('/client/nickname-update', onNickNameUpdate);
    loadLast20Messages()
    sendToUpdateNickName()
}

function onError(error) {
    $("#errorMessage").text( 'Произошла ошибка подключения. Попробуйте обновить страницу и проверьте ваше' +
        ' интернет соединение.');
    $("#errorMessage").css('color','red');
}

function onReply(payload){
    var answer = JSON.parse(payload.body);
    rollDown = false;
    if($("#messages-container").scrollTop() + $("#messages-container").innerHeight() >= $("#messages-container")[0].scrollHeight-0,172){
        rollDown = true;
    }
    if (answer.type == "IGNORE"){return}
    $("#messages-container").append(
        '<div class="row"><h5>' +
        answer.senderNickname +
        '</h5></div>' +
        '<div class="row"><label>' +
        answer.message +
        '</label></div>'
    )
    if(rollDown) {
        $("#messages-container").animate({
            scrollTop: $("#messages-container").get(0).scrollHeight
        }, 1000);
    }
}

function onChatDownload(payload){
    var answer = JSON.parse(payload.body);
    scrollHeightStart = $("#messages-container")[0].scrollHeight
    for (i = 0; i<answer.length; i++){
        $("#messages-container").prepend(
            '<div class="row"><h5>' +
            answer[i].senderNickname +
            '</h5></div>' +
            '<div class="row"><label>' +
            answer[i].message +
            '</label></div>'
        )
    }
    if (rollUpAmount==0){
        $("#messages-container").animate({
            scrollTop: $("#messages-container").get(0).scrollHeight
        }, 1000);
    } else {
        $("#messages-container").animate({
            scrollTop: $("#messages-container").get(0).scrollHeight - scrollHeightStart
        }, 1);
    }
    rollUpAmount++
}

function onNickNameUpdate(payload){
    var answer = JSON.parse(payload.body);
    if (answer.type == "SEND_TO_EVERYONE"){
        if($("#"+answer.nickname)){
            $("#"+answer.nickname).remove()
        }
        $("#users-container").append(
            "<h5 id = '"+answer.nickname + "'>" + answer.nickname + "</h5>"
        )
        sendToUpdateNickName()
    } else if (answer.type == "DELETE_ONE"){
        $("#"+answer.nickname).remove()
    }else if(answer.type == "SEND_TO_NEW"){
        if($("#"+answer.nickname)){
            $("#"+answer.nickname).remove()
        }
        $("#users-container").append(
            "<h5 id = '"+answer.nickname + "'>" + answer.nickname + "</h5>"
        )
    } else if(answer.type == "ACCEPT_OTHERS"){
        sendToUpdateNickName()
    }
}

function sendMessage() {
    chatMessage = {message: $("#chat-input").val()}
    $("#chat-input").val("")
    if(stompClient) {
        stompClient.send("/app/main-chat.send-message"
            ,{},
            JSON.stringify(chatMessage))
    }
}

function loadLast20Messages(){
    if(stompClient) {
        stompClient.send("/app/main-chat.loadLastMessages"
            ,{},
            rollUpAmount)
    }
}

function sendToUpdateNickName() {
    if(stompClient) {
        stompClient.send("/app/main-chat.update-nickname-list"
            ,{},
            JSON.stringify(rollUpAmount))
    }
}

