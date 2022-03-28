package telegramBot.dao;

import lombok.Getter;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.cfg.Configuration;
import telegramBot.crypt.XORCrypt;
import telegramBot.entity.Message;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.stream.Collectors;

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
    public List<Message> getAllMessages() {
        Session session;
        List<?> tempList = null;
    try{
        session = this.sessionFactory.getCurrentSession();
        session.beginTransaction();
        tempList = session.createSQLQuery("SELECT * FROM MESSAGES").
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
    public Message getMessageByChatIdAndMaintenance(String chatId, String maintenance) {
        Session session;
        List<Object> objects = new ArrayList<>();
        try {
            session = this.sessionFactory.getCurrentSession();
            session.beginTransaction();
            objects = (ArrayList<Object>) session.createSQLQuery("SELECT * FROM MESSAGES " +
                    "WHERE CHAT_ID=:id").setParameter("id", chatId).list();
            session.getTransaction().commit();
        } catch (Exception e) {
            e.printStackTrace();
        }
        if (objects.isEmpty()) return null;
        List<Message> messages = new ArrayList<>();
        for (Iterator<Object> iterator = objects.iterator(); iterator.hasNext(); ) {
            Object[] line = (Object[]) iterator.next();
            messages.add(new Message((String) line[1], (String) line[2], (String) line[3], (Integer) line[4]));
        }

        try {
            return messages.stream().filter((m) -> {
                return XORCrypt.decrypt(XORCrypt.
                                stringToIntArray(m.getEncrypted_maintenance()),
                        m.getKey()).equals(maintenance);
            }).collect(Collectors.toList()).get(0);
        } catch (IndexOutOfBoundsException e) {
            return null;
        }
    }
}
