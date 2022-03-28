package telegramBot.entity;


import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import telegramBot.crypt.XORCrypt;

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
    @Column(name = "ENCRYPTED_MAINTENANCE")
    private String encrypted_maintenance;
    @Column(name = "KEY_TO_DECRYPT")
    private String key;
    @Column(name = "MESSAGE_ID")
    private Integer messageId;


    public Message(String chatId, String encrypted_maintenance, String key, Integer messageId){
        this.messageId = messageId;
        this.encrypted_maintenance = encrypted_maintenance;
        this.key = key;
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
        String thisDecrypt = XORCrypt.decrypt(XORCrypt.
                stringToIntArray(this.encrypted_maintenance), this.key);
        String mDecrypt = XORCrypt.decrypt(XORCrypt.
                stringToIntArray(m.encrypted_maintenance), m.key);
        return thisDecrypt.equals(mDecrypt) && this.chatId.equals(m.chatId);
    }
}
