package telegramBot.command;

import org.telegram.telegrambots.meta.api.objects.Update;
import telegramBot.bot.TelegramBot;
import telegramBot.service.SendMessageService;

public class AddCommand implements Command{
    public static String ADD_COMMAND = "Введите то, о чем вам нужно напомнить и через пробел дату напоминания. " +
            "Для ежедневных напоминаний поставьте 'р' и через пробел " +
            "содержание с датой напоминания. \nФормат даты - dd.mm.yyyy .";

    private final SendMessageService sendMessageService;

    public AddCommand(SendMessageService sendMessageService){

        this.sendMessageService = sendMessageService;
    }

    @Override
    public boolean execute(Update update) {
        if(this.sendMessageService.sendMessage(update.getMessage().getChatId().toString(), ADD_COMMAND)){
            TelegramBot.toSleep();
        }
        return true;}

    }

