package telegramBot.command;

import org.telegram.telegrambots.meta.api.objects.Update;
import telegramBot.bot.TelegramBot;
import telegramBot.service.SendMessageService;

public class UnknownCommand implements Command{
    public static final String UNKNOWN_COMMAND = "Я понимаю команды /start, /stop, " +
            "/add,\n/restart. Введите команду /instr для получения сводки.";
    private final SendMessageService sendMessageService;

    public UnknownCommand(SendMessageService sendMessageService){
        this.sendMessageService = sendMessageService;}

    @Override
    public boolean execute(Update update) {
       return sendMessageService.sendMessage(update.getMessage().getChatId().toString(), UNKNOWN_COMMAND);
    }
}

