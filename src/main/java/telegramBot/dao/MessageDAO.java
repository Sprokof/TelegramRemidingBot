package telegramBot.dao;


import telegramBot.entity.Message;
import telegramBot.entity.User;

import java.util.List;

public interface MessageDAO {
    void save(Message message);
    void deleteAllMessages();
    void deleteMessageByMessageId(Integer messageId);
    List<Message> getAllRemindMessages();
    Message getMessageByChatAndRemindId(String chatId, String remindId);
    void updateMessage(Message message);
    boolean isSentMessage(Message message);
    void deleteAllNotRemindMessage(User user);
    List<Message> getAllNotRemindMessage(User user);
    List<Message> getRemindMessagesByChatId(String chatId);
    Message deleteLastSendMessage(User user);
}
