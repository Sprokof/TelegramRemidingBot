package telegramBot.entity;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import telegramBot.validate.Validate;

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
    @Column(name = "FIRST_PART_OF_MAINTENANCE")
    private String first_part;
    @Column(name = "SECOND_PART_OF_MAINTENANCE")
    private String second_part;
    @Column(name = "REMIND_DATE")
    private String remindDate;

    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "details_id")
    private Details details;

    public Remind(String first_part, String second_part, String remindDate){
        this.first_part = first_part;
        this.second_part = second_part;
        this.remindDate = remindDate;
    }

    @Override
    public String toString() {
        return "Remind{" +
                "id=" + id +
                ", maintenance=" + (this.first_part + this.second_part) + '\'' +
                ", remindDate=" + remindDate + '\'';
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (! (obj instanceof Remind)) return false;
        Remind remind = (Remind) obj;
        return (this.first_part+this.second_part).
                equals(remind.first_part+remind.second_part) && this.remindDate.replaceAll("\\p{P}", "\\.").
                equals(remind.remindDate.replaceAll("\\p{P}", "\\."));
    }

    @Override
    public int hashCode() {
        char[] chArray = Validate.decodedMaintenance(this.first_part+this.second_part).toCharArray();
        int result = (int)Character.toUpperCase(chArray[0]);
        for(int i = 1; i<chArray.length; i++){
            result+=(int)chArray[i];}
        return result;
    }
}

