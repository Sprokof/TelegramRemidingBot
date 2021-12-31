package telegramBot.command;

import org.telegram.telegrambots.meta.api.objects.Update;
import telegramBot.service.SendMessageService;

public class RestartCommand implements Command{
    public static String RESTART_COMMAND = "Вы возообновили оповещения.";

    private SendMessageService sendMessageService;

    public RestartCommand(SendMessageService sendMessageService){

        this.sendMessageService = sendMessageService;
    }

    @Override
    public boolean execute(Update update) {
        this.sendMessageService.sendMessage(update.getMessage().getChatId().toString(), RESTART_COMMAND);
        return true;}

}

