package telegramBot.dao;

import lombok.Getter;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.cfg.Configuration;
import telegramBot.entity.Message;
import telegramBot.entity.User;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class MessageDAOImpl implements MessageDAO{
    @Getter
    private final SessionFactory sessionFactory = new Configuration().
            configure("hibernate.cfg.xml").addAnnotatedClass(Message.class).buildSessionFactory();

    @Override
    public void save(Message message) {
        Session session;
    try{
        session = this.sessionFactory.getCurrentSession();
        session.beginTransaction();
        session.save(message);
        session.getTransaction().commit();}
    catch (Exception e){e.printStackTrace();}
    finally {
        this.sessionFactory.close(); }
    }

    @Override
    public void deleteMessageByMessageId(Integer messageId) {
        Session session;
    try{
        session = this.sessionFactory.getCurrentSession();
        session.beginTransaction();
        session.createSQLQuery("DELETE FROM MESSAGES WHERE MESSAGE_ID=:id").
                setParameter("id", messageId).executeUpdate();
        session.getTransaction().commit();
    }
    catch (Exception e){e.printStackTrace();}
    finally {
        this.sessionFactory.close();
    }
    }

    @Override
    public void deleteAllMessages() {
        Session session;
    try{
        session = this.sessionFactory.getCurrentSession();
        session.beginTransaction();
        session.createSQLQuery("DELETE FROM MESSAGES").executeUpdate();
        session.getTransaction().commit();}
    catch (Exception e){e.printStackTrace();}
    finally {
        this.sessionFactory.close();
    }

    }

    @Override
    public List<Message> getAllRemindMessages() {
        Session session;
        List<?> tempList = null;
    try{
        session = this.sessionFactory.getCurrentSession();
        session.beginTransaction();
        tempList = session.createSQLQuery("SELECT * FROM MESSAGES " +
                        "WHER IS_REMIND_MESSAGE is true").
                addEntity(Message.class).list();
        session.getTransaction().commit();}
    catch (Exception e){ e.getCause();}
    finally{
        this.sessionFactory.close();}
    List<Message> messages = new ArrayList<>();
        for(Iterator<?> it = tempList.iterator(); it.hasNext();){
            messages.add((Message) it.next());
    }
    return messages;
    }

    @SuppressWarnings("unchecked")
    public Message getMessageByChatAndRemindId(String chatId, String remindId) {
        Session session;
        List<Message> messages = new ArrayList<>();
        try {
            session = this.sessionFactory.getCurrentSession();
            session.beginTransaction();
            messages = (ArrayList<Message>) session.createSQLQuery("SELECT * FROM MESSAGES " +
                    "WHERE CHAT_ID=:cId AND REMIND_ID=:rId ").
                    addEntity(Message.class).setParameter("cId", chatId).
                    setParameter("rId", remindId).list();
            session.getTransaction().commit();
        } catch (Exception e) {
            e.printStackTrace();
        }
        finally {
            this.sessionFactory.close();
        }
        try {
            return messages.get(0);
        } catch (IndexOutOfBoundsException e) {
            return null;
        }
    }

    @Override
    public void updateMessage(Message message) {
        Session session;
        try {
            session = this.sessionFactory.getCurrentSession();
            session.beginTransaction();
            session.update(message);
            session.getTransaction().commit();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            this.sessionFactory.close();
        }
    }

    @Override
    public boolean isSentMessage(Message message) {
        Session session;
        Message sentMessage = null;
    try{
        session = this.sessionFactory.getCurrentSession();
        session.beginTransaction();
        try{
            sentMessage = (Message) session.createSQLQuery("SELECT * FROM MESSAGES " +
                            "WHERE REMIND_ID=:remindId " +
                        "AND CHAT_ID=:chatId AND IS_REMIND_MESSAGE is true").
                addEntity(Message.class).setParameter("remindId", message.getRemindId()).
                setParameter("chatId", message.getChatId()).getSingleResult(); }
        catch (Exception e){ return false; }
        session.getTransaction().commit();
    }
    catch (Exception e){
        e.printStackTrace(); }
    finally {
        this.sessionFactory.close();
    }
    return (sentMessage != null);

    }

    @Override
    @SuppressWarnings("unchecked")
    public List<Message> getAllNotRemindMessage(User user) {
        Session session;
        List<Message> messages = null;
        try{
            session = this.sessionFactory.getCurrentSession();
            session.beginTransaction();
        messages = session.createSQLQuery("SELECT * FROM MESSAGES WHERE CHAT_ID=:id AND " +
                    "IS_REMIND_MESSAGE is false").
                    addEntity(Message.class).setParameter("id", user.getChatId()).list();
            session.getTransaction().commit();
        }
        catch (Exception e){ e.printStackTrace();}
        finally {
            this.sessionFactory.close();
        }
        return messages;
    }

    @Override
    public void deleteAllNotRemindMessage(User user) {
        Session session;
    try{
        session = this.sessionFactory.getCurrentSession();
        session.beginTransaction();
        session.createSQLQuery("DELETE FROM MESSAGES WHERE CHAT_ID=:id AND " +
                "IS_REMIND_MESSAGE is false").setParameter("id", user.getChatId()).executeUpdate();
        session.getTransaction().commit();
    }
        catch (Exception e){ e.printStackTrace();}
    finally {
        this.sessionFactory.close();
    }


    }
}
