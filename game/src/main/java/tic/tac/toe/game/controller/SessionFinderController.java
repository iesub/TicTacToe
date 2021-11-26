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
import tic.tac.toe.game.model.entity.GameSession;
import tic.tac.toe.game.model.entity.User;
import tic.tac.toe.game.model.enumiration.GameTypesEnum;
import tic.tac.toe.game.model.service.QueueService;
import tic.tac.toe.game.model.service.GameSessionService;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static java.lang.String.valueOf;

@Controller
public class SessionFinderController {
    @Autowired
    QueueService queueService;
    @Autowired
    GameSessionService sessionService;
    @Autowired
    private SimpMessagingTemplate simpMessagingTemplate;

    @GetMapping("/game-finder/3x3-nr")
    public String noRan3x3(Model model){
        model.addAttribute("finderType", GameTypesEnum.NO_RANK_3x3);
        return "game-finder";
    }

    @GetMapping("/game-finder/3x3-r")
    public String ran3x3(Model model){
        model.addAttribute("finderType", GameTypesEnum.RANK_3x3);
        return "game-finder";
    }

    @GetMapping("/game-finder/15x15-nr")
    public String noRan15x15(Model model){
        model.addAttribute("finderType", GameTypesEnum.NO_RANK_15x15);
        return "game-finder";
    }

    @GetMapping("/game-finder/15x15-r")
    public String ran15x15(Model model){
        model.addAttribute("finderType", GameTypesEnum.RANK_15x15);
        return "game-finder";
    }

    @MessageMapping("/{finderType}.add-to-queue")
    public void subscribeOnQueue(SimpMessageHeaderAccessor headerAccessor,
                                    @Payload int message, @DestinationVariable("finderType") String finderType){
        Map<String, Object> attrs = headerAccessor.getSessionAttributes();
        SecurityContextImpl securityContext = (SecurityContextImpl) attrs.get("SPRING_SECURITY_CONTEXT");
        User user = (User) securityContext.getAuthentication().getPrincipal();
        List<User> currentQueue = new ArrayList<>();
        GameTypesEnum currentGame = GameTypesEnum.NO_RANK_3x3;
        if (attrs.get(finderType) == null) {
            attrs.put(finderType, 1);
            headerAccessor.setSessionAttributes(attrs);
            switch (finderType){
                case "no-rank-queue-3x3":
                    queueService.addToQueue3x3NoRank(user);
                    currentGame = GameTypesEnum.NO_RANK_3x3;
                    currentQueue = queueService.getQueue3x3NRank();
                    break;
                case "rank-queue-3x3":
                    queueService.addToQueue3x3Rank(user);
                    currentGame = GameTypesEnum.RANK_3x3;
                    currentQueue = queueService.getQueue3x3Rank();
                    break;
                case "no-rank-queue-15x15":
                    queueService.addToQueue15x15NoRank(user);
                    currentGame = GameTypesEnum.NO_RANK_15x15;
                    currentQueue = queueService.getQueue15x15NRank();
                    break;
                case "rank-queue-15x15":
                    queueService.addToQueue15x15Rank(user);
                    currentGame = GameTypesEnum.RANK_15x15;
                    currentQueue = queueService.getQueue15x15Rank();
                    break;
                default:
                    return;
            }
        }
        if (currentQueue.size() > 1){
            int[][] map;
            switch (finderType){
                case "no-rank-queue-3x3":
                    map = new int[3][3];
                    break;
                case "rank-queue-3x3":
                    map = new int[3][3];
                    break;
                case "no-rank-queue-15x15":
                    map = new int[15][15];
                    break;
                case "rank-queue-15x15":
                    map = new int[15][15];
                    break;
                default:
                    return;
            }
            User opponent = currentQueue.get(0);
            GameSession session;
            session = new GameSession(currentGame, user, opponent);
            Long id = sessionService.saveSession(session);
            sessionService.putGameMap(id, map);
            String sessionId = valueOf(id);
            String sessionLink = "/session/"+ sessionId;
            simpMessagingTemplate.convertAndSendToUser(securityContext.getAuthentication().getName()
                    , "/client/queue/"+finderType, sessionLink);
            simpMessagingTemplate.convertAndSendToUser(opponent.getUsername()
                    , "/client/queue/"+finderType, sessionLink);
        } else {
            return;
        }
    }

}
