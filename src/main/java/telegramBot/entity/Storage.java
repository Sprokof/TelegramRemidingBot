package telegramBot.entity;

import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@Entity
@Table(name = "INTS_STORAGE")
@NoArgsConstructor
@Data
public class Storage {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;
    @Column(name = "RANDOM_INTS")
    private String randomInts;

    public Storage(String randomInts) {
        this.randomInts = randomInts;
    }

    public boolean isFull(){
        return randomInts.length() >= 30;
    }
}
