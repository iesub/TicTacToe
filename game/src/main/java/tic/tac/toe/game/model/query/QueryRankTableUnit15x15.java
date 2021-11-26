package tic.tac.toe.game.model.query;

import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class QueryRankTableUnit15x15 {
    Long id;
    String nickname;
    int score;
    public QueryRankTableUnit15x15(){}
    public QueryRankTableUnit15x15(Long id, String nickname, int score){
        this.id = id;
        this.score = score;
        this.nickname = nickname;
    }

}
