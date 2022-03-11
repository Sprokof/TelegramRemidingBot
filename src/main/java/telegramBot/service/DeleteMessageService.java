package telegramBot.service;

public interface DeleteMessageService {
    boolean deleteMessage(String chatId, Integer messageId);
}
