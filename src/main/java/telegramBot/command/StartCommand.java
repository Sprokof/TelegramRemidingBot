package telegramBot.command;

import org.telegram.telegrambots.meta.api.objects.Update;
import telegramBot.bot.TelegramBot;
import telegramBot.entity.Message;
import telegramBot.service.SendMessageService;
import telegramBot.service.SendMessageServiceImpl;

import static telegramBot.service.MessageServiceImpl.messageService;

public class StartCommand implements Command{
    private int counter = 1;
    public static String START_COMMAND = "Я бот, реализующий напоминательную функцию. Для получения информации " +
            "об моей работе введите команду /instr";

    private final SendMessageService sendMessageService;

    public StartCommand(SendMessageService sendMessageService){
        this.sendMessageService = sendMessageService;}


    @Override
    public boolean execute(Update update) {
        String chatId = update.getMessage().getChatId().toString();
        if(this.sendMessageService.sendMessage(chatId, START_COMMAND)){
            Message output = new Message(chatId, "0",
                    SendMessageServiceImpl.getMessageId(), false);
            messageService().save(output);}
        return true;
    }}

