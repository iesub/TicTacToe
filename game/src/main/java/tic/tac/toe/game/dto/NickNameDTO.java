package tic.tac.toe.game.dto;

import lombok.Getter;
import lombok.Setter;
import tic.tac.toe.game.enumiration.NickNameDTOTypes;

@Getter
@Setter
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
