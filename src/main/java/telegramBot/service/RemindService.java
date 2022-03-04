package telegramBot.service;

import telegramBot.entity.Details;
import telegramBot.entity.Remind;

import java.util.List;

public interface RemindService {
    boolean saveRemind(Remind remind, Details details);
    void deleteRemind(int index);
    void updateRemindDateField(Remind remind, String newDate);
    void updateMaintenanceField(Remind remind, String first_part, String second_part);
    List<Remind> getAllRemindsFromDB();
    Remind getRemindById(int id);
    List<Remind> getAllExecutingRemindsByChatId(String chatId);
    boolean isContainsInDB(Remind remind);
    void updateTimeToSendField(Remind remind, boolean flag);
    void updateCountSendField(Remind remind, int count);
    void updateSendHourField(Remind remind, int hour);
    void updateIsStopField(Remind remind, boolean flag);

}
