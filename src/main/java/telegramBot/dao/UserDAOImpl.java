package telegramBot.dao;

import lombok.Getter;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.cfg.Configuration;
import org.springframework.stereotype.Component;
import telegramBot.entity.Details;
import telegramBot.entity.Remind;
import telegramBot.entity.User;

import java.util.ArrayList;
import java.util.List;

@Component
public class UserDAOImpl implements UserDAO{
    @Getter
    private final SessionFactory sessionFactory =
            new Configuration().configure("hibernate.cfg.xml").
                    addAnnotatedClass(User.class).addAnnotatedClass(Remind.class).
                    addAnnotatedClass(Details.class).buildSessionFactory();
    @Override
    public void saveUser(User user) {
        Session session = null;
    try{
        session = this.sessionFactory.getCurrentSession();
        session.beginTransaction();
        session.save(user);
        session.getTransaction().commit();}
    catch (Exception e){e.printStackTrace();}
    finally {
        this.sessionFactory.close();
    }
    }

    @Override
    public User getUserByChatId(String chatId) {
        Session session;
        User user = null;
    try{
        session = this.sessionFactory.getCurrentSession();
        session.beginTransaction();
        try{
            user = (User) session.createSQLQuery("SELECT * FROM USERS WHERE CHAT_ID=:chatId").
                setParameter("chatId", chatId).addEntity(User.class).list().get(0);}
        catch (IndexOutOfBoundsException e){ return null; }
        session.getTransaction().commit();}
    catch (Exception e){e.printStackTrace();}
    finally {
            this.sessionFactory.close(); }
    return user;

    }

    @Override
    public void updateUser(User user) {
        Session session = null;
    try{
        session = this.sessionFactory.getCurrentSession();
        session.beginTransaction();
        session.update(user);
        session.getTransaction().commit();
    }
    catch (Exception e){e.printStackTrace();}
    finally {
        this.sessionFactory.close();
    }
    }

    @Override
    public void deleteUser(User user) {
        Session session = null;
    try{
        session = this.sessionFactory.getCurrentSession();
        session.beginTransaction();
        session.delete(user);
        session.getTransaction().commit();
    }
    catch (Exception e){e.printStackTrace();}
    finally {
        this.sessionFactory.close();
    }

    }

    @Override
    @SuppressWarnings("unchecked")
    public List<User> getAllActiveUser() {
        Session session;
        List<User> users = new ArrayList<>();
    try{
        session = this.sessionFactory.getCurrentSession();
        session.beginTransaction();
        users = session.createSQLQuery("SELECT * FROM USERS WHERE " +
                "IS_ACTIVE is true").addEntity(User.class).list();
        session.getTransaction().commit();
    }
    catch (Exception e){e.printStackTrace();}
    finally {
        this.sessionFactory.close();
    }
    return users;
    }
}
