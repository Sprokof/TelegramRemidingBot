package telegramBot.service;

import telegramBot.entity.Details;
import telegramBot.entity.Remind;

import java.util.List;

public interface RemindService {
    boolean saveRemind(Remind remind, Details details);
    void deleteRemind(int index);
    void updateRemindDateField(Remind remind, String newDate);
    List<Remind> getAllRemindsFromDB();
    Remind getRemindById(int id);
    List<Remind> getAllExecutingRemindsByChatId(Integer chatId);
    List<Details> getAllNotExecutingDetailsByChatId(Integer chatId);
    boolean isExist(Remind remind);
    void updateTimeToSendField(Remind remind, boolean flag);
    void updateCountSendField(Remind remind, int count);
    void updateSendHourField(Remind remind, String time);
    void updateIsStopField(Remind remind, boolean flag);

}
