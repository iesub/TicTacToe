package tic.tac.toe.game.entity;

import lombok.Getter;
import lombok.Setter;
import tic.tac.toe.game.enumiration.GameTypesEnum;

import javax.persistence.*;

@Entity
@Getter
@Setter
@Table(name = "session")
public class GameSession {
    @Id
    @Column(name = "id")
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    private Long id = Long.valueOf(0);
    @Column(name = "session_game_type")
    GameTypesEnum gameType;
    @ManyToOne()
    @JoinColumn(name = "game_winner_id", referencedColumnName = "id")
    private User gameWinner;
    @ManyToOne()
    @JoinColumn(name = "circle_side_player_id", referencedColumnName = "id")
    private User circleSidePlayer;
    @ManyToOne()
    @JoinColumn(name = "cross_side_player_id", referencedColumnName = "id")
    private User crossSidePlayer;

    public GameSession(){}

    public GameSession(GameTypesEnum gameType, User circleSidePlayer, User crossSidePlayer){
        this.gameType = gameType;
        this.circleSidePlayer = circleSidePlayer;
        this.crossSidePlayer = crossSidePlayer;
    }
}
