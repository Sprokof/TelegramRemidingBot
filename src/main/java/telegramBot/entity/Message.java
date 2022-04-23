package telegramBot.entity;


import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import javax.persistence.*;

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
    @Column(name = "REMIND_ID")
    private String remindId;
    @Column(name = "MESSAGE_ID")
    private Integer messageId;
    @Column(name = "IS_REMIND_MESSAGE")
    private boolean isRemindMessage;


    public Message(String chatId, String remindId, Integer messageId, boolean isRemindMessage){
        this.messageId = messageId;
        this.remindId = remindId;
        this.chatId = chatId;
        this.isRemindMessage = isRemindMessage;
    }

    public Message(String chatId, Integer messageId){
        this.chatId = chatId;
        this.messageId = messageId;
    }


    public Message buildSecondConstructorMessage(){
        return new Message(this.getChatId(), this.getMessageId());
    }

    @Override
    public boolean equals(Object obj) {
        if(this == obj) return true;
        if(!(obj instanceof Message)) return false;
        Message m = (Message) obj;
        return this.remindId.equals(m.remindId);
    }
}
