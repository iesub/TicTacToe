package tic.tac.toe.game.controller.dto;

import lombok.Getter;
import lombok.Setter;
import tic.tac.toe.game.controller.enumiration.WSChatMessageEnum;

@Setter
@Getter
public class WSChatMessage {
    String senderNickname;
    String message;
    WSChatMessageEnum type;

    public WSChatMessage(){}

    public WSChatMessage(WSChatMessageEnum type){
        this.type = type;
    }

    public WSChatMessage(String senderNickname, String message){
        this.senderNickname = senderNickname;
        this.message = message;
    }

}
