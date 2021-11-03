package tic.tac.toe.game.listener;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationListener;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageHeaders;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextImpl;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.messaging.SessionDisconnectEvent;
import tic.tac.toe.game.dto.NickNameDTO;
import tic.tac.toe.game.entity.User;
import tic.tac.toe.game.enumiration.NickNameDTOTypes;

import java.util.Map;

@Component
public class DisconnectListener implements ApplicationListener<SessionDisconnectEvent> {
    @Autowired
    private SimpMessagingTemplate simpMessagingTemplate;

    @Override
    public void onApplicationEvent(SessionDisconnectEvent event) {
        Message message = event.getMessage();
        StompHeaderAccessor headers = StompHeaderAccessor.wrap(message);
        Map<String, Object> sessionAtributes = headers.getSessionAttributes();
        SecurityContextImpl spring_security_context = (SecurityContextImpl) sessionAtributes.get("SPRING_SECURITY_CONTEXT");
        if (spring_security_context == null){return;}
        Authentication authentication = spring_security_context.getAuthentication();
        User user = (User) authentication.getPrincipal();
        NickNameDTO nickNameDTO = new NickNameDTO(user.getNickname(),
                NickNameDTOTypes.DELETE_ONE);
        simpMessagingTemplate.convertAndSend("/client/nickname-update", nickNameDTO);
    }
}
