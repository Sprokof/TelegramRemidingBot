package telegramBot.service;

import telegramBot.entity.Message;

public interface DeleteMessageService {
    boolean deleteMessage(Message message);
}
