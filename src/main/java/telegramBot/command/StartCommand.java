package telegramBot.command;

import org.telegram.telegrambots.meta.api.objects.Update;
import telegramBot.service.SendMessageService;

public class StartCommand implements Command{
    private int counter = 1;
    public static String START_COMMAND = "Я бот, напоминающий об важных событиях," +
            " которые вы не хотели бы пропустить. Для этого введите свое имя ибо Nickname " +
            "(для персонализации обращения), для добавления напоманиния введите команду '/add', после " +
            "содержание напоминания и саму дату напоминания в формате dd.mm.yyyy, где все буквы цифры." +
            "Для ежедневных напоминаний помимо содержание напоминания введите" +
            " время напоминания в 24-х часовом формате ( hh )";

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

