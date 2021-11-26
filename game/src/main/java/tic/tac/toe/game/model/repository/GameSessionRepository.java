package tic.tac.toe.game.model.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import tic.tac.toe.game.model.entity.GameSession;

import java.util.List;

public interface GameSessionRepository extends JpaRepository<GameSession, Long> {
    @Query(value="select * from (select * from session where circle_side_player_id = ?1 or cross_side_player_id = ?1)sub ORDER by id desc limit 50", nativeQuery = true)
    List<GameSession> findSessionsById(Long id);
}
