package telegramBot.hidenPackage.dao;

import telegramBot.hidenPackage.entity.RemindDPer;

public interface Dao {
    RemindDPer getRemindById(int id);
    boolean updateRemind(RemindDPer remindDPer);
}
