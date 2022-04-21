package telegramBot.service;

import telegramBot.entity.Details;
import telegramBot.entity.Remind;
import telegramBot.entity.User;

import java.util.List;

public interface RemindService {
    boolean saveRemind(Remind remind);
    void deleteRemind(int index);
    void updateRemindDateField(Remind remind, String newDate);
    List<Remind> getAllRemindsFromDB();
    Remind getRemindById(int id);
    List<Remind> getAllExecutingReminds(Remind remind);
    List<Integer> getIdOfAllReminds();
    String getMaxTime(Remind remind);
    boolean isExistRemind(Remind remind, Details details);
    void updateTimeToSendField(Remind remind, boolean flag);
    void updateCountSendField(Remind remind, int count);
    void updateSendHourField(Remind remind, String time);
}
