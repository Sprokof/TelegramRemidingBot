package telegramBot.command;

import org.telegram.telegrambots.meta.api.objects.Update;
import telegramBot.service.SendMessageService;

public class InstrCommand implements Command{
    public static final String INSTR_COMMAND = "Поддерживаю следующие команды.\n" +
            "/add - добавление напоминания на исполнение.\n" +
            "/stop - остановка напоминаний.\n" +
            "/restart  - возообнавление напоминаний.\n"+
            "/show - показ всех напоминаний на заданную дату.";
    private final SendMessageService sendMessageService;

    public InstrCommand(SendMessageService sendMessageService){
        this.sendMessageService = sendMessageService;}

    @Override
    public boolean execute(Update update) {
        sendMessageService.sendMessage(update.getMessage().getChatId().toString(), INSTR_COMMAND);
        return true;

    }
}

