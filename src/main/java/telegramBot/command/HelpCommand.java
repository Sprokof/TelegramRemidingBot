package telegramBot.command;

import org.telegram.telegrambots.meta.api.objects.Update;
import telegramBot.service.SendMessageService;

public class HelpCommand implements Command{
    public static final String STOP_COMMAND = "/add - добавление напоминания на исполнения.\n" +
            "/stop - остановка напоминаний.\n" +
            "/restart  - возообнавление воспоминаний.";
    private final SendMessageService sendMessageService;

    public HelpCommand(SendMessageService sendMessageService){
        this.sendMessageService = sendMessageService;}

    @Override
    public boolean execute(Update update) {
        sendMessageService.sendMessage(update.getMessage().getChatId().toString(), STOP_COMMAND);
        return true;

    }
}

