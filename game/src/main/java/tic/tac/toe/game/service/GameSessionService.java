package tic.tac.toe.game.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import tic.tac.toe.game.entity.GameSession;
import tic.tac.toe.game.entity.User;
import tic.tac.toe.game.repository.GameSessionRepository;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@Service
public class GameSessionService {

    @Autowired
    GameSessionRepository sessionRepository;

    Map<Long, int[][]> sessionGame = new HashMap<>();

    public void putGameMap(Long id, int[][] map){
        sessionGame.put(id, map);
    }

    public void deleteMap(Long id){
        sessionGame.remove(id);
    }

    public void putPoint(Long id, int y, int x){
        if (sessionGame.get(id)[y][x] == 0) {
            sessionGame.get(id)[y][x] = 1;
        }
    };

    public GameSession getSession(Long id){
        Optional<GameSession> gameSession = sessionRepository.findById(id);
        return gameSession.orElse(new GameSession());
    }

    public Long saveSession(GameSession session){
        return sessionRepository.save(session).getId();
    }

}
