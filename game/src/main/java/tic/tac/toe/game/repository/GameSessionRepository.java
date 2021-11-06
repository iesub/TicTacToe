package tic.tac.toe.game.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import tic.tac.toe.game.entity.ChatMessage;
import tic.tac.toe.game.entity.GameSession;

import java.util.List;

public interface GameSessionRepository extends JpaRepository<GameSession, Long> {
}
