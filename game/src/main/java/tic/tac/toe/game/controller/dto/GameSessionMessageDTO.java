package tic.tac.toe.game.controller.dto;

import lombok.Getter;
import lombok.Setter;
import tic.tac.toe.game.controller.enumiration.GameSessionMessageTypes;

@Setter
@Getter
public class GameSessionMessageDTO {
    GameSessionMessageTypes messageType;
    String message;

    public GameSessionMessageDTO(){}

    public GameSessionMessageDTO(GameSessionMessageTypes type){
        this.messageType = type;
    }

}
