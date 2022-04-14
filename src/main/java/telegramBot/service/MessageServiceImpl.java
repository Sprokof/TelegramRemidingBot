package telegramBot.service;

import telegramBot.dao.MessageDAOImpl;
import telegramBot.entity.Message;

import java.util.List;
public class MessageServiceImpl implements MessageService {

    public static Message storage = new MessageDAOImpl().getAllMessages().get(0);

    private final MessageDAOImpl messageDAO;

    public MessageServiceImpl(MessageDAOImpl messageDAO) {
        this.messageDAO = (MessageDAOImpl) messageDAO;
    }

    @Override
    public void save(Message message) {
        this.messageDAO.save(message);
    }


    public static MessageServiceImpl newMessageService() {
        return new MessageServiceImpl(new MessageDAOImpl());
    }

    @Override
    public List<Message> getAllMessages() {
        return this.messageDAO.getAllMessages();
    }

    @Override
    public void deleteAllMessages() {
        this.messageDAO.deleteAllMessages();
    }

    @Override
    public Message getMessageByNextField(String chatId, String remindId) {
        return this.messageDAO.getMessageByChatAndRemindId(chatId, remindId);
    }

    @Override
    public void deleteMessage(Message message) {
        this.messageDAO.deleteMessageByMessageId(message.getMessageId());
    }

    @Override
    public void updateMessage(Message message) {
        this.messageDAO.updateMessage(message);
    }

    public void updateStorageMessage(){
        double d = (Math.random() * 9);
        String result;
        int i = (int) d;
        if (i != 0) {
            result = (storage.getRemindId() + i);
            storage.setRemindId(result);
            this.updateMessage(storage);
        }
        if(storage.getRemindId().length() == 3){
            storage.setRemindId("");
            this.updateMessage(storage);
        }
    }
}


