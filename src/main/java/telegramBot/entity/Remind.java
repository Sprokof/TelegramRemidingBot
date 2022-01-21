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


    public Remind(String userChatID, String maintenance, String remindDate) {
        this.userChatID = userChatID;
        this.remindDate = remindDate;
        this.maintenance = maintenance;}

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
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Remind remind = (Remind) o;
        return id == remind.id && Objects.equals(maintenance, remind.maintenance) && Objects.equals(remindDate, remind.remindDate)
                && Objects.equals(userChatID, remind.userChatID);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, maintenance, remindDate, userChatID);
    }
}

