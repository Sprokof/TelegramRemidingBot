package telegramBot.service;

import telegramBot.entity.Remind;

import java.util.List;

public interface RemindService {
    boolean saveRemind(Remind remind);
    void deleteRemind(int[] arrayId, int index, int[] newArrayId) throws Exception;
    void deleteRemind(int index);
    void updateDate(Remind remind, String newDate);
    void updateMaintenance(Remind remind, String newMaintenance);
    List<Remind> getAllRemindsFromDB();
    Remind getRemindById(int id);
    boolean isContainsInDB(Remind remind);
}
