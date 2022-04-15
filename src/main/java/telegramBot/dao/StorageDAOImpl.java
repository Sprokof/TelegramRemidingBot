package telegramBot.dao;

import lombok.Getter;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.cfg.Configuration;
import org.springframework.stereotype.Component;
import telegramBot.entity.Storage;

@Component
public class StorageDAOImpl implements StorageDAO{

    @Getter
    private final SessionFactory sessionFactory = new Configuration().
            configure("hibernate.cfg.xml").addAnnotatedClass(Storage.class).buildSessionFactory();

    @Override
    public void saveStorage(Storage storage) {
        Session session = null;
    try{
        session = this.sessionFactory.getCurrentSession();
        session.beginTransaction();
        session.save(storage);
        session.getTransaction().commit();
    }
    catch (Exception e){e.printStackTrace();}
    finally {
        this.sessionFactory.close();
    }
    }

    @Override
    public void updateStorage(Storage storage) {
        Session session;
    try {
        session = this.sessionFactory.getCurrentSession();
        session.beginTransaction();
        session.update(storage);
        session.getTransaction().commit();
    }
     catch (Exception e){e.printStackTrace();}
    finally {
        this.sessionFactory.close();
    }
    }

    @Override
    public Storage getStorageById(int id) {
        Session session;
        Storage st = null;
    try{
        session = this.sessionFactory.getCurrentSession();
        session.beginTransaction();
        st = session.get(Storage.class, id);
        session.getTransaction().commit();
    }
    catch (Exception e){e.printStackTrace();}
    finally {
        this.sessionFactory.close();
    }
    return st;
    }
}
