package telegramBot.dao;

import telegramBot.entity.Remind;

// because i want only add entity in DB, interface has one save-method;
public interface RemindDAO {
    boolean save(Remind remind);
    Remind getObjectByID(int id);
    boolean deleteByID(int id);
    boolean update(Remind remind);
}
