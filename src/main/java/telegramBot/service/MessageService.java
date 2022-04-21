package telegramBot.service;


import telegramBot.entity.Message;
import telegramBot.entity.Remind;
import telegramBot.entity.User;
import telegramBot.manage.RemindManage;

import java.util.List;

public interface MessageService {
    void save(Message message);
    void deleteAllMessages();
    void deleteMessage(Message message);
    Message getMessageByNextFields(String chatId, String maintenance);
    List<Message> getAllRemindMessages();
    void updateMessage(Message message);
    void deleteAndAddMessage(User user, RemindManage manage, boolean isRemindSent);
    boolean isSentMessage(Message message);
    void deleteAllNotRemindMessage(User user, RemindManage manage);
    List<Message> getAllNotRemindMessage(User user);
}
