package telegramBot.entity;

import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "USERS")
@NoArgsConstructor
@Data
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;
    @Column(name = "CHAT_ID")
    private String chatId;
    @Column(name = "IS_ACTIVE")
    private boolean isActive;
    @Column(name = "IS_STARTED")
    private boolean isStarted;


    @OneToMany(cascade = CascadeType.MERGE, mappedBy = "user", fetch = FetchType.EAGER)
    private List<Remind> reminds;

    public void addRemind(Remind remind) {
        if (this.reminds == null) this.reminds = new ArrayList<>();
        this.reminds.add(remind);
        remind.setUser(this);

    }

    public void removeRemind(Remind remind){
        this.reminds.remove(remind);
        remind.setUser(null);
    }

    public User(String chatId, boolean isActive) {
        this.chatId = chatId;
        this.isActive = isActive;
        this.isStarted = false;
    }
}
