package telegramBot.service;

import telegramBot.dao.MessageDAOImpl;
import telegramBot.entity.Message;
import telegramBot.entity.Remind;
import telegramBot.entity.User;
import telegramBot.manage.RemindManage;


import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class MessageServiceImpl implements MessageService {


    private static final Map<String, List<Message>> wrongRemindsMessages;

    static {
      wrongRemindsMessages = new HashMap<>();
    }


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
    public void deleteAndAddMessage(User user, RemindManage manage, boolean isRemindSent) throws NullPointerException {
        if(!isRemindSent) return;
        String id = toStringId(user.getReminds());
        Message newMessage = new Message(user.getChatId(), id,
                SendMessageServiceImpl.getMessageId(), true);
        Message oldMessage;
        if (!isSentMessage(newMessage)){ save(newMessage); }
        else { oldMessage = messageService().getMessageByNextFields(user.getChatId(), id);
            manage.getDeleteService().deleteMessage(oldMessage);
            deleteMessage(oldMessage);
            save(newMessage);
            manage.getDeleteService().
                    deleteMessage(deleteLastSendMessage(user));

        }
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


    public static void saveMessage(User user, boolean isCommand) {
        Message message = null;
        int commandCorrecting = isCommand ? - 1 : 0;

        try {
            message = new Message(user.getChatId(), Message.DEFAULT_REMIND_ID,
                    (SendMessageServiceImpl.getMessageId() + commandCorrecting), false);
        } catch (NullPointerException e) {
            ignoreNullPointerException();
        }
        if (message != null) {
            new MessageDAOImpl().save(message);
        }
    }


    public static void deleteWrongRemindsMessages(User user, DeleteMessageServiceImpl service){
        if(wrongRemindsMessages.isEmpty()) return;

    String chatId = user.getChatId();
    List<Message> messagesToDelete;
        if(!(messagesToDelete = wrongRemindsMessages.get(chatId)).isEmpty()) {
                messagesToDelete.forEach(service::deleteMessage);
        }
        wrongRemindsMessages.get(chatId).clear();
    }
    public static void addWrongRemindsMessage(User user, Message message) {
        String chatId = user.getChatId();
        wrongRemindsMessages.putIfAbsent(chatId, new ArrayList<>());
        wrongRemindsMessages.get(chatId).add(message);
    }

    public static void ignoreNullPointerException(){};

    @Override
    public Message deleteLastSendMessage(User user) {
        return this.messageDAO.deleteLastSendMessage(user);
    }
}




