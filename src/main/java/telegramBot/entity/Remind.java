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
    @Column(name = "ENCRYPT_MAINTENANCE")
    private String encryptedMaintenance;
    @Column(name = "REMIND_DATE")
    private String remindDate;
    @Column(name = "KEY_TO_DECRYPT")
    private String key;

    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "details_id")
    private Details details;

    public Remind(String encryptedMaintenance, String remindDate, String key){
        this.encryptedMaintenance = encryptedMaintenance;
        this.remindDate = remindDate;
        this.key = key;
    }

    @Override
    public String toString() {
        return "Remind{" +
                "id=" + id +
                ", maintenance=" + (encryptedMaintenance) + '\'' +
                ", remindDate=" + remindDate + '\'';
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (! (obj instanceof Remind)) return false;
        Remind remind = (Remind) obj;
        return this.encryptedMaintenance.
                equals(remind.encryptedMaintenance) && this.remindDate.replaceAll("\\p{P}", "\\.").
                equals(remind.remindDate.replaceAll("\\p{P}", "\\."));
    }

    @Override
    public int hashCode() {
        char[] chArray = encryptedMaintenance.toCharArray();
        int result = (int)Character.toUpperCase(chArray[0]);
        for(int i = 1; i<chArray.length; i++){
            result+=(int)chArray[i];}
        return result;
    }
}

