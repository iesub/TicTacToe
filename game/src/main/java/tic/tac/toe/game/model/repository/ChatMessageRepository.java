package tic.tac.toe.game.model.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import tic.tac.toe.game.model.entity.ChatMessage;

import java.util.List;

public interface ChatMessageRepository extends JpaRepository<ChatMessage, Long> {
    @Query(value="select * from chat_message order by id desc LIMIT ?2 offset ?1", nativeQuery = true)
    public List<ChatMessage> findWithOffset(int offset, int limit);
}
