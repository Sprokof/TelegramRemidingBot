package telegramBot.entity;


import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import telegramBot.crypt.XORCrypt;

import javax.persistence.*;
import java.util.Arrays;
import java.util.stream.Collectors;

@Getter
@Setter
@Entity
@NoArgsConstructor
@Table(name = "MESSAGES")
@ToString
public class Message{
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;
    @Column(name = "CHAT_ID")
    private  String chatId;
    @Column(name = "ID_OF_REMIND")
    private String remindId;
    @Column(name = "MESSAGE_ID")
    private Integer messageId;


    public Message(String chatId, String remindId, Integer messageId){
        this.messageId = messageId;
        this.remindId = remindId;
        this.chatId = chatId;
    }

    public Message(String chatId, Integer messageId){
        this.chatId = chatId;
        this.messageId = messageId;
    }

    @Override
    public boolean equals(Object obj) {
        if(this == obj) return true;
        if(!(obj instanceof Message)) return false;
        Message m = (Message) obj;
        Integer[] a1 = Arrays.
                stream(this.remindId.split("\\p{P}")).
                map(Integer::parseInt).collect(Collectors.toList()).toArray(Integer[]::new);
        Integer[] a2 = Arrays.
                  stream(m.remindId.split("\\p{P}")).map(Integer::parseInt).
                collect(Collectors.toList()).toArray(Integer[]::new);
        return  Arrays.equals(a1, a2);
    }
}
