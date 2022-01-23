package telegramBot.service;

import telegramBot.entity.Remind;

import java.util.List;

public interface RemindService {
    boolean saveRemind(Remind remind);
    void deleteRemind(int[] arrayId, int index, int[] newArrayId) throws Exception;
    void updateDate(Remind remind, String newDate);
    List<Remind> getAllRemindsFromDB();
    Remind getRemindById(int id);
}
