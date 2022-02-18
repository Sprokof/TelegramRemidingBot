package telegramBot.service;

import telegramBot.entity.Remind;

import java.util.List;

public interface RemindService {
    boolean saveRemind(Remind remind);
    void deleteRemind(int index);
    void updateRemindDateField(Remind remind, String newDate);
    void updateTimeToSendField(Remind remind, boolean flag);
    void updateCountSendField(Remind remind, int count);
    void updateSendHourFiled(Remind remind, int hour);
    void updateMaintenanceField(Remind remind, String maintenance);
    List<Remind> getAllRemindsFromDB();
    Remind getRemindById(int id);
    boolean isContainsInDB(Remind remind);

}
