package tic.tac.toe.game.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import tic.tac.toe.game.entity.GameSession;
import tic.tac.toe.game.entity.User;
import tic.tac.toe.game.repository.GameSessionRepository;

import java.util.Optional;

@Service
public class GameSessionService {

    @Autowired
    GameSessionRepository sessionRepository;

    public GameSession getSession(Long id){
        Optional<GameSession> gameSession = sessionRepository.findById(id);
        return gameSession.orElse(new GameSession());
    }

    public Long saveSession(GameSession session){
        return sessionRepository.save(session).getId();
    }

}
