package telegramBot.service;

import telegramBot.dao.MessageDAOImpl;
import telegramBot.entity.Message;
import telegramBot.entity.Remind;
import telegramBot.entity.User;
import telegramBot.manage.RemindManage;


import java.util.List;
import java.util.stream.Collectors;

public class MessageServiceImpl implements MessageService {


    private final MessageDAOImpl messageDAO;

    public MessageServiceImpl(MessageDAOImpl messageDAO) {
        this.messageDAO = (MessageDAOImpl) messageDAO;
    }

    @Override
    public void save(Message message) {
        this.messageDAO.save(message);
    }


    public static MessageServiceImpl messageService() {
        return new MessageServiceImpl(new MessageDAOImpl());
    }

    @Override
    public List<Message> getAllRemindMessages() {
        return this.messageDAO.getAllRemindMessages();
    }

    @Override
    public void deleteAllMessages() {
        this.messageDAO.deleteAllMessages();
    }

    @Override
    public Message getMessageByNextFields(String chatId, String remindId) {
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

    @Override
    public void deleteAndAddMessage(User user, RemindManage manage, boolean isRemindSent) {
        if(!isRemindSent) return;
        String id = toStringId(user.getReminds());
        Message newMessage = new Message(user.getChatId(), id,
                SendMessageServiceImpl.getMessageId(), true);
        Message oldMessage;
        if (!isSentMessage(newMessage)){ messageService().save(newMessage); }
        else { oldMessage = messageService().getMessageByNextFields(user.getChatId(), id);
            manage.getDeleteService().deleteMessage(oldMessage);
            messageService().deleteMessage(oldMessage);
            messageService().save(newMessage); }
    }

    @Override
    public boolean isSentMessage(Message message) {
        return this.messageDAO.isSentMessage(message);
    }

    public String toStringId(List<Remind> reminds){
    List<Integer> ides;
    ides = reminds.stream().map(Remind::getId).sorted((i1, i2) -> i1 - i2).
            collect(Collectors.toList());
    return ides.toString().replaceAll("\\p{P}", "").
            replaceAll("\\s", "\\/");
    }

    @Override
    public List<Message> getAllNotRemindMessage(User user) {
        return this.messageDAO.getAllNotRemindMessage(user);
    }

    @Override
    public void deleteAllNotRemindMessage(User user, RemindManage manage) {
        List<Message> otherMessages = messageService().
                getAllNotRemindMessage(user);
        otherMessages.stream().
                map(Message::buildSecondConstructorMessage).
                forEach(manage.getDeleteService()::deleteMessage);
        this.messageDAO.deleteAllNotRemindMessage(user);
    }

    @Override
    public List<Message> getRemindMessagesByChatId(String chatId) {
        return this.messageDAO.getRemindMessagesByChatId(chatId);
    }
}




