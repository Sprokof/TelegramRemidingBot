package telegramBot.command;

import org.telegram.telegrambots.meta.api.objects.Update;
import telegramBot.bot.TelegramBot;
import telegramBot.service.SendMessageService;

public class StartCommand implements Command{
    private int counter = 1;
    public static String START_COMMAND = "Я бот, реализующий напоминательную функцию. Для получения информации " +
            "об принципе моей работе " +
            "введите команду /instr";

    private final SendMessageService sendMessageService;

    public StartCommand(SendMessageService sendMessageService){
        this.sendMessageService = sendMessageService;}


    @Override
    public boolean execute(Update update) {
        counter++;
        this.sendMessageService.sendMessage(update.getMessage().getChatId().toString(),
                START_COMMAND);
        if(counter>0){
            START_COMMAND = "Команда уже была запущена ранее";}
        counter=1;
        return true;
    }}

