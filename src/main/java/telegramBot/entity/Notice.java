package telegramBot.entity;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import java.util.Objects;

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
                ", maintenance='" + maintenance + '\'' +
                ", noticeDate='" + noticeDate + '\'' +
                ", userChatID='" + userChatID + '\'' +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Notice notice = (Notice) o;
        return id == notice.id && Objects.equals(maintenance, notice.maintenance) && Objects.equals(noticeDate, notice.noticeDate) && Objects.equals(userChatID, notice.userChatID);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, maintenance, noticeDate, userChatID);
    }
}

