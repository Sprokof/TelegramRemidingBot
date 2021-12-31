package telegramBot.command;

import org.telegram.telegrambots.meta.api.objects.Update;
import telegramBot.service.SendMessageService;

public class AddCommand implements Command{
    public static String ADD_COMMAND = "Введите то, о чем вам нужно напомнить и через пробел дату напоминания";

    private SendMessageService sendMessageService;

    public AddCommand(SendMessageService sendMessageService){

        this.sendMessageService = sendMessageService;
    }

    @Override
    public boolean execute(Update update) {
        this.sendMessageService.sendMessage(update.getMessage().getChatId().toString(), ADD_COMMAND);
        return true;}

    }

