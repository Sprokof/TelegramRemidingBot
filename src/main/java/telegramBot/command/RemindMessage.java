package telegramBot.command;

import org.springframework.beans.factory.annotation.Autowired;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import telegramBot.entity.Storage;
import telegramBot.entity.User;
import telegramBot.manage.DateManage;
import telegramBot.service.SendMessageService;
import telegramBot.service.SendMessageServiceImpl;
import telegramBot.service.StorageServiceImpl;
import telegramBot.service.UserServiceImpl;
import static telegramBot.service.StorageServiceImpl.*;


import java.util.List;
import java.util.stream.Collectors;


public class RemindMessage {

    private static final String[] datesToSend = {"10/17", "13/19", "15/21", "14/23", "12/27"};

    private  static final String REMIND_MESSAGE = "Позвольте напомнить, " +
            "что я поддерживаю следующие комманды:\n" +
            "/add - добавление напоминания на исполнение.\n" +
            "/stop - остановка напоминаний.\n" +
            "/restart - возообнавление напоминаний.\n" +
            "/show - показ всех напоминаний на заданную дату.";


    public static void sendToAllUsers(SendMessageService messageService){
    int full = 5;
    String mock = "MOCK";
    List<String> usersChatId =
            UserServiceImpl.userService().
                    getAllUsers().stream().map(User::getChatId).collect(Collectors.toList());

        String currentDay =
                DateManage.
                        currentDate().substring(0, (DateManage.currentDate().indexOf(".")));

        String currentMonth =
                DateManage.
                        currentDate().substring(DateManage.
                                        currentDate().indexOf(".") + 1,
                                (DateManage.currentDate().lastIndexOf(".")));

     Storage storage = createStorage((Integer.parseInt(currentMonth)), getSendDays());

     String executeDay = storage.getDaysToSend().substring(0,2);
    if(currentDay.equals(executeDay)){

        for(String chatId : usersChatId){
            messageService.sendMessage(chatId, REMIND_MESSAGE);
        }
        if(storage.getDaysToSend().length() == full){
        String nextExecuteDay = storage.getDaysToSend().substring(3);
        storage.setDaysToSend(nextExecuteDay);
        new StorageServiceImpl().updateStorage(storage);
        }
        else storage.setDaysToSend(mock);
             new StorageServiceImpl().updateStorage(storage);

    }

    }

    private static String getSendDays(){

        double d = (Math.random() * 3);

        int index = ((int) d);
        return datesToSend[index];

    }
}
