package telegramBot.dao;

import telegramBot.entity.Remind;

public interface DAO {
    boolean save(Object obj1, Object obj2);
    Object getObjectByID(int id);
    boolean deleteByID(int id);
    boolean update(Object obj);
}
