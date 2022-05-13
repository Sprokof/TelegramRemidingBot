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
    @Column(name = "DATES_TO_SEND")
    private String datesToSend;

    public Storage(int currentMonth, String datesToSend){
        this.currentMonth = currentMonth;
        this.datesToSend = datesToSend;
    }

}
