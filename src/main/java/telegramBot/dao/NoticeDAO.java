package telegramBot.dao;

import telegramBot.entity.Notice;

// because i want only add entity in DB, interface has one save-method;
public interface NoticeDAO {
    boolean save(Notice notice);
    Notice getObjectByID(int id);
    boolean deleteByID(int id);
    boolean update(Notice notice);
}
