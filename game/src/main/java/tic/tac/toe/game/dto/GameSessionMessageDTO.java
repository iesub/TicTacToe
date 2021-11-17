package tic.tac.toe.game.dto;

import lombok.Getter;
import lombok.Setter;
import tic.tac.toe.game.enumiration.GameSessionMessageTypes;

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
