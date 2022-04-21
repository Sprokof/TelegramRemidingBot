package telegramBot.command;

import org.telegram.telegrambots.meta.api.objects.Update;
import telegramBot.bot.TelegramBot;
import telegramBot.entity.Message;
import telegramBot.service.SendMessageService;
import telegramBot.service.SendMessageServiceImpl;

import static telegramBot.service.MessageServiceImpl.messageService;

public class UnknownCommand implements Command{
    public static final String UNKNOWN_COMMAND = "Unknown input command. /instr для получения сводки.";
    private final SendMessageService sendMessageService;

    public UnknownCommand(SendMessageService sendMessageService){
        this.sendMessageService = sendMessageService;}

    @Override
    public boolean execute(Update update) {
        String chatId = update.getMessage().getChatId().toString();
        if(this.sendMessageService.sendMessage(chatId, UNKNOWN_COMMAND)){
            Message output = new Message(chatId, "0",
                    SendMessageServiceImpl.getMessageId(), false);
            messageService().save(output);}
        return true;
    }
}

