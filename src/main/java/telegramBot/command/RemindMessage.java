package telegramBot.command;

import org.springframework.beans.factory.annotation.Autowired;
import telegramBot.entity.Storage;
import telegramBot.entity.User;
import telegramBot.manage.DateManage;
import telegramBot.service.SendMessageService;
import telegramBot.service.UserServiceImpl;
import static telegramBot.service.StorageServiceImpl.*;


import java.util.List;
import java.util.stream.Collectors;


public class RemindMessage {

    @Autowired
    private static SendMessageService sendMessageService;

    private static final String[] datesToSend = {"10/17", "13/19", "15/21"};

    private  static final String REMIND_MESSAGE = "Позвольте напомнить, " +
            "что я поддерживаю следующие комманды:\n" +
            "/add - добавление напоминания на исполнение.\n" +
            "/stop - остановка напоминаний.\n" +
            "/restart - возообнавление напоминаний.\n" +
            "/show - показ всех напоминаний на заданную дату.";


    public static void sendToAllUsers(){

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

     Storage storage = createStorage((Integer.parseInt(currentMonth)), getSendDates());

     String[] days = storage.getDatesToSend().split("\\/");

    if(currentDay.equals(days[0])
            || currentDay.equals(days[1])){
        for(String chatId : usersChatId){
            sendMessageService.sendMessage(chatId, REMIND_MESSAGE);
        }
    }

    }

    private static String getSendDates(){

        double d = (Math.random() * 3);

        int index = ((int) d);

        return datesToSend[index];

    }
}
