package telegramBot.service;


import telegramBot.entity.Message;

import java.util.List;

public interface MessageService {
    void save(Message message);
    void deleteAllMessages();
    Message getMessageByNextField(String chatId, String maintenance);
    List<Message> getAllMessages();
}
