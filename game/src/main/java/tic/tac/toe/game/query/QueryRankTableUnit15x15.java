package tic.tac.toe.game.query;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.Column;

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
