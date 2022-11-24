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
@Table(name = "REMINDS_DETAILS")
public class Details {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;
    @Column(name = "TIME_TO_SEND")
    private boolean timeToSend;
    @Column(name = "LAST_SEND_TIME")
    private String lastSendTime;
    @Column(name = "COUNT_SEND_OF_REMIND")
    private int countSendOfRemind;
    @Column(name = "KEY_TO_DECRYPT")
    private String key;

    public Details(String key, boolean timeToSend, int countSendOfRemind) {
        this.key = key;
        this.timeToSend = timeToSend;
        this.countSendOfRemind = countSendOfRemind;
    }

    @Override
    public String toString() {
        return "Details{" +
                "id=" + id +
                ", timeToSend='" + timeToSend + '\'' +
                ", lastSendTime='" + lastSendTime + '\'' +
                ", countSendOfRemind=" + countSendOfRemind;
    }

    public int getId(){
        return Integer.parseInt(this.toString().
                substring(this.toString().indexOf("=") + 1,
                        this.toString().indexOf(",")));
    }
}