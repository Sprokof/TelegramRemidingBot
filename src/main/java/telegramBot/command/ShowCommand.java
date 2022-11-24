package telegramBot.command;

import org.telegram.telegrambots.meta.api.objects.Update;
import telegramBot.service.SendMessageService;

public class ShowCommand implements Command{

    private SendMessageService sendMessageService;
    private static final String SHOW_MESSAGE = "Введите дату в формате dd.mm.yyyy для " +
            "показа всех напоминаний на заданную дату";

    public ShowCommand(SendMessageService sendMessageService){
        this.sendMessageService = sendMessageService;
    }

    @Override
    public boolean execute(Update update) {
        String chatId = update.getMessage().getChatId().toString();
        return this.sendMessageService.sendMessage(chatId, SHOW_MESSAGE);
    }

}
