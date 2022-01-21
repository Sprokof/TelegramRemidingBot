package telegramBot.bot;

import lombok.Getter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.objects.Update;
import telegramBot.validate.Validate;
import telegramBot.command.CommandContainer;
import telegramBot.dao.RemindDAOImpl;
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
            return new BufferedReader(new InputStreamReader(new FileInputStream("C:/Users/user/Desktop/token.txt"))).readLine();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Value("${bot.username}")
    private String botUsername;
    private final String botToken = tokenFromFile();
    private final String COMMAND_PREFIX = "/";
    private final CommandContainer commandContainer;
    public static final List<String> commands = new ArrayList<>();
    private final SendMessageServiceImpl sendMessageService;
    private static final SendRemind SEND_REMIND = new SendRemind();

    public TelegramBot() {
        this.sendMessageService = new SendMessageServiceImpl(this);
        this.commandContainer = new CommandContainer(sendMessageService);

        }

    @Override
    public void onUpdateReceived(Update update) {
        String command = "";
        if (update.hasMessage() && update.getMessage().hasText()) {
            String message = update.getMessage().getText().trim();
            if (message.startsWith(COMMAND_PREFIX)) {
                command = message.split(" ")[0].toLowerCase(Locale.ROOT);
                commandContainer.retrieveCommand(command).execute(update);
                commands.add(command);
            } else if (!message.startsWith(COMMAND_PREFIX)) {
                if ((!commands.isEmpty()) && commands.get(commands.size() - 1).equals("/add")) {
                    AcceptNoticeFromUser(update);
                } else {
                    commandContainer.retrieveCommand("/unknown").execute(update);}}}
            SEND_REMIND.executeRemindMessage();
    }


    private String getDateFromUserInput(String input) {
        int firstIndexOfDate = 0;
        int lastIndexOfDate = input.length();
        for (int i = 0; i < input.split("").length; i++) {
            if (String.valueOf(input.charAt(i)).matches("[0-9]")
                    &&String.valueOf(input.charAt(i + 1)).
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

    private void AcceptNoticeFromUser(Update update) {
        String chatId = update.getMessage().getChatId().toString();
        String input = update.getMessage().getText();
        if (isCorrectInput(input)) {
            try {
                Remind remind = new Remind(chatId,
                        getRemindContentFromUserInput(input), getDateFromUserInput(input));
                if (new RemindDAOImpl().save(remind)) {
                    this.sendMessageService.sendMessage(chatId, "Напоминание успешно" +
                            " добавлено");
                    //commands.clear();
                } else {
                    this.sendMessageService.sendMessage(chatId,
                            "Напоминание не было добавлено, проверьте формат даты (dd.mm.yyyy) или 00.00.0000. " +
                                    "Возможно, что вы указали уже прошедшую дату. " +
                                    "После введите команду '/add' еще раз для повторного добавления.");
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        else {
            this.sendMessageService.sendMessage(chatId,
                    "Напоминание не было добавлено, проверьте формат даты (dd.mm.yyyy) или 00.00.0000 " +
                            "для ежедневных напоминаний. Возможно, что вы указали уже прошедшую дату. " +
                            "После введите команду '/add' еще раз для повторного добавления.");}
    }

    private boolean isCorrectInput(String input){
        Pattern tempPattern = Pattern.compile("[Aa-zZ\\s][0-9]{2}\\p{P}[0-9]{2}\\p{P}[0-9]{4}");
        boolean textWithRightDate = tempPattern.matcher(input).find();
        if(textWithRightDate) return Validate.date(getDateFromUserInput(input).split("\\p{P}")[0],
                getDateFromUserInput(input).split("\\p{P}")[1],
                getDateFromUserInput(input).split("\\p{P}")[2]);
        else{return false;}
    }

    }


















