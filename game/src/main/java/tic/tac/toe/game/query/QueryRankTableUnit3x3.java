package tic.tac.toe.game.query;


import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class QueryRankTableUnit3x3 {
    Long id;
    String nickname;
    int score;

    public QueryRankTableUnit3x3(){}
    public QueryRankTableUnit3x3(Long id, String nickname, int score){
        this.id = id;
        this.score = score;
        this.nickname = nickname;
    }

}
