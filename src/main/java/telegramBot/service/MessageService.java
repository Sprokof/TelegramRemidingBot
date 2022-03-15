package telegramBot.service;


import telegramBot.entity.Message;

import java.util.List;

public interface MessageService {
    void save(Message message);
    void deleteMessage(Message message);
    void deleteMessageByMessageId(Integer messageId);
    List<Message> getAllMessages();
}
