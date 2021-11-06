package tic.tac.toe.game.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.simp.SimpMessageHeaderAccessor;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.security.core.context.SecurityContextImpl;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import tic.tac.toe.game.entity.GameSession;
import tic.tac.toe.game.entity.User;
import tic.tac.toe.game.enumiration.GameTypesEnum;
import tic.tac.toe.game.service.QueueService;
import tic.tac.toe.game.service.GameSessionService;

import java.util.Map;
import java.util.Random;

import static java.lang.String.valueOf;

@Controller
public class SessionFinderController {
    @Autowired
    QueueService queueService;
    @Autowired
    GameSessionService sessionService;
    @Autowired
    private SimpMessagingTemplate simpMessagingTemplate;

    @GetMapping("/3x3-no-rank-finder")
    public String noRan3x3(){
        return "3x3-no-rank-finder";
    }

    @MessageMapping("/no-rank-queue-3x3.add-to-queue")
    public void subscribeOnQueue3x3(SimpMessageHeaderAccessor headerAccessor,
                                    @Payload int message){
        Map<String, Object> attrs = headerAccessor.getSessionAttributes();
        SecurityContextImpl securityContext = (SecurityContextImpl) attrs.get("SPRING_SECURITY_CONTEXT");
        User user = (User) securityContext.getAuthentication().getPrincipal();
        if (attrs.get("subscribedTo3x3NoRank") == null) {
            attrs.put("subscribedTo3x3NoRank", 1);
            headerAccessor.setSessionAttributes(attrs);
            queueService.addToQueue3x3NoRank(user);
        }
        if (queueService.getQueue3x3NRank().size() > 1){
            User opponent = queueService.getQueue3x3NRank().get(0);
            Random random = new Random();
            GameSession session;
            if (random.nextBoolean()){
                session = new GameSession(GameTypesEnum.NO_RANK_3x3, user, opponent);
            } else {
                session = new GameSession(GameTypesEnum.NO_RANK_3x3, opponent, user);
            }
            Long id = sessionService.saveSession(session);
            String sessionId = valueOf(id);
            String sessionLink = "/session/"+ sessionId;
            simpMessagingTemplate.convertAndSendToUser(securityContext.getAuthentication().getName()
                    , "/client/queue/no-rank-3x3-redirect", sessionLink);
            simpMessagingTemplate.convertAndSendToUser(opponent.getUsername()
                    , "/client/queue/no-rank-3x3-redirect", sessionLink);
        } else {
            return;
        }
    }

}
