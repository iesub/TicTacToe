package tic.tac.toe.game.model.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import tic.tac.toe.game.model.entity.ChatMessage;
import tic.tac.toe.game.model.repository.ChatMessageRepository;

import java.util.List;

@Service
public class MessagingService {
    @Autowired
    ChatMessageRepository chatMessageRepository;

    public void saveNewMessage(ChatMessage message){
        chatMessageRepository.save(message);
    }

    public List<ChatMessage> getMessagesByOffset(int offset, int limit){
        return chatMessageRepository.findWithOffset(offset, limit);
    }
}
