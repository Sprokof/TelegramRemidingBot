package telegramBot.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.DeleteMessage;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import telegramBot.bot.TelegramBot;

@Component
public class DeleteMessageServiceImpl implements DeleteMessageService{

    private TelegramBot bot;

    @Autowired
    public DeleteMessageServiceImpl(TelegramBot telegramBot){
        this.bot = telegramBot;

    }

    @Override
    public boolean deleteMessage(String chatId, Integer messageId) {
        DeleteMessage deleteMessage = new DeleteMessage();
        deleteMessage.setChatId(chatId);
        deleteMessage.setMessageId(messageId);

    try{
        bot.execute(deleteMessage);}
    catch (TelegramApiException e){
        return false;
    }
    return true;
    }

}
