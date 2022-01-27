package telegramBot.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import telegramBot.bot.TelegramBot;

@Service
@Component("sendMessageServiceImpl")
public class SendMessageServiceImpl implements SendMessageService{
    private final TelegramBot telegramBot;

    @Autowired
    public SendMessageServiceImpl(TelegramBot telegramBot){
        this.telegramBot = telegramBot;}

    @Override
    public boolean sendMessage(String chatId, String message) {
        org.telegram.telegrambots.meta.api.methods.send.SendMessage sendMessage =
                new org.telegram.telegrambots.meta.api.methods.send.SendMessage();
        sendMessage.setChatId(chatId);
        sendMessage.enableHtml(true);
        sendMessage.setText(message);

    try{
        telegramBot.execute(sendMessage);}
    catch (TelegramApiException e){
    return false;}
    return true;}
    }


