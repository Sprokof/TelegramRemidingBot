package telegramBot.hidenPackage.entity;


import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.stereotype.Component;

import javax.persistence.*;

@Entity
@Table(name = "REMINDS_DEF_PER")
@NoArgsConstructor
@Getter
@Setter
@Component
public class RemindDPer {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @Column(name = "REMIND_ABOUT_TABLETS")
    private String remindAboutTablets;

    @Column(name = "REMIND_DATE")
    private String remindDate;

    @Column(name = "CHAT_ID_TO_SEND")
    private String chatId;

    @Column(name = "LAST_SEND_TIME")
    private String lastSendTime;

    @Column(name = "COUNT_SEND")
    private int count_send;

    public RemindDPer(String remindAboutTablets, String remindDate,
                      String chatId, String lastSendTime, int count_send) {
        this.remindAboutTablets = remindAboutTablets;
        this.remindDate = remindDate;
        this.chatId = chatId;
        this.lastSendTime = lastSendTime;
        this.count_send = count_send;
    }
}
