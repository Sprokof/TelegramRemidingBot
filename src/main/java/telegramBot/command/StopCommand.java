package telegramBot.command;

import org.telegram.telegrambots.meta.api.objects.Update;
import telegramBot.bot.TelegramBot;
import telegramBot.entity.Remind;
import telegramBot.service.RemindServiceImpl;
import telegramBot.service.SendMessageService;

public class StopCommand implements Command {
    public static final String STOP_COMMAND = "Вы остановили напоминания. /restart - для возообновления";
    private final SendMessageService sendMessageService;


    public StopCommand(SendMessageService sendMessageService){
        this.sendMessageService = sendMessageService;}

    @Override
    public boolean execute(Update update) {
        String chatId = update.getMessage().getChatId().toString();
        if(sendMessageService.sendMessage(chatId, STOP_COMMAND)){
            stop(chatId);}
        return true;
        }

    private void stop(String chatId){
        for(Remind r: RemindServiceImpl.newRemindService().getAllRemindsFromDB()){
            if(r.getChatIdToSend().equals(chatId))
                RemindServiceImpl.newRemindService().updateIsStopField(r, true);}
        }
    }




