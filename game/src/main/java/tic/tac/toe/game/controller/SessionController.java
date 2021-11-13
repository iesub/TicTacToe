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
import tic.tac.toe.game.dto.GameSessionMessageDTO;
import tic.tac.toe.game.entity.GameSession;
import tic.tac.toe.game.entity.User;
import tic.tac.toe.game.enumiration.GameSessionMessageTypes;
import tic.tac.toe.game.service.QueueService;
import tic.tac.toe.game.service.GameSessionService;
import tic.tac.toe.game.service.UserService;

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
    public void workWith3x3NoRank(SimpMessageHeaderAccessor headerAccessor,
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
                String[] position = message.getMessage().split("-");
                sessionService.putPoint(sessionId, Integer.parseInt(position[0]), Integer.parseInt(position[1]));
                if (session.getCrossSidePlayer().getId().equals(user.getId())){
                    simpMessagingTemplate.convertAndSendToUser(session.getCircleSidePlayer().getUsername(),
                            "/client/game/"+sessionId+"/"+gameType,
                            message);
                } else {
                    simpMessagingTemplate.convertAndSendToUser(session.getCrossSidePlayer().getUsername(),
                            "/client/game/"+sessionId+"/"+gameType,
                            message);
                }
                break;
        }
    }
}
