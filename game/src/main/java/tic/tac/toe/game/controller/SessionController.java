package tic.tac.toe.game.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.simp.SimpMessageHeaderAccessor;
import org.springframework.security.core.context.SecurityContextImpl;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import tic.tac.toe.game.entity.GameSession;
import tic.tac.toe.game.entity.User;
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

    @GetMapping("/session/{sessionId}")
    public String buildSession(@PathVariable("sessionId") Long id, Model model, HttpSession httpSession){
        SecurityContextImpl securityContext = (SecurityContextImpl) httpSession.getAttribute("SPRING_SECURITY_CONTEXT");
        User currentUser = (User) securityContext.getAuthentication().getPrincipal();
        GameSession session = sessionService.getSession(id);
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

    @MessageMapping("/game/{sessionId}/no-rank-game-3x3.message")
    public void subscribeOnGame3x3(SimpMessageHeaderAccessor headerAccessor,
                                    @Payload int message, @DestinationVariable("sessionId") Long sessionId){
        Map<String, Object> attrs = headerAccessor.getSessionAttributes();
        attrs.put("subscribeToGame3x3NoRank", 1);
        headerAccessor.setSessionAttributes(attrs);
    }
}
