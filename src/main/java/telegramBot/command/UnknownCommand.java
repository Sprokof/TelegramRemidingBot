package telegramBot.command;

import org.telegram.telegrambots.meta.api.objects.Update;
import telegramBot.service.SendMessageService;

public class UnknownCommand implements Command{
    public static final String UNKNOWN_COMMAND = "Unknown input command. /instr для получения сводки.";
    private final SendMessageService sendMessageService;

    public UnknownCommand(SendMessageService sendMessageService){
        this.sendMessageService = sendMessageService;}

    @Override
    public boolean execute(Update update) {
        String chatId = update.getMessage().getChatId().toString();
        return this.sendMessageService.sendMessage(chatId, UNKNOWN_COMMAND);
    }
}

