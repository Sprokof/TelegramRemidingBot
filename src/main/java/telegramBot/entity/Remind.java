package telegramBot.entity;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import java.util.Objects;

@Entity
@Table(name = "REMINDERS")
@NoArgsConstructor
public class Remind {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @Column(name = "USER_CHAT_ID")
    @Setter
    @Getter
    private String userChatID;

    @Column(name = "MAINTENANCE")
    @Setter
    @Getter
    private String maintenance;

    @Column(name = "REMIND_DATE")
    @Setter
    @Getter
    private String remindDate;

    @Column(name = "COUNT_SEND")
    @Getter
    @Setter
    private int countSend;

    @Column(name = "TIME_TO_SEND")
    @Getter
    @Setter
    private String timeToSend;

    @Column(name = "SEND_HOUR")
    @Getter
    @Setter
    private int sendHour;

    public Remind(String userChatID, String maintenance, String remindDate,
                  int countSend, String timeToSend, int sendHour) {
        this.userChatID = userChatID;
        this.remindDate = remindDate;
        this.maintenance = maintenance;
        this.countSend = countSend;
        this.timeToSend = timeToSend;
        this.sendHour = sendHour;}

    @Override
    public String toString() {
        return "Remind{" +
                "id=" + id +
                ", maintenance='" + maintenance + '\'' +
                ", noticeDate='" + remindDate + '\'' +
                ", userChatID='" + userChatID + '\'' +
                '}';
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (! (obj instanceof Remind)) return false;
        Remind remind = (Remind) obj;
        return this.userChatID.equals(remind.userChatID) &&
                this.maintenance.equals(remind.maintenance) && this.remindDate.
                equals(remind.remindDate.replaceAll("\\p{P}", "\\."));
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, maintenance, remindDate, userChatID);
    }
}

