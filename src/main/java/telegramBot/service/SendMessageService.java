package telegramBot.service;

public interface SendMessageService {
    boolean sendMessage(String chatId, String message);
}
