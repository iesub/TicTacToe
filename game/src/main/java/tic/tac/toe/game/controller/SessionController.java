package tic.tac.toe.game.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.simp.SimpMessageHeaderAccessor;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.security.core.context.SecurityContextImpl;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import tic.tac.toe.game.controller.dto.GameSessionMessageDTO;
import tic.tac.toe.game.model.entity.GameSession;
import tic.tac.toe.game.model.entity.User;
import tic.tac.toe.game.controller.enumiration.GameSessionMessageTypes;
import tic.tac.toe.game.model.service.QueueService;
import tic.tac.toe.game.model.service.GameSessionService;
import tic.tac.toe.game.model.service.UserService;

import javax.servlet.http.HttpSession;
import java.util.Map;

@Controller
public class SessionController {
    @Autowired
    GameSessionService sessionService;
    @Autowired
    QueueService queueService;
    @Autowired
    UserService userService;
    @Autowired
    private SimpMessagingTemplate simpMessagingTemplate;

    @GetMapping("/session/{sessionId}")
    public String buildSession(@PathVariable("sessionId") Long id, Model model, HttpSession httpSession){

        GameSession session = sessionService.getSession(id);


        if (session.getGameWinner() != null){
            return "redirect:/";
        }

        SecurityContextImpl securityContext = (SecurityContextImpl) httpSession.getAttribute("SPRING_SECURITY_CONTEXT");
        User currentUser = (User) securityContext.getAuthentication().getPrincipal();
        model.addAttribute("currenUser", currentUser);
        if (!session.getCrossSidePlayer().getId().equals(currentUser.getId()) &&
                !session.getCircleSidePlayer().getId().equals(currentUser.getId())){
            return "redirect:/";
        }
        model.addAttribute("currentUser", currentUser);
        model.addAttribute("gameType", session.getGameType());
        model.addAttribute("sessionId", session.getId());
        if (session.getCrossSidePlayer().getId().equals(currentUser.getId())){
            model.addAttribute("opponentCircle", session.getCircleSidePlayer());
        } else {
            model.addAttribute("opponentCross", session.getCrossSidePlayer());
        }
        return "session";
    }

    @MessageMapping("/game/{sessionId}/{gameType}.message")
    public void workWithGame(SimpMessageHeaderAccessor headerAccessor,
                                  @Payload GameSessionMessageDTO message, @DestinationVariable("sessionId") Long sessionId,
                                  @DestinationVariable("gameType") String gameType){
        GameSession session = sessionService.getSession(sessionId);
        Map<String, Object> attrs = headerAccessor.getSessionAttributes();
        SecurityContextImpl securityContext = (SecurityContextImpl) attrs.get("SPRING_SECURITY_CONTEXT");
        User user = (User) securityContext.getAuthentication().getPrincipal();
        switch (message.getMessageType()){
            case SUBSCRIBE:
                if (session.getGameWinner()!=null){
                    simpMessagingTemplate.convertAndSendToUser(user.getUsername(),
                            "/client/game/"+sessionId+"/"+gameType,
                            new GameSessionMessageDTO(GameSessionMessageTypes.LOOSE_MESSAGE_D));
                }
                if (session.getCrossSidePlayer().getId().equals(user.getId())){
                    message.setMessage("cross");
                } else {
                    message.setMessage("circle");
                }
                simpMessagingTemplate.convertAndSendToUser(user.getUsername(),
                        "/client/game/"+sessionId+"/"+gameType,
                        message);
                break;
            case DISCONNECT:
                if (session.getGameWinner() != null){
                    return;
                }
                User winner;
                if (session.getCrossSidePlayer().getId().equals(user.getId())){
                    session.setGameWinner(session.getCircleSidePlayer());
                    winner = session.getCircleSidePlayer();
                } else {
                    session.setGameWinner(session.getCrossSidePlayer());
                    winner = session.getCrossSidePlayer();
                }
                sessionService.saveSession(session);
                if (gameType.equals("no-rank-3x3")){winner.setGamesWonNoRank3x3(winner.getGamesWonNoRank3x3()+1);}
                if (gameType.equals("rank-3x3")){winner.setGamesWonRank3x3(winner.getGamesWonNoRank3x3()+1);}
                if (gameType.equals("no-rank-15x15")){winner.setGamesWonNoRank15x15(winner.getGamesWonNoRank15x15()+1);}
                if (gameType.equals("rank-15x15")){winner.setGamesWonRank15x15(winner.getGamesWonNoRank15x15()+1);}
                userService.saveUser(winner);
                simpMessagingTemplate.convertAndSendToUser(winner.getUsername(),
                        "/client/game/"+sessionId+"/"+gameType,
                        new GameSessionMessageDTO(GameSessionMessageTypes.WINNING_MESSAGE_D));
                sessionService.deleteMap(sessionId);
                break;
            case CHECK_POSITION:
                if (session.getGameWinner() != null) {return;}
                int mapSize = 0;
                boolean gameWinner = false;
                String[] position = message.getMessage().split("-");
                if (gameType.equals("no-rank-3x3")){mapSize = 3;}
                if (gameType.equals("rank-3x3")){mapSize = 3;}
                if (gameType.equals("no-rank-15x15")){mapSize = 15;}
                if (gameType.equals("rank-15x15")){mapSize = 15;}
                if (session.getCrossSidePlayer().getId().equals(user.getId())){
                    sessionService.putPoint(sessionId, Integer.parseInt(position[0]), Integer.parseInt(position[1]), true);
                    simpMessagingTemplate.convertAndSendToUser(session.getCircleSidePlayer().getUsername(),
                            "/client/game/"+sessionId+"/"+gameType,
                            message);
                    if (sessionService.checkWinningCondition(sessionId, mapSize,1)){
                        simpMessagingTemplate.convertAndSendToUser(session.getCrossSidePlayer().getUsername(),
                                "/client/game/"+sessionId+"/"+gameType,
                                new GameSessionMessageDTO(GameSessionMessageTypes.WINNING_MESSAGE));
                        session.setGameWinner(session.getCrossSidePlayer());
                        simpMessagingTemplate.convertAndSendToUser(session.getCircleSidePlayer().getUsername(),
                                "/client/game/"+sessionId+"/"+gameType,
                                new GameSessionMessageDTO(GameSessionMessageTypes.LOOSE_MESSAGE));
                        gameWinner = true;
                    }
                    if (sessionService.mapIsFull(mapSize, sessionId) && !gameWinner){
                        simpMessagingTemplate.convertAndSend("/client/game/"+sessionId+"/"+gameType,
                                new GameSessionMessageDTO(GameSessionMessageTypes.GAME_FULL));
                        User userDraw = userService.findUserById(1L);
                        session.setGameWinner(userDraw);
                        sessionService.saveSession(session);
                        return;
                    }
                } else {
                    sessionService.putPoint(sessionId, Integer.parseInt(position[0]), Integer.parseInt(position[1]), false);
                    simpMessagingTemplate.convertAndSendToUser(session.getCrossSidePlayer().getUsername(),
                            "/client/game/"+sessionId+"/"+gameType,
                            message);
                    if (sessionService.checkWinningCondition(sessionId, mapSize,2)){
                        simpMessagingTemplate.convertAndSendToUser(session.getCrossSidePlayer().getUsername(),
                                "/client/game/"+sessionId+"/"+gameType,
                                new GameSessionMessageDTO(GameSessionMessageTypes.LOOSE_MESSAGE));
                        simpMessagingTemplate.convertAndSendToUser(session.getCircleSidePlayer().getUsername(),
                                "/client/game/"+sessionId+"/"+gameType,
                                new GameSessionMessageDTO(GameSessionMessageTypes.WINNING_MESSAGE));
                        session.setGameWinner(session.getCircleSidePlayer());
                        gameWinner = true;
                    }
                    if (sessionService.mapIsFull(mapSize, sessionId) && !gameWinner){
                        simpMessagingTemplate.convertAndSend("/client/game/"+sessionId+"/"+gameType,
                                new GameSessionMessageDTO(GameSessionMessageTypes.GAME_FULL));
                        User userDraw = userService.findUserById(1L);
                        session.setGameWinner(userDraw);
                        sessionService.saveSession(session);
                        return;
                    }
                }
                if (gameWinner){
                    user = session.getGameWinner();
                    User looser;
                    if (session.getCrossSidePlayer().equals(user)){
                        looser = session.getCircleSidePlayer();
                    } else {
                        looser = session.getCrossSidePlayer();
                    }
                    switch (session.getGameType()){
                        case NO_RANK_3x3:
                            user.setGamesWonNoRank3x3(user.getGamesWonNoRank3x3()+1);
                            break;
                        case RANK_3x3:
                            user.setGamesWonRank3x3(user.getGamesWonRank3x3()+1);
                            user.setRankScore3x3(user.getRankScore3x3()+25);
                            if (looser.getRankScore3x3() >= 25){
                                looser.setRankScore3x3(looser.getRankScore3x3()-25);
                            } else if (looser.getRankScore3x3() > 0){
                                looser.setRankScore3x3(0);
                            }
                            break;
                        case NO_RANK_15x15:
                            user.setGamesWonNoRank15x15(user.getGamesWonNoRank15x15()+1);
                            break;
                        case RANK_15x15:
                            user.setGamesWonRank15x15(user.getGamesWonRank15x15()+1);
                            user.setRankScore15x15(user.getRankScore15x15()+25);
                            if (looser.getRankScore15x15() >= 25){
                                looser.setRankScore15x15(looser.getRankScore15x15()-25);
                            } else if (looser.getRankScore15x15() > 0){
                                looser.setRankScore15x15(0);
                            }
                            break;
                    }
                    sessionService.saveSession(session);
                    userService.saveUser(user);
                    userService.saveUser(looser);
                }
                break;
        }
    }
}
