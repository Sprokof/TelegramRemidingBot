package telegramBot.command;

import org.telegram.telegrambots.meta.api.objects.Update;
import telegramBot.service.SendMessageService;

public class HelpCommand implements Command{
    public static final String STOP_COMMAND = "Поддерживаю следующие команды.\n" +
            "/add - добавление напоминания на исполнение.\n" +
            "/stop - остановка напоминаний.\n" +
            "/restart  - возообнавление напоминаний."+
            "/show - показ всех напоминаний на текущую дату";
    private final SendMessageService sendMessageService;

    public HelpCommand(SendMessageService sendMessageService){
        this.sendMessageService = sendMessageService;}

    @Override
    public boolean execute(Update update) {
        sendMessageService.sendMessage(update.getMessage().getChatId().toString(), STOP_COMMAND);
        return true;

    }
}

