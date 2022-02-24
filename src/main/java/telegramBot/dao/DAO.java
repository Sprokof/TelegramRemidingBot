package telegramBot.dao;

import telegramBot.entity.Remind;

public interface DAO {
    boolean save(Object o);
    Object getObjectByID(int id);
    boolean deleteByID(int id);
    boolean update(Object o);
}
