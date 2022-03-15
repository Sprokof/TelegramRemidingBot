package telegramBot.dao;

import lombok.Getter;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.cfg.Configuration;
import telegramBot.entity.Message;

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
    public void deleteByMessageId(Integer messageId) {
        Session session;
    try{
        session = this.sessionFactory.getCurrentSession();
        session.beginTransaction();
        session.createSQLQuery("DELETE FROM MESSAGES WHERE MESSAGE_ID = "+messageId).
                addEntity(Message.class);
        session.getTransaction().commit();}
    catch (Exception e){e.printStackTrace();}
    finally {
        this.sessionFactory.close(); }
    }

    @Override
    public void deleteMessage(Message message) {
        Session session;
    try{
        session = this.sessionFactory.getCurrentSession();
        session.beginTransaction();
        session.delete(message);
        session.getTransaction().commit();}
    catch (Exception e){ e.printStackTrace(); }
    finally {
        this.sessionFactory.close();}

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
}
