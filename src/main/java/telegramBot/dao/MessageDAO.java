package telegramBot.dao;


import telegramBot.entity.Message;

import java.util.List;

public interface MessageDAO {
    void save(Message message);
    void deleteAllMessages();
    List<Message> getAllMessages();
    Message getMessageByChatIdAndMaintenance(String chatId, String maintenance);
}
