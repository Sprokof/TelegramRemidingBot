package telegramBot.bot;

import lombok.Getter;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.objects.Update;

import telegramBot.service.RemindServiceImpl;
import telegramBot.validate.Validate;
import telegramBot.command.CommandContainer;
import telegramBot.entity.Remind;
import telegramBot.sendRemind.SendRemind;
import telegramBot.service.SendMessageServiceImpl;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.*;
import java.util.regex.Pattern;

@Getter
@Component
public class TelegramBot extends TelegramLongPollingBot {

    private static String tokenFromFile() {
        try {
            return new BufferedReader(new InputStreamReader(
                    new FileInputStream("C:/Users/user/Desktop/token.txt"))).readLine();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    private final String botUsername = "Reminder";
    private final String botToken = tokenFromFile();
    private final String COMMAND_PREFIX = "/";
    private final CommandContainer commandContainer;
    public static final List<String> commands = new ArrayList<>();
    private final SendMessageServiceImpl sendMessageService;
    private final SendRemind sendRemind;

    public TelegramBot() {
        this.sendMessageService = new SendMessageServiceImpl(this);
        this.commandContainer = new CommandContainer(sendMessageService);
        this.sendRemind = new SendRemind(new SendMessageServiceImpl(this));

    }

    @Override
    public void onUpdateReceived(Update update) {
        String command = "";
        if (update.hasMessage() && update.getMessage().hasText()) {
            String message = update.getMessage().getText().trim();
            if (message.startsWith(COMMAND_PREFIX)) {
                command = message.split(" ")[0].toLowerCase(Locale.ROOT);
                this.commandContainer.retrieveCommand(command).execute(update);
                commands.add(command);
            } else if (!message.startsWith(COMMAND_PREFIX)) {
                if ((!commands.isEmpty()) && commands.get(commands.size() - 1).equals("/add")) {
                    acceptNewRemindFromUser(update);}
                else if((!commands.isEmpty()) && commands.get(commands.size() - 1).equals("/show")){
                    if(acceptDateFromUser(update)){
                    try{
                        if(!this.sendRemind.showRemindsByDate(update.getMessage().getChatId().toString(),
                                update.getMessage().getText())){
                            this.sendMessageService.sendMessage(update.getMessage().getChatId().toString(),
                                    "Не получилось найти напоминания, возможно " +
                                            "вы указали уже прошедшую дату, либо на эту дату нет напоминаний");}
                        }
                    catch (InterruptedException e){e.printStackTrace();}
                    }
                }
                else {
                    this.commandContainer.retrieveCommand("/unknown").execute(update);
                }
            }
        }
        this.sendRemind.executeRemindMessage();
    }


    private String getDateFromUserInput(String input) {
        int firstIndexOfDate = 0;
        int lastIndexOfDate = input.length();
        for (int i = 0; i < input.split("").length; i++) {
            if (String.valueOf(input.charAt(i)).matches("[0-9]")
                    && String.valueOf(input.charAt(i + 1)).
                    matches("\\p{P}")) {
                firstIndexOfDate = (i - 1);
                break;
            }
        }
        return input.substring(firstIndexOfDate, lastIndexOfDate);
    }


    private String getRemindContentFromUserInput(String input) {
        return input.substring(0, input.length() - getDateFromUserInput(input).length());
    }

    private void acceptNewRemindFromUser(Update update) {
        String chatId = update.getMessage().getChatId().toString();
        String input = update.getMessage().getText();
        boolean isContains;
        if (isCorrectInput(input)) {
            try {
                Remind remind = new Remind(chatId,
                        getRemindContentFromUserInput(input),
                        getDateFromUserInput(input).replaceAll("\\p{P}", "\\."));
                isContains = RemindServiceImpl.newRemindService().isContainsInDB(remind);
                 if(!isContains){
                    if (RemindServiceImpl.newRemindService().saveRemind(remind)) {
                        this.sendMessageService.sendMessage(chatId, "Напоминание успешно" +
                                " добавлено.");
                        clearingCommandStorage();
                    } else {
                        this.sendMessageService.sendMessage(chatId,
                                "Напоминание не было добавлено, проверьте формат даты (dd.mm.yyyy)" +
                                        "Возможно, что вы указали уже прошедшую дату. " +
                                        "После введите команду '/add' еще раз для повторного добавления.");
                    }
                }
                 else{
                     this.sendMessageService.sendMessage(chatId,
                         "Данное напоминание было добавлено ранее.");}
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else {
            this.sendMessageService.sendMessage(chatId,
                    "Напоминание не было добавлено, проверьте формат даты (dd.mm.yyyy) " +
                            "для ежедневных напоминаний. Возможно, что вы указали уже прошедшую дату. " +
                            "После введите команду '/add' еще раз для повторного добавления.");
        }
    }

    private boolean isCorrectInput(String input) {
        Pattern p = Pattern.compile("[Aa-zZ\\s][0-9]{2}\\p{P}[0-9]{2}\\p{P}[0-9]{4}");
        boolean textWithRightDate = p.matcher(input).find();
        if (textWithRightDate) {
            return Validate.date(getDateFromUserInput(input).split("\\p{P}")[0],
                    getDateFromUserInput(input).split("\\p{P}")[1],
                    getDateFromUserInput(input).split("\\p{P}")[2]);}

        else {
            return false;
        }
    }

    private boolean acceptDateFromUser(Update update){
        String input = update.getMessage().getText();
        Pattern p = Pattern.compile("[0-9]{2}\\p{P}[0-9]{2}\\p{P}[0-9]{4}");
        boolean isDate = p.matcher(input).find();
        if (isDate) {
            String[] dateArray = input.split("\\p{P}");
            Validate.date(dateArray[0], dateArray[1], dateArray[2]);
            return true;
        } else {
             return false;
        }
    }

    private void clearingCommandStorage(){
        int index = commands.size()-1;
        while(index != 0){
            commands.remove(index);
            index --;}
        }
    }

























