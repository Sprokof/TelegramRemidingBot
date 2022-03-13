package telegramBot.entity;


import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;

@Getter
@Setter
@Entity
@NoArgsConstructor
@Table(name = "MESSAGES")
public class Message{
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;
    @Column(name = "CHAT_ID")
    private  String chatId;
    @Column(name = "MESSAGE_ID")
    private Integer messageId;

    public Message(String chatId, Integer messageId){
        this.messageId = messageId;
        this.chatId = chatId;

    }
}
