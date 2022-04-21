package telegramBot.command;

import org.telegram.telegrambots.meta.api.objects.Update;
import telegramBot.bot.TelegramBot;
import telegramBot.entity.Message;
import telegramBot.service.SendMessageService;
import telegramBot.service.SendMessageServiceImpl;

import static telegramBot.service.MessageServiceImpl.messageService;

public class ShowCommand implements Command{
    private SendMessageService sendMessageService;
    private final String SHOW_MESSAGE = "Введите дату в формате dd.mm.yyyy для " +
            "показа всех напоминаний на заданную дату";

    public ShowCommand(SendMessageService sendMessageService){
        this.sendMessageService = sendMessageService;
    }

    @Override
    public boolean execute(Update update) {
        String chatId = update.getMessage().getChatId().toString();
        if(this.sendMessageService.sendMessage(chatId, SHOW_MESSAGE)){
            Message output = new Message(chatId, "0",
                    SendMessageServiceImpl.getMessageId(), false);
            messageService().save(output); }
        return true;
    }
}
