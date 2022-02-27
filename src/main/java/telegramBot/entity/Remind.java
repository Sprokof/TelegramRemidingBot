package telegramBot.entity;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;

@Entity
@Table(name = "REMINDERS")
@NoArgsConstructor
@Getter
@Setter
public class Remind {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;


    @Column(name = "CHAT_ID_TO_SEND")
    private String chatIdToSend;
    @Column(name = "TIME_TO_SEND")
    private String timeToSend;
    @Column(name = "MAINTENANCE")
    private String maintenance;
    @Column(name = "REMIND_DATE")
    private String remindDate;
    @Column(name = "LAST_SEND_HOUR")
    private int lastSendHour;
    @Column(name = "COUNT_SEND_OF_REMIND")
    private int countSendOfRemind;
    @Column(name = "IS_STOP")
    private String isStop;

    public Remind(String chatIdToSend, String maintenance, String remindDate,
                  String timeToSend, int countSendOfRemind, int lastSendHour, String isStop){
        this.isStop = isStop;
        this.chatIdToSend = chatIdToSend;
        this.maintenance = maintenance;
        this.remindDate = remindDate;
        this.timeToSend = timeToSend;
        this.countSendOfRemind = countSendOfRemind;
        this.lastSendHour = lastSendHour;
    }

    @Override
    public String toString() {
        return "Remind{" +
                "id=" + id +
                ", maintenance=" + maintenance + '\'' +
                ", remindDate=" + remindDate + '\'';
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (! (obj instanceof Remind)) return false;
        Remind remind = (Remind) obj;
        return this.maintenance.equals(remind.maintenance) && this.remindDate.replaceAll("\\p{P}", "\\.").
                equals(remind.remindDate.replaceAll("\\p{P}", "\\."));
    }

    @Override
    public int hashCode() {
        char[] chArray = this.maintenance.toCharArray();
        int result = (int)Character.toUpperCase(chArray[0]);
        for(int i = 1; i<chArray.length; i++){
            result+=(int)chArray[i];}
        return result;
    }
}

