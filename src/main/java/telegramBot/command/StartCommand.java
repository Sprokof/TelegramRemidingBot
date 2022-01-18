package telegramBot.command;

import org.telegram.telegrambots.meta.api.objects.Update;
import telegramBot.service.SendMessageService;

public class StartCommand implements Command{
    private int counter = 1;
    public static String START_COMMAND = "Я бот, напоминающий об важных событиях," +
            "для добавления напоманиния введите команду '/add', после " +
            "содержание напоминания и саму дату напоминания в формате dd.mm.yyyy, где все буквы цифры." +
            "Для ежедневных напоминаний помимо содержание напоминания и даты старта напоминаний" +
            "добавьте буквы 'р' в начале сообщения.";

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

