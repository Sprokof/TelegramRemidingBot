package telegramBot.entity;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

@Entity
@Table(name = "NOTIFICATIONS")
@NoArgsConstructor
public class Notice {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private int id;
    @Column(name = "MAINTENANCE")
    @Setter
    @Getter
    private String maintenance;
    @Column(name = "NOTICEDATE")
    @Setter
    @Getter
    private String noticeDate;
    @Column(name = "USERID")
    @Setter
    @Getter
    private String userChatID;

    public Notice(String userChatID, String maintenance, String noticeDate) {
        this.userChatID = userChatID;
        this.noticeDate = noticeDate;
        this.maintenance = maintenance;}

    @Override
    public String toString() {
        return "Notice{" +
                "id=" + id +
                ", maintenance='" + maintenance + '\'' +
                ", noticeDate='" + noticeDate + '\'' +
                ", userChatID='" + userChatID + '\'' +
                '}';
    }
}

