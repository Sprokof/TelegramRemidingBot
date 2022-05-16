package telegramBot.dao;

import lombok.Getter;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.cfg.Configuration;
import org.springframework.stereotype.Component;
import telegramBot.entity.Details;
import telegramBot.entity.Remind;
import telegramBot.entity.User;

import javax.persistence.NoResultException;
import java.util.ArrayList;
import java.util.List;

@Component
public class UserDAOImpl implements UserDAO{

    private static final SessionFactory sessionFactory =
            DB.getInstance().getSessionFactory(new Class[]{User.class, Remind.class,
                    Details.class});

    @Override
    public void saveUser(User user) {
        Session session = null;
        try {
            session = sessionFactory.openSession();
            session.beginTransaction();
            session.save(user);
            session.getTransaction().commit();
        } catch (Exception e) {
            if (session != null) {
                if (session.getTransaction() != null) {
                    session.getTransaction().rollback();
                }
            }
        } finally {
            if (session != null) {
                session.close();
            }
        }

    }

    @Override
    public User getUserByChatId(String chatId) {
        Session session = null;
        User user = null;
        try {
            session = sessionFactory.openSession();
            session.beginTransaction();
            user = (User) session.createSQLQuery("SELECT * FROM USERS WHERE CHAT_ID=:chatId").
                    setParameter("chatId", chatId).addEntity(User.class).getSingleResult();
            session.getTransaction().commit();
        } catch (Exception e) {
            if (session != null) {
                if (session.getTransaction() != null) {
                    session.getTransaction().rollback();
                    if (e instanceof NoResultException) return null;
                }
            }
        } finally {
            if (session != null) {
                session.close();
            }
        }
    return user;
    }


    @Override
    public void updateUser(User user) {
        Session session = null;
        try {
            session = sessionFactory.openSession();
            session.beginTransaction();
            session.update(user);
            session.getTransaction().commit();
        } catch (Exception e) {
            if (session != null) {
                if (session.getTransaction() != null) {
                    session.getTransaction().rollback();
                }
            }
        } finally {
            if (session != null) {
                session.close();
            }
        }
    }

    @Override
    public void deleteUser(User user) {
        Session session = null;
    try{
        session = sessionFactory.openSession();
        session.beginTransaction();
        session.delete(user);
        session.getTransaction().commit();
    }
    catch (Exception e) {
        if (session != null) {
            if (session.getTransaction() != null) {
                session.getTransaction().rollback();
            }
        }
    } finally {
        if (session != null) {
            session.close();
        }
    }
    }

    @Override
    @SuppressWarnings("unchecked")
    public List<User> getAllActiveUser() {
        Session session = null;
        List<User> users = new ArrayList<>();
    try{
        session = sessionFactory.openSession();
        session.beginTransaction();
        users = session.createSQLQuery("SELECT * FROM USERS WHERE " +
                "IS_ACTIVE is true").addEntity(User.class).list();
        session.getTransaction().commit();
    }
    catch (Exception e) {
        if (session != null) {
            if (session.getTransaction() != null) {
                session.getTransaction().rollback();
            }
        }
    } finally {
        if (session != null) {
            session.close();
        }
    }
    return users;
    }
}
