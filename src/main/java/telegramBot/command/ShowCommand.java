package telegramBot.command;

import org.telegram.telegrambots.meta.api.objects.Update;
import telegramBot.bot.TelegramBot;
import telegramBot.service.SendMessageService;

public class ShowCommand implements Command{
    private SendMessageService sendMessageService;
    private final String SHOW_MESSAGE = "Введите дату в формате dd.mm.yyyy для " +
            "показа всех напоминаний на заданную дату";

    public ShowCommand(SendMessageService sendMessageService){
        this.sendMessageService = sendMessageService;
    }

    @Override
    public boolean execute(Update update) {
        TelegramBot.setRun(false);
        return this.sendMessageService.sendMessage(update.getMessage().getChatId().toString(),
                SHOW_MESSAGE);

    }
}
