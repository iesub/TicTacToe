package tic.tac.toe.game.controller.dto;

import lombok.Getter;
import lombok.Setter;
import tic.tac.toe.game.controller.enumiration.NickNameDTOTypes;

@Setter
@Getter
public class NickNameDTO {

    String nickname;
    NickNameDTOTypes type;

    public NickNameDTO(){}

    public NickNameDTO(String nickname, NickNameDTOTypes type){
        this.nickname = nickname;
        this.type = type;
    }

    public NickNameDTO(NickNameDTOTypes type){
        this.type = type;
    }

}
