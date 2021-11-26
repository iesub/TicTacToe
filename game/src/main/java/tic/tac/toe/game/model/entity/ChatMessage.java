package tic.tac.toe.game.model.entity;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;

@Setter
@Getter
@Entity
@Table(name = "chat_message")
public class ChatMessage {
    @Id
    @Column(name = "id")
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id = Long.valueOf(0);
    @Column(name = "content")
    private String message;
    @ManyToOne()
    @JoinColumn(name = "user_id", referencedColumnName = "id")
    private User sender;

    public ChatMessage(){}

    public ChatMessage(String message, User sender){
        this.message = message;
        this.sender = sender;
    }

}
