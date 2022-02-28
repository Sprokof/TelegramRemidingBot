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
    @Column(name = "MAINTENANCE")
    private String maintenance;
    @Column(name = "REMIND_DATE")
    private String remindDate;

    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "details_id")
    private Details details;

    public Remind(String maintenance, String remindDate){
        this.maintenance = maintenance;
        this.remindDate = remindDate;
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

