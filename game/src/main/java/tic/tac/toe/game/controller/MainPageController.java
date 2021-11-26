package tic.tac.toe.game.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.messaging.simp.SimpMessageHeaderAccessor;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextImpl;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import tic.tac.toe.game.controller.dto.NickNameDTO;

import tic.tac.toe.game.controller.dto.WSChatMessage;
import tic.tac.toe.game.model.entity.ChatMessage;
import tic.tac.toe.game.model.entity.User;
import tic.tac.toe.game.controller.enumiration.NickNameDTOTypes;
import tic.tac.toe.game.controller.enumiration.WSChatMessageEnum;
import tic.tac.toe.game.model.service.MessagingService;
import tic.tac.toe.game.model.service.UserService;

import javax.servlet.http.HttpSession;
import java.util.*;


@Controller
public class MainPageController {
    @Autowired
    UserService userService;
    @Autowired
    MessagingService messagingService;
    @Autowired
    private SimpMessagingTemplate simpMessagingTemplate;

    @GetMapping("/")
    public String handleMainPage(Model model, HttpSession session){
        model.addAttribute("RankTable3x3", userService.get3x3RankList());
        model.addAttribute("RankTable15x15", userService.get15x15RankList());

        SecurityContextImpl securityContext = (SecurityContextImpl) session.getAttribute("SPRING_SECURITY_CONTEXT");
        if (securityContext == null) {return "index";}
        User user = (User) securityContext.getAuthentication().getPrincipal();
        model.addAttribute("currenUser", user);
        return "index";
    }

    @MessageMapping("/main-chat.send-message")
    @SendTo("/client/main-chat")
    public WSChatMessage sendMessage(SimpMessageHeaderAccessor headerAccessor,
                                     @Payload WSChatMessage message) {
        Map<String, Object> attrs = headerAccessor.getSessionAttributes();
        SecurityContextImpl securityContext = (SecurityContextImpl) attrs.get("SPRING_SECURITY_CONTEXT");
        User user = (User) securityContext.getAuthentication().getPrincipal();
        if (message.getMessage() == null){
            return new WSChatMessage(WSChatMessageEnum.IGNORE);
        }
        if (message.getMessage().length() > 1000){
            return new WSChatMessage(WSChatMessageEnum.IGNORE);
        }
        messagingService.saveNewMessage(new ChatMessage(message.getMessage(), user));
        message.setSenderNickname(user.getNickname());
        return message;
    }

    @MessageMapping("/main-chat.loadLastMessages")
    public void loadLastMessages(SimpMessageHeaderAccessor headerAccessor,
                                 @Payload int message) {

        ArrayList<WSChatMessage> messages = new ArrayList<>();
        List<ChatMessage> chatMessages = messagingService.getMessagesByOffset(message*20, 20);
        for (int i = 0; i < chatMessages.size(); i++){
            messages.add(new WSChatMessage(chatMessages.get(i).getSender().getNickname(),
                    chatMessages.get(i).getMessage()));
        }

        Map<String, Object> attrs = headerAccessor.getSessionAttributes();
        if (attrs.get("SPRING_SECURITY_CONTEXT") == null){return;}
        SecurityContextImpl spring_security_context = (SecurityContextImpl) attrs.get("SPRING_SECURITY_CONTEXT");
        Authentication authentication = spring_security_context.getAuthentication();
        String name = authentication.getName();
        simpMessagingTemplate.convertAndSendToUser(name,
                "/client/main-chat-load", messages);
    }

    @MessageMapping("/main-chat.update-nickname-list")
    public void updateNickNameList(SimpMessageHeaderAccessor headerAccessor,
                                   @Payload int message){
        Map<String, Object> attrs = headerAccessor.getSessionAttributes();
        if (attrs.get("SPRING_SECURITY_CONTEXT") == null){
            if (attrs.get("nicknameUpdateState") == null) {
                attrs.put("nicknameUpdateState", 1);
                headerAccessor.setSessionAttributes(attrs);
                simpMessagingTemplate.convertAndSend("/client/nickname-update",
                        new NickNameDTO(NickNameDTOTypes.ACCEPT_OTHERS));
            }
            return;
        }
        SecurityContextImpl spring_security_context = (SecurityContextImpl) attrs.get("SPRING_SECURITY_CONTEXT");
        Authentication authentication = spring_security_context.getAuthentication();
        User user = (User) authentication.getPrincipal();
        if (attrs.get("nicknameUpdateState") == null) { //при присоединении отправил сообщение всем остальным
            attrs.put("nicknameUpdateState", 1);
            headerAccessor.setSessionAttributes(attrs);
            simpMessagingTemplate.convertAndSend("/client/nickname-update", new NickNameDTO(user.getNickname()
                    , NickNameDTOTypes.SEND_TO_EVERYONE));
        } else if ((int)attrs.get("nicknameUpdateState") == 1){ //отправил всем, теперь получает от остальных, а сам отправляет пустое сообщение
            attrs.put("nicknameUpdateState", 2);
            headerAccessor.setSessionAttributes(attrs);
            simpMessagingTemplate.convertAndSend("/client/nickname-update",
                    new NickNameDTO(NickNameDTOTypes.IGNORE));
        } else { //уже подключился, все обновил, тут просто отправляет сообщение новеньким
                simpMessagingTemplate.convertAndSend("/client/nickname-update", new NickNameDTO(user.getNickname()
                        , NickNameDTOTypes.SEND_TO_NEW));
        }
    }
}
