package telegramBot.entity;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import javax.persistence.*;

@Entity
@NoArgsConstructor
@Getter
@Setter
@Table(name = "DETAILS")
public class Details {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;
    @Column(name = "CHAT_ID_TO_SEND")
    private Integer chatIdToSend;
    @Column(name = "TIME_TO_SEND")
    private String timeToSend;
    @Column(name = "LAST_SEND_TIME")
    private String lastSendTime;
    @Column(name = "COUNT_SEND_OF_REMIND")
    private int countSendOfRemind;
    @Column(name = "IS_STOP")
    private String isStop;

    public Details(Integer chatIdToSend, boolean timeToSend,
                   String lastSendTime, int countSendOfRemind, boolean isStop) {
        this.chatIdToSend = chatIdToSend;
        this.timeToSend = String.valueOf(timeToSend);
        this.lastSendTime = lastSendTime;
        this.countSendOfRemind = countSendOfRemind;
        this.isStop = String.valueOf(isStop);

    }

    @Override
    public String toString() {
        return "Details{" +
                "id=" + id +
                ", chatIdToSend='" + chatIdToSend + '\'' +
                ", timeToSend='" + timeToSend + '\'' +
                ", lastSendTime='" + lastSendTime + '\'' +
                ", countSendOfRemind=" + countSendOfRemind +
                ", isStop='" + isStop + '\'' +
                '}';
    }

    public int getId(){
        return Integer.parseInt(this.toString().
                substring(this.toString().indexOf("=") + 1,
                        this.toString().indexOf(",")));
    }
}