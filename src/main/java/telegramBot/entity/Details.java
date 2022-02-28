package telegramBot.entity;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import javax.persistence.*;

@Entity
@NoArgsConstructor
@ToString
@Getter
@Setter
@Table(name = "DETAILS")
public class Details {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;
    @Column(name = "CHAT_ID_TO_SEND")
    private String chatIdToSend;
    @Column(name = "TIME_TO_SEND")
    private String timeToSend;
    @Column(name = "LAST_SEND_HOUR")
    private int lastSendHour;
    @Column(name = "COUNT_SEND_OF_REMIND")
    private int countSendOfRemind;
    @Column(name = "IS_STOP")
    private String isStop;

    public Details(String chatIdToSend, String timeToSend,
                   int lastSendHour, int countSendOfRemind, String isStop) {
        this.chatIdToSend = chatIdToSend;
        this.timeToSend = timeToSend;
        this.lastSendHour = lastSendHour;
        this.countSendOfRemind = countSendOfRemind;
        this.isStop = isStop;

    }
}