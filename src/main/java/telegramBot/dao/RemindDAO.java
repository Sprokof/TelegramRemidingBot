package telegramBot.dao;

import telegramBot.entity.Details;
import telegramBot.entity.Remind;
import telegramBot.entity.User;

import java.util.List;

public interface RemindDAO {
    boolean save(Remind remind);
    Object getRemindByID(int id);
    boolean deleteByID(int id);
    boolean update(Remind remind);
    List<Remind> getAllExecutingReminds(Remind remind);
    List<Integer> getIdOfAllReminds();
    List<Remind> getAllRemindsFromDB();
    boolean isExistRemind(User user, Remind remind, Details details);
}
