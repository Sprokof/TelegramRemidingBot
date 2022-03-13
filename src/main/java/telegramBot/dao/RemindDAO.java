package telegramBot.dao;

import telegramBot.entity.Details;
import telegramBot.entity.Remind;

public interface RemindDAO {
    boolean save(Remind remind , Details details);
    Object getObjectByID(int id);
    boolean deleteByID(int id);
    boolean update(Remind remind);
}
