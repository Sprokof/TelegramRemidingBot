package telegramBot.service;

import telegramBot.dao.MessageDAOImpl;
import telegramBot.entity.Message;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class MessageServiceImpl implements MessageService {

    private MessageDAOImpl messageDAO;

    public MessageServiceImpl(MessageDAOImpl messageDAO){
        this.messageDAO = (MessageDAOImpl) messageDAO;

    }

    @Override
    public void save(Message message) {
        this.messageDAO.save(message);
    }



    public static MessageServiceImpl newMessageService(){
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
        Integer[] ides = Arrays.stream(remindId.split("\\p{P}"))
                .map(Integer::parseInt).collect(Collectors.toList()).
                toArray(Integer[]::new); Arrays.sort(ides);
        remindId = Arrays.toString(ides).
                replaceAll("\\p{P}", "\\s").replaceAll("\\s", "");
        return this.messageDAO.getMessageByChatAndRemindId(chatId, remindId);
    }

    @Override
    public void deleteMessage(Message message) {
        this.messageDAO.deleteMessageByMessageId(message.getMessageId());
    }
}
