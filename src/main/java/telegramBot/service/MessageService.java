package telegramBot.service;


import telegramBot.entity.Message;

import java.util.List;

public interface MessageService {
    void save(Message message);
    void deleteAllMessages();
    void deleteMessage(Message message);
    Message getMessageByNextField(String chatId, String maintenance);
    List<Message> getAllMessages();
}
