package telegramBot.bot;

import lombok.Getter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.objects.Update;
import telegramBot.validate.Validate;
import telegramBot.command.CommandContainer;
import telegramBot.dao.NoticeDAOImpl;
import telegramBot.entity.Notice;
import telegramBot.notification.SendNotice;
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
    private static final SendNotice sendNotice = new SendNotice();

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
            sendNotice.executeNoticeAtDate();
    }


    private String getDateFromUserInput(Update update) {
        int firstIndexOfDate = 0;
        int lastIndexOfDate = update.getMessage().getText().length();
        for (int i = 0; i < update.getMessage().getText().split("").length; i++) {
            if (String.valueOf(update.getMessage().getText().charAt(i)).matches("[0-9]")
                    &&String.valueOf(update.getMessage().getText().charAt(i + 1)).
                    matches("\\p{P}")) {
                firstIndexOfDate = (i - 1);
                break;
            }
        }
        return update.getMessage().getText().substring(firstIndexOfDate, lastIndexOfDate);
    }


    private String getNoticeContentFromUserInput(Update update) {
        String s = update.getMessage().getText();
        return s.substring(0, s.length() - getDateFromUserInput(update).length());
    }

    private void AcceptNoticeFromUser(Update update) {
        String chatId = update.getMessage().getChatId().toString();
        Pattern date = Pattern.compile("[Aa-zZ\\s][0-9]{2}\\p{P}[0-9]{2}\\p{P}[0-9]{4}");
        boolean isDateInInput = date.matcher(update.getMessage().getText()).find();
        boolean rightDateInput = Validate.date(getDateFromUserInput(update).split("\\p{P}")[0],
                getDateFromUserInput(update).split("\\p{P}")[1],
                getDateFromUserInput(update).split("\\p{P}")[2]);

        if ((isDateInInput) && (rightDateInput)) {
            try {
                Notice notice = new Notice(chatId,
                        getNoticeContentFromUserInput(update), getDateFromUserInput(update));
                if (new NoticeDAOImpl().save(notice)) {
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

    }


















