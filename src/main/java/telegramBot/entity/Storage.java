package telegramBot.entity;

import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@Entity
@Table(name = "DATES_STORAGE")
@NoArgsConstructor
@Data
public class Storage {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;
    @Column(name = "CURRENT_MONTH")
    private int currentMonth;
    @Column(name = "DAYS_TO_SEND")
    private String daysToSend;


    public static final int FULL = 5;
    public static final String MOCK = "MOCK";


    public Storage(int currentMonth, String daysToSend){
        this.currentMonth = currentMonth;
        this.daysToSend = daysToSend;
    }



}
