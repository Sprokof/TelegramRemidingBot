package telegramBot.hidenPackage.service;

import telegramBot.hidenPackage.entity.RemindDPer;

public interface RemindService {
    RemindDPer getRemindById(int id);
    boolean updateRemindDateField(RemindDPer remindDPer, String nextDate);
    boolean updateCountSendField(RemindDPer remindDPer, int count);
}
