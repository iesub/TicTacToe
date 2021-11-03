package tic.tac.toe.game.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import tic.tac.toe.game.entity.ChatMessage;
import tic.tac.toe.game.repository.ChatMessageRepository;

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
