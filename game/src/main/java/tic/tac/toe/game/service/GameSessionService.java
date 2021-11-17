package tic.tac.toe.game.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import tic.tac.toe.game.entity.GameSession;
import tic.tac.toe.game.repository.GameSessionRepository;

import java.util.HashMap;
import java.util.List;
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

    public void putPoint(Long id, int y, int x, boolean isCross){
        if (sessionGame.get(id)[y][x] == 0 && isCross) {
            sessionGame.get(id)[y][x] = 1;
        } else if (sessionGame.get(id)[y][x] == 0 && !isCross){
            sessionGame.get(id)[y][x] = 2;
        }
    };

    public boolean checkWinningCondition(Long sessionId, int mapSize, int sign){
        int[][] map = sessionGame.get(sessionId);
        boolean foundTheLine = false;
        for (int i = 0; i < mapSize; i ++){
            for (int j = 0; j < mapSize; j++){
                if (map[i][j]==sign){
                    foundTheLine = checkTheLine(i, j, 0, 0, mapSize, 0, map, sign);
                    if (foundTheLine){return foundTheLine;}
                }
            }
        }
        return foundTheLine;
    }

    public boolean checkTheLine(int posY, int posX, int posYDiff, int posXDiff,
                                int mapSize, int lineCheckSize, int[][] map, int sign){
        boolean checkAny = true;
        if (lineCheckSize > 0){
            checkAny = false;
        }
        if (checkAny){
            boolean foundTheLine = false;
            for (int i = posY-1; i <=posY+1; i++){
                for (int j = posX-1; j<=posX+1; j++){
                    try{
                        if (map[i][j] == sign){
                            if (posX==j && posY==i){continue;}
                            lineCheckSize=1;
                            foundTheLine = checkTheLine(i, j, i-posY, j-posX,
                                    mapSize, lineCheckSize, map, sign);
                            if (foundTheLine){
                                return true;
                            }
                        }
                    } catch (IndexOutOfBoundsException e){
                    }
                }
            }
            return foundTheLine;
        } else {
            try{
                if (map[posY][posX]==sign){
                    lineCheckSize++;
                } else {
                    return false;
                }
                if (mapSize == 3 && lineCheckSize == 3){
                    return true;
                } else if (mapSize == 15 && lineCheckSize == 5){
                    return true;
                } else {
                    return checkTheLine(posY + posYDiff, posX + posXDiff, posYDiff, posXDiff, mapSize,
                            lineCheckSize, map, sign);
                }
            } catch (IndexOutOfBoundsException e){
            }
            return false;
        }
    }

    public boolean mapIsFull(int mapSize, Long sessionId){
        int[][] map = sessionGame.get(sessionId);
        int emptySlots = 0;
        for (int i = 0; i < mapSize; i++){
            for(int j = 0; j < mapSize; j++){
                if (map[i][j] == 0){
                    emptySlots++;
                }
            }
        }
        if (emptySlots == 0){return true;}
        else {return false;}
    }

    public GameSession getSession(Long id){
        Optional<GameSession> gameSession = sessionRepository.findById(id);
        return gameSession.orElse(new GameSession());
    }

    public List<GameSession> getGameSessionsById(Long id){
        return sessionRepository.findSessionsById(id);
    }

    public Long saveSession(GameSession session){
        return sessionRepository.save(session).getId();
    }

}
