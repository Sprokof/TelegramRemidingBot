package telegramBot.entity;


import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;

@Getter
@Setter
@Entity
@NoArgsConstructor
public class Message{
    @Id
    private int id;
    private  String chatId;
    private Integer messageId;


    public Message(String chatId, Integer messageId){
        this.messageId = messageId;
        this.chatId = chatId;

    }
}
