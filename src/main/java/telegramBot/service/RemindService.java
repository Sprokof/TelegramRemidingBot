package telegramBot.service;

import telegramBot.entity.Details;
import telegramBot.entity.Remind;
import telegramBot.entity.User;

import java.util.List;

public interface RemindService{
    boolean saveRemind(Remind remind);
    void deleteRemind(int id);
    RemindServiceImpl updateRemindDateField(Remind remind, String newDate);
    List<Remind> getAllRemindsFromDB();
    Remind getRemindById(int id);
    List<Remind> getAllExecutingReminds(Remind remind);
    List<Integer> getIdOfAllReminds();
    void extendsLastSendTimeIfAbsent(Remind remind);
    boolean isExistRemind(User user, Remind remind);
    RemindServiceImpl updateTimeToSendField(Remind remind, boolean flag);
    RemindServiceImpl updateCountSendField(Remind remind, int count);
    void updateSendHourField(Remind remind, String time);
    void markAsExecuted(Remind remind);
    boolean remindExecuted(Remind remind);
    Remind findExecutedRemindByDate(String date);
    boolean sendRemind(String chatId, String maintenance);
    void deleteExecutedRemind(Remind remind);
}

