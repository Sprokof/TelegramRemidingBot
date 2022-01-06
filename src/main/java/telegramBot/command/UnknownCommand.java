package telegramBot.command;

import org.telegram.telegrambots.meta.api.objects.Update;
import telegramBot.service.SendMessageService;

public class UnknownCommand implements Command{
    public static final String UNKNOWN_COMMAND = "Я понимаю команды '/start', '/stop', " +
            "'/add'/, '/restart'";
    private final SendMessageService sendMessageService;

    public UnknownCommand(SendMessageService sendMessageService){
        this.sendMessageService = sendMessageService;}

    @Override
    public boolean execute(Update update) {
        sendMessageService.sendMessage(update.getMessage().getChatId().toString(), UNKNOWN_COMMAND);
        return true;

    }}

