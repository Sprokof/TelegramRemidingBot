package telegramBot.command;

import org.telegram.telegrambots.meta.api.objects.Update;
import telegramBot.service.SendMessageService;

public class AddCommand implements Command {
    public static String ADD_COMMAND = "Введите напоминание в формате -> text date (dd.mm.yyyy - формат даты) . \n" +
            "для ежедневных напоминаний поставьте в начале букву 'р' и через пробел ваше напоминание";

    private final SendMessageService sendMessageService;


    public AddCommand(SendMessageService sendMessageService) {
        this.sendMessageService = sendMessageService;
    }


    @Override
    public boolean execute(Update update) {
        String chatId = update.getMessage().getChatId().toString();
        return this.sendMessageService.sendMessage(chatId, ADD_COMMAND);
    }


}

