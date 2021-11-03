package tic.tac.toe.game.listener;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationListener;
import org.springframework.messaging.Message;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextImpl;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.messaging.SessionUnsubscribeEvent;
import tic.tac.toe.game.entity.User;
import tic.tac.toe.game.service.QueueService;

import java.util.Map;

@Component
public class UnsubscribeListener implements ApplicationListener<SessionUnsubscribeEvent> {
    @Autowired
    QueueService queueService;
    @Override
    public void onApplicationEvent(SessionUnsubscribeEvent event) {
        Message message = event.getMessage();
        StompHeaderAccessor headers = StompHeaderAccessor.wrap(message);
        Map<String, Object> sessionAtributes = headers.getSessionAttributes();
        SecurityContextImpl spring_security_context = (SecurityContextImpl) sessionAtributes.get("SPRING_SECURITY_CONTEXT");
        Authentication authentication = spring_security_context.getAuthentication();
        User user = (User) authentication.getPrincipal();
        if ((Integer)sessionAtributes.get("subscribedTo3x3NoRank") == 1){
            queueService.deleteFrom3x3NoRank(user);
            sessionAtributes.remove("subscribedTo3x3NoRank");
            System.out.println("Удален из 3x3 без ранга");
        }

        headers.setSessionAttributes(sessionAtributes);
    }
}
