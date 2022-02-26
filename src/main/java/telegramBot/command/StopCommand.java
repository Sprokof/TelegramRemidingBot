package telegramBot.command;

import org.telegram.telegrambots.meta.api.objects.Update;
import telegramBot.bot.TelegramBot;
import telegramBot.service.SendMessageService;

public class StopCommand implements Command {
    public static final String STOP_COMMAND = "Вы остановили напоминания. /restart - для возобновления";
    private final SendMessageService sendMessageService;

    public StopCommand(SendMessageService sendMessageService){
        this.sendMessageService = sendMessageService;}

    @Override
    public boolean execute(Update update) {
        sendMessageService.sendMessage(update.getMessage().getChatId().toString(), STOP_COMMAND);
        TelegramBot.setRun(false);
        return true;

    }}


