package telegramBot.dao;

import telegramBot.entity.Remind;

public interface RemindDAO {
    boolean save(Object obj1, Object obj2);
    Object getObjectByID(int id);
    boolean deleteByID(int id);
    boolean update(Object obj);
}
