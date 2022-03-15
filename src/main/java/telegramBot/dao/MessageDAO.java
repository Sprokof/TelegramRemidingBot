package telegramBot.dao;


import telegramBot.entity.Message;

import java.util.List;

public interface MessageDAO {
    void save(Message message);
    void deleteMessage(Message message);
    void deleteByMessageId(Integer messageId);
    List<Message> getAllMessages();
}
